/**
 * Created by drogoul, 4 sept. 2016
 * 
 */
package msi.gama.lang.gaml.ui.decorators;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.ui.validation.DefaultResourceUIValidatorExtension;
import org.eclipse.xtext.validation.CheckMode;

/**
 * The class GamlUIValidatorExtension.
 *
 * @author drogoul
 * @since 4 sept. 2016
 *
 */
public class GamlUIValidatorExtension extends DefaultResourceUIValidatorExtension {

	@Override
	public void updateValidationMarkers(final IFile file, final Resource resource, final CheckMode mode,
			final IProgressMonitor monitor) throws OperationCanceledException {
		if (shouldProcess(file)) {
			addMarkers(file, resource, mode, monitor);
		}
	}
}
