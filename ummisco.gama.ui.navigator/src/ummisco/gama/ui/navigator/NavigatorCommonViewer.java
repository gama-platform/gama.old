/*******************************************************************************************************
 *
 * NavigatorCommonViewer.java, in ummisco.gama.ui.navigator, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package ummisco.gama.ui.navigator;

import java.util.Iterator;

import org.eclipse.core.resources.IContainer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.navigator.CommonViewer;

import ummisco.gama.ui.navigator.contents.TopLevelFolder;

/**
 * The Class NavigatorCommonViewer.
 */
public class NavigatorCommonViewer extends CommonViewer {

	/**
	 * Instantiates a new navigator common viewer.
	 *
	 * @param aViewerId the a viewer id
	 * @param aParent the a parent
	 * @param aStyle the a style
	 */
	public NavigatorCommonViewer(final String aViewerId, final Composite aParent, final int aStyle) {
		super(aViewerId, aParent, aStyle);
	}

	@Override
	public void expandAll() {
		getControl().setRedraw(false);
		NavigatorContentProvider.FILE_CHILDREN_ENABLED = false;
		final IStructuredSelection currentSelection = (IStructuredSelection) getSelection();
		if (currentSelection == null || currentSelection.isEmpty()) {
			super.expandAll();
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

}
