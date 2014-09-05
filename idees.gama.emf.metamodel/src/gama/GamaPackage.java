/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package gama;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

/**
 * <!-- begin-user-doc -->
 * The <b>Package</b> for the model.
 * It contains accessors for the meta objects to represent
 * <ul>
 *   <li>each class,</li>
 *   <li>each feature of each class,</li>
 *   <li>each enum,</li>
 *   <li>and each data type</li>
 * </ul>
 * <!-- end-user-doc -->
 * @see gama.GamaFactory
 * @model kind="package"
 * @generated
 */
public interface GamaPackage extends EPackage {
	/**
	 * The package name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNAME = "gama";

	/**
	 * The package namespace URI.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_URI = "http://gama/1.0";

	/**
	 * The package namespace name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_PREFIX = "gama";

	/**
	 * The singleton instance of the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	GamaPackage eINSTANCE = gama.impl.GamaPackageImpl.init();

	/**
	 * The meta object id for the '{@link gama.impl.EGamaModelImpl <em>EGama Model</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see gama.impl.EGamaModelImpl
	 * @see gama.impl.GamaPackageImpl#getEGamaModel()
	 * @generated
	 */
	int EGAMA_MODEL = 0;

	/**
	 * The feature id for the '<em><b>Objects</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EGAMA_MODEL__OBJECTS = 0;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EGAMA_MODEL__NAME = 1;

	/**
	 * The feature id for the '<em><b>Links</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EGAMA_MODEL__LINKS = 2;

	/**
	 * The number of structural features of the '<em>EGama Model</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EGAMA_MODEL_FEATURE_COUNT = 3;

	/**
	 * The meta object id for the '{@link gama.impl.EGamaObjectImpl <em>EGama Object</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see gama.impl.EGamaObjectImpl
	 * @see gama.impl.GamaPackageImpl#getEGamaObject()
	 * @generated
	 */
	int EGAMA_OBJECT = 1;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EGAMA_OBJECT__NAME = 0;

	/**
	 * The feature id for the '<em><b>Model</b></em>' container reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EGAMA_OBJECT__MODEL = 1;

	/**
	 * The feature id for the '<em><b>Color Picto</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EGAMA_OBJECT__COLOR_PICTO = 2;

	/**
	 * The feature id for the '<em><b>Has Error</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EGAMA_OBJECT__HAS_ERROR = 3;

	/**
	 * The feature id for the '<em><b>Error</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EGAMA_OBJECT__ERROR = 4;

	/**
	 * The number of structural features of the '<em>EGama Object</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EGAMA_OBJECT_FEATURE_COUNT = 5;

	/**
	 * The meta object id for the '{@link gama.impl.ESpeciesImpl <em>ESpecies</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see gama.impl.ESpeciesImpl
	 * @see gama.impl.GamaPackageImpl#getESpecies()
	 * @generated
	 */
	int ESPECIES = 2;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ESPECIES__NAME = EGAMA_OBJECT__NAME;

	/**
	 * The feature id for the '<em><b>Model</b></em>' container reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ESPECIES__MODEL = EGAMA_OBJECT__MODEL;

	/**
	 * The feature id for the '<em><b>Color Picto</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ESPECIES__COLOR_PICTO = EGAMA_OBJECT__COLOR_PICTO;

	/**
	 * The feature id for the '<em><b>Has Error</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ESPECIES__HAS_ERROR = EGAMA_OBJECT__HAS_ERROR;

	/**
	 * The feature id for the '<em><b>Error</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ESPECIES__ERROR = EGAMA_OBJECT__ERROR;

	/**
	 * The feature id for the '<em><b>Variables</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ESPECIES__VARIABLES = EGAMA_OBJECT_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Reflex List</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ESPECIES__REFLEX_LIST = EGAMA_OBJECT_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Torus</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ESPECIES__TORUS = EGAMA_OBJECT_FEATURE_COUNT + 2;

	/**
	 * The feature id for the '<em><b>Experiment Links</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ESPECIES__EXPERIMENT_LINKS = EGAMA_OBJECT_FEATURE_COUNT + 3;

	/**
	 * The feature id for the '<em><b>Aspect Links</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ESPECIES__ASPECT_LINKS = EGAMA_OBJECT_FEATURE_COUNT + 4;

	/**
	 * The feature id for the '<em><b>Action Links</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ESPECIES__ACTION_LINKS = EGAMA_OBJECT_FEATURE_COUNT + 5;

	/**
	 * The feature id for the '<em><b>Reflex Links</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ESPECIES__REFLEX_LINKS = EGAMA_OBJECT_FEATURE_COUNT + 6;

	/**
	 * The feature id for the '<em><b>Shape</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ESPECIES__SHAPE = EGAMA_OBJECT_FEATURE_COUNT + 7;

	/**
	 * The feature id for the '<em><b>Location</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ESPECIES__LOCATION = EGAMA_OBJECT_FEATURE_COUNT + 8;

	/**
	 * The feature id for the '<em><b>Size</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ESPECIES__SIZE = EGAMA_OBJECT_FEATURE_COUNT + 9;

	/**
	 * The feature id for the '<em><b>Width</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ESPECIES__WIDTH = EGAMA_OBJECT_FEATURE_COUNT + 10;

	/**
	 * The feature id for the '<em><b>Heigth</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ESPECIES__HEIGTH = EGAMA_OBJECT_FEATURE_COUNT + 11;

	/**
	 * The feature id for the '<em><b>Radius</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ESPECIES__RADIUS = EGAMA_OBJECT_FEATURE_COUNT + 12;

	/**
	 * The feature id for the '<em><b>Micro Species Links</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ESPECIES__MICRO_SPECIES_LINKS = EGAMA_OBJECT_FEATURE_COUNT + 13;

	/**
	 * The feature id for the '<em><b>Macro Species Links</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ESPECIES__MACRO_SPECIES_LINKS = EGAMA_OBJECT_FEATURE_COUNT + 14;

	/**
	 * The feature id for the '<em><b>Skills</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ESPECIES__SKILLS = EGAMA_OBJECT_FEATURE_COUNT + 15;

	/**
	 * The feature id for the '<em><b>Topology</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ESPECIES__TOPOLOGY = EGAMA_OBJECT_FEATURE_COUNT + 16;

	/**
	 * The feature id for the '<em><b>Inherits From</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ESPECIES__INHERITS_FROM = EGAMA_OBJECT_FEATURE_COUNT + 17;

	/**
	 * The feature id for the '<em><b>Torus Type</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ESPECIES__TORUS_TYPE = EGAMA_OBJECT_FEATURE_COUNT + 18;

	/**
	 * The feature id for the '<em><b>Shape Type</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ESPECIES__SHAPE_TYPE = EGAMA_OBJECT_FEATURE_COUNT + 19;

	/**
	 * The feature id for the '<em><b>Location Type</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ESPECIES__LOCATION_TYPE = EGAMA_OBJECT_FEATURE_COUNT + 20;

	/**
	 * The feature id for the '<em><b>Points</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ESPECIES__POINTS = EGAMA_OBJECT_FEATURE_COUNT + 21;

	/**
	 * The feature id for the '<em><b>Expression Shape</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ESPECIES__EXPRESSION_SHAPE = EGAMA_OBJECT_FEATURE_COUNT + 22;

	/**
	 * The feature id for the '<em><b>Expression Loc</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ESPECIES__EXPRESSION_LOC = EGAMA_OBJECT_FEATURE_COUNT + 23;

	/**
	 * The feature id for the '<em><b>Expression Torus</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ESPECIES__EXPRESSION_TORUS = EGAMA_OBJECT_FEATURE_COUNT + 24;

	/**
	 * The feature id for the '<em><b>Shape Function</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ESPECIES__SHAPE_FUNCTION = EGAMA_OBJECT_FEATURE_COUNT + 25;

	/**
	 * The feature id for the '<em><b>Shape Update</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ESPECIES__SHAPE_UPDATE = EGAMA_OBJECT_FEATURE_COUNT + 26;

	/**
	 * The feature id for the '<em><b>Shape Is Function</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ESPECIES__SHAPE_IS_FUNCTION = EGAMA_OBJECT_FEATURE_COUNT + 27;

	/**
	 * The feature id for the '<em><b>Location Is Function</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ESPECIES__LOCATION_IS_FUNCTION = EGAMA_OBJECT_FEATURE_COUNT + 28;

	/**
	 * The feature id for the '<em><b>Location Function</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ESPECIES__LOCATION_FUNCTION = EGAMA_OBJECT_FEATURE_COUNT + 29;

	/**
	 * The feature id for the '<em><b>Location Update</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ESPECIES__LOCATION_UPDATE = EGAMA_OBJECT_FEATURE_COUNT + 30;

	/**
	 * The feature id for the '<em><b>Init</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ESPECIES__INIT = EGAMA_OBJECT_FEATURE_COUNT + 31;

	/**
	 * The feature id for the '<em><b>Inheriting Links</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ESPECIES__INHERITING_LINKS = EGAMA_OBJECT_FEATURE_COUNT + 32;

	/**
	 * The feature id for the '<em><b>Schedules</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ESPECIES__SCHEDULES = EGAMA_OBJECT_FEATURE_COUNT + 33;

	/**
	 * The number of structural features of the '<em>ESpecies</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ESPECIES_FEATURE_COUNT = EGAMA_OBJECT_FEATURE_COUNT + 34;

	/**
	 * The meta object id for the '{@link gama.impl.EActionImpl <em>EAction</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see gama.impl.EActionImpl
	 * @see gama.impl.GamaPackageImpl#getEAction()
	 * @generated
	 */
	int EACTION = 3;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EACTION__NAME = EGAMA_OBJECT__NAME;

	/**
	 * The feature id for the '<em><b>Model</b></em>' container reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EACTION__MODEL = EGAMA_OBJECT__MODEL;

	/**
	 * The feature id for the '<em><b>Color Picto</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EACTION__COLOR_PICTO = EGAMA_OBJECT__COLOR_PICTO;

	/**
	 * The feature id for the '<em><b>Has Error</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EACTION__HAS_ERROR = EGAMA_OBJECT__HAS_ERROR;

	/**
	 * The feature id for the '<em><b>Error</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EACTION__ERROR = EGAMA_OBJECT__ERROR;

	/**
	 * The feature id for the '<em><b>Gaml Code</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EACTION__GAML_CODE = EGAMA_OBJECT_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Action Links</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EACTION__ACTION_LINKS = EGAMA_OBJECT_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Variables</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EACTION__VARIABLES = EGAMA_OBJECT_FEATURE_COUNT + 2;

	/**
	 * The feature id for the '<em><b>Return Type</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EACTION__RETURN_TYPE = EGAMA_OBJECT_FEATURE_COUNT + 3;

	/**
	 * The number of structural features of the '<em>EAction</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EACTION_FEATURE_COUNT = EGAMA_OBJECT_FEATURE_COUNT + 4;

	/**
	 * The meta object id for the '{@link gama.impl.EAspectImpl <em>EAspect</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see gama.impl.EAspectImpl
	 * @see gama.impl.GamaPackageImpl#getEAspect()
	 * @generated
	 */
	int EASPECT = 4;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EASPECT__NAME = EGAMA_OBJECT__NAME;

	/**
	 * The feature id for the '<em><b>Model</b></em>' container reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EASPECT__MODEL = EGAMA_OBJECT__MODEL;

	/**
	 * The feature id for the '<em><b>Color Picto</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EASPECT__COLOR_PICTO = EGAMA_OBJECT__COLOR_PICTO;

	/**
	 * The feature id for the '<em><b>Has Error</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EASPECT__HAS_ERROR = EGAMA_OBJECT__HAS_ERROR;

	/**
	 * The feature id for the '<em><b>Error</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EASPECT__ERROR = EGAMA_OBJECT__ERROR;

	/**
	 * The feature id for the '<em><b>Gaml Code</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EASPECT__GAML_CODE = EGAMA_OBJECT_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Aspect Links</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EASPECT__ASPECT_LINKS = EGAMA_OBJECT_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Layers</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EASPECT__LAYERS = EGAMA_OBJECT_FEATURE_COUNT + 2;

	/**
	 * The number of structural features of the '<em>EAspect</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EASPECT_FEATURE_COUNT = EGAMA_OBJECT_FEATURE_COUNT + 3;

	/**
	 * The meta object id for the '{@link gama.impl.EReflexImpl <em>EReflex</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see gama.impl.EReflexImpl
	 * @see gama.impl.GamaPackageImpl#getEReflex()
	 * @generated
	 */
	int EREFLEX = 5;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EREFLEX__NAME = EGAMA_OBJECT__NAME;

	/**
	 * The feature id for the '<em><b>Model</b></em>' container reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EREFLEX__MODEL = EGAMA_OBJECT__MODEL;

	/**
	 * The feature id for the '<em><b>Color Picto</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EREFLEX__COLOR_PICTO = EGAMA_OBJECT__COLOR_PICTO;

	/**
	 * The feature id for the '<em><b>Has Error</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EREFLEX__HAS_ERROR = EGAMA_OBJECT__HAS_ERROR;

	/**
	 * The feature id for the '<em><b>Error</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EREFLEX__ERROR = EGAMA_OBJECT__ERROR;

	/**
	 * The feature id for the '<em><b>Gaml Code</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EREFLEX__GAML_CODE = EGAMA_OBJECT_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Condition</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EREFLEX__CONDITION = EGAMA_OBJECT_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Reflex Links</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EREFLEX__REFLEX_LINKS = EGAMA_OBJECT_FEATURE_COUNT + 2;

	/**
	 * The number of structural features of the '<em>EReflex</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EREFLEX_FEATURE_COUNT = EGAMA_OBJECT_FEATURE_COUNT + 3;

	/**
	 * The meta object id for the '{@link gama.impl.EExperimentImpl <em>EExperiment</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see gama.impl.EExperimentImpl
	 * @see gama.impl.GamaPackageImpl#getEExperiment()
	 * @generated
	 */
	int EEXPERIMENT = 6;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EEXPERIMENT__NAME = EGAMA_OBJECT__NAME;

	/**
	 * The feature id for the '<em><b>Model</b></em>' container reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EEXPERIMENT__MODEL = EGAMA_OBJECT__MODEL;

	/**
	 * The feature id for the '<em><b>Color Picto</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EEXPERIMENT__COLOR_PICTO = EGAMA_OBJECT__COLOR_PICTO;

	/**
	 * The feature id for the '<em><b>Has Error</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EEXPERIMENT__HAS_ERROR = EGAMA_OBJECT__HAS_ERROR;

	/**
	 * The feature id for the '<em><b>Error</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EEXPERIMENT__ERROR = EGAMA_OBJECT__ERROR;

	/**
	 * The feature id for the '<em><b>Experiment Link</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EEXPERIMENT__EXPERIMENT_LINK = EGAMA_OBJECT_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Display Links</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EEXPERIMENT__DISPLAY_LINKS = EGAMA_OBJECT_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Parameters</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EEXPERIMENT__PARAMETERS = EGAMA_OBJECT_FEATURE_COUNT + 2;

	/**
	 * The feature id for the '<em><b>Monitors</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EEXPERIMENT__MONITORS = EGAMA_OBJECT_FEATURE_COUNT + 3;

	/**
	 * The number of structural features of the '<em>EExperiment</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EEXPERIMENT_FEATURE_COUNT = EGAMA_OBJECT_FEATURE_COUNT + 4;

	/**
	 * The meta object id for the '{@link gama.impl.EGUIExperimentImpl <em>EGUI Experiment</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see gama.impl.EGUIExperimentImpl
	 * @see gama.impl.GamaPackageImpl#getEGUIExperiment()
	 * @generated
	 */
	int EGUI_EXPERIMENT = 7;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EGUI_EXPERIMENT__NAME = EEXPERIMENT__NAME;

	/**
	 * The feature id for the '<em><b>Model</b></em>' container reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EGUI_EXPERIMENT__MODEL = EEXPERIMENT__MODEL;

	/**
	 * The feature id for the '<em><b>Color Picto</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EGUI_EXPERIMENT__COLOR_PICTO = EEXPERIMENT__COLOR_PICTO;

	/**
	 * The feature id for the '<em><b>Has Error</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EGUI_EXPERIMENT__HAS_ERROR = EEXPERIMENT__HAS_ERROR;

	/**
	 * The feature id for the '<em><b>Error</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EGUI_EXPERIMENT__ERROR = EEXPERIMENT__ERROR;

	/**
	 * The feature id for the '<em><b>Experiment Link</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EGUI_EXPERIMENT__EXPERIMENT_LINK = EEXPERIMENT__EXPERIMENT_LINK;

	/**
	 * The feature id for the '<em><b>Display Links</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EGUI_EXPERIMENT__DISPLAY_LINKS = EEXPERIMENT__DISPLAY_LINKS;

	/**
	 * The feature id for the '<em><b>Parameters</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EGUI_EXPERIMENT__PARAMETERS = EEXPERIMENT__PARAMETERS;

	/**
	 * The feature id for the '<em><b>Monitors</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EGUI_EXPERIMENT__MONITORS = EEXPERIMENT__MONITORS;

	/**
	 * The number of structural features of the '<em>EGUI Experiment</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EGUI_EXPERIMENT_FEATURE_COUNT = EEXPERIMENT_FEATURE_COUNT + 0;

	/**
	 * The meta object id for the '{@link gama.impl.EBatchExperimentImpl <em>EBatch Experiment</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see gama.impl.EBatchExperimentImpl
	 * @see gama.impl.GamaPackageImpl#getEBatchExperiment()
	 * @generated
	 */
	int EBATCH_EXPERIMENT = 8;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EBATCH_EXPERIMENT__NAME = EEXPERIMENT__NAME;

	/**
	 * The feature id for the '<em><b>Model</b></em>' container reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EBATCH_EXPERIMENT__MODEL = EEXPERIMENT__MODEL;

	/**
	 * The feature id for the '<em><b>Color Picto</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EBATCH_EXPERIMENT__COLOR_PICTO = EEXPERIMENT__COLOR_PICTO;

	/**
	 * The feature id for the '<em><b>Has Error</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EBATCH_EXPERIMENT__HAS_ERROR = EEXPERIMENT__HAS_ERROR;

	/**
	 * The feature id for the '<em><b>Error</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EBATCH_EXPERIMENT__ERROR = EEXPERIMENT__ERROR;

	/**
	 * The feature id for the '<em><b>Experiment Link</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EBATCH_EXPERIMENT__EXPERIMENT_LINK = EEXPERIMENT__EXPERIMENT_LINK;

	/**
	 * The feature id for the '<em><b>Display Links</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EBATCH_EXPERIMENT__DISPLAY_LINKS = EEXPERIMENT__DISPLAY_LINKS;

	/**
	 * The feature id for the '<em><b>Parameters</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EBATCH_EXPERIMENT__PARAMETERS = EEXPERIMENT__PARAMETERS;

	/**
	 * The feature id for the '<em><b>Monitors</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EBATCH_EXPERIMENT__MONITORS = EEXPERIMENT__MONITORS;

	/**
	 * The number of structural features of the '<em>EBatch Experiment</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EBATCH_EXPERIMENT_FEATURE_COUNT = EEXPERIMENT_FEATURE_COUNT + 0;

	/**
	 * The meta object id for the '{@link gama.impl.EGamaLinkImpl <em>EGama Link</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see gama.impl.EGamaLinkImpl
	 * @see gama.impl.GamaPackageImpl#getEGamaLink()
	 * @generated
	 */
	int EGAMA_LINK = 9;

