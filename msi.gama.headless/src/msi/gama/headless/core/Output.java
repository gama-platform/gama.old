package msi.gama.headless.core;

public class Output {
	public String name;
	public int frameRate;
	public String	id;

	
	public Output(String name, int frameRate, String id) {
		super();
		this.name = name;
		this.frameRate = frameRate;
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getFrameRate() {
		return frameRate;
	}
	public void setFrameRate(int frameRate) {
		this.frameRate = frameRate;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
}
