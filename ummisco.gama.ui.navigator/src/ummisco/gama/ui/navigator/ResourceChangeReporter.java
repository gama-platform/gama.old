package ummisco.gama.ui.navigator;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.runtime.CoreException;

public class ResourceChangeReporter implements IResourceChangeListener {

	class DeltaPrinter implements IResourceDeltaVisitor {
		@Override
		public boolean visit(final IResourceDelta delta) {
			final IResource res = delta.getResource();
			switch (delta.getKind()) {
				case IResourceDelta.ADDED:
					System.out.print("Resource ");
					System.out.print(res.getFullPath());
					System.out.println(" was added.");
					break;
				case IResourceDelta.REMOVED:
					System.out.print("Resource ");
					System.out.print(res.getFullPath());
					System.out.println(" was removed.");
					break;
				case IResourceDelta.CHANGED:
					System.out.print("Resource ");
					System.out.print(delta.getFullPath());
					System.out.println(" has changed.");
					final int flags = delta.getFlags();
					if ((flags & IResourceDelta.CONTENT) != 0) {
						System.out.println("--> Content Change");
					}
					if ((flags & IResourceDelta.REPLACED) != 0) {
						System.out.println("--> Content Replaced");
					}
					if ((flags & IResourceDelta.MARKERS) != 0) {
						System.out.println("--> Marker Change");
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
		final IResource res = event.getResource();
		switch (event.getType()) {
			case IResourceChangeEvent.PRE_CLOSE:
				System.out.print("Project ");
				System.out.print(res.getFullPath());
				System.out.println(" is about to close.");
				break;
			case IResourceChangeEvent.PRE_DELETE:
				System.out.print("Project ");
				System.out.print(res.getFullPath());
				System.out.println(" is about to be deleted.");
				break;
			case IResourceChangeEvent.POST_CHANGE:
				System.out.println("Resources have changed.");
				try {
					event.getDelta().accept(new DeltaPrinter());
				} catch (final CoreException e) {}
				break;
			case IResourceChangeEvent.PRE_BUILD:
				try {
					System.out.println("Build about to run.");
					event.getDelta().accept(new DeltaPrinter());
				} catch (final CoreException e) {}
				break;
			case IResourceChangeEvent.POST_BUILD:
				try {
					System.out.println("Build complete.");
					event.getDelta().accept(new DeltaPrinter());
				} catch (final CoreException e) {}
				break;
		}
	}
}