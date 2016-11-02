/*********************************************************************************************
 *
 * 'PanAction.java, in plugin ummisco.gama.ui.viewers, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/

package ummisco.gama.ui.viewers.gis.geotools.action;

import ummisco.gama.ui.resources.GamaIcons;
import ummisco.gama.ui.resources.IGamaIcons;
import ummisco.gama.ui.viewers.gis.geotools.SwtMapPane;
import ummisco.gama.ui.viewers.gis.geotools.tool.PanTool;

/**
 * Action that activates the Pan tool for the current {@link SwtMapPane map
 * pane}.
 * 
 * @author Andrea Antonello (www.hydrologis.com)
 *
 *
 *
 * @source $URL$
 */
public class PanAction extends MapAction {

	public PanAction() {
		super(PanTool.TOOL_NAME + "@P", PanTool.TOOL_TIP, GamaIcons.create(IGamaIcons.IMAGE_PAN).image());
	}

	@Override
	public void run() {
		getMapPane().setCursorTool(new PanTool());
	}

}
