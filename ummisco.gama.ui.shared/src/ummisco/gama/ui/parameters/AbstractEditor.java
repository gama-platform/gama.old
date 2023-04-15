/*******************************************************************************************************
 *
 * AbstractEditor.java, in ummisco.gama.ui.shared, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.9.1).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gama.ui.parameters;

import static msi.gama.runtime.GAMA.getRuntimeScope;
import static msi.gama.runtime.GAMA.reportError;
import static msi.gama.runtime.exceptions.GamaRuntimeException.create;
import static ummisco.gama.ui.utils.WorkbenchHelper.asyncRun;

import java.util.Objects;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import com.google.common.primitives.Ints;

import msi.gama.common.util.StringUtils;
import msi.gama.kernel.experiment.ExperimentParameter;
import msi.gama.kernel.experiment.IParameter;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.types.GamaStringType;
import msi.gaml.types.IType;
import msi.gaml.types.Types;
import msi.gaml.variables.Variable;
import ummisco.gama.ui.interfaces.EditorListener;
import ummisco.gama.ui.interfaces.IParameterEditor;
import ummisco.gama.ui.resources.GamaColors;
import ummisco.gama.ui.utils.WorkbenchHelper;

/**
 * The Class AbstractEditor.
 *
 * @param <T>
 *            the generic type
 */
public abstract class AbstractEditor<T> implements SelectionListener, ModifyListener, IParameterEditor<T> {

	/** The order. */
	private static int ORDER;

	/** The order. */
	private final int order = ORDER++;

	/** The listener. */
	@Nullable private final EditorListener<T> listener;

	/** The agent. */
	@Nullable private final IAgent agent;

	/** The scope. */
	private final IScope scope;

	/** The name. */
	protected String name;

	/** The param. */
	@Nonnull protected final IParameter param;

	/** The different values. */
	// Values
	protected T originalValue, currentValue;

	/** The max, min and step values. */
	protected T minValue, stepValue, maxValue;

	/** The accept null. */
	// Properties
	protected boolean noScope = false, acceptNull = true;

	/** The is sub parameter. */
	protected /* almost final */ boolean isSubParameter;

	/** The internal modification. */
	protected volatile boolean internalModification;

	/** The expected type. */
	protected IType<?> expectedType = Types.NO_TYPE;

	/** The composite. */
	// UI Components
	protected Composite composite;

	/** The parent. */
	protected EditorsGroup parent;

	/** The editor toolbar. */
	protected EditorToolbar editorToolbar;

	/** The editor label. */
	protected EditorLabel editorLabel;

	/** The editor control. */
	protected EditorControl editorControl;

	/**
	 * Instantiates a new abstract editor.
	 *
	 * @param scope
	 *            the scope
	 * @param a
	 *            the a
	 * @param parameter
	 *            the parameter
	 * @param l
	 *            the l
	 */
	@SuppressWarnings ("unchecked")
	public AbstractEditor(final IScope scope, @Nullable final IAgent a, @Nonnull final IParameter parameter,
			@Nullable final EditorListener<T> l) {
		this.scope = scope;
		param = parameter;
		agent = a;
		name = param.getTitle();
		expectedType = param.getType();
		computeMaxMinAndStepValues();
		listener = l;
		try {
			currentValue = originalValue = retrieveValueOfParameter(false);
		} catch (final GamaRuntimeException e1) {
			e1.addContext("Impossible to obtain the value of " + name);
			GAMA.reportError(GAMA.getRuntimeScope(), e1, false);
		}
	}

	/**
	 * Compute max min and step values.
	 */
	protected void computeMaxMinAndStepValues() {
		Object o = param.getMinValue(getScope());
		if (o != null) { minValue = castValueToInnerType(o); }
		o = param.getMaxValue(getScope());
		if (o != null) { maxValue = castValueToInnerType(o); }
		o = param.getStepValue(getScope());
		if (o != null) { stepValue = castValueToInnerType(o); }
		if (getStepValue() == null) { stepValue = defaultStepValue(); }
	}

	/**
	 * Cast value to inner type.
	 *
	 * @param v
	 *            the v
	 * @return the t
	 */
	@SuppressWarnings ("unchecked")
	protected T castValueToInnerType(final Object v) {
		return (T) getExpectedType().cast(getScope(), v, null, false);
	}

