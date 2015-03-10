/**
 * Created by drogoul, 10 mars 2015
 * 
 */
package msi.gama.outputs;

import java.awt.Color;
import msi.gama.metamodel.shape.*;

/**
 */
public class LayeredDisplayData {

	/**
	 * 
	 */
	private Color backgroundColor;
	/**
	 * 
	 */
	private boolean autosave;
	/**
	 * 
	 */
	private boolean output3D;
	/**
	 * 
	 */
	private boolean tesselation;
	/**
	 * 
	 */
	private int traceDisplay;
	/**
	 * 
	 */
	private boolean z_fighting;
	/**
	 * 
	 */
	private boolean draw_norm;
	/**
	 * 
	 */
	private boolean cubeDisplay;
	/**
	 * 
	 */
	private boolean ortho;
	/**
	 * 
	 */
	private boolean displayScale;
	/**
	 * 
	 */
	private boolean showfps;
	/**
	 * 
	 */
	private boolean drawEnv;
	/**
	 * 
	 */
	private boolean isLightOn;
	/**
	 * 
	 */
	private boolean drawDiffLight;
	/**
	 * 
	 */
	private Color ambientLightColor;
	/**
	 * 
	 */
	private Color diffuseLightColor;
	/**
	 * 
	 */
	private GamaPoint diffuseLightPosition;
	/**
	 * 
	 */
	private ILocation cameraPos;
	/**
	 * 
	 */
	private ILocation cameraLookPos;
	/**
	 * 
	 */
	private ILocation cameraUpVector;
	/**
	 * 
	 */
	private boolean polygonMode;
	/**
	 * 
	 */
	private String displayType;
	/**
	 * 
	 */
	private ILocation imageDimension;
	/**
	 * 
	 */
	private ILocation output3DNbCycles;
	/**
	 * 
	 */
	private double envWidth;
	/**
	 * 
	 */
	private double envHeight;
	/**
	 * 
	 */
	private Color highlightColor;

	/**
	 * 
	 */
	public LayeredDisplayData(final Color backgroundColor, final boolean autosave, final boolean output3d,
		final boolean tesselation, final int traceDisplay, final boolean z_fighting, final boolean draw_norm,
		final boolean cubeDisplay, final boolean ortho, final boolean displayScale, final boolean showfps,
		final boolean drawEnv, final boolean isLightOn, final boolean drawDiffLight, final Color ambientLightColor,
		final Color diffuseLightColor, final GamaPoint diffuseLightPosition, final ILocation cameraPos,
		final ILocation cameraLookPos, final ILocation cameraUpVector, final boolean polygonMode,
		final String displayType, final ILocation imageDimension, final ILocation output3dNbCycles,
		final Color highlightColor) {
		this.backgroundColor = backgroundColor;
		this.autosave = autosave;
		output3D = output3d;
		this.tesselation = tesselation;
		this.traceDisplay = traceDisplay;
		this.z_fighting = z_fighting;
		this.draw_norm = draw_norm;
		this.cubeDisplay = cubeDisplay;
		this.ortho = ortho;
		this.displayScale = displayScale;
		this.showfps = showfps;
		this.drawEnv = drawEnv;
		this.isLightOn = isLightOn;
		this.drawDiffLight = drawDiffLight;
		this.ambientLightColor = ambientLightColor;
		this.diffuseLightColor = diffuseLightColor;
		this.diffuseLightPosition = diffuseLightPosition;
		this.cameraPos = cameraPos;
		this.cameraLookPos = cameraLookPos;
		this.cameraUpVector = cameraUpVector;
		this.polygonMode = polygonMode;
		this.displayType = displayType;
		this.imageDimension = imageDimension;
		output3DNbCycles = output3dNbCycles;
		this.highlightColor = highlightColor;
	}

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