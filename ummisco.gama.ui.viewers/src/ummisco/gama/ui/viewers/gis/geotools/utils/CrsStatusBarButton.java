/*
 * GeoTools - The Open Source Java GIS Toolkit
 * http://geotools.org
 *
 * (C) 2011, Open Source Geospatial Foundation (OSGeo)
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
package ummisco.gama.ui.viewers.gis.geotools.utils;

import org.eclipse.jface.action.ControlContribution;
import org.eclipse.jface.action.StatusLineLayoutData;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.event.MapBoundsEvent;
import org.geotools.map.event.MapBoundsListener;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import ummisco.gama.ui.viewers.gis.geotools.SwtMapPane;
import ummisco.gama.ui.viewers.gis.geotools.control.CRSChooserDialog;
import ummisco.gama.ui.viewers.gis.geotools.event.MapPaneAdapter;
import ummisco.gama.ui.viewers.gis.geotools.event.MapPaneEvent;

/**
 * The {@link CoordinateReferenceSystem} button to put on the statusbar.
 * 
 * @author Andrea Antonello - www.hydrologis.com
 *
 *
 *
 * @source $URL$
 */
public class CrsStatusBarButton extends ControlContribution implements MapBoundsListener {

	public final static String ID = "eu.hydrologis.toolbar.toponimicombo"; //$NON-NLS-1$
	private final SwtMapPane mapPane;
	private Button crsButton;
	private MapPaneAdapter mapPaneListener;

	public CrsStatusBarButton(final SwtMapPane mapPane) {
		this(ID, mapPane);
	}

	protected CrsStatusBarButton(final String id, final SwtMapPane mapPane) {
		super(id);
		this.mapPane = mapPane;
	}

	@Override
	protected Control createControl(final Composite parent) {
		createListeners();
		mapPane.addMapPaneListener(mapPaneListener);
		mapPane.getMapContent().addMapBoundsListener(this);

		final Composite mainComposite = new Composite(parent, SWT.NONE);
		final GridLayout gridLayout = new GridLayout(1, false);
		mainComposite.setLayout(gridLayout);
		final StatusLineLayoutData statusLineLayoutData = new StatusLineLayoutData();
		// statusLineLayoutData.widthHint = 500;
		mainComposite.setLayoutData(statusLineLayoutData);

		crsButton = new Button(mainComposite, SWT.PUSH);
		final GridData crsButtonGD = new GridData(SWT.BEGINNING, SWT.CENTER, false, false);
		crsButtonGD.widthHint = 300;
		crsButton.setLayoutData(crsButtonGD);
		final CoordinateReferenceSystem crs = getCrs();
		displayCRS(crs);
		crsButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				final CRSChooserDialog dialog = new CRSChooserDialog(crsButton.getShell(), getCrs());
				dialog.setBlockOnOpen(true);
				dialog.open();
				final CoordinateReferenceSystem newCrs = dialog.getResult();
				mapPane.setCrs(newCrs);
				mapPane.redraw();
				displayCRS(newCrs);
			}
		});

		return mainComposite;
	}

	private CoordinateReferenceSystem getCrs() {
		return mapPane.getMapContent().getCoordinateReferenceSystem();
	}

	protected void displayCRS(final CoordinateReferenceSystem crs) {
		if (crs == null) {
			crsButton.setText("No CRS defined");
		} else {
			crsButton.setText(crs.getName().toString());
		}
	}

	/**
	 * Initialize the mouse and map bounds listeners
	 */
	private void createListeners() {
		mapPaneListener = new MapPaneAdapter() {

			@Override
			public void onDisplayAreaChanged(final MapPaneEvent ev) {
				final ReferencedEnvelope env = mapPane.getDisplayArea();
				if (env != null) {
					displayCRS(env.getCoordinateReferenceSystem());
				}
			}

			@Override
			public void onResized(final MapPaneEvent ev) {
				final ReferencedEnvelope env = mapPane.getDisplayArea();
				if (env != null) {
					displayCRS(env.getCoordinateReferenceSystem());
				}
			}

			@Override
			public void onRenderingStarted(final MapPaneEvent ev) {
			}

			@Override
			public void onRenderingStopped(final MapPaneEvent ev) {
			}

			@Override
			public void onRenderingProgress(final MapPaneEvent ev) {
			}

		};
	}

	@Override
	public void mapBoundsChanged(final MapBoundsEvent event) {
		final ReferencedEnvelope env = mapPane.getDisplayArea();
		if (env != null) {
			displayCRS(env.getCoordinateReferenceSystem());
		}
	}

}
