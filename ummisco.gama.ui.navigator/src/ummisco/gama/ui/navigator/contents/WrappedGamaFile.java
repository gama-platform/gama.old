/*******************************************************************************************************
 *
 * WrappedGamaFile.java, in ummisco.gama.ui.navigator, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package ummisco.gama.ui.navigator.contents;

import static msi.gama.common.GamlFileExtension.isExperiment;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.common.util.URI;

import msi.gama.common.GamlFileExtension;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.runtime.GAMA;
import msi.gama.util.GamaMapFactory;
import msi.gama.util.IMap;
import msi.gama.util.file.GamlFileInfo;
import msi.gama.util.file.IGamaFileMetaData;
import msi.gaml.compilation.GAML;
import msi.gaml.compilation.ast.ISyntacticElement;
import msi.gaml.descriptions.IExpressionDescription;
import ummisco.gama.ui.navigator.NavigatorContentProvider;
import ummisco.gama.ui.resources.GamaIcons;

/**
 * The Class WrappedGamaFile.
 */
public class WrappedGamaFile extends WrappedFile {

	/** The is experiment. */
	boolean isExperiment;
	
	/** The uri problems. */
	IMap<String, Integer> uriProblems;

	/**
	 * Instantiates a new wrapped gama file.
	 *
	 * @param root the root
	 * @param wrapped the wrapped
	 */
	public WrappedGamaFile(final WrappedContainer<?> root, final IFile wrapped) {
		super(root, wrapped);
		computeURIProblems();
	}

	/**
	 * Compute URI problems.
	 */
	public void computeURIProblems() {
		try {
			uriProblems = null;
			final IMarker[] markers = getResource().findMarkers(IMarker.PROBLEM, true, IResource.DEPTH_ZERO);
			for (final IMarker marker : markers) {
				final String s = marker.getAttribute("URI_KEY", "UNKNOWN");
				final int severity = marker.getAttribute(IMarker.SEVERITY, IMarker.SEVERITY_INFO);
				if (uriProblems == null) {
					uriProblems = GamaMapFactory.createUnordered();
				}
				uriProblems.put(s, severity);
			}
		} catch (final CoreException ce) {}

	}

	@Override
	public boolean canBeDecorated() {
		return true;
	}

	@Override
	public boolean hasChildren() {
		return true;
	}

	@Override
	public Object[] getNavigatorChildren() {
		if (NavigatorContentProvider.FILE_CHILDREN_ENABLED) { return getFileChildren(); }
		return EMPTY;
	}

	@Override
	public boolean isGamaFile() {
		return true;
	}

	@Override
	public int countModels() {
		return 1;
	}

	@Override
	protected void computeFileImage() {
		// final IFile f = getResource();
		if (isExperiment) {
			image = GamaIcons.create("navigator/file.experiment2").image();
		} else {
			image = GamaIcons.create("navigator/file.icon2").image();
		}

	}

	@Override
	protected void computeFileType() {
		final IFile f = getResource();
		isExperiment = isExperiment(f.getName());
	}

	/**
	 * Checks for tag.
	 *
	 * @param tag the tag
	 * @return true, if successful
	 */
	public boolean hasTag(final String tag) {
		final IGamaFileMetaData metaData = GAMA.getGui().getMetaDataProvider().getMetaData(getResource(), false, false);
		// DEBUG.LOG("Tags of " + getName() + ": " + ((GamlFileInfo) metaData).getTags());
		if (metaData instanceof GamlFileInfo) {
			for (final String t : ((GamlFileInfo) metaData).getTags()) {
				if (t.contains(tag)) { return true; }
			}
		}
		return false;
	}

	@Override
	public Object[] getFileChildren() {
		final IGamaFileMetaData metaData = GAMA.getGui().getMetaDataProvider().getMetaData(getResource(), false, false);
		if (metaData instanceof GamlFileInfo) {
			final GamlFileInfo info = (GamlFileInfo) metaData;
			final List<VirtualContent<?>> l = new ArrayList<>();
			final String path = getResource().getFullPath().toOSString();
			final ISyntacticElement element = GAML.getContents(URI.createPlatformResourceURI(path, true));
			if (element != null) {
				if (!GamlFileExtension.isExperiment(path)) {
					l.add(new WrappedModelContent(this, element));
				}
				element.visitExperiments(exp -> {
					final IExpressionDescription d = exp.getExpressionAt(IKeyword.VIRTUAL);
					if (d == null || !d.equalsString("true")) {
						l.add(new WrappedExperimentContent(this, exp));
					}
				});
			}
			if (!info.getImports().isEmpty()) {
				final Category wf = new Category(this, info.getImports(), "Imports");
				if (wf.getNavigatorChildren().length > 0) {
					l.add(wf);
				}
			}
			if (!info.getUses().isEmpty()) {
				final Category wf = new Category(this, info.getUses(), "Uses");
				if (wf.getNavigatorChildren().length > 0) {
					l.add(wf);
				}
			}
			if (!info.getTags().isEmpty()) {
				final Tags wf = new Tags(this, info.getTags(), "Tags");
				if (wf.getNavigatorChildren().length > 0) {
					l.add(wf);
				}
			}
			return l.toArray();
		}
		return VirtualContent.EMPTY;
	}

	/**
	 * Gets the URI problem.
	 *
	 * @param uri the uri
	 * @return the URI problem
	 */
	public int getURIProblem(final URI uri) {

		if (uri == null) { return -1; }
		if (uriProblems == null) { return -1; }
		final String fragment = uri.toString();
		final int[] severity = new int[] { -1 };
		uriProblems.forEachPair((s, arg1) -> {
			if (s.startsWith(fragment)) {
				severity[0] = arg1;
				return false;
			}
			return true;
		});
		return severity[0];

	}

}
