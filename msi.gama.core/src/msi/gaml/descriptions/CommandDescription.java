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
 * - Benoît Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gaml.descriptions;

import static msi.gama.common.interfaces.IKeyword.DO;
import java.util.*;
import msi.gama.common.interfaces.*;
import msi.gama.runtime.GAMA;
import msi.gaml.commands.Facets;
import msi.gaml.expressions.*;
import msi.gaml.factories.DescriptionFactory;
import msi.gaml.types.*;

/**
 * Written by drogoul Modified on 10 f√©vr. 2010
 * 
 * @todo Description
 * 
 */

public class CommandDescription extends SymbolDescription {

	private final Map<String, IVarExpression> temps;

	private final Map<String, IDescription> args;

	private final boolean verifyMandatoryArgs;

	private static int index = 0;

	public CommandDescription(final String keyword, final IDescription superDesc,
		final List<IDescription> children, final boolean hasScope, final boolean hasArgs,
		final ISyntacticElement source, final SymbolMetaDescription md) {
		super(keyword, superDesc, children, source, md);
		this.verifyMandatoryArgs = !keyword.equals(IKeyword.PRIMITIVE);

		if ( hasScope ) {
			temps = new HashMap();
		} else {
			temps = null;
		}
		if ( hasArgs ) {
			args = new HashMap();
			collectArgs();
		} else {
			args = null;
		}
	}

	@Override
	public void copyTempsAbove() {
		IDescription d = getSuperDescription();
		while (d != null && d instanceof CommandDescription) {
			if ( ((CommandDescription) d).hasTemps() ) {
				temps.putAll(((CommandDescription) d).temps);
			}
			d = d.getSuperDescription();
		}
	}

	private void collectArgs() {
		List<IDescription> argList = new ArrayList();
		explodeArgs(argList);
		exploreArgs(argList);
		children.addAll(argList);
		facets.remove(IKeyword.WITH);

		// Now puts all the args in the "args" list, removing them from children
		// Made here in case other args than the previous ones were declared this way.
		for ( IDescription c : children ) {
			if ( c.getKeyword().equals(IKeyword.ARG) ) {
				args.put(c.getName(), c);
			}
		}
		// if ( getKeyword().equals(IKeyword.DO) && !args.isEmpty() ) {
		// GuiUtils.debug("arguments derived from WITH :" + args);
		// }
		children.removeAll(args.values());
	}

	static final Set<String> builtin = SymbolMetaDescription.getAllowedFacetsFor(DO);
	static final Map<String, IExpressionDescription> retained = new HashMap();

	private void exploreArgs(final List<IDescription> argList) {
		if ( !getKeyword().equals(IKeyword.DO) ) { return; }
		for ( Map.Entry<String, IExpressionDescription> entry : facets.entrySet() ) {
			String facet = entry.getKey();
			if ( !builtin.contains(facet) ) {
				retained.put(facet, entry.getValue());
			}
		}
		addArgs(retained, argList);
		retained.clear();
	}

	private void explodeArgs(final List<IDescription> argList) {
		addArgs(GAMA.getExpressionFactory().createArgumentMap(facets.get(IKeyword.WITH), this),
			argList);
	}

	private void addArgs(final Map<String, IExpressionDescription> args,
		final List<IDescription> argList) {
		if ( args == null ) { return; }
		for ( Map.Entry<String, IExpressionDescription> arg : args.entrySet() ) {
			Facets f = new Facets(IKeyword.NAME, arg.getKey());
			f.put(IKeyword.VALUE, arg.getValue());
			argList.add(DescriptionFactory.createDescription(IKeyword.ARG, this, null, f));
		}
	}

	public IVarExpression addNewTempIfNecessary(final String facetName,
		final SymbolMetaDescription md, final String type, final String contentType,
		final IExpressionFactory f) {

		if ( md.isFacetDeclaringANewTemp(facetName) ) {
			String varName = facets.getLabel(facetName);
			if ( getSuperDescription() == null ||
				!(getSuperDescription() instanceof CommandDescription) ) {
				flagError("Impossible to return " + varName);
			}
			// TODO ExpressionParser.getInstance().verifyVarName(varName, getModel());
			return (IVarExpression) ((CommandDescription) getSuperDescription()).addTemp(varName,
				type == null ? Types.NO_TYPE : getTypeOf(type), contentType == null ? Types.NO_TYPE
					: getTypeOf(contentType), f);
		}
		return null;
	}

