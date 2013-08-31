package msi.gama.common;

import java.awt.Color;
import java.util.*;
import java.util.prefs.*;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.runtime.*;
import msi.gama.util.*;
import msi.gaml.operators.Cast;
import msi.gaml.types.IType;
import org.eclipse.swt.graphics.RGB;

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

	public static <T> Entry<T> create(final String key, final T value) {
		return create(key, value, IType.STRING);
	}

	public static <T> Entry<T> create(final String key, final T value, final int type) {
		return create(key, "Value of " + key, value, type);
	}

	public static <T> Entry<T> create(final String key, final String title, final T value, final int type) {
		return new Entry(key).named(title).in(GENERAL).init(value).typed(type);
	}

	public static class Entry<T> {

		String key, title, tab, group;
		T value, initial;
		int type;
		List<T> values;
		Number min, max;
		String activates;

		private Entry(final String key) {
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
			this.type = type;
			return this;
		}

		public Entry activates(final String link) {
			activates = link;
			return this;
		}

		public T getValue() {
			return value;
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
		IType.BOOL).activates("core.seed");
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

	// DISPLAY
	public static final Entry<String> CORE_DISPLAY = create("core.display", "Default display method", "Java 2D",
		IType.STRING).among("Java 2D", "OpenGL").in(DISPLAY).group("Properties");
	public static final Entry<Boolean> CORE_SYNC = create("core.sync",
		"Synchronize displays with simulations by default", false, IType.BOOL).in(DISPLAY).group("Properties");
	public static final Entry<Boolean> CORE_OVERLAY = create("core.overlay", "Show display overlays by default", false,
		IType.BOOL).in(DISPLAY).group("Properties");
	public static final Entry<Boolean> CORE_ANTIALIAS = create("core.antialias", "Apply antialiasing by default",
		false, IType.BOOL).in(DISPLAY).group("Properties");
	public static final Entry<String> CORE_SHAPE = create("core.shape", "Defaut shape to use for agents", "shape",
		IType.STRING).among("circle", "square", "triangle", "point", "cube", "sphere").in(DISPLAY)
		.group("Default aspect");
	public static final Entry<Double> CORE_SIZE = create("core.size", "Default size to use for agents", 1.0,
		IType.FLOAT).between(0.01, null).in(DISPLAY).group("Default aspect");
	public static final Entry<Color> CORE_COLOR = create("core.color", "Default color to use for agents", Color.yellow,
		IType.COLOR).in(DISPLAY).group("Default aspect");

	private static Preferences storedPreferences = Preferences.userRoot().node("gama");

	private static List<String> storedKeys;
	private static Map<String, Object> preferences = new HashMap();

	static {
		try {
			storedKeys = new GamaList(storedPreferences.keys());
		} catch (BackingStoreException e) {
			e.printStackTrace();
		}
	}

	// 1. Methods to create a preference
	public static void register(final String propertyKey, final IType type, final Object defaultValue) {
		IScope scope = GAMA.obtainNewScope();
		Object value = defaultValue;
		if ( storedKeys.contains(propertyKey) && value == null ) {
			switch (type.id()) {
				case IType.INT:
					value = storedPreferences.getInt(propertyKey, Cast.asInt(scope, defaultValue));
					break;
				case IType.FLOAT:
					value = storedPreferences.getDouble(propertyKey, Cast.asFloat(scope, defaultValue));
					break;
				case IType.BOOL:
					value = storedPreferences.getBoolean(propertyKey, Cast.asBool(scope, defaultValue));
					break;
				case IType.STRING:
					value = storedPreferences.get(propertyKey, Cast.asString(scope, defaultValue));
					break;
				case IType.COLOR:
					// Stores the preference as an int
					int code = storedPreferences.getInt(propertyKey, Cast.asInt(scope, defaultValue));
					GamaColor gc = GamaColor.getInt(code);
					value = new RGB(gc.getRed(), gc.getGreen(), gc.getBlue());
					break;
				default:
					value = storedPreferences.get(propertyKey, Cast.asString(scope, defaultValue));
			}
		} else {
			switch (type.id()) {
				case IType.INT:
					value = Cast.asInt(scope, defaultValue);
					storedPreferences.putInt(propertyKey, (Integer) value);
					break;
				case IType.FLOAT:
					value = Cast.asFloat(scope, defaultValue);
					storedPreferences.putDouble(propertyKey, (Double) value);
					break;
				case IType.BOOL:
					value = Cast.asBool(scope, defaultValue);
					storedPreferences.putBoolean(propertyKey, (Boolean) value);
					break;
				case IType.STRING:
					value = Cast.asString(scope, defaultValue);
					storedPreferences.put(propertyKey, (String) value);
					break;
				case IType.COLOR:
					// Stores the preference as an int
					int code = Cast.asInt(scope, defaultValue);
					storedPreferences.putInt(propertyKey, code);
					GamaColor gc = GamaColor.getInt(code);
					value = new RGB(gc.getRed(), gc.getGreen(), gc.getBlue());
					break;
				default:
					value = Cast.asString(scope, defaultValue);
					storedPreferences.put(propertyKey, (String) value);
			}
			try {
				storedPreferences.flush();
			} catch (BackingStoreException e) {
				e.printStackTrace();
			}
		}
		if ( value != null ) {
			preferences.put(propertyKey, value);
		}
	}

	public static void register(final String propertyKey, final IType type) {
		register(propertyKey, type, type.getDefault());
	}

	// public static void main(final String[] args) {
	//
	// try {
	// System.out.println(Arrays.toString(storedPreferences.keys()));
	// // System.out.println(storedPreferences.);
	// } catch (BackingStoreException e1) {
	// e1.printStackTrace();
	// }
	//
	// Locale.setDefault(Locale.ENGLISH);
	//
	// final Display display = new Display();
	// final Shell shell = new Shell(display);
	// shell.setText("PreferenceWindow snippet");
	// shell.setLayout(new FillLayout(SWT.VERTICAL));
	//
	// final Button button1 = new Button(shell, SWT.PUSH);
	// button1.setText("Open preference window");
	//
	// final Map<String, Object> data = fillData();
	//
	// button1.addSelectionListener(new SelectionAdapter() {
	//
	// /**
	// * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
	// */
	// @Override
	// public void widgetSelected(final SelectionEvent e) {
	// final PreferenceWindow window = PreferenceWindow.create(shell, data);
	//
	// createDocumentTab(window);
	// createInfoTab(window);
	// createTerminalTab(window);
	// createPrinterTab(window);
	// createSystemTab(window);
	//
	// window.setSelectedTab(2);
	//
	// window.open();
	// System.out.println(window.getValues());
	// }
	// });
	//
	// shell.pack();
	// shell.open();
	// SWTGraphicUtil.centerShell(shell);
	//
	// while (!shell.isDisposed()) {
	// if ( !display.readAndDispatch() ) {
	// display.sleep();
	// }
	// }
	//
	// display.dispose();
	// }
	//
	// private static Map<String, Object> fillData() {
	// final Map<String, Object> data = new HashMap<String, Object>();
	// data.put("text", "A string");
	// data.put("int", new Integer(42));
	// data.put("float", "123.43");
	// data.put("url", "http://www.google.fr/");
	// data.put("password", "password");
	// data.put("directory", "");
	// data.put("file", "");
	// data.put("textarea", "long long\nlong long\nlong long\ntext...");
	// data.put("comboReadOnly", "Value 1");
	// data.put("combo", "Other Value");
	//
	// data.put("cb1", new Boolean(true));
	// // cb2 is not initialized
	// data.put("slider", new Integer(40));
	// data.put("spinner", new Integer(30));
	// data.put("color", new RGB(120, 15, 30));
	// // font is not initialized
	//
	// data.put("radio", "Radio button 3");
	// data.put("cb3", new Boolean(true));
	//
	// // cb4 to cb14 are not initialised
	//
	// data.put("cacheSizeUnit", "Megabytes");
	// data.put("openMode", "Double click");
	//
	// return data;
	// }
	//
	// protected static void createDocumentTab(final PreferenceWindow window) {
	// final PWTab documentTab = window.addTab(Display.getDefault().getSystemImage(SWT.ICON_CANCEL), "Document");
	//
	// documentTab.add(new PWLabel("Let's start with Text, Separator, Combo and button")).//
	// add(new PWStringText("String :", "text").setAlignment(GridData.FILL)).//
	// add(new PWIntegerText("Integer :", "int"));
	// documentTab.add(new PWFloatText("Float :", "float"));
	// documentTab.add(new PWURLText("URL :", "url"));
	// documentTab.add(new PWPasswordText("Password :", "password"));
	// documentTab.add(new PWDirectoryChooser("Directory :", "directory"));
	// documentTab.add(new PWFileChooser("File :", "file"));
	// documentTab.add(new PWTextarea("Textarea :", "textarea"));
	//
	// documentTab.add(new PWSeparator());
	//
	// documentTab.add(new PWCombo("Combo (read-only):", "comboReadOnly", "Value 1", "Value 2", "Value 3"));
	// documentTab.add(new PWCombo("Combo (editable):", "combo", true, "Value 1", "Value 2", "Value 3"));
	//
	// documentTab.add(new PWSeparator("Titled separator"));
	// documentTab.add(new PWButton("First button", new SelectionAdapter() {
	//
	// /**
	// * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
	// */
	// @Override
	// public void widgetSelected(final SelectionEvent e) {
	// Dialog.inform("Hi", "You pressed the first button");
	// }
	//
	// }).setAlignment(GridData.END));
	// }
	//
	// protected static void createInfoTab(final PreferenceWindow window) {
	// final PWTab infoTab = window.addTab(Display.getDefault().getSystemImage(SWT.ICON_ERROR), "Info");
	//
	// infoTab.add(new PWLabel("Checkboxes, Slider,Spinner, Color chooser, Font chooser"));
	// infoTab.add(new PWCheckbox("Checkbox 1", "cb1"));
	// infoTab.add(new PWCheckbox("Checkbox 2", "cb2"));
	//
	// infoTab.add(new PWSeparator());
	//
	// infoTab.add(new PWScale("Slider : ", "slider", 0, 100, 10));
	// infoTab.add(new PWSpinner("Spinner :", "spinner", 0, 100));
	//
	// infoTab.add(new PWSeparator());
	//
	// infoTab.add(new PWColorChooser("Color :", "color"));
	// infoTab.add(new PWFontChooser("Font :", "font"));
	//
	// }
	//
	// protected static void createTerminalTab(final PreferenceWindow window) {
	// final PWTab terminalTab = window.addTab(Display.getDefault().getSystemImage(SWT.ICON_INFORMATION), "Terminal");
	//
	// terminalTab.add(new PWLabel("Group, radio, indentation and group of buttons in a row"));
	//
	// final PWGroup group = new PWGroup("Group of buttons");
	// group.add(new PWRadio("Radio buttons:", "radio", "Radio button 1", "Radio button 2", "Radio button 3"));
	// terminalTab.add(group);
	//
	// terminalTab.add(new PWCheckbox("Checkbox 3 (indented)", "cb3").setIndent(30).setWidth(200));
	//
	// terminalTab.add(new PWRow().//
	// add(new PWButton("First button", new SelectionAdapter() {})).//
	// add(new PWButton("Second button", new SelectionAdapter() {})).//
	// add(new PWButton("Third button", new SelectionAdapter() {})));
	//
	// }
	//
	// protected static void createPrinterTab(final PreferenceWindow window) {
	// final PWTab printerTab = window.addTab(Display.getDefault().getSystemImage(SWT.ICON_QUESTION), "Printer");
	//
	// printerTab.add(new PWLabel("Play <i>with</i> <b>checkboxes</b>"));
	//
	// final PWGroup group = new PWGroup(false);
	// group.add(new PWRow().add(new PWCheckbox("First choice", "cb4")).add(new PWCheckbox("Second choice", "cb5")));
	// group.add(new PWRow().add(new PWCheckbox("Third choice", "cb6")).add(new PWCheckbox("Fourth choice", "cb7")));
	// group.add(new PWRow().add(new PWCheckbox("Fifth choice", "cb8")).add(new PWCheckbox("Sixth choice", "cb9")));
	// group.add(new PWRow().add(new PWCheckbox("Seventh choice", "cb10"))
	// .add(new PWCheckbox("Eighth choice", "cb11")));
	// printerTab.add(group);
	//
	// printerTab.add(new PWRow().//
	// add(new PWCheckbox("Automatically check for new versions", "cb12").setWidth(300)).//
	// add(new PWButton("Check for updates...", new SelectionAdapter() {}).setWidth(250)
	// .setAlignment(GridData.END)));
	//
	// printerTab.add(new PWSeparator());
	//
	// final PWGroup group2 = new PWGroup(false);
	// group2.add(new PWRow().add(new PWLabel("Aligned checkbox")).add(new PWCheckbox("Bla bla bla 1", "cb13")));
	// group2.add(new PWRow().add(new PWLabel("")).add(new PWCheckbox("Bla bla bla 2", "cb14")));
	// printerTab.add(group2);
	// }
	//
	// protected static void createSystemTab(final PreferenceWindow window) {
	// final PWTab systemTab = window.addTab(Display.getDefault().getSystemImage(SWT.ICON_SEARCH), "System");
	//
	// systemTab.add(new PWLabel("Rows..."));
	//
	// systemTab.add(new PWRow().add(new PWCombo("Cache size", "cacheSize", true, "128", "256", "512", "1024")).//
	// add(new PWCombo(null, "cacheSizeUnit", "Bytes", "Kilobytes", "Megabytes")));
	//
	// systemTab.add(new PWRow().//
	// add(new PWCombo("Display:", "display", "10", "20", "30", "40", "50")).//
	// add(new PWLabel("per page")));
	//
	// systemTab.add(new PWSeparator());
	//
	// systemTab.add(new PWLabel("Enabled/disabled..."));
	//
	// systemTab.add(new PWCheckbox("Show information", "show").setWidth(150));
	// systemTab.add(new PWGroup("Open Mode")
	// .setEnabler(new EnabledIfTrue("show"))
	// .//
	// add(new PWRadio(null, "openMode", "Double click", "Single click"))
	// .//
	// add(new PWCheckbox("Select on hover", "selectonhover").setIndent(10).setWidth(200)
	// .setEnabler(new EnabledIfEquals("openMode", "Single click"))).//
	// add(new PWCheckbox("Open when using arrow keys", "openarrow").setIndent(10).setWidth(200)
	// .setEnabler(new EnabledIfEquals("openMode", "Single click"))));
	// }
	//
}
