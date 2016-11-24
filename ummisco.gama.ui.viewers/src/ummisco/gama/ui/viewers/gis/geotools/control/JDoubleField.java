/*********************************************************************************************
 *
 * 'JDoubleField.java, in plugin ummisco.gama.ui.viewers, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
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

/**
 * Widget for double values
 * 
 * @author Andrea Antonello (www.hydrologis.com)
 * @author gdavis
 *
 *
 *
 *
 * @source $URL$
 */
public class JDoubleField extends ParamField {

	private Text text;

	public JDoubleField(final Composite parent, final Parameter<?> parameter) {
		super(parent, parameter);
	}

	@Override
	public Control doLayout() {
		text = new Text(parent, SWT.SINGLE | SWT.LEAD | SWT.BORDER);
		text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		return text;
	}

	@Override
	public Object getValue() {
		final String val = text.getText();
		if (val == null || val.equals("")) {
			return new Double(0);
		}
		try {
			return new Double(val);
		} catch (final NumberFormatException e) {
			return new Double(0);
		}
	}

	@Override
	public void setValue(final Object value) {
		text.setText(((Double) value).toString());
	}

	@Override
	public boolean validate() {
		final String val = text.getText();
		try {
			final Double d = Double.parseDouble(val);
			return true;
		} catch (final NumberFormatException e) {
			return false;
		}
	}

}
