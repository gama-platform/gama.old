/*********************************************************************************************
 *
 * 'ResetAction.java, in plugin ummisco.gama.ui.viewers, is part of the source code of the
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

/**
 * Action that triggers view reset for the current {@link SwtMapPane map pane}.
 * 
 * @author Andrea Antonello (www.hydrologis.com)
 *
 *
 *
 * @source $URL$
 */
public class ResetAction extends MapAction {

	/** Name for this tool */
	public static final String TOOL_NAME = "Reset";
	/** Tool tip text */
	public static final String TOOL_TIP = "Display full extent of all layers";

	public ResetAction() {
		super(TOOL_NAME + "@A", TOOL_TIP, GamaIcons.create(IGamaIcons.IMAGE_FULLEXTENT).image());
	}

	@Override
	public void run() {
		getMapPane().reset();
	}

}
