/*********************************************************************************************
 *
 * 'JGeometryField.java, in plugin ummisco.gama.ui.viewers, is part of the source code of the GAMA modeling and
 * simulation platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package ummisco.gama.ui.viewers.gis.geotools.control;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.geotools.data.Parameter;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.WKTReader;
import com.vividsolutions.jts.io.WKTWriter;

import ummisco.gama.ui.utils.WorkbenchHelper;

/**
 * Text field for filling in a Geometry parameter using WKT.
 *
 *
 *
 * @source $URL$
 */
public class JGeometryField extends ParamField {
	private Text text;

	public JGeometryField(final Composite parent, final Parameter<?> parameter) {
		super(parent, parameter);
	}

	@Override
	public Control doLayout() {
		text = new Text(parent, SWT.MULTI | SWT.WRAP | SWT.V_SCROLL | SWT.BORDER);
		text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		text.addModifyListener(arg0 -> validate());
		return text;
	}

	@Override
	public Object getValue() {
		final WKTReader reader = new WKTReader();
		final String wkt = text.getText();
		if (wkt.length() == 0) { return null; }

		try {
			return reader.read(wkt);
		} catch (final Throwable eek) {
			return null;
		}
	}

	/**
	 * Determine the number of dimensions based on the CRS metadata.
	 * 
	 * @return Number of dimensions expected based on metadata, default of 2
	 */
	int getD() {
		try {
			final CoordinateReferenceSystem crs = (CoordinateReferenceSystem) parameter.metadata.get(Parameter.CRS);
			if (crs == null) {
				return 2;
			} else {
				return crs.getCoordinateSystem().getDimension();
			}
		} catch (final Throwable t) {
			return 2;
		}
	}

	@Override
	public void setValue(final Object value) {
		final Geometry geom = (Geometry) value;

		final WKTWriter writer = new WKTWriter(getD());
		final String wkt = writer.write(geom);

		text.setText(wkt);
	}

	@Override
	public boolean validate() {
		final WKTReader reader = new WKTReader();
		final String wkt = text.getText();
		if (wkt.length() == 0) { return true; }

		try {
			final Geometry geom = reader.read(wkt);
			if (parameter.type.isInstance(geom)) {
				text.setToolTipText(null);
				text.setForeground(WorkbenchHelper.getDisplay().getSystemColor(SWT.COLOR_BLACK));
				return true;
			} else {
				text.setToolTipText("Could not use " + geom.getClass() + " as " + parameter.type);
				text.setForeground(WorkbenchHelper.getDisplay().getSystemColor(SWT.COLOR_RED));
				return false;
			}
		} catch (final Throwable eek) {
			text.setToolTipText(eek.getLocalizedMessage());
			text.setForeground(WorkbenchHelper.getDisplay().getSystemColor(SWT.COLOR_RED));
			return false;
		}
	}

}
