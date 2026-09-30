package edutrack.ui;

import edutrack.algorithms.dp.BitmaskDP;
import edutrack.core.MyArrayList;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.*;

/**
 * Interactive 2D Vector Campus Map & Traveling Academic Auditor (TSP) Canvas.
 * Visualizes the Bitmask DP state transitions with real-time teacher/auditor
 * animation across university departments.
 */
public class CampusAuditorCanvas extends JPanel {

    public static class DeptNode {
        public int id;
        public String name;
        public String shortCode;
        public double x, y;
        public Color accentColor;
        public String description;

        public DeptNode(int id, String name, String shortCode, double x, double y, Color accentColor, String description) {
            this.id = id;
            this.name = name;
            this.shortCode = shortCode;
            this.x = x;
            this.y = y;
            this.accentColor = accentColor;
            this.description = description;
        }
    }

    private final DeptNode[] departments;
    private final int[][] distMatrix;
    private BitmaskDP.TourResult currentTour;

    // Animation & State
    private int currentStepIndex = 0;
    private double transitionProgress = 0.0; // 0.0 to 1.0
    private boolean isPlaying = false;
    private int animationSpeedMs = 25; // timer delay
    private double speedFactor = 0.03; // step increment
    private Timer animTimer;

    // Hover state
    private DeptNode hoveredNode = null;

    // High-Contrast Warm Skin & Slate Colors
    private static final Color BG_CANVAS = new Color(15, 20, 32);           // Slate 900 / Midnight
    private static final Color PATH_DEFAULT = new Color(59, 73, 103, 150);  // Slate transit
    private static final Color PATH_ACTIVE = new Color(245, 210, 165);      // Warm Golden Sand / Skin
    private static final Color PATH_TRAVERSED = new Color(34, 197, 94);     // Emerald 500
    private static final Color TEXT_WHITE = new Color(248, 250, 252);
    private static final Color TEXT_MUTED = new Color(203, 213, 225);
    private static final Color ACCENT_SKIN = new Color(245, 210, 165);
    private static final Color ACCENT_AMBER = new Color(245, 158, 11);
    private static final Color ACCENT_GREEN = new Color(34, 197, 94);

    public CampusAuditorCanvas() {
        setBackground(BG_CANVAS);
        setPreferredSize(new Dimension(850, 480));

        this.departments = new DeptNode[]{
                new DeptNode(0, "Computer Science", "CS", 0.16, 0.32, new Color(56, 189, 248), "Main Computing Wing & Server Core"),
                new DeptNode(1, "Artificial Intelligence", "AI", 0.50, 0.18, new Color(192, 132, 252), "Neural Robotics & Machine Learning Lab"),
                new DeptNode(2, "Data Science", "DS", 0.84, 0.32, new Color(253, 186, 140), "Big Data Analytics & Statistical Cluster"),
                new DeptNode(3, "Mathematics", "MATH", 0.74, 0.76, new Color(254, 243, 199), "Discrete Math & Cryptography Hall"),
                new DeptNode(4, "Electronics", "ELEC", 0.50, 0.86, new Color(251, 113, 133), "VLSI Circuits & Embedded Systems Lab"),
                new DeptNode(5, "Cybersecurity", "CYBER", 0.26, 0.76, new Color(34, 197, 94), "Network Defense & Threat Analytics Center")
        };

        this.distMatrix = new int[][]{
                {0, 10, 15, 20, 25, 30},
                {10, 0, 35, 25, 18, 22},
                {15, 35, 0, 30, 28, 14},
                {20, 25, 30, 0, 12, 16},
                {25, 18, 28, 12, 0, 24},
                {30, 22, 14, 16, 24, 0}
        };

        initMouseListeners();
        initTimer();
    }

