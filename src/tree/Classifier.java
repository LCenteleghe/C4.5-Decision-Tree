package tree;

public interface Classifier {

    public String getSizeDescription();

    public void classify(InstancesSet testInstances);
    
    public void classify(Instance instances);

}
