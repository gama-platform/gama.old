/*********************************************************************************************
 *
 *
 * 'VariableDescription.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gaml.descriptions;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.ecore.EObject;

import gnu.trove.map.hash.THashMap;
import gnu.trove.procedure.TObjectObjectProcedure;
import msi.gama.common.interfaces.IGamlIssue;
import msi.gama.precompiler.ITypeProvider;
import msi.gama.util.GAML;
import msi.gaml.compilation.GamaHelper;
import msi.gaml.descriptions.SymbolSerializer.VarSerializer;
import msi.gaml.expressions.IExpression;
import msi.gaml.expressions.IVarExpression;
import msi.gaml.factories.ChildrenProvider;
import msi.gaml.statements.Facets;
import msi.gaml.types.GamaIntegerType;
import msi.gaml.types.IType;
import msi.gaml.types.Types;

/**
 * Written by drogoul Modified on 16 mai 2010
 *
 * @todo Description
 *
 */
public class VariableDescription extends SymbolDescription {

	private THashMap<String, VariableDescription> dependencies;
	private String plugin;

	private final boolean _isGlobal, _isNotModifiable;
	// for variables automatically added to species for containing micro-agents
	private boolean _isSyntheticSpeciesContainer, dependenciesComputed;
	private GamaHelper get, init, set;

	public VariableDescription(final String keyword, final IDescription superDesc, final ChildrenProvider cp,
			final EObject source, final Facets facets, final Set<String> dependencies) {
		super(keyword, superDesc, cp, source, facets);
		if (!facets.containsKey(TYPE) && !isExperimentParameter()) {
			facets.putAsLabel(TYPE, keyword);
		}
		_isGlobal = superDesc instanceof ModelDescription;
		_isNotModifiable = facets.containsKey(FUNCTION) || facets.equals(CONST, TRUE) && !isParameter();
		addDependenciesNames(dependencies);
	}

	public boolean isExperimentParameter() {
		return PARAMETER.equals(keyword);
	}

	public void setSyntheticSpeciesContainer() {
		_isSyntheticSpeciesContainer = true;
	}

	public boolean isSyntheticSpeciesContainer() {
		return _isSyntheticSpeciesContainer;
	}

	@Override
	public void dispose() {
		if (isBuiltIn()) {
			return;
		}
		if (dependencies != null) {
			dependencies.clear();
			dependencies = null;
		}
		super.dispose();
	}

	public void copyFrom(final VariableDescription v2) {
		// Special cases for functions
		final boolean isFunction = hasFacet(FUNCTION);
		// We dont replace existing facets

		v2.visitFacets(new FacetVisitor() {

			@Override
			public boolean visit(final String facetName, final IExpressionDescription exp) {
				if (isFunction) {
					if (facetName.equals(INIT) || facetName.equals(UPDATE) || facetName.equals(VALUE)) {
						return true;
					}
				}
				if (!hasFacet(facetName)) {
					setFacet(facetName, exp);
				}
				return true;
			}
		});

		if (get == null) {
			get = v2.get;
		}
		if (set == null) {
			set = v2.set;
		}
		if (init == null) {
			init = v2.init;
		}
	}

	@Override
	public VariableDescription copy(final IDescription into) {
		final VariableDescription vd = new VariableDescription(getKeyword(), into, ChildrenProvider.NONE, element,
				getFacetsCopy(), dependencies == null ? null : dependencies.keySet());
		vd.addHelpers(get, init, set);
		vd.originName = getOriginName();
		return vd;
	}

	@Override
	protected SymbolSerializer createSerializer() {
		return VarSerializer.getInstance();
	}

	/**
	 * A variable is said to be contextual if its type or contents type depends
	 * on the species context. For example, 'simulation' in experiments. If so,
	 * it has to be copied in subspecies
	 * 
	 * @return
	 */
	public boolean isContextualType() {
		String type = getLitteral(TYPE);
		int provider = GamaIntegerType.staticCast(null, type, null, false);
		if (provider < 0)
			return true;
		type = getLitteral(OF);
		provider = GamaIntegerType.staticCast(null, type, null, false);
		return provider < 0;
	}

	/**
	 * Returns the type denoted by this string. This is a contextual retrieval,
	 * as the string can contain the value of one of the ITypeProvider
	 * constants. Method getTypeNamed()
	 * 
	 * @see msi.gaml.descriptions.SymbolDescription#getTypeNamed(java.lang.String)
	 */
	@Override
	public IType getTypeNamed(final String s) {
		final IType result = super.getTypeNamed(s);
		if (result == Types.NO_TYPE) {
			final int provider = GamaIntegerType.staticCast(null, s, null, false);
			switch (provider) {
			case ITypeProvider.MACRO_TYPE:
				final IDescription species = this.getEnclosingDescription();
				final IDescription macro = species.getEnclosingDescription();
				if (macro == null) {
					return Types.AGENT;
				}
				return macro.getType();
			case ITypeProvider.OWNER_TYPE: // This represents the type of the
											// agents of the enclosing species
				if (this.getEnclosingDescription() == null) {
					return Types.AGENT;
				}
				return this.getEnclosingDescription().getType();
			case ITypeProvider.MODEL_TYPE: // This represents the type of the
											// model (used for simulations)
				final ModelDescription md = this.getModelDescription();
				if (md == null) {
					return Types.get("model");
				}
				return md.getType();
			case ITypeProvider.MIRROR_TYPE:
				if (getEnclosingDescription() == null) {
					return null;
				}
				final IExpression mirrors = getEnclosingDescription().getFacetExpr(MIRRORS);
				if (mirrors != null) {
					// We try to change the type of the 'target' variable if the
					// expression contains only agents from the
					// same species
					final IType t = mirrors.getType().getContentType();
					if (t.isAgentType() && t.id() != IType.AGENT) {
						getEnclosingDescription().info("The 'target' attribute will be of type " + t.getSpeciesName(),
								IGamlIssue.GENERAL, MIRRORS);
					}
					return t;
				} else {
					getEnclosingDescription().info(
							"No common species detected in 'mirrors'. The 'target' variable will be of generic type 'agent'",
							IGamlIssue.WRONG_TYPE, MIRRORS);
				}
			}
		}
		return result;
	}

