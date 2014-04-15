/*********************************************************************************************
 * 
 *
 * 'UserInputStatement.java', in plugin 'msi.gama.core', is part of the source code of the 
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gaml.architecture.user;

import java.util.List;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.kernel.experiment.IParameter;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.*;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.descriptions.IDescription;
import msi.gaml.expressions.IExpression;
import msi.gaml.statements.AbstractPlaceHolderStatement;
import msi.gaml.types.*;

/**
 * Written by drogoul Modified on 7 févr. 2010
 * 
 * @todo Description
 * 
 */
@symbol(name = { IKeyword.USER_INPUT }, kind = ISymbolKind.SINGLE_STATEMENT, with_sequence = false)
@inside(symbols = IKeyword.USER_COMMAND)
@facets(value = { @facet(name = IKeyword.NAME, type = IType.LABEL, optional = true),
	@facet(name = IKeyword.TYPE, type = IType.TYPE_ID, optional = true),
	@facet(name = IKeyword.INIT, type = IType.NONE, optional = true),
	@facet(name = IKeyword.MIN, type = IType.FLOAT, optional = true),
	@facet(name = IKeyword.MAX, type = IType.FLOAT, optional = true),
	@facet(name = IKeyword.RETURNS, type = IType.NEW_TEMP_ID, optional = false),
	@facet(name = IKeyword.AMONG, type = IType.LIST, optional = true) }, omissible = IKeyword.NAME)
public class UserInputStatement extends AbstractPlaceHolderStatement implements IParameter {

	int order;
	static int index;
	Object value;
	Object initialValue;
	IExpression min, max, among;
	String tempVar;

	public UserInputStatement(final IDescription desc) {
		super(desc);
		order = index++;
		value = initialValue = getFacet(IKeyword.INIT).value(null);
		min = getFacet(IKeyword.MIN);
		max = getFacet(IKeyword.MAX);
		among = getFacet(IKeyword.AMONG);
		tempVar = getLiteral(IKeyword.RETURNS);
	}

	@Override
	public String getTitle() {
		return description.getName();
	}

	@Override
	public String getCategory() {
		return null;
	}

	@Override
	public String getUnitLabel() {
		return null;
	}

	@Override
	public Integer getDefinitionOrder() {
		return order;
	}

	@Override
	public void setValue(final IScope scope, final Object value) {
		this.value = value;
	}

	@Override
	public Object value(final IScope scope) throws GamaRuntimeException {
		return value;
	}

	@Override
	public IType getType() {
		final IType type = description.getType();
		if ( type != Types.NO_TYPE ) { return type; }
		if ( value == null ) { return Types.NO_TYPE; }
		return description.getModelDescription().getTypesManager().get(value.getClass());
	}

	@Override
	public Object getInitialValue(final IScope scope) {
		return initialValue;
	}

	@Override
	public Number getMinValue() {
		return min == null ? null : (Number) min.value(null);
	}

	@Override
	public Number getMaxValue() {
		return max == null ? null : (Number) max.value(null);
	}

	@Override
	protected Object privateExecuteIn(final IScope scope) {
		scope.setVarValue(tempVar, value);
		return value;
	}

	public String getTempVarName() {
		return tempVar;
	}

	@Override
	public List getAmongValue() {
		return among == null ? null : (List) among.value(null);
	}

	@Override
	public boolean isEditable() {
		return true;
	}

	// @Override
	// public boolean isLabel() {
	// return false;
	// }

	@Override
	public Number getStepValue() {
		return null;
	}

	//
	// @Override
	// public void tryToInit(IScope scope) {}

	/**
	 * Method setUnitLabel()
	 * @see msi.gama.kernel.experiment.IParameter#setUnitLabel(java.lang.String)
	 */
	@Override
	public void setUnitLabel(final String label) {}

}
