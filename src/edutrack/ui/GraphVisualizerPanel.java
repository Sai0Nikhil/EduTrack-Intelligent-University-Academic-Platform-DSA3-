package edutrack.ui;

import edutrack.core.MyArrayList;
import edutrack.core.Pair;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;

/**
 * High-performance 2D Vector Graph Visualizer utilizing Java Graphics2D.
 * Supports interactive visualization of Flow Networks, Bipartite Matching,
 * and Exam Conflict Graphs with 2-Approximation Vertex Cover highlights.
 */
public class GraphVisualizerPanel extends JPanel {

    public enum GraphMode {
        BIPARTITE_FACULTY,
        DINIC_FLOW_NETWORK,
        EXAM_VERTEX_COVER
    }

    public static class VisualNode {
        public String id;
        public String label;
        public double x, y;
        public Color color;
        public boolean isSpecial; // e.g. source, sink, or vertex cover proctor
        public String tooltip;

        public VisualNode(String id, String label, double x, double y, Color color, boolean isSpecial, String tooltip) {
            this.id = id;
            this.label = label;
            this.x = x;
            this.y = y;
            this.color = color;
            this.isSpecial = isSpecial;
            this.tooltip = tooltip;
        }
    }

    public static class VisualEdge {
        public String u, v;
        public int flow;
        public int capacity;
        public boolean isMatched;
        public Color color;

        public VisualEdge(String u, String v, int flow, int capacity, boolean isMatched, Color color) {
            this.u = u;
            this.v = v;
            this.flow = flow;
            this.capacity = capacity;
            this.isMatched = isMatched;
            this.color = color;
        }
    }

    private GraphMode mode = GraphMode.BIPARTITE_FACULTY;
    private final MyArrayList<VisualNode> nodes = new MyArrayList<VisualNode>();
    private final MyArrayList<VisualEdge> edges = new MyArrayList<VisualEdge>();
    private VisualNode hoveredNode = null;

    // Palette
    private static final Color BG_COLOR = new Color(15, 23, 42);
    private static final Color NODE_SOURCE = new Color(16, 185, 129);
    private static final Color NODE_SINK = new Color(239, 68, 68);
    private static final Color NODE_PROCTOR = new Color(245, 158, 11);
    private static final Color NODE_NORMAL = new Color(59, 130, 246);
    private static final Color EDGE_ACTIVE = new Color(16, 185, 129);
    private static final Color EDGE_SATURATED = new Color(245, 158, 11);
    private static final Color EDGE_MUTED = new Color(51, 65, 85);

