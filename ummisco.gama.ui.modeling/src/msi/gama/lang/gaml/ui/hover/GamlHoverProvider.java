/*******************************************************************************************************
 *
 * GamlHoverProvider.java, in ummisco.gama.ui.modeling, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.9.3).
 *
 * (c) 2007-2024 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.lang.gaml.ui.hover;

import java.net.URL;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.jface.internal.text.html.BrowserInformationControl;
import org.eclipse.jface.text.DefaultInformationControl;
import org.eclipse.jface.text.IInformationControl;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.editors.text.EditorsUI;
import org.eclipse.xtext.Keyword;
import org.eclipse.xtext.documentation.IEObjectDocumentationProvider;
import org.eclipse.xtext.nodemodel.ILeafNode;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;
import org.eclipse.xtext.resource.EObjectAtOffsetHelper;
import org.eclipse.xtext.resource.ILocationInFileProvider;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.ui.editor.IURIEditorOpener;
import org.eclipse.xtext.ui.editor.hover.DispatchingEObjectTextHover;
import org.eclipse.xtext.ui.editor.hover.html.DefaultEObjectHoverProvider;
import org.eclipse.xtext.ui.editor.hover.html.IXtextBrowserInformationControl;
import org.eclipse.xtext.ui.editor.hover.html.XtextBrowserInformationControl;
import org.eclipse.xtext.ui.editor.hover.html.XtextBrowserInformationControlInput;
import org.eclipse.xtext.ui.editor.hover.html.XtextElementLinks;
import org.eclipse.xtext.util.ITextRegion;
import org.eclipse.xtext.util.Pair;
import org.eclipse.xtext.util.Tuples;

import com.google.common.base.Strings;
import com.google.inject.Inject;

import msi.gama.lang.gaml.gaml.ActionRef;
import msi.gama.lang.gaml.gaml.Function;
import msi.gama.lang.gaml.gaml.VariableRef;
import msi.gama.lang.gaml.ui.hover.GamlHoverDocumentationProvider.Result;
import msi.gaml.interfaces.IGamlDescription;
import ummisco.gama.ui.utils.WebHelper;
import ummisco.gama.ui.utils.WorkbenchHelper;

/**
 * The Class GamlHoverProvider.
 */
public class GamlHoverProvider extends DefaultEObjectHoverProvider {

	/**
	 * The Class GamlDispatchingEObjectTextHover.
	 */
	public static class GamlDispatchingEObjectTextHover extends DispatchingEObjectTextHover {

		/** The e object at offset helper. */
		@Inject private EObjectAtOffsetHelper eObjectAtOffsetHelper;

		/** The location in file provider. */
		@Inject private ILocationInFileProvider locationInFileProvider;

		/** The correct. */
		EObject correct = null;

		@Override
		protected Pair<EObject, IRegion> getXtextElementAt(final XtextResource resource, final int offset) {
			// BUGFIX AD 2/4/13 : getXtextElementAt() is called twice, one to
			// compute the region
			// from the UI thread, one to compute the objects from the hover
			// thread. The offset in
			// the second call is always false (maybe we should file a bug in
			// XText). The following
			// code is a workaround.
			ITextRegion region = null;
			EObject o;
			if (correct == null) {
				correct = eObjectAtOffsetHelper.resolveContainedElementAt(resource, offset);
				o = correct;
			} else {
				o = correct;
				correct = null;
			}
			// /BUGFIX
			if (o != null) {
				if (o instanceof ActionRef) {
					final EObject container = o.eContainer();
					if (container instanceof Function) {
						o = container;
						region = locationInFileProvider.getFullTextRegion(o);
					}
				}
				if (region == null) { region = locationInFileProvider.getSignificantTextRegion(o); }
				final IRegion region2 = new Region(region.getOffset(), region.getLength());
				return Tuples.create(o, region2);
			}
			final ILeafNode node = NodeModelUtils.findLeafNodeAtOffset(resource.getParseResult().getRootNode(), offset);
			if (node != null && node.getGrammarElement() instanceof Keyword) {
				final IRegion region2 = new Region(node.getOffset(), node.getLength());
				return Tuples.create(node.getGrammarElement(), region2);
			}
			return null;
		}

		@Override
		public Object getHoverInfo(final EObject first, final ITextViewer textViewer, final IRegion hoverRegion) {
			return super.getHoverInfo(first, textViewer, hoverRegion);
		}

	}

	/**
	 * The Class GamlHoverControlCreator.
	 */
	public class GamlHoverControlCreator extends HoverControlCreator {

		/**
		 * @param informationPresenterControlCreator
		 */
		public GamlHoverControlCreator(final IInformationControlCreator informationPresenterControlCreator) {
			super(informationPresenterControlCreator);
		}

		/**
		 * The Class GamlInformationControl.
		 */
		public class GamlInformationControl extends XtextBrowserInformationControl {

