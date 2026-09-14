package edutrack.service;

import edutrack.algorithms.strings.ZAlgorithm;
import edutrack.algorithms.suffix.KasaiLCP;
import edutrack.algorithms.suffix.SuffixAutomaton;
import edutrack.core.MyArrayList;
import edutrack.model.AssignmentSubmission;

/**
 * Service for document similarity, plagiarism detection, and suffix indexing.
 * Zero java.util.* dependencies.
 */
public class PlagiarismDetectionService {

    public static class PlagiarismReport {
        public String sub1Title;
        public String sub2Title;
        public MyArrayList<KasaiLCP.SharedExcerpt> sharedExcerpts;
        public double similarityScore;

        public PlagiarismReport(String sub1Title, String sub2Title, MyArrayList<KasaiLCP.SharedExcerpt> sharedExcerpts, double similarityScore) {
            this.sub1Title = sub1Title;
            this.sub2Title = sub2Title;
            this.sharedExcerpts = sharedExcerpts;
            this.similarityScore = similarityScore;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("=== Plagiarism Report: ").append(sub1Title).append(" vs ").append(sub2Title).append(" ===\n");
            sb.append(String.format("  Estimated Plagiarism Overlap: %.2f%%\n", similarityScore * 100));
            sb.append("  Detected Shared Passages (>= 40 chars): ").append(sharedExcerpts.size()).append("\n");
            for (int i = 0; i < sharedExcerpts.size(); i++) {
                KasaiLCP.SharedExcerpt ex = sharedExcerpts.get(i);
                sb.append("    [").append(i + 1).append("] (Length: ").append(ex.length).append(" chars)\n");
                String preview = ex.text.replace("\n", " ").trim();
                if (preview.length() > 90) preview = preview.substring(0, 90) + "...";
                sb.append("        \"").append(preview).append("\"\n");
            }
            return sb.toString();
        }
    }

    /**
     * Compares two assignments using Suffix Array + Kasai's LCP algorithm.
     */
    public PlagiarismReport compareAssignments(AssignmentSubmission sub1, AssignmentSubmission sub2, int minThreshold) {
        String t1 = sub1.getTextContent();
        String t2 = sub2.getTextContent();

        MyArrayList<KasaiLCP.SharedExcerpt> excerpts = KasaiLCP.findSharedExcerpts(t1, t2, minThreshold);

        int totalSharedChars = 0;
        for (int i = 0; i < excerpts.size(); i++) {
            totalSharedChars += excerpts.get(i).length;
        }

        int minDocLen = Math.min(t1.length(), t2.length());
        double score = minDocLen > 0 ? Math.min(1.0, (double) totalSharedChars / minDocLen) : 0.0;

        return new PlagiarismReport(sub1.getTitle(), sub2.getTitle(), excerpts, score);
    }

    /**
     * Detects repeated phrases within a single submission using Z-Algorithm.
     */
    public int detectInternalRepetitionZ(AssignmentSubmission sub) {
        return ZAlgorithm.findLongestRepeatedPrefix(sub.getTextContent());
    }

    /**
     * Indexes an academic document into a Suffix Automaton and queries substrings.
     */
    public SuffixAutomaton indexDocumentSAM(String text) {
        return new SuffixAutomaton(text);
    }
}
