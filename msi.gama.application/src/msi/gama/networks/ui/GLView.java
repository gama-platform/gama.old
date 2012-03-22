package msi.gama.networks.ui;

import java.awt.Frame;
import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.graphstream.algorithm.generator.WattsStrogatzGenerator;
import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.layout.springbox.SpringBox;
import org.graphstream.ui.swingViewer.DefaultView;
import org.graphstream.ui.swingViewer.GraphRenderer;
import org.graphstream.ui.swingViewer.View;
import org.graphstream.ui.swingViewer.Viewer;
import org.graphstream.ui.swingViewer.util.FpsCounter;
import msi.gama.common.interfaces.IGamaView;
import msi.gama.common.util.GuiUtils;
import msi.gama.gui.views.GamaViewPart;
import msi.gama.outputs.IDisplayOutput;

public class GLView extends GamaViewPart {

	public static final String ID = GuiUtils.GL_VIEW_ID;

	Composite myComposite = null;
	
	public GLView() {
		
	}

	

	@Override
	protected Integer[] getToolbarActionsId() {
		// TODO
		return new Integer[] { 
			//PAUSE, REFRESH, SYNCHRONIZE, SEPARATOR, 
			// TODO SNAPSHOT,
			//SEPARATOR, ZOOM_IN, ZOOM_OUT, ZOOM_FIT 
			};
	}

	@Override
	public void ownCreatePartControl(Composite parent) {
		
		myComposite  = new Composite(parent, SWT.EMBEDDED);
		myComposite.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		myComposite.setLayout(new FillLayout());

		// fill in the blanks ! 
		
	}
	

}
