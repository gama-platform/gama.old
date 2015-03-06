/*********************************************************************************************
 * 
 * 
 * 'StatusControlContribution.java', in plugin 'msi.gama.application', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.gui.swt.controls;

import msi.gama.common.*;
import msi.gama.common.interfaces.*;
import msi.gama.common.util.GuiUtils;
import msi.gama.gui.swt.GamaColors.GamaUIColor;
import msi.gama.gui.swt.*;
import msi.gama.kernel.simulation.*;
import msi.gama.runtime.GAMA;
import msi.gaml.operators.Strings;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.menus.WorkbenchWindowControlContribution;
import org.jfree.util.StringUtils;

public class StatusControlContribution extends WorkbenchWindowControlContribution implements IPopupProvider, IUpdaterTarget<IStatusMessage> {

	// private Composite compo;
	// private Composite parent;
	FlatButton label;
	private Popup popup;
	int state;
	volatile String mainTaskName;
	volatile String subTaskName;
	volatile boolean inSubTask = false;
	volatile Double subTaskCompletion;
	private final static int WIDTH = 300;

	public StatusControlContribution() {}

	public StatusControlContribution(final String id) {
		super(id);
	}

	@Override
	protected int computeWidth(final Control control) {
		return WIDTH;
		// return label.computeMinWidth();
		// return compo.getBounds().y;
		// return control.computeSize(SWT.DEFAULT, SWT.DEFAULT, true).x;
	}

	@Override
	protected Control createControl(final Composite parent) {
		parent.setBackground(IGamaColors.VERY_LIGHT_GRAY.color());
		// this.parent = parent;
		Composite compo = new Composite(parent, SWT.DOUBLE_BUFFERED);
		compo.setBackground(IGamaColors.VERY_LIGHT_GRAY.color());
		GridLayout layout = new GridLayout(1, false);
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		compo.setLayout(layout);
		GridData data = new GridData(SWT.FILL, SWT.CENTER, true, true);
		data.widthHint = WIDTH;
		data.heightHint = 24;
		label = FlatButton.label(compo, IGamaColors.NEUTRAL, "No simulation running");
		label.setLayoutData(data);

		label.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseDown(final MouseEvent e) {
				GAMA.getClock().toggleDisplay();
			}
		});
		popup = new Popup(this, label);
		SwtGui.setStatusControl(this);
		return compo;
	}

	@Override
	public boolean isDisposed() {
		return label.isDisposed();
	}

	/**
	 * @see msi.gama.gui.swt.controls.IPopupProvider#getPopupText()
	 */
	@Override
	public String getPopupText() {
		if ( !GuiUtils.isSimulationPerspective() ) { return null; }
		if ( state == IGui.ERROR || state == IGui.WAIT ) { return label.getText(); }
		StringBuilder sb = new StringBuilder(300);
		String nl = StringUtils.getLineSeparator();
		SimulationAgent simulation = GAMA.getSimulation();
		if ( simulation == null ) { return "No simulation running"; }
		SimulationClock clock = simulation.getClock();
		sb.append("Cycles elapsed: ").append(Strings.TAB).append(clock.getCycle()).append(" | ");
		sb.append("Simulated time: ").append(Strings.TAB).append(Strings.asDate(clock.getTime(), null)).append(nl);
		sb.append("Durations | cycle: ").append(Strings.TAB).append(Strings.TAB).append(clock.getDuration())
			.append("ms").append(" | ");
		sb.append("average: ").append(Strings.TAB).append((int) clock.getAverageDuration()).append("ms").append(" | ");
		sb.append("total: ").append(Strings.TAB).append(Strings.TAB).append(clock.getTotalDuration()).append("ms");
		return sb.toString();
	}

	/**
	 * @see msi.gama.gui.swt.controls.IPopupProvider#getPopupBackground()
	 */
	@Override
	public GamaUIColor getPopupBackground() {
		return state == IGui.ERROR ? IGamaColors.ERROR : state == IGui.WAIT ? IGamaColors.WARNING
			: state == IGui.NEUTRAL ? IGamaColors.NEUTRAL : IGamaColors.OK;
	}

	@Override
	public Shell getControllingShell() {
		return label.getShell();
	}

	@Override
	public Point getAbsoluteOrigin() {
		return label.toDisplay(new Point(label.getLocation().x, label.getSize().y));
	}

	Runnable updater = new Runnable() {

		@Override
		public void run() {
			label.setColor(getPopupBackground());
			if ( inSubTask ) {
				label.setText(subTaskName +
					(subTaskCompletion != null ? " [" + (int) (subTaskCompletion * 100) + "%]" : ""));
			} else {
				label.setText(mainTaskName);
			}
			// int width = label.computeMinWidth();
			// compo.setSize(width, compo.getSize().y);
			// ((GridData) label.getLayoutData()).widthHint = width;
			// parent.layout(true, true);
			if ( popup.isVisible() ) {
				popup.display();
			}
		}

	};

	/**
	 * Method updateWith()
	 * @see msi.gama.gui.swt.controls.ThreadedUpdater.IUpdaterTarget#updateWith(java.lang.Object)
	 */
	@Override
	public void updateWith(final IStatusMessage m) {
		if ( m instanceof SubTaskMessage ) {
			SubTaskMessage m2 = (SubTaskMessage) m;
			Boolean beginOrEnd = m2.getBeginOrEnd();
			if ( beginOrEnd == null ) {
				// completion
				subTaskCompletion = ((SubTaskMessage) m).getCompletion();
			} else if ( beginOrEnd ) {
				// begin task
				subTaskName = m.getText();
				inSubTask = true;
				subTaskCompletion = null;
			} else {
				// end task
				inSubTask = false;
				subTaskCompletion = null;
			}

		} else {
			inSubTask = false; // in case
			mainTaskName = m.getText();
			state = m.getCode();
		}

		GuiUtils.run(updater);

	}

	@Override
	public int getCurrentState() {
		return state;
	}

	@Override
	public boolean isDynamic() {
		return false;
	}

}
