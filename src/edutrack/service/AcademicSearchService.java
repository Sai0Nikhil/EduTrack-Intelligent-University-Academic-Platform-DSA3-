package edutrack.service;

import edutrack.algorithms.strings.AhoCorasick;
import edutrack.algorithms.strings.KmpMatcher;
import edutrack.algorithms.strings.RabinKarp;
import edutrack.algorithms.dp.DamerauLevenshtein;
import edutrack.algorithms.dp.Levenshtein;
import edutrack.core.MyArrayList;
import edutrack.core.Pair;
import edutrack.model.Course;

/**
 * Service for course searching, keyword tagging, and typo correction.
 * Zero java.util.* dependencies.
 */
public class AcademicSearchService {
    private final MyArrayList<Course> courses;

    public AcademicSearchService(MyArrayList<Course> courses) {
        this.courses = courses;
    }

    /**
     * Searches course titles and descriptions using KMP.
     */
    public MyArrayList<Pair<Course, Integer>> searchCoursesByKMP(String query) {
        MyArrayList<Pair<Course, Integer>> results = new MyArrayList<>();
        for (int i = 0; i < courses.size(); i++) {
            Course c = courses.get(i);
            MyArrayList<Integer> matches = KmpMatcher.search(c.getTitle(), query, true);
            if (!matches.isEmpty()) {
                results.add(new Pair<>(c, matches.size()));
            }
        }
        return results;
    }

    /**
     * Searches course codes using Rabin-Karp rolling hash.
     */
    public MyArrayList<Course> searchCodeByRabinKarp(String codeQuery) {
        MyArrayList<Course> results = new MyArrayList<>();
        for (int i = 0; i < courses.size(); i++) {
            Course c = courses.get(i);
            MyArrayList<Integer> matches = RabinKarp.search(c.getCode().toUpperCase(), codeQuery.toUpperCase());
            if (!matches.isEmpty()) {
                results.add(c);
            }
        }
        return results;
    }

    /**
     * Tags a textbook or syllabus text with multiple academic keywords simultaneously using Aho-Corasick.
     */
    public MyArrayList<AhoCorasick.MatchResult> tagKeywordsAhoCorasick(String documentText, String[] keywords) {
        AhoCorasick ac = new AhoCorasick();
        for (String kw : keywords) {
            ac.addPattern(kw);
        }
        ac.buildAutomation();
        return ac.search(documentText);
    }

    /**
     * Suggests closest course titles using Levenshtein distance.
     */
    public MyArrayList<Pair<Course, Integer>> suggestTypoLevenshtein(String query, int maxDistance) {
        MyArrayList<Pair<Course, Integer>> suggestions = new MyArrayList<>();
        for (int i = 0; i < courses.size(); i++) {
            Course c = courses.get(i);
            int dist = Levenshtein.computeDistance(query.toLowerCase(), c.getTitle().toLowerCase());
            if (dist <= maxDistance) {
                suggestions.add(new Pair<>(c, dist));
            }
        }
        return suggestions;
    }

    /**
     * Suggests course codes using Damerau-Levenshtein (transposition-aware).
     */
    public MyArrayList<Pair<Course, Integer>> suggestCourseCodeDamerau(String queryCode, int maxDistance) {
        MyArrayList<Pair<Course, Integer>> suggestions = new MyArrayList<>();
        for (int i = 0; i < courses.size(); i++) {
            Course c = courses.get(i);
            int dist = DamerauLevenshtein.computeDistance(queryCode.toUpperCase(), c.getCode().toUpperCase());
            if (dist <= maxDistance) {
                suggestions.add(new Pair<>(c, dist));
            }
        }
        return suggestions;
    }
}
