/*
 * GAMA - V1.4 http://gama-platform.googlecode.com
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
package msi.gama.gui.application.views;

import java.io.*;
import org.eclipse.jface.text.*;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.console.*;

public class ConsoleView extends GamaViewPart {

	public static final String ID = "msi.gama.gui.application.view.ConsoleView";
	BufferedWriter bw;
	MessageConsole msgConsole;
	// private final boolean wrap = true;
	private final boolean follow = true;

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