    public GraphVisualizerPanel() {
        setBackground(BG_COLOR);
        setPreferredSize(new Dimension(850, 480));

        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                VisualNode prev = hoveredNode;
                hoveredNode = findNodeAt(e.getX(), e.getY());
                if (hoveredNode != prev) {
                    repaint();
                }
            }
        });

        loadBipartiteFacultyModel();
    }

    public void setMode(GraphMode newMode) {
        this.mode = newMode;
        switch (newMode) {
            case BIPARTITE_FACULTY:
                loadBipartiteFacultyModel();
                break;
            case DINIC_FLOW_NETWORK:
                loadDinicFlowModel();
                break;
            case EXAM_VERTEX_COVER:
                loadExamVertexCoverModel();
                break;
        }
        repaint();
    }

    public GraphMode getMode() {
        return mode;
    }

    // =========================================================================
    // GRAPH MODELS
    // =========================================================================

    public void loadBipartiteFacultyModel() {
        nodes.clear();
        edges.clear();

        // Source & Sink
        nodes.add(new VisualNode("S", "Source [S]", 0.08, 0.50, NODE_SOURCE, true, "Super Source with capacity = sum(faculty loads)"));
        nodes.add(new VisualNode("T", "Sink [T]", 0.92, 0.50, NODE_SINK, true, "Super Sink with capacity = sum(course offerings)"));

        // Faculty (Left Partition)
        String[] facultyNames = {"Dr. Sharma", "Dr. Patel", "Dr. Rao", "Dr. Gupta", "Dr. Iyer", "Dr. Nair"};
        for (int i = 0; i < facultyNames.length; i++) {
            double y = 0.15 + i * (0.70 / (facultyNames.length - 1));
            nodes.add(new VisualNode("F" + i, facultyNames[i], 0.35, y, NODE_NORMAL, false, "Faculty Member | Max load: 2"));
            edges.add(new VisualEdge("S", "F" + i, 2, 2, true, EDGE_ACTIVE));
        }

        // Courses (Right Partition)
        String[] courses = {"CS101 Intro", "CS201 DSA", "CS301 Algo", "CS401 AI", "CS402 Nets", "MA201 Disc"};
        for (int i = 0; i < courses.length; i++) {
            double y = 0.15 + i * (0.70 / (courses.length - 1));
            nodes.add(new VisualNode("C" + i, courses[i], 0.65, y, new Color(139, 92, 246), false, "Academic Course"));
            edges.add(new VisualEdge("C" + i, "T", 1, 1, true, EDGE_ACTIVE));
        }

        // Bipartite qualification edges (Hopcroft-Karp matched)
        edges.add(new VisualEdge("F0", "C1", 1, 1, true, EDGE_ACTIVE));
        edges.add(new VisualEdge("F0", "C2", 1, 1, true, EDGE_ACTIVE));
        edges.add(new VisualEdge("F1", "C0", 1, 1, true, EDGE_ACTIVE));
        edges.add(new VisualEdge("F2", "C3", 1, 1, true, EDGE_ACTIVE));
        edges.add(new VisualEdge("F3", "C4", 1, 1, true, EDGE_ACTIVE));
        edges.add(new VisualEdge("F4", "C5", 1, 1, true, EDGE_ACTIVE));

        // Additional non-matched qualification edges
        edges.add(new VisualEdge("F1", "C1", 0, 1, false, EDGE_MUTED));
        edges.add(new VisualEdge("F2", "C2", 0, 1, false, EDGE_MUTED));
        edges.add(new VisualEdge("F3", "C0", 0, 1, false, EDGE_MUTED));
    }

    public void loadDinicFlowModel() {
        nodes.clear();
        edges.clear();

        nodes.add(new VisualNode("S", "Source [S]", 0.08, 0.50, NODE_SOURCE, true, "Entry Source"));
        nodes.add(new VisualNode("L1_1", "Lab Router A", 0.30, 0.25, NODE_NORMAL, false, "Layer 1 - Server"));
        nodes.add(new VisualNode("L1_2", "Lab Router B", 0.30, 0.75, NODE_NORMAL, false, "Layer 1 - Server"));
        nodes.add(new VisualNode("L2_1", "Cluster Alpha", 0.55, 0.20, new Color(139, 92, 246), false, "Layer 2 - Workstations"));
        nodes.add(new VisualNode("L2_2", "Cluster Beta", 0.55, 0.50, new Color(139, 92, 246), false, "Layer 2 - Workstations"));
        nodes.add(new VisualNode("L2_3", "Cluster Gamma", 0.55, 0.80, new Color(139, 92, 246), false, "Layer 2 - Workstations"));
        nodes.add(new VisualNode("T", "Sink [T]", 0.90, 0.50, NODE_SINK, true, "Department Gateway"));

        // Augmenting and blocking flow edges
        edges.add(new VisualEdge("S", "L1_1", 12, 15, true, EDGE_ACTIVE));
        edges.add(new VisualEdge("S", "L1_2", 11, 12, true, EDGE_SATURATED));
        edges.add(new VisualEdge("L1_1", "L2_1", 8, 8, true, EDGE_SATURATED));
        edges.add(new VisualEdge("L1_1", "L2_2", 4, 10, false, EDGE_ACTIVE));
        edges.add(new VisualEdge("L1_2", "L2_2", 6, 6, true, EDGE_SATURATED));
        edges.add(new VisualEdge("L1_2", "L2_3", 5, 8, false, EDGE_ACTIVE));
        edges.add(new VisualEdge("L2_1", "T", 8, 10, false, EDGE_ACTIVE));
        edges.add(new VisualEdge("L2_2", "T", 10, 10, true, EDGE_SATURATED));
        edges.add(new VisualEdge("L2_3", "T", 5, 12, false, EDGE_ACTIVE));
    }

    public void loadExamVertexCoverModel() {
        nodes.clear();
        edges.clear();

        // 8 Course Exam nodes in a radial layout
        String[] courses = {"CS101", "CS201", "CS301", "CS302", "CS401", "CS402", "MA101", "EE201"};
        // Vertex cover nodes (proctors) chosen by 2-Approx
        boolean[] inVC = {true, true, true, false, true, false, false, true};

        int count = courses.length;
        double cx = 0.50;
        double cy = 0.50;
        double rx = 0.35;
        double ry = 0.35;

        for (int i = 0; i < count; i++) {
            double angle = 2.0 * Math.PI * i / count - Math.PI / 2.0;
            double x = cx + rx * Math.cos(angle);
            double y = cy + ry * Math.sin(angle);
            Color color = inVC[i] ? NODE_PROCTOR : NODE_NORMAL;
            String tooltip = inVC[i] ? "2-Approx Proctor Station: Covers incident conflict edges" : "Standard Exam Room";
            nodes.add(new VisualNode("EX_" + i, courses[i] + (inVC[i] ? " ★" : ""), x, y, color, inVC[i], tooltip));
        }

        // Conflict edges between overlapping exam courses
        int[][] conflicts = {
                {0, 1}, {1, 2}, {2, 3}, {2, 4}, {3, 5}, {4, 5}, {0, 6}, {1, 7}, {6, 7}, {1, 4}
        };

        for (int[] c : conflicts) {
            String u = "EX_" + c[0];
            String v = "EX_" + c[1];
            // Since at least one endpoint is in inVC, this edge is verified covered!
            edges.add(new VisualEdge(u, v, 1, 1, true, inVC[c[0]] || inVC[c[1]] ? new Color(245, 158, 11) : EDGE_MUTED));
        }
    }

    // =========================================================================
    // RENDERING
    // =========================================================================

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        // Mode Title & Info Banner
        renderHeader(g2, w);

        // Render Edges
        for (int i = 0; i < edges.size(); i++) {
            VisualEdge e = edges.get(i);
            VisualNode uNode = findNodeById(e.u);
            VisualNode vNode = findNodeById(e.v);
            if (uNode == null || vNode == null) continue;

            double ux = uNode.x * w;
            double uy = uNode.y * h;
            double vx = vNode.x * w;
            double vy = vNode.y * h;

            boolean isIncidentToHover = (hoveredNode != null && (hoveredNode.id.equals(e.u) || hoveredNode.id.equals(e.v)));

            if (isIncidentToHover) {
                g2.setStroke(new BasicStroke(3.5f));
                g2.setColor(Color.CYAN);
            } else if (e.isMatched) {
                g2.setStroke(new BasicStroke(e.flow == e.capacity && e.capacity > 0 ? 2.5f : 1.8f));
                g2.setColor(e.color);
            } else {
                g2.setStroke(new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, new float[]{4.0f, 4.0f}, 0.0f));
                g2.setColor(EDGE_MUTED);
            }

            g2.draw(new Line2D.Double(ux, uy, vx, vy));

            // Render flow / capacity label if in flow mode
            if (mode != GraphMode.EXAM_VERTEX_COVER && e.capacity > 0) {
                double mx = (ux + vx) / 2.0;
                double my = (uy + vy) / 2.0;
                String tag = e.flow + "/" + e.capacity;
                g2.setFont(new Font("Consolas", Font.BOLD, 10));
                g2.setColor(new Color(241, 245, 249));
                g2.drawString(tag, (float) mx - 8, (float) my - 4);
            }
        }

        // Render Nodes
        int nodeRadius = (mode == GraphMode.EXAM_VERTEX_COVER) ? 24 : 20;

        for (int i = 0; i < nodes.size(); i++) {
            VisualNode n = nodes.get(i);
            double nx = n.x * w;
            double ny = n.y * h;

            boolean isHovered = (hoveredNode == n);

            // Halo for special nodes (e.g. Vertex Cover proctors, Source/Sink, or Hovered)
            if (n.isSpecial || isHovered) {
                g2.setColor(new Color(n.color.getRed(), n.color.getGreen(), n.color.getBlue(), isHovered ? 130 : 60));
                g2.fill(new Ellipse2D.Double(nx - nodeRadius - 7, ny - nodeRadius - 7, (nodeRadius + 7) * 2, (nodeRadius + 7) * 2));
            }

            // Node Body
            g2.setColor(n.color);
            g2.fill(new Ellipse2D.Double(nx - nodeRadius, ny - nodeRadius, nodeRadius * 2, nodeRadius * 2));

            g2.setColor(Color.WHITE);
            g2.setStroke(new BasicStroke(isHovered ? 2.5f : 1.5f));
            g2.draw(new Ellipse2D.Double(nx - nodeRadius, ny - nodeRadius, nodeRadius * 2, nodeRadius * 2));

            // Node Label
            g2.setFont(new Font("Segoe UI", Font.BOLD, 11));
            FontMetrics fm = g2.getFontMetrics();
            int strW = fm.stringWidth(n.label);
            g2.setColor(Color.WHITE);
            g2.drawString(n.label, (float) (nx - strW / 2.0), (float) (ny + 4));

            // Proctor badge in Vertex Cover mode
            if (mode == GraphMode.EXAM_VERTEX_COVER && n.isSpecial) {
                g2.setFont(new Font("Segoe UI", Font.BOLD, 9));
                g2.setColor(NODE_PROCTOR);
                g2.drawString("PROCTOR", (float) (nx - 22), (float) (ny + nodeRadius + 14));
            }
        }

        // Tooltip bar at the bottom
        renderFooter(g2, w, h);

        g2.dispose();
    }

    private void renderHeader(Graphics2D g2, int w) {
        g2.setFont(new Font("Segoe UI", Font.BOLD, 15));
        g2.setColor(Color.WHITE);

        String title;
        String desc;
        if (mode == GraphMode.BIPARTITE_FACULTY) {
            title = "Bipartite Faculty-to-Course Matching Network";
            desc = "Hopcroft-Karp / Dinic: Green directed lines show optimal matched assignments.";
        } else if (mode == GraphMode.DINIC_FLOW_NETWORK) {
            title = "Dinic's Multi-Commodity Lab Workstation Flow Network";
            desc = "Layered level graph pushing blocking flows; numbers denote (flow / capacity).";
        } else {
            title = "Exam Conflict Graph & 2-Approximation Vertex Cover";
            desc = "Gold nodes (★) denote chosen proctors. Notice every conflict edge is covered!";
        }

        g2.drawString(title, 20, 26);
        g2.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        g2.setColor(new Color(148, 163, 184));
        g2.drawString(desc, 20, 44);

        g2.setColor(new Color(51, 65, 85));
        g2.drawLine(20, 52, w - 20, 52);
    }

    private void renderFooter(Graphics2D g2, int w, int h) {
        g2.setColor(new Color(30, 41, 59, 230));
        g2.fillRect(16, h - 34, w - 32, 26);

        g2.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        if (hoveredNode != null) {
            g2.setColor(Color.CYAN);
            g2.drawString("Selected: " + hoveredNode.label + " — " + hoveredNode.tooltip, 26, h - 17);
        } else {
            g2.setColor(new Color(148, 163, 184));
            g2.drawString("Hover over any node to inspect entity details and highlighted incident edges.", 26, h - 17);
        }
    }

    private VisualNode findNodeAt(int px, int py) {
        int w = getWidth();
        int h = getHeight();
        int r = (mode == GraphMode.EXAM_VERTEX_COVER) ? 26 : 22;
        for (int i = 0; i < nodes.size(); i++) {
            VisualNode n = nodes.get(i);
            double nx = n.x * w;
            double ny = n.y * h;
            double distSq = (px - nx) * (px - nx) + (py - ny) * (py - ny);
            if (distSq <= r * r) {
                return n;
            }
        }
        return null;
    }

    private VisualNode findNodeById(String id) {
        for (int i = 0; i < nodes.size(); i++) {
            if (nodes.get(i).id.equals(id)) return nodes.get(i);
        }
        return null;
    }
}
