/*********************************************************************************************
 *
 * 'ExpressionControl.java, in plugin ummisco.gama.ui.shared, is part of the source code of the GAMA modeling and
 * simulation platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package ummisco.gama.ui.parameters;

import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseTrackAdapter;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import msi.gama.common.util.StringUtils;
import msi.gama.kernel.simulation.SimulationAgent;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GAML;
import msi.gaml.expressions.IExpression;
import msi.gaml.types.GamaStringType;
import msi.gaml.types.IType;
import msi.gaml.types.Types;
import ummisco.gama.ui.controls.ITooltipDisplayer;
import ummisco.gama.ui.resources.GamaColors.GamaUIColor;
import ummisco.gama.ui.resources.IGamaColors;
import ummisco.gama.ui.views.toolbar.GamaToolbarFactory;

@SuppressWarnings ({ "rawtypes", "unchecked" })
public class ExpressionControl implements /* IPopupProvider, */SelectionListener, ModifyListener, FocusListener {

	private final Text text;
	private final ExpressionBasedEditor<Object> editor;
	private GamaUIColor background;
	private Object currentValue;
	protected Exception currentException;
	final boolean evaluateExpression;
	private final IAgent hostAgent;
	private final IScope scope;
	private final IType<?> expectedType;
	MouseTrackListener tooltipListener = new MouseTrackAdapter() {

		@Override
		public void mouseExit(final MouseEvent arg0) {
			removeTooltip();
		}
	};

	public ExpressionControl(final IScope scope, final Composite comp, final ExpressionBasedEditor ed,
			final IAgent agent, final IType<?> expectedType, final int controlStyle, final boolean evaluate) {
		this.scope = scope;
		editor = ed;
		evaluateExpression = evaluate;
		hostAgent = agent;
		this.expectedType = expectedType;
		text = createTextBox(comp, controlStyle);
		text.addModifyListener(this);
		text.addFocusListener(this);
		text.addSelectionListener(this);
		text.addMouseTrackListener(tooltipListener);
		if (ed != null) {
			ed.getLabel().addMouseTrackListener(tooltipListener);
		}
	}

	@Override
	public void modifyText(final ModifyEvent event) {
		// if ( editor == null ) { return; }
		if (editor != null && editor.internalModification) { return; }
		modifyValue();
		displayTooltip();
	}

	protected void displayTooltip() {
		final String s = getPopupText();
		if (s == null || s.isEmpty()) {
			removeTooltip();
		} else {
			final ITooltipDisplayer displayer = GamaToolbarFactory.findTooltipDisplayer(text);
			if (displayer != null) {
				displayer.displayTooltip(s, background);
			}
		}
		if (editor != null && background != null) {
			editor.getComposite().setBackground(background.inactive());
		}
	}

	protected void removeTooltip() {
		final ITooltipDisplayer displayer = GamaToolbarFactory.findTooltipDisplayer(text);
		if (displayer != null) {
			displayer.stopDisplayingTooltips();
		}
		if (editor != null) {
			editor.getComposite().setBackground(AbstractEditor.NORMAL_BACKGROUND);
		}

	}

	@Override
	public void widgetDefaultSelected(final SelectionEvent me) {
		try {
			if (text == null || text.isDisposed()) { return; }
			modifyValue();
			displayValue(getCurrentValue());
		} catch (final RuntimeException e) {
			e.printStackTrace();
		}
	}

	private Object computeValue() {
		try {
			currentException = null;
			IAgent agent = getHostAgent();
			// AD: fix for SWT Issue in Eclipse 4.4
			if (text == null || text.isDisposed()) { return null; }
			String s = text.getText();
			if (expectedType == Types.STRING) {
				if (!StringUtils.isGamaString(s))
					s = StringUtils.toGamlString(s);
			}
			// AD: Fix for Issue 1042
			if (agent != null && agent.getScope().interrupted() && agent instanceof SimulationAgent) {
				agent = agent.getScope().getExperiment();
			}
			if (NumberEditor.UNDEFINED_LABEL.equals(s)) {
				setCurrentValue(null);
			} else if (agent == null) {
				if (expectedType == Types.STRING)
					setCurrentValue(StringUtils.toJavaString(GamaStringType.staticCast(null, s, false)));
				else
					setCurrentValue(expectedType.cast(scope, s, null, false));
			} else {
				setCurrentValue(evaluateExpression ? GAML.evaluateExpression(s, agent)
						: GAML.compileExpression(s, agent, true));
			}
		} catch (final Exception e) {
			currentException = e;
			return null;
		}
		return getCurrentValue();
	}

