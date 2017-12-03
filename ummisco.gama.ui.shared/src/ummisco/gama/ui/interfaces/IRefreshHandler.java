package ummisco.gama.ui.interfaces;

import java.util.List;

import org.eclipse.core.resources.IResource;

public interface IRefreshHandler {

	void run(IResource resource);

	void completeRefresh(List<? extends IResource> resources);
}
