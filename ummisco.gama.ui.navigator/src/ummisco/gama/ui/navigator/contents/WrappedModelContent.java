package ummisco.gama.ui.navigator.contents;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;

import gnu.trove.map.hash.TObjectIntHashMap;
import msi.gaml.compilation.ast.ISyntacticElement;

public class WrappedModelContent extends WrappedSyntacticContent {

	TObjectIntHashMap<String> uriProblems;

	public WrappedModelContent(final WrappedFile file, final ISyntacticElement e) {
		super(file, e, "Contents");
		try {
			final IMarker[] markers = file.getResource().findMarkers(IMarker.PROBLEM, true, IResource.DEPTH_ZERO);
			for (final IMarker marker : markers) {
				final String s = marker.getAttribute("URI_KEY", null);
				final int severity = marker.getAttribute(IMarker.SEVERITY, IMarker.SEVERITY_INFO);
				if (uriProblems == null)
					uriProblems = new TObjectIntHashMap<String>();
				uriProblems.put(s, severity);
			}
		} catch (final CoreException ce) {}
	}

	@Override
	public int getURIProblem(final String fragment) {
		// final IFile file = (IFile) getParent();
		if (uriProblems == null)
			return -1;
		final int[] severity = new int[] { -1 };
		uriProblems.forEachEntry((s, arg1) -> {
			if (s != null && s.startsWith(fragment)) {
				severity[0] = arg1;
				return false;
			}
			return true;
		});
		return severity[0];

	}

	@Override
	public boolean hasChildren() {
		return true;
	}

}