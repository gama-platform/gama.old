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
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.diagnostics.IDiagnosticConsumer;
import org.eclipse.xtext.linking.lazy.LazyLinker;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;

/**
 * The class GamlLinker.
 * 
 * @author drogoul
 * @since 6 avr. 2012
 * 
 */
public class GamlLinker extends LazyLinker implements IGamlBuilder, IErrorCollector {

	private final Map<URI, GamlBuilder> builders = new HashMap();

	public GamlLinker() {
		GAMA.setGamlBuilder(this);
	}

	private boolean hasErrors;

	@Override
	protected void afterModelLinked(final EObject model, final IDiagnosticConsumer d) {
		hasErrors = false;
		if ( !GamaBundleLoader.contributionsLoaded ) { return; }
		Resource r = model.eResource();
		validate(r);
	}

	private GamlBuilder getBuilder(final URI uri) {
		GamlBuilder result = builders.get(uri);
		if ( result == null ) {
			result = new GamlBuilder(this);
			builders.put(uri, result);
		}
		return result;
	}

	private void error(final String message, final EObject object) {
		hasErrors = true;
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

	@Override
	public boolean hasErrors() {
		return hasErrors;
	}

	/**
	 * @param r
	 * @return
	 */
	public Map<URI, ISyntacticElement> buildCompleteSyntacticTree(final Resource r) {
		Map<URI, ISyntacticElement> trees = new LinkedHashMap();
		getBuilder(r.getURI()).buildRecursiveSyntacticTree(trees, r);
		return trees;
	}

	/**
	 * @see msi.gama.common.interfaces.IGamlBuilder#build(org.eclipse.emf.ecore.resource.Resource)
	 */
	@Override
	public IModel build(final Resource r) {
		// try {
		return getBuilder(r.getURI()).build(r);
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		// return null;
	}

	@Override
	public void validate(final Resource r) {
		boolean isOK = false;
		try {
			getBuilder(r.getURI()).validate(r);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @see msi.gama.common.interfaces.IGamlBuilder#addListener(org.eclipse.emf.ecore.resource.Resource,
	 *      msi.gama.common.interfaces.IGamlBuilder.Listener)
	 */
	@Override
	public void addListener(final URI uri, final Listener listener) {
		GamlBuilder b = getBuilder(uri);
		b.setListener(listener);
	}

	/**
	 * @see msi.gama.common.interfaces.IGamlBuilder#removeListener(msi.gama.common.interfaces.IGamlBuilder.Listener)
	 */
	@Override
	public void removeListener(final Listener listener) {
		for ( Map.Entry<URI, GamlBuilder> entry : builders.entrySet() ) {
			GamlBuilder b = entry.getValue();
			IGamlBuilder.Listener l = b.getListener();
			if ( l != null && l.equals(listener) ) {
				b.removeListener();
				break;
			}
		}
	}

}
