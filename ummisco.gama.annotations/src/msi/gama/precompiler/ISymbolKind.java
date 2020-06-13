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

	public static interface Variable {

		public static final int NUMBER = 101;

		public static final int CONTAINER = 102;

		public static final int SIGNAL = 103;

		public static final int REGULAR = 104;

		public static final Set<Integer> KINDS = new HashSet<>(Arrays.asList(NUMBER, CONTAINER, REGULAR, SIGNAL));

		public static final Map<Integer, String> KINDS_AS_STRING = new HashMap<Integer, String>() {
			{
				put(NUMBER, "number");
				put(CONTAINER, "container");
				put(REGULAR, "regular");
				put(SIGNAL, "signal");
			}
		};
	}

	public static final int SPECIES = 0;

	public static final int MODEL = 1;

	public static final int SINGLE_STATEMENT = 2;

	public static final int BEHAVIOR = 3;

	public static final int PARAMETER = 4;

	public static final int OUTPUT = 5;

	public static final int LAYER = 6;

	public static final int SKILL = 7;

	public static final int BATCH_SECTION = 8;

	public static final int BATCH_METHOD = 9;

	public static final int ENVIRONMENT = 10;

	public static final int SEQUENCE_STATEMENT = 11;

	// Equal to SEQUENCE_STATEMENT
	public static final int ACTION = 11;

	public static final int EXPERIMENT = 13;

	public static final int ABSTRACT_SECTION = 14;

	public static final int OPERATOR = 15;

	public static final int PLATFORM = 16;

	// Update this variable when adding a kind of symbol
	public static final int __NUMBER__ = 17;

	public static final String[] TEMPLATE_MENU = { "Species", "Model", "Statement", "Behavior", "Parameter", "Output",
			"Layer", "Skill", "Batch", "Batch", "", "Statement", "Statement", "Experiment", "", "Operator", "" };

}
