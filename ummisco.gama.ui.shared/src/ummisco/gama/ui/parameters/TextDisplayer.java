/*******************************************************************************************************
 *
 * TextDisplayer.java, in ummisco.gama.ui.shared, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.9.1).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gama.ui.parameters;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import msi.gama.kernel.experiment.InputParameter;
import msi.gama.kernel.experiment.TextStatement;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaFont;
import ummisco.gama.ui.resources.GamaColors;
import ummisco.gama.ui.utils.WorkbenchHelper;

/**
 * The Class TextDisplayer.
 */
public class TextDisplayer extends AbstractEditor<TextStatement> {

	/** The text. */
	StyledText text;
	TextStatement statement;

	final Color back, front;
	final Font font;

	/**
	 * Instantiates a new command editor.
	 *
	 * @param scope
	 *            the scope
	 * @param command
	 *            the command
	 * @param l
	 *            the l
	 */
	public TextDisplayer(final IScope scope, final TextStatement command) {
		super(scope, null, new InputParameter(command.getName(), null), null);
		statement = command;
		java.awt.Color c = command.getColor(scope);
		java.awt.Color b = command.getBackground(scope);
		front = c == null ? null : GamaColors.toSwtColor(c);
		back = b == null ? null : GamaColors.toSwtColor(b);
		GamaFont f = command.getFont(scope);
		font = f == null ? null : new Font(WorkbenchHelper.getDisplay(), f.getFontName(), f.getSize(), f.getStyle());
	}

	@Override
	public void createControls(final EditorsGroup parent) {
		this.parent = parent;
		internalModification = true;
		// Create the label of the value editor
		editorLabel = createEditorLabel();
		// Create the composite that will hold the value editor and the toolbar
		createValueComposite();
		// Create and initialize the value editor
		editorControl = createEditorControl();

		// Create and initialize the toolbar associated with the value editor
		editorToolbar = null;
		internalModification = false;
		parent.requestLayout();
	}

	@Override
	Composite createValueComposite() {
		composite = new Composite(parent, SWT.NONE);
		GamaColors.setBackground(parent.getBackground(), composite);
		final var data = new GridData(SWT.FILL, SWT.FILL, true, true);
		data.minimumWidth = 100;
		data.horizontalSpan = 3;
		composite.setLayoutData(data);
		final var layout = new FillLayout();
		composite.setLayout(layout);
		return composite;
	}

	@Override
	protected Control createCustomParameterControl(final Composite composite) throws GamaRuntimeException {
		text = new StyledText(composite, SWT.WRAP | SWT.READ_ONLY);
		text.setJustify(true);
		text.setMargins(4, 4, 4, 4);
		if (back != null) {
			if (front != null) {
				GamaColors.setBackAndForeground(back, front, text);
			} else {
				GamaColors.setBackground(back, text);
			}
		} else if (front != null) { GamaColors.setForeground(front, text); }
		text.setText(statement.getText(getScope()));
		if (font != null) {
			int a = text.getCharCount();
			StyleRange[] sr = new StyleRange[1];
			sr[0] = new StyleRange();
			sr[0].start = 0;
			sr[0].length = a;
			sr[0].font = font;
			sr[0].foreground = front;
			sr[0].background = back;
			text.replaceStyleRanges(sr[0].start, sr[0].length, sr);
		} else if (front != null || back != null) {
			int a = text.getCharCount();
			StyleRange[] sr = new StyleRange[1];
			sr[0] = new StyleRange();
			sr[0].start = 0;
			sr[0].length = a;
			sr[0].foreground = front;
			sr[0].background = back;
			text.replaceStyleRanges(sr[0].start, sr[0].length, sr);
		}
		composite.requestLayout();
		return text;

	}

	@Override
	EditorLabel createEditorLabel() {
		return null;
	}

	@Override
	Color getEditorControlBackground() { return back == null ? super.getEditorControlBackground() : back; }

	@Override
	Color getEditorControlForeground() { return front == null ? super.getEditorControlForeground() : front; }

	@Override
	protected int[] getToolItems() { return new int[0]; }

	@Override
	protected void displayParameterValue() {

	}

}
