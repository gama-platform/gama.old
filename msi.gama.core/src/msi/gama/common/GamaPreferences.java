package msi.gama.common;

import java.awt.Color;
import java.util.*;
import java.util.prefs.*;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.kernel.experiment.IParameter;
import msi.gama.runtime.*;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.*;
import msi.gaml.operators.Cast;
import msi.gaml.types.*;

/**
 * Class GamaPreferencesView.
 * 
 * @author drogoul
 * @since 26 août 2013
 * 
 */
public class GamaPreferences {

	public static final String GENERAL = "General";
	public static final String DISPLAY = "Display";
	public static final String CODE = "Code";
	public static final String EDITOR = "Editor";
	public static final String WORKSPACE = "Workspace";
	private static Preferences store = Preferences.userRoot().node("gama");
	private static Map<String, Entry> prefs = new LinkedHashMap();
	private static List<String> storeKeys;

	static {

		try {
			storeKeys = new GamaList(store.keys());
		} catch (BackingStoreException e) {
			e.printStackTrace();
		}
	}

	public static <T> Entry<T> create(final String key, final T value) {
		return create(key, value, IType.STRING);
	}

	public static <T> Entry<T> create(final String key, final T value, final int type) {
		return create(key, "Value of " + key, value, type);
	}

	public static <T> Entry<T> create(final String key, final String title, final T value, final int type) {
		Entry e = new Entry(key).named(title).in(GENERAL).init(value).typed(type);
		register(e);
		return e;
	}

	public static class Entry<T> implements IParameter {

		String key, title, tab, group;
		T value, initial;
		IType type;
		List<T> values;
		Number min, max;
		String activates;

		private Entry(final String key) {
			tab = GENERAL;
			this.key = key;
		}

		public Entry group(final String group) {
			this.group = group;
			return this;
		}

		public Entry among(final T ... values) {
			return among(new GamaList(values));
		}

		public Entry among(final List<T> values) {
			this.values = values;
			return this;
		}

		public Entry between(final Number min, final Number max) {
			this.min = min;
			this.max = max;
			return this;
		}

		public Entry in(final String category) {
			this.tab = category;
			return this;
		}

		public Entry named(final String title) {
			this.title = title;
			return this;
		}

		public Entry init(final T value) {
			this.initial = value;
			this.value = value;
			return this;
		}

		public Entry set(final T value) {
			this.value = value;
			return this;
		}

		public Entry typed(final int type) {
			this.type = Types.get(type);
			return this;
		}

		public Entry activates(final String link) {
			activates = link;
			return this;
		}

		public T getValue() {
			return value;
		}

		@Override
		public IType getType() {
			return type;
		}

		@Override
		public String getTitle() {
			return title;
		}

		public String getKey() {
			return key;
		}

		public List<T> getValues() {
			return values;
		}

		@Override
		public String getName() {
			return key;
		}

		@Override
		public String getCategory() {
			return group;
		}

		@Override
		public String getUnitLabel() {
			return null;
		}

		@Override
		public void setUnitLabel(final String label) {}

		@Override
		public Integer getDefinitionOrder() {
			return 0;
		}

		@Override
		public void setValue(final Object value) {
			this.value = (T) value;
		}

		@Override
		public Object value(final IScope scope) throws GamaRuntimeException {
			return value;
		}

		@Override
		public IType getContentType() {
			return Types.NO_TYPE;
		}

		@Override
		public String toGaml() {
			return Cast.toGaml(value);
		}

		@Override
		public Object getInitialValue(final IScope scope) {
			return initial;
		}

		@Override
		public Number getMinValue() {
			return min;
		}

		@Override
		public Number getMaxValue() {
			return max;
		}

		@Override
		public List getAmongValue() {
			return values;
		}

		@Override
		public boolean isEditable() {
			return true;
		}

		// @Override
		// public boolean isLabel() {
		// return false;
		// }

		@Override
		public Number getStepValue() {
			return null;
		}

	}

	/**
	 * Core preferences
	 */

	// GENERAL

	public static final List<String> GENERATOR_NAMES = Arrays.asList(IKeyword.CELLULAR, IKeyword.XOR, IKeyword.JAVA,
		IKeyword.MERSENNE);

