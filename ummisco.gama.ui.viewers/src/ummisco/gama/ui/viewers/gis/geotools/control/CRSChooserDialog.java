/**
 * 
 */
package ummisco.gama.ui.viewers.gis.geotools.control;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

public class CRSChooserDialog extends Dialog {

	private final CRSChooser chooser = new CRSChooser();
	private final CoordinateReferenceSystem initialValue;
	private CoordinateReferenceSystem result;

	public CRSChooserDialog(final Shell parentShell, final CoordinateReferenceSystem initialValue) {
		super(parentShell);
		this.initialValue = initialValue;
	}

	@Override
	protected Control createDialogArea(final Composite parent) {
		getShell().setText("Select Coordinate Reference System");
		chooser.setController(new Controller() {

			@Override
			public void handleClose() {
				close();
			}

			@Override
			public void handleOk() {
				result = chooser.getCRS();
			}

		});

		final Control control = chooser.createControl(parent, initialValue);
		chooser.setFocus();
		return control;
	}

	@Override
	public boolean close() {
		result = chooser.getCRS();
		return super.close();
	}

	public CoordinateReferenceSystem getResult() {
		return result;
	}

	public static void main(final String[] args) {
		final CRSChooserDialog dialog =
			new CRSChooserDialog(new Shell(Display.getDefault()), DefaultGeographicCRS.WGS84);
		dialog.setBlockOnOpen(true);
		dialog.open();

		final CoordinateReferenceSystem crs = dialog.getResult();
		System.out.println(crs);

	}
}