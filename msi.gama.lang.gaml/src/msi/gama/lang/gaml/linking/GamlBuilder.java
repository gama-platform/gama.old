/**
 * Created by drogoul, 11 avr. 2012
 * 
 */
package msi.gama.lang.gaml.linking;

import static msi.gaml.factories.DescriptionFactory.getModelFactory;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.*;
import msi.gama.common.interfaces.ISyntacticElement;
import msi.gama.common.util.IErrorCollector;
import msi.gama.kernel.model.IModel;
import msi.gama.lang.gaml.gaml.*;
import msi.gama.lang.utils.*;
import msi.gaml.compilation.GamlCompilationError;
import msi.gaml.factories.ModelStructure;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.resource.XtextResource;

/**
 * The class GamlBuilder.
 * 
 * @author drogoul
 * @since 11 avr. 2012
 * 
 */
public class GamlBuilder {

	private final XtextResource resource;
	private final IErrorCollector collect;

	public GamlBuilder(final XtextResource r, final IErrorCollector c) {
		resource = r;
		collect = c;
	}

	public boolean validate() throws MalformedURLException, IOException {
		ModelStructure ms = parse();
		if ( ms != null ) {
			getModelFactory().validate(ms, collect);
		}
		return isOK();
	}

	public IModel build() throws MalformedURLException, IOException {
		ModelStructure ms = parse();
		if ( ms != null ) {
			IModel result = getModelFactory().compile(ms, collect);
			if ( isOK() ) {
				return result;
			} else if ( result != null ) {
				result.dispose();
			}
		}
		return null;
	}

	private ModelStructure parse() throws MalformedURLException, IOException {
		final Map<Resource, ISyntacticElement> trees = new LinkedHashMap();
		buildRecursiveSyntacticTree(trees, resource);
		if ( isOK() ) {
			ModelStructure ms = new ModelStructure(resource, trees, collect);
			if ( isOK() ) { return ms; }
		}
		return null;
	}

	private boolean isOK() {
		return resource.getErrors().isEmpty() && !collect.hasErrors();
	}

	public void buildRecursiveSyntacticTree(final Map<Resource, ISyntacticElement> trees,
		final Resource r) {
		computeSyntacticTree(trees, r);
		for ( Import imp : ((Model) r.getContents().get(0)).getImports() ) {
			String importUri = imp.getImportURI();
			URI iu = URI.createURI(importUri).resolve(r.getURI());
			if ( iu != null && !iu.isEmpty() ) {
				Resource ir = r.getResourceSet().getResource(iu, true);
				if ( !trees.containsKey(ir) ) {
					if ( !ir.getErrors().isEmpty() ) {
						collect.add(new GamlCompilationError("Imported file " + importUri +
							" has errors.", new ECoreBasedStatementDescription("import", imp,
							collect)));
						continue;
					}
					buildRecursiveSyntacticTree(trees, ir);
				}
			}
		}
	}

	private void computeSyntacticTree(final Map<Resource, ISyntacticElement> trees, final Resource r) {
		if ( trees.containsKey(r) ) { return; }
		ISyntacticElement e =
			GamlToSyntacticElements.doConvert((Model) r.getContents().get(0), collect);
		trees.put(r, e);
	}

}
