/*******************************************************************************************************
 *
 * OverridingModule.java, in ummisco.gama.ui.modeling, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.1).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.lang.gaml.ui;

import org.eclipse.xtext.builder.clustering.ClusteringBuilderState;
import org.eclipse.xtext.builder.resourceloader.IResourceLoader;
import org.eclipse.xtext.builder.resourceloader.ResourceLoaderProviders;
import org.eclipse.xtext.service.AbstractGenericModule;

import com.google.inject.Binder;
import com.google.inject.name.Names;

/**
 * The class OverridingModule.
 *
 * @author drogoul
 * @since 11 sept. 2016
 *
 */
public class OverridingModule extends AbstractGenericModule {

	@Override
	public void configure(final Binder binder) {
		super.configure(binder);
		binder.bind(IResourceLoader.class)
				.annotatedWith(Names.named(ClusteringBuilderState.RESOURCELOADER_GLOBAL_INDEX))
				.toProvider(ResourceLoaderProviders.getParallelLoader(8, 8));

		binder.bind(IResourceLoader.class)
				.annotatedWith(Names.named(ClusteringBuilderState.RESOURCELOADER_CROSS_LINKING))
				.toProvider(ResourceLoaderProviders.getParallelLoader(8, 8));
	}

}
