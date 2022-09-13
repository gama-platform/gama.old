package gospl.generator.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import core.configuration.dictionary.AttributeDictionary;
import core.metamodel.attribute.Attribute;
import core.metamodel.attribute.AttributeFactory;
import core.metamodel.value.IValue;
import core.util.GSPerformanceUtil;
import core.util.GSPerformanceUtil.Level;
import core.util.data.GSEnumDataType;
import core.util.excpetion.GSIllegalRangedData;
import core.util.random.GenstarRandom;
import core.util.random.GenstarRandomUtils;
import core.util.random.roulette.ARouletteWheelSelection;
import core.util.random.roulette.RouletteWheelSelectionFactory;
import gospl.GosplEntity;
import gospl.GosplPopulation;
import gospl.generator.ISyntheticGosplPopGenerator;

/**
 *
 * Fully random generator:
 * <p>
 * <ul>
 * <li>1st constructor: lead to a fully random population (attribute & value are generated randomly from a set of chars)
 * </ul>
 * 2nd constructor: given a set of attributes generate a population
 * <p>
 * NOTE: intended to be used as a population supplier on any test
 *
 * @author kevinchapuis
 *
 */
public class GSUtilGenerator implements ISyntheticGosplPopGenerator {

	private int maxAtt;
	private int maxVal;

	char[] chars = "abcdefghijklmnopqrstuvwxyz".toCharArray();

	private AttributeDictionary dictionary;
	private Map<Attribute<? extends IValue>, ARouletteWheelSelection<Double, IValue>> attributesProba;

	Random random = GenstarRandom.getInstance();

	/**
	 * Set the maximum number of attribute and maximum value per attribute
	 *
	 * @param maxAtt
	 * @param maxVal
	 */
	public GSUtilGenerator(final int maxAtt, final int maxVal) {
		this.maxAtt = maxAtt;
		this.maxVal = maxVal;
	}

	/**
	 * Provide the set of attribute to draw entity from
	 *
	 * @param attributes
	 */
	public GSUtilGenerator(final AttributeDictionary attributes) {
		this.dictionary = attributes;
	}

	@Override
	public GosplPopulation generate(final int numberOfIndividual) {

		GSPerformanceUtil gspu = new GSPerformanceUtil("Generate util population", Level.TRACE);

		// Basic population to feed
		GosplPopulation gosplPop = new GosplPopulation();

		if (dictionary == null) { dictionary = new AttributeDictionary(); }

		// Attribute Factory
		if (dictionary.getAttributes() == null || dictionary.getAttributes().isEmpty()) {
			gspu.getStempPerformance("Generating dictionary from scratch");
			int nb = random.nextInt(maxAtt);
			nb = nb <= 1 ? 2 : nb;
			@SuppressWarnings ("unchecked") Attribute<? extends IValue>[] arr = new Attribute[nb];
			this.dictionary.addAttributes(IntStream.range(0, nb)
					.mapToObj(i -> random.nextDouble() > 0.5 ? createStringAtt() : createIntegerAtt())
					.collect(Collectors.toList()).toArray(arr));
		}

		// Attribute probability table
		this.setupAttributeProbabilityTable();

		gspu.sysoStempPerformance(0.0, "Start generating " + numberOfIndividual + " entity", this);

		for (int i = 0; i < numberOfIndividual; i++) {
			GosplEntity entity = new GosplEntity(
					attributesProba.keySet().stream().collect(Collectors.toMap(Function.identity(), this::randomVal)));
			for (Attribute<? extends IValue> mapAtt : dictionary.getAttributes().stream()
					.filter(a -> !attributesProba.containsKey(a)).collect(Collectors.toSet())) {
				IValue refValue = entity.getValueForAttribute(mapAtt.getReferentAttribute());
				if (refValue != null) {
					Collection<? extends IValue> mapValues = mapAtt.findMappedAttributeValues(refValue);
					entity.setAttributeValue(mapAtt, randomUniformVal(mapValues));
				}
			}
			gosplPop.add(entity);
			if (i % (numberOfIndividual / 10.0) == 0) {
				gspu.sysoStempPerformance(i / (numberOfIndividual * 1.0), this);
			}
		}
		gspu.sysoStempPerformance(1.0, this);

		return gosplPop;
	}

	// ------------------------------------------------------ //
	// ---------- attribute & value random creator ---------- //
	// ------------------------------------------------------ //

	private Attribute<? extends IValue> createIntegerAtt() {
		int valNb = random.nextInt(maxVal);
		valNb = valNb <= 1 ? 2 : valNb;
		Attribute<? extends IValue> asa = null;
		try {
			asa = AttributeFactory.getFactory().createAttribute(generateName(random.nextInt(6) + 1),
					GSEnumDataType.Integer,
					IntStream.range(0, valNb).mapToObj(String::valueOf).collect(Collectors.toList()));
		} catch (GSIllegalRangedData e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return asa;
	}

	private Attribute<? extends IValue> createStringAtt() {
		int valNb = random.nextInt(maxVal);
		valNb = valNb <= 1 ? 2 : valNb;
		Attribute<? extends IValue> asa = null;
		try {
			asa = AttributeFactory.getFactory().createAttribute(generateName(random.nextInt(6) + 1),
					GSEnumDataType.Nominal, IntStream.range(0, valNb).mapToObj(j -> generateName(random.nextInt(j + 1)))
							.collect(Collectors.toList()));
		} catch (GSIllegalRangedData e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return asa;
	}

	private String generateName(final int size) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < size; i++) {
			char c = chars[random.nextInt(chars.length)];
			sb.append(c);
		}
		return sb.toString();
	}

	/*
	 * TODO: make mapped attribute coherent
	 */
	private void setupAttributeProbabilityTable() {
		attributesProba = new HashMap<>();

		// Only set probability for referent attribute
		Set<Attribute<? extends IValue>> referentAttribute = dictionary.getAttributes().stream()
				.filter(a -> a.getReferentAttribute().equals(a)).collect(Collectors.toSet());

		for (Attribute<? extends IValue> att : referentAttribute) {
			List<IValue> keys = new ArrayList<>(att.getValueSpace().getValues());
			List<Double> probaList = keys.stream().map(v -> random.nextDouble()).collect(Collectors.toList());
			double sop = probaList.stream().mapToDouble(Double::doubleValue).sum();
			attributesProba.put(att, RouletteWheelSelectionFactory
					.getRouletteWheel(probaList.stream().map(d -> d / sop).collect(Collectors.toList()), keys));
		}

	}

	// ---------------------- utilities ---------------------- //

	private IValue randomVal(final Attribute<? extends IValue> attribute) {
		return attributesProba.get(attribute).drawObject();
	}

	private IValue randomUniformVal(final Collection<? extends IValue> values) {
		return GenstarRandomUtils.oneOf(values);
	}

}
