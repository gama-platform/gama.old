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
	 * The feature id for the '<em><b>Incoming Links</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EGAMA_OBJECT__INCOMING_LINKS = 2;

	/**
	 * The feature id for the '<em><b>Outcoming Links</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EGAMA_OBJECT__OUTCOMING_LINKS = 3;

	/**
	 * The number of structural features of the '<em>EGama Object</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EGAMA_OBJECT_FEATURE_COUNT = 4;

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
	 * The feature id for the '<em><b>Incoming Links</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ESPECIES__INCOMING_LINKS = EGAMA_OBJECT__INCOMING_LINKS;

	/**
	 * The feature id for the '<em><b>Outcoming Links</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ESPECIES__OUTCOMING_LINKS = EGAMA_OBJECT__OUTCOMING_LINKS;

	/**
	 * The feature id for the '<em><b>Variables</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ESPECIES__VARIABLES = EGAMA_OBJECT_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>ESpecies</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ESPECIES_FEATURE_COUNT = EGAMA_OBJECT_FEATURE_COUNT + 1;

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
	 * The feature id for the '<em><b>Incoming Links</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EACTION__INCOMING_LINKS = EGAMA_OBJECT__INCOMING_LINKS;

	/**
	 * The feature id for the '<em><b>Outcoming Links</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EACTION__OUTCOMING_LINKS = EGAMA_OBJECT__OUTCOMING_LINKS;

	/**
	 * The number of structural features of the '<em>EAction</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EACTION_FEATURE_COUNT = EGAMA_OBJECT_FEATURE_COUNT + 0;

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
	 * The feature id for the '<em><b>Incoming Links</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EASPECT__INCOMING_LINKS = EGAMA_OBJECT__INCOMING_LINKS;

	/**
	 * The feature id for the '<em><b>Outcoming Links</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EASPECT__OUTCOMING_LINKS = EGAMA_OBJECT__OUTCOMING_LINKS;

	/**
	 * The number of structural features of the '<em>EAspect</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EASPECT_FEATURE_COUNT = EGAMA_OBJECT_FEATURE_COUNT + 0;

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
	 * The feature id for the '<em><b>Incoming Links</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EREFLEX__INCOMING_LINKS = EGAMA_OBJECT__INCOMING_LINKS;

	/**
	 * The feature id for the '<em><b>Outcoming Links</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EREFLEX__OUTCOMING_LINKS = EGAMA_OBJECT__OUTCOMING_LINKS;

	/**
	 * The number of structural features of the '<em>EReflex</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EREFLEX_FEATURE_COUNT = EGAMA_OBJECT_FEATURE_COUNT + 0;

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
	 * The feature id for the '<em><b>Incoming Links</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EEXPERIMENT__INCOMING_LINKS = EGAMA_OBJECT__INCOMING_LINKS;

	/**
	 * The feature id for the '<em><b>Outcoming Links</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EEXPERIMENT__OUTCOMING_LINKS = EGAMA_OBJECT__OUTCOMING_LINKS;

	/**
	 * The number of structural features of the '<em>EExperiment</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EEXPERIMENT_FEATURE_COUNT = EGAMA_OBJECT_FEATURE_COUNT + 0;

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
	 * The feature id for the '<em><b>Incoming Links</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EGUI_EXPERIMENT__INCOMING_LINKS = EEXPERIMENT__INCOMING_LINKS;

	/**
	 * The feature id for the '<em><b>Outcoming Links</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EGUI_EXPERIMENT__OUTCOMING_LINKS = EEXPERIMENT__OUTCOMING_LINKS;

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
	 * The feature id for the '<em><b>Incoming Links</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EBATCH_EXPERIMENT__INCOMING_LINKS = EEXPERIMENT__INCOMING_LINKS;

	/**
	 * The feature id for the '<em><b>Outcoming Links</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EBATCH_EXPERIMENT__OUTCOMING_LINKS = EEXPERIMENT__OUTCOMING_LINKS;

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
	 * The number of structural features of the '<em>ESub Species Link</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ESUB_SPECIES_LINK_FEATURE_COUNT = EGAMA_LINK_FEATURE_COUNT + 0;

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
	 * The number of structural features of the '<em>EAction Link</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EACTION_LINK_FEATURE_COUNT = EGAMA_LINK_FEATURE_COUNT + 0;

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
	 * The number of structural features of the '<em>EAspect Link</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EASPECT_LINK_FEATURE_COUNT = EGAMA_LINK_FEATURE_COUNT + 0;

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
	 * The number of structural features of the '<em>EReflex Link</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EREFLEX_LINK_FEATURE_COUNT = EGAMA_LINK_FEATURE_COUNT + 0;

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
	 * The number of structural features of the '<em>EDisplay Link</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EDISPLAY_LINK_FEATURE_COUNT = EGAMA_LINK_FEATURE_COUNT + 0;

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
	 * The feature id for the '<em><b>Incoming Links</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EDISPLAY__INCOMING_LINKS = EGAMA_OBJECT__INCOMING_LINKS;

	/**
	 * The feature id for the '<em><b>Outcoming Links</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EDISPLAY__OUTCOMING_LINKS = EGAMA_OBJECT__OUTCOMING_LINKS;

	/**
	 * The number of structural features of the '<em>EDisplay</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EDISPLAY_FEATURE_COUNT = EGAMA_OBJECT_FEATURE_COUNT + 0;

	/**
	 * The meta object id for the '{@link gama.impl.EGridImpl <em>EGrid</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see gama.impl.EGridImpl
	 * @see gama.impl.GamaPackageImpl#getEGrid()
	 * @generated
	 */
	int EGRID = 16;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EGRID__NAME = ESPECIES__NAME;

	/**
	 * The feature id for the '<em><b>Model</b></em>' container reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EGRID__MODEL = ESPECIES__MODEL;

	/**
	 * The feature id for the '<em><b>Incoming Links</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EGRID__INCOMING_LINKS = ESPECIES__INCOMING_LINKS;

	/**
	 * The feature id for the '<em><b>Outcoming Links</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EGRID__OUTCOMING_LINKS = ESPECIES__OUTCOMING_LINKS;

	/**
	 * The feature id for the '<em><b>Variables</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EGRID__VARIABLES = ESPECIES__VARIABLES;

	/**
	 * The feature id for the '<em><b>Nb columns</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EGRID__NB_COLUMNS = ESPECIES_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Nb rows</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EGRID__NB_ROWS = ESPECIES_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Neighbourhood</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EGRID__NEIGHBOURHOOD = ESPECIES_FEATURE_COUNT + 2;

	/**
	 * The number of structural features of the '<em>EGrid</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EGRID_FEATURE_COUNT = ESPECIES_FEATURE_COUNT + 3;

	/**
	 * The meta object id for the '{@link gama.impl.EVariableImpl <em>EVariable</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see gama.impl.EVariableImpl
	 * @see gama.impl.GamaPackageImpl#getEVariable()
	 * @generated
	 */
	int EVARIABLE = 17;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EVARIABLE__NAME = 0;

	/**
	 * The feature id for the '<em><b>Init</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EVARIABLE__INIT = 1;

	/**
	 * The feature id for the '<em><b>Min</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EVARIABLE__MIN = 2;

	/**
	 * The feature id for the '<em><b>Max</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EVARIABLE__MAX = 3;

	/**
	 * The feature id for the '<em><b>Update</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EVARIABLE__UPDATE = 4;

	/**
	 * The feature id for the '<em><b>Function</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EVARIABLE__FUNCTION = 5;

	/**
	 * The feature id for the '<em><b>Type</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EVARIABLE__TYPE = 6;

	/**
	 * The number of structural features of the '<em>EVariable</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EVARIABLE_FEATURE_COUNT = 7;


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
	 * Returns the meta object for the reference list '{@link gama.EGamaObject#getIncomingLinks <em>Incoming Links</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference list '<em>Incoming Links</em>'.
	 * @see gama.EGamaObject#getIncomingLinks()
	 * @see #getEGamaObject()
	 * @generated
	 */
	EReference getEGamaObject_IncomingLinks();

	/**
	 * Returns the meta object for the reference list '{@link gama.EGamaObject#getOutcomingLinks <em>Outcoming Links</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference list '<em>Outcoming Links</em>'.
	 * @see gama.EGamaObject#getOutcomingLinks()
	 * @see #getEGamaObject()
	 * @generated
	 */
	EReference getEGamaObject_OutcomingLinks();

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
	 * Returns the meta object for class '{@link gama.EAction <em>EAction</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>EAction</em>'.
	 * @see gama.EAction
	 * @generated
	 */
	EClass getEAction();

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
	 * Returns the meta object for class '{@link gama.EReflex <em>EReflex</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>EReflex</em>'.
	 * @see gama.EReflex
	 * @generated
	 */
	EClass getEReflex();

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
	 * Returns the meta object for class '{@link gama.EActionLink <em>EAction Link</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>EAction Link</em>'.
	 * @see gama.EActionLink
	 * @generated
	 */
	EClass getEActionLink();

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
	 * Returns the meta object for class '{@link gama.EReflexLink <em>EReflex Link</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>EReflex Link</em>'.
	 * @see gama.EReflexLink
	 * @generated
	 */
	EClass getEReflexLink();

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
	 * Returns the meta object for class '{@link gama.EDisplay <em>EDisplay</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>EDisplay</em>'.
	 * @see gama.EDisplay
	 * @generated
	 */
	EClass getEDisplay();

	/**
	 * Returns the meta object for class '{@link gama.EGrid <em>EGrid</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>EGrid</em>'.
	 * @see gama.EGrid
	 * @generated
	 */
	EClass getEGrid();

	/**
	 * Returns the meta object for the attribute '{@link gama.EGrid#getNb_columns <em>Nb columns</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Nb columns</em>'.
	 * @see gama.EGrid#getNb_columns()
	 * @see #getEGrid()
	 * @generated
	 */
	EAttribute getEGrid_Nb_columns();

	/**
	 * Returns the meta object for the attribute '{@link gama.EGrid#getNb_rows <em>Nb rows</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Nb rows</em>'.
	 * @see gama.EGrid#getNb_rows()
	 * @see #getEGrid()
	 * @generated
	 */
	EAttribute getEGrid_Nb_rows();

	/**
	 * Returns the meta object for the attribute '{@link gama.EGrid#getNeighbourhood <em>Neighbourhood</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Neighbourhood</em>'.
	 * @see gama.EGrid#getNeighbourhood()
	 * @see #getEGrid()
	 * @generated
	 */
	EAttribute getEGrid_Neighbourhood();

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
		 * The meta object literal for the '<em><b>Incoming Links</b></em>' reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference EGAMA_OBJECT__INCOMING_LINKS = eINSTANCE.getEGamaObject_IncomingLinks();

		/**
		 * The meta object literal for the '<em><b>Outcoming Links</b></em>' reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference EGAMA_OBJECT__OUTCOMING_LINKS = eINSTANCE.getEGamaObject_OutcomingLinks();

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
		 * The meta object literal for the '{@link gama.impl.EActionImpl <em>EAction</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see gama.impl.EActionImpl
		 * @see gama.impl.GamaPackageImpl#getEAction()
		 * @generated
		 */
		EClass EACTION = eINSTANCE.getEAction();

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
		 * The meta object literal for the '{@link gama.impl.EReflexImpl <em>EReflex</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see gama.impl.EReflexImpl
		 * @see gama.impl.GamaPackageImpl#getEReflex()
		 * @generated
		 */
		EClass EREFLEX = eINSTANCE.getEReflex();

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
		 * The meta object literal for the '{@link gama.impl.EActionLinkImpl <em>EAction Link</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see gama.impl.EActionLinkImpl
		 * @see gama.impl.GamaPackageImpl#getEActionLink()
		 * @generated
		 */
		EClass EACTION_LINK = eINSTANCE.getEActionLink();

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
		 * The meta object literal for the '{@link gama.impl.EReflexLinkImpl <em>EReflex Link</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see gama.impl.EReflexLinkImpl
		 * @see gama.impl.GamaPackageImpl#getEReflexLink()
		 * @generated
		 */
		EClass EREFLEX_LINK = eINSTANCE.getEReflexLink();

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
		 * The meta object literal for the '{@link gama.impl.EDisplayImpl <em>EDisplay</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see gama.impl.EDisplayImpl
		 * @see gama.impl.GamaPackageImpl#getEDisplay()
		 * @generated
		 */
		EClass EDISPLAY = eINSTANCE.getEDisplay();

		/**
		 * The meta object literal for the '{@link gama.impl.EGridImpl <em>EGrid</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see gama.impl.EGridImpl
		 * @see gama.impl.GamaPackageImpl#getEGrid()
		 * @generated
		 */
		EClass EGRID = eINSTANCE.getEGrid();

		/**
		 * The meta object literal for the '<em><b>Nb columns</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute EGRID__NB_COLUMNS = eINSTANCE.getEGrid_Nb_columns();

		/**
		 * The meta object literal for the '<em><b>Nb rows</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute EGRID__NB_ROWS = eINSTANCE.getEGrid_Nb_rows();

		/**
		 * The meta object literal for the '<em><b>Neighbourhood</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute EGRID__NEIGHBOURHOOD = eINSTANCE.getEGrid_Neighbourhood();

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
		 * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute EVARIABLE__NAME = eINSTANCE.getEVariable_Name();

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

	}

} //GamaPackage
