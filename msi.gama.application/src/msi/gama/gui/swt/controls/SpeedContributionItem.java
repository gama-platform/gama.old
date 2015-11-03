/*********************************************************************************************
 * 
 * 
 * 'SpeedContributionItem.java', in plugin 'msi.gama.application', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.gui.swt.controls;

import msi.gama.common.interfaces.ISpeedDisplayer;
import msi.gama.gui.swt.GamaColors.GamaUIColor;
import msi.gaml.operators.Comparison;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.menus.WorkbenchWindowControlContribution;

public class SpeedContributionItem extends WorkbenchWindowControlContribution implements ISpeedDisplayer {

	public final static int widthSize = 100;
	public final static int heightSize = 16;
	private final Image thumb_image;
	private final double init;
	private final GamaUIColor popupColor, sliderColor;
	private final IPositionChangeListener listener;
	private final IToolTipProvider tip;
	private SimpleSlider slider;

	public SpeedContributionItem(final double init, final IPositionChangeListener listener, final IToolTipProvider tip,
		final Image thumb, final GamaUIColor color, final GamaUIColor popupColor) {
		thumb_image = thumb;
		sliderColor = color;
		this.popupColor = popupColor;
		this.tip = tip;
		this.init = init;
		this.listener = listener;
		// SwtGui.setSpeedControl(this);
	}

	@Override
	protected int computeWidth(final Control control) {
		return control.computeSize(widthSize, SWT.DEFAULT, true).x;
	}

	@Override
	public Control createControl(final Composite parent) {
		final Composite composite = new Composite(parent, SWT.DOUBLE_BUFFERED);
		GridLayout layout = new GridLayout(1, false);
		layout.horizontalSpacing = 4;
		layout.verticalSpacing = 0;
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		composite.setLayout(layout);
		composite.setBackground(parent.getBackground());
		GridData data = new GridData(SWT.FILL, SWT.CENTER, true, true);
		data.widthHint = widthSize;
		data.minimumWidth = widthSize;
		slider = new SimpleSlider(composite, sliderColor.color(), thumb_image);
		slider.setTooltipInterperter(tip);
		slider.setLayoutData(data);
		slider.setSize(widthSize, heightSize);
		slider.specifyHeight(heightSize); // fix the problem of wrong position 
		// for the tooltip. Certainly not the best way but it does the trick 
		slider.addPositionChangeListener(listener);
		slider.setPopupBackground(popupColor);
		slider.updateSlider(getInitialValue(), false);
		slider.setBackground(parent.getBackground());
		return composite;
	}

	/**
	 * @return
	 */
	protected double getInitialValue() {
		return init;
	}

	@Override
	public void setInit(final double i, final boolean notify) {
		if ( slider == null ) { return; }
		if ( slider.isDisposed() ) { return; }
		if ( Comparison.different(i, slider.getCurrentPosition()) ) {
			slider.updateSlider(i, notify);
		}

	}

}
