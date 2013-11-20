/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package gama;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>EGrid Topology</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link gama.EGridTopology#getNb_columns <em>Nb columns</em>}</li>
 *   <li>{@link gama.EGridTopology#getNb_rows <em>Nb rows</em>}</li>
 *   <li>{@link gama.EGridTopology#getNeighbourhood <em>Neighbourhood</em>}</li>
 *   <li>{@link gama.EGridTopology#getNeighbourhoodType <em>Neighbourhood Type</em>}</li>
 * </ul>
 * </p>
 *
 * @see gama.GamaPackage#getEGridTopology()
 * @model
 * @generated
 */
public interface EGridTopology extends ETopology {
	/**
	 * Returns the value of the '<em><b>Nb columns</b></em>' attribute.
	 * The default value is <code>"100"</code>.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Nb columns</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Nb columns</em>' attribute.
	 * @see #setNb_columns(String)
	 * @see gama.GamaPackage#getEGridTopology_Nb_columns()
	 * @model default="100"
	 * @generated
	 */
	String getNb_columns();

	/**
	 * Sets the value of the '{@link gama.EGridTopology#getNb_columns <em>Nb columns</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Nb columns</em>' attribute.
	 * @see #getNb_columns()
	 * @generated
	 */
	void setNb_columns(String value);

	/**
	 * Returns the value of the '<em><b>Nb rows</b></em>' attribute.
	 * The default value is <code>"100"</code>.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Nb rows</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Nb rows</em>' attribute.
	 * @see #setNb_rows(String)
	 * @see gama.GamaPackage#getEGridTopology_Nb_rows()
	 * @model default="100"
	 * @generated
	 */
	String getNb_rows();

	/**
	 * Sets the value of the '{@link gama.EGridTopology#getNb_rows <em>Nb rows</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Nb rows</em>' attribute.
	 * @see #getNb_rows()
	 * @generated
	 */
	void setNb_rows(String value);

	/**
	 * Returns the value of the '<em><b>Neighbourhood</b></em>' attribute.
	 * The default value is <code>""</code>.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Neighbourhood</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Neighbourhood</em>' attribute.
	 * @see #setNeighbourhood(String)
	 * @see gama.GamaPackage#getEGridTopology_Neighbourhood()
	 * @model default=""
	 * @generated
	 */
	String getNeighbourhood();

	/**
	 * Sets the value of the '{@link gama.EGridTopology#getNeighbourhood <em>Neighbourhood</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Neighbourhood</em>' attribute.
	 * @see #getNeighbourhood()
	 * @generated
	 */
	void setNeighbourhood(String value);

	/**
	 * Returns the value of the '<em><b>Neighbourhood Type</b></em>' attribute.
	 * The default value is <code>"4 (square - von Neumann)"</code>.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Neighbourhood Type</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Neighbourhood Type</em>' attribute.
	 * @see #setNeighbourhoodType(String)
	 * @see gama.GamaPackage#getEGridTopology_NeighbourhoodType()
	 * @model default="4 (square - von Neumann)"
	 * @generated
	 */
	String getNeighbourhoodType();

	/**
	 * Sets the value of the '{@link gama.EGridTopology#getNeighbourhoodType <em>Neighbourhood Type</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Neighbourhood Type</em>' attribute.
	 * @see #getNeighbourhoodType()
	 * @generated
	 */
	void setNeighbourhoodType(String value);

} // EGridTopology
