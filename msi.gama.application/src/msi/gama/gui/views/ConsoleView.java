/*********************************************************************************************
 *
 *
 * 'ConsoleView.java', in plugin 'msi.gama.application', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gama.gui.views;

import java.io.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.console.*;
import org.eclipse.ui.internal.console.IOConsoleViewer;
import msi.gama.common.GamaPreferences;
import msi.gama.common.GamaPreferences.IPreferenceChangeListener;
import msi.gama.gui.swt.*;
import msi.gama.gui.swt.controls.GamaToolbar2;
import msi.gama.gui.views.actions.GamaToolbarFactory;
import msi.gama.runtime.GAMA;

public class ConsoleView extends GamaViewPart implements IToolbarDecoratedView.Sizable, IToolbarDecoratedView.Pausable {

	public static final String ID = "msi.gama.application.view.ConsoleView";
	private BufferedWriter bw;
	private MessageConsole msgConsole;
	IOConsoleViewer viewer;
	boolean paused = false;
	private final StringBuilder pauseBuffer = new StringBuilder(GamaPreferences.CORE_CONSOLE_BUFFER.getValue());

	public void setCharacterLimit(final int limit) {
		msgConsole.setWaterMarks(limit, limit * 2);
	}

	@Override
	public void ownCreatePartControl(final Composite parent) {
		msgConsole = new MessageConsole("GAMA Console", null);
		setCharacterLimit(GamaPreferences.CORE_CONSOLE_SIZE.getValue());
		GamaPreferences.CORE_CONSOLE_SIZE.addChangeListener(new IPreferenceChangeListener<Integer>() {

			@Override
			public boolean beforeValueChange(final Integer newValue) {
				return true;
			}

			@Override
			public void afterValueChange(final Integer newValue) {
				setCharacterLimit(newValue);
			}
		});
		viewer = new IOConsoleViewer(parent, msgConsole);

		IOConsoleOutputStream stream = msgConsole.newOutputStream();
		stream.setActivateOnWrite(false);
		bw = new BufferedWriter(new OutputStreamWriter(stream));
	}

	private boolean indicated = false;

	/**
	 * Append the text to the console.
	 * @param text to display in the console
	 */
	public void append(final String text) {
		try {
			if ( !paused ) {
				bw.append(text);
				bw.flush();
			} else {
				int maxMemorized = GamaPreferences.CORE_CONSOLE_BUFFER.getValue();
				int maxDisplayed = GamaPreferences.CORE_CONSOLE_SIZE.getValue();
				if ( maxDisplayed > -1 ) {
					// we limit the size of the buffer to the size of the displayed characters, as there is no need to buffer more than what can be displayed
					if ( maxMemorized == -1 ) {
						maxMemorized = maxDisplayed;
					} else {
						maxMemorized = Math.min(maxMemorized, maxDisplayed);
					}
				}
				if ( maxMemorized > 0 ) {
					pauseBuffer.append(text);
					if ( pauseBuffer.length() > maxMemorized ) {
						pauseBuffer.delete(0, pauseBuffer.length() - maxMemorized - 1);
						pauseBuffer.insert(0, "(...)\n");
					}
				} else if ( maxMemorized == -1 ) {
					pauseBuffer.append(text);
				}
				if ( !indicated ) {
					GAMA.getGui().run(new Runnable() {

						@Override
						public void run() {
							if ( toolbar != null ) {
								toolbar.status((Image) null, "New contents available", IGamaColors.BLUE, SWT.LEFT);
							}
							indicated = true;
						}
					});
				}

			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void setText(final String string) {
		msgConsole.clearConsole();
		try {
			if ( !paused ) {
				bw.append(string);
				bw.flush();
			} else {
				pauseBuffer.append(string);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@Override
	public Control getSizableFontControl() {
		if ( viewer == null ) { return null; }
		return viewer.getTextWidget();
	}

	@Override
	public void pauseChanged() {
		if ( paused ) {
			GAMA.getGui().asyncRun(new Runnable() {

				@Override
				public void run() {
					if ( toolbar != null ) {
						toolbar.wipe(SWT.LEFT, true);
					}
					indicated = false;
				}
			});

		}
		paused = !paused;
		if ( paused ) {
			pauseBuffer.setLength(0);
		} else {
			append(pauseBuffer.toString());
		}
	}

	@Override
	public void createToolItems(final GamaToolbar2 tb) {
		super.createToolItems(tb);
		tb.sep(GamaToolbarFactory.TOOLBAR_SEP, SWT.RIGHT);
		tb.button(IGamaIcons.ACTION_CLEAR.getCode(), "Clear", "Clear the console", new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent arg0) {
				setText("");
			}
		}, SWT.RIGHT);

	}

	@Override
	protected GamaUIJob createUpdateJob() {
		return null;
	}

	/**
	 * As ConsoleView is automatically opened by moving to the simulation perspective, the automatic closing can cause problems. So the view is stated as accepting an "experiment-less" mode. See Issue
	 * #1361
	 * Method shouldBeClosedWhenNoExperiments()
	 * @see msi.gama.gui.views.GamaViewPart#shouldBeClosedWhenNoExperiments()
	 */
	@Override
	protected boolean shouldBeClosedWhenNoExperiments() {
		return false;
	}

	/**
	 * Method synchronizeChanged()
	 * @see msi.gama.gui.views.IToolbarDecoratedView.Pausable#synchronizeChanged()
	 */
	@Override
	public void synchronizeChanged() {}

}
