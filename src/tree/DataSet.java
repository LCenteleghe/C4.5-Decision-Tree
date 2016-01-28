package tree;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class DataSet {
    private List<String> features = new ArrayList<>();
    private List<Integer> categories = new ArrayList<>();
    private InstancesSet instances;

    public DataSet(File namesFile, File trainingFile) {
	try (BufferedReader brNamesFile = new BufferedReader(new FileReader(namesFile));) {
	    instances = new InstancesSet(trainingFile);
	    
	    for (String category : brNamesFile.readLine().split(",")) {
		categories.add(Integer.parseInt(category.trim()));
	    }

	    for (String feature : brNamesFile.readLine().split(",")) {
		features.add(feature.trim());
	    }

	} catch (Exception e) {
	   throw new InvalidDataSetException("Invalid data set(s)!");
	}
    }

    public List<String> getFeatures() {
        return features;
    }

    public List<Integer> getCategories() {
        return categories;
    }

    public InstancesSet getInstancesSet() {
        return instances;
    }
}
