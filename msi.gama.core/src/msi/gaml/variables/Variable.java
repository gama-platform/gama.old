/*
 * GAMA - V1.4 http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2012
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2012
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen (Batch, GeoTools & JTS), 2009-2012
 * - Beno”t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gaml.variables;

import java.util.List;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.*;
import msi.gama.runtime.*;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.compilation.*;
import msi.gaml.descriptions.*;
import msi.gaml.expressions.*;
import msi.gaml.operators.Cast;
import msi.gaml.skills.ISkill;
import msi.gaml.types.*;

/**
 * The Class Var.
 * 
 * 
 * FOR THE MOMENT SPECIES_WIDE CONSTANTS ARE NOT CONSIDERED (TOO MANY THINGS TO CONSIDER AND
 * POSSIBILITIES TO MAKE FALSE POSITIVE)
 */
@facets(value = { @facet(name = IKeyword.NAME, type = IType.NEW_VAR_ID, optional = false),
	@facet(name = IKeyword.TYPE, type = IType.TYPE_ID, optional = true),
	@facet(name = IKeyword.INIT, type = IType.NONE_STR, optional = true),
	@facet(name = IKeyword.VALUE, type = IType.NONE_STR, optional = true),
	@facet(name = IKeyword.UPDATE, type = IType.NONE_STR, optional = true),
	@facet(name = IKeyword.FUNCTION, type = IType.NONE_STR, optional = true),
	@facet(name = IKeyword.CONST, type = IType.BOOL_STR, optional = true),
	@facet(name = IKeyword.CATEGORY, type = IType.LABEL, optional = true),
	@facet(name = IKeyword.PARAMETER, type = IType.LABEL, optional = true),
	@facet(name = IKeyword.INITER, type = IType.LABEL, optional = true),
	@facet(name = IKeyword.GETTER, type = IType.LABEL, optional = true),
	@facet(name = IKeyword.SETTER, type = IType.LABEL, optional = true),
	@facet(name = IKeyword.AMONG, type = IType.LIST_STR, optional = true) }, omissible = IKeyword.NAME)
@symbol(kind = ISymbolKind.Variable.REGULAR, with_sequence = false)
@inside(kinds = { ISymbolKind.SPECIES, ISymbolKind.EXPERIMENT })
public class Variable extends Symbol implements IVariable {

	protected IExpression updateExpression, initExpression, amongExpression, functionExpression;
	protected IType type, contentType;
	protected boolean isNotModifiable, doUpdate;
	private final int definitionOrder;
	public IVarGetter getter, initer;
	public IVarSetter setter;
	protected String gName, sName, iName, pName, cName;
	protected ISkill gSkill, iSkill, sSkill;

	// public boolean javaInternal;

	public Variable(final IDescription sd) {
		super(sd);
		VariableDescription desc = (VariableDescription) sd;
		doUpdate = true;
		setName(getFacet(IKeyword.NAME).literalValue());
		pName = desc.getParameterName();
		cName = getLiteral(IKeyword.CATEGORY, null);
		updateExpression = getFacet(IKeyword.VALUE, getFacet(IKeyword.UPDATE));
		functionExpression = getFacet(IKeyword.FUNCTION);
		initExpression = getFacet(IKeyword.INIT);
		amongExpression = getFacet(IKeyword.AMONG);
		isNotModifiable = desc.isNotModifiable();
		type = desc.getType();
		contentType = desc.getContentType();
		definitionOrder = desc.getDefinitionOrder();
		SpeciesDescription context = desc.getSpeciesContext();
		buildHelpers(context);
		// javaInternal = getter != null && setter != null;

	}

	private void buildHelpers(final SpeciesDescription context) {
		if ( getFacet(IKeyword.GETTER) != null ) {
			gName = getLiteral(IKeyword.GETTER);
			gSkill = context.getSkillFor(gName);
			getter = AbstractGamlAdditions.getGetter(context.getSkillClassFor(gName), gName);
		}
		if ( getFacet(IKeyword.INITER) != null ) {
			iName = getLiteral(IKeyword.INITER);
			iSkill = context.getSkillFor(iName);
			initer = AbstractGamlAdditions.getGetter(context.getSkillClassFor(iName), iName);
		}
		if ( getFacet(IKeyword.SETTER) != null ) {
			sName = getLiteral(IKeyword.SETTER);
			sSkill = context.getSkillFor(sName);
			setter = AbstractGamlAdditions.getSetter(context.getSkillClassFor(sName), sName);
		}
	}

	protected Object coerce(final IAgent agent, final IScope scope, final Object v)
		throws GamaRuntimeException {
		return Types.coerce(scope, v, type, null);
	}

	// private void computeSpeciesConst(final IScope scope) {
	// if ( !isNotModifiable ) { return; }
	// if ( initExpression != null && !initExpression.isConst() ) { return; }
	// if ( updateExpression != null && !updateExpression.isConst() ) { return; }
	// if ( getter != null || setter != null ) { return; }
	// for ( final IVariable v : dependsOn ) {
	// if ( !v.isConst() ) { return; }
	// }
	// isSpeciesConst = true;
	// computeStaticValue(scope);
	// }
	//
	// public void computeStaticValue(final IScope scope) {
	// staticValue = coerce(null, getAnyExpression(scope).value(scope));
	// }

