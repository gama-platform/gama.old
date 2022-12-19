/*******************************************************************************************************
 *
 * AControl.java, in espacedev.gaml.extensions.genstar, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package gospl.distribution.matrix.control;

/**
 * The Class AControl.
 *
 * @param <T>
 *            the generic type
 */
public abstract class AControl<T extends Number> implements Comparable<AControl<T>> {

	/** The control. */
	private T control;

	/**
	 * Instantiates a new a control.
	 *
	 * @param control
	 *            the control
	 */
	protected AControl(final T control) {
		this.control = control;
	}

	/**
	 * Gives the value of this {@link AControl}
	 *
	 * @return T value
	 */
	public T getValue() { return control; }

	/**
	 * Add the value of {@code controlCombiner} passed in argument to this {@link AControl}
	 *
	 * @param controlCombiner
	 * @return the {@link AControl} that calls the methods
	 */
	public abstract AControl<T> add(AControl<? extends Number> controlCombiner);

	/**
	 * Add the {@code controlCombiner} value to this {@link AControl}
	 *
	 * @param controlCombiner
	 * @return
	 */
	public abstract AControl<T> add(T controlCombiner);

	/**
	 * Multiplies this {@link AControl} by the value of {@code controlMultiplier} passed in argument
	 *
	 * @param controlMultiplier
	 * @return the {@link AControl} that calls the methods
	 */
	public abstract AControl<T> multiply(AControl<? extends Number> controlMultiplier);

	/**
	 * Multiplies this {@link AControl} by the T type value passed in argument
	 *
	 * @param controlMultiplier
	 * @return
	 */
	public abstract AControl<T> multiply(T controlMultiplier);

	/**
	 * Gives the value that results from the sum of this {@link AControl} value and the one of {@code controlSum} passed
	 * in argument
	 *
	 * @param controlSum
	 * @return T value
	 */
	public abstract T getSum(AControl<? extends Number> controlSum);

	/**
	 * Gives the value that results from the difference of this {@link AControl} value and the one of
	 * {@code controlDiff} passed in argument
	 *
	 * @param controlDiff
	 * @return
	 */
	public abstract T getDiff(AControl<? extends Number> controlDiff);

	/**
	 * Gives the value that result from product of this {@link AControl} value with <i> proper </i> {@code controlProd}
	 * value passed in argument. By proper value this means the concrete value type behind {@link Number}
	 *
	 * @param controlProd
	 * @return T value
	 */
	public abstract T getRowProduct(AControl<? extends Number> controlProd);

	/**
	 * Gives the value that result from product of this {@link AControl} value with {@code controlProd} value passed in
	 * argument. The resulting product is eventually rounded to fit the concrete value type behind {@link Number}
	 *
	 * @param controlProd
	 * @return T value
	 */
	public abstract T getRoundedProduct(AControl<? extends Number> controlProd);

	/**
	 * Inner modifier of T value
	 *
	 * @param control
	 */
	protected void setValue(final T control) { this.control = control; }

	/**
	 * Test if this {@link AControl} has value equals to {@code val} passed in argument. The two inner value are
	 * compared and equality is assessed with precision equals to a {@code delta} (i.e. proportional difference)
	 *
	 * @param total
	 * @param delta
	 * @return
	 */
	public abstract boolean equalsVal(AControl<T> val, double delta);

	/**
	 * Test if this {@link AControl} has value equals to {@code val} passed as argument. The two inner value are cast
	 * (to double) and compared and equality is assessed with precision equals to a {@code delta} (i.e. proportional
	 * difference)
	 *
	 * @param val
	 * @param epsilon
	 * @return
	 */
	public abstract boolean equalsCastedVal(AControl<? extends Number> val, double delta);

	@Override
	public String toString() {
		return control.toString();
	}

}
