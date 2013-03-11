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
} // EDisplay
