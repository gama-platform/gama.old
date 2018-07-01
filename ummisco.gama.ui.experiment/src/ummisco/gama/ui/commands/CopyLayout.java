package ummisco.gama.ui.commands;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.util.tree.GamaTree;
import ummisco.gama.ui.utils.WorkbenchHelper;

public class CopyLayout extends AbstractHandler {

	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		final GamaTree<String> tree =
				new LayoutTreeConverter().convertCurrentLayout(ArrangeDisplayViews.listDisplayViews());
		if (tree == null) { return this; }
		WorkbenchHelper.copy(IKeyword.PERMANENT + " layout: " + tree.getRoot().getChildren().get(0).toString() + ";");
		return this;
	}

}
