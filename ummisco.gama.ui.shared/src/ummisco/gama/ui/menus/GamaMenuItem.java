/*********************************************************************************************
 *
 * 'GamaMenuItem.java, in plugin ummisco.gama.ui.shared, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package ummisco.gama.ui.menus;

import org.eclipse.swt.events.*;
import org.eclipse.swt.widgets.*;

/**
 * The class GamaMenuItem.
 * 
 * @author drogoul
 * @since 11 déc. 2014
 * 
 */
public class GamaMenuItem extends MenuItem {

	private final GamaMenu topLevelMenu;
	protected static final String TOOLTIP_KEY = "tooltip";
	protected static final ArmListener LISTENER = new ArmListener() {

		@Override
		public void widgetArmed(final ArmEvent e) {
			GamaMenuItem item = (GamaMenuItem) e.widget;
			item.showTooltip();
		}
	};

	public GamaMenuItem(final Menu parent, final int style, final GamaMenu topLevel) {
		super(parent, style);
		topLevelMenu = topLevel;
		addArmListener(LISTENER);
	}

	protected void showTooltip() {}

	@Override
	protected void checkSubclass() {}

	public void setTooltipText(final String t) {
		setData(TOOLTIP_KEY, t);
	}

	public String getTooltipText() {
		return (String) getData(TOOLTIP_KEY);
	}

	@Override
	public void dispose() {
		removeArmListener(LISTENER);
		super.dispose();
	}

	public GamaMenu getTopLevelMenu() {
		return topLevelMenu;
	}

}