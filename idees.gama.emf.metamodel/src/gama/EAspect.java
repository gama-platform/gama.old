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
 * A representation of the model object '<em><b>EAspect</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link gama.EAspect#getGamlCode <em>Gaml Code</em>}</li>
 *   <li>{@link gama.EAspect#getAspectLinks <em>Aspect Links</em>}</li>
 *   <li>{@link gama.EAspect#getLayers <em>Layers</em>}</li>
 * </ul>
 * </p>
 *
 * @see gama.GamaPackage#getEAspect()
 * @model
 * @generated
 */
public interface EAspect extends EGamaObject {
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
	 * @see gama.GamaPackage#getEAspect_GamlCode()
	 * @model derived="true"
	 * @generated
	 */
	String getGamlCode();

	/**
	 * Sets the value of the '{@link gama.EAspect#getGamlCode <em>Gaml Code</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Gaml Code</em>' attribute.
	 * @see #getGamlCode()
	 * @generated
	 */
	void setGamlCode(String value);

	/**
	 * Returns the value of the '<em><b>Aspect Links</b></em>' reference list.
	 * The list contents are of type {@link gama.EAspectLink}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Aspect Links</em>' reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Aspect Links</em>' reference list.
	 * @see gama.GamaPackage#getEAspect_AspectLinks()
	 * @model
	 * @generated
	 */
	EList<EAspectLink> getAspectLinks();

	/**
	 * Returns the value of the '<em><b>Layers</b></em>' reference list.
	 * The list contents are of type {@link gama.ELayerAspect}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Layers</em>' reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Layers</em>' reference list.
	 * @see gama.GamaPackage#getEAspect_Layers()
	 * @model
	 * @generated
	 */
	EList<ELayerAspect> getLayers();

} // EAspect
