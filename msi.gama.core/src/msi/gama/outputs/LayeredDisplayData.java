/*******************************************************************************************************
 *
 * msi.gama.outputs.LayeredDisplayData.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling
 * and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
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
import msi.gama.metamodel.shape.ILocation;
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

	public enum Changes {
		SPLIT_LAYER,
		CHANGE_CAMERA,
		CAMERA_POS,
		CAMERA_TARGET,
		CAMERA_ORIENTATION,
		CAMERA_PRESET,
		BACKGROUND,
		HIGHLIGHT,
		ZOOM,
		KEYSTONE,
		ANTIALIAS,
		ROTATION;
	}

	public static final String JAVA2D = "java2D";
	public static final String OPENGL = "opengl";
	public static final String OPENGL2 = "opengl2";
	public static final String WEB = "web";
	public static final String THREED = "3D";
	public static final Double INITIAL_ZOOM = 1.0;

	public interface DisplayDataListener {

		void changed(Changes property, Object value);
	}

	public final Set<DisplayDataListener> listeners = new CopyOnWriteArraySet<>();

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
	private GamaColor toolbarColor = GamaColor.NamedGamaColor.getNamed("white");

	/**
	 * Properties
	 */
	private boolean isAutosaving = false;
	private String autosavingPath = "";
	private boolean isToolbarVisible = GamaPreferences.Displays.CORE_DISPLAY_TOOLBAR.getValue();
	private boolean isSynchronized = GamaPreferences.Runtime.CORE_SYNC.getValue();
	private String displayType =
			GamaPreferences.Displays.CORE_DISPLAY.getValue().equalsIgnoreCase(JAVA2D) ? JAVA2D : OPENGL;
	private double envWidth = 0d;
	private double envHeight = 0d;
	private boolean isAntialiasing = GamaPreferences.Displays.CORE_ANTIALIAS.getValue();
	private ILocation imageDimension = new GamaPoint(-1, -1);
	private Double zoomLevel = INITIAL_ZOOM;
	private final LightPropertiesStructure lights[] = new LightPropertiesStructure[8];
	public static final ICoordinates KEYSTONE_IDENTITY =
			ICoordinates.ofLength(4).setTo(0d, 0, 0, 0, 1, 0, 1, 1, 0, 1, 0, 0);

	private final ICoordinates keystone = (ICoordinates) KEYSTONE_IDENTITY.clone();
	private double zRotationAngleDelta = 0;
	private double currentRotationAboutZ = 0;
	private boolean isOpenGL;

	/**
	 * OpenGL
	 */

	private boolean isWireframe = false;
	private boolean ortho = false;
	private boolean disableCameraInteraction = false; // "fixed_camera" facet
	private boolean isShowingFPS = false; // GamaPreferences.CORE_SHOW_FPS.getValue();
	private boolean isDrawingEnvironment = GamaPreferences.Displays.CORE_DRAW_ENV.getValue();
	private boolean isLightOn = true; // GamaPreferences.CORE_IS_LIGHT_ON.getValue();
	private GamaPoint cameraPos = null;
	private GamaPoint cameraLookPos = null;
	private GamaPoint cameraOrientation = null;
	private String presetCamera = "";
	private int cameraLens = 45;
	private Double splitDistance;
	private boolean isRotating;
	private boolean isUsingArcBallCamera = true;
	private boolean isSplittingLayers;
	private boolean constantBackground = true;
	private boolean constantAmbientLight = true;
	private boolean constantCamera = true;
	private boolean constantCameraLook = true;
	private double zNear = -1.0;
	private double zFar = -1.0;
	/**
	 * Overlay
	 */

	// private boolean isDisplayingScale = GamaPreferences.Displays.CORE_SCALE.getValue();
	private int fullScreen = -1;

	/**
	 *
	 */

	IPreferenceAfterChangeListener<GamaColor> highlightListener = newValue -> setHighlightColor(newValue);

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

	public void setAutosavePath(final String p) {
		this.autosavingPath = p;
	}

	public String getAutosavePath() {
		return autosavingPath;
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

	public double getzNear() {
		return zNear;
	}

	public double getzFar() {
		return zFar;
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
		final ArrayList<LightPropertiesStructure> result = new ArrayList<>();
		for (final LightPropertiesStructure lightProp : lights) {
			if (lightProp != null) {
				// TODO : check if the light is active
				result.add(lightProp);
			}
		}
		return result;
	}

	public void setLightActive(final int lightId, final boolean value) {
		if (lights[lightId] == null) { lights[lightId] = new LightPropertiesStructure(); }
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

	public boolean isCameraPosDefined() {
		return cameraPos != null;
	}

	public boolean isCameraLookAtDefined() {
		return cameraLookPos != null;
	}

	public boolean isCameraUpVectorDefined() {
		return getCameraOrientation() != null;
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
	public void setCameraPos(final GamaPoint point) {
		if (point == null) return;
		final GamaPoint c = point;
		if (cameraPos != null) {
			if (c.equals(cameraPos))
				return;
			else {
				cameraPos.setLocation(c);
			}
		} else {
			cameraPos = new GamaPoint(c);
		}

		notifyListeners(Changes.CAMERA_POS, cameraPos);
	}

	/**
	 * @return the cameraLookPos
	 */
	public GamaPoint getCameraTarget() {
		return cameraLookPos;
	}

	/**
	 * @param cameraLookPos
	 *            the cameraLookPos to set
	 */
	public void setCameraLookPos(final GamaPoint point) {
		if (point == null) return;
		final GamaPoint c = point;
		if (cameraLookPos != null) {
			if (c.equals(cameraLookPos))
				return;
			else {
				cameraLookPos.setLocation(c);
			}
		} else {
			cameraLookPos = new GamaPoint(c);
		}

		notifyListeners(Changes.CAMERA_TARGET, cameraLookPos);
	}

	/**
	 * @return the cameraUpVector
	 */
	public GamaPoint getCameraOrientation() {
		return cameraOrientation;
	}

	/**
	 * @param cameraOrientation
	 *            the cameraUpVector to set
	 */
	public void setCameraOrientation(final GamaPoint point) {
		if (point == null) return;
		final GamaPoint c = point;
		if (cameraOrientation != null) {
			if (c.equals(cameraOrientation))
				return;
			else {
				DEBUG.OUT("UpVectors different: x " + point.x + " != " + cameraOrientation.x);
				cameraOrientation.setLocation(c);
			}
		} else {
			cameraOrientation = new GamaPoint(c);
		}

		notifyListeners(Changes.CAMERA_ORIENTATION, cameraOrientation);
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
		if (this.cameraLens != cameraLens) { this.cameraLens = cameraLens; }
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
		isOpenGL = displayType.equals(OPENGL) || displayType.equals(THREED) || displayType.equals(OPENGL2);

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

	public double getMaxEnvDim() {
		return envWidth > envHeight ? envWidth : envHeight;
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
		notifyListeners(Changes.ANTIALIAS, a);
	}

	/**
	 * @return
	 */
	public boolean isContinuousRotationOn() {
		return isRotating;
	}

	public void setContinuousRotation(final boolean r) {
		isRotating = r;
		if (r && zRotationAngleDelta == 0) { zRotationAngleDelta = 0.2; }
		if (!r) { zRotationAngleDelta = 0; }
	}

	public double getCurrentRotationAboutZ() {
		return currentRotationAboutZ;
	}

	public void setZRotationAngle(final double val) {
		zRotationAngleDelta = val;
		currentRotationAboutZ = val;
		// notifyListeners(Changes.ROTATION, val);
	}

	public void incrementZRotation() {
		currentRotationAboutZ += zRotationAngleDelta;
	}

	public void resetZRotation() {
		currentRotationAboutZ = zRotationAngleDelta;
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
		if (s) {
			notifyListeners(Changes.SPLIT_LAYER, splitDistance);
		} else {
			notifyListeners(Changes.SPLIT_LAYER, 0d);
		}
	}

	public Double getSplitDistance() {
		if (splitDistance == null) { splitDistance = 0.05; }
		return splitDistance;
	}

	public void setSplitDistance(final Double s) {
		splitDistance = s;
		if (isSplittingLayers) { notifyListeners(Changes.SPLIT_LAYER, s); }
	}

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
	public void setZoomLevel(final Double zoomLevel, final boolean notify, final boolean force) {
		if (this.zoomLevel != null && this.zoomLevel.equals(zoomLevel)) return;
		this.zoomLevel = zoomLevel;
		if (notify) { notifyListeners(Changes.ZOOM, this.zoomLevel); }
	}

	public int fullScreen() {
		return fullScreen;
	}

	public void setFullScreen(final int fs) {
		fullScreen = fs;
	}

	public void setKeystone(final List<GamaPoint> value) {
		if (value == null) return;
		this.keystone.setTo(value.toArray(new GamaPoint[4]));
		notifyListeners(Changes.KEYSTONE, this.keystone);
	}

	public void setKeystone(final ICoordinates value) {
		if (value == null) return;
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

	public boolean isToolbarVisible() {
		return this.isToolbarVisible;
	}

	public GamaColor getToolbarColor() {
		return toolbarColor;
	}

	public void setToolbarVisible(final boolean b) {
		isToolbarVisible = b;
	}

	public void initWith(final IScope scope, final IDescription desc) {
		final Facets facets = desc.getFacets();
		// Initializing the size of the environment
		SimulationAgent sim = scope.getSimulation();
		// hqnghi if layer come from micro-model
		final ModelDescription micro = desc.getModelDescription();
		final ModelDescription main = (ModelDescription) scope.getModel().getDescription();
		final Boolean fromMicroModel = main.getMicroModel(micro.getAlias()) != null;
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
			setLightDirection(1, (GamaPoint) Cast.asPoint(scope, light3.value(scope)));
		}

		final IExpression drawLights = facets.getExpr(IKeyword.DRAW_DIFFUSE_LIGHT);
		if (drawLights != null) {
			if (Cast.asBool(scope, drawLights.value(scope)) == true) {
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
		}

		// Set the up vector of the opengl Camera (see gluPerspective)
		final IExpression cameraUp = facets.getExpr(IKeyword.CAMERA_ORIENTATION);
		if (cameraUp != null) {
			final GamaPoint location = (GamaPoint) Cast.asPoint(scope, cameraUp.value(scope));
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
			final GamaPoint location = (GamaPoint) Cast.asPoint(scope, camera.value(scope));
			location.y = -location.y; // y component need to be reverted
			setCameraPos(location);
			constantCamera = camera.isConst();
			// cameraFix = true;
		}

		final IExpression cameraLook = facets.getExpr(IKeyword.CAMERA_TARGET);
		if (cameraLook != null) {
			final GamaPoint location = (GamaPoint) Cast.asPoint(scope, cameraLook.value(scope));
			location.setY(-location.getY()); // y component need to be reverted
			setCameraLookPos(location);
			constantCameraLook = cameraLook.isConst();
		}

	}

	private void setZFar(final Double zF) {
		zFar = zF;

	}

	private void setZNear(final Double zN) {
		zNear = zN;
	}

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
				final GamaPoint location = (GamaPoint) Cast.asPoint(scope, camera.value(scope));
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
				final GamaPoint location = (GamaPoint) Cast.asPoint(scope, cameraLook.value(scope));
				if (location != null) {
					location.setY(-location.getY()); // y component need to be
				}
				// reverted
				setCameraLookPos(location);
			}
		}

	}

	public boolean isOpenGL2() {
		return displayType.equals(OPENGL2);
	}

	public boolean isWeb() {
		return displayType.equals(WEB);
	}

	public boolean isOpenGL() {
		return isOpenGL;
	}

}