/*
 * GAMA - V1.4 http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2012
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2012
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen (Batch, GeoTools & JTS), 2009-2012
 * - Beno�t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.gui.parameters;

import msi.gama.common.interfaces.*;
import msi.gama.common.util.*;
import msi.gama.gui.swt.SwtGui;
import msi.gama.kernel.experiment.IParameter;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.runtime.*;
import msi.gama.runtime.GAMA.InScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaList;
import msi.gaml.types.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

public abstract class AbstractEditor implements SelectionListener, ModifyListener, EditorListener,
/* MouseTrackListener, */Comparable<AbstractEditor>, IParameterEditor {

	public static final Color normal_bg = Display.getDefault().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND);
	public static final Color changed_bg = Display.getDefault().getSystemColor(SWT.COLOR_INFO_BACKGROUND);
	private static int ORDER;
	private final int order;
	private final IAgent agent;
	protected String[] stringTab = null;
	private final String name;
	protected Label titleLabel = null;
	private Label unitLabel = null;
	protected final IParameter param;
	boolean acceptNull = true;
	private Object originalValue = null;
	protected Object currentValue = null;
	private GamaList possibleValues = null;
	private final Boolean isCombo, isEditable, hasUnit;
	protected Number minValue;
	protected Number maxValue;
	private Combo combo;
	private CLabel fixedValue;
	protected EditorToolTip tooltip;
	protected volatile boolean internalModification;
	private final EditorListener listener;

	public AbstractEditor(final IParameter variable) {
		this(null, variable, null);
	}

	@Override
	public void valueModified(final Object newValue) throws GamaRuntimeException {
		IAgent a = agent;
		if ( a == null ) {
			a = GAMA.getExperiment().getAgent();
			param.setValue(newValue);
		}
		if ( a != null /* && a.getSpecies().hasVar(param.getName()) */) {
			GAMA.getExperiment().getAgent().getScope().setAgentVarValue(a, param.getName(), newValue);
		}
	}

	@Override
	public IType getExpectedType() {
		return Types.NO_TYPE;
	}

	public AbstractEditor(final IParameter variable, final EditorListener l) {
		this(null, variable, l);
	}

	// In case the editor allows to edit the expression, should it be evaluated ?
	public boolean evaluateExpression() {
		return true;
	}

	public AbstractEditor(final IAgent a, final IParameter variable, final EditorListener l) {
		order = ORDER++;
		param = variable;
		agent = a;
		isCombo = param.getAmongValue() != null;
		isEditable = param.isEditable();
		hasUnit = param.getUnitLabel() != null;
		name = param.getTitle();
		minValue = param.getMinValue();
		maxValue = param.getMaxValue();
		listener = l != null ? l : this;
	}

	public Integer getOrder() {
		return order;
	}

	@Override
	public int compareTo(final AbstractEditor e) {
		return getOrder().compareTo(e.getOrder());
	}

	public Label getLabel() {
		return titleLabel;
	}

	public Control getEditor() {
		return !isEditable ? fixedValue : isCombo ? combo : getEditorControl();
	}

	protected abstract Control getEditorControl();

	public void createComposite(final Composite parent) {
		// TODO Fixer automatiquement le layout du parent. Ou alors utiliser un nouveau composite.

		internalModification = true;
		titleLabel = SwtGui.createLeftLabel(parent, name);
		try {
			setOriginalValue(getParameterValue());
		} catch (final GamaRuntimeException e1) {
			e1.addContext("Impossible to obtain the value of " + name);
			GAMA.reportError(e1, false);
		}
		currentValue = getOriginalValue();
		final Composite comp = new Composite(parent, SWT.NONE);
		comp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		final GridLayout layout = new GridLayout(hasUnit ? 2 : 1, false);
		layout.verticalSpacing = 0;
		layout.marginHeight = 1;
		layout.marginWidth = 1;
		comp.setLayout(layout);
		Control paramControl;
		try {
			paramControl =
				!isEditable ? createLabelParameterControl(comp) : isCombo ? createComboParameterControl(comp)
					: createCustomParameterControl(comp);
		} catch (final GamaRuntimeException e1) {
			e1.addContext("The editor for " + name + " could not be created");
			GAMA.reportError(e1, false);
			return;
		}

		paramControl.setLayoutData(getParameterGridData());
		paramControl.setBackground(normal_bg);
		if ( isEditable && !isCombo ) {
			displayParameterValue();
		}

		if ( hasUnit ) {
			unitLabel = new Label(comp, SWT.READ_ONLY);
			unitLabel.setFont(SwtGui.getUnitFont());
			unitLabel.setLayoutData(getParameterGridData());
			unitLabel.setText(param.getUnitLabel());
			unitLabel.pack();
		}
		internalModification = false;
	}

	protected Object getParameterValue() throws GamaRuntimeException {
		return GAMA.run(new InScope() {

			@Override
			public Object run(final IScope scope) {
				if ( agent == null ) { return param.value(scope); }
				return scope.getAgentVarValue(getAgent(), param.getName());
			}
		});

	}

	protected void setParameterValue(final Object val) {
		if ( listener == null ) { return; }
		GuiUtils.run(new Runnable() {

			@Override
			public void run() {
				try {
					listener.valueModified(val);
				} catch (final GamaRuntimeException e) {
					e.printStackTrace();
					e.addContext("Value of " + name + " cannot be modified");
					GAMA.reportError(GamaRuntimeException.create(e), false);
					return;
				}
			}
		});
	}

	protected GridData getParameterGridData() {
		final GridData d = new GridData(SWT.FILL, SWT.CENTER, true, false);
		d.minimumWidth = 100;
		d.widthHint = 300; // SWT.DEFAULT
		return d;
	}

	protected abstract Control createCustomParameterControl(Composite composite) throws GamaRuntimeException;

	protected Control createLabelParameterControl(final Composite composite) {
		fixedValue = new CLabel(composite, SWT.READ_ONLY | SWT.BORDER_SOLID);
		fixedValue.setText(getOriginalValue() instanceof String ? (String) getOriginalValue() : StringUtils
			.toGaml(getOriginalValue()));
		return fixedValue;
	}

	protected Control createComboParameterControl(final Composite composite) {
		possibleValues = new GamaList(param.getAmongValue());
		final String[] valuesAsString = new String[possibleValues.size()];
		for ( int i = 0; i < possibleValues.size(); i++ ) {
			if ( param.isLabel() ) {
				valuesAsString[i] = possibleValues.get(i).toString();
			} else {
				valuesAsString[i] = StringUtils.toGaml(possibleValues.get(i));
			}
		}
		combo = new Combo(composite, SWT.READ_ONLY | SWT.DROP_DOWN);
		combo.setItems(valuesAsString);
		combo.select(possibleValues.indexOf(getOriginalValue()));
		combo.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(final ModifyEvent me) {
				modifyValue(possibleValues.get(combo.getSelectionIndex()));
			}
		});
		return combo;
	}

	protected abstract void displayParameterValue();

	@Override
	public boolean isValueModified() {
		return isValueDifferent(getOriginalValue());
	}

	@Override
	public boolean isValueDifferent(final Object newVal) {
		return newVal == null ? currentValue != null : !newVal.equals(currentValue);
	}

	@Override
	public void revertToDefaultValue() {
		modifyAndDisplayValue(getOriginalValue());
	}

	@Override
	public IParameter getParam() {
		return param;
	}

	// public final void setTooltip(final String s) {
	// if ( tooltip != null ) {
	// tooltip.setText(s.trim());
	// }
	// }

	protected String getTooltipText() {
		String s = "name: " + param.getName() + "\n" + "type: " + getExpectedType().toString();
		if ( minValue != null || maxValue != null ) {
			s +=
				"\nrange: [" + (minValue != null ? StringUtils.toGaml(minValue) : "?") + ".." +
					(maxValue != null ? StringUtils.toGaml(maxValue) : "?") + "]";
		}
		if ( isValueModified() ) {
			s += "\ninit: " + StringUtils.toGaml(getOriginalValue());
		}
		return s;
	}

	protected void modifyValue(final Object val) throws GamaRuntimeException {
		currentValue = val;
		if ( titleLabel != null ) {
			titleLabel.setBackground(isValueModified() ? changed_bg : normal_bg);
		}
		if ( !internalModification ) {
			setParameterValue(val);
		}
	}

	@Override
	public void updateValue() {
		try {
			final Object newVal = getParameterValue();
			if ( !isValueDifferent(newVal) ) { return; }
			internalModification = true;
			if ( titleLabel != null ) {
				modifyAndDisplayValue(newVal);
			}
			internalModification = false;
		} catch (final GamaRuntimeException e) {
			e.addContext("Unable to obtain the value of " + name);
			GAMA.reportError(e, false);
			return;
		}
	}

	protected final void modifyAndDisplayValue(final Object val) {
		modifyValue(val);
		if ( !isEditable ) {
			fixedValue.setText(val instanceof String ? (String) val : StringUtils.toGaml(val));
		} else if ( isCombo ) {
			combo.select(possibleValues.indexOf(val));
		} else {
			displayParameterValue();
		}
	}

	protected IAgent getAgent() {
		if ( agent != null ) { return agent; }
		return GAMA.run(new InScope<IAgent>() {

			@Override
			public IAgent run(final IScope scope) {
				return scope.getSimulationScope();
			}
		});
	}

	protected boolean acceptTooltip() {
		return param.allowsTooltip();
	}

	@Override
	public void modifyText(final ModifyEvent e) {}

	@Override
	public void widgetSelected(final SelectionEvent e) {}

	@Override
	public void widgetDefaultSelected(final SelectionEvent e) {}

	public Label getUnitLabel() {
		return unitLabel;
	}

	protected Object getOriginalValue() {
		return originalValue;
	}

	protected void setOriginalValue(final Object originalValue) {
		this.originalValue = originalValue;
	}

}
