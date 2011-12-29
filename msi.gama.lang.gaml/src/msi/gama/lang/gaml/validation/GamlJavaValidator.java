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
package msi.gama.lang.gaml.validation;

import java.io.File;
import java.net.URL;
import java.util.*;
import msi.gama.precompiler.MultiProperties;
import msi.gama.lang.gaml.descript.*;
import msi.gama.lang.gaml.gaml.*;
import msi.gama.lang.gaml.gaml.impl.GamlKeywordRefImpl;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.xtext.validation.Check;

public class GamlJavaValidator extends AbstractGamlJavaValidator {

	final static List<String> modelChilds = Arrays.asList("global", "environment", "entities",
		"output", "experiment");

	public static final String QF_NOTFACETOFKEY = "NOTFACETOFKEY";
	public static final String QF_UNKNOWNFACET = "UNKNOWNFACET";
	public static final String QF_KEYHASNOFACET = "KEYHASNOFACET";
	public static final String QF_NOTKEYOFCONTEXT = "NOTKEYOFCONTEXT";
	public static final String QF_NOTKEYOFMODEL = "NOTKEYOFMODEL";
	public static final String QF_INVALIDSETVAR = "INVALIDSETVAR";

	private GamlDescriptError autoBuildError;
	private static long lastTimeCompil = 0;

	// used to validating some "special facet"
	// (for ex, "min, max" are only allowed in "var" if "type" is "int" or "float")
	private final Map<String, String> specialRules = new HashMap<String, String>() {

		{
			put("var", "type");
			put("experiment", "type");
		}
	};
	private static final String generatedPath =
		getPath("platform:/plugin/msi.gama.core/generated/");
	private static final MultiProperties allowedFacets = MultiProperties.loadFrom(new File(
		generatedPath + MultiProperties.FACETS), MultiProperties.FACETS);

	private static String getPath(final String strURI) {
		try {
			return FileLocator.toFileURL(new URL(strURI)).getPath();
		} catch (Exception e) {}
		return null;
	}

	// for context
	private DefKeyword getParentName(final EObject container) {
		if ( container instanceof Block ) { return getKey(container.eContainer()); }
		return null;
	}

	// for facet
	private DefKeyword getKey(final EObject container) {
		if ( container instanceof SubStatement ) {
			SubStatement stm = (SubStatement) container;
			GamlKeywordRefImpl key = (GamlKeywordRefImpl) stm.getKey();
			if ( key.basicGetRef() != null ) { return key.basicGetRef(); }
		}
		return null;
	}

	@Check
	public void checkModel(final Model m) {

		// TODO test if we are saving the model (auto-build slow down the
		// writing)
		// TODO test if the model being validated is the one being edited (to
		// prevent slow down)
		long current = System.currentTimeMillis();
		// no more than 1 compilation/second
		if ( current - lastTimeCompil > 1000 ) {
			// test if there is others errors (background compilation)
			try {
				autoBuildError = null;
				// Deactivate temporarily the automatic background validation...
				GamlDescriptIO.getInstance().process(m.eResource());
			} catch (Exception e) {
				if ( e instanceof GamlDescriptError ) {
					autoBuildError = (GamlDescriptError) e;
				}
			}
			lastTimeCompil = current;
		}
	}

