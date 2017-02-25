/*********************************************************************************************
 * 
 *
 * 'Parameter.java', in plugin 'msi.gama.headless', is part of the source code of the 
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.headless.job;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.headless.common.DataType;
import msi.gama.kernel.model.IModel;
import msi.gaml.descriptions.IDescription;
import msi.gaml.expressions.IExpression;
import msi.gaml.types.IType;

public class Parameter {

	private String name;
	private String var;
	private Object value;
	private final DataType type;

	public Parameter(final Parameter p) {
		this.name = p.name;
		this.var = p.var;		
		this.value = p.value;
		this.type = p.type;
	}

	public Parameter clone(final Parameter p) {
		return new Parameter(p);
	}

	public static Parameter loadAndBuildParameter(final IDescription paramDesc, final IModel model) {
		final String name = paramDesc.getLitteral(IKeyword.NAME);
		final String varName = paramDesc.getLitteral(IKeyword.VAR);
		final IExpression exp = paramDesc.getFacetExpr(IKeyword.INIT);
		final Object val = exp.isConst() ? exp.value(null) : exp.serialize(true);
		final Parameter res = new Parameter(name, varName, val, translate(paramDesc.getType().id()));
		return res;
	}

	public static DataType translate(final Integer t) {
//		final DataType res;
		if (t.equals(IType.BOOL)) {
			return DataType.BOOLEAN;
		} else if (t.equals(IType.INT)) {
			return DataType.INT;
		} else if (t.equals(IType.FLOAT)) {
			return DataType.FLOAT;
		} else if (t.equals(IType.STRING)) {
			return DataType.STRING;
		}
		return DataType.UNDEFINED;
	}

	public Parameter(final String name,final String var, final Object value, final DataType type) {
		super();
		this.name = name;
		this.var = var;
		this.value = value;
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public String getVar() {
		return var;
	}
	
	public void setVar(final String var) {
		this.var = var;
	}	
	
	public Object getValue() {
		return this.value;
	}

	public void setValue(final Object value) {
		this.value = value;
	}

	public DataType getType() {
		return type;
	}

}
