import javax.swing.tree.DefaultMutableTreeNode;
import java.util.List;
import java.util.ArrayList;
public class CustomTreeNode extends DefaultMutableTreeNode {
    private String nodeType; // "circle" or "rectangle"
    private List<CustomTreeNode> siblings;

    public CustomTreeNode(Object userObject, String nodeType) {
        super(userObject);
        this.nodeType = nodeType;
        this.siblings = new ArrayList<>();
    }

    public void addSibling(CustomTreeNode sibling) {
        siblings.add(sibling);
    }

    public List<CustomTreeNode> getSiblings() {
        return siblings;
    }

    public String getNodeType() {
        return nodeType;
    }
}