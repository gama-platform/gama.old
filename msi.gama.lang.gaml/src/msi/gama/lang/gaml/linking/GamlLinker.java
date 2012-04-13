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

	public GamlLinker() {
		GAMA.setGamlBuilder(this);
	}

	private final Set<IBuilderListener> listeners = new HashSet();
	private final Map<Resource, GamlBuilder> builders = new HashMap();
	private boolean hasErrors;

	@Override
	protected void afterModelLinked(final EObject model, final IDiagnosticConsumer d) {
		hasErrors = false;
		if ( !GamaBundleLoader.contributionsLoaded || model == null ) { return; }
		IModel currentModel = null;
		Resource r = model.eResource();
		try {
			fireBuildBegun(r);
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

	@Override
	public boolean addListener(final IBuilderListener l) {
		if ( l == null ) { return false; }
		return listeners.add(l);
	}

	@Override
	public boolean removeListener(final IBuilderListener l) {
		return listeners.remove(l);
	}

	private void fireBuildBegun(final Resource m) {
		for ( IBuilderListener l : listeners ) {
			l.beforeBuilding(m);
		}

	}

	private void fireBuildEnded(final Resource m, final IModel result) {
		boolean accepted = false;
		for ( IBuilderListener l : listeners ) {
			accepted = accepted || l.afterBuilding(m, result);
			if ( accepted ) { return; }
		}
		if ( result != null ) {
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
