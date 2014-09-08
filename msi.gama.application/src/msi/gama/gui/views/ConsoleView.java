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
import msi.gama.common.*;
import msi.gama.common.GamaPreferences.IPreferenceChange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.console.*;
import org.eclipse.ui.internal.console.IOConsoleViewer;

public class ConsoleView extends GamaViewPart {

	public static final String ID = "msi.gama.application.view.ConsoleView";
	private BufferedWriter bw;
	private MessageConsole msgConsole;
	// private final boolean wrap = true;
	// private final static boolean follow = true;
	boolean paused = false;
	private final StringBuilder pauseBuffer = new StringBuilder(GamaPreferences.CORE_CONSOLE_BUFFER.getValue());

	/**
	 * @see msi.gama.gui.views.GamaViewPart#getToolbarActionsId()
	 */
	@Override
	public Integer[] getToolbarActionsId() {
		// TODO Need to be usable (not the case now)
		return new Integer[] { PAUSE, REFRESH, CLEAR };
	}

	public void setCharacterLimit(final int limit) {
		msgConsole.setWaterMarks(limit, limit * 2);
	}

	@Override
	public void ownCreatePartControl(final Composite parent) {
		msgConsole = new MessageConsole("GAMA Console", null);
		setCharacterLimit(GamaPreferences.CORE_CONSOLE_SIZE.getValue());
		GamaPreferences.CORE_CONSOLE_SIZE.onChange(new IPreferenceChange<Integer>() {

			@Override
			public boolean valueChange(final Integer newValue) {
				setCharacterLimit(newValue);
				return true;
			}
		});
		IOConsoleViewer viewer = new IOConsoleViewer(parent, msgConsole);
		final StyledText textWidget = viewer.getTextWidget();
		// textWidget.setTextLimit(GamaPreferences.CORE_CONSOLE_SIZE.getValue());
		// viewer.addTextListener(new ITextListener() {
		//
		// @Override
		// public void textChanged(final TextEvent event) {
		// if ( textWidget != null && !textWidget.isDisposed() && follow ) {
		// textWidget.setTopIndex(textWidget.getLineCount() - 1);
		// }
		// }
		//
		// });
		IOConsoleOutputStream stream = msgConsole.newOutputStream();

		stream.setActivateOnWrite(false);

		bw = new BufferedWriter(new OutputStreamWriter(stream));
	}

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
				int max = GamaPreferences.CORE_CONSOLE_BUFFER.getValue();
				if ( max > 0 ) {
					pauseBuffer.append(text);
					if ( pauseBuffer.length() > max ) {
						pauseBuffer.delete(0, pauseBuffer.length() - max - 1);
						pauseBuffer.insert(0, "(...)\n");
					}
				} else if ( max == -1 ) {
					pauseBuffer.append(text);
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
	public void pauseChanged() {
		paused = !paused;
		if ( paused ) {
			pauseBuffer.setLength(0);
		} else {
			append(pauseBuffer.toString());
		}
	}

}
