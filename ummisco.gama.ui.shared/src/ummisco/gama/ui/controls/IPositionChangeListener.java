/*********************************************************************************************
 *
 * 'IPositionChangeListener.java, in plugin ummisco.gama.ui.shared, is part of the source code of the GAMA modeling and
 * simulation platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package ummisco.gama.ui.controls;

/**
 * Listener interface for position change events of CoolSlider
 * 
 */
public interface IPositionChangeListener {
	/**
	 * Puts the position of the thumb of the slider after a change has occurred. The position has range from min to max
	 * and represents a integer that is a multiple of the incrementValue.<br>
	 * <br>
	 * 
	 * @param position
	 */
	public void positionChanged(SimpleSlider slider, double position);
}