	/**
	 * Read Project Builders and Natures
	 * 
	 * @see http://www.eclipse.org/articles/Article-Builders/builders.html <br/>
	 *      Handling exceptions and reporting problems
	 * @see http://www.eclipse.org/articles/Article-Builders/builders.html#1c
	 * @see http://www.google.com/search?q=Eclipse+builder
	 * @see http
	 *      ://www.eclipse.org/articles/Article-Mark%20My%20Words/mark-my-words
	 *      .html
	 * @see http://www.eclipse.org/resources/?category=Builders
	 * @see org.eclipse.xtext.ui.editor.model.IXtextDocument#addModelListener(org.eclipse.xtext.ui.editor.model.IXtextModelListener)
	 * @see org.eclipse.xtext.ui.editor.model.IXtextModelListener
	 */
	@Check
	public void checkSubStatement(final SubStatement s) {
		DefKeyword parent = getParentName(s.eContainer());
		String sName = s.getKey().getRef().getName();
		if ( sName == null ) { return; }
		if ( parent != null && parent.getBlock() != null ) {
			EList<DefKeyword> keys = parent.getBlock().getChilds();
			if ( !keys.contains(s.getKey().getRef()) ) {
				error("Can not declare " + sName + " in " + parent.getName(),
					GamlPackage.Literals.SUB_STATEMENT__KEY, QF_NOTKEYOFCONTEXT);
			} else {
				final String parentName = parent.getName();
				if ( parentName.equals("entities") || parentName.equals("species") ) {
					if ( sName.equals("global") ) {
						error("Can not declare " + sName + " in " + parentName,
							GamlPackage.Literals.SUB_STATEMENT__KEY, QF_NOTKEYOFCONTEXT);
					}
				}
			}
		} else {
			if ( s.eContainer() instanceof Model ) {
				// no other way to do, since Model is define in the grammar
				if ( !modelChilds.contains(sName) ) {
					warning(s.getKey().getRef().getName() + " is not a section of the model",
						GamlPackage.Literals.SUB_STATEMENT__KEY, QF_NOTKEYOFMODEL);
				}
			} else {
				warning("this context has no key", GamlPackage.Literals.SUB_STATEMENT__KEY);
			}
		}

		if ( autoBuildError != null && EcoreUtil.equals(s, autoBuildError.getStatement()) ) {
			error(autoBuildError.getMessage(), GamlPackage.Literals.SUB_STATEMENT__KEY);
		}
	}

	@Check
	public void checkSetEval(final SetEval s) {
		if ( autoBuildError != null && EcoreUtil.equals(s, autoBuildError.getStatement()) ) {
			error(autoBuildError.getMessage(), GamlPackage.Literals.SET_EVAL__VAR, QF_INVALIDSETVAR);
		}
	}

	@Check
	public void checkFacetExpr(final FacetExpr f) {
		DefKeyword key = getKey(f.eContainer());
		if ( key == null ) { return; }
		final String kName = key.getName();
		if ( kName == null ) { return; }
		if ( key.getBlock() != null ) {
			Set<String> facets1 = allowedFacets.get(kName);// list of facets allowed in this key
			Set<String> facets2 = null;
			if ( f.getKey() != null ) {
				final String keyofRule = specialRules.get(kName);
				if ( keyofRule != null ) {
					String correspondingFacetName = null;
					EList<FacetExpr> facetList = null;
					if ( f.eContainer() instanceof Definition ) {
						facetList = ((Definition) f.eContainer()).getFacets();
					}
					if ( facetList != null ) {
						for ( FacetExpr fe : facetList ) {
							if ( fe != null && fe.getKey().getRef().getName().equals(keyofRule) ) {
								if ( fe.getExpr() instanceof VariableRef ) {
									correspondingFacetName =
										((VariableRef) fe.getExpr()).getRef().getName();
								}
								break;
							}
						}
					}
					if ( correspondingFacetName != null ) {
						facets2 = allowedFacets.get(correspondingFacetName);
					}

				}
			}
			if ( f.getKey() != null &&
				!(facets1.contains(f.getKey().getRef().getName()) || facets2 != null &&
					facets2.contains(f.getKey().getRef().getName())) ) {
				warning(f.getKey().getRef().getName() + " is not a facet of " + kName,
					GamlPackage.Literals.FACET_EXPR__KEY, QF_NOTFACETOFKEY);
			}

		} else if ( f.getKey() != null ) {
			if ( !kName.equals("set") ) {
				warning("this key has no facet", GamlPackage.Literals.FACET_EXPR__KEY,
					QF_KEYHASNOFACET);
			}
		}
	}

}
