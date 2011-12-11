/*
 * GAMA - V1.4  http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2011
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2011
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen  (Batch, GeoTools & JTS), 2009-2011
 * - Beno”t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2011
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.util;

import java.awt.Color;
import java.lang.reflect.Field;
import java.util.*;
import msi.gama.interfaces.*;
import msi.gama.internal.types.Types;
import msi.gama.kernel.GAMA;
import msi.gama.kernel.exceptions.GamaRuntimeException;
import msi.gama.precompiler.GamlAnnotations.getter;
import msi.gama.precompiler.GamlAnnotations.var;
import msi.gama.precompiler.GamlAnnotations.vars;
import msi.gama.util.matrix.GamaIntMatrix;

/**
 * The Class GamaColor.
 */
@vars({ @var(name = GamaColor.COLOR_RED, type = IType.INT_STR),
	@var(name = GamaColor.COLOR_GREEN, type = IType.INT_STR),
	@var(name = GamaColor.COLOR_BLUE, type = IType.INT_STR),
	@var(name = GamaColor.BRIGHTER, type = IType.COLOR_STR),
	@var(name = GamaColor.DARKER, type = IType.COLOR_STR) })
public class GamaColor extends Color implements IGamaContainer<Integer, Integer> {

	// public final static Map<Integer, GamaColor> instancesArray = new HashMap(1000);

	public static final String COLOR_RED = "red";
	public static final String DARKER = "darker";
	public static final String BRIGHTER = "brighter";
	public static final String COLOR_GREEN = "green";
	public static final String COLOR_BLUE = "blue";

	public final static Map<String, GamaColor> colors = new HashMap();

	public final static Map<Integer, GamaColor> int_colors = new HashMap();

	public static GamaColor getInt(final int rgb) {
		GamaColor result = int_colors.get(rgb);
		if ( result == null ) {
			result = new GamaColor(rgb);
			int_colors.put(rgb, result);
		}
		return result;
	}

	static {
		final Field[] colorFields = Color.class.getDeclaredFields();
		for ( final Field f : colorFields ) {
			try {
				final Color color = (Color) f.get((Object) null);
				final GamaColor gc = new GamaColor(color);
				colors.put(f.getName(), gc);
				int_colors.put(gc.getRGB(), gc);
			} catch (final IllegalAccessException iae) {} catch (final ClassCastException cce) {}
		}
	}

	//
	// public static void add(final int i, final GamaColor c) {
	//
	// instancesArray.put(i, c);
	// }

	// public static GamaColor get(final int r, final int g, final int b) {
	// final int value =
	// (normalize(r) & 0xFF) << 16 | (normalize(g) & 0xFF) << 8 | (normalize(b) & 0xFF) << 0;
	// return Cast.asColor(null, value);
	// }

	private static int normalize(final int rgbComp) {
		return rgbComp < 0 ? 0 : rgbComp > 255 ? 255 : rgbComp;
	}

	// private String name;

	public GamaColor(final Color c) {
		super(c.getRGB());
	}

	protected GamaColor(final int awtRGB) {
		super(awtRGB);
	}

	public GamaColor(final int r, final int g, final int b) {
		super(normalize(r), normalize(g), normalize(b));
	}

	@Override
	public String toString() {
		return "GamaColor[" + getRGB() /* + ";" + swtValue */+ "]";
	}

	@Override
	public String toGaml() {
		return "rgb " + listValue(GAMA.getDefaultScope()).toGaml();
	}

	@Override
	public String toJava() {
		return "new GamaColor(" + getRGB() + ")";
	}

	@Override
	public GamaList listValue(final IScope scope) {
		return GamaList.with(getRed(), getGreen(), getBlue());
	}

	@Override
	public IMatrix matrixValue(final IScope scope) throws GamaRuntimeException {
		final IMatrix result = new GamaIntMatrix(3, 1);
		result.put(0, 0, getRed());
		result.put(1, 0, getGreen());
		result.put(2, 0, getBlue());
		return result;
	}

	@Override
	public IMatrix matrixValue(final IScope scope, final GamaPoint preferredSize)
		throws GamaRuntimeException {
		if ( preferredSize == null ) { return matrixValue(scope); }
		final int xSize = (int) preferredSize.x;
		final int ySize = (int) preferredSize.y;
		final IMatrix result = new GamaIntMatrix(xSize, ySize);
		if ( xSize < 3 || ySize == 0 ) { return result; }
		result.put(0, 0, getRed());
		result.put(1, 0, getGreen());
		result.put(2, 0, getBlue());
		return result;
	}

	@Override
	public String stringValue() {
		return String.valueOf(getRGB());
	}

	@Override
	public IType type() {
		return Types.get(IType.COLOR);
	}

	@Override
	// @operator(value = "map", can_be_const = true)
	public GamaMap mapValue(final IScope scope) {
		final GamaMap result = new GamaMap();
		result.put("r", getRed());
		result.put("g", getGreen());
		result.put("b", getBlue());
		return result;

	}

