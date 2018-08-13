package ummisco.gama.ui.navigator;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.runtime.CoreException;

import ummisco.gama.dev.utils.DEBUG;

public class ResourceChangeReporter implements IResourceChangeListener {

	class DeltaPrinter implements IResourceDeltaVisitor {
		@Override
		public boolean visit(final IResourceDelta delta) {
			if (!DEBUG.IS_ON()) { return false; }
			final IResource res = delta.getResource();
			switch (delta.getKind()) {
				case IResourceDelta.ADDED:
					DEBUG.OUT("Resource " + res.getFullPath() + " was added.");
					break;
				case IResourceDelta.REMOVED:
					DEBUG.OUT("Resource " + res.getFullPath() + " was removed.");
					break;
				case IResourceDelta.CHANGED:
					DEBUG.OUT("Resource " + delta.getFullPath() + " has changed.");
					final int flags = delta.getFlags();
					if ((flags & IResourceDelta.CONTENT) != 0) {
						DEBUG.OUT("--> Content Change");
					}
					if ((flags & IResourceDelta.REPLACED) != 0) {
						DEBUG.OUT("--> Content Replaced");
					}
					if ((flags & IResourceDelta.MARKERS) != 0) {
						DEBUG.OUT("--> Marker Change");
						// final IMarkerDelta[] markers = delta.getMarkerDeltas();
						// if interested in markers, check these deltas
					}
					break;
			}
			return true; // visit the children
		}
	}

	@Override
	public void resourceChanged(final IResourceChangeEvent event) {
		if (!DEBUG.IS_ON()) { return; }
		final IResource res = event.getResource();
		switch (event.getType()) {
			case IResourceChangeEvent.PRE_CLOSE:
				DEBUG.OUT("Project " + res.getFullPath() + " is about to close.");
				break;
			case IResourceChangeEvent.PRE_DELETE:
				DEBUG.OUT("Project " + res.getFullPath() + " is about to be deleted.");
				break;
			case IResourceChangeEvent.POST_CHANGE:
				DEBUG.OUT("Resources have changed.");
				try {
					event.getDelta().accept(new DeltaPrinter());
				} catch (final CoreException e) {}
				break;
			case IResourceChangeEvent.PRE_BUILD:
				try {
					DEBUG.OUT("Build about to run.");
					event.getDelta().accept(new DeltaPrinter());
				} catch (final CoreException e) {}
				break;
			case IResourceChangeEvent.POST_BUILD:
				try {
					DEBUG.OUT("Build complete.");
					event.getDelta().accept(new DeltaPrinter());
				} catch (final CoreException e) {}
				break;
		}
	}
}