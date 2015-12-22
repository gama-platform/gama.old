/*********************************************************************************************
 *
 *
 * 'AbstractEditor.java', in plugin 'msi.gama.application', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gama.gui.parameters;

import java.util.*;
import java.util.List;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;
import msi.gama.common.interfaces.*;
import msi.gama.common.util.*;
import msi.gama.gui.swt.*;
import msi.gama.kernel.experiment.*;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.runtime.*;
import msi.gama.runtime.GAMA.InScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.types.*;

public abstract class AbstractEditor<T> implements SelectionListener, ModifyListener, Comparable<AbstractEditor>, IParameterEditor<T> {

	private class ItemSelectionListener extends SelectionAdapter {

		private final int code;

		ItemSelectionListener(final int code) {
			this.code = code;
		}

		@Override
		public void widgetSelected(final SelectionEvent e) {
			switch (code) {
				case REVERT:
					modifyAndDisplayValue(applyRevert());
					break;
				case PLUS:
					modifyAndDisplayValue(applyPlus());
					break;
				case MINUS:
					modifyAndDisplayValue(applyMinus());
					break;
				case EDIT:
					applyEdit();
					break;
				case INSPECT:
					applyInspect();
					break;
				case BROWSE:
					applyBrowse();
					break;
				case CHANGE:
					if ( e.detail != SWT.ARROW ) { return; }
					applyChange();
					break;
				case DEFINE:
					applyDefine();
					break;
			}
		}

	}

	public static final Color NORMAL_BACKGROUND = IGamaColors.PARAMETERS_BACKGROUND.color();
	public static final Color HOVERED_BACKGROUND = IGamaColors.WHITE.darker();
	public static final Color CHANGED_BACKGROUND = IGamaColors.TOOLTIP.color();
	private static int ORDER;
	private final Integer order = ORDER++;
	private final IAgent agent;
	private final String name;
	protected Label titleLabel = null;
	protected final IParameter param;
	boolean acceptNull = true;
	private T originalValue = null;
	protected T currentValue = null;
	private List<T> possibleValues = null;
	private final Boolean isCombo, isEditable;
	protected Number minValue;
	protected Number maxValue;
	private Combo combo;
	private CLabel fixedValue;
	protected volatile boolean internalModification;
	private final EditorListener listener;
	// private final boolean acceptPopup = true;
	private Composite composite;
	// protected ToolItem editor;
	protected final ToolItem[] items = new ToolItem[8];
	boolean isSubParameter;
	Composite parent;
	protected ToolBar toolbar;
	protected Set<Control> controlsThatShowHideToolbars = new HashSet();
	private final MouseTrackListener hideShowToolbarListener = new MouseTrackListener() {

		@Override
		public void mouseEnter(final MouseEvent e) {
			showToolbar();
		}

		@Override
		public void mouseExit(final MouseEvent e) {
			if ( isCombo && combo.getListVisible() ) { return; }
			hideToolbar();
		}

		@Override
		public void mouseHover(final MouseEvent e) {}

	};

	public AbstractEditor(final IParameter variable) {
		this(null, variable, null);
	}

	public AbstractEditor(final IParameter variable, final EditorListener l) {
		this(null, variable, l);
	}

	public AbstractEditor(final IAgent a, final IParameter variable) {
		this(a, variable, null);
	}

	public AbstractEditor(final IAgent a, final IParameter variable, final EditorListener l) {
		param = variable;
		agent = a;
		isCombo = param.getAmongValue() != null;
		isEditable = param.isEditable();
		name = param.getTitle();
		minValue = param.getMinValue();
		maxValue = param.getMaxValue();
		listener = l;
	}

	// public boolean isSubParameter() {
	// return isSubParameter;
	// }

	@Override
	public void isSubParameter(final boolean b) {
		isSubParameter = b;
	}

	protected abstract int[] getToolItems();

	@Override
	public void setActive(final Boolean active) {
		if ( titleLabel != null ) {
			titleLabel.setForeground(active ? SwtGui.getDisplay().getSystemColor(SWT.COLOR_BLACK)
				: SwtGui.getDisplay().getSystemColor(SWT.COLOR_GRAY));
		}
		if ( !active ) {
			for ( ToolItem t : items ) {
				if ( t == null ) {
					continue;
				}
				t.setEnabled(false);
			}
		} else {
			checkButtons();
		}
		this.getEditor().setEnabled(active);
	}

	private final void valueModified(final Object newValue) throws GamaRuntimeException {
		IAgent a = agent;
		if ( a == null ) {
			IExperimentPlan exp = GAMA.getExperiment();
			if ( exp != null ) {
				a = exp.getAgent();
			}
			param.setValue(a == null ? null : a.getScope(), newValue);
		}
		if ( a != null && GAMA.getExperiment() != null && GAMA.getExperiment().getAgent() != null ) {
			GAMA.getExperiment().getAgent().getScope().setAgentVarValue(a, param.getName(), newValue);
		}
	}

	@Override
	public IType getExpectedType() {
		return Types.NO_TYPE;
	}

	// In case the editor allows to edit the expression, should it be evaluated ?
	protected boolean evaluateExpression() {
		return true;
	}

	@Override
	public int compareTo(final AbstractEditor e) {
		return order.compareTo(e.order);
	}

	public Label getLabel() {
		return titleLabel;
	}

	public Control getEditor() {
		return !isEditable ? fixedValue : isCombo ? combo : getEditorControl();
	}

	protected abstract Control getEditorControl();

	protected Control createEditorControl(final Composite composite) {
		Control paramControl;
		try {
			paramControl = !isEditable ? createLabelParameterControl(composite)
				: isCombo ? createComboParameterControl(composite) : createCustomParameterControl(composite);
		} catch (final GamaRuntimeException e1) {
			e1.addContext("The editor for " + name + " could not be created");
			GAMA.reportError(GAMA.getRuntimeScope(), e1, false);
			return null;
		}

		GridData data = getParameterGridData();
		paramControl.setLayoutData(data);
		paramControl.setBackground(composite.getBackground());
		addToolbarHiders(paramControl);
		return paramControl;
	}

	protected Color getNormalBackground() {
		return /* NORMAL_BACKGROUND */parent.getBackground();
	}

	public static Label createLeftLabel(final Composite parent, final String title) {
		final Label label = new Label(parent, SWT.NONE | SWT.WRAP);
		label.setBackground(parent.getBackground());
		GridData d = new GridData(SWT.END, SWT.CENTER, false, true);
		label.setLayoutData(d);
		label.setFont(SwtGui.getLabelfont());
		label.setText(title);
		return label;
	}

	public void createComposite(final Composite parent) {
		this.parent = parent;
		internalModification = true;
		if ( !isSubParameter ) {
			titleLabel = createLeftLabel(parent, name);
		} else {
			Label l = createLeftLabel(parent, " ");
		}
		try {
			setOriginalValue(getParameterValue());
		} catch (final GamaRuntimeException e1) {
			e1.addContext("Impossible to obtain the value of " + name);
			GAMA.reportError(GAMA.getRuntimeScope(), e1, false);
		}
		currentValue = getOriginalValue();
		composite = new Composite(parent, SWT.NONE);
		composite.setBackground(parent.getBackground());
		final GridData data = new GridData(SWT.FILL, SWT.CENTER, true, false);
		data.minimumWidth = 150;
		composite.setLayoutData(data);

		final GridLayout layout = new GridLayout(isSubParameter ? 3 : 2, false);
		// layout.verticalSpacing = 8;
		// layout.marginHeight = 5;
		layout.marginWidth = 5;

		composite.setLayout(layout);
		if ( isSubParameter ) {
			titleLabel = createLeftLabel(composite, name);
			titleLabel.setFont(SwtGui.getNavigFolderFont());
			GridData d = new GridData(SWT.FILL, SWT.CENTER, true, false);
			d.grabExcessHorizontalSpace = false;
			titleLabel.setLayoutData(d);
		}
		createEditorControl(composite);
		toolbar = createToolbar();

		if ( isEditable && !isCombo ) {
			displayParameterValueAndCheckButtons();
		}
		internalModification = false;
		composite.layout();

		addToolbarHiders(composite, toolbar, titleLabel);
		// toolbar.addDisposeListener(new DisposeListener() {
		//
		// @Override
		// public void widgetDisposed(final DisposeEvent e) {
		// System.out.println("Toolbar disposed !");
		// }
		// });
		for ( final Control c : controlsThatShowHideToolbars ) {
			c.addMouseTrackListener(hideShowToolbarListener);
			c.addDisposeListener(new DisposeListener() {

				@Override
				public void widgetDisposed(final DisposeEvent e) {
					c.removeMouseTrackListener(hideShowToolbarListener);
					controlsThatShowHideToolbars.remove(c);
				}
			});
		}
		hideToolbar();
	}

	protected void addToolbarHiders(final Control ... c) {
		controlsThatShowHideToolbars.addAll(Arrays.asList(c));
	}

	protected void hideToolbar() {
		GridData d = (GridData) toolbar.getLayoutData();
		if ( d.exclude ) { return; }
		d.exclude = true;
		toolbar.setVisible(false);
		composite.setBackground(getNormalBackground());
		composite.layout();
	}

	protected void showToolbar() {
		GridData d = (GridData) toolbar.getLayoutData();
		if ( !d.exclude ) { return; }
		d.exclude = false;
		toolbar.setVisible(true);
		composite.setBackground(HOVERED_BACKGROUND);
		composite.layout();

		if ( combo != null ) {
			combo.forceFocus();
		} else {
			Control c = getEditorControl();
			if ( c != null ) {
				c.forceFocus();
			}
		}
	}

	private String computeUnitLabel() {
		String s = typeToDisplay();
		if ( minValue != null ) {
			String min = StringUtils.toGaml(minValue, false);
			if ( maxValue != null ) {
				s += " [" + min + ".." + StringUtils.toGaml(maxValue, false) + "]";
			} else {
				s += ">= " + min;
			}
		} else {
			if ( maxValue != null ) {
				s += "<=" + StringUtils.toGaml(maxValue, false);
			}
		}
		String u = param.getUnitLabel();
		if ( u != null ) {
			s += " " + u;
		}
		return s;
	}

	protected String typeToDisplay() {
		return param.getType().serialize(false);
	}

	private ToolBar createToolbar() {
		ToolBar t = new ToolBar(composite, SWT.FLAT | SWT.RIGHT | SWT.HORIZONTAL | SWT.WRAP);
		GridData d = this.getParameterGridData();
		d.grabExcessHorizontalSpace = false;
		t.setLayoutData(d);
		String unitText = computeUnitLabel();
		if ( !unitText.isEmpty() ) {
			ToolItem unitItem = new ToolItem(t, SWT.READ_ONLY | SWT.FLAT);
			unitItem.setText(unitText);
			unitItem.setEnabled(false);
		}
		int[] codes = this.getToolItems();
		for ( int i : codes ) {
			ToolItem item = null;
			switch (i) {
				case REVERT:
					item = createItem(t, "Revert to original value", GamaIcons.create("small.revert").image());
					break;
				case PLUS:
					item = createPlusItem(t);
					break;
				case MINUS:
					item = createItem(t, "Decrement the parameter", IGamaIcons.SMALL_MINUS.image());
					break;
				case EDIT:
					item = createItem(t, "Edit the parameter", GamaIcons.create("small.edit").image());
					break;
				case INSPECT:
					item = createItem(t, "Inspect the agent", GamaIcons.create("small.inspect").image());
					break;
				case BROWSE:
					item = createItem(t, "Browse the list of agents", GamaIcons.create("small.browse").image());
					break;
				case CHANGE:
					item = createItem(t, "Choose another agent", GamaIcons.create("small.change").image());
					break;
				case DEFINE:
					item = createItem(t, "Set the parameter to undefined", GamaIcons.create("small.undefine").image());
			}
			if ( item != null ) {
				items[i] = item;
				item.addSelectionListener(new ItemSelectionListener(i));

			}
		}
		t.layout();
		t.pack();
		return t;
	}

	protected ToolItem createPlusItem(final ToolBar t) {
		ToolItem item = createItem(t, "Increment the parameter", IGamaIcons.SMALL_PLUS.image());
		return item;
	}

	/**
	 * @param string
	 * @param image
	 */
	private ToolItem createItem(final ToolBar t, final String string, final Image image) {
		ToolItem i = new ToolItem(t, SWT.FLAT | SWT.PUSH);
		i.setToolTipText(string);
		i.setImage(image);
		return i;
	}

	protected T getParameterValue() throws GamaRuntimeException {
		return GAMA.run(new InScope<T>() {

			@Override
			public T run(final IScope scope) {
				Object result;
				if ( agent == null ) {
					result = param.value(scope);
				} else {
					result = scope.getAgentVarValue(getAgent(), param.getName());
				}
				return (T) getExpectedType().cast(scope, result, null, false);
			}
		});

	}

	protected void setParameterValue(final Object val) {
		// if ( listener == null ) { return; }
		GuiUtils.run(new Runnable() {

			@Override
			public void run() {
				try {
					if ( listener == null ) {
						valueModified(val);
					} else {
						listener.valueModified(val);
					}
				} catch (final GamaRuntimeException e) {
					e.printStackTrace();
					e.addContext("Value of " + name + " cannot be modified");
					GAMA.reportError(GAMA.getRuntimeScope(), GamaRuntimeException.create(e), false);
					return;
				}
			}
		});
	}

	protected GridData getParameterGridData() {
		final GridData d = new GridData(SWT.FILL, SWT.TOP, true, false);

		d.minimumWidth = 70;
		// d.widthHint = 100; // SWT.DEFAULT
		return d;
	}

	protected abstract Control createCustomParameterControl(Composite composite) throws GamaRuntimeException;

	protected Control createLabelParameterControl(final Composite composite) {
		fixedValue = new CLabel(composite, SWT.READ_ONLY | SWT.BORDER_SOLID);
		fixedValue.setText(getOriginalValue() instanceof String ? (String) getOriginalValue()
			: StringUtils.toGaml(getOriginalValue(), false));
		// addToolbarHiders(fixedValue);
		return fixedValue;
	}

	protected Control createComboParameterControl(final Composite composite) {
		possibleValues = new ArrayList(param.getAmongValue());
		final String[] valuesAsString = new String[possibleValues.size()];
		for ( int i = 0; i < possibleValues.size(); i++ ) {
			// if ( param.isLabel() ) {
			// valuesAsString[i] = possibleValues.get(i).toString();
			// } else {
			valuesAsString[i] = StringUtils.toGaml(possibleValues.get(i), false);
			// }
		}
		combo = new Combo(composite, SWT.READ_ONLY | SWT.DROP_DOWN);
		combo.setItems(valuesAsString);
		combo.select(possibleValues.indexOf(getOriginalValue()));
		// combo.addModifyListener(new ModifyListener() {
		//
		// @Override
		// public void modifyText(final ModifyEvent me) {
		// modifyValue(possibleValues.get(combo.getSelectionIndex()));
		// }
		// });
		combo.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent me) {
				modifyValue(possibleValues.get(combo.getSelectionIndex()));
			}
		});

		final GridData d = new GridData(SWT.LEFT, SWT.CENTER, false, true);
		d.minimumWidth = 48;
		// d.widthHint = 100; // SWT.DEFAULT
		combo.setLayoutData(d);
		combo.pack();
		return combo;
	}

	protected abstract void displayParameterValue();

	protected void checkButtons() {
		ToolItem revert = items[REVERT];
		if ( revert == null || revert.isDisposed() ) { return; }
		revert.setEnabled(currentValue == null ? originalValue != null : !currentValue.equals(originalValue));
	}

	@Override
	public boolean isValueModified() {
		return isValueDifferent(getOriginalValue());
	}

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

	// protected String getTooltipText() {
	// String s = param.getName() + " (of type " + getExpectedType().serialize(true);
	// if ( isValueModified() ) {
	// s += ", with an initial value of " + StringUtils.toGaml(getOriginalValue(), false);
	// }
	// return s + ")";
	// }

	protected void modifyValue(final T val) throws GamaRuntimeException {
		currentValue = val;
		if ( titleLabel != null && !titleLabel.isDisposed() ) {
			titleLabel
				.setBackground(isValueModified() ? CHANGED_BACKGROUND : IGamaColors.PARAMETERS_BACKGROUND.color());
		}
		if ( !internalModification ) {
			setParameterValue(val);
		}
	}

	@Override
	public void updateValue() {
		try {
			final T newVal = getParameterValue();
			if ( !isValueDifferent(newVal) ) { return; }
			internalModification = true;
			if ( titleLabel != null && !titleLabel.isDisposed() ) {
				modifyAndDisplayValue(newVal);
			}
			internalModification = false;
		} catch (final GamaRuntimeException e) {
			e.addContext("Unable to obtain the value of " + name);
			GAMA.reportError(GAMA.getRuntimeScope(), e, false);
			return;
		}
	}

	private void displayParameterValueAndCheckButtons() {
		GuiUtils.run(new Runnable() {

			@Override
			public void run() {
				displayParameterValue();
				checkButtons();
			}
		});

	}

	protected final void modifyAndDisplayValue(final T val) {
		modifyValue(val);
		if ( !isEditable ) {
			fixedValue.setText(val instanceof String ? (String) val : StringUtils.toGaml(val, false));
		} else if ( isCombo ) {
			combo.select(possibleValues.indexOf(val));
		} else {
			displayParameterValueAndCheckButtons();
		}
		composite.update();
	}

	protected IAgent getAgent() {
		if ( agent != null ) { return agent; }
		return GAMA.run(new InScope<IAgent>() {

			@Override
			public IAgent run(final IScope scope) {
				if ( scope == null ) { return null; }
				return scope.getSimulationScope();
			}
		});
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

	// public boolean acceptPopup() {
	// return acceptPopup;
	// }
	//
	// public void acceptPopup(final boolean accept) {
	// acceptPopup = accept;
	// }

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

}
