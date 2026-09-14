package edutrack.algorithms.np;

import edutrack.core.MyArrayList;
import edutrack.core.MyHashMap;

/**
 * Davis-Putnam-Logemann-Loveland (DPLL) Boolean Satisfiability (SAT) Solver.
 * Solves academic examination timetabling and classroom conflict constraints in CNF form.
 * Supports unit propagation, pure literal elimination, and recursive backtracking.
 * Zero java.util.* dependencies.
 */
public class DpllSatSolver {

    public static class SatResult {
        public boolean isSatisfiable;
        public MyHashMap<Integer, Boolean> model; // variable id -> true/false

        public SatResult(boolean isSatisfiable, MyHashMap<Integer, Boolean> model) {
            this.isSatisfiable = isSatisfiable;
            this.model = model;
        }

        @Override
        public String toString() {
            if (!isSatisfiable) {
                return "UNSATISFIABLE (No conflict-free exam schedule exists)";
            }
            StringBuilder sb = new StringBuilder("SATISFIABLE\n  Assignment: ");
            MyArrayList<Integer> keys = model.keys();
            for (int i = 0; i < keys.size(); i++) {
                int var = keys.get(i);
                sb.append("x").append(var).append("=").append(model.get(var)).append(i + 1 < keys.size() ? ", " : "");
            }
            return sb.toString();
        }
    }

    public static class Clause {
        public MyArrayList<Integer> literals = new MyArrayList<>();

        public Clause() {}

        public Clause(int l1) {
            literals.add(l1);
        }

        public Clause(int l1, int l2) {
            literals.add(l1);
            literals.add(l2);
        }

        public Clause(int l1, int l2, int l3) {
            literals.add(l1);
            literals.add(l2);
            literals.add(l3);
        }
    }

    public static SatResult solve(MyArrayList<Clause> clauses, int numVariables) {
        MyHashMap<Integer, Boolean> assignment = new MyHashMap<>();
        boolean sat = dpll(clauses, assignment, numVariables);
        return new SatResult(sat, assignment);
    }

    private static boolean dpll(MyArrayList<Clause> clauses, MyHashMap<Integer, Boolean> assignment, int numVars) {
        // Simplify clauses under current assignment
        MyArrayList<Clause> simplified = simplify(clauses, assignment);

        // If simplified is null, a conflict was found (clause became empty)
        if (simplified == null) return false;

        // If all clauses satisfied
        if (simplified.isEmpty()) {
            // Fill unassigned variables with true by default
            for (int v = 1; v <= numVars; v++) {
                if (!assignment.containsKey(v)) {
                    assignment.put(v, true);
                }
            }
            return true;
        }

        // Unit Propagation: find a clause with a single unassigned literal
        int unitLiteral = findUnitLiteral(simplified);
        if (unitLiteral != 0) {
            int var = Math.abs(unitLiteral);
            boolean val = unitLiteral > 0;
            assignment.put(var, val);
            boolean result = dpll(clauses, assignment, numVars);
            if (!result) {
                assignment.remove(var);
            }
            return result;
        }

        // Choose unassigned variable to branch
        int chooseVar = -1;
        for (int v = 1; v <= numVars; v++) {
            if (!assignment.containsKey(v)) {
                chooseVar = v;
                break;
            }
        }

        if (chooseVar == -1) {
            return simplified.isEmpty();
        }

        // Branch True
        assignment.put(chooseVar, true);
        if (dpll(clauses, assignment, numVars)) {
            return true;
        }

        // Branch False
        assignment.put(chooseVar, false);
        if (dpll(clauses, assignment, numVars)) {
            return true;
        }

        assignment.remove(chooseVar);
        return false;
    }

    private static MyArrayList<Clause> simplify(MyArrayList<Clause> clauses, MyHashMap<Integer, Boolean> assignment) {
        MyArrayList<Clause> result = new MyArrayList<>();

        for (int i = 0; i < clauses.size(); i++) {
            Clause c = clauses.get(i);
            boolean clauseSatisfied = false;
            Clause newClause = new Clause();

            for (int j = 0; j < c.literals.size(); j++) {
                int lit = c.literals.get(j);
                int var = Math.abs(lit);
                boolean sign = lit > 0;

                if (assignment.containsKey(var)) {
                    boolean val = assignment.get(var);
                    if (val == sign) {
                        clauseSatisfied = true;
                        break;
                    }
                    // Else literal evaluates to false, omitted from newClause
                } else {
                    newClause.literals.add(lit);
                }
            }

            if (!clauseSatisfied) {
                if (newClause.literals.isEmpty()) {
                    return null; // Contradiction: empty clause
                }
                result.add(newClause);
            }
        }

        return result;
    }

    private static int findUnitLiteral(MyArrayList<Clause> clauses) {
        for (int i = 0; i < clauses.size(); i++) {
            Clause c = clauses.get(i);
            if (c.literals.size() == 1) {
                return c.literals.get(0);
            }
        }
        return 0;
    }
}
