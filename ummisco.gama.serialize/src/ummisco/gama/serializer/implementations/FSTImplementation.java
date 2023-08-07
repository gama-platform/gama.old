/*******************************************************************************************************
 *
 * FSTImplementation.java, in ummisco.gama.serialize, is part of the source code of the GAMA modeling and
 * simulation platform (v.1.9.2).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gama.serializer.implementations;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.geom.PrecisionModel;
import org.nustaq.serialization.FSTBasicObjectSerializer;
import org.nustaq.serialization.FSTClazzInfo;
import org.nustaq.serialization.FSTClazzInfo.FSTFieldInfo;
import org.nustaq.serialization.FSTConfiguration;
import org.nustaq.serialization.FSTObjectInput;
import org.nustaq.serialization.FSTObjectOutput;
import org.nustaq.serialization.coders.FSTJsonFieldNames;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import msi.gama.common.geometry.GamaCoordinateSequence;
import msi.gama.common.geometry.GamaCoordinateSequenceFactory;
import msi.gama.common.geometry.GamaGeometryFactory;
import msi.gama.common.geometry.GeometryUtils;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.common.util.RandomUtils;
import msi.gama.kernel.simulation.SimulationAgent;
import msi.gama.metamodel.agent.GamlAgent;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.agent.IMacroAgent;
import msi.gama.metamodel.agent.MinimalAgent;
import msi.gama.metamodel.population.IPopulation;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.metamodel.shape.GamaShape;
import msi.gama.metamodel.shape.IShape;
import msi.gama.metamodel.shape.IShape.Type;
import msi.gama.runtime.IScope;
import msi.gama.util.GamaFont;
import msi.gama.util.GamaList;
import msi.gama.util.GamaListFactory;
import msi.gama.util.GamaMap;
import msi.gama.util.GamaMapFactory;
import msi.gama.util.IList;
import msi.gama.util.IMap;
import msi.gama.util.file.IGamaFile;
import msi.gama.util.graph.IGraph;
import msi.gaml.compilation.kernel.GamaClassLoader;
import msi.gaml.species.ISpecies;
import msi.gaml.types.GamaType;
import msi.gaml.types.IType;
import one.util.streamex.StreamEx;
import ummisco.gama.dev.utils.DEBUG;

/**
 * The Class FSTImplementation. Allows to provide common initializations to FST Configurations and do the dirty
 * work. Not thread / simulation safe.
 *
 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
 * @date 2 août 2023
 */
public class FSTImplementation extends SerialisationImplementation {

	/** The Constant zip. */
	boolean zip = true;

	/** The Constant JSON. */
	boolean json = false;

	static {
		DEBUG.ON();
	}

	/** The scope. */
	IScope scope;

	/** The json. */
	public FSTConfiguration jsonConf = initJson(FSTConfiguration.createJsonConfiguration(false, false));

	/** The bin. */
	public FSTConfiguration unsafeConf = initCommon(FSTConfiguration.createUnsafeBinaryConfiguration());

	/** The bin. */
	public FSTConfiguration binaryConf = initCommon(FSTConfiguration.createDefaultConfiguration());

	/** The configurations. */
	private final List<FSTConfiguration> configurations = Arrays.asList(jsonConf, unsafeConf, binaryConf);

	/** The saved graphs. */
	BiMap<IGraph, String> savedGraphs = HashBiMap.create();

	/** The saved graphs. */
	BiMap<IGamaFile, String> savedFiles = HashBiMap.create();

	/**
	 * Instantiates a new gama FST serialiser.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param scope
	 *            the scope.
	 * @date 5 août 2023
	 */
	public FSTImplementation(final boolean json, final boolean zip) {
		super();
		this.json = json;
		this.zip = zip;
		registerSerialisers();
	}

