/*
 * GAMA - V1.4 http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC
 * 
 * Developers :
 * 
 * - Alexis Drogoul, IRD (Kernel, Metamodel, XML-based GAML), 2007-2011
 * - Vo Duc An, IRD & AUF (SWT integration, multi-level architecture), 2008-2011
 * - Patrick Taillandier, AUF & CNRS (batch framework, GeoTools & JTS integration), 2009-2011
 * - Pierrick Koch, IRD (XText-based GAML environment), 2010-2011
 * - Romain Lavaud, IRD (project-based environment), 2010
 * - Francois Sempe, IRD & AUF (EMF behavioral model, batch framework), 2007-2009
 * - Edouard Amouroux, IRD (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, IRD (OpenMap integration), 2007-2008
 */
package msi.gama.internal.descriptions;

import java.util.*;
import msi.gama.factories.DescriptionFactory;
import msi.gama.gui.application.GUI;
import msi.gama.interfaces.*;
import msi.gama.internal.expressions.*;
import msi.gama.internal.types.Types;
import msi.gama.kernel.exceptions.GamlException;
import msi.gama.lang.utils.ISyntacticElement;
import msi.gama.util.GamaList;

/**
 * Written by drogoul Modified on 10 févr. 2010
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
		final Facets facets, final List<IDescription> children, final boolean hasScope,
		final boolean hasArgs, final ISyntacticElement source) throws GamlException {
		super(keyword, superDesc, facets, children, source);
		this.verifyMandatoryArgs = !keyword.equals(ISymbol.PRIMITIVE);

		/*
		 * if ("create".equals(this.getKeyword()) && "species1".equals(superDesc.getName())) {
		 * System.out.println();
		 * }
		 */

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

	private void collectArgs() throws GamlException {
		if ( facets.containsKey(ISymbol.WITH) ) {
			List<IDescription> argList = explodeArgs(facets.getString(ISymbol.WITH));
			children.addAll(argList);
			facets.remove(ISymbol.WITH);
		}
		for ( IDescription c : children ) {
			if ( c.getKeyword().equals(ISymbol.ARG) ) {
				args.put(c.getName(), c);
			}
		}
		children.removeAll(args.values());
	}

	private List<IDescription> explodeArgs(final String args) throws GamlException {
		List<IDescription> argList = new GamaList();
		ExpressionDescription words = new ExpressionDescription(args, true);
		words = new ExpressionDescription(words.subList(1, words.size() - 1));
		int begin = 0;
		int end = 0;
		int listLevel = 0;
		while (begin < words.size()) {
			while (end < words.size()) {
				String endString = words.get(end);
				if ( endString.equals(IExpressionParser.OPEN_LIST) ||
					endString.equals(IExpressionParser.OPEN_POINT) ) {
					listLevel++;
				} else if ( endString.equals(IExpressionParser.CLOSE_LIST) ||
					endString.equals(IExpressionParser.CLOSE_POINT) ) {
					listLevel--;
				} else if ( IExpressionParser.COMMA.equals(endString) && listLevel == 0 ) {
					break;
				}
				end++;
			}
			int parenthesis = 0;
			if ( words.get(begin).equals(IExpressionParser.OPEN_EXP) ) {
				parenthesis = 1;
			}
			String arg = words.get(begin + parenthesis);
			String sep = words.get(begin + parenthesis + 1);
			if ( !sep.equals("::") ) { throw new GamlException(
				"Arguments must be provided as pairs arg::value; " +
					words.subConcatenation(begin, end) + " is not a pair"); }
			String expr = words.subConcatenation(begin + parenthesis + 2, end - parenthesis);
			String[] facetArray = new String[] { ISymbol.NAME, arg, ISymbol.VALUE, expr };
			GUI.debug("Found a new argument:" + Arrays.toString(facetArray));
			argList.add(DescriptionFactory.createDescription(ISymbol.ARG, this, ISymbol.NAME, arg,
				ISymbol.VALUE, expr));
			begin = end + 1;
			end = begin;
		}
		return argList;
	}

	public IVarExpression addNewTempIfNecessary(final String facetName,
		final SymbolMetaDescription md, final String type, final String contentType,
		final IExpressionFactory f) throws GamlException {

		if ( md.isFacetDeclaringANewTemp(facetName) ) {
			String varName = facets.getString(facetName);
			if ( getSuperDescription() == null ||
				!(getSuperDescription() instanceof CommandDescription) ) { throw new GamlException(
				"Impossible to return " + varName); }
			// TODO ExpressionParser.getInstance().verifyVarName(varName, getModel());
			return (IVarExpression) ((CommandDescription) getSuperDescription()).addTemp(varName,
				type == null ? Types.NO_TYPE : getTypeOf(type), contentType == null ? Types.NO_TYPE
					: getTypeOf(contentType), f);
		}
		return null;
	}

	public SpeciesDescription extractExtraSpeciesContext() {
		if ( facets.getString(ISymbol.KEYWORD).equals(ISymbol.CREATE) ) {
			SpeciesDescription species =
				getModelDescription().getSpeciesDescription(facets.getString(ISymbol.SPECIES));
			if ( species == null ) {
				species = getModelDescription().getSpeciesDescription(facets.getString(ISymbol.AS));
			}
			return species;
		}
		return null;
	}

	@Override
	public CommandDescription shallowCopy(final IDescription superDescription) throws GamlException {
		List<IDescription> children = new ArrayList();
		children.addAll(getChildren());
		if ( args != null ) {
			children.addAll(args.values());
		}
		return new CommandDescription(getKeyword(), null, facets, children, temps != null,
			args != null, source);
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
	public IExpression getVarExpr(final String name, final IExpressionFactory f) {
		if ( temps != null && temps.containsKey(name) ) { return temps.get(name); }
		return null;
	}

	public void verifyArgs(final Set<String> names) throws GamlException {
		if ( args == null ) { return; }
		List<String> mandatoryArgs = new ArrayList();
		Set<String> allArgs = args.keySet();
		for ( IDescription c : args.values() ) {
			String n = c.getName();
			if ( !c.getFacets().containsKey(ISymbol.DEFAULT) ) {
				mandatoryArgs.add(n);
			}
		}
		if ( verifyMandatoryArgs ) {
			for ( String arg : mandatoryArgs ) {
				if ( !names.contains(arg) ) { throw new GamlException("Missing argument " + arg +
					" in call to " + getName()); }
			}
		}
		for ( String arg : names ) {
			if ( !allArgs.contains(arg) ) { throw new GamlException("Unknown argument" + arg +
				" in call to " + getName()); }
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
			if ( getKeyword().equals(ISymbol.ASPECT) ) {
				s = ISymbol.DEFAULT;
			} else {
				s = getKeyword() + String.valueOf(index++);
			}
			facets.putAsLabel(ISymbol.NAME, s);
		}
		return s;
	}

	public IType getReturnType() {
		return getTypeOf(facets.getString(ISymbol.TYPE));
	}

	public IType getReturnContentType() {
		return getTypeOf(facets.getString(ISymbol.OF));
	}

	@Override
	public String toString() {
		return getKeyword() + " " + getName();
	}

	@Override
	public IType getTypeOf(final String s) {
		if ( s == null ) { return Types.NO_TYPE; }
		return this.getSpeciesContext().getTypeOf(s);
	}

	@Override
	public SpeciesDescription getSpeciesContext() {
		return enclosing.getSpeciesContext();
	}
}