	public void modifyValue() {
		final Object oldValue = getCurrentValue();
		final Object value = computeValue();
		if (currentException != null) {
			setCurrentValue(oldValue);
			return;
		}
		if (editor != null) {
			try {

				if (editor.acceptNull && value == null) {
					editor.modifyValue(null);
				} else {
					if (expectedType == Types.STRING) {
						editor.modifyValue(evaluateExpression
								? StringUtils.toJavaString(GamaStringType.staticCast(scope, value, false)) : value);
					} else
						editor.modifyValue(evaluateExpression ? expectedType.cast(scope, value, false, false) : value);
				}
				editor.checkButtons();

			} catch (final GamaRuntimeException e) {
				setCurrentValue(oldValue);
				currentException = e;
			}
		}
	}

	protected Text createTextBox(final Composite comp, final int controlStyle) {
		return new Text(comp, controlStyle);
	}

	@Override
	public void focusGained(final FocusEvent e) {
		// if ( editor != null ) {
		// DEBUG.LOG("Focus gained:" + editor.getParam().getName());
		// }
		// // if ( e.widget == null || !e.widget.equals(text) ) { return; }
		// computeValue();
	}

	@Override
	public void focusLost(final FocusEvent e) {
		if (e.widget == null || !e.widget.equals(text)) { return; }
		widgetDefaultSelected(null);
		/* async is needed to wait until focus reaches its new Control */
		removeTooltip();
		// SwtGui.getDisplay().timerExec(100, new Runnable() {
		//
		// @Override
		// public void run() {
		// if ( SwtGui.getDisplay().isDisposed() ) { return; }
		// final Control control = SwtGui.getDisplay().getFocusControl();
		// if ( control != text ) {
		// widgetDefaultSelected(null);
		// }
		// }
		// });

	}

	public Text getControl() {
		return text;
	}

	@Override
	public void widgetSelected(final SelectionEvent e) {}

	/**
	 * @see ummisco.gama.ui.controls.IPopupProvider#getPopupText()
	 */
	public String getPopupText() {
		String result = "";
		// if ( getCurrentValue() == null ) {
		// final Object value = computeValue();
		// }
		final Object value = getCurrentValue();
		if (currentException != null) {
			background = IGamaColors.ERROR;
			result += currentException.getMessage();
		} else {
			if (isOK(value)) {
				background = IGamaColors.OK;
			} else {
				background = IGamaColors.WARNING;
				result += "The current value should be of type " + expectedType.toString();
			}
		}
		return result;
	}

	private Boolean isOK(final Object value) {
		if (evaluateExpression) {
			return expectedType.canBeTypeOf(scope, value);
		} else if (value instanceof IExpression) {
			return expectedType.isAssignableFrom(((IExpression) value).getGamlType());
		} else {
			return false;
		}
	}

	IAgent getHostAgent() {
		return hostAgent == null ? editor == null ? null : editor.getAgent() : hostAgent;
	}

	/**
	 * @return the currentValue
	 */
	protected Object getCurrentValue() {
		return currentValue;
	}

	/**
	 * @param currentValue
	 *            the currentValue to set
	 */
	protected void setCurrentValue(final Object currentValue) {
		this.currentValue = currentValue;
	}

	/**
	 * @param currentValue2
	 */
	public void displayValue(final Object currentValue2) {
		setCurrentValue(evaluateExpression ? expectedType == Types.STRING
				? StringUtils.toJavaString(GamaStringType.staticCast(scope, currentValue2, false))
				: expectedType.cast(scope, currentValue2, null, false) : currentValue2);
		if (expectedType == Types.STRING) {
			text.setText(currentValue == null ? "" : StringUtils.toJavaString(currentValue.toString()));
		} else
			text.setText(StringUtils.toGaml(currentValue2, false));
	}

}
