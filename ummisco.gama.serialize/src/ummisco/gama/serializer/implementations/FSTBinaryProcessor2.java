/*******************************************************************************************************
 *
 * FSTBinaryProcessor.java, in ummisco.gama.serialize, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.9.3).
 *
 * (c) 2007-2024 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gama.serializer.implementations;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.locationtech.jts.geom.Geometry;
import org.nustaq.serialization.FSTBasicObjectSerializer;
import org.nustaq.serialization.FSTClazzInfo;
import org.nustaq.serialization.FSTClazzInfo.FSTFieldInfo;
import org.nustaq.serialization.FSTConfiguration;
import org.nustaq.serialization.FSTObjectInput;
import org.nustaq.serialization.FSTObjectOutput;

import msi.gama.common.geometry.GamaCoordinateSequenceFactory;
import msi.gama.common.geometry.GamaGeometryFactory;
import msi.gama.common.geometry.GeometryUtils;
import msi.gama.common.interfaces.ISerialisationConstants;
import msi.gama.metamodel.agent.AgentReference;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.agent.ISerialisedAgent;
import msi.gama.metamodel.agent.SerialisedAgent;
import msi.gama.metamodel.population.ISerialisedPopulation;
import msi.gama.metamodel.population.SerialisedGrid;
import msi.gama.metamodel.population.SerialisedPopulation;
import msi.gama.metamodel.shape.GamaShape;
import msi.gama.metamodel.shape.GamaShapeFactory;
import msi.gama.metamodel.shape.IShape;
import msi.gama.metamodel.shape.IShape.Type;
import msi.gama.metamodel.topology.grid.IGrid;
import msi.gama.runtime.IScope;
import msi.gama.util.GamaFont;
import msi.gama.util.GamaListFactory;
import msi.gama.util.GamaMapFactory;
import msi.gama.util.IList;
import msi.gama.util.IMap;
import msi.gaml.compilation.kernel.GamaClassLoader;
import msi.gaml.species.ISpecies;
import msi.gaml.types.GamaType;
import msi.gaml.types.IType;

/**
 * The Class FSTImplementation. Allows to provide common initializations to FST Configurations and do the dirty work.
 * Not thread / simulation safe.
 *
 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
 * @date 2 août 2023
 */
public class FSTBinaryProcessor2 implements ISerialisationProcessor, ISerialisationConstants {

	/** The fst. */
	private final FSTConfiguration fst;

	/** The in agent. */
	protected boolean inAgent;

	/** The target agent. */
	protected IAgent targetAgent;

	/** The scope. */
	protected IScope scope;

	/** The instance. */
	// public static final FSTBinaryProcessor INSTANCE = new FSTBinaryProcessor();

	/**
	 * Instantiates a new gama FST serialiser.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param scope
	 *            the scope.
	 * @date 5 août 2023
	 */
	public FSTBinaryProcessor2() {
		fst = FSTConfiguration.createDefaultConfiguration();
		initConfiguration();
	}

	/**
	 * Restore simulation from.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param sim
	 *            the sim
	 * @param some
	 *            the some
	 * @date 8 août 2023
	 */
	@Override
	public void restoreAgentFromBytes(final IAgent sim, final byte[] input) {
		try {
			targetAgent = sim;
			scope = sim.getScope();
			// return (IAgent)
			fst.asObject(input);
			// SerialisedAgent sa = (SerialisedAgent) fst.asObject(input);
			// sa.restoreAs(sim.getScope(), sim);
		} finally {
			scope = null;
			targetAgent = null;
		}
	}

	/**
	 * Save simulation to bytes.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param sim
	 *            the sim
	 * @return the byte[]
	 * @date 8 août 2023
	 */
	@Override
	public byte[] saveObjectToBytes(final IScope scope, final Object sim) {
		inAgent = false;
		return fst.asByteArray(sim);
	}

	/**
	 * Restore object from bytes.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param scope
	 *            the scope
	 * @param input
	 *            the input
	 * @return the object
	 * @date 29 sept. 2023
	 */
	@Override
	public Object createObjectFromBytes(final IScope scope, final byte[] input) {
		try {
			this.scope = scope;
			return fst.asObject(input);
		} finally {
			this.scope = null;
		}

	}