	/**
	 * Register serialisers.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @date 5 août 2023
	 */
	private void registerSerialisers() {

		register(GamaShape.class, new GamaFSTSerialiser<GamaShape>() {

			// The inner attributes of the shape should be saved (ie the ones that do not belong to the var names of the
			// species
			@Override
			public void write(final FSTObjectOutput out, final GamaShape toWrite) throws Exception {
				Double d = toWrite.getDepth();
				IShape.Type t = toWrite.getGeometricalType();
				out.writeDouble(d == null ? 0d : d);
				out.writeInt(t.ordinal());
				out.writeObject(toWrite.getInnerGeometry());
				out.writeObject(toWrite.getAgent());
			}

			@Override
			public GamaShape read(final IScope scope, final FSTObjectInput in) throws Exception {
				double d = in.readDouble();
				IShape.Type t = IShape.Type.values()[in.readInt()];
				GamaShape result = new GamaShape((Geometry) in.readObject());
				IAgent agent = (IAgent) in.readObject();
				if (agent != null) { result.setAgent(agent); }
				if (d > 0d) { result.setDepth(d); }
				if (t != Type.NULL) { result.setGeometricalType(t); }
				return result;
			}
		});

		register(PrecisionModel.Type.class, new GamaFSTSerialiser<PrecisionModel.Type>() {
			// Necessary because of the JSON mode that requires it -- maybe use it only for JSON ?
			@Override
			public void write(final FSTObjectOutput out, final PrecisionModel.Type o) throws Exception {
				out.writeStringUTF(switch (o.toString()) {
					default -> "F";
					case "FIXED" -> "F";
					case "FLOATING" -> "D";
					case "FLOATING_SINGLE" -> "S";
				}

				);
			}

			@Override
			public PrecisionModel.Type read(final IScope scope, final FSTObjectInput in) throws Exception {
				return switch (in.readStringUTF()) {
					default -> PrecisionModel.FIXED;
					case "F" -> PrecisionModel.FIXED;
					case "D" -> PrecisionModel.FLOATING;
					case "S SINGLE" -> PrecisionModel.FLOATING_SINGLE;
				};
			}

		});

		register(IAgent.class, new GamaFSTSerialiser<IAgent>() {

			@Override
			public void write(final FSTObjectOutput out, final IAgent o) throws Exception {
				out.writeObject(new AgentReference(o));
			}

			@Override
			public IAgent read(final IScope scope, final FSTObjectInput in) throws Exception {
				AgentReference ref = (AgentReference) in.readObject();
				return ref.getReferencedAgent(scope);
			}

		});

		register(IGraph.class, new GamaFSTSerialiser<IGraph>() {
			// This serializer is a way of "cheating" by considering that graphs are immutable throughout the
			// simulations
			// To use only in JSON.
			int i;

			@Override
			public void write(final FSTObjectOutput out, final IGraph o) throws Exception {
				if (!savedGraphs.containsKey(o)) { savedGraphs.put(o, "graph" + i++); }
				out.writeStringUTF(savedGraphs.get(o));
			}

			@Override
			public IGraph read(final IScope scope, final FSTObjectInput in) throws Exception {
				String key = in.readStringUTF();
				return savedGraphs.inverse().get(key);
			}

		});

		register(IGamaFile.class, new GamaFSTSerialiser<IGamaFile>() {
			// This serializer is a way of "cheating" by considering that files are immutable throughout the
			// simulations
			// To use only in JSON.

			@Override
			public void write(final FSTObjectOutput out, final IGamaFile o) throws Exception {
				if (!savedFiles.containsKey(o)) { savedFiles.put(o, o.getOriginalPath()); }
				out.writeStringUTF(savedFiles.get(o));
			}

			@Override
			public IGamaFile read(final IScope scope, final FSTObjectInput in) throws Exception {
				String key = in.readStringUTF();
				return savedFiles.inverse().get(key);
			}

		});

		register(IType.class, new GamaFSTSerialiser<IType>() {

			@Override
			public void write(final FSTObjectOutput out, final IType toWrite) throws Exception {
				out.writeStringUTF(toWrite.getGamlType().getName());
				if (toWrite.isCompoundType()) {
					out.writeObject(toWrite.getKeyType());
					out.writeObject(toWrite.getContentType());
				}
			}

			@Override
			public IType read(final IScope scope, final FSTObjectInput in) throws Exception {
				String name = in.readStringUTF();
				IType type = scope.getType(name);
				if (type.isCompoundType()) {
					IType key = (IType) in.readObject();
					IType content = (IType) in.readObject();
					return GamaType.from(type, key, content);
				}
				return type;
			}

		});

		register(IScope.class, new GamaFSTSerialiser<IScope>() {

			@Override
			public void write(final FSTObjectOutput out, final IScope toWrite) throws Exception {
				out.writeStringUTF(toWrite.getName());
			}

			@Override
			public IScope read(final IScope scope, final FSTObjectInput in) throws Exception {
				String name = in.readStringUTF();
				return scope.copy(name);
			}

		});

		register(ISpecies.class, new GamaFSTSerialiser<ISpecies>() {

			@Override
			public void write(final FSTObjectOutput out, final ISpecies o) throws Exception {
				out.writeStringUTF(o.getName());
			}

			@Override
			public ISpecies read(final IScope scope, final FSTObjectInput in) throws Exception {
				String name = in.readStringUTF();
				return scope.getModel().getSpecies(name);
			}

		});

		register(AgentReference.class, new GamaFSTSerialiser<AgentReference>() {

			@Override
			public void write(final FSTObjectOutput out, final AgentReference o) throws Exception {
				out.writeObject(o.species());
				out.writeObject(o.index());
			}

			@Override
			public AgentReference read(final IScope scope, final FSTObjectInput in) throws Exception {
				return new AgentReference((String[]) in.readObject(), (Integer[]) in.readObject());
			}
		});

		register(SerialisedAgent.class, new GamaFSTSerialiser<SerialisedAgent>() {

			@Override
			public void write(final FSTObjectOutput out, final SerialisedAgent o) throws Exception {
				out.writeInt(o.index());
				out.writeObject(o.attributes());
			}

			@Override
			public SerialisedAgent read(final IScope scope, final FSTObjectInput in) throws Exception {
				return new SerialisedAgent(in.readInt(), (Map<String, Object>) in.readObject());
			}
		});

		register(SerialisedPopulation.class, new GamaFSTSerialiser<SerialisedPopulation>() {

			@Override
			public void write(final FSTObjectOutput out, final SerialisedPopulation o) throws Exception {
				out.writeStringUTF(o.speciesName());
				out.writeObject(o.agents());
			}

			@Override
			public SerialisedPopulation read(final IScope scope, final FSTObjectInput in) throws Exception {
				return new SerialisedPopulation(in.readStringUTF(), (List<SerialisedAgent>) in.readObject());
			}
		});

		register(GamaGeometryFactory.class, new GamaFSTSerialiser<GamaGeometryFactory>() {

			@Override
			public void write(final FSTObjectOutput out, final GamaGeometryFactory o) throws Exception {
				out.writeStringUTF("*GGF*");
			}

			@Override
			public GamaGeometryFactory read(final IScope scope, final FSTObjectInput in) throws Exception {
				in.readStringUTF();
				return GeometryUtils.GEOMETRY_FACTORY;
			}
		});

		register(GamaFont.class, new GamaFSTSerialiser<GamaFont>() {

			@Override
			public void write(final FSTObjectOutput out, final GamaFont o) throws Exception {
				out.writeStringUTF(o.getName());
				out.writeInt(o.getStyle());
				out.writeInt(o.getSize());
			}

			@Override
			public GamaFont read(final IScope scope, final FSTObjectInput in) throws Exception {
				return new GamaFont(in.readStringUTF(), in.readInt(), in.readInt());
			}
		});

		register(IMap.class, new GamaFSTSerialiser<IMap>() {

			@Override
			public void write(final FSTObjectOutput out, final IMap o) throws Exception {
				out.writeObject(o.getGamlType().getKeyType());
				out.writeObject(o.getGamlType().getContentType());
				out.writeBoolean(o.isOrdered());
				out.writeInt(o.size());
				o.forEach((k, v) -> {
					try {
						out.writeObject(k);
						out.writeObject(v);
					} catch (IOException e) {
						e.printStackTrace();
					}
				});
			}

			@Override
			public IMap read(final IScope scope, final FSTObjectInput in) throws Exception {
				IType k = (IType) in.readObject();
				IType c = (IType) in.readObject();
				boolean ordered = in.readBoolean();
				IMap<Object, Object> result = GamaMapFactory.create(k, c, ordered);
				int size = in.readInt();
				for (int i = 0; i < size; i++) { result.put(in.readObject(), in.readObject()); }
				return result;
			}

		});

		register(IList.class, new GamaFSTSerialiser<IList>() {

			@Override
			public void write(final FSTObjectOutput out, final IList o) throws Exception {
				out.writeObject(o.getGamlType().getContentType());
				out.writeInt(o.size());
				o.forEach(v -> {
					try {
						out.writeObject(v);
					} catch (IOException e) {
						e.printStackTrace();
					}
				});
			}

			@Override
			public IList read(final IScope scope, final FSTObjectInput in) throws Exception {
				IType c = (IType) in.readObject();
				IList<Object> result = GamaListFactory.create(c);
				int size = in.readInt();
				for (int i = 0; i < size; i++) { result.add(in.readObject()); }
				return result;
			}

		});

		register(GamaCoordinateSequenceFactory.class, new GamaFSTSerialiser<GamaCoordinateSequenceFactory>() {

			@Override
			public void write(final FSTObjectOutput out, final GamaCoordinateSequenceFactory o) throws Exception {
				out.writeStringUTF("*GCSF*");
			}

			@Override
			public GamaCoordinateSequenceFactory read(final IScope scope, final FSTObjectInput in) throws Exception {
				in.readStringUTF();
				return GeometryUtils.GEOMETRY_FACTORY.getCoordinateSequenceFactory();
			}
		});
	}

