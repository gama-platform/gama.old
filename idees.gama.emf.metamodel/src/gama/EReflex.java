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
 * A representation of the model object '<em><b>EReflex</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link gama.EReflex#getGamlCode <em>Gaml Code</em>}</li>
 *   <li>{@link gama.EReflex#getCondition <em>Condition</em>}</li>
 *   <li>{@link gama.EReflex#getReflexLinks <em>Reflex Links</em>}</li>
 * </ul>
 * </p>
 *
 * @see gama.GamaPackage#getEReflex()
 * @model
 * @generated
 */
public interface EReflex extends EGamaObject {
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
	 * @see gama.GamaPackage#getEReflex_GamlCode()
	 * @model derived="true"
	 * @generated
	 */
	String getGamlCode();

	/**
	 * Sets the value of the '{@link gama.EReflex#getGamlCode <em>Gaml Code</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Gaml Code</em>' attribute.
	 * @see #getGamlCode()
	 * @generated
	 */
	void setGamlCode(String value);

	/**
	 * Returns the value of the '<em><b>Condition</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Condition</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Condition</em>' attribute.
	 * @see #setCondition(String)
	 * @see gama.GamaPackage#getEReflex_Condition()
	 * @model derived="true"
	 * @generated
	 */
	String getCondition();

	/**
	 * Sets the value of the '{@link gama.EReflex#getCondition <em>Condition</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Condition</em>' attribute.
	 * @see #getCondition()
	 * @generated
	 */
	void setCondition(String value);

	/**
	 * Returns the value of the '<em><b>Reflex Links</b></em>' reference list.
	 * The list contents are of type {@link gama.EReflexLink}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Reflex Links</em>' reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Reflex Links</em>' reference list.
	 * @see gama.GamaPackage#getEReflex_ReflexLinks()
	 * @model
	 * @generated
	 */
	EList<EReflexLink> getReflexLinks();

} // EReflex
