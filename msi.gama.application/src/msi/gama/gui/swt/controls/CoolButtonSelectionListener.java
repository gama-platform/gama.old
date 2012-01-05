package msi.gama.gui.swt.controls;

/**
 * Listener interface for selection of Coolbutton
 * 
 * @author Code Crofter
 * On behalf Polymorph Systems
 * 
 * @since RCP Toolbox v0.1 <br>
 */
public interface CoolButtonSelectionListener {
	/**
	 * Selection on mouse or key release
	 * @param e
	 */
	public void selectionOnRelease(CoolButtonSelectionEvent e);
	/**
	 * Selection on mouse or key press
	 * @param e
	 */
	public void selectionOnPress(CoolButtonSelectionEvent e);
}
