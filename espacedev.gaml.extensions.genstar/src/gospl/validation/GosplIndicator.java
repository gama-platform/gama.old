package gospl.validation;

public enum GosplIndicator {

	TAE("Total Absolute Error"), 
	TACE("Total Absolute Cell Error"), 
	AAPD("Absolute Average Percentage Difference"),
	SRMSE("Standardized Root Mean Square Error"),
	RSSZstar("Modified Sum of Square Z-score");
	
	private String fullName;
	
	private GosplIndicator(String fullName){
		this.fullName = fullName;
	} 
	
	public String getFullName(){
		return fullName;
	}
	
}
