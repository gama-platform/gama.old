/*******************************************************************************************************
 *
 * ummisco.gama.ui.commands.CopyLayout.java, in plugin ummisco.gama.ui.experiment, is part of the source code of the
 * GAMA modeling and simulation platform (v. 1.8)
 *
 * (c) 2007-2018 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gama.ui.commands;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import msi.gama.application.workbench.PerspectiveHelper;
import msi.gama.util.tree.GamaNode;
import msi.gama.util.tree.GamaTree;
import ummisco.gama.ui.utils.WorkbenchHelper;

public class CopyLayout extends AbstractHandler {

	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		final GamaTree<String> tree =
				new LayoutTreeConverter().convertCurrentLayout(ArrangeDisplayViews.listDisplayViews());
		if (tree == null) { return this; }
		final GamaNode<String> firstSash = tree.getRoot().getChildren().get(0);
		firstSash.setWeight(null);
		final StringBuilder sb = new StringBuilder();
		sb.append(" layout " + firstSash);
		if (PerspectiveHelper.keepTabs() != null) {
			sb.append(" tabs:").append(PerspectiveHelper.keepTabs());
		}
		if (PerspectiveHelper.keepToolbars() != null) {
			sb.append(" toolbars:").append(PerspectiveHelper.keepTabs());
		}
		sb.append(" editors: ").append(WorkbenchHelper.getPage().isEditorAreaVisible()).append(";");
		WorkbenchHelper.copy(sb.toString());
		tree.dispose();
		return this;
	}

}
