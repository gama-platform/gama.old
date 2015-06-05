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

import gnu.trove.procedure.TObjectObjectProcedure;
import java.util.*;
import msi.gama.common.interfaces.*;
import msi.gama.util.*;
import msi.gaml.compilation.ISymbol;
import msi.gaml.descriptions.SymbolSerializer.StatementSerializer;
import msi.gaml.expressions.*;
import msi.gaml.factories.*;
import msi.gaml.statements.*;
import msi.gaml.types.*;
import org.eclipse.emf.ecore.EObject;

/**
 * Written by drogoul Modified on 10 févr. 2010
 *
 * @todo Description
 *
 */

public class StatementDescription extends SymbolDescription {

	public static class StatementWithChildrenDescription extends StatementDescription {

		protected List<IDescription> children;

		public StatementWithChildrenDescription(final String keyword, final IDescription superDesc,
			final ChildrenProvider cp, final boolean hasScope, final boolean hasArgs, final EObject source,
			final Facets facets) {
			super(keyword, superDesc, cp, hasScope, hasArgs, source, facets);
		}

		@Override
		public List<IDescription> getChildren() {
			if ( children == null ) {
				children = new ArrayList();
			}
			return children;
		}

		@Override
		public void dispose() {
			if ( children != null ) {
				for ( IDescription c : children ) {
					c.dispose();
				}
				children.clear();
			}
		}

		@Override
		public IDescription addChild(final IDescription child) {
			getChildren().add(child);
			return super.addChild(child);
		}

		@Override
		public StatementDescription copy(final IDescription into) {
			List<IDescription> children = new ArrayList();
			for ( IDescription child : getChildren() ) {
				children.add(child.copy(into));
			}
			if ( args != null ) {
				for ( IDescription child : args.values() ) {
					children.add(child.copy(into));
				}
			}
			StatementDescription desc =
				new StatementWithChildrenDescription(getKeyword(), into, new ChildrenProvider(children), temps != null,
					args != null, element, facets.cleanCopy());
			desc.originName = originName;
			return desc;
		}

	}

	protected final Map<String, IVarExpression> temps;
	protected final Map<String, StatementDescription> args;

	private static int COMMAND_INDEX = 0;

	private IDescription previousDescription;

	public StatementDescription(final String keyword, final IDescription superDesc, final ChildrenProvider cp,
		final boolean hasScope, final boolean hasArgs, final EObject source, final Facets facets) {
		super(keyword, superDesc, cp, source, facets);
		temps = hasScope ? new TOrderedHashMap() : null;
		args = hasArgs ? new TOrderedHashMap() : null;
		if ( hasArgs ) {
			collectArgs();
		}
	}

	@Override
	protected StatementSerializer createSerializer() {
		return new StatementSerializer();
	}

	@Override
	public void dispose() {
		if ( /* isDisposed || */isBuiltIn() ) { return; }
		if ( temps != null ) {
			temps.clear();
		}
		if ( args != null ) {
			args.clear();
		}
		previousDescription = null;
		super.dispose();
		// isDisposed = true;
	}

	@Override
	public void copyTempsAbove() {
		IDescription d = getEnclosingDescription();
		while (d != null && d instanceof StatementDescription) {
			if ( ((StatementDescription) d).hasTemps() ) {
				temps.putAll(((StatementDescription) d).temps);
			}
			d = d.getEnclosingDescription();
		}
	}

	private void collectArgs() {
		for ( Iterator<IDescription> it = getChildren().iterator(); it.hasNext(); ) {
			IDescription c = it.next();
			if ( c.getKeyword().equals(ARG) ) {
				args.put(c.getName(), (StatementDescription) c);
				it.remove();
			}
		}
		explodeArgs();
		exploreArgs();
	}

	// Only for "do". Explore the facets that may play the role of arguments
	private void exploreArgs() {
		if ( !getKeyword().equals(DO) ) { return; }
		// final List<String> removed = new ArrayList();
		facets.forEachEntry(new TObjectObjectProcedure<String, IExpressionDescription>() {

			@Override
			public boolean execute(final String facet, final IExpressionDescription b) {
				if ( !DoStatement.DO_FACETS.contains(facet) ) {
					args.put(facet, createArg(facet, b));
					// removed.add(facet);
				}
				return true;
			}
		});
		// for ( String s : removed ) {
		// facets.remove(s);
		// }
	}

	private StatementDescription createArg(final String n, final IExpressionDescription v) {
		Facets f = new Facets(NAME, n);
		f.put(VALUE, v);
		StatementDescription a = (StatementDescription) DescriptionFactory.create(ARG, this, ChildrenProvider.NONE, f);
		return a;
	}

