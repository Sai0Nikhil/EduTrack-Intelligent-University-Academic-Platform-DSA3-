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
     * Searches course titles using Bittu's Algorithm (Invention: Fast Rolling Bigram Cosine Vector Matching).
     */
    public MyArrayList<Pair<Course, Integer>> searchCoursesByBittu(String query) {
        MyArrayList<Pair<Course, Integer>> results = new MyArrayList<>();
        for (int i = 0; i < courses.size(); i++) {
            Course c = courses.get(i);
            MyArrayList<Integer> matches = edutrack.algorithms.strings.BittuAlgorithm.search(c.getTitle(), query);
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
     * Suggests closest course titles using Levenshtein distance (checking full title and individual words).
     */
    public MyArrayList<Pair<Course, Integer>> suggestTypoLevenshtein(String query, int maxDistance) {
        MyArrayList<Pair<Course, Integer>> suggestions = new MyArrayList<>();
        if (query == null || query.trim().isEmpty()) return suggestions;
        String qLower = query.toLowerCase().trim();

        for (int i = 0; i < courses.size(); i++) {
            Course c = courses.get(i);
            int bestDist = Levenshtein.computeDistance(qLower, c.getTitle().toLowerCase());
            String[] words = c.getTitle().split("\\s+");
            for (String w : words) {
                if (!w.isEmpty()) {
                    int d = Levenshtein.computeDistance(qLower, w.toLowerCase());
                    if (d < bestDist) {
                        bestDist = d;
                    }
                }
            }
            if (bestDist <= maxDistance) {
                suggestions.add(new Pair<>(c, bestDist));
            }
        }

        // Sort suggestions by distance ascending (Zero-java.util in-place sort)
        for (int i = 0; i < suggestions.size(); i++) {
            for (int j = i + 1; j < suggestions.size(); j++) {
                if (suggestions.get(j).second < suggestions.get(i).second) {
                    Pair<Course, Integer> temp = suggestions.get(i);
                    suggestions.set(i, suggestions.get(j));
                    suggestions.set(j, temp);
                }
            }
        }
        return suggestions;
    }

    /**
     * Suggests closest course codes using Damerau-Levenshtein distance (handles transpositions).
     */
    public MyArrayList<Pair<Course, Integer>> suggestCourseCodeDamerau(String query, int maxDistance) {
        MyArrayList<Pair<Course, Integer>> suggestions = new MyArrayList<>();
        if (query == null || query.trim().isEmpty()) return suggestions;
        String qUpper = query.toUpperCase().trim();

        for (int i = 0; i < courses.size(); i++) {
            Course c = courses.get(i);
            int dist = DamerauLevenshtein.computeDistance(qUpper, c.getCode().toUpperCase());
            if (dist <= maxDistance) {
                suggestions.add(new Pair<>(c, dist));
            }
        }

        // Sort by distance ascending
        for (int i = 0; i < suggestions.size(); i++) {
            for (int j = i + 1; j < suggestions.size(); j++) {
                if (suggestions.get(j).second < suggestions.get(i).second) {
                    Pair<Course, Integer> temp = suggestions.get(i);
                    suggestions.set(i, suggestions.get(j));
                    suggestions.set(j, temp);
                }
            }
        }
        return suggestions;
    }

    /**
     * Intelligent live auto-suggestion combining prefix matching, substring matching,
     * and Levenshtein typo-tolerance for instant query completion.
     */
    public MyArrayList<String> autoSuggest(String query, int maxResults) {
        MyArrayList<String> suggestions = new MyArrayList<>();
        if (query == null || query.trim().isEmpty()) {
            return suggestions;
        }

        String qLower = query.trim().toLowerCase();
        String qUpper = query.trim().toUpperCase();

        // 1. Exact Prefix matches on Course Code or Title
        for (int i = 0; i < courses.size(); i++) {
            Course c = courses.get(i);
            if (c.getCode().toUpperCase().startsWith(qUpper) || c.getTitle().toLowerCase().startsWith(qLower)) {
                String label = c.getCode() + " - " + c.getTitle();
                if (!suggestions.contains(label)) {
                    suggestions.add(label);
                    if (suggestions.size() >= maxResults) return suggestions;
                }
            }
        }

        // 2. Substring matches
        for (int i = 0; i < courses.size(); i++) {
            Course c = courses.get(i);
            if (c.getTitle().toLowerCase().contains(qLower) || c.getCode().toUpperCase().contains(qUpper)) {
                String label = c.getCode() + " - " + c.getTitle();
                if (!suggestions.contains(label)) {
                    suggestions.add(label);
                    if (suggestions.size() >= maxResults) return suggestions;
                }
            }
        }

        // 3. Typo-tolerant Levenshtein matches across words (edit distance <= 2)
        for (int i = 0; i < courses.size(); i++) {
            Course c = courses.get(i);
            String[] words = c.getTitle().split("\\s+");
            boolean match = false;
            for (String w : words) {
                if (w.length() >= 3 && Math.abs(w.length() - qLower.length()) <= 2) {
                    int dist = Levenshtein.computeDistance(qLower, w.toLowerCase());
                    if (dist <= 2) {
                        match = true;
                        break;
                    }
                }
            }
            if (match) {
                String label = c.getCode() + " - " + c.getTitle() + " (Did you mean?)";
                if (!suggestions.contains(label)) {
                    suggestions.add(label);
                    if (suggestions.size() >= maxResults) return suggestions;
                }
            }
        }

        return suggestions;
    }
}
