package edutrack.algorithms.flow;

import edutrack.core.MyArrayList;
import edutrack.core.Pair;

/**
 * Maximum Bipartite Matching for University Faculty-to-Course Allocation.
 * Connects Source -> Faculty (capacity = maxWorkload),
 * Faculty -> Eligible Course (capacity = 1),
 * Course -> Sink (capacity = 1).
 * Solved via network flow with zero java.util.* dependencies.
 */
public class BipartiteMatching {

    public static class AssignmentResult {
        public int totalMatches;
        public MyArrayList<Pair<String, String>> assignments; // (FacultyName/ID, CourseCode)

        public AssignmentResult(int totalMatches, MyArrayList<Pair<String, String>> assignments) {
            this.totalMatches = totalMatches;
            this.assignments = assignments;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder("Total Course Assignments: " + totalMatches + "\n");
            for (int i = 0; i < assignments.size(); i++) {
                Pair<String, String> p = assignments.get(i);
                sb.append("  • ").append(p.first).append(" ───> ").append(p.second).append("\n");
            }
            return sb.toString();
        }
    }

    /**
     * Solves the faculty-to-course allocation problem.
     * facultyIds: array of faculty identifiers
     * courseCodes: array of course identifiers
     * facultyCapacities: max number of courses each faculty can take
     * eligibilityMatrix[f][c]: true if faculty f is qualified to teach course c
     */
    public static AssignmentResult allocate(String[] facultyIds, String[] courseCodes,
                                            int[] facultyCapacities, boolean[][] eligibilityMatrix) {
        int numFaculty = facultyIds.length;
        int numCourses = courseCodes.length;

        int source = 0;
        int facultyBase = 1;
        int courseBase = facultyBase + numFaculty;
        int sink = courseBase + numCourses;
        int totalNodes = sink + 1;

        FlowNetwork net = new FlowNetwork(totalNodes);

        // Source to Faculty
        for (int i = 0; i < numFaculty; i++) {
            net.addEdge(source, facultyBase + i, facultyCapacities[i]);
        }

        // Faculty to Course
        for (int i = 0; i < numFaculty; i++) {
            for (int j = 0; j < numCourses; j++) {
                if (eligibilityMatrix[i][j]) {
                    net.addEdge(facultyBase + i, courseBase + j, 1);
                }
            }
        }

        // Course to Sink
        for (int j = 0; j < numCourses; j++) {
            net.addEdge(courseBase + j, sink, 1);
        }

        int maxFlow = DinicsAlgorithm.maxFlow(net, source, sink);

        // Reconstruct assignments
        MyArrayList<Pair<String, String>> pairs = new MyArrayList<>();
        for (int i = 0; i < numFaculty; i++) {
            int facultyNode = facultyBase + i;
            MyArrayList<FlowNetwork.Edge> edges = net.adj.get(facultyNode);
            for (int k = 0; k < edges.size(); k++) {
                FlowNetwork.Edge e = edges.get(k);
                if (e.to >= courseBase && e.to < sink && e.flow > 0) {
                    int courseIndex = e.to - courseBase;
                    pairs.add(new Pair<>(facultyIds[i], courseCodes[courseIndex]));
                }
            }
        }

        return new AssignmentResult(maxFlow, pairs);
    }
}