	public static final Entry<String> CORE_RNG = create("core.rng", "Random number generator", IKeyword.MERSENNE,
		IType.STRING).among(GENERATOR_NAMES).in(GENERAL).group("Random Number Generation");
	public static final Entry<Boolean> CORE_SEED_DEFINED = create("core.seed_defined", "Define a default seed", false,
		IType.BOOL).activates("core.seed").in(GENERAL).group("Random Number Generation");
	public static final Entry<Double> CORE_SEED = create("core.seed", "Default seed value", 0d, IType.FLOAT)
		.in(GENERAL).group("Random Number Generation");
	public static final Entry<Boolean> CORE_RND_EDITABLE = create("core.define_rng",
		"Include in the parameters of models", true, IType.BOOL).in(GENERAL).group("Random Number Generation");
	public static final Entry<Boolean> CORE_PERSPECTIVE = create("core.perspective",
		"Automatically switch to modeling perspective", false, IType.BOOL).in(GENERAL).group("User interface");
	public static final Entry<Integer> CORE_MENU_SIZE = create("core.menu_size", "Break down agents in menus every",
		50, IType.INT).between(10, 100).in(GENERAL).group("User interface");
	public static final Entry<Boolean> CORE_REVEAL_AND_STOP = create("core.stop", "Stop simulation at first error",
		true, IType.BOOL).in(GENERAL).group("Simulation errors");
	public static final Entry<Boolean> CORE_WARNINGS = create("core.warnings", "Treat warnings as errors", false,
		IType.BOOL).in(GENERAL).group("Simulation errors");
	public static final Entry<Integer> CORE_ERRORS_NUMBER = create("core.errors_number", "Number of errors to display",
		10, IType.INT).in(GENERAL).group("Simulation errors").between(1, null);
	public static final Entry<Boolean> CORE_RECENT = create("core.recent", "Display most recent first", true,
		IType.BOOL).in(GENERAL).group("Simulation errors");
	public static final Entry<Boolean> CORE_SHOW_ERRORS = create("core.display_errors", "Display errors", true,
		IType.BOOL).in(GENERAL).group("Simulation errors");
	public static final Entry<Double> CORE_DELAY_STEP = create("core.delay_step",
		"Default step for delay slider (in sec.)", 0.01, IType.FLOAT).in(GENERAL).group("Runtime");

	// DISPLAY
	public static final Entry<String> CORE_DISPLAY = create("core.display", "Default display method", "Java2D",
		IType.STRING).among("Java2D", "OpenGL").in(DISPLAY).group("Properties");
	public static final Entry<Boolean> CORE_SYNC = create("core.sync",
		"Synchronize displays with simulations by default", false, IType.BOOL).in(DISPLAY).group("Properties");
	public static final Entry<Boolean> CORE_OVERLAY = create("core.overlay", "Show display overlay by default", false,
		IType.BOOL).in(DISPLAY).group("Properties");
	public static final Entry<Boolean> CORE_SCALE = create("core.scale", "Show scale bar in overlay by default", false,
		IType.BOOL).in(DISPLAY).group("Properties");
	public static final Entry<Boolean> CORE_ANTIALIAS = create("core.antialias", "Apply antialiasing by default",
		false, IType.BOOL).in(DISPLAY).group("Properties");
	public static final Entry<Color> CORE_BACKGROUND = create("core.background", "Default background color",
		Color.white, IType.COLOR).in(DISPLAY).group("Properties");
	public static final Entry<Color> CORE_HIGHLIGHT = create("core.highlight", "Default highlight color",
		new Color(0, 200, 200), IType.COLOR).in(DISPLAY).group("Properties");
	public static final Entry<String> CORE_SHAPE = create("core.shape", "Defaut shape to use for agents", "shape",
		IType.STRING).among("shape", "circle", "square", "triangle", "point", "cube", "sphere").in(DISPLAY)
		.group("Default aspect");
	public static final Entry<Double> CORE_SIZE = create("core.size", "Default size to use for agents", 1.0,
		IType.FLOAT).between(0.01, null).in(DISPLAY).group("Default aspect");
	public static final Entry<Color> CORE_COLOR = create("core.color", "Default color to use for agents", Color.yellow,
		IType.COLOR).in(DISPLAY).group("Default aspect");
	public static final Entry<Boolean> CORE_Z_FIGHTING = create("core.z_fighting", "Use z-fighting by default", true,
		IType.BOOL).in(DISPLAY).group("OpenGL");