	/**
	 * The feature id for the '<em><b>Target</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EGAMA_LINK__TARGET = 0;

	/**
	 * The feature id for the '<em><b>Source</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EGAMA_LINK__SOURCE = 1;

	/**
	 * The feature id for the '<em><b>Model</b></em>' container reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EGAMA_LINK__MODEL = 2;

	/**
	 * The number of structural features of the '<em>EGama Link</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EGAMA_LINK_FEATURE_COUNT = 3;

	/**
	 * The meta object id for the '{@link gama.impl.ESubSpeciesLinkImpl <em>ESub Species Link</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see gama.impl.ESubSpeciesLinkImpl
	 * @see gama.impl.GamaPackageImpl#getESubSpeciesLink()
	 * @generated
	 */
	int ESUB_SPECIES_LINK = 10;

	/**
	 * The feature id for the '<em><b>Target</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ESUB_SPECIES_LINK__TARGET = EGAMA_LINK__TARGET;

	/**
	 * The feature id for the '<em><b>Source</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ESUB_SPECIES_LINK__SOURCE = EGAMA_LINK__SOURCE;

	/**
	 * The feature id for the '<em><b>Model</b></em>' container reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ESUB_SPECIES_LINK__MODEL = EGAMA_LINK__MODEL;

	/**
	 * The feature id for the '<em><b>Macro</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ESUB_SPECIES_LINK__MACRO = EGAMA_LINK_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Micro</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ESUB_SPECIES_LINK__MICRO = EGAMA_LINK_FEATURE_COUNT + 1;

	/**
	 * The number of structural features of the '<em>ESub Species Link</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ESUB_SPECIES_LINK_FEATURE_COUNT = EGAMA_LINK_FEATURE_COUNT + 2;

	/**
	 * The meta object id for the '{@link gama.impl.EActionLinkImpl <em>EAction Link</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see gama.impl.EActionLinkImpl
	 * @see gama.impl.GamaPackageImpl#getEActionLink()
	 * @generated
	 */
	int EACTION_LINK = 11;

	/**
	 * The feature id for the '<em><b>Target</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EACTION_LINK__TARGET = EGAMA_LINK__TARGET;

	/**
	 * The feature id for the '<em><b>Source</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EACTION_LINK__SOURCE = EGAMA_LINK__SOURCE;

	/**
	 * The feature id for the '<em><b>Model</b></em>' container reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EACTION_LINK__MODEL = EGAMA_LINK__MODEL;

	/**
	 * The feature id for the '<em><b>Action</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EACTION_LINK__ACTION = EGAMA_LINK_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Species</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EACTION_LINK__SPECIES = EGAMA_LINK_FEATURE_COUNT + 1;

	/**
	 * The number of structural features of the '<em>EAction Link</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EACTION_LINK_FEATURE_COUNT = EGAMA_LINK_FEATURE_COUNT + 2;

	/**
	 * The meta object id for the '{@link gama.impl.EAspectLinkImpl <em>EAspect Link</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see gama.impl.EAspectLinkImpl
	 * @see gama.impl.GamaPackageImpl#getEAspectLink()
	 * @generated
	 */
	int EASPECT_LINK = 12;

	/**
	 * The feature id for the '<em><b>Target</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EASPECT_LINK__TARGET = EGAMA_LINK__TARGET;

	/**
	 * The feature id for the '<em><b>Source</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EASPECT_LINK__SOURCE = EGAMA_LINK__SOURCE;

	/**
	 * The feature id for the '<em><b>Model</b></em>' container reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EASPECT_LINK__MODEL = EGAMA_LINK__MODEL;

	/**
	 * The feature id for the '<em><b>Aspect</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EASPECT_LINK__ASPECT = EGAMA_LINK_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Species</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EASPECT_LINK__SPECIES = EGAMA_LINK_FEATURE_COUNT + 1;

	/**
	 * The number of structural features of the '<em>EAspect Link</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EASPECT_LINK_FEATURE_COUNT = EGAMA_LINK_FEATURE_COUNT + 2;

	/**
	 * The meta object id for the '{@link gama.impl.EReflexLinkImpl <em>EReflex Link</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see gama.impl.EReflexLinkImpl
	 * @see gama.impl.GamaPackageImpl#getEReflexLink()
	 * @generated
	 */
	int EREFLEX_LINK = 13;

	/**
	 * The feature id for the '<em><b>Target</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EREFLEX_LINK__TARGET = EGAMA_LINK__TARGET;

	/**
	 * The feature id for the '<em><b>Source</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EREFLEX_LINK__SOURCE = EGAMA_LINK__SOURCE;

	/**
	 * The feature id for the '<em><b>Model</b></em>' container reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EREFLEX_LINK__MODEL = EGAMA_LINK__MODEL;

	/**
	 * The feature id for the '<em><b>Reflex</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EREFLEX_LINK__REFLEX = EGAMA_LINK_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Species</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EREFLEX_LINK__SPECIES = EGAMA_LINK_FEATURE_COUNT + 1;

	/**
	 * The number of structural features of the '<em>EReflex Link</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EREFLEX_LINK_FEATURE_COUNT = EGAMA_LINK_FEATURE_COUNT + 2;

	/**
	 * The meta object id for the '{@link gama.impl.EDisplayLinkImpl <em>EDisplay Link</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see gama.impl.EDisplayLinkImpl
	 * @see gama.impl.GamaPackageImpl#getEDisplayLink()
	 * @generated
	 */
	int EDISPLAY_LINK = 14;

	/**
	 * The feature id for the '<em><b>Target</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EDISPLAY_LINK__TARGET = EGAMA_LINK__TARGET;

	/**
	 * The feature id for the '<em><b>Source</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EDISPLAY_LINK__SOURCE = EGAMA_LINK__SOURCE;

	/**
	 * The feature id for the '<em><b>Model</b></em>' container reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EDISPLAY_LINK__MODEL = EGAMA_LINK__MODEL;

	/**
	 * The feature id for the '<em><b>Experiment</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EDISPLAY_LINK__EXPERIMENT = EGAMA_LINK_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Display</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EDISPLAY_LINK__DISPLAY = EGAMA_LINK_FEATURE_COUNT + 1;

	/**
	 * The number of structural features of the '<em>EDisplay Link</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EDISPLAY_LINK_FEATURE_COUNT = EGAMA_LINK_FEATURE_COUNT + 2;

	/**
	 * The meta object id for the '{@link gama.impl.EDisplayImpl <em>EDisplay</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see gama.impl.EDisplayImpl
	 * @see gama.impl.GamaPackageImpl#getEDisplay()
	 * @generated
	 */
	int EDISPLAY = 15;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EDISPLAY__NAME = EGAMA_OBJECT__NAME;

	/**
	 * The feature id for the '<em><b>Model</b></em>' container reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EDISPLAY__MODEL = EGAMA_OBJECT__MODEL;

	/**
	 * The feature id for the '<em><b>Color Picto</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EDISPLAY__COLOR_PICTO = EGAMA_OBJECT__COLOR_PICTO;

	/**
	 * The feature id for the '<em><b>Has Error</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EDISPLAY__HAS_ERROR = EGAMA_OBJECT__HAS_ERROR;

	/**
	 * The feature id for the '<em><b>Error</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EDISPLAY__ERROR = EGAMA_OBJECT__ERROR;

	/**
	 * The feature id for the '<em><b>Layers</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EDISPLAY__LAYERS = EGAMA_OBJECT_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Display Link</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EDISPLAY__DISPLAY_LINK = EGAMA_OBJECT_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Opengl</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EDISPLAY__OPENGL = EGAMA_OBJECT_FEATURE_COUNT + 2;

	/**
	 * The feature id for the '<em><b>Refresh</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EDISPLAY__REFRESH = EGAMA_OBJECT_FEATURE_COUNT + 3;

	/**
	 * The feature id for the '<em><b>Background</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EDISPLAY__BACKGROUND = EGAMA_OBJECT_FEATURE_COUNT + 4;

	/**
	 * The feature id for the '<em><b>Layer List</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EDISPLAY__LAYER_LIST = EGAMA_OBJECT_FEATURE_COUNT + 5;

	/**
	 * The feature id for the '<em><b>Color</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EDISPLAY__COLOR = EGAMA_OBJECT_FEATURE_COUNT + 6;

	/**
	 * The feature id for the '<em><b>Is Color Cst</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EDISPLAY__IS_COLOR_CST = EGAMA_OBJECT_FEATURE_COUNT + 7;

	/**
	 * The feature id for the '<em><b>Color RBG</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EDISPLAY__COLOR_RBG = EGAMA_OBJECT_FEATURE_COUNT + 8;

	/**
	 * The feature id for the '<em><b>Gaml Code</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EDISPLAY__GAML_CODE = EGAMA_OBJECT_FEATURE_COUNT + 9;

	/**
	 * The feature id for the '<em><b>Ambient Light</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EDISPLAY__AMBIENT_LIGHT = EGAMA_OBJECT_FEATURE_COUNT + 10;

	/**
	 * The feature id for the '<em><b>Draw Diffuse Light</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EDISPLAY__DRAW_DIFFUSE_LIGHT = EGAMA_OBJECT_FEATURE_COUNT + 11;

	/**
	 * The feature id for the '<em><b>Diffuse Light</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EDISPLAY__DIFFUSE_LIGHT = EGAMA_OBJECT_FEATURE_COUNT + 12;

	/**
	 * The feature id for the '<em><b>Diffuse Light Pos</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EDISPLAY__DIFFUSE_LIGHT_POS = EGAMA_OBJECT_FEATURE_COUNT + 13;

	/**
	 * The feature id for the '<em><b>ZFighting</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EDISPLAY__ZFIGHTING = EGAMA_OBJECT_FEATURE_COUNT + 14;

	/**
	 * The feature id for the '<em><b>Camera Pos</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EDISPLAY__CAMERA_POS = EGAMA_OBJECT_FEATURE_COUNT + 15;

	/**
	 * The feature id for the '<em><b>Camera Look Pos</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EDISPLAY__CAMERA_LOOK_POS = EGAMA_OBJECT_FEATURE_COUNT + 16;

	/**
	 * The feature id for the '<em><b>Camera Up Vector</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EDISPLAY__CAMERA_UP_VECTOR = EGAMA_OBJECT_FEATURE_COUNT + 17;

	/**
	 * The number of structural features of the '<em>EDisplay</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EDISPLAY_FEATURE_COUNT = EGAMA_OBJECT_FEATURE_COUNT + 18;

	/**
	 * The meta object id for the '{@link gama.impl.EVariableImpl <em>EVariable</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see gama.impl.EVariableImpl
	 * @see gama.impl.GamaPackageImpl#getEVariable()
	 * @generated
	 */
	int EVARIABLE = 16;

	/**
	 * The feature id for the '<em><b>Init</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EVARIABLE__INIT = 0;

	/**
	 * The feature id for the '<em><b>Min</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EVARIABLE__MIN = 1;

	/**
	 * The feature id for the '<em><b>Max</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EVARIABLE__MAX = 2;

	/**
	 * The feature id for the '<em><b>Update</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EVARIABLE__UPDATE = 3;

	/**
	 * The feature id for the '<em><b>Function</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EVARIABLE__FUNCTION = 4;

	/**
	 * The feature id for the '<em><b>Type</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EVARIABLE__TYPE = 5;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EVARIABLE__NAME = 6;

	/**
	 * The feature id for the '<em><b>Has Error</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EVARIABLE__HAS_ERROR = 7;

	/**
	 * The feature id for the '<em><b>Error</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EVARIABLE__ERROR = 8;

	/**
	 * The feature id for the '<em><b>Owner</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EVARIABLE__OWNER = 9;

	/**
	 * The number of structural features of the '<em>EVariable</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EVARIABLE_FEATURE_COUNT = 10;

	/**
	 * The meta object id for the '{@link gama.impl.EWorldAgentImpl <em>EWorld Agent</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see gama.impl.EWorldAgentImpl
	 * @see gama.impl.GamaPackageImpl#getEWorldAgent()
	 * @generated
	 */
	int EWORLD_AGENT = 17;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EWORLD_AGENT__NAME = ESPECIES__NAME;

	/**
	 * The feature id for the '<em><b>Model</b></em>' container reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EWORLD_AGENT__MODEL = ESPECIES__MODEL;

	/**
	 * The feature id for the '<em><b>Color Picto</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EWORLD_AGENT__COLOR_PICTO = ESPECIES__COLOR_PICTO;

	/**
	 * The feature id for the '<em><b>Has Error</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EWORLD_AGENT__HAS_ERROR = ESPECIES__HAS_ERROR;

	/**
	 * The feature id for the '<em><b>Error</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EWORLD_AGENT__ERROR = ESPECIES__ERROR;

	/**
	 * The feature id for the '<em><b>Variables</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EWORLD_AGENT__VARIABLES = ESPECIES__VARIABLES;

	/**
	 * The feature id for the '<em><b>Reflex List</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EWORLD_AGENT__REFLEX_LIST = ESPECIES__REFLEX_LIST;

	/**
	 * The feature id for the '<em><b>Torus</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EWORLD_AGENT__TORUS = ESPECIES__TORUS;

	/**
	 * The feature id for the '<em><b>Experiment Links</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EWORLD_AGENT__EXPERIMENT_LINKS = ESPECIES__EXPERIMENT_LINKS;

	/**
	 * The feature id for the '<em><b>Aspect Links</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EWORLD_AGENT__ASPECT_LINKS = ESPECIES__ASPECT_LINKS;

	/**
	 * The feature id for the '<em><b>Action Links</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EWORLD_AGENT__ACTION_LINKS = ESPECIES__ACTION_LINKS;

	/**
	 * The feature id for the '<em><b>Reflex Links</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EWORLD_AGENT__REFLEX_LINKS = ESPECIES__REFLEX_LINKS;

	/**
	 * The feature id for the '<em><b>Shape</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EWORLD_AGENT__SHAPE = ESPECIES__SHAPE;

	/**
	 * The feature id for the '<em><b>Location</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EWORLD_AGENT__LOCATION = ESPECIES__LOCATION;

	/**
	 * The feature id for the '<em><b>Size</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EWORLD_AGENT__SIZE = ESPECIES__SIZE;

	/**
	 * The feature id for the '<em><b>Width</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EWORLD_AGENT__WIDTH = ESPECIES__WIDTH;

	/**
	 * The feature id for the '<em><b>Heigth</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EWORLD_AGENT__HEIGTH = ESPECIES__HEIGTH;

	/**
	 * The feature id for the '<em><b>Radius</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EWORLD_AGENT__RADIUS = ESPECIES__RADIUS;

	/**
	 * The feature id for the '<em><b>Micro Species Links</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EWORLD_AGENT__MICRO_SPECIES_LINKS = ESPECIES__MICRO_SPECIES_LINKS;

	/**
	 * The feature id for the '<em><b>Macro Species Links</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EWORLD_AGENT__MACRO_SPECIES_LINKS = ESPECIES__MACRO_SPECIES_LINKS;

	/**
	 * The feature id for the '<em><b>Skills</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EWORLD_AGENT__SKILLS = ESPECIES__SKILLS;

	/**
	 * The feature id for the '<em><b>Topology</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EWORLD_AGENT__TOPOLOGY = ESPECIES__TOPOLOGY;

	/**
	 * The feature id for the '<em><b>Inherits From</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EWORLD_AGENT__INHERITS_FROM = ESPECIES__INHERITS_FROM;

	/**
	 * The feature id for the '<em><b>Torus Type</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EWORLD_AGENT__TORUS_TYPE = ESPECIES__TORUS_TYPE;

	/**
	 * The feature id for the '<em><b>Shape Type</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EWORLD_AGENT__SHAPE_TYPE = ESPECIES__SHAPE_TYPE;

	/**
	 * The feature id for the '<em><b>Location Type</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EWORLD_AGENT__LOCATION_TYPE = ESPECIES__LOCATION_TYPE;

	/**
	 * The feature id for the '<em><b>Points</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EWORLD_AGENT__POINTS = ESPECIES__POINTS;

	/**
	 * The feature id for the '<em><b>Expression Shape</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EWORLD_AGENT__EXPRESSION_SHAPE = ESPECIES__EXPRESSION_SHAPE;

	/**
	 * The feature id for the '<em><b>Expression Loc</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EWORLD_AGENT__EXPRESSION_LOC = ESPECIES__EXPRESSION_LOC;

	/**
	 * The feature id for the '<em><b>Expression Torus</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EWORLD_AGENT__EXPRESSION_TORUS = ESPECIES__EXPRESSION_TORUS;

	/**
	 * The feature id for the '<em><b>Shape Function</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EWORLD_AGENT__SHAPE_FUNCTION = ESPECIES__SHAPE_FUNCTION;

	/**
	 * The feature id for the '<em><b>Shape Update</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EWORLD_AGENT__SHAPE_UPDATE = ESPECIES__SHAPE_UPDATE;

	/**
	 * The feature id for the '<em><b>Shape Is Function</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EWORLD_AGENT__SHAPE_IS_FUNCTION = ESPECIES__SHAPE_IS_FUNCTION;

	/**
	 * The feature id for the '<em><b>Location Is Function</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EWORLD_AGENT__LOCATION_IS_FUNCTION = ESPECIES__LOCATION_IS_FUNCTION;

	/**
	 * The feature id for the '<em><b>Location Function</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EWORLD_AGENT__LOCATION_FUNCTION = ESPECIES__LOCATION_FUNCTION;

	/**
	 * The feature id for the '<em><b>Location Update</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EWORLD_AGENT__LOCATION_UPDATE = ESPECIES__LOCATION_UPDATE;

	/**
	 * The feature id for the '<em><b>Init</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EWORLD_AGENT__INIT = ESPECIES__INIT;

	/**
	 * The feature id for the '<em><b>Inheriting Links</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EWORLD_AGENT__INHERITING_LINKS = ESPECIES__INHERITING_LINKS;

	/**
	 * The feature id for the '<em><b>Schedules</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EWORLD_AGENT__SCHEDULES = ESPECIES__SCHEDULES;

	/**
	 * The feature id for the '<em><b>Bounds Width</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EWORLD_AGENT__BOUNDS_WIDTH = ESPECIES_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Bounds Heigth</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EWORLD_AGENT__BOUNDS_HEIGTH = ESPECIES_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Bounds Path</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EWORLD_AGENT__BOUNDS_PATH = ESPECIES_FEATURE_COUNT + 2;

	/**
	 * The feature id for the '<em><b>Bounds Expression</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EWORLD_AGENT__BOUNDS_EXPRESSION = ESPECIES_FEATURE_COUNT + 3;

	/**
	 * The feature id for the '<em><b>Bounds Type</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EWORLD_AGENT__BOUNDS_TYPE = ESPECIES_FEATURE_COUNT + 4;

	/**
	 * The number of structural features of the '<em>EWorld Agent</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EWORLD_AGENT_FEATURE_COUNT = ESPECIES_FEATURE_COUNT + 5;

	/**
	 * The meta object id for the '{@link gama.impl.ELayerImpl <em>ELayer</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see gama.impl.ELayerImpl
	 * @see gama.impl.GamaPackageImpl#getELayer()
	 * @generated
	 */
	int ELAYER = 18;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ELAYER__NAME = EGAMA_OBJECT__NAME;

