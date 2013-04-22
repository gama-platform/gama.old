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

import java.util.*;
import msi.gama.common.interfaces.*;
import msi.gama.runtime.GAMA;
import msi.gama.util.GamaList;
import msi.gaml.compilation.IPrimRun;
import msi.gaml.expressions.*;
import msi.gaml.factories.*;
import msi.gaml.statements.*;
import msi.gaml.statements.Facets.Facet;
import msi.gaml.types.*;
import org.eclipse.emf.ecore.EObject;

/**
 * Written by drogoul Modified on 10 f√©vr. 2010
 * 
 * @todo Description
 * 
 */

public class StatementDescription extends SymbolDescription {

	private IPrimRun helper; // TODO Only used by primitives. Should be in a subclass.
	private final Map<String, IVarExpression> temps;
	private final Map<String, IDescription> args;
	private final static String INTERNAL = "internal_";
	private static int COMMAND_INDEX = 0;
	static final Set<String> doFacets = DescriptionFactory.getAllowedFacetsFor(DO);

	public IPrimRun getHelper() {
		return helper;
	}

	public void setHelper(final IPrimRun helper) {
		this.helper = helper;
	}

	public StatementDescription(final String keyword, final IDescription superDesc, final IChildrenProvider cp,
		final boolean hasScope, final boolean hasArgs, final EObject source, Facets facets) {
		super(keyword, superDesc, cp, source, facets);
		temps = hasScope ? new LinkedHashMap() : null;
		args = hasArgs ? new LinkedHashMap() : null;
		if ( hasArgs ) {
			collectArgs();
		}
	}

	@Override
	public void dispose() {
		if ( isBuiltIn() ) { return; }
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
		while (d != null && d instanceof StatementDescription) {
			if ( ((StatementDescription) d).hasTemps() ) {
				temps.putAll(((StatementDescription) d).temps);
			}
			d = d.getSuperDescription();
		}
	}

	private void collectArgs() {
		for ( Iterator<IDescription> it = getChildren().iterator(); it.hasNext(); ) {
			IDescription c = it.next();
			if ( c.getKeyword().equals(ARG) ) {
				args.put(c.getName(), c);
				it.remove();
			}
		}
		explodeArgs();
		exploreArgs();
	}

	private void exploreArgs() {
		if ( !getKeyword().equals(DO) ) { return; }
		for ( Map.Entry<String, IExpressionDescription> entry : facets.entrySet() ) {
			if ( entry == null ) {
				continue;
			}
			String facet = entry.getKey();
			if ( !doFacets.contains(facet) ) {
				args.put(facet, createArg(facet, entry.getValue()));
			}
		}
	}

	private IDescription createArg(final String n, final IExpressionDescription v) {
		Facets f = new Facets(NAME, n);
		f.put(VALUE, v);
		IDescription a = DescriptionFactory.create(ARG, this, null, f);
		return a;
	}

	private void explodeArgs() {
		if ( getKeyword().equals(ACTION) || getKeyword().equals(PRIMITIVE) ) { return; }
		for ( Map.Entry<String, IExpressionDescription> arg : GAMA.getExpressionFactory()
			.createArgumentMap(getAction(), facets.get(WITH), this).entrySet() ) {
			String name = arg.getKey();
			args.put(name, createArg(name, arg.getValue()));
		}
		facets.remove(WITH);
	}

	private StatementDescription getAction() {
		String actionName = getFacets().getLabel(IKeyword.ACTION);
		if ( actionName == null ) { return null; }
		TypeDescription declPlace = (TypeDescription) getDescriptionDeclaringAction(actionName);
		StatementDescription executer = null;
		if ( declPlace != null ) {
			executer = declPlace.getAction(actionName);
		}
		return executer;
	}

	public IVarExpression addNewTempIfNecessary(final String facetName, final IType type, final IType contentType,
		final IType keyType) {
		String varName = facets.getLabel(facetName);
		if ( facetName.equals(VAR) ) {
			// Case of loops
			return (IVarExpression) addTemp(varName, type, contentType, keyType);
		}

		IDescription sup = getSuperDescription();
		if ( !(sup instanceof StatementDescription) ) {
			error("Impossible to return " + facets.getLabel(facetName), IGamlIssue.GENERAL);
			return null;
		}
		return (IVarExpression) ((StatementDescription) sup).addTemp(varName, type, contentType, keyType);
	}

	@Override
	public StatementDescription copy(final IDescription into) {
		List<IDescription> children = new ArrayList();
		for ( IDescription child : getChildren() ) {
			children.add(child.copy(into));
		}
		if ( args != null ) {
			for ( IDescription child : args.values() ) {
				children.add(child.copy(into));
			}
		}
		StatementDescription desc =
			new StatementDescription(getKeyword(), into, new ChildrenProvider(children), temps != null, args != null,
				element, facets);
		desc.originName = originName;
		desc.setHelper(helper);
		return desc;
	}

	@Override
	public boolean hasVar(final String name) {
		return temps != null && temps.containsKey(name);
	}

	@Override
	public IExpression addTemp(final String name, final IType type, final IType contentType, final IType keyType) {
		if ( temps == null ) {
			if ( getSuperDescription() == null ) { return null; }
			if ( !(getSuperDescription() instanceof StatementDescription) ) { return null; }
			return ((StatementDescription) getSuperDescription()).addTemp(name, type, contentType, keyType);
		}
		IVarExpression result =
			GAMA.getExpressionFactory().createVar(name, type, contentType, keyType, false, IVarExpression.TEMP, this);
		temps.put(name, result);
		return result;
	}

