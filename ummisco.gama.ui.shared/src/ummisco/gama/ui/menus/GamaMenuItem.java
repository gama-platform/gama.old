/**
 * Created by drogoul, 11 déc. 2014
 * 
 */
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