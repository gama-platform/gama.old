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
import org.eclipse.jface.text.*;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.console.*;

public class ConsoleView extends GamaViewPart {

	public static final String ID = "msi.gama.application.view.ConsoleView";
	private BufferedWriter bw;
	private MessageConsole msgConsole;
	// private final boolean wrap = true;
	private final static boolean follow = true;

	/**
	 * @see msi.gama.gui.views.GamaViewPart#getToolbarActionsId()
	 */
	@Override
	public Integer[] getToolbarActionsId() {
		// TODO Need to be usable (not the case now)
		return new Integer[] { PAUSE, REFRESH, CLEAR };
	}

	@Override
	public void ownCreatePartControl(final Composite parent) {
		msgConsole = new MessageConsole("GAMA Console", null, true);
		TextConsoleViewer viewer = new TextConsoleViewer(parent, msgConsole);
		final StyledText textWidget = viewer.getTextWidget();
		viewer.addTextListener(new ITextListener() {

			@Override
			public void textChanged(final TextEvent event) {
				if ( textWidget != null && !textWidget.isDisposed() && follow ) {
					textWidget.setTopIndex(textWidget.getLineCount() - 1);
				}
			}

		});
		MessageConsoleStream stream = msgConsole.newMessageStream();
		stream.setActivateOnWrite(false);
		bw = new BufferedWriter(new OutputStreamWriter(stream));
	}

	/**
	 * Append the text to the console.
	 * @param text to display in the console
	 */
	public void append(final String text) {
		try {
			bw.append(text);
			bw.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void setText(final String string) {
		msgConsole.clearConsole();
		try {
			bw.append(string);
			bw.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