	@Override
	public void add(final Integer index, final Integer value, final Object param)
		throws GamaRuntimeException {
		immutableException();
	}

	@Override
	public void add(final Integer value, final Object param) throws GamaRuntimeException {
		immutableException();
	}

	@Override
	public void put(final Integer index, final Integer value, final Object param)
		throws GamaRuntimeException {
		immutableException();

	}

	@Override
	public boolean removeAll(final IGamaContainer<?, Integer> value) throws GamaRuntimeException {
		immutableException();
		return false;

	}

	@Override
	public boolean removeFirst(final Integer value) throws GamaRuntimeException {
		immutableException();
		return false;
	}

	@Override
	public Object removeAt(final Integer index) throws GamaRuntimeException {
		immutableException();
		return null;
	}

	private void immutableException() throws GamaRuntimeException {
		throw new GamaRuntimeException("Cannot change " + this + " as rgb values are immutable",
			true);
	}

	@Override
	public Integer first() {
		return getRed();
	}

	@Override
	public Integer last() {
		return getBlue();
	}

	@Override
	public boolean isEmpty() {
		return false;
	}

	@Override
	public Integer get(final Integer index) {
		if ( index != null ) {
			int i = index.intValue();
			switch (i) {
				case 0:
					return getRed();
				case 1:
					return getGreen();
				case 2:
					return getBlue();
			}
		}
		return 0;
	}

	@getter(var = GamaColor.COLOR_RED)
	public Integer red() {
		return super.getRed();
	}

	@getter(var = GamaColor.COLOR_BLUE)
	public Integer blue() {
		return super.getBlue();
	}

	@getter(var = GamaColor.COLOR_GREEN)
	public Integer green() {
		return super.getGreen();
	}

	@getter(var = GamaColor.BRIGHTER)
	public GamaColor getBrighter() {
		return new GamaColor(super.brighter());
	}

	@getter(var = GamaColor.DARKER)
	public GamaColor getDarker() {
		return new GamaColor(super.darker());
	}

	@Override
	public Object sum() {
		return getRed() + getGreen() + getBlue();
	}

	@Override
	public Object product() {
		return getRed() * getGreen() * getBlue();
	}

	@Override
	public int length() {
		return 3;
	}

	@Override
	public Integer max() {
		Integer i = getRed();
		if ( getGreen() > i ) {
			i = getGreen();
		}
		if ( getBlue() > i ) {
			i = getBlue();
		}
		return i;

	}

	@Override
	public Integer min() {
		Integer i = getRed();
		if ( getGreen() < i ) {
			i = getGreen();
		}
		if ( getBlue() < i ) {
			i = getBlue();
		}
		return i;
	}

	@Override
	public boolean contains(final Object o) {
		if ( o instanceof Integer ) {
			int i = ((Integer) o).intValue();
			return i == getRed() || i == getBlue() || i == getGreen();
		}
		return false;
	}

	@Override
	public GamaColor reverse() {
		return new GamaColor(getBlue(), getGreen(), getRed());
	}

	@Override
	public void putAll(final Integer value, final Object param) throws GamaRuntimeException {
		immutableException();
	}

	@Override
	public void clear() throws GamaRuntimeException {
		immutableException();
	}

	@Override
	public GamaColor copy() {
		return this;
		// Colors are immutable
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.interfaces.IGamaContainer#checkIndex(java.lang.Object)
	 */
	@Override
	public boolean checkIndex(final Object index) {
		return index instanceof Integer;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.interfaces.IGamaContainer#checkValue(java.lang.Object)
	 */
	@Override
	public boolean checkValue(final Object value) {
		return value instanceof Integer;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.interfaces.IGamaContainer#checkBounds(java.lang.Object)
	 */
	@Override
	public boolean checkBounds(final Integer index, final boolean forAdding) {
		return index >= 0 && index < 3;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.interfaces.IGamaContainer#isFixedLength()
	 */
	@Override
	public boolean isFixedLength() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Iterable#iterator()
	 */
	@Override
	public Iterator<Integer> iterator() {
		return listValue(null).iterator();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.interfaces.IGamaContainer#addAll(msi.gama.interfaces.IGamaContainer,
	 * java.lang.Object)
	 */
	@Override
	public void addAll(final IGamaContainer value, final Object param) throws GamaRuntimeException {
		immutableException();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.interfaces.IGamaContainer#addAll(java.lang.Object,
	 * msi.gama.interfaces.IGamaContainer, java.lang.Object)
	 */
	@Override
	public void addAll(final Integer index, final IGamaContainer value, final Object param)
		throws GamaRuntimeException {
		immutableException();
	}

	@Override
	public Integer any() {
		int i = GAMA.getRandom().between(0, 2);
		return this.get(i);
	}
}