	/**
	 * The feature id for the '<em><b>Model</b></em>' container reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ELAYER__MODEL = EGAMA_OBJECT__MODEL;

	/**
	 * The feature id for the '<em><b>Color Picto</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ELAYER__COLOR_PICTO = EGAMA_OBJECT__COLOR_PICTO;

	/**
	 * The feature id for the '<em><b>Has Error</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ELAYER__HAS_ERROR = EGAMA_OBJECT__HAS_ERROR;

	/**
	 * The feature id for the '<em><b>Error</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ELAYER__ERROR = EGAMA_OBJECT__ERROR;

	/**
	 * The feature id for the '<em><b>Gaml Code</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ELAYER__GAML_CODE = EGAMA_OBJECT_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Display</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ELAYER__DISPLAY = EGAMA_OBJECT_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Type</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ELAYER__TYPE = EGAMA_OBJECT_FEATURE_COUNT + 2;

	/**
	 * The feature id for the '<em><b>File</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ELAYER__FILE = EGAMA_OBJECT_FEATURE_COUNT + 3;

	/**
	 * The feature id for the '<em><b>Text</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ELAYER__TEXT = EGAMA_OBJECT_FEATURE_COUNT + 4;

	/**
	 * The feature id for the '<em><b>Size</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ELAYER__SIZE = EGAMA_OBJECT_FEATURE_COUNT + 5;

	/**
	 * The feature id for the '<em><b>Species</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ELAYER__SPECIES = EGAMA_OBJECT_FEATURE_COUNT + 6;

	/**
	 * The feature id for the '<em><b>Transparency</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ELAYER__TRANSPARENCY = EGAMA_OBJECT_FEATURE_COUNT + 7;

	/**
	 * The feature id for the '<em><b>Agents</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ELAYER__AGENTS = EGAMA_OBJECT_FEATURE_COUNT + 8;

	/**
	 * The feature id for the '<em><b>Position x</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ELAYER__POSITION_X = EGAMA_OBJECT_FEATURE_COUNT + 9;

	/**
	 * The feature id for the '<em><b>Position y</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ELAYER__POSITION_Y = EGAMA_OBJECT_FEATURE_COUNT + 10;

	/**
	 * The feature id for the '<em><b>Size x</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ELAYER__SIZE_X = EGAMA_OBJECT_FEATURE_COUNT + 11;

	/**
	 * The feature id for the '<em><b>Size y</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ELAYER__SIZE_Y = EGAMA_OBJECT_FEATURE_COUNT + 12;

	/**
	 * The feature id for the '<em><b>Aspect</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ELAYER__ASPECT = EGAMA_OBJECT_FEATURE_COUNT + 13;

	/**
	 * The feature id for the '<em><b>Color</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ELAYER__COLOR = EGAMA_OBJECT_FEATURE_COUNT + 14;

	/**
	 * The feature id for the '<em><b>Is Color Cst</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ELAYER__IS_COLOR_CST = EGAMA_OBJECT_FEATURE_COUNT + 15;

	/**
	 * The feature id for the '<em><b>Color RBG</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ELAYER__COLOR_RBG = EGAMA_OBJECT_FEATURE_COUNT + 16;

	/**
	 * The feature id for the '<em><b>Grid</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ELAYER__GRID = EGAMA_OBJECT_FEATURE_COUNT + 17;

	/**
	 * The feature id for the '<em><b>Refresh</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ELAYER__REFRESH = EGAMA_OBJECT_FEATURE_COUNT + 18;

	/**
	 * The feature id for the '<em><b>Chartlayers</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ELAYER__CHARTLAYERS = EGAMA_OBJECT_FEATURE_COUNT + 19;

	/**
	 * The feature id for the '<em><b>Chart type</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ELAYER__CHART_TYPE = EGAMA_OBJECT_FEATURE_COUNT + 20;

	/**
	 * The feature id for the '<em><b>Show Lines</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ELAYER__SHOW_LINES = EGAMA_OBJECT_FEATURE_COUNT + 21;

	/**
	 * The number of structural features of the '<em>ELayer</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ELAYER_FEATURE_COUNT = EGAMA_OBJECT_FEATURE_COUNT + 22;

	/**
	 * The meta object id for the '{@link gama.impl.ETopologyImpl <em>ETopology</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see gama.impl.ETopologyImpl
	 * @see gama.impl.GamaPackageImpl#getETopology()
	 * @generated
	 */
	int ETOPOLOGY = 24;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ETOPOLOGY__NAME = EGAMA_OBJECT__NAME;

	/**
	 * The feature id for the '<em><b>Model</b></em>' container reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ETOPOLOGY__MODEL = EGAMA_OBJECT__MODEL;

	/**
	 * The feature id for the '<em><b>Color Picto</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ETOPOLOGY__COLOR_PICTO = EGAMA_OBJECT__COLOR_PICTO;

	/**
	 * The feature id for the '<em><b>Has Error</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ETOPOLOGY__HAS_ERROR = EGAMA_OBJECT__HAS_ERROR;

	/**
	 * The feature id for the '<em><b>Error</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ETOPOLOGY__ERROR = EGAMA_OBJECT__ERROR;

	/**
	 * The feature id for the '<em><b>Species</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ETOPOLOGY__SPECIES = EGAMA_OBJECT_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>ETopology</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ETOPOLOGY_FEATURE_COUNT = EGAMA_OBJECT_FEATURE_COUNT + 1;

	/**
	 * The meta object id for the '{@link gama.impl.EGraphTopologyNodeImpl <em>EGraph Topology Node</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see gama.impl.EGraphTopologyNodeImpl
	 * @see gama.impl.GamaPackageImpl#getEGraphTopologyNode()
	 * @generated
	 */
	int EGRAPH_TOPOLOGY_NODE = 19;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EGRAPH_TOPOLOGY_NODE__NAME = ETOPOLOGY__NAME;

	/**
	 * The feature id for the '<em><b>Model</b></em>' container reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EGRAPH_TOPOLOGY_NODE__MODEL = ETOPOLOGY__MODEL;

	/**
	 * The feature id for the '<em><b>Color Picto</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EGRAPH_TOPOLOGY_NODE__COLOR_PICTO = ETOPOLOGY__COLOR_PICTO;

	/**
	 * The feature id for the '<em><b>Has Error</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EGRAPH_TOPOLOGY_NODE__HAS_ERROR = ETOPOLOGY__HAS_ERROR;

	/**
	 * The feature id for the '<em><b>Error</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EGRAPH_TOPOLOGY_NODE__ERROR = ETOPOLOGY__ERROR;

	/**
	 * The feature id for the '<em><b>Species</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EGRAPH_TOPOLOGY_NODE__SPECIES = ETOPOLOGY__SPECIES;

	/**
	 * The number of structural features of the '<em>EGraph Topology Node</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EGRAPH_TOPOLOGY_NODE_FEATURE_COUNT = ETOPOLOGY_FEATURE_COUNT + 0;

	/**
	 * The meta object id for the '{@link gama.impl.EExperimentLinkImpl <em>EExperiment Link</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see gama.impl.EExperimentLinkImpl
	 * @see gama.impl.GamaPackageImpl#getEExperimentLink()
	 * @generated
	 */
	int EEXPERIMENT_LINK = 20;

	/**
	 * The feature id for the '<em><b>Target</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EEXPERIMENT_LINK__TARGET = EGAMA_LINK__TARGET;

	/**
	 * The feature id for the '<em><b>Source</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EEXPERIMENT_LINK__SOURCE = EGAMA_LINK__SOURCE;

	/**
	 * The feature id for the '<em><b>Model</b></em>' container reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EEXPERIMENT_LINK__MODEL = EGAMA_LINK__MODEL;

	/**
	 * The feature id for the '<em><b>Species</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EEXPERIMENT_LINK__SPECIES = EGAMA_LINK_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Experiment</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EEXPERIMENT_LINK__EXPERIMENT = EGAMA_LINK_FEATURE_COUNT + 1;

	/**
	 * The number of structural features of the '<em>EExperiment Link</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EEXPERIMENT_LINK_FEATURE_COUNT = EGAMA_LINK_FEATURE_COUNT + 2;

	/**
	 * The meta object id for the '{@link gama.impl.ELayerAspectImpl <em>ELayer Aspect</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see gama.impl.ELayerAspectImpl
	 * @see gama.impl.GamaPackageImpl#getELayerAspect()
	 * @generated
	 */
	int ELAYER_ASPECT = 21;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ELAYER_ASPECT__NAME = EGAMA_OBJECT__NAME;

	/**
	 * The feature id for the '<em><b>Model</b></em>' container reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ELAYER_ASPECT__MODEL = EGAMA_OBJECT__MODEL;

	/**
	 * The feature id for the '<em><b>Color Picto</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ELAYER_ASPECT__COLOR_PICTO = EGAMA_OBJECT__COLOR_PICTO;

	/**
	 * The feature id for the '<em><b>Has Error</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ELAYER_ASPECT__HAS_ERROR = EGAMA_OBJECT__HAS_ERROR;

	/**
	 * The feature id for the '<em><b>Error</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ELAYER_ASPECT__ERROR = EGAMA_OBJECT__ERROR;

	/**
	 * The feature id for the '<em><b>Gaml Code</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ELAYER_ASPECT__GAML_CODE = EGAMA_OBJECT_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Shape</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ELAYER_ASPECT__SHAPE = EGAMA_OBJECT_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Color</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ELAYER_ASPECT__COLOR = EGAMA_OBJECT_FEATURE_COUNT + 2;

	/**
	 * The feature id for the '<em><b>Empty</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ELAYER_ASPECT__EMPTY = EGAMA_OBJECT_FEATURE_COUNT + 3;

	/**
	 * The feature id for the '<em><b>Rotate</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ELAYER_ASPECT__ROTATE = EGAMA_OBJECT_FEATURE_COUNT + 4;

	/**
	 * The feature id for the '<em><b>Size</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ELAYER_ASPECT__SIZE = EGAMA_OBJECT_FEATURE_COUNT + 5;

	/**
	 * The feature id for the '<em><b>Width</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ELAYER_ASPECT__WIDTH = EGAMA_OBJECT_FEATURE_COUNT + 6;

	/**
	 * The feature id for the '<em><b>Heigth</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ELAYER_ASPECT__HEIGTH = EGAMA_OBJECT_FEATURE_COUNT + 7;

	/**
	 * The feature id for the '<em><b>Radius</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ELAYER_ASPECT__RADIUS = EGAMA_OBJECT_FEATURE_COUNT + 8;

	/**
	 * The feature id for the '<em><b>Path</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ELAYER_ASPECT__PATH = EGAMA_OBJECT_FEATURE_COUNT + 9;

	/**
	 * The feature id for the '<em><b>Text</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ELAYER_ASPECT__TEXT = EGAMA_OBJECT_FEATURE_COUNT + 10;

	/**
	 * The feature id for the '<em><b>Type</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ELAYER_ASPECT__TYPE = EGAMA_OBJECT_FEATURE_COUNT + 11;

	/**
	 * The feature id for the '<em><b>Expression</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ELAYER_ASPECT__EXPRESSION = EGAMA_OBJECT_FEATURE_COUNT + 12;

	/**
	 * The feature id for the '<em><b>Points</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ELAYER_ASPECT__POINTS = EGAMA_OBJECT_FEATURE_COUNT + 13;

	/**
	 * The feature id for the '<em><b>At</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ELAYER_ASPECT__AT = EGAMA_OBJECT_FEATURE_COUNT + 14;

	/**
	 * The feature id for the '<em><b>Shape Type</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ELAYER_ASPECT__SHAPE_TYPE = EGAMA_OBJECT_FEATURE_COUNT + 15;

	/**
	 * The feature id for the '<em><b>Is Color Cst</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ELAYER_ASPECT__IS_COLOR_CST = EGAMA_OBJECT_FEATURE_COUNT + 16;

	/**
	 * The feature id for the '<em><b>Text Size</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ELAYER_ASPECT__TEXT_SIZE = EGAMA_OBJECT_FEATURE_COUNT + 17;

	/**
	 * The feature id for the '<em><b>Image Size</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ELAYER_ASPECT__IMAGE_SIZE = EGAMA_OBJECT_FEATURE_COUNT + 18;

	/**
	 * The feature id for the '<em><b>Color RBG</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ELAYER_ASPECT__COLOR_RBG = EGAMA_OBJECT_FEATURE_COUNT + 19;

	/**
	 * The feature id for the '<em><b>Aspect</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ELAYER_ASPECT__ASPECT = EGAMA_OBJECT_FEATURE_COUNT + 20;

	/**
	 * The feature id for the '<em><b>Depth</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ELAYER_ASPECT__DEPTH = EGAMA_OBJECT_FEATURE_COUNT + 21;

	/**
	 * The feature id for the '<em><b>Texture</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ELAYER_ASPECT__TEXTURE = EGAMA_OBJECT_FEATURE_COUNT + 22;

	/**
	 * The number of structural features of the '<em>ELayer Aspect</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ELAYER_ASPECT_FEATURE_COUNT = EGAMA_OBJECT_FEATURE_COUNT + 23;

	/**
	 * The meta object id for the '{@link gama.impl.EGridTopologyImpl <em>EGrid Topology</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see gama.impl.EGridTopologyImpl
	 * @see gama.impl.GamaPackageImpl#getEGridTopology()
	 * @generated
	 */
	int EGRID_TOPOLOGY = 22;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EGRID_TOPOLOGY__NAME = ETOPOLOGY__NAME;

	/**
	 * The feature id for the '<em><b>Model</b></em>' container reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EGRID_TOPOLOGY__MODEL = ETOPOLOGY__MODEL;

	/**
	 * The feature id for the '<em><b>Color Picto</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EGRID_TOPOLOGY__COLOR_PICTO = ETOPOLOGY__COLOR_PICTO;

	/**
	 * The feature id for the '<em><b>Has Error</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EGRID_TOPOLOGY__HAS_ERROR = ETOPOLOGY__HAS_ERROR;

	/**
	 * The feature id for the '<em><b>Error</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EGRID_TOPOLOGY__ERROR = ETOPOLOGY__ERROR;

	/**
	 * The feature id for the '<em><b>Species</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EGRID_TOPOLOGY__SPECIES = ETOPOLOGY__SPECIES;

	/**
	 * The feature id for the '<em><b>Nb columns</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EGRID_TOPOLOGY__NB_COLUMNS = ETOPOLOGY_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Nb rows</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EGRID_TOPOLOGY__NB_ROWS = ETOPOLOGY_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Neighbourhood</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EGRID_TOPOLOGY__NEIGHBOURHOOD = ETOPOLOGY_FEATURE_COUNT + 2;

	/**
	 * The feature id for the '<em><b>Neighbourhood Type</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EGRID_TOPOLOGY__NEIGHBOURHOOD_TYPE = ETOPOLOGY_FEATURE_COUNT + 3;

	/**
	 * The number of structural features of the '<em>EGrid Topology</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EGRID_TOPOLOGY_FEATURE_COUNT = ETOPOLOGY_FEATURE_COUNT + 4;

	/**
	 * The meta object id for the '{@link gama.impl.EContinuousTopologyImpl <em>EContinuous Topology</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see gama.impl.EContinuousTopologyImpl
	 * @see gama.impl.GamaPackageImpl#getEContinuousTopology()
	 * @generated
	 */
	int ECONTINUOUS_TOPOLOGY = 23;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ECONTINUOUS_TOPOLOGY__NAME = ETOPOLOGY__NAME;

	/**
	 * The feature id for the '<em><b>Model</b></em>' container reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ECONTINUOUS_TOPOLOGY__MODEL = ETOPOLOGY__MODEL;

	/**
	 * The feature id for the '<em><b>Color Picto</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ECONTINUOUS_TOPOLOGY__COLOR_PICTO = ETOPOLOGY__COLOR_PICTO;

	/**
	 * The feature id for the '<em><b>Has Error</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ECONTINUOUS_TOPOLOGY__HAS_ERROR = ETOPOLOGY__HAS_ERROR;

	/**
	 * The feature id for the '<em><b>Error</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ECONTINUOUS_TOPOLOGY__ERROR = ETOPOLOGY__ERROR;

	/**
	 * The feature id for the '<em><b>Species</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ECONTINUOUS_TOPOLOGY__SPECIES = ETOPOLOGY__SPECIES;

	/**
	 * The number of structural features of the '<em>EContinuous Topology</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ECONTINUOUS_TOPOLOGY_FEATURE_COUNT = ETOPOLOGY_FEATURE_COUNT + 0;

	/**
	 * The meta object id for the '{@link gama.impl.EInheritLinkImpl <em>EInherit Link</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see gama.impl.EInheritLinkImpl
	 * @see gama.impl.GamaPackageImpl#getEInheritLink()
	 * @generated
	 */
	int EINHERIT_LINK = 25;

	/**
	 * The feature id for the '<em><b>Target</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EINHERIT_LINK__TARGET = EGAMA_LINK__TARGET;

	/**
	 * The feature id for the '<em><b>Source</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EINHERIT_LINK__SOURCE = EGAMA_LINK__SOURCE;

	/**
	 * The feature id for the '<em><b>Model</b></em>' container reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EINHERIT_LINK__MODEL = EGAMA_LINK__MODEL;

	/**
	 * The feature id for the '<em><b>Parent</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EINHERIT_LINK__PARENT = EGAMA_LINK_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Child</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EINHERIT_LINK__CHILD = EGAMA_LINK_FEATURE_COUNT + 1;

	/**
	 * The number of structural features of the '<em>EInherit Link</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EINHERIT_LINK_FEATURE_COUNT = EGAMA_LINK_FEATURE_COUNT + 2;

	/**
	 * The meta object id for the '{@link gama.impl.EGraphTopologyEdgeImpl <em>EGraph Topology Edge</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see gama.impl.EGraphTopologyEdgeImpl
	 * @see gama.impl.GamaPackageImpl#getEGraphTopologyEdge()
	 * @generated
	 */
	int EGRAPH_TOPOLOGY_EDGE = 26;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EGRAPH_TOPOLOGY_EDGE__NAME = ETOPOLOGY__NAME;

	/**
	 * The feature id for the '<em><b>Model</b></em>' container reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EGRAPH_TOPOLOGY_EDGE__MODEL = ETOPOLOGY__MODEL;

	/**
	 * The feature id for the '<em><b>Color Picto</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EGRAPH_TOPOLOGY_EDGE__COLOR_PICTO = ETOPOLOGY__COLOR_PICTO;

	/**
	 * The feature id for the '<em><b>Has Error</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EGRAPH_TOPOLOGY_EDGE__HAS_ERROR = ETOPOLOGY__HAS_ERROR;

	/**
	 * The feature id for the '<em><b>Error</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EGRAPH_TOPOLOGY_EDGE__ERROR = ETOPOLOGY__ERROR;

	/**
	 * The feature id for the '<em><b>Species</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EGRAPH_TOPOLOGY_EDGE__SPECIES = ETOPOLOGY__SPECIES;

	/**
	 * The number of structural features of the '<em>EGraph Topology Edge</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EGRAPH_TOPOLOGY_EDGE_FEATURE_COUNT = ETOPOLOGY_FEATURE_COUNT + 0;

	/**
	 * The meta object id for the '{@link gama.impl.EGraphLinkImpl <em>EGraph Link</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see gama.impl.EGraphLinkImpl
	 * @see gama.impl.GamaPackageImpl#getEGraphLink()
	 * @generated
	 */
	int EGRAPH_LINK = 27;

	/**
	 * The feature id for the '<em><b>Target</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EGRAPH_LINK__TARGET = EGAMA_LINK__TARGET;

	/**
	 * The feature id for the '<em><b>Source</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EGRAPH_LINK__SOURCE = EGAMA_LINK__SOURCE;

	/**
	 * The feature id for the '<em><b>Model</b></em>' container reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EGRAPH_LINK__MODEL = EGAMA_LINK__MODEL;

	/**
	 * The feature id for the '<em><b>Node</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EGRAPH_LINK__NODE = EGAMA_LINK_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Edge</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EGRAPH_LINK__EDGE = EGAMA_LINK_FEATURE_COUNT + 1;

	/**
	 * The number of structural features of the '<em>EGraph Link</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EGRAPH_LINK_FEATURE_COUNT = EGAMA_LINK_FEATURE_COUNT + 2;

	/**
	 * The meta object id for the '{@link gama.impl.EChartLayerImpl <em>EChart Layer</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see gama.impl.EChartLayerImpl
	 * @see gama.impl.GamaPackageImpl#getEChartLayer()
	 * @generated
	 */
	int ECHART_LAYER = 28;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ECHART_LAYER__NAME = EGAMA_OBJECT__NAME;

