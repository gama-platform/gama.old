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
import msi.gaml.commands.*;
import msi.gaml.expressions.*;
import msi.gaml.factories.DescriptionFactory;
import msi.gaml.types.IType;

/**
 * Written by drogoul Modified on 10 f√©vr. 2010
 * 
 * @todo Description
 * 
 */

public class CommandDescription extends SymbolDescription {

	private final Map<String, IVarExpression> temps;
	private Map<String, IDescription> args = null;
	private final static String INTERNAL = "internal_";
	private static int COMMAND_INDEX = 0;
	static final Set<String> doFacets = DescriptionFactory.getAllowedFacetsFor(DO);

	public CommandDescription(final String keyword, final IDescription superDesc,
		final List<IDescription> children, final boolean hasScope, final boolean hasArgs,
		final ISyntacticElement source, final SymbolMetaDescription md) {
		super(keyword, superDesc, children, source, md);
		temps = hasScope ? new HashMap() : null;
		if ( hasArgs ) {
			collectArgs();
		}
	}

	@Override
	public void dispose() {
		if ( temps != null ) {
			temps.clear();
		}
		if ( args != null ) {
			args.clear();
		}
		super.dispose();
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
		args = new HashMap();
		for ( Iterator<IDescription> it = getChildren().iterator(); it.hasNext(); ) {
			IDescription c = it.next();
			if ( c.getKeyword().equals(IKeyword.ARG) ) {
				args.put(c.getName(), c);
				it.remove();
			}
		}
		explodeArgs();
		exploreArgs();
	}

	private void exploreArgs() {
		if ( !getKeyword().equals(IKeyword.DO) ) { return; }
		for ( Map.Entry<String, IExpressionDescription> entry : facets.entrySet() ) {
			String facet = entry.getKey();
			if ( !doFacets.contains(facet) ) {
				Facets f = new Facets(IKeyword.NAME, facet);
				f.put(IKeyword.VALUE, entry.getValue());
				args.put(facet, DescriptionFactory.createDescription(IKeyword.ARG, this, null, f));
			}
		}
	}

	private void explodeArgs() {
		addArgs(GAMA.getExpressionFactory().createArgumentMap(facets.get(IKeyword.WITH), this));
		facets.remove(IKeyword.WITH);
	}

	private void addArgs(final Map<String, IExpressionDescription> arguments) {
		if ( arguments == null ) { return; }
		for ( Map.Entry<String, IExpressionDescription> arg : arguments.entrySet() ) {
			String name = arg.getKey();
			Facets f = new Facets(IKeyword.NAME, name);
			f.put(IKeyword.VALUE, arg.getValue());
			args.put(name, DescriptionFactory.createDescription(IKeyword.ARG, this, null, f));
		}
	}

	public IVarExpression addNewTempIfNecessary(final String facetName, final IType type,
		final IType contentType) {
		IDescription sup = getSuperDescription();
		if ( !(sup instanceof CommandDescription) ) {
			flagError("Impossible to return " + facets.getLabel(facetName), IGamlIssue.GENERAL);
			return null;
		}
		String varName = facets.getLabel(facetName);
		return (IVarExpression) ((CommandDescription) sup).addTemp(varName, type, contentType);
	}

	@Override
	public CommandDescription copy() {
		List<IDescription> children = new ArrayList();
		for ( IDescription child : getChildren() ) {
			children.add(child.copy());
		}
		if ( args != null ) {
			for ( IDescription child : args.values() ) {
				children.add(child.copy());
			}
		}
		return new CommandDescription(getKeyword(), null, children, temps != null, args != null,
			getSourceInformation(), meta);
	}

	@Override
	public boolean hasVar(final String name) {
		return temps != null && temps.containsKey(name);
	}

	@Override
	public IExpression addTemp(final String name, final IType type, final IType contentType) {
		if ( temps == null ) {
			if ( getSuperDescription() == null ) { return null; }
			if ( !(getSuperDescription() instanceof CommandDescription) ) { return null; }
			return ((CommandDescription) getSuperDescription()).addTemp(name, type, contentType);
		}
		IVarExpression result =
			GAMA.getExpressionFactory().createVar(name, type, contentType, false,
				IVarExpression.TEMP, this);
		temps.put(name, result);
		return result;
	}

	@Override
	public IExpression getVarExpr(final String name) {
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
		if ( !getKeyword().equals(IKeyword.PRIMITIVE) ) {
			for ( String arg : mandatoryArgs ) {
				if ( !names.contains(arg) ) {
					caller.flagError("Missing argument " + arg + " in call to " + getName() +
						". Arguments passed are : " + names, IGamlIssue.MISSING_ARGUMENT, null,
						new String[] { arg });
				}
			}
		}
		for ( String arg : names ) {
			if ( !allArgs.contains(arg) ) {
				caller.flagError("Unknown argument" + arg + " in call to " + getName(),
					IGamlIssue.UNKNOWN_ARGUMENT, null, new String[] { arg });
			}
		}
	}

	public void verifyArgs(final String actionName, final Arguments args) {
		SpeciesDescription declPlace =
			(SpeciesDescription) getDescriptionDeclaringAction(actionName);
		CommandDescription executer = null;
		if ( declPlace != null ) {
			executer = declPlace.getAction(actionName);
		}
		if ( executer == null ) {
			flagError("Unknown action " + actionName, IKeyword.ACTION);
			return;
		}
		executer.verifyArgs(this, args.keySet());
	}

	public Collection<IDescription> getArgs() {
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
			if ( getKeyword().equals(IKeyword.ASPECT) ) {
				s = IKeyword.DEFAULT;
			} else {
				if ( getKeyword().equals(IKeyword.REFLEX) ) {
					flagWarning("Reflexes should be named", IGamlIssue.MISSING_NAME, null);
				}
				s = INTERNAL + getKeyword() + String.valueOf(COMMAND_INDEX++);
			}
			facets.putAsLabel(IKeyword.NAME, s);
		}
		return s;
	}

	public IType getReturnType() {
		return getTypeNamed(facets.getLabel(IKeyword.TYPE));
	}

	public IType getReturnContentType() {
		return getTypeNamed(facets.getLabel(IKeyword.OF));
	}

	@Override
	public String toString() {
		return getKeyword() + " " + getName();
	}

	/**
	 * @return
	 */
	public Set<String> getArgNames() {
		return args == null ? Collections.EMPTY_SET : args.keySet();
	}

	@Override
	public String getTitle() {
		String kw = getKeyword();
		kw = Character.toUpperCase(kw.charAt(0)) + kw.substring(1);
		String name = getName();
		if ( name.contains(INTERNAL) ) {
			name = facets.getLabel(IKeyword.ACTION);
			if ( name == null ) {
				name = "statement";
			}
		}
		String in = "";
		if ( meta.isTopLevel() ) {
			in = " of " + getSuperDescription().getTitle();
		}
		return kw + " <b>" + getName() + "</b> " + in;
	}

	/**
	 * @return
	 */
	public boolean isAbstract() {
		return !getKeyword().equals(IKeyword.PRIMITIVE) && getChildren().isEmpty();
	}

}
