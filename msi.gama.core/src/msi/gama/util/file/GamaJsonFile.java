/*******************************************************************************************************
 *
 * GamaJsonFile.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.util.file;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;

import msi.gama.common.geometry.Envelope3D;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.example;
import msi.gama.precompiler.GamlAnnotations.file;
import msi.gama.precompiler.IConcept;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaMapFactory;
import msi.gama.util.IMap;
import msi.gama.util.IModifiableContainer;
import msi.gama.util.file.json.Json;
import msi.gama.util.file.json.ParseException;
import msi.gama.util.file.json.WriterConfig;
import msi.gaml.statements.Facets;
import msi.gaml.types.IContainerType;
import msi.gaml.types.IType;
import msi.gaml.types.Types;

/**
 * The Class GamaJsonFile.
 */
@file (
		name = "json",
		extensions = { "json" },
		buffer_type = IType.MAP,
		buffer_index = IType.STRING,
		concept = { IConcept.FILE })
@doc ("Reads a JSON file into a map<string, unknown>. Either a direct map of the object denoted in the JSON file, or a map with only one key ('"
		+ Json.CONTENTS_WITH_REFERENCES_LABEL
		+ "') containing the list in the JSON file. All data structures (JSON object and JSON array) are properly converted into GAMA structures recursively. ")
@SuppressWarnings ({ "rawtypes", "unchecked" })
public class GamaJsonFile extends GamaFile<IMap<String, Object>, Object> {

	/**
	 * Instantiates a new gama json file.
	 *
	 * @param scope
	 *            the scope
	 * @param pathName
	 *            the path name
	 * @throws GamaRuntimeException
	 *             the gama runtime exception
	 */
	@doc (
			value = "This file constructor allows to read a json file",
			examples = { @example (
					value = "file f <-json_file(\"file.json\");",
					isExecutable = false) })

	public GamaJsonFile(final IScope scope, final String pathName) throws GamaRuntimeException {
		super(scope, pathName);
	}

	/**
	 * Instantiates a new gama json file.
	 *
	 * @param scope
	 *            the scope
	 * @param pathName
	 *            the path name
	 * @param container
	 *            the container
	 */
	@doc (
			value = "This constructor allows to  store a map in a json file (it does not save it). The file can then be saved later using the `save` statement",
			examples = { @example (
					value = "file f <-json_file(\"file.json\", map([\"var1\"::1.0, \"var2\"::3.0]));",
					isExecutable = false) })

	public GamaJsonFile(final IScope scope, final String pathName, final IMap<String, Object> container) {
		super(scope, pathName, container);
	}

	@Override
	public Envelope3D computeEnvelope(final IScope scope) {
		return null;
	}

	@Override
	public IContainerType getGamlType() { return Types.MAP.of(Types.STRING, Types.NO_TYPE); }

	@Override
	protected void fillBuffer(final IScope scope) throws GamaRuntimeException {
		if (getBuffer() != null) return;
		try (FileReader reader = new FileReader(getFile(scope))) {
			final IMap<String, Object> map;
			final Object o = Json.getNew().parse(reader).toGamlValue(scope);
			if (o instanceof IMap) {
				map = (IMap<String, Object>) o;
			} else {
				map = GamaMapFactory.create();
				map.put(Json.CONTENTS_WITH_REFERENCES_LABEL, o);
			}
			setBuffer(map);
		} catch (final IOException | ParseException e) {
			throw GamaRuntimeException.create(e, scope);
		}
	}

	@Override
	protected String getHttpContentType() { return "application/json"; }

	@Override
	protected void flushBuffer(final IScope scope, final Facets facets) throws GamaRuntimeException {
		final IMap<String, Object> map = getBuffer();
		Object toSave = map;
		if (map.size() == 1 && map.containsKey(Json.CONTENTS_WITH_REFERENCES_LABEL)) {
			toSave = map.get(Json.CONTENTS_WITH_REFERENCES_LABEL);
		}
		final File file = getFile(scope);
		if (file.exists()) {
			GAMA.reportAndThrowIfNeeded(scope, GamaRuntimeException.warning(file.getName() + " already exists", scope),
					false);
		}
		try (Writer writer = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8)) {
			writer.write(Json.getNew().valueOf(toSave).toString(WriterConfig.PRETTY_PRINT));
		} catch (final IOException e) {
			throw GamaRuntimeException.create(e, scope);
		}
	}

	@Override
	public IModifiableContainer ensureContentsIsCompatible(final IModifiableContainer contents) {
		if (contents instanceof IMap) return contents;
		IMap map = GamaMapFactory.create();
		map.put(Json.CONTENTS_WITH_REFERENCES_LABEL, contents);
		return map;
	}

}
