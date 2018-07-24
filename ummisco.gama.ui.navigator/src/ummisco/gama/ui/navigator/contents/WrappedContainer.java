package ummisco.gama.ui.navigator.contents;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;

import one.util.streamex.StreamEx;

public abstract class WrappedContainer<C extends IContainer> extends WrappedResource<VirtualContent<?>, C> {

	int modelsCount = NOT_COMPUTED;
	String suffix = null;
	Object[] children;

	public WrappedContainer(final VirtualContent<?> root, final C wrapped) {
		super(root, wrapped);
		initializeChildren();
	}

	public void initializeChildren() {
		if (!isOpen()) {
			children = EMPTY;
			return;
		}
		try {
			final IResource[] cc = getResource().members();
			final int size = cc.length;
			if (size == 0)
				children = EMPTY;
			else {
				children = StreamEx.of(cc).filter(r -> r.getName().charAt(0) != '.').map(r -> getManager().wrap(this, r))
						.toArray(WrappedResource.class);
			}
		} catch (final CoreException e) {
			e.printStackTrace();
		}
		countModels();
	}

	@Override
	public boolean hasChildren() {
		return isOpen() && children.length > 0;
	}

	@Override
	public Object[] getNavigatorChildren() {
		return children;
	}

	@Override
	public int countModels() {
		if (modelsCount == NOT_COMPUTED) {
			modelsCount = 0;
			suffix = null;
			if (isOpen()) {
				for (final Object c : children) {
					modelsCount += ((WrappedResource<?, ?>) c).countModels();
				}
				if (modelsCount > 0) {
					suffix = "" + modelsCount + " model" + (modelsCount > 1 ? "s" : "");
				}
			}
		}
		return modelsCount;
	}

	public void invalidateModelsCount() {
		modelsCount = NOT_COMPUTED;
		final Object p = getParent();
		if (p instanceof WrappedContainer) {
			((WrappedContainer<?>) p).invalidateModelsCount();
		}
	}

	@Override
	public void getSuffix(final StringBuilder sb) {
		countModels();
		if (suffix != null)
			sb.append(suffix);
	}

}
