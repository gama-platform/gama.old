/*********************************************************************************************
 *
 * 'InfoAction.java, in plugin ummisco.gama.ui.viewers, is part of the source code of the
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
import ummisco.gama.ui.viewers.gis.geotools.tool.InfoTool;

/**
 * Action that activates the Info tool for the current {@link SwtMapPane map
 * pane}.
 * 
 * @author Andrea Antonello (www.hydrologis.com)
 *
 *
 *
 * @source $URL$
 */
public class InfoAction extends MapAction {

	public InfoAction() {
		super(InfoTool.TOOL_NAME + "@I", InfoTool.TOOL_TIP, GamaIcons.create(IGamaIcons.IMAGE_INFO).image());
	}

	/**
	 * Called when the associated control is activated. Leads to the map pane's
	 * cursor tool being set to a PanTool object
	 * 
	 * @param ev
	 *            the event (not used)
	 */
	@Override
	public void run() {
		getMapPane().setCursorTool(new InfoTool());
	}

}
