/*********************************************************************************************
 * 
 *
 * 'IPositionChangeListener.java', in plugin 'msi.gama.application', is part of the source code of the 
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package ummisco.gama.ui.controls;
/**
 * Listener interface for position change events of CoolSlider
 * 
 * @author Code Crofter
 * On behalf Polymorph Systems
 * 
 * @since RCP Toolbox v0.1 <br>
 */
public interface IPositionChangeListener {
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
