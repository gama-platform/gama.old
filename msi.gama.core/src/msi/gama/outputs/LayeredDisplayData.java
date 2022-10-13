/*******************************************************************************************************
 *
 * LayeredDisplayData.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.outputs;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import com.google.common.base.Objects;

import msi.gama.common.geometry.Envelope3D;
import msi.gama.common.geometry.ICoordinates;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.common.preferences.GamaPreferences;
import msi.gama.common.preferences.IPreferenceChangeListener.IPreferenceAfterChangeListener;
import msi.gama.kernel.experiment.ExperimentAgent;
import msi.gama.kernel.simulation.SimulationAgent;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.outputs.layers.properties.GenericCameraDefinition;
import msi.gama.outputs.layers.properties.GenericLightDefinition;
import msi.gama.outputs.layers.properties.ICameraDefinition;
import msi.gama.outputs.layers.properties.ILightDefinition;
import msi.gama.outputs.layers.properties.LightDefinition;
import msi.gama.outputs.layers.properties.RotationDefinition;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaColor;
import msi.gama.util.GamaListFactory;
import msi.gaml.compilation.GAML;
import msi.gaml.descriptions.IDescription;
import msi.gaml.descriptions.ModelDescription;
import msi.gaml.expressions.IExpression;
import msi.gaml.operators.Cast;
import msi.gaml.statements.Facets;
import msi.gaml.types.Types;
import ummisco.gama.dev.utils.DEBUG;

/**
 */
public class LayeredDisplayData {

	static {
		DEBUG.OFF();
	}

	/**
	 * The Enum Changes.
	 */
	public enum Changes {

		/** The background. */
		BACKGROUND,

		/** The zoom. */
		ZOOM
	}

	/** The Constant OPENGL2. */
	public static final String OPENGL2 = "opengl2";

	/** The Constant WEB. */
	public static final String WEB = "web";

	/** The Constant INITIAL_ZOOM. */
	public static final Double INITIAL_ZOOM = 1.0;

	/**
	 * The listener interface for receiving displayData events. The class that is interested in processing a displayData
	 * event implements this interface, and the object created with that class is registered with a component using the
	 * component's <code>addDisplayDataListener<code> method. When the displayData event occurs, that object's
	 * appropriate method is invoked.
	 *
	 * @see DisplayDataEvent
	 */
	public interface DisplayDataListener {

		/**
		 * Changed.
		 *
		 * @param property
		 *            the property
		 * @param value
		 *            the value
		 */
		void changed(Changes property, Object value);
	}

	/** The listeners. */
	public final Set<DisplayDataListener> listeners = new CopyOnWriteArraySet<>();

	/**
	 * Adds the listener.
	 *
	 * @param listener
	 *            the listener
	 */
	public void addListener(final DisplayDataListener listener) {
		listeners.add(listener);
	}

	/**
	 * Removes the listener.
	 *
	 * @param listener
	 *            the listener
	 */
	public void removeListener(final DisplayDataListener listener) {
		listeners.remove(listener);
	}

	/**
	 * Notify listeners.
	 *
	 * @param property
	 *            the property
	 * @param value
	 *            the value
	 */
	public void notifyListeners(final Changes property, final Object value) {
		for (final DisplayDataListener listener : listeners) { listener.changed(property, value); }
	}

	/**
	 * Colors
	 */
	private GamaColor backgroundColor = GamaPreferences.Displays.CORE_BACKGROUND.getValue();
	//
	// /** The ambient color. */
	// private GamaColor ambientColor = new GamaColor(64, 64, 64, 255);

	/** The highlight color. */
	private GamaColor highlightColor = GamaPreferences.Displays.CORE_HIGHLIGHT.getValue();

	/** The toolbar color. */
	private GamaColor toolbarColor = null;

	/**
	 * Properties
	 */
	private boolean isAutosaving = false;

	/** The autosaving path. */
	private String autosavingPath = "";

	/** The is toolbar visible. */
	private boolean isToolbarVisible = GamaPreferences.Displays.CORE_DISPLAY_TOOLBAR.getValue();

	/** The is synchronized. */
	// private boolean isSynchronized = GamaPreferences.Runtime.CORE_SYNC.getValue();