	@Override
	public void isSubParameter(final boolean b) {
		isSubParameter = b;
	}

	/**
	 * Gets the tool items.
	 *
	 * @return the tool items
	 */
	protected abstract int[] getToolItems();

	/**
	 * Returns null by default as only some types can define a "step" value
	 *
	 * @return null
	 */
	protected T defaultStepValue() {
		return null;
	}

	@Override
	public IType<?> getExpectedType() { return expectedType; }

	@Override
	public IScope getScope() {
		if (noScope) return null;
		if (scope != null) return scope;
		if (agent != null) return agent.getScope();
		return GAMA.getRuntimeScope();
	}

	@Override
	public void setActive(final Boolean active) {
		if (editorLabel != null) { editorLabel.setActive(active); }
		editorToolbar.setActive(active);
		editorControl.setActive(active);
	}

	/**
	 * Retrieve value of parameter.
	 *
	 * @return the t
	 * @throws GamaRuntimeException
	 *             the gama runtime exception
	 */
	@SuppressWarnings ("unchecked")
	protected T retrieveValueOfParameter(final boolean retrieveVarValue) throws GamaRuntimeException {
		try {
			Object result;
			if (scope != null && agent == null && retrieveVarValue) {
				// We are in a case where this is an experiment/simulation parameter and we want to retrieve the "deep"
				// value of it
				result = scope.getAgentVarValue(getAgent(), param.getName());
			} else if (agent == null || !agent.getSpecies().hasVar(param.getName())) {
				result = param.value(scope);
			} else {
				result = scope.getAgentVarValue(getAgent(), param.getName());
			}
			if (getExpectedType() == Types.STRING)
				return (T) StringUtils.toJavaString(GamaStringType.staticCast(scope, result, false));
			return (T) getExpectedType().cast(scope, result, null, false);
		} catch (Exception e) {
			throw create(e, scope);
		}

	}

	/**
	 * Modify value of parameter with.
	 *
	 * @param newValue
	 *            the new value
	 * @throws GamaRuntimeException
	 *             the gama runtime exception
	 */
	private final void modifyValueOfParameterWith(final Object newValue) throws GamaRuntimeException {
		var a = agent;
		if (param instanceof ExperimentParameter) {
			if (a == null) {
				final var exp = GAMA.getExperiment();
				if (exp != null) { a = exp.getAgent(); }
			}
			if (a != null && GAMA.getExperiment() != null && GAMA.getExperiment().getAgent() != null) {
				GAMA.getExperiment().getAgent().getScope().setAgentVarValue(a, param.getName(), newValue);
			}
			// Introduced to deal with #2306
			if (agent == null) { param.setValue(a == null ? null : a.getScope(), newValue); }
		} else if (a == null) {
			param.setValue(null, newValue);
		} else if (param instanceof Variable) {
			((Variable) param).setVal(scope, a, newValue);
		} else {
			param.setValue(a.getScope(), newValue);
		}
	}

	/**
	 * Compare to.
	 *
	 * @param e
	 *            the e
	 * @return the int
	 */
	@Override
	public int compareTo(final IParameterEditor<T> e) {
		return Ints.compare(order, e.getOrder());
	}

	@Override
	public int getOrder() { return order; }

	/**
	 * Gets the label.
	 *
	 * @return the label
	 */
	public EditorLabel getLabel() { return editorLabel; }

	/**
	 * Gets the editor.
	 *
	 * @return the editor
	 */
	public Control getEditor() { return editorControl.getControl(); }

	/**
	 * Creates the controls.
	 *
	 * @param parent
	 *            the parent
	 */
	@Override
	public void createControls(final EditorsGroup parent) {
		this.parent = parent;
		internalModification = true;
		// Create the label of the value editor
		editorLabel = createEditorLabel();
		// Create the composite that will hold the value editor and the toolbar
		composite = createValueComposite();
		// Create and initialize the value editor
		editorControl = createEditorControl();
		// Create and initialize the toolbar associated with the value editor
		editorToolbar = createEditorToolbar();
		internalModification = false;
		parent.requestLayout();
	}

