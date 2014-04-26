/*********************************************************************************************
 * 
 * 
 * 'GamlResource.java', in plugin 'msi.gama.lang.gaml', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.lang.gaml.resource;

import static msi.gaml.factories.DescriptionFactory.getModelFactory;
import gnu.trove.set.hash.TLinkedHashSet;
import java.net.URLDecoder;
import java.util.*;
import msi.gama.common.interfaces.IGamlIssue;
import msi.gama.kernel.model.IModel;
import msi.gama.lang.gaml.gaml.*;
import msi.gama.lang.gaml.parsing.*;
import msi.gama.lang.gaml.parsing.GamlSyntacticParser.GamlParseResult;
import msi.gama.lang.gaml.validation.IGamlBuilderListener;
import msi.gama.util.*;
import msi.gaml.compilation.*;
import msi.gaml.descriptions.*;
import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.*;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.linking.lazy.LazyLinkingResource;
import com.google.common.base.Function;
import com.google.common.collect.*;

/*
 * 
 * The class GamlResource.
 * 
 * @author drogoul
 * 
 * @since 24 avr. 2012
 */
public class GamlResource extends LazyLinkingResource {

	private IGamlBuilderListener listener;
	private volatile ErrorCollector collector;
	private volatile boolean isValidating;
	private volatile boolean isEdited;

	public ErrorCollector getErrorCollector() {
		if ( collector == null ) {
			collector = new ErrorCollector(this);
		}
		return collector;
	}

	public void resetErrorCollector() {
		if ( collector == null ) {
			getErrorCollector();
		} else {
			collector.clear();
		}
	}

	@Override
	public String toString() {
		return "GAML resource" + "[" + getURI() + "]";
	}

	@Override
	protected void addSyntaxErrors() {
		super.addSyntaxErrors();
		GamlParseResult r = getParseResult();
		if ( r != null ) {
			getWarnings().addAll(r.getWarnings());
		}
	}

	@Override
	public GamlParseResult getParseResult() {
		GamlParseResult r = (GamlParseResult) super.getParseResult();
		r.fixURIsWith(this);
		return r;
	}

	public void updateWith(final Set<String> experiments) {
		if ( listener != null ) {
			listener.validationEnded(experiments, collector);
		}
	}

	public void setListener(final IGamlBuilderListener listener) {
		this.listener = listener;
	}

	public void removeListener() {
		listener = null;
	}

	public IGamlBuilderListener getListener() {
		return listener;
	}

	public ISyntacticElement getSyntacticContents() {
		GamlParseResult parseResult = getParseResult();
		if ( parseResult == null ) { // Should not happen, but in case...
			Set<org.eclipse.xtext.diagnostics.Diagnostic> errors = new LinkedHashSet();
			ISyntacticElement result = GamlCompatibilityConverter.buildSyntacticContents(getContents().get(0), errors);
			getWarnings().addAll(errors);
			return result;
		}
		return parseResult.getSyntacticContents();
	}

	// AD 8/4/14 The resource itself is now responsible for returning the whole syntactic contents of the set of files
	// that constitute the model

	private Iterable<ISyntacticElement> getAllSyntacticContents(final Set<GamlResource> totalResources) {
		return Iterables.transform(totalResources, new Function<GamlResource, ISyntacticElement>() {

			@Override
			public ISyntacticElement apply(final GamlResource r) {
				return r.getSyntacticContents();
			}

		});
	}

	private ModelDescription buildModelDescription(final Set<GamlResource> resources) {
		final Iterable<ISyntacticElement> models = getAllSyntacticContents(resources);
		GAML.getExpressionFactory().resetParser();
		IPath path = getPath();
		String modelPath, projectPath;
		if ( getURI().isFile() ) {
			modelPath = path.toOSString();
			projectPath = modelPath;
		} else {
			final IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(path);
			IPath fullPath = file.getLocation();
			modelPath = fullPath == null ? "" : fullPath.toOSString();
			fullPath = file.getProject().getLocation();
			projectPath = fullPath == null ? "" : fullPath.toOSString();
		}
		// GamlResourceDocManager.clearCache();
		// We document only when the resource is marked as 'edited'
		return getModelFactory().createModelDescription(projectPath, modelPath, ImmutableList.copyOf(models), getErrorCollector(),
			isEdited);
	}