	// Transforms the arguments passed with the "with:" facets into "arg" statements
	private void explodeArgs() {
		if ( getKeyword().equals(ACTION) || getKeyword().equals(PRIMITIVE) ) { return; }
		for ( Map.Entry<String, IExpressionDescription> arg : msi.gama.util.GAML.getExpressionFactory()
			.createArgumentMap(getAction(), facets.get(WITH), this).entrySet() ) {
			String name = arg.getKey();
			args.put(name, createArg(name, arg.getValue()));
		}
		// FIXME We should not play with the facets like this. Commented for the moment unless it creates problems.
		// facets.remove(WITH);
	}

	private StatementDescription getAction() {
		String actionName = getFacets().getLabel(IKeyword.ACTION);
		if ( actionName == null ) { return null; }
		TypeDescription declPlace = (TypeDescription) getDescriptionDeclaringAction(actionName);
		StatementDescription executer = null;
		if ( declPlace != null ) {
			executer = declPlace.getAction(actionName);
		}
		return executer;
	}

	public IVarExpression addNewTempIfNecessary(final String facetName, final IType type) {
		String varName = facets.getLabel(facetName);
		if ( getKeyword().equals(LOOP) && facetName.equals(NAME) ) {
			// Case of loops: the variable is inside the loop (not outside)
			return (IVarExpression) addTemp(this, varName, type);
		}

		IDescription sup = getEnclosingDescription();
		if ( !(sup instanceof StatementDescription) ) {
			error("Impossible to return " + facets.getLabel(facetName), IGamlIssue.GENERAL);
			return null;
		}
		return (IVarExpression) ((StatementDescription) sup).addTemp(this, varName, type);
	}

	@Override
	public StatementDescription copy(final IDescription into) {
		List<IDescription> children = new ArrayList();
		for ( IDescription child : getChildren() ) {
			children.add(child.copy(into));
		}
		if ( args != null ) {
			for ( IDescription child : args.values() ) {
				children.add(child.copy(into));
			}
		}
		StatementDescription desc =
			new StatementDescription(getKeyword(), into, new ChildrenProvider(children), temps != null, args != null,
				element, facets.cleanCopy());
		desc.originName = originName;
		return desc;
	}

	@Override
	public boolean hasVar(final String name) {
		return temps != null && temps.containsKey(name);
	}

	@Override
	public IExpression addTemp(final IDescription declaration, final String name, final IType type) {
		// TODO Should separate validation from execution, here.

		if ( temps == null ) {
			if ( getEnclosingDescription() == null ) { return null; }
			if ( !(getEnclosingDescription() instanceof StatementDescription) ) { return null; }

			return ((StatementDescription) getEnclosingDescription()).addTemp(declaration, name, type);
		}
		String kw = getKeyword();
		String facet = kw == IKeyword.LET || kw == IKeyword.LOOP ? IKeyword.NAME : IKeyword.RETURNS;

		if ( temps.containsKey(name) && !name.equals(MYSELF) ) {
			declaration.warning("This declaration of " + name + " shadows a previous declaration",
				IGamlIssue.SHADOWS_NAME, facet);
		}
		SpeciesDescription sd = declaration.getSpeciesContext();
		ModelDescription md = declaration.getModelDescription();
		if ( sd != null && sd != md && sd.hasVar(name) ) {
			declaration.warning(
				"This declaration of " + name + " shadows the declaration of an attribute of " + sd.getName(),
				IGamlIssue.SHADOWS_NAME, facet);
		}
		if ( md != null && md.hasVar(name) ) {
			declaration.warning("This declaration of " + name + " shadows the declaration of a global attribute",
				IGamlIssue.SHADOWS_NAME, facet);
		}
		IExpression result =
			msi.gama.util.GAML.getExpressionFactory().createVar(name, type, false, IVarExpression.TEMP, this);
		temps.put(name, (IVarExpression) result);
		return result;
	}

	@Override
	public IExpression getVarExpr(final String name) {
		if ( temps != null ) { return temps.get(name); }
		return null;
	}

