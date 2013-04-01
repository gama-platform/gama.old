/**
 * Created by drogoul, 27 avr. 2012
 * 
 */
package msi.gaml.descriptions;

import msi.gama.common.interfaces.ITyped;
import org.eclipse.emf.common.notify.Adapter;

/**
 * The class IGamlDescription.
 * 
 * @author drogoul
 * @since 27 avr. 2012
 * 
 */
public interface IGamlDescription extends Adapter.Internal, ITyped {

	public void dispose();

	public String getTitle();

	public String getDocumentation();

	public abstract String getName();

}
