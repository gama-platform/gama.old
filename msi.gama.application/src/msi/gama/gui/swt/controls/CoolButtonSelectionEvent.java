package msi.gama.gui.swt.controls;

import org.eclipse.swt.events.TypedEvent;

/**
 * @author Code Crofter
 * On behalf Polymorph Systems
 * 
 * @since RCP Toolbox v0.1 <br>
 */
public class CoolButtonSelectionEvent extends TypedEvent{
	private static final long serialVersionUID = 1L;
	
	public int x;
	public int y;
	
	public CoolButtonSelectionEvent(Object source,int x, int y) {
		super(source);			
		this.x = x;
		this.y = y;
	}
}
