/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package gama;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>EDisplay</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link gama.EDisplay#getLayers <em>Layers</em>}</li>
 *   <li>{@link gama.EDisplay#getDisplayLink <em>Display Link</em>}</li>
 *   <li>{@link gama.EDisplay#getOpengl <em>Opengl</em>}</li>
 *   <li>{@link gama.EDisplay#getRefresh <em>Refresh</em>}</li>
 *   <li>{@link gama.EDisplay#getBackground <em>Background</em>}</li>
 *   <li>{@link gama.EDisplay#getLayerList <em>Layer List</em>}</li>
 *   <li>{@link gama.EDisplay#getColor <em>Color</em>}</li>
 *   <li>{@link gama.EDisplay#getIsColorCst <em>Is Color Cst</em>}</li>
 *   <li>{@link gama.EDisplay#getColorRBG <em>Color RBG</em>}</li>
 *   <li>{@link gama.EDisplay#getGamlCode <em>Gaml Code</em>}</li>
 *   <li>{@link gama.EDisplay#getAmbientLight <em>Ambient Light</em>}</li>
 *   <li>{@link gama.EDisplay#getDrawDiffuseLight <em>Draw Diffuse Light</em>}</li>
 *   <li>{@link gama.EDisplay#getDiffuseLight <em>Diffuse Light</em>}</li>
 *   <li>{@link gama.EDisplay#getDiffuseLightPos <em>Diffuse Light Pos</em>}</li>
 *   <li>{@link gama.EDisplay#getZFighting <em>ZFighting</em>}</li>
 *   <li>{@link gama.EDisplay#getCameraPos <em>Camera Pos</em>}</li>
 *   <li>{@link gama.EDisplay#getCameraLookPos <em>Camera Look Pos</em>}</li>
 *   <li>{@link gama.EDisplay#getCameraUpVector <em>Camera Up Vector</em>}</li>
 * </ul>
 * </p>
 *
 * @see gama.GamaPackage#getEDisplay()
 * @model
 * @generated
 */
public interface EDisplay extends EGamaObject {
	/**
	 * Returns the value of the '<em><b>Layers</b></em>' reference list.
	 * The list contents are of type {@link gama.ELayer}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Layers</em>' reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Layers</em>' reference list.
	 * @see gama.GamaPackage#getEDisplay_Layers()
	 * @model
	 * @generated
	 */
	EList<ELayer> getLayers();

	/**
	 * Returns the value of the '<em><b>Display Link</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Display Link</em>' reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Display Link</em>' reference.
	 * @see #setDisplayLink(EDisplayLink)
	 * @see gama.GamaPackage#getEDisplay_DisplayLink()
	 * @model
	 * @generated
	 */
	EDisplayLink getDisplayLink();

	/**
	 * Sets the value of the '{@link gama.EDisplay#getDisplayLink <em>Display Link</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Display Link</em>' reference.
	 * @see #getDisplayLink()
	 * @generated
	 */
	void setDisplayLink(EDisplayLink value);

	/**
	 * Returns the value of the '<em><b>Opengl</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Opengl</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Opengl</em>' attribute.
	 * @see #setOpengl(Boolean)
	 * @see gama.GamaPackage#getEDisplay_Opengl()
	 * @model
	 * @generated
	 */
	Boolean getOpengl();

	/**
	 * Sets the value of the '{@link gama.EDisplay#getOpengl <em>Opengl</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Opengl</em>' attribute.
	 * @see #getOpengl()
	 * @generated
	 */
	void setOpengl(Boolean value);

	/**
	 * Returns the value of the '<em><b>Refresh</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Refresh</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Refresh</em>' attribute.
	 * @see #setRefresh(String)
	 * @see gama.GamaPackage#getEDisplay_Refresh()
	 * @model
	 * @generated
	 */
	String getRefresh();

	/**
	 * Sets the value of the '{@link gama.EDisplay#getRefresh <em>Refresh</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Refresh</em>' attribute.
	 * @see #getRefresh()
	 * @generated
	 */
	void setRefresh(String value);

	/**
	 * Returns the value of the '<em><b>Background</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Background</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Background</em>' attribute.
	 * @see #setBackground(String)
	 * @see gama.GamaPackage#getEDisplay_Background()
	 * @model
	 * @generated
	 */
	String getBackground();

	/**
	 * Sets the value of the '{@link gama.EDisplay#getBackground <em>Background</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Background</em>' attribute.
	 * @see #getBackground()
	 * @generated
	 */
	void setBackground(String value);

	/**
	 * Returns the value of the '<em><b>Layer List</b></em>' attribute list.
	 * The list contents are of type {@link java.lang.String}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Layer List</em>' attribute list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Layer List</em>' attribute list.
	 * @see gama.GamaPackage#getEDisplay_LayerList()
	 * @model
	 * @generated
	 */
	EList<String> getLayerList();

	/**
	 * Returns the value of the '<em><b>Color</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Color</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Color</em>' attribute.
	 * @see #setColor(String)
	 * @see gama.GamaPackage#getEDisplay_Color()
	 * @model
	 * @generated
	 */
	String getColor();

	/**
	 * Sets the value of the '{@link gama.EDisplay#getColor <em>Color</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Color</em>' attribute.
	 * @see #getColor()
	 * @generated
	 */
	void setColor(String value);

	/**
	 * Returns the value of the '<em><b>Is Color Cst</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Is Color Cst</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Is Color Cst</em>' attribute.
	 * @see #setIsColorCst(Boolean)
	 * @see gama.GamaPackage#getEDisplay_IsColorCst()
	 * @model derived="true"
	 * @generated
	 */
	Boolean getIsColorCst();

	/**
	 * Sets the value of the '{@link gama.EDisplay#getIsColorCst <em>Is Color Cst</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Is Color Cst</em>' attribute.
	 * @see #getIsColorCst()
	 * @generated
	 */
	void setIsColorCst(Boolean value);

	/**
	 * Returns the value of the '<em><b>Color RBG</b></em>' attribute list.
	 * The list contents are of type {@link java.lang.Integer}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Color RBG</em>' attribute list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Color RBG</em>' attribute list.
	 * @see gama.GamaPackage#getEDisplay_ColorRBG()
	 * @model unique="false" upper="3"
	 * @generated
	 */
	EList<Integer> getColorRBG();

	/**
	 * Returns the value of the '<em><b>Gaml Code</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Gaml Code</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Gaml Code</em>' attribute.
	 * @see #setGamlCode(String)
	 * @see gama.GamaPackage#getEDisplay_GamlCode()
	 * @model
	 * @generated
	 */
	String getGamlCode();

	/**
	 * Sets the value of the '{@link gama.EDisplay#getGamlCode <em>Gaml Code</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Gaml Code</em>' attribute.
	 * @see #getGamlCode()
	 * @generated
	 */
	void setGamlCode(String value);

	/**
	 * Returns the value of the '<em><b>Ambient Light</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Ambient Light</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Ambient Light</em>' attribute.
	 * @see #setAmbientLight(String)
	 * @see gama.GamaPackage#getEDisplay_AmbientLight()
	 * @model
	 * @generated
	 */
	String getAmbientLight();

	/**
	 * Sets the value of the '{@link gama.EDisplay#getAmbientLight <em>Ambient Light</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Ambient Light</em>' attribute.
	 * @see #getAmbientLight()
	 * @generated
	 */
	void setAmbientLight(String value);

	/**
	 * Returns the value of the '<em><b>Draw Diffuse Light</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Draw Diffuse Light</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Draw Diffuse Light</em>' attribute.
	 * @see #setDrawDiffuseLight(String)
	 * @see gama.GamaPackage#getEDisplay_DrawDiffuseLight()
	 * @model
	 * @generated
	 */
	String getDrawDiffuseLight();

	/**
	 * Sets the value of the '{@link gama.EDisplay#getDrawDiffuseLight <em>Draw Diffuse Light</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Draw Diffuse Light</em>' attribute.
	 * @see #getDrawDiffuseLight()
	 * @generated
	 */
	void setDrawDiffuseLight(String value);

	/**
	 * Returns the value of the '<em><b>Diffuse Light</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Diffuse Light</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Diffuse Light</em>' attribute.
	 * @see #setDiffuseLight(String)
	 * @see gama.GamaPackage#getEDisplay_DiffuseLight()
	 * @model
	 * @generated
	 */
	String getDiffuseLight();

	/**
	 * Sets the value of the '{@link gama.EDisplay#getDiffuseLight <em>Diffuse Light</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Diffuse Light</em>' attribute.
	 * @see #getDiffuseLight()
	 * @generated
	 */
	void setDiffuseLight(String value);

	/**
	 * Returns the value of the '<em><b>Diffuse Light Pos</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Diffuse Light Pos</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Diffuse Light Pos</em>' attribute.
	 * @see #setDiffuseLightPos(String)
	 * @see gama.GamaPackage#getEDisplay_DiffuseLightPos()
	 * @model
	 * @generated
	 */
	String getDiffuseLightPos();

	/**
	 * Sets the value of the '{@link gama.EDisplay#getDiffuseLightPos <em>Diffuse Light Pos</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Diffuse Light Pos</em>' attribute.
	 * @see #getDiffuseLightPos()
	 * @generated
	 */
	void setDiffuseLightPos(String value);

	/**
	 * Returns the value of the '<em><b>ZFighting</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>ZFighting</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>ZFighting</em>' attribute.
	 * @see #setZFighting(String)
	 * @see gama.GamaPackage#getEDisplay_ZFighting()
	 * @model
	 * @generated
	 */
	String getZFighting();

	/**
	 * Sets the value of the '{@link gama.EDisplay#getZFighting <em>ZFighting</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>ZFighting</em>' attribute.
	 * @see #getZFighting()
	 * @generated
	 */
	void setZFighting(String value);

	/**
	 * Returns the value of the '<em><b>Camera Pos</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Camera Pos</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Camera Pos</em>' attribute.
	 * @see #setCameraPos(String)
	 * @see gama.GamaPackage#getEDisplay_CameraPos()
	 * @model
	 * @generated
	 */
	String getCameraPos();

	/**
	 * Sets the value of the '{@link gama.EDisplay#getCameraPos <em>Camera Pos</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Camera Pos</em>' attribute.
	 * @see #getCameraPos()
	 * @generated
	 */
	void setCameraPos(String value);

	/**
	 * Returns the value of the '<em><b>Camera Look Pos</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Camera Look Pos</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Camera Look Pos</em>' attribute.
	 * @see #setCameraLookPos(String)
	 * @see gama.GamaPackage#getEDisplay_CameraLookPos()
	 * @model
	 * @generated
	 */
	String getCameraLookPos();

	/**
	 * Sets the value of the '{@link gama.EDisplay#getCameraLookPos <em>Camera Look Pos</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Camera Look Pos</em>' attribute.
	 * @see #getCameraLookPos()
	 * @generated
	 */
	void setCameraLookPos(String value);

	/**
	 * Returns the value of the '<em><b>Camera Up Vector</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Camera Up Vector</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Camera Up Vector</em>' attribute.
	 * @see #setCameraUpVector(String)
	 * @see gama.GamaPackage#getEDisplay_CameraUpVector()
	 * @model
	 * @generated
	 */
	String getCameraUpVector();

	/**
	 * Sets the value of the '{@link gama.EDisplay#getCameraUpVector <em>Camera Up Vector</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Camera Up Vector</em>' attribute.
	 * @see #getCameraUpVector()
	 * @generated
	 */
	void setCameraUpVector(String value);

} // EDisplay
