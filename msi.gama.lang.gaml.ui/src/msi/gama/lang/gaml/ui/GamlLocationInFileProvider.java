/**
 * Created by drogoul, 28 avr. 2012
 * 
 */
package msi.gama.lang.gaml.ui;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.resource.DefaultLocationInFileProvider;
import org.eclipse.xtext.util.ITextRegion;

/**
 * The class GamlLocationInFileProvider.
 * 
 * @author drogoul
 * @since 28 avr. 2012
 * 
 */
public class GamlLocationInFileProvider extends DefaultLocationInFileProvider {

	/**
	 * This implementation considers all the nodes of an EObject to be "significant".
	 * @see org.eclipse.xtext.resource.DefaultLocationInFileProvider#getTextRegion(org.eclipse.emf.ecore.EObject,
	 *      boolean)
	 */
	@Override
	protected ITextRegion getTextRegion(final EObject obj, final boolean isSignificant) {
		// return super.getTextRegion(obj, false);
		return super.getTextRegion(obj, isSignificant);
	}

}
