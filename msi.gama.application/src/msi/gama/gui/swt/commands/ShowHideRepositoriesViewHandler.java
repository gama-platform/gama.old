/*
 * GAMA - V1.4 http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2012
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2012
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen (Batch, GeoTools & JTS), 2009-2012
 * - Beno”t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.gui.swt.commands;

import msi.gama.common.util.GuiUtils;
import org.eclipse.core.commands.*;
import org.eclipse.team.svn.ui.repository.RepositoriesView;
import org.eclipse.ui.*;

public class ShowHideRepositoriesViewHandler extends AbstractHandler {

	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {

		// final String REPOSITORIES_VIEW_ID = RepositoriesView.VIEW_ID;
		IWorkbenchPage activePage =
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		IWorkbenchPart part = activePage.findView(RepositoriesView.VIEW_ID);

		try {
			if ( !activePage.isPartVisible(part) ) {
				if ( !GuiUtils.isModelingPerspective() ) {
					GuiUtils.openModelingPerspective();
				}
				IViewPart pp = activePage.showView(RepositoriesView.VIEW_ID);

				// ((RepositoriesView) pp).getRepositoryTree().addRefreshListener(
				// new IRefreshListener() {
				//
				// @Override
				// public void refreshed(final Object arg0) {
				// GuiUtils.debug("Refresh : " + arg0);
				// }
				// });

			} else {
				if ( GuiUtils.isModelingPerspective() ) {
					activePage.hideView((IViewPart) part);
				}
			}
		} catch (PartInitException e) {
			e.printStackTrace();
		}
		return null;
	}
}
