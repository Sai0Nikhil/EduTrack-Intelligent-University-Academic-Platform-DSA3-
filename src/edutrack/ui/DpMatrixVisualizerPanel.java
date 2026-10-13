package edutrack.ui;

import edutrack.algorithms.dp.Levenshtein;
import edutrack.core.Pair;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * Interactive 2D Dynamic Programming Cost Matrix & Backtrace Alignment Visualizer.
 * Renders the full (M+1) x (N+1) Wagner-Fischer DP table, displays the mathematical recurrence,
 * highlights optimal backtrace cells in glowing amber, and prints step-by-step alignment operations.
 */
public class DpMatrixVisualizerPanel extends JPanel {

    private final JTable table;
    private final DefaultTableModel tableModel;
    private final JLabel formulaLabel;
    private final JTextArea traceArea;
    private Levenshtein.DpTraceResult currentResult;

    // Palette
    private static final Color BG_DARK = new Color(15, 20, 32);
    private static final Color BG_CARD = new Color(24, 32, 50);
    private static final Color ACCENT_SKIN = new Color(245, 210, 165);
    private static final Color TEXT_MAIN = new Color(240, 240, 245);
    private static final Color TEXT_MUTED = new Color(160, 170, 190);
    private static final Color BORDER_COLOR = new Color(45, 55, 80);

    public DpMatrixVisualizerPanel() {
        setLayout(new BorderLayout(10, 10));
        setBackground(BG_DARK);
        setBorder(new EmptyBorder(12, 12, 12, 12));

        // Recurrence header
        formulaLabel = new JLabel("<html><span style='color:#F5D2A5; font-weight:bold;'>DP Recurrence:</span> " +
                "<span style='color:#E2E8F0; font-family:monospace;'>D[i, j] = min( D[i-1, j] + 1 (Del), D[i, j-1] + 1 (Ins), D[i-1, j-1] + cost (Sub/Match) )</span></html>");
        formulaLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        formulaLabel.setBorder(new EmptyBorder(0, 4, 8, 4));
        add(formulaLabel, BorderLayout.NORTH);

        tableModel = new DefaultTableModel();
        table = new JTable(tableModel);
        table.setFont(new Font("Consolas", Font.BOLD, 13));
        table.setRowHeight(28);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        table.getTableHeader().setBackground(BG_CARD);
        table.getTableHeader().setForeground(ACCENT_SKIN);
        table.setShowGrid(true);
        table.setGridColor(BORDER_COLOR);
        table.setBackground(new Color(20, 26, 42));

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable tbl, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
                Component c = super.getTableCellRendererComponent(tbl, value, isSelected, hasFocus, row, col);
                setHorizontalAlignment(SwingConstants.CENTER);

                if (currentResult != null && col > 0) {
                    int i = row;
                    int j = col - 1;

                    boolean isPath = false;
                    for (int k = 0; k < currentResult.backtracePath.size(); k++) {
                        Pair<Integer, Integer> p = currentResult.backtracePath.get(k);
                        if (p.first == i && p.second == j) {
                            isPath = true;
                            break;
                        }
                    }

                    if (isPath) {
                        c.setBackground(new Color(254, 240, 138)); // Glowing amber for backtrace path
                        c.setForeground(new Color(20, 20, 20));
                        setFont(new Font("Consolas", Font.BOLD, 14));
                    } else {
                        c.setBackground(new Color(20, 26, 42));
                        c.setForeground(TEXT_MAIN);
                        setFont(new Font("Consolas", Font.PLAIN, 13));
                    }
                } else {
                    c.setBackground(BG_CARD);
                    c.setForeground(ACCENT_SKIN);
                    setFont(new Font("Segoe UI", Font.BOLD, 12));
                }
                return c;
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(BG_DARK);
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        scrollPane.setPreferredSize(new Dimension(550, 240));

        traceArea = new JTextArea(5, 30);
        traceArea.setEditable(false);
        traceArea.setFont(new Font("Consolas", Font.PLAIN, 12));
        traceArea.setBackground(BG_CARD);
        traceArea.setForeground(new Color(220, 225, 235));
        traceArea.setCaretColor(Color.WHITE);
        traceArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1), "Reconstructed Step-by-Step Alignment Path", 0, 0, new Font("Segoe UI", Font.BOLD, 12), ACCENT_SKIN),
                new EmptyBorder(6, 8, 6, 8)
        ));
        JScrollPane traceScroll = new JScrollPane(traceArea);
        traceScroll.setBorder(null);

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, scrollPane, traceScroll);
        split.setResizeWeight(0.60);
        split.setBorder(null);
        split.setOpaque(false);
        add(split, BorderLayout.CENTER);
    }

    public void displayTrace(Levenshtein.DpTraceResult result) {
        this.currentResult = result;
        tableModel.setRowCount(0);
        tableModel.setColumnCount(0);

        String s1 = result.source;
        String s2 = result.target;

        // Headers
        tableModel.addColumn("s1 \\ s2");
        tableModel.addColumn("ε (0)");
        for (int j = 0; j < s2.length(); j++) {
            tableModel.addColumn(s2.charAt(j) + " (" + (j + 1) + ")");
        }

        for (int i = 0; i <= s1.length(); i++) {
            Object[] row = new Object[s2.length() + 2];
            row[0] = (i == 0) ? "ε (0)" : (s1.charAt(i - 1) + " (" + i + ")");
            for (int j = 0; j <= s2.length(); j++) {
                row[j + 1] = result.dpTable[i][j];
            }
            tableModel.addRow(row);
        }

        // Trace operations text
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Minimum Edit Distance: %d (Source: \"%s\" -> Target: \"%s\")\n", result.distance, s1, s2));
        sb.append("Reconstructed Optimal Path Operations:\n");
        for (int i = result.operations.size() - 1; i >= 0; i--) {
            sb.append("  [Step ").append(result.operations.size() - i).append("] ").append(result.operations.get(i)).append("\n");
        }
        traceArea.setText(sb.toString());
        traceArea.setCaretPosition(0);
    }
}
