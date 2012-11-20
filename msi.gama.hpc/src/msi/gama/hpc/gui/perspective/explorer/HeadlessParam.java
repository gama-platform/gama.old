package msi.gama.hpc.gui.perspective.explorer;

import msi.gama.runtime.exceptions.GamaRuntimeException;
import org.eclipse.jface.preference.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.part.ViewPart;

import msi.gama.hpc.gui.common.GUIUtils;
import msi.gama.hpc.gui.perspective.chart.HeadlessChart;
import msi.gama.hpc.simulation.*;

public class HeadlessParam extends ViewPart {

	public static final String ID = GUIUtils.HEADLESSPARAM_ID;

	public HeadlessParam() {
		super();
	}





	public void createPartControl(final Composite parent) {

		// GridLayout parentLayout = new GridLayout(3, true);
		// parentLayout.marginWidth = 0;
		// parentLayout.marginHeight = 0;
		// parentLayout.verticalSpacing = 0;
		// parent.setLayout(parentLayout);

		// RowLayout rowLayout = new RowLayout();
		// rowLayout.wrap = true;
		// rowLayout.pack = true;
		// rowLayout.justify = true;
		// rowLayout.type = SWT.VERTICAL;
		// rowLayout.marginLeft = 5;
		// rowLayout.marginTop = 5;
		// rowLayout.marginRight = 5;
		// rowLayout.marginBottom = 5;
		// rowLayout.spacing = 0;
		//
		// parent.setLayout(rowLayout);


		final DirectoryFieldEditor de_bin =
			new DirectoryFieldEditor("the GAMA binary folder", "Select GAMA binary folder", parent);

		final DirectoryFieldEditor de_inp =
			new DirectoryFieldEditor("the Input folder", "Select input folder", parent);
		final DirectoryFieldEditor de_out =
			new DirectoryFieldEditor("the Output folder", "Select output folder", parent);

		final IntegerFieldEditor ie =
			new IntegerFieldEditor("Cores number", "Enter number of cores", parent);
		// parent.pack();
		
		Button b = new Button(parent, SWT.PUSH);
		b.setText("Run simulation");

		final FileFieldEditor fe = new FileFieldEditor("the XML file for charts display", "Select XML file: ", parent);
		String[] extensions = new String[] { "*.xml" }; // NON-NLS-1
		fe.setFileExtensions(extensions);


		b.addMouseListener(new MouseListener() {

			@Override
			public void mouseUp(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseDown(MouseEvent e) throws GamaRuntimeException {
				// TODO Auto-generated method stub

				//BatchExecutor.submitExperiment(de_bin.getStringValue(), de_inp.getStringValue(),
				//	de_out.getStringValue(), ie.getIntValue());
				
				
				// BatchExecutor.submitExperiment("C://Users//Administrator//Desktop//GAMA//eclipse",
				// "C://Users//Administrator//Desktop//GAMA//eclipse//samples",
				// "C://Users//Administrator//Desktop//GAMA//eclipse//samples", 10);
			}

			@Override
			public void mouseDoubleClick(MouseEvent e) {
				// TODO Auto-generated method stub

			}
		});

		Button b1 = new Button(parent, SWT.PUSH);
		b1.setText("Draw charts");

		b1.addMouseListener(new MouseListener() {

			@Override
			public void mouseUp(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseDown(MouseEvent e) throws GamaRuntimeException {
				HeadlessChart.xmlfilename=fe.getStringValue();
			//	GuiUtils.showHeadlessChartView();

			}

			@Override
			public void mouseDoubleClick(MouseEvent e) {
				// TODO Auto-generated method stub

			}
		});

	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}

}