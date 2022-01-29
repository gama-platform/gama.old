/*******************************************************************************************************
 *
 * GamaRange.java, in espacedev.gaml.extensions.genstar, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package espacedev.gaml.extensions.genstar.type;

import java.util.Random;

import core.util.random.GenstarRandom;
import msi.gama.common.interfaces.IValue;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.getter;
import msi.gama.precompiler.GamlAnnotations.variable;
import msi.gama.precompiler.GamlAnnotations.vars;
import msi.gama.runtime.IScope;
import msi.gaml.types.IType;

/**
 * The Class GamaRange.
 */
@vars({ 
	@variable(name = "min_value", type = IType.FLOAT, doc = 	@doc("The lower bound of the range.")),
	@variable(name = "max_value", type = IType.FLOAT, doc = @doc("The upper bound of the range."))
	})
public class GamaRange implements IValue{

	/** The min. */
	Number min; 
	
	/** The max. */
	Number max;
	
	/**
	 * Instantiates a new gama range.
	 *
	 * @param min the min
	 * @param max the max
	 */
	public GamaRange(Number min, Number max) {
		this.min = min;
		this.max = max;
	}

	/**
	 * Gets the min.
	 *
	 * @return the min
	 */
	@getter("min_value")
	public Number getMin() {
		return min.doubleValue();
	}

	/**
	 * Gets the max.
	 *
	 * @return the max
	 */
	@getter("max_value")
	public Number getMax() {
		return max.doubleValue();
	}
	
	@Override
	public String serialize(boolean includingBuiltIn) {
		return min +"->" +max;
	}

	@Override
	public String stringValue(IScope scope) {
		return serialize(true);
	}
	
	public String toString()  {
		return serialize(true);
	}

	@Override
	public IValue copy(IScope scope) {
		return new GamaRange(min, max);
	}
	
	/**
	 * Cast.
	 *
	 * @param scope the scope
	 * @param type the type
	 * @return the object
	 */
	@SuppressWarnings("rawtypes")
	public Object cast(IScope scope, IType type) {
		if(type == null) { return this; }
		
		if(type.id() == IType.INT) {
			return intValue();
		} 
		if(type.id() == IType.FLOAT) {
			return floatValue();
		}
		if(type.id() == IType.STRING) {
			return stringValue(scope);
		}
		return this;
	}

	/**
	 * Float value.
	 *
	 * @return the double
	 */
	// TODO : à raffiner ... 
	private double floatValue() {
		Random random = GenstarRandom.getInstance();
		return (max.doubleValue() - min.doubleValue() + 1) * random.nextDouble() + min.doubleValue();
	}

	/**
	 * Int value.
	 *
	 * @return the int
	 */
	private int intValue() {
		return (int) floatValue();
	}
}