	public boolean verifyArgs(final IDescription caller, final Arguments names) {

		// GuiUtils.debug(this.toString() + " called by " + caller + " with " + names);
		// if ( args == null ) { return; }
		Set<String> allArgs = args == null ? Collections.EMPTY_SET : args.keySet();
		if ( caller.getKeyword().equals(DO) ) {
			// If the names were not known at the time of the creation of the caller, only the order
			if ( names.containsKey("0") ) {
				int index = 0;
				for ( String name : allArgs ) {
					String key = String.valueOf(index++);
					IExpressionDescription old = names.get(key);
					if ( old != null ) {
						names.put(name, old);
						names.remove(key);
					}
				}
			}
		}

		// We compute the list of mandatory args
		List<String> mandatoryArgs = new ArrayList();
		if ( args != null ) {
			for ( IDescription c : args.values() ) {
				String n = c.getName();
				Facets argFacets = c.getFacets();
				if ( argFacets.containsKey(DEFAULT) ) {
					// AD: we compile the default (which is, otherwise, not computed before validation
					argFacets.get(DEFAULT).compile(this);
					continue;
				}
				if ( c.getFacets().containsKey(OPTIONAL) && c.getFacets().get(OPTIONAL).equalsString(FALSE) ||
					!c.getFacets().containsKey(OPTIONAL) ) {
					mandatoryArgs.add(n);
				}
			}
		}
		// If one is missing in the arguments passed, we raise an error
		// (except for primitives for the moment)
		// if ( !getKeyword().equals(PRIMITIVE) ) {
		// AD: Change in the policy regarding primitives; if it is not stated, the arguments are now considered as
		// optional.
		for ( String arg : mandatoryArgs ) {
			if ( !names.containsKey(arg) ) {
				caller.error("Missing argument " + arg + " in call to " + getName() + ". Arguments passed are : " +
					names, IGamlIssue.MISSING_ARGUMENT, caller.getUnderlyingElement(null), new String[] { arg });
				return false;
			}
		}
		// }
		for ( Map.Entry<String, IExpressionDescription> arg : names.entrySet() ) {
			// A null value indicates a previous compilation error in the arguments
			if ( arg != null ) {
				String name = arg.getKey();
				if ( !allArgs.contains(name) ) {
					caller.error("Unknown argument " + name + " in call to " + getName(), IGamlIssue.UNKNOWN_ARGUMENT,
						arg.getValue().getTarget(), new String[] { arg.getKey() });
					return false;
				} else if ( arg.getValue() != null && arg.getValue().getExpression() != null ) {
					IType formalType = args.get(name).getType();
					IType callerType = arg.getValue().getExpression().getType();
					if ( Types.intFloatCase(formalType, callerType) ) {
						caller.warning("The argument " + name + " (of type " + callerType + ") will be casted to " +
							formalType, IGamlIssue.WRONG_TYPE, arg.getValue().getTarget());
					} else {
						boolean accepted = formalType == Types.NO_TYPE || callerType.isTranslatableInto(formalType);
						accepted = accepted || callerType == Types.NO_TYPE && formalType.getDefault() == null;
						if ( !accepted ) {
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
		StatementDescription executer = getAction();
		if ( executer == null ) { return false; }
		return executer.verifyArgs(this, args);
	}

	public Collection<StatementDescription> getArgs() {
		return args == null ? Collections.EMPTY_SET : args.values();
	}

	public boolean hasTemps() {
		return temps != null;
	}

	public boolean hasArgs() {
		return args != null;

	}

	public boolean containsArg(final String s) {
		if ( args == null ) { return false; }
		return args.containsKey(s);
	}

	@Override
	public String getName() {
		String s = super.getName();
		if ( s == null ) {
			// Special case for aspects
			if ( getKeyword().equals(ASPECT) ) {
				s = DEFAULT;
			} else {
				if ( getKeyword().equals(REFLEX) ) {
					warning("Reflexes should be named", IGamlIssue.MISSING_NAME, getUnderlyingElement(null));
				}
				s = INTERNAL + getKeyword() + String.valueOf(COMMAND_INDEX++);
			}
			facets.putAsLabel(NAME, s);
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
		String kw = getKeyword();
		// kw = Character.toUpperCase(kw.charAt(0)) + kw.substring(1);
		String name = getName();
		if ( name.contains(INTERNAL) ) {
			name = facets.getLabel(ACTION);
			if ( name == null ) {
				name = "statement";
			}
		}
		String in = "";
		if ( getMeta().isTopLevel() ) {
			IDescription d = getEnclosingDescription();
			if ( d == null ) {
				in = " defined in " + originName;
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
		return TRUE.equals(facets.getLabel(VIRTUAL));
		// return !getKeyword().equals(PRIMITIVE) && getChildren().isEmpty();
	}

	public void collectChildren(final String keyword, final Set<StatementDescription> returns) {
		if ( getKeyword().equals(keyword) ) {
			returns.add(this);
		} else/* if ( children != null ) */{
			for ( IDescription child : getChildren() ) {
				if ( child instanceof StatementDescription ) {
					((StatementDescription) child).collectChildren(keyword, returns);
				}
			}
		}
	}

	@Override
	public void setEnclosingDescription(final IDescription desc) {
		previousDescription = getEnclosingDescription();
		super.setEnclosingDescription(desc);
	}

	@Override
	public ModelDescription getModelDescription() {
		ModelDescription result = super.getModelDescription();
		if ( result == null && previousDescription != null ) {
			result = previousDescription.getModelDescription();
		}
		return result;
	}

	@Override
	public IDescription getDescriptionDeclaringVar(final String name) {
		IDescription result = super.getDescriptionDeclaringVar(name);
		if ( result == null && previousDescription != null ) {
			result = previousDescription.getDescriptionDeclaringVar(name);
		}
		return result;
	}

	@Override
	public IDescription getDescriptionDeclaringAction(final String name) {
		IDescription result = super.getDescriptionDeclaringAction(name);
		if ( result == null && previousDescription != null ) {
			result = previousDescription.getDescriptionDeclaringAction(name);
		}
		return result;
	}

	@Override
	public void validateChildren() {
		if ( hasArgs() ) {
			validateArgs();
		}
		IDescription previousEnclosingDescription = null;
		if ( getMeta().isRemoteContext() ) {
			final SpeciesDescription actualSpecies = computeSpecies();
			if ( actualSpecies != null ) {
				final SpeciesDescription s = getSpeciesContext();
				if ( s != null ) {
					final IType t = s.getType();
					addTemp(this, MYSELF, t);
					previousEnclosingDescription = getEnclosingDescription();
					setEnclosingDescription(actualSpecies);

					// FIXME ===> Model Description is lost if we are dealing with a built-in species !
				}
			}
		}
		super.validateChildren();
		if ( previousEnclosingDescription != null ) {
			setEnclosingDescription(previousEnclosingDescription);
		}
	}

	@Override
	public List<? extends ISymbol> compileChildren() {
		if ( getMeta().isRemoteContext() ) {
			final SpeciesDescription actualSpecies = computeSpecies();
			if ( actualSpecies != null ) {
				final IType t = getSpeciesContext().getType();
				addTemp(this, MYSELF, t);
				setEnclosingDescription(actualSpecies);
			}
		}
		return super.compileChildren();
	}

	public SpeciesDescription computeSpecies() {

		// TODO is there a way to extract the species from a constant expression (like
		// species("ant")) ? cf. Issue 145
		IExpressionDescription ed = facets.getDescr(SPECIES, AS, TARGET);
		if ( ed == null ) { return null; }
		IExpression facet = ed.getExpression();
		// We try to compute as much as possible, for example if the facet is not compiled yet (see Issue 618)
		if ( facet == null ) {
			facet = GAML.getExpressionFactory().createExpr(ed, this);
		}

		// final IExpression facet = facets.getExpr(SPECIES, facets.getExpr(AS, facets.getExpr(TARGET)));
		if ( facet == null ) { return null; }
		IType t = facet.getType();
		SpeciesDescription result = null;
		if ( t.id() == IType.SPECIES && facet instanceof SpeciesConstantExpression ) {
			result = facet.getType().getContentType().getSpecies();// getSpeciesDescription(facet.literalValue());
		} else if ( t.id() == IType.STRING && facet.isConst() ) {
			result = getSpeciesDescription(facet.literalValue());
		} else if ( t.isAgentType() ) {
			result = t.getSpecies();
		} else {
			result = facet.getType().getContentType().getSpecies();
		}
		return result;

	}

	public Arguments validateArgs() {
		final Arguments ca = new Arguments();
		final boolean isCalling = keyword.equals(CREATE) || keyword.equals(DO) || keyword.equals(PRIMITIVE);
		Facets argFacets;
		for ( final IDescription sd : getArgs() ) {
			argFacets = sd.getFacets();
			final String name = sd.getName();
			IExpression e = null;
			final IDescription superDesc = getEnclosingDescription();
			IExpressionDescription ed = argFacets.getDescr(VALUE, DEFAULT);
			if ( ed != null ) {
				e = ed.compile(superDesc);
			}

			ca.put(name, e);
			if ( !isCalling ) {
				IType type = sd.getType();
				addTemp(this, name, type);
			}

		}
		if ( keyword.equals(IKeyword.DO) ) {
			verifyArgs(getFacets().getLabel(IKeyword.ACTION), ca);
		} else if ( keyword.equals(IKeyword.CREATE) ) {
			verifyInits(ca);
		}
		return ca;

	}

	private void verifyInits(final Arguments ca) {
		final SpeciesDescription sd = computeSpecies();
		final Collection<StatementDescription> args = getArgs();
		if ( sd == null ) {
			if ( !args.isEmpty() ) {
				warning(
					"Impossible to verify the validity of the arguments. Use them at your own risk ! (and don't complain about exceptions)",
					IGamlIssue.UNKNOWN_ARGUMENT);
			}
			return;
		}
		for ( final StatementDescription arg : args ) {
			final String name = arg.getName();
			// hqnghi check attribute is not exist in both main model and micro-model
			if ( !sd.hasVar(name) && sd instanceof ExperimentDescription && !sd.getModelDescription().hasVar(name) ) {
				// end-hqnghi
				error("Attribute " + name + " does not exist in species " + sd.getName(), IGamlIssue.UNKNOWN_ARGUMENT,
					arg.getFacets().get(VALUE).getTarget(), (String[]) null);
			} else {
				IType initType = Types.NO_TYPE;
				IType varType = Types.NO_TYPE;
				VariableDescription vd = sd.getVariable(name);
				if ( vd != null ) {
					varType = vd.getType();
				}
				IExpressionDescription ed = ca.get(name);
				if ( ed != null ) {
					IExpression expr = ed.getExpression();
					if ( expr != null ) {
						initType = expr.getType();
					}
				}
				if ( varType != Types.NO_TYPE && !initType.isTranslatableInto(varType) ) {
					warning("The type of attribute " + name + " should be " + varType, IGamlIssue.SHOULD_CAST, arg
						.getFacets().get(VALUE).getTarget(), varType.toString());
				}
				// else {
				// varType = sd.getVariable(name).getContentType();
				// initType = ca.get(name).getExpression().getContentType();
				// if ( varType != Types.NO_TYPE && !initType.isTranslatableInto(varType) ) {
				// warning("The content type of attribute " + name + " should be " + varType,
				// IGamlIssue.WRONG_TYPE, arg.getFacets().get(VALUE).getTarget(), (String[]) null);
				// }
				// }
			}

		}
	}

	@Override
	protected IExpression createVarWithTypes(final String tag) {

		compileTypeProviderFacets();

		// Definition of the type
		IType t = super.getType();

		if ( t == Types.NO_TYPE ) {
			if ( keyword.equals(CREATE) || keyword.equals(CAPTURE) || keyword.equals(RELEASE) ) {
				t = Types.LIST;
			} else if ( facets.contains(VALUE) ) {
				IExpression value = facets.getExpr(VALUE);
				if ( value != null ) {
					t = value.getType();
				}
			} else if ( facets.contains(OVER) ) {
				IExpression expr = facets.getExpr(OVER);
				if ( expr != null ) {
					t = expr.getType().getContentType();
				}
			} else if ( facets.contains(FROM) && facets.contains(TO) ) {
				IExpression expr = facets.getExpr(FROM);
				if ( expr != null ) {
					t = expr.getType();
				}
			}
		}
		IType ct = t.getContentType();
		IType kt = t.getKeyType();
		// Definition of the content type and key type
		if ( facets.contains(AS) ) {
			ct = facets.getTypeDenotedBy(AS, this);
		} else if ( facets.contains(SPECIES) ) {
			IExpression expr = facets.getExpr(SPECIES);
			if ( expr != null ) {
				ct = expr.getType().getContentType();
				kt = expr.getType().getKeyType();
			}
		}
		// Last chance: grab the content and key from the value
		// TODO Verify: maybe useless as already done above in getType()
		boolean isContainerWithNoContentsType = t.isContainer() && ct == Types.NO_TYPE;
		boolean isContainerWithNoKeyType = t.isContainer() && kt == Types.NO_TYPE;
		boolean isSpeciesWithAgentType = t.id() == IType.SPECIES && ct.id() == IType.AGENT;
		if ( isContainerWithNoContentsType || isContainerWithNoKeyType || isSpeciesWithAgentType ) {
			IExpression value = facets.getExpr(VALUE, DEFAULT);
			if ( value != null ) {
				IType tt = t.typeIfCasting(value);
				if ( isContainerWithNoContentsType || isSpeciesWithAgentType ) {
					ct = tt.getContentType();
				}
				if ( isContainerWithNoKeyType ) {
					kt = tt.getKeyType();
				}
			}
		}

		return addNewTempIfNecessary(tag, GamaType.from(t, kt, ct));

	}

	/**
	 * Method getChildren()
	 * @see msi.gaml.descriptions.SymbolDescription#getChildren()
	 */
	@Override
	public List<IDescription> getChildren() {
		return Collections.EMPTY_LIST;
	}

	/**
	 * @return
	 */
	public boolean isBreakable() {
		return getMeta().isBreakable();
	}

}