	/** The display type. */
	private String displayType = IKeyword._2D.equalsIgnoreCase(GamaPreferences.Displays.CORE_DISPLAY.getValue())
			? IKeyword._2D : IKeyword._3D;

	/** The env width. */
	private double envWidth = 0d;

	/** The env height. */
	private double envHeight = 0d;

	/** The is antialiasing. */
	private boolean isAntialiasing = GamaPreferences.Displays.CORE_ANTIALIAS.getValue();

	/** The image dimension. */
	private GamaPoint imageDimension = new GamaPoint(-1, -1);

	/** The zoom level. */
	private Double zoomLevel = INITIAL_ZOOM;

	/** The Constant KEYSTONE_IDENTITY. */
	public static final ICoordinates KEYSTONE_IDENTITY =
			ICoordinates.ofLength(4).setTo(0d, 0, 0, 0, 1, 0, 1, 1, 0, 1, 0, 0);

	/** The keystone. */
	private final ICoordinates keystone = (ICoordinates) KEYSTONE_IDENTITY.clone();

	/** The is open GL. */
	private boolean is3D;

	/**
	 * OpenGL
	 */

	private boolean isWireframe = false;

	/** The ortho. */
	private boolean ortho = false;

	/** The is showing FPS. */
	private boolean isShowingFPS = false; // GamaPreferences.CORE_SHOW_FPS.getValue();

	/** The is drawing environment. */
	private boolean isDrawingEnvironment = GamaPreferences.Displays.CORE_DRAW_ENV.getValue();

	/** The constant background. */
	private boolean constantBackground = true;

	/** The z near. */
	private double zNear = -1.0;

	/** The z far. */
	private double zFar = -1.0;

	/** The full screen. */
	private int fullScreen = -1;

	/** The highlight listener. */
	IPreferenceAfterChangeListener<GamaColor> highlightListener = this::setHighlightColor;

	/**
	 * Instantiates a new layered display data.
	 */
	public LayeredDisplayData() {
		GamaPreferences.Displays.CORE_HIGHLIGHT.addChangeListener(highlightListener);
	}

	/**
	 * Dispose.
	 */
	public void dispose() {
		GamaPreferences.Displays.CORE_HIGHLIGHT.removeChangeListener(highlightListener);
		listeners.clear();
	}

	/**
	 * @return the backgroundColor
	 */
	public GamaColor getBackgroundColor() { return backgroundColor; }

	/**
	 * @param backgroundColor
	 *            the backgroundColor to set
	 */
	public void setBackgroundColor(final GamaColor backgroundColor) {
		this.backgroundColor = backgroundColor;
		notifyListeners(Changes.BACKGROUND, backgroundColor);
	}

	/**
	 * @return the autosave
	 */
	public boolean isAutosave() { return isAutosaving; }

	/**
	 * @param autosave
	 *            the autosave to set
	 */
	public void setAutosave(final boolean autosave) { this.isAutosaving = autosave; }

	/**
	 * Sets the autosave path.
	 *
	 * @param p
	 *            the new autosave path
	 */
	public void setAutosavePath(final String p) { this.autosavingPath = p; }

	/**
	 * Gets the autosave path.
	 *
	 * @return the autosave path
	 */
	public String getAutosavePath() { return autosavingPath; }

	/**
	 * Checks if is wireframe.
	 *
	 * @return true, if is wireframe
	 */
	public boolean isWireframe() { return isWireframe; }

	/**
	 * Sets the wireframe.
	 *
	 * @param t
	 *            the new wireframe
	 */
	public void setWireframe(final boolean t) { isWireframe = t; }

	/**
	 * @return the ortho
	 */
	public boolean isOrtho() { return ortho; }

	/**
	 * @param ortho
	 *            the ortho to set
	 */
	public void setOrtho(final boolean ortho) { this.ortho = ortho; }

	/**
	 * @return the showfps
	 */
	public boolean isShowfps() { return isShowingFPS; }

	/**
	 * @param showfps
	 *            the showfps to set
	 */
	public void setShowfps(final boolean showfps) { this.isShowingFPS = showfps; }

	/**
	 * Gets the z near.
	 *
	 * @return the z near
	 */
	public double getzNear() {
		return zNear;
	}

