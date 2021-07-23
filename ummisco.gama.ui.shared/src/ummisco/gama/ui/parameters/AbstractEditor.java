/*********************************************************************************************
 *
 * 'AbstractEditor.java, in plugin ummisco.gama.ui.shared, is part of the source code of the GAMA modeling and
 * simulation platform. (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 *
 *
 **********************************************************************************************/
package ummisco.gama.ui.parameters;

import static msi.gama.runtime.GAMA.getRuntimeScope;
import static msi.gama.runtime.exceptions.GamaRuntimeException.create;

import java.util.Objects;

import javax.annotation.Nonnull;
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
import ummisco.gama.ui.utils.WorkbenchHelper;

public abstract class AbstractEditor<T>
		implements SelectionListener, ModifyListener, Comparable<AbstractEditor<T>>, IParameterEditor<T> {

	private static int ORDER;
	private final int order = ORDER++;
	private final EditorListener<T> listener;
	private final IAgent agent;
	private final IScope scope;
	protected String name;
	protected final IParameter param;

	// Values
	protected T originalValue, currentValue, minValue, maxValue, stepValue;

	// Properties
	protected boolean noScope = false, acceptNull = true;
	protected /* almost final */ boolean isSubParameter;
	protected volatile boolean internalModification;
	protected IType<?> expectedType = Types.NO_TYPE;

	// UI Components
	protected Composite composite;
	protected EditorsGroup parent;
	protected EditorToolbar editorToolbar;
	protected EditorLabel editorLabel;
	protected EditorControl editorControl;

	public AbstractEditor(final IScope scope, final IParameter variable, final EditorListener<T> l) {
		this(scope, null, variable, l);
	}

	public AbstractEditor(final IScope scope, @Nullable final IAgent a, @Nonnull final IParameter parameter,
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
	public IType<?> getExpectedType() {
		return expectedType;
	}

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

	@SuppressWarnings ("unchecked")
	protected T retrieveValueOfParameter() throws GamaRuntimeException {
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

	}

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

	public EditorLabel getLabel() {
		return editorLabel;
	}

	public Control getEditor() {
		return editorControl.getControl();
	}

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

	EditorLabel createEditorLabel() {
		editorLabel = new EditorLabel(this, parent, name, isSubParameter);
		return editorLabel;
	}

	EditorToolbar createEditorToolbar() {
		editorToolbar = new EditorToolbar(this, parent);
		updateToolbar();
		return editorToolbar;
	}

	EditorControl createEditorControl() {
		boolean isCombo = param != null && param.getAmongValue(getScope()) != null;
		boolean isEditable = param == null /* statement */ || param != null && param.isEditable();
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

	protected T getMinValue() {
		return minValue;
	}

	protected T getMaxValue() {
		return maxValue;
	}

	protected T getStepValue() {
		return stepValue;
	}

	protected EditorListener<?> getListener() {
		return listener;
	}

	protected void setParameterValue(final T val) {
		WorkbenchHelper.asyncRun(() -> {
			try {
				if (listener == null) {
					modifyValueOfParameterWith(val);
				} else {
					listener.valueModified(val);
				}
			} catch (final GamaRuntimeException e) {
				e.addContext("Value of " + name + " cannot be modified");
				GAMA.reportError(getRuntimeScope(), create(e, getRuntimeScope()), false);
				return;
			}
		});
	}

	protected GridData getParameterGridData() {
		final var d = new GridData(SWT.FILL, SWT.CENTER, true, false);
		d.minimumWidth = 100;
		return d;
	}

	protected abstract Control createCustomParameterControl(Composite comp) throws GamaRuntimeException;

	protected abstract void displayParameterValue();

	@Override
	public boolean isValueModified() {
		return isValueDifferentFrom(getOriginalValue());
	}

	public boolean isValueDifferentFrom(final Object newVal) {
		return !Objects.equals(currentValue, newVal);
	}

	@Override
	public void revertToDefaultValue() {
		modifyAndDisplayValue(getOriginalValue());
	}

	@Override
	public IParameter getParam() {
		return param;
	}

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
			WorkbenchHelper.asyncRun(() -> {
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
			GAMA.reportError(GAMA.getRuntimeScope(), e, false);
			return;
		}
	}

	protected final void modifyAndDisplayValue(final T val) {
		modifyValue(val);
		WorkbenchHelper.asyncRun(() -> {
			editorControl.displayParameterValue();
			updateToolbar();
		});

	}

	protected void updateToolbar() {
		editorToolbar.update();
	}

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

	protected T getOriginalValue() {
		return originalValue;
	}

	@Override
	public T getCurrentValue() {
		return currentValue;
	}

	protected void setOriginalValue(final T originalValue) {
		this.originalValue = originalValue;
	}

	protected T applyPlus() {
		return null;
	}

	protected T applyMinus() {
		return null;
	}

	protected T applyRevert() {
		return getOriginalValue();
	}

	protected void applyBrowse() {}

	protected void applyInspect() {}

	protected void applyEdit() {}

	protected void applyChange() {}

	protected void applyDefine() {}

	public void dontUseScope(final boolean dont) {
		this.noScope = dont;
	}

}