	/**
	 * Creates the value composite.
	 *
	 * @return the composite
	 */
	Composite createValueComposite() {
		composite = new Composite(parent, SWT.NONE);
		GamaColors.setBackground(parent.getBackground(), composite);
		final var data = new GridData(SWT.FILL, SWT.CENTER, true, false);
		data.minimumWidth = 100;
		composite.setLayoutData(data);
		// Important to keep two columns as AbstractStatementEditor relies on it
		final var layout = new GridLayout(2, false);
		composite.setLayout(layout);
		return composite;
	}

	/**
	 * Creates the editor label.
	 *
	 * @return the editor label
	 */
	EditorLabel createEditorLabel() {
		editorLabel = new EditorLabel(this, parent, name, isSubParameter);
		return editorLabel;
	}

	/**
	 * Gets the editor control background.
	 *
	 * @return the editor control background
	 */
	Color getEditorControlBackground() {
		return parent.getBackground(); // by default
	}

	/**
	 * Gets the editor control foreground.
	 *
	 * @return the editor control foreground
	 */
	Color getEditorControlForeground() {
		return GamaColors.getTextColorForBackground(getEditorControlBackground()).color(); // by default
	}

	/**
	 * Creates the editor toolbar.
	 *
	 * @return the editor toolbar
	 */
	@SuppressWarnings ("unchecked")
	EditorToolbar createEditorToolbar() {
		editorToolbar = new EditorToolbar(this, parent);
		updateToolbar();
		return editorToolbar;
	}

	/**
	 * Creates the editor control.
	 *
	 * @return the editor control
	 */
	EditorControl createEditorControl() {
		boolean isCombo = param.getAmongValue(getScope()) != null;
		boolean isEditable = param.isEditable();
		if (isEditable) {
			if (isCombo) {
				editorControl =
						new ComboEditorControl(this, composite, getExpectedType(), param.getAmongValue(getScope()));
			} else {
				editorControl = new EditorControl<>(this, createCustomParameterControl(composite));
			}
		} else {
			editorControl = new FixedValueEditorControl(this, composite);
		}
		editorControl.displayParameterValue();
		return editorControl;
	}

	/**
	 * Gets the min value.
	 *
	 * @return the min value
	 */
	protected T getMinValue() { return minValue; }

	/**
	 * Gets the max value.
	 *
	 * @return the max value
	 */
	protected T getMaxValue() { return maxValue; }

	/**
	 * Gets the step value.
	 *
	 * @return the step value
	 */
	protected T getStepValue() { return stepValue; }

	/**
	 * Gets the listener.
	 *
	 * @return the listener
	 */
	protected EditorListener<?> getListener() { return listener; }

	/**
	 * Sets the parameter value.
	 *
	 * @param val
	 *            the new parameter value
	 */
	protected void setParameterValue(final T val) {
		asyncRun(() -> {
			try {
				if (listener == null) {
					modifyValueOfParameterWith(val);
				} else {
					listener.valueModified(val);
				}
			} catch (final Exception e) {
				GamaRuntimeException ex = create(e, scope);
				ex.addContext("Value of " + name + " cannot be modified");
				GAMA.reportError(scope, ex, false);
				return;
			}
		});
	}

	/**
	 * Gets the parameter grid data.
	 *
	 * @return the parameter grid data
	 */
	protected GridData getEditorControlGridData() {
		final var d = new GridData(SWT.FILL, SWT.CENTER, true, false);
		d.minimumWidth = 50;
		return d;
	}

	/**
	 * Creates the custom parameter control.
	 *
	 * @param comp
	 *            the comp
	 * @return the control
	 * @throws GamaRuntimeException
	 *             the gama runtime exception
	 */
	protected abstract Control createCustomParameterControl(Composite comp) throws GamaRuntimeException;

	/**
	 * Display parameter value.
	 */
	protected abstract void displayParameterValue();

	@Override
	public boolean isValueModified() { return isValueDifferentFrom(getOriginalValue()); }

	/**
	 * Checks if is value different from.
	 *
	 * @param newVal
	 *            the new val
	 * @return true, if is value different from
	 */
	public boolean isValueDifferentFrom(final Object newVal) {
		return !Objects.equals(currentValue, newVal);
	}

	@Override
	public void revertToDefaultValue() {
		modifyAndDisplayValue(getOriginalValue());
	}

	@Override
	public IParameter getParam() { return param; }

