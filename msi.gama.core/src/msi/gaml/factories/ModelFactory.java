/*******************************************************************************************************
 *
 * ModelFactory.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.1).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gaml.factories;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.ecore.EObject;

import msi.gama.common.interfaces.IKeyword;
import msi.gaml.compilation.IAgentConstructor;
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
// @factory (
// handles = { ISymbolKind.MODEL })
public class ModelFactory extends SymbolFactory {

	/** The assembler. */
	final ModelAssembler assembler = new ModelAssembler();

	/**
	 * Creates a new Model object.
	 *
	 * @param projectPath
	 *            the project path
	 * @param modelPath
	 *            the model path
	 * @param models
	 *            the models
	 * @param collector
	 *            the collector
	 * @param document
	 *            the document
	 * @param mm
	 *            the mm
	 * @return the model description
	 */
	public ModelDescription createModelDescription(final String projectPath, final String modelPath,
			final Iterable<ISyntacticElement> models, final ValidationContext collector, final boolean document,
			final Map<String, ModelDescription> mm) {
		return assembler.assemble(projectPath, modelPath, models, collector, document, mm);
	}

	/**
	 * Creates a new Model object.
	 *
	 * @param name
	 *            the name
	 * @param clazz
	 *            the clazz
	 * @param macro
	 *            the macro
	 * @param parent
	 *            the parent
	 * @param helper
	 *            the helper
	 * @param skills
	 *            the skills
	 * @param plugin
	 *            the plugin
	 * @return the model description
	 */
	@SuppressWarnings ("rawtypes")
	public static ModelDescription createRootModel(final String name, final Class clazz, final SpeciesDescription macro,
			final SpeciesDescription parent, final IAgentConstructor helper, final Set<String> skills,
			final String plugin) {
		if (IKeyword.MODEL.equals(name)) {
			ModelDescription.ROOT = new ModelDescription(name, clazz, "", "", null, macro, parent, null, null,
					ValidationContext.NULL, Collections.EMPTY_SET, helper);
			return ModelDescription.ROOT;
		}
		// we are with a built-in model species
		// for the moment we suppose its parent is the root (macro)
		ModelDescription model = new ModelDescription(name, clazz, "", "", null, null, ModelDescription.ROOT, null,
				null, ValidationContext.NULL, Collections.EMPTY_SET, helper, skills);
		ModelDescription.BUILT_IN_MODELS.put(name, model);
		return model;

	}

	@Override
	protected IDescription buildDescription(final String keyword, final Facets facets, final EObject element,
			final Iterable<IDescription> children, final IDescription enclosing, final SymbolProto proto) {
		// This method is actually never called.
		return null;
	}

}
