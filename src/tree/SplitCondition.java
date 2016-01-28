package tree;

public class SplitCondition {
    private Integer featureIdx;
    private Double splitPoint;
    
    public SplitCondition(Integer featureIdx, Double splitPoint) {
	super();
	this.featureIdx = featureIdx;
	this.splitPoint = splitPoint;
    }

    public boolean belongsToLeftNode(Instance instance){
	return instance.getFeaturesValues().get(featureIdx) <= splitPoint;
    }
    
    public boolean belongsToRightNode(Instance instance){
	return !belongsToLeftNode(instance);
    }

    public Integer getFeatureIdx() {
        return featureIdx;
    }

    public Double getSplitPoint() {
        return splitPoint;
    }
    
    
}
