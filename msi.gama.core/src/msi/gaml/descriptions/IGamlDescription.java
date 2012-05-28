/**
 * Created by drogoul, 27 avr. 2012
 * 
 */
package msi.gaml.descriptions;

import msi.gaml.types.IType;
import org.eclipse.emf.common.notify.Adapter;

/**
 * The class IGamlDescription.
 * 
 * @author drogoul
 * @since 27 avr. 2012
 * 
 */
public interface IGamlDescription extends Adapter.Internal {

	public void dispose();

	public String getTitle();

	public String getDocumentation();

	public abstract IType getContentType();

	public IType getType();

	public abstract String getName();

}
