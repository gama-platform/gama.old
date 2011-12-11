/*
 * GAMA - V1.4  http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2011
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2011
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen  (Batch, GeoTools & JTS), 2009-2011
 * - Beno”t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2011
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.internal.descriptions;

import java.util.*;
import msi.gama.interfaces.*;
import msi.gama.internal.expressions.*;
import msi.gama.internal.types.Types;
import msi.gama.kernel.exceptions.GamlException;
import msi.gama.lang.utils.ISyntacticElement;
import msi.gama.util.GamaList;
import msi.gaml.expressions.GamlExpressionParser;

/**
 * Written by drogoul Modified on 16 mai 2010
 * 
 * @todo Description
 * 
 */
public class VariableDescription extends SymbolDescription {

	private Set<VariableDescription>	dependencies;

	private List<String>				builtInDependencies;

	private boolean						isBuiltIn;

	private final boolean				isUserDefined;

	private int							definitionOrder	= -1;

	private IVarExpression				varExpr			= null;

	private IType						contentType		= null;

	private boolean						isGlobal;

	public VariableDescription(final String keyword, final IDescription superDesc,
		final Facets facets, final List<IDescription> children, final ISyntacticElement source)
		throws GamlException {
		super(keyword, superDesc, facets, children, source);
		isBuiltIn = source == null;
		isUserDefined = source != null;
		facets.putIfAbsent(ISymbol.TYPE, keyword);
		ExpressionDescription s = facets.getTokens(ISymbol.DEPENDS_ON);
		if ( s == null ) {
			builtInDependencies = new GamaList();
		} else {
			builtInDependencies = s;
		}

	}

	// public String typeAsString() {
	// return facets.getString(ISymbol.TYPE);
	// }

	@Override
	public void setSuperDescription(final IDescription s) throws GamlException {
		isGlobal = s != null && ISymbol.WORLD_SPECIES_NAME.equals(s.getName());
		super.setSuperDescription(s);
	}

	/**
	 * @param builtIn
	 */
	public void copyFrom(final VariableDescription v2) {
		isBuiltIn = v2.isBuiltIn;
		// isGlobal = v2.isGlobal;
		if ( isBuiltIn ) {
			builtInDependencies = v2.builtInDependencies;
		}
		facets.addAll(v2.facets);
	}

	@Override
	public String getKeyword() {
		String keyword = facets.getString(ISymbol.TYPE, super.getKeyword());
		if ( this.getModelDescription().getSpeciesDescription(keyword) != null &&
			!keyword.equals(ISymbol.SIGNAL) ) { return IType.AGENT_STR; }
		return keyword;
	}

	@Override
	public VariableDescription shallowCopy(final IDescription superDesc) throws GamlException {
		VariableDescription copy =
			new VariableDescription(getKeyword(), superDesc, facets, children, source);
		copy.isBuiltIn = this.isBuiltIn;
		return copy;
	}

	public boolean isUserDefined() {
		return isUserDefined;
	}

	public boolean isBuiltIn() {
		return isBuiltIn;
	}

	@Override
	public IType getType() {
		ModelDescription md = this.getModelDescription();
		if ( md != null ) { return md.getTypeOf(facets.getString(ISymbol.TYPE)); }

		return Types.get(facets.getString(ISymbol.TYPE));
	}

	public void setType(final IType type) {
		facets.putAsLabel(ISymbol.TYPE, type.toString());
	}

	public Set<VariableDescription> usedVariables(final ExecutionContextDescription context) {
		if ( dependencies != null ) { return dependencies; }
		dependencies = new HashSet();
		final Set<String> names = new HashSet();
		names.addAll(builtInDependencies);
		names.addAll(variablesUsedIn(context, facets.getTokens(ISymbol.INIT)));
		names.addAll(variablesUsedIn(context, facets.getTokens(ISymbol.VALUE)));
		names.addAll(variablesUsedIn(context, facets.getTokens(ISymbol.MIN)));
		names.addAll(variablesUsedIn(context, facets.getTokens(ISymbol.MAX)));
		names.addAll(variablesUsedIn(context, facets.getTokens(ISymbol.SIZE)));

		for ( final String s : names ) {
			VariableDescription v =
				((ExecutionContextDescription) getSuperDescription()).getVariable(s);
			if ( v != null ) {
				dependencies.add(v);
			}
		}
		dependencies.remove(this);
		return dependencies;
	}

	private Set<String> variablesUsedIn(final ExecutionContextDescription context,
		final ExpressionDescription facet) {
		final Set<String> vars = new HashSet();
		if ( facet == null ) { return vars; }
		final Set<String> all = new HashSet();
		for ( String s : facet ) {
			if ( GamlExpressionParser.isDottedExpr(s) ) {
				final String[] ss = s.split("\\.");
				for ( String name : ss ) {
					all.add(name);
				}
			} else {
				all.add(s);
			}
		}
		for ( final String s : all ) {
			if ( context.hasVar(s) ) {
				vars.add(s);
			}
		}

		return vars;
	}

	public void expandDependencies(final List<VariableDescription> without) throws GamlException {
		final Set<VariableDescription> accumulator = new HashSet();
		for ( final VariableDescription dep : dependencies ) {
			// if ( dep.getDependencies().contains(this) ) { throw new GamlException(getName() +
			// " and " + dep.getName() + " depend on each other." +
			// "Their value cannot be computed"); }
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
		return !isNotModifiable() && facets.containsKey(ISymbol.VALUE);
	}

	public boolean isNotModifiable() {
		return facets.equals(ISymbol.CONST, "true") && !isParameter();
	}

	public boolean isParameter() {
		return facets.containsKey(ISymbol.PARAMETER);
	}

	@Override
	public IType getContentType() {
		if ( contentType != null ) { return contentType; }
		String of = facets.getString(ISymbol.OF);
		if ( of != null ) {
			if ( "self".equals(of) ) {
				contentType = getTypeOf(getSuperDescription().getName());
			} else if ( "instance".equals(of) ) {
				contentType = Types.NO_TYPE;
			} else {
				contentType = getTypeOf(of);
			}
		}
		return contentType == null ? getType().defaultContentType() : contentType;
	}

	@Override
	public IType getTypeOf(final String s) {
		ExecutionContextDescription ecd = this.getSpeciesContext();
		if ( ecd != null ) { return ecd.getTypeOf(s); }
		return Types.get(s);
	}

	public IVarExpression getVarExpr(final IExpressionFactory f) {
		if ( varExpr != null ) { return varExpr; }
		varExpr =
			f.createVar(getName(), getType(), getContentType(), isNotModifiable(), isGlobal
				? IVarExpression.GLOBAL : IVarExpression.AGENT);
		return varExpr;
	}

	public void setContentType(final IType type) {
		// sent by the variable once its value and init are compiled
		if ( contentType != null && contentType != Types.NO_TYPE ) { return; }
		contentType = type;
		// if ( varExpr != null ) {
		// OutputManager.debug("Variable description " + getName() + " content type changed to " +
		// contentType.toString());
		// varExpr.setContentType(type);
		// }
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

	@Override
	public SpeciesDescription getSpeciesContext() {
		if ( enclosing == null ) { return super.getSpeciesContext(); }

		return enclosing.getSpeciesContext();
	}

}
