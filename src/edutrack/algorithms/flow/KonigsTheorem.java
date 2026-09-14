package edutrack.algorithms.flow;

import edutrack.core.MyArrayList;
import edutrack.core.MyQueue;

/**
 * König's Theorem: In any bipartite graph, the size of a Maximum Matching equals
 * the size of a Minimum Vertex Cover.
 *
 * Used in EduTrack to analyze faculty-course conflicts and identify the minimal set
 * of bottleneck entities that must be rescheduled or staffed to resolve all conflicts.
 * Zero java.util.* dependencies.
 */
public class KonigsTheorem {

    public static class ConflictBottleneck {
        public int minVertexCoverSize;
        public MyArrayList<String> criticalFaculty;
        public MyArrayList<String> criticalCourses;

        public ConflictBottleneck(int minVertexCoverSize, MyArrayList<String> criticalFaculty, MyArrayList<String> criticalCourses) {
            this.minVertexCoverSize = minVertexCoverSize;
            this.criticalFaculty = criticalFaculty;
            this.criticalCourses = criticalCourses;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder("Min Conflict Bottleneck Set (Size: " + minVertexCoverSize + "):\n");
            sb.append("  Critical Faculty (").append(criticalFaculty.size()).append("): ");
            for (int i = 0; i < criticalFaculty.size(); i++) {
                sb.append(criticalFaculty.get(i)).append(i + 1 < criticalFaculty.size() ? ", " : "\n");
            }
            sb.append("  Critical Courses (").append(criticalCourses.size()).append("): ");
            for (int i = 0; i < criticalCourses.size(); i++) {
                sb.append(criticalCourses.get(i)).append(i + 1 < criticalCourses.size() ? ", " : "\n");
            }
            return sb.toString();
        }
    }

    /**
     * Solves König's Minimum Vertex Cover on a bipartite graph.
     * numL: number of faculty (Left)
     * numR: number of courses (Right)
     * conflictEdges: boolean matrix where conflictEdges[i][j] is true if faculty i and course j have a conflict
     */
    public static ConflictBottleneck findMinimumBottleneck(String[] facultyNames, String[] courseNames, boolean[][] conflictEdges) {
        int nL = facultyNames.length;
        int nR = courseNames.length;

        int source = 0;
        int lBase = 1;
        int rBase = lBase + nL;
        int sink = rBase + nR;
        int totalNodes = sink + 1;

        FlowNetwork net = new FlowNetwork(totalNodes);

        for (int i = 0; i < nL; i++) {
            net.addEdge(source, lBase + i, 1);
        }
        for (int i = 0; i < nL; i++) {
            for (int j = 0; j < nR; j++) {
                if (conflictEdges[i][j]) {
                    net.addEdge(lBase + i, rBase + j, 1);
                }
            }
        }
        for (int j = 0; j < nR; j++) {
            net.addEdge(rBase + j, sink, 1);
        }

        int maxMatch = DinicsAlgorithm.maxFlow(net, source, sink);

        // Find reachable vertices in the residual network from Source
        boolean[] reachable = new boolean[totalNodes];
        MyQueue<Integer> q = new MyQueue<>();
        q.enqueue(source);
        reachable[source] = true;

        while (!q.isEmpty()) {
            int u = q.dequeue();
            MyArrayList<FlowNetwork.Edge> edges = net.adj.get(u);
            for (int i = 0; i < edges.size(); i++) {
                FlowNetwork.Edge e = edges.get(i);
                if (e.remainingCapacity() > 0 && !reachable[e.to]) {
                    reachable[e.to] = true;
                    q.enqueue(e.to);
                }
            }
        }

        // Min Vertex Cover = (L \ reachable) U (R ∩ reachable)
        MyArrayList<String> critFaculty = new MyArrayList<>();
        MyArrayList<String> critCourses = new MyArrayList<>();

        for (int i = 0; i < nL; i++) {
            if (!reachable[lBase + i]) {
                critFaculty.add(facultyNames[i]);
            }
        }

        for (int j = 0; j < nR; j++) {
            if (reachable[rBase + j]) {
                critCourses.add(courseNames[j]);
            }
        }

        int coverSize = critFaculty.size() + critCourses.size();
        return new ConflictBottleneck(coverSize, critFaculty, critCourses);
    }
}
