/*******************************************************************************************************
 *
 * FSTAbstractProcessor.java, in ummisco.gama.serialize, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gama.serializer.implementations;

import java.io.IOException;
import java.util.ArrayList;
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
import msi.gama.metamodel.population.IPopulation;
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
public abstract class FSTAbstractProcessor extends AbstractSerialisationProcessor<SerialisedAgent> {

	/**
	 * The Class AgentReference.
	 */
	public record AgentReference(String[] species, Integer[] index) {

		/**
		 * Instantiates a new reference to agent.
		 *
		 * @param agt
		 *            the agt
		 */
		public AgentReference(final IAgent agt) {
			this(buildSpeciesArray(agt), buildIndicesArray(agt));
		}

		@Override
		public String toString() {
			String res = "";
			for (int i = 0; i < species.length; i++) { res = "/" + species[i] + index[i]; }
			return res;
		}

		/**
		 * Gets the referenced agent.
		 *
		 * @param sim
		 *            the sim
		 * @return the referenced agent
		 */
		public IAgent getReferencedAgent(final IScope scope) {
			SimulationAgent sim = scope.getSimulation();
			IPopulation<? extends IAgent> pop = sim.getPopulationFor(species[species.length - 1]);
			if (pop == null) { pop = sim.getPopulation(); }
			IAgent referencedAgt = pop.getOrCreateAgent(scope, index[index.length - 1]);

			for (int i = index.length - 2; i >= 0; i--) {
				pop = sim.getPopulationFor(species[i]);
				referencedAgt = pop.getOrCreateAgent(scope, index[i]);
			}
			return referencedAgt;
		}

		/**
		 * Gets the last index.
		 *
		 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
		 * @return the last index
		 * @date 6 août 2023
		 */
		public Integer getLastIndex() { return index[index.length - 1]; }

		/**
		 * Builds the species array.
		 *
		 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
		 * @param a
		 *            the a
		 * @return the string[]
		 * @date 7 août 2023
		 */
		static String[] buildSpeciesArray(final IAgent a) {
			List<String> species = new ArrayList<>();
			species.add(a.getSpeciesName());
			IAgent host = a.getHost();
			while (host != null && !(host instanceof SimulationAgent)) {
				species.add(host.getSpeciesName());
				host = host.getHost();
			}
			return species.toArray(new String[0]);
		}

		/**
		 * Builds the species array.
		 *
		 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
		 * @param a
		 *            the a
		 * @return the int[]
		 * @date 7 août 2023
		 */
		static Integer[] buildIndicesArray(final IAgent a) {
			List<Integer> species = new ArrayList<>();
			species.add(a.getIndex());
			IAgent host = a.getHost();
			while (host != null && !(host instanceof SimulationAgent)) {
				species.add(host.getIndex());
				host = host.getHost();
			}
			return species.toArray(new Integer[0]);
		}

	}

	/** The fst. */
	FSTConfiguration fst;

	/** The current scope. */
	IScope currentScope;

	/**
	 * Instantiates a new gama FST serialiser.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param scope
	 *            the scope.
	 * @date 5 août 2023
	 */
	public FSTAbstractProcessor(final FSTConfiguration conf) {
		fst = initConfiguration(conf);
	}

	/**
	 * Register serialisers.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @date 5 août 2023
	 */
	protected void registerSerialisers(final FSTConfiguration conf) {

		register(conf, GamaShape.class, new GamaFSTSerialiser<GamaShape>() {

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
				GamaShape result = GamaShapeFactory.createFrom((Geometry) in.readObject());
				IAgent agent = (IAgent) in.readObject();
				if (agent != null) { result.setAgent(agent); }
				if (d > 0d) { result.setDepth(d); }
				if (t != Type.NULL) { result.setGeometricalType(t); }
				return result;
			}
		});

		register(conf, IAgent.class, new GamaFSTSerialiser<IAgent>() {

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

		register(conf, IType.class, new GamaFSTSerialiser<IType>() {

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

		register(conf, IScope.class, new GamaFSTSerialiser<IScope>() {

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

		register(conf, ISpecies.class, new GamaFSTSerialiser<ISpecies>() {

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

		register(conf, AgentReference.class, new GamaFSTSerialiser<AgentReference>() {

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

		// register(conf,SerialisedSimulationHeader.class, new GamaFSTSerialiser<SerialisedSimulationHeader>() {
		//
		// @Override
		// public void serialise(final FSTObjectOutput out, final SerialisedSimulationHeader o) throws Exception {
		// out.writeStringUTF(o.pathToModel());
		// out.writeStringUTF(o.nameOfExperiment());
		// }
		//
		// @Override
		// public SerialisedSimulationHeader deserialise(final IScope scope, final FSTObjectInput in)
		// throws Exception {
		// return new SerialisedSimulationHeader(in.readStringUTF(), in.readStringUTF());
		// }
		// });

		register(conf, SerialisedAgent.class, new GamaFSTSerialiser<SerialisedAgent>() {

			@Override
			public void serialise(final FSTObjectOutput out, final SerialisedAgent o) throws Exception {
				// out.writeObject(o.ref());
				out.writeInt(o.index());
				out.writeObject(o.attributes());
			}

			@SuppressWarnings ("unchecked")
			@Override
			public SerialisedAgent deserialise(final IScope scope, final FSTObjectInput in) throws Exception {
				return new SerialisedAgent(/* (AgentReference) in.readObject(), */in.readInt(),
						(Map<String, Object>) in.readObject());
			}
		});

		register(conf, SerialisedPopulation.class, new GamaFSTSerialiser<SerialisedPopulation>() {

			@Override
			public void serialise(final FSTObjectOutput out, final SerialisedPopulation o) throws Exception {
				out.writeStringUTF(o.speciesName());
				out.writeObject(o.agents());
			}

			@SuppressWarnings ("unchecked")
			@Override
			public SerialisedPopulation deserialise(final IScope scope, final FSTObjectInput in) throws Exception {
				return new SerialisedPopulation(in.readStringUTF(), (List<SerialisedAgent>) in.readObject());
			}
		});

		register(conf, SerialisedGrid.class, new GamaFSTSerialiser<SerialisedGrid>() {

			@Override
			public void serialise(final FSTObjectOutput out, final SerialisedGrid o) throws Exception {
				out.writeStringUTF(o.speciesName());
				out.writeObject(o.agents());
				out.writeObject(o.matrix());
			}

			@SuppressWarnings ("unchecked")
			@Override
			public SerialisedGrid deserialise(final IScope scope, final FSTObjectInput in) throws Exception {
				return new SerialisedGrid(in.readStringUTF(), (List<SerialisedAgent>) in.readObject(),
						(IGrid) in.readObject());
			}
		});

		register(conf, GamaGeometryFactory.class, new GamaFSTSerialiser<GamaGeometryFactory>() {

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

		register(conf, GamaFont.class, new GamaFSTSerialiser<GamaFont>() {

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

		register(conf, IMap.class, new GamaFSTSerialiser<IMap>() {

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

		register(conf, IList.class, new GamaFSTSerialiser<IList>() {

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

		register(conf, GamaCoordinateSequenceFactory.class, new GamaFSTSerialiser<GamaCoordinateSequenceFactory>() {

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
	public <T> void register(final FSTConfiguration conf, final Class<T> clazz, final GamaFSTSerialiser<T> ser) {
		ser.setName(clazz.getSimpleName());
		conf.registerSerializer(clazz, ser, true);
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
			T result = deserialise(currentScope, in);
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
		registerSerialisers(conf);
		return conf;
	}

	@Override
	protected SerialisedAgent encodeToSerialisedForm(final IAgent agent) {
		return SerialisedAgentFactory.createFor(agent);
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
	public byte[] write(final IScope scope, final SerialisedAgent sa) {
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
	public SerialisedAgent read(final IScope scope, final byte[] input) {
		currentScope = scope;
		try {
			return (SerialisedAgent) fst.asObject(input);
		} finally {
			currentScope = null;
		}
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
	public void restoreFromSerialisedForm(final IAgent sim, final SerialisedAgent image) {
		SerialisedAgentFactory.restoreAgent(sim.getScope(), sim, image);
	}

}
