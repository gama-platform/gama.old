/*******************************************************************************************************
 *
 * KryoBinaryProcessor.java, in ummisco.gama.serialize, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gama.serializer.implementations;

import java.util.Arrays;

import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.Polygon;
import org.objenesis.strategy.StdInstantiatorStrategy;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.unsafe.UnsafeByteBufferInput;
import com.esotericsoftware.kryo.unsafe.UnsafeByteBufferOutput;

import msi.gama.common.geometry.GamaCoordinateSequence;
import msi.gama.kernel.simulation.SimulationAgent;
import msi.gama.metamodel.agent.GamlAgent;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.agent.MinimalAgent;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.metamodel.shape.GamaShape;
import msi.gama.metamodel.shape.IShape;
import msi.gama.runtime.IScope;
import msi.gama.util.GamaList;
import msi.gama.util.GamaMap;

/**
 * The Class KryoBinaryImplementation.
 *
 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
 * @date 8 août 2023
 */
public class KryoBinaryProcessor extends AbstractSerialisationProcessor<SerialisedAgent> {

	/** The kryo. */
	Kryo kryo = new Kryo();

	/** The use unsafe. */
	final boolean useUnsafe = false; // Util.unsafe;

	{
		kryo.setAutoReset(true);
		kryo.setReferences(true);
		kryo.setRegistrationRequired(false);
		kryo.setWarnUnregisteredClasses(false);
		kryo.setInstantiatorStrategy(new StdInstantiatorStrategy());
		for (Class c : Arrays.asList(GamaPoint.class, GamaPoint[].class, Integer[].class, String[].class,
				MinimalAgent.class, GamlAgent.class, SimulationAgent.Type.class, Envelope.class, LinearRing.class,
				LinearRing[].class, Polygon.class, GamaCoordinateSequence.class, IShape.Type.class,
				GamaShape.ShapeData.class, GamaList.class, GamaMap.class)) {
			kryo.register(c);
		}

	}

	@Override
	protected SerialisedAgent encodeToSerialisedForm(final IAgent agent) {
		return SerialisedAgentFactory.createFor(agent);
	}

	/**
	 * Restore from serialised form.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param sim
	 *            the sim
	 * @param image
	 *            the image
	 * @date 8 août 2023
	 */
	@Override
	protected void restoreFromSerialisedForm(final IAgent sim, final SerialisedAgent image) {
		SerialisedAgentFactory.restoreAgent(sim.getScope(), sim, image);
	}

	@Override
	public byte[] write(final IScope scope, final SerialisedAgent object) {
		Output output = useUnsafe ? new UnsafeByteBufferOutput(0, Integer.MAX_VALUE) : new Output(0, Integer.MAX_VALUE);
		kryo.writeClassAndObject(output, object);
		return output.toBytes();
	}

	/**
	 * Read.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param bytes
	 *            the bytes
	 * @return the serialised agent
	 * @date 8 août 2023
	 */
	@Override
	public SerialisedAgent read(final IScope scope, final byte[] bytes) {
		Input input = useUnsafe ? new UnsafeByteBufferInput(bytes) : new Input(bytes);
		return (SerialisedAgent) kryo.readClassAndObject(input);
	}

	@Override
	public byte getFormatIdentifier() { return 2; }

	/**
	 * Gets the name.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @return the name
	 * @date 8 août 2023
	 */
	@Override
	public String getFormat() { return BINARY_FORMAT + " Kryo"; }
}
