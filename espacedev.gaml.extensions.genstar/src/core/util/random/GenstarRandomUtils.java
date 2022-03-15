package core.util.random;

import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.Set;

import core.util.data.GSDataParser;
import core.util.data.GSEnumDataType;

public class GenstarRandomUtils {
	
	private GenstarRandomUtils() {}
	
	/**
	 * Returns one element uniformly picked from the list
	 * @param l
	 * @return
	 */
	public static <T> T oneOf(List<T> l) {
		
		// check param
		if (l.isEmpty())
			throw new IllegalArgumentException("cannot take one value out of an empty list");
		
		// quick exit 
		if (l.size() == 1)
			return l.get(0);
		
		return l.get(GenstarRandom.getInstance().nextInt(l.size()));
	}
	
	/**
	 * returns one element for a set. 
	 * Warning: should in theory not be used on a Set which is not Sorted or ordered, 
	 * as this underlying random order breaks the reproductibility of the generation. 
	 * @param s
	 * @return
	 */
	public static <T> T oneOf(Set<T> s) {
		
		// check param
		if (s.isEmpty())
			throw new IllegalArgumentException("cannot take one value out of an empty set");
		
		// quick exit 
		if (s.size() == 1)
			return s.iterator().next();
		
		return s.stream().skip(GenstarRandom.getInstance().nextInt(s.size()))
				.findFirst().orElseThrow(AssertionError::new);
		
	}
	
	/**
	 * 
	 * @param <T>
	 * @param c
	 * @return
	 */
	public static <T> T oneOf(Collection<T> c){
		// check param
		if (c.isEmpty())
			throw new IllegalArgumentException("cannot take one value out of an empty list");
		
		// quick exit 
		if (c.size() == 1)
			return c.iterator().next();
			
		return c.stream().skip(GenstarRandom.getInstance().nextInt(c.size()))
				.findFirst().orElseThrow(AssertionError::new);
	}

	/**
	 * make a coin flip with given probability d to be true
	 * 
	 * @param d
	 * @return
	 */
	public static boolean flip(double d) {
		return GenstarRandom.getInstance().nextDouble() < d;
	}
	
	/**
	 * Get a random number between n1 and n2 either using double -- {@link Random#nextDouble()} -- or integer -- {@link Random#nextInt(int)} .
	 * 
	 * @param n1
	 * @param n2
	 * @return
	 */
	public static Number rnd(Number n1, Number n2) {
		Random rand = GenstarRandom.getInstance();
		GSDataParser gsdp = new GSDataParser();
		GSEnumDataType type = gsdp.getValueType(n1.toString());
		switch (type) {
		case Continue:
			return rand.nextDouble() * (n2.doubleValue() - n1.doubleValue()) + n1.doubleValue();
		case Integer:
			return rand.nextInt(n2.intValue() - n1.intValue()) + n1.intValue();
		default:
			throw new RuntimeException();
		}
	}

}
