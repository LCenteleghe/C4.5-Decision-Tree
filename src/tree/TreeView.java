package tree;

import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.Enumeration;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

public class TreeView extends JFrame {
    private static final long serialVersionUID = 1L;
    private JTree tree;
    
    public TreeView(DecisionTree decisionTree) {

	DefaultMutableTreeNode treeViewRoot = new DefaultMutableTreeNode();

	createTreeViewNode(decisionTree.getRoot(), treeViewRoot);

	tree = new JTree(treeViewRoot);
	DefaultTreeCellRenderer renderer = (DefaultTreeCellRenderer) tree.getCellRenderer();
	renderer.setLeafIcon(null);
	renderer.setClosedIcon(null);
	renderer.setOpenIcon(null);

	tree.setRootVisible(false);
	tree.setShowsRootHandles(true);
	expandAll(tree);

	Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	this.add(tree);
	this.add(new JScrollPane(tree));
	this.setPreferredSize(new Dimension(screenSize.width * 2 / 7, screenSize.height * 4 / 5));
	this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	this.setTitle("Decision Tree Model");
	this.pack();
	this.setLocationRelativeTo(null);
	this.setModalExclusionType(Dialog.ModalExclusionType.APPLICATION_EXCLUDE);
	
	this.setVisible(true);
    }

    private void createTreeViewNode(Node subTreeRoot, DefaultMutableTreeNode subTreeViewRoot) {
	if (subTreeRoot.getLeft().isLeaf()) {
	    String description = subTreeRoot.getFeatureName() + " <= " + subTreeRoot.getSplitCondition().getSplitPoint()
		    + "  then class =  " + subTreeRoot.getLeft().getClassValue();
	    if(subTreeRoot.getLeft().isLogicallyPruned()){
		description += " (Soft Pruned)";
	    }
	    
	    subTreeViewRoot.add(new DefaultMutableTreeNode(description));
	} else {
	    String description = subTreeRoot.getFeatureName() + " <= "
		    + subTreeRoot.getSplitCondition().getSplitPoint();

	    DefaultMutableTreeNode dmtn = new DefaultMutableTreeNode(description);
	    subTreeViewRoot.add(dmtn);
	    createTreeViewNode(subTreeRoot.getLeft(), dmtn);
	}

	if (subTreeRoot.getRight().isLeaf()) {
	    String description = subTreeRoot.getFeatureName() + " > " + subTreeRoot.getSplitCondition().getSplitPoint()
		    + "  then class =  " + subTreeRoot.getRight().getClassValue();
	    
	    if(subTreeRoot.getRight().isLogicallyPruned()){
		description += " (Soft Pruned)";
	    }

	    subTreeViewRoot.add(new DefaultMutableTreeNode(description));
	} else {
	    String description = subTreeRoot.getFeatureName() + " > " + subTreeRoot.getSplitCondition().getSplitPoint();

	    DefaultMutableTreeNode dmtn = new DefaultMutableTreeNode(description);
	    subTreeViewRoot.add(dmtn);
	    createTreeViewNode(subTreeRoot.getRight(), dmtn);
	}
    }

    public void expandAll(JTree tree) {
	TreeNode root = (TreeNode) tree.getModel().getRoot();
	expandAll(tree, new TreePath(root));
    }

    private void expandAll(JTree tree, TreePath parent) {
	TreeNode node = (TreeNode) parent.getLastPathComponent();
	if (node.getChildCount() >= 0) {
	    for (Enumeration<?> e = node.children(); e.hasMoreElements();) {
		TreeNode n = (TreeNode) e.nextElement();
		TreePath path = parent.pathByAddingChild(n);
		expandAll(tree, path);
	    }
	}
	tree.expandPath(parent);
    }
}