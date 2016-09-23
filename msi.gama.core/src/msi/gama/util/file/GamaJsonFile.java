package msi.gama.util.file;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import com.vividsolutions.jts.geom.Envelope;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.file;
import msi.gama.precompiler.IConcept;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaListFactory;
import msi.gama.util.GamaMap;
import msi.gama.util.GamaMapFactory;
import msi.gama.util.IList;
import msi.gaml.operators.Cast;
import msi.gaml.types.IContainerType;
import msi.gaml.types.IType;
import msi.gaml.types.Types;

@file(name = "json", extensions = { "json" }, buffer_type = IType.MAP, buffer_index = IType.STRING, concept = {
		IConcept.FILE })
@doc("Reads a JSON file into a map<string, unknown>. Either a direct map of the object denoted in the JSON file, or a map with only one key ('contents') containing the list in the JSON file. All data structures (JSON object and JSON array) are properly converted into GAMA structures recursively. ")
public class GamaJsonFile extends GamaFile<GamaMap<String, Object>, Object, String, Object> {

	public GamaJsonFile(final IScope scope, final String pathName) throws GamaRuntimeException {
		super(scope, pathName);
	}

	public GamaJsonFile(final IScope scope, final String pathName, final GamaMap<String, Object> container) {
		super(scope, pathName, container);
	}

	@Override
	public Envelope computeEnvelope(final IScope scope) {
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
		FileReader reader = null;
		try {
			final GamaMap<String, Object> map;
			reader = new FileReader(getFile());
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
		} finally {
			if (reader != null)
				try {
					reader.close();
				} catch (final IOException e) {
					throw GamaRuntimeException.create(e, scope);
				}
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
	protected void flushBuffer(final IScope scope) throws GamaRuntimeException {
		final GamaMap<String, Object> map = getBuffer();
		FileWriter writer = null;
		try {
			final File file = getFile();
			if (!file.exists() && file.createNewFile()) {
				writer = new FileWriter(getFile());
				JSONValue.writeJSONString(map, writer);
			}
		} catch (final IOException e) {
			throw GamaRuntimeException.create(e, scope);
		} finally {
			if (writer != null)
				try {
					writer.close();
				} catch (final IOException e) {
					throw GamaRuntimeException.create(e, scope);
				}
		}
	}

}