	@Override
	public IExpression getVarExpr(final String name) {
		if ( temps != null && temps.containsKey(name) ) { return temps.get(name); }
		return null;
	}

	public void verifyArgs(final IDescription caller, final Arguments names) {
		// if ( args == null ) { return; }
		Set<String> allArgs = args == null ? Collections.EMPTY_SET : args.keySet();
		if ( caller.getKeyword().equals(DO) ) {
			// If the names were not known at the time of the creation of the caller, only the order
			if ( names.containsKey("0") ) {
				int index = 0;
				for ( String name : allArgs ) {
					String key = String.valueOf(index++);
					IExpressionDescription old = names.get(key);
					if ( old != null ) {
						names.put(name, old);
						names.remove(key);
					}
				}
			}
		}

		// We compute the list of mandatory args
		List<String> mandatoryArgs = new ArrayList();

		for ( IDescription c : args.values() ) {
			String n = c.getName();
			if ( !c.getFacets().containsKey(DEFAULT) ) {
				// GuiUtils.debug("Mandatory arg: " + n);
				mandatoryArgs.add(n);
			}
		}
		// If one is missing in the arguments passed, we raise an error
		// (except for primitives for the moment)
		if ( !getKeyword().equals(PRIMITIVE) ) {
			for ( String arg : mandatoryArgs ) {
				if ( !names.containsKey(arg) ) {
					caller.error("Missing argument " + arg + " in call to " + getName() + ". Arguments passed are : " +
						names, IGamlIssue.MISSING_ARGUMENT, caller.getUnderlyingElement(null), new String[] { arg });
				}
			}
		}
		for ( Facet arg : names.entrySet() ) {
			if ( arg != null ) {
				String name = arg.getKey();
				if ( !allArgs.contains(name) ) {
					caller.error("Unknown argument " + name + " in call to " + getName(), IGamlIssue.UNKNOWN_ARGUMENT,
						arg.getValue().getTarget(), new String[] { arg.getKey() });
				} else {
					IType formalType = args.get(name).getType();
					IType callerType = arg.getValue().getExpression().getType();
					if ( formalType != Types.NO_TYPE && !callerType.isTranslatableInto(formalType) ) {
						caller
							.error("The type of argument " + name + " should be " + formalType, IGamlIssue.WRONG_TYPE);
					} else if ( formalType.hasContents() ) {
						formalType = args.get(name).getContentType();
						callerType = arg.getValue().getExpression().getContentType();
						if ( formalType != Types.NO_TYPE && !callerType.isTranslatableInto(formalType) ) {
							caller.error("The content type of argument " + name + " should be " + formalType,
								IGamlIssue.WRONG_TYPE);
						}
					}
				}
			}
		}
	}

	public void verifyArgs(final String actionName, final Arguments args) {
		StatementDescription executer = getAction();
		if ( executer == null ) {
			// error("Unknown action " + actionName, ACTION);
			return;
		}

		executer.verifyArgs(this, args);
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
			if ( getKeyword().equals(ASPECT) ) {
				s = DEFAULT;
			} else {
				if ( getKeyword().equals(REFLEX) ) {
					warning("Reflexes should be named", IGamlIssue.MISSING_NAME, getUnderlyingElement(null));
				}
				s = INTERNAL + getKeyword() + String.valueOf(COMMAND_INDEX++);
			}
			facets.putAsLabel(NAME, s);
		}
		return s;
	}

	@Override
	public IType getType() {
		return getTypeNamed(facets.getLabel(TYPE));
	}

	@Override
	public IType getContentType() {
		if ( facets.containsKey(OF) ) { return getTypeNamed(facets.getLabel(OF)); }
		return getType().defaultContentType();
	}

	@Override
	public IType getKeyType() {
		if ( facets.containsKey(INDEX) ) { return getTypeNamed(facets.getLabel(INDEX)); }
		return getType().defaultKeyType();
	}

	@Override
	public String toString() {
		return getKeyword() + " " + getName();
	}

	/**
	 * @return
	 */
	public List<String> getArgNames() {
		return args == null ? Collections.EMPTY_LIST : new GamaList(args.keySet());
	}

	@Override
	public String getTitle() {
		String kw = getKeyword();
		// kw = Character.toUpperCase(kw.charAt(0)) + kw.substring(1);
		String name = getName();
		if ( name.contains(INTERNAL) ) {
			name = facets.getLabel(ACTION);
			if ( name == null ) {
				name = "statement";
			}
		}
		String in = "";
		if ( meta.isTopLevel() ) {
			in = " of " + getSuperDescription().getTitle();
		}
		return kw + " " + getName() + " " + in;
	}

	/**
	 * @return
	 */
	public boolean isAbstract() {
		return TRUE.equals(facets.getLabel(VIRTUAL));
		// return !getKeyword().equals(PRIMITIVE) && getChildren().isEmpty();
	}

	public void collectChildren(String keyword, Set<StatementDescription> returns) {
		if ( getKeyword().equals(keyword) ) {
			returns.add(this);
		} else if ( children != null ) {
			for ( IDescription child : children ) {
				if ( child instanceof StatementDescription ) {
					((StatementDescription) child).collectChildren(keyword, returns);
				}
			}
		}
	}
}
