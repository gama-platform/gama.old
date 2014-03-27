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

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import msi.gama.common.interfaces.IGamlIssue;
import msi.gama.common.util.GuiUtils;
import msi.gama.kernel.model.IModel;
import msi.gama.lang.gaml.gaml.GamlPackage;
import msi.gama.lang.gaml.gaml.Import;
import msi.gama.lang.gaml.gaml.Model;
import msi.gama.lang.gaml.gaml.Statement;
import msi.gama.lang.gaml.resource.GamlResource;
import msi.gama.lang.utils.GamlExpressionCompiler;
import msi.gama.util.GAML;
import msi.gaml.compilation.GamlCompilationError;
import msi.gaml.compilation.ISyntacticElement;
import msi.gaml.descriptions.ModelDescription;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.xtext.resource.SynchronizedXtextResourceSet;
import org.eclipse.xtext.resource.XtextResourceSet;
import org.eclipse.xtext.util.Arrays;
import org.eclipse.xtext.validation.Check;

public class GamlJavaValidator extends AbstractGamlJavaValidator {

	static {
		GuiUtils.debug(">> Registering GAML expression compiler.");
		GAML.getExpressionFactory().registerParser(new GamlExpressionCompiler());
	}

	// static {
	// GAML.getExpressionFactory().registerParser(new GamlExpressionCompiler());
	// }

	private final static XtextResourceSet buildResourceSet = new SynchronizedXtextResourceSet();

	// AD 22/1/13 : set to false to avoid lags in compilation.
	static boolean FORCE_VALIDATION = false;

	// AD 15/9/13 : suppression of currentResource to avoid threading problems between validators
	// GamlResource currentResource;

	// private DiagnosticChain diagnosticsChain;

