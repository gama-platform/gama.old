/*
 * GeoTools - The Open Source Java GIS Toolkit
 * http://geotools.org
 *
 * (C) 2002-2008, Open Source Geospatial Foundation (OSGeo)
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation;
 * version 2.1 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 */

package ummisco.gama.ui.viewers.gis.geotools.action;

import ummisco.gama.ui.viewers.gis.geotools.SwtMapPane;
import ummisco.gama.ui.viewers.gis.geotools.utils.ImageCache;

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
		super(TOOL_NAME + "@A", TOOL_TIP, ImageCache.getInstance().getImage(ImageCache.IMAGE_FULLEXTENT));
	}

	@Override
	public void run() {
		getMapPane().reset();
	}

}
