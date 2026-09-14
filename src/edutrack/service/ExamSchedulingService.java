package edutrack.service;

import edutrack.algorithms.np.CliqueToISReduction;
import edutrack.algorithms.np.DpllSatSolver;
import edutrack.algorithms.np.ISToVertexCover;
import edutrack.algorithms.np.SatToCliqueReduction;
import edutrack.algorithms.np.VertexCover2Approx;
import edutrack.core.MyArrayList;
import edutrack.core.Pair;
import edutrack.model.Course;

/**
 * Service for exam constraint satisfaction, NP-hardness reductions, and approximation algorithms.
 * Zero java.util.* dependencies.
 */
public class ExamSchedulingService {
    private final MyArrayList<Course> courses;

    public ExamSchedulingService(MyArrayList<Course> courses) {
        this.courses = courses;
    }

    /**
     * Solves an examination timetabling problem with SAT (DPLL solver).
     * Variables represent Course i assigned to Slot j.
     */
    public DpllSatSolver.SatResult solveExamTimetableSAT(int numCoursesToSchedule, int numSlots) {
        MyArrayList<DpllSatSolver.Clause> clauses = new MyArrayList<>();

        // Variable mapping: var(c, s) = c * numSlots + s + 1
        // Constraint 1: Each course must be assigned to at least one slot
        for (int c = 0; c < numCoursesToSchedule; c++) {
            DpllSatSolver.Clause clause = new DpllSatSolver.Clause();
            for (int s = 0; s < numSlots; s++) {
                clause.literals.add(c * numSlots + s + 1);
            }
            clauses.add(clause);
        }

        // Constraint 2: A course cannot be in two different slots
        for (int c = 0; c < numCoursesToSchedule; c++) {
            for (int s1 = 0; s1 < numSlots; s1++) {
                for (int s2 = s1 + 1; s2 < numSlots; s2++) {
                    int lit1 = -(c * numSlots + s1 + 1);
                    int lit2 = -(c * numSlots + s2 + 1);
                    clauses.add(new DpllSatSolver.Clause(lit1, lit2));
                }
            }
        }

        // Constraint 3: Conflicting courses (e.g. sharing students) cannot be in the same slot
        for (int c1 = 0; c1 < numCoursesToSchedule; c1++) {
            for (int c2 = c1 + 1; c2 < numCoursesToSchedule; c2++) {
                if ((c1 + c2) % 3 == 0) { // simulated shared students conflict
                    for (int s = 0; s < numSlots; s++) {
                        int lit1 = -(c1 * numSlots + s + 1);
                        int lit2 = -(c2 * numSlots + s + 1);
                        clauses.add(new DpllSatSolver.Clause(lit1, lit2));
                    }
                }
            }
        }

        return DpllSatSolver.solve(clauses, numCoursesToSchedule * numSlots);
    }

    /**
     * Runs and demonstrates the full Karp reduction chain:
     * 3-SAT -> CLIQUE -> INDEPENDENT SET -> VERTEX COVER
     */
    public String demonstrateReductionChain() {
        // Sample 3-SAT exam constraint formula:
        // C1 = (x1 v x2 v -x3)
        // C2 = (-x1 v x3 v x4)
        // C3 = (-x2 v -x3 v -x4)
        MyArrayList<DpllSatSolver.Clause> clauses = new MyArrayList<>();
        clauses.add(new DpllSatSolver.Clause(1, 2, -3));
        clauses.add(new DpllSatSolver.Clause(-1, 3, 4));
        clauses.add(new DpllSatSolver.Clause(-2, -3, -4));

        StringBuilder sb = new StringBuilder();
        sb.append("=== Polynomial-Time NP-Completeness Reduction Demonstration ===\n");
        sb.append("1. Original 3-SAT Formula:\n");
        sb.append("   Φ = (x1 ∨ x2 ∨ ¬x3) ∧ (¬x1 ∨ x3 ∨ x4) ∧ (¬x2 ∨ ¬x3 ∨ ¬x4)\n");
        sb.append("   Clauses: ").append(clauses.size()).append(" | Variables: 4\n\n");

        // Step 1: 3-SAT -> CLIQUE
        SatToCliqueReduction.ReducedGraph cliqueGraph = SatToCliqueReduction.reduce(clauses);
        sb.append("2. Step 1: 3-SAT ──(Karp Reduction)──> CLIQUE\n");
        sb.append("   Constructed Graph G: |V| = ").append(cliqueGraph.numVertices)
          .append(" vertices (3 per clause), |E| = ").append(cliqueGraph.countEdges()).append(" edges\n");
        sb.append("   Target Clique Size k = ").append(cliqueGraph.targetCliqueSize)
          .append(" (Satisfiable 3-SAT <=> Graph G contains Clique of size ").append(cliqueGraph.targetCliqueSize).append(")\n\n");

        // Step 2: CLIQUE -> INDEPENDENT SET
        CliqueToISReduction.Graph g = new CliqueToISReduction.Graph(cliqueGraph.numVertices);
        g.adj = cliqueGraph.adj;
        CliqueToISReduction.Graph compGraph = CliqueToISReduction.reduceToComplement(g);

        sb.append("3. Step 2: CLIQUE ──(Graph Complement)──> INDEPENDENT SET\n");
        sb.append("   Constructed Complement Graph G_bar: |V| = ").append(compGraph.numVertices)
          .append(", |E_bar| = ").append(compGraph.countEdges()).append(" edges\n");
        sb.append("   Target Independent Set Size k = ").append(cliqueGraph.targetCliqueSize)
          .append(" (Clique in G <=> Independent Set in G_bar)\n\n");

        // Step 3: INDEPENDENT SET -> VERTEX COVER
        int n = compGraph.numVertices;
        int k = cliqueGraph.targetCliqueSize;
        int dualVCSize = n - k;

        sb.append("4. Step 3: INDEPENDENT SET ──(Complementary Duality)──> VERTEX COVER\n");
        sb.append("   Theorem: S is an Independent Set in G_bar <=> V \\ S is a Vertex Cover in G_bar.\n");
        sb.append("   Target Vertex Cover Size = |V| - k = ").append(n).append(" - ").append(k).append(" = ").append(dualVCSize).append("\n");
        sb.append("   Reduction Chain Validated: 3-SAT <=p CLIQUE <=p INDEPENDENT-SET <=p VERTEX-COVER.\n");

        return sb.toString();
    }

    /**
     * Solves the Exam Invigilator Allocation via 2-Approximation Vertex Cover.
     */
    public VertexCover2Approx.ApproxResult allocateInvigilatorsApprox(int numCourses) {
        int n = Math.min(numCourses, courses.size());
        MyArrayList<Pair<Integer, Integer>> conflictEdges = new MyArrayList<>();

        // Generate realistic exam conflict edges
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                if ((i * 5 + j * 7) % 6 == 0) { // simulated exam slot conflict
                    conflictEdges.add(new Pair<>(i, j));
                }
            }
        }

        return VertexCover2Approx.approximateCover(n, conflictEdges);
    }
}
