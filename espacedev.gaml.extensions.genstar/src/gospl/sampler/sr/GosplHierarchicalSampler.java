/*******************************************************************************************************
 *
 * GosplHierarchicalSampler.java, in espacedev.gaml.extensions.genstar, is part of the source code of the GAMA modeling
 * and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package gospl.sampler.sr;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import core.metamodel.attribute.Attribute;
import core.metamodel.value.IValue;
import core.util.random.GenstarRandomUtils;
import core.util.random.roulette.RouletteWheelSelectionFactory;
import gospl.distribution.matrix.ASegmentedNDimensionalMatrix;
import gospl.distribution.matrix.CachedSegmentedNDimensionalMatrix;
import gospl.distribution.matrix.INDimensionalMatrix;
import gospl.distribution.matrix.ISegmentedNDimensionalMatrix;
import gospl.distribution.matrix.coordinate.ACoordinate;
import gospl.distribution.matrix.coordinate.GosplCoordinate;
import gospl.sampler.IHierarchicalSampler;
import ummisco.gama.dev.utils.DEBUG;

/**
 * A Hierarchical sampler explores the variables in a given order to generate the individuals.
 *
 * @author Samuel Thiriot
 *
 */
public class GosplHierarchicalSampler implements IHierarchicalSampler {

	/** The exploration order. */
	private Collection<List<Attribute<? extends IValue>>> explorationOrder = null;

	/** The segmented matrix. */
	private ISegmentedNDimensionalMatrix<Double> segmentedMatrix;

	/**
	 * Instantiates a new gospl hierarchical sampler.
	 */
	public GosplHierarchicalSampler() {
		// TODO Auto-generated constructor stub
	}

	// -------------------- setup methods -------------------- //

	@Override
	public void setDistribution(final Collection<List<Attribute<? extends IValue>>> explorationOrder,
			final ASegmentedNDimensionalMatrix<Double> segmentedMatrix) {
		this.explorationOrder = explorationOrder;

		// create a cached version of this segmented matrix, to save time in our intensive computation of probabilities
		this.segmentedMatrix = new CachedSegmentedNDimensionalMatrix<>(segmentedMatrix);

	}

	// -------------------- main contract -------------------- //

