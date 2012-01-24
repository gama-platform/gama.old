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
package msi.gama.lang.gaml.ui.contentassist;

import java.io.File;
import java.net.URL;
import java.util.Set;
import msi.gama.lang.gaml.gaml.*;
import msi.gama.lang.gaml.gaml.impl.GamlKeywordRefImpl;
import msi.gama.precompiler.GamlProperties;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.xtext.Assignment;
import org.eclipse.xtext.ui.editor.contentassist.*;

/**
 * see
 * http://www.eclipse.org/Xtext/documentation/latest/xtext.html#contentAssist on
 * how to customize content assistant
 */
public class GamlProposalProvider extends AbstractGamlProposalProvider {

	private static Set<String> typeList;
	private static GamlProperties allowedFacets;
	private static Image rgbImage = ImageDescriptor.createFromFile(GamlProposalProvider.class,
		"/icons/_rgb.png").createImage();
	private static Image facetImage = ImageDescriptor.createFromFile(GamlProposalProvider.class,
		"/icons/_facet.png").createImage();
	private static Image typeImage = ImageDescriptor.createFromFile(GamlProposalProvider.class,
		"/icons/_type.png").createImage();

	private static String getPath(final String strURI) {
		try {
			URL url = FileLocator.resolve(new URL(strURI));
			return new File(url.getFile()).getAbsolutePath();
		} catch (Exception e) {}
		return null;
	}

	private DefKeyword getKey(final EObject container) {
		if ( container instanceof SubStatement ) {
			SubStatement stm = (SubStatement) container;
			GamlKeywordRefImpl key = (GamlKeywordRefImpl) stm.getKey();
			if ( key.basicGetRef() != null ) { return key.basicGetRef(); }
		}
		return null;
	}

	private String getValueFromFacteExpr(final EObject container, final String facetName) {

		EList<FacetExpr> facetList;
		if ( container instanceof SetEval ) {
			facetList = ((SetEval) container).getFacets();
		} else if ( container instanceof Definition ) {
			facetList = ((Definition) container).getFacets();
		} else {
			return null;
		}

		for ( FacetExpr fe : facetList ) {
			if ( fe != null && fe.getKey().getRef().getName().equals(facetName) ) {
				if ( fe.getExpr() instanceof VariableRef ) { return ((VariableRef) fe.getExpr())
					.getRef().getName(); }
				return null;
			}
		}
		return null;
	}

	// @Override
	// protected ConfigurableCompletionProposal doCreateProposal(final String proposal,
	// final StyledString displayString, final Image image, final int priority,
	// final ContentAssistContext context) {
	//
	// ConfigurableCompletionProposal result = null;
	// if ( displayString.toString().endsWith(" ") ) {
	// result = super.doCreateProposal(proposal, displayString, image, priority, context);
	// }
	// return result;
	// }

	public void completeFacetExpr_Expr(final FacetExpr f, final Assignment assignment,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {

		final String keyName = f.getKey().getRef().getName();
		if ( keyName == null ) { return; }
		final String defKeyName = getKey(f.eContainer()).getName();
		if ( defKeyName == null ) { return; }
		final String valueExpr = getValueFromFacteExpr(f.eContainer(), "type");

		if ( keyName.equals("init") || keyName.equals("value") ) {
			if ( valueExpr != null && valueExpr.equals("rgb") || defKeyName.equals("rgb") ) {

				ConfigurableCompletionProposal editColor =
					(ConfigurableCompletionProposal) createCompletionProposal("Edit color...",
						" Edit color... ", rgbImage, context);
				if ( editColor != null ) {
					editColor.setTextApplier(new ReplacementTextApplier() {

						@Override
						public String getActualReplacementString(
							final ConfigurableCompletionProposal proposal) {
							Display display = context.getViewer().getTextWidget().getDisplay();
							ColorDialog colorDialog = new ColorDialog(display.getActiveShell());
							RGB newColor = colorDialog.open();
							if ( newColor != null ) { return " rgb[" + newColor.red + "," +
								newColor.green + "," + newColor.blue + "] "; }
							return "";
						}
					});

					acceptor.accept(createCompletionProposal("'white'", " white ", rgbImage,
						context));
					acceptor.accept(createCompletionProposal("'black'", " black ", rgbImage,
						context));
					acceptor.accept(createCompletionProposal("'red'", " red ", rgbImage, context));
					acceptor.accept(createCompletionProposal("'yellow'", " yellow ", rgbImage,
						context));
					acceptor.accept(createCompletionProposal("'green'", " green ", rgbImage,
						context));
					acceptor
						.accept(createCompletionProposal("'blue'", " blue ", rgbImage, context));
					acceptor.accept(editColor);
				}
			}
		} else if ( keyName.equals("type") ) {
			for ( String st : getTypelist() ) {
				acceptor.accept(createCompletionProposal(st, " " + st + " ", typeImage, context));
			}

		} else if ( keyName.equals("torus") ) {
			acceptor.accept(createCompletionProposal("true", " true ", null, context));
			acceptor.accept(createCompletionProposal("false", " false ", null, context));
		}

	}

	public void completeDefinition_Facets(final Definition d, final Assignment assignment,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
		// !!!!
		if ( d.getKey() != null && d.getKey().getRef() != null &&
			d.getKey().getRef().getName() != null ) {
			Set<String> facets = getAllowedfacets().get(d.getKey().getRef().getName());
			if ( facets != null ) {
				for ( String st : facets ) {
					acceptor.accept(createCompletionProposal(st, " " + st + " ", facetImage,
						context));
				}
			}
		}
	}

	public void completeEvaluation_Facets(final Evaluation e, final Assignment assignment,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
		Set<String> facets = getAllowedfacets().get(e.getKey().getRef().getName());
		if ( facets != null ) {
			for ( String st : facets ) {
				acceptor.accept(createCompletionProposal(st, " " + st + " ", facetImage, context));
			}
		}
	}

	public void completeSetEval_Facets(final SetEval s, final Assignment assignment,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
		for ( String st : getAllowedfacets().get("set") ) {
			acceptor.accept(createCompletionProposal(st, " " + st + " ", facetImage, context));
		}
	}

	private static Set<String> getTypelist() {
		if ( typeList == null ) {
			typeList = GamlProperties.loadFrom(GamlProperties.TYPES).values();
		}
		return typeList;
	}

	private static GamlProperties getAllowedfacets() {
		if ( allowedFacets == null ) {
			allowedFacets = GamlProperties.loadFrom(GamlProperties.FACETS);
		}
		return allowedFacets;
	}
}
