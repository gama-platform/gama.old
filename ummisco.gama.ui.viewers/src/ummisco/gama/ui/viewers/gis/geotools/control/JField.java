/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 * 
 *    (C) 2008, Open Source Geospatial Foundation (OSGeo)
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
package ummisco.gama.ui.viewers.gis.geotools.control;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;
import org.geotools.data.Parameter;
import org.geotools.util.Converters;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * Field that uses the converter API to hack away at a text representation of
 * the provided value.
 *
 *
 *
 *
 * @source $URL$
 */
public class JField extends ParamField {
	private Text text;
	private boolean single = true;

	public JField(final Composite parent, final Parameter<?> parameter) {
		super(parent, parameter);
	}

	public void setSingleLine(final boolean single) {
		this.single = single;
	}

	@Override
	public Control doLayout() {
		if (parameter.metadata != null && parameter.metadata.get(Parameter.IS_PASSWORD) == Boolean.TRUE) {
			text = new Text(parent, SWT.SINGLE | SWT.LEAD | SWT.PASSWORD | SWT.BORDER);
			text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		} else if (single) {
			text = new Text(parent, SWT.SINGLE | SWT.LEAD | SWT.BORDER);
			text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		} else {
			text = new Text(parent, SWT.MULTI | SWT.WRAP | SWT.V_SCROLL | SWT.BORDER);
			text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		}
		text.addModifyListener(arg0 -> validate());
		return text;
	}

	@Override
	public Object getValue() {
		final String txt = text.getText();
		if (txt.length() == 0) {
			return null;
		}
		final Object value = Converters.convert(txt, parameter.type);
		return value;
	}

	/**
	 * Determine the number of dimensions based on the CRS metadata.
	 * 
	 * @return Number of dimensions expected based on metadata, default of 2
	 */
	int getD() {
		final CoordinateReferenceSystem crs = (CoordinateReferenceSystem) parameter.metadata.get(Parameter.CRS);
		if (crs == null) {
			return 2;
		}
		return crs.getCoordinateSystem().getDimension();
	}

	@Override
	public void setValue(final Object value) {
		String txt = Converters.convert(value, String.class);
		if (txt == null) {
			txt = "";
		}
		text.setText(txt);
	}

	@Override
	public boolean validate() {
		final String txt = text.getText();
		if (txt.length() == 0) {
			return true;
		}
		final Object value = Converters.convert(txt, parameter.type);
		if (value == null) {
			text.setToolTipText("Could not create " + parameter.type);
			text.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
			return false;
		}
		text.setToolTipText(null);
		text.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_BLACK));
		return true;
	}

}
