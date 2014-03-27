/**
 * Created by drogoul, 5 f�vr. 2012
 * 
 */
package msi.gama.lang.gaml.ui.hover;

import msi.gama.lang.gaml.gaml.*;
import msi.gama.lang.utils.EGaml;
import msi.gaml.descriptions.SymbolProto;
import msi.gaml.factories.*;
import msi.gaml.factories.DescriptionFactory.Documentation;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.internal.text.html.BrowserInformationControl;
import org.eclipse.jface.text.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.editors.text.EditorsUI;
import org.eclipse.xtext.Keyword;
import org.eclipse.xtext.nodemodel.*;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;
import org.eclipse.xtext.resource.*;
import org.eclipse.xtext.ui.editor.hover.DispatchingEObjectTextHover;
import org.eclipse.xtext.ui.editor.hover.html.*;
import org.eclipse.xtext.util.*;
import org.eclipse.xtext.util.Pair;
import com.google.inject.Inject;

public class GamlHoverProvider extends DefaultEObjectHoverProvider {

	public static class NonXRefEObjectAtOffset extends EObjectAtOffsetHelper {

		@Override
		protected EObject resolveCrossReferencedElement(final INode node) {
			EObject referenceOwner = NodeModelUtils.findActualSemanticObjectFor(node);
			return referenceOwner;
		}

	}

	public static class GamlDispatchingEObjectTextHover extends DispatchingEObjectTextHover {

		@Inject
		private EObjectAtOffsetHelper eObjectAtOffsetHelper;

		@Inject
		private ILocationInFileProvider locationInFileProvider;

		EObject correct = null;

		@Override
		protected Pair<EObject, IRegion> getXtextElementAt(final XtextResource resource, final int offset) {
			// BUGFIX AD 2/4/13 : getXtextElementAt() is called twice, one to compute the region
			// from the UI thread, one to compute the objects from the hover thread. The offset in
			// the second call is always false (maybe we should file a bug in XText). The following
			// code is a workaround.
			ITextRegion region = null;
			EObject o;
			if ( correct == null ) {
				correct = eObjectAtOffsetHelper.resolveContainedElementAt(resource, offset);
				o = correct;
			} else {
				o = correct;
				correct = null;
			}
			// /BUGFIX
			if ( o != null ) {
				// GuiUtils.debug("Object under hover:" + o.toString());
				if ( o instanceof ActionRef ) {
					EObject container = o.eContainer();
					// GuiUtils.debug("Found " + ((ActionRef) o).getRef().getName());
					if ( container instanceof Function ) {
						// GuiUtils.debug("---> Is a function");
						o = container;
						region = locationInFileProvider.getFullTextRegion(o);
					}
				}
				if ( region == null ) {
					region = locationInFileProvider.getSignificantTextRegion(o);
				}
				final IRegion region2 = new Region(region.getOffset(), region.getLength());
				/* if ( TextUtilities.overlaps(region2, new Region(offset, 0)) ) */{
					return Tuples.create(o, region2);
				}
			} else {
				ILeafNode node = NodeModelUtils.findLeafNodeAtOffset(resource.getParseResult().getRootNode(), offset);
				if ( node.getGrammarElement() instanceof Keyword ) {
					IRegion region2 = new Region(node.getOffset(), node.getLength());
					return Tuples.create(node.getGrammarElement(), region2);
				}
			}
			return null;
		}

		@Override
		public Object getHoverInfo(final EObject first, final ITextViewer textViewer, final IRegion hoverRegion) {
			return super.getHoverInfo(first, textViewer, hoverRegion);
		}

	}

	public class GamlHoverControlCreator extends HoverControlCreator {

		/**
		 * @param informationPresenterControlCreator
		 */
		public GamlHoverControlCreator(final IInformationControlCreator informationPresenterControlCreator) {
			super(informationPresenterControlCreator);
		}

		public class GamlInformationControl extends XtextBrowserInformationControl {

			@Override
			public void setSize(final int width, final int height) {
				super.setSize(width, height);
				org.eclipse.swt.graphics.Point p = Display.getDefault().getCursorLocation();
				p.x -= 5;
				p.y += 15;
				setLocation(p);
			}

			/**
			 * @param parent
			 * @param symbolicFontName
			 * @param statusFieldText
			 */
			public GamlInformationControl(final Shell parent, final String symbolicFontName,
				final String statusFieldText) {
				super(parent, symbolicFontName, statusFieldText);
			}

			/*
			 * @see
			 * org.eclipse.jface.text.IInformationControlExtension5#getInformationPresenterControlCreator()
			 */
			@Override
			public IInformationControlCreator getInformationPresenterControlCreator() {
				return GamlHoverProvider.this.getInformationPresenterControlCreator();
			}
		}

		@Override
		public IInformationControl doCreateInformationControl(final Shell parent) {

			String tooltipAffordanceString = EditorsUI.getTooltipAffordanceString();
			if ( BrowserInformationControl.isAvailable(parent) ) {
				String font = "org.eclipse.jdt.ui.javadocfont"; // FIXME: PreferenceConstants.APPEARANCE_JAVADOC_FONT;
				IXtextBrowserInformationControl iControl =
					new GamlInformationControl(parent, font, tooltipAffordanceString) {

					};
				addLinkListener(iControl);
				return iControl;
			} else {
				return new DefaultInformationControl(parent, tooltipAffordanceString);
			}

			// final XtextBrowserInformationControl c =
			// (XtextBrowserInformationControl) super.doCreateInformationControl(parent);
			// c.addFocusListener(new FocusListener() {
			//
			// @Override
			// public void focusGained(final FocusEvent e) {
			// c.setLocation(Display.getDefault().getCursorLocation());
			// }
			//
			// @Override
			// public void focusLost(final FocusEvent e) {}
			//
			// });
			// return c;
		}
	}

	private IInformationControlCreator creator;

	@Override
	public IInformationControlCreator getHoverControlCreator() {
		if ( creator == null ) {
			creator = new GamlHoverControlCreator(getInformationPresenterControlCreator());
		}
		return creator;
	}

	@Override
	public IInformationControlCreatorProvider getHoverInfo(final EObject first, final ITextViewer textViewer,
		final IRegion hoverRegion) {

		return super.getHoverInfo(first, textViewer, hoverRegion);

	}

	@Override
	protected boolean hasHover(final EObject o) {
		return true;
	}

	@Override
	protected String getFirstLine(final EObject o) {
		if(o == null){return "";}
		Documentation description = DescriptionFactory.getGamlDocumentation(o);
		if ( description == null ) {
			Statement s = EGaml.getStatement(o);
			if ( s != null && SymbolProto.nonTypeStatements.contains(EGaml.getKeyOf(o)) ) {
				if(s==o){return "";}
				return getFirstLine(s);
			} else {
				if ( o instanceof TypeRef ) {
					return "type " + EGaml.getKeyOf(o);
				} else {
					return "";
				}
			}
		} else {
			String result = description.getTitle();
			result = "<b>" + result + "</b>";
			return result;
		}
	}

}