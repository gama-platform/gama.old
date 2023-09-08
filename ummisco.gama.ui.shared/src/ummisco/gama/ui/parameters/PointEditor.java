/*******************************************************************************************************
 *
 * PointEditor.java, in ummisco.gama.ui.shared, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.3).
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
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.operators.Cast;
import msi.gaml.types.IType;
import msi.gaml.types.Types;
import ummisco.gama.ui.interfaces.EditorListener;

/**
 * The Class PointEditor.
 */
public class PointEditor extends AbstractEditor<GamaPoint> implements VerifyListener {

	/** The Constant LABELS. */
	private static final String[] LABELS = { "x", "y", "z" };

	/** The Constant LABEL_WIDTH. */
	private static final int LABEL_WIDTH = 15;

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
	private final Text[] ordinates = new Text[3];

	/** The labels. */
	private final Label[] labels = new Label[3];

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
	PointEditor(final IAgent agent, final IParameter param, final EditorListener<GamaPoint> l) {
		super(agent, param, l);
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
		for (var i = 0; i < 3; i++) {
			labels[i] = new Label(pointEditor, SWT.LEFT);
			labels[i].setText(LABELS[i]);
			RowDataFactory.swtDefaults().hint(LABEL_WIDTH, SWT.DEFAULT).applyTo(labels[i]);
			ordinates[i] = new Text(pointEditor, SWT.NONE);
			RowDataFactory.swtDefaults().applyTo(ordinates[i]);
			ordinates[i].addModifyListener(this);
			ordinates[i].addVerifyListener(this);
		}
		pointEditor.addControlListener(new ControlAdapter() {

			@Override
			public void controlResized(final ControlEvent e) {
				int size = (pointEditor.getSize().x - (3 * LABEL_WIDTH + 2 * MARGIN + 5 * SPACING)) / 3;
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
			if (isReverting || !ordinates[i].isFocusControl()) {
				ordinates[i].setText(currentValue == null ? "0.0" : StringUtils.toGaml(p.getOrdinate(i), false));
			}
		}
		isReverting = false;
		allowVerification = true;
	}

	@Override
	public void modifyText(final ModifyEvent me) {
		if (internalModification || !allowVerification) return;
		modifyAndDisplayValue(new GamaPoint(Cast.asFloat(getScope(), ordinates[0].getText()),
				Cast.asFloat(getScope(), ordinates[1].getText()), Cast.asFloat(getScope(), ordinates[2].getText())));

	}

	@Override
	protected boolean modifyValue(final Object val) throws GamaRuntimeException {
		GamaPoint i = Cast.asPoint(getScope(), val);
		if (minValue != null && i.smallerThan(Cast.asPoint(getScope(), minValue))
				|| getMaxValue() != null && i.biggerThan(Cast.asPoint(getScope(), getMaxValue())))
			return false;
		return super.modifyValue(i);
	}

	@Override
	protected GamaPoint defaultStepValue() {
		return new GamaPoint(0.1, 0.1, 0.1);
	}

	@SuppressWarnings ({ "unchecked", "rawtypes" })
	@Override
	public IType getExpectedType() { return Types.POINT; }

	@Override
	protected int[] getToolItems() { return new int[] { PLUS, MINUS, REVERT }; }

	@Override
	protected GamaPoint applyRevert() {
		isReverting = true;
		return super.applyRevert();
	}

	@Override
	protected GamaPoint applyPlus() {
		isReverting = true;
		GamaPoint p = currentValue.clone();
		p.add(getStepValue());
		return p;
	}

	@Override
	protected GamaPoint applyMinus() {
		isReverting = true;
		GamaPoint p = currentValue.clone();
		p.subtract(getStepValue());
		return p;
	}

}
