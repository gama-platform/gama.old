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
package msi.gama.gui.application.views;

import java.util.*;
import msi.gama.gui.application.GUI;
import msi.gama.interfaces.IOutput;
import org.eclipse.ui.*;

public class ViewSourceProvider extends AbstractSourceProvider {

	Map result = new HashMap();
	public static String var = "msi.gama.gui.application.view.paused";
	boolean state = false;

	public ViewSourceProvider() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public Map getCurrentState() {
		IWorkbenchPage page = GUI.getPage();
		if ( page == null ) {
			state = false;
		} else {
			IWorkbenchPart part = page.getActivePart();
			if ( part instanceof IGamaView ) {
				IOutput output = ((IGamaView) part).getOutput();
				if ( output == null ) {
					state = false;
				} else {
					state = ((IGamaView) part).getOutput().isPaused();
				}
			} else {
				state = false;
			}
		}
		result.put(var, state);
		return result;
	}

	@Override
	public String[] getProvidedSourceNames() {
		return new String[] { var };
	}

	public void changeState(final boolean state) {
		this.state = state;
		fireSourceChanged(ISources.WORKBENCH, "msi.gama.gui.application.view.paused", state);
	}

	public void changeState() {
		fireSourceChanged(ISources.WORKBENCH, getCurrentState());
	}
}
