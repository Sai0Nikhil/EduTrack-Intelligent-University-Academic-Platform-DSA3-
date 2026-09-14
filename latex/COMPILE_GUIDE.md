# EduTrack LaTeX Report Compilation Guide

This LaTeX project is configured to produce a 40--50+ page comprehensive publication-grade academic report covering all theoretical, mathematical, architectural, and empirical aspects of the **EduTrack** platform.

---

## Method 1: Compile in Overleaf (Recommended - Fastest & Easiest)
1. In your file explorer, select the `latex` directory (or zip its contents: `main.tex`, `references.bib`, `chapters/`, `screenshots/`).
2. Open [Overleaf](https://www.overleaf.com/) and click **New Project** $\to$ **Upload Project**.
3. Upload the `.zip` file (or files).
4. Ensure the compiler is set to **pdfLaTeX** (default).
5. Click **Recompile**.
6. The full $\ge 30-50$ page PDF report will be generated immediately with Table of Contents, List of Figures, List of Tables, List of Algorithms, full mathematical proofs, algorithms, code listings, and formatted screenshot placeholder panels.

---

## Method 2: Compile Locally using TeX Live / MiKTeX / MacTeX
Run the standard two-pass compilation with BibTeX in PowerShell or Command Prompt:

```bash
cd c:\DSA_3\latex
pdflatex -synctex=1 -interaction=nonstopmode main.tex
bibtex main
pdflatex -synctex=1 -interaction=nonstopmode main.tex
pdflatex -synctex=1 -interaction=nonstopmode main.tex
```

---

## Document Structure & Chapter Map

- `main.tex`: Master configuration (geometry, packages, hyperref, code styling, theorem environments).
- `chapters/abstract.tex`: Executive Abstract and Problem Formulation.
- `chapters/ch01_introduction.tex`: Chapter 1: Introduction, Scope, and Pedagogical Constraints.
- `chapters/ch02_core_structures.tex`: Chapter 2: First-Principles Zero-`java.util.*` Infrastructure (Custom Collections, DSU, Min-Heap, Hash Map).
- `chapters/ch03_string_algorithms.tex`: Chapter 3: String Matching Algorithms (KMP, Z-Algorithm, Rabin-Karp, Aho-Corasick).
- `chapters/ch04_suffix_structures.tex`: Chapter 4: Suffix Structures \& Text Similarity (Suffix Array, $O(N)$ SA-IS, Kasai LCP, Suffix Automaton DAWG).
- `chapters/ch05_dynamic_programming.tex`: Chapter 5: Advanced Dynamic Programming (Levenshtein, Damerau-Levenshtein, Matrix Chain Multiplication, Bitmask DP TSP, Optimal BST).
- `chapters/ch06_network_flow.tex`: Chapter 6: Network Flow \& Bipartite Matching (Ford-Fulkerson, Edmonds-Karp, Dinic's Blocking Flow, König's Min-Cut).
- `chapters/ch07_np_completeness.tex`: Chapter 7: NP-Completeness, Reductions \& Approximation (DPLL SAT Solver, 3-SAT $\to$ CLIQUE $\to$ IS $\to$ VC Reduction Chain, 2-Approx Vertex Cover).
- `chapters/ch08_parallel_randomized.tex`: Chapter 8: Randomized \& Parallel Primitives (Randomized QuickSort, Reservoir Sampling Algorithm R, Miller-Rabin Primality, Blelloch Work-Efficient Scan, Parallel Reduce, Brent's Theorem).
- `chapters/ch09_system_architecture.tex`: Chapter 9: System Architecture, Data Engineering \& Dual Presentation Layer (CLI + Swing GUI).
- `chapters/ch10_evaluation_screenshots.tex`: Chapter 10: System Evaluation, Automated Verification Suite (16/16 Algorithms) \& Complete Screenshot Catalog.
- `chapters/ch11_conclusion.tex`: Chapter 11: Master Asymptotic Complexity Reference Table, In-Depth Algorithmic Observations \& Concluding Remarks.
- `references.bib`: Canonical literature references (CLRS, Kleinberg-Tardos, Erickson, Gusfield, Knuth, Rabin, Aho, Kasai, Nong, Dinic, Cook, Karp, etc.).
