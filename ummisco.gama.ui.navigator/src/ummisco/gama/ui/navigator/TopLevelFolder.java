/**
 * Created by drogoul, 30 déc. 2015
 *
 */
package ummisco.gama.ui.navigator;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;

import msi.gaml.compilation.GamaBundleLoader;
import ummisco.gama.ui.resources.GamaColors.GamaUIColor;
import ummisco.gama.ui.resources.GamaFonts;
import ummisco.gama.ui.resources.IGamaColors;

/**
 * Class TopLevelFolder.
 *
 * @author drogoul
 * @since 30 déc. 2015
 *
 */
public abstract class TopLevelFolder extends VirtualContent {

	enum Location {
		CoreModels, Plugins, Other, Unknown
	}

	/**
	 * @param root
	 * @param name
	 */
	public TopLevelFolder(final Object root, final String name) {
		super(root, name);
	}

	@Override
	public boolean hasChildren() {
		return getNavigatorChildren().length > 0;
	}

	@Override
	public Font getFont() {
		return GamaFonts.getNavigHeaderFont();
	}

	@Override
	public Object[] getNavigatorChildren() {
		final List<IProject> totalList = Arrays.asList(ResourcesPlugin.getWorkspace().getRoot().getProjects());
		final List<IProject> resultList = new ArrayList<IProject>();
		for (final IProject project : totalList) {
			if (accepts(project)) {
				resultList.add(project);
			}
		}
		return resultList.toArray();
	}

	@Override
	public int findMaxProblemSeverity() {
		int severity = -1;
		for (final IProject p : ResourcesPlugin.getWorkspace().getRoot().getProjects()) {
			if (accepts(p)) {
				try {
					final int s = p.findMaxProblemSeverity(IMarker.PROBLEM, true, IResource.DEPTH_INFINITE);
					if (s > severity) {
						severity = s;
					}
					if (severity == IMarker.SEVERITY_ERROR) {
						break;
					}
				} catch (final CoreException e) {
				}

			}
		}
		return severity;
	}

	/**
	 * @param desc
	 * @return
	 */
	protected final boolean accepts(final IProject project) {
		if (project == null) {
			return false;
		}
		if (!project.exists()) {
			return false;
		}
		// TODO This one is clearly a hack. Should be replaced by a proper way
		// to track persistently the closed projects
		if (!project.isOpen()) {
			return getLocation(project.getLocation()) == getModelsLocation();
		}
		try {
			return accepts(project.getDescription());
		} catch (final CoreException e) {
			return false;
		}
	}

	/**
	 * @return
	 */
	protected abstract Location getModelsLocation();

	protected Location getLocation(final IPath location) {
		URL urlRep = null;
		try {

			final URL old_url = new URL("platform:/plugin/" + GamaBundleLoader.CORE_MODELS + "/");
			final URL new_url = FileLocator.resolve(old_url);
			// windows URL formating
			final String path_s = new_url.getPath().replaceFirst("^/(.:/)", "$1");
			final java.nio.file.Path normalizedPath = Paths.get(path_s).normalize();
			urlRep = normalizedPath.toUri().toURL();
			// urlRep = FileLocator.resolve(new URL("platform:/plugin/" +
			// GamaBundleLoader.CORE_MODELS + "/"));

			// System.out.println("Model path:" + location.toOSString() + " |||
			// Plugin path: " + urlRep.getPath());
			if (location.toOSString().startsWith(urlRep.getPath())) {
				return Location.CoreModels;
			}
			if (location.toOSString().startsWith(urlRep.getPath().replace(GamaBundleLoader.CORE_MODELS + "/", ""))) {
				return Location.Plugins;
			}
			return Location.Other;
		} catch (final IOException e) {
			e.printStackTrace();
			return Location.Unknown;
		} /*
			 * catch (URISyntaxException e) { e.printStackTrace(); return
			 * Location.Unknown; }
			 */

	}

	/**
	 * @param description
	 * @return
	 */
	protected abstract boolean accepts(IProjectDescription description);

	public abstract Image getImageForStatus();

	public abstract String getMessageForStatus();

	public abstract GamaUIColor getColorForStatus();

	@Override
	public Color getColor() {
		return IGamaColors.GRAY_LABEL.color();
	}

	/**
	 * Method canBeDecorated()
	 * 
	 * @see ummisco.gama.ui.navigator.VirtualContent#canBeDecorated()
	 */
	@Override
	public boolean canBeDecorated() {
		return true;
	}

}
