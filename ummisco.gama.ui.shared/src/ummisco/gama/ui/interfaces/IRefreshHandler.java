package ummisco.gama.ui.interfaces;

import java.util.List;

import org.eclipse.core.resources.IResource;

public interface IRefreshHandler {

	void completeRefresh(List<? extends IResource> resources);

	void refreshResource(final IResource resource);

	void refreshNavigator();

}