	/**
	 * The feature id for the '<em><b>Model</b></em>' container reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ECHART_LAYER__MODEL = EGAMA_OBJECT__MODEL;

	/**
	 * The feature id for the '<em><b>Color Picto</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ECHART_LAYER__COLOR_PICTO = EGAMA_OBJECT__COLOR_PICTO;

	/**
	 * The feature id for the '<em><b>Has Error</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ECHART_LAYER__HAS_ERROR = EGAMA_OBJECT__HAS_ERROR;

	/**
	 * The feature id for the '<em><b>Error</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ECHART_LAYER__ERROR = EGAMA_OBJECT__ERROR;

	/**
	 * The feature id for the '<em><b>Style</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ECHART_LAYER__STYLE = EGAMA_OBJECT_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Color</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ECHART_LAYER__COLOR = EGAMA_OBJECT_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Value</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ECHART_LAYER__VALUE = EGAMA_OBJECT_FEATURE_COUNT + 2;

	/**
	 * The number of structural features of the '<em>EChart Layer</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ECHART_LAYER_FEATURE_COUNT = EGAMA_OBJECT_FEATURE_COUNT + 3;

	/**
	 * The meta object id for the '{@link gama.impl.EParameterImpl <em>EParameter</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see gama.impl.EParameterImpl
	 * @see gama.impl.GamaPackageImpl#getEParameter()
	 * @generated
	 */
	int EPARAMETER = 29;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EPARAMETER__NAME = EGAMA_OBJECT__NAME;

	/**
	 * The feature id for the '<em><b>Model</b></em>' container reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EPARAMETER__MODEL = EGAMA_OBJECT__MODEL;

	/**
	 * The feature id for the '<em><b>Color Picto</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EPARAMETER__COLOR_PICTO = EGAMA_OBJECT__COLOR_PICTO;

	/**
	 * The feature id for the '<em><b>Has Error</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EPARAMETER__HAS_ERROR = EGAMA_OBJECT__HAS_ERROR;

	/**
	 * The feature id for the '<em><b>Error</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EPARAMETER__ERROR = EGAMA_OBJECT__ERROR;

	/**
	 * The feature id for the '<em><b>Variable</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EPARAMETER__VARIABLE = EGAMA_OBJECT_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Min</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EPARAMETER__MIN = EGAMA_OBJECT_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Init</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EPARAMETER__INIT = EGAMA_OBJECT_FEATURE_COUNT + 2;

	/**
	 * The feature id for the '<em><b>Step</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EPARAMETER__STEP = EGAMA_OBJECT_FEATURE_COUNT + 3;

	/**
	 * The feature id for the '<em><b>Max</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EPARAMETER__MAX = EGAMA_OBJECT_FEATURE_COUNT + 4;

	/**
	 * The feature id for the '<em><b>Among</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EPARAMETER__AMONG = EGAMA_OBJECT_FEATURE_COUNT + 5;

	/**
	 * The feature id for the '<em><b>Category</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EPARAMETER__CATEGORY = EGAMA_OBJECT_FEATURE_COUNT + 6;

	/**
	 * The number of structural features of the '<em>EParameter</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EPARAMETER_FEATURE_COUNT = EGAMA_OBJECT_FEATURE_COUNT + 7;


	/**
	 * The meta object id for the '{@link gama.impl.EMonitorImpl <em>EMonitor</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see gama.impl.EMonitorImpl
	 * @see gama.impl.GamaPackageImpl#getEMonitor()
	 * @generated
	 */
	int EMONITOR = 30;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EMONITOR__NAME = EGAMA_OBJECT__NAME;

	/**
	 * The feature id for the '<em><b>Model</b></em>' container reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EMONITOR__MODEL = EGAMA_OBJECT__MODEL;

	/**
	 * The feature id for the '<em><b>Color Picto</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EMONITOR__COLOR_PICTO = EGAMA_OBJECT__COLOR_PICTO;

	/**
	 * The feature id for the '<em><b>Has Error</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EMONITOR__HAS_ERROR = EGAMA_OBJECT__HAS_ERROR;

	/**
	 * The feature id for the '<em><b>Error</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EMONITOR__ERROR = EGAMA_OBJECT__ERROR;

	/**
	 * The feature id for the '<em><b>Value</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EMONITOR__VALUE = EGAMA_OBJECT_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>EMonitor</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EMONITOR_FEATURE_COUNT = EGAMA_OBJECT_FEATURE_COUNT + 1;


	/**
	 * Returns the meta object for class '{@link gama.EGamaModel <em>EGama Model</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>EGama Model</em>'.
	 * @see gama.EGamaModel
	 * @generated
	 */
	EClass getEGamaModel();

	/**
	 * Returns the meta object for the containment reference list '{@link gama.EGamaModel#getObjects <em>Objects</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Objects</em>'.
	 * @see gama.EGamaModel#getObjects()
	 * @see #getEGamaModel()
	 * @generated
	 */
	EReference getEGamaModel_Objects();

	/**
	 * Returns the meta object for the attribute '{@link gama.EGamaModel#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see gama.EGamaModel#getName()
	 * @see #getEGamaModel()
	 * @generated
	 */
	EAttribute getEGamaModel_Name();

	/**
	 * Returns the meta object for the containment reference list '{@link gama.EGamaModel#getLinks <em>Links</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Links</em>'.
	 * @see gama.EGamaModel#getLinks()
	 * @see #getEGamaModel()
	 * @generated
	 */
	EReference getEGamaModel_Links();

	/**
	 * Returns the meta object for class '{@link gama.EGamaObject <em>EGama Object</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>EGama Object</em>'.
	 * @see gama.EGamaObject
	 * @generated
	 */
	EClass getEGamaObject();

	/**
	 * Returns the meta object for the attribute '{@link gama.EGamaObject#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see gama.EGamaObject#getName()
	 * @see #getEGamaObject()
	 * @generated
	 */
	EAttribute getEGamaObject_Name();

	/**
	 * Returns the meta object for the container reference '{@link gama.EGamaObject#getModel <em>Model</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the container reference '<em>Model</em>'.
	 * @see gama.EGamaObject#getModel()
	 * @see #getEGamaObject()
	 * @generated
	 */
	EReference getEGamaObject_Model();

	/**
	 * Returns the meta object for the attribute list '{@link gama.EGamaObject#getColorPicto <em>Color Picto</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute list '<em>Color Picto</em>'.
	 * @see gama.EGamaObject#getColorPicto()
	 * @see #getEGamaObject()
	 * @generated
	 */
	EAttribute getEGamaObject_ColorPicto();

	/**
	 * Returns the meta object for the attribute '{@link gama.EGamaObject#getHasError <em>Has Error</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Has Error</em>'.
	 * @see gama.EGamaObject#getHasError()
	 * @see #getEGamaObject()
	 * @generated
	 */
	EAttribute getEGamaObject_HasError();

	/**
	 * Returns the meta object for the attribute '{@link gama.EGamaObject#getError <em>Error</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Error</em>'.
	 * @see gama.EGamaObject#getError()
	 * @see #getEGamaObject()
	 * @generated
	 */
	EAttribute getEGamaObject_Error();

	/**
	 * Returns the meta object for class '{@link gama.ESpecies <em>ESpecies</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>ESpecies</em>'.
	 * @see gama.ESpecies
	 * @generated
	 */
	EClass getESpecies();

	/**
	 * Returns the meta object for the containment reference list '{@link gama.ESpecies#getVariables <em>Variables</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Variables</em>'.
	 * @see gama.ESpecies#getVariables()
	 * @see #getESpecies()
	 * @generated
	 */
	EReference getESpecies_Variables();

	/**
	 * Returns the meta object for the attribute list '{@link gama.ESpecies#getReflexList <em>Reflex List</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute list '<em>Reflex List</em>'.
	 * @see gama.ESpecies#getReflexList()
	 * @see #getESpecies()
	 * @generated
	 */
	EAttribute getESpecies_ReflexList();

	/**
	 * Returns the meta object for the attribute '{@link gama.ESpecies#getTorus <em>Torus</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Torus</em>'.
	 * @see gama.ESpecies#getTorus()
	 * @see #getESpecies()
	 * @generated
	 */
	EAttribute getESpecies_Torus();

	/**
	 * Returns the meta object for the reference list '{@link gama.ESpecies#getExperimentLinks <em>Experiment Links</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference list '<em>Experiment Links</em>'.
	 * @see gama.ESpecies#getExperimentLinks()
	 * @see #getESpecies()
	 * @generated
	 */
	EReference getESpecies_ExperimentLinks();

	/**
	 * Returns the meta object for the reference list '{@link gama.ESpecies#getAspectLinks <em>Aspect Links</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference list '<em>Aspect Links</em>'.
	 * @see gama.ESpecies#getAspectLinks()
	 * @see #getESpecies()
	 * @generated
	 */
	EReference getESpecies_AspectLinks();

	/**
	 * Returns the meta object for the reference list '{@link gama.ESpecies#getActionLinks <em>Action Links</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference list '<em>Action Links</em>'.
	 * @see gama.ESpecies#getActionLinks()
	 * @see #getESpecies()
	 * @generated
	 */
	EReference getESpecies_ActionLinks();

	/**
	 * Returns the meta object for the reference list '{@link gama.ESpecies#getReflexLinks <em>Reflex Links</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference list '<em>Reflex Links</em>'.
	 * @see gama.ESpecies#getReflexLinks()
	 * @see #getESpecies()
	 * @generated
	 */
	EReference getESpecies_ReflexLinks();

	/**
	 * Returns the meta object for the attribute '{@link gama.ESpecies#getShape <em>Shape</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Shape</em>'.
	 * @see gama.ESpecies#getShape()
	 * @see #getESpecies()
	 * @generated
	 */
	EAttribute getESpecies_Shape();

	/**
	 * Returns the meta object for the attribute '{@link gama.ESpecies#getLocation <em>Location</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Location</em>'.
	 * @see gama.ESpecies#getLocation()
	 * @see #getESpecies()
	 * @generated
	 */
	EAttribute getESpecies_Location();

	/**
	 * Returns the meta object for the attribute '{@link gama.ESpecies#getSize <em>Size</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Size</em>'.
	 * @see gama.ESpecies#getSize()
	 * @see #getESpecies()
	 * @generated
	 */
	EAttribute getESpecies_Size();

	/**
	 * Returns the meta object for the attribute '{@link gama.ESpecies#getWidth <em>Width</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Width</em>'.
	 * @see gama.ESpecies#getWidth()
	 * @see #getESpecies()
	 * @generated
	 */
	EAttribute getESpecies_Width();

	/**
	 * Returns the meta object for the attribute '{@link gama.ESpecies#getHeigth <em>Heigth</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Heigth</em>'.
	 * @see gama.ESpecies#getHeigth()
	 * @see #getESpecies()
	 * @generated
	 */
	EAttribute getESpecies_Heigth();

	/**
	 * Returns the meta object for the attribute '{@link gama.ESpecies#getRadius <em>Radius</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Radius</em>'.
	 * @see gama.ESpecies#getRadius()
	 * @see #getESpecies()
	 * @generated
	 */
	EAttribute getESpecies_Radius();

	/**
	 * Returns the meta object for the reference list '{@link gama.ESpecies#getMicroSpeciesLinks <em>Micro Species Links</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference list '<em>Micro Species Links</em>'.
	 * @see gama.ESpecies#getMicroSpeciesLinks()
	 * @see #getESpecies()
	 * @generated
	 */
	EReference getESpecies_MicroSpeciesLinks();

	/**
	 * Returns the meta object for the reference list '{@link gama.ESpecies#getMacroSpeciesLinks <em>Macro Species Links</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference list '<em>Macro Species Links</em>'.
	 * @see gama.ESpecies#getMacroSpeciesLinks()
	 * @see #getESpecies()
	 * @generated
	 */
	EReference getESpecies_MacroSpeciesLinks();

	/**
	 * Returns the meta object for the attribute list '{@link gama.ESpecies#getSkills <em>Skills</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute list '<em>Skills</em>'.
	 * @see gama.ESpecies#getSkills()
	 * @see #getESpecies()
	 * @generated
	 */
	EAttribute getESpecies_Skills();

	/**
	 * Returns the meta object for the reference '{@link gama.ESpecies#getTopology <em>Topology</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Topology</em>'.
	 * @see gama.ESpecies#getTopology()
	 * @see #getESpecies()
	 * @generated
	 */
	EReference getESpecies_Topology();

	/**
	 * Returns the meta object for the reference '{@link gama.ESpecies#getInheritsFrom <em>Inherits From</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Inherits From</em>'.
	 * @see gama.ESpecies#getInheritsFrom()
	 * @see #getESpecies()
	 * @generated
	 */
	EReference getESpecies_InheritsFrom();

	/**
	 * Returns the meta object for the attribute '{@link gama.ESpecies#getTorusType <em>Torus Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Torus Type</em>'.
	 * @see gama.ESpecies#getTorusType()
	 * @see #getESpecies()
	 * @generated
	 */
	EAttribute getESpecies_TorusType();

	/**
	 * Returns the meta object for the attribute '{@link gama.ESpecies#getShapeType <em>Shape Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Shape Type</em>'.
	 * @see gama.ESpecies#getShapeType()
	 * @see #getESpecies()
	 * @generated
	 */
	EAttribute getESpecies_ShapeType();

	/**
	 * Returns the meta object for the attribute '{@link gama.ESpecies#getLocationType <em>Location Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Location Type</em>'.
	 * @see gama.ESpecies#getLocationType()
	 * @see #getESpecies()
	 * @generated
	 */
	EAttribute getESpecies_LocationType();

	/**
	 * Returns the meta object for the attribute '{@link gama.ESpecies#getPoints <em>Points</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Points</em>'.
	 * @see gama.ESpecies#getPoints()
	 * @see #getESpecies()
	 * @generated
	 */
	EAttribute getESpecies_Points();

	/**
	 * Returns the meta object for the attribute '{@link gama.ESpecies#getExpressionShape <em>Expression Shape</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Expression Shape</em>'.
	 * @see gama.ESpecies#getExpressionShape()
	 * @see #getESpecies()
	 * @generated
	 */
	EAttribute getESpecies_ExpressionShape();

	/**
	 * Returns the meta object for the attribute '{@link gama.ESpecies#getExpressionLoc <em>Expression Loc</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Expression Loc</em>'.
	 * @see gama.ESpecies#getExpressionLoc()
	 * @see #getESpecies()
	 * @generated
	 */
	EAttribute getESpecies_ExpressionLoc();

	/**
	 * Returns the meta object for the attribute '{@link gama.ESpecies#getExpressionTorus <em>Expression Torus</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Expression Torus</em>'.
	 * @see gama.ESpecies#getExpressionTorus()
	 * @see #getESpecies()
	 * @generated
	 */
	EAttribute getESpecies_ExpressionTorus();

	/**
	 * Returns the meta object for the attribute '{@link gama.ESpecies#getShapeFunction <em>Shape Function</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Shape Function</em>'.
	 * @see gama.ESpecies#getShapeFunction()
	 * @see #getESpecies()
	 * @generated
	 */
	EAttribute getESpecies_ShapeFunction();

	/**
	 * Returns the meta object for the attribute '{@link gama.ESpecies#getShapeUpdate <em>Shape Update</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Shape Update</em>'.
	 * @see gama.ESpecies#getShapeUpdate()
	 * @see #getESpecies()
	 * @generated
	 */
	EAttribute getESpecies_ShapeUpdate();

	/**
	 * Returns the meta object for the attribute '{@link gama.ESpecies#getShapeIsFunction <em>Shape Is Function</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Shape Is Function</em>'.
	 * @see gama.ESpecies#getShapeIsFunction()
	 * @see #getESpecies()
	 * @generated
	 */
	EAttribute getESpecies_ShapeIsFunction();

	/**
	 * Returns the meta object for the attribute '{@link gama.ESpecies#getLocationIsFunction <em>Location Is Function</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Location Is Function</em>'.
	 * @see gama.ESpecies#getLocationIsFunction()
	 * @see #getESpecies()
	 * @generated
	 */
	EAttribute getESpecies_LocationIsFunction();

	/**
	 * Returns the meta object for the attribute '{@link gama.ESpecies#getLocationFunction <em>Location Function</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Location Function</em>'.
	 * @see gama.ESpecies#getLocationFunction()
	 * @see #getESpecies()
	 * @generated
	 */
	EAttribute getESpecies_LocationFunction();

	/**
	 * Returns the meta object for the attribute '{@link gama.ESpecies#getLocationUpdate <em>Location Update</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Location Update</em>'.
	 * @see gama.ESpecies#getLocationUpdate()
	 * @see #getESpecies()
	 * @generated
	 */
	EAttribute getESpecies_LocationUpdate();

	/**
	 * Returns the meta object for the attribute '{@link gama.ESpecies#getInit <em>Init</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Init</em>'.
	 * @see gama.ESpecies#getInit()
	 * @see #getESpecies()
	 * @generated
	 */
	EAttribute getESpecies_Init();

	/**
	 * Returns the meta object for the reference list '{@link gama.ESpecies#getInheritingLinks <em>Inheriting Links</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference list '<em>Inheriting Links</em>'.
	 * @see gama.ESpecies#getInheritingLinks()
	 * @see #getESpecies()
	 * @generated
	 */
	EReference getESpecies_InheritingLinks();

	/**
	 * Returns the meta object for the attribute '{@link gama.ESpecies#getSchedules <em>Schedules</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Schedules</em>'.
	 * @see gama.ESpecies#getSchedules()
	 * @see #getESpecies()
	 * @generated
	 */
	EAttribute getESpecies_Schedules();

	/**
	 * Returns the meta object for class '{@link gama.EAction <em>EAction</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>EAction</em>'.
	 * @see gama.EAction
	 * @generated
	 */
	EClass getEAction();

	/**
	 * Returns the meta object for the attribute '{@link gama.EAction#getGamlCode <em>Gaml Code</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Gaml Code</em>'.
	 * @see gama.EAction#getGamlCode()
	 * @see #getEAction()
	 * @generated
	 */
	EAttribute getEAction_GamlCode();

	/**
	 * Returns the meta object for the reference list '{@link gama.EAction#getActionLinks <em>Action Links</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference list '<em>Action Links</em>'.
	 * @see gama.EAction#getActionLinks()
	 * @see #getEAction()
	 * @generated
	 */
	EReference getEAction_ActionLinks();

	/**
	 * Returns the meta object for the containment reference list '{@link gama.EAction#getVariables <em>Variables</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Variables</em>'.
	 * @see gama.EAction#getVariables()
	 * @see #getEAction()
	 * @generated
	 */
	EReference getEAction_Variables();

	/**
	 * Returns the meta object for the attribute '{@link gama.EAction#getReturnType <em>Return Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Return Type</em>'.
	 * @see gama.EAction#getReturnType()
	 * @see #getEAction()
	 * @generated
	 */
	EAttribute getEAction_ReturnType();

	/**
	 * Returns the meta object for class '{@link gama.EAspect <em>EAspect</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>EAspect</em>'.
	 * @see gama.EAspect
	 * @generated
	 */
	EClass getEAspect();

	/**
	 * Returns the meta object for the attribute '{@link gama.EAspect#getGamlCode <em>Gaml Code</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Gaml Code</em>'.
	 * @see gama.EAspect#getGamlCode()
	 * @see #getEAspect()
	 * @generated
	 */
	EAttribute getEAspect_GamlCode();

	/**
	 * Returns the meta object for the reference list '{@link gama.EAspect#getAspectLinks <em>Aspect Links</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference list '<em>Aspect Links</em>'.
	 * @see gama.EAspect#getAspectLinks()
	 * @see #getEAspect()
	 * @generated
	 */
	EReference getEAspect_AspectLinks();

	/**
	 * Returns the meta object for the reference list '{@link gama.EAspect#getLayers <em>Layers</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference list '<em>Layers</em>'.
	 * @see gama.EAspect#getLayers()
	 * @see #getEAspect()
	 * @generated
	 */
	EReference getEAspect_Layers();

