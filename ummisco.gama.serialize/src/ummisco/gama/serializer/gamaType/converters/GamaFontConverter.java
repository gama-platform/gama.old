/*******************************************************************************************************
 *
 * GamaFontConverter.java, in ummisco.gama.serialize, is part of the source code of the GAMA modeling and simulation
 * platform (v.2.0.0).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama2 for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gama.serializer.gamaType.converters;

import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

import msi.gama.runtime.IScope;
import msi.gama.util.GamaFont;
import msi.gaml.operators.Cast;

/**
 * The Class GamaColorConverter.
 */
@SuppressWarnings ("rawtypes")
public class GamaFontConverter extends AbstractGamaConverter<GamaFont, GamaFont> {

	/** The Constant TAG. */
	private final static String TAG = "GamaFont";

	/** The Constant SEPARATOR. */
	private final static String SEPARATOR = ":";

	/**
	 * Instantiates a new gama color converter.
	 *
	 * @param scope
	 *            the scope
	 * @param target
	 *            the target
	 */
	public GamaFontConverter(final Class<GamaFont> target) {
		super(target);
	}

	/**
	 * Write.
	 *
	 * @param scope
	 *            the scope
	 * @param color
	 *            the color
	 * @param writer
	 *            the writer
	 * @param context
	 *            the context
	 */
	@Override
	public void write(final IScope scope, final GamaFont font, final HierarchicalStreamWriter writer,
			final MarshallingContext context) {
		writer.startNode(TAG);
		writer.setValue(font.getName() + SEPARATOR + font.getStyle() + SEPARATOR + font.getSize());
		writer.endNode();
	}

	@Override
	public GamaFont read(final IScope scope, final HierarchicalStreamReader reader,
			final UnmarshallingContext context) {
		try {
			reader.moveDown();
			String[] lines = reader.getValue().split(SEPARATOR);
			return new GamaFont(lines[0], Cast.asInt(scope, lines[1]), Cast.asInt(scope, lines[2]));
		} finally {
			reader.moveUp();
		}
	}

}