	@Check()
	public synchronized void validate(final Model model) {
		try {
			final GamlResource r = (GamlResource) model.eResource();
			Map<Object, Object> context = getContext();
			// AD 15/9/13 Addition of a check in the current context to verify if this validator (or any other) is not
			// already validating the resource
			// if ( context.containsKey(r) ) {
			// // r is already validated
			// return;
			// }
			// context.put(r, this);
			// currentResource = r;
			ModelDescription result = null;
			if ( FORCE_VALIDATION || r.getErrors().isEmpty() ) {
				final long begin = System.nanoTime();
				final XtextResourceSet resourceSet = (XtextResourceSet) r.getResourceSet();
				result = parse(r, resourceSet, true);
				if ( r.getErrors().isEmpty() ) {
					result.validate();
				}
				GuiUtils.debug("=> " + result + " in " + (System.nanoTime() - begin) / 1000000d + " ms.");
			}
			if ( result != null ) {
				final boolean hasError = result.hasErrors();
				for ( final GamlCompilationError error : result.getErrors() ) {
					add(error);
				}
				r.updateWith(hasError, hasError ? Collections.EMPTY_SET : result.getExperimentTitles());
				// AD 7/9/2013: Addition of a force disposal to get rid of the description
				result.dispose();
			} else {
				r.updateWith(true, Collections.EMPTY_SET);
			}
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	public IModel build(final EObject object) {
		if ( !(object instanceof Model) ) { return null; }
		Model model = (Model) object;
		final GamlResource r = (GamlResource) model.eResource();
		try {
			ModelDescription description = parse(r, buildResourceSet, false);
			if ( r.getErrors().isEmpty() ) {
				final IModel result_model = (IModel) description.compile();
				return result_model;
			}
		} catch (Exception e) {
			error("Cannot compile file because of : " + e.getMessage(), r.getContents().get(0), null);
			return null;
		} finally {
			cleanResourceSet(buildResourceSet, true);
		}
		return null;
	} 
	
	public String validateModel(final EObject object) {
		if ( !(object instanceof Model) ) { return null; }
		String valVerdict = "";
		Model model = (Model) object;
		final GamlResource r = (GamlResource) model.eResource();
		try {
			ModelDescription result = parse(r, buildResourceSet, false);
			final XtextResourceSet resourceSet = (XtextResourceSet) r.getResourceSet();
			if ( r.getErrors().isEmpty() ) {
				result.validate();
			}
			if ( result != null ) {
				final boolean hasError = result.hasErrors();
				if (! hasError) {valVerdict = "Compilation: ok";}
				r.updateWith(hasError, hasError ? Collections.EMPTY_SET : result.getExperimentTitles());
				valVerdict = "Errors during Compilation:";
				result.dispose();
			} else {
				r.updateWith(true, Collections.EMPTY_SET);
				valVerdict = "Errors during Compilation:";
				for (GamlCompilationError er: result.getErrors()) {
					valVerdict +="\n" + er.toString();
				}
			}
			return valVerdict;
		} catch (final Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	// TODO : Verify the behavior in case of compilation errors.
	public IModel build(final GamlResource resource) {
		try {
			final ModelDescription description = parse(resource, buildResourceSet, true);
			if ( resource.getErrors().isEmpty() ) {
				final IModel model = (IModel) description.compile();
				return model;
			}
		} catch (Exception e) {
			error("Cannot compile file because of : " + e.getMessage(), resource.getContents().get(0), null);
			return null;
		} finally {
			cleanResourceSet(buildResourceSet, true);
		}
		return null;
	}

	public Map<URI, ISyntacticElement> buildCompleteSyntacticTree(final GamlResource resource,
		final ResourceSet resourceSet, final boolean forceReload) {
		final Map<URI, ISyntacticElement> models = new LinkedHashMap();
		final LinkedHashSet<GamlResource> totalResources = new LinkedHashSet<GamlResource>();
		final LinkedHashSet<GamlResource> newResources = new LinkedHashSet<GamlResource>();
		// Forcing the resource set to reload the primary resource, even though it has been
		// passed, in order to be sure that all resources will belong to the same resource set.
		final GamlResource first;
		if ( forceReload ) {
			first = (GamlResource) resourceSet.getResource(resource.getURI(), true);
		} else {
			first = resource;
		}

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
			ISyntacticElement s = r.getSyntacticContents();
			// GuiUtils.debug("Building " + r + " as part of the validation of " + resource);
			if ( s != null ) {
				models.put(r.getURI(), s);
			}
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
				if ( !ir.getErrors().isEmpty() ) {
					add(new GamlCompilationError("Imported file " + ir.getURI().lastSegment() +
						" has errors. Fix them first.", IGamlIssue.IMPORT_ERROR, imp, false, false));
				} else {
					imports.add(ir);
				}
			} catch (Exception e) {
				GuiUtils.debug("Error in validation: XText cannot load " + iu);
			}

		}
		return imports;
	}

	@SuppressWarnings("restriction")
	private ModelDescription parse(final GamlResource resource, final XtextResourceSet resourceSet,
		final boolean forceReload) {
		final Map<URI, ISyntacticElement> models = buildCompleteSyntacticTree(resource, resourceSet, forceReload);
		GAML.getExpressionFactory().getParser().reset();
		IPath path;
		boolean isFile = false;
		if ( resource.getURI().isPlatform() ) {
			path = new Path(resource.getURI().toPlatformString(false));
		} else if ( resource.getURI().isFile() ) {
			isFile = true;
			path = new Path(resource.getURI().toFileString());
		} else {
			path = new Path(resource.getURI().path());
		}
		path = new Path(URLDecoder.decode(path.toOSString()));

		String modelPath, projectPath;

		if ( isFile ) {
			modelPath = path.toOSString();
			projectPath = modelPath; // removeLastSegment(1) ?
		} else {
			final IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(path);
			IPath fullPath = file.getLocation();
			modelPath = fullPath == null ? "" : fullPath.toOSString();
			fullPath = file.getProject().getLocation();
			projectPath = fullPath == null ? "" : fullPath.toOSString();
		}
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

	// public GamlResource getCurrentRessource() {
	// return currentResource;
	// }

	public void add(final GamlCompilationError e) {
		final EObject object = e.getStatement();
		if ( object == null ) {
			GuiUtils.debug("*** Internal compilation problem : " + e.toString());
			return;
		}
		if ( object.eResource() == null ) { throw new RuntimeException(
			"Error detected in a syntethic object. Please debug to understand the cause"); }
		boolean isCurrentResource = true;
		try {
			checkIsFromCurrentlyCheckedResource(object);
		} catch (IllegalArgumentException ex) {
			isCurrentResource = false;
		}
		if ( !isCurrentResource ) {
			if ( !e.isWarning() && !e.isInfo() ) {
				final EObject imp = findImport(object.eResource().getURI());
				if ( imp != null ) {
					importedError("Error detected in imported file " + ": " + e.toString(), imp,
						GamlPackage.Literals.IMPORT__IMPORT_URI, IGamlIssue.IMPORT_ERROR, object.eResource().getURI()
							.toString());
				} else {
					importedError("Errors detected in indirectly imported file " + object.eResource().getURI() + ": " +
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

	/**
	 * @param string
	 * @param currentObject
	 * @param gamlDefinitionName
	 * @param importError
	 * @param string2
	 */
	private void importedError(final String string, final EObject currentObject, final EAttribute gamlDefinitionName,
		final String importError, final String uri) {
		Map<Object, Object> context = getContext();
		if ( context.containsKey(currentObject) ) {
			Set<String> importsWithErrors = (Set<String>) context.get(currentObject);
			if ( importsWithErrors.contains(uri) ) { return; }
		} else {
			Set<String> importsWithErrors = new HashSet();
			importsWithErrors.add(uri);
			context.put(currentObject, importsWithErrors);
			warning(string, currentObject, gamlDefinitionName, importError, uri);
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
			final URI iu = URI.createURI(imp.getImportURI()).resolve(m.eResource().getURI());
			if ( uri.equals(iu) ) { return imp; }
		}
		return null;
	}

}
