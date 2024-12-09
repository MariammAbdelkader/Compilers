import javax.swing.*;
import java.awt.*;

public class TreeVisualizationApp extends JPanel {
    private CustomTreeNode root;

    public TreeVisualizationApp(CustomTreeNode root) {
        this.root = root;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (root != null) {
            drawTree((Graphics2D) g, root, getWidth() / 2, 30, 600, 50);
        }
    }

    private void drawNode(Graphics2D g, CustomTreeNode node, int x, int y) {
        String text = node.toString();
        int width = text.length() * 9;
        int height = 30;
        
        g.setColor(new Color(00, 100, 67));
        
        if (node.getNodeType().equals("circle")) {
            g.fillOval(x - width/2, y - height/2, width, height);
        } else if (node.getNodeType().equals("rectangle")) {
            g.fillRect(x - width/2, y - height/2, width, height);
        }
        
        g.setColor(Color.WHITE);
        // Center the text in the node
        FontMetrics fm = g.getFontMetrics();
        int textX = x - fm.stringWidth(text)/2;
        int textY = y + fm.getHeight()/4;
        g.drawString(text, textX, textY);
    }

    private void drawTree(Graphics2D g, CustomTreeNode node, int x, int y, int xOffset, int yOffset) {
        // Draw the current node
        drawNode(g, node, x, y);

        // Draw sibling connections first
        g.setColor(Color.BLUE);
        g.setStroke(new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{9}, 0));
        for (CustomTreeNode sibling : node.getSiblings()) {
            int siblingX = x + xOffset;
            // Draw dashed line for sibling connection
            g.drawLine(x + node.toString().length() * 2, y, 
                      siblingX - sibling.toString().length() * 2, y);
            // Draw the sibling node
            drawNode(g, sibling, siblingX, y);
        }

        // Draw lines and child nodes
        int childCount = node.getChildCount();
        g.setColor(Color.BLACK);
        g.setStroke(new BasicStroke(2));
        
        for (int i = 0; i < childCount; i++) {
            CustomTreeNode childNode = (CustomTreeNode) node.getChildAt(i);
            int childX = x - (childCount - 1) * xOffset / 2 + i * xOffset;
            int childY = y + yOffset;

            // Draw line to child
            g.drawLine(x, y + 15, childX, childY - 15);

            // Recursively draw the child node
            drawTree(g, childNode, childX, childY, xOffset / 2, yOffset);
        }
    }

    public static void main(String[] args){
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
        // Create the frame
        JFrame frame = new JFrame("Custom Tree Visualization");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1500, 400);

        //Add the custom tree panel
        frame.add(app);

        // Display the frame
        frame.setVisible(true);
    }
}