	/**
	 * Modify value.
	 *
	 * @param val
	 *            the val
	 * @return true, if successful
	 * @throws GamaRuntimeException
	 *             the gama runtime exception
	 */
	@SuppressWarnings ("unchecked")
	// Passes Object on purpose so that Float and Int editors can cast it.
	// Returns whether or not the modification is **accepted**
	protected boolean modifyValue(final Object val) throws GamaRuntimeException {
		if (!isValueDifferentFrom(val)) return true;
		currentValue = (T) val;
		editorLabel.signalChanged(isValueModified());
		if (!internalModification) { setParameterValue(currentValue); }
		return true;
	}

	@Override
	public void updateWithValueOfParameter(final boolean synchronously, final boolean retrieveVarValue) {
		try {
			final var newVal = retrieveValueOfParameter(retrieveVarValue);
			currentValue = newVal;
			Runnable run = () -> {
				internalModification = true;
				if (!parent.isDisposed()) {
					editorControl.updateAmongValues(param.getAmongValue(getScope()));
					computeMaxMinAndStepValues();
					editorLabel.signalChanged(isValueModified());
					editorControl.displayParameterValue();
					updateToolbar();
					composite.update();
				}
				internalModification = false;
			};
			if (synchronously) {
				WorkbenchHelper.run(run);
			} else {
				WorkbenchHelper.asyncRun(run);
			}

		} catch (final GamaRuntimeException e) {
			e.addContext("Unable to obtain the value of " + name);
			reportError(getRuntimeScope(), e, false);
			return;
		}
	}

	/**
	 * Modify and display value.
	 *
	 * @param val
	 *            the val
	 */
	protected final void modifyAndDisplayValue(final T val) {
		modifyValue(val);
		asyncRun(() -> {
			editorControl.displayParameterValue();
			updateToolbar();
		});

	}

	/**
	 * Update toolbar. Redefined in subclasses
	 */
	protected void updateToolbar() {
		editorToolbar.update();
	}

	/**
	 * Gets the agent.
	 *
	 * @return the agent
	 */
	protected IAgent getAgent() {
		if (agent != null) return agent;
		if (scope == null) return null;
		return scope.getSimulation();

	}

	@Override
	public void modifyText(final ModifyEvent e) {}

	@Override
	public void widgetSelected(final SelectionEvent e) {}

	@Override
	public void widgetDefaultSelected(final SelectionEvent e) {}

	/**
	 * Gets the original value.
	 *
	 * @return the original value
	 */
	protected T getOriginalValue() { return originalValue; }

	@Override
	public T getCurrentValue() { return currentValue; }

	/**
	 * Sets the original value.
	 *
	 * @param originalValue
	 *            the new original value
	 */
	protected void setOriginalValue(final T originalValue) { this.originalValue = originalValue; }

	/**
	 * Apply plus.
	 *
	 * @return the t
	 */
	protected T applyPlus() {
		return null;
	}

	/**
	 * Apply minus.
	 *
	 * @return the t
	 */
	protected T applyMinus() {
		return null;
	}

	/**
	 * Apply revert.
	 *
	 * @return the t
	 */
	protected T applyRevert() {
		return getOriginalValue();
	}

	/**
	 * Apply browse.
	 */
	protected void applyBrowse() {}

	/**
	 * Apply inspect.
	 */
	protected void applyInspect() {}

	/**
	 * Apply edit.
	 */
	protected void applyEdit() {}

	/**
	 * Apply change.
	 */
	protected void applyChange() {}

	/**
	 * Apply define.
	 */
	protected void applyDefine() {}

	/**
	 * Dont use scope.
	 *
	 * @param dont
	 *            the dont
	 */
	public void dontUseScope(final boolean dont) {
		this.noScope = dont;
	}

	/**
	 * Apply save.
	 */
	protected void applySave() {}

	/**
	 * Dispose.
	 */
	public void dispose() {
		if (editorLabel != null && !editorLabel.isDisposed()) {
			editorLabel.dispose();
			editorLabel = null;
		}
		if (editorControl != null && !editorControl.getControl().isDisposed()) {
			editorControl.getControl().dispose();
			editorControl = null;
		}
		if (composite != null && !composite.isDisposed()) {
			composite.dispose();
			composite = null;
		}
	}

}
