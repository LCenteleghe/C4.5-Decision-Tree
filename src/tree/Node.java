package tree;

public class Node {
    private Node left;
    private Node right;
    private SplitCondition splitCondition;
    private String featureName;
    private Integer classValue;
    
    public Node(){}
    
    public Node(Integer classValue){
	this.classValue = classValue;
    }
    
    public boolean isLeaf(){
	return left == null && right == null;
    }

    public SplitCondition getSplitCondition() {
        return splitCondition;
    }

    public Node getLeft() {
        return left;
    }

    public Node getRight() {
        return right;
    }

    public void setSplitCondition(SplitCondition splitCondition) {
        this.splitCondition = splitCondition;
    }

    public void setClassValue(Integer classValue) {
        this.classValue = classValue;
    }
    
    public void createLeaves(){
	this.left = new Node();
	this.right = new Node();
    }

    public String getFeatureName() {
        return featureName;
    }

    public void setFeatureName(String featureName) {
        this.featureName = featureName;
    }

    public Integer getClassValue() {
        return classValue;
    }
    
    /* The attributes and methods bellow are used just for pruning, 
     * since when pruning we have to keep track of which instances arrive
     * to each Node  */
    public InstancesSet trainingInstances = new InstancesSet();
    public InstancesSet pruningInstances = new InstancesSet();
    private boolean isLogicallyPruned = false;

    public void clearPrunningStructure() {
	trainingInstances.clear();
	pruningInstances.clear();
    }
    
    
    public Double simulatePruningAcc(Integer simuClass){
	if(!this.isLeaf()){
	    throw new IllegalStateException("This method can be used just for leaf nodes.");
	}
	Integer correct = 0;
	for (Instance instance : pruningInstances.getInstances()) {
	    if(instance.getCategoryValue().equals(simuClass)){
		correct++;
	    }
	}
	return (double)correct/pruningInstances.getNumberOfInstances();
    }
    
    public Integer getMajorityClassTrainingSet(){
	return trainingInstances.getMajorityClass();
    }

    public void addTrainingInstance(Instance instance) {
	trainingInstances.addInstance(instance);
    }

    public void addPruningInstance(Instance instance) {
	pruningInstances.addInstance(instance);
    }

    public boolean isLogicallyPruned() {
        return isLogicallyPruned;
    }

    public void prune(Integer parentsMajorityClass) {
	this.classValue = parentsMajorityClass;
        this.isLogicallyPruned = true;
    }

    public void pruneLeaves() {
	left = right = null;
    }
}
