/*******************************************************************************************************
 *
 * ISymbolKind.java, in ummisco.gama.annotations, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.9.2).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.precompiler;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Written by drogoul Modified on 1 ao�t 2010
 *
 * @todo Description
 *
 */
public interface ISymbolKind {

	/**
	 * The Interface Variable.
	 */
	public interface Variable {

		/** The number. */
		int NUMBER = 101;

		/** The container. */
		int CONTAINER = 102;

		/** The regular. */
		int REGULAR = 104;

		/** The kinds. */
		Set<Integer> KINDS = new HashSet<>(Arrays.asList(NUMBER, CONTAINER, REGULAR));

		/** The kinds as string. */
		Map<Integer, String> KINDS_AS_STRING = new HashMap<>() {
			{
				put(NUMBER, "number variable");
				put(CONTAINER, "container variable");
				put(REGULAR, "variable");
			}
		};
	}

	/** The species. */
	int SPECIES = 0;

	/** The model. */
	int MODEL = 1;

	/** The single statement. */
	int SINGLE_STATEMENT = 2;

	/** The behavior. */
	int BEHAVIOR = 3;

	/** The parameter. */
	int PARAMETER = 4;

	/** The output. */
	int OUTPUT = 5;

	/** The layer. */
	int LAYER = 6;

	/** The skill. */
	int SKILL = 7;

	/** The batch section. */
	int BATCH_SECTION = 8;

	/** The batch method. */
	int BATCH_METHOD = 9;

	/** The environment. */
	int ENVIRONMENT = 10;

	/** The sequence statement. */
	int SEQUENCE_STATEMENT = 11;

	/** The action. */
	// Equal to SEQUENCE_STATEMENT
	int ACTION = 11;

	/** The experiment. */
	int EXPERIMENT = 13;

	/** The abstract section. */
	int ABSTRACT_SECTION = 14;

	/** The operator. */
	int OPERATOR = 15;

	/** The platform. */
	int PLATFORM = 16;

	/** The number. */
	// Update this variable when adding a kind of symbol
	int __NUMBER__ = 17;

	/** The template menu. */
	String[] TEMPLATE_MENU = { "Species", "Model", "Statement", "Behavior", "Parameter", "Output", "Layer", "Skill",
			"Batch", "Batch", "", "Statement", "Statement", "Experiment", "", "Operator", "" };

	/** The Constant STATEMENTS_WITH_ATTRIBUTES. */
	Set<Integer> STATEMENTS_CONTAINING_ATTRIBUTES = new HashSet<>(Arrays.asList(SPECIES, EXPERIMENT, OUTPUT, MODEL));

}
