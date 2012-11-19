package msi.gama.hpc.gui;

public class Result {
	private String name;
	private Double value;

	
	
	public Result(String name, Double value) {
		super();
		this.name = name;
		this.value = value;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	
	public Double getValue() {
		return this.value;
	}
	
	
	public void setValue(Double value) {
		this.value = value;
	}


}
