/**
 * Created by drogoul, 11 avr. 2012
 * 
 */
package msi.gama.lang.gaml.linking;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.*;
import msi.gama.common.interfaces.ISyntacticElement;
import msi.gama.common.util.IErrorCollector;
import msi.gama.kernel.model.IModel;
import msi.gama.lang.gaml.gaml.*;
import msi.gama.lang.utils.*;
import msi.gaml.compilation.GamlCompilationError;
import msi.gaml.factories.*;
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
	private final Map<Resource, ISyntacticElement> trees = new HashMap(2);

	public GamlBuilder(final XtextResource r, final IErrorCollector c) {
		resource = r;
		collect = c;
	}

	public IModel build() throws MalformedURLException, IOException, InterruptedException {
		IModel result = null;
		trees.clear();
		buildRecursiveSyntacticTree(resource);
		if ( resource.getErrors().isEmpty() ) {
			ModelStructure ms = new ModelStructure(resource, trees, collect);
			result = (IModel) DescriptionFactory.getModelFactory().compile(ms, collect);
			if ( resource.getErrors().isEmpty() ) { return result; }
		}
		return null;
	}

	public Map<Resource, ISyntacticElement> buildRecursiveSyntacticTree(final Resource r) {
		computeSyntacticTree(r);
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
					buildRecursiveSyntacticTree(ir);
				}
			}
		}
		return trees;
	}

	private void computeSyntacticTree(final Resource r) {
		if ( trees.containsKey(r) ) { return; }
		Model m = (Model) r.getContents().get(0);
		ISyntacticElement e = GamlToSyntacticElements.doConvert(m, collect);
		trees.put(r, e);
	}

}
