package core.metamodel.entity.comparator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonProperty;

import core.metamodel.attribute.Attribute;
import core.metamodel.attribute.IAttribute;
import core.metamodel.entity.IEntity;
import core.metamodel.entity.matcher.AttributeVectorMatcher;
import core.metamodel.value.IValue;

/**
 * Compare agent using a third referent: the closest agent to referent will be sorted first !
 * 
 * @author kevinchapuis
 *
 */
public class HammingEntityComparator extends ImplicitEntityComparator {

	public static final String SELF = "HAMMING ENTITY COMPARATOR";
	public static final String HAMMING_VECTOR = "HAMMING VECTOR MATCHER";
	
	@JsonProperty(HammingEntityComparator.HAMMING_VECTOR)
	private AttributeVectorMatcher vector;

	/**
	 * Hamming distance will be computed using a referent entity (one value for each attribute) 
	 * 
	 * @param entity
	 */
	public HammingEntityComparator(IEntity<Attribute<? extends IValue>> entity) {
		super(new ArrayList<>(entity.getAttributes()));
		this.vector = new AttributeVectorMatcher(entity);
	}
	
	/**
	 * Hamming distance will be computed with several value being possible for each attribute
	 * 
	 * @param vector
	 */
	public HammingEntityComparator(Map<IAttribute<? extends IValue>, Set<IValue>> vector) {
		super(new ArrayList<>(vector.keySet()));
		this.vector = new AttributeVectorMatcher(vector);
	}
	
	/**
	 * Equivalent to {link {@link #HammingEntityComparator(Map)}} but without consistency
	 * 
	 * @param vector
	 */
	public HammingEntityComparator(IValue... vector) {
		super(new ArrayList<>(Arrays.asList(vector).stream()
				.map(v -> v.getValueSpace().getAttribute())
				.collect(Collectors.toSet())));
		this.vector = new AttributeVectorMatcher();
		this.vector.addMatchToVector(vector);
	}

	@JsonProperty(HammingEntityComparator.HAMMING_VECTOR)
	public AttributeVectorMatcher getVectorMatcher() {
		return this.vector;
	}
	
	@JsonProperty(HammingEntityComparator.HAMMING_VECTOR)
	public void setVectorMatcher(AttributeVectorMatcher vector) {
		this.vector = vector;
	}
	
	// ------------------------------------------------------------ //

	@Override
	public int compare(IEntity<? extends IAttribute<? extends IValue>> e1, IEntity<? extends IAttribute<? extends IValue>> e2) {
		int scoreOne = 0;
		int scoreTwo = 0;
		if(vector != null) {
			scoreOne = vector.getHammingDistance(e1);
			scoreTwo = vector.getHammingDistance(e2);
		}
		return scoreOne > scoreTwo ? -1 : scoreOne < scoreTwo ? 1 : super.compare(e1, e2);
	}

}
