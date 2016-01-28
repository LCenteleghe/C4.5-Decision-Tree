package tree;

public class SampleGenerator {
    public static InstancesSet generateSampleWithReplacement(InstancesSet instanceSet){
	InstancesSet sampleInstancesSet = new InstancesSet();
	
	for(int i = 0; i < instanceSet.getNumberOfInstances(); i++){
	    int randomUniformInt = (int)(Math.random()*instanceSet.getNumberOfInstances());
	    sampleInstancesSet.addInstance(instanceSet.getInstance(randomUniformInt));
	}
	
	return sampleInstancesSet;
    }
}