	/**
	 * Returns the meta object for class '{@link gama.EReflex <em>EReflex</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>EReflex</em>'.
	 * @see gama.EReflex
	 * @generated
	 */
	EClass getEReflex();

	/**
	 * Returns the meta object for the attribute '{@link gama.EReflex#getGamlCode <em>Gaml Code</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Gaml Code</em>'.
	 * @see gama.EReflex#getGamlCode()
	 * @see #getEReflex()
	 * @generated
	 */
	EAttribute getEReflex_GamlCode();

	/**
	 * Returns the meta object for the attribute '{@link gama.EReflex#getCondition <em>Condition</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Condition</em>'.
	 * @see gama.EReflex#getCondition()
	 * @see #getEReflex()
	 * @generated
	 */
	EAttribute getEReflex_Condition();

	/**
	 * Returns the meta object for the reference list '{@link gama.EReflex#getReflexLinks <em>Reflex Links</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference list '<em>Reflex Links</em>'.
	 * @see gama.EReflex#getReflexLinks()
	 * @see #getEReflex()
	 * @generated
	 */
	EReference getEReflex_ReflexLinks();

	/**
	 * Returns the meta object for class '{@link gama.EExperiment <em>EExperiment</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>EExperiment</em>'.
	 * @see gama.EExperiment
	 * @generated
	 */
	EClass getEExperiment();

	/**
	 * Returns the meta object for the reference '{@link gama.EExperiment#getExperimentLink <em>Experiment Link</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Experiment Link</em>'.
	 * @see gama.EExperiment#getExperimentLink()
	 * @see #getEExperiment()
	 * @generated
	 */
	EReference getEExperiment_ExperimentLink();

	/**
	 * Returns the meta object for the reference list '{@link gama.EExperiment#getDisplayLinks <em>Display Links</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference list '<em>Display Links</em>'.
	 * @see gama.EExperiment#getDisplayLinks()
	 * @see #getEExperiment()
	 * @generated
	 */
	EReference getEExperiment_DisplayLinks();

	/**
	 * Returns the meta object for the reference list '{@link gama.EExperiment#getParameters <em>Parameters</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference list '<em>Parameters</em>'.
	 * @see gama.EExperiment#getParameters()
	 * @see #getEExperiment()
	 * @generated
	 */
	EReference getEExperiment_Parameters();

	/**
	 * Returns the meta object for the reference list '{@link gama.EExperiment#getMonitors <em>Monitors</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference list '<em>Monitors</em>'.
	 * @see gama.EExperiment#getMonitors()
	 * @see #getEExperiment()
	 * @generated
	 */
	EReference getEExperiment_Monitors();

	/**
	 * Returns the meta object for class '{@link gama.EGUIExperiment <em>EGUI Experiment</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>EGUI Experiment</em>'.
	 * @see gama.EGUIExperiment
	 * @generated
	 */
	EClass getEGUIExperiment();

	/**
	 * Returns the meta object for class '{@link gama.EBatchExperiment <em>EBatch Experiment</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>EBatch Experiment</em>'.
	 * @see gama.EBatchExperiment
	 * @generated
	 */
	EClass getEBatchExperiment();

	/**
	 * Returns the meta object for class '{@link gama.EGamaLink <em>EGama Link</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>EGama Link</em>'.
	 * @see gama.EGamaLink
	 * @generated
	 */
	EClass getEGamaLink();

	/**
	 * Returns the meta object for the reference '{@link gama.EGamaLink#getTarget <em>Target</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Target</em>'.
	 * @see gama.EGamaLink#getTarget()
	 * @see #getEGamaLink()
	 * @generated
	 */
	EReference getEGamaLink_Target();

	/**
	 * Returns the meta object for the reference '{@link gama.EGamaLink#getSource <em>Source</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Source</em>'.
	 * @see gama.EGamaLink#getSource()
	 * @see #getEGamaLink()
	 * @generated
	 */
	EReference getEGamaLink_Source();

	/**
	 * Returns the meta object for the container reference '{@link gama.EGamaLink#getModel <em>Model</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the container reference '<em>Model</em>'.
	 * @see gama.EGamaLink#getModel()
	 * @see #getEGamaLink()
	 * @generated
	 */
	EReference getEGamaLink_Model();

	/**
	 * Returns the meta object for class '{@link gama.ESubSpeciesLink <em>ESub Species Link</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>ESub Species Link</em>'.
	 * @see gama.ESubSpeciesLink
	 * @generated
	 */
	EClass getESubSpeciesLink();

	/**
	 * Returns the meta object for the reference '{@link gama.ESubSpeciesLink#getMacro <em>Macro</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Macro</em>'.
	 * @see gama.ESubSpeciesLink#getMacro()
	 * @see #getESubSpeciesLink()
	 * @generated
	 */
	EReference getESubSpeciesLink_Macro();

	/**
	 * Returns the meta object for the reference '{@link gama.ESubSpeciesLink#getMicro <em>Micro</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Micro</em>'.
	 * @see gama.ESubSpeciesLink#getMicro()
	 * @see #getESubSpeciesLink()
	 * @generated
	 */
	EReference getESubSpeciesLink_Micro();

	/**
	 * Returns the meta object for class '{@link gama.EActionLink <em>EAction Link</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>EAction Link</em>'.
	 * @see gama.EActionLink
	 * @generated
	 */
	EClass getEActionLink();

	/**
	 * Returns the meta object for the reference '{@link gama.EActionLink#getAction <em>Action</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Action</em>'.
	 * @see gama.EActionLink#getAction()
	 * @see #getEActionLink()
	 * @generated
	 */
	EReference getEActionLink_Action();

	/**
	 * Returns the meta object for the reference '{@link gama.EActionLink#getSpecies <em>Species</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Species</em>'.
	 * @see gama.EActionLink#getSpecies()
	 * @see #getEActionLink()
	 * @generated
	 */
	EReference getEActionLink_Species();

	/**
	 * Returns the meta object for class '{@link gama.EAspectLink <em>EAspect Link</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>EAspect Link</em>'.
	 * @see gama.EAspectLink
	 * @generated
	 */
	EClass getEAspectLink();

	/**
	 * Returns the meta object for the reference '{@link gama.EAspectLink#getAspect <em>Aspect</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Aspect</em>'.
	 * @see gama.EAspectLink#getAspect()
	 * @see #getEAspectLink()
	 * @generated
	 */
	EReference getEAspectLink_Aspect();

	/**
	 * Returns the meta object for the reference '{@link gama.EAspectLink#getSpecies <em>Species</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Species</em>'.
	 * @see gama.EAspectLink#getSpecies()
	 * @see #getEAspectLink()
	 * @generated
	 */
	EReference getEAspectLink_Species();

	/**
	 * Returns the meta object for class '{@link gama.EReflexLink <em>EReflex Link</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>EReflex Link</em>'.
	 * @see gama.EReflexLink
	 * @generated
	 */
	EClass getEReflexLink();

	/**
	 * Returns the meta object for the reference '{@link gama.EReflexLink#getReflex <em>Reflex</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Reflex</em>'.
	 * @see gama.EReflexLink#getReflex()
	 * @see #getEReflexLink()
	 * @generated
	 */
	EReference getEReflexLink_Reflex();

	/**
	 * Returns the meta object for the reference '{@link gama.EReflexLink#getSpecies <em>Species</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Species</em>'.
	 * @see gama.EReflexLink#getSpecies()
	 * @see #getEReflexLink()
	 * @generated
	 */
	EReference getEReflexLink_Species();

	/**
	 * Returns the meta object for class '{@link gama.EDisplayLink <em>EDisplay Link</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>EDisplay Link</em>'.
	 * @see gama.EDisplayLink
	 * @generated
	 */
	EClass getEDisplayLink();

	/**
	 * Returns the meta object for the reference '{@link gama.EDisplayLink#getExperiment <em>Experiment</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Experiment</em>'.
	 * @see gama.EDisplayLink#getExperiment()
	 * @see #getEDisplayLink()
	 * @generated
	 */
	EReference getEDisplayLink_Experiment();

	/**
	 * Returns the meta object for the reference '{@link gama.EDisplayLink#getDisplay <em>Display</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Display</em>'.
	 * @see gama.EDisplayLink#getDisplay()
	 * @see #getEDisplayLink()
	 * @generated
	 */
	EReference getEDisplayLink_Display();

	/**
	 * Returns the meta object for class '{@link gama.EDisplay <em>EDisplay</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>EDisplay</em>'.
	 * @see gama.EDisplay
	 * @generated
	 */
	EClass getEDisplay();

	/**
	 * Returns the meta object for the reference list '{@link gama.EDisplay#getLayers <em>Layers</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference list '<em>Layers</em>'.
	 * @see gama.EDisplay#getLayers()
	 * @see #getEDisplay()
	 * @generated
	 */
	EReference getEDisplay_Layers();

	/**
	 * Returns the meta object for the reference '{@link gama.EDisplay#getDisplayLink <em>Display Link</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Display Link</em>'.
	 * @see gama.EDisplay#getDisplayLink()
	 * @see #getEDisplay()
	 * @generated
	 */
	EReference getEDisplay_DisplayLink();

	/**
	 * Returns the meta object for the attribute '{@link gama.EDisplay#getOpengl <em>Opengl</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Opengl</em>'.
	 * @see gama.EDisplay#getOpengl()
	 * @see #getEDisplay()
	 * @generated
	 */
	EAttribute getEDisplay_Opengl();

	/**
	 * Returns the meta object for the attribute '{@link gama.EDisplay#getRefresh <em>Refresh</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Refresh</em>'.
	 * @see gama.EDisplay#getRefresh()
	 * @see #getEDisplay()
	 * @generated
	 */
	EAttribute getEDisplay_Refresh();

	/**
	 * Returns the meta object for the attribute '{@link gama.EDisplay#getBackground <em>Background</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Background</em>'.
	 * @see gama.EDisplay#getBackground()
	 * @see #getEDisplay()
	 * @generated
	 */
	EAttribute getEDisplay_Background();

	/**
	 * Returns the meta object for the attribute list '{@link gama.EDisplay#getLayerList <em>Layer List</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute list '<em>Layer List</em>'.
	 * @see gama.EDisplay#getLayerList()
	 * @see #getEDisplay()
	 * @generated
	 */
	EAttribute getEDisplay_LayerList();

	/**
	 * Returns the meta object for the attribute '{@link gama.EDisplay#getColor <em>Color</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Color</em>'.
	 * @see gama.EDisplay#getColor()
	 * @see #getEDisplay()
	 * @generated
	 */
	EAttribute getEDisplay_Color();

	/**
	 * Returns the meta object for the attribute '{@link gama.EDisplay#getIsColorCst <em>Is Color Cst</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Is Color Cst</em>'.
	 * @see gama.EDisplay#getIsColorCst()
	 * @see #getEDisplay()
	 * @generated
	 */
	EAttribute getEDisplay_IsColorCst();

	/**
	 * Returns the meta object for the attribute list '{@link gama.EDisplay#getColorRBG <em>Color RBG</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute list '<em>Color RBG</em>'.
	 * @see gama.EDisplay#getColorRBG()
	 * @see #getEDisplay()
	 * @generated
	 */
	EAttribute getEDisplay_ColorRBG();

	/**
	 * Returns the meta object for the attribute '{@link gama.EDisplay#getGamlCode <em>Gaml Code</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Gaml Code</em>'.
	 * @see gama.EDisplay#getGamlCode()
	 * @see #getEDisplay()
	 * @generated
	 */
	EAttribute getEDisplay_GamlCode();

	/**
	 * Returns the meta object for the attribute '{@link gama.EDisplay#getAmbientLight <em>Ambient Light</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Ambient Light</em>'.
	 * @see gama.EDisplay#getAmbientLight()
	 * @see #getEDisplay()
	 * @generated
	 */
	EAttribute getEDisplay_AmbientLight();

	/**
	 * Returns the meta object for the attribute '{@link gama.EDisplay#getDrawDiffuseLight <em>Draw Diffuse Light</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Draw Diffuse Light</em>'.
	 * @see gama.EDisplay#getDrawDiffuseLight()
	 * @see #getEDisplay()
	 * @generated
	 */
	EAttribute getEDisplay_DrawDiffuseLight();

	/**
	 * Returns the meta object for the attribute '{@link gama.EDisplay#getDiffuseLight <em>Diffuse Light</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Diffuse Light</em>'.
	 * @see gama.EDisplay#getDiffuseLight()
	 * @see #getEDisplay()
	 * @generated
	 */
	EAttribute getEDisplay_DiffuseLight();

	/**
	 * Returns the meta object for the attribute '{@link gama.EDisplay#getDiffuseLightPos <em>Diffuse Light Pos</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Diffuse Light Pos</em>'.
	 * @see gama.EDisplay#getDiffuseLightPos()
	 * @see #getEDisplay()
	 * @generated
	 */
	EAttribute getEDisplay_DiffuseLightPos();

	/**
	 * Returns the meta object for the attribute '{@link gama.EDisplay#getZFighting <em>ZFighting</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>ZFighting</em>'.
	 * @see gama.EDisplay#getZFighting()
	 * @see #getEDisplay()
	 * @generated
	 */
	EAttribute getEDisplay_ZFighting();

	/**
	 * Returns the meta object for the attribute '{@link gama.EDisplay#getCameraPos <em>Camera Pos</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Camera Pos</em>'.
	 * @see gama.EDisplay#getCameraPos()
	 * @see #getEDisplay()
	 * @generated
	 */
	EAttribute getEDisplay_CameraPos();

	/**
	 * Returns the meta object for the attribute '{@link gama.EDisplay#getCameraLookPos <em>Camera Look Pos</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Camera Look Pos</em>'.
	 * @see gama.EDisplay#getCameraLookPos()
	 * @see #getEDisplay()
	 * @generated
	 */
	EAttribute getEDisplay_CameraLookPos();

	/**
	 * Returns the meta object for the attribute '{@link gama.EDisplay#getCameraUpVector <em>Camera Up Vector</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Camera Up Vector</em>'.
	 * @see gama.EDisplay#getCameraUpVector()
	 * @see #getEDisplay()
	 * @generated
	 */
	EAttribute getEDisplay_CameraUpVector();

	/**
	 * Returns the meta object for class '{@link gama.EVariable <em>EVariable</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>EVariable</em>'.
	 * @see gama.EVariable
	 * @generated
	 */
	EClass getEVariable();

	/**
	 * Returns the meta object for the attribute '{@link gama.EVariable#getInit <em>Init</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Init</em>'.
	 * @see gama.EVariable#getInit()
	 * @see #getEVariable()
	 * @generated
	 */
	EAttribute getEVariable_Init();

	/**
	 * Returns the meta object for the attribute '{@link gama.EVariable#getMin <em>Min</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Min</em>'.
	 * @see gama.EVariable#getMin()
	 * @see #getEVariable()
	 * @generated
	 */
	EAttribute getEVariable_Min();

	/**
	 * Returns the meta object for the attribute '{@link gama.EVariable#getMax <em>Max</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Max</em>'.
	 * @see gama.EVariable#getMax()
	 * @see #getEVariable()
	 * @generated
	 */
	EAttribute getEVariable_Max();

	/**
	 * Returns the meta object for the attribute '{@link gama.EVariable#getUpdate <em>Update</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Update</em>'.
	 * @see gama.EVariable#getUpdate()
	 * @see #getEVariable()
	 * @generated
	 */
	EAttribute getEVariable_Update();

	/**
	 * Returns the meta object for the attribute '{@link gama.EVariable#getFunction <em>Function</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Function</em>'.
	 * @see gama.EVariable#getFunction()
	 * @see #getEVariable()
	 * @generated
	 */
	EAttribute getEVariable_Function();

	/**
	 * Returns the meta object for the attribute '{@link gama.EVariable#getType <em>Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Type</em>'.
	 * @see gama.EVariable#getType()
	 * @see #getEVariable()
	 * @generated
	 */
	EAttribute getEVariable_Type();

	/**
	 * Returns the meta object for the attribute '{@link gama.EVariable#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see gama.EVariable#getName()
	 * @see #getEVariable()
	 * @generated
	 */
	EAttribute getEVariable_Name();

	/**
	 * Returns the meta object for the attribute '{@link gama.EVariable#getHasError <em>Has Error</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Has Error</em>'.
	 * @see gama.EVariable#getHasError()
	 * @see #getEVariable()
	 * @generated
	 */
	EAttribute getEVariable_HasError();

	/**
	 * Returns the meta object for the attribute '{@link gama.EVariable#getError <em>Error</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Error</em>'.
	 * @see gama.EVariable#getError()
	 * @see #getEVariable()
	 * @generated
	 */
	EAttribute getEVariable_Error();

	/**
	 * Returns the meta object for the reference '{@link gama.EVariable#getOwner <em>Owner</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Owner</em>'.
	 * @see gama.EVariable#getOwner()
	 * @see #getEVariable()
	 * @generated
	 */
	EReference getEVariable_Owner();

	/**
	 * Returns the meta object for class '{@link gama.EWorldAgent <em>EWorld Agent</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>EWorld Agent</em>'.
	 * @see gama.EWorldAgent
	 * @generated
	 */
	EClass getEWorldAgent();

	/**
	 * Returns the meta object for the attribute '{@link gama.EWorldAgent#getBoundsWidth <em>Bounds Width</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Bounds Width</em>'.
	 * @see gama.EWorldAgent#getBoundsWidth()
	 * @see #getEWorldAgent()
	 * @generated
	 */
	EAttribute getEWorldAgent_BoundsWidth();

	/**
	 * Returns the meta object for the attribute '{@link gama.EWorldAgent#getBoundsHeigth <em>Bounds Heigth</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Bounds Heigth</em>'.
	 * @see gama.EWorldAgent#getBoundsHeigth()
	 * @see #getEWorldAgent()
	 * @generated
	 */
	EAttribute getEWorldAgent_BoundsHeigth();

	/**
	 * Returns the meta object for the attribute '{@link gama.EWorldAgent#getBoundsPath <em>Bounds Path</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Bounds Path</em>'.
	 * @see gama.EWorldAgent#getBoundsPath()
	 * @see #getEWorldAgent()
	 * @generated
	 */
	EAttribute getEWorldAgent_BoundsPath();

	/**
	 * Returns the meta object for the attribute '{@link gama.EWorldAgent#getBoundsExpression <em>Bounds Expression</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Bounds Expression</em>'.
	 * @see gama.EWorldAgent#getBoundsExpression()
	 * @see #getEWorldAgent()
	 * @generated
	 */
	EAttribute getEWorldAgent_BoundsExpression();

	/**
	 * Returns the meta object for the attribute '{@link gama.EWorldAgent#getBoundsType <em>Bounds Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Bounds Type</em>'.
	 * @see gama.EWorldAgent#getBoundsType()
	 * @see #getEWorldAgent()
	 * @generated
	 */
	EAttribute getEWorldAgent_BoundsType();

	/**
	 * Returns the meta object for class '{@link gama.ELayer <em>ELayer</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>ELayer</em>'.
	 * @see gama.ELayer
	 * @generated
	 */
	EClass getELayer();

	/**
	 * Returns the meta object for the attribute '{@link gama.ELayer#getGamlCode <em>Gaml Code</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Gaml Code</em>'.
	 * @see gama.ELayer#getGamlCode()
	 * @see #getELayer()
	 * @generated
	 */
	EAttribute getELayer_GamlCode();

	/**
	 * Returns the meta object for the reference '{@link gama.ELayer#getDisplay <em>Display</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Display</em>'.
	 * @see gama.ELayer#getDisplay()
	 * @see #getELayer()
	 * @generated
	 */
	EReference getELayer_Display();

	/**
	 * Returns the meta object for the attribute '{@link gama.ELayer#getType <em>Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Type</em>'.
	 * @see gama.ELayer#getType()
	 * @see #getELayer()
	 * @generated
	 */
	EAttribute getELayer_Type();

