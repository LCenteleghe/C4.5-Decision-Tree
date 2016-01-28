package tree;


import java.util.List;

public class Instance {
	private List<Double> featuresValues;
	private Integer categoryValue;
	private Integer predictedCategoryValue;
	
	public Instance(List<Double> featuresValues, Integer categoryValue) {
		super();
		this.featuresValues = featuresValues;
		this.categoryValue = categoryValue;
	}
	
	public Double getValue(Integer featureIdx){
	    return featuresValues.get(featureIdx);
	}
	
	public List<Double> getFeaturesValues() {
		return featuresValues;
	}
	
	public Integer getCategoryValue() {
		return categoryValue;
	}

	@Override
	public String toString() {
	    return "Instance [featuresValues=" + featuresValues + ", categoryValue=" + categoryValue + "]";
	}

	public Integer getPredictedCategoryValue() {
	    return predictedCategoryValue;
	}

	public void setPredictedCategoryValue(Integer predictedCategoryValue) {
	    this.predictedCategoryValue = predictedCategoryValue;
	}
	
	public boolean isCorrectPredicted(){
	    return categoryValue.equals(predictedCategoryValue);
	}
}
