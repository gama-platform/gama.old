/**
 * Created by drogoul, 30 déc. 2015
 *
 */
package msi.gama.gui.navigator;

import java.util.*;
import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.graphics.*;
import msi.gama.gui.swt.*;
import msi.gama.gui.swt.GamaColors.GamaUIColor;

/**
 * Class TopLevelFolder.
 *
 * @author drogoul
 * @since 30 déc. 2015
 *
 */
public abstract class TopLevelFolder extends VirtualContent {

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
			if ( isParentOf(project) ) {
				resultList.add(project);
			}
		}
		return resultList.toArray();
	}

	/**
	 * Method isParentOf()
	 * @see msi.gama.gui.navigator.VirtualContent#isParentOf(java.lang.Object)
	 */
	@Override
	public boolean isParentOf(final Object element) {
		if ( !(element instanceof IProject) ) { return false; }
		IProject project = (IProject) element;
		if ( project.isAccessible() ) {
			try {
				IProjectDescription desc = project.getDescription();
				return accepts(desc);
			} catch (CoreException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	/**
	 * @param desc
	 * @return
	 */
	protected abstract boolean accepts(IProjectDescription desc);

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
