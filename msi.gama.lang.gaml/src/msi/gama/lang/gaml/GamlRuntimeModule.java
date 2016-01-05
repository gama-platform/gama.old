/*********************************************************************************************
 *
 *
 * 'GamlRuntimeModule.java', in plugin 'msi.gama.lang.gaml', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gama.lang.gaml;

import org.eclipse.emf.ecore.util.Diagnostician;
import org.eclipse.xtext.generator.*;
import org.eclipse.xtext.linking.ILinkingService;
import org.eclipse.xtext.naming.*;
import org.eclipse.xtext.parser.*;
import org.eclipse.xtext.resource.*;
import org.eclipse.xtext.resource.containers.StateBasedContainerManager;
import org.eclipse.xtext.scoping.impl.ImportUriGlobalScopeProvider;
import org.eclipse.xtext.service.*;
import com.google.inject.Binder;
import msi.gama.lang.gaml.generator.*;
import msi.gama.lang.gaml.linking.*;
import msi.gama.lang.gaml.parsing.GamlSyntacticParser;
import msi.gama.lang.gaml.resource.*;
import msi.gama.lang.gaml.scoping.BuiltinGlobalScopeProvider.AllImportUriGlobalScopeProvider;
import msi.gama.lang.gaml.scoping.GamlQualifiedNameProvider;
import msi.gama.lang.gaml.validation.*;
import msi.gama.lang.utils.*;
import msi.gaml.compilation.IModelBuilder;
import msi.gaml.expressions.*;
import msi.gaml.factories.*;
import msi.gaml.factories.ModelFactory.IModelBuilderProvider;

/**
 * Use this class to register components to be used at runtime / without the Equinox extension
 * registry.
 */
public class GamlRuntimeModule extends msi.gama.lang.gaml.AbstractGamlRuntimeModule {

	private static boolean initialized;

	public static void staticInitialize() {
		if ( !initialized ) {
			System.out.println(">> Registering GAML expression compiler.");
			GamlExpressionFactory.registerParserProvider(new GamlExpressionCompilerProvider());
			ModelFactory.registerModelBuilderProvider(new IModelBuilderProvider() {

				@Override
				public IModelBuilder get() {
					return new GamlModelBuilder();
				}
			});
			DescriptionFactory.registerDocManager(GamlResourceDocManager.getInstance());
			initialized = true;
		}

	}

	@Override
	public void configure(final Binder binder) {
		super.configure(binder);
		staticInitialize();
		binder.bind(IDefaultResourceDescriptionStrategy.class).to(GamlResourceDescriptionStrategy.class);
		binder.bind(IQualifiedNameConverter.class).to(GamlNameConverter.class);
		binder.bind(IResourceDescription.Manager.class).to(GamlResourceDescriptionManager.class);
		binder.bind(ImportUriGlobalScopeProvider.class).to(AllImportUriGlobalScopeProvider.class);
		binder.bind(IGenerator.class).to(GamlGenerator.class);
		binder.bind(IOutputConfigurationProvider.class).to(GamlOutputConfigurationProvider.class);
		// binder.bind(IResourceDescription.class).to(GamlResourceDescription.class);
		// binder.bind(DescriptionUtils.class).to(GamlDescriptionUtils.class);
	}

	@Override
	public Class<? extends IQualifiedNameProvider> bindIQualifiedNameProvider() {
		return GamlQualifiedNameProvider.class;
	}

	@Override
	// @SingletonBinding(eager = true)
	public Class<? extends GamlJavaValidator> bindGamlJavaValidator() {
		return GamlJavaValidator.class;
	}

	public Class<? extends IExpressionCompiler> bindIGamlExpressionCompiler() {
		return GamlExpressionCompiler.class;
	}

	@Override
	@SingletonBinding(eager = true)
	public Class<? extends org.eclipse.xtext.scoping.IGlobalScopeProvider> bindIGlobalScopeProvider() {
		return msi.gama.lang.gaml.scoping.BuiltinGlobalScopeProvider.class;
	}

	@Override
	public Class<? extends ILinkingService> bindILinkingService() {
		return GamlLinkingService.class;
	}

	@Override
	public Class<? extends XtextResource> bindXtextResource() {
		return GamlResource.class;
	}

	@Override
	public Class<? extends IContainer.Manager> bindIContainer$Manager() {
		return StateBasedContainerManager.class;
	}

	@Override
	public Class<? extends IParser> bindIParser() {
		return GamlSyntacticParser.class;
	}

	@Override
	@SingletonBinding
	public Class<? extends Diagnostician> bindDiagnostician() {
		return GamlDiagnostician.class;
	}

	// public Class<? extends IResourceValidator> bindIResourceValidator() {
	// return GamlResourceValidator.class;
	// }

	@Override
	public void configureRuntimeEncodingProvider(final Binder binder) {
		binder.bind(IEncodingProvider.class).annotatedWith(DispatchingProvider.Runtime.class)
			.to(GamlEncodingProvider.class);
	}

	// public Class<? extends IEncodingProvider> bindIEncodingProvider() {
	// return GamlEncodingProvider.class;
	// }

	// @Override
	// public
	// com.google.inject.Provider<org.eclipse.xtext.resource.containers.IAllContainersState>
	// provideIAllContainersState()
	// {return org.eclipse.xtext.ui.shared.Access.getWorkspaceProjectsState();}

	// @Override
	// public Class<? extends IResourceSetProvider> bindIResourceSetProvider()
	// { return SimpleResourceSetProvider.class; }

	// @Override
	// public Class<? extends IResourceForEditorInputFactory> bindIResourceForEditorInputFactory() {
	// return ResourceForIEditorInputFactory.class;
	// }
}
