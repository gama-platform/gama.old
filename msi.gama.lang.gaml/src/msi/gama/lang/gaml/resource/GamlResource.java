/**
 * Created by drogoul, 24 avr. 2012
 * 
 */
package msi.gama.lang.gaml.resource;

import java.util.*;
import msi.gama.common.interfaces.ISyntacticElement;
import msi.gama.lang.gaml.validation.*;
import msi.gaml.compilation.GamlCompilationError;
import msi.gaml.descriptions.ModelDescription;
import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.util.*;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.linking.lazy.LazyLinkingResource;
import com.google.inject.Inject;

/**
 * The class GamlResource.
 * 
 * @author drogoul
 * @since 24 avr. 2012
 * 
 */
public class GamlResource extends LazyLinkingResource {

	static int counter = 0;
	private ISyntacticElement syntacticContents;
	private IGamlBuilderListener listener;
	private GamlCompatibilityConverter converter;

	@Inject
	GamlJavaValidator validator;

	@Override
	public String toString() {
		return "resource" + "[" + getURI() + "]";
	}

	public GamlCompatibilityConverter getConverter() {
		if ( converter == null ) {
			converter = new GamlCompatibilityConverter(this);
		}
		return converter;
	}

	public void setModelDescription(final boolean withErrors, final ModelDescription model) {
		if ( listener != null ) {
			Set<String> exp = model == null ? Collections.EMPTY_SET : model.getExperimentNames();
			listener.validationEnded(exp, withErrors);
		}
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

	public void error(final String message, final ISyntacticElement elt, final boolean warning) {
		validator.add(new GamlCompilationError(message, elt.getElement(), warning));
	}

	@Override
	protected void unload(final EObject oldRootObject) {
		if ( oldRootObject != null ) {
			EList<Adapter> list = new BasicEList<Adapter>(oldRootObject.eAdapters());
			for ( Adapter adapter : list ) {
				if ( adapter instanceof ModelDescription ) {
					((ModelDescription) adapter).dispose();
				}
			}
		}
		super.unload(oldRootObject);
	}

	public ISyntacticElement getSyntacticContents() {
		if ( syntacticContents == null ) {
			syntacticContents = getConverter().buildSyntacticContents();
		}
		return syntacticContents;
	}

	public void eraseSyntacticContents() {
		syntacticContents = null;
	}

}
