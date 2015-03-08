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

import msi.gama.common.util.StringUtils;
import msi.gama.gui.swt.GamaColors.GamaUIColor;
import msi.gama.gui.swt.*;
import msi.gama.gui.swt.controls.ITooltipDisplayer;
import msi.gama.gui.views.actions.GamaToolbarFactory;
import msi.gama.kernel.simulation.SimulationAgent;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.runtime.*;
import msi.gama.runtime.GAMA.InScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GAML;
import msi.gaml.operators.Cast;
import msi.gaml.types.IType;
import org.eclipse.swt.events.*;
import org.eclipse.swt.widgets.*;

public class ExpressionControl implements /* IPopupProvider, */SelectionListener, ModifyListener, FocusListener {

	private final Text text;
	private final ExpressionBasedEditor editor;
	private GamaUIColor background;
	protected Object currentValue;
	protected Exception currentException;
	final boolean evaluateExpression;
	private final IAgent hostAgent;
	final IType expectedType;
	MouseTrackListener tooltipListener = new MouseTrackAdapter() {

		@Override
		public void mouseExit(final MouseEvent arg0) {
			removeTooltip();
		}
	};

	public ExpressionControl(final Composite comp, final ExpressionBasedEditor ed, final IAgent agent,
		final IType expectedType, final int controlStyle) {
		editor = ed;
		evaluateExpression = ed == null ? false : ed.evaluateExpression();
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
		if ( editor != null && editor.internalModification ) { return; }
		modifyValue();
		displayTooltip();
	}

	void displayTooltip() {
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

	void removeTooltip() {
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
		if ( !text.isDisposed() ) {
			text.setText(StringUtils.toGaml(currentValue, false));
		}
		if ( editor != null ) {
			IScope scope = GAMA.obtainNewScope();
			if ( editor.acceptNull && currentValue == null ) {
				editor.modifyValue(null);
			} else {
				editor.modifyValue(editor.getExpectedType().cast(scope, currentValue, false, false));
			}
			GAMA.releaseScope(scope);
			editor.internalModification = false;
			editor.checkButtons();
		}
	}

	@Override
	public void widgetDefaultSelected(final SelectionEvent me) {
		try {
			modifyValue();
			modifyNoPopup();
		} catch (final RuntimeException e) {
			e.printStackTrace();
		}
	}

	private void computeValue() {
		try {
			currentException = null;
			IAgent agent = getHostAgent();
			String s = text.getText();
			// AD: Fix for Issue 1042
			if ( getHostAgent() != null && getHostAgent().getScope().interrupted() &&
				getHostAgent() instanceof SimulationAgent ) {
				agent = getHostAgent().getExperiment();
			}
			if ( NumberEditor.UNDEFINED_LABEL.equals(s) ) {
				currentValue = null;
			} else if ( agent == null ) {
				currentValue = Cast.as(s, expectedType.toClass(), false);
			} else {
				currentValue =
					evaluateExpression ? GAML.evaluateExpression(s, agent) : GAML.compileExpression(s, agent);
			}
		} catch (final Exception e) {
			currentException = e;
		}
	}

	public void modifyValue() {
		final Object oldValue = currentValue;
		computeValue();
		if ( currentException != null ) {
			currentValue = oldValue;
			return;
		}
		if ( editor != null ) {
			try {
				IScope scope = GAMA.obtainNewScope();
				editor.modifyValue(expectedType.cast(scope, currentValue, false, false));
				GAMA.releaseScope(scope);
				editor.checkButtons();
			} catch (final GamaRuntimeException e) {
				currentValue = oldValue;
				currentException = e;
			}
		}
	}

	protected Text createTextBox(final Composite comp, final int controlStyle) {
		return new Text(comp, controlStyle);
	}

	@Override
	public void focusGained(final FocusEvent e) {
		computeValue();
	}

	@Override
	public void focusLost(final FocusEvent e) {
		/* async is needed to wait until focus reaches its new Control */
		removeTooltip();
		SwtGui.getDisplay().asyncExec(new Runnable() {

			@Override
			public void run() {
				if ( SwtGui.getDisplay().isDisposed() ) { return; }
				final Control control = SwtGui.getDisplay().getFocusControl();
				if ( control != text ) {
					widgetDefaultSelected(null);
				}
			}
		});

	}

	public Text getControl() {
		return text;
	}

	@Override
	public void widgetSelected(final SelectionEvent e) {}

	public void setFocus() {
		text.setFocus();
		// displayTooltip();
	}

	/**
	 * @see msi.gama.gui.swt.controls.IPopupProvider#getPopupText()
	 */
	public String getPopupText() {
		String result = "";
		if ( currentValue == null ) {
			computeValue();
		}
		if ( currentException != null ) {
			background = IGamaColors.ERROR;
			result += currentException.getMessage();
		} else {
			if ( GAMA.run(new InScope<Boolean>() {

				@Override
				public Boolean run(final IScope scope) {
					return expectedType.canBeTypeOf(scope, currentValue);
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

}