    private void initMouseListeners() {
        MouseAdapter adapter = new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int w = getWidth();
                int h = getHeight();
                DeptNode found = null;
                for (DeptNode node : departments) {
                    double px = node.x * w;
                    double py = node.y * h;
                    if (Point2D.distance(e.getX(), e.getY(), px, py) <= 32) {
                        found = node;
                        break;
                    }
                }
                if (found != hoveredNode) {
                    hoveredNode = found;
                    repaint();
                }
            }
        };
        addMouseListener(adapter);
        addMouseMotionListener(adapter);
    }

    private void initTimer() {
        animTimer = new Timer(animationSpeedMs, e -> {
            if (!isPlaying || currentTour == null || currentTour.path == null || currentTour.path.size() < 2) return;

            transitionProgress += speedFactor;
            if (transitionProgress >= 1.0) {
                transitionProgress = 0.0;
                currentStepIndex++;
                if (currentStepIndex >= currentTour.path.size() - 1) {
                    currentStepIndex = currentTour.path.size() - 1;
                    isPlaying = false;
                }
            }
            repaint();
        });
    }

    public void setTour(BitmaskDP.TourResult tour) {
        this.currentTour = tour;
        this.currentStepIndex = 0;
        this.transitionProgress = 0.0;
        this.isPlaying = false;
        repaint();
    }

    public void play() {
        if (currentTour == null) return;
        if (currentStepIndex >= currentTour.path.size() - 1) {
            currentStepIndex = 0;
            transitionProgress = 0.0;
        }
        isPlaying = true;
        animTimer.start();
        repaint();
    }

    public void pause() {
        isPlaying = false;
        if (animTimer != null) animTimer.stop();
        repaint();
    }

    public void reset() {
        isPlaying = false;
        if (animTimer != null) animTimer.stop();
        currentStepIndex = 0;
        transitionProgress = 0.0;
        repaint();
    }

    public void stepForward() {
        if (currentTour == null) return;
        if (currentStepIndex < currentTour.path.size() - 1) {
            currentStepIndex++;
            transitionProgress = 0.0;
            repaint();
        }
    }

    public void stepBackward() {
        if (currentTour == null) return;
        if (currentStepIndex > 0) {
            currentStepIndex--;
            transitionProgress = 0.0;
            repaint();
        }
    }

    public void setSpeed(int speedPercent) {
        this.speedFactor = 0.01 + (speedPercent / 100.0) * 0.06;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        // 1. Draw Background Grid & Subtle Campus Boundary
        drawCampusGrid(g2, w, h);

        // 2. Draw Transit Edges (Inter-building paths)
        drawTransitNetwork(g2, w, h);

        // 3. Draw Optimal TSP Tour Path (Active / Traversed)
        drawOptimalTourPath(g2, w, h);

        // 4. Draw Department Buildings / Nodes
        drawDepartmentNodes(g2, w, h);

        // 5. Draw Animated Teacher/Auditor Avatar
        drawAuditorAvatar(g2, w, h);

        // 6. Draw Real-time Bitmask Telemetry HUD
        drawTelemetryHUD(g2, w, h);

        g2.dispose();
    }

    private void drawCampusGrid(Graphics2D g2, int w, int h) {
        g2.setColor(new Color(26, 34, 52, 100));
        g2.setStroke(new BasicStroke(1.0f));
        int step = 40;
        for (int x = 0; x < w; x += step) {
            g2.drawLine(x, 0, x, h);
        }
        for (int y = 0; y < h; y += step) {
            g2.drawLine(0, y, w, y);
        }

        // Campus boundary title
        g2.setColor(ACCENT_SKIN);
        g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
        g2.drawString("EDUTRACK UNIVERSITY CENTRAL CAMPUS MAP • BITMASK DP TSP SOLVER", 24, 26);
    }

    private void drawTransitNetwork(Graphics2D g2, int w, int h) {
        g2.setColor(PATH_DEFAULT);
        g2.setStroke(new BasicStroke(1.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1.0f, new float[]{4, 4}, 0));

        for (int i = 0; i < departments.length; i++) {
            for (int j = i + 1; j < departments.length; j++) {
                double x1 = departments[i].x * w;
                double y1 = departments[i].y * h;
                double x2 = departments[j].x * w;
                double y2 = departments[j].y * h;
                g2.draw(new Line2D.Double(x1, y1, x2, y2));

                // Draw transit distance label in minutes
                double mx = (x1 + x2) / 2.0;
                double my = (y1 + y2) / 2.0;
                g2.setFont(new Font("Segoe UI", Font.PLAIN, 10));
                g2.setColor(new Color(148, 163, 184, 180));
                g2.drawString(distMatrix[i][j] + "m", (float) mx - 8, (float) my - 2);
                g2.setColor(PATH_DEFAULT);
            }
        }
    }

    private void drawOptimalTourPath(Graphics2D g2, int w, int h) {
        if (currentTour == null || currentTour.path == null || currentTour.path.size() < 2) return;

        MyArrayList<Integer> p = currentTour.path;

        // Draw full planned optimal tour outline in subtle skin tone
        g2.setStroke(new BasicStroke(3.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.setColor(new Color(245, 210, 165, 100));
        for (int i = 0; i < p.size() - 1; i++) {
            int u = p.get(i);
            int v = p.get(i + 1);
            g2.draw(new Line2D.Double(departments[u].x * w, departments[u].y * h,
                    departments[v].x * w, departments[v].y * h));
        }

        // Draw traversed edges in glowing emerald
        g2.setColor(PATH_TRAVERSED);
        g2.setStroke(new BasicStroke(4.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        for (int i = 0; i < currentStepIndex && i < p.size() - 1; i++) {
            int u = p.get(i);
            int v = p.get(i + 1);
            g2.draw(new Line2D.Double(departments[u].x * w, departments[u].y * h,
                    departments[v].x * w, departments[v].y * h));
        }

        // Draw currently animating active edge in glowing skin/sand gold
        if (currentStepIndex < p.size() - 1) {
            int u = p.get(currentStepIndex);
            int v = p.get(currentStepIndex + 1);
            double x1 = departments[u].x * w;
            double y1 = departments[u].y * h;
            double x2 = departments[v].x * w;
            double y2 = departments[v].y * h;

            double currX = x1 + (x2 - x1) * transitionProgress;
            double currY = y1 + (y2 - y1) * transitionProgress;

            g2.setColor(PATH_ACTIVE);
            g2.setStroke(new BasicStroke(4.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.draw(new Line2D.Double(x1, y1, currX, currY));
        }
    }

    private void drawDepartmentNodes(Graphics2D g2, int w, int h) {
        int r = 26; // node radius

        for (DeptNode node : departments) {
            double cx = node.x * w;
            double cy = node.y * h;

            boolean isVisited = false;
            boolean isCurrent = false;

            if (currentTour != null && currentTour.path != null) {
                for (int i = 0; i <= currentStepIndex && i < currentTour.path.size(); i++) {
                    if (currentTour.path.get(i) == node.id) {
                        isVisited = true;
                    }
                }
                if (currentStepIndex < currentTour.path.size() && currentTour.path.get(currentStepIndex) == node.id) {
                    isCurrent = true;
                }
            }

            // Glow ring if current or hovered
            if (isCurrent || node == hoveredNode) {
                g2.setColor(new Color(node.accentColor.getRed(), node.accentColor.getGreen(), node.accentColor.getBlue(), 70));
                g2.fill(new Ellipse2D.Double(cx - r - 8, cy - r - 8, (r + 8) * 2, (r + 8) * 2));
            }

            // Node Circle
            g2.setColor(isCurrent ? PATH_ACTIVE : (isVisited ? PATH_TRAVERSED : node.accentColor));
            g2.fill(new Ellipse2D.Double(cx - r, cy - r, r * 2, r * 2));

            // Inner dark circle for contrast
            g2.setColor(new Color(24, 28, 36));
            g2.fill(new Ellipse2D.Double(cx - r + 3, cy - r + 3, (r - 3) * 2, (r - 3) * 2));

            // Node Short Code
            g2.setColor(isCurrent ? ACCENT_SKIN : TEXT_WHITE);
            g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
            FontMetrics fm = g2.getFontMetrics();
            int tw = fm.stringWidth(node.shortCode);
            g2.drawString(node.shortCode, (float) (cx - tw / 2.0), (float) (cy + fm.getAscent() / 2.0 - 2));

            // Department Name Label Box below node
            g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
            FontMetrics fmName = g2.getFontMetrics();
            int nameWidth = fmName.stringWidth(node.name);

            g2.setColor(new Color(18, 24, 38, 220));
            g2.fillRoundRect((int) (cx - nameWidth / 2.0 - 6), (int) (cy + r + 4), nameWidth + 12, 20, 6, 6);
            g2.setColor(new Color(59, 73, 103));
            g2.drawRoundRect((int) (cx - nameWidth / 2.0 - 6), (int) (cy + r + 4), nameWidth + 12, 20, 6, 6);

            g2.setColor(isVisited ? new Color(74, 222, 128) : TEXT_WHITE);
            g2.drawString(node.name, (float) (cx - nameWidth / 2.0), (float) (cy + r + 18));

            // Visited Checkmark Badge
            if (isVisited) {
                g2.setColor(new Color(16, 185, 129));
                g2.fillOval((int) (cx + r - 10), (int) (cy - r), 16, 16);
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 10));
                g2.drawString("✓", (float) (cx + r - 6), (float) (cy - r + 12));
            }
        }
    }

    private void drawAuditorAvatar(Graphics2D g2, int w, int h) {
        if (currentTour == null || currentTour.path == null || currentTour.path.isEmpty()) return;

        MyArrayList<Integer> p = currentTour.path;
        double avatarX, avatarY;

        if (currentStepIndex >= p.size() - 1) {
            int lastNode = p.get(p.size() - 1);
            avatarX = departments[lastNode].x * w;
            avatarY = departments[lastNode].y * h;
        } else {
            int u = p.get(currentStepIndex);
            int v = p.get(currentStepIndex + 1);
            double x1 = departments[u].x * w;
            double y1 = departments[u].y * h;
            double x2 = departments[v].x * w;
            double y2 = departments[v].y * h;

            avatarX = x1 + (x2 - x1) * transitionProgress;
            avatarY = y1 + (y2 - y1) * transitionProgress;
        }

        // Draw glowing halo around teacher avatar
        g2.setColor(new Color(245, 210, 165, 140));
        g2.fill(new Ellipse2D.Double(avatarX - 22, avatarY - 22, 44, 44));

        // Draw Avatar Pin in Warm Skin/Sand tone
        g2.setColor(ACCENT_SKIN);
        g2.fill(new Ellipse2D.Double(avatarX - 16, avatarY - 16, 32, 32));
        g2.setColor(new Color(15, 20, 32));
        g2.setStroke(new BasicStroke(2.0f));
        g2.draw(new Ellipse2D.Double(avatarX - 16, avatarY - 16, 32, 32));

        // Teacher Icon Symbol
        g2.setFont(new Font("Segoe UI", Font.BOLD, 13));
        FontMetrics fm = g2.getFontMetrics();
        String icon = "★";
        int tw = fm.stringWidth(icon);
        g2.drawString(icon, (float) (avatarX - tw / 2.0), (float) (avatarY + fm.getAscent() / 2.0 - 2));

        // Callout Tag above teacher
        String tag = "TEACHER AUDITOR";
        g2.setFont(new Font("Segoe UI", Font.BOLD, 9));
        FontMetrics fmTag = g2.getFontMetrics();
        int tagW = fmTag.stringWidth(tag);

        g2.setColor(ACCENT_SKIN);
        g2.fillRoundRect((int) (avatarX - tagW / 2.0 - 5), (int) (avatarY - 32), tagW + 10, 14, 4, 4);
        g2.setColor(new Color(15, 20, 32));
        g2.drawString(tag, (float) (avatarX - tagW / 2.0), (float) (avatarY - 21));
    }

    private void drawTelemetryHUD(Graphics2D g2, int w, int h) {
        int panelW = 340;
        int panelH = 110;
        int px = w - panelW - 20;
        int py = h - panelH - 20;

        // HUD Background Card
        g2.setColor(new Color(26, 34, 52, 240)); // Slate 800
        g2.fillRoundRect(px, py, panelW, panelH, 10, 10);
        g2.setColor(new Color(59, 73, 103));
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawRoundRect(px, py, panelW, panelH, 10, 10);

        // Header
        g2.setColor(ACCENT_SKIN);
        g2.setFont(new Font("Segoe UI", Font.BOLD, 11));
        g2.drawString("BITMASK DP REAL-TIME TELEMETRY", px + 14, py + 20);

        if (currentTour == null || currentTour.path == null) {
            g2.setColor(TEXT_MUTED);
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            g2.drawString("Click [Plan Optimal Tour (Bitmask DP)] to solve TSP", px + 14, py + 50);
            return;
        }

        // Calculate visited bitmask
        int mask = 0;
        int currentCost = 0;
        MyArrayList<Integer> p = currentTour.path;

        for (int i = 0; i <= currentStepIndex && i < p.size(); i++) {
            mask |= (1 << p.get(i));
            if (i > 0) {
                currentCost += distMatrix[p.get(i - 1)][p.get(i)];
            }
        }

        // Bitmask Binary string
        StringBuilder binStr = new StringBuilder();
        for (int i = departments.length - 1; i >= 0; i--) {
            binStr.append((mask & (1 << i)) != 0 ? "1" : "0");
        }

        int currDeptId = p.get(Math.min(currentStepIndex, p.size() - 1));
        String currDeptName = departments[currDeptId].name;

        // Draw details
        g2.setFont(new Font("Consolas", Font.BOLD, 12));
        g2.setColor(ACCENT_SKIN);
        g2.drawString("Bitmask State : " + binStr + " (Dec: " + mask + " / 63)", px + 14, py + 42);

        g2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        g2.setColor(TEXT_WHITE);
        g2.drawString("Current Node  : [" + currDeptId + "] " + currDeptName, px + 14, py + 62);

        g2.setColor(new Color(74, 222, 128));
        g2.drawString(String.format("Elapsed Time  : %d / %d mins (Tour Step %d of %d)",
                currentCost, currentTour.minCost, (currentStepIndex + 1), p.size()), px + 14, py + 82);

        // Efficiency Badge
        g2.setColor(ACCENT_GREEN);
        g2.setFont(new Font("Segoe UI", Font.BOLD, 10));
        g2.drawString("OPTIMAL TOUR: 85 MINS", px + 14, py + 100);
    }
}
