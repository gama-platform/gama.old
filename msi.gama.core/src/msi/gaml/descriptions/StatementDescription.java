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

import java.util.Set;

import org.eclipse.emf.ecore.EObject;

import msi.gama.common.interfaces.IGamlIssue;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.util.GAML;
import msi.gaml.descriptions.SymbolSerializer.StatementSerializer;
import msi.gaml.expressions.IExpression;
import msi.gaml.expressions.IOperator;
import msi.gaml.expressions.IVarExpression;
import msi.gaml.factories.ChildrenProvider;
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
@SuppressWarnings({ "rawtypes" })
public class StatementDescription extends SymbolDescription {

	// Corresponds to the "with" facet
	protected final Arguments passedArgs;
	private static int COMMAND_INDEX = 0;

	public StatementDescription(final String keyword, final IDescription superDesc, final ChildrenProvider cp,
			final boolean hasArgs, final EObject source, final Facets facets, final Arguments alreadyComputedArgs) {
		super(keyword, superDesc, cp, source, facets);
		passedArgs = alreadyComputedArgs != null ? alreadyComputedArgs : hasArgs ? createArgs() : null;
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

		if (passedArgs != null)
			passedArgs.dispose();
	}

	private Arguments createArgs() {
		if (!hasFacets())
			return null;
		if (!hasFacet(WITH)) {
			if (!getKeyword().equals(DO))
				return null;
			if (hasFacetsNotIn(DoStatement.DO_FACETS)) {
				final Arguments args = new Arguments();
				visitFacets(new FacetVisitor() {

					@Override
					public final boolean visit(final String facet, final IExpressionDescription b) {
						if (!DoStatement.DO_FACETS.contains(facet)) {
							args.put(facet, b);
						}
						return true;
					}
				});
				return args;
			} else
				return null;
		} else {
			try {
				return GAML.getExpressionFactory().createArgumentMap(getAction(), getFacet(WITH), this);
			} finally {
				removeFacets(WITH);
			}
		}

	}

	private ActionDescription getAction() {
		final String actionName = getLitteral(IKeyword.ACTION);
		if (actionName == null) {
			return null;
		}
		final TypeDescription declPlace = (TypeDescription) getDescriptionDeclaringAction(actionName);
		ActionDescription executer = null;
		if (declPlace != null) {
			executer = declPlace.getAction(actionName);
		}
		return executer;
	}

	@Override
	public StatementDescription copy(final IDescription into) {
		final StatementDescription desc = new StatementDescription(getKeyword(), into, ChildrenProvider.NONE, false,
				element, getFacetsCopy(), passedArgs == null ? null : passedArgs.cleanCopy());
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

	public boolean verifyArgs(final Arguments args) {
		final ActionDescription executer = getAction();
		if (executer == null) {
			return false;
		}
		return executer.verifyArgs(this, args);
	}

	public Iterable<IDescription> getFormalArgs() {
		return getChildrenWithKeyword(ARG);
	}

	public Facets getPassedArgs() {
		return passedArgs == null ? Facets.NULL : passedArgs;
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

	@Override
	public String getTitle() {
		final String kw = getKeyword();
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
	public IDescription validate() {

		final IDescription result = super.validate();
		if (passedArgs != null)
			validatePassedArgs();
		return result;
	}

	public Arguments validatePassedArgs() {
		if (passedArgs == null)
			return null;
		final IDescription superDesc = getEnclosingDescription();
		passedArgs.forEachEntry(new FacetVisitor() {

			@Override
			public boolean visit(final String name, final IExpressionDescription exp) {
				exp.compile(superDesc);
				return true;
			}
		});
		if (keyword.equals(IKeyword.DO)) {
			verifyArgs(passedArgs);
		} else if (keyword.equals(IKeyword.CREATE)) {
			verifyInits(passedArgs);
		}
		return passedArgs;
	}

	private void verifyInits(final Arguments ca) {
		final SpeciesDescription denotedSpecies = getType().getDenotedSpecies();
		if (denotedSpecies == null) {
			if (!ca.isEmpty()) {
				warning("Impossible to verify the validity of the arguments. Use them at your own risk ! (and don't complain about exceptions)",
						IGamlIssue.UNKNOWN_ARGUMENT);
			}
			return;
		}
		ca.forEachEntry(new FacetVisitor() {

			@Override
			public boolean visit(final String name, final IExpressionDescription exp) {
				// hqnghi check attribute is not exist in both main model and
				// micro-model
				if (!denotedSpecies.hasAttribute(name) && denotedSpecies instanceof ExperimentDescription
						&& !denotedSpecies.getModelDescription().hasAttribute(name)) {
					// end-hqnghi
					error("Attribute " + name + " does not exist in species " + denotedSpecies.getName(),
							IGamlIssue.UNKNOWN_ARGUMENT, exp.getTarget(), (String[]) null);
					return false;
				} else {
					IType initType = Types.NO_TYPE;
					IType varType = Types.NO_TYPE;
					final VariableDescription vd = denotedSpecies.getAttribute(name);
					if (vd != null) {
						varType = vd.getType();
					}
					if (exp != null) {
						final IExpression expr = exp.getExpression();
						if (expr != null) {
							initType = expr.getType();
						}
					}
					if (varType != Types.NO_TYPE && !initType.isTranslatableInto(varType)) {
						warning("The type of attribute " + name + " should be " + varType, IGamlIssue.SHOULD_CAST,
								exp.getTarget(), varType.toString());
					}
				}

				return true;
			}
		});

	}

	@Override
	protected IExpression createVarWithTypes(final String tag) {

		compileTypeProviderFacets();

		// Definition of the type
		IType t = super.getType();
		final String keyword = getKeyword();
		IType ct = t.getContentType();
		if (keyword.equals(CREATE) || keyword.equals(CAPTURE) || keyword.equals(RELEASE)) {
			ct = t;
			t = Types.LIST;

		} else if (t == Types.NO_TYPE) {
			if (hasFacet(VALUE)) {
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

		return addNewTempIfNecessary(tag, GamaType.from(t, kt, ct));

	}

	public IVarExpression addNewTempIfNecessary(final String facetName, final IType type) {
		final String varName = getLitteral(facetName);
		final IDescription sup = getEnclosingDescription();
		if (!(sup instanceof StatementWithChildrenDescription)) {
			error("Impossible to return " + varName, IGamlIssue.GENERAL);
			return null;
		}
		return (IVarExpression) ((StatementWithChildrenDescription) sup).addTemp(this, varName, type);
	}

	@Override
	public boolean visitChildren(final DescriptionVisitor visitor) {
		return true;
	}

	@Override
	public boolean visitOwnChildren(final DescriptionVisitor visitor) {
		return true;
	}

	public Arguments createCompiledArgs() {
		return passedArgs;
	}

}
