/*********************************************************************************************
 *
 *
 * 'Variable.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gaml.variables;

import java.util.Arrays;
import java.util.List;

import com.google.common.base.Objects;

import msi.gama.common.interfaces.IGamlIssue;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.common.interfaces.ISkill;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.GamlAnnotations.validator;
import msi.gama.precompiler.IConcept;
import msi.gama.precompiler.ISymbolKind;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.compilation.GamaHelper;
import msi.gaml.compilation.IDescriptionValidator;
import msi.gaml.compilation.ISymbol;
import msi.gaml.compilation.Symbol;
import msi.gaml.descriptions.ConstantExpressionDescription;
import msi.gaml.descriptions.IDescription;
import msi.gaml.descriptions.IExpressionDescription;
import msi.gaml.descriptions.VariableDescription;
import msi.gaml.expressions.IExpression;
import msi.gaml.expressions.ListExpression;
import msi.gaml.operators.Cast;
import msi.gaml.species.AbstractSpecies;
import msi.gaml.statements.IExecutable;
import msi.gaml.types.GamaListType;
import msi.gaml.types.IType;
import msi.gaml.types.Types;

/**
 * The Class Var.
 *
 *
 * FIXME FOR THE MOMENT SPECIES_WIDE CONSTANTS ARE NOT CONSIDERED (TOO MANY
 * THINGS TO CONSIDER AND POSSIBILITIES TO MAKE FALSE POSITIVE)
 */
@facets(value = {
		@facet(name = IKeyword.NAME, type = IType.NEW_VAR_ID, optional = false, doc = @doc("The name of the attribute")),
		@facet(name = IKeyword.TYPE, type = IType.TYPE_ID, optional = true, doc = {
				@doc("The type of this attribute. Can be combined with facets 'of' and 'index' to describe container types") }),
		@facet(name = IKeyword.OF, type = IType.TYPE_ID, optional = true, doc = {
				@doc("The type of the elements contained in the type of this attribute if it is a container type") }),
		@facet(name = IKeyword.INDEX, type = IType.TYPE_ID, optional = true, doc = {
				@doc("The type of the index used to retrieve elements if the type of the attribute is a container type") }),
		@facet(name = IKeyword.INIT,
				// AD 02/16 TODO Allow to declare ITypeProvider.OWNER_TYPE here
				type = IType.NONE, optional = true, doc = @doc("The initial value of the attribute")),
		@facet(name = IKeyword.VALUE,
				// AD 02/16 TODO Allow to declare ITypeProvider.OWNER_TYPE here
				type = IType.NONE, optional = true, doc = @doc(value = "", deprecated = "Use 'update' instead")),
		@facet(name = IKeyword.UPDATE,
				// AD 02/16 TODO Allow to declare ITypeProvider.OWNER_TYPE here
				type = IType.NONE, optional = true, doc = @doc("An expression that will be evaluated each cycle to compute a new value for the attribute")),
		@facet(name = IKeyword.ON_CHANGE, type = IType.NONE, optional = true, doc = @doc("Provides a block of statements that will be executed whenever the value of the attribute changes")),

		@facet(name = IKeyword.FUNCTION,
				// AD 02/16 TODO Allow to declare ITypeProvider.OWNER_TYPE here
				type = IType.NONE, optional = true, doc = @doc("Used to specify an expression that will be evaluated each time the attribute is accessed. This facet is incompatible with both 'init:' and 'update:'")),
		@facet(name = IKeyword.CONST, type = IType.BOOL, optional = true, doc = @doc("Indicates whether this attribute can be subsequently modified or not")),
		@facet(name = IKeyword.CATEGORY, type = IType.LABEL, optional = true, doc = @doc("Soon to be deprecated. Declare the parameter in an experiment instead")),
		@facet(name = IKeyword.PARAMETER, type = IType.LABEL, optional = true, doc = @doc("Soon to be deprecated. Declare the parameter in an experiment instead")),
		@facet(name = IKeyword.AMONG, type = IType.LIST, optional = true, doc = @doc("A list of constant values among which the attribute can take its value")) }, omissible = IKeyword.NAME)
@symbol(kind = ISymbolKind.Variable.REGULAR, with_sequence = false, concept = { IConcept.ATTRIBUTE })
@inside(kinds = { ISymbolKind.SPECIES, ISymbolKind.EXPERIMENT, ISymbolKind.MODEL })
@doc("Allows to declare an attribute of a species or an experiment")
@validator(msi.gaml.variables.Variable.VarValidator.class)
@SuppressWarnings({ "rawtypes" })
public class Variable extends Symbol implements IVariable {

	public static class VarValidator implements IDescriptionValidator {

		// public static List<String> valueFacetsList = Arrays.asList(VALUE,
		// INIT, FUNCTION, UPDATE, MIN, MAX);
		public static List<String> assignmentFacets = Arrays.asList(VALUE, INIT, FUNCTION, UPDATE, MIN, MAX);

