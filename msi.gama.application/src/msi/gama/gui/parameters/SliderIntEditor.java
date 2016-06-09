package msi.gama.gui.parameters;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import msi.gama.common.interfaces.EditorListener;
import msi.gama.common.util.StringUtils;
import msi.gama.gui.swt.GamaIcons;
import msi.gama.gui.swt.IGamaColors;
import msi.gama.gui.swt.controls.IPositionChangeListener;
import msi.gama.gui.swt.controls.SimpleSlider;
import msi.gama.kernel.experiment.IParameter;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;

public class SliderIntEditor extends AbstractEditor {

	private static final int[] NULL = new int[0];
	SimpleSlider slider;

	public SliderIntEditor(final IScope scope, final IAgent a, final IParameter variable, final EditorListener l) {
		super(scope, a, variable);
	}

	@Override
	protected int[] getToolItems() {
		// TODO Auto-generated method stub
		return NULL;
	}

	@Override
	protected Control getEditorControl() {
		return slider;
	}

	@Override
	protected Control createCustomParameterControl(final Composite composite) throws GamaRuntimeException {

		slider = new SimpleSlider(composite, IGamaColors.OK.color(), IGamaColors.GRAY_LABEL.lighter(),
			GamaIcons.create("small.slider2").image(), false) {

		};

		slider.addPositionChangeListener(new IPositionChangeListener() {

			@Override
			public void positionChanged(final double position) {
				modifyValue(computeValue(position));
				unitItem.setText(computeUnitLabel());
				toolbar.layout();
				toolbar.pack();
			}

		});
		slider.pack(true);
		return slider;
	}

	@Override
	protected String computeUnitLabel() {
		String s = StringUtils.toGaml(this.currentValue, false);
		if ( minValue != null ) {
			final String min = StringUtils.toGaml(minValue, false);
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
		final String u = param.getUnitLabel(getScope());
		if ( u != null ) {
			s += " " + u;
		}
		return s;
	}

	@Override
	protected GridData getParameterGridData() {
		final GridData result = super.getParameterGridData();
		result.heightHint = 24;
		return result;
	}

	@Override
	protected void hideToolbar() {
		final GridData data = new GridData(SWT.FILL, SWT.BOTTOM, false, false);

		toolbar.setLayoutData(data);
		toolbar.layout();
	}

	@Override
	protected void addToolbarHiders(final Control ... c) {}

	private int computeValue(final double position) {
		return (int) (minValue.intValue() + Math.round(position * (maxValue.intValue() - minValue.intValue())));
	}

	@Override
	protected void displayParameterValue() {
		final int p = (int) this.getParameterValue();
		final double position =
			(double) (p - minValue.intValue()) / (double) (maxValue.intValue() - minValue.intValue());
		slider.updateSlider(position, false);

	}

}
