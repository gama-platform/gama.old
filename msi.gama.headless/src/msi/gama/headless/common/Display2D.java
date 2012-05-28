package msi.gama.headless.common;

import java.io.Serializable;

public class Display2D implements Serializable{

	private String path;
	//private String key;

	public static Display2D valueOf(String path)
	{
		return new Display2D(path);
	}
	
	
	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String toString()
	{
		return path;
	}
	
	public Display2D(String path)
	{
		this.path=path;
	}
	
}
