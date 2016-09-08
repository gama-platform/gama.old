/**
 * Created by drogoul, 4 sept. 2016
 * 
 */
package msi.gama.lang.gaml.ui;

import org.eclipse.core.resources.IProject;
import org.eclipse.xtext.builder.impl.BuildScheduler;
import org.eclipse.xtext.builder.impl.IBuildFlag;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import msi.gama.lang.gaml.indexer.IModelIndexer;

/**
 * The class GamaBuildScheduler.
 *
 * @author drogoul
 * @since 4 sept. 2016
 *
 */
@Singleton
public class GamaBuildScheduler extends BuildScheduler {

	@Inject IModelIndexer indexer;

	@Inject
	public GamaBuildScheduler() {
	}

	@Override
	public void scheduleBuildIfNecessary(final Iterable<IProject> toUpdate, final IBuildFlag... buildFlags) {
		// for (final IProject p : toUpdate) {
		//// System.out.println("Building index of : " + p.getName());
		// ((WorkspaceIndexer) indexer).buildIndex(p);
		// }
		super.scheduleBuildIfNecessary(toUpdate, buildFlags);
	}

}
