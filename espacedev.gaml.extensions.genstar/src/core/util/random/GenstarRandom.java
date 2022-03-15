package core.util.random;

import java.util.Random;

public class GenstarRandom {
	
	private static Random randomEngine;
	
	public static Random getInstance(){
		if(randomEngine == null)
			randomEngine = new Random();
		return randomEngine;
	}
	
	public static void setInstance(Random random){
		randomEngine = random;
	}
	
}