	public void usedVariablesIn(final Map<String, VariableDescription> vars) {

		if (!dependenciesComputed) {
			dependenciesComputed = true;
			if (dependencies == null)
				return;
			for (final Map.Entry<String, VariableDescription> entry : dependencies.entrySet()) {
				entry.setValue(vars.get(entry.getKey()));
			}
			dependencies.remove(getName());
		}
	}

	public void expandDependencies(final List<VariableDescription> without) {
		if (dependencies == null)
			return;
		final Map<String, VariableDescription> accumulator = new THashMap();
		for (final VariableDescription dep : dependencies.values()) {
			if (dep == null)
				continue;
			if (!without.contains(dep)) {
				without.add(this);
				dep.expandDependencies(without);
				for (final VariableDescription vd : dep.getDependencies()) {
					if (vd != null)
						accumulator.put(vd.getName(), vd);
				}
			}
		}
		dependencies.putAll(accumulator);
	}

	public Collection<VariableDescription> getDependencies() {
		if (dependencies == null)
			return Collections.EMPTY_LIST;
		// May contain null values
		return dependencies.values();
	}

	public void getExtraDependencies(final Set<String> into) {
		if (dependencies == null)
			return;
		dependencies.forEachEntry(new TObjectObjectProcedure<String, VariableDescription>() {

			@Override
			public boolean execute(final String a, final VariableDescription b) {
				if (b == null)
					into.add(a);
				return true;
			}
		});
	}

	public boolean isUpdatable() {
		return !_isNotModifiable && (hasFacet(VALUE) || hasFacet(UPDATE));
	}

	public boolean isNotModifiable() {
		return _isNotModifiable;
	}

	public boolean isParameter() {
		return isExperimentParameter() || hasFacet(PARAMETER);
	}

	// If asField is true, should not try to build a GlobalVarExpr
	public IExpression getVarExpr(final boolean asField) {
		final boolean asGlobal = _isGlobal && !asField;

		final IExpression varExpr = GAML.getExpressionFactory().createVar(getName(), getType(), isNotModifiable(),
				asGlobal ? IVarExpression.GLOBAL : IVarExpression.AGENT, this.getEnclosingDescription());
		return varExpr;
	}

	@Override
	public String toString() {
		return getName() + " (description)";
	}

	public String getParameterName() {
		final String pName = getLitteral(PARAMETER);
		if (pName == null || pName.equals(TRUE)) {
			return getName();
		}
		return pName;
	}

	@Override
	public String getTitle() {
		final String title = "Definition of "
				+ (isParameter() ? "parameter " : isNotModifiable() ? "constant " : "attribute ");
		return title + " "
				+ getName() /* + " of type " + getType().getTitle() */;
	}

	@Override
	public String getDocumentation() {
		final String title = "a" + (isParameter() ? "  parameter " : isNotModifiable() ? " constant " : "n attribute ");
		if (getEnclosingDescription() == null) {
			return "This statement declares " + getName() + " as " + title + "<br/>" + super.getDocumentation();
		}
		final String s = "This statement declares " + getName() + " as " + title + ", of type " + getType().getTitle()
				+ ", in " + this.getEnclosingDescription().getTitle() + "<br/>";
		return s + super.getDocumentation();
	}

	public void addHelpers(final GamaHelper get, final GamaHelper init, final GamaHelper set) {
		this.get = get;
		this.set = set;
		this.init = init;
	}

	public GamaHelper getGetter() {
		return get;
	}

	public GamaHelper getIniter() {
		return init;
	}

	public GamaHelper getSetter() {
		return set;
	}

	public boolean isGlobal() {
		return _isGlobal;
	}
	//
	// @Override
	// public List<IDescription> getChildren() {
	// return Collections.EMPTY_LIST;
	// }
	//
	// @Override
	// public List<IDescription> getOwnChildren() {
	// return Collections.EMPTY_LIST;
	// }

	@Override
	public String getDefiningPlugin() {
		return plugin;
	}

	/**
	 * @param plugin
	 *            name
	 */
	@Override
	public void setDefiningPlugin(final String plugin) {
		this.plugin = plugin;
	}

	public void addDependenciesNames(final Set<String> dependencies) {
		final IExpressionDescription exp = getFacet("depends_on");
		if (exp != null) {
			addDependenciesNoCheck(exp.getStrings(this, false));
			removeFacets("depends_on");
		}
		addDependenciesNoCheck(dependencies);
	}

	private void addDependenciesNoCheck(final Set<String> deps) {
		if (deps == null || deps.isEmpty())
			return;
		if (dependencies == null)
			dependencies = new THashMap();
		for (final String s : deps) {
			dependencies.put(s, null);
		}
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
