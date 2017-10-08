/*********************************************************************************************
 *
 * 'TopLevelFolder.java, in plugin ummisco.gama.ui.navigator, is part of the source code of the GAMA modeling and
 * simulation platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
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

import msi.gaml.compilation.kernel.GamaBundleLoader;
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
		CoreModels, Plugins, Other, Unknown, Tests
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
				} catch (final CoreException e) {}

			}
		}
		return severity;
	}

	/**
	 * @param desc
	 * @return
	 */
	protected final boolean accepts(final IProject project) {
		if (project == null) { return false; }
		if (!project.exists()) { return false; }
		// TODO This one is clearly a hack. Should be replaced by a proper way
		// to track persistently the closed projects
		if (!project.isOpen()) { return getLocation(project.getLocation()) == getModelsLocation(); }
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
			final String osString = location.toOSString();
			final boolean isTest = osString.contains(GamaBundleLoader.CORE_TESTS);
			if (!isTest && osString.startsWith(urlRep.getPath())) { return Location.CoreModels; }
			if (osString.startsWith(urlRep.getPath().replace(GamaBundleLoader.CORE_MODELS + "/", ""))) {
				if (isTest)
					return Location.Tests;
				return Location.Plugins;
			}
			return Location.Other;
		} catch (final IOException e) {
			e.printStackTrace();
			return Location.Unknown;
		}

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

	public String getSuffix() {
		int modelCount = 0;
		for (final Object o : getNavigatorChildren()) {
			modelCount += NavigatorBaseLighweightDecorator.countModels((IProject) o);
		}

		if (modelCount > 0) { return String.valueOf(modelCount); }
		return " ";
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
