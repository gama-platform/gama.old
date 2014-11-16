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
import msi.gama.gui.swt.SwtGui;
import msi.gama.kernel.simulation.*;
import msi.gama.runtime.GAMA;
import msi.gaml.operators.Strings;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.menus.WorkbenchWindowControlContribution;
import org.jfree.util.StringUtils;

public class StatusControlContribution extends WorkbenchWindowControlContribution implements IPopupProvider, IUpdaterTarget<IStatusMessage> {

	private Composite compo;
	Label label;
	private Popup popup;
	int state;
	volatile String mainTaskName;
	volatile String subTaskName;
	volatile boolean inSubTask = false;
	volatile Double subTaskCompletion;

	public StatusControlContribution() {}

	public StatusControlContribution(final String id) {
		super(id);
	}

	@Override
	protected int computeWidth(final Control control) {
		return control.computeSize(SWT.DEFAULT, SWT.DEFAULT, true).x;
	}

	@Override
	protected Control createControl(final Composite parent) {
		compo = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(2, false);
		layout.marginHeight = 0;
		layout.marginWidth = 2;
		compo.setLayout(layout);
		GridData data = new GridData(SWT.FILL, SWT.CENTER, true, true);
		data.widthHint = 300;
		label = new Label(compo, SWT.LEFT);
		label.setLayoutData(data);
		label.setBackground(SwtGui.getNeutralColor());
		label.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		label.setText("No simulation running");
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
		sb.append("Cycles elapsed: ").append(Strings.TAB).append(clock.getCycle()).append(nl);
		sb.append("Simulated time: ").append(Strings.TAB).append(Strings.asDate(clock.getTime(), null)).append(nl);
		sb.append("Cycle duration: ").append(Strings.TAB).append(Strings.TAB).append(clock.getDuration()).append("ms")
			.append(nl);
		sb.append("Average duration: ").append(Strings.TAB).append((int) clock.getAverageDuration()).append("ms")
			.append(nl);
		sb.append("Total duration: ").append(Strings.TAB).append(Strings.TAB).append(clock.getTotalDuration())
			.append("ms");
		return sb.toString();
	}

	/**
	 * @see msi.gama.gui.swt.controls.IPopupProvider#getPopupBackground()
	 */
	@Override
	public Color getPopupBackground() {
		return state == IGui.ERROR ? SwtGui.getErrorColor() : state == IGui.WAIT ? SwtGui.getWarningColor()
			: state == IGui.NEUTRAL ? SwtGui.getNeutralColor() : SwtGui.getOkColor();
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
			label.setBackground(getPopupBackground());
			if ( inSubTask ) {
				label.setText(subTaskName +
					(subTaskCompletion != null ? " [" + (int) (subTaskCompletion * 100) + "%]" : ""));
			} else {
				label.setText(mainTaskName);
			}
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
				System.out.println("Begin Sub Task =" + m.getText());
				subTaskName = m.getText();
				inSubTask = true;
				subTaskCompletion = null;
			} else {
				// end task
				System.out.println("End Sub Task =" + m.getText());
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

}
