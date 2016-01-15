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

import java.util.*;
import org.eclipse.emf.ecore.EObject;
import gnu.trove.set.hash.*;
import msi.gaml.compilation.GamaHelper;
import msi.gaml.descriptions.SymbolSerializer.VarSerializer;
import msi.gaml.expressions.*;
import msi.gaml.factories.ChildrenProvider;
import msi.gaml.statements.Facets;
import msi.gaml.types.IType;

/**
 * Written by drogoul Modified on 16 mai 2010
 *
 * @todo Description
 *
 */
public class VariableDescription extends SymbolDescription {

	private Set<VariableDescription> dependencies;
	// Represents the variable that are not declared in the species but that the variable depends
	// on.
	private Set<String> extraDependencies;
	private String plugin;
	private int definitionOrder = -1;
	private IExpression varExpr = null;
	private IType type = null;
	private final boolean _isGlobal, /* _isFunction, */_isNotModifiable, _isParameter;
	private boolean _isUpdatable;
	// for variables automatically added to species for containing micro-agents
	private boolean _isSyntheticSpeciesContainer;
	private GamaHelper get, init, set;

	public VariableDescription(final String keyword, final IDescription superDesc, final ChildrenProvider cp,
		final EObject source, final Facets facets) {
		super(keyword, superDesc, cp, source, facets);
		boolean isExperimentParameter = facets.equals(KEYWORD, PARAMETER);
		if ( !facets.containsKey(TYPE) && !isExperimentParameter ) {
			facets.putAsLabel(TYPE, keyword);
		}
		_isGlobal = superDesc instanceof ModelDescription;
		_isParameter = isExperimentParameter || facets.containsKey(PARAMETER);
		_isNotModifiable = facets.containsKey(FUNCTION) || facets.equals(CONST, TRUE) && !_isParameter;
		_isUpdatable = !_isNotModifiable && (facets.containsKey(VALUE) || facets.containsKey(UPDATE));

	}

	public boolean isExperimentParameter() {
		return facets.equals(KEYWORD, PARAMETER);
	}

	public void setSyntheticSpeciesContainer() {
		_isSyntheticSpeciesContainer = true;
	}

	public boolean isSyntheticSpeciesContainer() {
		return _isSyntheticSpeciesContainer;
	}

	@Override
	public void dispose() {
		if ( /* isDisposed || */isBuiltIn() ) { return; }
		if ( dependencies != null ) {
			dependencies.clear();
		}
		varExpr = null;
		super.dispose();
		// isDisposed = true;
	}

	public void copyFrom(final VariableDescription v2) {
		// Special cases for functions
		boolean isFunction = this.getFacets().containsKey(FUNCTION);
		// We dont replace existing facets
		for ( Map.Entry<String, IExpressionDescription> entry : v2.facets.entrySet() ) {
			if ( entry == null ) {
				continue;
			}
			String facetName = entry.getKey();
			if ( isFunction ) {
				if ( facetName.equals(INIT) || facetName.equals(UPDATE) || facetName.equals(VALUE) ) {
					continue;
				}
			}
			if ( !facets.containsKey(facetName) ) {
				facets.put(facetName, entry.getValue());
			}
		}
		if ( get == null ) {
			get = v2.get;
		}
		if ( set == null ) {
			set = v2.set;
		}
		if ( init == null ) {
			init = v2.init;
		}
		// originName = v2.originName;
		// if ( originName == null ) {
		// originName = v2.originName;
		// }
	}

	@Override
	public VariableDescription copy(final IDescription into) {
		VariableDescription vd = new VariableDescription(getKeyword(), into, null, element, facets.cleanCopy());
		vd.addHelpers(get, init, set);
		vd.originName = originName;
		return vd;
	}

	@Override
	protected SymbolSerializer createSerializer() {
		return new VarSerializer();
	}

	@Override
	public IType getType() {

		if ( type == null ) {
			type = super.getType();
		}
		return type;
	}

	public Set<VariableDescription> usedVariablesIn(final Map<String, VariableDescription> vars) {
		if ( dependencies == null ) {
			dependencies = new TLinkedHashSet();
			extraDependencies = new TLinkedHashSet();
			IExpressionDescription depends = facets.get(DEPENDS_ON);
			if ( depends != null ) {
				for ( final String s : depends.getStrings(getSpeciesContext(), false) ) {
					VariableDescription v = vars.get(s);
					if ( v != null ) {
						dependencies.add(v);
					} else {
						extraDependencies.add(s);
					}
				}
				dependencies.remove(this);
				extraDependencies.remove(getName());
			}
		}
		return dependencies;
	}

	public void expandDependencies(final List<VariableDescription> without) {
		final Set<VariableDescription> accumulator = new THashSet();
		for ( final VariableDescription dep : dependencies ) {
			if ( !without.contains(dep) ) {
				without.add(this);
				dep.expandDependencies(without);
				accumulator.addAll(dep.getDependencies());
			}
		}
		dependencies.addAll(accumulator);
	}

	public Set<VariableDescription> getDependencies() {
		return dependencies;
	}

	public Set<String> getExtraDependencies() {
		return extraDependencies;
	}

	public boolean isUpdatable() {
		return _isUpdatable;
	}

	public void setUpdatable(final boolean b) {
		_isUpdatable = b;
	}

	// public boolean isFunction() {
	// return _isFunction;
	// }

	public boolean isNotModifiable() {
		return _isNotModifiable;
	}

	public boolean isParameter() {
		return _isParameter;
	}

	public IExpression getVarExpr() {
		if ( varExpr != null ) { return varExpr; }
		varExpr = msi.gama.util.GAML.getExpressionFactory().createVar(getName(), getType(), isNotModifiable(),
			_isGlobal ? IVarExpression.GLOBAL : IVarExpression.AGENT, this.getEnclosingDescription());
		return varExpr;
	}

	public void setDefinitionOrder(final int i) {
		definitionOrder = i;
	}

	public int getDefinitionOrder() {
		return definitionOrder;
	}

	@Override
	public String toString() {
		return getName() + " (description)";
	}

	public String getParameterName() {
		final String pName = facets.getLabel(PARAMETER);
		if ( pName == null || pName.equals(TRUE) ) { return getName(); }
		return pName;
	}

	@Override
	public String getTitle() {
		String title =
			"Definition of " + (isParameter() ? "parameter " : isNotModifiable() ? "constant " : "attribute ");
		return title + " " + getName() /* + " of type " + getType().getTitle() */;
	}

	@Override
	public String getDocumentation() {
		String title = "a" + (isParameter() ? "  parameter " : isNotModifiable() ? " constant " : "n attribute ");
		if ( getEnclosingDescription() == null ) { return "This statement declares " + getName() + " as " + title +
			"<br/>" + super.getDocumentation(); }
		String s = "This statement declares " + getName() + " as " + title + ", of type " + getType().getTitle() +
			", in " + this.getEnclosingDescription().getTitle() + "<br/>";
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

	public void setType(final IType t) {
		type = t;
	}

	public boolean isGlobal() {
		return _isGlobal;
	}

	@Override
	public List<IDescription> getChildren() {
		return Collections.EMPTY_LIST;
	}

	@Override
	public String getDefiningPlugin() {
		return plugin;
	}

	/**
	 * @param plugin name
	 */
	@Override
	public void setDefiningPlugin(final String plugin) {
		this.plugin = plugin;
	}

}
