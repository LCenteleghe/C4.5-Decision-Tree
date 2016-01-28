package main;

import java.io.File;
import java.text.NumberFormat;

import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import tree.Classifier;
import tree.DataSet;
import tree.DecisionTree;
import tree.DecisionTreeEnsemble;
import tree.InstancesSet;
import tree.InvalidDataSetException;
import tree.TreeView;
import util.Utils;

public class Main {
    private static final int RESTART = 2;
    private static final int APPLY_TEST_SET = 1;
    private static final int VISUALIZE_TREE = 0;
    private static final int LOAD_TRAINING = 0;

    static Classifier classifier;

    public static void main(String[] args) {
	start();
    }

    public static void start() {
	showInitialMenu();
	showSecondMenu();
    }

    private static void showInitialMenu() {
	String[] buttons = { "Load Training Set", "Exit" };
	JDialog pleaseWaitMessage = createPleaseWaitMessage();

	while (true) {
	    int option = JOptionPane.showOptionDialog(null, "No Tree Built", "Decision Tree Classifier",
		    JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, buttons, buttons[0]);

	    if (option == LOAD_TRAINING) {
		JCheckBox chkPrunning = new JCheckBox("Use Reduced Error Prunning");
		JCheckBox chkBagging = new JCheckBox("Use Bagging");

		Object[] params = { chkPrunning, chkBagging };
		if (JOptionPane.showConfirmDialog(null, params, "Building Parameters",
			JOptionPane.DEFAULT_OPTION) < 0) {
		    continue;
		}

		File namesFile = Utils.filePicker("Select the 'names' file...", "Names File", "names");
		if (namesFile == null)
		    continue;

		File trainingFile = Utils.filePicker("Select the 'training set' file...", "Data File", "data", "csv");
		if (trainingFile == null)
		    continue;

		try {
		    if (chkPrunning.isSelected()) {
			File prunningFile = Utils.filePicker("Select the 'prunning set' file...", "Data File", "data",
				"csv");
			if (prunningFile == null)
			    continue;

			if (chkBagging.isSelected()) { // IF PRUNING AND BAGGING
						       // IS SELECTED
			    Integer numberOfTrees = intInputDialog(
				    "Enter the number of trees to generate: \nIt may take several seconds to generate all the trees!");
			    if (numberOfTrees == null)
				continue;

			    pleaseWaitMessage.setVisible(true);
			    classifier = new DecisionTreeEnsemble(new DataSet(namesFile, trainingFile),
				    new DataSet(namesFile, prunningFile), numberOfTrees);
			} else {// IF JUST PRUNING IS SELECTED
			    pleaseWaitMessage.setVisible(true);
			    classifier = new DecisionTree(new DataSet(namesFile, trainingFile),
				    new DataSet(namesFile, prunningFile));
			}

		    } else {

			if (chkBagging.isSelected()) { // IF JUST BAGGING IS
						       // SELECTED
			    Integer numberOfTrees = intInputDialog(
				    "Enter the number of trees to generate: \nIt may take several seconds to generate all the trees!");
			    if (numberOfTrees == null)
				continue;

			    pleaseWaitMessage.setVisible(true);
			    classifier = new DecisionTreeEnsemble(new DataSet(namesFile, trainingFile), numberOfTrees);
			} else {// IF NO EXTRA PARAMETER IS SELECTED
			    pleaseWaitMessage.setVisible(true);
			    classifier = new DecisionTree(new DataSet(namesFile, trainingFile));
			}
		    }
		} catch (InvalidDataSetException exp) {
		    JOptionPane.showMessageDialog(null, exp.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		    continue;
		}

		pleaseWaitMessage.setVisible(false);
		break;
	    } else {
		System.exit(0);
	    }
	}
    }

    private static void showSecondMenu() {
	while (true) {
	    String[] buttons = { "Visualize Tree", "Apply Test Set", "Restart", "Exit" };
	    int option = JOptionPane.showOptionDialog(null, classifier.getSizeDescription(), "Decision Tree Classifier",
		    JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, buttons, buttons[0]);
	    switch (option) {
	    case VISUALIZE_TREE:
		if (classifier instanceof DecisionTree) {
		    showTree((DecisionTree) classifier);
		} else {
		    JOptionPane.showMessageDialog(null,
			    "Option available just for simple decision trees (not ensembles)", "Invalid Operation",
			    JOptionPane.WARNING_MESSAGE);
		}
		break;
	    case APPLY_TEST_SET:
		File testFile = Utils.filePicker("Select the 'test set' file...", "Data File", "data", "csv");
		if (testFile == null) {
		    continue;
		}
		try {
		    InstancesSet testInstances = new InstancesSet(testFile);
		    classifier.classify(testInstances);

		    JOptionPane.showMessageDialog(null,
			    "Accuracy: " + formatAccuracy(testInstances.predictionsAccuracy()));
		} catch (InvalidDataSetException exp) {
		    JOptionPane.showMessageDialog(null, exp.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		    continue;
		}
		break;
	    case RESTART:
		start();
		break;
	    default:
		System.exit(0);
		break;
	    }
	}
    }

    private static Integer intInputDialog(String message) {
	String errorMessage = "";
	do {
	    String stringInput = JOptionPane.showInputDialog(null, errorMessage + message, "Bagging Parameter",
		    JOptionPane.PLAIN_MESSAGE);
	    if (stringInput == null) {
		return null;
	    }
	    if (!stringInput.matches("\\d+")) {
		errorMessage = "Please enter a number! ";
	    } else {
		return Integer.parseInt(stringInput);
	    }
	} while (true);
    }

    private static String formatAccuracy(Double accuracy) {
	NumberFormat defaultFormat = NumberFormat.getPercentInstance();
	defaultFormat.setMinimumFractionDigits(2);
	defaultFormat.setMaximumFractionDigits(4);
	return defaultFormat.format(accuracy);
    }

    private static JDialog createPleaseWaitMessage() {
	final JOptionPane optionPane = new JOptionPane("Building model...", JOptionPane.INFORMATION_MESSAGE,
		JOptionPane.DEFAULT_OPTION, null, new Object[] {}, null);
	final JDialog dialog = new JDialog();
	dialog.setContentPane(optionPane);
	dialog.setLocationRelativeTo(null);
	dialog.setTitle("Please Wait");
	dialog.pack();

	return dialog;
    }

    private static void showTree(DecisionTree decisionTree) {
	SwingUtilities.invokeLater(new Runnable() {
	    public void run() {
		new TreeView(decisionTree);
	    }
	});
    }
}
