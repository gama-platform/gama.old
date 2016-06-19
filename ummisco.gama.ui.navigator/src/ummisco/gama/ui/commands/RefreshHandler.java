/*********************************************************************************************
 *
 *
 * 'RefreshHandler.java', in plugin 'msi.gama.application', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package ummisco.gama.ui.commands;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IResource;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;

import msi.gama.common.interfaces.IGui;
import ummisco.gama.ui.navigator.GamaNavigator;
import ummisco.gama.ui.utils.WorkbenchHelper;

public class RefreshHandler extends AbstractHandler {

	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		run((IResource) null);
		return null;
	}

	public static void run(final IResource resource) {
		final Display d = PlatformUI.getWorkbench().getDisplay();
		if (d.isDisposed())
			return;
		d.asyncExec(new Runnable() {

			@Override
			public void run() {
				final IWorkbenchPage page = WorkbenchHelper.getPage();
				if (page == null)
					return;
				final IViewPart view = page.findView(IGui.NAVIGATOR_VIEW_ID);
				if (view == null) {
					return;
				}
				((GamaNavigator) view).safeRefresh(resource == null ? null : resource.getParent());
			}
		});
	}

}
