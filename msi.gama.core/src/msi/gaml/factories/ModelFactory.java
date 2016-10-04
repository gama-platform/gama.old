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

import java.util.List;
import java.util.Map;

import org.eclipse.emf.ecore.EObject;

import msi.gama.precompiler.GamlAnnotations.factory;
import msi.gama.precompiler.ISymbolKind;
import msi.gaml.compilation.ast.ISyntacticElement;
import msi.gaml.descriptions.IDescription;
import msi.gaml.descriptions.ModelDescription;
import msi.gaml.descriptions.SpeciesDescription;
import msi.gaml.descriptions.SymbolProto;
import msi.gaml.descriptions.ValidationContext;
import msi.gaml.statements.Facets;

/**
 * Written by drogoul Modified on 27 oct. 2009
 *
 * @todo Description
 */
@factory(handles = { ISymbolKind.MODEL })
public class ModelFactory extends SymbolFactory {

	final ModelAssembler assembler = new ModelAssembler();

	public ModelFactory(final List<Integer> handles) {
		super(handles);
	}

	public ModelDescription createModelDescription(final String projectPath, final String modelPath,
			final Iterable<ISyntacticElement> models, final ValidationContext collector, final boolean document,
			final Map<String, ModelDescription> mm) {
		return assembler.assemble(projectPath, modelPath, models, collector, document, mm);
	}

	public static ModelDescription createRootModel(final String name, final Class clazz, final SpeciesDescription macro,
			final SpeciesDescription parent) {
		ModelDescription.ROOT = new ModelDescription(name, clazz, macro, parent);
		return ModelDescription.ROOT;
	}

	@Override
	protected IDescription buildDescription(final String keyword, final Facets facets, final EObject element,
			final ChildrenProvider children, final IDescription enclosing, final SymbolProto proto) {
		// This method is actually never called.
		return null;
	}

}
