/*********************************************************************************************
 *
 * 'IToolTipProvider.java, in plugin ummisco.gama.ui.shared, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package ummisco.gama.ui.controls;

/**
 * An interface for the users of <code>CoolSlider</code> to listen to mouse hover and mouse over
 * events.
 * In addition to receiving the current percentage of the <code>CoolSlider's</code> thumb position.
 * The user of
 * this interface will need to return a user understandable <code>String</code> so that the tooltip
 * of the <code>CoolSlider</code> can be set.<br>
 * <br>
 * 
 * NOTE: If the CoolSlider is using the SNAP style, it will give it a value between max and min
 * (inclusive) it will be
 * an multiple of the increment value specified.
 * 
 * @author Code Crofter <br>
 *         On behalf Polymorph Systems
 * 
 * @since RCP Toolbox v0.1 <br>
 */
public interface IToolTipProvider {

	public String getToolTipText(double value);

}
