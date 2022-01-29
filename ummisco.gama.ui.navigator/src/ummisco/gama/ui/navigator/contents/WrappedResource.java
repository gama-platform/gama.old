/*******************************************************************************************************
 *
 * WrappedResource.java, in ummisco.gama.ui.navigator, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package ummisco.gama.ui.navigator.contents;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.resource.ImageDescriptor;

/**
 * The Class WrappedResource.
 *
 * @param <P> the generic type
 * @param <T> the generic type
 */
public abstract class WrappedResource<P extends VirtualContent<?>, T extends IResource> extends VirtualContent<P>
		implements IAdaptable {
	
	/** The Constant NOT_COMPUTED. */
	final static int NOT_COMPUTED = Integer.MAX_VALUE;
	
	/** The resource. */
	final T resource;
	
	/** The severity. */
	int severity = NOT_COMPUTED;

	/**
	 * Instantiates a new wrapped resource.
	 *
	 * @param root the root
	 * @param wrapped the wrapped
	 */
	public WrappedResource(final P root, final T wrapped) {
		super(root, wrapped.getName());
		resource = wrapped;
		findMaxProblemSeverity();
	}

	@SuppressWarnings ({ "unchecked" })
	@Override
	public <C> C getAdapter(final Class<C> adapter) {
		if (adapter.isInstance(resource)) { return (C) resource; }
		return null;
	}

	/**
	 * Gets the resource.
	 *
	 * @return the resource
	 */
	public T getResource() {
		return resource;
	}

	/**
	 * Can be decorated.
	 *
	 * @return true, if successful
	 */
	public abstract boolean canBeDecorated();

	/**
	 * Checks if is open.
	 *
	 * @return true, if is open
	 */
	public boolean isOpen() {
		return resource.isAccessible();
	}

	@Override
	public int findMaxProblemSeverity() {
		if (severity == NOT_COMPUTED) {
			if (isOpen()) {
				try {
					severity = resource.findMaxProblemSeverity(IMarker.PROBLEM, true, IResource.DEPTH_INFINITE);
				} catch (final CoreException e) {}
			} else {
				severity = CLOSED;
			}
		}
		return severity;
	}

	@Override
	public ImageDescriptor getOverlay() {
		return canBeDecorated() ? DESCRIPTORS.get(findMaxProblemSeverity()) : null;
	}

	/**
	 * Invalidate severity.
	 */
	public void invalidateSeverity() {
		severity = NOT_COMPUTED;
		final Object p = getParent();
		if (p instanceof WrappedContainer) {
			((WrappedContainer<?>) p).invalidateSeverity();
		}
	}

	/**
	 * Count models.
	 *
	 * @return the int
	 */
	public abstract int countModels();

}