	/**
	 * Gets the z far.
	 *
	 * @return the z far
	 */
	public double getzFar() {
		return zFar;
	}

	/**
	 * @return the drawEnv
	 */
	public boolean isDrawEnv() { return isDrawingEnvironment; }

	/**
	 * @param drawEnv
	 *            the drawEnv to set
	 */
	public void setDrawEnv(final boolean drawEnv) { this.isDrawingEnvironment = drawEnv; }

	/**
	 * @return the displayType
	 */
	public String getDisplayType() { return displayType; }

	/**
	 * @param displayType
	 *            the displayType to set
	 */
	public void setDisplayType(final String displayType) {
		this.displayType = displayType;
		is3D = IKeyword.OPENGL.equals(displayType) || IKeyword._3D.equals(displayType) || OPENGL2.equals(displayType);

	}

	/**
	 * @return the imageDimension
	 */
	public GamaPoint getImageDimension() { return imageDimension; }

	/**
	 * @param imageDimension
	 *            the imageDimension to set
	 */
	public void setImageDimension(final GamaPoint imageDimension) { this.imageDimension = imageDimension; }

	/**
	 * @return the envWidth
	 */
	public double getEnvWidth() { return envWidth; }

	/**
	 * @param envWidth
	 *            the envWidth to set
	 */
	public void setEnvWidth(final double envWidth) { this.envWidth = envWidth; }

	/**
	 * @return the envHeight
	 */
	public double getEnvHeight() { return envHeight; }

	/**
	 * @param envHeight
	 *            the envHeight to set
	 */
	public void setEnvHeight(final double envHeight) { this.envHeight = envHeight; }

	/**
	 * Gets the max env dim.
	 *
	 * @return the max env dim
	 */
	public double getMaxEnvDim() { return envWidth > envHeight ? envWidth : envHeight; }

	/**
	 * @return
	 */
	public GamaColor getHighlightColor() { return highlightColor; }

	/**
	 * Sets the highlight color.
	 *
	 * @param hc
	 *            the new highlight color
	 */
	public void setHighlightColor(final GamaColor hc) { highlightColor = hc; }

	/**
	 * Checks if is antialias.
	 *
	 * @return true, if is antialias
	 */
	public boolean isAntialias() { return isAntialiasing; }

	/**
	 * Sets the antialias.
	 *
	 * @param a
	 *            the new antialias
	 */
	public void setAntialias(final boolean a) { isAntialiasing = a; }

	/**
	 * @return the zoomLevel
	 */
	public Double getZoomLevel() { return zoomLevel; }

	/**
	 * @param zoomLevel
	 *            the zoomLevel to set
	 */
	public void setZoomLevel(final Double zoomLevel, final boolean notify) {
		if (this.zoomLevel != null && this.zoomLevel.equals(zoomLevel)) return;
		this.zoomLevel = zoomLevel;
		if (notify) { notifyListeners(Changes.ZOOM, this.zoomLevel); }
	}

	/**
	 * Full screen.
	 *
	 * @return the int
	 */
	public int fullScreen() {
		return fullScreen;
	}

	/**
	 * Sets the overlay.
	 *
	 * @param fs
	 *            the new overlay
	 */
	public void setFullScreen(final int fs) { fullScreen = fs; }

	/**
	 * Sets the keystone.
	 *
	 * @param value
	 *            the new keystone
	 */
	public void setKeystone(final List<GamaPoint> value) {
		if (value == null) return;
		keystone.setTo(value.toArray(new GamaPoint[4]));
	}

	/**
	 * Sets the keystone.
	 *
	 * @param value
	 *            the new keystone
	 */
	public void setKeystone(final ICoordinates value) {
		if (value == null) return;
		this.keystone.setTo(value.toCoordinateArray());
	}

	/**
	 * Gets the keystone.
	 *
	 * @return the keystone
	 */
	public ICoordinates getKeystone() { return this.keystone; }

	/**
	 * Checks if is keystone defined.
	 *
	 * @return true, if is keystone defined
	 */
	public boolean isKeystoneDefined() { return !keystone.equals(KEYSTONE_IDENTITY); }

