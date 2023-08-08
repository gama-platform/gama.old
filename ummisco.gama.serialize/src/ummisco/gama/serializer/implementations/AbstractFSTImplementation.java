/*******************************************************************************************************
 *
 * AbstractFSTImplementation.java, in ummisco.gama.serialize, is part of the source code of the GAMA modeling and
 * simulation platform (v.1.9.2).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
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
import msi.gama.kernel.simulation.SimulationAgent;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.GamaShape;
import msi.gama.metamodel.shape.IShape;
import msi.gama.metamodel.shape.IShape.Type;
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
public abstract class AbstractFSTImplementation extends AbstractSerialisationImplementation<SerialisedAgent> {

	/** The fst. */
	final FSTConfiguration fst;

	/**
	 * Instantiates a new gama FST serialiser.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param scope
	 *            the scope.
	 * @date 5 août 2023
	 */
	public AbstractFSTImplementation(final FSTConfiguration conf, final boolean zip, final boolean save) {
		super(zip, save);
		fst = initConfiguration(conf);
		registerSerialisers();
	}

	/**
	 * Register serialisers.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @date 5 août 2023
	 */
	protected void registerSerialisers() {

		register(GamaShape.class, new GamaFSTSerialiser<GamaShape>() {

			// TODO The inner attributes of the shape should be saved (ie the ones that do not belong to the var names
			// of the species
			@Override
			public void serialise(final FSTObjectOutput out, final GamaShape toWrite) throws Exception {
				Double d = toWrite.getDepth();
				IShape.Type t = toWrite.getGeometricalType();
				out.writeDouble(d == null ? 0d : d);
				out.writeInt(t.ordinal());
				out.writeObject(toWrite.getInnerGeometry());
				out.writeObject(toWrite.getAgent());
			}

			@Override
			public GamaShape deserialise(final IScope scope, final FSTObjectInput in) throws Exception {
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

		register(IAgent.class, new GamaFSTSerialiser<IAgent>() {

			@Override
			public void serialise(final FSTObjectOutput out, final IAgent o) throws Exception {
				out.writeObject(new AgentReference(o));
			}

			@Override
			public IAgent deserialise(final IScope scope, final FSTObjectInput in) throws Exception {
				AgentReference ref = (AgentReference) in.readObject();
				return ref.getReferencedAgent(scope);
			}

		});

		register(IType.class, new GamaFSTSerialiser<IType>() {

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

		register(IScope.class, new GamaFSTSerialiser<IScope>() {

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

		register(ISpecies.class, new GamaFSTSerialiser<ISpecies>() {

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

		register(AgentReference.class, new GamaFSTSerialiser<AgentReference>() {

			@Override
			public void serialise(final FSTObjectOutput out, final AgentReference o) throws Exception {
				out.writeObject(o.species());
				out.writeObject(o.index());
			}

			@Override
			public AgentReference deserialise(final IScope scope, final FSTObjectInput in) throws Exception {
				return new AgentReference((String[]) in.readObject(), (Integer[]) in.readObject());
			}
		});

		register(SerialisedAgent.class, new GamaFSTSerialiser<SerialisedAgent>() {

			@Override
			public void serialise(final FSTObjectOutput out, final SerialisedAgent o) throws Exception {
				out.writeInt(o.index());
				out.writeObject(o.attributes());
			}

			@Override
			public SerialisedAgent deserialise(final IScope scope, final FSTObjectInput in) throws Exception {
				return new SerialisedAgent(in.readInt(), (Map<String, Object>) in.readObject());
			}
		});

		register(SerialisedPopulation.class, new GamaFSTSerialiser<SerialisedPopulation>() {

			@Override
			public void serialise(final FSTObjectOutput out, final SerialisedPopulation o) throws Exception {
				out.writeStringUTF(o.speciesName());
				out.writeObject(o.agents());
			}

			@Override
			public SerialisedPopulation deserialise(final IScope scope, final FSTObjectInput in) throws Exception {
				return new SerialisedPopulation(in.readStringUTF(), (List<SerialisedAgent>) in.readObject());
			}
		});

		register(GamaGeometryFactory.class, new GamaFSTSerialiser<GamaGeometryFactory>() {

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

		register(GamaFont.class, new GamaFSTSerialiser<GamaFont>() {

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

		register(IMap.class, new GamaFSTSerialiser<IMap>() {

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

		register(IList.class, new GamaFSTSerialiser<IList>() {

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

		register(GamaCoordinateSequenceFactory.class, new GamaFSTSerialiser<GamaCoordinateSequenceFactory>() {

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
	public <T> void register(final Class<T> clazz, final GamaFSTSerialiser<T> ser) {
		ser.setName(clazz.getSimpleName());
		fst.registerSerializer(clazz, ser, true);
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
		public void serialise(final FSTObjectOutput out, final T toWrite) throws Exception {}

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

	/**
	 * Inits the common.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param conf
	 *            the conf
	 * @return the FST configuration
	 * @date 2 août 2023
	 */
	public FSTConfiguration initConfiguration(final FSTConfiguration conf) {
		conf.setClassLoader(GamaClassLoader.getInstance());
		conf.setForceSerializable(true);
		conf.setShareReferences(true);
		return conf;
	}

	@Override
	protected SerialisedAgent encodeToSerialisedForm(final SimulationAgent agent) {
		return new SerialisedAgent(agent);
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
	protected byte[] write(final SerialisedAgent sa) {
		return fst.asByteArray(sa);
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
	public SerialisedAgent read(final byte[] input) {
		return (SerialisedAgent) fst.asObject(input);
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
	@Override
	public void restoreFromSerialisedForm(final SimulationAgent sim, final SerialisedAgent image) {
		SerialisedAgentFactory.restoreSimulation(scope, sim, image);
	}

}
