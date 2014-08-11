/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package gama.impl;

import gama.EDisplay;
import gama.EDisplayLink;
import gama.ELayer;
import gama.GamaPackage;

import java.util.Collection;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

import org.eclipse.emf.ecore.util.EDataTypeEList;
import org.eclipse.emf.ecore.util.EDataTypeUniqueEList;
import org.eclipse.emf.ecore.util.EObjectResolvingEList;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>EDisplay</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link gama.impl.EDisplayImpl#getLayers <em>Layers</em>}</li>
 *   <li>{@link gama.impl.EDisplayImpl#getDisplayLink <em>Display Link</em>}</li>
 *   <li>{@link gama.impl.EDisplayImpl#getOpengl <em>Opengl</em>}</li>
 *   <li>{@link gama.impl.EDisplayImpl#getRefresh <em>Refresh</em>}</li>
 *   <li>{@link gama.impl.EDisplayImpl#getBackground <em>Background</em>}</li>
 *   <li>{@link gama.impl.EDisplayImpl#getLayerList <em>Layer List</em>}</li>
 *   <li>{@link gama.impl.EDisplayImpl#getColor <em>Color</em>}</li>
 *   <li>{@link gama.impl.EDisplayImpl#getIsColorCst <em>Is Color Cst</em>}</li>
 *   <li>{@link gama.impl.EDisplayImpl#getColorRBG <em>Color RBG</em>}</li>
 *   <li>{@link gama.impl.EDisplayImpl#getGamlCode <em>Gaml Code</em>}</li>
 *   <li>{@link gama.impl.EDisplayImpl#getAmbientLight <em>Ambient Light</em>}</li>
 *   <li>{@link gama.impl.EDisplayImpl#getDrawDiffuseLight <em>Draw Diffuse Light</em>}</li>
 *   <li>{@link gama.impl.EDisplayImpl#getDiffuseLight <em>Diffuse Light</em>}</li>
 *   <li>{@link gama.impl.EDisplayImpl#getDiffuseLightPos <em>Diffuse Light Pos</em>}</li>
 *   <li>{@link gama.impl.EDisplayImpl#getZFighting <em>ZFighting</em>}</li>
 *   <li>{@link gama.impl.EDisplayImpl#getCameraPos <em>Camera Pos</em>}</li>
 *   <li>{@link gama.impl.EDisplayImpl#getCameraLookPos <em>Camera Look Pos</em>}</li>
 *   <li>{@link gama.impl.EDisplayImpl#getCameraUpVector <em>Camera Up Vector</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class EDisplayImpl extends EGamaObjectImpl implements EDisplay {
	/**
	 * The cached value of the '{@link #getLayers() <em>Layers</em>}' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getLayers()
	 * @generated
	 * @ordered
	 */
	protected EList<ELayer> layers;

	/**
	 * The cached value of the '{@link #getDisplayLink() <em>Display Link</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDisplayLink()
	 * @generated
	 * @ordered
	 */
	protected EDisplayLink displayLink;

	/**
	 * The default value of the '{@link #getOpengl() <em>Opengl</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getOpengl()
	 * @generated
	 * @ordered
	 */
	protected static final Boolean OPENGL_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getOpengl() <em>Opengl</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getOpengl()
	 * @generated
	 * @ordered
	 */
	protected Boolean opengl = OPENGL_EDEFAULT;

	/**
	 * The default value of the '{@link #getRefresh() <em>Refresh</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getRefresh()
	 * @generated
	 * @ordered
	 */
	protected static final String REFRESH_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getRefresh() <em>Refresh</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getRefresh()
	 * @generated
	 * @ordered
	 */
	protected String refresh = REFRESH_EDEFAULT;

	/**
	 * The default value of the '{@link #getBackground() <em>Background</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getBackground()
	 * @generated
	 * @ordered
	 */
	protected static final String BACKGROUND_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getBackground() <em>Background</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getBackground()
	 * @generated
	 * @ordered
	 */
	protected String background = BACKGROUND_EDEFAULT;

	/**
	 * The cached value of the '{@link #getLayerList() <em>Layer List</em>}' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getLayerList()
	 * @generated
	 * @ordered
	 */
	protected EList<String> layerList;

	/**
	 * The default value of the '{@link #getColor() <em>Color</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getColor()
	 * @generated
	 * @ordered
	 */
	protected static final String COLOR_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getColor() <em>Color</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getColor()
	 * @generated
	 * @ordered
	 */
	protected String color = COLOR_EDEFAULT;

	/**
	 * The default value of the '{@link #getIsColorCst() <em>Is Color Cst</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getIsColorCst()
	 * @generated
	 * @ordered
	 */
	protected static final Boolean IS_COLOR_CST_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getIsColorCst() <em>Is Color Cst</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getIsColorCst()
	 * @generated
	 * @ordered
	 */
	protected Boolean isColorCst = IS_COLOR_CST_EDEFAULT;

	/**
	 * The cached value of the '{@link #getColorRBG() <em>Color RBG</em>}' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getColorRBG()
	 * @generated
	 * @ordered
	 */
	protected EList<Integer> colorRBG;

	/**
	 * The default value of the '{@link #getGamlCode() <em>Gaml Code</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getGamlCode()
	 * @generated
	 * @ordered
	 */
	protected static final String GAML_CODE_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getGamlCode() <em>Gaml Code</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getGamlCode()
	 * @generated
	 * @ordered
	 */
	protected String gamlCode = GAML_CODE_EDEFAULT;

	/**
	 * The default value of the '{@link #getAmbientLight() <em>Ambient Light</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getAmbientLight()
	 * @generated
	 * @ordered
	 */
	protected static final String AMBIENT_LIGHT_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getAmbientLight() <em>Ambient Light</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getAmbientLight()
	 * @generated
	 * @ordered
	 */
	protected String ambientLight = AMBIENT_LIGHT_EDEFAULT;

	/**
	 * The default value of the '{@link #getDrawDiffuseLight() <em>Draw Diffuse Light</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDrawDiffuseLight()
	 * @generated
	 * @ordered
	 */
	protected static final String DRAW_DIFFUSE_LIGHT_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getDrawDiffuseLight() <em>Draw Diffuse Light</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDrawDiffuseLight()
	 * @generated
	 * @ordered
	 */
	protected String drawDiffuseLight = DRAW_DIFFUSE_LIGHT_EDEFAULT;

	/**
	 * The default value of the '{@link #getDiffuseLight() <em>Diffuse Light</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDiffuseLight()
	 * @generated
	 * @ordered
	 */
	protected static final String DIFFUSE_LIGHT_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getDiffuseLight() <em>Diffuse Light</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDiffuseLight()
	 * @generated
	 * @ordered
	 */
	protected String diffuseLight = DIFFUSE_LIGHT_EDEFAULT;

	/**
	 * The default value of the '{@link #getDiffuseLightPos() <em>Diffuse Light Pos</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDiffuseLightPos()
	 * @generated
	 * @ordered
	 */
	protected static final String DIFFUSE_LIGHT_POS_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getDiffuseLightPos() <em>Diffuse Light Pos</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDiffuseLightPos()
	 * @generated
	 * @ordered
	 */
	protected String diffuseLightPos = DIFFUSE_LIGHT_POS_EDEFAULT;

	/**
	 * The default value of the '{@link #getZFighting() <em>ZFighting</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getZFighting()
	 * @generated
	 * @ordered
	 */
	protected static final String ZFIGHTING_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getZFighting() <em>ZFighting</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getZFighting()
	 * @generated
	 * @ordered
	 */
	protected String zFighting = ZFIGHTING_EDEFAULT;

	/**
	 * The default value of the '{@link #getCameraPos() <em>Camera Pos</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getCameraPos()
	 * @generated
	 * @ordered
	 */
	protected static final String CAMERA_POS_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getCameraPos() <em>Camera Pos</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getCameraPos()
	 * @generated
	 * @ordered
	 */
	protected String cameraPos = CAMERA_POS_EDEFAULT;

	/**
	 * The default value of the '{@link #getCameraLookPos() <em>Camera Look Pos</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getCameraLookPos()
	 * @generated
	 * @ordered
	 */
	protected static final String CAMERA_LOOK_POS_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getCameraLookPos() <em>Camera Look Pos</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getCameraLookPos()
	 * @generated
	 * @ordered
	 */
	protected String cameraLookPos = CAMERA_LOOK_POS_EDEFAULT;

	/**
	 * The default value of the '{@link #getCameraUpVector() <em>Camera Up Vector</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getCameraUpVector()
	 * @generated
	 * @ordered
	 */
	protected static final String CAMERA_UP_VECTOR_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getCameraUpVector() <em>Camera Up Vector</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getCameraUpVector()
	 * @generated
	 * @ordered
	 */
	protected String cameraUpVector = CAMERA_UP_VECTOR_EDEFAULT;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected EDisplayImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return GamaPackage.Literals.EDISPLAY;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<ELayer> getLayers() {
		if (layers == null) {
			layers = new EObjectResolvingEList<ELayer>(ELayer.class, this, GamaPackage.EDISPLAY__LAYERS);
		}
		return layers;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EDisplayLink getDisplayLink() {
		if (displayLink != null && displayLink.eIsProxy()) {
			InternalEObject oldDisplayLink = (InternalEObject)displayLink;
			displayLink = (EDisplayLink)eResolveProxy(oldDisplayLink);
			if (displayLink != oldDisplayLink) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, GamaPackage.EDISPLAY__DISPLAY_LINK, oldDisplayLink, displayLink));
			}
		}
		return displayLink;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EDisplayLink basicGetDisplayLink() {
		return displayLink;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setDisplayLink(EDisplayLink newDisplayLink) {
		EDisplayLink oldDisplayLink = displayLink;
		displayLink = newDisplayLink;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, GamaPackage.EDISPLAY__DISPLAY_LINK, oldDisplayLink, displayLink));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Boolean getOpengl() {
		return opengl;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setOpengl(Boolean newOpengl) {
		Boolean oldOpengl = opengl;
		opengl = newOpengl;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, GamaPackage.EDISPLAY__OPENGL, oldOpengl, opengl));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getRefresh() {
		return refresh;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setRefresh(String newRefresh) {
		String oldRefresh = refresh;
		refresh = newRefresh;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, GamaPackage.EDISPLAY__REFRESH, oldRefresh, refresh));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getBackground() {
		return background;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setBackground(String newBackground) {
		String oldBackground = background;
		background = newBackground;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, GamaPackage.EDISPLAY__BACKGROUND, oldBackground, background));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<String> getLayerList() {
		if (layerList == null) {
			layerList = new EDataTypeUniqueEList<String>(String.class, this, GamaPackage.EDISPLAY__LAYER_LIST);
		}
		return layerList;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getColor() {
		return color;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setColor(String newColor) {
		String oldColor = color;
		color = newColor;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, GamaPackage.EDISPLAY__COLOR, oldColor, color));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Boolean getIsColorCst() {
		return isColorCst;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setIsColorCst(Boolean newIsColorCst) {
		Boolean oldIsColorCst = isColorCst;
		isColorCst = newIsColorCst;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, GamaPackage.EDISPLAY__IS_COLOR_CST, oldIsColorCst, isColorCst));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<Integer> getColorRBG() {
		if (colorRBG == null) {
			colorRBG = new EDataTypeEList<Integer>(Integer.class, this, GamaPackage.EDISPLAY__COLOR_RBG);
		}
		return colorRBG;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getGamlCode() {
		return gamlCode;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setGamlCode(String newGamlCode) {
		String oldGamlCode = gamlCode;
		gamlCode = newGamlCode;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, GamaPackage.EDISPLAY__GAML_CODE, oldGamlCode, gamlCode));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getAmbientLight() {
		return ambientLight;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setAmbientLight(String newAmbientLight) {
		String oldAmbientLight = ambientLight;
		ambientLight = newAmbientLight;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, GamaPackage.EDISPLAY__AMBIENT_LIGHT, oldAmbientLight, ambientLight));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getDrawDiffuseLight() {
		return drawDiffuseLight;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setDrawDiffuseLight(String newDrawDiffuseLight) {
		String oldDrawDiffuseLight = drawDiffuseLight;
		drawDiffuseLight = newDrawDiffuseLight;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, GamaPackage.EDISPLAY__DRAW_DIFFUSE_LIGHT, oldDrawDiffuseLight, drawDiffuseLight));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getDiffuseLight() {
		return diffuseLight;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setDiffuseLight(String newDiffuseLight) {
		String oldDiffuseLight = diffuseLight;
		diffuseLight = newDiffuseLight;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, GamaPackage.EDISPLAY__DIFFUSE_LIGHT, oldDiffuseLight, diffuseLight));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getDiffuseLightPos() {
		return diffuseLightPos;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setDiffuseLightPos(String newDiffuseLightPos) {
		String oldDiffuseLightPos = diffuseLightPos;
		diffuseLightPos = newDiffuseLightPos;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, GamaPackage.EDISPLAY__DIFFUSE_LIGHT_POS, oldDiffuseLightPos, diffuseLightPos));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getZFighting() {
		return zFighting;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setZFighting(String newZFighting) {
		String oldZFighting = zFighting;
		zFighting = newZFighting;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, GamaPackage.EDISPLAY__ZFIGHTING, oldZFighting, zFighting));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getCameraPos() {
		return cameraPos;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setCameraPos(String newCameraPos) {
		String oldCameraPos = cameraPos;
		cameraPos = newCameraPos;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, GamaPackage.EDISPLAY__CAMERA_POS, oldCameraPos, cameraPos));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getCameraLookPos() {
		return cameraLookPos;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setCameraLookPos(String newCameraLookPos) {
		String oldCameraLookPos = cameraLookPos;
		cameraLookPos = newCameraLookPos;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, GamaPackage.EDISPLAY__CAMERA_LOOK_POS, oldCameraLookPos, cameraLookPos));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getCameraUpVector() {
		return cameraUpVector;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setCameraUpVector(String newCameraUpVector) {
		String oldCameraUpVector = cameraUpVector;
		cameraUpVector = newCameraUpVector;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, GamaPackage.EDISPLAY__CAMERA_UP_VECTOR, oldCameraUpVector, cameraUpVector));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case GamaPackage.EDISPLAY__LAYERS:
				return getLayers();
			case GamaPackage.EDISPLAY__DISPLAY_LINK:
				if (resolve) return getDisplayLink();
				return basicGetDisplayLink();
			case GamaPackage.EDISPLAY__OPENGL:
				return getOpengl();
			case GamaPackage.EDISPLAY__REFRESH:
				return getRefresh();
			case GamaPackage.EDISPLAY__BACKGROUND:
				return getBackground();
			case GamaPackage.EDISPLAY__LAYER_LIST:
				return getLayerList();
			case GamaPackage.EDISPLAY__COLOR:
				return getColor();
			case GamaPackage.EDISPLAY__IS_COLOR_CST:
				return getIsColorCst();
			case GamaPackage.EDISPLAY__COLOR_RBG:
				return getColorRBG();
			case GamaPackage.EDISPLAY__GAML_CODE:
				return getGamlCode();
			case GamaPackage.EDISPLAY__AMBIENT_LIGHT:
				return getAmbientLight();
			case GamaPackage.EDISPLAY__DRAW_DIFFUSE_LIGHT:
				return getDrawDiffuseLight();
			case GamaPackage.EDISPLAY__DIFFUSE_LIGHT:
				return getDiffuseLight();
			case GamaPackage.EDISPLAY__DIFFUSE_LIGHT_POS:
				return getDiffuseLightPos();
			case GamaPackage.EDISPLAY__ZFIGHTING:
				return getZFighting();
			case GamaPackage.EDISPLAY__CAMERA_POS:
				return getCameraPos();
			case GamaPackage.EDISPLAY__CAMERA_LOOK_POS:
				return getCameraLookPos();
			case GamaPackage.EDISPLAY__CAMERA_UP_VECTOR:
				return getCameraUpVector();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
			case GamaPackage.EDISPLAY__LAYERS:
				getLayers().clear();
				getLayers().addAll((Collection<? extends ELayer>)newValue);
				return;
			case GamaPackage.EDISPLAY__DISPLAY_LINK:
				setDisplayLink((EDisplayLink)newValue);
				return;
			case GamaPackage.EDISPLAY__OPENGL:
				setOpengl((Boolean)newValue);
				return;
			case GamaPackage.EDISPLAY__REFRESH:
				setRefresh((String)newValue);
				return;
			case GamaPackage.EDISPLAY__BACKGROUND:
				setBackground((String)newValue);
				return;
			case GamaPackage.EDISPLAY__LAYER_LIST:
				getLayerList().clear();
				getLayerList().addAll((Collection<? extends String>)newValue);
				return;
			case GamaPackage.EDISPLAY__COLOR:
				setColor((String)newValue);
				return;
			case GamaPackage.EDISPLAY__IS_COLOR_CST:
				setIsColorCst((Boolean)newValue);
				return;
			case GamaPackage.EDISPLAY__COLOR_RBG:
				getColorRBG().clear();
				getColorRBG().addAll((Collection<? extends Integer>)newValue);
				return;
			case GamaPackage.EDISPLAY__GAML_CODE:
				setGamlCode((String)newValue);
				return;
			case GamaPackage.EDISPLAY__AMBIENT_LIGHT:
				setAmbientLight((String)newValue);
				return;
			case GamaPackage.EDISPLAY__DRAW_DIFFUSE_LIGHT:
				setDrawDiffuseLight((String)newValue);
				return;
			case GamaPackage.EDISPLAY__DIFFUSE_LIGHT:
				setDiffuseLight((String)newValue);
				return;
			case GamaPackage.EDISPLAY__DIFFUSE_LIGHT_POS:
				setDiffuseLightPos((String)newValue);
				return;
			case GamaPackage.EDISPLAY__ZFIGHTING:
				setZFighting((String)newValue);
				return;
			case GamaPackage.EDISPLAY__CAMERA_POS:
				setCameraPos((String)newValue);
				return;
			case GamaPackage.EDISPLAY__CAMERA_LOOK_POS:
				setCameraLookPos((String)newValue);
				return;
			case GamaPackage.EDISPLAY__CAMERA_UP_VECTOR:
				setCameraUpVector((String)newValue);
				return;
		}
		super.eSet(featureID, newValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void eUnset(int featureID) {
		switch (featureID) {
			case GamaPackage.EDISPLAY__LAYERS:
				getLayers().clear();
				return;
			case GamaPackage.EDISPLAY__DISPLAY_LINK:
				setDisplayLink((EDisplayLink)null);
				return;
			case GamaPackage.EDISPLAY__OPENGL:
				setOpengl(OPENGL_EDEFAULT);
				return;
			case GamaPackage.EDISPLAY__REFRESH:
				setRefresh(REFRESH_EDEFAULT);
				return;
			case GamaPackage.EDISPLAY__BACKGROUND:
				setBackground(BACKGROUND_EDEFAULT);
				return;
			case GamaPackage.EDISPLAY__LAYER_LIST:
				getLayerList().clear();
				return;
			case GamaPackage.EDISPLAY__COLOR:
				setColor(COLOR_EDEFAULT);
				return;
			case GamaPackage.EDISPLAY__IS_COLOR_CST:
				setIsColorCst(IS_COLOR_CST_EDEFAULT);
				return;
			case GamaPackage.EDISPLAY__COLOR_RBG:
				getColorRBG().clear();
				return;
			case GamaPackage.EDISPLAY__GAML_CODE:
				setGamlCode(GAML_CODE_EDEFAULT);
				return;
			case GamaPackage.EDISPLAY__AMBIENT_LIGHT:
				setAmbientLight(AMBIENT_LIGHT_EDEFAULT);
				return;
			case GamaPackage.EDISPLAY__DRAW_DIFFUSE_LIGHT:
				setDrawDiffuseLight(DRAW_DIFFUSE_LIGHT_EDEFAULT);
				return;
			case GamaPackage.EDISPLAY__DIFFUSE_LIGHT:
				setDiffuseLight(DIFFUSE_LIGHT_EDEFAULT);
				return;
			case GamaPackage.EDISPLAY__DIFFUSE_LIGHT_POS:
				setDiffuseLightPos(DIFFUSE_LIGHT_POS_EDEFAULT);
				return;
			case GamaPackage.EDISPLAY__ZFIGHTING:
				setZFighting(ZFIGHTING_EDEFAULT);
				return;
			case GamaPackage.EDISPLAY__CAMERA_POS:
				setCameraPos(CAMERA_POS_EDEFAULT);
				return;
			case GamaPackage.EDISPLAY__CAMERA_LOOK_POS:
				setCameraLookPos(CAMERA_LOOK_POS_EDEFAULT);
				return;
			case GamaPackage.EDISPLAY__CAMERA_UP_VECTOR:
				setCameraUpVector(CAMERA_UP_VECTOR_EDEFAULT);
				return;
		}
		super.eUnset(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean eIsSet(int featureID) {
		switch (featureID) {
			case GamaPackage.EDISPLAY__LAYERS:
				return layers != null && !layers.isEmpty();
			case GamaPackage.EDISPLAY__DISPLAY_LINK:
				return displayLink != null;
			case GamaPackage.EDISPLAY__OPENGL:
				return OPENGL_EDEFAULT == null ? opengl != null : !OPENGL_EDEFAULT.equals(opengl);
			case GamaPackage.EDISPLAY__REFRESH:
				return REFRESH_EDEFAULT == null ? refresh != null : !REFRESH_EDEFAULT.equals(refresh);
			case GamaPackage.EDISPLAY__BACKGROUND:
				return BACKGROUND_EDEFAULT == null ? background != null : !BACKGROUND_EDEFAULT.equals(background);
			case GamaPackage.EDISPLAY__LAYER_LIST:
				return layerList != null && !layerList.isEmpty();
			case GamaPackage.EDISPLAY__COLOR:
				return COLOR_EDEFAULT == null ? color != null : !COLOR_EDEFAULT.equals(color);
			case GamaPackage.EDISPLAY__IS_COLOR_CST:
				return IS_COLOR_CST_EDEFAULT == null ? isColorCst != null : !IS_COLOR_CST_EDEFAULT.equals(isColorCst);
			case GamaPackage.EDISPLAY__COLOR_RBG:
				return colorRBG != null && !colorRBG.isEmpty();
			case GamaPackage.EDISPLAY__GAML_CODE:
				return GAML_CODE_EDEFAULT == null ? gamlCode != null : !GAML_CODE_EDEFAULT.equals(gamlCode);
			case GamaPackage.EDISPLAY__AMBIENT_LIGHT:
				return AMBIENT_LIGHT_EDEFAULT == null ? ambientLight != null : !AMBIENT_LIGHT_EDEFAULT.equals(ambientLight);
			case GamaPackage.EDISPLAY__DRAW_DIFFUSE_LIGHT:
				return DRAW_DIFFUSE_LIGHT_EDEFAULT == null ? drawDiffuseLight != null : !DRAW_DIFFUSE_LIGHT_EDEFAULT.equals(drawDiffuseLight);
			case GamaPackage.EDISPLAY__DIFFUSE_LIGHT:
				return DIFFUSE_LIGHT_EDEFAULT == null ? diffuseLight != null : !DIFFUSE_LIGHT_EDEFAULT.equals(diffuseLight);
			case GamaPackage.EDISPLAY__DIFFUSE_LIGHT_POS:
				return DIFFUSE_LIGHT_POS_EDEFAULT == null ? diffuseLightPos != null : !DIFFUSE_LIGHT_POS_EDEFAULT.equals(diffuseLightPos);
			case GamaPackage.EDISPLAY__ZFIGHTING:
				return ZFIGHTING_EDEFAULT == null ? zFighting != null : !ZFIGHTING_EDEFAULT.equals(zFighting);
			case GamaPackage.EDISPLAY__CAMERA_POS:
				return CAMERA_POS_EDEFAULT == null ? cameraPos != null : !CAMERA_POS_EDEFAULT.equals(cameraPos);
			case GamaPackage.EDISPLAY__CAMERA_LOOK_POS:
				return CAMERA_LOOK_POS_EDEFAULT == null ? cameraLookPos != null : !CAMERA_LOOK_POS_EDEFAULT.equals(cameraLookPos);
			case GamaPackage.EDISPLAY__CAMERA_UP_VECTOR:
				return CAMERA_UP_VECTOR_EDEFAULT == null ? cameraUpVector != null : !CAMERA_UP_VECTOR_EDEFAULT.equals(cameraUpVector);
		}
		return super.eIsSet(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String toString() {
		if (eIsProxy()) return super.toString();

		StringBuffer result = new StringBuffer(super.toString());
		result.append(" (opengl: ");
		result.append(opengl);
		result.append(", refresh: ");
		result.append(refresh);
		result.append(", background: ");
		result.append(background);
		result.append(", layerList: ");
		result.append(layerList);
		result.append(", color: ");
		result.append(color);
		result.append(", isColorCst: ");
		result.append(isColorCst);
		result.append(", colorRBG: ");
		result.append(colorRBG);
		result.append(", gamlCode: ");
		result.append(gamlCode);
		result.append(", ambientLight: ");
		result.append(ambientLight);
		result.append(", drawDiffuseLight: ");
		result.append(drawDiffuseLight);
		result.append(", diffuseLight: ");
		result.append(diffuseLight);
		result.append(", diffuseLightPos: ");
		result.append(diffuseLightPos);
		result.append(", zFighting: ");
		result.append(zFighting);
		result.append(", cameraPos: ");
		result.append(cameraPos);
		result.append(", cameraLookPos: ");
		result.append(cameraLookPos);
		result.append(", cameraUpVector: ");
		result.append(cameraUpVector);
		result.append(')');
		return result.toString();
	}

} //EDisplayImpl