	/**
	 * Checks if is toolbar visible.
	 *
	 * @return true, if is toolbar visible
	 */
	public boolean isToolbarVisible() { return this.isToolbarVisible; }

	/**
	 * Gets the toolbar color.
	 *
	 * @return the toolbar color
	 */
	public GamaColor getToolbarColor() { return toolbarColor == null ? getBackgroundColor() : toolbarColor; }

	/**
	 * Sets the toolbar visible.
	 *
	 * @param b
	 *            the new toolbar visible
	 */
	public void setToolbarVisible(final boolean b) { isToolbarVisible = b; }

	/**
	 * Inits the with.
	 *
	 * @param scope
	 *            the scope
	 * @param desc
	 *            the desc
	 */
	public void initWith(final IScope scope, final IDescription desc) {
		final Facets facets = desc.getFacets();
		// Initializing the size of the environment
		SimulationAgent sim = scope.getSimulation();
		// hqnghi if layer come from micro-model
		final ModelDescription micro = desc.getModelDescription();
		final ModelDescription main = scope.getModel().getDescription();
		final boolean fromMicroModel = main.getMicroModel(micro.getAlias()) != null;
		if (fromMicroModel) {
			final ExperimentAgent exp = (ExperimentAgent) scope.getRoot()
					.getExternMicroPopulationFor(micro.getAlias() + "." + desc.getOriginName()).getAgent(0);
			sim = exp.getSimulation();
		}
		// end-hqnghi
		Envelope3D env = null;
		if (sim != null) {
			env = sim.getEnvelope();
		} else {
			env = Envelope3D.of(0, 100, 0, 100, 0, 0);
		}
		setEnvWidth(env.getWidth());
		setEnvHeight(env.getHeight());
		env.dispose();

		updateAutoSave(scope, facets.getExpr(IKeyword.AUTOSAVE));
		final IExpression toolbar = facets.getExpr(IKeyword.TOOLBAR);
		if (toolbar != null) {
			if (toolbar.getGamlType() == Types.BOOL) {
				setToolbarVisible(Cast.asBool(scope, toolbar.value(scope)));
			} else {
				setToolbarVisible(true);
				toolbarColor = Cast.asColor(scope, toolbar.value(scope));
			}
		}
		final IExpression fps = facets.getExpr("show_fps");
		if (fps != null) { setShowfps(Cast.asBool(scope, fps.value(scope))); }

		final IExpression nZ = facets.getExpr("z_near");
		if (nZ != null) { setZNear(Cast.asFloat(scope, nZ.value(scope))); }

		final IExpression fZ = facets.getExpr("z_far");
		if (fZ != null) { setZFar(Cast.asFloat(scope, fZ.value(scope))); }
		final IExpression denv = facets.getExpr("draw_env", "axes");
		if (denv != null) { setDrawEnv(Cast.asBool(scope, denv.value(scope))); }

		final IExpression ortho = facets.getExpr(IKeyword.ORTHOGRAPHIC_PROJECTION);
		if (ortho != null) { setOrtho(Cast.asBool(scope, ortho.value(scope))); }

		final IExpression keystone_exp = facets.getExpr(IKeyword.KEYSTONE);
		if (keystone_exp != null) {
			@SuppressWarnings ("unchecked") final List<GamaPoint> val =
					GamaListFactory.create(scope, Types.POINT, Cast.asList(scope, keystone_exp.value(scope)));
			if (val.size() >= 4) { setKeystone(val); }
		}

		initRotationFacets(scope, facets);

		final IExpression lightOn = facets.getExpr(IKeyword.IS_LIGHT_ON);
		if (lightOn != null) { setLightOn(Cast.asBool(scope, lightOn.value(scope))); }

		initializePresetCameraDefinitions();
		cameraNameExpression = facets.getExpr(IKeyword.CAMERA);
		setCameraNameFromGaml(cameraNameExpression != null ? Cast.asString(scope, cameraNameExpression.value(scope))
				: IKeyword.DEFAULT);

		final IExpression fs = facets.getExpr(IKeyword.FULLSCREEN);
		if (fs != null) {
			int monitor;
			if (fs.getGamlType() == Types.BOOL) {
				monitor = Cast.asBool(scope, fs.value(scope)) ? 0 : -1;
			} else {
				monitor = Cast.asInt(scope, fs.value(scope));
			}
			setFullScreen(monitor);
		}

		// final IExpression use_shader = facets.getExpr("use_shader");
		// if (use_shader != null) {
		// this.useShader = Cast.asBool(scope, use_shader.value(scope));
		// }

		final IExpression color = facets.getExpr(IKeyword.BACKGROUND);
		if (color != null) {
			setBackgroundColor(Cast.asColor(scope, color.value(scope)));
			constantBackground = color.isConst();
		}

		final IExpression light = facets.getExpr("ambient_light");
		if (light != null) {
			GamaColor intensity;
			if (light.getGamlType().equals(Types.COLOR)) {
				intensity = Cast.asColor(scope, light.value(scope));
			} else {
				final int meanValue = Cast.asInt(scope, light.value(scope));
				intensity = new GamaColor(meanValue, meanValue, meanValue, 255);
			}
			lights.put(ILightDefinition.ambient, new GenericLightDefinition(ILightDefinition.ambient, -1, intensity));
		}

		final IExpression antialias = facets.getExpr("antialias");
		if (antialias != null) { setAntialias(Cast.asBool(scope, antialias.value(scope))); }

		if (camera != null) { camera.refresh(scope); }
		if (rotation != null) { rotation.refresh(scope); }
		lights.forEach((s, l) -> l.refresh(scope));
	}

