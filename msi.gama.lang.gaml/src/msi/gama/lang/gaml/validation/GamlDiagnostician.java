/**
 * Created by drogoul, 21 mai 2013
 * 
 */
package msi.gama.lang.gaml.validation;

import java.util.Map;
import msi.gama.lang.gaml.gaml.*;
import org.eclipse.emf.common.util.DiagnosticChain;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.validation.CancelableDiagnostician;
import com.google.inject.Inject;

/**
 * Class GamlDiagnostician.
 * 
 * @author drogoul
 * @since 21 mai 2013
 * 
 */
public class GamlDiagnostician extends CancelableDiagnostician {

	@Inject
	GamlJavaValidator validator;

	@Inject
	public GamlDiagnostician(final Registry registry) {
		super(registry);
	}

	@Override
	protected boolean doValidateContents(final EObject eObject, final DiagnosticChain diagnostics,
		final Map<Object, Object> context) {
		boolean result = true;
		if ( eObject instanceof Model ) {
			final Model m = (Model) eObject;
			for ( final Import imp : m.getImports() ) {
				result &= validate(imp, diagnostics, context);
			}
		}
		return result;
	}

}
