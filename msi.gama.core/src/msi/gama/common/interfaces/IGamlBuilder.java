/**
 * Created by drogoul, 2 mars 2012
 * 
 */
package msi.gama.common.interfaces;


/**
 * The class IGamlBuilder.
 * 
 * @author drogoul
 * @since 2 mars 2012
 * 
 */
public interface IGamlBuilder {

	/**
	 * @return
	 * @param gamlEditor
	 */
	boolean addListener(IBuilderListener gamlEditor);

	boolean removeListener(IBuilderListener gamlEditor);

}
