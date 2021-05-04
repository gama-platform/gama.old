/*******************************************************************************************************
 *
 * msi.gama.util.file.GamaGMLFile.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling and
 * simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.util.file;

import java.io.FileInputStream;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.wfs.GML;
import org.geotools.wfs.GML.Version;
import org.xml.sax.SAXException;

import msi.gama.common.geometry.Envelope3D;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.example;
import msi.gama.precompiler.GamlAnnotations.file;
import msi.gama.precompiler.IConcept;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaListFactory;
import msi.gama.util.IList;
import msi.gaml.types.IType;

/**
 * Written by drogoul Modified on 13 nov. 2011
 *
 * @todo Description
 *
 */
@file (
		name = "gml",
		extensions = { "gml" },
		buffer_type = IType.LIST,
		buffer_content = IType.GEOMETRY,
		buffer_index = IType.INT,
		concept = { IConcept.GML, IConcept.FILE },
		doc = @doc ("Represents a Geography Markup Language (GML) file as defined by the Open Geospatial Consortium. See https://en.wikipedia.org/wiki/Geography_Markup_Language for more information."))
@SuppressWarnings ({ "unchecked" })
public class GamaGMLFile extends GamaGisFile {

	/**
	 * @throws GamaRuntimeException
	 * @param scope
	 * @param pathName
	 */
	@doc (
			value = "This file constructor allows to read a gml file",
			examples = { @example (
					value = "file f <- gml_file(\"file.gml\");",
					isExecutable = false) })
	public GamaGMLFile(final IScope scope, final String pathName) throws GamaRuntimeException {
		super(scope, pathName, (Integer) null);
	}

	@doc (
			value = "This file constructor allows to read a gml file and specifying the coordinates system code, as an int (epsg code)",
			examples = { @example (
					value = "file f <- gml_file(\"file.gml\", 32648);",
					isExecutable = false) })
	public GamaGMLFile(final IScope scope, final String pathName, final Integer code) throws GamaRuntimeException {
		super(scope, pathName, code);
	}

	@doc (
			value = "This file constructor allows to read a gml file and specifying the coordinates system code (epg,...,), as a string",
			examples = { @example (
					value = "file f <- gml_file(\"file.gml\", \"EPSG:32648\");",
					isExecutable = false) })

	public GamaGMLFile(final IScope scope, final String pathName, final String code) throws GamaRuntimeException {
		super(scope, pathName, code);
	}

	@doc (
			value = "This file constructor allows to read a gml file and take a potential z value (not taken in account by default)",
			examples = { @example (
					value = "file f <- gml_file(\"file.gml\", true);",
					isExecutable = false) })

	public GamaGMLFile(final IScope scope, final String pathName, final boolean with3D) throws GamaRuntimeException {
		super(scope, pathName, (Integer) null, with3D);
	}

	@doc (
			value = "This file constructor allows to read a gml file, specifying the coordinates system code, as an int (epsg code) and take a potential z value (not taken in account by default)",
			examples = { @example (
					value = "file f <- gml_file(\"file.gml\", 32648, true);",
					isExecutable = false) })

	public GamaGMLFile(final IScope scope, final String pathName, final Integer code, final boolean with3D)
			throws GamaRuntimeException {
		super(scope, pathName, code, with3D);
	}

	@doc (
			value = "This file constructor allows to read a gml file, specifying the coordinates system code (epg,...,), as a string and take a potential z value (not taken in account by default",
			examples = { @example (
					value = "file f <- gml_file(\"file.gml\", \"EPSG:32648\",true);",
					isExecutable = false) })

	public GamaGMLFile(final IScope scope, final String pathName, final String code, final boolean with3D)
			throws GamaRuntimeException {
		super(scope, pathName, code, with3D);
	}

	@Override
	public IList<String> getAttributes(final IScope scope) {
		return GamaListFactory.EMPTY_LIST;
	}

	@Override
	protected SimpleFeatureCollection getFeatureCollection(final IScope scope) {
		final var gml = new GML(Version.GML3);
		try {
			SimpleFeatureCollection collection = gml.decodeFeatureCollection(new FileInputStream(getFile(scope)));
			computeProjection(scope, Envelope3D.of(collection.getBounds()));
			return collection;
		} catch (IOException | SAXException | ParserConfigurationException e) {
			throw GamaRuntimeException.create(e, scope);
		}
	}

}
