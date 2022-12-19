/*******************************************************************************************************
 *
 * ControlFrequency.java, in espacedev.gaml.extensions.genstar, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package gospl.distribution.matrix.control;

/**
 * The Class ControlFrequency.
 */
public class ControlFrequency extends AControl<Double> {

	/**
	 * Instantiates a new control frequency.
	 *
	 * @param control the control
	 */
	public ControlFrequency(final Double control) {
		super(control);
	}

	@Override
	public AControl<Double> add(final AControl<? extends Number> controlCombiner) {
		return this.add(controlCombiner.getValue().doubleValue());
	}

	@Override
	public AControl<Double> add(final Double controlCombiner) {
		this.setValue(this.getValue() + controlCombiner);
		return this;
	}

	@Override
	public AControl<Double> multiply(final AControl<? extends Number> controlMultiplier) {
		return this.multiply(controlMultiplier.getValue().doubleValue());
	}

	@Override
	public AControl<Double> multiply(final Double controlMultiplier) {
		this.setValue(this.getValue() * controlMultiplier);
		return this;
	}

	@Override
	public Double getSum(final AControl<? extends Number> controlSum) {
		return this.getValue().doubleValue() + controlSum.getValue().doubleValue();
	}

	@Override
	public Double getDiff(final AControl<? extends Number> controlDiff) {
		return this.getValue().doubleValue() - controlDiff.getValue().doubleValue();
	}

	@Override
	public Double getRowProduct(final AControl<? extends Number> controlProd) {
		return this.getValue().doubleValue() * controlProd.getValue().doubleValue();
	}

	@Override
	public Double getRoundedProduct(final AControl<? extends Number> controlProd) {
		return getRowProduct(controlProd);
	}

	@Override
	public boolean equalsVal(final AControl<Double> val, final double delta) {
		return Math.abs(this.getValue() - val.getValue()) <= this.getValue() * delta;
	}

	@Override
	public boolean equalsCastedVal(final AControl<? extends Number> val, final double delta) {
		return Math.abs(this.getValue() - val.getValue().doubleValue()) <= this.getValue() * delta;
	}

	@Override
	public int compareTo(final AControl<Double> o) {
		if (getValue() > o.getValue()) return 1;
		if (getValue() < o.getValue()) return -1;
		if (getValue().equals(o.getValue()) && this.hashCode() != o.hashCode())
			return this.hashCode() < o.hashCode() ? -1 : 1;
		return 0;
	}

}
