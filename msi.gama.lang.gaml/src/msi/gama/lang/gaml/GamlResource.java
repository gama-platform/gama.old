/**
 * Created by drogoul, 24 avr. 2012
 * 
 */
package msi.gama.lang.gaml;

import java.util.*;
import msi.gama.common.util.GuiUtils;
import msi.gama.kernel.model.IModel;
import msi.gama.lang.gaml.linking.GamlDiagnostic;
import msi.gama.lang.gaml.validation.*;
import msi.gaml.descriptions.ModelDescription;
import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.util.*;
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
public class GamlResource extends LazyLinkingResource {

	GamlBuilder builder;
	private IGamlBuilderListener listener;

	// ModelDescription lastDescription;

	public GamlBuilder getBuilder() {
		if ( builder == null ) {
			builder = new GamlBuilder(this);
		}
		return builder;
	}

	public void setModelDescription(final boolean withErrors, final ModelDescription model) {

		if ( listener != null ) {
			Set<String> exp = model == null ? Collections.EMPTY_SET : model.getExperimentNames();
			listener.validationEnded(exp, withErrors);
		}
		// if ( model != null ) {
		// model.dispose();
		// if ( lastDescription != null ) {
		// lastDescription.dispose();
		// }
		// lastDescription = model;
		// }
	}

	public IModel doBuild() {
		return getBuilder().build();
	}

	public ModelDescription doValidate() {
		long begin = System.currentTimeMillis();
		ModelDescription md = getBuilder().validate();
		long end = System.currentTimeMillis();
		GuiUtils.debug("Validation of " + getURI().lastSegment() + " took " + (end - begin) +
			" milliseconds");
		return md;
	}

	public void setListener(final IGamlBuilderListener listener) {
		this.listener = listener;
	}

	public void removeListener() {
		listener = null;
	}

	public IGamlBuilderListener getListener() {
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

	@Override
	protected void unload(final EObject oldRootObject) {
		// GuiUtils.debug("Unloading " + oldRootObject + " && getContents().size : " +
		// getContents().size());
		if ( oldRootObject == null ) { return; }
		EList<Adapter> list = new BasicEList<Adapter>(oldRootObject.eAdapters());
		for ( Adapter adapter : list ) {
			if ( adapter instanceof ModelDescription ) {
				((ModelDescription) adapter).dispose();
			}
		}
	}

	// @Override
	// public IParseResult getParseResult() {
	// return super.getParseResult();
	// }
	//
	// @Override
	// public void reparse(final String newContent) throws IOException {
	// GuiUtils.debug("Reparsing " + this);
	// super.reparse(newContent);
	// }
	//
	// @Override
	// protected void updateInternalState(final IParseResult newParseResult) {
	// GuiUtils.debug("Updating the internal state of " + this);
	// super.updateInternalState(newParseResult);
	// }
	//
	// @Override
	// public void doSave(final OutputStream outputStream, final Map<?, ?> options) throws
	// IOException {
	// GuiUtils.debug("Saving " + this);
	// super.doSave(outputStream, options);
	// }
	//
	// @Override
	// public boolean isValidationDisabled() {
	// return super.isValidationDisabled();
	// }
	//
	// @Override
	// public void setValidationDisabled(final boolean validationDisabled) {
	// super.setValidationDisabled(validationDisabled);
	// }

}
