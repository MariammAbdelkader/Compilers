import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class TreeVisualizationApp extends JPanel {
    private DefaultMutableTreeNode root;

    public TreeVisualizationApp(DefaultMutableTreeNode root) {
        this.root = root;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (root != null) {
            drawTree((Graphics2D) g, root, getWidth() / 2, 30, 600, 50);
        }
    }

    private void drawTree(Graphics2D g, DefaultMutableTreeNode node, int x, int y, int xOffset, int yOffset) {
        // Draw the current node
        g.setColor(new Color(00, 100, 67));
        g.fillOval(x - 10, y - 20, node.toString().length() * 9, 30);
        g.setColor(Color.WHITE);
        g.drawString(node.toString(), x, y);

        // Draw lines and child nodes
        int childCount = node.getChildCount();
        for (int i = 0; i < childCount; i++) {
            DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) node.getChildAt(i);
            int childX = x - (childCount - 1) * xOffset / 2 + i * xOffset;
            int childY = y + yOffset;

            // Draw line to child
            g.setColor(Color.BLACK);
            g.setStroke(new BasicStroke(2));
            g.drawLine(x + node.toString().length() * 2, y + 10, childX + childNode.toString().length() * 2, childY - 10);

            // Recursively draw the child node
            drawTree(g, childNode, childX, childY, xOffset / 2, yOffset);
        }
    }

    public static void main(String[] args) {
        // Create the tree structure
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Root");
        DefaultMutableTreeNode child1 = new DefaultMutableTreeNode("Child 1");
        DefaultMutableTreeNode child2 = new DefaultMutableTreeNode("Child 2");
        root.add(child1);
        root.add(child2);
        child1.add(new DefaultMutableTreeNode("Grandchild 1"));
        child2.add(new DefaultMutableTreeNode("Grandchild 2"));
        child2.add(new DefaultMutableTreeNode("Grandchild 3"));

        // Create the frame
        JFrame frame = new JFrame("Custom Tree Visualization");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1500, 400);

        //Add the custom tree panel
        TreeVisualizationApp treePanel = new TreeVisualizationApp(root);
        frame.add(treePanel);

        // Display the frame
        frame.setVisible(true);

//        List<Token> tokens= new ArrayList<>();
//        tokens.add(new Token("IDENTIFIER", "x"));  // Identifier for the variable
//        tokens.add(new Token("ASSIGN", ":="));      // Assignment operator
//        tokens.add(new Token("NUMBER", "5"));       // Number for the right-hand side
//        tokens.add(new Token("SEMICOLON", ";"));     // End of statement
//
//
//        // Create the parser with the input tokens
//       Parser parser = new Parser(tokens);
//
//        try {
//            DefaultMutableTreeNode root = parser.parse();
//            JFrame frame = new JFrame("Custom Tree Visualization");
//            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//            frame.setSize(1500, 400);
//            TreeVisualizationApp treePanel = new TreeVisualizationApp(root);
//            frame.add(treePanel);
//            frame.setVisible(true);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    // Helper method to print the tree for visual confirmation

}