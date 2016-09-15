/*********************************************************************************************
 *
 *
 * 'StatementDescription.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gaml.descriptions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.ecore.EObject;

import msi.gama.common.interfaces.IGamlIssue;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.util.GAML;
import msi.gama.util.TOrderedHashMap;
import msi.gaml.compilation.ISymbol;
import msi.gaml.descriptions.SymbolSerializer.StatementSerializer;
import msi.gaml.expressions.IExpression;
import msi.gaml.expressions.IOperator;
import msi.gaml.expressions.IVarExpression;
import msi.gaml.expressions.SpeciesConstantExpression;
import msi.gaml.factories.ChildrenProvider;
import msi.gaml.factories.DescriptionFactory;
import msi.gaml.statements.Arguments;
import msi.gaml.statements.DoStatement;
import msi.gaml.statements.Facets;
import msi.gaml.types.GamaType;
import msi.gaml.types.IType;
import msi.gaml.types.Types;

/**
 * Written by drogoul Modified on 10 févr. 2010
 *
 * @todo Description
 *
 */

public class StatementDescription extends SymbolDescription {

	protected TOrderedHashMap<String, StatementDescription> args;

	private static int COMMAND_INDEX = 0;

	protected IDescription previousDescription;
	/* If the statement makes reference to another species */
	protected SpeciesDescription denotedSpecies;
	private boolean denotedSpeciesComputed;
	private final boolean isAbstract;

	public StatementDescription(final String keyword, final IDescription superDesc, final ChildrenProvider cp,
			final boolean hasArgs, final EObject source, final Facets facets) {
		super(keyword, superDesc, cp, source, facets);
		isAbstract = computeAbstract();
		if (hasArgs) {
			collectArgs();
		}
	}

	private boolean computeAbstract() {
		final boolean result = TRUE.equals(getLitteral(VIRTUAL));
		removeFacets(VIRTUAL);
		return result;
	}

	@Override
	protected StatementSerializer createSerializer() {
		return StatementSerializer.getInstance();
	}

	@Override
	public void dispose() {
		if (isBuiltIn()) {
			return;
		}
		super.dispose();
		args = null;
		previousDescription = null;
		denotedSpecies = null;

	}

	@Override
	public IDescription addChild(final IDescription child) {
		final IDescription d = super.addChild(child);
		if (d != null)
			if (child.getKeyword().equals(ARG)) {
				putArg(child.getName(), (StatementDescription) child);
				return null;
			}
		return d;
	}

	private void collectArgs() {
		explodeArgs();
		exploreArgs();
	}

	private void putArg(final String name, final StatementDescription sd) {
		if (args == null)
			args = new TOrderedHashMap();
		args.put(name, sd);
	}

	// Only for "do". Explore the facets that may play the role of arguments
	private void exploreArgs() {
		if (!getKeyword().equals(DO)) {
			return;
		}
		// final List<String> removed = new ArrayList();
		visitFacets(new FacetVisitor() {

			@Override
			public final boolean visit(final String facet, final IExpressionDescription b) {
				if (!DoStatement.DO_FACETS.contains(facet)) {
					putArg(facet, createArg(facet, b));
				}
				return true;
			}
		});
	}

	private StatementDescription createArg(final String n, final IExpressionDescription v) {
		final Facets f = new Facets(NAME, n);
		f.put(VALUE, v);
		final StatementDescription a = (StatementDescription) DescriptionFactory.create(ARG, this,
				ChildrenProvider.NONE, f);
		return a;
	}

	// Transforms the arguments passed with the "with:" facets into "arg"
	// statements
	private void explodeArgs() {
		if (getKeyword().equals(ACTION) || getKeyword().equals(PRIMITIVE)) {
			return;
		}
		for (final Map.Entry<String, IExpressionDescription> arg : msi.gama.util.GAML.getExpressionFactory()
				.createArgumentMap(getAction(), getFacet(WITH), this).entrySet()) {
			final String name = arg.getKey();
			putArg(name, createArg(name, arg.getValue()));
		}
		removeFacets(WITH);
	}

