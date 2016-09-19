/**
 * Created by drogoul, 19 sept. 2016
 * 
 */
package msi.gama.lang.gaml.ui.outline;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.ui.editor.model.IXtextDocument;
import org.eclipse.xtext.ui.editor.outline.IOutlineNode;
import org.eclipse.xtext.ui.editor.outline.IOutlineTreeProvider;
import org.eclipse.xtext.ui.editor.outline.impl.IOutlineTreeStructureProvider;

/**
 * The class GamlOutlineSyntacticTreeProvider.
 *
 * @author drogoul
 * @since 19 sept. 2016
 *
 */
public class GamlOutlineSyntacticTreeProvider
		implements IOutlineTreeStructureProvider, IOutlineTreeProvider, IOutlineTreeProvider.Background {

	/**
	 * @see org.eclipse.xtext.ui.editor.outline.IOutlineTreeProvider#createRoot(org.eclipse.xtext.ui.editor.model.IXtextDocument)
	 */
	@Override
	public IOutlineNode createRoot(final IXtextDocument document) {
		return null;
	}

	/**
	 * @see org.eclipse.xtext.ui.editor.outline.impl.IOutlineTreeStructureProvider#createChildren(org.eclipse.xtext.ui.editor.outline.IOutlineNode,
	 *      org.eclipse.emf.ecore.EObject)
	 */
	@Override
	public void createChildren(final IOutlineNode parentNode, final EObject modelElement) {
	}

}