	public SpeciesDescription extractExtraSpeciesContext() {
		if ( facets.getLabel(IKeyword.KEYWORD).equals(IKeyword.CREATE) ) {
			SpeciesDescription species =
				getModelDescription().getSpeciesDescription(facets.getLabel(IKeyword.SPECIES));
			if ( species == null ) {
				species = getModelDescription().getSpeciesDescription(facets.getLabel(IKeyword.AS));
			}
			return species;
		}
		return null;
	}

	@Override
	public CommandDescription shallowCopy(final IDescription superDescription) {
		List<IDescription> children = new ArrayList();
		// GuiUtils.debug("" + this + " is being copied");
		// TODO Is it necessary to copy all the statements ? Only actions and primitive should be
		// copied, maybe arg ? Try to see why it is necessary...
		children.addAll(getChildren());
		if ( args != null ) {
			children.addAll(args.values());
		}
		return new CommandDescription(getKeyword(), null, children, temps != null, args != null,
			getSource(), meta);
	}

	@Override
	public boolean hasVar(final String name) {
		return temps != null && temps.containsKey(name);
	}

	@Override
	public IExpression addTemp(final String name, final IType type, final IType contentType,
		final IExpressionFactory f) {
		if ( temps == null ) {
			if ( getSuperDescription() == null ) { return null; }
			if ( !(getSuperDescription() instanceof CommandDescription) ) { return null; }
			return ((CommandDescription) getSuperDescription()).addTemp(name, type, contentType, f);
		}
		IVarExpression result = f.createVar(name, type, contentType, false, IVarExpression.TEMP);

		temps.put(name, result);
		return result;
	}

	@Override
	public IExpression getVarExpr(final String name, final IExpressionFactory factory) {
		if ( temps != null && temps.containsKey(name) ) { return temps.get(name); }
		return null;
	}

	public void verifyArgs(final IDescription caller, final Set<String> names) {
		if ( args == null ) { return; }
		List<String> mandatoryArgs = new ArrayList();
		Set<String> allArgs = args.keySet();
		for ( IDescription c : args.values() ) {
			String n = c.getName();
			if ( !c.getFacets().containsKey(IKeyword.DEFAULT) ) {
				mandatoryArgs.add(n);
			}
		}
		if ( verifyMandatoryArgs ) {
			for ( String arg : mandatoryArgs ) {
				if ( !names.contains(arg) ) {
					caller.flagError("Missing argument " + arg + " in call to " + getName() +
						". Arguments passed are : " + names);
				}
			}
		}
		for ( String arg : names ) {
			if ( !allArgs.contains(arg) ) {
				caller.flagError("Unknown argument" + arg + " in call to " + getName());
			}
		}
	}

	public List<IDescription> getArgs() {
		List<IDescription> result = new ArrayList();
		if ( args != null ) {
			result.addAll(args.values());
		}
		return result;
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
			if ( getKeyword().equals(IKeyword.ASPECT) ) {
				s = IKeyword.DEFAULT;
			} else {
				if ( getKeyword().equals(IKeyword.REFLEX) ) {
					flagWarning("Reflexes should be named", null);
				}
				s = getKeyword() + String.valueOf(index++);
			}
			facets.putAsLabel(IKeyword.NAME, s);
		}
		return s;
	}

	public IType getReturnType() {
		return getTypeOf(facets.getLabel(IKeyword.TYPE));
	}

	public IType getReturnContentType() {
		return getTypeOf(facets.getLabel(IKeyword.OF));
	}

	@Override
	public String toString() {
		return getKeyword() + " " + getName();
	}

	@Override
	public IType getTypeOf(final String s) {
		if ( s == null ) { return Types.NO_TYPE; }
		IDescription species = getSpeciesContext();
		return species == null ? Types.NO_TYPE : species.getTypeOf(s);
	}

}
