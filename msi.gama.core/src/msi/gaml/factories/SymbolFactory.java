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
package msi.gaml.factories;

import static msi.gama.common.interfaces.IKeyword.*;
import static msi.gaml.factories.DescriptionValidator.verifyFacetsType;
import java.util.*;
import msi.gama.common.interfaces.*;
import msi.gama.precompiler.GamlAnnotations.handles;
import msi.gama.precompiler.*;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.compilation.ISymbol;
import msi.gaml.descriptions.*;
import msi.gaml.statements.*;
import msi.gaml.statements.Facets.Facet;

/**
 * Written by Alexis Drogoul Modified on 11 mai 2010
 * 
 * @todo Description
 * 
 */
@handles({ ISymbolKind.ENVIRONMENT, ISymbolKind.ABSTRACT_SECTION })
public class SymbolFactory extends AbstractFactory {

	public SymbolFactory(final List<Integer> handles, final List<Integer> uses) {
		super(handles, uses);
	}

	@Override
	public final SymbolMetaDescription getMetaDescriptionFor(final IDescription context,
		final String keyword) {
		SymbolMetaDescription md = registeredSymbols.get(keyword);
		if ( md == null ) {
			String upper = context == null ? null : context.getKeyword();
			ISymbolFactory f = chooseFactoryFor(keyword, upper);
			if ( f == null ) {
				if ( context != null ) {
					context.flagError("Unknown symbol " + keyword, IGamlIssue.UNKNOWN_KEYWORD,
						null, keyword, upper);
				}
				return null;
			}
			return f.getMetaDescriptionFor(context, keyword);
		}
		return md;
	}

	@Override
	public String getOmissibleFacetForSymbol(final String keyword) {
		SymbolMetaDescription md = getMetaDescriptionFor(null, keyword);
		return md == null ? IKeyword.NAME /* by default */: md.getOmissible();
	}

	protected String getKeyword(final ISyntacticElement cur) {
		return cur.getKeyword();
	}

	/**
	 * Creates a semantic description based on a source element, a super-description, and a --
	 * possibly null -- list of children. In this method, the children of the source element are not
	 * considered, so if "children" is null or empty, the description is created without children.
	 */
	@Override
	public final IDescription createDescription(final ISyntacticElement source,
		final IDescription superDesc, final List<IDescription> children) {
		String keyword = getKeyword(source);
		// if ( keyword.equals("species") ) {
		// GuiUtils.debug("CreateDescription");
		// }
		SymbolFactory f = (SymbolFactory) chooseFactoryFor(keyword, null);
		if ( f == null ) {
			superDesc.flagError("Impossible to parse keyword " + keyword + " in this context",
				IGamlIssue.UNKNOWN_KEYWORD, source, keyword);
			return null;
		}
		SymbolMetaDescription md = f.getMetaDescriptionFor(superDesc, keyword);
		return f.createDescriptionInternal(keyword, source, superDesc, children, md);
	}

	private IDescription createDescriptionInternal(final String keyword,
		final ISyntacticElement source, final IDescription superDesc,
		final List<IDescription> children, final SymbolMetaDescription md) {
		List<IDescription> statements = children == null ? new ArrayList() : children;
		md.verifyFacets(source, source.getFacets(), superDesc);
		IDescription desc = buildDescription(source, keyword, statements, superDesc, md);
		desc.getSourceInformation().setDescription(desc);
		return desc;
	}

	/**
	 * Creates a semantic tree based on a source element and a super-description. The
	 * children of the source element are used as a basis for building, recursively, the tree.
	 */
	@Override
	public final IDescription createDescriptionRecursively(final ISyntacticElement source,
		final IDescription superDesc) {
		if ( source == null ) { return null; }
		String keyword = getKeyword(source);
		String context = superDesc == null ? null : superDesc.getKeyword();
		SymbolFactory f = (SymbolFactory) chooseFactoryFor(keyword, context);
		if ( f == null ) {
			if ( superDesc != null ) {
				superDesc.flagError("Impossible to parse keyword " + keyword,
					IGamlIssue.UNKNOWN_KEYWORD, source, keyword, context);
			}
			return null;
		}
		SymbolMetaDescription md = f.getMetaDescriptionFor(superDesc, keyword);
		return f.createDescriptionRecursivelyInternal(keyword, source, superDesc, md);
	}

