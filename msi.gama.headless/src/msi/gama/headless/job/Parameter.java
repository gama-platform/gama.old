/*******************************************************************************************************
 *
 * Parameter.java, in msi.gama.headless, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.headless.job;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.headless.common.DataType;
import msi.gama.kernel.model.IModel; 
import msi.gaml.descriptions.IDescription;
import msi.gaml.expressions.IExpression;
import msi.gaml.expressions.operators.BinaryOperator;
import msi.gaml.types.GamaFileType;
import msi.gaml.types.IType;

/**
 * The Class Parameter.
 */
public class Parameter {

	/** The name. */
	private String name;
	
	/** The var. */
	private String var;
	
	/** The value. */
	private Object value;
	
	/** The type. */
	private final DataType type;

	/**
	 * Instantiates a new parameter.
	 *
	 * @param p the p
	 */
	public Parameter(final Parameter p) {
		this.name = p.name;
		this.var = p.var;
		this.value = p.value;
		this.type = p.type;
	}

	/**
	 * Clone.
	 *
	 * @param p the p
	 * @return the parameter
	 */
	public Parameter clone(final Parameter p) {
		return new Parameter(p);
	}

	/**
	 * Load and build parameter.
	 *
	 * @param paramDesc the param desc
	 * @param model the model
	 * @return the parameter
	 */
	public static Parameter loadAndBuildParameter(final IDescription paramDesc, final IModel model) {
		final String name = paramDesc.getLitteral(IKeyword.NAME);
		final String varName = paramDesc.getLitteral(IKeyword.VAR);
		final IExpression exp = paramDesc.getFacetExpr(IKeyword.INIT);
		Object val = exp.isConst() ? exp.getConstValue() : exp.serialize(true);
		if(exp.getGamlType().getParent() instanceof GamaFileType) {
			val=((BinaryOperator)exp).arg(0);
		}
		final Parameter res = new Parameter(name, varName, val, translate(paramDesc.getGamlType().id()));
		return res;
	}

	/**
	 * Translate.
	 *
	 * @param t the t
	 * @return the data type
	 */
	public static DataType translate(final Integer t) {
		// final DataType res;
		if (t.equals(IType.BOOL)) {
			return DataType.BOOLEAN;
		} else if (t.equals(IType.INT)) {
			return DataType.INT;
		} else if (t.equals(IType.FLOAT)) {
			return DataType.FLOAT;
		} else if (t.equals(IType.STRING)) { return DataType.STRING; }
		return DataType.UNDEFINED;
	}

	/**
	 * Instantiates a new parameter.
	 *
	 * @param name the name
	 * @param var the var
	 * @param value the value
	 * @param type the type
	 */
	public Parameter(final String name, final String var, final Object value, final DataType type) {
		super();
		this.name = name;
		this.var = var;
		this.value = value;
		this.type = type;
	}

	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name.
	 *
	 * @param name the new name
	 */
	public void setName(final String name) {
		this.name = name;
	}

	/**
	 * Gets the var.
	 *
	 * @return the var
	 */
	public String getVar() {
		return var;
	}

	/**
	 * Sets the var.
	 *
	 * @param var the new var
	 */
	public void setVar(final String var) {
		this.var = var;
	}

	/**
	 * Gets the value.
	 *
	 * @return the value
	 */
	public Object getValue() {
		return this.value;
	}

	/**
	 * Sets the value.
	 *
	 * @param value the new value
	 */
	public void setValue(final Object value) {
		this.value = value;
	}

	/**
	 * Gets the type.
	 *
	 * @return the type
	 */
	public DataType getType() {
		return type;
	}

}
