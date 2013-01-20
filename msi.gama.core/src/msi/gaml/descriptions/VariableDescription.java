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
package msi.gaml.descriptions;

import static msi.gama.common.interfaces.IKeyword.*;
import java.util.*;
import msi.gama.common.interfaces.ISyntacticElement;
import msi.gama.runtime.GAMA;
import msi.gaml.compilation.*;
import msi.gaml.expressions.IVarExpression;
import msi.gaml.factories.IChildrenProvider;
import msi.gaml.statements.Facets;
import msi.gaml.types.*;

/**
 * Written by drogoul Modified on 16 mai 2010
 * 
 * @todo Description
 * 
 */
public class VariableDescription extends SymbolDescription {

	private Set<VariableDescription> dependencies;

	private int definitionOrder = -1;
	private IVarExpression varExpr = null;
	private IType type = null, contentType = null;
	private final boolean _isGlobal, _isFunction, _isNotModifiable, _isParameter, _isUpdatable;
	private IVarGetter get;
	private IVarGetter init;
	private IVarSetter set;

	public VariableDescription(final String keyword, final IDescription superDesc,
		final Facets facets, final IChildrenProvider cp, final ISyntacticElement source,
		final SymbolProto md) {
		super(keyword, superDesc, cp, source, md);
		boolean isExperimentParameter = facets.equals(KEYWORD, PARAMETER);
		if ( !facets.containsKey(TYPE) && !isExperimentParameter ) {
			facets.putAsLabel(TYPE, keyword);
		}
		_isGlobal = superDesc != null && WORLD_SPECIES.equals(superDesc.getName());
		_isFunction = facets.containsKey(FUNCTION);
		_isParameter = isExperimentParameter || facets.containsKey(PARAMETER);
		_isNotModifiable = _isFunction || facets.equals(CONST, TRUE) && !_isParameter;
		_isUpdatable =
			!_isNotModifiable && (facets.containsKey(VALUE) || facets.containsKey(UPDATE));

	}

	@Override
	public void dispose() {
		if ( isBuiltIn() ) { return; }
		if ( dependencies != null ) {
			dependencies.clear();
		}
		varExpr = null;
		super.dispose();
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
	}

	@Override
	public VariableDescription copy() {
		VariableDescription vd =
			new VariableDescription(getKeyword(), null, facets, null, getSourceInformation(), meta);
		vd.addHelpers(get, init, set);
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
			dependencies = new HashSet();
			final Set<String> names = new HashSet();
			IExpressionDescription depends = facets.get(DEPENDS_ON);
			if ( depends != null ) {
				List<String> dependsList =
					GAMA.getExpressionFactory().parseLiteralArray(depends, getSuperDescription());
				names.addAll(dependsList);
				for ( final String s : names ) {
					VariableDescription v = vars.get(s);
					if ( v != null ) {
						dependencies.add(v);
					}
				}
				dependencies.remove(this);
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

	public boolean isUpdatable() {
		return _isUpdatable;
	}

	public boolean isFunction() {
		return _isFunction;
	}

	public boolean isNotModifiable() {
		return _isNotModifiable;
	}

	public boolean isParameter() {
		return _isParameter;
	}

	@Override
	public IType getContentType() {
		if ( contentType != null && contentType != Types.NO_TYPE ) { return contentType; }
		String of = facets.getLabel(OF);
		if ( of != null ) {
			if ( "self".equals(of) ) {
				contentType = getTypeNamed(getSuperDescription().getName());
			} else if ( "instance".equals(of) ) {
				contentType = Types.NO_TYPE;
			} else {
				contentType = getTypeNamed(of);
			}
		}
		return contentType == null ? getType().defaultContentType() : contentType;
	}

	public IVarExpression getVarExpr() {
		if ( varExpr != null ) { return varExpr; }
		//
		varExpr =
			GAMA.getExpressionFactory().createVar(getName(), getType(), getContentType(),
				isNotModifiable(), _isGlobal ? IVarExpression.GLOBAL : IVarExpression.AGENT,
				this.getSuperDescription());
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
		String title = isParameter() ? "Parameter " : isNotModifiable() ? "Constant " : "Variable ";
		return title + "<b>" + getName() + "</b>";
		// TODO add type, etc...
	}

	public void addHelpers(final IVarGetter get, final IVarGetter init, final IVarSetter set) {
		this.get = get;
		this.set = set;
		this.init = init;
	}

	public IVarGetter getGetter() {
		return get;
	}

	public IVarGetter getIniter() {
		return init;
	}

	public IVarSetter getSetter() {
		return set;
	}

}
