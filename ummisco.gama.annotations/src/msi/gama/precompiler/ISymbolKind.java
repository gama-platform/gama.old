/*********************************************************************************************
 *
 * 'ISymbolKind.java, in plugin ummisco.gama.annotations, is part of the source code of the GAMA modeling and simulation
 * platform. (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 *
 *
 **********************************************************************************************/
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

	public interface Variable {

		int NUMBER = 101;

		int CONTAINER = 102;

		int REGULAR = 104;

		Set<Integer> KINDS = new HashSet<>(Arrays.asList(NUMBER, CONTAINER, REGULAR));

		Map<Integer, String> KINDS_AS_STRING = new HashMap<>() {
			{
				put(NUMBER, "number");
				put(CONTAINER, "container");
				put(REGULAR, "regular");
			}
		};
	}

	int SPECIES = 0;

	int MODEL = 1;

	int SINGLE_STATEMENT = 2;

	int BEHAVIOR = 3;

	int PARAMETER = 4;

	int OUTPUT = 5;

	int LAYER = 6;

	int SKILL = 7;

	int BATCH_SECTION = 8;

	int BATCH_METHOD = 9;

	int ENVIRONMENT = 10;

	int SEQUENCE_STATEMENT = 11;

	// Equal to SEQUENCE_STATEMENT
	int ACTION = 11;

	int EXPERIMENT = 13;

	int ABSTRACT_SECTION = 14;

	int OPERATOR = 15;

	int PLATFORM = 16;

	// Update this variable when adding a kind of symbol
	int __NUMBER__ = 17;

	String[] TEMPLATE_MENU = { "Species", "Model", "Statement", "Behavior", "Parameter", "Output", "Layer", "Skill",
			"Batch", "Batch", "", "Statement", "Statement", "Experiment", "", "Operator", "" };

}
