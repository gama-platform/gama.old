package ummisco.gama.ui.navigator;

import java.util.Iterator;

import org.eclipse.core.resources.IContainer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.navigator.CommonViewer;
import org.eclipse.ui.navigator.INavigatorContentService;

import ummisco.gama.ui.navigator.contents.TopLevelFolder;

public class NavigatorCommonViewer extends CommonViewer {

	public NavigatorCommonViewer(final String aViewerId, final Composite aParent, final int aStyle) {
		super(aViewerId, aParent, aStyle);
	}

	@Override
	public void expandAll() {
		getControl().setRedraw(false);
		NavigatorContentProvider.FILE_CHILDREN_ENABLED = false;
		final IStructuredSelection currentSelection = (IStructuredSelection) getSelection();
		if (currentSelection == null || currentSelection.isEmpty()) {
			super.expandAll(); // .expandToLevel(3);
		} else {
			final Iterator<?> it = currentSelection.iterator();
			while (it.hasNext()) {
				final Object o = it.next();
				if (o instanceof TopLevelFolder) {
					expandToLevel(o, CommonViewer.ALL_LEVELS); // 2
				} else if (o instanceof IContainer) {
					expandToLevel(o, CommonViewer.ALL_LEVELS);
				}
			}

		}
		NavigatorContentProvider.FILE_CHILDREN_ENABLED = true;
		this.refresh(false);
		getControl().setRedraw(true);

	}

	@Override
	public INavigatorContentService getNavigatorContentService() {
		// TODO Auto-generated method stub
		return super.getNavigatorContentService();
	}

}
