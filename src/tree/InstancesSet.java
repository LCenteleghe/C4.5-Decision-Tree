package tree;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

public class InstancesSet {
    private List<Instance> instances;
    private HashMap<Integer, Integer> classCounter = new HashMap<>();
    private Integer featuresNumber;

    public InstancesSet() {
	super();
	this.instances = new ArrayList<>();
    }

    public InstancesSet(File instancesFile) {
	this.instances = new ArrayList<>();
	try (BufferedReader brTrainingFile = new BufferedReader(new FileReader(instancesFile));) {

	    String line = brTrainingFile.readLine();
	    while (line != null) {
		String strArr[] = line.split(",");
		List<Double> featuresValues = new ArrayList<>(strArr.length);
		for (int i = 0; i < strArr.length - 1; i++) {
		    featuresValues.add(Double.parseDouble(strArr[i]));
		}
		Instance instance = new Instance(featuresValues, Integer.parseInt(strArr[strArr.length - 1].trim()));
		this.addInstance(instance);
		line = brTrainingFile.readLine();
	    }
	} catch (FileNotFoundException e) {
	    throw new RuntimeException(e);
	} catch (IOException e) {
	    throw new RuntimeException(e);
	} catch (Exception e) {
	    throw new InvalidDataSetException("Invalid data set(s)");
	}
    }

    public void addInstance(Instance instance) {
	if (featuresNumber == null) {
	    featuresNumber = instance.getFeaturesValues().size();
	}
	if (instance.getFeaturesValues().size() != featuresNumber) {
	    throw new RuntimeException("All the instances must have the same number of features.");
	}
	instances.add(instance);

	if (classCounter.containsKey(instance.getCategoryValue())) {
	    classCounter.put(instance.getCategoryValue(), classCounter.get(instance.getCategoryValue()) + 1);
	} else {
	    classCounter.put(instance.getCategoryValue(), 1);
	}
    }

    public boolean isEmpty() {
	return instances.isEmpty();
    }

    public Integer getNumberOfInstances() {
	return instances.size();
    }

    public Integer getMajorityClass() {
	Entry<Integer, Integer> maxEntry = null;
	for (Entry<Integer, Integer> entry : classCounter.entrySet()) {
	    if (maxEntry == null || entry.getValue().compareTo(maxEntry.getValue()) > 0) {
		maxEntry = entry;
	    }
	}

	return maxEntry.getKey();
    }

    public boolean haveAllSameClass() {
	return classCounter.size() == 1;
    }

    public Double getEntropy() {
	Double entropy = 0.0;
	for (Integer count : classCounter.values()) {
	    Double freqClass = ((double) count / getNumberOfInstances());
	    entropy += -(freqClass) * (Math.log(freqClass) / Math.log(2));
	}
	return entropy;
    }

    public SplitCondition getBestSplitCondition() {
	SplitCondition bestSplitCondition = null;
	Double bestInfoGain = 0.0;
	Double thisEntropy = this.getEntropy();

	// For each feature...
	for (int featureIdx = 0; featureIdx < featuresNumber; featureIdx++) {
	    Set<Double> set = new HashSet<>();
	    for (Instance instance : instances) {
		set.add(instance.getValue(featureIdx));
	    }
	    Double[] arr = new Double[set.size()];
	    arr = set.toArray(arr);
	    Arrays.sort(arr);

	    // For each split point...
	    for (int arrIdx = 0; arrIdx < arr.length - 1; arrIdx++) {
		Double splitPoint = (arr[arrIdx] + arr[arrIdx + 1]) / 2;
		SplitCondition splitCondition = new SplitCondition(featureIdx, splitPoint);
		InstancesSet[] instancesSets = split(splitCondition);

		Double weightedEntropy = (instancesSets[0].getEntropy()
			* ((double) instancesSets[0].getNumberOfInstances() / this.getNumberOfInstances()))
			+ (instancesSets[1].getEntropy()
				* ((double) instancesSets[1].getNumberOfInstances() / this.getNumberOfInstances()));

		Double infoGain = thisEntropy - weightedEntropy;

		if (infoGain > bestInfoGain) {
		    bestInfoGain = infoGain;
		    bestSplitCondition = splitCondition;
		}
	    }
	}

	return bestSplitCondition;
    }

    public InstancesSet[] split(SplitCondition splitCondition) {
	InstancesSet[] instancesSets = new InstancesSet[2];
	InstancesSet instancesLeft = new InstancesSet();
	InstancesSet instancesRight = new InstancesSet();
	instancesSets[0] = instancesLeft;
	instancesSets[1] = instancesRight;

	for (Instance instance : instances) {
	    if (splitCondition.belongsToLeftNode(instance)) {
		instancesLeft.addInstance(instance);
	    } else {
		instancesRight.addInstance(instance);
	    }
	}

	return instancesSets;
    }

    public Double predictionsAccuracy() {
	Integer correct = 0;
	for (Instance instance : instances) {
	    if (instance.isCorrectPredicted()) {
		correct++;
	    }
	}
	return (double) correct / getNumberOfInstances();
    }

    @Override
    public String toString() {
	return "InstancesSet [instances=" + instances + ", classCounter=" + classCounter + ", featuresNumber="
		+ featuresNumber + "]";
    }

    public List<Instance> getInstances() {
	return instances;
    }

    public Instance getInstance(int index) {
	return instances.get(index);
    }

    public void clear() {
	instances.clear();
    }
}