	private StatementDescription getAction() {
		final String actionName = getLitteral(IKeyword.ACTION);
		if (actionName == null) {
			return null;
		}
		final TypeDescription declPlace = (TypeDescription) getDescriptionDeclaringAction(actionName);
		StatementDescription executer = null;
		if (declPlace != null) {
			executer = declPlace.getAction(actionName);
		}
		return executer;
	}

	@Override
	public StatementDescription copy(final IDescription into) {
		final List<IDescription> children = new ArrayList();

		if (args != null) {
			for (final IDescription child : args.values()) {
				children.add(child.copy(into));
			}
		}
		final StatementDescription desc = new StatementDescription(getKeyword(), into, new ChildrenProvider(children),
				args != null, element, getFacetsCopy());
		desc.originName = getOriginName();
		return desc;
	}

	@Override
	public boolean manipulatesVar(final String name) {
		if (getKeyword().equals(EQUATION)) {
			final Iterable<IDescription> equations = getChildrenWithKeyword(EQUATION_OP);
			for (final IDescription equation : equations) {
				final IExpressionDescription desc = equation.getFacet(EQUATION_LEFT);
				desc.compile(equation);
				final IExpression exp = desc.getExpression();
				if (exp instanceof IOperator) {
					final IOperator op = (IOperator) exp;
					if (op.arg(0).getName().equals(name))
						return true;
					if (op.arg(1) != null && op.arg(1).getName().equals(name))
						return true;
				}
			}
		}
		return false;
	}

	public boolean verifyArgs(final IDescription caller, final Arguments names) {

		if (args == null && names.isEmpty()) {
			return true;
		}
		final Set<String> allArgs = args == null ? Collections.EMPTY_SET : args.keySet();
		if (caller.getKeyword().equals(DO)) {
			// If the names were not known at the time of the creation of the
			// caller, only the order
			if (names.containsKey("0")) {
				int index = 0;
				for (final String name : allArgs) {
					final String key = String.valueOf(index++);
					final IExpressionDescription old = names.get(key);
					if (old != null) {
						names.put(name, old);
						names.remove(key);
					}
				}
			}
		}

		// We compute the list of mandatory args
		final List<String> mandatoryArgs = new ArrayList();
		if (args != null) {
			for (final IDescription c : args.values()) {
				final String n = c.getName();
				if (c.hasFacet(DEFAULT)) {
					// AD: we compile the default (which is, otherwise, not
					// computed before validation
					c.getFacet(DEFAULT).compile(this);
					continue;
				}
				if (c.hasFacet(OPTIONAL) && c.getFacet(OPTIONAL).equalsString(FALSE) || !c.hasFacet(OPTIONAL)) {
					mandatoryArgs.add(n);
				}
			}
		}
		// If one is missing in the arguments passed, we raise an error
		// (except for primitives for the moment)
		// if ( !getKeyword().equals(PRIMITIVE) ) {
		// AD: Change in the policy regarding primitives; if it is not stated,
		// the arguments are now considered as
		// optional.
		for (final String arg : mandatoryArgs) {
			if (!names.containsKey(arg)) {
				caller.error(
						"Missing argument " + arg + " in call to " + getName() + ". Arguments passed are : " + names,
						IGamlIssue.MISSING_ARGUMENT, caller.getUnderlyingElement(null), new String[] { arg });
				return false;
			}
		}
		// }
		for (final Map.Entry<String, IExpressionDescription> arg : names.entrySet()) {
			// A null value indicates a previous compilation error in the
			// arguments
			if (arg != null) {
				final String name = arg.getKey();
				if (!allArgs.contains(name)) {
					caller.error("Unknown argument " + name + " in call to " + getName(), IGamlIssue.UNKNOWN_ARGUMENT,
							arg.getValue().getTarget(), new String[] { arg.getKey() });
					return false;
				} else if (arg.getValue() != null && arg.getValue().getExpression() != null) {
					final IType formalType = args.get(name).getType();
					final IType callerType = arg.getValue().getExpression().getType();
					if (Types.intFloatCase(formalType, callerType)) {
						caller.warning("The argument " + name + " (of type " + callerType + ") will be casted to "
								+ formalType, IGamlIssue.WRONG_TYPE, arg.getValue().getTarget());
					} else {
						boolean accepted = formalType == Types.NO_TYPE || callerType.isTranslatableInto(formalType);
						accepted = accepted || callerType == Types.NO_TYPE && formalType.getDefault() == null;
						if (!accepted) {
							caller.error("The type of argument " + name + " should be " + formalType,
									IGamlIssue.WRONG_TYPE);
							return false;
						}
					}
				}
			}
		}
		return true;
	}

