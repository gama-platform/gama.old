/*
 * GAMA - V1.4  http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC 
 * 
 * Developers :
 * 
 * - Alexis Drogoul, IRD (Kernel, Metamodel, XML-based GAML), 2007-2011
 * - Vo Duc An, IRD & AUF (SWT integration, multi-level architecture), 2008-2011
 * - Patrick Taillandier, AUF & CNRS (batch framework, GeoTools & JTS integration), 2009-2011
 * - Pierrick Koch, IRD (XText-based GAML environment), 2010-2011
 * - Romain Lavaud, IRD (project-based environment), 2010
 * - Francois Sempe, IRD & AUF (EMF behavioral model, batch framework), 2007-2009
 * - Edouard Amouroux, IRD (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, IRD (OpenMap integration), 2007-2008
 */
package msi.gama.gui.application.commands;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import msi.gama.gui.application.GUI;
import msi.gama.gui.parameters.EditorsList;
import msi.gama.kernel.GAMA;
import msi.gama.kernel.exceptions.GamaRuntimeException;
import org.eclipse.core.commands.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;

public class SaveParametersHandler extends AbstractHandler {

	@Override
	public Object execute(final ExecutionEvent event) {
		EditorsList eds = GAMA.getExperiment().getParametersEditors();
		if ( eds != null ) {
			final Calendar cal = Calendar.getInstance();
			final SimpleDateFormat sdf = new SimpleDateFormat("_yyyy_MM_dd_HH_mm_ss");
			final String dateAndTime = sdf.format(cal.getTime());
			final String fileName =
				"saved_parameters_for_" + GAMA.getExperiment().getName() + dateAndTime + ".xml";
			final FileDialog saveXmlFileDialog = new FileDialog(GUI.getShell(), SWT.SAVE);
			saveXmlFileDialog.setFileName(fileName);
			saveXmlFileDialog.setText("Save a new XML File");
			saveXmlFileDialog.setFilterPath(GAMA.getExperiment().getModel().getBaseDirectory());
			final String xmlFilePath = saveXmlFileDialog.open();
			final File file = new File(xmlFilePath);
			PrintWriter pw;
			try {
				pw = new PrintWriter(file);
			} catch (FileNotFoundException e) {
				GAMA.reportError(new GamaRuntimeException(e));
				return null;
			}
			pw.println("<?xml version=\"1.0\" encoding=\"ISO-8859-1\" standalone=\"yes\"?>");
			pw.println("<" + GAMA.getExperiment().getName() + ">");
			final String simpleName =
				new File(GAMA.getExperiment().getModel().getFileName()).getName();
			pw.println("<include file=\"" + simpleName + "\"/>");
			pw.println("<global>");
			try {
				eds.writeModifiedParameters(pw);
			} catch (GamaRuntimeException e) {
				GAMA.reportError(e);
				return null;
			}
			pw.println("</global>");
			GAMA.getExperiment().getOutputManager().exportOutputsOn(pw);
			pw.println("</" + GAMA.getExperiment().getName() + ">");
			pw.flush();

		}
		return null;
	}

}
