/**
 * Created by drogoul, 24 janv. 2014
 * 
 */
package msi.gama.util;

/**
 * Class ModifiableContainer.
 * 
 * @author drogoul
 * @since 24 janv. 2014
 * 
 */
public interface IModifiableContainer<K, V, KeyToAdd, ValueToAdd> extends IContainer<K, V>,
	IContainer.Modifiable<KeyToAdd, ValueToAdd> {

}
