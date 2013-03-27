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
 * - Beno”t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
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
import msi.gama.common.interfaces.*;
import msi.gama.common.util.GuiUtils;
import msi.gama.kernel.model.IModel;
import msi.gama.lang.gaml.gaml.*;
import msi.gama.lang.gaml.resource.GamlResource;
import msi.gama.lang.utils.*;
import msi.gama.runtime.GAMA;
import msi.gaml.compilation.*;
import msi.gaml.descriptions.ModelDescription;
import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.*;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.*;
import org.eclipse.xtext.resource.*;
import org.eclipse.xtext.util.Arrays;
import org.eclipse.xtext.validation.Check;

public class GamlJavaValidator extends AbstractGamlJavaValidator {

	static {
		GAMA.getExpressionFactory().registerParser(new GamlExpressionCompiler());
	}

	private final static XtextResourceSet buildResourceSet = new SynchronizedXtextResourceSet();

	// AD 22/1/13 : set to false to avoid lags in compilation.
	static boolean FORCE_VALIDATION = false;
	GamlResource currentResource;

	@Check()
	public synchronized void validate(final Model model) {
		try {
			GuiUtils.debug("GamlJavaValidator processing " +
				model.eResource().getURI().lastSegment() + "...");
			GamlResource r = (GamlResource) model.eResource();
			currentResource = r;
			ModelDescription result = null;
			if ( FORCE_VALIDATION || r.getErrors().isEmpty() ) {
				result = validate(r);
			}
			boolean hasError = result == null || !result.getErrors().isEmpty();
			if ( result != null ) {
				for ( GamlCompilationError warning : result.getWarnings() ) {
					add(warning);
				}
				if ( hasError ) {
					for ( GamlCompilationError error : result.getErrors() ) {
						add(error);
					}
					r.setModelDescription(true, result);
				} else {
					r.setModelDescription(false, result);
				}
			} else {
				r.setModelDescription(true, null);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private ModelDescription validate(GamlResource r) {
		// this.listVisibleResourcesFromMe();
		long begin = System.nanoTime();
		XtextResourceSet resourceSet = (XtextResourceSet) r.getResourceSet();
		ModelDescription description = parse(r, resourceSet);
		if ( r.getErrors().isEmpty() ) {
			cleanResourceSet(resourceSet, false);
			description = getModelFactory().validate(description);
		}
		long end = System.nanoTime();
		GuiUtils.debug("Validation of " + description + " took " + (end - begin) / 1000000d +
			" milliseconds");
		return description;
	}

	public IModel build(final GamlResource resource) {
		ModelDescription description = parse(resource, buildResourceSet);
		if ( resource.getErrors().isEmpty() ) {
			IModel model = getModelFactory().compile(description);
			cleanResourceSet(buildResourceSet, true);
			return model;
		}
		return null;
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
			// we ignore "platform:" extensions
			if ( importUri.startsWith("platform:") ) {
				continue;
			}
			URI iu = URI.createURI(importUri).resolve(resource.getURI());
			GamlResource ir = (GamlResource) resourceSet.getResource(iu, true);
			if ( !ir.getErrors().isEmpty() ) {
				resource.error("Imported file " + ir.getURI().lastSegment() +
					" has errors. Fix them first.", new SyntacticStatement(IKeyword.INCLUDE, imp),
					true);
			}
			imports.add(ir);
		}
		return imports;
	}

	@SuppressWarnings("restriction")
	private ModelDescription parse(final GamlResource resource, final XtextResourceSet resourceSet) {
		final Map<URI, ISyntacticElement> models =
			buildCompleteSyntacticTree(resource, resourceSet);

		IPath path = new Path(resource.getURI().toPlatformString(false));
		IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(path);
		// NullPointerException when accessing a file / project with a space in it !
		// FIX: see http://trac.rtsys.informatik.uni-kiel.de/trac/kieler/ticket/1065
		// This is a workaround, not very elegant, but it works.
		IPath fullPath = file.getLocation();
		if ( fullPath == null && file instanceof org.eclipse.core.internal.resources.Resource ) {
			org.eclipse.core.internal.resources.Resource r =
				(org.eclipse.core.internal.resources.Resource) file;
			fullPath = r.getLocalManager().locationFor(r);
		}
		String modelPath = fullPath == null ? "" : fullPath.toOSString();
		fullPath = file.getProject().getLocation();
		if ( fullPath == null &&
			file.getProject() instanceof org.eclipse.core.internal.resources.Resource ) {
			org.eclipse.core.internal.resources.Resource r =
				(org.eclipse.core.internal.resources.Resource) file.getProject();
			fullPath = r.getLocalManager().locationFor(r);
		}
		String projectPath = fullPath == null ? "" : fullPath.toOSString();
		return getModelFactory().assemble(projectPath, modelPath, new ArrayList(models.values()));
	}

	private void cleanResourceSet(final XtextResourceSet resourceSet, final boolean clear) {
		for ( Resource r : resourceSet.getResources() ) {
			((GamlResource) r).eraseSyntacticContents();
		}
		if ( clear ) {
			resourceSet.getResources().clear();
		}
	}

	public GamlResource getCurrentRessource() {
		return currentResource;
	}

	public void add(final GamlCompilationError e) {
		Object source = e.getStatement();
		if ( !(source instanceof EObject) ) {
			if ( source instanceof SyntheticStatement ) {
				GuiUtils.debug("*** Internal compilation problem in synthetic statement: " +
					e.toString());
			}
			return;
		}
		EObject object = (EObject) e.getStatement();
		if ( object == null ) {
			try {
				object = getCurrentObject();
			} catch (NullPointerException ex) {
				return;
			}
		}
		if ( object.eResource() == null ) { return; }
		if ( object.eResource() != getCurrentRessource() ) {
			if ( !e.isWarning() ) {
				EObject imp = findImport(object.eResource().getURI());
				if ( imp != null ) {
					warning("Fix import error first: " + e.toString(), imp, null, "ERROR",
						(String[]) null);
				}
			}
			return;
		}
		if ( !Arrays.contains(e.getData(), null) ) {
			if ( e.isWarning() ) {
				warning(e.toString(), object, null, 0, e.getCode(), e.getData());
			} else {
				error(e.toString(), object, null, 0, e.getCode(), e.getData());
			}
		}
	}

	private EObject findImport(final URI uri) {
		Model m;
		try {
			m = (Model) getCurrentObject();
		} catch (NullPointerException ex) {
			return null;
		}
		for ( Import imp : m.getImports() ) {
			URI iu = URI.createURI(imp.getImportURI()).resolve(getCurrentRessource().getURI());
			if ( uri.equals(iu) ) { return imp; }
		}
		return null;
	}
}