	/**
	 * Update auto save.
	 *
	 * @param scope
	 *            the scope
	 * @param auto
	 *            the auto
	 * @throws GamaRuntimeException
	 *             the gama runtime exception
	 */
	private void updateAutoSave(final IScope scope, final IExpression auto) throws GamaRuntimeException {
		if (auto == null) return;
		if (auto.getGamlType().equals(Types.POINT)) {
			GamaPoint result = Cast.asPoint(scope, auto.value(scope));
			setAutosave(result != null);
			setImageDimension(result);
		} else if (auto.getGamlType().equals(Types.STRING)) {
			String result = Cast.asString(scope, auto.value(scope));
			setAutosave(result != null && !result.isBlank());
			setAutosavePath(result);
		} else {
			setAutosave(Cast.asBool(scope, auto.value(scope)));
		}
	}

	/**
	 * Sets the z far.
	 *
	 * @param zF
	 *            the new z far
	 */
	public void setZFar(final Double zF) {
		zFar = zF;

	}

	/**
	 * Sets the z near.
	 *
	 * @param zN
	 *            the new z near
	 */
	public void setZNear(final Double zN) { zNear = zN; }

	/**
	 * Update.
	 *
	 * @param scope
	 *            the scope
	 * @param facets
	 *            the facets
	 */
	public void update(final IScope scope, final Facets facets) {

		if (cameraNameExpression != null) {
			setCameraNameFromGaml(Cast.asString(scope, cameraNameExpression.value(scope)));
		}

		if (camera != null) { camera.refresh(scope); }
		if (rotation != null) { rotation.refresh(scope); }
		lights.forEach((s, l) -> l.refresh(scope));

		updateAutoSave(scope, facets.getExpr(IKeyword.AUTOSAVE));
		// /////////////// dynamic Lighting ///////////////////

		if (!constantBackground) {

			final IExpression color = facets.getExpr(IKeyword.BACKGROUND);
			if (color != null) { setBackgroundColor(Cast.asColor(scope, color.value(scope))); }

		}

	}

	/**
	 * Checks if is open GL 2.
	 *
	 * @return true, if is open GL 2
	 */
	public boolean isOpenGL2() { return OPENGL2.equals(displayType); }

	/**
	 * Checks if is web.
	 *
	 * @return true, if is web
	 */
	public boolean isWeb() { return WEB.equals(displayType); }

	/**
	 * Checks if is open GL.
	 *
	 * @return true, if is open GL
	 */
	public boolean is3D() {
		return is3D;
	}

	// ************************************************************************************************
	// ************************************************************************************************
	// * CAMERA
	// ************************************************************************************************
	// ************************************************************************************************

	/** The camera definitions. */
	private final Map<String, ICameraDefinition> cameraDefinitions = new LinkedHashMap<>();

