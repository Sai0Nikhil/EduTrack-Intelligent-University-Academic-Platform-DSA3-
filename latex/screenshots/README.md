# EduTrack Screenshot Assets Directory

Drop your captured screenshots in this directory with the following filenames:

1. `fig01_gui_search.png`: GUI Tab 1 - Academic Search Interface (Aho-Corasick or KMP query results table)
2. `fig02_gui_plagiarism.png`: GUI Tab 2 - Plagiarism Detection Center (Alice vs. Bob with 278-char highlighted verbatim excerpt)
3. `fig03_gui_suffix.png`: GUI Tab 3 - Suffix Structure Indexer (SA-IS suffix array, Kasai LCP heights, and DAWG state metrics)
4. `fig04_gui_flow.png`: GUI Tab 4 - Resource Allocation (Dinic's blocking flow network and Bipartite faculty matching)
5. `fig05_gui_np.png`: GUI Tab 5 - Exam Timetabling (DPLL 3-SAT solution truth assignment and 2-approximation vertex cover)
6. `fig06_gui_parallel.png`: GUI Tab 6 - Parallel Analytics Dashboard (Randomized QuickSort merit rank and Blelloch prefix scan)
7. `fig07_gui_crypto.png`: GUI Tab 7 - Cryptographic Workbench (Miller-Rabin primality test for 1000000007 and composite witness)
8. `fig08_gui_verification.png`: GUI Tab 8 - Automated System Verification (All 16 algorithm PASS badges and latencies)
9. `fig09_cli_menu.png`: Terminal Console - Main Menu (Options 0 through 9)
10. `fig10_cli_plagiarism.png`: Terminal Console - Plagiarism Analysis Option 2 execution log
11. `fig11_gui_graph_canvas.png`: GUI Tab 4/5 - Interactive 2D Visual Graph Canvas (Dinic flow capacities & glowing 2-approx vertex cover proctor stations)
12. `fig12_gui_benchmark_arena.png`: GUI Tab 9 - Algorithm Benchmark Arena (Head-to-head algorithm race bar charts with microsecond timers and speedup multipliers)

## How to Enable Images in LaTeX:
In `latex/chapters/ch10_evaluation_screenshots.tex`, simply uncomment the line:
`%\includegraphics[width=0.92\textwidth]{screenshots/figXX_...png}`
in each corresponding figure environment.