	@Override
	public String toGaml() {
		return getName();
	}

	@Override
	public String toString() {
		String result = isConst() ? IKeyword.CONST : IKeyword.VAR;
		result += " " + type.toString() + "[" + getName() + "]";
		return result;
	}

	@Override
	public void setValue(final Object initial) {
		initExpression = new JavaConstExpression(initial);
		setFacet(IKeyword.INIT, initExpression);
	}

	@Override
	public boolean isConst() {
		return false /* isSpeciesConst */;
	}

	@Override
	public void dispose() {
		super.dispose();
		initer = null;
		getter = null;
		setter = null;
		sSkill = null;
		iSkill = null;
		gSkill = null;
	}

	@Override
	public boolean isParameter() {
		return getDescription().isParameter();
	}

	@Override
	public VariableDescription getDescription() {
		return (VariableDescription) description;
	}

	@Override
	public boolean isUpdatable() {
		return updateExpression != null && !isNotModifiable;
	}

	@Override
	public IType type() {
		return type;
	}

	@Override
	public void initializeWith(final IScope scope, final IAgent a, final Object v)
		throws GamaRuntimeException {
		try {
			doUpdate = false;
			if ( v != null ) {
				_setVal(a, scope, v);
			} else if ( initExpression != null ) {
				_setVal(a, scope, scope.evaluate(initExpression, a));
			} else if ( initer != null ) {
				_setVal(a, scope, initer.execute(a, iSkill == null ? (ISkill) a : iSkill));
			} else {
				doUpdate = true;
				_setVal(a, scope, type().getDefault());
			}
		} catch (GamaRuntimeException e) {
			e.addContext("in initializing attribute " + getName());
			throw e;
		}
	}

	@Override
	public String getTitle() {
		return pName;
	}

	@Override
	public String getCategory() {
		return cName;
	}

	@Override
	public Integer getDefinitionOrder() {
		return definitionOrder;
	}

	@Override
	public void setChildren(final List<? extends ISymbol> children) {}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(final String name) {
		this.name = name;
	}

	@Override
	public final void setVal(final IScope scope, final IAgent agent, final Object v)
		throws GamaRuntimeException {
		if ( isNotModifiable ) { return; }
		_setVal(agent, scope, v);
	}

	protected void _setVal(final IAgent agent, final IScope scope, final Object v)
		throws GamaRuntimeException {
		Object val;
		val = coerce(agent, scope, v);
		val = checkAmong(agent, scope, val);
		if ( setter != null ) {
			setter.execute(agent, sSkill == null ? agent : sSkill, val);
		} else {
			agent.setAttribute(name, val);
		}
	}

	protected Object checkAmong(final IAgent agent, final IScope scope, final Object val)
		throws GamaRuntimeException {
		if ( amongExpression == null ) { return val; }
		List among = Cast.asList(scope, scope.evaluate(amongExpression, agent));
		if ( among == null ) { return val; }
		if ( among.contains(val) ) { return val; }
		if ( among.isEmpty() ) { return null; }
		throw new GamaRuntimeException("Value " + val +
			" is not included in the possible values of variable " + name);
	}

	@Override
	public Object value(final IScope scope) throws GamaRuntimeException {
		return value(scope, scope.getAgentScope());
	}

	@Override
	public Object value(final IScope scope, final IAgent agent) throws GamaRuntimeException {
		if ( getter != null ) { return getter.execute(agent, gSkill == null ? agent : gSkill); }
		if ( functionExpression != null ) { return scope.evaluate(functionExpression, agent); }
		return agent.getAttribute(name);
	}

	@Override
	public void updateFor(final IScope scope, final IAgent agent) throws GamaRuntimeException {
		if ( !doUpdate ) {
			doUpdate = true;
			return;
		}
		try {
			_setVal(agent, scope, updateExpression.value(scope));
		} catch (GamaRuntimeException e) {
			e.addContext("in updating attribute " + getName());
			throw e;
		}

	}

	@Override
	public Number getMinValue() {
		return null;
	}

	@Override
	public Number getMaxValue() {
		return null;
	}

	@Override
	public Number getStepValue() {
		return null;
	}

	@Override
	public List getAmongValue() {
		if ( amongExpression == null ) { return null; }
		if ( !amongExpression.isConst() ) { return null; }
		try {
			return Cast.asList(GAMA.getDefaultScope(),
				amongExpression.value(GAMA.getDefaultScope()));
		} catch (GamaRuntimeException e) {
			return null;
		}
	}

	@Override
	public Object getInitialValue() {
		if ( initExpression != null && initExpression.isConst() ) {
			try {
				return initExpression.value(GAMA.getDefaultScope());
			} catch (GamaRuntimeException e) {
				return null;
			}
		}
		return null;
	}

	@Override
	public String getUnitLabel() {
		return null;
	}

	@Override
	public boolean isEditable() {
		return true;
	}

	@Override
	public boolean isLabel() {
		return false;
	}

	@Override
	public boolean allowsTooltip() {
		return true;
	}

}
