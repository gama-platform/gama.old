/**
 * Created by drogoul, 11 avr. 2012
 * 
 */
package msi.gama.lang.gaml.linking;

import static msi.gaml.factories.DescriptionFactory.getModelFactory;
import java.util.*;
import msi.gama.common.interfaces.*;
import msi.gama.common.util.IErrorCollector;
import msi.gama.kernel.model.IModel;
import msi.gama.lang.gaml.gaml.*;
import msi.gama.lang.utils.*;
import msi.gaml.compilation.GamlCompilationError;
import msi.gaml.factories.ModelStructure;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;

/**
 * The class GamlBuilder.
 * 
 * @author drogoul
 * @since 11 avr. 2012
 * 
 */
public class GamlBuilder {

	private final IErrorCollector collect;
	private IGamlBuilder.Listener listener;

	public GamlBuilder(final IErrorCollector c) {
		collect = c;
	}

	public boolean validate(final Resource r) {
		ModelStructure ms = parse(r);
		if ( ms != null ) {
			getModelFactory().validate(ms, collect);
		}
		if ( listener != null ) {
			listener.validationEnded(r);
		}
		return isOK(r);
	}

	public IModel build(final Resource r) {
		ModelStructure ms = parse(r);
		if ( ms != null ) {
			IModel result = getModelFactory().compile(ms, collect);
			if ( isOK(r) ) {
				return result;
			} else if ( result != null ) {
				result.dispose();
			}
		}
		return null;
	}

	private ModelStructure parse(final Resource r) {
		final Map<URI, ISyntacticElement> models = new LinkedHashMap();
		buildRecursiveSyntacticTree(models, r);
		if ( isOK(r) ) {
			ModelStructure ms =
				new ModelStructure(r.getURI().toString(), new ArrayList(models.values()), collect);
			if ( isOK(r) ) { return ms; }
		}
		return null;
	}

	private boolean isOK(final Resource r) {
		return r.getErrors().isEmpty() && !collect.hasErrors();
	}

	public void buildRecursiveSyntacticTree(final Map<URI, ISyntacticElement> models,
		final Resource r) {
		computeSyntacticTree(models, r);
		for ( Import imp : ((Model) r.getContents().get(0)).getImports() ) {
			String importUri = imp.getImportURI();
			URI iu = URI.createURI(importUri).resolve(r.getURI());
			if ( iu != null && !iu.isEmpty() ) {
				Resource ir = r.getResourceSet().getResource(iu, true);
				if ( !models.containsKey(iu) ) {
					if ( !ir.getErrors().isEmpty() ) {
						collect.add(new GamlCompilationError("Imported file " + importUri +
							" has errors.", new ECoreBasedStatementDescription("import", imp,
							collect)));
						continue;
					}
					buildRecursiveSyntacticTree(models, ir);
				}
			}
		}
	}

	private void computeSyntacticTree(final Map<URI, ISyntacticElement> trees, final Resource r) {
		URI uri = r.getURI();
		if ( trees.containsKey(uri) ) { return; }
		trees.put(uri, GamlToSyntacticElements.doConvert((Model) r.getContents().get(0), collect));
	}

	/**
	 * @param listener2
	 */
	public void setListener(final IGamlBuilder.Listener listener) {
		this.listener = listener;
	}

	public void removeListener() {
		listener = null;
	}

	public IGamlBuilder.Listener getListener() {
		return listener;
	}

}
