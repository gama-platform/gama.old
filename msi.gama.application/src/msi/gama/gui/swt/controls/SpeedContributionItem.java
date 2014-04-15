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
import msi.gama.gui.swt.*;
import msi.gaml.operators.Comparison;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.menus.WorkbenchWindowControlContribution;

public class SpeedContributionItem extends WorkbenchWindowControlContribution implements ISpeedDisplayer {

	private final static int size = 100;
	private final Image thumb_image;
	private final Image thumb_image_hover;
	private final String tooltipText;
	private final double init;
	private final Color popupColor;
	private final IPositionChangeListener listener;
	private final IToolTipProvider tip;
	private CoolSlider slider;

	public SpeedContributionItem(final String toolTip, final double init, final IPositionChangeListener listener,
		final IToolTipProvider tip, final Image thumb, final Image over, final Color popupColor) {
		thumb_image = thumb;
		thumb_image_hover = over;
		this.popupColor = popupColor;
		tooltipText = toolTip;
		this.tip = tip;
		this.init = init;
		this.listener = listener;
		SwtGui.setSpeedControl(this);
	}

	@Override
	protected int computeWidth(final Control control) {
		return control.computeSize(size, SWT.DEFAULT, true).x;
	}

	@Override
	protected Control createControl(final Composite parent) {
		final Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(1, false);
		layout.horizontalSpacing = 4;
		layout.verticalSpacing = 0;
		layout.marginHeight = 0;
		layout.marginWidth = 4;
		composite.setLayout(layout);
		GridData data = new GridData(SWT.FILL, SWT.CENTER, true, true);
		data.widthHint = size;
		slider =
			new CoolSlider(composite, SWT.HORIZONTAL | CoolSlider.SMOOTH_STYLE, IGamaIcons.TOOLBAR_SLIDER.image(),
				IGamaIcons.TOOLBAR_SLIDER.image(), thumb_image, thumb_image, thumb_image_hover,
				IGamaIcons.TOOLBAR_SLIDER.image(), IGamaIcons.TOOLBAR_SLIDER.image());
		slider.setTooltipInterperter(tip);
		slider.setLayoutData(data);
		slider.setSize(size, 16);
		slider.addPositionChangeListener(listener);
		slider.setToolTipText(tooltipText);
		slider.setPopupBackground(popupColor);
		slider.updateSlider(getInitialValue(), false);
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
