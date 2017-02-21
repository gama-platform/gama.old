/*********************************************************************************************
 *
 * 'LayeredDisplayData.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling and simulation
 * platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package msi.gama.outputs;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import msi.gama.common.geometry.ICoordinates;
import msi.gama.common.preferences.GamaPreferences;
import msi.gama.common.preferences.IPreferenceChangeListener;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.metamodel.shape.ILocation;
import msi.gama.util.GamaColor;

/**
 */
public class LayeredDisplayData {

	public enum Changes {
		SPLIT_LAYER,
		CHANGE_CAMERA,
		CAMERA_POS,
		CAMERA_TARGET,
		CAMERA_UP,
		CAMERA_PRESET,
		BACKGROUND,
		HIGHLIGHT,
		ZOOM,
		KEYSTONE;
	}

	public static final String JAVA2D = "java2D";
	public static final String OPENGL = "opengl";
	public static final String WEB = "web";
	public static final String THREED = "3D";

	public static interface DisplayDataListener {

		void changed(Changes property, Object value);
	}

	final Set<DisplayDataListener> listeners = new HashSet<>();

	public void addListener(final DisplayDataListener listener) {
		listeners.add(listener);
	}

	public void removeListener(final DisplayDataListener listener) {
		listeners.remove(listener);
	}

	public void notifyListeners(final Changes property, final Object value) {
		for (final DisplayDataListener listener : listeners) {
			listener.changed(property, value);
		}
	}

	/**
	 * Colors
	 */
	private GamaColor backgroundColor = GamaPreferences.Displays.CORE_BACKGROUND.getValue();
	private GamaColor ambientColor = new GamaColor(64, 64, 64, 255);
	private GamaColor highlightColor = GamaPreferences.Displays.CORE_HIGHLIGHT.getValue();

	/**
	 * Properties
	 */
	private boolean isAutosaving = false;
	private boolean isSynchronized = GamaPreferences.Runtime.CORE_SYNC.getValue();
	private String displayType =
			GamaPreferences.Displays.CORE_DISPLAY.getValue().equalsIgnoreCase(JAVA2D) ? JAVA2D : OPENGL;
	private double envWidth = 0d;
	private double envHeight = 0d;
	private boolean isAntialiasing = GamaPreferences.Displays.CORE_ANTIALIAS.getValue();
	private ILocation imageDimension = new GamaPoint(-1, -1);
	private Double zoomLevel = null;
	private final LightPropertiesStructure lights[] = new LightPropertiesStructure[8];
	public static final ICoordinates KEYSTONE_IDENTITY =
			ICoordinates.ofLength(4).setTo(0, 0, 0, 0, 1, 0, 1, 1, 0, 1, 0, 0);

	private final ICoordinates keystone = (ICoordinates) KEYSTONE_IDENTITY.clone();
	private double zRotationAngle = 0.2;
	private double currentRotationAboutZ = 0;

	/**
	 * OpenGL
	 */

	private boolean isWireframe = false;
	private boolean ortho = false;
	private boolean disableCameraInteraction = false; // "fixed_camera" facet
	private boolean isShowingFPS = false; // GamaPreferences.CORE_SHOW_FPS.getValue();
	private boolean isDrawingEnvironment = GamaPreferences.OpenGL.CORE_DRAW_ENV.getValue();
	private boolean isLightOn = true; // GamaPreferences.CORE_IS_LIGHT_ON.getValue();
	private GamaPoint cameraPos = null;
	private GamaPoint cameraLookPos = null;
	private GamaPoint cameraUpVector = null;
	private String presetCamera = "";
	private int cameraLens = 45;
	private final boolean isDrawingPolygons = true;
	private boolean isRotating;
	private boolean isUsingArcBallCamera = true;
	private boolean isSplittingLayers;
	// private volatile boolean isCameraLock = true;

	/**
	 * Overlay
	 */

	private boolean isDisplayingScale = GamaPreferences.Displays.CORE_SCALE.getValue();
	private int fullScreen = -1;

	/**
	 *
	 */

