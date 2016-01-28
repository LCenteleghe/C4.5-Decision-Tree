package tree;

import java.util.List;

public class DecisionTree implements Classifier {
    private Node root = new Node();
    private Integer size = 0;
    private Integer maxDepth = 0;

    public DecisionTree(DataSet trainingDataSet) {
	maxDepth = decisionTreeLearning(trainingDataSet.getInstancesSet(), new InstancesSet(),
		trainingDataSet.getFeatures(), root, 0);
    }

    public DecisionTree(DataSet trainingDataSet, DataSet pruningDataSet) {
	this(trainingDataSet);
	prune(trainingDataSet.getInstancesSet(), pruningDataSet.getInstancesSet());
    }

    protected DecisionTree(InstancesSet trainingInstances, List<String> featuresList) {
	maxDepth = decisionTreeLearning(trainingInstances, new InstancesSet(), featuresList, root, 0);
    }

    protected DecisionTree(InstancesSet trainingInstances, List<String> features, DataSet pruningSet) {
	this(trainingInstances, features);
	prune(trainingInstances, pruningSet.getInstancesSet());
    }

    private Integer decisionTreeLearning(InstancesSet instances, InstancesSet parentInstances, List<String> features,
	    Node subTreeRoot, Integer maxDepth) {
	size++;
	if (instances.isEmpty()) {
	    subTreeRoot.setClassValue((instances.getMajorityClass()));
	} else if (instances.haveAllSameClass()) {
	    subTreeRoot.setClassValue((instances.getMajorityClass()));
	} else {
	    SplitCondition bestSplitCondition = instances.getBestSplitCondition();
	    if (bestSplitCondition != null) {
		subTreeRoot.setSplitCondition(bestSplitCondition);
		subTreeRoot.setFeatureName(features.get(bestSplitCondition.getFeatureIdx()));
		subTreeRoot.createLeaves();
		InstancesSet[] instancesSets = instances.split(bestSplitCondition);
		return Math.max(
			decisionTreeLearning(instancesSets[0], instances, features, subTreeRoot.getLeft(),
				maxDepth + 1),
			decisionTreeLearning(instancesSets[1], instances, features, subTreeRoot.getRight(),
				maxDepth + 1));
	    } else {
		subTreeRoot.setClassValue((instances.getMajorityClass()));
	    }
	}
	return maxDepth;
    }

    public void classify(InstancesSet testInstances) {
	for (Instance instance : testInstances.getInstances()) {
	    classify(instance);
	}
    }

    public void classify(Instance instance) {
	instance.setPredictedCategoryValue(this.getClassification(instance));
    }

    public Integer getClassification(Instance instance) {
	Node current = root;
	while (!current.isLeaf()) {
	    if (current.getSplitCondition().belongsToLeftNode(instance)) {
		current = current.getLeft();
	    } else {
		current = current.getRight();
	    }
	}

	return current.getClassValue();
    }

    public Node getRoot() {
	return root;
    }

    public Integer getSize() {
	return size;
    }

    public Integer getMaxDepth() {
	return maxDepth;
    }

    private void prune(InstancesSet trainingInstances, InstancesSet pruningInstances) {
	pupulatePruningStructure(trainingInstances, pruningInstances);
	postOrderPruning(root);
	maxDepth = calculateDepth(root, 0);
    }

    private void postOrderPruning(Node subtreeRoot) {
	if (subtreeRoot.getLeft() != null && subtreeRoot.getLeft().isLeaf()) {
	    if (shouldPrune(subtreeRoot.getLeft(), subtreeRoot)) {
		subtreeRoot.getLeft().prune(subtreeRoot.getMajorityClassTrainingSet());
		size--;
	    }
	} else {
	    postOrderPruning(subtreeRoot.getLeft());
	}

	if (subtreeRoot.getRight() != null && subtreeRoot.getRight().isLeaf()) {
	    if (shouldPrune(subtreeRoot.getRight(), subtreeRoot)) {
		subtreeRoot.getRight().prune(subtreeRoot.getMajorityClassTrainingSet());
		size--;
	    }
	} else {
	    postOrderPruning(subtreeRoot.getRight());
	}

	if ((subtreeRoot.getLeft() != null && subtreeRoot.getRight() != null)
		&& (subtreeRoot.getLeft().isLogicallyPruned() && subtreeRoot.getRight().isLogicallyPruned())) {
	    subtreeRoot.setClassValue(subtreeRoot.getMajorityClassTrainingSet());
	    subtreeRoot.pruneLeaves();
	}
    }

    private boolean shouldPrune(Node candidate, Node candidatesParent) {
	if (candidate.simulatePruningAcc(candidate.getClassValue()) <= candidate.simulatePruningAcc(candidatesParent.getMajorityClassTrainingSet())) {
	    return true;
	}
	return false;
    }

    private void pupulatePruningStructure(InstancesSet trainingInstances, InstancesSet pruningInstances) {
	for (Instance instance : trainingInstances.getInstances()) {
	    Node current = root;
	    while (!current.isLeaf()) {
		current.addTrainingInstance(instance);
		if (current.getSplitCondition().belongsToLeftNode(instance)) {
		    current = current.getLeft();
		} else {
		    current = current.getRight();
		}
	    }
	    current.addTrainingInstance(instance);
	}
	for (Instance instance : pruningInstances.getInstances()) {
	    Node current = root;
	    while (!current.isLeaf()) {
		current.addPruningInstance(instance);
		if (current.getSplitCondition().belongsToLeftNode(instance)) {
		    current = current.getLeft();
		} else {
		    current = current.getRight();
		}
	    }
	    current.addPruningInstance(instance);
	}
    }
    
    private Integer calculateDepth(Node subtreeRoot, Integer depth){
	if(subtreeRoot.isLeaf()){
	    return depth;
	}
	
	return Math.max(calculateDepth(subtreeRoot.getLeft(),depth+1), calculateDepth(subtreeRoot.getRight(), depth+1));
    }

    @Override
    public String getSizeDescription() {
	return "Number of Nodes: " + this.getSize() + "   Max Depth: " + this.getMaxDepth();
    }
    


}