	public Set<URI> computeImportedURIs() {
		ISyntacticElement element = this.getSyntacticContents();
		if ( !(element instanceof SyntacticModelElement) ) { return Collections.EMPTY_SET; }
		return ((SyntacticModelElement) element).getImports();
	}

	public LinkedHashSet<URI> computeAllImportedURIs() {

		final LinkedHashSet<URI> totalResources = new LinkedHashSet();
		final TLinkedHashSet<GamlResource> newResources = new TLinkedHashSet<GamlResource>();
		newResources.add(this);
		while (!newResources.isEmpty()) {
			final List<GamlResource> resourcesToConsider = new ArrayList(newResources);
			newResources.clear();
			for ( final GamlResource gr : resourcesToConsider ) {
				if ( totalResources.add(gr.getURI()) ) {
					final TOrderedHashMap<GamlResource, Import> imports = gr.loadImports();
					newResources.addAll(imports.keySet());
				}
			}
		}
		return totalResources;

	}

	public TOrderedHashMap<GamlResource, Import> loadImports() {
		final TOrderedHashMap<GamlResource, Import> imports = new TOrderedHashMap();
		final Model model = (Model) getContents().get(0);
		for ( final Import imp : model.getImports() ) {
			final String importUri = imp.getImportURI();
			final URI iu = URI.createURI(importUri).resolve(getURI());
			if ( EcoreUtil2.isValidUri(this, iu) ) {
				final GamlResource ir = (GamlResource) resourceSet.getResource(iu, true);
				if ( ir != null ) {
					imports.put(ir, imp);
				}
			}
		}
		return imports;
	}

	public TLinkedHashSet<GamlResource> loadAllResources() {
		final TLinkedHashSet<GamlResource> totalResources = new TLinkedHashSet<GamlResource>();
		Set<URI> uris = computeAllImportedURIs();
		for ( URI uri : uris ) {
			final GamlResource ir = (GamlResource) resourceSet.getResource(uri, true);
			if ( ir != null ) {
				totalResources.add(ir);
			}
		}
		return totalResources;
	}

	public EObject findImport(final URI uri) {
		Model m = (Model) getContents().get(0);
		for ( final Import imp : m.getImports() ) {
			final URI iu = URI.createURI(imp.getImportURI()).resolve(getURI());
			if ( uri.equals(iu) ) { return imp; }
		}
		return null;
	}

	private void invalidateBecauseOfImportedProblem(final String msg, final GamlResource resource) {
		getErrorCollector().add(
			new GamlCompilationError(msg, IGamlIssue.GENERAL, resource.getContents().get(0), false, false));
		updateWith(Collections.EMPTY_SET);
	}

	private ModelDescription buildCompleteDescription(final ResourceSet set) {
		resetErrorCollector();
		// We make sure the resource is loaded in the ResourceSet passed
		// TODO Does it validate it ?
		set.getResource(getURI(), true);
		ModelDescription model = null;

		// If one of the resources has already errors, no need to validate
		// We first build the list of resources (including this);
		Set<GamlResource> imports = loadAllResources();
		for ( GamlResource r : imports ) {
			if ( !r.getErrors().isEmpty() ) {
				invalidateBecauseOfImportedProblem("Syntax errors detected ", r);
				return null;
			}
		}

		model = buildModelDescription(imports);

		// If, for whatever reason, the description is null, we stop the semantic validation
		if ( model == null ) {
			invalidateBecauseOfImportedProblem("Impossible to validate model " + getURI().lastSegment() +
				" (check the logs)", this);
		}
		return model;
	}

