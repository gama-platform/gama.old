/*******************************************************************************************************
 *
 * IJsonConstants.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.util.file.json;

/**
 * The Interface IJsonConstants.
 *
 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
 * @date 5 nov. 2023
 */
public interface IJsonConstants {

	/** The Constant NAME_GEOMETRY. */
	String NAME_GEOMETRY = "geometry";

	/** The Constant NAME_FEATURES. */
	String NAME_FEATURES = "features";

	/** The Constant NAME_GEOMETRIES. */
	String NAME_GEOMETRIES = "geometries";

	/** The Constant NAME_CRS. */
	String NAME_CRS = "crs";

	/** The Constant NAME_PROPERTIES. */
	String NAME_PROPERTIES = "properties";

	/** The Constant NAME_NAME. */
	String NAME_NAME = "name";

	/** The Constant NAME_TYPE. */
	String NAME_TYPE = "type";

	/** The Constant NAME_POINT. */
	String NAME_POINT = "Point";

	/** The Constant NAME_LINESTRING. */
	String NAME_LINESTRING = "LineString";

	/** The Constant NAME_POLYGON. */
	String NAME_POLYGON = "Polygon";

	/** The Constant NAME_COORDINATES. */
	String NAME_COORDINATES = "coordinates";

	/** The Constant NAME_GEOMETRYCOLLECTION. */
	String NAME_GEOMETRYCOLLECTION = "GeometryCollection";

	/** The Constant NAME_MULTIPOLYGON. */
	String NAME_MULTIPOLYGON = "MultiPolygon";

	/** The Constant NAME_MULTILINESTRING. */
	String NAME_MULTILINESTRING = "MultiLineString";

	/** The Constant NAME_MULTIPOINT. */
	String NAME_MULTIPOINT = "MultiPoint";

	/** The Constant NAME_FEATURE. */
	String NAME_FEATURE = "Feature";

	/** The Constant NAME_FEATURECOLLECTION. */
	String NAME_FEATURECOLLECTION = "FeatureCollection";

	/** The Constant GAML_TYPE_LABEL. */
	String GAML_TYPE_LABEL = "gaml_type";

	/** The Constant GAML_SPECIES_LABEL. */
	String GAML_SPECIES_LABEL = "gaml_species";

	/** The Constant GAMA_OBJECT_LABEL. */
	String CONTENTS_WITH_REFERENCES_LABEL = "gama_contents";

	/** The Constant AGENT_REFERENCE_LABEL. */
	String AGENT_REFERENCE_LABEL = "agent_reference";

	/** The Constant REFERENCE_TABLE_LABEL. */
	String REFERENCE_TABLE_LABEL = "gama_references";

	/**
	 * Represents the JSON literal <code>null</code>.
	 */
	JsonValue NULL = new JsonNull();

	/**
	 * Represents the JSON literal <code>true</code>.
	 */
	JsonValue TRUE = new JsonTrue();

	/**
	 * Represents the JSON literal <code>false</code>.
	 */
	JsonValue FALSE = new JsonFalse();

}
