/*********************************************************************************************
 *
 * 'GamaJsonFile.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package msi.gama.util.file;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Set;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import msi.gama.common.geometry.Envelope3D;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.file;
import msi.gama.precompiler.IConcept;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaListFactory;
import msi.gama.util.GamaMap;
import msi.gama.util.GamaMapFactory;
import msi.gama.util.IList;
import msi.gaml.operators.Cast;
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
public class GamaJsonFile extends GamaFile<GamaMap<String, Object>, Object> {

	// GamaMap<String, Object>, Object, String, Object
	public GamaJsonFile(final IScope scope, final String pathName) throws GamaRuntimeException {
		super(scope, pathName);
	}

	public GamaJsonFile(final IScope scope, final String pathName, final GamaMap<String, Object> container) {
		super(scope, pathName, container);
	}

	@Override
	public Envelope3D computeEnvelope(final IScope scope) {
		return null;
	}

	@Override
	public IContainerType getType() {
		return Types.MAP.of(Types.STRING, Types.NO_TYPE);
	}

	@Override
	protected void fillBuffer(final IScope scope) throws GamaRuntimeException {
		if (getBuffer() != null)
			return;
		try (FileReader reader = new FileReader(getFile(scope))) {
			final GamaMap<String, Object> map;
			final Object o = convertToGamaStructures(scope, JSONValue.parse(reader));
			if (o instanceof GamaMap) {
				map = (GamaMap<String, Object>) o;
			} else {
				map = GamaMapFactory.create();
				map.put(IKeyword.CONTENTS, o);
			}
			setBuffer(map);
		} catch (final IOException e) {
			throw GamaRuntimeException.create(e, scope);
		}
	}

	// AD : To remove at one point, as it should be handled by the casting
	// mechanism
	private Object convertToGamaStructures(final IScope scope, final Object o) {
		Object result;
		if (o instanceof JSONArray) {
			final JSONArray array = (JSONArray) o;
			final IList list = GamaListFactory.create(Types.NO_TYPE, array.size());
			for (final Object object : array) {
				list.add(convertToGamaStructures(scope, object));
			}
			result = list;
		} else if (o instanceof JSONObject) {
			final JSONObject json = (JSONObject) o;
			final GamaMap map = GamaMapFactory.create();
			for (final Map.Entry entry : (Set<Map.Entry>) json.entrySet()) {
				map.put(Cast.asString(scope, entry.getKey()), convertToGamaStructures(scope, entry.getValue()));
			}
			result = map;
		} else // we assume we have strings, bool, ints or floats
		{
			if (o instanceof Long)
				result = Integer.valueOf(((Long) o).intValue());
			else if (o instanceof Float)
				result = Double.valueOf(((Float) o).doubleValue());
			else
				result = o;
		}
		return result;
	}

	@Override
	protected String getHttpContentType() {
		return "application/json; charset=UTF-8";
	}

	@Override
	protected void flushBuffer(final IScope scope, final Facets facets) throws GamaRuntimeException {
		final GamaMap<String, Object> map = getBuffer();
		try {
			final File file = getFile(scope);
			if (file.exists()) {
				GAMA.reportAndThrowIfNeeded(scope,
						GamaRuntimeException.warning(file.getName() + " already exists", scope), false);
			} else if (file.createNewFile()) {
				try (OutputStreamWriter writer =
						new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8)) {
					writer.write(JSONValue.toJSONString(map));
				}
			}
		} catch (final IOException e) {
			throw GamaRuntimeException.create(e, scope);
		}
	}

}
