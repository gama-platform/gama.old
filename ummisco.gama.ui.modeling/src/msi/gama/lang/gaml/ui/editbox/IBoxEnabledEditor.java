/**
 * Created by drogoul, 16 nov. 2014
 * 
 */
package msi.gama.lang.gaml.ui.editbox;

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

	void createDecorator();

	void decorate(boolean doIt);

	void enableUpdates(boolean visible);

}