			@Override
			public void setSize(final int width, final int height) {
				super.setSize(width, height + 30);
				final org.eclipse.swt.graphics.Point p = WorkbenchHelper.getDisplay().getCursorLocation();
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
			 * @see org.eclipse.jface.text.IInformationControlExtension5# getInformationPresenterControlCreator()
			 */
			@Override
			public IInformationControlCreator getInformationPresenterControlCreator() {
				return GamlHoverProvider.this.getInformationPresenterControlCreator();
			}
		}

		@Override
		public IInformationControl doCreateInformationControl(final Shell parent) {

			final String tooltipAffordanceString = EditorsUI.getTooltipAffordanceString();
			if (BrowserInformationControl.isAvailable(parent)) {
				final String font = "org.eclipse.jdt.ui.javadocfont";
				final IXtextBrowserInformationControl iControl =
						new GamlInformationControl(parent, font, tooltipAffordanceString);
				addLinkListener(iControl);
				return iControl;
			}
			return new DefaultInformationControl(parent, tooltipAffordanceString);
		}
	}

	/** The creator. */
	private IInformationControlCreator creator;

	/** The decorated provider. */
	@Inject private IEObjectDocumentationProvider decoratedProvider;

	/** The provider. */
	@Inject private GamlHoverDocumentationProvider provider;

	@Override
	public IInformationControlCreator getHoverControlCreator() {
		if (creator == null) { creator = new GamlHoverControlCreator(getInformationPresenterControlCreator()); }
		return creator;
	}

	/**
	 * @since 2.3
	 */
	@Override
	protected void addLinkListener(final IXtextBrowserInformationControl control) {
		control.addLocationListener(getElementLinks().createLocationListener(new XtextElementLinks.ILinkHandler() {

			/** The uri editor opener. */
			@Inject private IURIEditorOpener uriEditorOpener;

			@Override
			public void handleXtextdocViewLink(final URI linkTarget) {
				// TODO: enable when XtextDoc view available
				// control.notifyDelayedInputChange(null);
				// control.setVisible(false);
				// control.dispose(); //FIXME: should have protocol to hide, rather than dispose
				// try {
				// JavadocView view= (JavadocView) JavaPlugin.getActivePage().showView(JavaUI.ID_JAVADOC_VIEW);
				// view.setInput(linkTarget);
				// } catch (PartInitException e) {
				// JavaPlugin.log(e);
				// }
			}

			@Override
			public void handleInlineXtextdocLink(final URI linkTarget) {
				XtextBrowserInformationControlInput hoverInfo = getHoverInfo(getTarget(linkTarget), null,
						(XtextBrowserInformationControlInput) control.getInput());
				if (control.hasDelayedInputChangeListener()) {
					control.notifyDelayedInputChange(hoverInfo);
				} else {
					control.setInput(hoverInfo);
				}
			}

			@Override
			public void handleDeclarationLink(final URI linkTarget) {
				control.notifyDelayedInputChange(null);
				control.dispose(); // FIXME: should have protocol to hide, rather than dispose
				if (uriEditorOpener != null) { uriEditorOpener.open(linkTarget, true); }
			}

			@Override
			public boolean handleExternalLink(final URL url, final Display display) {
				control.notifyDelayedInputChange(null);
				control.dispose(); // FIXME: should have protocol to hide, rather than dispose

				// open external links in real browser:
				WebHelper.openPage(url.toString());
				return true;
			}

			@Override
			public void handleTextSet() {}

			EObject getTarget(final URI uri) {
				ResourceSet rs = ((XtextBrowserInformationControlInput) control.getInput()).getElement().eResource()
						.getResourceSet();
				return rs.getEObject(uri, true);
			}
		}));
	}

	@Override
	protected String getHoverInfoAsHtml(final EObject o) {
		StringBuilder buffer = new StringBuilder();
		IGamlDescription doc = provider.getDoc(o);
		if (doc == null) {
			doc = new Result("Unknow object of type " + o.getClass().getSimpleName(),
					"File an issue at https://github.com/gama-platform/gama/issues to document it");
		}
		String title = doc.getTitle();
		String documentation = doc.getDocumentation().get();
		String comment = decoratedProvider.getDocumentation(o);
		if (Strings.isNullOrEmpty(comment)) if (o instanceof VariableRef) {
			comment = super.getDocumentation(((VariableRef) o).getRef());
		} else if (o instanceof ActionRef) { comment = super.getDocumentation(((ActionRef) o).getRef()); }
		if (!Strings.isNullOrEmpty(title)) { buffer.append("<b>").append(title).append("</b><hr>"); }
		if (!Strings.isNullOrEmpty(comment)) { buffer.append("<p><i>").append(comment).append("</i></p><hr>"); }
		if (!Strings.isNullOrEmpty(documentation)) { buffer.append("<p>").append(documentation).append("</p>"); }
		return buffer.toString();
	}

}