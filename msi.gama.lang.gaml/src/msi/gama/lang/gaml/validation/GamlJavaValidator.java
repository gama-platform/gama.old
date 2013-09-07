/*
 * GAMA - V1.4 http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2012
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2012
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen (Batch, GeoTools & JTS), 2009-2012
 * - Beno�t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.lang.gaml.validation;

import static msi.gaml.factories.DescriptionFactory.getModelFactory;
import java.util.*;
import msi.gama.common.interfaces.IGamlIssue;
import msi.gama.common.util.GuiUtils;
import msi.gama.kernel.model.IModel;
import msi.gama.lang.gaml.gaml.*;
import msi.gama.lang.gaml.resource.GamlResource;
import msi.gama.lang.utils.GamlExpressionCompiler;
import msi.gama.util.GAML;
import msi.gaml.compilation.*;
import msi.gaml.descriptions.ModelDescription;
import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.*;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.*;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.xtext.resource.*;
import org.eclipse.xtext.util.Arrays;
import org.eclipse.xtext.validation.Check;

public class GamlJavaValidator extends AbstractGamlJavaValidator {

	static {
		GAML.getExpressionFactory().registerParser(new GamlExpressionCompiler());
	}

	private final static XtextResourceSet buildResourceSet = new SynchronizedXtextResourceSet();

	// AD 22/1/13 : set to false to avoid lags in compilation.
	static boolean FORCE_VALIDATION = false;
	GamlResource currentResource;

	// private DiagnosticChain diagnosticsChain;

	@Check()
	public synchronized void validate(final Model model) {
		try {
			final GamlResource r = (GamlResource) model.eResource();
			currentResource = r;
			ModelDescription result = null;
			if ( FORCE_VALIDATION || r.getErrors().isEmpty() ) {
				final long begin = System.nanoTime();
				final XtextResourceSet resourceSet = (XtextResourceSet) r.getResourceSet();
				result = parse(r, resourceSet);
				if ( r.getErrors().isEmpty() ) {
					result = getModelFactory().validate(result);
				}
				GuiUtils.debug("=> " + result + " in " + (System.nanoTime() - begin) / 1000000d + " ms.");
			}
			if ( result != null ) {
				final boolean hasError = result.hasErrors();
				for ( final GamlCompilationError error : result.getErrors() ) {
					add(error);
				}
				r.updateWith(hasError, hasError ? Collections.EMPTY_SET : result.getExperimentNames());
				// AD 7/9/2013: Addition of a force disposal to get rid of the description
				result.dispose();
			} else {
				r.updateWith(true, Collections.EMPTY_SET);
			}
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	public ModelDescription validateForContentAssist(final GamlResource r) {
		return null;
	}

	public IModel build(final GamlResource resource) {
		final ModelDescription description = parse(resource, buildResourceSet);
		if ( resource.getErrors().isEmpty() ) {
			final IModel model = getModelFactory().compile(description);
			cleanResourceSet(buildResourceSet, true);
			return model;
		}
		return null;
	}

	public Map<URI, SyntacticElement> buildCompleteSyntacticTree(final GamlResource resource,
		final ResourceSet resourceSet) {
		final Map<URI, SyntacticElement> models = new LinkedHashMap();
		final LinkedHashSet<GamlResource> totalResources = new LinkedHashSet<GamlResource>();
		final LinkedHashSet<GamlResource> newResources = new LinkedHashSet<GamlResource>();
		// Forcing the resource set to reload the primary resource, even though it has been
		// passed, in order to be sure that all resources will belong to the same resource set.
		final GamlResource first = (GamlResource) resourceSet.getResource(resource.getURI(), true);

		newResources.add(first);
		while (!newResources.isEmpty()) {
			final List<GamlResource> resourcesToConsider = new ArrayList<GamlResource>(newResources);
			newResources.clear();
			for ( final GamlResource gr : resourcesToConsider ) {
				if ( totalResources.add(gr) ) {
					final LinkedHashSet<GamlResource> imports = listImports(gr, resourceSet);
					newResources.addAll(imports);
				}
			}
		}
		for ( final GamlResource r : totalResources ) {
			// GuiUtils.debug("Building " + r + " as part of the validation of " + resource);
			models.put(r.getURI(), r.getSyntacticContents());
		}
		return models;
	}

	public LinkedHashSet<GamlResource> listImports(final GamlResource resource, final ResourceSet resourceSet) {
		final LinkedHashSet<GamlResource> imports = new LinkedHashSet();
		final Model model = (Model) resource.getContents().get(0);
		for ( final Import imp : model.getImports() ) {
			final String importUri = imp.getImportURI();
			final URI iu = URI.createURI(importUri).resolve(resource.getURI());
			try {
				final GamlResource ir = (GamlResource) resourceSet.getResource(iu, true);
				imports.add(ir);
			} catch (Exception e) {
				GuiUtils.debug("Error in validation: XText cannot load " + iu);
			}
			// if ( !ir.getErrors().isEmpty() ) {
			// resource.error("Imported file " + ir.getURI().lastSegment() + " has errors. Fix them first.",
			// new SyntacticElement(IKeyword.INCLUDE, imp), true);
			// }

		}
		return imports;
	}

	@SuppressWarnings("restriction")
	private ModelDescription parse(final GamlResource resource, final XtextResourceSet resourceSet) {
		final Map<URI, SyntacticElement> models = buildCompleteSyntacticTree(resource, resourceSet);
		GAML.getExpressionFactory().getParser().reset();
		final IPath path;
		if ( resource.getURI().isPlatform() ) {
			path = new Path(resource.getURI().toPlatformString(false));
		} else {
			path = new Path(resource.getURI().path());
		}
		final IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(path);
		// NullPointerException when accessing a file / project with a space in it !
		// FIX: see http://trac.rtsys.informatik.uni-kiel.de/trac/kieler/ticket/1065
		// This is a workaround, not very elegant, but it works.
		IPath fullPath = file.getLocation();
		if ( fullPath == null && file instanceof org.eclipse.core.internal.resources.Resource ) {
			final org.eclipse.core.internal.resources.Resource r = (org.eclipse.core.internal.resources.Resource) file;
			fullPath = r.getLocalManager().locationFor(r);
		}
		final String modelPath = fullPath == null ? "" : fullPath.toOSString();
		fullPath = file.getProject().getLocation();
		if ( fullPath == null && file.getProject() instanceof org.eclipse.core.internal.resources.Resource ) {
			final org.eclipse.core.internal.resources.Resource r =
				(org.eclipse.core.internal.resources.Resource) file.getProject();
			fullPath = r.getLocalManager().locationFor(r);
		}
		final String projectPath = fullPath == null ? "" : fullPath.toOSString();
		return getModelFactory().assemble(projectPath, modelPath, new ArrayList(models.values()));
	}

	private void cleanResourceSet(final XtextResourceSet resourceSet, final boolean clear) {
		// for ( Resource r : resourceSet.getResources() ) {
		// // if ( !r.getErrors().isEmpty() ) {
		// // ((GamlResource) r).eraseSyntacticContents();
		// // }
		// }
		if ( clear ) {
			resourceSet.getResources().clear();
		}
	}

	public GamlResource getCurrentRessource() {
		return currentResource;
	}

	public void add(final GamlCompilationError e) {
		final EObject object = e.getStatement();
		if ( object == null ) {
			GuiUtils.debug("*** Internal compilation problem : " + e.toString());
			return;
		}
		if ( object.eResource() == null ) { return; }
		if ( object.eResource() != getCurrentRessource() ) {
			if ( !e.isWarning() && !e.isInfo() ) {
				final EObject imp = findImport(object.eResource().getURI());
				if ( imp != null ) {
					error("Error detected in imported file: " + e.toString(), imp,
						GamlPackage.Literals.IMPORT__IMPORT_URI, IGamlIssue.IMPORT_ERROR, object.eResource().getURI()
							.toString());
				} else {
					error(
						"Errors detected in indirectly imported file " + object.eResource().getURI() + ": " +
							e.toString(), getCurrentObject(), GamlPackage.Literals.GAML_DEFINITION__NAME,
						IGamlIssue.IMPORT_ERROR, object.eResource().getURI().toString());
				}
			}
			return;
		}
		EStructuralFeature feature = null;
		if ( e instanceof Statement ) {
			feature = GamlPackage.Literals.STATEMENT__KEY;
		}
		if ( !Arrays.contains(e.getData(), null) ) {
			if ( e.isInfo() ) {
				info(e.toString(), object, feature, 0, e.getCode(), e.getData());
			} else if ( e.isWarning() ) {
				warning(e.toString(), object, feature, 0, e.getCode(), e.getData());
			} else {
				error(e.toString(), object, feature, 0, e.getCode(), e.getData());
			}
		}
	}

	private EObject findImport(final URI uri) {
		Model m;
		try {
			m = (Model) getCurrentObject();
		} catch (final NullPointerException ex) {
			return null;
		}
		for ( final Import imp : m.getImports() ) {
			final URI iu = URI.createURI(imp.getImportURI()).resolve(getCurrentRessource().getURI());
			if ( uri.equals(iu) ) { return imp; }
		}
		return null;
	}

}
