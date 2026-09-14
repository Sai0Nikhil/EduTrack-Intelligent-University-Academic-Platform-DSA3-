package edutrack.service;

import edutrack.algorithms.flow.BipartiteMatching;
import edutrack.algorithms.flow.DinicsAlgorithm;
import edutrack.algorithms.flow.EdmondsKarp;
import edutrack.algorithms.flow.FlowNetwork;
import edutrack.algorithms.flow.FordFulkerson;
import edutrack.algorithms.flow.KonigsTheorem;
import edutrack.core.MyArrayList;
import edutrack.model.Course;
import edutrack.model.Faculty;

/**
 * Service for faculty-course assignment, classroom resource allocation,
 * and conflict bottleneck analysis via Network Flow & König's Theorem.
 * Zero java.util.* dependencies.
 */
public class ResourceAllocationService {
    private final MyArrayList<Faculty> facultyList;
    private final MyArrayList<Course> courseList;

    public ResourceAllocationService(MyArrayList<Faculty> facultyList, MyArrayList<Course> courseList) {
        this.facultyList = facultyList;
        this.courseList = courseList;
    }

    /**
     * Allocates faculty members to courses based on qualifications and max teaching loads.
     */
    public BipartiteMatching.AssignmentResult assignFacultyToCourses() {
        int nF = facultyList.size();
        int nC = courseList.size();

        String[] fNames = new String[nF];
        int[] fCaps = new int[nF];
        for (int i = 0; i < nF; i++) {
            Faculty f = facultyList.get(i);
            fNames[i] = f.getName() + " (" + f.getId() + ")";
            fCaps[i] = f.getMaxWorkload();
        }

        String[] cCodes = new String[nC];
        for (int j = 0; j < nC; j++) {
            cCodes[j] = courseList.get(j).getCode() + ": " + courseList.get(j).getTitle();
        }

        boolean[][] eligibility = new boolean[nF][nC];
        for (int i = 0; i < nF; i++) {
            Faculty f = facultyList.get(i);
            for (int j = 0; j < nC; j++) {
                if (f.getEligibleCourses().contains(courseList.get(j).getCode())) {
                    eligibility[i][j] = true;
                }
            }
        }

        return BipartiteMatching.allocate(fNames, cCodes, fCaps, eligibility);
    }

    /**
     * Solves workstation/classroom allocation comparing Ford-Fulkerson, Edmonds-Karp, and Dinic's.
     */
    public String compareFlowAlgorithms(int numStudents, int numLabs, int labCapacity) {
        int source = 0;
        int sBase = 1;
        int labBase = sBase + numStudents;
        int sink = labBase + numLabs;
        int totalNodes = sink + 1;

        FlowNetwork netFF = new FlowNetwork(totalNodes);
        FlowNetwork netEK = new FlowNetwork(totalNodes);
        FlowNetwork netDinic = new FlowNetwork(totalNodes);

        for (int i = 0; i < numStudents; i++) {
            netFF.addEdge(source, sBase + i, 1);
            netEK.addEdge(source, sBase + i, 1);
            netDinic.addEdge(source, sBase + i, 1);

            // Connect student to labs
            for (int j = 0; j < numLabs; j++) {
                if ((i + j) % 2 == 0) { // arbitrary eligibility pattern
                    netFF.addEdge(sBase + i, labBase + j, 1);
                    netEK.addEdge(sBase + i, labBase + j, 1);
                    netDinic.addEdge(sBase + i, labBase + j, 1);
                }
            }
        }

        for (int j = 0; j < numLabs; j++) {
            netFF.addEdge(labBase + j, sink, labCapacity);
            netEK.addEdge(labBase + j, sink, labCapacity);
            netDinic.addEdge(labBase + j, sink, labCapacity);
        }

        long t1 = System.nanoTime();
        int flowFF = FordFulkerson.maxFlow(netFF, source, sink);
        long tFF = System.nanoTime() - t1;

        long t2 = System.nanoTime();
        int flowEK = EdmondsKarp.maxFlow(netEK, source, sink);
        long tEK = System.nanoTime() - t2;

        long t3 = System.nanoTime();
        int flowDinic = DinicsAlgorithm.maxFlow(netDinic, source, sink);
        long tDinic = System.nanoTime() - t3;

        StringBuilder sb = new StringBuilder();
        sb.append("=== Network Flow Algorithm Comparison on Lab Allocation ===\n");
        sb.append(String.format("  Ford-Fulkerson (DFS) : Flow = %d | Time = %.3f ms\n", flowFF, tFF / 1e6));
        sb.append(String.format("  Edmonds-Karp (BFS)   : Flow = %d | Time = %.3f ms\n", flowEK, tEK / 1e6));
        sb.append(String.format("  Dinic's Algorithm    : Flow = %d | Time = %.3f ms\n", flowDinic, tDinic / 1e6));
        return sb.toString();
    }

    /**
     * Applies König's Theorem to analyze faculty-course conflicts.
     */
    public KonigsTheorem.ConflictBottleneck analyzeConflictBottlenecks() {
        int nF = Math.min(10, facultyList.size());
        int nC = Math.min(10, courseList.size());

        String[] fNames = new String[nF];
        for (int i = 0; i < nF; i++) fNames[i] = facultyList.get(i).getName();

        String[] cNames = new String[nC];
        for (int j = 0; j < nC; j++) cNames[j] = courseList.get(j).getCode();

        boolean[][] conflicts = new boolean[nF][nC];
        // Sample conflict: faculty scheduled simultaneously with required course slots
        for (int i = 0; i < nF; i++) {
            for (int j = 0; j < nC; j++) {
                if ((i * 3 + j * 2) % 5 == 0) {
                    conflicts[i][j] = true;
                }
            }
        }

        return KonigsTheorem.findMinimumBottleneck(fNames, cNames, conflicts);
    }
}
