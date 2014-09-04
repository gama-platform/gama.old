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
 *   <li>{@link gama.ESpecies#getSkills <em>Skills</em>}</li>
 *   <li>{@link gama.ESpecies#getTopology <em>Topology</em>}</li>
 *   <li>{@link gama.ESpecies#getInheritsFrom <em>Inherits From</em>}</li>
 *   <li>{@link gama.ESpecies#getTorusType <em>Torus Type</em>}</li>
 *   <li>{@link gama.ESpecies#getShapeType <em>Shape Type</em>}</li>
 *   <li>{@link gama.ESpecies#getLocationType <em>Location Type</em>}</li>
 *   <li>{@link gama.ESpecies#getPoints <em>Points</em>}</li>
 *   <li>{@link gama.ESpecies#getExpressionShape <em>Expression Shape</em>}</li>
 *   <li>{@link gama.ESpecies#getExpressionLoc <em>Expression Loc</em>}</li>
 *   <li>{@link gama.ESpecies#getExpressionTorus <em>Expression Torus</em>}</li>
 *   <li>{@link gama.ESpecies#getShapeFunction <em>Shape Function</em>}</li>
 *   <li>{@link gama.ESpecies#getShapeUpdate <em>Shape Update</em>}</li>
 *   <li>{@link gama.ESpecies#getShapeIsFunction <em>Shape Is Function</em>}</li>
 *   <li>{@link gama.ESpecies#getLocationIsFunction <em>Location Is Function</em>}</li>
 *   <li>{@link gama.ESpecies#getLocationFunction <em>Location Function</em>}</li>
 *   <li>{@link gama.ESpecies#getLocationUpdate <em>Location Update</em>}</li>
 *   <li>{@link gama.ESpecies#getInit <em>Init</em>}</li>
 *   <li>{@link gama.ESpecies#getInheritingLinks <em>Inheriting Links</em>}</li>
 *   <li>{@link gama.ESpecies#getSchedules <em>Schedules</em>}</li>
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
	 * If the meaning of the '<em>Variables</em>' containment reference list isn't clear,
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

	/**
	 * Returns the value of the '<em><b>Skills</b></em>' attribute list.
	 * The list contents are of type {@link java.lang.String}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Skills</em>' attribute list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Skills</em>' attribute list.
	 * @see gama.GamaPackage#getESpecies_Skills()
	 * @model
	 * @generated
	 */
	EList<String> getSkills();

	/**
	 * Returns the value of the '<em><b>Topology</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Topology</em>' reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Topology</em>' reference.
	 * @see #setTopology(ETopology)
	 * @see gama.GamaPackage#getESpecies_Topology()
	 * @model
	 * @generated
	 */
	ETopology getTopology();

	/**
	 * Sets the value of the '{@link gama.ESpecies#getTopology <em>Topology</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Topology</em>' reference.
	 * @see #getTopology()
	 * @generated
	 */
	void setTopology(ETopology value);

	/**
	 * Returns the value of the '<em><b>Inherits From</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Inherits From</em>' reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Inherits From</em>' reference.
	 * @see #setInheritsFrom(ESpecies)
	 * @see gama.GamaPackage#getESpecies_InheritsFrom()
	 * @model
	 * @generated
	 */
	ESpecies getInheritsFrom();

	/**
	 * Sets the value of the '{@link gama.ESpecies#getInheritsFrom <em>Inherits From</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Inherits From</em>' reference.
	 * @see #getInheritsFrom()
	 * @generated
	 */
	void setInheritsFrom(ESpecies value);

	/**
	 * Returns the value of the '<em><b>Torus Type</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Torus Type</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Torus Type</em>' attribute.
	 * @see #setTorusType(String)
	 * @see gama.GamaPackage#getESpecies_TorusType()
	 * @model
	 * @generated
	 */
	String getTorusType();

	/**
	 * Sets the value of the '{@link gama.ESpecies#getTorusType <em>Torus Type</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Torus Type</em>' attribute.
	 * @see #getTorusType()
	 * @generated
	 */
	void setTorusType(String value);

	/**
	 * Returns the value of the '<em><b>Shape Type</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Shape Type</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Shape Type</em>' attribute.
	 * @see #setShapeType(String)
	 * @see gama.GamaPackage#getESpecies_ShapeType()
	 * @model
	 * @generated
	 */
	String getShapeType();

	/**
	 * Sets the value of the '{@link gama.ESpecies#getShapeType <em>Shape Type</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Shape Type</em>' attribute.
	 * @see #getShapeType()
	 * @generated
	 */
	void setShapeType(String value);

	/**
	 * Returns the value of the '<em><b>Location Type</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Location Type</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Location Type</em>' attribute.
	 * @see #setLocationType(String)
	 * @see gama.GamaPackage#getESpecies_LocationType()
	 * @model
	 * @generated
	 */
	String getLocationType();

	/**
	 * Sets the value of the '{@link gama.ESpecies#getLocationType <em>Location Type</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Location Type</em>' attribute.
	 * @see #getLocationType()
	 * @generated
	 */
	void setLocationType(String value);

	/**
	 * Returns the value of the '<em><b>Points</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Points</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Points</em>' attribute.
	 * @see #setPoints(String)
	 * @see gama.GamaPackage#getESpecies_Points()
	 * @model
	 * @generated
	 */
	String getPoints();

	/**
	 * Sets the value of the '{@link gama.ESpecies#getPoints <em>Points</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Points</em>' attribute.
	 * @see #getPoints()
	 * @generated
	 */
	void setPoints(String value);

	/**
	 * Returns the value of the '<em><b>Expression Shape</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Expression Shape</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Expression Shape</em>' attribute.
	 * @see #setExpressionShape(String)
	 * @see gama.GamaPackage#getESpecies_ExpressionShape()
	 * @model
	 * @generated
	 */
	String getExpressionShape();

	/**
	 * Sets the value of the '{@link gama.ESpecies#getExpressionShape <em>Expression Shape</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Expression Shape</em>' attribute.
	 * @see #getExpressionShape()
	 * @generated
	 */
	void setExpressionShape(String value);

	/**
	 * Returns the value of the '<em><b>Expression Loc</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Expression Loc</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Expression Loc</em>' attribute.
	 * @see #setExpressionLoc(String)
	 * @see gama.GamaPackage#getESpecies_ExpressionLoc()
	 * @model
	 * @generated
	 */
	String getExpressionLoc();

	/**
	 * Sets the value of the '{@link gama.ESpecies#getExpressionLoc <em>Expression Loc</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Expression Loc</em>' attribute.
	 * @see #getExpressionLoc()
	 * @generated
	 */
	void setExpressionLoc(String value);

	/**
	 * Returns the value of the '<em><b>Expression Torus</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Expression Torus</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Expression Torus</em>' attribute.
	 * @see #setExpressionTorus(String)
	 * @see gama.GamaPackage#getESpecies_ExpressionTorus()
	 * @model
	 * @generated
	 */
	String getExpressionTorus();

	/**
	 * Sets the value of the '{@link gama.ESpecies#getExpressionTorus <em>Expression Torus</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Expression Torus</em>' attribute.
	 * @see #getExpressionTorus()
	 * @generated
	 */
	void setExpressionTorus(String value);

	/**
	 * Returns the value of the '<em><b>Shape Function</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Shape Function</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Shape Function</em>' attribute.
	 * @see #setShapeFunction(String)
	 * @see gama.GamaPackage#getESpecies_ShapeFunction()
	 * @model
	 * @generated
	 */
	String getShapeFunction();

	/**
	 * Sets the value of the '{@link gama.ESpecies#getShapeFunction <em>Shape Function</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Shape Function</em>' attribute.
	 * @see #getShapeFunction()
	 * @generated
	 */
	void setShapeFunction(String value);

	/**
	 * Returns the value of the '<em><b>Shape Update</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Shape Update</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Shape Update</em>' attribute.
	 * @see #setShapeUpdate(String)
	 * @see gama.GamaPackage#getESpecies_ShapeUpdate()
	 * @model
	 * @generated
	 */
	String getShapeUpdate();

	/**
	 * Sets the value of the '{@link gama.ESpecies#getShapeUpdate <em>Shape Update</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Shape Update</em>' attribute.
	 * @see #getShapeUpdate()
	 * @generated
	 */
	void setShapeUpdate(String value);

	/**
	 * Returns the value of the '<em><b>Shape Is Function</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Shape Is Function</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Shape Is Function</em>' attribute.
	 * @see #setShapeIsFunction(Boolean)
	 * @see gama.GamaPackage#getESpecies_ShapeIsFunction()
	 * @model
	 * @generated
	 */
	Boolean getShapeIsFunction();

	/**
	 * Sets the value of the '{@link gama.ESpecies#getShapeIsFunction <em>Shape Is Function</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Shape Is Function</em>' attribute.
	 * @see #getShapeIsFunction()
	 * @generated
	 */
	void setShapeIsFunction(Boolean value);

	/**
	 * Returns the value of the '<em><b>Location Is Function</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Location Is Function</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Location Is Function</em>' attribute.
	 * @see #setLocationIsFunction(Boolean)
	 * @see gama.GamaPackage#getESpecies_LocationIsFunction()
	 * @model
	 * @generated
	 */
	Boolean getLocationIsFunction();

	/**
	 * Sets the value of the '{@link gama.ESpecies#getLocationIsFunction <em>Location Is Function</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Location Is Function</em>' attribute.
	 * @see #getLocationIsFunction()
	 * @generated
	 */
	void setLocationIsFunction(Boolean value);

	/**
	 * Returns the value of the '<em><b>Location Function</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Location Function</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Location Function</em>' attribute.
	 * @see #setLocationFunction(String)
	 * @see gama.GamaPackage#getESpecies_LocationFunction()
	 * @model
	 * @generated
	 */
	String getLocationFunction();

	/**
	 * Sets the value of the '{@link gama.ESpecies#getLocationFunction <em>Location Function</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Location Function</em>' attribute.
	 * @see #getLocationFunction()
	 * @generated
	 */
	void setLocationFunction(String value);

	/**
	 * Returns the value of the '<em><b>Location Update</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Location Update</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Location Update</em>' attribute.
	 * @see #setLocationUpdate(String)
	 * @see gama.GamaPackage#getESpecies_LocationUpdate()
	 * @model
	 * @generated
	 */
	String getLocationUpdate();

	/**
	 * Sets the value of the '{@link gama.ESpecies#getLocationUpdate <em>Location Update</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Location Update</em>' attribute.
	 * @see #getLocationUpdate()
	 * @generated
	 */
	void setLocationUpdate(String value);

	/**
	 * Returns the value of the '<em><b>Init</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Init</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Init</em>' attribute.
	 * @see #setInit(String)
	 * @see gama.GamaPackage#getESpecies_Init()
	 * @model
	 * @generated
	 */
	String getInit();

	/**
	 * Sets the value of the '{@link gama.ESpecies#getInit <em>Init</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Init</em>' attribute.
	 * @see #getInit()
	 * @generated
	 */
	void setInit(String value);

	/**
	 * Returns the value of the '<em><b>Inheriting Links</b></em>' reference list.
	 * The list contents are of type {@link gama.EInheritLink}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Inheriting Links</em>' reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Inheriting Links</em>' reference list.
	 * @see gama.GamaPackage#getESpecies_InheritingLinks()
	 * @model
	 * @generated
	 */
	EList<EInheritLink> getInheritingLinks();

	/**
	 * Returns the value of the '<em><b>Schedules</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Schedules</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Schedules</em>' attribute.
	 * @see #setSchedules(String)
	 * @see gama.GamaPackage#getESpecies_Schedules()
	 * @model
	 * @generated
	 */
	String getSchedules();

	/**
	 * Sets the value of the '{@link gama.ESpecies#getSchedules <em>Schedules</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Schedules</em>' attribute.
	 * @see #getSchedules()
	 * @generated
	 */
	void setSchedules(String value);

} // ESpecies
