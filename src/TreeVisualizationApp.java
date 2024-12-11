import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class TreeVisualizationApp extends JPanel {
    private CustomTreeNode root;
    private Map<CustomTreeNode, Point> nodePositions = new HashMap<>();
    private int maxWidth = 800;  // Default minimum width
    private int maxHeight = 600; // Default minimum height

    public TreeVisualizationApp(CustomTreeNode root) {
        this.root = root;
        calculateNodePositions(root, (getWidth() / 2) + 50, 30, 400, 50);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (root != null) {
            drawTree((Graphics2D) g);
        }
    }

    private void calculateNodePositions(CustomTreeNode node, int x, int y, int xOffset, int yOffset) {
        if (node == null) return;

        // Store the current node's position
        nodePositions.put(node, new Point(x, y));

        // Update maxWidth and maxHeight for the preferred size
        maxWidth = Math.max(maxWidth, x + xOffset);
        maxHeight = Math.max(maxHeight, y + yOffset);

        // Calculate sibling positions
        int siblingX = x + xOffset;
        for (CustomTreeNode sibling : node.getSiblings()) {
            calculateNodePositions(sibling, siblingX, y, xOffset, yOffset);
            siblingX += xOffset;
        }

        // Calculate child positions
        int childCount = node.getChildCount();
        for (int i = 0; i < childCount; i++) {
            CustomTreeNode childNode = (CustomTreeNode) node.getChildAt(i);
            int childX = x - (childCount - 1) * xOffset / 2 + i * xOffset;
            int childY = y + yOffset;
            calculateNodePositions(childNode, childX, childY, Math.toIntExact(Math.round(xOffset / 1.5)), yOffset);
        }
    }

    private void drawTree(Graphics2D g) {
        g.setColor(Color.BLACK);
        g.setStroke(new BasicStroke(2));

        for (Map.Entry<CustomTreeNode, Point> entry : nodePositions.entrySet()) {
            CustomTreeNode node = entry.getKey();
            Point position = entry.getValue();
            drawNode(g, node, position.x, position.y);
            g.setColor(Color.BLACK);
            g.setStroke(new BasicStroke(2));
            // Draw connections to children
            int childCount = node.getChildCount();
            Point nodePos = nodePositions.get(node);
            for (int i = 0; i < childCount; i++) {
                CustomTreeNode childNode = (CustomTreeNode) node.getChildAt(i);
                Point childPos = nodePositions.get(childNode);
                g.drawLine(nodePos.x, nodePos.y + 15, childPos.x, childPos.y - 15);
            }

            // Draw connections to siblings
            for (CustomTreeNode sibling : node.getSiblings()) {
                Point siblingPos = nodePositions.get(sibling);
                int lineBeginning = Math.toIntExact(Math.round(position.x + node.toString().length()*4.5));
                int lineEnding = Math.toIntExact(Math.round(siblingPos.x - sibling.toString().length()*4.5));
                g.drawLine(lineBeginning, position.y, lineEnding, siblingPos.y);
            }
        }
    }

    private void drawNode(Graphics2D g, CustomTreeNode node, int x, int y) {
        String text = node.toString();
        int width = text.length() * 9;
        int height = 30;

        g.setColor(new Color(0, 100, 67));
        if (node.getNodeType().equals("circle")) {
            g.fillOval(x - width / 2, y - height / 2, width, height);
        } else if (node.getNodeType().equals("rectangle")) {
            g.fillRect(x - width / 2, y - height / 2, width, height);
        }

        g.setColor(Color.WHITE);
        FontMetrics fm = g.getFontMetrics();
        int textX = x - fm.stringWidth(text) / 2;
        int textY = y + fm.getHeight() / 4;
        g.drawString(text, textX, textY);
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(maxWidth, maxHeight);
    }

    public static void main(String[] args) {
        // Create nodes with specific types
        CustomTreeNode root = new CustomTreeNode("Root", "circle");
        CustomTreeNode child1 = new CustomTreeNode("Child 1", "rectangle");
        CustomTreeNode child2 = new CustomTreeNode("Child 2", "circle");
        CustomTreeNode sibling = new CustomTreeNode("Sibling", "rectangle");

        // Build the tree
        root.add(child1);
        root.add(child2);
        child1.addSibling(sibling);

        // Create and display the visualization
        TreeVisualizationApp app = new TreeVisualizationApp(root);

        // Wrap in a JScrollPane
        JScrollPane scrollPane = new JScrollPane(app);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        // Create the frame
        JFrame frame = new JFrame("Custom Tree Visualization");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);

        // Add the scrollable panel
        frame.add(scrollPane);

        // Display the frame
        frame.setVisible(true);
    }
}
