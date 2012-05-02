/**
 * Created by drogoul, 5 févr. 2012
 * 
 */
package msi.gama.lang.gaml.ui.hover;

import msi.gama.lang.utils.EGaml;
import msi.gaml.descriptions.IGamlDescription;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.text.*;
import org.eclipse.xtext.nodemodel.INode;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;
import org.eclipse.xtext.resource.*;
import org.eclipse.xtext.ui.editor.hover.DispatchingEObjectTextHover;
import org.eclipse.xtext.ui.editor.hover.html.DefaultEObjectHoverProvider;
import org.eclipse.xtext.util.*;
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

		NonXRefEObjectAtOffset eObjectAtOffsetHelper = new NonXRefEObjectAtOffset();

		@Inject
		private ILocationInFileProvider locationInFileProvider;

		@Override
		protected Pair<EObject, IRegion> getXtextElementAt(final XtextResource resource,
			final int offset) {
			EObject o = eObjectAtOffsetHelper.resolveElementAt(resource, offset);
			if ( o != null ) {
				ITextRegion region = locationInFileProvider.getSignificantTextRegion(o);
				final IRegion region2 = new Region(region.getOffset(), region.getLength());
				if ( TextUtilities.overlaps(region2, new Region(offset, 0)) ) { return Tuples
					.create(o, region2); }
			}
			return null;
		}
	}

	@Override
	protected boolean hasHover(final EObject o) {
		return true;
	}

	@Override
	protected String getFirstLine(final EObject o) {
		IGamlDescription description = EGaml.getGamlDescription(o);
		if ( description == null ) { return super.getFirstLine(o); }
		return description.getTitle() + "(" + o.getClass().getSimpleName() + ")";
	}

}