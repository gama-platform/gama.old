/**
 * Created by drogoul, 10 mars 2015
 * 
 */
package msi.gama.outputs;

import java.awt.Color;
import msi.gama.common.GamaPreferences;
import msi.gama.metamodel.shape.*;
import msi.gama.util.GamaColor;

/**
 */
public class LayeredDisplayData {

	public static final String JAVA2D = "java2D";
	public static final String OPENGL = "opengl";
	public static final String WEB = "web";
	public static final String THREED = "3D";

	/**
	 * 
	 */
	private Color backgroundColor = GamaPreferences.CORE_BACKGROUND.getValue();
	/**
	 * 
	 */
	private boolean autosave = false;
	/**
	 * 
	 */
	private boolean output3D = false;
	/**
	 * 
	 */
	private boolean tesselation = true;
	/**
	 * 
	 */
	private int traceDisplay = 0;
	/**
	 * 
	 */
	private boolean z_fighting = GamaPreferences.CORE_Z_FIGHTING.getValue();
	/**
	 * 
	 */
	private boolean draw_norm = GamaPreferences.CORE_DRAW_NORM.getValue();
	/**
	 * 
	 */
	private boolean cubeDisplay = GamaPreferences.CORE_CUBEDISPLAY.getValue();
	/**
	 * 
	 */
	private boolean ortho = false;
	/**
	 * 
	 */
	private boolean displayScale = GamaPreferences.CORE_SCALE.getValue();
	/**
	 * 
	 */
	private boolean showfps = GamaPreferences.CORE_SHOW_FPS.getValue();
	/**
	 * 
	 */
	private boolean drawEnv = GamaPreferences.CORE_DRAW_ENV.getValue();
	/**
	 * 
	 */
	private boolean isLightOn = GamaPreferences.CORE_IS_LIGHT_ON.getValue();
	/**
	 * 
	 */
	private boolean drawDiffLight = false;
	/**
	 * 
	 */
	private Color ambientLightColor = new GamaColor(100, 100, 100, 255);
	/**
	 * 
	 */
	private Color diffuseLightColor = new GamaColor(10, 10, 10, 255);
	/**
	 * 
	 */
	private GamaPoint diffuseLightPosition = new GamaPoint(-1, -1, -1);
	/**
	 * 
	 */
	private ILocation cameraPos = new GamaPoint(-1, -1, -1);
	/**
	 * 
	 */
	private ILocation cameraLookPos = new GamaPoint(-1, -1, -1);
	/**
	 * 
	 */
	private ILocation cameraUpVector = new GamaPoint(0, 1, 0);
	/**
	 * 
	 */
	private boolean polygonMode = true;
	/**
	 * 
	 */
	private String displayType = GamaPreferences.CORE_DISPLAY.getValue().equalsIgnoreCase(JAVA2D) ? JAVA2D : OPENGL;
	/**
	 * 
	 */
	private ILocation imageDimension = new GamaPoint(-1, -1);
	/**
	 * 
	 */
	private ILocation output3DNbCycles = new GamaPoint(0, 0);
	/**
	 * 
	 */
	private double envWidth = 0d;
	/**
	 * 
	 */
	private double envHeight = 0d;
	/**
	 * 
	 */
	private Color highlightColor = GamaPreferences.CORE_HIGHLIGHT.getValue();

	/**
	 * @return the backgroundColor
	 */
	public Color getBackgroundColor() {
		return backgroundColor;
	}

	/**
	 * @param backgroundColor the backgroundColor to set
	 */
	public void setBackgroundColor(final Color backgroundColor) {
		this.backgroundColor = backgroundColor;
	}

	/**
	 * @return the autosave
	 */
	public boolean isAutosave() {
		return autosave;
	}

	/**
	 * @param autosave the autosave to set
	 */
	public void setAutosave(final boolean autosave) {
		this.autosave = autosave;
	}

	/**
	 * @return the output3D
	 */
	public boolean isOutput3D() {
		return output3D;
	}

	/**
	 * @param output3d the output3D to set
	 */
	public void setOutput3D(final boolean output3d) {
		output3D = output3d;
	}

	/**
	 * @return the tesselation
	 */
	public boolean isTesselation() {
		return tesselation;
	}

	/**
	 * @param tesselation the tesselation to set
	 */
	public void setTesselation(final boolean tesselation) {
		this.tesselation = tesselation;
	}

	/**
	 * @return the traceDisplay
	 */
	public int getTraceDisplay() {
		return traceDisplay;
	}

	/**
	 * @param traceDisplay the traceDisplay to set
	 */
	public void setTraceDisplay(final int traceDisplay) {
		this.traceDisplay = traceDisplay;
	}

	/**
	 * @return the z_fighting
	 */
	public boolean isZ_fighting() {
		return z_fighting;
	}

	/**
	 * @param z_fighting the z_fighting to set
	 */
	public void setZ_fighting(final boolean z_fighting) {
		this.z_fighting = z_fighting;
	}

	/**
	 * @return the draw_norm
	 */
	public boolean isDraw_norm() {
		return draw_norm;
	}

	/**
	 * @param draw_norm the draw_norm to set
	 */
	public void setDraw_norm(final boolean draw_norm) {
		this.draw_norm = draw_norm;
	}

	/**
	 * @return the cubeDisplay
	 */
	public boolean isCubeDisplay() {
		return cubeDisplay;
	}

