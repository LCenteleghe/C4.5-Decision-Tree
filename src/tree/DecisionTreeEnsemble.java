package tree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

public class DecisionTreeEnsemble implements Classifier {
    private List<DecisionTree> decisionTreeLst = new ArrayList<>();
    private Integer numberOfTrees;

    public DecisionTreeEnsemble(DataSet trainingDataSet, Integer numberOfTrees) {
	this.numberOfTrees = numberOfTrees;

	decisionTreeLst.add(new DecisionTree(trainingDataSet.getInstancesSet(), trainingDataSet.getFeatures()));
	for (int i = 1; i < numberOfTrees; i++) {
	    InstancesSet sample = SampleGenerator.generateSampleWithReplacement(trainingDataSet.getInstancesSet());
	    decisionTreeLst.add(new DecisionTree(sample, trainingDataSet.getFeatures()));
	}
    }

    public DecisionTreeEnsemble(DataSet trainingDataSet, DataSet prunningset, Integer numberOfTrees) {
	this.numberOfTrees = numberOfTrees;

	decisionTreeLst.add(new DecisionTree(trainingDataSet.getInstancesSet(), trainingDataSet.getFeatures()));
	for (int i = 1; i < numberOfTrees; i++) {
	    InstancesSet sample = SampleGenerator.generateSampleWithReplacement(trainingDataSet.getInstancesSet());
	    decisionTreeLst.add(new DecisionTree(sample, trainingDataSet.getFeatures(), prunningset));
	}
    }

    @Override
    public String getSizeDescription() {
	return "Number of trees: " + this.numberOfTrees;
    }

    @Override
    public void classify(InstancesSet testInstances) {
	for (Instance instance : testInstances.getInstances()) {
	    classify(instance);
	}
    }

    @Override
    public void classify(Instance instances) {
	HashMap<Integer, Integer> voteCounter = new HashMap<>();

	for (DecisionTree decisionTree : decisionTreeLst) {
	    Integer singleTreeClassification = decisionTree.getClassification(instances);
	    if (voteCounter.containsKey(singleTreeClassification)) {
		voteCounter.put(singleTreeClassification, voteCounter.get(singleTreeClassification) + 1);
	    } else {
		voteCounter.put(singleTreeClassification, 1);
	    }
	}

	instances.setPredictedCategoryValue(getMajorityVote(voteCounter));
    }

    private Integer getMajorityVote(HashMap<Integer, Integer> voteCounter) {
	Entry<Integer, Integer> maxEntry = null;
	for (Entry<Integer, Integer> entry : voteCounter.entrySet()) {
	    if (maxEntry == null || entry.getValue().compareTo(maxEntry.getValue()) > 0) {
		maxEntry = entry;
	    }
	}
	return maxEntry.getKey();
    }
}
