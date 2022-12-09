/*******************************************************************************************************
 *
 * MarginDescriptor.java, in espacedev.gaml.extensions.genstar, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package gospl.algo.ipf.margin;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;

import core.metamodel.value.IValue;

/**
 * A margin for the IPF algorithm to build constraint for updating process. It is related to an attribute of reference,
 * i.e. the margin it is in, and describe a combination of value(s) for the other attribute(s) of interest. <br>
 * If control and seed attribute are exactly the same, it has records for control as well ass seed marginal values
 * description.
 * <p>
 * For ex. we have 3 attributes 'AGE', 'GENDER' and 'OCCUPATION'. A margin descriptor for attribute 'AGE' is a
 * combination of value of 'GENDER' and 'OCCUPATION', e.g. {'25 to 29'; 'employee'} or {'under 5' and 'empty'}. What
 * make this descriptor important is when control and seed attribute values differs, like for ex. 'AGE' and 'AGE2' for
 * which the former describe range age and the last int age
 *
 * @author kevinchapuis
 *
 */
public class MarginDescriptor {

	/** The seed. */
	private Set<IValue> seed;
	
	/** The control. */
	private Set<IValue> control;

	/**
	 * Instantiates a new margin descriptor.
	 */
	public MarginDescriptor() {}

	/**
	 * Instantiates a new margin descriptor.
	 *
	 * @param seed the seed
	 * @param control the control
	 */
	public MarginDescriptor(final Set<IValue> seed, final Set<IValue> control) {
		this.seed = seed;
		this.control = control;
	}

	/**
	 * The values that describe margin according to seed attribute encoding
	 *
	 * @return
	 */
	public Set<IValue> getSeed() { return Collections.unmodifiableSet(seed); }

	/**
	 * Sets the seed.
	 *
	 * @param seed the seed
	 * @return the margin descriptor
	 */
	public MarginDescriptor setSeed(final Set<IValue> seed) {
		this.seed = seed;
		return this;
	}

	/**
	 * The values that describe margin according to control attribute encoding
	 *
	 * @return
	 */
	public Set<IValue> getControl() { return Collections.unmodifiableSet(control); }

	/**
	 * Sets the control.
	 *
	 * @param control the control
	 * @return the margin descriptor
	 */
	public MarginDescriptor setControl(final Set<IValue> control) {
		this.control = control;
		return this;
	}

	@Override
	public String toString() {
		return Arrays.toString(control.toArray());
	}

}