	/**
	 * Register.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param <T>
	 *            the generic type
	 * @param clazz
	 *            the clazz
	 * @date 5 août 2023
	 */
	public <T> void register(final Class<T> clazz, final GamaFSTSerialiser<T> ser) {
		ser.setName(clazz.getSimpleName());
		configurations.forEach(c -> {
			c.registerSerializer(clazz, ser, true);
			c.registerCrossPlatformClassMapping(ser.shortName, clazz.getName());
		});
	}

	/**
	 * The Class GamaFSTSerialiser.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @date 5 août 2023
	 */
	abstract class GamaFSTSerialiser<T> extends FSTBasicObjectSerializer {

		/** The short name. */
		String shortName;

		/** The Constant CLASS_PREFIX. */
		static final String CLASS_PREFIX = "";

		/**
		 * Instantiates a new gama FST serialiser.
		 *
		 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
		 * @param name
		 *            the name
		 * @date 7 août 2023
		 */
		void setName(final String name) { shortName = CLASS_PREFIX + name; }

		/**
		 * Instantiate.
		 *
		 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
		 * @param objectClass
		 *            the object class
		 * @param in
		 *            the in
		 * @param serializationInfo
		 *            the serialization info
		 * @param referencee
		 *            the referencee
		 * @param streamPosition
		 *            the stream position
		 * @return the t
		 * @throws Exception
		 *             the exception
		 * @date 7 août 2023
		 */
		@Override
		public final T instantiate(final Class objectClass, final FSTObjectInput in,
				final FSTClazzInfo serializationInfo, final FSTFieldInfo referencee, final int streamPosition)
				throws Exception {
			T result = read(scope, in);
			in.registerObject(result, streamPosition, serializationInfo, referencee);
			return result;
		}

