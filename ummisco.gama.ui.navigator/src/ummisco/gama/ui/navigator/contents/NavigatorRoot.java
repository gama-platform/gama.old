/*********************************************************************************************
 *
 * 'NavigatorRoot.java, in plugin ummisco.gama.ui.navigator, is part of the source code of the GAMA modeling and
 * simulation platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package ummisco.gama.ui.navigator.contents;

import org.eclipse.core.internal.runtime.AdapterManager;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;

import msi.gama.common.GamlFileExtension;
import ummisco.gama.ui.resources.GamaColors.GamaUIColor;
import ummisco.gama.ui.resources.GamaIcons;
import ummisco.gama.ui.resources.IGamaColors;

@SuppressWarnings ({ "rawtypes", "unchecked" })
public class NavigatorRoot extends VirtualContent implements IAdaptable {

	private TopLevelFolder userFolder;
	private TopLevelFolder testFolder;
	private TopLevelFolder pluginFolder;
	private TopLevelFolder libraryFolder;
	public static NavigatorRoot INSTANCE;
	public ResourceManager mapper;

	public NavigatorRoot() {
		super(null, "Workspace");
		INSTANCE = this;
	}

	public TopLevelFolder getUserFolder() {
		if (userFolder == null)
			userFolder = new UserProjectsFolder(this, "User models");
		return userFolder;
	}

	public TopLevelFolder getTestFolder() {
		if (testFolder == null)
			testFolder = new TestModelsFolder(this, "Test models");
		return testFolder;
	}

	public TopLevelFolder getPluginFolder() {
		if (pluginFolder == null)
			pluginFolder = new PluginsModelsFolder(this, "Plugin models");
		return pluginFolder;
	}

	public TopLevelFolder getLibraryFolder() {
		if (libraryFolder == null)
			libraryFolder = new ModelsLibraryFolder(this, "Library models");
		return libraryFolder;
	}

	public void initializeVirtualFolders(final ResourceManager mapper) {
		this.mapper = mapper;
		setUserFolder(null);
		setPluginFolder(null);
		setTestFolder(null);
		setLibraryFolder(null);
	}

	@Override
	public Object getAdapter(final Class adapter) {
		if (adapter == IResource.class
				|| adapter == IContainer.class) { return ResourcesPlugin.getWorkspace().getRoot(); }
		return AdapterManager.getDefault().getAdapter(this, adapter);
	}

	@Override
	public boolean hasChildren() {
		return true;
	}

	@Override
	public Object[] getNavigatorChildren() {
		return new TopLevelFolder[] { getLibraryFolder(), getPluginFolder(), getTestFolder(), getUserFolder() };
	}

	@Override
	public Image getImage() {
		return null;
	}

	@Override
	public Color getColor() {
		return null;
	}

	@Override
	public void getSuffix(final StringBuilder sb) {}

	@Override
	public int findMaxProblemSeverity() {
		return 0;
	}

	@Override
	public ImageDescriptor getOverlay() {
		return null;
	}

	@Override
	public TopLevelFolder getTopLevelFolder() {
		return getUserFolder();
	}

	@Override
	public VirtualContentType getType() {
		return VirtualContentType.ROOT;
	}

	@Override
	public String getStatusMessage() {
		final IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		try {
			final int projectsCount = root.members().length;
			int modelsCount = 0;
			for (final IResource r : root.members()) {
				final int modelCount[] = new int[1];
				try {
					if (r.isAccessible())
						r.accept(proxy -> {
							if (proxy.getType() == IResource.FILE && GamlFileExtension.isAny(proxy.getName()))
								modelCount[0]++;
							return true;
						}, IResource.NONE);
				} catch (final CoreException e) {
					e.printStackTrace();
				}
				modelsCount += modelCount[0];
			}
			final String loc = root.getLocation().lastSegment();
			return getName() + " " + loc + " (" + projectsCount + " projects, " + modelsCount + " models)";
		} catch (final CoreException e) {}
		return getName();

	}

	@Override
	public String getStatusTooltip() {
		return ResourcesPlugin.getWorkspace().getRoot().getLocation().toOSString();
	}

	@Override
	public GamaUIColor getStatusColor() {
		return IGamaColors.NEUTRAL;
	}

	@Override
	public Image getStatusImage() {
		return GamaIcons.create("menu.about2").image();
	}

	public void setLibraryFolder(final TopLevelFolder libraryFolder) {
		this.libraryFolder = libraryFolder;
	}

	public void setPluginFolder(final TopLevelFolder pluginFolder) {
		this.pluginFolder = pluginFolder;
	}

	public void setUserFolder(final TopLevelFolder userFolder) {
		this.userFolder = userFolder;
	}

	public void setTestFolder(final TopLevelFolder testFolder) {
		this.testFolder = testFolder;
	}

}
