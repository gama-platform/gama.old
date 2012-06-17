/**
 * Created by drogoul, 11 avr. 2012
 * 
 */
package msi.gama.lang.gaml.validation;

import static msi.gama.common.interfaces.IKeyword.INCLUDE;
import static msi.gaml.factories.DescriptionFactory.getModelFactory;
import java.util.*;
import msi.gama.common.interfaces.ISyntacticElement;
import msi.gama.kernel.model.IModel;
import msi.gama.lang.gaml.GamlResource;
import msi.gama.lang.gaml.gaml.*;
import msi.gama.lang.utils.*;
import msi.gama.runtime.GAMA;
import msi.gaml.compilation.GamlCompilationError;
import msi.gaml.descriptions.ModelDescription;
import msi.gaml.expressions.IExpressionFactory;
import msi.gaml.factories.ModelStructure;
import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.*;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.*;
import org.eclipse.xtext.resource.*;

/**
 * The class GamlBuilder.
 * 
 * @author drogoul
 * @since 11 avr. 2012
 * 
 */
public class GamlBuilder {

	public final static GamlBuilder INSTANCE = new GamlBuilder();

	private final static XtextResourceSet buildResourceSet = new SynchronizedXtextResourceSet();

	static {
		IExpressionFactory fact = GAMA.getExpressionFactory();
		fact.registerParser(new NewGamlExpressionCompiler());
	}

	public ModelDescription validate(final GamlResource resource) {
		XtextResourceSet resourceSet = (XtextResourceSet) resource.getResourceSet();
		ModelDescription model = getModelFactory().validate(parse(resource, resourceSet));
		cleanResourceSet(resourceSet, false);
		return model;
	}

	public IModel build(final GamlResource resource) {
		IModel model = getModelFactory().compile(parse(resource, buildResourceSet));
		cleanResourceSet(buildResourceSet, true);
		return model;
	}

	private void cleanResourceSet(final XtextResourceSet resourceSet, final boolean clear) {
		for ( Resource r : resourceSet.getResources() ) {
			((GamlResource) r).eraseSyntacticContents();
		}
		if ( clear ) {
			resourceSet.getResources().clear();
		}
	}

	private ModelStructure parse(final GamlResource resource, final XtextResourceSet resourceSet) {
		final Map<URI, ISyntacticElement> models =
			buildCompleteSyntacticTree(resource, resourceSet);

		IPath path = new Path(resource.getURI().toPlatformString(false));
		IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(path);
		String modelPath = file.getLocation().toOSString();
		String projectPath = file.getProject().getLocation().toOSString();
		return new ModelStructure(projectPath, modelPath, new ArrayList(models.values()));
	}

	public Map<URI, ISyntacticElement> buildCompleteSyntacticTree(final GamlResource resource,
		final ResourceSet resourceSet) {
		final Map<URI, ISyntacticElement> models = new LinkedHashMap();
		LinkedHashSet<GamlResource> totalResources = new LinkedHashSet<GamlResource>();
		LinkedHashSet<GamlResource> newResources = new LinkedHashSet<GamlResource>();
		// Forcing the resource set to reload the primary resource, even though it has been
		// passed, in order to be sure that all resources will belong to the same resource set.
		GamlResource first = (GamlResource) resourceSet.getResource(resource.getURI(), true);

		newResources.add(first);
		while (!newResources.isEmpty()) {
			List<GamlResource> resourcesToConsider = new ArrayList<GamlResource>(newResources);
			newResources.clear();
			for ( GamlResource gr : resourcesToConsider ) {
				if ( totalResources.add(gr) ) {
					LinkedHashSet<GamlResource> imports = listImports(gr, resourceSet);
					newResources.addAll(imports);
				}
			}
		}
		for ( GamlResource r : totalResources ) {
			// GuiUtils.debug("Building " + r + " as part of the validation of " + resource);
			models.put(r.getURI(), r.getSyntacticContents());
		}
		return models;
	}

	public LinkedHashSet<GamlResource> listImports(final GamlResource resource,
		final ResourceSet resourceSet) {
		LinkedHashSet<GamlResource> imports = new LinkedHashSet();
		Model model = (Model) resource.getContents().get(0);
		for ( Import imp : model.getImports() ) {
			String importUri = imp.getImportURI();
			URI iu = URI.createURI(importUri).resolve(resource.getURI());
			GamlResource ir = (GamlResource) resourceSet.getResource(iu, true);
			if ( !ir.getErrors().isEmpty() ) {
				resource.add(new GamlCompilationError("Imported file " + ir.getURI().lastSegment() +
					" has errors. Fix them first.", new SyntacticStatement(INCLUDE, imp), true));
			}
			imports.add(ir);
		}
		return imports;
	}
}