		/**
		 * Method validate()
		 * 
		 * @see msi.gaml.compilation.IDescriptionValidator#validate(msi.gaml.descriptions.IDescription)
		 */
		@Override
		public void validate(final IDescription vd) {
			final VariableDescription cd = (VariableDescription) vd;
			final boolean isParameter = cd.isExperimentParameter();
			final String name = cd.getName();
			// Verifying that the name is not null
			if (name == null) {
				cd.error("The attribute name is missing", IGamlIssue.MISSING_NAME);
				return;
			}

			if (!isParameter) {
				// Verifying that the name is not a type
				final IType t = cd.getEnclosingDescription().getTypeNamed(name);
				if (t != Types.NO_TYPE && !t.isAgentType()) {
					cd.error(name + " is a type name. It cannot be used as an attribute name", IGamlIssue.IS_A_TYPE,
							NAME, name);
					return;
				}
				// Verifying that the name is not reserved
				if (RESERVED.contains(name)) {
					cd.error(name + " is a reserved keyword. It cannot be used as an attribute name",
							IGamlIssue.IS_RESERVED, NAME, name);
					return;
				}
			}
			// The name is ok. Now verifying the logic of facets
			// Verifying that 'function' is not used in conjunction with other
			// "value" facets
			if (cd.hasFacet(FUNCTION)
					&& (cd.hasFacet(INIT) || cd.hasFacet(UPDATE) || cd.hasFacet(VALUE) || cd.hasFacet(ON_CHANGE))) {
				cd.error("A function cannot have an 'init', 'on_change' or 'update' facet", IGamlIssue.REMOVE_VALUE,
						FUNCTION);
				return;
			}
			// Verifying that a constant has not 'update' or 'function' facet
			// and is not a parameter
			if (TRUE.equals(cd.getLitteral(CONST))) {
				if (cd.hasFacet(VALUE) || cd.hasFacet(UPDATE)) {
					cd.warning("A constant attribute cannot have an update value (use init or <- instead)",
							IGamlIssue.REMOVE_CONST, UPDATE);
				} else if (cd.hasFacet(FUNCTION)) {
					cd.error("A function cannot be constant (use init or <- instead)", IGamlIssue.REMOVE_CONST,
							FUNCTION);
					return;
				} else if (cd.isParameter()) {
					cd.error("Parameter '" + cd.getParameterName() + "'  cannot be declared as constant ",
							IGamlIssue.REMOVE_CONST);
					return;
				} else if (cd.hasFacet(ON_CHANGE)) {
					cd.warning("A constant attribute cannot declare an on_change facet", IGamlIssue.REMOVE_CONST,
							ON_CHANGE);
				}
			}
			if (cd.isParameter()) {
				assertCanBeParameter(cd);
			} else {
				assertValueFacetsTypes(cd, cd.getType());
			}
			assertAssignmentFacetsTypes(cd);
			assertAmongValues(cd);
		}

		public void assertAmongValues(final VariableDescription vd) {
			// if (vd.isParameter() && vd.getSpeciesContext().isExperiment()
			// && ((ExperimentDescription) vd.getSpeciesContext()).isBatch())
			// return;
			final IExpression amongExpression = vd.getFacetExpr(AMONG);
			final IExpression initExpression = vd.getFacetExpr(INIT);
			if (amongExpression == null || initExpression == null)
				return;
			if (!(amongExpression instanceof ListExpression) || !initExpression.isConst())
				return;
			final ListExpression list = (ListExpression) amongExpression;
			final Object init = initExpression.value(null);
			if (!list.containsValue(init)) {
				vd.warning(
						"The initial value of " + vd.getName()
								+ " does not belong to the list of possible values. It will be initialized to "
								+ list.getElements()[0].serialize(true) + " instead.",
						IGamlIssue.WRONG_VALUE, IKeyword.AMONG);
			}

		}

		public void assertAssignmentFacetsTypes(final VariableDescription vd) {
			for (final String s : assignmentFacets) {
				Assert.typesAreCompatibleForAssignment(vd, vd.getName(),
						vd.getType(), /* vd.getContentType(), */
						vd.getFacet(s));
			}
		}

		public void assertValueFacetsTypes(final VariableDescription vd, final IType<?> vType) {

			// final IType type = null;
			// final String firstValueFacet = null;
			final IExpression amongExpression = vd.getFacetExpr(AMONG);
			if (amongExpression != null && !vType.isAssignableFrom(amongExpression.getType().getContentType())) {
				vd.error("Variable " + vd.getName() + " of type " + vType + " cannot be chosen among "
						+ amongExpression.serialize(false), IGamlIssue.NOT_AMONG, AMONG);
				return;
			}
		}

