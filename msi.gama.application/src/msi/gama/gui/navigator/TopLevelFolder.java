/**
 * Created by drogoul, 30 déc. 2015
 *
 */
package msi.gama.gui.navigator;

import java.io.IOException;
import java.net.*;
import java.nio.file.Paths;
import java.util.*;
import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.*;
import org.eclipse.swt.graphics.*;
import msi.gama.gui.swt.*;
import msi.gama.gui.swt.GamaColors.GamaUIColor;
import msi.gaml.compilation.GamaBundleLoader;

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
		return SwtGui.getNavigHeaderFont();
	}

	@Override
	public Object[] getNavigatorChildren() {
		List<IProject> totalList = Arrays.asList(ResourcesPlugin.getWorkspace().getRoot().getProjects());
		List<IProject> resultList = new ArrayList();
		for ( IProject project : totalList ) {
			if ( accepts(project) ) {
				resultList.add(project);
			}
		}
		return resultList.toArray();
	}

	/**
	 * @param desc
	 * @return
	 */
	protected final boolean accepts(final IProject project) {
		if ( project == null ) { return false; }
		if ( !project.exists() ) { return false; }
		// TODO This one is clearly a hack. Should be replaced by a proper way to track persistently the closed projects
		if ( !project.isOpen() ) { return getLocation(project.getLocation()) == getModelsLocation(); }
		try {
			return accepts(project.getDescription());
		} catch (CoreException e) {
			e.printStackTrace();
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
			URL new_url  = FileLocator.resolve(new URL("platform:/plugin/" + GamaBundleLoader.CORE_MODELS + "/"));
			//windows URL formating
			String path_s = new_url.getPath().replaceFirst("^/(.:/)", "$1");
			java.nio.file.Path normalizedPath = Paths.get(path_s).normalize();
			urlRep = normalizedPath.toUri().toURL(); 
			//urlRep = FileLocator.resolve(new URL("platform:/plugin/" + GamaBundleLoader.CORE_MODELS + "/"));
			
			// System.out.println("Model path:" + location.toOSString() + " ||| Plugin path: " + urlRep.getPath());
			if ( location.toOSString().startsWith(urlRep.getPath()) ) { return Location.CoreModels; }
			if ( location.toOSString().startsWith(
				urlRep.getPath().replace(GamaBundleLoader.CORE_MODELS + "/", "")) ) { return Location.Plugins; }
			return Location.Other;
		} catch (IOException e) {
			e.printStackTrace();
			return Location.Unknown;
		} /*catch (URISyntaxException e) {
			e.printStackTrace();
			return Location.Unknown;
		}*/

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
	 * @see msi.gama.gui.navigator.VirtualContent#canBeDecorated()
	 */
	@Override
	public boolean canBeDecorated() {
		return true;
	}

}
