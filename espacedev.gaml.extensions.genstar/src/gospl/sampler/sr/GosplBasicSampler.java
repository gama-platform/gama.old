package gospl.sampler.sr;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import core.metamodel.attribute.Attribute;
import core.metamodel.value.IValue;
import core.util.random.roulette.ARouletteWheelSelection;
import core.util.random.roulette.RouletteWheelSelectionFactory;
import gospl.distribution.matrix.AFullNDimensionalMatrix;
import gospl.distribution.matrix.control.AControl;
import gospl.distribution.matrix.coordinate.ACoordinate;
import gospl.sampler.IDistributionSampler;

/**
 * Basic Monte Carlo sampler based on {@link ARouletteWheelSelection} implementation using 
 * a gospl distribution, i.e. {@link AFullNDimensionalMatrix} and drawing {@link ACoordinate} from it
 * 
 * @author kevinchapuis
 *
 */
public class GosplBasicSampler implements IDistributionSampler {

	ARouletteWheelSelection<Double, ACoordinate<Attribute<? extends IValue>, IValue>> sampler;

	private final double EPSILON = Math.pow(10, -6);

	// -------------------- setup methods -------------------- //


	@Override
	public void setDistribution(AFullNDimensionalMatrix<Double> distribution) {
		if(distribution == null)
			throw new NullPointerException();
		if(distribution.getMatrix().isEmpty())
			throw new IllegalArgumentException("Cannot setup a sampler with an empty distribution matrix "+distribution);
		
		Map<ACoordinate<Attribute<? extends IValue>, IValue>, AControl<Double>> mat = distribution.getMatrix();
		
		List<ACoordinate<Attribute<? extends IValue>, IValue>> keys = new ArrayList<>(mat.keySet());
		List<Double> probabilities = new ArrayList<>(distribution.size());
		
		double sumOfProbabilities = 0d;
		for(ACoordinate<Attribute<? extends IValue>, IValue> key : keys){
			double proba = mat.get(key).getValue();
			sumOfProbabilities += proba;
			probabilities.add(proba);
		}
		
		if(Math.abs(sumOfProbabilities - 1d) > EPSILON){
			// TODO: move to a BigDecimal distribution requirement
			throw new IllegalArgumentException("Sum of probabilities for this sampler is not equal to 1 (SOP = "+sumOfProbabilities+")");
		}
		
		sampler = RouletteWheelSelectionFactory.getRouletteWheel(probabilities, keys);
	}
	

	// -------------------- main contract -------------------- //
	
	@Override
	public ACoordinate<Attribute<? extends IValue>, IValue> draw() {
		return sampler.drawObject();
	}


	/**
	 * {@inheritDoc}
	 * <p>
	 * WARNING: make use of {@link Stream#parallel()}
	 */
	@Override
	public final Collection<ACoordinate<Attribute<? extends IValue>, IValue>> draw(int numberOfDraw) {
		return IntStream.range(0, numberOfDraw).mapToObj(i -> draw()).collect(Collectors.toList());
	}
		
	// -------------------- utility -------------------- //

	@Override
	public String toCsv(String csvSeparator) {
		List<Attribute<? extends IValue>> attributs = new ArrayList<>(sampler.getKeys()
				.parallelStream().flatMap(coord -> coord.getDimensions().stream())
				.collect(Collectors.toSet()));
		String s = "Basic sampler: "+sampler.getKeys().size()+" discret probabilities\n";
		s += String.join(csvSeparator, attributs.stream().map(att -> att.getAttributeName()).collect(Collectors.toList()))+"; Probability\n";
		for(ACoordinate<Attribute<? extends IValue>, IValue> coord : sampler.getKeys()){
			String line = "";
			for(Attribute<? extends IValue> att : attributs){
				if(coord.getDimensions().contains(att)){
					if(line.isEmpty())
						line += coord.getMap().get(att).getStringValue();
					else
						line += csvSeparator + coord.getMap().get(att).getStringValue();
				} else {
					if(line.isEmpty())
						line += " ";
					else
						line += csvSeparator + " ";
				}
			}
			s += line + csvSeparator + sampler.getValue(coord) +"\n";
		}
		return s;
	}

}
