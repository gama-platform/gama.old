/*******************************************************************************************************
 *
 * ControlContingency.java, in espacedev.gaml.extensions.genstar, is part of the source code of the GAMA modeling and
 * simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package gospl.distribution.matrix.control;

/**
 * The Class ControlContingency.
 */
public class ControlContingency extends AControl<Integer> {

	/**
	 * Instantiates a new control contingency.
	 *
	 * @param control
	 *            the control
	 */
	public ControlContingency(final Integer control) {
		super(control);
	}

	@Override
	public AControl<Integer> add(final AControl<? extends Number> controlCombiner) {
		this.setValue(this.getValue().intValue() + controlCombiner.getValue().intValue());
		return this;
	}

	@Override
	public AControl<Integer> add(final Integer controlCombiner) {
		this.setValue(this.getValue().intValue() + controlCombiner);
		return this;
	}

	@Override
	public AControl<Integer> multiply(final AControl<? extends Number> controlMultiplier) {
		this.setValue((int) Math.round(this.getValue().intValue() * controlMultiplier.getValue().doubleValue()));
		return this;
	}

	@Override
	public AControl<Integer> multiply(final Integer controlMultiplier) {
		this.setValue(this.getValue().intValue() * controlMultiplier);
		return this;
	}

	@Override
	public Integer getSum(final AControl<? extends Number> controlSum) {
		return this.getValue().intValue() + controlSum.getValue().intValue();
	}

	@Override
	public Integer getDiff(final AControl<? extends Number> controlDiff) {
		return this.getValue().intValue() - controlDiff.getValue().intValue();
	}

	@Override
	public Integer getRowProduct(final AControl<? extends Number> controlProd) {
		return this.getValue().intValue() * controlProd.getValue().intValue();
	}

	@Override
	public Integer getRoundedProduct(final AControl<? extends Number> controlProd) {
		return (int) Math.round(this.getValue().intValue() * controlProd.getValue().doubleValue());
	}

	@Override
	public boolean equalsVal(final AControl<Integer> val, final double delta) {
		return Math.abs(this.getValue() - val.getValue()) <= this.getValue() * delta;
	}

	@Override
	public boolean equalsCastedVal(final AControl<? extends Number> val, final double delta) {
		return Math.abs(Math.abs(this.getValue() - val.getValue().doubleValue())) <= this.getValue() * delta;
	}

	@Override
	public int compareTo(final AControl<Integer> o) {
		if (getValue() > o.getValue()) return 1;
		if (getValue() < o.getValue()) return -1;
		if (getValue().equals(o.getValue()) && this.hashCode() != o.hashCode())
			return this.hashCode() < o.hashCode() ? -1 : 1;
		return 0;
	}

}
