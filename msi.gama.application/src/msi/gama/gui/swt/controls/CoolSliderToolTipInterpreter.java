package msi.gama.gui.swt.controls;
/**
 * An interface for the users of <code>CoolSlider</code> to listen to mouse hover and mouse over events. 
 * In addition to receiving the current percentage of the <code>CoolSlider's</code> thumb position. The user of 
 * this interface will need to return a user understandable <code>String</code> so that the tooltip
 * of the <code>CoolSlider</code> can be set.<br><br>
 * 
 * NOTE: If the CoolSlider is using the SNAP style, it will give it a value between max and min (inclusive) it will be
 * an multiple of the increment value specified. 
 * 
 * @author Code Crofter <br>
 * On behalf Polymorph Systems
 *
 * @since RCP Toolbox v0.1 <br>
 */
public interface CoolSliderToolTipInterpreter {
	/** return the string for the tooltip where the slider has position of value. SMOOTH STYLE: value = percentage (0->1)
	 *  SNAP_STYLE: value = min >= NxIncrement <= max. For when the mouse hovers over the thumb of the slider*/
	public String getToolTipForPositionOnMouseHover(double value);
	/** return the string for the tooltip where the slider has position of value. SMOOTH STYLE: value = percentage (0->1)
	 *  SNAP_STYLE: value = min >= NxIncrement <= max. For when the mouse moves over the thumb of the slider*/
	public String getToolTipForPositionOnMouseMoveOver(double value);
}