	IPreferenceChangeListener<GamaColor> highlightListener = new IPreferenceChangeListener<GamaColor>() {

		@Override
		public boolean beforeValueChange(final GamaColor newValue) {
			return true;
		}

		@Override
		public void afterValueChange(final GamaColor newValue) {
			setHighlightColor(newValue);

		}
	};

	public LayeredDisplayData() {
		GamaPreferences.Displays.CORE_HIGHLIGHT.addChangeListener(highlightListener);
	}

	public void dispose() {
		GamaPreferences.Displays.CORE_HIGHLIGHT.removeChangeListener(highlightListener);
	}

	/**
	 * @return the backgroundColor
	 */
	public Color getBackgroundColor() {
		return backgroundColor;
	}

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
	public boolean isAutosave() {
		return isAutosaving;
	}

	/**
	 * @param autosave
	 *            the autosave to set
	 */
	public void setAutosave(final boolean autosave) {
		this.isAutosaving = autosave;
	}

	public boolean isWireframe() {
		return isWireframe;
	}

	public void setWireframe(final boolean t) {
		isWireframe = t;
	}

	/**
	 * @return the ortho
	 */
	public boolean isOrtho() {
		return ortho;
	}

	/**
	 * @param ortho
	 *            the ortho to set
	 */
	public void setOrtho(final boolean ortho) {
		this.ortho = ortho;
	}

	/**
	 * @return the displayScale
	 */
	public boolean isDisplayScale() {
		return isDisplayingScale;
	}

	/**
	 * @param displayScale
	 *            the displayScale to set
	 */
	public void setDisplayScale(final boolean displayScale) {
		this.isDisplayingScale = displayScale;
	}

	/**
	 * @return the showfps
	 */
	public boolean isShowfps() {
		return isShowingFPS;
	}

	/**
	 * @param showfps
	 *            the showfps to set
	 */
	public void setShowfps(final boolean showfps) {
		this.isShowingFPS = showfps;
	}

	/**
	 * @return the drawEnv
	 */
	public boolean isDrawEnv() {
		return isDrawingEnvironment;
	}

	/**
	 * @param drawEnv
	 *            the drawEnv to set
	 */
	public void setDrawEnv(final boolean drawEnv) {
		this.isDrawingEnvironment = drawEnv;
	}

	/**
	 * @return the isLightOn
	 */
	public boolean isLightOn() {
		return isLightOn;
	}

	/**
	 * @param isLightOn
	 *            the isLightOn to set
	 */
	public void setLightOn(final boolean isLightOn) {
		this.isLightOn = isLightOn;
	}

	// Change lights to a possibly null structure instead of generating an array for each data
	public List<LightPropertiesStructure> getDiffuseLights() {
		final ArrayList<LightPropertiesStructure> result = new ArrayList<LightPropertiesStructure>();
		for (final LightPropertiesStructure lightProp : lights) {
			if (lightProp != null) {
				// TODO : check if the light is active
				result.add(lightProp);
			}
		}
		return result;
	}

	public void setLightActive(final int lightId, final boolean value) {
		if (lights[lightId] == null) {
			lights[lightId] = new LightPropertiesStructure();
		}
		lights[lightId].id = lightId;
		lights[lightId].active = value;
	}

	public void setLightType(final int lightId, final String type) {
		if (type.compareTo("direction") == 0) {
			lights[lightId].type = LightPropertiesStructure.TYPE.DIRECTION;
		} else if (type.compareTo("point") == 0) {
			lights[lightId].type = LightPropertiesStructure.TYPE.POINT;
		} else {
			lights[lightId].type = LightPropertiesStructure.TYPE.SPOT;
		}
	}

	public void setLightPosition(final int lightId, final GamaPoint position) {
		lights[lightId].position = position;
	}

	public void setLightDirection(final int lightId, final GamaPoint direction) {
		lights[lightId].direction = direction;
	}

	public void setDiffuseLightColor(final int lightId, final GamaColor color) {
		lights[lightId].color = color;
	}

	public void setSpotAngle(final int lightId, final float angle) {
		lights[lightId].spotAngle = angle;
	}

	public void setLinearAttenuation(final int lightId, final float linearAttenuation) {
		lights[lightId].linearAttenuation = linearAttenuation;
	}

	public void setQuadraticAttenuation(final int lightId, final float quadraticAttenuation) {
		lights[lightId].quadraticAttenuation = quadraticAttenuation;
	}