	/**
	 * Register serialisers.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @date 5 août 2023
	 */
	protected void registerSerialisers() {

		register(GamaShape.class, new FSTIndividualSerialiser<GamaShape>() {

			// TODO The inner attributes of the shape should be saved (ie the ones that do not belong to the var names
			// of the species
			@Override
			public void serialise(final FSTObjectOutput out, final GamaShape toWrite) throws Exception {
				Double d = toWrite.getDepth();
				IShape.Type t = toWrite.getGeometricalType();
				out.writeDouble(d == null ? 0d : d);
				out.writeInt(t.ordinal());
				out.writeObject(toWrite.getInnerGeometry());
				out.writeObject(AgentReference.of(toWrite.getAgent()));
			}

			@Override
			public GamaShape deserialise(final IScope scope, final FSTObjectInput in) throws Exception {
				double d = in.readDouble();
				IShape.Type t = IShape.Type.values()[in.readInt()];
				GamaShape result = GamaShapeFactory.createFrom((Geometry) in.readObject());
				AgentReference agent = (AgentReference) in.readObject(AgentReference.class);
				if (agent != AgentReference.NULL) { result.setAgent(agent.getReferencedAgent(scope)); }
				if (d > 0d) { result.setDepth(d); }
				if (t != Type.NULL) { result.setGeometricalType(t); }
				return result;
			}
		});

		register(IAgent.class, new FSTIndividualSerialiser<IAgent>() {

			@Override
			public void serialise(final FSTObjectOutput out, final IAgent o) throws Exception {
				if (inAgent) {
					out.writeObject(AgentReference.of(o));
				} else {
					inAgent = true;
					try {
						out.writeObject(SerialisedAgent.of(o, true));
					} finally {
						inAgent = false;
					}
				}
			}

			@Override
			public IAgent deserialise(final IScope scope, final FSTObjectInput in) throws Exception {
				Object object = in.readObject(AgentReference.class, SerialisedAgent.class);
				if (object instanceof AgentReference ref) return ref.getReferencedAgent(scope);

				if (object instanceof SerialisedAgent sa) {
					if (targetAgent == null) return sa.recreateIn(scope);
					try {
						sa.restoreAs(scope, targetAgent);
						IAgent result = targetAgent;
						return result;
					} finally {
						targetAgent = null;
					}
				}
				return null;
			}

		});

		register(SerialisedAgent.class, new FSTIndividualSerialiser<SerialisedAgent>() {

			@Override
			public void serialise(final FSTObjectOutput out, final SerialisedAgent o) throws Exception {
				out.writeInt(o.index());
				out.writeStringUTF(o.species());
				out.writeObject(o.attributes());
				out.writeObject(o.innerPopulations());
			}

			@SuppressWarnings ("unchecked")
			@Override
			public SerialisedAgent deserialise(final IScope scope, final FSTObjectInput in) throws Exception {
				int index = in.readInt();
				String species = in.readStringUTF();
				Map<String, Object> attributes = (Map<String, Object>) in.readObject(Map.class);
				Map<String, ISerialisedPopulation> pops = (Map<String, ISerialisedPopulation>) in.readObject(Map.class);
				return new SerialisedAgent(index, species, attributes, pops);
			}
		});

		register(IType.class, new FSTIndividualSerialiser<IType>() {

			@Override
			public void serialise(final FSTObjectOutput out, final IType toWrite) throws Exception {
				out.writeStringUTF(toWrite.getGamlType().getName());
				if (toWrite.isCompoundType()) {
					out.writeObject(toWrite.getKeyType());
					out.writeObject(toWrite.getContentType());
				}
			}

			@Override
			public IType deserialise(final IScope scope, final FSTObjectInput in) throws Exception {
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

		register(IScope.class, new FSTIndividualSerialiser<IScope>() {

			@Override
			public void serialise(final FSTObjectOutput out, final IScope toWrite) throws Exception {
				out.writeStringUTF(toWrite.getName());
			}

			@Override
			public IScope deserialise(final IScope scope, final FSTObjectInput in) throws Exception {
				String name = in.readStringUTF();
				return scope.copy(name);
			}

		});

		register(ISpecies.class, new FSTIndividualSerialiser<ISpecies>() {

			@Override
			public void serialise(final FSTObjectOutput out, final ISpecies o) throws Exception {
				out.writeStringUTF(o.getName());
			}

			@Override
			public ISpecies deserialise(final IScope scope, final FSTObjectInput in) throws Exception {
				String name = in.readStringUTF();
				return scope.getModel().getSpecies(name);
			}

		});

		register(AgentReference.class, new FSTIndividualSerialiser<AgentReference>() {

			@Override
			public void serialise(final FSTObjectOutput out, final AgentReference o) throws Exception {
				out.writeObject(o.species());
				out.writeObject(o.index());
			}

			@Override
			public AgentReference deserialise(final IScope scope, final FSTObjectInput in) throws Exception {
				return AgentReference.of((String[]) in.readObject(), (Integer[]) in.readObject());
			}
		});

		register(SerialisedPopulation.class, new FSTIndividualSerialiser<SerialisedPopulation>() {

			@Override
			public void serialise(final FSTObjectOutput out, final SerialisedPopulation o) throws Exception {
				out.writeStringUTF(o.speciesName());
				out.writeObject(o.agents());
			}

			@SuppressWarnings ("unchecked")
			@Override
			public SerialisedPopulation deserialise(final IScope scope, final FSTObjectInput in) throws Exception {
				return new SerialisedPopulation(in.readStringUTF(), (List<ISerialisedAgent>) in.readObject());
			}
		});

		register(SerialisedGrid.class, new FSTIndividualSerialiser<SerialisedGrid>() {

			@Override
			public void serialise(final FSTObjectOutput out, final SerialisedGrid o) throws Exception {
				out.writeStringUTF(o.speciesName());
				out.writeObject(o.agents());
				out.writeObject(o.matrix());
			}

			@SuppressWarnings ("unchecked")
			@Override
			public SerialisedGrid deserialise(final IScope scope, final FSTObjectInput in) throws Exception {
				return new SerialisedGrid(in.readStringUTF(), (List<ISerialisedAgent>) in.readObject(),
						(IGrid) in.readObject());
			}
		});

		register(GamaGeometryFactory.class, new FSTIndividualSerialiser<GamaGeometryFactory>() {

			@Override
			public void serialise(final FSTObjectOutput out, final GamaGeometryFactory o) throws Exception {
				out.writeStringUTF("*GGF*");
			}

			@Override
			public GamaGeometryFactory deserialise(final IScope scope, final FSTObjectInput in) throws Exception {
				in.readStringUTF();
				return GeometryUtils.GEOMETRY_FACTORY;
			}
		});

		register(GamaFont.class, new FSTIndividualSerialiser<GamaFont>() {

			@Override
			public void serialise(final FSTObjectOutput out, final GamaFont o) throws Exception {
				out.writeStringUTF(o.getName());
				out.writeInt(o.getStyle());
				out.writeInt(o.getSize());
			}

			@Override
			public GamaFont deserialise(final IScope scope, final FSTObjectInput in) throws Exception {
				return new GamaFont(in.readStringUTF(), in.readInt(), in.readInt());
			}
		});

		register(IMap.class, new FSTIndividualSerialiser<IMap>() {

			@Override
			public void serialise(final FSTObjectOutput out, final IMap o) throws Exception {
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
			public IMap deserialise(final IScope scope, final FSTObjectInput in) throws Exception {
				IType k = (IType) in.readObject();
				IType c = (IType) in.readObject();
				boolean ordered = in.readBoolean();
				IMap<Object, Object> result = GamaMapFactory.create(k, c, ordered);
				int size = in.readInt();
				for (int i = 0; i < size; i++) { result.put(in.readObject(), in.readObject()); }
				return result;
			}

		});

		register(IList.class, new FSTIndividualSerialiser<IList>() {

			@Override
			public void serialise(final FSTObjectOutput out, final IList o) throws Exception {
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
			public IList deserialise(final IScope scope, final FSTObjectInput in) throws Exception {
				IType c = (IType) in.readObject();
				IList<Object> result = GamaListFactory.create(c);
				int size = in.readInt();
				for (int i = 0; i < size; i++) { result.add(in.readObject()); }
				return result;
			}

		});

		register(GamaCoordinateSequenceFactory.class, new FSTIndividualSerialiser<GamaCoordinateSequenceFactory>() {

			@Override
			public void serialise(final FSTObjectOutput out, final GamaCoordinateSequenceFactory o) throws Exception {
				out.writeStringUTF("*GCSF*");
			}

			@Override
			public GamaCoordinateSequenceFactory deserialise(final IScope scope, final FSTObjectInput in)
					throws Exception {
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
	public <T> void register(final Class<T> clazz, final FSTIndividualSerialiser<T> ser) {
		ser.setName(clazz.getSimpleName());
		fst.registerSerializer(clazz, ser, true);
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
	public void initConfiguration() {
		fst.setClassLoader(GamaClassLoader.getInstance());
		fst.setForceSerializable(true);
		fst.setShareReferences(true);
		registerSerialisers();
	}

	/**
	 * The Class FSTIndividualSerialiser.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param <T>
	 *            the generic type
	 * @date 21 févr. 2024
	 */
	abstract class FSTIndividualSerialiser<T> extends FSTBasicObjectSerializer {

		/** The short name. */
		String shortName;

		/**
		 * Instantiates a new gama FST serialiser.
		 *
		 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
		 * @param name
		 *            the name
		 * @date 7 août 2023
		 */
		void setName(final String name) { shortName = ISerialisationConstants.CLASS_PREFIX + name; }

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

			T result = deserialise(scope, in);
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
		@SuppressWarnings ("unchecked")
		@Override
		public void writeObject(final FSTObjectOutput out, final Object toWrite, final FSTClazzInfo clzInfo,
				final FSTFieldInfo referencedBy, final int streamPosition) throws IOException {
			try {
				serialise(out, (T) toWrite);
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
		abstract public void serialise(final FSTObjectOutput out, final T toWrite) throws Exception;

		/**
		 * Read.
		 *
		 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
		 * @param in
		 *            the in
		 * @return the t
		 * @date 5 août 2023
		 */
		abstract public T deserialise(IScope scope, FSTObjectInput in) throws Exception;

	}

}
