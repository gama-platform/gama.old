/*********************************************************************************************
 *
 * 'GamlUIValidatorExtension.java, in plugin ummisco.gama.ui.modeling, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
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