	public void setDrawLight(final int lightId, final boolean value) {
		lights[lightId].drawLight = value;
	}

	public void disableCameraInteractions(final boolean disableCamInteract) {
		this.disableCameraInteraction = disableCamInteract;
	}

	public boolean cameraInteractionDisabled() {
		return disableCameraInteraction;
	}

	/**
	 * @return the ambientLightColor
	 */
	public Color getAmbientLightColor() {
		return ambientColor;
	}

	/**
	 * @param ambientLightColor
	 *            the ambientLightColor to set
	 */
	public void setAmbientLightColor(final GamaColor ambientLightColor) {
		this.ambientColor = ambientLightColor;
	}

	public boolean isCameraDefined() {
		return cameraPos != null;
	}

	/**
	 * @return the cameraPos
	 */
	public GamaPoint getCameraPos() {
		return cameraPos;
	}

	/**
	 * @param cameraPos
	 *            the cameraPos to set
	 */
	public void setCameraPos(final GamaPoint c) {
		if (c == null)
			return;
		setCameraPos(c.x, c.y, c.z);
	}

	public void setCameraPos(final double xPos, final double yPos, final double zPos) {
		if (cameraPos != null)
			if (xPos == cameraPos.x && yPos == cameraPos.y && zPos == cameraPos.z)
				return;
			else
				cameraPos.setLocation(xPos, yPos, zPos);
		else
			cameraPos = new GamaPoint(xPos, yPos, zPos);

		notifyListeners(Changes.CAMERA_POS, cameraPos);
	}

	/**
	 * @return the cameraLookPos
	 */
	public GamaPoint getCameraLookPos() {
		return cameraLookPos;
	}

	/**
	 * @param cameraLookPos
	 *            the cameraLookPos to set
	 */
	public void setCameraLookPos(final GamaPoint c) {
		if (c == null)
			return;
		setCameraLookPos(c.x, c.y, c.z);
	}

	public void setCameraLookPos(final double xPos, final double yPos, final double zPos) {
		if (cameraLookPos != null)
			if (xPos == cameraLookPos.x && yPos == cameraLookPos.y && zPos == cameraLookPos.z)
				return;
			else
				cameraLookPos.setLocation(xPos, yPos, zPos);
		else
			cameraLookPos = new GamaPoint(xPos, yPos, zPos);

		notifyListeners(Changes.CAMERA_TARGET, cameraLookPos);
	}

	/**
	 * @return the cameraUpVector
	 */
	public GamaPoint getCameraUpVector() {
		return cameraUpVector;
	}

	/**
	 * @param cameraUpVector
	 *            the cameraUpVector to set
	 */
	public void setCameraUpVector(final GamaPoint c, final boolean notify) {
		if (c == null)
			return;
		setCameraUpVector(c.x, c.y, c.z, notify);
	}

	public void setCameraUpVector(final double xPos, final double yPos, final double zPos, final boolean notify) {
		if (cameraUpVector != null)
			if (xPos == cameraUpVector.x && yPos == cameraUpVector.y && zPos == cameraUpVector.z)
				return;
			else
				cameraUpVector.setLocation(xPos, yPos, zPos);
		else
			cameraUpVector = new GamaPoint(xPos, yPos, zPos);

		notifyListeners(Changes.CAMERA_UP, cameraUpVector);
	}

	/**
	 * @return the cameraLens
	 */
	public int getCameralens() {
		return cameraLens;
	}

	/**
	 * @param cameraLens
	 *            the cameraLens to set
	 */
	public void setCameraLens(final int cameraLens) {
		if (this.cameraLens != cameraLens) {
			this.cameraLens = cameraLens;
		}
	}

	/**
	 * @return the displayType
	 */
	public String getDisplayType() {
		return displayType;
	}

	/**
	 * @param displayType
	 *            the displayType to set
	 */
	public void setDisplayType(final String displayType) {
		this.displayType = displayType;
	}

	/**
	 * @return the imageDimension
	 */
	public ILocation getImageDimension() {
		return imageDimension;
	}

	/**
	 * @param imageDimension
	 *            the imageDimension to set
	 */
	public void setImageDimension(final ILocation imageDimension) {
		this.imageDimension = imageDimension;
	}

