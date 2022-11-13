package gospl.sampler.multilayer.sr;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import core.metamodel.attribute.Attribute;
import core.metamodel.attribute.EmergentAttribute;
import core.metamodel.value.IValue;
import gospl.GosplEntity;
import gospl.distribution.exception.IllegalDistributionCreation;
import gospl.distribution.matrix.AFullNDimensionalMatrix;
import gospl.distribution.matrix.coordinate.GosplCoordinate;
import gospl.distribution.matrix.coordinate.GosplMultiLayerCoordinate;
import gospl.sampler.sr.GosplCompletionDirectSampling;

/**
 * A sampler based on SR techniques that will draw a two layer entity, e.g. individuals in household. 
 * 
 * TODO : development of sample-free solution based on "symbolic link" between different statistical level
 * 
 * @author kevinchapuis
 *
 */
public class GosplBiLayerReconstructionSampler implements ISRMultiLayerSampler {
	
	private GosplCompletionDirectSampling groupSampler = new GosplCompletionDirectSampling();
	private Set<Attribute<? extends IValue>> groupD;
	private Map<EmergentAttribute<? extends IValue,?,?>,Attribute<? extends IValue>> anchorD;
	private GosplCompletionDirectSampling entitySampler = new GosplCompletionDirectSampling();
	
	@SuppressWarnings("unchecked")
	@Override
	public GosplMultiLayerCoordinate draw() {
		System.out.println("[CRADO SYSO - GosplBiLayerSampler]");
		
		GosplEntity indivA = new GosplEntity(entitySampler.draw().getMap());
		System.out.println("ANCHOR INDIV: "+indivA);
		
		Map<Attribute<? extends IValue>, IValue> anchorCoordinates = new HashMap<>();
		for (@SuppressWarnings("rawtypes") EmergentAttribute ea : anchorD.keySet()) { 
			anchorCoordinates.put(anchorD.get(ea), ea.getEmergentValue(indivA));
		}
		System.out.println("ANCHOR VALUES: "+anchorCoordinates.values()
			.stream().map(IValue::getStringValue).collect(Collectors.joining(";")));

		GosplMultiLayerCoordinate coord = new GosplMultiLayerCoordinate(groupSampler
				.complete(new GosplCoordinate(anchorCoordinates)));
		System.out.println("FIRST GROUP ENTITY IS: "+coord);
		
		Map<Attribute<? extends IValue>, IValue> deciders = coord.getMap().keySet().stream()
				.filter(a -> !a.getReferentAttribute().equals(a))
				.collect(Collectors.toMap(Function.identity(), k -> coord.getMap().get(k)));
		coord.addChild(new GosplMultiLayerCoordinate(entitySampler.complete(new GosplCoordinate(deciders))));
		return coord;
	}

	@Override
	public Collection<GosplMultiLayerCoordinate> draw(int numberOfDraw) {
		return IntStream.range(0,numberOfDraw).mapToObj(i -> this.draw()).toList();
	}

	/**
	 * Describe the distribution of attribute for group layer
	 * \p
	 * Mandatory in order to draw entities
	 * 
	 * TODO : there is a little dirty tricks here, about EmergentAttribute which are cast rather than properly retrieved
	 * 
	 * @param distribution
	 * @throws IllegalDistributionCreation
	 */
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void setGroupLevelDistribution(AFullNDimensionalMatrix<Double> distribution) {
		this.groupD = distribution.getDimensions();
		this.anchorD = groupD.stream().filter(Attribute::isEmergent)
				.collect(Collectors.toMap(d -> ((EmergentAttribute) d),Function.identity()));
		this.groupSampler.setDistribution(distribution);;
	}

	/**
	 * Describe the distribution of attribute for entity layer
	 * \p
	 * Mandatory in order to draw entities
	 * @param distribution
	 */
	public void setEntityLevelDistribution(AFullNDimensionalMatrix<Double> distribution) {
		distribution.getDimensions();
		this.entitySampler.setDistribution(distribution);
	}
	
	@Override
	public String toCsv(String csvSeparator) {
		
		return null;
	}
	
}
