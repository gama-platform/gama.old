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
package msi.gaml.descriptions;

import java.util.*;
import msi.gaml.compilation.GamaHelper;
import msi.gaml.expressions.IVarExpression;
import msi.gaml.factories.IChildrenProvider;
import msi.gaml.statements.Facets;
import msi.gaml.types.*;
import org.eclipse.emf.ecore.EObject;

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

	private int definitionOrder = -1;
	private IVarExpression varExpr = null;
	private IType type = null, contentType = null, keyType = null;
	private final boolean _isGlobal,/* _isFunction, */_isNotModifiable, _isParameter;
	private boolean _isUpdatable;
	private GamaHelper get, init, set;

	public VariableDescription(final String keyword, final IDescription superDesc, final IChildrenProvider cp,
		final EObject source, final Facets facets) {
		super(keyword, superDesc, cp, source, facets);
		boolean isExperimentParameter = facets.equals(KEYWORD, PARAMETER);
		if ( !facets.containsKey(TYPE) && !isExperimentParameter ) {
			facets.putAsLabel(TYPE, keyword);
		}
		_isGlobal = superDesc instanceof ModelDescription;
		// _isFunction = facets.containsKey(FUNCTION);
		_isParameter = isExperimentParameter || facets.containsKey(PARAMETER);
		_isNotModifiable =
		/* _isFunction */facets.containsKey(FUNCTION) || facets.equals(CONST, TRUE) && !_isParameter;
		_isUpdatable = !_isNotModifiable && (facets.containsKey(VALUE) || facets.containsKey(UPDATE));

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
		// Without replacing
		for ( Map.Entry<String, IExpressionDescription> entry : v2.facets.entrySet() ) {
			if ( entry == null ) {
				continue;
			}
			if ( !facets.containsKey(entry.getKey()) ) {
				facets.put(entry.getKey(), entry.getValue());
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
		VariableDescription vd = new VariableDescription(getKeyword(), into, null, element, facets);
		vd.addHelpers(get, init, set);
		vd.originName = originName;
		return vd;
	}

	@Override
	public IType getType() {
		if ( type == null ) {
			type = getTypeNamed(facets.getLabel(TYPE));
		}
		return type;
	}

	public Set<VariableDescription> usedVariablesIn(final Map<String, VariableDescription> vars) {
		if ( dependencies == null ) {
			dependencies = new LinkedHashSet();
			extraDependencies = new LinkedHashSet();
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
		final Set<VariableDescription> accumulator = new HashSet();
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

	@Override
	public IType getContentType() {
		if ( !getType().hasContents() ) { return Types.NO_TYPE; }
		if ( contentType == null ) {
			String of = facets.getLabel(OF);
			if ( of != null ) {
				contentType = getTypeNamed(of);
			}
			if ( contentType == null || contentType == Types.NO_TYPE ) {
				contentType = getType().defaultContentType();
			}
		}
		return contentType;
	}

	@Override
	public IType getKeyType() {
		if ( !getType().hasContents() ) { return Types.NO_TYPE; }
		if ( keyType == null ) {
			String index = facets.getLabel(INDEX);
			if ( index != null ) {
				keyType = getTypeNamed(index);
			}
			if ( keyType == null || keyType == Types.NO_TYPE ) {
				keyType = getType().defaultKeyType();
			}
		}
		return keyType;
	}

	public IVarExpression getVarExpr() {
		if ( varExpr != null ) { return varExpr; }
		//
		varExpr =
			msi.gama.util.GAML.getExpressionFactory().createVar(getName(), getType(), getContentType(), getKeyType(),
				isNotModifiable(), _isGlobal ? IVarExpression.GLOBAL : IVarExpression.AGENT,
				this.getEnclosingDescription());
		return varExpr;
	}

	public void setContentType(final IType type) {
		// sent by the variable once its value and init are compiled
		if ( contentType != null && contentType != Types.NO_TYPE ) { return; }
		contentType = type;
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
		String title = isParameter() ? "parameter " : isNotModifiable() ? "constant " : "attribute ";
		return title + " " + getName() + " of type " + typeToString();
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

	/**
	 * @return
	 */
	public boolean isGlobal() {
		return _isGlobal;
	}

}
