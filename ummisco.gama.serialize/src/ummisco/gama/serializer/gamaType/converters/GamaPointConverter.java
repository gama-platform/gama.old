/*******************************************************************************************************
 *
 * GamaPointConverter.java, in ummisco.gama.serialize, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gama.serializer.gamaType.converters;

import static java.lang.Double.parseDouble;

import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.runtime.IScope;

/**
 * The Class GamaPointConverter.
 */
public class GamaPointConverter extends AbstractGamaConverter<GamaPoint, GamaPoint> {

	/** The Constant TAG. */
	private final static String TAG = "GamaPoint";

	/** The Constant SEPARATOR. */
	private final static String SEPARATOR = ":";

	/**
	 * Instantiates a new gama point converter.
	 *
	 * @param target
	 *            the target
	 */
	public GamaPointConverter(final Class<GamaPoint> target) {
		super(target);
	}

	@Override
	public void write(final IScope scope, final GamaPoint pt, final HierarchicalStreamWriter writer,
			final MarshallingContext context) {
		writer.startNode(TAG);
		writer.setValue(pt.x + SEPARATOR + pt.y + SEPARATOR + pt.z);
		writer.endNode();
	}

	@Override
	public GamaPoint read(final IScope scope, final HierarchicalStreamReader reader, final UnmarshallingContext arg1) {
		try {
			reader.moveDown();
			String[] lines = reader.getValue().split(SEPARATOR);
			return new GamaPoint(parseDouble(lines[0]), parseDouble(lines[1]), parseDouble(lines[2]));
		} finally {
			reader.moveUp();
		}
	}

}