	public boolean verifyArgs(final String actionName, final Arguments args) {
		final StatementDescription executer = getAction();
		if (executer == null) {
			return false;
		}
		return executer.verifyArgs(this, args);
	}

	public Collection<StatementDescription> getArgs() {
		return args == null ? Collections.EMPTY_SET : args.values();
	}

	public boolean hasArgs() {
		return args != null;

	}

	public boolean containsArg(final String s) {
		if (args == null) {
			return false;
		}
		return args.containsKey(s);
	}

	@Override
	public String getName() {
		String s = super.getName();
		if (s == null) {
			// Special case for aspects
			if (getKeyword().equals(ASPECT)) {
				s = DEFAULT;
			} else {
				if (getKeyword().equals(REFLEX)) {
					warning("Reflexes should be named", IGamlIssue.MISSING_NAME, getUnderlyingElement(null));
				}
				s = INTERNAL + getKeyword() + String.valueOf(COMMAND_INDEX++);
			}
			setName(s);
		}
		return s;
	}

	@Override
	public String toString() {
		return getKeyword() + " " + getName();
	}

	/**
	 * @return
	 */
	public List<String> getArgNames() {
		return args == null ? Collections.EMPTY_LIST : new ArrayList(args.keySet());
	}

	@Override
	public String getTitle() {
		final String kw = getKeyword();
		// kw = Character.toUpperCase(kw.charAt(0)) + kw.substring(1);
		String name = getName();
		if (name.contains(INTERNAL)) {
			name = getLitteral(ACTION);
			if (name == null) {
				name = "statement";
			}
		}
		String in = "";
		if (getMeta().isTopLevel()) {
			final IDescription d = getEnclosingDescription();
			if (d == null) {
				in = " defined in " + getOriginName();
			} else {
				in = " of " + d.getTitle();
			}
		}
		return kw + " " + getName() + " " + in;
	}

	/**
	 * @return
	 */
	public boolean isAbstract() {
		return isAbstract;
	}

	public void collectChildren(final String keyword, final Set<StatementDescription> returns) {

		visitChildren(new DescriptionVisitor() {

			@Override
			public boolean visit(final IDescription desc) {
				if (desc.getKeyword().equals(keyword)) {
					returns.add((StatementDescription) desc);
				}
				((StatementDescription) desc).visitChildren(this);
				return true;
			}
		});

	}

	@Override
	public void setEnclosingDescription(final IDescription desc) {
		previousDescription = getEnclosingDescription();
		super.setEnclosingDescription(desc);
	}

	@Override
	public ModelDescription getModelDescription() {
		ModelDescription result = super.getModelDescription();
		if (result == null && previousDescription != null) {
			result = previousDescription.getModelDescription();
		}
		return result;
	}

	@Override
	public IDescription getDescriptionDeclaringVar(final String name) {
		IDescription result = super.getDescriptionDeclaringVar(name);
		if (result == null && previousDescription != null) {
			result = previousDescription.getDescriptionDeclaringVar(name);
		}
		return result;
	}

	@Override
	public IDescription getDescriptionDeclaringAction(final String name) {
		IDescription result = super.getDescriptionDeclaringAction(name);
		if (result == null && previousDescription != null) {
			result = previousDescription.getDescriptionDeclaringAction(name);
		}
		return result;
	}

	@Override
	public StatementDescription validate() {
		computeSpecies();
		return (StatementDescription) super.validate();
	}

	@Override
	public boolean validateChildren() {
		if (hasArgs()) {
			if (validateArgs() == null)
				return false;
		}

		IDescription previousEnclosingDescription = null;
		try {
			if (getMeta().isRemoteContext()) {
				if (denotedSpecies != null) {
					final SpeciesDescription s = getSpeciesContext();
					if (s != null) {
						final IType t = s.getType();
						addTemp(this, MYSELF, t);
						previousEnclosingDescription = getEnclosingDescription();
						setEnclosingDescription(denotedSpecies);

						// FIXME ===> Model Description is lost if we are
						// dealing
						// with a built-in species !
					}
				}
			}
			return super.validateChildren();
		} finally {
			if (previousEnclosingDescription != null) {
				setEnclosingDescription(previousEnclosingDescription);
			}
		}
	}

