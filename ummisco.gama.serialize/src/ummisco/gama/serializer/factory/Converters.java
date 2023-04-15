/*******************************************************************************************************
 *
 * Converters.java, in ummisco.gama.serialize, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.2).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gama.serializer.factory;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.agent.SavedAgent;
import msi.gama.metamodel.population.IPopulation;
import msi.gama.runtime.IScope;
import msi.gama.util.GamaColor;
import msi.gama.util.GamaPair;
import msi.gama.util.IList;
import msi.gama.util.IMap;
import msi.gama.util.file.IGamaFile;
import msi.gama.util.graph.IGraph;
import msi.gama.util.matrix.IMatrix;
import msi.gama.util.path.GamaPath;
import msi.gaml.species.ISpecies;
import msi.gaml.types.IType;
import msi.gaml.types.Types;
import ummisco.gama.serializer.gamaType.converter;
import ummisco.gama.serializer.gamaType.converters.GamaAgentConverter;
import ummisco.gama.serializer.gamaType.converters.GamaAgentConverterNetwork;
import ummisco.gama.serializer.gamaType.converters.GamaBasicTypeConverter;
import ummisco.gama.serializer.gamaType.converters.GamaColorConverter;
import ummisco.gama.serializer.gamaType.converters.GamaFileConverter;
import ummisco.gama.serializer.gamaType.converters.GamaGraphConverter;
import ummisco.gama.serializer.gamaType.converters.GamaListConverter;
import ummisco.gama.serializer.gamaType.converters.GamaListConverterNetwork;
import ummisco.gama.serializer.gamaType.converters.GamaMapConverter;
import ummisco.gama.serializer.gamaType.converters.GamaMatrixConverter;
import ummisco.gama.serializer.gamaType.converters.GamaPairConverter;
import ummisco.gama.serializer.gamaType.converters.GamaPathConverter;
import ummisco.gama.serializer.gamaType.converters.GamaPopulationConverter;
import ummisco.gama.serializer.gamaType.converters.GamaSpeciesConverter;
import ummisco.gama.serializer.gamaType.converters.IGamaConverter;
import ummisco.gama.serializer.gamaType.converters.LogConverter;
import ummisco.gama.serializer.gamaType.converters.ReferenceAgentConverter;
import ummisco.gama.serializer.gamaType.converters.SavedAgentConverter;
import ummisco.gama.serializer.gamaType.reference.ReferenceAgent;

/**
 * The Class Converters.
 */
public abstract class Converters {

	/** The Constant REGULAR. */
	private final static IGamaConverter[] REGULAR;

	/** The Constant NETWORK. */
	private final static IGamaConverter[] NETWORK;

	/** The Constant DISCOVERED. */
	private final static IGamaConverter[] DISCOVERED;

	static {
		final List<IGamaConverter> converters = new ArrayList<>();
		Types.builtInTypes.getAllTypes().forEach(t -> {
			converter converter = t.getClass().getAnnotation(converter.class);
			if (converter != null) {
				try {
					Constructor<? extends IGamaConverter> constructor = converter.value().getConstructor(Class.class);
					converters.add(constructor.newInstance(t.toClass()));
				} catch (Exception e) {}
			}
		});
		DISCOVERED = converters.toArray(new IGamaConverter[converters.size()]);
		REGULAR = concat(new IGamaConverter[] { new GamaBasicTypeConverter(IType.class),
				new GamaAgentConverter(IAgent.class), new GamaListConverter(IList.class),
				new GamaMapConverter(IMap.class), new GamaPairConverter(GamaPair.class),
				new GamaMatrixConverter(IMatrix.class), new GamaGraphConverter(IGraph.class),
				new GamaFileConverter(IGamaFile.class), new GamaColorConverter(GamaColor.class),
				new LogConverter(Object.class), new SavedAgentConverter(SavedAgent.class),
				new GamaPopulationConverter(IPopulation.class), new GamaSpeciesConverter(ISpecies.class),
				new ReferenceAgentConverter(ReferenceAgent.class), new GamaPathConverter(GamaPath.class) }, DISCOVERED);

		NETWORK = concat(new IGamaConverter[] { new GamaBasicTypeConverter(IType.class),
				new GamaAgentConverterNetwork(IAgent.class), new GamaListConverterNetwork(IList.class),
				new GamaMapConverter(IMap.class), new GamaPairConverter(GamaPair.class),
				new GamaMatrixConverter(IMatrix.class), new GamaGraphConverter(IGraph.class),
				new GamaFileConverter(IGamaFile.class), new GamaColorConverter(GamaColor.class),
				new LogConverter(Object.class), new SavedAgentConverter(SavedAgent.class),
				new GamaPopulationConverter(IPopulation.class), new GamaSpeciesConverter(ISpecies.class),
				new GamaPathConverter(GamaPath.class) }, DISCOVERED);
	}

	/**
	 * Converter factory.
	 *
	 * @param cs
	 *            the cs
	 * @return the converter[]
	 */
	public static IGamaConverter[] converterFactory(final IScope cs) {
		for (IGamaConverter c : REGULAR) { c.setScope(cs); }
		return REGULAR;
	}

	/**
	 * Converter network factory.
	 *
	 * @param cs
	 *            the cs
	 * @return the converter[]
	 */
	public static IGamaConverter[] converterNetworkFactory(final IScope cs) {
		for (IGamaConverter c : NETWORK) { c.setScope(cs); }
		return NETWORK;
	}

	/**
	 * Concat with array copy.
	 *
	 * @param <T>
	 *            the generic type
	 * @param array1
	 *            the array 1
	 * @param array2
	 *            the array 2
	 * @return the t[]
	 */
	static <T> T[] concat(final T[] array1, final T[] array2) {
		T[] result = Arrays.copyOf(array1, array1.length + array2.length);
		System.arraycopy(array2, 0, result, array1.length, array2.length);
		return result;
	}
	// END TODO
}
