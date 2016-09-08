package msi.gama.lang.gaml.resource;

import java.util.Map;
import java.util.Set;

import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.resource.XtextResourceSet;

import com.google.inject.Inject;

import gnu.trove.set.hash.TLinkedHashSet;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.lang.gaml.gaml.Facet;
import msi.gama.lang.gaml.gaml.S_Experiment;
import msi.gama.lang.gaml.gaml.Statement;
import msi.gama.lang.gaml.gaml.StringLiteral;
import msi.gama.lang.gaml.indexer.IModelIndexer;
import msi.gama.lang.utils.EGaml;
import msi.gama.util.file.GAMLFile;
import msi.gaml.compilation.GamaBundleLoader;

public class GamlResourceInfoProvider {

	@Inject IModelIndexer indexer;

	@Inject XtextResourceSet resourceSet;

	@Inject
	public GamlResourceInfoProvider() {
	}

	public GAMLFile.GamlInfo getInfo(final Resource r, final long stamp) {

		Set<String> imports = null;
		final Set<URI> uris = indexer.directImportsOf(r.getURI());
		// System.out.println("Direct imports of " + r.getURI() + " : " + uris);
		for (final URI u : uris) {
			if (imports == null)
				imports = new TLinkedHashSet();
			imports.add(u.deresolve(r.getURI()).toString());
		}

		Set<String> uses = null;
		Set<String> exps = null;

		final TreeIterator<EObject> tree = EcoreUtil2.getAllContents(r, false);

		while (tree.hasNext()) {
			final EObject e = tree.next();
			if (e instanceof StringLiteral) {
				final String s = ((StringLiteral) e).getOp();
				if (s.length() > 4) {
					final URI u = URI.createFileURI(s);
					final String ext = u.fileExtension();
					if (GamaBundleLoader.HANDLED_FILE_EXTENSIONS.contains(ext)) {
						if (uses == null)
							uses = new TLinkedHashSet();
						uses.add(s);
					}
				}
			} else if (e instanceof S_Experiment) {
				String s = ((S_Experiment) e).getName();
				final Map<String, Facet> f = EGaml.getFacetsMapOf((Statement) e);
				final Facet typeFacet = f.get(IKeyword.TYPE);
				if (typeFacet != null) {
					final String type = EGaml.getKeyOf(typeFacet.getExpr());
					if (IKeyword.BATCH.equals(type)) {
						s = GAMLFile.GamlInfo.BATCH_PREFIX + s;
					}
				}
				if (exps == null)
					exps = new TLinkedHashSet();
				exps.add(s);
			}
		}

		return new GAMLFile.GamlInfo(stamp, imports, uses, exps);

	}

	public GAMLFile.GamlInfo getInfo(final URI uri, final long stamp) {
		try {

			final GamlResource r = (GamlResource) resourceSet.getResource(uri, true);
			return getInfo(r, stamp);
		} finally {
			clearResourceSet(resourceSet);
		}
	}

	protected void clearResourceSet(final ResourceSet resourceSet) {
		final boolean wasDeliver = resourceSet.eDeliver();
		try {
			resourceSet.eSetDeliver(false);
			resourceSet.getResources().clear();
		} finally {
			resourceSet.eSetDeliver(wasDeliver);
		}
	}

}
