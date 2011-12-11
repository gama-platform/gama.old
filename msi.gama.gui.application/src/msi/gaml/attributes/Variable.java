/*
 * GAMA - V1.4  http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2011
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2011
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen  (Batch, GeoTools & JTS), 2009-2011
 * - Beno”t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2011
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gaml.attributes;

import java.util.*;
import msi.gama.interfaces.*;
import msi.gama.internal.compilation.*;
import msi.gama.internal.descriptions.*;
import msi.gama.internal.types.Types;
import msi.gama.java.JavaConstExpression;
import msi.gama.kernel.*;
import msi.gama.kernel.exceptions.*;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.*;
import msi.gama.util.Cast;

/**
 * The Class Var.
 * 
 * 
 * FOR THE MOMENT SPECIES_WIDE CONSTANTS ARE NOT CONSIDERED (TOO MANY THINGS TO CONSIDER AND
 * POSSIBILITIES TO MAKE FALSE POSITIVE)
 */
@facets({ @facet(name = ISymbol.NAME, type = IType.NEW_VAR_ID, optional = false),
	@facet(name = ISymbol.TYPE, type = IType.TYPE_ID, optional = true),
	@facet(name = ISymbol.INIT, type = IType.NONE_STR, optional = true),
	@facet(name = ISymbol.VALUE, type = IType.NONE_STR, optional = true),
	@facet(name = ISymbol.CONST, type = IType.BOOL_STR, optional = true),
	@facet(name = ISymbol.CATEGORY, type = IType.LABEL, optional = true),
	@facet(name = ISymbol.PARAMETER, type = IType.LABEL, optional = true),
	@facet(name = ISymbol.DEPENDS_ON, type = IType.LABEL, optional = true),
	@facet(name = ISymbol.INITER, type = IType.LABEL, optional = true),
	@facet(name = ISymbol.GETTER, type = IType.LABEL, optional = true),
	@facet(name = ISymbol.SETTER, type = IType.LABEL, optional = true),
	@facet(name = ISymbol.AMONG, type = IType.LIST_STR, optional = true) })
/*
 * @symbol(name = { ISymbol.VAR, ISymbol.CONST, IType.SPECIES_STR, IType.COLOR_STR, IType.PAIR_STR,
 * IType.MAP_STR, IType.POINT_STR, IType.AGENT_STR, IType.NONE_STR, IType.FILE_STR, IType.BOOL_STR,
 * IType.STRING_STR, IType.GEOM_STR }, kind = ISymbolKind.VARIABLE) remove the IType.SPECIES_STR for
 * the Delegation be parsed and compiled successfully.
 */
@symbol(name = { ISymbol.VAR, ISymbol.CONST, IType.AGENT_STR, IType.NONE_STR, IType.BOOL_STR,
	IType.STRING_STR, IType.GEOM_STR, IType.TOPOLOGY_STR, IType.PATH_STR }, kind = ISymbolKind.VARIABLE)
@inside(kinds = { ISymbolKind.SPECIES })
//
//
//
//
// TODO
// PENSER A ELIMINER LA LISTE PRECEDENTE EN RAJOUTANT L'INFORMATION DE LA CLASSE DE VARIABLES
// AU TYPE
// TODO
// TODO
// PENSER A FAIRE UN TEST CONCERNANT LES PARAMETRES: INIT doit etre const; pas de VALUE, etc.
// TODO
//
public class Variable extends Symbol implements IVariable {

	protected IExpression updateExpression, initExpression, amongExpression;
	protected IType type, contentType;
	protected boolean isNotModifiable, doUpdate;
	private final int definitionOrder;
	public IVarGetter getter, initer;
	public IVarSetter setter;
	protected String gName, sName, iName, pName, cName;
	protected ISkill gSkill, iSkill, sSkill;
	public boolean javaInternal;

