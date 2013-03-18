/**
 * Created by drogoul, 24 avr. 2012
 * 
 */
package msi.gama.lang.gaml.resource;

import java.util.*;
import msi.gama.common.interfaces.ISyntacticElement;
import msi.gama.common.util.GuiUtils;
import msi.gama.kernel.model.IModel;
import msi.gama.lang.gaml.validation.*;
import msi.gama.precompiler.ISymbolKind;
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
	static final List<Integer> STATEMENTS_WITH_ATTRIBUTES = Arrays.asList(ISymbolKind.SPECIES,
		ISymbolKind.EXPERIMENT, ISymbolKind.OUTPUT);

	private ISyntacticElement syntacticContents;
	private IGamlBuilderListener listener;

	@Inject
	GamlJavaValidator validator;

	// @Inject
	// IContainer.Manager manager;
	// @Inject
	// IResourceDescriptions index;
	// @Inject
	// IResourceServiceProvider.Registry resourceServiceProviderRegistry =
	// IResourceServiceProvider.Registry.INSTANCE;

	@Override
	public String toString() {
		return "resource" + "[" + getURI() + "]";
	}

	public void setModelDescription(final boolean withErrors, final ModelDescription model) {
		if ( listener != null ) {
			Set<String> exp = model == null ? Collections.EMPTY_SET : model.getExperimentNames();
			listener.validationEnded(exp, withErrors);
		}
	}

	public IModel doBuild() {
		return GamlBuilder.INSTANCE.build(this);
	}

	public ModelDescription doValidate() {
		// this.listVisibleResourcesFromMe();
		long begin = System.nanoTime();
		ModelDescription md = GamlBuilder.INSTANCE.validate(this);
		long end = System.nanoTime();
		GuiUtils.debug("Validation of " + md + " took " + (end - begin) / 1000000d +
			" milliseconds");
		// this.listExportedObjectsByMe();
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

	public void error(final String message, final ISyntacticElement elt, final boolean warning) {
		validator.add(new GamlCompilationError(message, elt, warning));
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
			syntacticContents = new GamlCompatibilityConverter(this).buildSyntacticContents();
		}
		return syntacticContents;
	}

	public void eraseSyntacticContents() {
		if ( syntacticContents != null ) {
			syntacticContents.dispose();
		}
		syntacticContents = null;
	}
	//
	// public void listVisibleResourcesFromMe() {
	// IResourceDescription descr = index.getResourceDescription(getURI());
	// GuiUtils.debug("Resources visible from " + getURI().lastSegment());
	// for ( IContainer visibleContainer : manager.getVisibleContainers(descr, index) ) {
	// for ( IResourceDescription visibleResourceDesc : visibleContainer
	// .getResourceDescriptions() ) {
	// System.out.println("      " + visibleResourceDesc.getURI());
	// }
	// }
	// }
	//
	// public void listExportedObjectsByMe() {
	// final IResourceServiceProvider resourceServiceProvider =
	// resourceServiceProviderRegistry.getResourceServiceProvider(getURI());
	// IResourceDescription.Manager manager =
	// resourceServiceProvider.getResourceDescriptionManager();
	// IResourceDescription description = manager.getResourceDescription(this);
	// Iterable<IEObjectDescription> descriptions = description.getExportedObjects();
	// GuiUtils.debug("Objects exported by " + getURI().lastSegment());
	// for ( IEObjectDescription d : descriptions ) {
	// System.out.println("     " + d.getName());
	// }
	//
	// }

}
