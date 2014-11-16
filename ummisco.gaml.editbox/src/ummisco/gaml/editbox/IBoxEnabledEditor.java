/**
 * Created by drogoul, 16 nov. 2014
 * 
 */
package ummisco.gaml.editbox;

/**
 * Class IBoxEnabledEditor.
 * 
 * @author drogoul
 * @since 16 nov. 2014
 * 
 */
public interface IBoxEnabledEditor {

	IBoxDecorator getDecorator();

	boolean isDecorationEnabled();

	/**
	 * @param provider
	 * @param settings
	 */
	void createDecorator();

	void decorate();

	void undecorate();

	void enableUpdates(boolean visible);

}
