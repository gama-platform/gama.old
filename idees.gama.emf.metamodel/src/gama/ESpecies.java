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
 * A representation of the model object '<em><b>ESpecies</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link gama.ESpecies#getVariables <em>Variables</em>}</li>
 *   <li>{@link gama.ESpecies#getTopology <em>Topology</em>}</li>
 *   <li>{@link gama.ESpecies#getReflexList <em>Reflex List</em>}</li>
 *   <li>{@link gama.ESpecies#getTorus <em>Torus</em>}</li>
 *   <li>{@link gama.ESpecies#getExperimentLinks <em>Experiment Links</em>}</li>
 *   <li>{@link gama.ESpecies#getAspectLinks <em>Aspect Links</em>}</li>
 *   <li>{@link gama.ESpecies#getActionLinks <em>Action Links</em>}</li>
 *   <li>{@link gama.ESpecies#getReflexLinks <em>Reflex Links</em>}</li>
 *   <li>{@link gama.ESpecies#getShape <em>Shape</em>}</li>
 *   <li>{@link gama.ESpecies#getLocation <em>Location</em>}</li>
 *   <li>{@link gama.ESpecies#getSize <em>Size</em>}</li>
 *   <li>{@link gama.ESpecies#getWidth <em>Width</em>}</li>
 *   <li>{@link gama.ESpecies#getHeigth <em>Heigth</em>}</li>
 *   <li>{@link gama.ESpecies#getRadius <em>Radius</em>}</li>
 *   <li>{@link gama.ESpecies#getMicroSpeciesLinks <em>Micro Species Links</em>}</li>
 *   <li>{@link gama.ESpecies#getMacroSpeciesLinks <em>Macro Species Links</em>}</li>
 * </ul>
 * </p>
 *
 * @see gama.GamaPackage#getESpecies()
 * @model
 * @generated
 */
public interface ESpecies extends EGamaObject {
	/**
	 * Returns the value of the '<em><b>Variables</b></em>' containment reference list.
	 * The list contents are of type {@link gama.EVariable}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Variables</em>' reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Variables</em>' containment reference list.
	 * @see gama.GamaPackage#getESpecies_Variables()
	 * @model containment="true"
	 * @generated
	 */
	EList<EVariable> getVariables();

	/**
	 * Returns the value of the '<em><b>Topology</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Topology</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Topology</em>' attribute.
	 * @see #setTopology(String)
	 * @see gama.GamaPackage#getESpecies_Topology()
	 * @model
	 * @generated
	 */
	String getTopology();

	/**
	 * Sets the value of the '{@link gama.ESpecies#getTopology <em>Topology</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Topology</em>' attribute.
	 * @see #getTopology()
	 * @generated
	 */
	void setTopology(String value);

	/**
	 * Returns the value of the '<em><b>Reflex List</b></em>' attribute list.
	 * The list contents are of type {@link java.lang.String}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Reflex List</em>' attribute list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Reflex List</em>' attribute list.
	 * @see gama.GamaPackage#getESpecies_ReflexList()
	 * @model
	 * @generated
	 */
	EList<String> getReflexList();

	/**
	 * Returns the value of the '<em><b>Torus</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Torus</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Torus</em>' attribute.
	 * @see #setTorus(String)
	 * @see gama.GamaPackage#getESpecies_Torus()
	 * @model
	 * @generated
	 */
	String getTorus();

	/**
	 * Sets the value of the '{@link gama.ESpecies#getTorus <em>Torus</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Torus</em>' attribute.
	 * @see #getTorus()
	 * @generated
	 */
	void setTorus(String value);

	/**
	 * Returns the value of the '<em><b>Experiment Links</b></em>' reference list.
	 * The list contents are of type {@link gama.EExperimentLink}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Experiment Links</em>' reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Experiment Links</em>' reference list.
	 * @see gama.GamaPackage#getESpecies_ExperimentLinks()
	 * @model
	 * @generated
	 */
	EList<EExperimentLink> getExperimentLinks();

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
	 * @see gama.GamaPackage#getESpecies_AspectLinks()
	 * @model
	 * @generated
	 */
	EList<EAspectLink> getAspectLinks();

	/**
	 * Returns the value of the '<em><b>Action Links</b></em>' reference list.
	 * The list contents are of type {@link gama.EActionLink}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Action Links</em>' reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Action Links</em>' reference list.
	 * @see gama.GamaPackage#getESpecies_ActionLinks()
	 * @model
	 * @generated
	 */
	EList<EActionLink> getActionLinks();

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
	 * @see gama.GamaPackage#getESpecies_ReflexLinks()
	 * @model
	 * @generated
	 */
	EList<EReflexLink> getReflexLinks();

	/**
	 * Returns the value of the '<em><b>Shape</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Shape</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Shape</em>' attribute.
	 * @see #setShape(String)
	 * @see gama.GamaPackage#getESpecies_Shape()
	 * @model
	 * @generated
	 */
	String getShape();

	/**
	 * Sets the value of the '{@link gama.ESpecies#getShape <em>Shape</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Shape</em>' attribute.
	 * @see #getShape()
	 * @generated
	 */
	void setShape(String value);

	/**
	 * Returns the value of the '<em><b>Location</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Location</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Location</em>' attribute.
	 * @see #setLocation(String)
	 * @see gama.GamaPackage#getESpecies_Location()
	 * @model
	 * @generated
	 */
	String getLocation();

	/**
	 * Sets the value of the '{@link gama.ESpecies#getLocation <em>Location</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Location</em>' attribute.
	 * @see #getLocation()
	 * @generated
	 */
	void setLocation(String value);

	/**
	 * Returns the value of the '<em><b>Size</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Size</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Size</em>' attribute.
	 * @see #setSize(String)
	 * @see gama.GamaPackage#getESpecies_Size()
	 * @model
	 * @generated
	 */
	String getSize();

	/**
	 * Sets the value of the '{@link gama.ESpecies#getSize <em>Size</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Size</em>' attribute.
	 * @see #getSize()
	 * @generated
	 */
	void setSize(String value);

	/**
	 * Returns the value of the '<em><b>Width</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Width</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Width</em>' attribute.
	 * @see #setWidth(String)
	 * @see gama.GamaPackage#getESpecies_Width()
	 * @model
	 * @generated
	 */
	String getWidth();

	/**
	 * Sets the value of the '{@link gama.ESpecies#getWidth <em>Width</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Width</em>' attribute.
	 * @see #getWidth()
	 * @generated
	 */
	void setWidth(String value);

	/**
	 * Returns the value of the '<em><b>Heigth</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Heigth</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Heigth</em>' attribute.
	 * @see #setHeigth(String)
	 * @see gama.GamaPackage#getESpecies_Heigth()
	 * @model
	 * @generated
	 */
	String getHeigth();

	/**
	 * Sets the value of the '{@link gama.ESpecies#getHeigth <em>Heigth</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Heigth</em>' attribute.
	 * @see #getHeigth()
	 * @generated
	 */
	void setHeigth(String value);

	/**
	 * Returns the value of the '<em><b>Radius</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Radius</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Radius</em>' attribute.
	 * @see #setRadius(String)
	 * @see gama.GamaPackage#getESpecies_Radius()
	 * @model
	 * @generated
	 */
	String getRadius();

	/**
	 * Sets the value of the '{@link gama.ESpecies#getRadius <em>Radius</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Radius</em>' attribute.
	 * @see #getRadius()
	 * @generated
	 */
	void setRadius(String value);

	/**
	 * Returns the value of the '<em><b>Micro Species Links</b></em>' reference list.
	 * The list contents are of type {@link gama.ESubSpeciesLink}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Micro Species Links</em>' reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Micro Species Links</em>' reference list.
	 * @see gama.GamaPackage#getESpecies_MicroSpeciesLinks()
	 * @model
	 * @generated
	 */
	EList<ESubSpeciesLink> getMicroSpeciesLinks();

	/**
	 * Returns the value of the '<em><b>Macro Species Links</b></em>' reference list.
	 * The list contents are of type {@link gama.ESubSpeciesLink}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Macro Species Links</em>' reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Macro Species Links</em>' reference list.
	 * @see gama.GamaPackage#getESpecies_MacroSpeciesLinks()
	 * @model
	 * @generated
	 */
	EList<ESubSpeciesLink> getMacroSpeciesLinks();

} // ESpecies
