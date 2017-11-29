package ummisco.gama.ui.navigator.contents;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.resource.ImageDescriptor;

public abstract class WrappedResource<T extends IResource> extends VirtualContent implements IAdaptable {
	final static int NOT_COMPUTED = Integer.MAX_VALUE;
	final T resource;
	int severity = NOT_COMPUTED;

	public WrappedResource(final Object root, final T wrapped) {
		super(root, wrapped.getName());
		resource = wrapped;
		findMaxProblemSeverity();
	}

	@SuppressWarnings ({ "unchecked" })
	@Override
	public <C> C getAdapter(final Class<C> adapter) {
		if (adapter.isInstance(resource))
			return (C) resource;
		return null;
	}

	public T getResource() {
		return resource;
	}

	public abstract boolean canBeDecorated();

	public boolean isOpen() {
		return resource.isAccessible();
	}

	@Override
	public int findMaxProblemSeverity() {
		if (severity == NOT_COMPUTED) {
			if (isOpen())
				try {
					severity = resource.findMaxProblemSeverity(IMarker.PROBLEM, true, IResource.DEPTH_INFINITE);
				} catch (final CoreException e) {}
			else
				severity = CLOSED;
		}
		return severity;
	}

	@Override
	public ImageDescriptor getOverlay() {
		return canBeDecorated() ? DESCRIPTORS.get(findMaxProblemSeverity()) : null;
	}

	public void invalidateSeverity() {
		severity = NOT_COMPUTED;
		final Object p = getParent();
		if (p instanceof WrappedContainer) {
			((WrappedContainer<?>) p).invalidateSeverity();
		}
	}

	public abstract int countModels();

}
