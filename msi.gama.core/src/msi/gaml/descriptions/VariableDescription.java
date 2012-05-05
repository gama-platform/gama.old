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

import java.util.*;
import msi.gama.common.interfaces.*;
import msi.gama.runtime.GAMA;
import msi.gaml.commands.Facets;
import msi.gaml.expressions.IVarExpression;
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
	private IType contentType = null;
	private boolean isGlobal;

	public VariableDescription(final String keyword, final IDescription superDesc,
		final Facets facets, final List<IDescription> children, final ISyntacticElement source,
		final SymbolMetaDescription md) {
		super(keyword, superDesc, children, source, md);
		if ( !facets.containsKey(IKeyword.TYPE) &&
			!facets.equals(IKeyword.KEYWORD, IKeyword.PARAMETER) ) {
			facets.putAsLabel(IKeyword.TYPE, keyword);
		}
	}

	@Override
	public void dispose() {
		if ( builtIn ) { return; }
		if ( dependencies != null ) {
			dependencies.clear();
		}
		varExpr = null;
		super.dispose();
	}

	@Override
	public void setSuperDescription(final IDescription s) {
		isGlobal = s != null && IKeyword.WORLD_SPECIES_NAME.equals(s.getName());
		super.setSuperDescription(s);
	}

	public void copyFrom(final VariableDescription v2) {
		builtIn = v2.builtIn;
		// Without replacing
		for ( Map.Entry<String, IExpressionDescription> entry : v2.facets.entrySet() ) {
			if ( entry == null ) {
				continue;
			}
			if ( !facets.containsKey(entry.getKey()) ) {
				facets.put(entry.getKey(), entry.getValue());
			}
		}
	}

	@Override
	public VariableDescription copy() {
		VariableDescription v2 =
			new VariableDescription(getKeyword(), null, facets, null, getSourceInformation(), meta);
		v2.builtIn = builtIn;
		return v2;
	}

	public boolean isBuiltIn() {
		return builtIn;
	}

	@Override
	public IType getType() {
		return getTypeNamed(facets.getLabel(IKeyword.TYPE));
	}

	public Set<VariableDescription> usedVariablesIn(final Map<String, VariableDescription> vars) {
		if ( dependencies == null ) {
			dependencies = new HashSet();
			final Set<String> names = new HashSet();
			IExpressionDescription depends = facets.get(IKeyword.DEPENDS_ON);
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
		return !isNotModifiable() &&
			(facets.containsKey(IKeyword.VALUE) || facets.containsKey(IKeyword.UPDATE));
	}

	public boolean isFunction() {
		return facets.containsKey(IKeyword.FUNCTION);
	}

	public boolean isNotModifiable() {
		return isFunction() || facets.equals(IKeyword.CONST, "true") && !isParameter();
	}

	public boolean isParameter() {
		return facets.containsKey(IKeyword.PARAMETER) ||
			facets.equals(IKeyword.KEYWORD, IKeyword.PARAMETER);
	}

	@Override
	public IType getContentType() {
		if ( contentType != null && contentType != Types.NO_TYPE ) { return contentType; }
		String of = facets.getLabel(IKeyword.OF);
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
		varExpr =
			GAMA.getExpressionFactory().createVar(getName(), getType(), getContentType(),
				isNotModifiable(), isGlobal ? IVarExpression.GLOBAL : IVarExpression.AGENT,
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
		final String pName = facets.getLabel(IKeyword.PARAMETER);
		if ( pName == null || pName.equals(IKeyword.TRUE) ) { return getName(); }
		return pName;
	}

	@Override
	public String getTitle() {
		String title = isParameter() ? "Parameter " : isNotModifiable() ? "Constant " : "Variable ";
		return title + "<b>" + getName() + "</b>";
		// TODO add type, etc...
	}

}
