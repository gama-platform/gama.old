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

import static msi.gama.common.util.StringUtils.toGaml;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import com.google.common.primitives.Ints;

import msi.gama.application.workbench.ThemeHelper;
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
import ummisco.gama.ui.resources.IGamaColors;
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
	List<T> possibleValues = null;
	protected T originalValue, currentValue, minValue, maxValue, stepValue;

	// Properties
	protected boolean noScope = false, acceptNull = true;
	protected final boolean isCombo;
	protected /* almost final */ boolean isSubParameter;
	protected final boolean isEditable;
	protected volatile boolean internalModification;

	// UI Components
	protected Combo combo;
	protected EditorLabel titleLabel;
	private CLabel fixedValue;
	protected Composite composite, parent;
	final EditorToolbar toolbar;

	public AbstractEditor(final IScope scope, final IParameter variable) {
		this(scope, null, variable, null);
	}

	public AbstractEditor(final IScope scope, final IParameter variable, final EditorListener<T> l) {
		this(scope, null, variable, l);
	}

	public AbstractEditor(final IScope scope, final IAgent a, final IParameter variable) {
		this(scope, a, variable, null);
	}

	protected void computeStepValue() {
		stepValue = (T) param.getStepValue(getScope());
	}

	@Override
	public IScope getScope() {
		if (noScope) return null;
		if (scope != null) return scope;
		if (agent != null) return agent.getScope();
		return GAMA.getRuntimeScope();
	}

	public AbstractEditor(final IScope scope, final IAgent a, final IParameter variable, final EditorListener<T> l) {
		this.scope = scope;
		param = variable;
		agent = a;
		if (param != null) {
			isCombo = param.getAmongValue(getScope()) != null;
			isEditable = param.isEditable();
			name = param.getTitle();
			minValue = (T) param.getMinValue(getScope());
			maxValue = (T) param.getMaxValue(getScope());
			computeStepValue();
		} else {
			isCombo = false;
			isEditable = true;
			name = "";
		}
		listener = l;
		toolbar = new EditorToolbar(this);
	}

	@Override
	public void isSubParameter(final boolean b) {
		isSubParameter = b;
	}

	protected abstract int[] getToolItems();

	@Override
	public void setActive(final Boolean active) {
		if (titleLabel != null) {
			if (active) {
				titleLabel.setActive();
			} else {
				titleLabel.setInactive();
			}
		}
		toolbar.setActive(active);
		if (active) { updateToolbar(); }
		this.getEditor().setEnabled(active);
	}

	private final void valueModified(final Object newValue) throws GamaRuntimeException {

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
	public IType<?> getExpectedType() {
		return Types.NO_TYPE;
	}

	// In case the editor allows to edit the expression, should it be evaluated
	// ?
	protected boolean evaluateExpression() {
		return true;
	}

	@Override
	public int compareTo(final AbstractEditor<T> e) {
		return Ints.compare(order, e.order);
	}

	public EditorLabel getLabel() {
		return titleLabel;
	}

	public Control getEditor() {
		return !isEditable ? fixedValue : isCombo ? combo : getEditorControl();
	}

	protected abstract Control getEditorControl();

	protected Control createEditorControl(final Composite comp) {
		Control paramControl;
		try {
			paramControl = !isEditable ? createLabelParameterControl(comp)
					: isCombo ? createComboParameterControl(comp) : createCustomParameterControl(comp);
		} catch (final GamaRuntimeException e1) {
			e1.addContext("The editor for " + name + " could not be created");
			GAMA.reportError(GAMA.getRuntimeScope(), e1, false);
			return null;
		}

		final var data = getParameterGridData();
		paramControl.setLayoutData(data);
		paramControl.setBackground(getNormalBackground());
		return paramControl;
	}

	public void createComposite(final Composite comp) {
		// Necessary to force SWT to "reskin" and give the right background to the composite (issue in the CSS engine)
		comp.computeSize(SWT.DEFAULT, SWT.DEFAULT);
		try {
			setOriginalValue(getParameterValue());
			currentValue = getOriginalValue();
		} catch (final GamaRuntimeException e1) {
			e1.addContext("Impossible to obtain the value of " + name);
			GAMA.reportError(GAMA.getRuntimeScope(), e1, false);
		}
		parent = comp;
		internalModification = true;
		titleLabel = new EditorLabel(comp, name, computeLabelTooltip(), isSubParameter);
		composite = new Composite(comp, SWT.NONE);
		composite.setBackground(getNormalBackground());
		final var data = new GridData(SWT.FILL, SWT.CENTER, true, false);
		composite.setLayoutData(data);
		final var layout = new GridLayout(2, false);
		layout.marginWidth = 5;
		composite.setLayout(layout);
		createEditorControl(composite);
		toolbar.createOn(composite);
		if (isEditable && !isCombo) { displayParameterValueAndCheckButtons(); }
		internalModification = false;
		comp.layout();
		// composite.layout();
	}

	protected String computeLabelTooltip() {
		boolean isBatch = GAMA.getExperiment() != null && GAMA.getExperiment().isBatch();
		boolean isExperiment = param.isDefinedInExperiment();
		StringBuilder s = new StringBuilder();
		if (isEditable) { s.append("Parameter of type ").append(typeToDisplay()).append(" that represents the "); }
		s.append("value of " + (isExperiment ? "experiment" : "model") + " attribute " + param.getName());
		if (!isBatch) {
			if (getMinValue() != null) {
				final var min = StringUtils.toGaml(getMinValue(), false);
				if (maxValue != null) {
					s.append(" [").append(min).append("..").append(toGaml(maxValue, false)).append("]");
				} else {
					s.append(">= ").append(min);
				}
			} else if (maxValue != null) { s.append("<=").append(toGaml(maxValue, false)); }
			if ((minValue != null || maxValue != null) && stepValue != null) { s.append(" every ").append(stepValue); }
		} else {
			final var u = param.getUnitLabel(getScope());
			if (u != null) { s.append(" ").append(u); }
		}
		return s.toString();
	}

	protected T getMinValue() {
		return minValue;
	}

	protected T getMaxValue() {
		return maxValue;
	}

	protected String typeToDisplay() {
		if (!this.isEditable) return "";
		return param.getType().serialize(false);
	}

	@SuppressWarnings ("unchecked")
	protected T getParameterValue() throws GamaRuntimeException {
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

	protected EditorListener<?> getListener() {
		return listener;
	}

	protected void setParameterValue(final T val) {
		WorkbenchHelper.asyncRun(() -> {
			try {
				if (listener == null) {
					valueModified(val);
				} else {
					listener.valueModified(val);
				}
			} catch (final GamaRuntimeException e) {
				e.printStackTrace();
				e.addContext("Value of " + name + " cannot be modified");
				GAMA.reportError(GAMA.getRuntimeScope(), GamaRuntimeException.create(e, GAMA.getRuntimeScope()), false);
				return;
			}
		});
	}

	protected GridData getParameterGridData() {
		final var d = new GridData(SWT.FILL, SWT.TOP, true, false);
		d.minimumWidth = 100;
		return d;
	}

	protected abstract Control createCustomParameterControl(Composite comp) throws GamaRuntimeException;

	protected Control createLabelParameterControl(final Composite comp) {
		fixedValue = new CLabel(comp, SWT.READ_ONLY | SWT.BORDER_SOLID);
		fixedValue
				.setForeground(ThemeHelper.isDark() ? IGamaColors.VERY_LIGHT_GRAY.color() : IGamaColors.BLACK.color());
		// force text color, see #2601
		fixedValue.setText(
				getOriginalValue() instanceof String ? (String) getOriginalValue() : toGaml(getOriginalValue(), false));
		return fixedValue;
	}

	protected Control createComboParameterControl(final Composite comp) {
		possibleValues = new ArrayList<T>(param.getAmongValue(getScope()));
		final var valuesAsString = new String[possibleValues.size()];
		for (var i = 0; i < possibleValues.size(); i++) {
			if (getExpectedType() == Types.STRING) {
				valuesAsString[i] = StringUtils.toJavaString(toGaml(possibleValues.get(i), false));
			} else {
				valuesAsString[i] = toGaml(possibleValues.get(i), false);
				// }
			}
		}
		combo = new Combo(comp, SWT.READ_ONLY | SWT.DROP_DOWN);
		combo.setForeground(ThemeHelper.isDark() ? IGamaColors.VERY_LIGHT_GRAY.color() : IGamaColors.BLACK.color());
		// force text color, see #2601
		combo.setItems(valuesAsString);
		combo.select(possibleValues.indexOf(getOriginalValue()));
		combo.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent me) {
				modifyValue(possibleValues.get(combo.getSelectionIndex()));
			}
		});

		final var d = new GridData(SWT.FILL, SWT.CENTER, true, false);
		//
		d.minimumWidth = 48;
		combo.setLayoutData(d);
		combo.pack();
		return combo;
	}

	protected abstract void displayParameterValue();

	@Override
	public boolean isValueModified() {
		return isValueDifferent(getOriginalValue());
	}

	public boolean isValueDifferent(final Object newVal) {
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
		if (!isValueDifferent(val)) return true;
		currentValue = (T) val;
		if (isValueModified()) {
			getLabel().signalChanged();
		} else {
			getLabel().cancelChanged();
		}
		if (!internalModification) { setParameterValue(currentValue); }
		return true;
	}

	protected Color getNormalBackground() {
		return parent.getBackground();
	}

	@Override
	public void updateValue(final boolean force) {
		try {
			final var newVal = getParameterValue();
			if (!force && !isValueDifferent(newVal)) return;
			internalModification = true;
			modifyAndDisplayValue(newVal);
			internalModification = false;
		} catch (final GamaRuntimeException e) {
			e.addContext("Unable to obtain the value of " + name);
			GAMA.reportError(GAMA.getRuntimeScope(), e, false);
			return;
		}
	}

	@Override
	public void forceUpdateValueAsynchronously() {
		final var newVal = getParameterValue();
		currentValue = newVal;
		WorkbenchHelper.asyncRun(() -> {
			internalModification = true;
			if (isValueModified()) {
				titleLabel.signalChanged();
			} else {
				titleLabel.cancelChanged();
			}
			if (!parent.isDisposed()) {
				if (!isEditable) {
					fixedValue.setText(newVal instanceof String ? (String) newVal : toGaml(newVal, false));
				} else if (isCombo) {
					combo.select(possibleValues.indexOf(newVal));
				} else {
					displayParameterValue();
					updateToolbar();
				}
				composite.update();
				internalModification = false;
			}
		});

	}

	private void displayParameterValueAndCheckButtons() {
		WorkbenchHelper.run(() -> {
			displayParameterValue();
			updateToolbar();
		});

	}

	protected final void modifyAndDisplayValue(final T val) {
		if (modifyValue(val)) {
			WorkbenchHelper.asyncRun(() -> {
				if (!isEditable) {
					if (!fixedValue.isDisposed()) {
						fixedValue.setText(val instanceof String ? (String) val : StringUtils.toGaml(val, false));
					}
				} else if (isCombo) {
					if (!combo.isDisposed()) { combo.select(possibleValues.indexOf(val)); }
				} else {
					displayParameterValueAndCheckButtons();
				}
				if (!composite.isDisposed()) { composite.update(); }
			});
		} else {
			WorkbenchHelper.asyncRun(() -> {
				displayParameterValue();
				updateToolbar();
			});
		}

	}

	protected void updateToolbar() {
		toolbar.update();
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

	public Composite getComposite() {
		return composite;
	}

	@Override
	public T getCurrentValue() {
		return currentValue;
	}

	public void dontUseScope(final boolean dont) {
		this.noScope = dont;

	}

}
