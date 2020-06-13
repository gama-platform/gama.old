/*******************************************************************************************************
 *
 * msi.gama.util.file.GamaXMLFile.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling and
 * simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.util.file;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import msi.gama.common.geometry.Envelope3D;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.example;
import msi.gama.precompiler.GamlAnnotations.file;
import msi.gama.precompiler.IConcept;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaListFactory;
import msi.gama.util.GamaMapFactory;
import msi.gama.util.IList;
import msi.gama.util.IMap;
import msi.gaml.types.IContainerType;
import msi.gaml.types.IType;
import msi.gaml.types.Types;

/**
 * Class GamaXMLFile. TODO: Everything ! What kind of buffer should be returned from here ? The current implementation
 * does not make any sense at all.
 *
 * @author drogoul
 * @since 9 janv. 2014
 *
 */
@file (
		name = "xml",
		extensions = "xml",
		buffer_type = IType.MAP,
		concept = { IConcept.FILE, IConcept.XML },
		doc = @doc ("Represents XML files. The internal representation is a list of strings"))
public class GamaXMLFile extends GamaFile<IMap<String, String>, String> {

	/**
	 * @param scope
	 * @param pathName
	 * @throws GamaRuntimeException
	 */
	@doc (
			value = "This file constructor allows to read a xml file",
			examples = { @example (
					value = "file f <-xml_file(\"file.xml\");",
					isExecutable = false) })
	public GamaXMLFile(final IScope scope, final String pathName) throws GamaRuntimeException {
		super(scope, pathName);
	}

	public String getRootTag(final IScope scope) {
		final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder db;
		try {
			db = factory.newDocumentBuilder();
			final Document doc = db.parse(new File(this.getPath(scope)));
			return doc.getFirstChild().getNodeName();
		} catch (final ParserConfigurationException | SAXException | IOException e1) {
			e1.printStackTrace();
		}
		return null;
	}

	@Override
	public IContainerType<?> getGamlType() {
		return Types.FILE.of(Types.INT, Types.NO_TYPE);
	}

	@Override
	public IList<String> getAttributes(final IScope scope) {
		// TODO depends on the contents...
		return GamaListFactory.create(Types.STRING);
	}

	/**
	 * Method computeEnvelope()
	 *
	 * @see msi.gama.util.file.IGamaFile#computeEnvelope(msi.gama.runtime.IScope)
	 */
	@Override
	public Envelope3D computeEnvelope(final IScope scope) {
		return null;
	}

	/**
	 * Method fillBuffer()
	 *
	 * @see msi.gama.util.file.GamaFile#fillBuffer(msi.gama.runtime.IScope)
	 */
	@Override
	protected void fillBuffer(final IScope scope) throws GamaRuntimeException {
		if (getBuffer() != null) { return; }
		try (final BufferedReader in = new BufferedReader(new FileReader(getFile(scope)))) {
			final IMap<String, String> allLines = GamaMapFactory.create(Types.STRING, Types.STRING);
			String str;
			str = in.readLine();
			while (str != null) {
				allLines.put(str, str + "\n");
				str = in.readLine();
			}
			setBuffer(allLines);
		} catch (final IOException e) {
			throw GamaRuntimeException.create(e, scope);
		}
	}

}
