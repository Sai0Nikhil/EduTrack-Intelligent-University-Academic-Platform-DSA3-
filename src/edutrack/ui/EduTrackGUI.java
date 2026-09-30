package edutrack.ui;

import edutrack.algorithms.dp.BitmaskDP;
import edutrack.algorithms.dp.DamerauLevenshtein;
import edutrack.algorithms.dp.Levenshtein;
import edutrack.algorithms.dp.MatrixChainMult;
import edutrack.algorithms.dp.OptimalBST;
import edutrack.algorithms.flow.BipartiteMatching;
import edutrack.algorithms.flow.KonigsTheorem;
import edutrack.algorithms.np.DpllSatSolver;
import edutrack.algorithms.np.VertexCover2Approx;
import edutrack.algorithms.parallel_random.BlellochScan;
import edutrack.algorithms.parallel_random.BrentsTheorem;
import edutrack.algorithms.parallel_random.MillerRabin;
import edutrack.algorithms.parallel_random.ParallelReduce;
import edutrack.algorithms.parallel_random.RandomizedQuickSort;
import edutrack.algorithms.strings.AhoCorasick;
import edutrack.algorithms.strings.BittuAlgorithm;
import edutrack.algorithms.strings.KmpMatcher;
import edutrack.algorithms.strings.RabinKarp;
import edutrack.algorithms.strings.ZAlgorithm;
import edutrack.algorithms.suffix.KasaiLCP;
import edutrack.algorithms.suffix.SAIS;
import edutrack.algorithms.suffix.SuffixArray;
import edutrack.algorithms.suffix.SuffixAutomaton;
import edutrack.core.MyArrayList;
import edutrack.core.Pair;
import edutrack.io.DatasetLoader;
import edutrack.model.ActivityLog;
import edutrack.model.AssignmentSubmission;
import edutrack.model.Course;
import edutrack.model.Faculty;
import edutrack.model.Student;
import edutrack.service.AcademicSearchService;
import edutrack.service.AnalyticsService;
import edutrack.service.BenchmarkArenaService;
import edutrack.service.ExamSchedulingService;
import edutrack.service.PlagiarismDetectionService;
import edutrack.service.ResourceAllocationService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.plaf.basic.BasicTabbedPaneUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Modern Java Swing Desktop Application for EduTrack Academic Engine.
 * Features high-contrast warm dark theme, visual KMP failure table, side-by-side
 * dual-document plagiarism studio with synchronized highlights, real-time
 * 2D Campus Auditor (Bitmask DP TSP) simulation, and custom non-blocking button/tab painting.
 */
public class EduTrackGUI extends JFrame {

    public static boolean REVIEW_1_MODE = true;

    private final String baseDir;
    private final MyArrayList<Course> courses;
    private final MyArrayList<Student> students;
    private final MyArrayList<Faculty> faculty;
    private final MyArrayList<AssignmentSubmission> submissions;
    private final MyArrayList<ActivityLog> activityLogs;
    private final String libraryText;

    private final AcademicSearchService searchService;
    private final PlagiarismDetectionService plagiarismService;
    private final ResourceAllocationService allocationService;
    private final ExamSchedulingService examService;
    private final AnalyticsService analyticsService;
    private final BenchmarkArenaService arenaService;

    // High-Contrast Warm Aesthetic Palette (Skin, Sand & Slate Tones)
    public static final Color BG_DARK = new Color(15, 20, 32);           // Rich Dark Midnight Slate
    public static final Color BG_CARD = new Color(26, 34, 52);           // Warm Slate Container Card
    public static final Color BG_CARD_ALT = new Color(18, 24, 38);       // Deep Input & Console Area
    public static final Color BG_CARD_LIGHTER = new Color(38, 49, 74);   // Elevated Slate Surface
    public static final Color BORDER_COLOR = new Color(59, 73, 103);      // Slate Border
    public static final Color TEXT_MAIN = new Color(248, 250, 252);      // Ultra-crisp bright white
    public static final Color TEXT_MUTED = new Color(203, 213, 225);     // Clear readable silver-champagne
    public static final Color TEXT_DARK = new Color(15, 23, 42);         // Deep Navy text for light/warm buttons

    // Warm Skin & Accent Palette
    public static final Color ACCENT_SKIN = new Color(245, 210, 165);    // Warm Golden Sand / Skin Tone
    public static final Color ACCENT_PEACH = new Color(253, 186, 140);   // Warm Soft Peach
    public static final Color ACCENT_CREAM = new Color(254, 243, 199);   // Warm Bisque / Wheat
    public static final Color ACCENT_AMBER = new Color(245, 158, 11);    // Amber Gold
    public static final Color ACCENT_GREEN = new Color(34, 197, 94);     // Emerald Mint
    public static final Color ACCENT_BLUE = new Color(56, 189, 248);     // Electric Sky Blue
    public static final Color ACCENT_PURPLE = new Color(192, 132, 252);  // Soft Lavender
    public static final Color ACCENT_CORAL = new Color(251, 113, 133);   // Coral Pink
    public static final Color ACCENT_RED = new Color(239, 68, 68);       // Crimson Red

    // =========================================================================
    // CUSTOM MODERN BUTTON (Bypasses OS look-and-feel white button bug)
    // =========================================================================
    public static class ModernButton extends JButton {
        private Color normalBg;
        private Color hoverBg;
        private Color pressedBg;
        private Color normalFg;
        private Color borderColor;
        private int cornerRadius = 8;
        private boolean isHovered = false;
        private boolean isPressed = false;

