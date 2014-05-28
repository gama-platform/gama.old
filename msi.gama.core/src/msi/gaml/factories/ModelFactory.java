/*********************************************************************************************
 * 
 * 
 * 'ModelFactory.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gaml.factories;

import static msi.gama.common.interfaces.IKeyword.*;
import java.io.InputStream;
import java.util.List;
import msi.gama.kernel.model.IModel;
import msi.gama.precompiler.GamlAnnotations.factory;
import msi.gama.precompiler.*;
import msi.gama.util.GamaMap;
import msi.gaml.compilation.*;
import msi.gaml.descriptions.*;
import msi.gaml.statements.Facets;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;

/**
 * Written by drogoul Modified on 27 oct. 2009
 * 
 * @todo Description
 */
@factory(handles = { ISymbolKind.MODEL })
public class ModelFactory extends SymbolFactory implements IModelBuilder {
	
	ModelAssembler assembler = new ModelAssembler();
	IModelBuilder delegate;

	public ModelFactory(final List<Integer> handles) {
		super(handles);
	}

	public ModelDescription createModelDescription(final String projectPath, final String modelPath,
		final List<ISyntacticElement> models, final ErrorCollector collector, final boolean document, final GamaMap<String, ModelDescription> mm) {
		return assembler.assemble(projectPath, modelPath, models, collector, document, mm);
	}

	public ModelDescription createRootModel(final String name, final Class clazz, final SpeciesDescription macro,
		final SpeciesDescription parent) {
		final Facets f = new Facets(NAME, name, KEYWORD, MODEL);
		ModelDescription.ROOT = new ModelDescription(name, clazz, macro, parent, f);
		return ModelDescription.ROOT;
	}

	@Override
	protected IDescription buildDescription(final String keyword, final Facets facets, final EObject element,
		final ChildrenProvider children, final IDescription enclosing, final SymbolProto proto) {
		// This method is actually never called.
		return null;
	}

	// Callback method from XText
	public void registerModelBuilder(final IModelBuilder instance) {
		delegate = instance;
	}

	/**
	 * Method validate()
	 * @see msi.gaml.compilation.IModelBuilder#validate(org.eclipse.emf.ecore.resource.Resource)
	 */
	@Override
	public ErrorCollector validate(final Resource resource) {
		return delegate == null ? new ErrorCollector() : delegate.validate(resource);
	}

	/**
	 * Method validate()
	 * @see msi.gaml.compilation.IModelBuilder#validate(org.eclipse.emf.common.util.URI)
	 */
	@Override
	public ErrorCollector validate(final URI uri) {
		return delegate == null ? new ErrorCollector() : delegate.validate(uri);
	}

	/**
	 * Method compile()
	 * @see msi.gaml.compilation.IModelBuilder#compile(org.eclipse.emf.ecore.resource.Resource)
	 */
	@Override
	public IModel compile(final Resource resource) {
		return delegate == null ? null : delegate.compile(resource);
	}

	/**
	 * Method compile()
	 * @see msi.gaml.compilation.IModelBuilder#compile(org.eclipse.emf.common.util.URI)
	 */
	@Override
	public IModel compile(final URI uri) {
		return delegate == null ? null : delegate.compile(uri);
	}

	/**
	 * Method compile()
	 * @see msi.gaml.compilation.IModelBuilder#compile(org.eclipse.emf.ecore.resource.Resource, java.util.List)
	 */
	@Override
	public IModel compile(final Resource resource, final List<GamlCompilationError> errors) {
		return delegate == null ? null : delegate.compile(resource, errors);
	}

	/**
	 * Method compile()
	 * @see msi.gaml.compilation.IModelBuilder#compile(org.eclipse.emf.common.util.URI, java.util.List)
	 */
	@Override
	public IModel compile(final URI uri, final List<GamlCompilationError> errors) {
		return delegate == null ? null : delegate.compile(uri, errors);
	}

	/**
	 * Method compile()
	 * @see msi.gaml.compilation.IModelBuilder#compile(java.io.InputStream, java.util.List)
	 */
	@Override
	public IModel compile(final InputStream contents, final List<GamlCompilationError> errors) {
		return delegate == null ? null : delegate.compile(contents, errors);
	}

	@Override
	public ModelDescription buildModelDescription(URI uri,
			List<GamlCompilationError> errors) {
		return delegate == null ? null : delegate.buildModelDescription(uri, errors);
	}

}
