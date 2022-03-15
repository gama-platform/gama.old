package spll.localizer.distribution.function;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

import core.metamodel.entity.AGeoEntity;
import core.metamodel.value.IValue;
import spll.SpllEntity;

/**
 * Function that computes probability based on the gravity model
 * 
 * @author kevinchapuis
 *
 */
public class GravityFunction implements ISpatialComplexFunction<Double> {

	private Map<AGeoEntity<? extends IValue>, Double> mass; 
	private double buffer = -1;
	private double frictionCoeff = 1.0;
	
	private BiFunction<Double, Double, Double> function;

	private GravityFunction() {
		this.function = new BiFunction<Double, Double, Double>() {
			@Override public Double apply(Double mass, Double distance) { 
				return mass / Math.pow(distance, frictionCoeff); }
		};
	}
	
	/**
	 * Mass of spatial entity is defined as the sum of distance between the spatial entity and all entities
	 * 
	 * @param candidates
	 * @param frictionCoeff
	 * @param entities
	 */
	public GravityFunction( Collection<? extends AGeoEntity<? extends IValue>> candidates, double frictionCoeff, SpllEntity... entities) {
		this();
		this.frictionCoeff = frictionCoeff;
		this.mass = candidates.stream().collect(Collectors.toMap(Function.identity(), se -> Arrays.asList(entities).stream()
				.mapToDouble(e -> se.getGeometry().distance(e.getLocation())).sum()));
	}

	/**
	 * Mass of spatial entity is defined as the number of entity within a given buffer around the spatial entity
	 * 
	 * @param candidates
	 * @param frictionCoeff
	 * @param buffer
	 * @param entities
	 */
	public GravityFunction(Collection<? extends AGeoEntity<? extends IValue>> candidates, 
			double frictionCoeff, double buffer, SpllEntity... entities) {
		this();
		this.mass = candidates.stream().collect(Collectors.toMap(Function.identity(), spacEntity -> (double) Arrays.asList(entities).stream()
				.filter(e -> spacEntity.getGeometry().buffer(buffer).contains(e.getLocation())).count()));
		this.buffer = buffer;
		this.frictionCoeff = frictionCoeff;
	}
	
	// ------------------------------------------ //
	
	/**
	 * Set the function that compute probability from mass of space entity and distance
	 * from population entity
	 * @param function
	 */
	public void setMassDistanceFunction(BiFunction<Double, Double, Double> function) {
		this.function = function;
	}
	
	/**
	 * Add / Replace the recorded mass of spatial entity
	 * @param mass
	 */
	public void setSpatialEntityMass(Map<AGeoEntity<? extends IValue>, Double> mass) {
		this.mass.putAll(mass);
	}
	
	// ------------------------------------------ //

	@Override
	public Double apply(AGeoEntity<? extends IValue> spatialEntity, SpllEntity entity) {
		return function.apply(mass.get(spatialEntity), spatialEntity.getGeometry().distance(entity.getLocation()));
	}

	@Override
	public void updateFunctionState(Collection<SpllEntity> entities,
			Collection<AGeoEntity<? extends IValue>> candidates) {
		if(buffer <= 0)
			for(AGeoEntity<? extends IValue> se : candidates)
				mass.put(se, entities.stream().mapToDouble(e -> se.getGeometry().distance(e.getLocation())).sum());
		else
			for(AGeoEntity<? extends IValue> se : candidates)
				mass.put(se, (double) entities.stream().filter(e -> se.getGeometry().contains(e.getLocation())).count());
	}

	@Override
	public void clear() {
		mass.clear();
	}

}
