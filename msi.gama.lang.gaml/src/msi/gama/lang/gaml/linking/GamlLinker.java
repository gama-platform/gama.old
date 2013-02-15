/**
 * Created by drogoul, 6 avr. 2012
 * 
 */
package msi.gama.lang.gaml.linking;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.diagnostics.IDiagnosticConsumer;
import org.eclipse.xtext.linking.lazy.LazyLinker;

/**
 * The class GamlLinker.
 * 
 * @author drogoul
 * @since 6 avr. 2012
 * 
 */
public class GamlLinker extends LazyLinker {

	@Override
	protected void afterModelLinked(final EObject model, final IDiagnosticConsumer d) {

		// GuiUtils.debug("Model " + model.eResource().getURI().lastSegment() + " has been linked");
		// GamlResource r = (GamlResource) model.eResource();
		// r.createSyntacticDescription();
		// super.afterModelLinked(model, d);
	}

}
