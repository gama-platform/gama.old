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

import static msi.gama.common.interfaces.IKeyword.PRIMITIVE;
import static msi.gaml.factories.DescriptionValidator.verifyFacetsType;
import java.util.*;
import msi.gama.common.interfaces.*;
import msi.gama.precompiler.GamlAnnotations.factory;
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
@factory(handles = { ISymbolKind.ENVIRONMENT, ISymbolKind.ABSTRACT_SECTION,
	ISymbolKind.BATCH_METHOD, ISymbolKind.OUTPUT })
public class SymbolFactory {

	protected final Set<Integer> kindsHandled;

	public SymbolFactory(final List<Integer> handles) {
		kindsHandled = new HashSet(handles);
	}

	Set<Integer> getHandles() {
		return kindsHandled;
	}

	protected String getKeyword(final ISyntacticElement source) {
		return source.getKeyword();
	}

	/**
	 * Creates a semantic description based on a source element, a super-description, and a --
	 * possibly null -- list of children. In this method, the children of the source element are not
	 * considered, so if "children" is null or empty, the description is created without children.
	 */
	final IDescription create(final ISyntacticElement source, final IDescription superDesc,
		final IChildrenProvider cp) {
		String keyword = getKeyword(source);
		SymbolProto md = DescriptionFactory.getProto(superDesc, source, keyword);
		if ( md == null ) { return null; }
		/* if (cp == null || cp.getChildren().isEmpty()) */
		return md.getFactory().createDescriptionInternal(keyword, source, superDesc, cp, md);
	}

	private final IDescription createDescriptionInternal(final String keyword,
		final ISyntacticElement source, final IDescription superDesc, final IChildrenProvider cp,
		final SymbolProto md) {
		md.verifyFacets(source, source.getFacets(), superDesc);
		IDescription desc = buildDescription(source, keyword, cp, superDesc, md);
		desc.getSourceInformation().setDescription(desc);
		return desc;
	}

	/**
	 * Creates a semantic tree based on a source element and a super-description. The
	 * children of the source element are used as a basis for building, recursively, the tree.
	 */
	final IDescription create(final ISyntacticElement source, final IDescription superDesc) {
		if ( source == null ) { return null; }
		String keyword = getKeyword(source);
		SymbolProto md = DescriptionFactory.getProto(superDesc, source, keyword);
		if ( md == null ) { return null; }
		return md.getFactory().createDescriptionRecursivelyInternal(keyword, source, superDesc, md);
	}

	private final IDescription createDescriptionRecursivelyInternal(final String keyword,
		final ISyntacticElement source, final IDescription superDesc, final SymbolProto md) {
		Facets facets = source.getFacets();
		md.verifyFacets(source, facets, superDesc);
		List<IDescription> children = new ArrayList();
		for ( ISyntacticElement e : source.getChildren() ) {
			children.add(create(e, superDesc));
		}
		IDescription desc =
			buildDescription(source, keyword, new ChildrenProvider(children), superDesc, md);
		desc.getSourceInformation().setDescription(desc);
		return desc;
	}

	protected IDescription buildDescription(final ISyntacticElement source, final String keyword,
		final IChildrenProvider cp, final IDescription superDesc, final SymbolProto md) {
		return new SymbolDescription(keyword, superDesc, cp, source/* , md */);
	}

	final ISymbol compile(final IDescription desc) {
		SymbolFactory f = DescriptionFactory.getFactory(desc.getKeyword());
		return f.privateCompile(desc);
	}

	final void validate(final IDescription desc) {
		SymbolFactory f = DescriptionFactory.getFactory(desc.getKeyword());
		if ( f == null ) {
			desc.flagError("Impossible to validate " + desc.getKeyword(),
				IGamlIssue.UNKNOWN_KEYWORD, null, desc.getKeyword());
			return;
		}
		f.privateValidate(desc);
	}

	protected void privateValidate(final IDescription desc) {
		SymbolProto md = desc.getMeta();
		if ( md == null ) { return; }
		DescriptionValidator.assertDescriptionIsInsideTheRightSuperDescription(desc);
		Facets rawFacets = desc.getFacets();
		// Validation of the facets (through their compilation)

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
			validate(sd);
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

	final ISymbol privateCompile(final IDescription desc) {
		SymbolProto md = desc.getMeta();
		if ( md == null ) { return null; }
		Facets rawFacets = desc.getFacets();
		for ( Facet f : rawFacets.entrySet() ) {
			if ( f != null ) {
				compileFacet(f.getKey(), desc);
			}
		}
		ISymbol cs = md.getConstructor().create(desc);
		if ( cs == null ) { return null; }
		if ( md.hasArgs() ) {
			((IStatement.WithArgs) cs)
				.setFormalArgs(privateCompileArgs((StatementDescription) desc));
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
			ISymbol s = compile(sd);
			if ( s != null ) {
				lce.add(s);
			}
		}
		return lce;
	}

}