	/**
	 * Returns the meta object for the attribute '{@link gama.ELayer#getFile <em>File</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>File</em>'.
	 * @see gama.ELayer#getFile()
	 * @see #getELayer()
	 * @generated
	 */
	EAttribute getELayer_File();

	/**
	 * Returns the meta object for the attribute '{@link gama.ELayer#getText <em>Text</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Text</em>'.
	 * @see gama.ELayer#getText()
	 * @see #getELayer()
	 * @generated
	 */
	EAttribute getELayer_Text();

	/**
	 * Returns the meta object for the attribute '{@link gama.ELayer#getSize <em>Size</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Size</em>'.
	 * @see gama.ELayer#getSize()
	 * @see #getELayer()
	 * @generated
	 */
	EAttribute getELayer_Size();

	/**
	 * Returns the meta object for the attribute '{@link gama.ELayer#getSpecies <em>Species</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Species</em>'.
	 * @see gama.ELayer#getSpecies()
	 * @see #getELayer()
	 * @generated
	 */
	EAttribute getELayer_Species();

	/**
	 * Returns the meta object for the attribute '{@link gama.ELayer#getTransparency <em>Transparency</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Transparency</em>'.
	 * @see gama.ELayer#getTransparency()
	 * @see #getELayer()
	 * @generated
	 */
	EAttribute getELayer_Transparency();

	/**
	 * Returns the meta object for the attribute '{@link gama.ELayer#getAgents <em>Agents</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Agents</em>'.
	 * @see gama.ELayer#getAgents()
	 * @see #getELayer()
	 * @generated
	 */
	EAttribute getELayer_Agents();

	/**
	 * Returns the meta object for the attribute '{@link gama.ELayer#getPosition_x <em>Position x</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Position x</em>'.
	 * @see gama.ELayer#getPosition_x()
	 * @see #getELayer()
	 * @generated
	 */
	EAttribute getELayer_Position_x();

	/**
	 * Returns the meta object for the attribute '{@link gama.ELayer#getPosition_y <em>Position y</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Position y</em>'.
	 * @see gama.ELayer#getPosition_y()
	 * @see #getELayer()
	 * @generated
	 */
	EAttribute getELayer_Position_y();

	/**
	 * Returns the meta object for the attribute '{@link gama.ELayer#getSize_x <em>Size x</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Size x</em>'.
	 * @see gama.ELayer#getSize_x()
	 * @see #getELayer()
	 * @generated
	 */
	EAttribute getELayer_Size_x();

	/**
	 * Returns the meta object for the attribute '{@link gama.ELayer#getSize_y <em>Size y</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Size y</em>'.
	 * @see gama.ELayer#getSize_y()
	 * @see #getELayer()
	 * @generated
	 */
	EAttribute getELayer_Size_y();

	/**
	 * Returns the meta object for the attribute '{@link gama.ELayer#getAspect <em>Aspect</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Aspect</em>'.
	 * @see gama.ELayer#getAspect()
	 * @see #getELayer()
	 * @generated
	 */
	EAttribute getELayer_Aspect();

	/**
	 * Returns the meta object for the attribute '{@link gama.ELayer#getColor <em>Color</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Color</em>'.
	 * @see gama.ELayer#getColor()
	 * @see #getELayer()
	 * @generated
	 */
	EAttribute getELayer_Color();

	/**
	 * Returns the meta object for the attribute '{@link gama.ELayer#getIsColorCst <em>Is Color Cst</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Is Color Cst</em>'.
	 * @see gama.ELayer#getIsColorCst()
	 * @see #getELayer()
	 * @generated
	 */
	EAttribute getELayer_IsColorCst();

	/**
	 * Returns the meta object for the attribute list '{@link gama.ELayer#getColorRBG <em>Color RBG</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute list '<em>Color RBG</em>'.
	 * @see gama.ELayer#getColorRBG()
	 * @see #getELayer()
	 * @generated
	 */
	EAttribute getELayer_ColorRBG();

	/**
	 * Returns the meta object for the attribute '{@link gama.ELayer#getGrid <em>Grid</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Grid</em>'.
	 * @see gama.ELayer#getGrid()
	 * @see #getELayer()
	 * @generated
	 */
	EAttribute getELayer_Grid();

	/**
	 * Returns the meta object for the attribute '{@link gama.ELayer#getRefresh <em>Refresh</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Refresh</em>'.
	 * @see gama.ELayer#getRefresh()
	 * @see #getELayer()
	 * @generated
	 */
	EAttribute getELayer_Refresh();

	/**
	 * Returns the meta object for the reference list '{@link gama.ELayer#getChartlayers <em>Chartlayers</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference list '<em>Chartlayers</em>'.
	 * @see gama.ELayer#getChartlayers()
	 * @see #getELayer()
	 * @generated
	 */
	EReference getELayer_Chartlayers();

	/**
	 * Returns the meta object for the attribute '{@link gama.ELayer#getChart_type <em>Chart type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Chart type</em>'.
	 * @see gama.ELayer#getChart_type()
	 * @see #getELayer()
	 * @generated
	 */
	EAttribute getELayer_Chart_type();

	/**
	 * Returns the meta object for the attribute '{@link gama.ELayer#isShowLines <em>Show Lines</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Show Lines</em>'.
	 * @see gama.ELayer#isShowLines()
	 * @see #getELayer()
	 * @generated
	 */
	EAttribute getELayer_ShowLines();

	/**
	 * Returns the meta object for class '{@link gama.EGraphTopologyNode <em>EGraph Topology Node</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>EGraph Topology Node</em>'.
	 * @see gama.EGraphTopologyNode
	 * @generated
	 */
	EClass getEGraphTopologyNode();

	/**
	 * Returns the meta object for class '{@link gama.EExperimentLink <em>EExperiment Link</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>EExperiment Link</em>'.
	 * @see gama.EExperimentLink
	 * @generated
	 */
	EClass getEExperimentLink();

	/**
	 * Returns the meta object for the reference '{@link gama.EExperimentLink#getSpecies <em>Species</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Species</em>'.
	 * @see gama.EExperimentLink#getSpecies()
	 * @see #getEExperimentLink()
	 * @generated
	 */
	EReference getEExperimentLink_Species();

	/**
	 * Returns the meta object for the reference '{@link gama.EExperimentLink#getExperiment <em>Experiment</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Experiment</em>'.
	 * @see gama.EExperimentLink#getExperiment()
	 * @see #getEExperimentLink()
	 * @generated
	 */
	EReference getEExperimentLink_Experiment();

	/**
	 * Returns the meta object for class '{@link gama.ELayerAspect <em>ELayer Aspect</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>ELayer Aspect</em>'.
	 * @see gama.ELayerAspect
	 * @generated
	 */
	EClass getELayerAspect();

	/**
	 * Returns the meta object for the attribute '{@link gama.ELayerAspect#getGamlCode <em>Gaml Code</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Gaml Code</em>'.
	 * @see gama.ELayerAspect#getGamlCode()
	 * @see #getELayerAspect()
	 * @generated
	 */
	EAttribute getELayerAspect_GamlCode();

	/**
	 * Returns the meta object for the attribute '{@link gama.ELayerAspect#getShape <em>Shape</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Shape</em>'.
	 * @see gama.ELayerAspect#getShape()
	 * @see #getELayerAspect()
	 * @generated
	 */
	EAttribute getELayerAspect_Shape();

	/**
	 * Returns the meta object for the attribute '{@link gama.ELayerAspect#getColor <em>Color</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Color</em>'.
	 * @see gama.ELayerAspect#getColor()
	 * @see #getELayerAspect()
	 * @generated
	 */
	EAttribute getELayerAspect_Color();

	/**
	 * Returns the meta object for the attribute '{@link gama.ELayerAspect#getEmpty <em>Empty</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Empty</em>'.
	 * @see gama.ELayerAspect#getEmpty()
	 * @see #getELayerAspect()
	 * @generated
	 */
	EAttribute getELayerAspect_Empty();

	/**
	 * Returns the meta object for the attribute '{@link gama.ELayerAspect#getRotate <em>Rotate</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Rotate</em>'.
	 * @see gama.ELayerAspect#getRotate()
	 * @see #getELayerAspect()
	 * @generated
	 */
	EAttribute getELayerAspect_Rotate();

	/**
	 * Returns the meta object for the attribute '{@link gama.ELayerAspect#getSize <em>Size</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Size</em>'.
	 * @see gama.ELayerAspect#getSize()
	 * @see #getELayerAspect()
	 * @generated
	 */
	EAttribute getELayerAspect_Size();

	/**
	 * Returns the meta object for the attribute '{@link gama.ELayerAspect#getWidth <em>Width</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Width</em>'.
	 * @see gama.ELayerAspect#getWidth()
	 * @see #getELayerAspect()
	 * @generated
	 */
	EAttribute getELayerAspect_Width();

	/**
	 * Returns the meta object for the attribute '{@link gama.ELayerAspect#getHeigth <em>Heigth</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Heigth</em>'.
	 * @see gama.ELayerAspect#getHeigth()
	 * @see #getELayerAspect()
	 * @generated
	 */
	EAttribute getELayerAspect_Heigth();

	/**
	 * Returns the meta object for the attribute '{@link gama.ELayerAspect#getRadius <em>Radius</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Radius</em>'.
	 * @see gama.ELayerAspect#getRadius()
	 * @see #getELayerAspect()
	 * @generated
	 */
	EAttribute getELayerAspect_Radius();

	/**
	 * Returns the meta object for the attribute '{@link gama.ELayerAspect#getPath <em>Path</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Path</em>'.
	 * @see gama.ELayerAspect#getPath()
	 * @see #getELayerAspect()
	 * @generated
	 */
	EAttribute getELayerAspect_Path();

	/**
	 * Returns the meta object for the attribute '{@link gama.ELayerAspect#getText <em>Text</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Text</em>'.
	 * @see gama.ELayerAspect#getText()
	 * @see #getELayerAspect()
	 * @generated
	 */
	EAttribute getELayerAspect_Text();

	/**
	 * Returns the meta object for the attribute '{@link gama.ELayerAspect#getType <em>Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Type</em>'.
	 * @see gama.ELayerAspect#getType()
	 * @see #getELayerAspect()
	 * @generated
	 */
	EAttribute getELayerAspect_Type();

	/**
	 * Returns the meta object for the attribute '{@link gama.ELayerAspect#getExpression <em>Expression</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Expression</em>'.
	 * @see gama.ELayerAspect#getExpression()
	 * @see #getELayerAspect()
	 * @generated
	 */
	EAttribute getELayerAspect_Expression();

	/**
	 * Returns the meta object for the attribute '{@link gama.ELayerAspect#getPoints <em>Points</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Points</em>'.
	 * @see gama.ELayerAspect#getPoints()
	 * @see #getELayerAspect()
	 * @generated
	 */
	EAttribute getELayerAspect_Points();

	/**
	 * Returns the meta object for the attribute '{@link gama.ELayerAspect#getAt <em>At</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>At</em>'.
	 * @see gama.ELayerAspect#getAt()
	 * @see #getELayerAspect()
	 * @generated
	 */
	EAttribute getELayerAspect_At();

	/**
	 * Returns the meta object for the attribute '{@link gama.ELayerAspect#getShapeType <em>Shape Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Shape Type</em>'.
	 * @see gama.ELayerAspect#getShapeType()
	 * @see #getELayerAspect()
	 * @generated
	 */
	EAttribute getELayerAspect_ShapeType();

	/**
	 * Returns the meta object for the attribute '{@link gama.ELayerAspect#getIsColorCst <em>Is Color Cst</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Is Color Cst</em>'.
	 * @see gama.ELayerAspect#getIsColorCst()
	 * @see #getELayerAspect()
	 * @generated
	 */
	EAttribute getELayerAspect_IsColorCst();

	/**
	 * Returns the meta object for the attribute '{@link gama.ELayerAspect#getTextSize <em>Text Size</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Text Size</em>'.
	 * @see gama.ELayerAspect#getTextSize()
	 * @see #getELayerAspect()
	 * @generated
	 */
	EAttribute getELayerAspect_TextSize();

	/**
	 * Returns the meta object for the attribute '{@link gama.ELayerAspect#getImageSize <em>Image Size</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Image Size</em>'.
	 * @see gama.ELayerAspect#getImageSize()
	 * @see #getELayerAspect()
	 * @generated
	 */
	EAttribute getELayerAspect_ImageSize();

	/**
	 * Returns the meta object for the attribute list '{@link gama.ELayerAspect#getColorRBG <em>Color RBG</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute list '<em>Color RBG</em>'.
	 * @see gama.ELayerAspect#getColorRBG()
	 * @see #getELayerAspect()
	 * @generated
	 */
	EAttribute getELayerAspect_ColorRBG();

	/**
	 * Returns the meta object for the reference '{@link gama.ELayerAspect#getAspect <em>Aspect</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Aspect</em>'.
	 * @see gama.ELayerAspect#getAspect()
	 * @see #getELayerAspect()
	 * @generated
	 */
	EReference getELayerAspect_Aspect();

	/**
	 * Returns the meta object for the attribute '{@link gama.ELayerAspect#getDepth <em>Depth</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Depth</em>'.
	 * @see gama.ELayerAspect#getDepth()
	 * @see #getELayerAspect()
	 * @generated
	 */
	EAttribute getELayerAspect_Depth();

	/**
	 * Returns the meta object for the attribute '{@link gama.ELayerAspect#getTexture <em>Texture</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Texture</em>'.
	 * @see gama.ELayerAspect#getTexture()
	 * @see #getELayerAspect()
	 * @generated
	 */
	EAttribute getELayerAspect_Texture();

	/**
	 * Returns the meta object for class '{@link gama.EGridTopology <em>EGrid Topology</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>EGrid Topology</em>'.
	 * @see gama.EGridTopology
	 * @generated
	 */
	EClass getEGridTopology();

	/**
	 * Returns the meta object for the attribute '{@link gama.EGridTopology#getNb_columns <em>Nb columns</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Nb columns</em>'.
	 * @see gama.EGridTopology#getNb_columns()
	 * @see #getEGridTopology()
	 * @generated
	 */
	EAttribute getEGridTopology_Nb_columns();

	/**
	 * Returns the meta object for the attribute '{@link gama.EGridTopology#getNb_rows <em>Nb rows</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Nb rows</em>'.
	 * @see gama.EGridTopology#getNb_rows()
	 * @see #getEGridTopology()
	 * @generated
	 */
	EAttribute getEGridTopology_Nb_rows();

	/**
	 * Returns the meta object for the attribute '{@link gama.EGridTopology#getNeighbourhood <em>Neighbourhood</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Neighbourhood</em>'.
	 * @see gama.EGridTopology#getNeighbourhood()
	 * @see #getEGridTopology()
	 * @generated
	 */
	EAttribute getEGridTopology_Neighbourhood();

	/**
	 * Returns the meta object for the attribute '{@link gama.EGridTopology#getNeighbourhoodType <em>Neighbourhood Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Neighbourhood Type</em>'.
	 * @see gama.EGridTopology#getNeighbourhoodType()
	 * @see #getEGridTopology()
	 * @generated
	 */
	EAttribute getEGridTopology_NeighbourhoodType();

	/**
	 * Returns the meta object for class '{@link gama.EContinuousTopology <em>EContinuous Topology</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>EContinuous Topology</em>'.
	 * @see gama.EContinuousTopology
	 * @generated
	 */
	EClass getEContinuousTopology();

	/**
	 * Returns the meta object for class '{@link gama.ETopology <em>ETopology</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>ETopology</em>'.
	 * @see gama.ETopology
	 * @generated
	 */
	EClass getETopology();

	/**
	 * Returns the meta object for the reference '{@link gama.ETopology#getSpecies <em>Species</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Species</em>'.
	 * @see gama.ETopology#getSpecies()
	 * @see #getETopology()
	 * @generated
	 */
	EReference getETopology_Species();

	/**
	 * Returns the meta object for class '{@link gama.EInheritLink <em>EInherit Link</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>EInherit Link</em>'.
	 * @see gama.EInheritLink
	 * @generated
	 */
	EClass getEInheritLink();

	/**
	 * Returns the meta object for the reference '{@link gama.EInheritLink#getParent <em>Parent</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Parent</em>'.
	 * @see gama.EInheritLink#getParent()
	 * @see #getEInheritLink()
	 * @generated
	 */
	EReference getEInheritLink_Parent();

	/**
	 * Returns the meta object for the reference '{@link gama.EInheritLink#getChild <em>Child</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Child</em>'.
	 * @see gama.EInheritLink#getChild()
	 * @see #getEInheritLink()
	 * @generated
	 */
	EReference getEInheritLink_Child();

	/**
	 * Returns the meta object for class '{@link gama.EGraphTopologyEdge <em>EGraph Topology Edge</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>EGraph Topology Edge</em>'.
	 * @see gama.EGraphTopologyEdge
	 * @generated
	 */
	EClass getEGraphTopologyEdge();

	/**
	 * Returns the meta object for class '{@link gama.EGraphLink <em>EGraph Link</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>EGraph Link</em>'.
	 * @see gama.EGraphLink
	 * @generated
	 */
	EClass getEGraphLink();

	/**
	 * Returns the meta object for the reference '{@link gama.EGraphLink#getNode <em>Node</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Node</em>'.
	 * @see gama.EGraphLink#getNode()
	 * @see #getEGraphLink()
	 * @generated
	 */
	EReference getEGraphLink_Node();

	/**
	 * Returns the meta object for the reference '{@link gama.EGraphLink#getEdge <em>Edge</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Edge</em>'.
	 * @see gama.EGraphLink#getEdge()
	 * @see #getEGraphLink()
	 * @generated
	 */
	EReference getEGraphLink_Edge();

	/**
	 * Returns the meta object for class '{@link gama.EChartLayer <em>EChart Layer</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>EChart Layer</em>'.
	 * @see gama.EChartLayer
	 * @generated
	 */
	EClass getEChartLayer();

	/**
	 * Returns the meta object for the attribute '{@link gama.EChartLayer#getStyle <em>Style</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Style</em>'.
	 * @see gama.EChartLayer#getStyle()
	 * @see #getEChartLayer()
	 * @generated
	 */
	EAttribute getEChartLayer_Style();

	/**
	 * Returns the meta object for the attribute '{@link gama.EChartLayer#getColor <em>Color</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Color</em>'.
	 * @see gama.EChartLayer#getColor()
	 * @see #getEChartLayer()
	 * @generated
	 */
	EAttribute getEChartLayer_Color();

	/**
	 * Returns the meta object for the attribute '{@link gama.EChartLayer#getValue <em>Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Value</em>'.
	 * @see gama.EChartLayer#getValue()
	 * @see #getEChartLayer()
	 * @generated
	 */
	EAttribute getEChartLayer_Value();

	/**
	 * Returns the meta object for class '{@link gama.EParameter <em>EParameter</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>EParameter</em>'.
	 * @see gama.EParameter
	 * @generated
	 */
	EClass getEParameter();

	/**
	 * Returns the meta object for the attribute '{@link gama.EParameter#getVariable <em>Variable</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Variable</em>'.
	 * @see gama.EParameter#getVariable()
	 * @see #getEParameter()
	 * @generated
	 */
	EAttribute getEParameter_Variable();

	/**
	 * Returns the meta object for the attribute '{@link gama.EParameter#getMin <em>Min</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Min</em>'.
	 * @see gama.EParameter#getMin()
	 * @see #getEParameter()
	 * @generated
	 */
	EAttribute getEParameter_Min();

	/**
	 * Returns the meta object for the attribute '{@link gama.EParameter#getInit <em>Init</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Init</em>'.
	 * @see gama.EParameter#getInit()
	 * @see #getEParameter()
	 * @generated
	 */
	EAttribute getEParameter_Init();

	/**
	 * Returns the meta object for the attribute '{@link gama.EParameter#getStep <em>Step</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Step</em>'.
	 * @see gama.EParameter#getStep()
	 * @see #getEParameter()
	 * @generated
	 */
	EAttribute getEParameter_Step();

