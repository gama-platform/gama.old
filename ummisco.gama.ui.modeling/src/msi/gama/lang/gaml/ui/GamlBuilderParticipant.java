/**
 * Created by drogoul, 22 avr. 2016
 * 
 */
package msi.gama.lang.gaml.ui;

import java.util.Set;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.xtext.builder.EclipseResourceFileSystemAccess2;
import org.eclipse.xtext.builder.ParallelBuilderParticipant;
import org.eclipse.xtext.generator.OutputConfiguration;
import org.eclipse.xtext.resource.IResourceDescription.Delta;

/**
 * The class GamlBuilderParticipant.
 *
 * @author drogoul
 * @since 22 avr. 2016
 *
 */
public class GamlBuilderParticipant extends ParallelBuilderParticipant {

	@Override
	protected void cleanDerivedResources(final Delta delta, final Set<IFile> derivedResources,
			final IBuildContext context, final EclipseResourceFileSystemAccess2 access,
			final IProgressMonitor deleteMonitor) throws CoreException {
		try {
			super.cleanDerivedResources(delta, derivedResources, context, access, deleteMonitor);
		} catch (final IllegalArgumentException e) {
			System.out.println("Catching harmless exception in the cleansing of derived resources");
		}
	}

	@Override
	protected void cleanOutput(final IBuildContext ctx, final OutputConfiguration config,
			final IProgressMonitor monitor) throws CoreException {
		try {
			super.cleanOutput(ctx, config, monitor);
		} catch (final IllegalArgumentException e) {
			System.out.println("Catching harmless exception in the cleansing of derived resources");
		}
	}

	@Override
	protected void cleanOutput(final IBuildContext ctx, final OutputConfiguration config,
			final EclipseResourceFileSystemAccess2 access, final IProgressMonitor monitor) throws CoreException {
		super.cleanOutput(ctx, config, access, monitor);
	}

	@Override
	protected boolean canClean(final IContainer container, final OutputConfiguration config) {
		return super.canClean(container, config);
	}

}