	@Override
	public List<? extends ISymbol> compileChildren() {
		if (getMeta().isRemoteContext()) {
			if (denotedSpecies != null) {
				final IType t = getSpeciesContext().getType();
				addTemp(this, MYSELF, t);
				setEnclosingDescription(denotedSpecies);
			}
		}
		return super.compileChildren();
	}

	public SpeciesDescription computeSpecies() {
		if (denotedSpeciesComputed) {
			return denotedSpecies;
		}
		denotedSpeciesComputed = true;

		// TODO is there a way to extract the species from a constant expression
		// (like
		// species("ant")) ? cf. Issue 145
		final IExpressionDescription ed = getFacet(SPECIES, AS, TARGET, ON);
		if (ed == null) {
			return null;
		}
		IExpression facet = ed.getExpression();
		// We try to compute as much as possible, for example if the facet is
		// not compiled yet (see Issue 618)
		if (facet == null) {
			facet = GAML.getExpressionFactory().createExpr(ed, this);
		}

		if (facet == null) {
			return null;
		}
		final IType t = facet.getType();
		SpeciesDescription result = null;
		if (t.id() == IType.SPECIES && facet instanceof SpeciesConstantExpression) {
			result = facet.getType().getContentType().getSpecies();// getSpeciesDescription(facet.literalValue());
		} else if (t.id() == IType.STRING && facet.isConst()) {
			result = getSpeciesDescription(facet.literalValue());
		} else if (t.isAgentType()) {
			result = t.getSpecies();
		} else {
			result = facet.getType().getContentType().getSpecies();
		}
		denotedSpecies = result;
		return result;

	}

	/**
	 * Cannot add temporary variables to statements without children
	 * 
	 * @param declaration
	 * @param name
	 * @param type
	 * @return
	 */
	public IExpression addTemp(final IDescription declaration, final String name, final IType type) {
		return null;
	}

	public Arguments validateArgs() {
		final Arguments ca = new Arguments();
		final String keyword = getKeyword();
		final boolean isCalling = keyword.equals(CREATE) || keyword.equals(DO) || keyword.equals(PRIMITIVE);
		for (final IDescription sd : getArgs()) {
			final String name = sd.getName();
			IExpression e = null;
			final IDescription superDesc = getEnclosingDescription();
			final IExpressionDescription ed = sd.getFacet(VALUE, DEFAULT);
			if (ed != null) {
				e = ed.compile(superDesc);
			}

			ca.put(name, e);
			if (!isCalling) {
				final IType type = sd.getType();
				addTemp(this, name, type);
			}

		}
		if (keyword.equals(IKeyword.DO)) {
			verifyArgs(getLitteral(IKeyword.ACTION), ca);
		} else if (keyword.equals(IKeyword.CREATE)) {
			verifyInits(ca);
		}
		return ca;

	}

	private void verifyInits(final Arguments ca) {
		final Collection<StatementDescription> args = getArgs();
		if (denotedSpecies == null) {
			if (!args.isEmpty()) {
				warning("Impossible to verify the validity of the arguments. Use them at your own risk ! (and don't complain about exceptions)",
						IGamlIssue.UNKNOWN_ARGUMENT);
			}
			return;
		}
		for (final StatementDescription arg : args) {
			final String name = arg.getName();
			// hqnghi check attribute is not exist in both main model and
			// micro-model
			if (!denotedSpecies.hasAttribute(name) && denotedSpecies instanceof ExperimentDescription
					&& !denotedSpecies.getModelDescription().hasAttribute(name)) {
				// end-hqnghi
				error("Attribute " + name + " does not exist in species " + denotedSpecies.getName(),
						IGamlIssue.UNKNOWN_ARGUMENT, arg.getFacet(VALUE).getTarget(), (String[]) null);
			} else {
				IType initType = Types.NO_TYPE;
				IType varType = Types.NO_TYPE;
				final VariableDescription vd = denotedSpecies.getAttribute(name);
				if (vd != null) {
					varType = vd.getType();
				}
				final IExpressionDescription ed = ca.get(name);
				if (ed != null) {
					final IExpression expr = ed.getExpression();
					if (expr != null) {
						initType = expr.getType();
					}
				}
				if (varType != Types.NO_TYPE && !initType.isTranslatableInto(varType)) {
					warning("The type of attribute " + name + " should be " + varType, IGamlIssue.SHOULD_CAST,
							arg.getFacet(VALUE).getTarget(), varType.toString());
				}
			}

		}
	}

