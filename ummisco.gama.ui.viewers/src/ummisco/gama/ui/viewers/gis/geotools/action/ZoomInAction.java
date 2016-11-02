/*********************************************************************************************
 *
 * 'ZoomInAction.java, in plugin ummisco.gama.ui.viewers, is part of the source code of the
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
import ummisco.gama.ui.viewers.gis.geotools.tool.ZoomInTool;

/**
 * Action that activates the Zoom in tool for the current {@link SwtMapPane map
 * pane}.
 * 
 * @author Andrea Antonello (www.hydrologis.com)
 *
 *
 *
 * @source $URL$
 */
public class ZoomInAction extends MapAction {

	public ZoomInAction() {
		super(ZoomInTool.TOOL_NAME + "@Z", ZoomInTool.TOOL_TIP, GamaIcons.create(IGamaIcons.IMAGE_ZOOMIN).image());
	}

	@Override
	public void run() {
		getMapPane().setCursorTool(new ZoomInTool());
	}

}
