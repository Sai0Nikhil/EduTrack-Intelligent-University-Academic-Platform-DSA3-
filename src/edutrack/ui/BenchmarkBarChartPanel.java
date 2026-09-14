package edutrack.ui;

import edutrack.core.MyArrayList;
import edutrack.service.BenchmarkArenaService.ArenaResult;
import edutrack.service.BenchmarkArenaService.ShowdownCategory;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/**
 * Custom High-Performance Swing Component rendering horizontal comparative
 * bar charts for the Algorithm Benchmark Arena.
 */
public class BenchmarkBarChartPanel extends JPanel {

    private ShowdownCategory currentCategory;

    // Palette
    private static final Color BG_PANEL = new Color(20, 24, 33);
    private static final Color BG_BAR_TRACK = new Color(30, 41, 59);
    private static final Color TEXT_TITLE = new Color(248, 250, 252);
    private static final Color TEXT_SUBTITLE = new Color(148, 163, 184);
    private static final Color TEXT_MUTED = new Color(100, 116, 139);

    private static final Color[] BAR_COLORS = new Color[]{
            new Color(16, 185, 129),  // Emerald Green (Fastest)
            new Color(59, 130, 246),  // Sky Blue
            new Color(139, 92, 246),  // Purple
            new Color(245, 158, 11),  // Amber
            new Color(239, 68, 68)    // Crimson (Slowest / Baseline)
    };

    public BenchmarkBarChartPanel() {
        setBackground(BG_PANEL);
        setPreferredSize(new Dimension(800, 360));
    }

    public void displayCategory(ShowdownCategory category) {
        this.currentCategory = category;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();

        if (currentCategory == null || currentCategory.results.size() == 0) {
            renderEmptyState(g2, width, height);
            g2.dispose();
            return;
        }

        // Render Title & Meta
        g2.setFont(new Font("Segoe UI", Font.BOLD, 17));
        g2.setColor(TEXT_TITLE);
        g2.drawString(currentCategory.title, 24, 34);

        g2.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        g2.setColor(TEXT_SUBTITLE);
        g2.drawString(currentCategory.description, 24, 54);

        g2.setFont(new Font("Consolas", Font.PLAIN, 11));
        g2.setColor(new Color(56, 189, 248));
        g2.drawString(currentCategory.inputDescription, 24, 72);

        // Separator rule
        g2.setColor(new Color(51, 65, 85));
        g2.drawLine(24, 84, width - 24, 84);

        // Find min and max times
        MyArrayList<ArenaResult> results = currentCategory.results;
        long maxTime = 1;
        long minTime = Long.MAX_VALUE;
        for (int i = 0; i < results.size(); i++) {
            long t = results.get(i).elapsedNanos;
            if (t > maxTime) maxTime = t;
            if (t < minTime) minTime = t;
        }

        int startY = 105;
        int rowHeight = Math.max(48, (height - startY - 30) / Math.max(1, results.size()));
        int barLeft = 260;
        int barMaxRight = width - 180;
        int maxBarWidth = barMaxRight - barLeft;

        for (int i = 0; i < results.size(); i++) {
            ArenaResult res = results.get(i);
            int y = startY + i * rowHeight;

            // Algorithm Name
            g2.setFont(new Font("Segoe UI", Font.BOLD, 13));
            g2.setColor(TEXT_TITLE);
            g2.drawString(res.algorithmName, 24, y + 16);

            // Complexity tag
            g2.setFont(new Font("Consolas", Font.PLAIN, 11));
            g2.setColor(TEXT_MUTED);
            g2.drawString(res.complexity, 24, y + 32);

            // Bar Track
            g2.setColor(BG_BAR_TRACK);
            g2.fill(new RoundRectangle2D.Float(barLeft, y + 6, maxBarWidth, 22, 6, 6));

            // Bar Fill (normalized to maxTime)
            double ratio = (double) res.elapsedNanos / maxTime;
            int barWidth = Math.max(12, (int) (ratio * maxBarWidth));

            Color barColor = BAR_COLORS[Math.min(i, BAR_COLORS.length - 1)];
            if (res.elapsedNanos == minTime && results.size() > 1) {
                barColor = new Color(16, 185, 129); // Fastest is always vibrant green
            } else if (res.elapsedNanos == maxTime && results.size() > 1) {
                barColor = new Color(239, 68, 68); // Slowest is red
            }

            GradientPaint gp = new GradientPaint(
                    barLeft, y, barColor,
                    barLeft + barWidth, y, barColor.brighter()
            );
            g2.setPaint(gp);
            g2.fill(new RoundRectangle2D.Float(barLeft, y + 6, barWidth, 22, 6, 6));

            // Time text
            g2.setFont(new Font("Consolas", Font.BOLD, 12));
            g2.setColor(TEXT_TITLE);
            String timeStr = formatTime(res.elapsedNanos);
            g2.drawString(timeStr, barLeft + barWidth + 12, y + 22);

            // Speedup pill badge if significantly faster
            if (res.speedup > 1.05) {
                String speedupText = String.format("⚡ %.1fx FASTER", res.speedup);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 10));
                FontMetrics fm = g2.getFontMetrics();
                int badgeWidth = fm.stringWidth(speedupText) + 14;
                int badgeX = barLeft + maxBarWidth - badgeWidth - 8;

                if (barWidth < maxBarWidth - badgeWidth - 20) {
                    g2.setColor(new Color(6, 78, 59, 220));
                    g2.fill(new RoundRectangle2D.Float(badgeX, y + 6, badgeWidth, 22, 6, 6));
                    g2.setColor(new Color(52, 211, 153));
                    g2.drawString(speedupText, badgeX + 7, y + 21);
                }
            }
        }

        g2.dispose();
    }

    private void renderEmptyState(Graphics2D g2, int width, int height) {
        g2.setFont(new Font("Segoe UI", Font.BOLD, 18));
        g2.setColor(TEXT_SUBTITLE);
        String msg1 = "ALGORITHM BENCHMARK ARENA";
        FontMetrics fm1 = g2.getFontMetrics();
        g2.drawString(msg1, (width - fm1.stringWidth(msg1)) / 2, height / 2 - 20);

        g2.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        g2.setColor(TEXT_MUTED);
        String msg2 = "Select a tournament category above and click 'START ALGORITHM RACE' to evaluate head-to-head performance.";
        FontMetrics fm2 = g2.getFontMetrics();
        g2.drawString(msg2, (width - fm2.stringWidth(msg2)) / 2, height / 2 + 10);
    }

    private String formatTime(long nanos) {
        if (nanos < 1000) {
            return nanos + " ns";
        } else if (nanos < 1_000_000) {
            return String.format("%.2f µs", nanos / 1000.0);
        } else {
            return String.format("%.2f ms", nanos / 1_000_000.0);
        }
    }
}
