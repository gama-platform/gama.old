/*********************************************************************************************
 * 
 *
 * 'ShowHideRepositoriesViewHandler.java', in plugin 'msi.gama.application', is part of the source code of the 
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.gui.swt.commands;

import msi.gama.common.util.GuiUtils;
import msi.gama.gui.swt.SwtGui;
import org.eclipse.core.commands.*;
import org.eclipse.team.svn.ui.repository.RepositoriesView;
import org.eclipse.ui.*;

public class ShowHideRepositoriesViewHandler extends AbstractHandler {

	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		IWorkbenchPart part = SwtGui.getPage().findView(RepositoriesView.VIEW_ID);

		try {
			if ( !SwtGui.getPage().isPartVisible(part) ) {
				if ( !GuiUtils.isModelingPerspective() ) {
					GuiUtils.openModelingPerspective();
				}
				SwtGui.getPage().showView(RepositoriesView.VIEW_ID);
			} else {
				if ( GuiUtils.isModelingPerspective() ) {
					SwtGui.getPage().hideView((IViewPart) part);
				}
			}
		} catch (PartInitException e) {
			e.printStackTrace();
		}
		return null;
	}
}
