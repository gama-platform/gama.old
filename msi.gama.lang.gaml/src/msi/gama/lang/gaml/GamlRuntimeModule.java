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

import java.util.function.Supplier;

import org.eclipse.xtext.generator.IGenerator;
import org.eclipse.xtext.generator.IOutputConfigurationProvider;
import org.eclipse.xtext.linking.ILinkingDiagnosticMessageProvider;
import org.eclipse.xtext.linking.ILinkingService;
import org.eclipse.xtext.naming.IQualifiedNameConverter;
import org.eclipse.xtext.naming.IQualifiedNameProvider;
import org.eclipse.xtext.parser.IEncodingProvider;
import org.eclipse.xtext.parser.IParser;
import org.eclipse.xtext.parser.antlr.ISyntaxErrorMessageProvider;
import org.eclipse.xtext.resource.IContainer;
import org.eclipse.xtext.resource.IDefaultResourceDescriptionStrategy;
import org.eclipse.xtext.resource.IResourceDescription;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.resource.containers.StateBasedContainerManager;
import org.eclipse.xtext.service.DispatchingProvider;
import org.eclipse.xtext.service.SingletonBinding;

import com.google.inject.Binder;

import msi.gama.lang.gaml.generator.GamlGenerator;
import msi.gama.lang.gaml.generator.GamlOutputConfigurationProvider;
import msi.gama.lang.gaml.linking.GamlLinkingErrorMessageProvider;
import msi.gama.lang.gaml.linking.GamlLinkingService;
import msi.gama.lang.gaml.linking.GamlNameConverter;
import msi.gama.lang.gaml.parsing.GamlSyntacticParser;
import msi.gama.lang.gaml.parsing.GamlSyntaxErrorMessageProvider;
import msi.gama.lang.gaml.resource.GamlModelBuilder;
import msi.gama.lang.gaml.resource.GamlResource;
import msi.gama.lang.gaml.resource.GamlResourceDescriptionManager;
import msi.gama.lang.gaml.resource.GamlResourceDescriptionStrategy;
import msi.gama.lang.gaml.resource.GamlResourceDocManager;
import msi.gama.lang.gaml.scoping.GamlQualifiedNameProvider;
import msi.gama.lang.gaml.validation.GamlJavaValidator;
import msi.gama.lang.utils.GamlEncodingProvider;
import msi.gama.lang.utils.GamlExpressionCompiler;
import msi.gaml.compilation.IModelBuilder;
import msi.gaml.expressions.GamlExpressionFactory;
import msi.gaml.expressions.IExpressionCompiler;
import msi.gaml.factories.DescriptionFactory;
import msi.gaml.factories.ModelFactory;

/**
 * Use this class to register components to be used at runtime / without the
 * Equinox extension registry.
 */
public class GamlRuntimeModule extends msi.gama.lang.gaml.AbstractGamlRuntimeModule {

	private static boolean initialized;

	public static void staticInitialize() {
		if (!initialized) {
			System.out.println(">GAMA initializing GAML expression compiler and model builder");
			GamlExpressionFactory.registerParserProvider(new Supplier<IExpressionCompiler>() {

				@Override
				public IExpressionCompiler get() {
					return new GamlExpressionCompiler();
				}
			});
			ModelFactory.registerModelBuilderProvider(new Supplier<IModelBuilder>() {

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
		// binder.bind(ImportUriGlobalScopeProvider.class).to(AllImportUriGlobalScopeProvider.class);
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
	@SingletonBinding(eager = true)
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

	public Class<? extends ISyntaxErrorMessageProvider> bindISyntaxErrorMessageProvider() {
		return GamlSyntaxErrorMessageProvider.class;
	}

	@Override
	public Class<? extends ILinkingService> bindILinkingService() {
		return GamlLinkingService.class;
	}

	public Class<? extends ILinkingDiagnosticMessageProvider.Extended> bindILinkingDiagnosticMessageProvider() {
		return GamlLinkingErrorMessageProvider.class;
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

	// @Override
	// @SingletonBinding
	// public Class<? extends Diagnostician> bindDiagnostician() {
	// return GamlDiagnostician.class;
	// }

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
	// public Class<? extends IResourceForEditorInputFactory>
	// bindIResourceForEditorInputFactory() {
	// return ResourceForIEditorInputFactory.class;
	// }
}