	/**
	 * @param cubeDisplay the cubeDisplay to set
	 */
	public void setCubeDisplay(final boolean cubeDisplay) {
		this.cubeDisplay = cubeDisplay;
	}

	/**
	 * @return the ortho
	 */
	public boolean isOrtho() {
		return ortho;
	}

	/**
	 * @param ortho the ortho to set
	 */
	public void setOrtho(final boolean ortho) {
		this.ortho = ortho;
	}

	/**
	 * @return the displayScale
	 */
	public boolean isDisplayScale() {
		return displayScale;
	}

	/**
	 * @param displayScale the displayScale to set
	 */
	public void setDisplayScale(final boolean displayScale) {
		this.displayScale = displayScale;
	}

	/**
	 * @return the showfps
	 */
	public boolean isShowfps() {
		return showfps;
	}

	/**
	 * @param showfps the showfps to set
	 */
	public void setShowfps(final boolean showfps) {
		this.showfps = showfps;
	}

	/**
	 * @return the drawEnv
	 */
	public boolean isDrawEnv() {
		return drawEnv;
	}

	/**
	 * @param drawEnv the drawEnv to set
	 */
	public void setDrawEnv(final boolean drawEnv) {
		this.drawEnv = drawEnv;
	}

	/**
	 * @return the isLightOn
	 */
	public boolean isLightOn() {
		return isLightOn;
	}

	/**
	 * @param isLightOn the isLightOn to set
	 */
	public void setLightOn(final boolean isLightOn) {
		this.isLightOn = isLightOn;
	}

	/**
	 * @return the drawDiffLight
	 */
	public boolean isDrawDiffLight() {
		return drawDiffLight;
	}

	/**
	 * @param drawDiffLight the drawDiffLight to set
	 */
	public void setDrawDiffLight(final boolean drawDiffLight) {
		this.drawDiffLight = drawDiffLight;
	}

	/**
	 * @return the ambientLightColor
	 */
	public Color getAmbientLightColor() {
		return ambientLightColor;
	}

	/**
	 * @param ambientLightColor the ambientLightColor to set
	 */
	public void setAmbientLightColor(final Color ambientLightColor) {
		this.ambientLightColor = ambientLightColor;
	}

	/**
	 * @return the diffuseLightColor
	 */
	public Color getDiffuseLightColor() {
		return diffuseLightColor;
	}

	/**
	 * @param diffuseLightColor the diffuseLightColor to set
	 */
	public void setDiffuseLightColor(final Color diffuseLightColor) {
		this.diffuseLightColor = diffuseLightColor;
	}

	/**
	 * @return the diffuseLightPosition
	 */
	public GamaPoint getDiffuseLightPosition() {
		return diffuseLightPosition;
	}

	/**
	 * @param diffuseLightPosition the diffuseLightPosition to set
	 */
	public void setDiffuseLightPosition(final GamaPoint diffuseLightPosition) {
		this.diffuseLightPosition = diffuseLightPosition;
	}

	/**
	 * @return the cameraPos
	 */
	public ILocation getCameraPos() {
		return cameraPos;
	}

	/**
	 * @param cameraPos the cameraPos to set
	 */
	public void setCameraPos(final ILocation cameraPos) {
		this.cameraPos = cameraPos;
	}

	/**
	 * @return the cameraLookPos
	 */
	public ILocation getCameraLookPos() {
		return cameraLookPos;
	}

	/**
	 * @param cameraLookPos the cameraLookPos to set
	 */
	public void setCameraLookPos(final ILocation cameraLookPos) {
		this.cameraLookPos = cameraLookPos;
	}

	/**
	 * @return the cameraUpVector
	 */
	public ILocation getCameraUpVector() {
		return cameraUpVector;
	}

	/**
	 * @param cameraUpVector the cameraUpVector to set
	 */
	public void setCameraUpVector(final ILocation cameraUpVector) {
		this.cameraUpVector = cameraUpVector;
	}

	/**
	 * @return the polygonMode
	 */
	public boolean isPolygonMode() {
		return polygonMode;
	}

	/**
	 * @param polygonMode the polygonMode to set
	 */
	public void setPolygonMode(final boolean polygonMode) {
		this.polygonMode = polygonMode;
	}

	/**
	 * @return the displayType
	 */
	public String getDisplayType() {
		return displayType;
	}

	/**
	 * @param displayType the displayType to set
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
	 * @param imageDimension the imageDimension to set
	 */
	public void setImageDimension(final ILocation imageDimension) {
		this.imageDimension = imageDimension;
	}

	/**
	 * @return the output3DNbCycles
	 */
	public ILocation getOutput3DNbCycles() {
		return output3DNbCycles;
	}

	/**
	 * @param output3dNbCycles the output3DNbCycles to set
	 */
	public void setOutput3DNbCycles(final ILocation output3dNbCycles) {
		output3DNbCycles = output3dNbCycles;
	}

	/**
	 * @return the envWidth
	 */
	public double getEnvWidth() {
		return envWidth;
	}

	/**
	 * @param envWidth the envWidth to set
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
	 * @param envHeight the envHeight to set
	 */
	public void setEnvHeight(final double envHeight) {
		this.envHeight = envHeight;
	}

	/**
	 * @return
	 */
	public Color getHighlightColor() {
		return highlightColor;
	}

	public void setHighlightColor(final Color hc) {
		highlightColor = hc;
	}
}