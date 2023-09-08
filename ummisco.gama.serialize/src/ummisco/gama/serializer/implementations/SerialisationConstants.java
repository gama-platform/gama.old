/*******************************************************************************************************
 *
 * SerialisationConstants.java, in ummisco.gama.serialize, is part of the source code of the GAMA modeling and
 * simulation platform (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gama.serializer.implementations;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Set;

import msi.gama.common.interfaces.IKeyword;

/**
 * The Interface SerialisedAgentConstants.
 *
 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
 * @date 21 août 2023
 */
public interface SerialisationConstants {

	/** The Constant KEY. */
	String HEADER_KEY = "gama_header";

	/** The Constant KEY. */
	String HISTORY_KEY = "**history**";

	/** The node key. */
	String NODE_KEY = "**node**";

	/** The serialisation string. */
	String SERIALISATION_STRING = "serialisation_string";

	/** The Constant CLASS_PREFIX. */
	String CLASS_PREFIX = "";

	/** The json format. */
	String JSON_FORMAT = "json";

	/** The xml format. */
	String XML_FORMAT = "xml";

	/** The binary format. */
	String BINARY_FORMAT = "binary";

	/** The agent format. */
	String AGENT_FILE = IKeyword.AGENT;

	/** The simulation formation. */
	String SIMULATION_FILE = IKeyword.SIMULATION;

	/** The gsim format. */
	String GSIM_FILE = "gsim";

	/** The file formats. */
	Set<String> FILE_FORMATS = Set.of(JSON_FORMAT, XML_FORMAT, BINARY_FORMAT);

	/** The file types. */
	Set<String> FILE_TYPES = Set.of(AGENT_FILE, SIMULATION_FILE, GSIM_FILE);

	/** The Constant NULL. */
	byte[] NULL = {};

	/** The Constant COMPRESSED. */
	byte COMPRESSED = 1;

	/** The Constant UNCOMPRESSED. */
	byte UNCOMPRESSED = 0;

	/** The Constant GAMA_IDENTIFIER. */
	byte GAMA_IDENTIFIER = 42;

	/** The Constant STRING_BYTE_ARRAY_CHARSET. The Charset to use to save byte arrays in strings and reversely */
	Charset STRING_BYTE_ARRAY_CHARSET = StandardCharsets.ISO_8859_1;

}
