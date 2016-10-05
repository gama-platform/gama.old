package msi.gama.lang.gaml.resource;

import java.util.Map;
import java.util.Set;

import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.resource.SynchronizedXtextResourceSet;
import org.eclipse.xtext.resource.XtextResourceSet;

import com.google.inject.Singleton;

import gnu.trove.set.hash.TLinkedHashSet;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.lang.gaml.EGaml;
import msi.gama.lang.gaml.gaml.Facet;
import msi.gama.lang.gaml.gaml.S_Experiment;
import msi.gama.lang.gaml.gaml.Statement;
import msi.gama.lang.gaml.gaml.StringLiteral;
import msi.gama.lang.gaml.indexer.GamlResourceIndexer;
import msi.gama.util.file.GamlFileInfo;
import msi.gama.util.file.IGamlResourceInfoProvider;
import msi.gaml.compilation.ast.ISyntacticElement;
import msi.gaml.compilation.kernel.GamaBundleLoader;

@Singleton
@SuppressWarnings({ "unchecked", "rawtypes" })
public class GamlResourceInfoProvider implements IGamlResourceInfoProvider {

	public static GamlResourceInfoProvider INSTANCE = new GamlResourceInfoProvider();

	private final XtextResourceSet resourceSet = new SynchronizedXtextResourceSet();

	public GamlFileInfo getInfo(final Resource r, final long stamp) {

		Set<String> imports = null;
		final Set<URI> uris = GamlResourceIndexer.directImportsOf(r.getURI());
		for (final URI u : uris) {
			if (imports == null)
				imports = new TLinkedHashSet();
			imports.add(u.deresolve(r.getURI()).toString());
		}

		Set<String> uses = null;
		Set<String> exps = null;

		final TreeIterator<EObject> tree = EcoreUtil2.getAllContents(r, true);

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
				if (s == null) {
					System.out.println("EXPERIMENT NULL");
				}
				final Map<String, Facet> f = EGaml.getFacetsMapOf((Statement) e);
				final Facet typeFacet = f.get(IKeyword.TYPE);
				if (typeFacet != null) {
					final String type = EGaml.getKeyOf(typeFacet.getExpr());
					if (IKeyword.BATCH.equals(type)) {
						s = GamlFileInfo.BATCH_PREFIX + s;
					}
				}
				if (exps == null)
					exps = new TLinkedHashSet();
				exps.add(s);
			}
		}

		return new GamlFileInfo(stamp, imports, uses, exps);

	}

	@Override
	public GamlFileInfo getInfo(final URI uri, final long stamp) {
		try {

			final GamlResource r = (GamlResource) resourceSet.getResource(uri, true);
			return getInfo(r, stamp);
		} finally {
			clearResourceSet(resourceSet);
		}
	}

	@Override
	public ISyntacticElement getContents(final URI uri) {
		try {
			final GamlResource r = (GamlResource) resourceSet.getResource(uri, true);
			return GamlResourceServices.buildSyntacticContents(r);
		} finally {
			clearResourceSet(resourceSet);
		}
	}

	protected void clearResourceSet(final ResourceSet resourceSet) {
		final boolean wasDeliver = resourceSet.eDeliver();
		try {
			resourceSet.eSetDeliver(false);
			resourceSet.getResources().clear();
		} catch (final Exception e) {
		}

		finally {
			resourceSet.eSetDeliver(wasDeliver);
		}
	}

}