	/**
	 * Returns the meta object for the attribute '{@link gama.EParameter#getMax <em>Max</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Max</em>'.
	 * @see gama.EParameter#getMax()
	 * @see #getEParameter()
	 * @generated
	 */
	EAttribute getEParameter_Max();

	/**
	 * Returns the meta object for the attribute '{@link gama.EParameter#getAmong <em>Among</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Among</em>'.
	 * @see gama.EParameter#getAmong()
	 * @see #getEParameter()
	 * @generated
	 */
	EAttribute getEParameter_Among();

	/**
	 * Returns the meta object for the attribute '{@link gama.EParameter#getCategory <em>Category</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Category</em>'.
	 * @see gama.EParameter#getCategory()
	 * @see #getEParameter()
	 * @generated
	 */
	EAttribute getEParameter_Category();

	/**
	 * Returns the meta object for class '{@link gama.EMonitor <em>EMonitor</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>EMonitor</em>'.
	 * @see gama.EMonitor
	 * @generated
	 */
	EClass getEMonitor();

	/**
	 * Returns the meta object for the attribute '{@link gama.EMonitor#getValue <em>Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Value</em>'.
	 * @see gama.EMonitor#getValue()
	 * @see #getEMonitor()
	 * @generated
	 */
	EAttribute getEMonitor_Value();

	/**
	 * Returns the factory that creates the instances of the model.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the factory that creates the instances of the model.
	 * @generated
	 */
	GamaFactory getGamaFactory();

	/**
	 * <!-- begin-user-doc -->
	 * Defines literals for the meta objects that represent
	 * <ul>
	 *   <li>each class,</li>
	 *   <li>each feature of each class,</li>
	 *   <li>each enum,</li>
	 *   <li>and each data type</li>
	 * </ul>
	 * <!-- end-user-doc -->
	 * @generated
	 */
	interface Literals {
		/**
		 * The meta object literal for the '{@link gama.impl.EGamaModelImpl <em>EGama Model</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see gama.impl.EGamaModelImpl
		 * @see gama.impl.GamaPackageImpl#getEGamaModel()
		 * @generated
		 */
		EClass EGAMA_MODEL = eINSTANCE.getEGamaModel();

		/**
		 * The meta object literal for the '<em><b>Objects</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference EGAMA_MODEL__OBJECTS = eINSTANCE.getEGamaModel_Objects();

		/**
		 * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute EGAMA_MODEL__NAME = eINSTANCE.getEGamaModel_Name();

		/**
		 * The meta object literal for the '<em><b>Links</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference EGAMA_MODEL__LINKS = eINSTANCE.getEGamaModel_Links();

		/**
		 * The meta object literal for the '{@link gama.impl.EGamaObjectImpl <em>EGama Object</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see gama.impl.EGamaObjectImpl
		 * @see gama.impl.GamaPackageImpl#getEGamaObject()
		 * @generated
		 */
		EClass EGAMA_OBJECT = eINSTANCE.getEGamaObject();

		/**
		 * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute EGAMA_OBJECT__NAME = eINSTANCE.getEGamaObject_Name();

		/**
		 * The meta object literal for the '<em><b>Model</b></em>' container reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference EGAMA_OBJECT__MODEL = eINSTANCE.getEGamaObject_Model();

		/**
		 * The meta object literal for the '<em><b>Color Picto</b></em>' attribute list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute EGAMA_OBJECT__COLOR_PICTO = eINSTANCE.getEGamaObject_ColorPicto();

		/**
		 * The meta object literal for the '<em><b>Has Error</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute EGAMA_OBJECT__HAS_ERROR = eINSTANCE.getEGamaObject_HasError();

		/**
		 * The meta object literal for the '<em><b>Error</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute EGAMA_OBJECT__ERROR = eINSTANCE.getEGamaObject_Error();

		/**
		 * The meta object literal for the '{@link gama.impl.ESpeciesImpl <em>ESpecies</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see gama.impl.ESpeciesImpl
		 * @see gama.impl.GamaPackageImpl#getESpecies()
		 * @generated
		 */
		EClass ESPECIES = eINSTANCE.getESpecies();

		/**
		 * The meta object literal for the '<em><b>Variables</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference ESPECIES__VARIABLES = eINSTANCE.getESpecies_Variables();

		/**
		 * The meta object literal for the '<em><b>Reflex List</b></em>' attribute list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ESPECIES__REFLEX_LIST = eINSTANCE.getESpecies_ReflexList();

		/**
		 * The meta object literal for the '<em><b>Torus</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ESPECIES__TORUS = eINSTANCE.getESpecies_Torus();

		/**
		 * The meta object literal for the '<em><b>Experiment Links</b></em>' reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference ESPECIES__EXPERIMENT_LINKS = eINSTANCE.getESpecies_ExperimentLinks();

		/**
		 * The meta object literal for the '<em><b>Aspect Links</b></em>' reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference ESPECIES__ASPECT_LINKS = eINSTANCE.getESpecies_AspectLinks();

		/**
		 * The meta object literal for the '<em><b>Action Links</b></em>' reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference ESPECIES__ACTION_LINKS = eINSTANCE.getESpecies_ActionLinks();

		/**
		 * The meta object literal for the '<em><b>Reflex Links</b></em>' reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference ESPECIES__REFLEX_LINKS = eINSTANCE.getESpecies_ReflexLinks();

		/**
		 * The meta object literal for the '<em><b>Shape</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ESPECIES__SHAPE = eINSTANCE.getESpecies_Shape();

		/**
		 * The meta object literal for the '<em><b>Location</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ESPECIES__LOCATION = eINSTANCE.getESpecies_Location();

		/**
		 * The meta object literal for the '<em><b>Size</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ESPECIES__SIZE = eINSTANCE.getESpecies_Size();

		/**
		 * The meta object literal for the '<em><b>Width</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ESPECIES__WIDTH = eINSTANCE.getESpecies_Width();

		/**
		 * The meta object literal for the '<em><b>Heigth</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ESPECIES__HEIGTH = eINSTANCE.getESpecies_Heigth();

		/**
		 * The meta object literal for the '<em><b>Radius</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ESPECIES__RADIUS = eINSTANCE.getESpecies_Radius();

		/**
		 * The meta object literal for the '<em><b>Micro Species Links</b></em>' reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference ESPECIES__MICRO_SPECIES_LINKS = eINSTANCE.getESpecies_MicroSpeciesLinks();

		/**
		 * The meta object literal for the '<em><b>Macro Species Links</b></em>' reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference ESPECIES__MACRO_SPECIES_LINKS = eINSTANCE.getESpecies_MacroSpeciesLinks();

		/**
		 * The meta object literal for the '<em><b>Skills</b></em>' attribute list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ESPECIES__SKILLS = eINSTANCE.getESpecies_Skills();

		/**
		 * The meta object literal for the '<em><b>Topology</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference ESPECIES__TOPOLOGY = eINSTANCE.getESpecies_Topology();

		/**
		 * The meta object literal for the '<em><b>Inherits From</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference ESPECIES__INHERITS_FROM = eINSTANCE.getESpecies_InheritsFrom();

		/**
		 * The meta object literal for the '<em><b>Torus Type</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ESPECIES__TORUS_TYPE = eINSTANCE.getESpecies_TorusType();

		/**
		 * The meta object literal for the '<em><b>Shape Type</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ESPECIES__SHAPE_TYPE = eINSTANCE.getESpecies_ShapeType();

		/**
		 * The meta object literal for the '<em><b>Location Type</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ESPECIES__LOCATION_TYPE = eINSTANCE.getESpecies_LocationType();

		/**
		 * The meta object literal for the '<em><b>Points</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ESPECIES__POINTS = eINSTANCE.getESpecies_Points();

		/**
		 * The meta object literal for the '<em><b>Expression Shape</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ESPECIES__EXPRESSION_SHAPE = eINSTANCE.getESpecies_ExpressionShape();

		/**
		 * The meta object literal for the '<em><b>Expression Loc</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ESPECIES__EXPRESSION_LOC = eINSTANCE.getESpecies_ExpressionLoc();

		/**
		 * The meta object literal for the '<em><b>Expression Torus</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ESPECIES__EXPRESSION_TORUS = eINSTANCE.getESpecies_ExpressionTorus();

		/**
		 * The meta object literal for the '<em><b>Shape Function</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ESPECIES__SHAPE_FUNCTION = eINSTANCE.getESpecies_ShapeFunction();

		/**
		 * The meta object literal for the '<em><b>Shape Update</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ESPECIES__SHAPE_UPDATE = eINSTANCE.getESpecies_ShapeUpdate();

		/**
		 * The meta object literal for the '<em><b>Shape Is Function</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ESPECIES__SHAPE_IS_FUNCTION = eINSTANCE.getESpecies_ShapeIsFunction();

		/**
		 * The meta object literal for the '<em><b>Location Is Function</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ESPECIES__LOCATION_IS_FUNCTION = eINSTANCE.getESpecies_LocationIsFunction();

		/**
		 * The meta object literal for the '<em><b>Location Function</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ESPECIES__LOCATION_FUNCTION = eINSTANCE.getESpecies_LocationFunction();

		/**
		 * The meta object literal for the '<em><b>Location Update</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ESPECIES__LOCATION_UPDATE = eINSTANCE.getESpecies_LocationUpdate();

		/**
		 * The meta object literal for the '<em><b>Init</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ESPECIES__INIT = eINSTANCE.getESpecies_Init();

		/**
		 * The meta object literal for the '<em><b>Inheriting Links</b></em>' reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference ESPECIES__INHERITING_LINKS = eINSTANCE.getESpecies_InheritingLinks();

		/**
		 * The meta object literal for the '<em><b>Schedules</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ESPECIES__SCHEDULES = eINSTANCE.getESpecies_Schedules();

		/**
		 * The meta object literal for the '{@link gama.impl.EActionImpl <em>EAction</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see gama.impl.EActionImpl
		 * @see gama.impl.GamaPackageImpl#getEAction()
		 * @generated
		 */
		EClass EACTION = eINSTANCE.getEAction();

		/**
		 * The meta object literal for the '<em><b>Gaml Code</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute EACTION__GAML_CODE = eINSTANCE.getEAction_GamlCode();

		/**
		 * The meta object literal for the '<em><b>Action Links</b></em>' reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference EACTION__ACTION_LINKS = eINSTANCE.getEAction_ActionLinks();

		/**
		 * The meta object literal for the '<em><b>Variables</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference EACTION__VARIABLES = eINSTANCE.getEAction_Variables();

		/**
		 * The meta object literal for the '<em><b>Return Type</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute EACTION__RETURN_TYPE = eINSTANCE.getEAction_ReturnType();

		/**
		 * The meta object literal for the '{@link gama.impl.EAspectImpl <em>EAspect</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see gama.impl.EAspectImpl
		 * @see gama.impl.GamaPackageImpl#getEAspect()
		 * @generated
		 */
		EClass EASPECT = eINSTANCE.getEAspect();

		/**
		 * The meta object literal for the '<em><b>Gaml Code</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute EASPECT__GAML_CODE = eINSTANCE.getEAspect_GamlCode();

		/**
		 * The meta object literal for the '<em><b>Aspect Links</b></em>' reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference EASPECT__ASPECT_LINKS = eINSTANCE.getEAspect_AspectLinks();

		/**
		 * The meta object literal for the '<em><b>Layers</b></em>' reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference EASPECT__LAYERS = eINSTANCE.getEAspect_Layers();

		/**
		 * The meta object literal for the '{@link gama.impl.EReflexImpl <em>EReflex</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see gama.impl.EReflexImpl
		 * @see gama.impl.GamaPackageImpl#getEReflex()
		 * @generated
		 */
		EClass EREFLEX = eINSTANCE.getEReflex();

		/**
		 * The meta object literal for the '<em><b>Gaml Code</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute EREFLEX__GAML_CODE = eINSTANCE.getEReflex_GamlCode();

		/**
		 * The meta object literal for the '<em><b>Condition</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute EREFLEX__CONDITION = eINSTANCE.getEReflex_Condition();

		/**
		 * The meta object literal for the '<em><b>Reflex Links</b></em>' reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference EREFLEX__REFLEX_LINKS = eINSTANCE.getEReflex_ReflexLinks();

		/**
		 * The meta object literal for the '{@link gama.impl.EExperimentImpl <em>EExperiment</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see gama.impl.EExperimentImpl
		 * @see gama.impl.GamaPackageImpl#getEExperiment()
		 * @generated
		 */
		EClass EEXPERIMENT = eINSTANCE.getEExperiment();

		/**
		 * The meta object literal for the '<em><b>Experiment Link</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference EEXPERIMENT__EXPERIMENT_LINK = eINSTANCE.getEExperiment_ExperimentLink();

		/**
		 * The meta object literal for the '<em><b>Display Links</b></em>' reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference EEXPERIMENT__DISPLAY_LINKS = eINSTANCE.getEExperiment_DisplayLinks();

		/**
		 * The meta object literal for the '<em><b>Parameters</b></em>' reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference EEXPERIMENT__PARAMETERS = eINSTANCE.getEExperiment_Parameters();

		/**
		 * The meta object literal for the '<em><b>Monitors</b></em>' reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference EEXPERIMENT__MONITORS = eINSTANCE.getEExperiment_Monitors();

		/**
		 * The meta object literal for the '{@link gama.impl.EGUIExperimentImpl <em>EGUI Experiment</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see gama.impl.EGUIExperimentImpl
		 * @see gama.impl.GamaPackageImpl#getEGUIExperiment()
		 * @generated
		 */
		EClass EGUI_EXPERIMENT = eINSTANCE.getEGUIExperiment();

		/**
		 * The meta object literal for the '{@link gama.impl.EBatchExperimentImpl <em>EBatch Experiment</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see gama.impl.EBatchExperimentImpl
		 * @see gama.impl.GamaPackageImpl#getEBatchExperiment()
		 * @generated
		 */
		EClass EBATCH_EXPERIMENT = eINSTANCE.getEBatchExperiment();

		/**
		 * The meta object literal for the '{@link gama.impl.EGamaLinkImpl <em>EGama Link</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see gama.impl.EGamaLinkImpl
		 * @see gama.impl.GamaPackageImpl#getEGamaLink()
		 * @generated
		 */
		EClass EGAMA_LINK = eINSTANCE.getEGamaLink();

		/**
		 * The meta object literal for the '<em><b>Target</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference EGAMA_LINK__TARGET = eINSTANCE.getEGamaLink_Target();

		/**
		 * The meta object literal for the '<em><b>Source</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference EGAMA_LINK__SOURCE = eINSTANCE.getEGamaLink_Source();

		/**
		 * The meta object literal for the '<em><b>Model</b></em>' container reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference EGAMA_LINK__MODEL = eINSTANCE.getEGamaLink_Model();

		/**
		 * The meta object literal for the '{@link gama.impl.ESubSpeciesLinkImpl <em>ESub Species Link</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see gama.impl.ESubSpeciesLinkImpl
		 * @see gama.impl.GamaPackageImpl#getESubSpeciesLink()
		 * @generated
		 */
		EClass ESUB_SPECIES_LINK = eINSTANCE.getESubSpeciesLink();

		/**
		 * The meta object literal for the '<em><b>Macro</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference ESUB_SPECIES_LINK__MACRO = eINSTANCE.getESubSpeciesLink_Macro();

		/**
		 * The meta object literal for the '<em><b>Micro</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference ESUB_SPECIES_LINK__MICRO = eINSTANCE.getESubSpeciesLink_Micro();

		/**
		 * The meta object literal for the '{@link gama.impl.EActionLinkImpl <em>EAction Link</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see gama.impl.EActionLinkImpl
		 * @see gama.impl.GamaPackageImpl#getEActionLink()
		 * @generated
		 */
		EClass EACTION_LINK = eINSTANCE.getEActionLink();

		/**
		 * The meta object literal for the '<em><b>Action</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference EACTION_LINK__ACTION = eINSTANCE.getEActionLink_Action();

		/**
		 * The meta object literal for the '<em><b>Species</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference EACTION_LINK__SPECIES = eINSTANCE.getEActionLink_Species();

		/**
		 * The meta object literal for the '{@link gama.impl.EAspectLinkImpl <em>EAspect Link</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see gama.impl.EAspectLinkImpl
		 * @see gama.impl.GamaPackageImpl#getEAspectLink()
		 * @generated
		 */
		EClass EASPECT_LINK = eINSTANCE.getEAspectLink();

		/**
		 * The meta object literal for the '<em><b>Aspect</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference EASPECT_LINK__ASPECT = eINSTANCE.getEAspectLink_Aspect();

		/**
		 * The meta object literal for the '<em><b>Species</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference EASPECT_LINK__SPECIES = eINSTANCE.getEAspectLink_Species();

		/**
		 * The meta object literal for the '{@link gama.impl.EReflexLinkImpl <em>EReflex Link</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see gama.impl.EReflexLinkImpl
		 * @see gama.impl.GamaPackageImpl#getEReflexLink()
		 * @generated
		 */
		EClass EREFLEX_LINK = eINSTANCE.getEReflexLink();

		/**
		 * The meta object literal for the '<em><b>Reflex</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference EREFLEX_LINK__REFLEX = eINSTANCE.getEReflexLink_Reflex();

		/**
		 * The meta object literal for the '<em><b>Species</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference EREFLEX_LINK__SPECIES = eINSTANCE.getEReflexLink_Species();

		/**
		 * The meta object literal for the '{@link gama.impl.EDisplayLinkImpl <em>EDisplay Link</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see gama.impl.EDisplayLinkImpl
		 * @see gama.impl.GamaPackageImpl#getEDisplayLink()
		 * @generated
		 */
		EClass EDISPLAY_LINK = eINSTANCE.getEDisplayLink();

		/**
		 * The meta object literal for the '<em><b>Experiment</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference EDISPLAY_LINK__EXPERIMENT = eINSTANCE.getEDisplayLink_Experiment();

		/**
		 * The meta object literal for the '<em><b>Display</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference EDISPLAY_LINK__DISPLAY = eINSTANCE.getEDisplayLink_Display();

		/**
		 * The meta object literal for the '{@link gama.impl.EDisplayImpl <em>EDisplay</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see gama.impl.EDisplayImpl
		 * @see gama.impl.GamaPackageImpl#getEDisplay()
		 * @generated
		 */
		EClass EDISPLAY = eINSTANCE.getEDisplay();

