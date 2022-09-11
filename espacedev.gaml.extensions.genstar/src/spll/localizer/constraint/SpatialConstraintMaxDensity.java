package spll.localizer.constraint;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

import core.metamodel.entity.AGeoEntity;
import core.metamodel.value.IValue;

public class SpatialConstraintMaxDensity extends SpatialConstraintMaxNumber {

	protected Map<String, Double> nestInitDensity;

	// maxVal: global value for the max density of entities per nest
	public SpatialConstraintMaxDensity(final Collection<? extends AGeoEntity<? extends IValue>> nests,
			final Double maxVal) {
		super(nests, maxVal);

	}

	// keyAttMax: name of the attribute that contains the max density of entities in the nest file
	public SpatialConstraintMaxDensity(final Collection<? extends AGeoEntity<? extends IValue>> nests,
			final String keyAttMax) {
		super(nests, keyAttMax);
	}

	@Override
	public void relaxConstraintOp(final Collection<AGeoEntity<? extends IValue>> nests) {
		for (AGeoEntity<? extends IValue> n : nests) {
			nestCapacities.put(n.getGenstarName(), Math
					.round(nestCapacities.get(n.getGenstarName())
							- (int) Math.round(nestInitDensity.get(n.getGenstarName()) * n.getArea()))
					+ (int) Math.round((nestInitDensity.get(n.getGenstarName()) + increaseStep * (1 + nbIncrements))
							* n.getArea()));
		}
	}

	@Override
	protected Map<String, Integer> computeMaxPerNest(final Collection<? extends AGeoEntity<? extends IValue>> nests,
			final String keyAttMax) {
		nestInitDensity = nests.stream().collect(Collectors.toMap(AGeoEntity::getGenstarName,
				a -> a.getNumericValueForAttribute(keyAttMax).doubleValue()));

		return nests.stream().collect(Collectors.toMap(AGeoEntity::getGenstarName,
				a -> (int) Math.round(a.getNumericValueForAttribute(keyAttMax).doubleValue() * a.getArea())));
	}

	@Override
	protected Map<String, Integer> computeMaxPerNest(final Collection<? extends AGeoEntity<? extends IValue>> nests,
			final Double maxVal) {
		nestInitDensity = nests.stream().collect(Collectors.toMap(AGeoEntity::getGenstarName, a -> maxVal));
		return nests.stream()
				.collect(Collectors.toMap(AGeoEntity::getGenstarName, a -> (int) Math.round(maxVal * a.getArea())));
	}

}
