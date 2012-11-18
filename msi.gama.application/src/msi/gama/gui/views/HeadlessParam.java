package msi.gama.gui.views;

import msi.gama.common.interfaces.EditorListener;
import msi.gama.common.util.GuiUtils;
import msi.gama.gui.parameters.EditorFactory;
import msi.gama.gui.parameters.FileEditor;
import msi.gama.headless.executor.BatchExecutor;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.expressions.IExpression;
import msi.gaml.types.IType;
import msi.gaml.types.Types;
import org.eclipse.core.internal.resources.refresh.win32.Convert;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.part.ViewPart;

public class HeadlessParam extends ViewPart {

	public static final String ID = GuiUtils.HEADLESSPARAM_ID;


	public HeadlessParam() {
		super();
	}


	public void createPartControl(Composite parent) {
		 GridLayout parentLayout = new GridLayout(3, false);
		 parentLayout.marginWidth = 0;
		 parentLayout.marginHeight = 0;
		 parentLayout.verticalSpacing = 0;
		 parent.setLayout(parentLayout);

//		RowLayout rowLayout = new RowLayout();
//		rowLayout.wrap = true;
//		rowLayout.pack = true;
//		rowLayout.justify = true;
//		rowLayout.type = SWT.VERTICAL;
//		rowLayout.marginLeft = 5;
//		rowLayout.marginTop = 5;
//		rowLayout.marginRight = 5;
//		rowLayout.marginBottom = 5;
//		rowLayout.spacing = 0;
//
//		parent.setLayout(rowLayout);

		// FileFieldEditor fe = new FileFieldEditor("fileSelect", "Select File: ", parent);
		// String[] extensions = new String[] { "*.*" }; // NON-NLS-1
		// fe.setFileExtensions(extensions);
		final DirectoryFieldEditor de_bin =
			new DirectoryFieldEditor("the GAMA binary folder", "Select GAMA binary folder", parent);

		final DirectoryFieldEditor de_inp =
			new DirectoryFieldEditor("the Input folder", "Select input folder", parent);
		final DirectoryFieldEditor de_out =
			new DirectoryFieldEditor("the Output folder", "Select output folder", parent);
		Label lbl = new Label(parent, 0);
		lbl.setText("Cores numbers");
		final IntegerFieldEditor ie=new IntegerFieldEditor("Cores number", "Enter number of cores", parent);

		Button b = new Button(parent, SWT.PUSH);
		b.setText("Run simulation");
		b.addMouseListener(new MouseListener() {

			@Override
			public void mouseUp(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseDown(MouseEvent e) throws GamaRuntimeException{
				// TODO Auto-generated method stub
				
				BatchExecutor.submitExperiment(de_bin.getStringValue(), de_inp.getStringValue(),
					de_out.getStringValue(), ie.getIntValue());
				// BatchExecutor.submitExperiment("C://Users//Administrator//Desktop//GAMA//eclipse",
				// "C://Users//Administrator//Desktop//GAMA//eclipse//samples",
				// "C://Users//Administrator//Desktop//GAMA//eclipse//samples", 10);
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