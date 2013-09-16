/**
 * Created by drogoul, 5 f�vr. 2012
 * 
 */
package msi.gama.lang.gaml.ui.hover;

import msi.gama.common.util.GuiUtils;
import msi.gama.lang.gaml.gaml.*;
import msi.gama.lang.utils.EGaml;
import msi.gaml.factories.*;
import msi.gaml.factories.DescriptionFactory.Documentation;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.text.*;
import org.eclipse.xtext.Keyword;
import org.eclipse.xtext.nodemodel.*;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;
import org.eclipse.xtext.resource.*;
import org.eclipse.xtext.ui.editor.hover.DispatchingEObjectTextHover;
import org.eclipse.xtext.ui.editor.hover.html.DefaultEObjectHoverProvider;
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

		@Override
		public IInformationControlCreator getHoverControlCreator() {
			IInformationControlCreator c = super.getHoverControlCreator();
			return c;
		}

	}

	@Override
	public IInformationControlCreatorProvider getHoverInfo(final EObject first, final ITextViewer textViewer,
		final IRegion hoverRegion) {
		if ( first instanceof Keyword ) {
			GuiUtils.debug("Implement getHoverInfo for keywords");

		}
		return super.getHoverInfo(first, textViewer, hoverRegion);

	}

	@Override
	protected boolean hasHover(final EObject o) {
		return true;
	}

	@Override
	protected String getFirstLine(final EObject o) {
		Documentation description = DescriptionFactory.getGamlDocumentation(o);
		String result =
			description == null ? (o instanceof TypeRef ? "type " : "") + EGaml.getKeyOf(o) : description.getTitle();
		result = "<b>" + result + "</b>";
		return result;
	}

}