		public void assertCanBeParameter(final VariableDescription cd) {
			if (PARAMETER.equals(
					cd.getKeyword()) /* facets.equals(KEYWORD, PARAMETER) */) {
				final String varName = cd.getLitteral(VAR);
				final VariableDescription targetedVar = cd.getModelDescription().getAttribute(varName);
				if (targetedVar == null) {
					final String p = "Parameter '" + cd.getParameterName() + "' ";
					cd.error(p + "cannot refer to the non-global variable " + varName, IGamlIssue.UNKNOWN_VAR,
							IKeyword.VAR);
					return;
				}
				if (!cd.getType().equals(Types.NO_TYPE) && cd.getType().id() != targetedVar.getType().id()) {
					final String p = "Parameter '" + cd.getParameterName() + "' ";
					cd.error(p + "type must be the same as that of " + varName, IGamlIssue.UNMATCHED_TYPES,
							IKeyword.TYPE);
					return;
				}
				assertValueFacetsTypes(cd, targetedVar.getType());
			}
			assertValueFacetsTypes(cd, cd.getType());
			final IExpression min = cd.getFacetExpr(MIN);
			if (min != null && !min.isConst()) {
				final String p = "Parameter '" + cd.getParameterName() + "' ";
				cd.error(p + " min value must be constant", IGamlIssue.NOT_CONST, MIN);
				return;
			}
			final IExpression max = cd.getFacetExpr(MAX);
			if (max != null && !max.isConst()) {
				final String p = "Parameter '" + cd.getParameterName() + "' ";
				cd.error(p + " max value must be constant", IGamlIssue.NOT_CONST, MAX);
				return;
			}
			final IExpression init = cd.getFacetExpr(INIT);

			if (init == null) {
				final String p = "Parameter '" + cd.getParameterName() + "' ";
				cd.error(p + " must have an initial value", IGamlIssue.NO_INIT, cd.getUnderlyingElement(null),
						cd.getType().toString());
				return;
			}
			// AD 15/04/14: special case for files
			// AD 17/06/16 The restriction is temporarily removed
			// if (!init.isConst() && init.getType().getType().id() !=
			// IType.FILE) {
			// final String p = "Parameter '" + cd.getParameterName() + "' ";
			// cd.error(p + "initial value must be constant",
			// IGamlIssue.NOT_CONST, INIT);
			// return;
			// }
			if (cd.hasFacet(UPDATE) || cd.hasFacet(VALUE) || cd.hasFacet(FUNCTION)) {
				final String p = "Parameter '" + cd.getParameterName() + "' ";
				cd.error(p + "cannot have an 'update', 'value' or 'function' facet", IGamlIssue.REMOVE_VALUE);
			}
		}

	}

	protected IExpression updateExpression, initExpression, amongExpression, functionExpression, onChangeExpression;
	protected IType type/* , contentType */;
	protected boolean isNotModifiable /* , doUpdate */;
	// private final int definitionOrder;
	public GamaHelper getter, initer, setter;
	private IExecutable on_changer;
	protected String /* gName, sName, iName, */ pName, cName;
	protected ISkill gSkill/* , iSkill */, sSkill;

	public Variable(final IDescription sd) {
		super(sd);
		final VariableDescription desc = (VariableDescription) sd;
		// doUpdate = true;
		setName(sd.getName());
		pName = desc.getParameterName();
		cName = getLiteral(IKeyword.CATEGORY, null);
		updateExpression = getFacet(IKeyword.VALUE, IKeyword.UPDATE);
		functionExpression = getFacet(IKeyword.FUNCTION);
		initExpression = getFacet(IKeyword.INIT);
		amongExpression = getFacet(IKeyword.AMONG);
		onChangeExpression = getFacet(IKeyword.ON_CHANGE);
		isNotModifiable = desc.isNotModifiable();
		type = desc.getType();
		// contentType = desc.getContentType();
		// definitionOrder = desc.getDefinitionOrder();
	}

	private void buildHelpers(final AbstractSpecies species) {
		getter = getDescription().getGetter();
		if (getter != null) {
			gSkill = species.getSkillInstanceFor(getter.getSkillClass());
		}
		initer = getDescription().getIniter();
		setter = getDescription().getSetter();
		if (setter != null) {
			sSkill = species.getSkillInstanceFor(setter.getSkillClass());
		}

	}

	protected Object coerce(final IAgent agent, final IScope scope, final Object v) throws GamaRuntimeException {
		return type.cast(scope, v, null, false);
	}

	// private void computeSpeciesConst(final IScope scope) {
	// if ( !isNotModifiable ) { return; }
	// if ( initExpression != null && !initExpression.isConst() ) { return; }
	// if ( updateExpression != null && !updateExpression.isConst() ) { return;
	// }
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

	// @Override
	// public String toGaml() {
	// return getName();
	// }

