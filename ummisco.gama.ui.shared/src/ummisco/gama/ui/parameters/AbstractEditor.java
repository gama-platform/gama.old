/*******************************************************************************************************
 *
 * AbstractEditor.java, in ummisco.gama.ui.shared, is part of the source code of the GAMA modeling and simulation
 * platform (v.2.0.0).
 *
 * (c) 2007-2021 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
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

import javax.annotation.Nullable;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
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

/**
 * The Class AbstractEditor.
 *
 * @param <T>
 *            the generic type
 */
public abstract class AbstractEditor<T>
		implements SelectionListener, ModifyListener, Comparable<AbstractEditor<T>>, IParameterEditor<T> {

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
	@Nullable protected final IParameter param;

	/** The step value. */
	// Values
	protected T originalValue, currentValue, minValue, maxValue, stepValue;

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
	 * @param l
	 *            the l
	 */
	public AbstractEditor(final IScope scope, final EditorListener<T> l) {
		this(scope, null, l);
	}

	/**
	 * Instantiates a new abstract editor.
	 *
	 * @param scope
	 *            the scope
	 * @param variable
	 *            the variable
	 * @param l
	 *            the l
	 */
	public AbstractEditor(final IScope scope, final IParameter variable, final EditorListener<T> l) {
		this(scope, null, variable, l);
	}

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
	public AbstractEditor(final IScope scope, @Nullable final IAgent a, @Nullable final IParameter parameter,
			@Nullable final EditorListener<T> l) {
		this.scope = scope;
		param = parameter;
		agent = a;
		if (param != null) {
			name = param.getTitle();
			expectedType = param.getType();
			minValue = (T) param.getMinValue(getScope());
			maxValue = (T) param.getMaxValue(getScope());
			stepValue = (T) param.getStepValue(getScope());
		}
		if (stepValue == null) { stepValue = defaultStepValue(); }
		listener = l;
		try {
			currentValue = originalValue = retrieveValueOfParameter();
		} catch (final GamaRuntimeException e1) {
			e1.addContext("Impossible to obtain the value of " + name);
			GAMA.reportError(GAMA.getRuntimeScope(), e1, false);
		}
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
		if (active) { updateToolbar(); }
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
	protected T retrieveValueOfParameter() throws GamaRuntimeException {
		try {
			if (param == null) return null;
			Object result;
			if (agent == null || !agent.getSpecies().hasVar(param.getName())) {
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

	@Override
	public int compareTo(final AbstractEditor<T> e) {
		return Ints.compare(order, e.order);
	}

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
		parent.layout();
	}

	/**
	 * Creates the value composite.
	 *
	 * @return the composite
	 */
	Composite createValueComposite() {
		composite = new Composite(parent, SWT.NONE);
		composite.setBackground(parent.getBackground());
		final var data = new GridData(SWT.FILL, SWT.CENTER, true, false);
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
	 * Creates the editor toolbar.
	 *
	 * @return the editor toolbar
	 */
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
		boolean isCombo = param != null && param.getAmongValue(getScope()) != null;
		boolean isEditable = param != null && param.isEditable() || param == null /* statement */;
		if (isEditable) {
			if (isCombo) {
				editorControl =
						new ComboEditorControl(this, composite, getExpectedType(), param.getAmongValue(getScope()));
			} else {
				editorControl = new EditorControl(this, createCustomParameterControl(composite));
			}
		} else {
			editorControl = new FixedValueEditorControl(this, composite);
		}
		editorControl.displayParameterValue();
		// displayParameterValue();
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
	protected GridData getParameterGridData() {
		final var d = new GridData(SWT.FILL, SWT.CENTER, true, false);
		d.minimumWidth = 100;
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
	public void updateWithValueOfParameter() {
		try {
			final var newVal = retrieveValueOfParameter();
			currentValue = newVal;
			asyncRun(() -> {
				internalModification = true;
				if (!parent.isDisposed()) {
					editorLabel.signalChanged(isValueModified());
					editorControl.displayParameterValue();
					updateToolbar();
					composite.update();
				}
				internalModification = false;
			});

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
	 * Update toolbar.
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

}
