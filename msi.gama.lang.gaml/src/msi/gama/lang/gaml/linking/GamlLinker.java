/**
 * Created by drogoul, 6 avr. 2012
 * 
 */
package msi.gama.lang.gaml.linking;

import java.util.*;
import msi.gama.common.interfaces.*;
import msi.gama.common.util.IErrorCollector;
import msi.gama.kernel.model.IModel;
import msi.gama.lang.gaml.gaml.*;
import msi.gama.lang.utils.GamlToSyntacticElements;
import msi.gama.runtime.GAMA;
import msi.gaml.compilation.*;
import msi.gaml.factories.*;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.EcoreUtil2;
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

	public GamlLinker() {
		GAMA.setGamlBuilder(this);
	}

	private final Set<IBuilderListener> listeners = new HashSet();

	@Override
	protected void afterModelLinked(final EObject model,
		final IDiagnosticConsumer diagnosticsConsumer) {
		if ( !GamaBundleLoader.contributionsLoaded || model == null ) { return; }
		Model m = (Model) model;
		IModel currentModel = null;
		Resource mainFile = m.eResource();
		try {
			// if ( mainFile.getErrors().isEmpty() ) {
			Map<Resource, ISyntacticElement> elements = buildCompleteSyntacticTree(mainFile);
			if ( !hasErrors(mainFile) ) {
				ModelStructure ms = new ModelStructure(mainFile, elements, this);
				currentModel = (IModel) DescriptionFactory.getModelFactory().compile(ms, this);
				if ( hasErrors(mainFile) ) {
					currentModel = null;
				}
			}
			// }
		} catch (Exception e1) {
			e1.printStackTrace();
		} finally {
			fireBuildEnded(m, currentModel);
		}

	}

	public Map<Resource, ISyntacticElement> buildCompleteSyntacticTree(final Resource r) {
		Map<Resource, ISyntacticElement> docs = new LinkedHashMap();
		buildRecursiveSyntacticTree(docs, r);
		return docs;
	}

	private void buildRecursiveSyntacticTree(final Map<Resource, ISyntacticElement> docs,
		final Resource r) {
		Model m = (Model) r.getContents().get(0);
		docs.put(r, getOwnSyntacticTree(r));
		for ( Import imp : m.getImports() ) {
			String importUri = imp.getImportURI();
			URI iu = URI.createURI(importUri).resolve(r.getURI());
			if ( iu != null && !iu.isEmpty() && EcoreUtil2.isValidUri(r, iu) ) {
				Resource ir = r.getResourceSet().getResource(iu, true);
				if ( ir != r && !docs.containsKey(ir) ) {
					if ( !ir.getErrors().isEmpty() ) {
						this.error("Imported file " + importUri + " has errors.", imp);
						continue;
					}
					buildRecursiveSyntacticTree(docs, ir);
				}
			}
		}
	}

	private ISyntacticElement getOwnSyntacticTree(final Resource r) {
		Model m = (Model) r.getContents().get(0);
		ISyntacticElement e = GamlToSyntacticElements.doConvert(m, this);
		return e;
	}

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

	/**
	 * @see msi.gama.common.util.IErrorCollector#hasErrors()
	 */

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

	private void fireBuildEnded(final Model m, final IModel result) {
		for ( IBuilderListener l : listeners ) {
			l.afterBuilding(m.eResource(), result);
		}
	}

}