	@Override
	public ACoordinate<Attribute<? extends IValue>, IValue> draw() {

		Map<Attribute<? extends IValue>, IValue> att2value = new HashMap<>();

		DEBUG.OUT("starting hierarchical sampling...");
		for (List<Attribute<? extends IValue>> subgraph : explorationOrder) {
			DEBUG.OUT("starting hierarchical sampling for the first subgraph :" + subgraph);
			for (Attribute<? extends IValue> att : subgraph) {

				// maybe we processed it already ? (because of control attributes / mapped aspects)
				if (att2value.containsKey(att)) { continue; }

				DEBUG.OUT("\tsampling att {}" + att);

				if (att2value.containsKey(att.getReferentAttribute())) {
					// this attribute as for a control attribute an attribute which was already sampled.
					// we should probably not sample it, but rather use this reference with the user rules
					// so we can translate it.
					DEBUG.OUT("\t\t{} was already defined to {}; let's reuse the mapping..."
							+ att.getReferentAttribute().getAttributeName() + ","
							+ att2value.get(att.getReferentAttribute()));

					IValue referentValue = att2value.get(att.getReferentAttribute());
					Collection<? extends IValue> mappedValues = att.findMappedAttributeValues(referentValue);

					DEBUG.LOG("\t\t{} maps to {}" + referentValue + "," + mappedValues);
					if (mappedValues.size() > 1) {
						DEBUG.OUT("\t\thypothesis of uniformity for {} => {}" + referentValue + "," + mappedValues);
					}
					IValue theOneMapped = GenstarRandomUtils.oneOf(mappedValues);
					att2value.put(att, theOneMapped);
					DEBUG.OUT("\t\tpicked {} = {} (through referent attribute)" + att + "," + theOneMapped);

				} else {

					DEBUG.OUT("\tshould pick one of the values {}" + att.getValueSpace());

					// what we want is the distribution of probabilities for each of these possible values of the
					// current attribute...
					List<IValue> keys = new ArrayList<>(att.getValueSpace().getValues());
					// knowing the previous ones !
					keys.addAll(att2value.values());

					// for each of the aspects of this attribute we're working on...
					List<Double> distribution = new ArrayList<>(att.getValueSpace().getValues().size() + 1);
					List<IValue> a = new ArrayList<>();
					// ... we want to add the values already defined that can condition the attribute of interest
					for (INDimensionalMatrix<Attribute<? extends IValue>, IValue, Double> m : this.segmentedMatrix
							.getMatricesInvolving(this.segmentedMatrix.getDimensions().stream()
									.filter(dim -> dim.equals(att.getReferentAttribute())).findAny().orElse(null))) {
						a.addAll(m.getDimensions().stream().filter(dim -> att2value.containsKey(dim))
								.map(dim -> att2value.get(dim)).collect(Collectors.toSet()));
					}
					double total = 0.;
					for (IValue val : att.getValueSpace().getValues()) {
						// construct the list of the attributes on which we want conditional probabilities
						Set<IValue> aa = new HashSet<>(a);
						// att2value.values()

						// this.segmentedMatrix.getMatricesInvolving(val).stream().filter(matrix ->
						// att2value.containsKey(key));
						// ... and for this specific val
						aa.add(val);
						// TODO sometimes I've here a NUllpointerexception when one of the values if empty (typically
						// Age3)
						try {
							DEBUG.LOG("\t\tfor aspects: {}, getVal returns {}" + aa + ","
									+ this.segmentedMatrix.getVal(aa));
							Double v = this.segmentedMatrix.getVal(aa).getValue();
							total += v;
							distribution.add(v);
						} catch (NullPointerException e) {
							DEBUG.OUT(
									"\t\tpotential value {} will be excluded from the distribution as it has no probability"
											+ val);
						}
					}
					IValue theOne = null;
					if (distribution.isEmpty() || total == 0.) {
						// okay, the mix of variables probably includes some "empty"; let's assume the value is then
						// empty as well...
						// TODO what to do here ?
						theOne = att.getEmptyValue();
						DEBUG.OUT("\t\tempty distribution; let's assume default value");
					} else {
						theOne = RouletteWheelSelectionFactory.getRouletteWheel(distribution, keys).drawObject();
					}
					DEBUG.OUT("\t\tpicked {} = {}" + att + "," + theOne);
					att2value.put(att, theOne);

					// well, we defined a value... maybe its defining the value of another thing ?
					if (att.getReferentAttribute() != att) {
						// yes, it has a reference attribute !
						Collection<? extends IValue> mappedValues = att.findMappedAttributeValues(theOne);
						DEBUG.OUT("\twe have a reference attribute {}, which maps to {}" + att.getReferentAttribute()
								+ "," + mappedValues);
						if (mappedValues.size() > 1) {
							DEBUG.OUT("\t\thypothesis of uniformity for {} => {}" + theOne + "," + mappedValues);
						}
						// let's randomly draw something there
						// another random
						IValue theOneMapped = GenstarRandomUtils.oneOf(mappedValues);
						// Little trick to get referent attribute without <? extends IValue> wildcard
						att2value.put(
								this.segmentedMatrix.getDimensions().stream()
										.filter(dim -> dim.equals(att.getReferentAttribute())).findAny().get(),
								theOneMapped);
						DEBUG.OUT("\t\tpicked {} = {} (through referent attribute)" + att.getReferentAttribute() + ","
								+ theOneMapped);

					}
				}
			}
		}

		// to test the efficiency of cache:
		/*
		 * System.err.println(
		 * "hits / missed     "+((CachedSegmentedNDimensionalMatrix)this.segmentedMatrix).getHits()+"/"+
		 * ((CachedSegmentedNDimensionalMatrix)this.segmentedMatrix).getMissed() );
		 */

		return new GosplCoordinate(att2value);

	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * WARNING: make use of {@link Stream#parallel()}
	 */
	@Override
	public final Collection<ACoordinate<Attribute<? extends IValue>, IValue>> draw(final int numberOfDraw) {
		return IntStream.range(0, numberOfDraw).parallel().mapToObj(i -> draw()).toList();
	}

	// -------------------- utility -------------------- //

	@Override
	public String toCsv(final String csvSeparator) {

		return null;
	}

}