		/**
		 * The meta object literal for the '<em><b>Layers</b></em>' reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference EDISPLAY__LAYERS = eINSTANCE.getEDisplay_Layers();

		/**
		 * The meta object literal for the '<em><b>Display Link</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference EDISPLAY__DISPLAY_LINK = eINSTANCE.getEDisplay_DisplayLink();

		/**
		 * The meta object literal for the '<em><b>Opengl</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute EDISPLAY__OPENGL = eINSTANCE.getEDisplay_Opengl();

		/**
		 * The meta object literal for the '<em><b>Refresh</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute EDISPLAY__REFRESH = eINSTANCE.getEDisplay_Refresh();

		/**
		 * The meta object literal for the '<em><b>Background</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute EDISPLAY__BACKGROUND = eINSTANCE.getEDisplay_Background();

		/**
		 * The meta object literal for the '<em><b>Layer List</b></em>' attribute list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute EDISPLAY__LAYER_LIST = eINSTANCE.getEDisplay_LayerList();

		/**
		 * The meta object literal for the '<em><b>Color</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute EDISPLAY__COLOR = eINSTANCE.getEDisplay_Color();

		/**
		 * The meta object literal for the '<em><b>Is Color Cst</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute EDISPLAY__IS_COLOR_CST = eINSTANCE.getEDisplay_IsColorCst();

		/**
		 * The meta object literal for the '<em><b>Color RBG</b></em>' attribute list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute EDISPLAY__COLOR_RBG = eINSTANCE.getEDisplay_ColorRBG();

		/**
		 * The meta object literal for the '<em><b>Gaml Code</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute EDISPLAY__GAML_CODE = eINSTANCE.getEDisplay_GamlCode();

		/**
		 * The meta object literal for the '<em><b>Ambient Light</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute EDISPLAY__AMBIENT_LIGHT = eINSTANCE.getEDisplay_AmbientLight();

		/**
		 * The meta object literal for the '<em><b>Draw Diffuse Light</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute EDISPLAY__DRAW_DIFFUSE_LIGHT = eINSTANCE.getEDisplay_DrawDiffuseLight();

		/**
		 * The meta object literal for the '<em><b>Diffuse Light</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute EDISPLAY__DIFFUSE_LIGHT = eINSTANCE.getEDisplay_DiffuseLight();

		/**
		 * The meta object literal for the '<em><b>Diffuse Light Pos</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute EDISPLAY__DIFFUSE_LIGHT_POS = eINSTANCE.getEDisplay_DiffuseLightPos();

		/**
		 * The meta object literal for the '<em><b>ZFighting</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute EDISPLAY__ZFIGHTING = eINSTANCE.getEDisplay_ZFighting();

		/**
		 * The meta object literal for the '<em><b>Camera Pos</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute EDISPLAY__CAMERA_POS = eINSTANCE.getEDisplay_CameraPos();

		/**
		 * The meta object literal for the '<em><b>Camera Look Pos</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute EDISPLAY__CAMERA_LOOK_POS = eINSTANCE.getEDisplay_CameraLookPos();

		/**
		 * The meta object literal for the '<em><b>Camera Up Vector</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute EDISPLAY__CAMERA_UP_VECTOR = eINSTANCE.getEDisplay_CameraUpVector();

		/**
		 * The meta object literal for the '{@link gama.impl.EVariableImpl <em>EVariable</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see gama.impl.EVariableImpl
		 * @see gama.impl.GamaPackageImpl#getEVariable()
		 * @generated
		 */
		EClass EVARIABLE = eINSTANCE.getEVariable();

		/**
		 * The meta object literal for the '<em><b>Init</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute EVARIABLE__INIT = eINSTANCE.getEVariable_Init();

		/**
		 * The meta object literal for the '<em><b>Min</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute EVARIABLE__MIN = eINSTANCE.getEVariable_Min();

		/**
		 * The meta object literal for the '<em><b>Max</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute EVARIABLE__MAX = eINSTANCE.getEVariable_Max();

		/**
		 * The meta object literal for the '<em><b>Update</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute EVARIABLE__UPDATE = eINSTANCE.getEVariable_Update();

		/**
		 * The meta object literal for the '<em><b>Function</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute EVARIABLE__FUNCTION = eINSTANCE.getEVariable_Function();

		/**
		 * The meta object literal for the '<em><b>Type</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute EVARIABLE__TYPE = eINSTANCE.getEVariable_Type();

		/**
		 * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute EVARIABLE__NAME = eINSTANCE.getEVariable_Name();

		/**
		 * The meta object literal for the '<em><b>Has Error</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute EVARIABLE__HAS_ERROR = eINSTANCE.getEVariable_HasError();

		/**
		 * The meta object literal for the '<em><b>Error</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute EVARIABLE__ERROR = eINSTANCE.getEVariable_Error();

		/**
		 * The meta object literal for the '<em><b>Owner</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference EVARIABLE__OWNER = eINSTANCE.getEVariable_Owner();

		/**
		 * The meta object literal for the '{@link gama.impl.EWorldAgentImpl <em>EWorld Agent</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see gama.impl.EWorldAgentImpl
		 * @see gama.impl.GamaPackageImpl#getEWorldAgent()
		 * @generated
		 */
		EClass EWORLD_AGENT = eINSTANCE.getEWorldAgent();

		/**
		 * The meta object literal for the '<em><b>Bounds Width</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute EWORLD_AGENT__BOUNDS_WIDTH = eINSTANCE.getEWorldAgent_BoundsWidth();

		/**
		 * The meta object literal for the '<em><b>Bounds Heigth</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute EWORLD_AGENT__BOUNDS_HEIGTH = eINSTANCE.getEWorldAgent_BoundsHeigth();

		/**
		 * The meta object literal for the '<em><b>Bounds Path</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute EWORLD_AGENT__BOUNDS_PATH = eINSTANCE.getEWorldAgent_BoundsPath();

		/**
		 * The meta object literal for the '<em><b>Bounds Expression</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute EWORLD_AGENT__BOUNDS_EXPRESSION = eINSTANCE.getEWorldAgent_BoundsExpression();

		/**
		 * The meta object literal for the '<em><b>Bounds Type</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute EWORLD_AGENT__BOUNDS_TYPE = eINSTANCE.getEWorldAgent_BoundsType();

		/**
		 * The meta object literal for the '{@link gama.impl.ELayerImpl <em>ELayer</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see gama.impl.ELayerImpl
		 * @see gama.impl.GamaPackageImpl#getELayer()
		 * @generated
		 */
		EClass ELAYER = eINSTANCE.getELayer();

		/**
		 * The meta object literal for the '<em><b>Gaml Code</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ELAYER__GAML_CODE = eINSTANCE.getELayer_GamlCode();

		/**
		 * The meta object literal for the '<em><b>Display</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference ELAYER__DISPLAY = eINSTANCE.getELayer_Display();

		/**
		 * The meta object literal for the '<em><b>Type</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ELAYER__TYPE = eINSTANCE.getELayer_Type();

		/**
		 * The meta object literal for the '<em><b>File</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ELAYER__FILE = eINSTANCE.getELayer_File();

		/**
		 * The meta object literal for the '<em><b>Text</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ELAYER__TEXT = eINSTANCE.getELayer_Text();

		/**
		 * The meta object literal for the '<em><b>Size</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ELAYER__SIZE = eINSTANCE.getELayer_Size();

		/**
		 * The meta object literal for the '<em><b>Species</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ELAYER__SPECIES = eINSTANCE.getELayer_Species();

		/**
		 * The meta object literal for the '<em><b>Transparency</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ELAYER__TRANSPARENCY = eINSTANCE.getELayer_Transparency();

		/**
		 * The meta object literal for the '<em><b>Agents</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ELAYER__AGENTS = eINSTANCE.getELayer_Agents();

		/**
		 * The meta object literal for the '<em><b>Position x</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ELAYER__POSITION_X = eINSTANCE.getELayer_Position_x();

		/**
		 * The meta object literal for the '<em><b>Position y</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ELAYER__POSITION_Y = eINSTANCE.getELayer_Position_y();

		/**
		 * The meta object literal for the '<em><b>Size x</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ELAYER__SIZE_X = eINSTANCE.getELayer_Size_x();

		/**
		 * The meta object literal for the '<em><b>Size y</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ELAYER__SIZE_Y = eINSTANCE.getELayer_Size_y();

		/**
		 * The meta object literal for the '<em><b>Aspect</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ELAYER__ASPECT = eINSTANCE.getELayer_Aspect();

		/**
		 * The meta object literal for the '<em><b>Color</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ELAYER__COLOR = eINSTANCE.getELayer_Color();

		/**
		 * The meta object literal for the '<em><b>Is Color Cst</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ELAYER__IS_COLOR_CST = eINSTANCE.getELayer_IsColorCst();

		/**
		 * The meta object literal for the '<em><b>Color RBG</b></em>' attribute list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ELAYER__COLOR_RBG = eINSTANCE.getELayer_ColorRBG();

		/**
		 * The meta object literal for the '<em><b>Grid</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ELAYER__GRID = eINSTANCE.getELayer_Grid();

		/**
		 * The meta object literal for the '<em><b>Refresh</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ELAYER__REFRESH = eINSTANCE.getELayer_Refresh();

		/**
		 * The meta object literal for the '<em><b>Chartlayers</b></em>' reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference ELAYER__CHARTLAYERS = eINSTANCE.getELayer_Chartlayers();

		/**
		 * The meta object literal for the '<em><b>Chart type</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ELAYER__CHART_TYPE = eINSTANCE.getELayer_Chart_type();

		/**
		 * The meta object literal for the '<em><b>Show Lines</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ELAYER__SHOW_LINES = eINSTANCE.getELayer_ShowLines();

		/**
		 * The meta object literal for the '{@link gama.impl.EGraphTopologyNodeImpl <em>EGraph Topology Node</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see gama.impl.EGraphTopologyNodeImpl
		 * @see gama.impl.GamaPackageImpl#getEGraphTopologyNode()
		 * @generated
		 */
		EClass EGRAPH_TOPOLOGY_NODE = eINSTANCE.getEGraphTopologyNode();

		/**
		 * The meta object literal for the '{@link gama.impl.EExperimentLinkImpl <em>EExperiment Link</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see gama.impl.EExperimentLinkImpl
		 * @see gama.impl.GamaPackageImpl#getEExperimentLink()
		 * @generated
		 */
		EClass EEXPERIMENT_LINK = eINSTANCE.getEExperimentLink();

		/**
		 * The meta object literal for the '<em><b>Species</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference EEXPERIMENT_LINK__SPECIES = eINSTANCE.getEExperimentLink_Species();

		/**
		 * The meta object literal for the '<em><b>Experiment</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference EEXPERIMENT_LINK__EXPERIMENT = eINSTANCE.getEExperimentLink_Experiment();

		/**
		 * The meta object literal for the '{@link gama.impl.ELayerAspectImpl <em>ELayer Aspect</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see gama.impl.ELayerAspectImpl
		 * @see gama.impl.GamaPackageImpl#getELayerAspect()
		 * @generated
		 */
		EClass ELAYER_ASPECT = eINSTANCE.getELayerAspect();

		/**
		 * The meta object literal for the '<em><b>Gaml Code</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ELAYER_ASPECT__GAML_CODE = eINSTANCE.getELayerAspect_GamlCode();

		/**
		 * The meta object literal for the '<em><b>Shape</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ELAYER_ASPECT__SHAPE = eINSTANCE.getELayerAspect_Shape();

		/**
		 * The meta object literal for the '<em><b>Color</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ELAYER_ASPECT__COLOR = eINSTANCE.getELayerAspect_Color();

		/**
		 * The meta object literal for the '<em><b>Empty</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ELAYER_ASPECT__EMPTY = eINSTANCE.getELayerAspect_Empty();

		/**
		 * The meta object literal for the '<em><b>Rotate</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ELAYER_ASPECT__ROTATE = eINSTANCE.getELayerAspect_Rotate();

		/**
		 * The meta object literal for the '<em><b>Size</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ELAYER_ASPECT__SIZE = eINSTANCE.getELayerAspect_Size();

		/**
		 * The meta object literal for the '<em><b>Width</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ELAYER_ASPECT__WIDTH = eINSTANCE.getELayerAspect_Width();

		/**
		 * The meta object literal for the '<em><b>Heigth</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ELAYER_ASPECT__HEIGTH = eINSTANCE.getELayerAspect_Heigth();

		/**
		 * The meta object literal for the '<em><b>Radius</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ELAYER_ASPECT__RADIUS = eINSTANCE.getELayerAspect_Radius();

		/**
		 * The meta object literal for the '<em><b>Path</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ELAYER_ASPECT__PATH = eINSTANCE.getELayerAspect_Path();

		/**
		 * The meta object literal for the '<em><b>Text</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ELAYER_ASPECT__TEXT = eINSTANCE.getELayerAspect_Text();

		/**
		 * The meta object literal for the '<em><b>Type</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ELAYER_ASPECT__TYPE = eINSTANCE.getELayerAspect_Type();

		/**
		 * The meta object literal for the '<em><b>Expression</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ELAYER_ASPECT__EXPRESSION = eINSTANCE.getELayerAspect_Expression();

		/**
		 * The meta object literal for the '<em><b>Points</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ELAYER_ASPECT__POINTS = eINSTANCE.getELayerAspect_Points();

		/**
		 * The meta object literal for the '<em><b>At</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ELAYER_ASPECT__AT = eINSTANCE.getELayerAspect_At();

		/**
		 * The meta object literal for the '<em><b>Shape Type</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ELAYER_ASPECT__SHAPE_TYPE = eINSTANCE.getELayerAspect_ShapeType();

		/**
		 * The meta object literal for the '<em><b>Is Color Cst</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ELAYER_ASPECT__IS_COLOR_CST = eINSTANCE.getELayerAspect_IsColorCst();

		/**
		 * The meta object literal for the '<em><b>Text Size</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ELAYER_ASPECT__TEXT_SIZE = eINSTANCE.getELayerAspect_TextSize();

		/**
		 * The meta object literal for the '<em><b>Image Size</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ELAYER_ASPECT__IMAGE_SIZE = eINSTANCE.getELayerAspect_ImageSize();

		/**
		 * The meta object literal for the '<em><b>Color RBG</b></em>' attribute list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ELAYER_ASPECT__COLOR_RBG = eINSTANCE.getELayerAspect_ColorRBG();

		/**
		 * The meta object literal for the '<em><b>Aspect</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference ELAYER_ASPECT__ASPECT = eINSTANCE.getELayerAspect_Aspect();

		/**
		 * The meta object literal for the '<em><b>Depth</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ELAYER_ASPECT__DEPTH = eINSTANCE.getELayerAspect_Depth();

		/**
		 * The meta object literal for the '<em><b>Texture</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ELAYER_ASPECT__TEXTURE = eINSTANCE.getELayerAspect_Texture();

		/**
		 * The meta object literal for the '{@link gama.impl.EGridTopologyImpl <em>EGrid Topology</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see gama.impl.EGridTopologyImpl
		 * @see gama.impl.GamaPackageImpl#getEGridTopology()
		 * @generated
		 */
		EClass EGRID_TOPOLOGY = eINSTANCE.getEGridTopology();

		/**
		 * The meta object literal for the '<em><b>Nb columns</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute EGRID_TOPOLOGY__NB_COLUMNS = eINSTANCE.getEGridTopology_Nb_columns();

		/**
		 * The meta object literal for the '<em><b>Nb rows</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute EGRID_TOPOLOGY__NB_ROWS = eINSTANCE.getEGridTopology_Nb_rows();

		/**
		 * The meta object literal for the '<em><b>Neighbourhood</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute EGRID_TOPOLOGY__NEIGHBOURHOOD = eINSTANCE.getEGridTopology_Neighbourhood();

		/**
		 * The meta object literal for the '<em><b>Neighbourhood Type</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute EGRID_TOPOLOGY__NEIGHBOURHOOD_TYPE = eINSTANCE.getEGridTopology_NeighbourhoodType();

		/**
		 * The meta object literal for the '{@link gama.impl.EContinuousTopologyImpl <em>EContinuous Topology</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see gama.impl.EContinuousTopologyImpl
		 * @see gama.impl.GamaPackageImpl#getEContinuousTopology()
		 * @generated
		 */
		EClass ECONTINUOUS_TOPOLOGY = eINSTANCE.getEContinuousTopology();

		/**
		 * The meta object literal for the '{@link gama.impl.ETopologyImpl <em>ETopology</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see gama.impl.ETopologyImpl
		 * @see gama.impl.GamaPackageImpl#getETopology()
		 * @generated
		 */
		EClass ETOPOLOGY = eINSTANCE.getETopology();

		/**
		 * The meta object literal for the '<em><b>Species</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference ETOPOLOGY__SPECIES = eINSTANCE.getETopology_Species();

		/**
		 * The meta object literal for the '{@link gama.impl.EInheritLinkImpl <em>EInherit Link</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see gama.impl.EInheritLinkImpl
		 * @see gama.impl.GamaPackageImpl#getEInheritLink()
		 * @generated
		 */
		EClass EINHERIT_LINK = eINSTANCE.getEInheritLink();

		/**
		 * The meta object literal for the '<em><b>Parent</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference EINHERIT_LINK__PARENT = eINSTANCE.getEInheritLink_Parent();

		/**
		 * The meta object literal for the '<em><b>Child</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference EINHERIT_LINK__CHILD = eINSTANCE.getEInheritLink_Child();

		/**
		 * The meta object literal for the '{@link gama.impl.EGraphTopologyEdgeImpl <em>EGraph Topology Edge</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see gama.impl.EGraphTopologyEdgeImpl
		 * @see gama.impl.GamaPackageImpl#getEGraphTopologyEdge()
		 * @generated
		 */
		EClass EGRAPH_TOPOLOGY_EDGE = eINSTANCE.getEGraphTopologyEdge();

		/**
		 * The meta object literal for the '{@link gama.impl.EGraphLinkImpl <em>EGraph Link</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see gama.impl.EGraphLinkImpl
		 * @see gama.impl.GamaPackageImpl#getEGraphLink()
		 * @generated
		 */
		EClass EGRAPH_LINK = eINSTANCE.getEGraphLink();

		/**
		 * The meta object literal for the '<em><b>Node</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference EGRAPH_LINK__NODE = eINSTANCE.getEGraphLink_Node();

		/**
		 * The meta object literal for the '<em><b>Edge</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference EGRAPH_LINK__EDGE = eINSTANCE.getEGraphLink_Edge();

		/**
		 * The meta object literal for the '{@link gama.impl.EChartLayerImpl <em>EChart Layer</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see gama.impl.EChartLayerImpl
		 * @see gama.impl.GamaPackageImpl#getEChartLayer()
		 * @generated
		 */
		EClass ECHART_LAYER = eINSTANCE.getEChartLayer();

		/**
		 * The meta object literal for the '<em><b>Style</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ECHART_LAYER__STYLE = eINSTANCE.getEChartLayer_Style();

		/**
		 * The meta object literal for the '<em><b>Color</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ECHART_LAYER__COLOR = eINSTANCE.getEChartLayer_Color();

		/**
		 * The meta object literal for the '<em><b>Value</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ECHART_LAYER__VALUE = eINSTANCE.getEChartLayer_Value();

		/**
		 * The meta object literal for the '{@link gama.impl.EParameterImpl <em>EParameter</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see gama.impl.EParameterImpl
		 * @see gama.impl.GamaPackageImpl#getEParameter()
		 * @generated
		 */
		EClass EPARAMETER = eINSTANCE.getEParameter();

		/**
		 * The meta object literal for the '<em><b>Variable</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute EPARAMETER__VARIABLE = eINSTANCE.getEParameter_Variable();

		/**
		 * The meta object literal for the '<em><b>Min</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute EPARAMETER__MIN = eINSTANCE.getEParameter_Min();

		/**
		 * The meta object literal for the '<em><b>Init</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute EPARAMETER__INIT = eINSTANCE.getEParameter_Init();

		/**
		 * The meta object literal for the '<em><b>Step</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute EPARAMETER__STEP = eINSTANCE.getEParameter_Step();

		/**
		 * The meta object literal for the '<em><b>Max</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute EPARAMETER__MAX = eINSTANCE.getEParameter_Max();

		/**
		 * The meta object literal for the '<em><b>Among</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute EPARAMETER__AMONG = eINSTANCE.getEParameter_Among();

		/**
		 * The meta object literal for the '<em><b>Category</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute EPARAMETER__CATEGORY = eINSTANCE.getEParameter_Category();

		/**
		 * The meta object literal for the '{@link gama.impl.EMonitorImpl <em>EMonitor</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see gama.impl.EMonitorImpl
		 * @see gama.impl.GamaPackageImpl#getEMonitor()
		 * @generated
		 */
		EClass EMONITOR = eINSTANCE.getEMonitor();

		/**
		 * The meta object literal for the '<em><b>Value</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute EMONITOR__VALUE = eINSTANCE.getEMonitor_Value();

	}

} //GamaPackage