		/**
		 * Write object.
		 *
		 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
		 * @param out
		 *            the out
		 * @param toWrite
		 *            the to write
		 * @param clzInfo
		 *            the clz info
		 * @param referencedBy
		 *            the referenced by
		 * @param streamPosition
		 *            the stream position
		 * @throws IOException
		 *             Signals that an I/O exception has occurred.
		 * @date 7 août 2023
		 */
		@Override
		public void writeObject(final FSTObjectOutput out, final Object toWrite, final FSTClazzInfo clzInfo,
				final FSTFieldInfo referencedBy, final int streamPosition) throws IOException {
			try {
				write(out, (T) toWrite);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		/**
		 * Write. The method to redefine to allow for
		 *
		 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
		 * @param out
		 *            the out
		 * @param toWrite
		 *            the to write
		 * @date 5 août 2023
		 */
		public void write(final FSTObjectOutput out, final T toWrite) throws Exception {}

		/**
		 * Read.
		 *
		 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
		 * @param in
		 *            the in
		 * @return the t
		 * @date 5 août 2023
		 */
		abstract public T read(IScope scope, FSTObjectInput in) throws Exception;

	}

	/**
	 * Inits the common.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param conf
	 *            the conf
	 * @return the FST configuration
	 * @date 2 août 2023
	 */
	private FSTConfiguration initCommon(final FSTConfiguration conf) {
		conf.setClassLoader(GamaClassLoader.getInstance());
		conf.setForceSerializable(true);
		conf.setShareReferences(true);

		return conf;
	}

	/**
	 * Inits the json.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param conf
	 *            the conf
	 * @return the FST configuration
	 * @date 2 août 2023
	 */
	private FSTConfiguration initJson(final FSTConfiguration conf) {
		initCommon(conf);
		conf.setJsonFieldNames(new FSTJsonFieldNames("t", "o", "st", "s", "e", "v", "r"));
		conf.registerCrossPlatformClassMappingUseSimpleName(GamaPoint.class, GamaPoint[].class, Integer[].class,
				String[].class, MinimalAgent.class, GamlAgent.class, SimulationAgent.Type.class, Envelope.class,
				LinearRing.class, LinearRing[].class, Polygon.class, GamaCoordinateSequence.class, IShape.Type.class,
				GamaShape.ShapeData.class, GamaList.class, GamaMap.class);
		conf.setShareReferences(false); // by default
		return conf;
	}

	/**
	 * Inits the bin.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param conf
	 *            the conf
	 * @return the FST configuration
	 * @date 2 août 2023
	 */
	private FSTConfiguration initBin(final FSTConfiguration conf) {
		initCommon(conf);
		conf.setShareReferences(true); // by default
		return conf;
	}

	/**
	 * Save.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param sim
	 *            the sim
	 * @date 6 août 2023
	 */
	@Override
	public void save(final SimulationAgent sim) {
		FSTConfiguration conf = json ? jsonConf : binaryConf;
		SerialisedAgent sa = new SerialisedAgent(sim);
		// Retrieving and putting apart the graphs
		Set<Map.Entry<String, Object>> entries = sa.attributes().entrySet();
		for (Map.Entry<String, Object> entry : entries) {
			if (entry.getValue() instanceof IGraph g) {
				savedGraphs.put(g, entry.getKey());
				entries.remove(entry.getKey());
			}
		}
		byte[] state = conf.asByteArray(sa);
		if (zip) { state = zip(state); }
		DEBUG.OUT("Size of serialised simulation = " + state.length);
		// DEBUG.OUT(new String(state));
		if (current == null) {
			current = history.setRoot(state);
		} else {
			current = current.addChild(state);
		}
	}

	/**
	 * Restore.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param sim
	 *            the sim
	 * @date 6 août 2023
	 */
	@Override
	public void restore(final SimulationAgent sim) {
		scope = sim.getScope();
		current = current.getParent();
		try {
			if (current != null) {
				byte[] input = current.getData();
				if (zip) { input = unzip(input); }
				FSTConfiguration conf = json ? jsonConf : binaryConf;
				SerialisedAgent previousSim = (SerialisedAgent) conf.asObject(input);
				restoreSimulation(sim, previousSim);
			}
		} catch (Throwable e) {
			e.printStackTrace();
		} finally {
			scope = null;
		}
	}

	/**
	 * Restore agent.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param agent
	 *            the agent
	 * @param image
	 *            the image
	 * @date 6 août 2023
	 */
	void restoreAgent(final IAgent agent, final SerialisedAgent image) {
		// DEBUG.OUT("Restoring " + agent.getName() + " from " + agent.getOrCreateAttributes() + " to "
		// + image.attributes());
		image.attributes().forEach((name, v) -> {
			if (agent instanceof IMacroAgent host && v instanceof SerialisedPopulation sp) {
				IPopulation<? extends IAgent> pop = host.getMicroPopulation(name);
				if (pop != null) { restorePopulation(pop, sp); }
			} else {
				agent.setDirectVarValue(scope, name, v);
			}
		});
	}

	/**
	 * Restore population.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param pop
	 *            the pop
	 * @param sp
	 *            the sp
	 * @date 6 août 2023
	 */
	private void restorePopulation(final IPopulation<? extends IAgent> pop, final SerialisedPopulation sp) {
		Map<Integer, IAgent> agents = StreamEx.of(pop).toMap(IAgent::getIndex, each -> each);
		Map<Integer, SerialisedAgent> images = StreamEx.of(sp.agents()).toMap(SerialisedAgent::getIndex, each -> each);
		Set<Entry<Integer, SerialisedAgent>> imagesEntries = images.entrySet();
		for (Map.Entry<Integer, SerialisedAgent> entry : imagesEntries) {
			int index = entry.getKey();
			// We gather the corresponding agent and remove it from this temp map
			IAgent agent = agents.remove(index);
			// If the agent is not found we create a new one
			if (agent == null) { agent = pop.getOrCreateAgent(scope, index); }
			restoreAgent(agent, entry.getValue());
		}
		// The remaining agents in the map are killed
		agents.forEach((i, a) -> { a.primDie(scope); });
	}

	/**
	 * Restore.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param sim
	 *            the sim
	 * @param image
	 *            the new sim
	 * @date 6 août 2023
	 */
	void restoreSimulation(final SimulationAgent sim, final SerialisedAgent image) {
		final Map<String, Object> attr = image.attributes();
		Double seedValue = (Double) attr.remove(IKeyword.SEED);
		String rngValue = (String) attr.remove(IKeyword.RNG);
		Integer usageValue = (Integer) attr.remove(SimulationAgent.USAGE);
		// Update Attributes and micropopulations
		this.restoreAgent(sim, image);
		// Update RNG
		sim.setRandomGenerator(new RandomUtils(seedValue, rngValue));
		sim.setUsage(usageValue);
		// Update Clock
		final Integer cycle = (Integer) sim.getAttribute(SimulationAgent.CYCLE);
		sim.getClock().setCycle(cycle);
	}

}
