package spll.localizer.distribution.function;

import java.util.Collection;
import java.util.function.BiFunction;

import core.metamodel.entity.AGeoEntity;
import core.metamodel.value.IValue;
import spll.SpllEntity;

/**
 * TODO javadoc
 * 
 * @author kevinchapuis
 *
 * @param <N>
 */
public interface ISpatialComplexFunction<N extends Number> extends BiFunction<AGeoEntity<? extends IValue>, SpllEntity, N> {

	/**
	 * TODO: javadoc
	 * 
	 * @param entities
	 * @param candidates
	 */
	public void updateFunctionState(Collection<SpllEntity> entities, Collection<AGeoEntity<? extends IValue>> candidates);
	
	/**
	 * TODO
	 */
	public void clear();
	
}