	public Variable(final IDescription sd) throws GamlException, GamaRuntimeException {
		super(sd);
		VariableDescription desc = (VariableDescription) sd;
		doUpdate = true;
		setName(getFacet(ISymbol.NAME).literalValue());
		computeParameterName();
		computeCategoryName();
		updateExpression = getFacet(ISymbol.VALUE);
		initExpression = getFacet(INIT);
		amongExpression = getFacet(AMONG);
		isNotModifiable = desc.isNotModifiable();
		type = desc.getType();
		contentType = desc.getContentType();
		definitionOrder = desc.getDefinitionOrder();

		if ( desc.isBuiltIn() ) {
			ExecutionContextDescription context = desc.getSpeciesContext();

			if ( context == null ) {
				desc.getSpeciesContext();
			}

			try {
				buildHelpers(context);
			} catch (final GamlException e) {
				e.addContext("in building variable " + name + " java helpers");
				throw e;
			}

		}
		javaInternal = getter != null && setter != null;

		if ( contentType.id() == IType.NONE ) {
			IType cType =
				updateExpression != null ? updateExpression.getContentType()
					: initExpression != null ? initExpression.getContentType() : Types.NO_TYPE;
			contentType = cType == null ? Types.NO_TYPE : cType;
			desc.setContentType(contentType);
		}

		if ( amongExpression != null && type.id() != amongExpression.getContentType().id() ) { throw new GamlException(
			"Var " + getName() + " of type " + type.toString() + " cannot be chosen among " +
				amongExpression.toGaml()); }

		assertCanBeParameter();

	}

	private void assertCanBeParameter() throws GamlException {
		if ( !isParameter() ) { return; }
		String p = "Parameter " + getTitle() + " ";
		IExpression min = getFacet(ISymbol.MIN);
		IExpression max = getFacet(ISymbol.MAX);
		if ( min != null && !min.isConst() ) { throw new GamlException(p +
			"min value must be constant"); }
		if ( max != null && !max.isConst() ) { throw new GamlException(p +
			"max value must be constant"); }
		if ( initExpression == null ) { throw new GamlException(
			"parameters must have an initial value"); }
		if ( !initExpression.isConst() ) { throw new GamlException(p +
			"initial value must be constant"); }
		if ( updateExpression != null ) { throw new GamlException(p + "cannot have a 'value' facet"); }
		if ( isNotModifiable ) { throw new GamlException(p + "cannot be declared as constant"); }
	}

	private void buildHelpers(final ExecutionContextDescription context) throws GamlException {
		Class valueClass = type.toClass();
		if ( getFacet(ISymbol.GETTER) != null ) {
			gName = getLiteral(ISymbol.GETTER);
			gSkill = context.getSkillFor(gName);
			getter = GamlCompiler.getGetter(context.getSkillClassFor(gName), gName, valueClass);
		}
		if ( getFacet(ISymbol.INITER) != null ) {
			iName = getLiteral(ISymbol.INITER);
			iSkill = context.getSkillFor(iName);
			initer = GamlCompiler.getGetter(context.getSkillClassFor(iName), iName, valueClass);
		}
		if ( getFacet(ISymbol.SETTER) != null ) {
			sName = getLiteral(ISymbol.SETTER);
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
		String result = isConst() ? ISymbol.CONST : VAR;
		result += " " + type.toString() + "[" + getName() + "]";
		return result;
	}

	@Override
	public void setValue(final Object initial) {
		initExpression = new JavaConstExpression(initial);
		setFacet(INIT, initExpression);
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
		// dependsOn.clear();
	}

	@Override
	public boolean isParameter() {
		return getFacet(ISymbol.PARAMETER) != null;
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

	public void computeParameterName() throws GamaRuntimeException {
		final IExpression result = getFacet(ISymbol.PARAMETER);
		if ( result == null ) {
			pName = getName();
			return;
		}
		pName = Cast.asString(result.literalValue());
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
		cName = getLiteral(ISymbol.CATEGORY, null);
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
		// scope.push(agent);
		// try {
		val = coerce(agent, scope, v);
		val = checkAmong(agent, scope, val);
		// } finally {
		// scope.pop(agent);
		// }
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
		return value(scope.getAgentScope());
	}

	@Override
	public Object value(final IAgent agent) throws GamaRuntimeException {
		if ( getter != null ) { return getter.execute(agent, gSkill == null ? agent : gSkill); }
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
			return Cast.asList(amongExpression.value(GAMA.getDefaultScope()));
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.interfaces.IParameter#isLabel()
	 */
	@Override
	public boolean isLabel() {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.interfaces.IParameter#allowsTooltip()
	 */
	@Override
	public boolean allowsTooltip() {
		return true;
	}

}
