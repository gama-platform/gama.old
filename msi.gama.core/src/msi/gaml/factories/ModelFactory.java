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

import static msi.gama.common.interfaces.IKeyword.KEYWORD;
import static msi.gama.common.interfaces.IKeyword.MODEL;
import static msi.gama.common.interfaces.IKeyword.NAME;

import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;

import msi.gama.kernel.model.IModel;
import msi.gama.precompiler.GamlAnnotations.factory;
import msi.gama.precompiler.ISymbolKind;
import msi.gama.util.file.GAMLFile;
import msi.gaml.compilation.GamlCompilationError;
import msi.gaml.compilation.IModelBuilder;
import msi.gaml.compilation.ISyntacticElement;
import msi.gaml.descriptions.ErrorCollector;
import msi.gaml.descriptions.IDescription;
import msi.gaml.descriptions.ModelDescription;
import msi.gaml.descriptions.SpeciesDescription;
import msi.gaml.descriptions.SymbolProto;
import msi.gaml.statements.Facets;

/**
 * Written by drogoul Modified on 27 oct. 2009
 *
 * @todo Description
 */
@factory(handles = { ISymbolKind.MODEL })
public class ModelFactory extends SymbolFactory implements IModelBuilder {

	public static interface IModelBuilderProvider {

		IModelBuilder get();
	}

	ModelAssembler assembler = new ModelAssembler();
	static IModelBuilderProvider delegate;

	public ModelFactory(final List<Integer> handles) {
		super(handles);
	}

	public ModelDescription createModelDescription(final String projectPath, final String modelPath,
			final List<ISyntacticElement> models, final ErrorCollector collector, final boolean document,
			final Map<String, ModelDescription> mm, final Collection<URI> absoluteAlternatePaths) {
		return assembler.assemble(projectPath, modelPath, models, collector, document, mm, absoluteAlternatePaths);
	}

	public ModelDescription createRootModel(final String name, final Class clazz, final SpeciesDescription macro,
			final SpeciesDescription parent) {
		final Facets f = new Facets(NAME, name, KEYWORD, MODEL);
		ModelDescription.ROOT = new ModelDescription(name, clazz, macro, parent, f);
		return ModelDescription.ROOT;
	}

	@Override
	protected IDescription buildDescription(final String keyword, final Facets facets, final EObject element,
			final ChildrenProvider children, final IDescription enclosing, final SymbolProto proto,
			final String plugin) {
		// This method is actually never called.
		return null;
	}

	// Callback method from XText
	public static void registerModelBuilderProvider(final IModelBuilderProvider instance) {
		delegate = instance;
	}

	/**
	 * Method validate()
	 * 
	 * @see msi.gaml.compilation.IModelBuilder#validate(org.eclipse.emf.ecore.resource.Resource)
	 */
	@Override
	public ErrorCollector validate(final Resource resource) {
		return delegate == null ? new ErrorCollector() : delegate.get().validate(resource);
	}

	/**
	 * Method validate()
	 * 
	 * @see msi.gaml.compilation.IModelBuilder#validate(org.eclipse.emf.common.util.URI)
	 */
	@Override
	public ErrorCollector validate(final URI uri) {
		return delegate == null ? new ErrorCollector() : delegate.get().validate(uri);
	}

	/**
	 * Method compile()
	 * 
	 * @see msi.gaml.compilation.IModelBuilder#compile(org.eclipse.emf.ecore.resource.Resource)
	 */
	@Override
	public IModel compile(final Resource resource) {
		return delegate == null ? null : delegate.get().compile(resource);
	}

	/**
	 * Method compile()
	 * 
	 * @see msi.gaml.compilation.IModelBuilder#compile(org.eclipse.emf.common.util.URI)
	 */
	@Override
	public IModel compile(final URI uri) {
		return delegate == null ? null : delegate.get().compile(uri);
	}

	/**
	 * Method compile()
	 * 
	 * @see msi.gaml.compilation.IModelBuilder#compile(org.eclipse.emf.ecore.resource.Resource,
	 *      java.util.List)
	 */
	@Override
	public IModel compile(final Resource resource, final List<GamlCompilationError> errors) {
		return delegate == null ? null : delegate.get().compile(resource, errors);
	}

	/**
	 * Method compile()
	 * 
	 * @see msi.gaml.compilation.IModelBuilder#compile(org.eclipse.emf.common.util.URI,
	 *      java.util.List)
	 */
	@Override
	public IModel compile(final URI uri, final List<GamlCompilationError> errors) {
		return delegate == null ? null : delegate.get().compile(uri, errors);
	}

	/**
	 * Method compile()
	 * 
	 * @see msi.gaml.compilation.IModelBuilder#compile(java.io.InputStream,
	 *      java.util.List)
	 */
	@Override
	public IModel compile(final InputStream contents, final List<GamlCompilationError> errors) {
		return delegate == null ? null : delegate.get().compile(contents, errors);
	}

	@Override
	public ModelDescription buildModelDescription(final URI uri, final List<GamlCompilationError> errors) {
		return delegate == null ? null : delegate.get().buildModelDescription(uri, errors);
	}

	@Override
	public GAMLFile.GamlInfo getInfo(final URI uri, final long stamp) {
		return delegate == null ? null : delegate.get().getInfo(uri, stamp);
	}

}
