/*********************************************************************************************
 * 
 *
 * 'ChartLayer.java', in plugin 'msi.gama.application', is part of the source code of the 
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.gui.displays.layers;

import java.awt.image.BufferedImage;
import msi.gama.common.interfaces.*;
import msi.gama.gui.swt.SwtGui;
import msi.gama.gui.swt.controls.SWTChartEditor;
import msi.gama.outputs.layers.*;
import msi.gama.runtime.IScope;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.*;
import org.jfree.chart.JFreeChart;

/**
 * Written by drogoul Modified on 1 avr. 2010
 * 
 * @todo Description
 * 
 */
public class ChartLayer extends AbstractLayer {

	public ChartLayer(final ILayerStatement model) {
		super(model);
	}

	private JFreeChart getChart() {
		return ((ChartLayerStatement) definition).getChart();
	}

	@Override
	public void fillComposite(final Composite compo, final IDisplaySurface container) {
		super.fillComposite(compo, container);
		final Button b = new Button(compo, SWT.PUSH);
		b.setText("Properties");
		b.setLayoutData(new GridData(SWT.END, SWT.FILL, false, false));
		b.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				// FIXME Editor not working for the moment
				Point p = b.toDisplay(b.getLocation());
				p.y = p.y + 30;
				SWTChartEditor editor = new SWTChartEditor(SwtGui.getDisplay(), getChart(), p);
				// TODO Revoir cet �diteur, tr�s laid !
				editor.open();
				if ( isPaused(container) ) {
					container.forceUpdateDisplay();
				}
			}

		});
		final Button save = new Button(compo, SWT.PUSH);
		save.setText("Save...");
		save.setLayoutData(new GridData(SWT.END, SWT.FILL, false, false));
		save.setToolTipText("Save the chart data as a CSV file");
		save.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				((ChartLayerStatement) definition).saveHistory();
			}

		});

	}

	@Override
	public String getType() {
		return "Chart layer";
	}

	@Override
	public void privateDrawDisplay(final IScope scope, final IGraphics dg) {
		BufferedImage im = getChart().createBufferedImage(getSizeInPixels().x, getSizeInPixels().y);
		dg.drawChart(scope, im, 0.0);
	}

	@Override
	public boolean stayProportional() {
		return false;
	}

}
