/*********************************************************************************************
 *
 *
 * 'ExpressionControl.java', in plugin 'msi.gama.application', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gama.gui.parameters;

import org.eclipse.swt.events.*;
import org.eclipse.swt.widgets.*;
import msi.gama.common.util.StringUtils;
import msi.gama.gui.swt.GamaColors.GamaUIColor;
import msi.gama.gui.swt.IGamaColors;
import msi.gama.gui.swt.controls.ITooltipDisplayer;
import msi.gama.gui.views.actions.GamaToolbarFactory;
import msi.gama.kernel.simulation.SimulationAgent;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.runtime.*;
import msi.gama.runtime.GAMA.InScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GAML;
import msi.gaml.expressions.IExpression;
import msi.gaml.operators.Cast;
import msi.gaml.types.IType;

public class ExpressionControl implements /* IPopupProvider, */SelectionListener, ModifyListener, FocusListener {

	private final Text text;
	private final ExpressionBasedEditor editor;
	private GamaUIColor background;
	private Object currentValue;
	protected Exception currentException;
	final boolean evaluateExpression;
	private final IAgent hostAgent;
	private final IType expectedType;
	MouseTrackListener tooltipListener = new MouseTrackAdapter() {

		@Override
		public void mouseExit(final MouseEvent arg0) {
			removeTooltip();
		}
	};

	public ExpressionControl(final Composite comp, final ExpressionBasedEditor ed, final IAgent agent,
		final IType expectedType, final int controlStyle, final boolean evaluate) {
		editor = ed;
		evaluateExpression = evaluate;
		hostAgent = agent;
		this.expectedType = expectedType;
		text = createTextBox(comp, controlStyle);
		text.addModifyListener(this);
		text.addFocusListener(this);
		text.addSelectionListener(this);
		text.addMouseTrackListener(tooltipListener);
		if ( ed != null ) {
			ed.getLabel().addMouseTrackListener(tooltipListener);
		}
	}

	@Override
	public void modifyText(final ModifyEvent event) {
		if ( editor == null ) { return; }
		if ( editor != null && editor.internalModification ) { return; }
		modifyValue();
		displayTooltip();
	}

	protected void displayTooltip() {
		String s = getPopupText();
		if ( s == null || s.isEmpty() ) {
			removeTooltip();
		} else {
			ITooltipDisplayer displayer = GamaToolbarFactory.findTooltipDisplayer(text);
			if ( displayer != null ) {
				displayer.displayTooltip(s, background);
			}
		}
		if ( editor != null && background != null ) {
			editor.getComposite().setBackground(background.inactive());
		}
	}

	protected void removeTooltip() {
		ITooltipDisplayer displayer = GamaToolbarFactory.findTooltipDisplayer(text);
		if ( displayer != null ) {
			displayer.stopDisplayingTooltips();
		}
		if ( editor != null ) {
			editor.getComposite().setBackground(AbstractEditor.NORMAL_BACKGROUND);
		}

	}

	void modifyNoPopup() {
		if ( editor != null ) {
			editor.internalModification = true;
		}
		currentException = null;
		Object value = computeValue();
		if ( !text.isDisposed() ) {
			text.setText(StringUtils.toGaml(value, false));
		}
		if ( editor != null ) {
			IScope scope = GAMA.obtainNewScope();
			if ( editor.acceptNull && value == null ) {
				editor.modifyValue(null);
			} else {
				editor.modifyValue(editor.getExpectedType().cast(scope, value, false, false));
			}
			GAMA.releaseScope(scope);
			editor.internalModification = false;
			editor.checkButtons();
		}
	}

	@Override
	public void widgetDefaultSelected(final SelectionEvent me) {
		try {
			if ( text == null || text.isDisposed() ) { return; }
			String s = text.getText();
			System.out.println(s);
			modifyValue();
			modifyNoPopup();
		} catch (final RuntimeException e) {
			e.printStackTrace();
		}
	}

	private Object computeValue() {
		try {
			currentException = null;
			IAgent agent = getHostAgent();
			// AD: fix for SWT Issue in Eclipse 4.4
			if ( text == null || text.isDisposed() ) { return null; }
			String s = text.getText();
			// AD: Fix for Issue 1042
			if ( getHostAgent() != null && getHostAgent().getScope().interrupted() &&
				getHostAgent() instanceof SimulationAgent ) {
				agent = getHostAgent().getExperiment();
			}
			if ( NumberEditor.UNDEFINED_LABEL.equals(s) ) {
				setCurrentValue(null);
				// return null;
			} else if ( agent == null ) {
				// return Cast.as(s, expectedType.toClass(), false);
				setCurrentValue(Cast.as(s, expectedType.toClass(), false));
			} else {
				// return evaluateExpression ? GAML.evaluateExpression(s, agent) : GAML.compileExpression(s, agent);
				setCurrentValue(
					evaluateExpression ? GAML.evaluateExpression(s, agent) : GAML.compileExpression(s, agent));
			}
		} catch (final Exception e) {
			currentException = e;
			return null;
		}
		return getCurrentValue();
	}

	public void modifyValue() {
		// final Object oldValue = getCurrentValue();
		Object value = computeValue();
		if ( currentException != null ) {
			// setCurrentValue(oldValue);
			return;
		}
		if ( editor != null ) {
			try {
				IScope scope = GAMA.obtainNewScope();
				editor.modifyValue(evaluateExpression ? expectedType.cast(scope, value, false, false) : value);
				GAMA.releaseScope(scope);
				editor.checkButtons();
			} catch (final GamaRuntimeException e) {
				// setCurrentValue(oldValue);
				currentException = e;
			}
		}
	}

	protected Text createTextBox(final Composite comp, final int controlStyle) {
		return new Text(comp, controlStyle);
	}

	@Override
	public void focusGained(final FocusEvent e) {
		if ( editor != null ) {
			System.out.println("Focus gained:" + editor.getParam().getName());
		}
		// if ( e.widget == null || !e.widget.equals(text) ) { return; }
		// computeValue();
	}

	@Override
	public void focusLost(final FocusEvent e) {
		if ( e.widget == null || !e.widget.equals(text) ) { return; }
		if ( editor != null ) {
			System.out.println("Focus lost:" + editor.getParam().getName());
		}
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
	 * @see msi.gama.gui.swt.controls.IPopupProvider#getPopupText()
	 */
	public String getPopupText() {
		String result = "";
		// if ( getCurrentValue() == null ) {
		final Object value = computeValue();
		// }
		if ( currentException != null ) {
			background = IGamaColors.ERROR;
			result += currentException.getMessage();
		} else {
			if ( GAMA.run(new InScope<Boolean>() {

				@Override
				public Boolean run(final IScope scope) {
					if ( evaluateExpression ) {
						return expectedType.canBeTypeOf(scope, value);
					} else if ( value instanceof IExpression ) {
						return expectedType.isAssignableFrom(((IExpression) value).getType());
					} else {
						return false;
					}
				}
			}) ) {
				background = IGamaColors.OK;
			} else {
				background = IGamaColors.WARNING;
				result += "The current value should be of type " + expectedType.toString();
			}
		}
		return result;
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
	 * @param currentValue the currentValue to set
	 */
	protected void setCurrentValue(final Object currentValue) {
		this.currentValue = currentValue;
	}

}
