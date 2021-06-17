/*******************************************************************************************************
 *
 * msi.gaml.descriptions.VariableDescription.java, in plugin msi.gama.core, is part of the source code of the GAMA
 * modeling and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gaml.descriptions;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.ecore.EObject;

import com.google.common.collect.ImmutableSet;

import msi.gama.common.interfaces.IGamlIssue;
import msi.gama.precompiler.ITypeProvider;
import msi.gama.util.Collector;
import msi.gama.util.GamaMapFactory;
import msi.gama.util.ICollector;
import msi.gaml.compilation.AbstractGamlAdditions;
import msi.gaml.compilation.GAML;
import msi.gaml.compilation.GamaHelper;
import msi.gaml.compilation.IGamaHelper;
import msi.gaml.expressions.IExpression;
import msi.gaml.expressions.IVarExpression;
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

	private static Map<String, Collection<String>> dependencies = GamaMapFactory.createUnordered();
	public final static Set<String> INIT_DEPENDENCIES_FACETS =
			ImmutableSet.<String> builder().add(INIT, MIN, MAX, STEP, SIZE, AMONG).build();
	public final static Set<String> UPDATE_DEPENDENCIES_FACETS =
			ImmutableSet.<String> builder().add(UPDATE, VALUE, MIN, MAX).build();
	public final static Set<String> FUNCTION_DEPENDENCIES_FACETS =
			ImmutableSet.<String> builder().add(FUNCTION).build();
	private String plugin;

	private final boolean _isGlobal, _isNotModifiable;
	// for variables automatically added to species for containing micro-agents
	private boolean _isSyntheticSpeciesContainer;
	private IGamaHelper<?> get, init, set;
	// private GamaHelper<?>[] listeners;

	public VariableDescription(final String keyword, final IDescription superDesc, final EObject source,
			final Facets facets) {
		super(keyword, superDesc, source, /* null, */facets);
		if (facets != null && !facets.containsKey(TYPE) && !isExperimentParameter()) {
			facets.putAsLabel(TYPE, keyword);
		}
		_isGlobal = superDesc instanceof ModelDescription;
		_isNotModifiable =
				facets != null && (facets.containsKey(FUNCTION) || facets.equals(CONST, TRUE)) && !isParameter();
		if (isBuiltIn() && hasFacet("depends_on")) {
			final IExpressionDescription desc = getFacet("depends_on");
			final Collection<String> strings = desc.getStrings(this, false);
			dependencies.put(getName(), strings);
			removeFacets("depends_on");
		}

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
		if (isBuiltIn()) return;
		super.dispose();
	}

	public void copyFrom(final VariableDescription v2) {
		// Special cases for functions
		final boolean isFunction = hasFacet(FUNCTION);
		// We dont replace existing facets

		v2.visitFacets((facetName, exp) -> {
			if (isFunction) {
				if (facetName.equals(INIT) || facetName.equals(UPDATE) || facetName.equals(VALUE)) return true;
			}
			if (!hasFacet(facetName)) { setFacet(facetName, exp); }
			return true;
		});

		if (get == null) { get = v2.get; }
		if (set == null) { set = v2.set; }
		if (init == null) { init = v2.init; }
		// if (listeners == null) { listeners = v2.listeners; }
	}

	@Override
	public VariableDescription copy(final IDescription into) {
		final VariableDescription vd = new VariableDescription(getKeyword(), into, element, getFacetsCopy());
		vd.addHelpers(get, init, set);
		// vd.listeners = listeners;
		vd.originName = getOriginName();
		return vd;
	}

	@Override
	protected SymbolSerializer<VariableDescription> createSerializer() {
		return VAR_SERIALIZER;
	}

	/**
	 * A variable is said to be contextual if its type or contents type depends on the species context. For example,
	 * 'simulation' in experiments. If so, it has to be copied in subspecies
	 *
	 * @return
	 */
	public boolean isContextualType() {
		String type = getLitteral(TYPE);
		int provider = GamaIntegerType.staticCast(null, type, null, false);
		if (provider < 0) return true;
		type = getLitteral(OF);
		provider = GamaIntegerType.staticCast(null, type, null, false);
		return provider < 0;
	}

	/**
	 * Returns the type denoted by this string. This is a contextual retrieval, as the string can contain the value of
	 * one of the ITypeProvider constants. Method getTypeNamed()
	 *
	 * @see msi.gaml.descriptions.SymbolDescription#getTypeNamed(java.lang.String)
	 */
	@Override
	public IType<?> getTypeNamed(final String s) {
		final IType<?> result = super.getTypeNamed(s);
		if (result == Types.NO_TYPE) {
			final int provider = GamaIntegerType.staticCast(null, s, null, false);
			switch (provider) {
				case ITypeProvider.MACRO_TYPE:
					final IDescription species = this.getEnclosingDescription();
					final IDescription macro = species.getEnclosingDescription();
					if (macro == null) return Types.AGENT;
					return macro.getGamlType();
				case ITypeProvider.OWNER_TYPE: // This represents the type of the
												// agents of the enclosing species
					if (this.getEnclosingDescription() == null) return Types.AGENT;
					return this.getEnclosingDescription().getGamlType();
				case ITypeProvider.MODEL_TYPE: // This represents the type of the
												// model (used for simulations)
					final ModelDescription md = this.getModelDescription();
					if (md == null) return Types.get("model");
					return md.getGamlType();
				case ITypeProvider.EXPERIMENT_TYPE:
					return Types.get("experiment");
				case ITypeProvider.MIRROR_TYPE:
					if (getEnclosingDescription() == null) return null;
					final IExpression mirrors = getEnclosingDescription().getFacetExpr(MIRRORS);
					if (mirrors != null) {
						// We try to change the type of the 'target' variable if the
						// expression contains only agents from the
						// same species
						final IType<?> t = mirrors.getGamlType().getContentType();
						if (t.isAgentType() && t.id() != IType.AGENT) {
							getEnclosingDescription().info(
									"The 'target' attribute will be of type " + t.getSpeciesName(), IGamlIssue.GENERAL,
									MIRRORS);
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

	public Collection<VariableDescription> getDependencies(final Set<String> facetsToVisit, final boolean includingThis,
			final boolean includingSpecies) {

		try (final ICollector<IVarDescriptionUser> alreadyProcessed = Collector.getSet();
				final ICollector<VariableDescription> result = Collector.getSet()) {
			final Collection<String> deps = dependencies.get(getName());
			if (deps != null) {
				for (final String s : deps) {
					final VariableDescription vd = getSpeciesContext().getAttribute(s);
					if (vd != null) { result.add(vd); }
				}
			}

			this.visitFacets(facetsToVisit, (fName, exp) -> {
				final IExpression expression = exp.getExpression();
				if (expression != null) { expression.collectUsedVarsOf(getSpeciesContext(), alreadyProcessed, result); }
				return true;
			});
			if (isSyntheticSpeciesContainer()) {
				final SpeciesDescription mySpecies = (SpeciesDescription) getEnclosingDescription();
				final SpeciesDescription sd = mySpecies.getMicroSpecies(getName());
				sd.collectUsedVarsOf(mySpecies, alreadyProcessed, result);
			}
			if (!includingThis) { result.remove(this); }
			if (!includingSpecies) { result.removeIf(v -> v.isSyntheticSpeciesContainer()); }
			result.remove(null);
			return result.items();
		}
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

		final IExpression varExpr = GAML.getExpressionFactory().createVar(getName(), getGamlType(), isNotModifiable(),
				asGlobal ? IVarExpression.GLOBAL : IVarExpression.AGENT, this.getEnclosingDescription());
		return varExpr;
	}

	@Override
	public String toString() {
		return getName() + " (description)";
	}

	public String getParameterName() {
		final String pName = getLitteral(PARAMETER);
		if (pName == null || pName.equals(TRUE)) return getName();
		return pName;
	}

	@Override
	public String getTitle() {
		final String title = getGamlType().getTitle()
				+ (isParameter() ? " parameter " : isNotModifiable() ? " constant " : " attribute ") + getName();
		if (getEnclosingDescription() == null) return title;
		return title + " of " + this.getEnclosingDescription().getTitle() + "<br/>";
	}

	@Override
	public String getDocumentation() {
		final String doc = getBuiltInDoc();
		if (isBuiltIn()) return doc == null ? "Not yet documented" : doc;
		String s = "";
		if (doc != null) { s += doc + "<br/>"; }
		return s + getMeta().getFacetsDocumentation();
	}

	public String getShortDescription() {
		String s = ", of type " + getGamlType().getTitle();
		final String doc = getBuiltInDoc();
		if (doc != null) { s += ": " + doc; }
		return s;
	}

	public String getBuiltInDoc() {
		final VariableDescription builtIn = getBuiltInAncestor();
		if (builtIn == null) return null;
		return AbstractGamlAdditions.TEMPORARY_BUILT_IN_VARS_DOCUMENTATION.get(builtIn.getName());
	}

	private VariableDescription getBuiltInAncestor() {
		if (getEnclosingDescription() instanceof TypeDescription) {
			final TypeDescription td = (TypeDescription) getEnclosingDescription();
			if (td.isBuiltIn()) return this;
			if (td.getParent() != null && td.getParent().hasAttribute(name))
				return td.getParent().getAttribute(name).getBuiltInAncestor();
		}
		return null;
	}

	public void addHelpers(final IGamaHelper<?> get, final IGamaHelper<?> init, final IGamaHelper<?> set) {
		this.get = get;
		this.set = set;
		this.init = init;
	}

	public void addHelpers(final Class<?> skill, final IGamaHelper<?> get, final IGamaHelper<?> init,
			final IGamaHelper<?> set) {
		addHelpers(get != null ? new GamaHelper<>(name, skill, get) : null,
				init != null ? new GamaHelper<>(name, skill, init) : null,
				set != null ? new GamaHelper<>(name, skill, set) : null);
	}

	// public void addListeners(final List<GamaHelper> listeners) {
	// this.listeners = listeners.toArray(new GamaHelper[listeners.size()]);
	// }
	//
	// public GamaHelper[] getListeners() {
	// return listeners;
	// }

	public IGamaHelper<?> getGetter() {
		return get;
	}

	public IGamaHelper<?> getIniter() {
		return init;
	}

	public IGamaHelper<?> getSetter() {
		return set;
	}

	public boolean isGlobal() {
		return _isGlobal;
	}

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

	@Override
	public boolean visitChildren(final DescriptionVisitor visitor) {
		return true;
	}

	@Override
	public boolean visitOwnChildren(final DescriptionVisitor visitor) {
		return true;
	}

	@Override
	public Iterable<IDescription> getOwnChildren() {
		return Collections.EMPTY_LIST;
	}

}
