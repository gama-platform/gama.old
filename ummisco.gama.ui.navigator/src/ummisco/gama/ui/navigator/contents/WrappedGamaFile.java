package ummisco.gama.ui.navigator.contents;

import static msi.gama.common.GamlFileExtension.isExperiment;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.common.util.URI;

import gnu.trove.map.hash.TObjectIntHashMap;
import msi.gama.common.GamlFileExtension;
import msi.gama.runtime.GAMA;
import msi.gama.util.GAML;
import msi.gama.util.file.GamlFileInfo;
import msi.gama.util.file.IGamaFileMetaData;
import msi.gaml.compilation.ast.ISyntacticElement;
import ummisco.gama.ui.navigator.NavigatorContentProvider;
import ummisco.gama.ui.resources.GamaIcons;

public class WrappedGamaFile extends WrappedFile {

	boolean isExperiment;
	TObjectIntHashMap<String> uriProblems;

	public WrappedGamaFile(final WrappedContainer<?> root, final IFile wrapped) {
		super(root, wrapped);
		computeURIProblems();
	}

	public void computeURIProblems() {
		try {
			uriProblems = null;
			final IMarker[] markers = getResource().findMarkers(IMarker.PROBLEM, true, IResource.DEPTH_ZERO);
			for (final IMarker marker : markers) {
				final String s = marker.getAttribute("URI_KEY", "UNKNOWN");
				final int severity = marker.getAttribute(IMarker.SEVERITY, IMarker.SEVERITY_INFO);
				if (uriProblems == null)
					uriProblems = new TObjectIntHashMap<String>();
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
		final IFile f = getResource();
		if (isExperiment) {
			image = GamaIcons.create("file.experiment2").image();
		} else {
			image = GamaIcons.create("file.icon2").image();
		}

	}

	@Override
	protected void computeFileType() {
		final IFile f = getResource();
		isExperiment = isExperiment(f.getName());
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
				element.visitExperiments(exp -> l.add(new WrappedExperimentContent(this, exp)));
			}
			if (!info.getImports().isEmpty()) {
				final Category wf = new Category(this, info.getImports(), "Imports");
				if (wf.getNavigatorChildren().length > 0)
					l.add(wf);
			}
			if (!info.getUses().isEmpty()) {
				final Category wf = new Category(this, info.getUses(), "Uses");
				if (wf.getNavigatorChildren().length > 0)
					l.add(wf);
			}
			return l.toArray();
		}
		return VirtualContent.EMPTY;
	}

	public int getURIProblem(final URI uri) {

		if (uri == null)
			return -1;
		if (uriProblems == null)
			return -1;
		final String fragment = uri.toString();
		final int[] severity = new int[] { -1 };
		uriProblems.forEachEntry((s, arg1) -> {
			if (s.startsWith(fragment)) {
				severity[0] = arg1;
				return false;
			}
			return true;
		});
		return severity[0];

	}

}
