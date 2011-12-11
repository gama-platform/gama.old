/*
 * GAMA - V1.4  http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC 
 * 
 * Developers :
 * 
 * - Alexis Drogoul, IRD (Kernel, Metamodel, XML-based GAML), 2007-2011
 * - Vo Duc An, IRD & AUF (SWT integration, multi-level architecture), 2008-2011
 * - Patrick Taillandier, AUF & CNRS (batch framework, GeoTools & JTS integration), 2009-2011
 * - Pierrick Koch, IRD (XText-based GAML environment), 2010-2011
 * - Romain Lavaud, IRD (project-based environment), 2010
 * - Francois Sempe, IRD & AUF (EMF behavioral model, batch framework), 2007-2009
 * - Edouard Amouroux, IRD (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, IRD (OpenMap integration), 2007-2008
 */
package msi.gama.gui.application.perspectives;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.eclipse.ui.IPlaceholderFolderLayout;

public class BatchPerspective implements IPerspectiveFactory {

	/** The Constant ID of the perspective */
	public static final String ID = "msi.gama.gui.application.perspectives.BatchPerspective";
	
	@Override
	public void createInitialLayout(IPageLayout layout) {
		layout.setEditorAreaVisible(false);
		String editorId = layout.getEditorArea();
		layout.addView("msi.gama.gui.view.GamaNavigator", IPageLayout.LEFT, 0.25f, editorId);
		
		IPlaceholderFolderLayout layersFolder = layout.createPlaceholderFolder("layersFolder", IPageLayout.TOP, 0.62f, editorId);
		
		layersFolder.addPlaceholder("msi.gama.gui.application.view.LayeredDisplayView");
		layersFolder.addPlaceholder("msi.gama.gui.application.view.LayeredDisplayView:*");

		layout.addPlaceholder("msi.gama.gui.application.view.BatchSummaryView", IPageLayout.RIGHT,
				0.65f, "layersFolder");
		
		layout.addView("msi.gama.gui.application.view.ConsoleView", IPageLayout.BOTTOM, 0.70f, "msi.gama.gui.view.GamaNavigator");
		
	}

}
