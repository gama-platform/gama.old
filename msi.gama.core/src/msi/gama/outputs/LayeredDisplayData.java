/*******************************************************************************************************
 *
 * LayeredDisplayData.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.8.2).
 *
 * (c) 2007-2021 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.outputs;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import msi.gama.common.geometry.Envelope3D;
import msi.gama.common.geometry.ICoordinates;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.common.preferences.GamaPreferences;
import msi.gama.common.preferences.IPreferenceChangeListener.IPreferenceAfterChangeListener;
import msi.gama.kernel.experiment.ExperimentAgent;
import msi.gama.kernel.simulation.SimulationAgent;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.runtime.IScope;
import msi.gama.util.GamaColor;
import msi.gama.util.GamaListFactory;
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

		/** The split layer. */
		SPLIT_LAYER,

		/** The change camera. */
		CHANGE_CAMERA,

		/** The camera pos. */
		CAMERA_POS,

		/** The camera target. */
		CAMERA_TARGET,

		/** The camera orientation. */
		CAMERA_ORIENTATION,

		/** The camera preset. */
		CAMERA_PRESET,

		/** The background. */
		BACKGROUND,

		/** The highlight. */
		HIGHLIGHT,

		/** The zoom. */
		ZOOM,

		/** The keystone. */
		KEYSTONE,

		/** The antialias. */
		ANTIALIAS,

		/** The rotation. */
		ROTATION;
	}

	/** The Constant JAVA2D. */
	public static final String JAVA2D = "java2D";

	/** The Constant OPENGL. */
	public static final String OPENGL = "opengl";

	/** The Constant OPENGL2. */
	public static final String OPENGL2 = "opengl2";

	/** The Constant WEB. */
	public static final String WEB = "web";

	/** The Constant THREED. */
	public static final String THREED = "3D";

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

	/** The ambient color. */
	private GamaColor ambientColor = new GamaColor(64, 64, 64, 255);

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
	private boolean isSynchronized = GamaPreferences.Runtime.CORE_SYNC.getValue();

	/** The display type. */
	private String displayType =
			JAVA2D.equalsIgnoreCase(GamaPreferences.Displays.CORE_DISPLAY.getValue()) ? JAVA2D : OPENGL;

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

	/** The lights. */
	private final LightPropertiesStructure lights[] = new LightPropertiesStructure[8];

	/** The Constant KEYSTONE_IDENTITY. */
	public static final ICoordinates KEYSTONE_IDENTITY =
			ICoordinates.ofLength(4).setTo(0d, 0, 0, 0, 1, 0, 1, 1, 0, 1, 0, 0);

	/** The keystone. */
	private final ICoordinates keystone = (ICoordinates) KEYSTONE_IDENTITY.clone();

	/** The z rotation angle delta. */
	private double zRotationAngleDelta = 0;

	/** The current rotation about Z. */
	private double currentRotationAboutZ = 0;

	/** The is open GL. */
	private boolean isOpenGL;

	/**
	 * OpenGL
	 */

	private boolean isWireframe = false;

	/** The ortho. */
	private boolean ortho = false;

	/** The disable camera interaction. */
	private boolean disableCameraInteraction = false; // "fixed_camera" facet

	/** The is showing FPS. */
	private boolean isShowingFPS = false; // GamaPreferences.CORE_SHOW_FPS.getValue();

	/** The is drawing environment. */
	private boolean isDrawingEnvironment = GamaPreferences.Displays.CORE_DRAW_ENV.getValue();

	/** The is light on. */
	private boolean isLightOn = true; // GamaPreferences.CORE_IS_LIGHT_ON.getValue();

	/** The camera pos. */
	private GamaPoint cameraPos = null;

	/** The camera look pos. */
	private GamaPoint cameraLookPos = null;

	/** The camera orientation. */
	private GamaPoint cameraOrientation = null;

	/** The preset camera. */
	private String presetCamera = "";

	/** The camera lens. */
	private int cameraLens = 45;

	/** The split distance. */
	private Double splitDistance;

	/** The is rotating. */
	private boolean isRotating;

	/** The is using arc ball camera. */
	private boolean isUsingArcBallCamera = true;

	/** The is splitting layers. */
	private boolean isSplittingLayers;

	/** The constant background. */
	private boolean constantBackground = true;

	/** The constant ambient light. */
	private boolean constantAmbientLight = true;

	/** The constant camera. */
	private boolean constantCamera = true;

	/** The constant camera look. */
	private boolean constantCameraLook = true;

	/** The z near. */
	private double zNear = -1.0;

	/** The z far. */
	private double zFar = -1.0;
	/**
	 * Overlay
	 */

	// private boolean isDisplayingScale = GamaPreferences.Displays.CORE_SCALE.getValue();
	private int fullScreen = -1;

	/**
	 *
	 */

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

	// /**
	// * @return the displayScale
	// */
	// public boolean isDisplayScale() {
	// return isDisplayingScale;
	// }

	// /**
	// * @param displayScale
	// * the displayScale to set
	// */
	// public void setDisplayScale(final boolean displayScale) {
	// this.isDisplayingScale = displayScale;
	// }

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
	// Change lights to a possibly null structure instead of generating an array for each data
	public List<LightPropertiesStructure> getDiffuseLights() {
		final ArrayList<LightPropertiesStructure> result = new ArrayList<>();
		for (final LightPropertiesStructure lightProp : lights) {
			if (lightProp != null) {
				// TODO : check if the light is active
				result.add(lightProp);
			}
		}
		return result;
	}

	/**
	 * Sets the light active.
	 *
	 * @param lightId
	 *            the light id
	 * @param value
	 *            the value
	 */
	public void setLightActive(final int lightId, final boolean value) {
		if (lights[lightId] == null) { lights[lightId] = new LightPropertiesStructure(); }
		lights[lightId].id = lightId;
		lights[lightId].active = value;
	}

	/**
	 * Sets the light type.
	 *
	 * @param lightId
	 *            the light id
	 * @param type
	 *            the type
	 */
	public void setLightType(final int lightId, final String type) {
		if (type.compareTo("direction") == 0) {
			lights[lightId].type = LightPropertiesStructure.TYPE.DIRECTION;
		} else if (type.compareTo("point") == 0) {
			lights[lightId].type = LightPropertiesStructure.TYPE.POINT;
		} else {
			lights[lightId].type = LightPropertiesStructure.TYPE.SPOT;
		}
	}

	/**
	 * Sets the light position.
	 *
	 * @param lightId
	 *            the light id
	 * @param position
	 *            the position
	 */
	public void setLightPosition(final int lightId, final GamaPoint position) {
		lights[lightId].position = position;
	}

	/**
	 * Sets the light direction.
	 *
	 * @param lightId
	 *            the light id
	 * @param direction
	 *            the direction
	 */
	public void setLightDirection(final int lightId, final GamaPoint direction) {
		lights[lightId].direction = direction;
	}

	/**
	 * Sets the diffuse light color.
	 *
	 * @param lightId
	 *            the light id
	 * @param color
	 *            the color
	 */
	public void setDiffuseLightColor(final int lightId, final GamaColor color) {
		lights[lightId].color = color;
	}

	/**
	 * Sets the spot angle.
	 *
	 * @param lightId
	 *            the light id
	 * @param angle
	 *            the angle
	 */
	public void setSpotAngle(final int lightId, final float angle) {
		lights[lightId].spotAngle = angle;
	}

	/**
	 * Sets the linear attenuation.
	 *
	 * @param lightId
	 *            the light id
	 * @param linearAttenuation
	 *            the linear attenuation
	 */
	public void setLinearAttenuation(final int lightId, final float linearAttenuation) {
		lights[lightId].linearAttenuation = linearAttenuation;
	}

	/**
	 * Sets the quadratic attenuation.
	 *
	 * @param lightId
	 *            the light id
	 * @param quadraticAttenuation
	 *            the quadratic attenuation
	 */
	public void setQuadraticAttenuation(final int lightId, final float quadraticAttenuation) {
		lights[lightId].quadraticAttenuation = quadraticAttenuation;
	}

	/**
	 * Sets the draw light.
	 *
	 * @param lightId
	 *            the light id
	 * @param value
	 *            the value
	 */
	public void setDrawLight(final int lightId, final boolean value) {
		lights[lightId].drawLight = value;
	}

	/**
	 * Disable camera interactions.
	 *
	 * @param disableCamInteract
	 *            the disable cam interact
	 */
	public void disableCameraInteractions(final boolean disableCamInteract) {
		this.disableCameraInteraction = disableCamInteract;
	}

	/**
	 * Camera interaction disabled.
	 *
	 * @return true, if successful
	 */
	public boolean cameraInteractionDisabled() {
		return disableCameraInteraction;
	}

	/**
	 * @return the ambientLightColor
	 */
	public Color getAmbientLightColor() { return ambientColor; }

	/**
	 * @param ambientLightColor
	 *            the ambientLightColor to set
	 */
	public void setAmbientLightColor(final GamaColor ambientLightColor) { this.ambientColor = ambientLightColor; }

	/**
	 * Checks if is camera pos defined.
	 *
	 * @return true, if is camera pos defined
	 */
	public boolean isCameraPosDefined() { return cameraPos != null; }

	/**
	 * Checks if is camera look at defined.
	 *
	 * @return true, if is camera look at defined
	 */
	public boolean isCameraLookAtDefined() { return cameraLookPos != null; }

	/**
	 * Checks if is camera up vector defined.
	 *
	 * @return true, if is camera up vector defined
	 */
	public boolean isCameraUpVectorDefined() { return getCameraOrientation() != null; }

	/**
	 * @return the cameraPos
	 */
	public GamaPoint getCameraPos() { return cameraPos; }

	/**
	 * @param cameraPos
	 *            the cameraPos to set
	 */
	public void setCameraPos(final GamaPoint point) {
		if (point == null) return;
		final GamaPoint c = point;
		if (cameraPos != null) {
			if (c.equals(cameraPos)) return;
			cameraPos.setLocation(c);
		} else {
			cameraPos = new GamaPoint(c);
		}

		notifyListeners(Changes.CAMERA_POS, cameraPos);
	}

	/**
	 * @return the cameraLookPos
	 */
	public GamaPoint getCameraTarget() { return cameraLookPos; }

	/**
	 * @param cameraLookPos
	 *            the cameraLookPos to set
	 */
	public void setCameraLookPos(final GamaPoint point) {
		if (point == null) return;
		final GamaPoint c = point;
		if (cameraLookPos != null) {
			if (c.equals(cameraLookPos)) return;
			cameraLookPos.setLocation(c);
		} else {
			cameraLookPos = new GamaPoint(c);
		}

		notifyListeners(Changes.CAMERA_TARGET, cameraLookPos);
	}

	/**
	 * @return the cameraUpVector
	 */
	public GamaPoint getCameraOrientation() { return cameraOrientation; }

	/**
	 * @param cameraOrientation
	 *            the cameraUpVector to set
	 */
	public void setCameraOrientation(final GamaPoint point) {
		if (point == null) return;
		final GamaPoint c = point;
		if (cameraOrientation != null) {
			if (c.equals(cameraOrientation)) return;
			DEBUG.OUT("UpVectors different: x " + point.x + " != " + cameraOrientation.x);
			cameraOrientation.setLocation(c);
		} else {
			cameraOrientation = new GamaPoint(c);
		}

		notifyListeners(Changes.CAMERA_ORIENTATION, cameraOrientation);
	}

	/**
	 * @return the cameraLens
	 */
	public int getCameralens() { return cameraLens; }

	/**
	 * @param cameraLens
	 *            the cameraLens to set
	 */
	public void setCameraLens(final int cameraLens) {
		if (this.cameraLens != cameraLens) { this.cameraLens = cameraLens; }
	}

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
		isOpenGL = OPENGL.equals(displayType) || THREED.equals(displayType) || OPENGL2.equals(displayType);

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
	public void setHighlightColor(final GamaColor hc) {
		highlightColor = hc;
		notifyListeners(Changes.HIGHLIGHT, highlightColor);
	}

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
	public void setAntialias(final boolean a) {
		isAntialiasing = a;
		notifyListeners(Changes.ANTIALIAS, a);
	}

	/**
	 * @return
	 */
	public boolean isContinuousRotationOn() { return isRotating; }

	/**
	 * Sets the continuous rotation.
	 *
	 * @param r
	 *            the new continuous rotation
	 */
	public void setContinuousRotation(final boolean r) {
		isRotating = r;
		if (r && zRotationAngleDelta == 0) { zRotationAngleDelta = 0.2; }
		if (!r) { zRotationAngleDelta = 0; }
	}

	/**
	 * Gets the current rotation about Z.
	 *
	 * @return the current rotation about Z
	 */
	public double getCurrentRotationAboutZ() { return currentRotationAboutZ; }

	/**
	 * Sets the z rotation angle.
	 *
	 * @param val
	 *            the new z rotation angle
	 */
	public void setZRotationAngle(final double val) {
		zRotationAngleDelta = val;
		currentRotationAboutZ = val;
		// notifyListeners(Changes.ROTATION, val);
	}

	/**
	 * Increment Z rotation.
	 */
	public void incrementZRotation() {
		currentRotationAboutZ += zRotationAngleDelta;
	}

	/**
	 * Reset Z rotation.
	 */
	public void resetZRotation() {
		currentRotationAboutZ = zRotationAngleDelta;
	}

	/**
	 * @return
	 */
	public boolean isArcBallCamera() { return isUsingArcBallCamera; }

	/**
	 * Sets the arc ball camera.
	 *
	 * @param c
	 *            the new arc ball camera
	 */
	public void setArcBallCamera(final boolean c) {
		isUsingArcBallCamera = c;
		notifyListeners(Changes.CHANGE_CAMERA, c);
	}

	/**
	 * @return
	 */
	public boolean isLayerSplitted() { return isSplittingLayers; }

	/**
	 * Sets the layer splitted.
	 *
	 * @param s
	 *            the new layer splitted
	 */
	public void setLayerSplitted(final boolean s) {
		isSplittingLayers = s;
		if (s) {
			notifyListeners(Changes.SPLIT_LAYER, splitDistance);
		} else {
			notifyListeners(Changes.SPLIT_LAYER, 0d);
		}
	}

	/**
	 * Gets the split distance.
	 *
	 * @return the split distance
	 */
	public Double getSplitDistance() {
		if (splitDistance == null) { splitDistance = 0.05; }
		return splitDistance;
	}

	/**
	 * Sets the split distance.
	 *
	 * @param s
	 *            the new split distance
	 */
	public void setSplitDistance(final Double s) {
		splitDistance = s;
		if (isSplittingLayers) { notifyListeners(Changes.SPLIT_LAYER, s); }
	}

	/**
	 * Checks if is synchronized.
	 *
	 * @return true, if is synchronized
	 */
	public boolean isSynchronized() { return isSynchronized; }

	/**
	 * Sets the synchronized.
	 *
	 * @param isSynchronized
	 *            the new synchronized
	 */
	public void setSynchronized(final boolean isSynchronized) { this.isSynchronized = isSynchronized; }

	/**
	 * @return the zoomLevel
	 */
	public Double getZoomLevel() { return zoomLevel; }

	/**
	 * @param zoomLevel
	 *            the zoomLevel to set
	 */
	public void setZoomLevel(final Double zoomLevel, final boolean notify, final boolean force) {
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
		this.keystone.setTo(value.toArray(new GamaPoint[4]));
		notifyListeners(Changes.KEYSTONE, this.keystone);
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
		notifyListeners(Changes.KEYSTONE, this.keystone);
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
	 * Sets the preset camera.
	 *
	 * @param newValue
	 *            the new preset camera
	 */
	public void setPresetCamera(final String newValue) {
		presetCamera = newValue;
		notifyListeners(Changes.CAMERA_PRESET, newValue);
	}

	/**
	 * Gets the preset camera.
	 *
	 * @return the preset camera
	 */
	public String getPresetCamera() { return presetCamera; }

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
		final ModelDescription main = (ModelDescription) scope.getModel().getDescription();
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

		final IExpression auto = facets.getExpr(IKeyword.AUTOSAVE);
		if (auto != null) {
			if (auto.getGamlType().equals(Types.POINT)) {
				setAutosave(true);
				setImageDimension(Cast.asPoint(scope, auto.value(scope)));
			} else if (auto.getGamlType().equals(Types.STRING)) {
				setAutosave(true);
				setAutosavePath(Cast.asString(scope, auto.value(scope)));
			} else {
				setAutosave(Cast.asBool(scope, auto.value(scope)));
			}
		}
		final IExpression toolbar = facets.getExpr(IKeyword.TOOLBAR);
		if (toolbar != null) {
			if (toolbar.getGamlType() == Types.BOOL) {
				setToolbarVisible(Cast.asBool(scope, toolbar.value(scope)));
			} else {
				setToolbarVisible(true);
				toolbarColor = Cast.asColor(scope, toolbar.value(scope));
			}
		}
		final IExpression fps = facets.getExpr(IKeyword.SHOWFPS);
		if (fps != null) { setShowfps(Cast.asBool(scope, fps.value(scope))); }

		final IExpression nZ = facets.getExpr("z_near");
		if (nZ != null) { setZNear(Cast.asFloat(scope, nZ.value(scope))); }

		final IExpression fZ = facets.getExpr("z_far");
		if (fZ != null) { setZFar(Cast.asFloat(scope, fZ.value(scope))); }
		final IExpression denv = facets.getExpr(IKeyword.DRAWENV);
		if (denv != null) { setDrawEnv(Cast.asBool(scope, denv.value(scope))); }

		final IExpression ortho = facets.getExpr(IKeyword.ORTHOGRAPHIC_PROJECTION);
		if (ortho != null) { setOrtho(Cast.asBool(scope, ortho.value(scope))); }

		final IExpression fixed_cam = facets.getExpr(IKeyword.CAMERA_INTERACTION);
		if (fixed_cam != null) { disableCameraInteractions(!Cast.asBool(scope, fixed_cam.value(scope))); }

		final IExpression keystone_exp = facets.getExpr(IKeyword.KEYSTONE);
		if (keystone_exp != null) {
			@SuppressWarnings ("unchecked") final List<GamaPoint> val =
					GamaListFactory.create(scope, Types.POINT, Cast.asList(scope, keystone_exp.value(scope)));
			if (val.size() >= 4) { setKeystone(val); }
		}

		final IExpression rotate_exp = facets.getExpr(IKeyword.ROTATE);
		if (rotate_exp != null) {
			final double val = Cast.asFloat(scope, rotate_exp.value(scope));
			setZRotationAngle(val);
		}

		final IExpression lightOn = facets.getExpr(IKeyword.IS_LIGHT_ON);
		if (lightOn != null) { setLightOn(Cast.asBool(scope, lightOn.value(scope))); }

		final IExpression light2 = facets.getExpr(IKeyword.DIFFUSE_LIGHT);
		// this facet is deprecated...
		if (light2 != null) {
			setLightActive(1, true);
			if (light2.getGamlType().equals(Types.COLOR)) {
				setDiffuseLightColor(1, Cast.asColor(scope, light2.value(scope)));
			} else {
				final int meanValue = Cast.asInt(scope, light2.value(scope));
				setDiffuseLightColor(1, new GamaColor(meanValue, meanValue, meanValue, 255));
			}
		}

		final IExpression light3 = facets.getExpr(IKeyword.DIFFUSE_LIGHT_POS);
		// this facet is deprecated...
		if (light3 != null) {
			setLightActive(1, true);
			setLightDirection(1, Cast.asPoint(scope, light3.value(scope)));
		}

		final IExpression drawLights = facets.getExpr(IKeyword.DRAW_DIFFUSE_LIGHT);
		if (drawLights != null && Cast.asBool(scope, drawLights.value(scope)) == true) {
			// set the drawLight attribute to true for all the already
			// existing light
			for (int i = 0; i < 8; i++) {
				boolean lightAlreadyCreated = false;
				for (final LightPropertiesStructure lightProp : getDiffuseLights()) {
					if (lightProp.id == i) {
						lightProp.drawLight = true;
						lightAlreadyCreated = true;
					}
				}
				// if the light does not exist yet, create it by using the
				// method "setLightActive", and set the drawLight attr to
				// true.
				if (!lightAlreadyCreated) {
					if (i < 2) {
						setLightActive(i, true);
					} else {
						setLightActive(i, false);
					}
					setDrawLight(i, true);
				}
				lightAlreadyCreated = false;
			}
		}

		// Set the up vector of the opengl Camera (see gluPerspective)
		final IExpression cameraUp = facets.getExpr(IKeyword.CAMERA_ORIENTATION);
		if (cameraUp != null) {
			final GamaPoint location = Cast.asPoint(scope, cameraUp.value(scope));
			location.setY(-location.getY()); // y component need to be reverted
			setCameraOrientation(location);
		}

		// Set the up vector of the opengl Camera (see gluPerspective)
		final IExpression cameraLens = facets.getExpr(IKeyword.CAMERA_LENS);
		if (cameraLens != null) {
			final int lens = Cast.asInt(scope, cameraLens.value(scope));
			setCameraLens(lens);
		}

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

		final IExpression light = facets.getExpr(IKeyword.AMBIENT_LIGHT);
		if (light != null) {
			if (light.getGamlType().equals(Types.COLOR)) {
				setAmbientLightColor(Cast.asColor(scope, light.value(scope)));
			} else {
				final int meanValue = Cast.asInt(scope, light.value(scope));
				setAmbientLightColor(new GamaColor(meanValue, meanValue, meanValue, 255));
			}
			constantAmbientLight = light.isConst();
		}

		final IExpression antialias = facets.getExpr("antialias");
		if (antialias != null) { setAntialias(Cast.asBool(scope, antialias.value(scope))); }

		final IExpression camera = facets.getExpr(IKeyword.CAMERA_LOCATION);
		if (camera != null) {
			final GamaPoint location = Cast.asPoint(scope, camera.value(scope));
			location.y = -location.y; // y component need to be reverted
			setCameraPos(location);
			constantCamera = camera.isConst();
			// cameraFix = true;
		}

		final IExpression cameraLook = facets.getExpr(IKeyword.CAMERA_TARGET);
		if (cameraLook != null) {
			final GamaPoint location = Cast.asPoint(scope, cameraLook.value(scope));
			location.setY(-location.getY()); // y component need to be reverted
			setCameraLookPos(location);
			constantCameraLook = cameraLook.isConst();
		}

	}

	/**
	 * Sets the z far.
	 *
	 * @param zF
	 *            the new z far
	 */
	private void setZFar(final Double zF) {
		zFar = zF;

	}

	/**
	 * Sets the z near.
	 *
	 * @param zN
	 *            the new z near
	 */
	private void setZNear(final Double zN) { zNear = zN; }

	/**
	 * Update.
	 *
	 * @param scope
	 *            the scope
	 * @param facets
	 *            the facets
	 */
	public void update(final IScope scope, final Facets facets) {
		final IExpression auto = facets.getExpr(IKeyword.AUTOSAVE);
		if (auto != null) {
			if (auto.getGamlType().equals(Types.POINT)) {
				setAutosave(true);
				setImageDimension(Cast.asPoint(scope, auto.value(scope)));
			} else if (auto.getGamlType().equals(Types.STRING)) {
				setAutosave(true);
				setAutosavePath(Cast.asString(scope, auto.value(scope)));
			} else {
				setAutosave(Cast.asBool(scope, auto.value(scope)));
			}
		}
		// /////////////// dynamic Lighting ///////////////////

		if (!constantBackground) {

			final IExpression color = facets.getExpr(IKeyword.BACKGROUND);
			if (color != null) { setBackgroundColor(Cast.asColor(scope, color.value(scope))); }

		}

		if (!constantAmbientLight) {
			final IExpression light = facets.getExpr(IKeyword.AMBIENT_LIGHT);
			if (light != null) {
				if (light.getGamlType().equals(Types.COLOR)) {
					setAmbientLightColor(Cast.asColor(scope, light.value(scope)));
				} else {
					final int meanValue = Cast.asInt(scope, light.value(scope));
					setAmbientLightColor(new GamaColor(meanValue, meanValue, meanValue, 255));
				}
			}
		}

		// /////////////////// dynamic camera ///////////////////
		if (!constantCamera) {
			final IExpression camera = facets.getExpr(IKeyword.CAMERA_LOCATION);
			if (camera != null) {
				final GamaPoint location = Cast.asPoint(scope, camera.value(scope));
				if (location != null) {
					location.y = -location.y; // y component need to be
				}
				// reverted
				setCameraPos(location);
			}
			// graphics.setCameraPosition(getCameraPos());
		}

		if (!constantCameraLook) {
			final IExpression cameraLook = facets.getExpr(IKeyword.CAMERA_TARGET);
			if (cameraLook != null) {
				final GamaPoint location = Cast.asPoint(scope, cameraLook.value(scope));
				if (location != null) {
					location.setY(-location.getY()); // y component need to be
				}
				// reverted
				setCameraLookPos(location);
			}
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
	public boolean isOpenGL() { return isOpenGL; }

}