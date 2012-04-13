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

import java.util.*;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
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
/*
 * @symbol(name = { ISymbol.VAR, ISymbol.CONST, IType.SPECIES_STR, IType.COLOR_STR, IType.PAIR_STR,
 * IType.MAP_STR, IType.POINT_STR, IType.AGENT_STR, IType.NONE_STR, IType.FILE_STR, IType.BOOL_STR,
 * IType.STRING_STR, IType.GEOM_STR }, kind = ISymbolKind.VARIABLE) remove the IType.SPECIES_STR for
 * the Delegation be parsed and compiled successfully.
 */
@symbol(name = { IKeyword.VAR, IKeyword.CONST, IType.AGENT_STR, IType.NONE_STR, IType.BOOL_STR,
	IType.STRING_STR, IType.GEOM_STR, IType.TOPOLOGY_STR, IType.PATH_STR }, kind = ISymbolKind.VARIABLE)
@inside(kinds = { ISymbolKind.SPECIES, ISymbolKind.EXPERIMENT })
//
//
//
//
// TODO
// PENSER A ELIMINER LA LISTE PRECEDENTE EN RAJOUTANT L'INFORMATION DE LA CLASSE DE VARIABLES
// AU TYPE
// TODO
//
public class Variable extends Symbol implements IVariable {

	protected IExpression updateExpression, initExpression, amongExpression, functionExpression;
	protected IType type, contentType;
	protected boolean isNotModifiable, doUpdate;
	private final int definitionOrder;
	public IVarGetter getter, initer;
	public IVarSetter setter;
	protected String gName, sName, iName, pName, cName;
	protected ISkill gSkill, iSkill, sSkill;
	public boolean javaInternal;

	public Variable(final IDescription sd) {
		super(sd);
		VariableDescription desc = (VariableDescription) sd;
		doUpdate = true;
		setName(getFacet(IKeyword.NAME).literalValue());
		computeParameterName();
		computeCategoryName();
		updateExpression = getFacet(IKeyword.VALUE, getFacet(IKeyword.UPDATE));
		functionExpression = getFacet(IKeyword.FUNCTION);
		initExpression = getFacet(IKeyword.INIT);
		amongExpression = getFacet(IKeyword.AMONG);
		isNotModifiable = desc.isNotModifiable();
		type = desc.getType();
		contentType = desc.getContentType();
		definitionOrder = desc.getDefinitionOrder();

		if ( functionExpression != null && (initExpression != null || updateExpression != null) ) {
			error("A function cannot have an 'init' or 'update' facet");
		}

		if ( desc.isBuiltIn() ) {
			ExecutionContextDescription context = desc.getSpeciesContext();

			if ( context == null ) {
				desc.getSpeciesContext();
			}

			// try {
			buildHelpers(context);
			// } catch (final GamlException e) {
			// e.addContext("in building variable " + name + " java helpers");
			// throw e;
			// }

		}
		javaInternal = getter != null && setter != null;

		if ( contentType.id() == IType.NONE ) {
			IType cType =
				updateExpression != null ? updateExpression.getContentType()
					: initExpression != null ? initExpression.getContentType()
						: functionExpression != null ? functionExpression.getContentType()
							: Types.NO_TYPE;
			contentType = cType == null ? Types.NO_TYPE : cType;
			desc.setContentType(contentType);
		}

		if ( amongExpression != null && type.id() != amongExpression.getContentType().id() ) {
			error("Var " + getName() + " of type " + type.toString() + " cannot be chosen among " +
				amongExpression.toGaml());
		}

		assertCanBeParameter();

	}

	private void assertCanBeParameter() {
		if ( !isParameter() ) { return; }
		String p = "Parameter " + getTitle() + " ";
		IExpression min = getFacet(IKeyword.MIN);
		IExpression max = getFacet(IKeyword.MAX);
		if ( functionExpression != null ) {
			error("Functions cannot be used as parameters");
		} else if ( min != null && !min.isConst() ) {
			error(p + " min value must be constant");
		} else if ( max != null && !max.isConst() ) {
			error(p + " max value must be constant");
		} else if ( initExpression == null ) {
			error("parameters must have an initial value");
		} else if ( !initExpression.isConst() ) {
			error(p + "initial value must be constant");
		} else if ( updateExpression != null ) {
			error(p + "cannot have an 'update' or 'value' facet");
		} else if ( isNotModifiable ) {
			error(p + " cannot be declared as constant ");
		}
	}

	private void buildHelpers(final ExecutionContextDescription context) {
		Class valueClass = type.toClass();
		if ( getFacet(IKeyword.GETTER) != null ) {
			gName = getLiteral(IKeyword.GETTER);
			gSkill = context.getSkillFor(gName);
			getter = GamlCompiler.getGetter(context.getSkillClassFor(gName), gName, valueClass);
		}
		if ( getFacet(IKeyword.INITER) != null ) {
			iName = getLiteral(IKeyword.INITER);
			iSkill = context.getSkillFor(iName);
			initer = GamlCompiler.getGetter(context.getSkillClassFor(iName), iName, valueClass);
		}
		if ( getFacet(IKeyword.SETTER) != null ) {
			sName = getLiteral(IKeyword.SETTER);
			sSkill = context.getSkillFor(sName);
			setter = GamlCompiler.getSetter(context.getSkillClassFor(sName), sName, valueClass);
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
		return getFacet(IKeyword.PARAMETER) != null;
	}

	@Override
	public boolean isUpdatable() {
		return updateExpression != null && !isNotModifiable;
	}

	@Override
	public IType getContentType() {
		return contentType;
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

	public void computeParameterName() {
		final IExpression result = getFacet(IKeyword.PARAMETER);
		if ( result == null ) {
			pName = getName();
			return;
		}
		pName = result.literalValue();
		if ( pName.equals("true") ) {
			pName = getName();
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

	public void computeCategoryName() {
		cName = getLiteral(IKeyword.CATEGORY, null);
	}

	@Override
	public Integer getDefinitionOrder() {
		return definitionOrder;
	}

	@Override
	public void setChildren(final List<? extends ISymbol> commands) {}

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

	public boolean hasChildren() {
		return false;
	}

	public List<? extends ISymbol> getChildren() {
		return Collections.EMPTY_LIST;
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
	public boolean isBuiltIn() {
		return ((VariableDescription) description).isBuiltIn();
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
