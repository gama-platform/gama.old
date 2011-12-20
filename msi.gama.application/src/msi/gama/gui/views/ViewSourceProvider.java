/*
 * GAMA - V1.4  http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2012
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2012
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen  (Batch, GeoTools & JTS), 2009-2012
 * - Beno”t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.gui.views;

import java.util.*;
import msi.gama.common.interfaces.IGamaView;
import msi.gama.gui.swt.SwtGui;
import msi.gama.outputs.IOutput;
import org.eclipse.ui.*;

public class ViewSourceProvider extends AbstractSourceProvider {

	Map result = new HashMap();
	public static String var = "msi.gama.application.view.paused";
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
		IWorkbenchPage page = SwtGui.getPage();
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
		fireSourceChanged(ISources.WORKBENCH, "msi.gama.application.view.paused", state);
	}

	public void changeState() {
		fireSourceChanged(ISources.WORKBENCH, getCurrentState());
	}
}
