/**
 * Created by drogoul, 22 mars 2015
 *
 */
package msi.gama.util;

import java.awt.Font;
import msi.gama.common.interfaces.*;
import msi.gama.precompiler.GamlAnnotations.*;
import msi.gama.precompiler.IConcept;
import msi.gama.precompiler.IOperatorCategory;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.types.*;

/**
 * Class GamaFont. A simple wrapper on an AWT Font
 *
 * @author drogoul
 * @since 22 mars 2015
 *
 */
@vars({ @var(name = IKeyword.NAME, type = IType.STRING, doc = { @doc("Returns the name of this font") }),
	@var(name = IKeyword.SIZE, type = IType.INT, doc = { @doc("Returns the size (in points) of this font") }),
	@var(name = IKeyword.STYLE,
		type = IType.INT,
		doc = { @doc("Returns the style of this font (0 for plain, 1 for bold, 2 for italic, 3 for bold+italic)") }) })
public class GamaFont extends Font implements IValue {

	/**
	 * @param name
	 * @param style
	 * @param size
	 */
	public GamaFont(final String name, final int style, final int size) {
		super(name, style, size);
	}

	public GamaFont(final Font font) {
		super(font);
	}

	@Override
	@getter(IKeyword.NAME)
	public String getName() {
		return name;
	}

	@Override
	@getter(IKeyword.SIZE)
	public int getSize() {
		return size;
	}

	@Override
	@getter(IKeyword.STYLE)
	public int getStyle() {
		return style;
	}

	/**
	 * Method serialize()
	 * @see msi.gama.common.interfaces.IGamlable#serialize(boolean)
	 */
	@Override
	public String serialize(final boolean includingBuiltIn) {
		String strStyle;

		if ( isBold() ) {
			strStyle = isItalic() ? "#bold + #italic" : "#bold";
		} else {
			strStyle = isItalic() ? "#italic" : "#plain";
		}

		return "font('" + name + "'," + pointSize + "," + strStyle + ")";
	}

	/**
	 * Method getType()
	 * @see msi.gama.common.interfaces.ITyped#getType()
	 */
	@Override
	public IType getType() {
		return Types.FONT;
	}

	/**
	 * Method stringValue(). Outputs to a format that is usable by Font.decode(String);
	 * @see msi.gama.common.interfaces.IValue#stringValue(msi.gama.runtime.IScope)
	 */
	@Override
	public String stringValue(final IScope scope) throws GamaRuntimeException {
		return toString();
	}

	@Override
	public String toString() {
		String strStyle;
		if ( isBold() ) {
			strStyle = isItalic() ? "bolditalic" : "bold";
		} else {
			strStyle = isItalic() ? "italic" : "plain";
		}
		return name + "-" + strStyle + "-" + size;
	}

	/**
	 * Method copy()
	 * @see msi.gama.common.interfaces.IValue#copy(msi.gama.runtime.IScope)
	 */
	@Override
	public IValue copy(final IScope scope) throws GamaRuntimeException {
		return new GamaFont(name, style, size);
	}

	@operator(value = "font", category = { IOperatorCategory.CASTING },
			concept = { IConcept.TEXT, IConcept.DISPLAY }, can_be_const = true)
	@doc(
		value = "Creates a new font, by specifying its name (either a font face name like 'Lucida Grande Bold' or 'Helvetica', or a logical name like 'Dialog', 'SansSerif', 'Serif', etc.), a size in points and a style, either #bold, #italic or #plain or a combination (addition) of them.",
		examples = @example(value = "font ('Helvetica Neue',12, #bold + #italic)",
			equals = "a bold and italic face of the Helvetica Neue family",
			test = false) )
	public static GamaFont font(final String name, final Integer size, final Integer style) {
		return new GamaFont(name, style, size);
	}

}