	/** The current camera. */
	private ICameraDefinition camera;

	/** The camera name expression. */
	private IExpression cameraNameExpression;

	/**
	 * Adds the camera definition.
	 *
	 * @param name
	 *            the name
	 * @param definition
	 *            the definition
	 */
	public void addCameraDefinition(final ICameraDefinition definition) {
		cameraDefinitions.putIfAbsent(definition.getName(), definition);
	}

	/**
	 * Gets the distance coefficient.
	 *
	 * @return the distance coefficient
	 */
	public double getCameraDistanceCoefficient() { return isDrawEnv() ? 1.60 : 1.46; }

	/**
	 * Reset camera.
	 */
	public void resetCamera() {
		if (camera != null) { camera.reset(); }
	}

	/**
	 * Gets the distance.
	 *
	 * @return the distance
	 */
	public double getCameraDistance() { return camera.getDistance(); }

	/**
	 * Sets the distance.
	 *
	 * @param distance
	 *            the new distance
	 */
	public void setCameraDistance(final double distance) {
		camera.setDistance(distance);
	}

	/**
	 * Initialize preset camera definitions.
	 */
	private void initializePresetCameraDefinitions() {
		double w = getEnvWidth();
		double h = getEnvHeight();
		double max = Math.max(w, h) * getCameraDistanceCoefficient();
		GamaPoint target = new GamaPoint(w / 2, -h / 2, 0); // Y negated
		for (String preset : ICameraDefinition.PRESETS) {
			addCameraDefinition(new GenericCameraDefinition(preset, target, getEnvWidth(), getEnvHeight(), max));
		}
		cameraDefinitions.putIfAbsent(IKeyword.DEFAULT,
				cameraDefinitions.get(GamaPreferences.Displays.OPENGL_DEFAULT_CAM.getValue()));
	}

	/**
	 * Sets the preset camera.
	 *
	 * @param newValue
	 *            the new preset camera
	 */
	public void setCameraNameFromGaml(final String newValue) {
		if (camera != null && Objects.equal(newValue, camera.getName())) return;
		resetCamera();
		camera = cameraDefinitions.get(newValue);
		if (camera == null) { camera = cameraDefinitions.get(IKeyword.DEFAULT); }
		// notifyListeners(Changes.CAMERA_PRESET, newValue);
	}

	/**
	 * Sets the preset camera.
	 *
	 * @param newValue
	 *            the new preset camera
	 */
	public void setCameraNameFromUser(final String newValue) {
		if (camera != null && Objects.equal(newValue, camera.getName())) return;
		// We force the camera name to remain the same by modifying the expression
		cameraNameExpression = GAML.getExpressionFactory().createConst(newValue, Types.STRING);
		// resetCamera();
		camera = cameraDefinitions.get(newValue);
		if (camera == null) { camera = cameraDefinitions.get(IKeyword.DEFAULT); }
		// notifyListeners(Changes.CAMERA_PRESET, newValue);
	}

	/**
	 * @return the cameraPos
	 */
	public GamaPoint getCameraPos() { return camera.getLocation(); }

	/**
	 * @param cameraPos
	 *            the cameraPos to set
	 */
	public void setCameraPos(final GamaPoint point) {
		camera.setLocation(point);
	}

	/**
	 * @return the cameraLookPos
	 */
	public GamaPoint getCameraTarget() { return camera.getTarget(); }

	/**
	 * @param cameraLookPos
	 *            the cameraLookPos to set
	 */
	public void setCameraTarget(final GamaPoint point) {
		camera.setTarget(point);
	}

	/**
	 * @return the cameraLens
	 */
	public int getCameraLens() { return camera.getLens(); }

	/**
	 * Gets the preset camera.
	 *
	 * @return the preset camera
	 */
	public String getCameraName() { return camera.getName(); }

	/**
	 * Gets the camera names.
	 *
	 * @return the camera names
	 */
	public Collection<String> getCameraNames() { return cameraDefinitions.keySet(); }

	/**
	 * Disable camera interactions.
	 *
	 * @param disableCamInteract
	 *            the disable cam interact
	 */
	public void setCameraLocked(final boolean disableCamInteract) {
		camera.setInteractive(!disableCamInteract);
	}

