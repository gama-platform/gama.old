/*
 * GAMA - V1.4 http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2012
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2012
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen (Batch, GeoTools & JTS), 2009-2012
 * - Beno�t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.gui.displays.layers;

import msi.gama.common.interfaces.*;
import msi.gama.gui.swt.SwtGui;
import msi.gama.outputs.layers.*;
import msi.gama.runtime.IScope;
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
		Button b = new Button(compo, SWT.PUSH);
		b.setText("Properties");
		b.setLayoutData(new GridData(SWT.END, SWT.FILL, false, false));
		b.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				// FIXME Editor not working for the moment
				SWTChartEditor editor = new SWTChartEditor(SwtGui.getDisplay(), getChart());
				// TODO Revoir cet �diteur, tr�s laid !
				editor.open();
				if ( isPaused(container) ) {
					container.forceUpdateDisplay();
				}
			}

		});
		b = new Button(compo, SWT.PUSH);
		b.setText("Save...");
		b.setLayoutData(new GridData(SWT.END, SWT.FILL, false, false));
		b.addSelectionListener(new SelectionAdapter() {

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
		dg.drawChart(scope, getChart(), 0.0);
	}

	@Override
	public boolean stayProportional() {
		return false;
	}

}
