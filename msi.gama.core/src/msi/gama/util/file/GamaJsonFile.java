/*******************************************************************************************************
 *
 * msi.gama.util.file.GamaJsonFile.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling and
 * simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
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
import msi.gama.common.interfaces.IKeyword;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.example;
import msi.gama.precompiler.GamlAnnotations.file;
import msi.gama.precompiler.IConcept;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaMapFactory;
import msi.gama.util.IMap;
import msi.gama.util.file.json.DeserializationException;
import msi.gama.util.file.json.Jsoner;
import msi.gaml.statements.Facets;
import msi.gaml.types.IContainerType;
import msi.gaml.types.IType;
import msi.gaml.types.Types;

@file (
		name = "json",
		extensions = { "json" },
		buffer_type = IType.MAP,
		buffer_index = IType.STRING,
		concept = { IConcept.FILE })
@doc ("Reads a JSON file into a map<string, unknown>. Either a direct map of the object denoted in the JSON file, or a map with only one key ('contents') containing the list in the JSON file. All data structures (JSON object and JSON array) are properly converted into GAMA structures recursively. ")
@SuppressWarnings ({ "rawtypes", "unchecked" })
public class GamaJsonFile extends GamaFile<IMap<String, Object>, Object> {

	@doc (
			value = "This file constructor allows to read a json file",
			examples = { @example (
					value = "file f <-json_file(\"file.json\");",
					isExecutable = false) })

	public GamaJsonFile(final IScope scope, final String pathName) throws GamaRuntimeException {
		super(scope, pathName);
	}

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
	public IContainerType getGamlType() {
		return Types.MAP.of(Types.STRING, Types.NO_TYPE);
	}

	@Override
	protected void fillBuffer(final IScope scope) throws GamaRuntimeException {
		if (getBuffer() != null) { return; }
		try (FileReader reader = new FileReader(getFile(scope))) {
			final IMap<String, Object> map;
			final Object o = /* convertToGamaStructures(scope, */ Jsoner.deserialize(reader)/* ) */;
			if (o instanceof IMap) {
				map = (IMap<String, Object>) o;
			} else {
				map = GamaMapFactory.create();
				map.put(IKeyword.CONTENTS, o);
			}
			setBuffer(map);
		} catch (final IOException | DeserializationException e) {
			throw GamaRuntimeException.create(e, scope);
		}
	}

	// AD : To remove at one point, as it should be handled by the casting
	// mechanism
	// private Object convertToGamaStructures(final IScope scope, final Object o) {
	// Object result;
	// if (o instanceof JSONArray) {
	// final IList array = (JSONArray) o;
	// final IList list = GamaListFactory.create(Types.NO_TYPE, array.size());
	// for (final Object object : array) {
	// list.add(convertToGamaStructures(scope, object));
	// }
	// result = list;
	// } else if (o instanceof JSONObject) {
	// final JSONObject json = (JSONObject) o;
	// final IMap map = GamaMapFactory.create();
	// for (final Map.Entry entry : json.entrySet()) {
	// map.put(Cast.asString(scope, entry.getKey()), convertToGamaStructures(scope, entry.getValue()));
	// }
	// result = map;
	// } else // we assume we have strings, bool, ints or floats
	// {
	// if (o instanceof Long) {
	// result = Integer.valueOf(((Long) o).intValue());
	// } else if (o instanceof Float) {
	// result = Double.valueOf(((Float) o).doubleValue());
	// } else {
	// result = o;
	// }
	// }
	// return result;
	// }

	@Override
	protected String getHttpContentType() {
		return "application/json";
	}

	@Override
	protected void flushBuffer(final IScope scope, final Facets facets) throws GamaRuntimeException {
		final IMap<String, Object> map = getBuffer();
		Object toSave = map;
		if (map.size() == 1 && map.containsKey("contents")) {
			toSave = map.get("contents");
		}
		final File file = getFile(scope);
		if (file.exists()) {
			GAMA.reportAndThrowIfNeeded(scope, GamaRuntimeException.warning(file.getName() + " already exists", scope),
					false);
		}
		try (Writer writer = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8)) {
			writer.write(Jsoner.serialize(toSave));
		} catch (final IOException e) {
			throw GamaRuntimeException.create(e, scope);
		}
	}

}
