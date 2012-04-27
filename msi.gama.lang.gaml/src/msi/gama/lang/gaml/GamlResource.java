/**
 * Created by drogoul, 24 avr. 2012
 * 
 */
package msi.gama.lang.gaml;

import msi.gama.common.interfaces.ISyntacticElement;
import msi.gama.common.util.IErrorCollector;
import msi.gama.kernel.model.IModel;
import msi.gama.lang.gaml.linking.*;
import msi.gama.lang.gaml.validation.IGamlBuilder;
import msi.gaml.compilation.GamlCompilationError;
import msi.gaml.descriptions.ModelDescription;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.linking.lazy.LazyLinkingResource;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;

/**
 * The class GamlResource.
 * 
 * @author drogoul
 * @since 24 avr. 2012
 * 
 */
public class GamlResource extends LazyLinkingResource implements IErrorCollector {

	GamlBuilder builder;
	ModelDescription semanticDescription;
	ISyntacticElement syntacticDescription;
	private IGamlBuilder.Listener listener;

	public GamlBuilder getBuilder() {
		if ( builder == null ) {
			builder = new GamlBuilder(this, this);
		}
		return builder;
	}

	public ModelDescription getModelDescription() {
		return semanticDescription;
	}

	public void setModelDescription(final boolean withErrors, final ModelDescription md) {
		if ( semanticDescription == md ) { return; }
		if ( semanticDescription != null ) {
			semanticDescription.dispose();
		}
		semanticDescription = md;
		if ( listener != null ) {
			listener.validationEnded(md.getExperimentNames(), withErrors);
		}
	}

	public IModel doBuild() {
		return getBuilder().build();
	}

	public void setListener(final IGamlBuilder.Listener listener) {
		this.listener = listener;
	}

	public void removeListener() {
		listener = null;
	}

	public IGamlBuilder.Listener getListener() {
		return listener;
	}

	public void error(final String message, final EObject object) {
		if ( object == null ) { return; }
		if ( object.eResource() != this ) {
			((GamlResource) object.eResource()).error(message, object);
			return;
		}
		getErrors().add(
			new GamlDiagnostic("", new String[0], message, NodeModelUtils.getNode(object)));
	}

	//
	// private void warning(final String message, final EObject object) {
	// if ( object == null ) { return; }
	// if ( object.eResource() != this ) {
	// ((GamlResource) object.eResource()).warning(message, object);
	// return;
	// }
	// getWarnings().add(
	// new GamlDiagnostic("", new String[0], message, NodeModelUtils.getNode(object)));
	// }

	/**
	 * @see msi.gama.common.util.IErrorCollector#add(msi.gaml.compilation.GamlCompilationError)
	 */
	@Override
	public void add(final GamlCompilationError error) {
		if ( !error.isWarning() ) {
			error(error.toString(), (EObject) error.getStatement());
		}
	}

}
