package msi.gama.lang.gaml.descript;

import java.util.Set;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;

public interface IUpdateOnChange {
	/**
	 * Callback<br/>
	 * this method will be called by the gaml (xtext) validator<br/>
	 * this method should get the current document, process it,
	 * and then throw the error(s) (GamlDescriptError)<br/>
	 * 
	 * The so-called implementation should pass its instance to
	 * GamlDescriptIO.getInstance().setCallback(new &lt;T&gt; implements IUpdateOnChange());
	 * @throws Exception the potential compilation exception
	 * @see GamlDescriptIO#setCallback(IUpdateOnChange)
	 * @see GamlDescriptIO#addError(GamlDescriptError)
	 */
	void update(Resource resource) throws Exception;
	Set<String> getVarContext(EObject context) throws Exception;
}