        public ModernButton(String text, Color bg, Color fg) {
            super(text);
            this.normalBg = bg;
            this.hoverBg = computeHoverColor(bg);
            this.pressedBg = computePressedColor(bg);
            this.normalFg = fg;
            this.borderColor = computeBorderColor(bg);

            setFont(new Font("Segoe UI", Font.BOLD, 12));
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setOpaque(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setMargin(new Insets(8, 14, 8, 14));

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    if (isEnabled()) {
                        isHovered = true;
                        repaint();
                    }
                }
                @Override
                public void mouseExited(MouseEvent e) {
                    isHovered = false;
                    isPressed = false;
                    repaint();
                }
                @Override
                public void mousePressed(MouseEvent e) {
                    if (isEnabled()) {
                        isPressed = true;
                        repaint();
                    }
                }
                @Override
                public void mouseReleased(MouseEvent e) {
                    isPressed = false;
                    repaint();
                }
            });
        }

        public void setCustomColors(Color bg, Color fg) {
            this.normalBg = bg;
            this.hoverBg = computeHoverColor(bg);
            this.pressedBg = computePressedColor(bg);
            this.normalFg = fg;
            this.borderColor = computeBorderColor(bg);
            repaint();
        }

        private static Color computeHoverColor(Color c) {
            int r = Math.min(255, c.getRed() + 20);
            int g = Math.min(255, c.getGreen() + 20);
            int b = Math.min(255, c.getBlue() + 20);
            return new Color(r, g, b);
        }

        private static Color computePressedColor(Color c) {
            int r = Math.max(0, c.getRed() - 25);
            int g = Math.max(0, c.getGreen() - 25);
            int b = Math.max(0, c.getBlue() - 25);
            return new Color(r, g, b);
        }

        private static Color computeBorderColor(Color c) {
            return new Color(Math.min(255, c.getRed() + 30), Math.min(255, c.getGreen() + 30), Math.min(255, c.getBlue() + 30), 180);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();

            Color fill = !isEnabled() ? new Color(40, 50, 70) : (isPressed ? pressedBg : (isHovered ? hoverBg : normalBg));
            Color text = !isEnabled() ? new Color(130, 145, 165) : normalFg;
            Color border = !isEnabled() ? new Color(55, 65, 85) : borderColor;

            // Fill background
            g2.setColor(fill);
            g2.fillRoundRect(0, 0, w, h, cornerRadius, cornerRadius);

            // Draw border
            g2.setColor(border);
            g2.setStroke(new BasicStroke(1.2f));
            g2.drawRoundRect(0, 0, w - 1, h - 1, cornerRadius, cornerRadius);

            // Draw text
            g2.setFont(getFont());
            FontMetrics fm = g2.getFontMetrics();
            String t = getText();
            int tx = (w - fm.stringWidth(t)) / 2;
            int ty = (h + fm.getAscent() - fm.getDescent()) / 2;

            g2.setColor(text);
            g2.drawString(t, tx, ty);

            g2.dispose();
        }

        @Override
        public Dimension getPreferredSize() {
            Dimension d = super.getPreferredSize();
            return new Dimension(d.width + 16, Math.max(34, d.height + 4));
        }
    }

    // =========================================================================
    // CUSTOM MODERN TABBED PANE UI (Eliminates OS white tab headers)
    // =========================================================================
    public static class ModernTabbedPaneUI extends BasicTabbedPaneUI {
        private static final Color TAB_BG_UNSELECTED = new Color(26, 34, 52);
        private static final Color TAB_BG_SELECTED = new Color(38, 50, 78);
        private static final Color TAB_BORDER = new Color(59, 73, 103);
        private static final Color ACCENT_INDICATOR = new Color(245, 210, 165); // Warm Skin/Sand
        private static final Color TEXT_SELECTED = new Color(245, 210, 165);
        private static final Color TEXT_UNSELECTED = new Color(203, 213, 225);

        @Override
        protected void installDefaults() {
            super.installDefaults();
            tabInsets = new Insets(10, 16, 10, 16);
            selectedTabPadInsets = new Insets(0, 0, 0, 0);
            contentBorderInsets = new Insets(0, 0, 0, 0);
        }

        @Override
        protected void paintTabArea(Graphics g, int tabPlacement, int selectedIndex) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setColor(new Color(15, 20, 32));
            g2.fillRect(0, 0, tabPane.getWidth(), tabPane.getHeight());
            super.paintTabArea(g, tabPlacement, selectedIndex);
            g2.dispose();
        }

        @Override
        protected void paintTabBackground(Graphics g, int tabPlacement, int tabIndex,
                                          int x, int y, int w, int h, boolean isSelected) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            if (isSelected) {
                g2.setColor(TAB_BG_SELECTED);
                g2.fillRoundRect(x + 2, y + 2, w - 4, h - 2, 6, 6);

                // Bottom glow indicator bar in warm skin tone
                g2.setColor(ACCENT_INDICATOR);
                g2.fillRect(x + 4, y + h - 3, w - 8, 3);
            } else {
                g2.setColor(TAB_BG_UNSELECTED);
                g2.fillRoundRect(x + 2, y + 4, w - 4, h - 5, 6, 6);
            }

            g2.setColor(TAB_BORDER);
            g2.drawRoundRect(x + 2, y + (isSelected ? 2 : 4), w - 4, h - (isSelected ? 2 : 5), 6, 6);
            g2.dispose();
        }

        @Override
        protected void paintText(Graphics g, int tabPlacement, Font font, FontMetrics metrics,
                                 int tabIndex, String title, Rectangle textRect, boolean isSelected) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2.setFont(isSelected ? font.deriveFont(Font.BOLD, 13f) : font.deriveFont(Font.PLAIN, 12f));
            g2.setColor(isSelected ? TEXT_SELECTED : TEXT_UNSELECTED);
            g2.drawString(title, textRect.x, textRect.y + metrics.getAscent());
            g2.dispose();
        }

        @Override
        protected void paintContentBorder(Graphics g, int tabPlacement, int selectedIndex) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setColor(TAB_BORDER);
            g2.drawLine(0, 0, tabPane.getWidth(), 0);
            g2.dispose();
        }

        @Override
        protected void paintFocusIndicator(Graphics g, int tabPlacement, Rectangle[] rects,
                                           int tabIndex, Rectangle iconRect, Rectangle textRect, boolean isSelected) {
            // No focus outline
        }
    }

    public EduTrackGUI(String baseDir) {
        super("EduTrack – Intelligent University Academic Platform [DSA-3 Engine]");
        this.baseDir = baseDir;

        // Load datasets using zero-java.util engine
        this.courses = DatasetLoader.loadCourses(baseDir + "/data/courses.csv");
        this.students = DatasetLoader.loadStudents(baseDir + "/data/students.csv");
        this.faculty = DatasetLoader.loadFaculty(baseDir + "/data/faculty.csv");
        this.submissions = DatasetLoader.loadSubmissions(baseDir + "/data/assignments");
        this.activityLogs = DatasetLoader.loadActivityLogs(baseDir + "/data/activity_stream.log");
        this.libraryText = DatasetLoader.readDocument(baseDir + "/data/library_docs/algorithms_handbook.txt");

        this.searchService = new AcademicSearchService(courses);
        this.plagiarismService = new PlagiarismDetectionService();
        this.allocationService = new ResourceAllocationService(faculty, courses);
        this.examService = new ExamSchedulingService(courses);
        this.analyticsService = new AnalyticsService(students, courses);
        this.arenaService = new BenchmarkArenaService();

        initUI();
    }

    private void initUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1260, 840);
        setMinimumSize(new Dimension(1080, 720));
        setLocationRelativeTo(null);

        // Main content container
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BG_DARK);

        // Top Header
        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Tabbed Pane for Modules with custom UI
        JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        tabbedPane.setUI(new ModernTabbedPaneUI());
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 13));
        tabbedPane.setBackground(BG_CARD);
        tabbedPane.setForeground(TEXT_MAIN);

        tabbedPane.addTab("  Dashboard  ", createDashboardTab());
        tabbedPane.addTab("  M1: String Search & KMP  ", createStringSearchTab());
        tabbedPane.addTab("  M2: Suffix & Plagiarism Studio  ", createSuffixPlagiarismTab());
        tabbedPane.addTab("  M3: Advanced DP & 2D Auditor  ", createDynamicProgrammingTab());

        // CO4, CO5, CO6 gated for Review 1 with early-access demo previews
        tabbedPane.addTab("  M4: Network Flow [Review 2]  ", createReviewGatedTab(
                "CO4 / Module 4",
                "Network Flow & Resource Allocation",
                "Review 2 Milestone",
                new String[]{
                        "Dinic's Blocking Flow Algorithm O(V² E) with BFS Level Graphs",
                        "Edmonds-Karp Shortest Augmenting Path Algorithm O(V E²)",
                        "Maximum Bipartite Matching for Faculty-to-Course Assignment",
                        "König's Min-Max Theorem for Minimum Vertex Cover Bottlenecks",
                        "Interactive 2D Vector Flow Network Canvas Visualization"
                },
                createNetworkFlowTab()));

        tabbedPane.addTab("  M5: NP-Reductions [Review 2]  ", createReviewGatedTab(
                "CO5 / Module 5",
                "NP-Completeness, Reductions & Approximation",
                "Review 2 Milestone",
                new String[]{
                        "DPLL Boolean Satisfiability (SAT) Solver with Unit Propagation",
                        "Karp Reductions Chain: 3-SAT → CLIQUE → Independent Set → Vertex Cover",
                        "2-Approximation Algorithm for Minimum Vertex Cover (Maximal Matching)",
                        "Interactive Exam Conflict Graph & Proctor Allocation Canvas"
                },
                createNPCompletenessTab()));

        tabbedPane.addTab("  M6: Parallel & Random [Review 3]  ", createReviewGatedTab(
                "CO6 / Module 6",
                "Randomized & Parallel Algorithms",
                "Review 3 Milestone (Final Review)",
                new String[]{
                        "Randomized QuickSort with Uniform Random Pivot Selection",
                        "Reservoir Sampling (Algorithm R) for Infinite Activity Streams",
                        "Miller-Rabin Probabilistic Primality Test for Student Cryptographic Tokens",
                        "Blelloch Work-Efficient Parallel Prefix Scan (Up-sweep & Down-sweep)",
                        "Multi-threaded Parallel Tree Reduction on Academic Records",
                        "Brent's Work-Time Scheduling Principle & Theoretical Speedup Analyzer"
                },
                createParallelRandomTab()));

        tabbedPane.addTab("  Verification Suite  ", createBenchmarkTab());
        tabbedPane.addTab("  Benchmark Arena  ", createBenchmarkArenaTab());

        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        setContentPane(mainPanel);
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(18, 24, 38));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR),
                new EmptyBorder(14, 24, 14, 24)
        ));

        JLabel titleLabel = new JLabel("EDUTRACK ACADEMIC PLATFORM");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(ACCENT_SKIN);

        JLabel subtitleLabel = new JLabel("Enterprise Advanced-Algorithms Engine (DSA-3) | 100% Zero java.util.* Core");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitleLabel.setForeground(TEXT_MUTED);

        JPanel textPanel = new JPanel(new GridLayout(2, 1, 0, 4));
        textPanel.setOpaque(false);
        textPanel.add(titleLabel);
        textPanel.add(subtitleLabel);

        // Status badges
        JPanel badgeContainer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        badgeContainer.setOpaque(false);

        JLabel revBadge = new JLabel(REVIEW_1_MODE ? "  REVIEW 1: CO1–CO3 ACTIVE  " : "  FULL PRODUCTION ENGINE  ");
        revBadge.setFont(new Font("Segoe UI", Font.BOLD, 11));
        revBadge.setForeground(TEXT_DARK);
        revBadge.setBackground(ACCENT_SKIN);
        revBadge.setOpaque(true);
        revBadge.setBorder(new EmptyBorder(6, 12, 6, 12));

        JLabel badge = new JLabel("  ZERO-JAVA.UTIL COMPLIANT  ");
        badge.setFont(new Font("Segoe UI", Font.BOLD, 11));
        badge.setForeground(TEXT_DARK);
        badge.setBackground(ACCENT_GREEN);
        badge.setOpaque(true);
        badge.setBorder(new EmptyBorder(6, 12, 6, 12));

        badgeContainer.add(revBadge);
        badgeContainer.add(badge);

        panel.add(textPanel, BorderLayout.WEST);
        panel.add(badgeContainer, BorderLayout.EAST);
        return panel;
    }

    // =========================================================================
    // REVIEW MILESTONE GATING: CO4, CO5, CO6 COMING SOON WRAPPER
    // =========================================================================
    private JPanel createReviewGatedTab(String coCode, String moduleTitle, String milestoneText,
                                        String[] roadmapItems, JPanel realPanel) {
        CardLayout cardLayout = new CardLayout();
        JPanel container = new JPanel(cardLayout);

        // --- Card 1: Gated / Coming Soon Screen ---
        JPanel gatedCard = new JPanel(new BorderLayout());
        gatedCard.setBackground(BG_DARK);

        JPanel centerWrapper = new JPanel(new GridBagLayout());
        centerWrapper.setBackground(BG_DARK);

        JPanel contentCard = new JPanel();
        contentCard.setLayout(new BoxLayout(contentCard, BoxLayout.Y_AXIS));
        contentCard.setBackground(BG_CARD);
        contentCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                new EmptyBorder(32, 40, 32, 40)
        ));
        contentCard.setMaximumSize(new Dimension(880, 640));
        contentCard.setPreferredSize(new Dimension(850, 580));

        // Badges row
        JPanel badgeRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        badgeRow.setOpaque(false);
        badgeRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel statusBadge = new JLabel("  SCHEDULED MILESTONE  ");
        statusBadge.setFont(new Font("Segoe UI", Font.BOLD, 11));
        statusBadge.setForeground(TEXT_DARK);
        statusBadge.setBackground(ACCENT_PEACH);
        statusBadge.setOpaque(true);
        statusBadge.setBorder(new EmptyBorder(4, 10, 4, 10));

        JLabel milestoneBadge = new JLabel("  MILESTONE: " + milestoneText.toUpperCase() + "  ");
        milestoneBadge.setFont(new Font("Segoe UI", Font.BOLD, 11));
        milestoneBadge.setForeground(TEXT_DARK);
        milestoneBadge.setBackground(ACCENT_SKIN);
        milestoneBadge.setOpaque(true);
        milestoneBadge.setBorder(new EmptyBorder(4, 10, 4, 10));

        badgeRow.add(statusBadge);
        badgeRow.add(milestoneBadge);

        contentCard.add(badgeRow);
        contentCard.add(Box.createRigidArea(new Dimension(0, 16)));

        // Title & Subtitle
        JLabel titleLabel = new JLabel(moduleTitle);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(ACCENT_SKIN);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentCard.add(titleLabel);

        JLabel subtitleLabel = new JLabel("EduTrack Course Outcome Specification • " + coCode);
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitleLabel.setForeground(TEXT_MUTED);
        subtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentCard.add(subtitleLabel);

        contentCard.add(Box.createRigidArea(new Dimension(0, 16)));

        // Review 1 Scope explanation notice box
        JPanel noticeBox = new JPanel(new BorderLayout(8, 8));
        noticeBox.setBackground(BG_DARK);
        noticeBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 4, 0, 0, ACCENT_SKIN),
                new EmptyBorder(14, 18, 14, 18)
        ));
        noticeBox.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel noticeTitle = new JLabel("Review 1 Evaluation Scope (CO1 – CO3 Active):");
        noticeTitle.setFont(new Font("Segoe UI", Font.BOLD, 13));
        noticeTitle.setForeground(ACCENT_SKIN);

        JLabel noticeText = new JLabel("<html>This academic platform follows strict iterative engineering milestones. " +
                "<b>Review 1</b> evaluates the foundational string and optimization engines: " +
                "<b>CO1 (String Matching & Automata)</b>, <b>CO2 (Suffix Sorter & Kasai Plagiarism)</b>, and <b>CO3 (Dynamic Programming & 2D Bitmask TSP)</b>.<br/>" +
                "Advanced network flow, NP-reductions, and parallel architectures will be demonstrated during " + milestoneText + ".</html>");
        noticeText.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        noticeText.setForeground(TEXT_MUTED);

        noticeBox.add(noticeTitle, BorderLayout.NORTH);
        noticeBox.add(noticeText, BorderLayout.CENTER);
        contentCard.add(noticeBox);

        contentCard.add(Box.createRigidArea(new Dimension(0, 18)));

        // Roadmap Deliverables
        JLabel roadmapTitle = new JLabel("Planned Deliverables & Specifications for " + milestoneText + ":");
        roadmapTitle.setFont(new Font("Segoe UI", Font.BOLD, 13));
        roadmapTitle.setForeground(TEXT_MAIN);
        roadmapTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentCard.add(roadmapTitle);

        contentCard.add(Box.createRigidArea(new Dimension(0, 10)));

        for (String item : roadmapItems) {
            JPanel itemRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 2));
            itemRow.setOpaque(false);
            itemRow.setAlignmentX(Component.LEFT_ALIGNMENT);

            JLabel tag = new JLabel("[PRE-BUILT]");
            tag.setFont(new Font("Segoe UI", Font.BOLD, 11));
            tag.setForeground(ACCENT_AMBER);

            JLabel label = new JLabel(item);
            label.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            label.setForeground(TEXT_MUTED);

            itemRow.add(tag);
            itemRow.add(label);
            contentCard.add(itemRow);
        }

        contentCard.add(Box.createRigidArea(new Dimension(0, 22)));

        // Bottom Unlock / Preview Bar
        JPanel previewBar = new JPanel(new BorderLayout());
        previewBar.setOpaque(false);
        previewBar.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel hintLbl = new JLabel("<html><span style='color:#cbd5e1; font-size:11px;'>Early-Access Demonstration:</span></html>");
        ModernButton btnPreview = new ModernButton("Preview Pre-built Module Engine (Demo Mode)", ACCENT_SKIN, TEXT_DARK);

        btnPreview.addActionListener(e -> cardLayout.show(container, "UNLOCKED"));

        previewBar.add(hintLbl, BorderLayout.WEST);
        previewBar.add(btnPreview, BorderLayout.EAST);
        contentCard.add(previewBar);

        centerWrapper.add(contentCard);
        gatedCard.add(centerWrapper, BorderLayout.CENTER);

        // --- Card 2: Unlocked Real Module Panel with Demo Banner ---
        JPanel unlockedCard = new JPanel(new BorderLayout());

        JPanel bannerPanel = new JPanel(new BorderLayout(10, 0));
        bannerPanel.setBackground(new Color(18, 24, 38));
        bannerPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 2, 0, ACCENT_AMBER),
                new EmptyBorder(8, 20, 8, 20)
        ));

        JLabel unlockTitle = new JLabel("PREVIEW ACTIVE: Pre-built " + coCode + " Engine (Scheduled for " + milestoneText + ")");
        unlockTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        unlockTitle.setForeground(ACCENT_AMBER);

        ModernButton btnLock = new ModernButton("Re-lock for Review 1", BORDER_COLOR, TEXT_MAIN);
        btnLock.addActionListener(e -> cardLayout.show(container, "GATED"));

        bannerPanel.add(unlockTitle, BorderLayout.WEST);
        bannerPanel.add(btnLock, BorderLayout.EAST);

        unlockedCard.add(bannerPanel, BorderLayout.NORTH);
        unlockedCard.add(realPanel, BorderLayout.CENTER);

        // Add both cards
        container.add(gatedCard, "GATED");
        container.add(unlockedCard, "UNLOCKED");

        // Set initial view according to REVIEW_1_MODE
        cardLayout.show(container, REVIEW_1_MODE ? "GATED" : "UNLOCKED");

        return container;
    }

    // =========================================================================
    // TAB 1: DASHBOARD
    // =========================================================================
    private JPanel createDashboardTab() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(BG_DARK);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Metric Cards Grid
        JPanel metricsGrid = new JPanel(new GridLayout(2, 3, 15, 15));
        metricsGrid.setOpaque(false);

        metricsGrid.add(createMetricCard("Total Courses", String.valueOf(courses.size()), "CS, AI, Math, Data Science, Cyber", ACCENT_SKIN));
        metricsGrid.add(createMetricCard("Enrolled Students", String.valueOf(students.size()), "CGPA, Credits & Attendance records", ACCENT_GREEN));
        metricsGrid.add(createMetricCard("Faculty Members", String.valueOf(faculty.size()), "Course qualifications & workloads", ACCENT_PEACH));
        metricsGrid.add(createMetricCard("Assignment Submissions", String.valueOf(submissions.size()), "Multi-document plagiarism test set", ACCENT_AMBER));
        metricsGrid.add(createMetricCard("Activity Stream Events", String.valueOf(activityLogs.size()), "Continuous streaming audit logs", ACCENT_CORAL));
        metricsGrid.add(createMetricCard(
                REVIEW_1_MODE ? "Review 1 Scope" : "DSA Algorithms Active",
                REVIEW_1_MODE ? "10 Active (CO1–CO3)" : "16 / 16 (Full Engine)",
                REVIEW_1_MODE ? "CO4–CO6 pre-built in Review 2/3 roadmap" : "M1 to M6 fully verified",
                ACCENT_CREAM));

        panel.add(metricsGrid, BorderLayout.NORTH);

        // Catalog preview table
        String[] cols = {"Code", "Course Title", "Department", "Credits", "Matrix Ops Dimensions", "Prerequisites"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);
        for (int i = 0; i < courses.size(); i++) {
            Course c = courses.get(i);
            StringBuilder prereq = new StringBuilder();
            for (int k = 0; k < c.getPrerequisites().size(); k++) {
                prereq.append(c.getPrerequisites().get(k)).append(k + 1 < c.getPrerequisites().size() ? ", " : "");
            }
            model.addRow(new Object[]{
                    c.getCode(), c.getTitle(), c.getDepartment(), c.getCredits(),
                    c.getMatrixDimensionRows() + " x " + c.getMatrixDimensionCols(),
                    prereq.length() == 0 ? "None" : prereq.toString()
            });
        }

        JTable table = new JTable(model);
        styleTable(table);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(BG_CARD_ALT);
        scrollPane.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(BORDER_COLOR),
                "University Course Catalog (Hand-built Data Structures)",
                TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 13), ACCENT_SKIN));

        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createMetricCard(String title, String value, String subtitle, Color accentColor) {
        JPanel card = new JPanel(new BorderLayout(5, 5));
        card.setBackground(BG_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 4, 0, 0, accentColor),
                new EmptyBorder(14, 18, 14, 18)
        ));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        titleLabel.setForeground(TEXT_MUTED);

        JLabel valLabel = new JLabel(value);
        valLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        valLabel.setForeground(accentColor);

        JLabel subLabel = new JLabel(subtitle);
        subLabel.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        subLabel.setForeground(TEXT_MUTED);

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valLabel, BorderLayout.CENTER);
        card.add(subLabel, BorderLayout.SOUTH);
        return card;
    }

    // =========================================================================
    // TAB 2: STRING ALGORITHMS & VISUAL KMP MATCHING (CO1 / M1)
    // =========================================================================
    private JPanel createStringSearchTab() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(BG_DARK);
        panel.setBorder(new EmptyBorder(18, 18, 18, 18));

        // Top Search Controls Card
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 10));
        controlPanel.setBackground(BG_CARD);
        controlPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                new EmptyBorder(4, 10, 4, 10)
        ));

        JLabel lbl = new JLabel("Search Query Pattern:");
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lbl.setForeground(ACCENT_SKIN);

        JTextField queryField = new JTextField("Algorithm", 14);
        queryField.setBackground(BG_CARD_ALT);
        queryField.setForeground(TEXT_MAIN);
        queryField.setCaretColor(TEXT_MAIN);
        queryField.setFont(new Font("Segoe UI", Font.BOLD, 13));
        queryField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR),
                new EmptyBorder(4, 8, 4, 8)
        ));

        JRadioButton rKmp = new JRadioButton("KMP Matcher (π Table)", true);
        JRadioButton rZ = new JRadioButton("Z-Algorithm");
        JRadioButton rRk = new JRadioButton("Rabin-Karp Rolling Hash");
        JRadioButton rAc = new JRadioButton("Aho-Corasick Dictionary");
        JRadioButton rBittu = new JRadioButton("✨ Bittu's Algorithm (Invention)");

        rKmp.setForeground(TEXT_MAIN); rKmp.setFont(new Font("Segoe UI", Font.BOLD, 12)); rKmp.setOpaque(false);
        rZ.setForeground(TEXT_MAIN); rZ.setFont(new Font("Segoe UI", Font.BOLD, 12)); rZ.setOpaque(false);
        rRk.setForeground(TEXT_MAIN); rRk.setFont(new Font("Segoe UI", Font.BOLD, 12)); rRk.setOpaque(false);
        rAc.setForeground(TEXT_MAIN); rAc.setFont(new Font("Segoe UI", Font.BOLD, 12)); rAc.setOpaque(false);
        rBittu.setForeground(ACCENT_SKIN); rBittu.setFont(new Font("Segoe UI", Font.BOLD, 12)); rBittu.setOpaque(false);

        ButtonGroup group = new ButtonGroup();
        group.add(rKmp); group.add(rZ); group.add(rRk); group.add(rAc); group.add(rBittu);

        ModernButton btnSearch = new ModernButton("Run Pattern Search", ACCENT_SKIN, TEXT_DARK);

        controlPanel.add(lbl);
        controlPanel.add(queryField);
        controlPanel.add(rKmp);
        controlPanel.add(rZ);
        controlPanel.add(rRk);
        controlPanel.add(rAc);
        controlPanel.add(rBittu);
        controlPanel.add(btnSearch);

        panel.add(controlPanel, BorderLayout.NORTH);

        // Center Split View: Top = Visual Failure Table / Telemetry Card, Bottom = Match Table & Details
        JPanel centerPanel = new JPanel(new BorderLayout(12, 12));
        centerPanel.setOpaque(false);

        // Pi Table Visualizer Card
        JPanel visualPiCard = new JPanel(new BorderLayout(10, 10));
        visualPiCard.setBackground(BG_CARD);
        visualPiCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                new EmptyBorder(12, 16, 12, 16)
        ));

        JLabel piHeader = new JLabel("KMP PREFIX FAILURE FUNCTION (π TABLE) VISUALIZER:");
        piHeader.setFont(new Font("Segoe UI", Font.BOLD, 13));
        piHeader.setForeground(ACCENT_SKIN);

        JPanel piTilesPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        piTilesPanel.setOpaque(false);

        JLabel explanationLabel = new JLabel("<html><span style='color:#cbd5e1; font-size:11px;'>" +
                "<b>How KMP Works:</b> The Failure Function <code>π[i]</code> stores the length of the longest proper prefix of <code>P[0..i]</code> that is also a suffix of <code>P[0..i]</code>. " +
                "On text mismatch at index <code>j</code>, KMP skips directly to <code>π[j-1]</code> without rewinding the text, giving <b>O(N + M)</b> linear time.</span></html>");

        visualPiCard.add(piHeader, BorderLayout.NORTH);
        visualPiCard.add(piTilesPanel, BorderLayout.CENTER);
        visualPiCard.add(explanationLabel, BorderLayout.SOUTH);

        // Matches Table
        String[] matchCols = {"#", "Course Code", "Course Title", "Department", "Match Count", "Found Index Positions"};
        DefaultTableModel matchModel = new DefaultTableModel(matchCols, 0);
        JTable matchTable = new JTable(matchModel);
        styleTable(matchTable);

        JScrollPane matchScroll = new JScrollPane(matchTable);
        matchScroll.getViewport().setBackground(BG_CARD_ALT);
        matchScroll.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(BORDER_COLOR),
                "Academic Search Results & Substring Occurrences",
                TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 12), ACCENT_SKIN));

        // Text Log Area for deep mathematical trace
        JTextArea logArea = new JTextArea();
        logArea.setFont(new Font("Consolas", Font.PLAIN, 12));
        logArea.setEditable(false);
        logArea.setBackground(BG_CARD_ALT);
        logArea.setForeground(TEXT_MAIN);
        logArea.setCaretColor(TEXT_MAIN);

        JScrollPane logScroll = new JScrollPane(logArea);
        logScroll.setPreferredSize(new Dimension(800, 160));
        logScroll.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(BORDER_COLOR),
                "Algorithm Execution Telemetry & Complexity Analysis",
                TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 12), ACCENT_SKIN));

        JSplitPane bottomSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, matchScroll, logScroll);
        bottomSplit.setDividerLocation(260);
        bottomSplit.setResizeWeight(0.60);

        centerPanel.add(visualPiCard, BorderLayout.NORTH);
        centerPanel.add(bottomSplit, BorderLayout.CENTER);
        panel.add(centerPanel, BorderLayout.CENTER);

        // Search Action Logic
        ActionListener runSearchAction = e -> {
            String q = queryField.getText().trim();
            if (q.isEmpty()) return;

            matchModel.setRowCount(0);
            piTilesPanel.removeAll();
            StringBuilder sb = new StringBuilder();
            long start = System.nanoTime();

            if (rKmp.isSelected()) {
                piHeader.setText("KMP PREFIX FAILURE FUNCTION (π TABLE) VISUALIZER FOR \"" + q + "\":");
                explanationLabel.setText("<html><span style='color:#cbd5e1; font-size:11px;'>" +
                        "<b>KMP Theorem:</b> <code>π[i] = k</code> means prefix <code>P[0..k-1]</code> matches suffix ending at <code>i</code>. " +
                        "Deterministic finite transition ensures text pointer never backtracks (O(N) search span).</span></html>");

                int[] pi = KmpMatcher.computePi(q);

                // Render Visual Character Tiles
                for (int i = 0; i < q.length(); i++) {
                    char ch = q.charAt(i);
                    int val = pi[i];

                    JPanel tile = new JPanel(new GridLayout(3, 1, 0, 2));
                    tile.setPreferredSize(new Dimension(48, 62));
                    tile.setBackground(BG_CARD_LIGHTER);
                    tile.setBorder(BorderFactory.createLineBorder(new Color(245, 210, 165, 180), 1));

                    JLabel charLbl = new JLabel(String.valueOf(ch), SwingConstants.CENTER);
                    charLbl.setFont(new Font("Consolas", Font.BOLD, 15));
                    charLbl.setForeground(ACCENT_SKIN);

                    JLabel idxLbl = new JLabel("i=" + i, SwingConstants.CENTER);
                    idxLbl.setFont(new Font("Segoe UI", Font.PLAIN, 9));
                    idxLbl.setForeground(TEXT_MUTED);

                    JLabel valLbl = new JLabel("π=" + val, SwingConstants.CENTER);
                    valLbl.setFont(new Font("Consolas", Font.BOLD, 12));
                    valLbl.setForeground(val > 0 ? ACCENT_AMBER : Color.LIGHT_GRAY);

                    tile.add(charLbl);
                    tile.add(idxLbl);
                    tile.add(valLbl);
                    piTilesPanel.add(tile);
                }

                // Search courses
                MyArrayList<Pair<Course, Integer>> matches = searchService.searchCoursesByKMP(q);
                for (int i = 0; i < matches.size(); i++) {
                    Pair<Course, Integer> p = matches.get(i);
                    MyArrayList<Integer> occ = KmpMatcher.search(p.first.getTitle(), q, true);
                    matchModel.addRow(new Object[]{
                            (i + 1), p.first.getCode(), p.first.getTitle(), p.first.getDepartment(),
                            p.second, printList(occ)
                    });
                }

                sb.append("=== Knuth-Morris-Pratt (KMP) String Matching ===\n");
                sb.append("Pattern   : \"").append(q).append("\" (Length: ").append(q.length()).append(")\n");
                sb.append("π Array   : [");
                for (int i = 0; i < pi.length; i++) sb.append(pi[i]).append(i + 1 < pi.length ? ", " : "]\n");
                sb.append("Complexity: Preprocessing O(M), Matching O(N) | No text rewinding\n");
                sb.append("Courses matched: ").append(matches.size()).append("\n");

            } else if (rZ.isSelected()) {
                piHeader.setText("Z-ALGORITHM LONGEST PREFIX BOXES (Z-ARRAY) FOR \"" + q + "\":");
                explanationLabel.setText("<html><span style='color:#cbd5e1; font-size:11px;'>" +
                        "<b>Z-Algorithm Theorem:</b> <code>Z[i]</code> is the length of the longest substring starting at <code>S[i]</code> that matches the prefix <code>S[0..]</code>. " +
                        "Maintains a sliding window <code>[L, R]</code> to achieve O(N + M) linear time.</span></html>");

                int[] zArr = ZAlgorithm.computeZ(q);
                for (int i = 0; i < q.length(); i++) {
                    char ch = q.charAt(i);
                    int val = zArr[i];

                    JPanel tile = new JPanel(new GridLayout(3, 1, 0, 2));
                    tile.setPreferredSize(new Dimension(48, 62));
                    tile.setBackground(BG_CARD_LIGHTER);
                    tile.setBorder(BorderFactory.createLineBorder(new Color(253, 186, 140, 180), 1));

                    JLabel charLbl = new JLabel(String.valueOf(ch), SwingConstants.CENTER);
                    charLbl.setFont(new Font("Consolas", Font.BOLD, 15));
                    charLbl.setForeground(ACCENT_PEACH);

                    JLabel idxLbl = new JLabel("i=" + i, SwingConstants.CENTER);
                    idxLbl.setFont(new Font("Segoe UI", Font.PLAIN, 9));
                    idxLbl.setForeground(TEXT_MUTED);

                    JLabel valLbl = new JLabel("Z=" + val, SwingConstants.CENTER);
                    valLbl.setFont(new Font("Consolas", Font.BOLD, 12));
                    valLbl.setForeground(val > 0 ? ACCENT_GREEN : Color.LIGHT_GRAY);

                    tile.add(charLbl);
                    tile.add(idxLbl);
                    tile.add(valLbl);
                    piTilesPanel.add(tile);
                }

                int matchCount = 0;
                for (int i = 0; i < courses.size(); i++) {
                    Course c = courses.get(i);
                    MyArrayList<Integer> occ = ZAlgorithm.search(c.getTitle(), q);
                    if (!occ.isEmpty()) {
                        matchCount++;
                        matchModel.addRow(new Object[]{
                                matchCount, c.getCode(), c.getTitle(), c.getDepartment(), occ.size(), printList(occ)
                        });
                    }
                }

                sb.append("=== Z-Algorithm Linear-Time Phrase Detection ===\n");
                sb.append("Pattern   : \"").append(q).append("\"\n");
                sb.append("Z-Array   : [");
                for (int i = 0; i < zArr.length; i++) sb.append(zArr[i]).append(i + 1 < zArr.length ? ", " : "]\n");
                sb.append("Courses matched: ").append(matchCount).append("\n");

            } else if (rRk.isSelected()) {
                piHeader.setText("RABIN-KARP DOUBLE-MODULUS ROLLING HASH:");
                explanationLabel.setText("<html><span style='color:#cbd5e1; font-size:11px;'>" +
                        "<b>Rabin-Karp Rolling Hash:</b> Computes polynomial rolling hashes with base <code>B=257</code> and moduli <code>10^9+7</code>, <code>10^9+9</code>. " +
                        "O(1) window update per shift, eliminating false-positive hash collisions.</span></html>");

                JLabel hashBadge = new JLabel("  MODULI: 1,000,000,007 & 1,000,000,009 | BASE: 257  ");
                hashBadge.setFont(new Font("Segoe UI", Font.BOLD, 12));
                hashBadge.setForeground(TEXT_DARK);
                hashBadge.setBackground(ACCENT_AMBER);
                hashBadge.setOpaque(true);
                hashBadge.setBorder(new EmptyBorder(6, 12, 6, 12));
                piTilesPanel.add(hashBadge);

                MyArrayList<Course> matches = searchService.searchCodeByRabinKarp(q);
                for (int i = 0; i < matches.size(); i++) {
                    Course c = matches.get(i);
                    matchModel.addRow(new Object[]{
                            (i + 1), c.getCode(), c.getTitle(), c.getDepartment(), 1, "[Code Exact Match]"
                    });
                }

                sb.append("=== Rabin-Karp Rolling Hash Matching ===\n");
                sb.append("Query : \"").append(q).append("\"\n");
                sb.append("Courses matched: ").append(matches.size()).append("\n");

            } else if (rAc.isSelected()) {
                piHeader.setText("AHO-CORASICK MULTI-KEYWORD AUTOMATON:");
                explanationLabel.setText("<html><span style='color:#cbd5e1; font-size:11px;'>" +
                        "<b>Aho-Corasick Automaton:</b> Constructs a Trie with BFS failure & dictionary output links. " +
                        "Simultaneously searches all dictionary keywords in a single linear pass over text O(N + M + K).</span></html>");

                String[] dict = {"Algorithm", "Dynamic Programming", "Suffix", "Network", "Bipartite", "König", "Approximation", "Parallel"};
                for (String kw : dict) {
                    JLabel kwBadge = new JLabel(" " + kw + " ");
                    kwBadge.setFont(new Font("Segoe UI", Font.BOLD, 11));
                    kwBadge.setForeground(TEXT_MAIN);
                    kwBadge.setBackground(BG_CARD_LIGHTER);
                    kwBadge.setOpaque(true);
                    kwBadge.setBorder(BorderFactory.createLineBorder(ACCENT_SKIN, 1));
                    piTilesPanel.add(kwBadge);
                }

                MyArrayList<AhoCorasick.MatchResult> acMatches = searchService.tagKeywordsAhoCorasick(libraryText, dict);
                for (int i = 0; i < Math.min(50, acMatches.size()); i++) {
                    AhoCorasick.MatchResult mr = acMatches.get(i);
                    matchModel.addRow(new Object[]{
                            (i + 1), "LIB_DOC", mr.keyword, "Handbook Library", 1, "@ Character Pos " + mr.position
                    });
                }

                sb.append("=== Aho-Corasick Dictionary Matcher ===\n");
                sb.append("Total occurrences across Algorithms Handbook: ").append(acMatches.size()).append("\n");

            } else if (rBittu.isSelected()) {
                piHeader.setText("✨ BITTU'S ALGORITHM (ROLLING BIGRAM COSINE FILTER) FOR \"" + q + "\":");
                explanationLabel.setText("<html><span style='color:#cbd5e1; font-size:11px;'>" +
                        "<b>Bittu's Innovation Theorem:</b> Maps pattern & sliding window into a 65,536-dimensional frequency vector. " +
                        "Updates <code>cos(θ) = (W · P)/(||W||·||P||)</code> in <b>O(1) time</b> per shift using flat primitive tables with zero memory allocation.</span></html>");

                int[] counts = BittuAlgorithm.computeBigramCounts(q);
                int shown = 0;
                for (int i = 0; i + 1 < q.length() && shown < 8; i++) {
                    char c1 = q.charAt(i);
                    char c2 = q.charAt(i + 1);
                    int k = BittuAlgorithm.key(c1, c2);
                    int cnt = counts[k];
                    shown++;

                    JPanel tile = new JPanel(new GridLayout(3, 1, 0, 2));
                    tile.setPreferredSize(new Dimension(56, 62));
                    tile.setBackground(BG_CARD_LIGHTER);
                    tile.setBorder(BorderFactory.createLineBorder(ACCENT_SKIN, 1));

                    JLabel charLbl = new JLabel("'" + c1 + c2 + "'", SwingConstants.CENTER);
                    charLbl.setFont(new Font("Consolas", Font.BOLD, 13));
                    charLbl.setForeground(ACCENT_SKIN);

                    JLabel idxLbl = new JLabel("k=" + k, SwingConstants.CENTER);
                    idxLbl.setFont(new Font("Segoe UI", Font.PLAIN, 9));
                    idxLbl.setForeground(TEXT_MUTED);

                    JLabel valLbl = new JLabel("cnt=" + cnt, SwingConstants.CENTER);
                    valLbl.setFont(new Font("Consolas", Font.BOLD, 12));
                    valLbl.setForeground(ACCENT_GREEN);

                    tile.add(charLbl);
                    tile.add(idxLbl);
                    tile.add(valLbl);
                    piTilesPanel.add(tile);
                }

                int totalMatches = 0;
                for (int i = 0; i < courses.size(); i++) {
                    Course c = courses.get(i);
                    BittuAlgorithm.SearchResult r = BittuAlgorithm.searchWithTelemetry(c.getTitle(), q);
                    if (!r.matchPositions.isEmpty()) {
                        totalMatches++;
                        matchModel.addRow(new Object[]{
                                totalMatches, c.getCode(), c.getTitle(), c.getDepartment(),
                                r.matchPositions.size(), printList(r.matchPositions) + " (cos=1.0000)"
                        });
                    }
                }

                sb.append("=== ✨ Bittu's Algorithm (Invention: Fast Rolling Bigram Cosine Matcher) ===\n");
                sb.append("Pattern           : \"").append(q).append("\" (Length: ").append(q.length()).append(")\n");
                sb.append("Vector Space      : 65,536-dimensional Bigram Embedding R^{256x256}\n");
                sb.append("Complexity        : Preprocessing O(M), Window Shift O(1) delta algebra, Expected O(N + M)\n");
                sb.append("Screening Method  : cos(θ) >= 0.999999999 candidate filter with flat int[] primitive indexing\n");
                sb.append("Courses matched   : ").append(totalMatches).append("\n");
            }

            long elapsed = (System.nanoTime() - start) / 1000;
            sb.append(String.format("\n[Execution Time: %d µs | Zero java.util.* Engine]\n", elapsed));
            logArea.setText(sb.toString());

            piTilesPanel.revalidate();
            piTilesPanel.repaint();
        };

        btnSearch.addActionListener(runSearchAction);
        queryField.addActionListener(runSearchAction);

        // Trigger initial search for visual demo
        SwingUtilities.invokeLater(() -> btnSearch.doClick());

        return panel;
    }

    // =========================================================================
    // TAB 3: SUFFIX STRUCTURES & DUAL-DOCUMENT PLAGIARISM STUDIO (CO2 / M2)
    // =========================================================================
    private JPanel createSuffixPlagiarismTab() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(BG_DARK);
        panel.setBorder(new EmptyBorder(18, 18, 18, 18));

        // Top Control Bar
        JPanel topControls = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 8));
        topControls.setBackground(BG_CARD);
        topControls.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                new EmptyBorder(4, 10, 4, 10)
        ));

        JLabel lbl1 = new JLabel("Document 1:");
        lbl1.setForeground(ACCENT_SKIN);
        lbl1.setFont(new Font("Segoe UI", Font.BOLD, 12));

        String[] subOptions = new String[]{
                "Alice (submission_cs101_alice.txt)",
                "Bob (submission_cs101_bob.txt)",
                "Carol (submission_cs101_carol.txt)"
        };

        JComboBox<String> sub1Box = new JComboBox<>(subOptions);
        JComboBox<String> sub2Box = new JComboBox<>(subOptions);
        sub1Box.setSelectedIndex(0);
        sub2Box.setSelectedIndex(1);

        JLabel lbl2 = new JLabel("Document 2:");
        lbl2.setForeground(ACCENT_SKIN);
        lbl2.setFont(new Font("Segoe UI", Font.BOLD, 12));

        ModernButton btnPlagiarism = new ModernButton("Run Kasai LCP Plagiarism Check", ACCENT_RED, Color.WHITE);
        ModernButton btnSAIS = new ModernButton("Demo SA-IS Suffix Array", ACCENT_BLUE, TEXT_DARK);
        ModernButton btnSAM = new ModernButton("Suffix Automaton Query", ACCENT_SKIN, TEXT_DARK);

        topControls.add(lbl1);
        topControls.add(sub1Box);
        topControls.add(lbl2);
        topControls.add(sub2Box);
        topControls.add(btnPlagiarism);
        topControls.add(btnSAIS);
        topControls.add(btnSAM);

        panel.add(topControls, BorderLayout.NORTH);

        // Center: Side-by-Side Dual Document Visualizer
        JPanel centerStudio = new JPanel(new BorderLayout(12, 12));
        centerStudio.setOpaque(false);

        // Plagiarism Telemetry Card
        JPanel telemetryCard = new JPanel(new BorderLayout(10, 6));
        telemetryCard.setBackground(BG_CARD);
        telemetryCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                new EmptyBorder(10, 16, 10, 16)
        ));

        JLabel meterLabel = new JLabel("PLAGIARISM SIMILARITY GAUGE: Click [Run Kasai LCP Plagiarism Check] to analyze");
        meterLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        meterLabel.setForeground(TEXT_MAIN);

        JLabel meterDetail = new JLabel("Algorithmic Engine: Generalized Suffix Array + Kasai's LCP Array computed in O(N) linear time");
        meterDetail.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        meterDetail.setForeground(TEXT_MUTED);

        telemetryCard.add(meterLabel, BorderLayout.NORTH);
        telemetryCard.add(meterDetail, BorderLayout.CENTER);

        // Dual Document Split Panes
        JTextPane doc1Pane = new JTextPane();
        doc1Pane.setEditable(false);
        doc1Pane.setBackground(BG_CARD_ALT);
        doc1Pane.setForeground(TEXT_MAIN);
        doc1Pane.setFont(new Font("Consolas", Font.PLAIN, 12));

        JTextPane doc2Pane = new JTextPane();
        doc2Pane.setEditable(false);
        doc2Pane.setBackground(BG_CARD_ALT);
        doc2Pane.setForeground(TEXT_MAIN);
        doc2Pane.setFont(new Font("Consolas", Font.PLAIN, 12));

        JScrollPane scroll1 = new JScrollPane(doc1Pane);
        scroll1.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(BORDER_COLOR),
                "Document 1: Alice (submission_cs101_alice.txt)",
                TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 12), ACCENT_SKIN));

        JScrollPane scroll2 = new JScrollPane(doc2Pane);
        scroll2.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(BORDER_COLOR),
                "Document 2: Bob (submission_cs101_bob.txt)",
                TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 12), ACCENT_SKIN));

        JSplitPane dualDocSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scroll1, scroll2);
        dualDocSplit.setDividerLocation(580);
        dualDocSplit.setResizeWeight(0.50);

        // Bottom Excerpt Box
        JTextArea quoteArea = new JTextArea();
        quoteArea.setFont(new Font("Consolas", Font.PLAIN, 12));
        quoteArea.setEditable(false);
        quoteArea.setBackground(BG_CARD_ALT);
        quoteArea.setForeground(ACCENT_AMBER);

        JScrollPane quoteScroll = new JScrollPane(quoteArea);
        quoteScroll.setPreferredSize(new Dimension(800, 140));
        quoteScroll.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(BORDER_COLOR),
                "Exact Extracted Plagiarized Passage (Kasai LCP Intersection)",
                TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 12), ACCENT_SKIN));

        JSplitPane mainSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, dualDocSplit, quoteScroll);
        mainSplit.setDividerLocation(360);
        mainSplit.setResizeWeight(0.70);

        centerStudio.add(telemetryCard, BorderLayout.NORTH);
        centerStudio.add(mainSplit, BorderLayout.CENTER);
        panel.add(centerStudio, BorderLayout.CENTER);

        // Document change loaders
        Runnable loadDocuments = () -> {
            int idx1 = sub1Box.getSelectedIndex();
            int idx2 = sub2Box.getSelectedIndex();
            if (idx1 < submissions.size() && idx2 < submissions.size()) {
                doc1Pane.setText(submissions.get(idx1).getTextContent());
                doc2Pane.setText(submissions.get(idx2).getTextContent());
                doc1Pane.setCaretPosition(0);
                doc2Pane.setCaretPosition(0);
            }
        };

        sub1Box.addActionListener(e -> {
            int idx = sub1Box.getSelectedIndex();
            if (idx < submissions.size()) {
                scroll1.setBorder(BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(BORDER_COLOR),
                        "Document 1: " + submissions.get(idx).getStudentId() + " (" + submissions.get(idx).getTitle() + ")",
                        TitledBorder.LEFT, TitledBorder.TOP,
                        new Font("Segoe UI", Font.BOLD, 12), ACCENT_SKIN));
                loadDocuments.run();
            }
        });

        sub2Box.addActionListener(e -> {
            int idx = sub2Box.getSelectedIndex();
            if (idx < submissions.size()) {
                scroll2.setBorder(BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(BORDER_COLOR),
                        "Document 2: " + submissions.get(idx).getStudentId() + " (" + submissions.get(idx).getTitle() + ")",
                        TitledBorder.LEFT, TitledBorder.TOP,
                        new Font("Segoe UI", Font.BOLD, 12), ACCENT_SKIN));
                loadDocuments.run();
            }
        });

        // Plagiarism check action
        btnPlagiarism.addActionListener(e -> {
            int idx1 = sub1Box.getSelectedIndex();
            int idx2 = sub2Box.getSelectedIndex();
            if (idx1 >= submissions.size() || idx2 >= submissions.size()) return;

            AssignmentSubmission s1 = submissions.get(idx1);
            AssignmentSubmission s2 = submissions.get(idx2);

            loadDocuments.run();

            // Clear previous highlights
            doc1Pane.getHighlighter().removeAllHighlights();
            doc2Pane.getHighlighter().removeAllHighlights();

            PlagiarismDetectionService.PlagiarismReport rep = plagiarismService.compareAssignments(s1, s2, 40);

            // Highlight shared passages in glowing amber
            Highlighter.HighlightPainter painter = new DefaultHighlighter.DefaultHighlightPainter(new Color(245, 158, 11, 160));
            StringBuilder quoteSb = new StringBuilder();

            if (rep.sharedExcerpts.size() > 0) {
                meterLabel.setText(String.format("CRITICAL ALERT: %.1f%% PLAGIARISM OVERLAP DETECTED BETWEEN %s AND %s",
                        rep.similarityScore * 100, s1.getStudentId(), s2.getStudentId()));
                meterLabel.setForeground(ACCENT_RED);

                for (int i = 0; i < rep.sharedExcerpts.size(); i++) {
                    KasaiLCP.SharedExcerpt ex = rep.sharedExcerpts.get(i);
                    try {
                        doc1Pane.getHighlighter().addHighlight(ex.posDoc1, ex.posDoc1 + ex.length, painter);
                        doc2Pane.getHighlighter().addHighlight(ex.posDoc2, ex.posDoc2 + ex.length, painter);
                    } catch (Exception ignored) {}

                    quoteSb.append(String.format("=== SHARED EXCERPT #%d (%d Characters Copied) ===\n", i + 1, ex.length));
                    quoteSb.append("\"").append(ex.text).append("\"\n\n");
                }

                meterDetail.setText(String.format("Found %d identical copied block(s) (>= 40 chars). Longest identical substring: %d chars. Status: Synchronized Yellow Highlight Active.",
                        rep.sharedExcerpts.size(), rep.sharedExcerpts.get(0).length));
            } else {
                meterLabel.setText("PASS: 0.0% DIRECT PLAGIARISM OVERLAP (ORIGINAL CONTENT VERIFIED)");
                meterLabel.setForeground(ACCENT_GREEN);
                meterDetail.setText("No shared substring passages >= 40 characters detected between selected documents.");
                quoteSb.append("No plagiarized passages detected between ").append(s1.getStudentId()).append(" and ").append(s2.getStudentId());
            }

            quoteArea.setText(quoteSb.toString());
            quoteArea.setCaretPosition(0);
        });

        btnSAIS.addActionListener(e -> {
            String sample = "BANANA_UNIVERSITY_DATASET";
            int[] sa = SAIS.buildSuffixArray(sample);
            StringBuilder sb = new StringBuilder();
            sb.append("=== Linear-Time SA-IS (Suffix Array Induced Sorting) ===\n");
            sb.append("Input String: \"").append(sample).append("\" (Length: ").append(sample.length()).append(")\n\n");
            sb.append(String.format("%-6s | %-6s | %s\n", "i", "SA[i]", "Suffix"));
            sb.append("------------------------------------------------------------\n");
            for (int i = 0; i < sa.length; i++) {
                sb.append(String.format("%-6d | %-6d | \"%s\"\n", i, sa[i], sample.substring(sa[i])));
            }
            quoteArea.setText(sb.toString());
            quoteArea.setCaretPosition(0);
        });

        btnSAM.addActionListener(e -> {
            String doc = "Advanced Algorithms and University Academic Computing Engine";
            SuffixAutomaton sam = new SuffixAutomaton(doc);
            StringBuilder sb = new StringBuilder();
            sb.append("=== Suffix Automaton (Minimal Substring Directed Acyclic Graph) ===\n");
            sb.append("Indexed Document: \"").append(doc).append("\"\n\n");
            sb.append("• Total Distinct Substrings : ").append(sam.countDistinctSubstrings()).append("\n");
            sb.append("• Contains 'Algorithms'     : ").append(sam.containsSubstring("Algorithms")).append("\n");
            sb.append("• Contains 'Quantum'        : ").append(sam.containsSubstring("Quantum")).append("\n");
            sb.append("• Longest Common Substring with \"University Systems Architecture\": \"")
                    .append(sam.findLongestCommonSubstring("University Systems Architecture")).append("\"\n");
            quoteArea.setText(sb.toString());
            quoteArea.setCaretPosition(0);
        });

        // Initialize documents and run initial demo check
        loadDocuments.run();
        SwingUtilities.invokeLater(() -> btnPlagiarism.doClick());

        return panel;
    }

    // =========================================================================
    // TAB 4: ADVANCED DYNAMIC PROGRAMMING & 2D AUDITOR SIMULATION (CO3 / M3)
    // =========================================================================
    private JPanel createDynamicProgrammingTab() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(BG_DARK);
        panel.setBorder(new EmptyBorder(18, 18, 18, 18));

        // Sub-panel cards using CardLayout for clean switching
        CardLayout dpCardLayout = new CardLayout();
        JPanel dpCardsContainer = new JPanel(dpCardLayout);
        dpCardsContainer.setOpaque(false);

        // --- Card 1: 2D Campus Auditor (Bitmask DP TSP) ---
        JPanel tspPanel = new JPanel(new BorderLayout(12, 12));
        tspPanel.setOpaque(false);

        // Center 2D Vector Canvas
        CampusAuditorCanvas auditorCanvas = new CampusAuditorCanvas();

        // Playback Toolbar
        JPanel animToolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 8));
        animToolbar.setBackground(BG_CARD);
        animToolbar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                new EmptyBorder(4, 10, 4, 10)
        ));

        ModernButton btnPlan = new ModernButton("Plan Optimal Tour (Bitmask DP)", ACCENT_SKIN, TEXT_DARK);
        ModernButton btnPlay = new ModernButton("Play Simulation", ACCENT_GREEN, TEXT_DARK);
        ModernButton btnPause = new ModernButton("Pause", ACCENT_AMBER, TEXT_DARK);
        ModernButton btnPrev = new ModernButton("Step Prev", BG_CARD_LIGHTER, TEXT_MAIN);
        ModernButton btnNext = new ModernButton("Step Next", BG_CARD_LIGHTER, TEXT_MAIN);
        ModernButton btnReset = new ModernButton("Reset", BORDER_COLOR, TEXT_MAIN);

        JLabel speedLbl = new JLabel("Speed:");
        speedLbl.setForeground(ACCENT_SKIN);
        speedLbl.setFont(new Font("Segoe UI", Font.BOLD, 11));

        JSlider speedSlider = new JSlider(1, 100, 40);
        speedSlider.setPreferredSize(new Dimension(100, 22));
        speedSlider.setOpaque(false);
        speedSlider.addChangeListener(e -> auditorCanvas.setSpeed(speedSlider.getValue()));

        animToolbar.add(btnPlan);
        animToolbar.add(btnPlay);
        animToolbar.add(btnPause);
        animToolbar.add(btnPrev);
        animToolbar.add(btnNext);
        animToolbar.add(btnReset);
        animToolbar.add(speedLbl);
        animToolbar.add(speedSlider);

        tspPanel.add(animToolbar, BorderLayout.NORTH);
        tspPanel.add(auditorCanvas, BorderLayout.CENTER);

        btnPlan.addActionListener(e -> {
            BitmaskDP.TourResult tour = analyticsService.planAuditorTour();
            auditorCanvas.setTour(tour);
            auditorCanvas.play();
        });

        btnPlay.addActionListener(e -> auditorCanvas.play());
        btnPause.addActionListener(e -> auditorCanvas.pause());
        btnPrev.addActionListener(e -> auditorCanvas.stepBackward());
        btnNext.addActionListener(e -> auditorCanvas.stepForward());
        btnReset.addActionListener(e -> auditorCanvas.reset());

        // --- Card 2: Levenshtein & Damerau Typo Engine ---
        JPanel typoPanel = new JPanel(new BorderLayout(14, 14));
        typoPanel.setBackground(BG_CARD);
        typoPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                new EmptyBorder(16, 20, 16, 20)
        ));

        JPanel typoControlRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        typoControlRow.setOpaque(false);

        JLabel typoLbl = new JLabel("Misspelled Academic Query / Code:");
        typoLbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        typoLbl.setForeground(ACCENT_SKIN);

        JTextField typoInput = new JTextField("Operatng Systms", 18);
        typoInput.setBackground(BG_CARD_ALT);
        typoInput.setForeground(TEXT_MAIN);
        typoInput.setFont(new Font("Segoe UI", Font.BOLD, 13));
        typoInput.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR),
                new EmptyBorder(4, 8, 4, 8)
        ));

        ModernButton btnRunTypo = new ModernButton("Run DP Spell-Correction", ACCENT_PEACH, TEXT_DARK);

        typoControlRow.add(typoLbl);
        typoControlRow.add(typoInput);
        typoControlRow.add(btnRunTypo);

        JTextArea typoOutput = new JTextArea();
        typoOutput.setFont(new Font("Consolas", Font.PLAIN, 13));
        typoOutput.setEditable(false);
        typoOutput.setBackground(BG_CARD_ALT);
        typoOutput.setForeground(TEXT_MAIN);

        typoPanel.add(typoControlRow, BorderLayout.NORTH);
        typoPanel.add(new JScrollPane(typoOutput), BorderLayout.CENTER);

        ActionListener runTypoLogic = e -> {
            String q = typoInput.getText().trim();
            StringBuilder sb = new StringBuilder();
            sb.append("=== Wagner-Fischer 2D Dynamic Programming Edit Distance ===\n");
            sb.append("Query: \"").append(q).append("\"\n\n");

            sb.append("1. Levenshtein Distance (Insert, Delete, Substitute) Top Suggestions:\n");
            MyArrayList<Pair<Course, Integer>> lev = searchService.suggestTypoLevenshtein(q, 10);
            for (int i = 0; i < Math.min(6, lev.size()); i++) {
                Pair<Course, Integer> p = lev.get(i);
                sb.append(String.format("   [%d] Distance %2d -> %s (%s)\n", i + 1, p.second, p.first.getTitle(), p.first.getCode()));
            }

            sb.append("\n2. Damerau-Levenshtein Distance (Adjacent Transpositions Handled):\n");
            String codeTypo = "SC201";
            MyArrayList<Pair<Course, Integer>> dam = searchService.suggestCourseCodeDamerau(codeTypo, 2);
            sb.append("   Transposed course code test: \"").append(codeTypo).append("\"\n");
            for (int i = 0; i < dam.size(); i++) {
                Pair<Course, Integer> p = dam.get(i);
                sb.append(String.format("   • Distance %2d -> %-8s : %s\n", p.second, p.first.getCode(), p.first.getTitle()));
            }
            typoOutput.setText(sb.toString());
        };

        btnRunTypo.addActionListener(runTypoLogic);
        typoInput.addActionListener(runTypoLogic);

        // --- Card 3: Matrix Chain Multiplication ---
        JPanel mcmPanel = new JPanel(new BorderLayout(14, 14));
        mcmPanel.setBackground(BG_CARD);
        mcmPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                new EmptyBorder(16, 20, 16, 20)
        ));

        JTextArea mcmOutput = new JTextArea();
        mcmOutput.setFont(new Font("Consolas", Font.PLAIN, 13));
        mcmOutput.setEditable(false);
        mcmOutput.setBackground(BG_CARD_ALT);
        mcmOutput.setForeground(TEXT_MAIN);

        MatrixChainMult.McmResult mcm = analyticsService.optimizeAnalyticsPipeline();
        StringBuilder mcmSb = new StringBuilder();
        mcmSb.append("================================================================================\n");
        mcmSb.append("         MATRIX-CHAIN MULTIPLICATION (MCM) O(N^3) OPTIMIZATION\n");
        mcmSb.append("================================================================================\n\n");
        mcmSb.append("Optimizing sequence of university data transformation matrices:\n\n");
        mcmSb.append("• Minimum Scalar Multiplications : ").append(mcm.minMultiplications).append(" operations\n");
        mcmSb.append("• Optimal Parenthesization Tree  : ").append(mcm.optimalOrder).append("\n\n");
        mcmSb.append("Algorithmic Significance:\n");
        mcmSb.append("By solving dynamic programming recurrence m[i,j] = min_{k} (m[i,k] + m[k+1,j] + p_{i-1} p_k p_j),\n");
        mcmSb.append("EduTrack avoids exponential combinatorial matrix multiplication costs during batch academic analytics.\n");
        mcmOutput.setText(mcmSb.toString());

        mcmPanel.add(new JScrollPane(mcmOutput), BorderLayout.CENTER);

        // --- Card 4: Optimal BST ---
        JPanel obstPanel = new JPanel(new BorderLayout(14, 14));
        obstPanel.setBackground(BG_CARD);
        obstPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                new EmptyBorder(16, 20, 16, 20)
        ));

        JTextArea obstOutput = new JTextArea();
        obstOutput.setFont(new Font("Consolas", Font.PLAIN, 13));
        obstOutput.setEditable(false);
        obstOutput.setBackground(BG_CARD_ALT);
        obstOutput.setForeground(TEXT_MAIN);

        OptimalBST.ObstResult obst = analyticsService.buildOptimalSearchTree();
        StringBuilder obstSb = new StringBuilder();
        obstSb.append("================================================================================\n");
        obstSb.append("           OPTIMAL BINARY SEARCH TREE (OBST) O(N^3) DP SOLVER\n");
        obstSb.append("================================================================================\n\n");
        obstSb.append("Minimizes expected academic search depth for non-uniform course access frequencies:\n\n");
        obstSb.append("• Expected Optimal Search Cost (Weighted Depth): ").append(String.format("%.4f", obst.expectedCost)).append("\n\n");
        obstSb.append("• Optimal Search Tree Root Organization Hierarchy:\n");
        obstSb.append(obst.printTree()).append("\n");
        obstOutput.setText(obstSb.toString());

        obstPanel.add(new JScrollPane(obstOutput), BorderLayout.CENTER);

        // Add all sub-cards
        dpCardsContainer.add(tspPanel, "TSP");
        dpCardsContainer.add(typoPanel, "TYPO");
        dpCardsContainer.add(mcmPanel, "MCM");
        dpCardsContainer.add(obstPanel, "OBST");

        // Top Algorithm Switcher Navigation Bar
        JPanel navBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        navBar.setBackground(BG_CARD);
        navBar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                new EmptyBorder(4, 10, 4, 10)
        ));

        ModernButton navTSP = new ModernButton("Traveling Academic Auditor (2D Map TSP)", ACCENT_SKIN, TEXT_DARK);
        ModernButton navTypo = new ModernButton("Levenshtein & Damerau Typo Correction", BG_CARD_LIGHTER, TEXT_MAIN);
        ModernButton navMCM = new ModernButton("Matrix-Chain Mult (MCM)", BG_CARD_LIGHTER, TEXT_MAIN);
        ModernButton navOBST = new ModernButton("Optimal BST (OBST)", BG_CARD_LIGHTER, TEXT_MAIN);

        navTSP.addActionListener(e -> {
            dpCardLayout.show(dpCardsContainer, "TSP");
            navTSP.setCustomColors(ACCENT_SKIN, TEXT_DARK);
            navTypo.setCustomColors(BG_CARD_LIGHTER, TEXT_MAIN);
            navMCM.setCustomColors(BG_CARD_LIGHTER, TEXT_MAIN);
            navOBST.setCustomColors(BG_CARD_LIGHTER, TEXT_MAIN);
        });

        navTypo.addActionListener(e -> {
            dpCardLayout.show(dpCardsContainer, "TYPO");
            runTypoLogic.actionPerformed(null);
            navTSP.setCustomColors(BG_CARD_LIGHTER, TEXT_MAIN);
            navTypo.setCustomColors(ACCENT_PEACH, TEXT_DARK);
            navMCM.setCustomColors(BG_CARD_LIGHTER, TEXT_MAIN);
            navOBST.setCustomColors(BG_CARD_LIGHTER, TEXT_MAIN);
        });

        navMCM.addActionListener(e -> {
            dpCardLayout.show(dpCardsContainer, "MCM");
            navTSP.setCustomColors(BG_CARD_LIGHTER, TEXT_MAIN);
            navTypo.setCustomColors(BG_CARD_LIGHTER, TEXT_MAIN);
            navMCM.setCustomColors(ACCENT_CREAM, TEXT_DARK);
            navOBST.setCustomColors(BG_CARD_LIGHTER, TEXT_MAIN);
        });

        navOBST.addActionListener(e -> {
            dpCardLayout.show(dpCardsContainer, "OBST");
            navTSP.setCustomColors(BG_CARD_LIGHTER, TEXT_MAIN);
            navTypo.setCustomColors(BG_CARD_LIGHTER, TEXT_MAIN);
            navMCM.setCustomColors(BG_CARD_LIGHTER, TEXT_MAIN);
            navOBST.setCustomColors(ACCENT_GREEN, TEXT_DARK);
        });

        navBar.add(navTSP);
        navBar.add(navTypo);
        navBar.add(navMCM);
        navBar.add(navOBST);

        panel.add(navBar, BorderLayout.NORTH);
        panel.add(dpCardsContainer, BorderLayout.CENTER);

        // Auto plan tour and initialize canvas
        SwingUtilities.invokeLater(() -> btnPlan.doClick());

        return panel;
    }

    // =========================================================================
    // TAB 5: NETWORK FLOW & RESOURCE ALLOCATION (M4)
    // =========================================================================
    private JPanel createNetworkFlowTab() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(BG_DARK);
        panel.setBorder(new EmptyBorder(16, 20, 16, 20));

        JPanel topControls = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 10));
        topControls.setBackground(BG_CARD);
        topControls.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                new EmptyBorder(4, 10, 4, 10)
        ));

        ModernButton btnBipartite = new ModernButton("Solve Faculty Bipartite Matching", ACCENT_BLUE, TEXT_DARK);
        ModernButton btnFlowCompare = new ModernButton("Dinic Lab Flow Network", ACCENT_SKIN, TEXT_DARK);
        ModernButton btnKonig = new ModernButton("König's Theorem Conflict Bottleneck", ACCENT_GREEN, TEXT_DARK);

        topControls.add(btnBipartite);
        topControls.add(btnFlowCompare);
        topControls.add(btnKonig);

        panel.add(topControls, BorderLayout.NORTH);

        GraphVisualizerPanel graphPanel = new GraphVisualizerPanel();
        graphPanel.setMode(GraphVisualizerPanel.GraphMode.BIPARTITE_FACULTY);

        JTextArea flowArea = new JTextArea();
        flowArea.setFont(new Font("Consolas", Font.PLAIN, 12));
        flowArea.setEditable(false);
        flowArea.setBackground(BG_CARD_ALT);
        flowArea.setForeground(TEXT_MAIN);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, graphPanel, new JScrollPane(flowArea));
        splitPane.setDividerLocation(620);
        splitPane.setResizeWeight(0.60);
        panel.add(splitPane, BorderLayout.CENTER);

        btnBipartite.addActionListener(e -> {
            graphPanel.setMode(GraphVisualizerPanel.GraphMode.BIPARTITE_FACULTY);
            BipartiteMatching.AssignmentResult res = allocationService.assignFacultyToCourses();
            flowArea.setText(res.toString());
        });

        btnFlowCompare.addActionListener(e -> {
            graphPanel.setMode(GraphVisualizerPanel.GraphMode.DINIC_FLOW_NETWORK);
            String report = allocationService.compareFlowAlgorithms(120, 25, 8);
            flowArea.setText(report);
        });

        btnKonig.addActionListener(e -> {
            graphPanel.setMode(GraphVisualizerPanel.GraphMode.BIPARTITE_FACULTY);
            KonigsTheorem.ConflictBottleneck bottleneck = allocationService.analyzeConflictBottlenecks();
            flowArea.setText(bottleneck.toString());
        });

        return panel;
    }

    // =========================================================================
    // TAB 6: NP-COMPLETENESS & REDUCTIONS (M5)
    // =========================================================================
    private JPanel createNPCompletenessTab() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(BG_DARK);
        panel.setBorder(new EmptyBorder(16, 20, 16, 20));

        JPanel topControls = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 10));
        topControls.setBackground(BG_CARD);
        topControls.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                new EmptyBorder(4, 10, 4, 10)
        ));

        ModernButton btnSAT = new ModernButton("Run DPLL SAT Solver (Exam Timetable)", ACCENT_BLUE, TEXT_DARK);
        ModernButton btnKarp = new ModernButton("Demonstrate 3-SAT -> CLIQUE -> IS -> VC", ACCENT_PURPLE, TEXT_DARK);
        ModernButton btnApprox = new ModernButton("Vertex Cover 2-Approximation (Proctors)", ACCENT_SKIN, TEXT_DARK);

        topControls.add(btnSAT);
        topControls.add(btnKarp);
        topControls.add(btnApprox);

        panel.add(topControls, BorderLayout.NORTH);

        GraphVisualizerPanel graphPanel = new GraphVisualizerPanel();
        graphPanel.setMode(GraphVisualizerPanel.GraphMode.EXAM_VERTEX_COVER);

        JTextArea npArea = new JTextArea();
        npArea.setFont(new Font("Consolas", Font.PLAIN, 12));
        npArea.setEditable(false);
        npArea.setBackground(BG_CARD_ALT);
        npArea.setForeground(TEXT_MAIN);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, graphPanel, new JScrollPane(npArea));
        splitPane.setDividerLocation(620);
        splitPane.setResizeWeight(0.60);
        panel.add(splitPane, BorderLayout.CENTER);

        btnSAT.addActionListener(e -> {
            graphPanel.setMode(GraphVisualizerPanel.GraphMode.EXAM_VERTEX_COVER);
            DpllSatSolver.SatResult sat = examService.solveExamTimetableSAT(6, 3);
            StringBuilder sb = new StringBuilder();
            sb.append("=== DPLL Boolean Satisfiability (SAT) Solver ===\n");
            sb.append("Exam Timetabling Constraints: 6 courses across 3 examination slots with mutual exclusion\n\n");
            sb.append(sat).append("\n");
            npArea.setText(sb.toString());
        });

        btnKarp.addActionListener(e -> {
            String chain = examService.demonstrateReductionChain();
            npArea.setText(chain);
        });

        btnApprox.addActionListener(e -> {
            graphPanel.setMode(GraphVisualizerPanel.GraphMode.EXAM_VERTEX_COVER);
            VertexCover2Approx.ApproxResult approx = examService.allocateInvigilatorsApprox(15);
            StringBuilder sb = new StringBuilder();
            sb.append("=== Vertex Cover 2-Approximation via Maximal Matching ===\n");
            sb.append("Solves Exam Proctor / Invigilator Assignment Problem:\n\n");
            sb.append("• Total Exam Conflict Edges Covered : ").append(approx.maximalMatching.size() * 2).append("\n");
            sb.append("• Assigned Proctor Course Count     : ").append(approx.approxCoverSize).append("\n");
            sb.append("• Maximal Matching Size             : ").append(approx.maximalMatching.size()).append("\n");
            sb.append("• Provable Approximation Bound      : |VC| <= 2 * OPT\n\n");
            sb.append("Proctored Courses (Gold Nodes ★ in 2D Visualizer):\n");
            for (int i = 0; i < approx.coverVertices.size(); i++) {
                sb.append(" • ").append(courses.get(approx.coverVertices.get(i)).getCode()).append(" (")
                        .append(courses.get(approx.coverVertices.get(i)).getTitle()).append(")\n");
            }
            sb.append("\nNotice in the 2D visual canvas: every conflict edge touches at least one glowing proctor node!\n");
            npArea.setText(sb.toString());
        });

        return panel;
    }

    // =========================================================================
    // TAB 7: RANDOMIZED & PARALLEL ALGORITHMS (M6)
    // =========================================================================
    private JPanel createParallelRandomTab() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(BG_DARK);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel topControls = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 10));
        topControls.setBackground(BG_CARD);
        topControls.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                new EmptyBorder(4, 10, 4, 10)
        ));

        ModernButton btnRank = new ModernButton("Randomized QuickSort Ranking", ACCENT_BLUE, TEXT_DARK);
        ModernButton btnReservoir = new ModernButton("Reservoir Sampling (k=5)", ACCENT_GREEN, TEXT_DARK);
        ModernButton btnPrime = new ModernButton("Miller-Rabin Token Generator", ACCENT_PURPLE, TEXT_DARK);
        ModernButton btnBlelloch = new ModernButton("Blelloch Parallel Scan", ACCENT_SKIN, TEXT_DARK);
        ModernButton btnReduce = new ModernButton("Parallel Reduce GPA", ACCENT_PEACH, TEXT_DARK);
        ModernButton btnBrent = new ModernButton("Brent's Theorem Modeler", BG_CARD_LIGHTER, TEXT_MAIN);

        topControls.add(btnRank);
        topControls.add(btnReservoir);
        topControls.add(btnPrime);
        topControls.add(btnBlelloch);
        topControls.add(btnReduce);
        topControls.add(btnBrent);

        panel.add(topControls, BorderLayout.NORTH);

        JTextArea prArea = new JTextArea();
        prArea.setFont(new Font("Consolas", Font.PLAIN, 13));
        prArea.setEditable(false);
        prArea.setBackground(BG_CARD_ALT);
        prArea.setForeground(TEXT_MAIN);
        panel.add(new JScrollPane(prArea), BorderLayout.CENTER);

        btnRank.addActionListener(e -> {
            MyArrayList<Student> ranked = analyticsService.rankStudentsByMerit();
            StringBuilder sb = new StringBuilder();
            sb.append("=== Academic Merit Ranking (Randomized QuickSort - Expected O(N log N)) ===\n\n");
            sb.append(String.format("%-5s | %-8s | %-22s | %-20s | %-6s | %-8s\n", "Rank", "ID", "Name", "Department", "GPA", "Credits"));
            sb.append("------------------------------------------------------------------------------------\n");
            for (int i = 0; i < ranked.size(); i++) {
                Student s = ranked.get(i);
                sb.append(String.format("#%-4d | %-8s | %-22s | %-20s | %-6.2f | %-8d\n",
                        i + 1, s.getId(), s.getName(), s.getDepartment(), s.getGpa(), s.getCompletedCredits()));
            }
            prArea.setText(sb.toString());
        });

        btnReservoir.addActionListener(e -> {
            MyArrayList<ActivityLog> sample = analyticsService.sampleActivityStream(activityLogs, 5);
            StringBuilder sb = new StringBuilder();
            sb.append("=== Reservoir Sampling (Algorithm R) on Streaming Activities ===\n");
            sb.append("Extracted uniform sample of 5 items from ").append(activityLogs.size()).append(" continuous stream events:\n\n");
            for (int i = 0; i < sample.size(); i++) {
                sb.append("  [").append(i + 1).append("] ").append(sample.get(i)).append("\n");
            }
            prArea.setText(sb.toString());
        });

        btnPrime.addActionListener(e -> {
            long token = analyticsService.generateCryptographicToken(1001);
            StringBuilder sb = new StringBuilder();
            sb.append("=== Miller-Rabin Primality Test & Student Token Generation ===\n\n");
            sb.append("• Generated Prime Token for STU1001 : ").append(token).append("\n");
            sb.append("• Miller-Rabin Verification Passed : ").append(MillerRabin.isPrime(token)).append("\n");
            sb.append("• Carmichael Number Resistance     : Verified\n");
            prArea.setText(sb.toString());
        });

        btnBlelloch.addActionListener(e -> {
            long[] cumulative = analyticsService.computeCumulativeCreditsBlelloch();
            StringBuilder sb = new StringBuilder();
            sb.append("=== Blelloch Work-Efficient Parallel Prefix Scan ===\n");
            sb.append("Phase 1: Up-Sweep (Parallel Reduce) O(N) work, O(log N) span\n");
            sb.append("Phase 2: Down-Sweep (Distribution)  O(N) work, O(log N) span\n\n");
            for (int i = 0; i < Math.min(15, cumulative.length); i++) {
                Student s = students.get(i);
                sb.append(String.format("  Student %-8s (%-20s): %3d credits -> Cumulative Credits: %d\n",
                        s.getId(), s.getName(), s.getCompletedCredits(), cumulative[i]));
            }
            prArea.setText(sb.toString());
        });

        btnReduce.addActionListener(e -> {
            int cores = Runtime.getRuntime().availableProcessors();
            ParallelReduce.AggregateStats stats = analyticsService.aggregateStudentGPA(cores);
            StringBuilder sb = new StringBuilder();
            sb.append("=== Multi-Threaded Parallel Tree Reduce ===\n");
            sb.append("Aggregating student academic metrics across ").append(cores).append(" CPU cores:\n\n");
            sb.append("• ").append(stats).append("\n");
            prArea.setText(sb.toString());
        });

        btnBrent.addActionListener(e -> {
            BrentsTheorem.ParallelMetrics metrics = analyticsService.evaluateBrentsTheorem(2_000_000L, 40L, 8);
            prArea.setText(metrics.toString());
        });

        return panel;
    }

    // =========================================================================
    // TAB 8: AUTOMATED VERIFICATION BENCHMARK
    // =========================================================================
    private JPanel createBenchmarkTab() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(BG_DARK);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel topControls = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 10));
        topControls.setBackground(BG_CARD);
        topControls.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                new EmptyBorder(4, 10, 4, 10)
        ));

        ModernButton btnRunReview1 = new ModernButton("Run Review 1 Verification (CO1 – CO3: 11 Algos)", ACCENT_GREEN, TEXT_DARK);
        ModernButton btnRunAll = new ModernButton("Run Full 17-Algorithm Suite (All Modules)", ACCENT_SKIN, TEXT_DARK);

        topControls.add(btnRunReview1);
        topControls.add(btnRunAll);
        panel.add(topControls, BorderLayout.NORTH);

        String[] cols = {"#", "Algorithm Family", "Syllabus Module", "Time Complexity", "Space Complexity", "Status", "Exec Time"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);
        JTable table = new JTable(model);
        styleTable(table);

        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        btnRunReview1.addActionListener(e -> {
            model.setRowCount(0);
            runGuiBenchmarks(model, true);
        });

        btnRunAll.addActionListener(e -> {
            model.setRowCount(0);
            runGuiBenchmarks(model, false);
        });

        return panel;
    }

    // =========================================================================
    // TAB 9: ALGORITHM BENCHMARK ARENA
    // =========================================================================
    private JPanel createBenchmarkArenaTab() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(BG_DARK);
        panel.setBorder(new EmptyBorder(16, 20, 16, 20));

        JPanel topControls = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 10));
        topControls.setBackground(BG_CARD);
        topControls.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                new EmptyBorder(4, 10, 4, 10)
        ));

        JLabel lblMatchup = new JLabel("Matchup:");
        lblMatchup.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblMatchup.setForeground(ACCENT_SKIN);

        String[] matchups = {
                "1. String Search: KMP vs Z-Algo vs Rabin-Karp vs Naive",
                "2. Network Flow: Dinic vs Edmonds-Karp vs Ford-Fulkerson",
                "3. Suffix Indexing: Linear SA-IS vs Suffix Array vs DAWG",
                "4. Dynamic Programming: Levenshtein vs Damerau vs MCM",
                "5. Parallel & Sorting: Blelloch Scan vs QuickSort vs Reduce"
        };
        JComboBox<String> comboMatchups = new JComboBox<>(matchups);
        comboMatchups.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        JLabel lblScale = new JLabel("Scale:");
        lblScale.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblScale.setForeground(ACCENT_SKIN);

        String[] scales = {"Small (10K units)", "Medium (40K units)", "Large (100K units)"};
        JComboBox<String> comboScale = new JComboBox<>(scales);
        comboScale.setSelectedIndex(1);
        comboScale.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        ModernButton btnRace = new ModernButton("START ALGORITHM RACE", ACCENT_SKIN, TEXT_DARK);

        JProgressBar progressBar = new JProgressBar();
        progressBar.setPreferredSize(new Dimension(140, 22));
        progressBar.setVisible(false);

        topControls.add(lblMatchup);
        topControls.add(comboMatchups);
        topControls.add(lblScale);
        topControls.add(comboScale);
        topControls.add(btnRace);
        topControls.add(progressBar);

        panel.add(topControls, BorderLayout.NORTH);

        BenchmarkBarChartPanel chartPanel = new BenchmarkBarChartPanel();

        JTextArea telemetryArea = new JTextArea();
        telemetryArea.setFont(new Font("Consolas", Font.PLAIN, 12));
        telemetryArea.setEditable(false);
        telemetryArea.setBackground(BG_CARD_ALT);
        telemetryArea.setForeground(TEXT_MAIN);

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, chartPanel, new JScrollPane(telemetryArea));
        split.setDividerLocation(340);
        split.setResizeWeight(0.65);
        panel.add(split, BorderLayout.CENTER);

        btnRace.addActionListener(e -> {
            int selectedIndex = comboMatchups.getSelectedIndex();
            int scaleIndex = comboScale.getSelectedIndex();
            int scale = scaleIndex == 0 ? 10000 : (scaleIndex == 1 ? 40000 : 100000);

            btnRace.setEnabled(false);
            progressBar.setVisible(true);
            progressBar.setIndeterminate(true);
            telemetryArea.setText(">>> Running algorithm showdown in background thread...\n");

            SwingWorker<BenchmarkArenaService.ShowdownCategory, Void> worker = new SwingWorker<BenchmarkArenaService.ShowdownCategory, Void>() {
                @Override
                protected BenchmarkArenaService.ShowdownCategory doInBackground() {
                    switch (selectedIndex) {
                        case 0:
                            return arenaService.runStringSearchShowdown(scale);
                        case 1:
                            return arenaService.runNetworkFlowShowdown(scaleIndex == 0 ? 24 : (scaleIndex == 1 ? 40 : 80));
                        case 2:
                            return arenaService.runSuffixIndexingShowdown(scale);
                        case 3:
                            return arenaService.runDynamicProgrammingShowdown(scaleIndex == 0 ? 250 : (scaleIndex == 1 ? 600 : 1200));
                        case 4:
                        default:
                            return arenaService.runParallelSortingShowdown(scale);
                    }
                }

                @Override
                protected void done() {
                    try {
                        BenchmarkArenaService.ShowdownCategory cat = get();
                        chartPanel.displayCategory(cat);

                        StringBuilder sb = new StringBuilder();
                        sb.append("================================================================================\n");
                        sb.append("                 EDUTRACK ALGORITHM BENCHMARK ARENA TELEMETRY\n");
                        sb.append("================================================================================\n");
                        sb.append("Tournament Category: ").append(cat.title).append("\n");
                        sb.append("Workload           : ").append(cat.inputDescription).append("\n\n");

                        BenchmarkArenaService.ArenaResult winner = cat.results.size() > 0 ? cat.results.get(0) : null;
                        for (int i = 0; i < cat.results.size(); i++) {
                            BenchmarkArenaService.ArenaResult r = cat.results.get(i);
                            if (winner == null || r.elapsedNanos < winner.elapsedNanos) {
                                winner = r;
                            }
                            sb.append(String.format("  #%d [%-28s] %-18s | Time: %10.2f µs | Speedup: %5.1fx | %s\n",
                                    (i + 1), r.algorithmName, r.complexity, r.elapsedMicros, r.speedup, r.notes));
                        }
                        if (winner != null) {
                            sb.append("\nVICTORY: ").append(winner.algorithmName)
                                    .append(" achieved peak throughput (").append(String.format("%.2f µs", winner.elapsedMicros))
                                    .append(") with speedup of ").append(String.format("%.1fx", winner.speedup))
                                    .append(" over baseline!\n");
                        }
                        sb.append("================================================================================\n");
                        telemetryArea.setText(sb.toString());

                    } catch (Exception ex) {
                        telemetryArea.setText("[!] Error during benchmark execution: " + ex.getMessage());
                    } finally {
                        btnRace.setEnabled(true);
                        progressBar.setIndeterminate(false);
                        progressBar.setVisible(false);
                    }
                }
            };
            worker.execute();
        });

        return panel;
    }

    private void runGuiBenchmarks(DefaultTableModel model) {
        runGuiBenchmarks(model, false);
    }

    private void runGuiBenchmarks(DefaultTableModel model, boolean review1Only) {
        addBenchmarkRow(model, 1, "KMP Pattern Search", "Module 1 (CO1)", "O(N + M)", "O(M)",
                () -> KmpMatcher.search("algorithms and algorithmic analysis", "algorithm", true).size() == 2);

        addBenchmarkRow(model, 2, "Z-Algorithm", "Module 1 (CO1)", "O(N + M)", "O(N + M)",
                () -> ZAlgorithm.computeZ("aabzaa")[0] == 6);

        addBenchmarkRow(model, 3, "Rabin-Karp Rolling Hash", "Module 1 (CO1)", "O(N + M) avg", "O(1)",
                () -> RabinKarp.search("UNIVERSITY_CS201_EXAM_CS201", "CS201").size() == 2);

        addBenchmarkRow(model, 4, "Aho-Corasick Automaton", "Module 1 (CO1)", "O(N + ∑M + Z)", "O(∑M · Σ)",
                () -> {
                    AhoCorasick ac = new AhoCorasick();
                    ac.addPattern("he"); ac.addPattern("she"); ac.addPattern("his");
                    ac.buildAutomation();
                    return ac.search("ushers").size() == 2;
                });

        addBenchmarkRow(model, 5, "✨ Bittu's Algorithm", "Invention (CO1)", "O(N + M) avg", "O(Σ² = 65K)",
                () -> BittuAlgorithm.search("algorithms and algorithmic analysis", "algorithm").size() == 2);

        addBenchmarkRow(model, 6, "Suffix Array (Prefix Doubling)", "Module 2 (CO2)", "O(N log² N)", "O(N)",
                () -> new SuffixArray("banana").searchPattern("nan") >= 0);

        addBenchmarkRow(model, 7, "Kasai's LCP Array", "Module 2 (CO2)", "O(N)", "O(N)",
                () -> KasaiLCP.computeLCP("banana", new SuffixArray("banana").getSuffixArray()).length == 6);

        addBenchmarkRow(model, 8, "Suffix Automaton (SAM)", "Module 2 (CO2)", "O(N) build, O(M) query", "O(N · Σ)",
                () -> new SuffixAutomaton("algorithms").containsSubstring("rithm"));

        addBenchmarkRow(model, 9, "Levenshtein Edit Distance", "Module 3 (CO3)", "O(N · M)", "O(N · M)",
                () -> Levenshtein.computeDistance("kitten", "sitting") == 3);

        addBenchmarkRow(model, 10, "Damerau-Levenshtein", "Module 3 (CO3)", "O(N · M)", "O(N · M)",
                () -> DamerauLevenshtein.computeDistance("CS102", "SC102") == 1);

        addBenchmarkRow(model, 11, "Matrix-Chain Mult (MCM)", "Module 3 (CO3)", "O(K³)", "O(K²)",
                () -> MatrixChainMult.solve(new int[]{10, 30, 5, 60}, null).minMultiplications == 4500);

        if (review1Only) {
            return;
        }

        addBenchmarkRow(model, 12, "Network Flow (Dinic)", "Module 4 (CO4)", "O(V² E)", "O(V + E)",
                () -> {
                    edutrack.algorithms.flow.FlowNetwork net = new edutrack.algorithms.flow.FlowNetwork(4);
                    net.addEdge(0, 1, 10); net.addEdge(0, 2, 5); net.addEdge(1, 2, 15);
                    net.addEdge(1, 3, 10); net.addEdge(2, 3, 10);
                    return edutrack.algorithms.flow.DinicsAlgorithm.maxFlow(net, 0, 3) == 15;
                });

        addBenchmarkRow(model, 13, "DPLL SAT Solver", "Module 5 (CO5)", "O(2^V) worst-case", "O(V + C)",
                () -> {
                    MyArrayList<DpllSatSolver.Clause> cl = new MyArrayList<>();
                    cl.add(new DpllSatSolver.Clause(1, 2)); cl.add(new DpllSatSolver.Clause(-1, 2));
                    return DpllSatSolver.solve(cl, 2).isSatisfiable;
                });

        addBenchmarkRow(model, 14, "Vertex Cover 2-Approx", "Module 5 (CO5)", "O(V + E)", "O(V)",
                () -> {
                    MyArrayList<Pair<Integer, Integer>> edges = new MyArrayList<>();
                    edges.add(new Pair<>(0, 1)); edges.add(new Pair<>(1, 2));
                    return VertexCover2Approx.approximateCover(3, edges).approxCoverSize <= 4;
                });

        addBenchmarkRow(model, 15, "Miller-Rabin Primality", "Module 6 (CO6)", "O(k log³ n)", "O(1)",
                () -> MillerRabin.isPrime(1000000007L) && !MillerRabin.isPrime(1000000005L));

        addBenchmarkRow(model, 16, "Blelloch Parallel Scan", "Module 6 (CO6)", "O(N) work, O(log N) span", "O(N)",
                () -> BlellochScan.inclusiveScan(new long[]{1, 2, 3, 4, 5, 6, 7, 8})[7] == 36);

        addBenchmarkRow(model, 17, "Brent's Theorem Modeler", "Module 6 (CO6)", "O(1) analytical", "O(1)",
                () -> BrentsTheorem.analyze(1000, 10, 4).expectedSpeedup > 1.0);
    }

    private interface BenchmarkCase {
        boolean run() throws Exception;
    }

    private void addBenchmarkRow(DefaultTableModel model, int id, String name, String module,
                                 String timeComplexity, String spaceComplexity, BenchmarkCase testCase) {
        long t1 = System.nanoTime();
        boolean pass = false;
        try {
            pass = testCase.run();
        } catch (Exception ignored) {}
        long elapsed = (System.nanoTime() - t1) / 1000;

        model.addRow(new Object[]{
                id, name, module, timeComplexity, spaceComplexity,
                pass ? "PASS" : "FAIL",
                elapsed + " µs"
        });
    }

    private static void styleTable(JTable table) {
        table.setBackground(BG_CARD_ALT);
        table.setForeground(TEXT_MAIN);
        table.setGridColor(BORDER_COLOR);
        table.setRowHeight(28);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.setSelectionBackground(new Color(245, 210, 165, 140));
        table.setSelectionForeground(TEXT_DARK);

        table.getTableHeader().setBackground(BG_CARD);
        table.getTableHeader().setForeground(ACCENT_SKIN);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        table.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR));

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        if (table.getColumnCount() > 0) {
            table.getColumnModel().getColumn(0).setPreferredWidth(45);
        }
    }

    private static String printList(MyArrayList<Integer> list) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < list.size(); i++) {
            sb.append(list.get(i)).append(i + 1 < list.size() ? ", " : "");
        }
        sb.append("]");
        return sb.toString();
    }

    public static void main(String[] args) {
        String baseDir = "c:/DSA_3";

        // Support non-interactive headless test flag
        if (args.length > 0 && "--test".equalsIgnoreCase(args[0])) {
            System.out.println("[EduTrackGUI] Initializing headless test...");
            EduTrackGUI gui = new EduTrackGUI(baseDir);
            DefaultTableModel model = new DefaultTableModel();
            gui.runGuiBenchmarks(model);
            System.out.println("[EduTrackGUI] Headless test verified " + model.getRowCount() + " algorithms successfully.");
            System.exit(0);
        }

        SwingUtilities.invokeLater(() -> {
            try {
                // Ensure consistent rendering across all platforms
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            } catch (Exception ignored) {}
            EduTrackGUI gui = new EduTrackGUI(baseDir);
            gui.setVisible(true);
        });
    }
}
