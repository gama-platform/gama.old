/*******************************************************************************************************
 *
 * PairEditor.java, in ummisco.gama.ui.shared, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.1).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gama.ui.parameters;

import static msi.gama.application.workbench.ThemeHelper.isDark;
import static org.eclipse.jface.layout.RowLayoutFactory.fillDefaults;
import static org.eclipse.swt.SWT.HORIZONTAL;
import static ummisco.gama.ui.resources.GamaColors.get;

import org.eclipse.jface.layout.RowDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import msi.gama.common.util.StringUtils;
import msi.gama.kernel.experiment.IParameter;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.runtime.IScope;
import msi.gama.util.GamaPair;
import msi.gaml.types.IType;
import msi.gaml.types.Types;
import ummisco.gama.ui.interfaces.EditorListener;

/**
 * The Class PointEditor.
 */
public class PairEditor extends AbstractEditor<GamaPair> implements VerifyListener {

	/** The Constant LABELS. */
	private static final String[] LABELS = { "key", "value" };

	/** The Constant LABEL_WIDTH. */
	private static final int LABEL_WIDTH = 25;

	/** The Constant SPACING. */
	private static final int SPACING = 4;

	/** The Constant MARGIN. */
	private static final int MARGIN = 2;

	/** The point editor. */
	private Composite pointEditor;

	/** The allow verification. */
	private boolean allowVerification;

	/** The is reverting. */
	private boolean isReverting;

	/** The ordinates. */
	private final Text[] ordinates = new Text[2];

	/** The labels. */
	private final Label[] labels = new Label[2];

	/**
	 * Instantiates a new point editor.
	 *
	 * @param scope
	 *            the scope
	 * @param agent
	 *            the agent
	 * @param param
	 *            the param
	 * @param l
	 *            the l
	 */
	PairEditor(final IScope scope, final IAgent agent, final IParameter param, final EditorListener<GamaPair> l) {
		super(scope, agent, param, l);
	}

	// /**
	// * Instantiates a new point editor.
	// *
	// * @param scope the scope
	// * @param parent the parent
	// * @param title the title
	// * @param value the value
	// * @param whenModified the when modified
	// */
	// PointEditor(final IScope scope, final EditorsGroup parent, final String title, final GamaPoint value,
	// final EditorListener<GamaPoint> whenModified) {
	// // Convenience method
	// super(scope, new InputParameter(title, value), whenModified);
	// this.createControls(parent);
	// }

	@Override
	public Control createCustomParameterControl(final Composite comp) {
		pointEditor = new Composite(comp, SWT.NONE);
		final RowLayout pointEditorLayout = fillDefaults().center(true).spacing(SPACING).margins(MARGIN, MARGIN)
				.type(HORIZONTAL).fill(true).wrap(false).create();
		final var d = new GridData(SWT.FILL, SWT.CENTER, true, false);
		pointEditor.setLayoutData(d);
		pointEditor.setLayout(pointEditorLayout);
		pointEditor.addListener(SWT.Paint, e -> {
			GC gc = e.gc;
			Rectangle bounds = pointEditor.getBounds();
			Color ref = comp.getBackground();
			Color back = isDark() ? get(ref).lighter() : get(ref).darker();
			gc.setBackground(back);
			gc.fillRoundRectangle(0, 0, bounds.width, bounds.height, 5, 5);
			for (Label c : labels) { c.setBackground(back); }
			for (Text t : ordinates) { t.setBackground(ref); }
		});
		for (var i = 0; i < 2; i++) {
			labels[i] = new Label(pointEditor, SWT.LEFT);
			labels[i].setText(LABELS[i]);
			RowDataFactory.swtDefaults().hint(LABEL_WIDTH, SWT.DEFAULT).applyTo(labels[i]);
			ordinates[i] = new Text(pointEditor, SWT.NONE);
			RowDataFactory.swtDefaults().applyTo(ordinates[i]);
			ordinates[i].addModifyListener(this);
			// ordinates[i].addVerifyListener(this);
		}
		pointEditor.addControlListener(new ControlAdapter() {

			@Override
			public void controlResized(final ControlEvent e) {
				int size = (pointEditor.getSize().x - (2 * LABEL_WIDTH + 2 * MARGIN + 4 * SPACING)) / 2;
				for (Text text : ordinates) { ((RowData) text.getLayoutData()).width = size; }
			}

		});
		displayParameterValue();
		return pointEditor;
	}

	@Override
	public void verifyText(final VerifyEvent event) {
		if (internalModification || !allowVerification) return;
		final var myChar = event.character;
		// Last one is for texts
		final var text = (Text) event.widget;
		final var old = text.getText();
		final var alreadyPoint = old.contains(".");
		final var atBeginning = event.start == 0;
		event.doit = Character.isDigit(myChar) || myChar == '\b' || myChar == '.' && !alreadyPoint
				|| myChar == '-' && atBeginning;
	}

	@Override
	protected void displayParameterValue() {
		allowVerification = false;
		final var p = currentValue;
		for (var i = 0; i < 3; i++) {
			if (isReverting || !ordinates[0].isFocusControl()) {
				ordinates[0].setText(currentValue == null ? "nil" : StringUtils.toGaml(p.getKey(), false));
			}
			if (isReverting || !ordinates[1].isFocusControl()) {
				ordinates[1].setText(currentValue == null ? "nil" : StringUtils.toGaml(p.getValue(), false));
			}
		}
		isReverting = false;
		allowVerification = true;
	}

	@Override
	public void modifyText(final ModifyEvent me) {
		if (internalModification || !allowVerification) return;
		modifyAndDisplayValue(
				new GamaPair(getScope(), ordinates[0].getText(), ordinates[1].getText(), Types.NO_TYPE, Types.NO_TYPE));

	}

	@SuppressWarnings ({ "unchecked", "rawtypes" })
	@Override
	public IType getExpectedType() { return Types.PAIR; }

	@Override
	protected int[] getToolItems() { return new int[] { PLUS, MINUS, REVERT }; }

	@Override
	protected GamaPair applyRevert() {
		isReverting = true;
		return super.applyRevert();
	}

}
