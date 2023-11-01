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
import java.util.List;
import java.util.Map;

import org.locationtech.jts.geom.Geometry;
import org.nustaq.serialization.FSTConfiguration;
import org.nustaq.serialization.FSTObjectInput;
import org.nustaq.serialization.FSTObjectOutput;

import msi.gama.common.geometry.GamaCoordinateSequenceFactory;
import msi.gama.common.geometry.GamaGeometryFactory;
import msi.gama.common.geometry.GeometryUtils;
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
import msi.gama.runtime.exceptions.GamaRuntimeException;
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

	/** The fst. */
	FSTConfiguration fst;

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

		register(conf, GamaShape.class, new FSTIndividualSerialiser<GamaShape>() {

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

		register(conf, IAgent.class, new FSTIndividualSerialiser<IAgent>() {

			@Override
			public void serialise(final FSTObjectOutput out, final IAgent o) throws Exception {
				out.writeObject(AgentReference.of(o));
			}

			@Override
			public IAgent deserialise(final IScope scope, final FSTObjectInput in) throws Exception {
				AgentReference ref = (AgentReference) in.readObject();
				return ref.getReferencedAgent(scope);
			}

		});

		register(conf, IType.class, new FSTIndividualSerialiser<IType>() {

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

		register(conf, IScope.class, new FSTIndividualSerialiser<IScope>() {

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

		register(conf, ISpecies.class, new FSTIndividualSerialiser<ISpecies>() {

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

		register(conf, AgentReference.class, new FSTIndividualSerialiser<AgentReference>() {

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

		register(conf, SerialisedAgent.class, new FSTIndividualSerialiser<SerialisedAgent>() {

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
				return new SerialisedAgent(in.readInt(), in.readStringUTF(), (Map<String, Object>) in.readObject(),
						(Map<String, ISerialisedPopulation>) in.readObject());
			}
		});

		register(conf, SerialisedPopulation.class, new FSTIndividualSerialiser<SerialisedPopulation>() {

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

		register(conf, SerialisedGrid.class, new FSTIndividualSerialiser<SerialisedGrid>() {

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

		register(conf, GamaGeometryFactory.class, new FSTIndividualSerialiser<GamaGeometryFactory>() {

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

		register(conf, GamaFont.class, new FSTIndividualSerialiser<GamaFont>() {

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

		register(conf, IMap.class, new FSTIndividualSerialiser<IMap>() {

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

		register(conf, IList.class, new FSTIndividualSerialiser<IList>() {

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

		register(conf, GamaCoordinateSequenceFactory.class,
				new FSTIndividualSerialiser<GamaCoordinateSequenceFactory>() {

					@Override
					public void serialise(final FSTObjectOutput out, final GamaCoordinateSequenceFactory o)
							throws Exception {
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
	public <T> void register(final FSTConfiguration conf, final Class<T> clazz, final FSTIndividualSerialiser<T> ser) {
		ser.setName(clazz.getSimpleName());
		conf.registerSerializer(clazz, ser, true);
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
		return SerialisedAgent.of(agent, true);
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

	@Override
	public byte[] write(final IScope scope, final Object obj) {
		return fst.asByteArray(obj);
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
			fst.setScope(scope);
			return fst.asObject(input);
		} finally {
			fst.setScope(null);
		}

	}

	@Override
	public IAgent createAgentFromBytes(final IScope scope, final byte[] input) {
		try {
			fst.setScope(scope);
			Object o = fst.asObject(input);
			if (o instanceof SerialisedAgent sa) return sa.recreateIn(scope);
			return null;
		} catch (Exception e) {
			throw GamaRuntimeException.create(e, scope);
		} finally {
			fst.setScope(null);
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
	public SerialisedAgent read(final IScope scope, final byte[] input) {
		try {
			fst.setScope(scope);
			return (SerialisedAgent) fst.asObject(input);
		} finally {
			fst.setScope(null);
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
		image.restoreAs(sim.getScope(), sim);
	}

}