	private static void register(final Entry gp) {
		IScope scope = GAMA.obtainNewScope();
		prefs.put(gp.getKey(), gp);
		String key = gp.key;
		Object value = gp.value;
		switch (gp.type.id()) {
			case IType.INT:
				if ( storeKeys.contains(key) ) {
					gp.setValue(store.getInt(key, Cast.as(value, Integer.class)));
				} else {
					store.putInt(key, Cast.as(value, Integer.class));
				}
				break;
			case IType.FLOAT:
				if ( storeKeys.contains(key) ) {
					gp.setValue(store.getDouble(key, Cast.as(value, Double.class)));
				} else {
					store.putDouble(key, Cast.as(value, Double.class));
				}
				break;
			case IType.BOOL:
				value = Cast.asBool(scope, value);
				if ( storeKeys.contains(key) ) {
					gp.setValue(store.getBoolean(key, Cast.as(value, Boolean.class)));
				} else {
					store.putBoolean(key, Cast.as(value, Boolean.class));
				}
				break;
			case IType.STRING:
				if ( storeKeys.contains(key) ) {
					gp.setValue(store.get(key, Cast.as(value, String.class)));
				} else {
					store.put(key, (String) value);
				}
				break;
			case IType.COLOR:
				// Stores the preference as an int but create a color
				if ( storeKeys.contains(key) ) {
					gp.setValue(GamaColor.getInt(store.getInt(key, Cast.as(value, Integer.class))));
				} else {
					store.putInt(key, Cast.as(value, Integer.class));
				}
				break;
			default:
				if ( storeKeys.contains(key) ) {
					gp.setValue(store.get(key, Cast.as(value, String.class)));
				} else {
					store.put(key, Cast.as(value, String.class));
				}
		}
		if ( scope != null ) {
			GAMA.releaseScope(scope);
		}
		try {
			store.flush();
		} catch (BackingStoreException e) {
			e.printStackTrace();
		}
	}

	private static void writeToStore(final Entry gp) {
		String key = gp.key;
		Object value = gp.value;
		switch (gp.type.id()) {
			case IType.INT:
				store.putInt(key, (Integer) value);
				break;
			case IType.FLOAT:
				store.putDouble(key, (Double) value);
				break;
			case IType.BOOL:
				store.putBoolean(key, (Boolean) value);
				break;
			case IType.STRING:
				store.put(key, (String) value);
				break;
			case IType.COLOR:
				// Stores the preference as an int but create a color
				int code = ((Color) value).getRGB();
				store.putInt(key, code);
				break;
			default:
				store.put(key, (String) value);
		}
	}

	public static Map<String, Map<String, List<Entry>>> organizePrefs() {
		Map<String, Map<String, List<Entry>>> result = new LinkedHashMap();
		for ( Entry e : prefs.values() ) {
			String tab = e.tab;
			Map<String, List<Entry>> groups = result.get(tab);
			if ( groups == null ) {
				groups = new LinkedHashMap();
				result.put(tab, groups);
			}
			String group = e.group;
			List<Entry> in_group = groups.get(group);
			if ( in_group == null ) {
				in_group = new ArrayList();
				groups.put(group, in_group);
			}
			in_group.add(e);
		}
		return result;
	}

	public static void setNewPreferences(final Map<String, Object> modelValues) {
		for ( String name : modelValues.keySet() ) {
			Entry e = prefs.get(name);
			if ( e == null ) {
				continue;
			}
			e.set(modelValues.get(name));
			writeToStore(e);
			try {
				store.flush();
			} catch (BackingStoreException ex) {
				ex.printStackTrace();
			}
		}
	}

	/**
	 * 
	 */
	public static void revertToDefaultValues(final Map<String, Object> modelValues) {
		for ( String name : modelValues.keySet() ) {
			Entry e = prefs.get(name);
			if ( e == null ) {
				continue;
			}
			modelValues.put(name, e.initial);
		}

	}

}