	/**
	 * @return the envWidth
	 */
	public double getEnvWidth() {
		return envWidth;
	}

	/**
	 * @param envWidth
	 *            the envWidth to set
	 */
	public void setEnvWidth(final double envWidth) {
		this.envWidth = envWidth;
	}

	/**
	 * @return the envHeight
	 */
	public double getEnvHeight() {
		return envHeight;
	}

	/**
	 * @param envHeight
	 *            the envHeight to set
	 */
	public void setEnvHeight(final double envHeight) {
		this.envHeight = envHeight;
	}

	/**
	 * @return
	 */
	public GamaColor getHighlightColor() {
		return highlightColor;
	}

	public void setHighlightColor(final GamaColor hc) {
		highlightColor = hc;
		notifyListeners(Changes.HIGHLIGHT, highlightColor);
	}

	public boolean isAntialias() {
		return isAntialiasing;
	}

	public void setAntialias(final boolean a) {
		isAntialiasing = a;
	}

	/**
	 * @return
	 */
	public boolean isRotationOn() {
		return isRotating;
	}

	public void setRotation(final boolean r) {
		isRotating = r;
		if (r && zRotationAngle == 0) {
			zRotationAngle = 0.2;
		}
	}

	public double getCurrentRotationAboutZ() {
		return currentRotationAboutZ;
	}

	public void setZRotationAngle(final double val) {
		zRotationAngle = val;
	}

	public void incrementZRotation() {
		currentRotationAboutZ += zRotationAngle;
	}

	public void resetZRotation() {
		currentRotationAboutZ = 0;
	}

	/**
	 * @return
	 */
	public boolean isArcBallCamera() {
		return isUsingArcBallCamera;
	}

	public void setArcBallCamera(final boolean c) {
		isUsingArcBallCamera = c;
		notifyListeners(Changes.CHANGE_CAMERA, c);
	}

	/**
	 * @return
	 */
	public boolean isLayerSplitted() {
		return isSplittingLayers;
	}

	public void setLayerSplitted(final boolean s) {
		isSplittingLayers = s;
		notifyListeners(Changes.SPLIT_LAYER, s);
	}

	/**
	 * @return
	 */
	// public boolean isCameraLock() {
	// return isCameraLock;
	// }
	//
	// public void setCameraLock(final boolean s) {
	// isCameraLock = s;
	// }

	// public static ILocation getNoChange() {
	// return noChange;
	// }

	public boolean isSynchronized() {
		return isSynchronized;
	}

	public void setSynchronized(final boolean isSynchronized) {
		this.isSynchronized = isSynchronized;
	}

	/**
	 * @return the zoomLevel
	 */
	public Double getZoomLevel() {
		return zoomLevel;
	}

	/**
	 * @param zoomLevel
	 *            the zoomLevel to set
	 */
	public void setZoomLevel(final Double zoomLevel, final boolean notify) {
		if (this.zoomLevel != null && this.zoomLevel.equals(zoomLevel)) { return; }
		this.zoomLevel = zoomLevel;
		if (notify)
			notifyListeners(Changes.ZOOM, this.zoomLevel);
	}

	public int fullScreen() {
		return fullScreen;
	}

	public void setFullScreen(final int fs) {
		fullScreen = fs;
	}

	public void setKeystone(final List<GamaPoint> value) {
		if (value == null)
			return;
		this.keystone.setTo(value.toArray(new GamaPoint[4]));
		notifyListeners(Changes.KEYSTONE, this.keystone);
	}

	public void setKeystone(final ICoordinates value) {
		if (value == null)
			return;
		this.keystone.setTo(value.toCoordinateArray());
		notifyListeners(Changes.KEYSTONE, this.keystone);
	}

	public ICoordinates getKeystone() {
		return this.keystone;
	}

	public boolean isKeystoneDefined() {
		return !keystone.equals(KEYSTONE_IDENTITY);
	}

	public void setPresetCamera(final String newValue) {
		presetCamera = newValue;
		notifyListeners(Changes.CAMERA_PRESET, newValue);
	}

	public String getPresetCamera() {
		return presetCamera;
	}

}