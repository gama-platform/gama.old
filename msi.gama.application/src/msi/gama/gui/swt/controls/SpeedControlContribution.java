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
 * - Beno”t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.gui.swt.controls;

import msi.gama.gui.swt.SwtGui;
import msi.gama.kernel.simulation.SimulationClock;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.menus.WorkbenchWindowControlContribution;

public class SpeedControlContribution extends WorkbenchWindowControlContribution {

	public SpeedControlContribution() {}

	public SpeedControlContribution(final String id) {
		super(id);
	}

	//
	@Override
	protected int computeWidth(final Control control) {
		return control.computeSize(50, SWT.DEFAULT, true).x;
	}

	@Override
	protected Control createControl(final Composite parent) {
		final Composite c = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(1, false);
		layout.horizontalSpacing = 4;
		layout.verticalSpacing = 0;
		layout.marginHeight = 0;
		layout.marginWidth = 4;
		c.setLayout(layout);
		GridData data = new GridData(SWT.FILL, SWT.CENTER, true, true);
		data.widthHint = 50;
		final CoolSlider l =
			new CoolSlider(c, SWT.HORIZONTAL | CoolSlider.SMOOTH_STYLE, SwtGui.line, SwtGui.line,
				SwtGui.thumb, SwtGui.thumb, SwtGui.thumb_pushed, SwtGui.line, SwtGui.line);
		l.setLayoutData(data);
		l.setSize(50, 20);
		l.updateSlider(SimulationClock.getDelay());
		l.addPositionChangeListener(new CoolSliderPositionChangeListener() {

			@Override
			public void positionChanged(final double position) {
				SimulationClock.setDelay(position);
			}

		});
		return c;
	}
}
