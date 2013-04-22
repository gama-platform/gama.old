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
	private final boolean follow = true;

	/**
	 * @see msi.gama.gui.views.GamaViewPart#getToolbarActionsId()
	 */
	@Override
	protected Integer[] getToolbarActionsId() {
		// TODO Need to be usable (not the case now)
		return new Integer[] { PAUSE, REFRESH };
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
