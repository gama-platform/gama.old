/**
 * Created by drogoul, 9 mars 2014
 * 
 */
package msi.gama.common.interfaces;

/**
 * Class IOverlay.
 * 
 * @author drogoul
 * @since 9 mars 2014
 * 
 */
public interface IOverlayProvider<Message> {

	public void setTarget(IUpdaterTarget<Message> overlay);
}
