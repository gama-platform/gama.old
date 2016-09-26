/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 *
 *    (C) 2011, Open Source Geospatial Foundation (OSGeo)
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */

package ummisco.gama.ui.viewers.gis.geotools.action;

import java.io.File;
import java.io.IOException;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.map.FeatureLayer;
import org.geotools.map.MapContent;
import org.geotools.styling.Style;

import ummisco.gama.ui.resources.GamaIcons;
import ummisco.gama.ui.resources.IGamaIcons;
import ummisco.gama.ui.viewers.gis.geotools.control.JFileDataStoreChooser;
import ummisco.gama.ui.viewers.gis.geotools.utils.Utils;

/**
 * Action to open shapefile.
 * 
 * @author Andrea Antonello (www.hydrologis.com)
 *
 *
 *
 * @source $URL$
 */
public class OpenShapefileAction extends MapAction implements ISelectionChangedListener {

	public OpenShapefileAction() {
		super("Open Shapefile", "Load a shapefile into the viewer.", GamaIcons.create(IGamaIcons.OPEN).image());
	}

	@Override
	public void run() {
		final Display display = Display.getCurrent();
		final Shell shell = new Shell(display);
		final File openFile = JFileDataStoreChooser.showOpenFile(new String[] { "*.shp" }, shell); //$NON-NLS-1$

		try {
			if (openFile != null && openFile.exists()) {
				final MapContent mapContent = mapPane.getMapContent();
				final FileDataStore store = FileDataStoreFinder.getDataStore(openFile);
				final SimpleFeatureSource featureSource = store.getFeatureSource();
				final Style style = Utils.createStyle(openFile, featureSource);
				final FeatureLayer featureLayer = new FeatureLayer(featureSource, style);
				mapContent.addLayer(featureLayer);
				mapPane.redraw();
			}
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void selectionChanged(final SelectionChangedEvent arg0) {

	}

}
