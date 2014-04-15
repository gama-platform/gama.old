/*********************************************************************************************
 * 
 *
 * 'FrequencyItem.java', in plugin 'msi.gama.application', is part of the source code of the 
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.gui.views.actions;

import msi.gama.gui.swt.*;
import msi.gama.gui.swt.controls.*;
import msi.gama.gui.views.GamaViewPart;
import msi.gama.outputs.IDisplayOutput;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.swt.graphics.*;

/**
 * The class SnapshotItem.
 * 
 * @author drogoul
 * @since 19 janv. 2012
 * 
 */
public class FrequencyItem extends GamaViewItem implements IToolTipProvider, IPositionChangeListener {

	private static Color thumbColor = new Color(SwtGui.getDisplay(), new RGB(87, 141, 225));

	/**
	 * @param view
	 */
	FrequencyItem(final GamaViewPart view) {
		super(view);
	}

	double getInit() {
		// refresh every 1 = 1d ; refresh every 1000 = 0d;
		IDisplayOutput output = view.getOutput();
		if ( output == null ) { return 1d; }
		int refresh = view.getOutput().getRefreshRate();
		if ( refresh >= 100 || refresh == 0 ) { return 0d; }
		return (100 - refresh) / 100d;
	}

	int getRefresh(final Double slider) {
		// slider = 0d, refresh = 100; slider= 1d = , refresh = 1
		if ( slider.equals(0d) ) { return 100; }
		if ( slider.equals(1d) ) { return 1; }
		return (int) (100 * (1 - slider));
	}

	/**
	 * @see msi.gama.gui.views.actions.GamaViewItem#createItem()
	 */
	@Override
	protected IContributionItem createItem() {
		IContributionItem item =
			new SpeedContributionItem("Adjust update frequency", getInit(), this, this,
				IGamaIcons.DISPLAY_TOOLBAR_KNOB.image(), IGamaIcons.DISPLAY_TOOLBAR_KNOB.image(), thumbColor);
		return item;
	}

	@Override
	public String getToolTipText(final double value) {
		return "Update every " + getRefresh(value) + " step";
	}

	@Override
	public void positionChanged(final double position) {
		IDisplayOutput output = view.getOutput();
		if ( output == null ) { return; }
		output.setRefreshRate(getRefresh(position));
	}

	@Override
	public void resetToInitialState() {
		((SpeedContributionItem) item).setInit(getInit(), true);
		super.resetToInitialState();
	}

}
