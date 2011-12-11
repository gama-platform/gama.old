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
package msi.gama.gui.displays;

import msi.gama.gui.application.GUI;
import msi.gama.gui.graphics.DisplayManager.DisplayItem;
import msi.gama.gui.graphics.*;
import msi.gama.kernel.exceptions.GamaRuntimeException;
import msi.gama.outputs.layers.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.*;
import org.jfree.chart.JFreeChart;
import org.jfree.experimental.chart.swt.editor.SWTChartEditor;

/**
 * Written by drogoul Modified on 1 avr. 2010
 * 
 * @todo Description
 * 
 */
public class ChartDisplay extends AbstractDisplay {

	public ChartDisplay(final double env_width, final double env_height,
		final AbstractDisplayLayer model, final IGraphics dg) {
		super(env_width, env_height, model, dg);
	}

	private JFreeChart getChart() {
		return ((ChartDisplayLayer) model).getChart();
	}

	@Override
	public void fillComposite(final Composite compo, final DisplayItem item,
		final IDisplaySurface container) throws GamaRuntimeException {
		super.fillComposite(compo, item, container);
		Button b = new Button(compo, SWT.PUSH);
		b.setText("Properties");
		b.setLayoutData(new GridData(SWT.END, SWT.FILL, false, false));
		b.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				SWTChartEditor editor = new SWTChartEditor(GUI.getDisplay(), getChart());
				// TODO Revoir cet éditeur, très laid !
				editor.open();
				container.updateDisplay();
			}

		});
		b = new Button(compo, SWT.PUSH);
		b.setText("Save...");
		b.setLayoutData(new GridData(SWT.END, SWT.FILL, false, false));
		b.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				((ChartDisplayLayer) model).saveHistory();
			}

		});

	}

	@Override
	protected String getType() {
		return "Chart layer";
	}

	@Override
	public void privateDrawDisplay(final IGraphics dg) {
		if ( !disposed ) {
			dg.drawChart(getChart());
		}
	}

}
