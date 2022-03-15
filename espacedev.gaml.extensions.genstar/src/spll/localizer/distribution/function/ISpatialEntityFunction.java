package spll.localizer.distribution.function;

import java.util.function.Function;

import core.metamodel.entity.AGeoEntity;
import core.metamodel.value.IValue;

/**
 * 
 * TODO javadoc
 * 
 * @author kevinchapuis
 *
 * @param <N>
 */
public interface ISpatialEntityFunction<N extends Number> extends Function<AGeoEntity<? extends IValue>, N> {
	
	/**
	 * TODO  javadoc
	 *  
	 * @param entity
	 */
	public void updateFunctionState(AGeoEntity<? extends IValue> entity);
	
}
