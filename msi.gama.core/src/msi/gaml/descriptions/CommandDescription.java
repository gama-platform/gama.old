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
import msi.gama.util.GamaList;
import msi.gaml.commands.Facets;
import msi.gaml.compilation.GamlException;
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
		final Facets facets, final List<IDescription> children, final boolean hasScope,
		final boolean hasArgs, final ISyntacticElement source, final SymbolMetaDescription md) {
		super(keyword, superDesc, facets, children, source, md);
		this.verifyMandatoryArgs = !keyword.equals(IKeyword.PRIMITIVE);

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
			try {
				collectArgs();
			} catch (GamlException e) {
				superDesc.flagError(e);
			}
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
		if ( facets.containsKey(IKeyword.WITH) ) {
			List<IDescription> argList = explodeArgs(facets.getString(IKeyword.WITH));
			children.addAll(argList);
			facets.remove(IKeyword.WITH);
		}
		for ( IDescription c : children ) {
			if ( c.getKeyword().equals(IKeyword.ARG) ) {
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
				if ( endString.equals(IKeyword.OPEN_LIST) || endString.equals(IKeyword.OPEN_POINT) ) {
					listLevel++;
				} else if ( endString.equals(IKeyword.CLOSE_LIST) ||
					endString.equals(IKeyword.CLOSE_POINT) ) {
					listLevel--;
				} else if ( IKeyword.COMMA.equals(endString) && listLevel == 0 ) {
					break;
				}
				end++;
			}
			int parenthesis = 0;
			if ( words.get(begin).equals(IKeyword.OPEN_EXP) ) {
				parenthesis = 1;
			}
			String arg = words.get(begin + parenthesis);
			String sep = words.get(begin + parenthesis + 1);
			if ( !sep.equals("::") ) { throw new GamlException(
				"Arguments must be provided as pairs arg::value; " +
					words.subConcatenation(begin, end) + " is not a pair",
				this.getSourceInformation()); }
			String expr = words.subConcatenation(begin + parenthesis + 2, end - parenthesis);
			// String[] facetArray = new String[] { IKeyword.NAME, arg, IKeyword.VALUE, expr };
			// GuiUtils.debug("Found a new argument:" + Arrays.toString(facetArray));
			argList.add(DescriptionFactory.createDescription(IKeyword.ARG, this, IKeyword.NAME,
				arg, IKeyword.VALUE, expr));
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
				"Impossible to return " + varName, getSourceInformation()); }
			// TODO ExpressionParser.getInstance().verifyVarName(varName, getModel());
			return (IVarExpression) ((CommandDescription) getSuperDescription()).addTemp(varName,
				type == null ? Types.NO_TYPE : getTypeOf(type), contentType == null ? Types.NO_TYPE
					: getTypeOf(contentType), f);
		}
		return null;
	}

	public SpeciesDescription extractExtraSpeciesContext() {
		if ( facets.getString(IKeyword.KEYWORD).equals(IKeyword.CREATE) ) {
			SpeciesDescription species =
				getModelDescription().getSpeciesDescription(facets.getString(IKeyword.SPECIES));
			if ( species == null ) {
				species =
					getModelDescription().getSpeciesDescription(facets.getString(IKeyword.AS));
			}
			return species;
		}
		return null;
	}

	@Override
	public CommandDescription shallowCopy(final IDescription superDescription) {
		List<IDescription> children = new ArrayList();
		children.addAll(getChildren());
		if ( args != null ) {
			children.addAll(args.values());
		}
		return new CommandDescription(getKeyword(), null, facets, children, temps != null,
			args != null, getSource(), meta);
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

	public void verifyArgs(final Set<String> names) throws GamlException {
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
				if ( !names.contains(arg) ) { throw new GamlException("Missing argument " + arg +
					" in call to " + getName(), getSourceInformation()); }
			}
		}
		for ( String arg : names ) {
			if ( !allArgs.contains(arg) ) { throw new GamlException("Unknown argument" + arg +
				" in call to " + getName(), getSourceInformation()); }
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
				s = getKeyword() + String.valueOf(index++);
			}
			facets.putAsLabel(IKeyword.NAME, s);
		}
		return s;
	}

	public IType getReturnType() {
		return getTypeOf(facets.getString(IKeyword.TYPE));
	}

	public IType getReturnContentType() {
		return getTypeOf(facets.getString(IKeyword.OF));
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
