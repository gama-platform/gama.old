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

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.geotools.coverage.grid.io.AbstractGridCoverage2DReader;
import org.geotools.coverage.grid.io.AbstractGridFormat;
import org.geotools.coverage.grid.io.GridFormatFinder;
import org.geotools.map.GridReaderLayer;
import org.geotools.map.Layer;
import org.geotools.map.MapContent;
import org.geotools.styling.RasterSymbolizer;
import org.geotools.styling.SLD;
import org.geotools.styling.Style;
import org.geotools.styling.StyleFactoryImpl;

import ummisco.gama.ui.resources.GamaIcons;
import ummisco.gama.ui.resources.IGamaIcons;
import ummisco.gama.ui.viewers.gis.geotools.control.JFileImageChooser;

/**
 * Action to open geotiff files.
 * 
 * @author Andrea Antonello (www.hydrologis.com)
 *
 *
 *
 * @source $URL$
 */
public class OpenGeotiffAction extends MapAction implements ISelectionChangedListener {

	public OpenGeotiffAction() {
		super("Open Image", "Load an image file into the viewer.", GamaIcons.create(IGamaIcons.OPEN).image());
	}

	@Override
	public void run() {
		final Display display = Display.getCurrent();
		final Shell shell = new Shell(display);
		final File openFile = JFileImageChooser.showOpenFile(shell);

		if (openFile != null && openFile.exists()) {
			final AbstractGridFormat format = GridFormatFinder.findFormat(openFile);
			final AbstractGridCoverage2DReader tiffReader = format.getReader(openFile);
			final StyleFactoryImpl sf = new StyleFactoryImpl();
			final RasterSymbolizer symbolizer = sf.getDefaultRasterSymbolizer();
			final Style defaultStyle = SLD.wrapSymbolizers(symbolizer);

			final MapContent mapContent = mapPane.getMapContent();
			final Layer layer = new GridReaderLayer(tiffReader, defaultStyle);
			layer.setTitle(openFile.getName());
			mapContent.addLayer(layer);
			mapPane.redraw();
		}
	}

	@Override
	public void selectionChanged(final SelectionChangedEvent arg0) {

	}

}
