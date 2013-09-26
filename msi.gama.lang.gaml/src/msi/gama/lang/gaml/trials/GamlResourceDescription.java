/**
 * Created by drogoul, 20 avr. 2012
 * 
 */
package msi.gama.lang.gaml.trials;

import java.util.*;
import msi.gama.lang.gaml.gaml.*;
import msi.gama.lang.gaml.resource.GamlResource;
import msi.gama.lang.gaml.scoping.BuiltinGlobalScopeProvider;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.*;
import org.eclipse.xtext.naming.QualifiedName;
import org.eclipse.xtext.resource.IDefaultResourceDescriptionStrategy;
import org.eclipse.xtext.resource.impl.DefaultResourceDescription;
import org.eclipse.xtext.util.IResourceScopeCache;
import com.google.common.base.Predicate;
import com.google.common.collect.*;
import com.google.inject.Inject;

/**
 * The class GamlResourceDescription.
 * 
 * @author drogoul
 * @since 20 avr. 2012
 * 
 */

public class GamlResourceDescription extends DefaultResourceDescription {

	Set<URI> imports = new HashSet();

	/**
	 * Default constructor
	 * 
	 * @param resource
	 * @param strategy
	 * @param cache
	 */
	@Inject
	public GamlResourceDescription(final Resource resource, final IDefaultResourceDescriptionStrategy strategy,
		final IResourceScopeCache cache) {

		super(resource, strategy, cache);
		GamlResource r = (GamlResource) resource;
		imports = listImports(r, r.getResourceSet());
	}

	public Set<URI> getImports() {
		return imports;
	}

	public LinkedHashSet<URI> listImports(final GamlResource resource, final ResourceSet resourceSet) {
		final LinkedHashSet<URI> imports = new LinkedHashSet();
		if ( resource.getContents().isEmpty() ) { return Sets.newLinkedHashSet(); }
		final Model model = (Model) resource.getContents().get(0);
		for ( final Import imp : model.getImports() ) {
			final String importUri = imp.getImportURI();
			if ( importUri == null ) {
				continue;
			}
			final URI iu = URI.createURI(importUri).resolve(resource.getURI());
			try {
				imports.add(iu);
			} catch (Exception e) {
				// GuiUtils.debug("Error in validation: XText cannot load " + iu);
			}
			// if ( !ir.getErrors().isEmpty() ) {
			// resource.error("Imported file " + ir.getURI().lastSegment() + " has errors. Fix them first.",
			// new SyntacticElement(IKeyword.INCLUDE, imp), true);
			// }

		}
		return imports;
	}

	// @Override
	// protected List<IEObjectDescription> computeExportedObjects() {
	// List<IEObjectDescription> result = computeExportedObjects();
	// // GuiUtils.debug("Exported objects from " + this.getURI().lastSegment());
	// // for ( IEObjectDescription o : result ) {
	// // // GuiUtils.debug("     " + o.toString());
	// // }
	// return result;
	// }

	@Override
	public Iterable<QualifiedName> getImportedNames() {
		Iterable<QualifiedName> result = super.getImportedNames();
		return Iterables.filter(result, new Predicate<QualifiedName>() {

			@Override
			public boolean apply(final QualifiedName input) {
				return !BuiltinGlobalScopeProvider.contains(input);
			}
		});
	}

}
