/*******************************************************************************************************
 *
 * CommandEditor.java, in ummisco.gama.ui.shared, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
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

import msi.gama.kernel.experiment.TextStatement;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaFont;
import ummisco.gama.ui.resources.GamaColors;
import ummisco.gama.ui.resources.IGamaColors;
import ummisco.gama.ui.utils.WorkbenchHelper;

/**
 * The Class CommandEditor.
 */
public class TextDisplayer extends AbstractStatementEditor<TextStatement> {

	StyledText text;

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
		super(scope, command, null);
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
		composite.setBackground(parent.getBackground());
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

		java.awt.Color c = getStatement().getColor(getScope());
		Color color;
		if (c == null) {
			color = IGamaColors.NEUTRAL.color();
		} else {
			color = GamaColors.toSwtColor(getStatement().getColor(getScope()));
		}
		text = new StyledText(composite, SWT.BORDER | SWT.WRAP);
		text.setJustify(true);
		text.setMargins(4, 4, 4, 4);
		GamaColors.setBackAndForeground(text, IGamaColors.WHITE.color(), color);
		text.setText(getStatement().getText(getScope()));
		GamaFont font = getStatement().getFont(getScope());
		if (font != null) {
			int a = text.getCharCount();
			Font f = new Font(WorkbenchHelper.getDisplay(), font.getFontName(), font.getSize(), font.getStyle());
			StyleRange[] sr = new StyleRange[1];
			sr[0] = new StyleRange();
			sr[0].start = 0;
			sr[0].length = a;
			sr[0].font = f;
			sr[0].foreground = color;
			text.replaceStyleRanges(sr[0].start, sr[0].length, sr);
		}
		composite.requestLayout();
		return text;

	}

	@Override
	EditorLabel createEditorLabel() {
		return null;
	}

}
