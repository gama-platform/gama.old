/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package gama;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>EGrid</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link gama.EGrid#getNb_columns <em>Nb columns</em>}</li>
 *   <li>{@link gama.EGrid#getNb_rows <em>Nb rows</em>}</li>
 *   <li>{@link gama.EGrid#getNeighbourhood <em>Neighbourhood</em>}</li>
 * </ul>
 * </p>
 *
 * @see gama.GamaPackage#getEGrid()
 * @model
 * @generated
 */
public interface EGrid extends ESpecies {
	/**
	 * Returns the value of the '<em><b>Nb columns</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Nb columns</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Nb columns</em>' attribute.
	 * @see #setNb_columns(String)
	 * @see gama.GamaPackage#getEGrid_Nb_columns()
	 * @model
	 * @generated
	 */
	String getNb_columns();

	/**
	 * Sets the value of the '{@link gama.EGrid#getNb_columns <em>Nb columns</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Nb columns</em>' attribute.
	 * @see #getNb_columns()
	 * @generated
	 */
	void setNb_columns(String value);

	/**
	 * Returns the value of the '<em><b>Nb rows</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Nb rows</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Nb rows</em>' attribute.
	 * @see #setNb_rows(String)
	 * @see gama.GamaPackage#getEGrid_Nb_rows()
	 * @model
	 * @generated
	 */
	String getNb_rows();

	/**
	 * Sets the value of the '{@link gama.EGrid#getNb_rows <em>Nb rows</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Nb rows</em>' attribute.
	 * @see #getNb_rows()
	 * @generated
	 */
	void setNb_rows(String value);

	/**
	 * Returns the value of the '<em><b>Neighbourhood</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Neighbourhood</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Neighbourhood</em>' attribute.
	 * @see #setNeighbourhood(String)
	 * @see gama.GamaPackage#getEGrid_Neighbourhood()
	 * @model
	 * @generated
	 */
	String getNeighbourhood();

	/**
	 * Sets the value of the '{@link gama.EGrid#getNeighbourhood <em>Neighbourhood</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Neighbourhood</em>' attribute.
	 * @see #getNeighbourhood()
	 * @generated
	 */
	void setNeighbourhood(String value);

} // EGrid
