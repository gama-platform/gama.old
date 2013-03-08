/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package gama.impl;

import gama.EAction;
import gama.EActionLink;
import gama.EAspect;
import gama.EAspectLink;
import gama.EBatchExperiment;
import gama.EDisplay;
import gama.EDisplayLink;
import gama.EExperiment;
import gama.EGUIExperiment;
import gama.EGamaLink;
import gama.EGamaModel;
import gama.EGamaObject;
import gama.EGrid;
import gama.EReflex;
import gama.EReflexLink;
import gama.ESpecies;
import gama.ESubSpeciesLink;
import gama.EVariable;
import gama.GamaFactory;
import gama.GamaPackage;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

import org.eclipse.emf.ecore.impl.EPackageImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Package</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class GamaPackageImpl extends EPackageImpl implements GamaPackage {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass eGamaModelEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass eGamaObjectEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass eSpeciesEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass eActionEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass eAspectEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass eReflexEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass eExperimentEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass eguiExperimentEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass eBatchExperimentEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass eGamaLinkEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass eSubSpeciesLinkEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass eActionLinkEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass eAspectLinkEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass eReflexLinkEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass eDisplayLinkEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass eDisplayEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass eGridEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass eVariableEClass = null;

	/**
	 * Creates an instance of the model <b>Package</b>, registered with
	 * {@link org.eclipse.emf.ecore.EPackage.Registry EPackage.Registry} by the package
	 * package URI value.
	 * <p>Note: the correct way to create the package is via the static
	 * factory method {@link #init init()}, which also performs
	 * initialization of the package, or returns the registered package,
	 * if one already exists.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.emf.ecore.EPackage.Registry
	 * @see gama.GamaPackage#eNS_URI
	 * @see #init()
	 * @generated
	 */
	private GamaPackageImpl() {
		super(eNS_URI, GamaFactory.eINSTANCE);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private static boolean isInited = false;

	/**
	 * Creates, registers, and initializes the <b>Package</b> for this model, and for any others upon which it depends.
	 * 
	 * <p>This method is used to initialize {@link GamaPackage#eINSTANCE} when that field is accessed.
	 * Clients should not invoke it directly. Instead, they should simply access that field to obtain the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #eNS_URI
	 * @see #createPackageContents()
	 * @see #initializePackageContents()
	 * @generated
	 */
	public static GamaPackage init() {
		if (isInited) return (GamaPackage)EPackage.Registry.INSTANCE.getEPackage(GamaPackage.eNS_URI);

		// Obtain or create and register package
		GamaPackageImpl theGamaPackage = (GamaPackageImpl)(EPackage.Registry.INSTANCE.get(eNS_URI) instanceof GamaPackageImpl ? EPackage.Registry.INSTANCE.get(eNS_URI) : new GamaPackageImpl());

		isInited = true;

		// Create package meta-data objects
		theGamaPackage.createPackageContents();

		// Initialize created meta-data
		theGamaPackage.initializePackageContents();

		// Mark meta-data to indicate it can't be changed
		theGamaPackage.freeze();

  
		// Update the registry and return the package
		EPackage.Registry.INSTANCE.put(GamaPackage.eNS_URI, theGamaPackage);
		return theGamaPackage;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getEGamaModel() {
		return eGamaModelEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getEGamaModel_Objects() {
		return (EReference)eGamaModelEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getEGamaModel_Name() {
		return (EAttribute)eGamaModelEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getEGamaModel_Links() {
		return (EReference)eGamaModelEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getEGamaObject() {
		return eGamaObjectEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getEGamaObject_Name() {
		return (EAttribute)eGamaObjectEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getEGamaObject_Model() {
		return (EReference)eGamaObjectEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getEGamaObject_IncomingLinks() {
		return (EReference)eGamaObjectEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getEGamaObject_OutcomingLinks() {
		return (EReference)eGamaObjectEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getESpecies() {
		return eSpeciesEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getESpecies_Variables() {
		return (EReference)eSpeciesEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getEAction() {
		return eActionEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getEAspect() {
		return eAspectEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getEReflex() {
		return eReflexEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getEExperiment() {
		return eExperimentEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getEGUIExperiment() {
		return eguiExperimentEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getEBatchExperiment() {
		return eBatchExperimentEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getEGamaLink() {
		return eGamaLinkEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getEGamaLink_Target() {
		return (EReference)eGamaLinkEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getEGamaLink_Source() {
		return (EReference)eGamaLinkEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getEGamaLink_Model() {
		return (EReference)eGamaLinkEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getESubSpeciesLink() {
		return eSubSpeciesLinkEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getEActionLink() {
		return eActionLinkEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getEAspectLink() {
		return eAspectLinkEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getEReflexLink() {
		return eReflexLinkEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getEDisplayLink() {
		return eDisplayLinkEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getEDisplay() {
		return eDisplayEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getEGrid() {
		return eGridEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getEGrid_Nb_columns() {
		return (EAttribute)eGridEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getEGrid_Nb_rows() {
		return (EAttribute)eGridEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getEGrid_Neighbourhood() {
		return (EAttribute)eGridEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getEVariable() {
		return eVariableEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getEVariable_Name() {
		return (EAttribute)eVariableEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getEVariable_Init() {
		return (EAttribute)eVariableEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getEVariable_Min() {
		return (EAttribute)eVariableEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getEVariable_Max() {
		return (EAttribute)eVariableEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getEVariable_Update() {
		return (EAttribute)eVariableEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getEVariable_Function() {
		return (EAttribute)eVariableEClass.getEStructuralFeatures().get(5);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getEVariable_Type() {
		return (EAttribute)eVariableEClass.getEStructuralFeatures().get(6);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public GamaFactory getGamaFactory() {
		return (GamaFactory)getEFactoryInstance();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private boolean isCreated = false;

	/**
	 * Creates the meta-model objects for the package.  This method is
	 * guarded to have no affect on any invocation but its first.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void createPackageContents() {
		if (isCreated) return;
		isCreated = true;

		// Create classes and their features
		eGamaModelEClass = createEClass(EGAMA_MODEL);
		createEReference(eGamaModelEClass, EGAMA_MODEL__OBJECTS);
		createEAttribute(eGamaModelEClass, EGAMA_MODEL__NAME);
		createEReference(eGamaModelEClass, EGAMA_MODEL__LINKS);

		eGamaObjectEClass = createEClass(EGAMA_OBJECT);
		createEAttribute(eGamaObjectEClass, EGAMA_OBJECT__NAME);
		createEReference(eGamaObjectEClass, EGAMA_OBJECT__MODEL);
		createEReference(eGamaObjectEClass, EGAMA_OBJECT__INCOMING_LINKS);
		createEReference(eGamaObjectEClass, EGAMA_OBJECT__OUTCOMING_LINKS);

		eSpeciesEClass = createEClass(ESPECIES);
		createEReference(eSpeciesEClass, ESPECIES__VARIABLES);

		eActionEClass = createEClass(EACTION);

		eAspectEClass = createEClass(EASPECT);

		eReflexEClass = createEClass(EREFLEX);

		eExperimentEClass = createEClass(EEXPERIMENT);

		eguiExperimentEClass = createEClass(EGUI_EXPERIMENT);

		eBatchExperimentEClass = createEClass(EBATCH_EXPERIMENT);

		eGamaLinkEClass = createEClass(EGAMA_LINK);
		createEReference(eGamaLinkEClass, EGAMA_LINK__TARGET);
		createEReference(eGamaLinkEClass, EGAMA_LINK__SOURCE);
		createEReference(eGamaLinkEClass, EGAMA_LINK__MODEL);

		eSubSpeciesLinkEClass = createEClass(ESUB_SPECIES_LINK);

		eActionLinkEClass = createEClass(EACTION_LINK);

		eAspectLinkEClass = createEClass(EASPECT_LINK);

		eReflexLinkEClass = createEClass(EREFLEX_LINK);

		eDisplayLinkEClass = createEClass(EDISPLAY_LINK);

		eDisplayEClass = createEClass(EDISPLAY);

		eGridEClass = createEClass(EGRID);
		createEAttribute(eGridEClass, EGRID__NB_COLUMNS);
		createEAttribute(eGridEClass, EGRID__NB_ROWS);
		createEAttribute(eGridEClass, EGRID__NEIGHBOURHOOD);

		eVariableEClass = createEClass(EVARIABLE);
		createEAttribute(eVariableEClass, EVARIABLE__NAME);
		createEAttribute(eVariableEClass, EVARIABLE__INIT);
		createEAttribute(eVariableEClass, EVARIABLE__MIN);
		createEAttribute(eVariableEClass, EVARIABLE__MAX);
		createEAttribute(eVariableEClass, EVARIABLE__UPDATE);
		createEAttribute(eVariableEClass, EVARIABLE__FUNCTION);
		createEAttribute(eVariableEClass, EVARIABLE__TYPE);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private boolean isInitialized = false;

	/**
	 * Complete the initialization of the package and its meta-model.  This
	 * method is guarded to have no affect on any invocation but its first.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void initializePackageContents() {
		if (isInitialized) return;
		isInitialized = true;

		// Initialize package
		setName(eNAME);
		setNsPrefix(eNS_PREFIX);
		setNsURI(eNS_URI);

		// Create type parameters

		// Set bounds for type parameters

		// Add supertypes to classes
		eSpeciesEClass.getESuperTypes().add(this.getEGamaObject());
		eActionEClass.getESuperTypes().add(this.getEGamaObject());
		eAspectEClass.getESuperTypes().add(this.getEGamaObject());
		eReflexEClass.getESuperTypes().add(this.getEGamaObject());
		eExperimentEClass.getESuperTypes().add(this.getEGamaObject());
		eguiExperimentEClass.getESuperTypes().add(this.getEExperiment());
		eBatchExperimentEClass.getESuperTypes().add(this.getEExperiment());
		eSubSpeciesLinkEClass.getESuperTypes().add(this.getEGamaLink());
		eActionLinkEClass.getESuperTypes().add(this.getEGamaLink());
		eAspectLinkEClass.getESuperTypes().add(this.getEGamaLink());
		eReflexLinkEClass.getESuperTypes().add(this.getEGamaLink());
		eDisplayLinkEClass.getESuperTypes().add(this.getEGamaLink());
		eDisplayEClass.getESuperTypes().add(this.getEGamaObject());
		eGridEClass.getESuperTypes().add(this.getESpecies());

		// Initialize classes and features; add operations and parameters
		initEClass(eGamaModelEClass, EGamaModel.class, "EGamaModel", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getEGamaModel_Objects(), this.getEGamaObject(), this.getEGamaObject_Model(), "objects", null, 0, -1, EGamaModel.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getEGamaModel_Name(), ecorePackage.getEString(), "name", null, 0, 1, EGamaModel.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getEGamaModel_Links(), this.getEGamaLink(), this.getEGamaLink_Model(), "links", null, 0, -1, EGamaModel.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(eGamaObjectEClass, EGamaObject.class, "EGamaObject", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getEGamaObject_Name(), ecorePackage.getEString(), "name", null, 0, 1, EGamaObject.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getEGamaObject_Model(), this.getEGamaModel(), this.getEGamaModel_Objects(), "model", null, 1, 1, EGamaObject.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getEGamaObject_IncomingLinks(), this.getEGamaLink(), this.getEGamaLink_Target(), "incomingLinks", null, 0, -1, EGamaObject.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getEGamaObject_OutcomingLinks(), this.getEGamaLink(), this.getEGamaLink_Source(), "outcomingLinks", null, 0, -1, EGamaObject.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(eSpeciesEClass, ESpecies.class, "ESpecies", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getESpecies_Variables(), this.getEVariable(), null, "variables", null, 0, -1, ESpecies.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(eActionEClass, EAction.class, "EAction", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

		initEClass(eAspectEClass, EAspect.class, "EAspect", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

		initEClass(eReflexEClass, EReflex.class, "EReflex", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

		initEClass(eExperimentEClass, EExperiment.class, "EExperiment", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

		initEClass(eguiExperimentEClass, EGUIExperiment.class, "EGUIExperiment", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

		initEClass(eBatchExperimentEClass, EBatchExperiment.class, "EBatchExperiment", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

		initEClass(eGamaLinkEClass, EGamaLink.class, "EGamaLink", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getEGamaLink_Target(), this.getEGamaObject(), this.getEGamaObject_IncomingLinks(), "target", null, 1, 1, EGamaLink.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getEGamaLink_Source(), this.getEGamaObject(), this.getEGamaObject_OutcomingLinks(), "source", null, 1, 1, EGamaLink.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getEGamaLink_Model(), this.getEGamaModel(), this.getEGamaModel_Links(), "model", null, 1, 1, EGamaLink.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(eSubSpeciesLinkEClass, ESubSpeciesLink.class, "ESubSpeciesLink", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

		initEClass(eActionLinkEClass, EActionLink.class, "EActionLink", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

		initEClass(eAspectLinkEClass, EAspectLink.class, "EAspectLink", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

		initEClass(eReflexLinkEClass, EReflexLink.class, "EReflexLink", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

		initEClass(eDisplayLinkEClass, EDisplayLink.class, "EDisplayLink", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

		initEClass(eDisplayEClass, EDisplay.class, "EDisplay", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

		initEClass(eGridEClass, EGrid.class, "EGrid", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getEGrid_Nb_columns(), ecorePackage.getEString(), "nb_columns", null, 0, 1, EGrid.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getEGrid_Nb_rows(), ecorePackage.getEString(), "nb_rows", null, 0, 1, EGrid.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getEGrid_Neighbourhood(), ecorePackage.getEString(), "neighbourhood", null, 0, 1, EGrid.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(eVariableEClass, EVariable.class, "EVariable", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getEVariable_Name(), ecorePackage.getEString(), "name", null, 0, 1, EVariable.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getEVariable_Init(), ecorePackage.getEString(), "init", null, 0, 1, EVariable.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getEVariable_Min(), ecorePackage.getEString(), "min", null, 0, 1, EVariable.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getEVariable_Max(), ecorePackage.getEString(), "max", null, 0, 1, EVariable.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getEVariable_Update(), ecorePackage.getEString(), "update", null, 0, 1, EVariable.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getEVariable_Function(), ecorePackage.getEString(), "function", null, 0, 1, EVariable.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getEVariable_Type(), ecorePackage.getEString(), "type", null, 0, 1, EVariable.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		// Create resource
		createResource(eNS_URI);
	}

} //GamaPackageImpl
