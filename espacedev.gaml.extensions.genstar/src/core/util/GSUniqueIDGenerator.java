package core.util;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Key id value generator encapsulating {@link AtomicInteger}
 * 
 * @author kevinchapuis
 *
 */
public class GSUniqueIDGenerator {

	private static GSUniqueIDGenerator idgen;
	
	private AtomicInteger ai;
	
	private GSUniqueIDGenerator(){
		this.ai = new AtomicInteger();
	}
	
	/**
	 * Access to singleton instance
	 * @return
	 */
	public static GSUniqueIDGenerator getInstance(){
		if(idgen == null)
			idgen = new GSUniqueIDGenerator();
		return idgen;
	}
	
	/**
	 * Get next unique ID
	 * @return
	 */
	public int getNextID(){
		return ai.getAndIncrement();
	}
	
	/**
	 * Reset the id generator to default initial value
	 */
	public void reset(){
		this.ai = new AtomicInteger();
	}
	
	/**
	 * Reset the id generator to {@code initValue}
	 * @param initValue
	 */
	public void reset(int initValue){
		this.ai = new AtomicInteger(initValue);
	}
	
}