	/**
	 * Validates the resource by compiling its contents into a ModelDescription.
	 * @return errors an ErrorCollector which contains semantic errors (as opposed to the ones obtained via
	 *         resource.getErrors(), which are syntactic errors), This collector can be probed for compilation
	 *         errors via its hasErrors(), hasInternalErrors(), hasImportedErrors() methods
	 * 
	 */
	public boolean validate(final ResourceSet set) {
		try {
			setValidating(true);
			// DescriptionFactory.HUGE_CACHE.clear();
			// We first build the model description
			final long begin = System.nanoTime();
			final long mem = Runtime.getRuntime().freeMemory();
			final long mb = 1024;

			ModelDescription model = buildCompleteDescription(set);
			if ( model == null ) { return false; }

			// We then validate it and get rid of the description. The documentation is produced only if the resource is
			// marked as 'edited'
			model.validate(isEdited);
			updateWith(model.getExperimentTitles());
			model.dispose();

			//
			System.out.println("****************************************************");
			System.out.println("Thread [" + Thread.currentThread().getName() + "] | Resource set [" +
				getResourceSet().getResources().size() + " resources]");
			System.out.println("'" + getURI().lastSegment() + "' validated in " + (System.nanoTime() - begin) /
				1000000d + " ms [ ~" + (mem - Runtime.getRuntime().freeMemory()) / mb + " kb used ]");
			System.out.println("****************************************************");

			return !getErrorCollector().hasInternalErrors();
		} catch (final Exception e) {
			e.printStackTrace();
			invalidateBecauseOfImportedProblem("An exception has occured during the validation of " +
				getURI().lastSegment() + ": " + e.getMessage(), this);
			return false;
		} finally {
			setValidating(false);
		}
	}

	IModel build(final ResourceSet set) {
		return build(set, new ArrayList());
	}
	

	ModelDescription buildDescription(final ResourceSet set, final List<GamlCompilationError> errors) {

		// We make sure the resource is loaded in the ResourceSet passed
		set.getResource(getURI(), true);

		System.out.println("Thread [" + Thread.currentThread().getName() + "]");
		System.out.println("Resource " + getURI().lastSegment() + " building");
		System.out.println("****************************************************");

		// Syntactic errors detected, we cannot build the resource
		if ( !getErrors().isEmpty() ) {
			getErrorCollector().add(
				new GamlCompilationError("Syntactic errors detected in " + getURI().lastSegment(), IGamlIssue.GENERAL,
					getContents().get(0), false, false));
			return null;
		}

		// We build the description
		ModelDescription model = buildCompleteDescription(set);
		// If the description has errors, we cannot build the resource
		if ( collector.hasErrors() ) {
			errors.addAll(collector.getInternalErrors());
			errors.addAll(collector.getImportedErrors());
			model.dispose();
			return null;
		}

		errors.addAll(ImmutableList.copyOf(collector));
		return model;
	}

	IModel build(final ResourceSet set, final List<GamlCompilationError> errors) {

		// We make sure the resource is loaded in the ResourceSet passed
		set.getResource(getURI(), true);

		System.out.println("Thread [" + Thread.currentThread().getName() + "]");
		System.out.println("Resource " + getURI().lastSegment() + " building");
		System.out.println("****************************************************");

		// Syntactic errors detected, we cannot build the resource
		if ( !getErrors().isEmpty() ) {
			getErrorCollector().add(
				new GamlCompilationError("Syntactic errors detected in " + getURI().lastSegment(), IGamlIssue.GENERAL,
					getContents().get(0), false, false));
			return null;
		}

		// We build the description
		ModelDescription model = buildCompleteDescription(set);
		// If the description has errors, we cannot build the resource
		if ( collector.hasErrors() ) {
			errors.addAll(collector.getInternalErrors());
			errors.addAll(collector.getImportedErrors());
			model.dispose();
			return null;
		}

		errors.addAll(ImmutableList.copyOf(collector));
		return (IModel) model.compile();
	}

	public IPath getPath() {
		IPath path;
		URI uri = getURI();
		if ( uri.isPlatform() ) {
			path = new Path(getURI().toPlatformString(false));
		} else if ( uri.isFile() ) {
			path = new Path(uri.toFileString());
		} else {
			path = new Path(uri.path());
		}
		path = new Path(URLDecoder.decode(path.toOSString()));
		return path;
	}

	public boolean isValidating() {
		return isValidating;
	}

	private void setValidating(final boolean v) {
		isValidating = v;
	}

	public void setEdited(final boolean b) {
		isEdited = b;
		GamlResourceDocManager.getInstance().document(this, b);
	}

	public boolean isEdited() {
		return isEdited;
	}

}
