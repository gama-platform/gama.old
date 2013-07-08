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
 * - Beno�t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
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
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.IList;
import msi.gaml.compilation.*;
import msi.gaml.descriptions.*;
import msi.gaml.expressions.IExpression;
import msi.gaml.operators.Cast;
import msi.gaml.skills.ISkill;
import msi.gaml.types.IType;

/**
 * The Class Var.
 * 
 * 
 * FIXME FOR THE MOMENT SPECIES_WIDE CONSTANTS ARE NOT CONSIDERED (TOO MANY THINGS TO CONSIDER AND
 * POSSIBILITIES TO MAKE FALSE POSITIVE)
 */
@facets(value = { @facet(name = IKeyword.NAME, type = IType.NEW_VAR_ID, optional = false),
	@facet(name = IKeyword.TYPE, type = IType.TYPE_ID, optional = true),
	@facet(name = IKeyword.OF, type = IType.TYPE_ID, optional = true),
	@facet(name = IKeyword.INDEX, type = IType.TYPE_ID, optional = true),
	@facet(name = IKeyword.INIT, type = IType.NONE, optional = true),
	@facet(name = IKeyword.VALUE, type = IType.NONE, optional = true),
	@facet(name = IKeyword.UPDATE, type = IType.NONE, optional = true),
	@facet(name = IKeyword.FUNCTION, type = IType.NONE, optional = true),
	@facet(name = IKeyword.CONST, type = IType.BOOL, optional = true),
	@facet(name = IKeyword.CATEGORY, type = IType.LABEL, optional = true),
	@facet(name = IKeyword.PARAMETER, type = IType.LABEL, optional = true),
	@facet(name = IKeyword.AMONG, type = IType.LIST, optional = true) }, omissible = IKeyword.NAME)
@symbol(kind = ISymbolKind.Variable.REGULAR, with_sequence = false)
@inside(kinds = { ISymbolKind.SPECIES, ISymbolKind.EXPERIMENT, ISymbolKind.MODEL })
public class Variable extends Symbol implements IVariable {

	protected IExpression updateExpression, initExpression, amongExpression, functionExpression;
	protected IType type, contentType;
	protected boolean isNotModifiable /* , doUpdate */;
	private final int definitionOrder;
	public GamaHelper getter, initer, setter;
	protected String /* gName, sName, iName, */pName, cName;
	protected ISkill gSkill/* , iSkill */, sSkill;

	public Variable(final IDescription sd) {
		super(sd);
		final VariableDescription desc = (VariableDescription) sd;
		// doUpdate = true;
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
		buildHelpers(desc);
	}

	private void buildHelpers(final VariableDescription var) {
		final SpeciesDescription species = var.getSpeciesContext();
		getter = var.getGetter();
		if ( getter != null ) {
			gSkill = species.getSkillFor(getter.getSkillClass());
		}
		initer = var.getIniter();
		setter = var.getSetter();
		if ( setter != null ) {
			sSkill = species.getSkillFor(setter.getSkillClass());
		}
	}

	protected Object coerce(final IAgent agent, final IScope scope, final Object v) throws GamaRuntimeException {
		return type.cast(scope, v, null);
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
		final IExpressionDescription desc = ConstantExpressionDescription.create(initial);
		initExpression = desc.getExpression();
		setFacet(IKeyword.INIT, desc);
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
	public IType getType() {
		return type;
	}

	@Override
	public void initializeWith(final IScope scope, final IAgent a, final Object v) throws GamaRuntimeException {
		try {
			// doUpdate = false;
			if ( v != null ) {
				_setVal(a, scope, v);
			} else if ( initExpression != null ) {
				_setVal(a, scope, scope.evaluate(initExpression, a));
			} else if ( initer != null ) {
				_setVal(a, scope, initer.run(scope, a, gSkill == null ? (ISkill) a : gSkill));
			} else {
				// doUpdate = true;
				_setVal(a, scope, getType().getDefault());
			}
		} catch (final GamaRuntimeException e) {
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
	public final void setVal(final IScope scope, final IAgent agent, final Object v) throws GamaRuntimeException {
		if ( isNotModifiable ) { return; }
		_setVal(agent, scope, v);
	}

	protected void _setVal(final IAgent agent, final IScope scope, final Object v) throws GamaRuntimeException {
		Object val;
		val = coerce(agent, scope, v);
		val = checkAmong(agent, scope, val);
		// TODO Verify that the agent is in the scope
		if ( setter != null ) {
			setter.run(scope, agent, sSkill == null ? agent : sSkill, val);
		} else {
			agent.setAttribute(name, val);
		}
	}

	protected Object checkAmong(final IAgent agent, final IScope scope, final Object val) throws GamaRuntimeException {
		if ( amongExpression == null ) { return val; }
		final List among = Cast.asList(scope, scope.evaluate(amongExpression, agent));
		if ( among == null ) { return val; }
		if ( among.contains(val) ) { return val; }
		if ( among.isEmpty() ) { return null; }
		throw GamaRuntimeException
			.error("Value " + val + " is not included in the possible values of variable " + name);
	}

	@Override
	public Object value(final IScope scope) throws GamaRuntimeException {
		return value(scope, scope.getAgentScope());
	}

	@Override
	public Object value(final IScope scope, final IAgent agent) throws GamaRuntimeException {
		if ( getter != null ) { return getter.run(scope, agent, gSkill == null ? agent : gSkill); }
		if ( functionExpression != null ) { return scope.evaluate(functionExpression, agent); }
		return agent.getAttribute(name);
	}

	// @Override
	// public void updateFor(final IScope scope, final IAgent agent) throws GamaRuntimeException {
	// // if ( !doUpdate ) {
	// // doUpdate = true;
	// // return;
	// // }
	// try {
	// _setVal(agent, scope, updateExpression.value(scope));
	// } catch (final GamaRuntimeException e) {
	// e.addContext("in updating attribute " + getName());
	// throw e;
	// }
	// }

	@Override
	public Object getUpdatedValue(final IScope scope) {
		return updateExpression.value(scope);
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
			return Cast.as(amongExpression, IList.class);
		} catch (final GamaRuntimeException e) {
			return null;
		}
	}

	@Override
	public Object getInitialValue(final IScope scope) {
		if ( initExpression != null /* && initExpression.isConst() */) {
			try {
				return initExpression.value(scope);
			} catch (final GamaRuntimeException e) {
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
	public void setUnitLabel(final String label) {}

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

	public ISkill getgSkill() {
		return gSkill;
	}

	/**
	 * Method getContentType()
	 * @see msi.gama.kernel.experiment.IParameter#getContentType()
	 */
	@Override
	public IType getContentType() {
		return contentType;
	}

}
