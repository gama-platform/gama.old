/**
 * Created by drogoul, 19 janv. 2012
 * 
 */
package msi.gama.gui.views.actions;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import msi.gama.gui.swt.SwtGui;
import msi.gama.gui.views.GamaViewPart;
import msi.gama.kernel.experiment.EditorsList;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import org.eclipse.jface.action.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;

/**
 * The class SnapshotItem.
 * 
 * @author drogoul
 * @since 19 janv. 2012
 * 
 */
public class SaveItem extends GamaViewItem {

	/**
	 * @param view
	 */
	SaveItem(final GamaViewPart view) {
		super(view);
	}

	/**
	 * @see msi.gama.gui.views.actions.GamaViewItem#createItem()
	 */
	@Override
	protected IContributionItem createItem() {
		IAction action =
			new GamaAction("Save parameters values", IAction.AS_PUSH_BUTTON,
				getImageDescriptor("icons/button_save.png")) {

				@Override
				public void run() {
					EditorsList eds = (EditorsList) GAMA.getExperiment().getParametersEditors();
					if ( eds != null ) {
						final Calendar cal = Calendar.getInstance();
						final SimpleDateFormat sdf = new SimpleDateFormat("_yyyy_MM_dd_HH_mm_ss");
						final String dateAndTime = sdf.format(cal.getTime());
						final String fileName =
							"saved_parameters_for_" + GAMA.getExperiment().getName() + dateAndTime +
								".xml";
						final FileDialog saveXmlFileDialog =
							new FileDialog(SwtGui.getShell(), SWT.SAVE);
						saveXmlFileDialog.setFileName(fileName);
						saveXmlFileDialog.setText("Save a new XML File");
						saveXmlFileDialog.setFilterPath(GAMA.getExperiment().getModel()
							.getFolderPath());
						final String xmlFilePath = saveXmlFileDialog.open();
						final File file = new File(xmlFilePath);
						PrintWriter pw;
						try {
							pw = new PrintWriter(file);
						} catch (FileNotFoundException e) {
							GAMA.reportError(GamaRuntimeException.create(e));
							return;
						}
						pw.println("<?xml version=\"1.0\" encoding=\"ISO-8859-1\" standalone=\"yes\"?>");
						pw.println("<" + GAMA.getExperiment().getName() + ">");
						final String simpleName =
							new File(GAMA.getExperiment().getModel().getFilePath()).getName();
						pw.println("<include file=\"" + simpleName + "\"/>");
						pw.println("<global>");
						try {
							eds.writeModifiedParameters(pw);
						} catch (GamaRuntimeException e) {
							GAMA.reportError(e);
							return;
						}
						pw.println("</global>");
						GAMA.getExperiment().getOutputManager().exportOutputsOn(pw);
						pw.println("</" + GAMA.getExperiment().getName() + ">");
						pw.flush();

					}
				}
			};
		return new ActionContributionItem(action);
	}
}