	/**
	 * Camera interaction disabled.
	 *
	 * @return true, if successful
	 */
	public boolean isCameraLocked() { return !camera.isInteractive(); }

	// ************************************************************************************************
	// ************************************************************************************************
	// * ROTATION
	// ************************************************************************************************
	// ************************************************************************************************

	/** The rotation. */
	RotationDefinition rotation;

	/**
	 * Inits the rotation facets.
	 *
	 * @param scope
	 *            the scope
	 * @param facets
	 *            the facets
	 */
	private void initRotationFacets(final IScope scope, final Facets facets) {
		final IExpression rotate_exp = facets.getExpr(IKeyword.ROTATE);
		if (rotate_exp != null) {
			final double val = Cast.asFloat(scope, rotate_exp.value(scope));
			setRotationAngle(val);
		}
	}

	/**
	 * Sets the rotation.
	 *
	 * @param rotation
	 *            the new rotation
	 */
	public void setRotation(final RotationDefinition rotation) { this.rotation = rotation; }

	/**
	 * @return
	 */
	public boolean isContinuousRotationOn() { return rotation != null && rotation.isDynamic(); }

	/**
	 * Sets the continuous rotation.
	 *
	 * @param r
	 *            the new continuous rotation
	 */
	public void setContinuousRotation(final boolean r) {
		if (rotation != null) { rotation.setDynamic(r); }
	}

	/**
	 * Gets the current rotation about Z.
	 *
	 * @return the current rotation about Z
	 */
	public double getRotationAngle() { return rotation == null ? 0d : rotation.getCurrentAngle(); }

	/**
	 * Checks for rotation.
	 *
	 * @return true, if successful
	 */
	public boolean hasRotation() {
		return rotation != null && rotation.getAngleDelta() != 0d && !rotation.getAxis().isNull();
	}

	/**
	 * Sets the z rotation angle.
	 *
	 * @param val
	 *            the new z rotation angle
	 */
	public void setRotationAngle(final double val) {
		if (rotation != null) { rotation.setAngle(val); }
	}

	/**
	 * Gets the rotation center.
	 *
	 * @return the rotation center
	 */
	public GamaPoint getRotationCenter() { return rotation != null ? rotation.getCenter().yNegated() : null; }

	/**
	 * Gets the rotation axis.
	 *
	 * @return the rotation axis
	 */
	public GamaPoint getRotationAxis() { return rotation != null ? rotation.getAxis().yNegated() : null; }

	/**
	 * Reset Z rotation.
	 */
	public void resetRotation() {
		if (rotation != null) { rotation.reset(); }
	}

	// ************************************************************************************************
	// ************************************************************************************************
	// * LIGHT
	// ************************************************************************************************
	// ************************************************************************************************

	/** The is light on. */
	private boolean isLightOn = true;

	/** The light index. */
	private int lightIndex = 1;

	/** The lights. */
	private final Map<String, ILightDefinition> lights = new LinkedHashMap<>() {
		{
			put(ILightDefinition.ambient, new GenericLightDefinition(ILightDefinition.ambient, -1,
					GamaPreferences.Displays.OPENGL_DEFAULT_LIGHT_INTENSITY.getValue()));
			put(IKeyword.DEFAULT, new GenericLightDefinition(IKeyword.DEFAULT, 0,
					GamaPreferences.Displays.OPENGL_DEFAULT_LIGHT_INTENSITY.getValue()));
		}
	};

	/**
	 * @return the isLightOn
	 */
	public boolean isLightOn() { return isLightOn; }

	/**
	 * @param isLightOn
	 *            the isLightOn to set
	 */
	public void setLightOn(final boolean isLightOn) { this.isLightOn = isLightOn; }

	/**
	 * Gets the diffuse lights.
	 *
	 * @return the diffuse lights
	 */
	public Map<String, ILightDefinition> getLights() { return lights; }

	/**
	 * Adds the light definition.
	 *
	 * @param definition
	 *            the definition
	 */
	public void addLightDefinition(final LightDefinition definition) {
		String name = definition.getName();
		int index = lights.containsKey(name) ? lights.get(name).getId() : lightIndex++;
		definition.setId(index);
		lights.put(name, definition);
	}

}