	/**
	 * Method getChildren()
	 * 
	 * @see msi.gaml.descriptions.SymbolDescription#getChildren()
	 */
	// @Override
	// public List<IDescription> getChildren() {
	// return Collections.EMPTY_LIST;
	// }

	/**
	 * @return
	 */
	public boolean isBreakable() {
		return getMeta().isBreakable();
	}
	//
	// @Override
	// public List<IDescription> getOwnChildren() {
	// return getChildren();
	// }

	@Override
	protected IExpression createVarWithTypes(final String tag) {

		compileTypeProviderFacets();

		// Definition of the type
		IType t = super.getType();

		if (t == Types.NO_TYPE) {
			final String keyword = getKeyword();
			if (keyword.equals(CREATE) || keyword.equals(CAPTURE) || keyword.equals(RELEASE)) {
				t = Types.LIST;
			} else if (hasFacet(VALUE)) {
				final IExpression value = getFacetExpr(VALUE);
				if (value != null) {
					t = value.getType();
				}
			} else if (hasFacet(OVER)) {
				final IExpression expr = getFacetExpr(OVER);
				if (expr != null) {
					t = expr.getType().getContentType();
				}
			} else if (hasFacet(FROM) && hasFacet(TO)) {
				final IExpression expr = getFacetExpr(FROM);
				if (expr != null) {
					t = expr.getType();
				}
			}
		}
		IType ct = t.getContentType();
		IType kt = t.getKeyType();
		// Definition of the content type and key type
		if (hasFacet(AS)) {
			ct = getTypeDenotedByFacet(AS);
		} else if (hasFacet(SPECIES)) {
			final IExpression expr = getFacetExpr(SPECIES);
			if (expr != null) {
				ct = expr.getType().getContentType();
				kt = expr.getType().getKeyType();
			}
		}
		// Last chance: grab the content and key from the value
		// TODO Verify: maybe useless as already done above in getType()
		final boolean isContainerWithNoContentsType = t.isContainer() && ct == Types.NO_TYPE;
		final boolean isContainerWithNoKeyType = t.isContainer() && kt == Types.NO_TYPE;
		final boolean isSpeciesWithAgentType = t.id() == IType.SPECIES && ct.id() == IType.AGENT;
		if (isContainerWithNoContentsType || isContainerWithNoKeyType || isSpeciesWithAgentType) {
			final IExpression value = getFacetExpr(VALUE, DEFAULT);
			if (value != null) {
				final IType tt = t.typeIfCasting(value);
				if (isContainerWithNoContentsType || isSpeciesWithAgentType) {
					ct = tt.getContentType();
				}
				if (isContainerWithNoKeyType) {
					kt = tt.getKeyType();
				}
			}
		}

		return addNewTempIfNecessary(tag, GamaType.from(t, kt, ct));

	}

	public IVarExpression addNewTempIfNecessary(final String facetName, final IType type) {
		final String varName = getLitteral(facetName);

		if (getKeyword().equals(LOOP) && facetName.equals(NAME)) {
			// Case of loops: the variable is inside the loop (not outside)
			return (IVarExpression) addTemp(this, varName, type);
		}

		final IDescription sup = getEnclosingDescription();
		if (!(sup instanceof StatementDescription)) {
			error("Impossible to return " + getLitteral(facetName), IGamlIssue.GENERAL);
			return null;
		}
		return (IVarExpression) ((StatementDescription) sup).addTemp(this, varName, type);
	}

	@Override
	public boolean visitChildren(final DescriptionVisitor visitor) {
		return true;
	}

	@Override
	public boolean visitOwnChildren(final DescriptionVisitor visitor) {
		return true;
	}

}
