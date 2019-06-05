/*********************************************************************************************
 *
 * 'JTextReporter.java, in plugin ummisco.gama.ui.viewers, is part of the source code of the GAMA modeling and
 * simulation platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 *
 *
 **********************************************************************************************/

package ummisco.gama.ui.viewers.gis.geotools.control;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import ummisco.gama.ui.viewers.gis.geotools.utils.Utils;

/**
 * A dialog to display text reports to the user and, if requested, save them to file.
 *
 * @author Michael Bedward
 * @since 2.6
 *
 *
 *
 * @source $URL$
 * @version $URL$
 */
public class JTextReporter extends Dialog {

	/**
	 * Default number of rows shown in the text display area's preferred size
	 */

	Text textArea;
	final String title;

	/**
	 * Creates a new JTextReporter with the following default options:
	 * <ul>
	 * <li>Remains on top of other application windows
	 * <li>Is not modal
	 * <li>Will be disposed of when closed
	 * </ul>
	 *
	 * @param dialogTrim
	 * @param parent
	 *
	 * @param title
	 *            title for the dialog (may be {@code null})
	 * @param rows
	 *            number of text rows displayed without scrolling (if zero or negative, the default is used)
	 * @param cols
	 *            number of text columns displayed without scrolling (if zero or negative the default is used)
	 *
	 */
	public JTextReporter(final Shell parent, final String title) {
		super(parent);
		this.title = title;

		setShellStyle(SWT.CLOSE | SWT.MODELESS | SWT.BORDER | SWT.TITLE);
		setBlockOnOpen(false);
	}

	@Override
	protected void configureShell(final Shell newShell) {
		newShell.setText(title);
		newShell.setSize(250, 400);

		super.configureShell(newShell);
	}

	/**
	 * Append text to the report being displayed. No additional line feeds are added after the text.
	 * <p>
	 * If called from other than the AWT event dispatch thread this method puts the append task onto the dispatch thread
	 * and waits for its completion.
	 *
	 * @param text
	 *            the text to be appended to the report
	 */
	public synchronized void append(final String text) {
		final Runnable runner = () -> {
			final StringBuilder sb = new StringBuilder();
			sb.append(textArea.getText());
			sb.append("\n");
			sb.append(text);
			textArea.setText(sb.toString());
			textArea.setSelection(textArea.getCharCount());
		};
		Utils.runGuiRunnableSafe(runner, true);
	}

	@Override
	protected Control createDialogArea(final Composite parent) {
		parent.setLayout(new GridLayout(1, false));

		textArea = new Text(parent, SWT.MULTI | SWT.WRAP | SWT.V_SCROLL | SWT.BORDER);
		textArea.setEditable(false);
		final GridData textAreaGD = new GridData(SWT.FILL, SWT.FILL, true, true);
		textArea.setLayoutData(textAreaGD);

		final Composite buttonComposite = new Composite(parent, SWT.NONE);
		buttonComposite.setLayoutData(new GridData(SWT.FILL, SWT.BOTTOM, true, false));
		buttonComposite.setLayout(new GridLayout(2, true));

		final Button saveButton = new Button(buttonComposite, SWT.PUSH);
		saveButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		saveButton.setText("Save");
		saveButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final org.eclipse.swt.events.SelectionEvent e) {
				saveReport();
			};
		});

		final Button clearButton = new Button(buttonComposite, SWT.PUSH);
		clearButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		clearButton.setText("Clear");
		clearButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final org.eclipse.swt.events.SelectionEvent e) {
				clearReport();
			};
		});

		return parent;
	}

	@Override
	protected Button createButton(final Composite parent, final int id, final String label,
			final boolean defaultButton) {
		return null;
	}

	/**
	 * Clear the report currently displayed
	 */
	void clearReport() {
		textArea.setText("");
	}

	void saveReport() {
		final File file = getFile();
		if (file != null) {
			try (Writer writer = new BufferedWriter(new FileWriter(file));) {
				writer.write(textArea.getText());
			} catch (final IOException ex) {
				throw new IllegalStateException(ex);
			}
		}
	}

	File getFile() {
		final FileDialog fileDialog = new FileDialog(textArea.getShell(), SWT.SAVE);
		final String path = fileDialog.open();
		if (path == null || path.length() < 1) { return null; }
		return new File(path);
	}

}
