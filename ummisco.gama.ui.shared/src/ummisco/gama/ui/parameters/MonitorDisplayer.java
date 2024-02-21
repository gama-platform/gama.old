/*******************************************************************************************************
 *
 * MonitorDisplayer.java, in ummisco.gama.ui.shared, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.9.3).
 *
 * (c) 2007-2024 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gama.ui.parameters;

import static msi.gama.runtime.GAMA.getRuntimeScope;
import static msi.gama.runtime.GAMA.reportError;
import static msi.gama.util.GamaListFactory.wrap;
import static msi.gaml.operators.System.enterValue;
import static msi.gaml.operators.System.userInputDialog;
import static msi.gaml.types.Types.NO_TYPE;
import static ummisco.gama.ui.menus.GamaMenu.action;
import static ummisco.gama.ui.menus.GamaMenu.separate;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;

import msi.gama.common.interfaces.IValue;
import msi.gama.kernel.experiment.InputParameter;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.outputs.MonitorOutput;
import msi.gama.outputs.ValuedDisplayOutputFactory;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaColor;
import msi.gaml.expressions.IExpression;
import msi.gaml.types.IType;
import msi.gaml.types.Types;
import ummisco.gama.ui.controls.FlatButton;
import ummisco.gama.ui.resources.GamaColors;
import ummisco.gama.ui.resources.IGamaColors;
import ummisco.gama.ui.utils.WorkbenchHelper;
import ummisco.gama.ui.views.toolbar.Selector;

/**
 * The Class TextDisplayer.
 */
public class MonitorDisplayer extends AbstractStatementEditor<MonitorOutput> {

	/** The closer. */
	private Runnable closer;

	/**
	 * Instantiates a new command editor.
	 *
	 * @param scope
	 *            the scope
	 * @param command
	 *            the command
	 * @param l
	 *            the l
	 */
	public MonitorDisplayer(final IScope scope, final MonitorOutput command) {
		super(scope, command, null);
	}

	@Override
	Composite createValueComposite() {
		composite = new Composite(parent, SWT.NONE);
		final var data = new GridData(SWT.FILL, SWT.FILL, true, true);
		data.minimumWidth = 100;
		data.horizontalSpan = 2;
		composite.setLayoutData(data);
		GridLayout l = new GridLayout(1, false);
		composite.setLayout(l);
		return composite;
	}

	@Override
	public void createControls(final EditorsGroup p) {
		super.createControls(p);
		if (getStatement().shouldBeInitialized()) {
			getStatement().shouldNotBeInitialized();
			applyEdit();
		}
	}

	@SuppressWarnings ("unchecked")
	@Override
	protected FlatButton createCustomParameterControl(final Composite composite) throws GamaRuntimeException {
		textBox = FlatButton.menu(composite, GamaColors.get(getStatement().getColor(getScope())),
				getStatement().getTitle());
		textBox.addSelectionListener((Selector) e -> {
			final Menu m = new Menu(textBox);
			action(m, "Edit...", ex -> { applyEdit(); });
			// item.setEnabled(false); // for the moment
			action(m, getStatement().isPaused() ? "Resume" : "Pause", ex -> {
				getStatement().setPaused(!getStatement().isPaused());
				updateWithValueOfParameter(false, false);
			});
			action(m, "Close", ex -> {
				closer.run();
				parent.requestLayout();
			});
			separate(m);
			action(m, "Copy value", ex -> {
				final Object v = getStatement().getLastValue();
				WorkbenchHelper.copy(
						v == null ? "nil" : v instanceof IValue ? ((IValue) v).serializeToGaml(true) : v.toString());
			});
			final IExpression exp = getStatement().getValue();
			final IType<?> type = exp == null ? Types.NO_TYPE : exp.getGamlType();
			if (type.isNumber() || type.isContainer() && type.getContentType().isNumber()) {
				action(m, "Save as CSV", ex -> { applySave(); });
			} else if (type.isAgentType()) {
				action(m, "Inspect agent", ex -> {
					getStatement().getScope().getGui().setSelectedAgent((IAgent) getStatement().getLastValue());
				});
			} else if (type.isContainer() && type.getContentType().isAgentType()) {
				action(m, "Browse agents", ex -> {
					ValuedDisplayOutputFactory.browse((Collection<? extends IAgent>) getStatement().getLastValue());
				});
			}
			m.setVisible(true);
		});
		composite.requestLayout();
		return textBox;

	}

	@Override
	EditorLabel createEditorLabel() {
		return null;
	}

	@Override
	public void updateWithValueOfParameter(final boolean synchronously, final boolean retrieveVarValue) {
		try {
			Runnable run = () -> {
				internalModification = true;
				if (parent != null && !parent.isDisposed() && !textBox.isDisposed()) {
					textBox.setText(getStatement().getTitle());
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

	@Override
	protected void applySave() {
		getStatement().saveHistory();
	}

	@Override
	protected void applyEdit() {
		GamaColor color = getStatement().getColor(getScope());
		if (color == null) { color = IGamaColors.NEUTRAL.gamaColor(); }
		Map<String, Object> init = userInputDialog(getScope(), "Edit monitor",
				wrap(NO_TYPE,
						List.of(enterValue(getScope(), "Title", getStatement().getName()),
								enterValue(getScope(), "Color", Types.COLOR, color),
								new InputParameter("Expression", getStatement().getValue()) {
									@Override
									public boolean isExpression() { return true; }
								})));
		getStatement().setName((String) init.get("Title"));
		getStatement().setColor((GamaColor) init.get("Color"));
		textBox.setColor(GamaColors.get(getStatement().getColor(getScope())));
		getStatement().setNewExpression((IExpression) init.get("Expression"));
		updateWithValueOfParameter(false, false);
	}

	@Override
	protected GridData getEditorControlGridData() {
		final var d = new GridData(SWT.FILL, SWT.CENTER, true, false);
		d.minimumWidth = 50;
		return d;
	}

	/**
	 * Sets the closer.
	 *
	 * @param object
	 *            the new closer
	 */
	public void setCloser(final Runnable object) { closer = object; }

}
