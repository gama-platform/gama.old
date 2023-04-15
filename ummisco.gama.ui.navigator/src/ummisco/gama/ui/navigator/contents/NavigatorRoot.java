/*******************************************************************************************************
 *
 * NavigatorRoot.java, in ummisco.gama.ui.navigator, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.9.2).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gama.ui.navigator.contents;

import org.eclipse.core.internal.runtime.AdapterManager;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;

import msi.gama.common.GamlFileExtension;

/**
 * The Class NavigatorRoot.
 */
@SuppressWarnings ({ "rawtypes", "unchecked" })
public class NavigatorRoot extends VirtualContent implements IAdaptable {

	/** The user folder. */
	private TopLevelFolder userFolder;

	/** The test folder. */
	private TopLevelFolder testFolder;

	/** The plugin folder. */
	private TopLevelFolder pluginFolder;

	/** The library folder. */
	private TopLevelFolder libraryFolder;

	/** The Constant instance. */
	private final static NavigatorRoot instance = new NavigatorRoot();

	/** The mapper. */
	private ResourceManager mapper;

	/**
	 * Instantiates a new navigator root.
	 */
	public NavigatorRoot() {
		super(null, "Workspace");
	}

	/**
	 * Gets the user folder.
	 *
	 * @return the user folder
	 */
	public TopLevelFolder getUserFolder() {
		if (userFolder == null) { userFolder = new UserProjectsFolder(this, "User models"); }
		return userFolder;
	}

	/**
	 * Gets the test folder.
	 *
	 * @return the test folder
	 */
	public TopLevelFolder getTestFolder() {
		if (testFolder == null) { testFolder = new TestModelsFolder(this, "Test models"); }
		return testFolder;
	}

	/**
	 * Gets the plugin folder.
	 *
	 * @return the plugin folder
	 */
	public TopLevelFolder getPluginFolder() {
		if (pluginFolder == null) { pluginFolder = new PluginsModelsFolder(this, "Plugin models"); }
		return pluginFolder;
	}

	/**
	 * Gets the library folder.
	 *
	 * @return the library folder
	 */
	public TopLevelFolder getLibraryFolder() {
		if (libraryFolder == null) { libraryFolder = new ModelsLibraryFolder(this, "Library models"); }
		return libraryFolder;
	}

	/**
	 * Reset virtual folders.
	 *
	 * @param manager
	 *            the manager
	 */
	public void resetVirtualFolders(final ResourceManager manager) {
		if (manager != null) { this.setManager(manager); }
		setUserFolder(null);
		setPluginFolder(null);
		setTestFolder(null);
		setLibraryFolder(null);
	}

	/**
	 * Recreate virtual folders.
	 */
	public void recreateVirtualFolders() {
		getFolders();
	}

	@Override
	public Object getAdapter(final Class adapter) {
		if (adapter == IResource.class || adapter == IContainer.class) return ResourcesPlugin.getWorkspace().getRoot();
		return AdapterManager.getDefault().getAdapter(this, adapter);
	}

	@Override
	public boolean hasChildren() {
		return true;
	}

	@Override
	public Object[] getNavigatorChildren() { return getFolders(); }

	/**
	 * Gets the folders.
	 *
	 * @return the folders
	 */
	public TopLevelFolder[] getFolders() {
		return new TopLevelFolder[] { getLibraryFolder(), getPluginFolder(), getTestFolder(), getUserFolder() };
	}

	@Override
	public ImageDescriptor getImageDescriptor() { return null; }

	@Override
	public void getSuffix(final StringBuilder sb) {}

	@Override
	public int findMaxProblemSeverity() {
		return 0;
	}

	@Override
	public ImageDescriptor getOverlay() { return null; }

	@Override
	public TopLevelFolder getTopLevelFolder() { return getUserFolder(); }

	@Override
	public VirtualContentType getType() { return VirtualContentType.ROOT; }

	@Override
	public String getStatusMessage() {
		final IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		try {
			final int projectsCount = root.members().length;
			int modelsCount = 0;
			for (final IResource r : root.members()) {
				final int modelCount[] = new int[1];
				try {
					if (r.isAccessible()) {
						r.accept(proxy -> {
							if (proxy.getType() == IResource.FILE && GamlFileExtension.isAny(proxy.getName())) {
								modelCount[0]++;
							}
							return true;
						}, IResource.NONE);
					}
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

	/**
	 * Sets the library folder.
	 *
	 * @param libraryFolder
	 *            the new library folder
	 */
	public void setLibraryFolder(final TopLevelFolder libraryFolder) { this.libraryFolder = libraryFolder; }

	/**
	 * Sets the plugin folder.
	 *
	 * @param pluginFolder
	 *            the new plugin folder
	 */
	public void setPluginFolder(final TopLevelFolder pluginFolder) { this.pluginFolder = pluginFolder; }

	/**
	 * Sets the user folder.
	 *
	 * @param userFolder
	 *            the new user folder
	 */
	public void setUserFolder(final TopLevelFolder userFolder) { this.userFolder = userFolder; }

	/**
	 * Sets the test folder.
	 *
	 * @param testFolder
	 *            the new test folder
	 */
	public void setTestFolder(final TopLevelFolder testFolder) { this.testFolder = testFolder; }

	@Override
	public ResourceManager getManager() { return mapper; }

	/**
	 * Sets the manager.
	 *
	 * @param mapper
	 *            the new manager
	 */
	public void setManager(final ResourceManager mapper) { this.mapper = mapper; }

	/**
	 * Gets the folder.
	 *
	 * @param s
	 *            the s
	 * @return the folder
	 */
	public TopLevelFolder getFolder(final String s) {
		for (final TopLevelFolder folder : getFolders()) { if (folder.getName().equals(s)) return folder; }
		return null;
	}

	/**
	 * Gets the single instance of NavigatorRoot.
	 *
	 * @return single instance of NavigatorRoot
	 */
	public static NavigatorRoot getInstance() { return instance; }

}