	@Override
	public String toString() {
		String result = isConst() ? IKeyword.CONST : IKeyword.VAR;
		result += " " + type.toString() + "[" + getName() + "]";
		return result;
	}

	@Override
	public void setValue(final IScope scope, final Object initial) {
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
			scope.setCurrentSymbol(this);
			if (v != null) {
				_setVal(a, scope, v);
			} else if (initExpression != null) {
				_setVal(a, scope, scope.evaluate(initExpression, a));
			} else if (initer != null) {
				final Object val = initer.run(scope, a, gSkill == null ? a : gSkill);
				_setVal(a, scope, val);
			} else {
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

	// @Override
	// public Integer getDefinitionOrder() {
	// return definitionOrder;
	// }

	@Override
	public void setChildren(final Iterable<? extends ISymbol> children) {
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(final String name) {
		this.name = name;
	}

	private static Object[] JunkResults = new Object[1];

	@Override
	public final void setVal(final IScope scope, final IAgent agent, final Object v) throws GamaRuntimeException {
		if (isNotModifiable) {
			return;
		}
		final Object oldValue = onChangeExpression == null ? null : value(scope, agent);
		_setVal(agent, scope, v);
		if (onChangeExpression != null && !Objects.equal(oldValue, v)) {
			if (on_changer == null) {
				on_changer = agent.getSpecies().getAction(Cast.asString(scope, onChangeExpression.value(scope)));
			}
			scope.execute(on_changer, agent, null, JunkResults);
		}
	}

	protected void _setVal(final IAgent agent, final IScope scope, final Object v) throws GamaRuntimeException {
		Object val;
		val = coerce(agent, scope, v);
		val = checkAmong(agent, scope, val);
		if (setter != null) {
			setter.run(scope, agent, sSkill == null ? agent : sSkill, val);
		} else {
			agent.setAttribute(name, val);
		}
	}

	protected Object checkAmong(final IAgent agent, final IScope scope, final Object val) throws GamaRuntimeException {
		if (amongExpression == null) {
			return val;
		}
		final List among = Cast.asList(scope, scope.evaluate(amongExpression, agent));
		if (among == null) {
			return val;
		}
		if (among.contains(val)) {
			return val;
		}
		if (among.isEmpty()) {
			return null;
		}
		throw GamaRuntimeException.error("Value " + val + " is not included in the possible values of variable " + name,
				scope);
	}

	@Override
	public Object value(final IScope scope) throws GamaRuntimeException {
		return value(scope, scope.getAgent());
	}

	@Override
	public Object value(final IScope scope, final IAgent agent) throws GamaRuntimeException {
		if (getter != null) {
			return getter.run(scope, agent, gSkill == null ? agent : gSkill);
		}
		if (functionExpression != null) {
			return scope.evaluate(functionExpression, agent);
		}
		return agent.getAttribute(name);
	}

	@Override
	public Object getUpdatedValue(final IScope scope) {
		return updateExpression.value(scope);
	}

	@Override
	public Number getMinValue(final IScope scope) {
		return null;
	}

	@Override
	public Number getMaxValue(final IScope scope) {
		return null;
	}

	@Override
	public Number getStepValue(final IScope scope) {
		return null;
	}

	@Override
	public List getAmongValue(final IScope scope) {
		if (amongExpression == null) {
			return null;
		}
		// if (!amongExpression.isConst()) {
		// return null;
		// }
		try {
			return GamaListType.staticCast(scope, amongExpression.value(scope), null, false);
			// return Cast.as(amongExpression, IList.class, false);
		} catch (final GamaRuntimeException e) {
			return null;
		}
	}

	@Override
	public Object getInitialValue(final IScope scope) {
		if (initExpression != null /* && initExpression.isConst() */ ) {
			try {
				return initExpression.value(scope);
			} catch (final GamaRuntimeException e) {
				return null;
			}
		}
		return value(scope);
	}

	@Override
	public String getUnitLabel(final IScope scope) {
		return null;
	}

	@Override
	public void setUnitLabel(final String label) {
	}

	@Override
	public boolean isEditable() {
		return true;
	}

	/**
	 * Method isDefined()
	 * 
	 * @see msi.gama.kernel.experiment.IParameter#isDefined()
	 */
	@Override
	public boolean isDefined() {
		return true;
	}

	/**
	 * Method setDefined()
	 * 
	 * @see msi.gama.kernel.experiment.IParameter#setDefined(boolean)
	 */
	@Override
	public void setDefined(final boolean b) {
	}

	@Override
	public boolean acceptsSlider(final IScope scope) {
		// No facets are available to describe whether or not a slider should be
		// defined.

		return false;
	}

	@Override
	public void setEnclosing(final ISymbol enclosing) {
		if (enclosing instanceof AbstractSpecies)
			buildHelpers((AbstractSpecies) enclosing);
	}

}
