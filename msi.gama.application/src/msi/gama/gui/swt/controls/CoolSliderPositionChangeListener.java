package msi.gama.gui.swt.controls;
/**
 * Listener interface for position change events of CoolSlider
 * 
 * @author Code Crofter
 * On behalf Polymorph Systems
 * 
 * @since RCP Toolbox v0.1 <br>
 */
public interface CoolSliderPositionChangeListener {
	/**
	 * SMOOTH STYLE:<br>
	 * Puts the position of the thumb of the slider after a change has occurred.
	 * The position has range from 0 to 1 and represents a percentage of the position.<br><br>
	 * 
	 * SNAP STYLE: <br>
	 * Puts the position of the thumb of the slider after a change has occurred.
	 * The position has range from min to max and represents a integer that is a multiple of the incrementValue.<br><br>
	 * 
	 * @param position
	 */
	public void positionChanged(double position);
}
