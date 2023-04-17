/*******************************************************************************************************
 *
 * GamlResourceInfoProvider.java, in msi.gama.lang.gaml, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.2).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.lang.gaml.resource;

import static java.util.Arrays.asList;
import static org.apache.commons.lang3.StringUtils.deleteWhitespace;
import static org.apache.commons.lang3.StringUtils.split;
import static org.apache.commons.lang3.StringUtils.substringAfter;
import static org.apache.commons.lang3.StringUtils.uncapitalize;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.xtext.resource.SynchronizedXtextResourceSet;
import org.eclipse.xtext.resource.XtextResourceSet;

import com.google.inject.Singleton;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.lang.gaml.EGaml;
import msi.gama.lang.gaml.gaml.HeadlessExperiment;
import msi.gama.lang.gaml.gaml.Pragma;
import msi.gama.lang.gaml.gaml.S_Experiment;
import msi.gama.lang.gaml.gaml.StringLiteral;
import msi.gama.lang.gaml.indexer.GamlResourceIndexer;
import msi.gama.util.file.GamlFileInfo;
import msi.gama.util.file.IGamlResourceInfoProvider;
import msi.gaml.compilation.ast.ISyntacticElement;
import ummisco.gama.dev.utils.DEBUG;

/**
 * The Class GamlResourceInfoProvider.
 */
@Singleton
@SuppressWarnings ({ "unchecked", "rawtypes" })
public class GamlResourceInfoProvider implements IGamlResourceInfoProvider {

	/** The instance. */
	public static GamlResourceInfoProvider INSTANCE = new GamlResourceInfoProvider();

	/** The resource set. */
	private XtextResourceSet resourceSet;

	/**
	 * Gets the info.
	 *
	 * @param originalURI the original URI
	 * @param r the r
	 * @param stamp the stamp
	 * @return the info
	 */
	public GamlFileInfo getInfo(final URI originalURI, final GamlResource r, final long stamp) {

		Set<String> imports = null;
		final Set<URI> uris = GamlResourceIndexer.directImportsOf(originalURI);
		for (final URI u : uris) {
			if (imports == null) { imports = new LinkedHashSet(); }
			imports.add(u.deresolve(originalURI).toString());
		}

		Set<String> tags = null;
		String str = "";
		try (InputStream is = resourceSet.getURIConverter().createInputStream(originalURI);
				BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
			boolean tagsFound = false;
			while (!tagsFound && (str = reader.readLine()) != null) { tagsFound = str.contains("Tags: "); }
			if (tagsFound) {
				tags = new HashSet<>(asList(split(uncapitalize(deleteWhitespace(substringAfter(str, "Tags: "))), ',')));
			}
		} catch (final IOException e1) {
			e1.printStackTrace();
		}

		Set<String> uses = null;
		Set<String> exps = null;

		final TreeIterator<EObject> tree = EcoreUtil.getAllContents(r, true);
		boolean processExperiments = true;
		while (tree.hasNext()) {
			final EObject e = tree.next();
			if (e instanceof Pragma) {
				final String s = ((Pragma) e).getName();
				if (IKeyword.PRAGMA_NO_EXPERIMENT.equals(s)) { processExperiments = false; }
			} else if (e instanceof StringLiteral) {
				final String s = ((StringLiteral) e).getOp();
				if (s.length() > 4) {
					final URI u = URI.createFileURI(s);
					final String ext = u.fileExtension();
					if (ext != null && !ext.isEmpty()) {
						// if (GamaBundleLoader.HANDLED_FILE_EXTENSIONS.contains(ext)) {
						if (uses == null) { uses = new LinkedHashSet(); }
						uses.add(s);
						// }
					}
				}
			} else if (processExperiments && e instanceof S_Experiment) {
				String s = ((S_Experiment) e).getName();
				if (s == null) { DEBUG.ERR("EXPERIMENT NULL"); }
				if (EGaml.getInstance().isBatch(e)) { s = GamlFileInfo.BATCH_PREFIX + s; }

				if (exps == null) { exps = new LinkedHashSet(); }
				exps.add(s);
			} else if (processExperiments && e instanceof HeadlessExperiment) {
				String s = ((HeadlessExperiment) e).getName();

				if (EGaml.getInstance().isBatch(e)) { s = GamlFileInfo.BATCH_PREFIX + s; }

				if (exps == null) { exps = new LinkedHashSet(); }
				exps.add(s);
			}
		}

		return new GamlFileInfo(stamp, imports, uses, exps, tags);

	}

	@Override
	public GamlFileInfo getInfo(final URI uri, final long stamp) {
		try {

			final GamlResource r = (GamlResource) getResourceSet().getResource(uri, true);
			return getInfo(uri, r, stamp);
		} finally {
			clearResourceSet(getResourceSet());
		}
	}

	@Override
	public ISyntacticElement getContents(final URI uri) {
		try {
			final GamlResource r = (GamlResource) getResourceSet().getResource(uri, true);
			return GamlResourceServices.buildSyntacticContents(r);
		} finally {
			clearResourceSet(getResourceSet());
		}
	}

	/**
	 * Clear resource set.
	 *
	 * @param resourceSet the resource set
	 */
	protected void clearResourceSet(final ResourceSet resourceSet) {
		final boolean wasDeliver = resourceSet.eDeliver();
		try {
			resourceSet.eSetDeliver(false);
			resourceSet.getResources().clear();
		} catch (final Exception e) {}

		finally {
			resourceSet.eSetDeliver(wasDeliver);
		}
	}

	/**
	 * Gets the resource set.
	 *
	 * @return the resource set
	 */
	private XtextResourceSet getResourceSet() {
		if (resourceSet == null) { resourceSet = new SynchronizedXtextResourceSet(); }
		return resourceSet;
	}

}