	private IDescription createDescriptionRecursivelyInternal(final String keyword,
		final ISyntacticElement source, final IDescription superDesc, final SymbolMetaDescription md) {
		Facets facets = source.getFacets();
		md.verifyFacets(source, facets, superDesc);
		List<IDescription> children = new ArrayList();
		for ( ISyntacticElement e : source.getChildren() ) {
			// Instead of having to consider this specific case, find a better solution.
			if ( !source.getKeyword().equals(SPECIES) ) {
				children.add(createDescriptionRecursively(e, superDesc));
			} else if ( source.hasParent(DISPLAY) || source.hasParent(SPECIES) ) {
				// "species" declared in "display" or "species" section
				children.add(createDescriptionRecursively(e, superDesc));
			}
		}
		IDescription desc = buildDescription(source, keyword, children, superDesc, md);
		desc.getSourceInformation().setDescription(desc);
		return desc;
	}

	protected IDescription buildDescription(final ISyntacticElement source, final String keyword,
		final List<IDescription> children, final IDescription superDesc,
		final SymbolMetaDescription md) {
		return new SymbolDescription(keyword, superDesc, children, source, md);
	}

	@Override
	public final ISymbol compileDescription(final IDescription desc) {
		IDescription sd = desc.getSuperDescription();
		SymbolFactory f =
			(SymbolFactory) chooseFactoryFor(desc.getKeyword(), sd == null ? null : sd.getKeyword());
		return f.privateCompile(desc);
	}

	@Override
	public final void validateDescription(final IDescription desc) {
		IDescription superDesc = desc.getSuperDescription();
		SymbolFactory f =
			(SymbolFactory) chooseFactoryFor(desc.getKeyword(), superDesc == null ? null
				: superDesc.getKeyword());
		if ( f == null ) {
			desc.flagError("Impossible to validate " + desc.getKeyword(),
				IGamlIssue.UNKNOWN_KEYWORD, null, desc.getKeyword());
			return;
		}
		f.privateValidate(desc);
	}

	protected void privateValidate(final IDescription desc) {
		SymbolMetaDescription md = desc.getMeta();
		if ( md == null ) { return; }
		Facets rawFacets = desc.getFacets();
		// Validation of the facets (through their compilation)
		rawFacets.putAsLabel(KEYWORD, desc.getKeyword());
		for ( Facet f : rawFacets.entrySet() ) {
			if ( f == null ) {
				continue;
			}
			compileFacet(f.getKey(), desc);
		}
		verifyFacetsType(desc);
		if ( md.hasSequence() && !desc.getKeyword().equals(PRIMITIVE) ) {
			if ( md.isRemoteContext() ) {
				desc.copyTempsAbove();
			}
			privateValidateChildren(desc);
		}
	}

	protected void privateValidateChildren(final IDescription desc) {
		for ( IDescription sd : desc.getChildren() ) {
			validateDescription(sd);
		}
	}

	protected void compileFacet(final String tag, final IDescription sd) {
		try {
			IExpressionDescription ed = sd.getFacets().get(tag);
			if ( ed == null ) { return; }
			ed.compile(sd);
		} catch (GamaRuntimeException e) {
			e.printStackTrace();
		}
	}

	protected ISymbol privateCompile(final IDescription desc) {
		SymbolMetaDescription md = desc.getMeta();
		if ( md == null ) { return null; }
		Facets rawFacets = desc.getFacets();
		// Addition of a facet to keep track of the keyword
		rawFacets.putAsLabel(KEYWORD, desc.getKeyword());
		for ( Facet f : rawFacets.entrySet() ) {
			if ( f != null ) {
				compileFacet(f.getKey(), desc);
			}
		}
		ISymbol cs = md.getConstructor().create(desc);
		if ( cs == null ) { return null; }
		if ( md.hasArgs() ) {
			((IStatement.WithArgs) cs).setFormalArgs(privateCompileArgs((StatementDescription) desc));
		}
		if ( md.hasSequence() && !desc.getKeyword().equals(PRIMITIVE) ) {
			if ( md.isRemoteContext() ) {
				desc.copyTempsAbove();
			}
			cs.setChildren(privateCompileChildren(desc));
		}
		return cs;

	}

	protected Arguments privateCompileArgs(final StatementDescription desc) {
		return new Arguments();
	}

	protected List<ISymbol> privateCompileChildren(final IDescription desc) {
		List<ISymbol> lce = new ArrayList();
		for ( IDescription sd : desc.getChildren() ) {
			ISymbol s = compileDescription(sd);
			if ( s != null ) {
				lce.add(s);
			}
		}
		return lce;
	}

}
