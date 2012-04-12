/**
 * Created by drogoul, 6 avr. 2012
 * 
 */
package msi.gama.lang.gaml.linking;

import java.util.*;
import msi.gama.common.interfaces.*;
import msi.gama.common.util.IErrorCollector;
import msi.gama.kernel.model.IModel;
import msi.gama.runtime.GAMA;
import msi.gaml.compilation.*;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.diagnostics.IDiagnosticConsumer;
import org.eclipse.xtext.linking.lazy.LazyLinker;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;
import org.eclipse.xtext.resource.XtextResource;

/**
 * The class GamlLinker.
 * 
 * @author drogoul
 * @since 6 avr. 2012
 * 
 */
public class GamlLinker extends LazyLinker implements IGamlBuilder, IErrorCollector {

	// private final GamlModelConverter converter = new GamlModelConverter();

	public GamlLinker() {
		GAMA.setGamlBuilder(this);
	}

	// Map<Resource, ISyntacticElement> trees = new HashMap();

	// @Override
	// protected void beforeModelLinked(final EObject model,
	// final IDiagnosticConsumer diagnosticsConsumer) {
	// Important to call the super method in order to remove all dangling references
	// super.beforeModelLinked(model, diagnosticsConsumer);
	// if ( !GamaBundleLoader.contributionsLoaded || model == null ) { return; }
	// Here, we do all the transformations necessary for making the eCore model on par with the
	// expected syntactic model of GAML. Involves moving EObjects around, etc.
	// converter.convert(model);

	// }

	private final Set<IBuilderListener> listeners = new HashSet();
	private final Map<Resource, GamlBuilder> builders = new HashMap();

	@Override
	protected void afterModelLinked(final EObject model, final IDiagnosticConsumer d) {
		if ( !GamaBundleLoader.contributionsLoaded || model == null ) { return; }
		IModel currentModel = null;
		Resource r = model.eResource();
		try {
			currentModel = getBuilder(r).build();
		} catch (Exception e1) {
			e1.printStackTrace();
		} finally {
			fireBuildEnded(r, currentModel);
		}

	}

	private GamlBuilder getBuilder(final Resource r) {
		GamlBuilder result = builders.get(r);
		if ( result == null ) {
			result = new GamlBuilder((XtextResource) r, this);
			builders.put(r, result);
		}
		return result;
	}

	// public Map<Resource, ISyntacticElement> buildCompleteSyntacticTree(final Resource r) {
	// Map<Resource, ISyntacticElement> docs = new LinkedHashMap();
	// buildRecursiveSyntacticTree(docs, r);
	// return docs;
	// }
	//
	// private void buildRecursiveSyntacticTree(final Map<Resource, ISyntacticElement> docs,
	// final Resource r) {
	// Model m = (Model) r.getContents().get(0);
	// docs.put(r, getOwnSyntacticTree(r));
	// for ( Import imp : m.getImports() ) {
	// String importUri = imp.getImportURI();
	// URI iu = URI.createURI(importUri).resolve(r.getURI());
	// if ( iu != null && !iu.isEmpty() && EcoreUtil2.isValidUri(r, iu) ) {
	// Resource ir = r.getResourceSet().getResource(iu, true);
	// if ( ir != r && !docs.containsKey(ir) ) {
	// if ( !ir.getErrors().isEmpty() ) {
	// this.error("Imported file " + importUri + " has errors.", imp);
	// continue;
	// }
	// buildRecursiveSyntacticTree(docs, ir);
	// }
	// }
	// }
	// }
	//
	// private ISyntacticElement getOwnSyntacticTree(final Resource r) {
	// Model m = (Model) r.getContents().get(0);
	// ISyntacticElement e = GamlToSyntacticElements.doConvert(m, this);
	// return e;
	// }

	private void error(final String message, final EObject object) {
		if ( object == null ) { return; }
		object.eResource().getErrors()
			.add(new GamlDiagnostic("", new String[0], message, NodeModelUtils.getNode(object)));
	}

	private void warning(final String message, final EObject object) {
		if ( object == null ) { return; }
		object.eResource().getWarnings()
			.add(new GamlDiagnostic("", new String[0], message, NodeModelUtils.getNode(object)));
	}

	/**
	 * @see msi.gama.common.util.IErrorCollector#add(msi.gaml.compilation.GamlCompilationError)
	 */
	@Override
	public void add(final GamlCompilationError e) {
		if ( e.isWarning() ) {
			warning(e.toString(), (EObject) e.getStatement());
		} else {
			error(e.toString(), (EObject) e.getStatement());
		}
	}

	public boolean hasErrors(final Resource resource) {
		return !resource.getErrors().isEmpty();
	}

	@Override
	public boolean addListener(final IBuilderListener l) {
		if ( l == null ) { return false; }
		return listeners.add(l);
	}

	@Override
	public boolean removeListener(final IBuilderListener l) {
		return listeners.remove(l);
	}

	private void fireBuildEnded(final Resource m, final IModel result) {
		boolean accepted = false;
		for ( IBuilderListener l : listeners ) {
			accepted = accepted || l.afterBuilding(m, result);
		}
		if ( !accepted && result != null ) {
			result.dispose();
		}
	}

	/**
	 * @param r
	 * @return
	 */
	public Map<Resource, ISyntacticElement> buildCompleteSyntacticTree(final Resource r) {
		return getBuilder(r).buildRecursiveSyntacticTree(r);
	}

	/**
	 * @see msi.gama.common.interfaces.IGamlBuilder#invalidate(org.eclipse.emf.ecore.resource.Resource)
	 */
	@Override
	public void invalidate(final Resource r) {
		// trees.remove(r);
	}

}
