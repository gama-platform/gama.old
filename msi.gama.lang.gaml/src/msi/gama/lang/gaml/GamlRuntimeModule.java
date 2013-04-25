/*
 * GAMA - V1.4 http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2012
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2012
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen (Batch, GeoTools & JTS), 2009-2012
 * - Beno”t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.lang.gaml;

import msi.gama.lang.gaml.linking.GamlLinkingService;
import msi.gama.lang.gaml.parsing.GamlSyntacticParser;
import msi.gama.lang.gaml.resource.*;
import msi.gama.lang.gaml.validation.GamlJavaValidator;
import org.eclipse.xtext.linking.ILinkingService;
import org.eclipse.xtext.parser.IParser;
import org.eclipse.xtext.resource.*;
import org.eclipse.xtext.resource.containers.StateBasedContainerManager;
import org.eclipse.xtext.service.SingletonBinding;
import com.google.inject.Binder;

/**
 * Use this class to register components to be used at runtime / without the Equinox extension
 * registry.
 */
public class GamlRuntimeModule extends msi.gama.lang.gaml.AbstractGamlRuntimeModule {

	@Override
	public void configure(final Binder binder) {
		super.configure(binder);
		binder.bind(IDefaultResourceDescriptionStrategy.class).to(GamlResourceDescriptionStrategy.class);
		// binder.bind(IQualifiedNameConverter.class).to(GamlNameConverter.class);
		// binder.bind(IResourceDescription.Manager.class).to(GamlResourceDescriptionManager.class);
		// binder.bind(DescriptionUtils.class).to(GamlDescriptionUtils.class);
	}

	// @Override
	// public Class<? extends IQualifiedNameProvider> bindIQualifiedNameProvider() {
	// return GamlQualifiedNameProvider.class;
	// }

	@Override
	@SingletonBinding(eager = true)
	public Class<? extends msi.gama.lang.gaml.validation.GamlJavaValidator> bindGamlJavaValidator() {
		return GamlJavaValidator.class;
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

}
