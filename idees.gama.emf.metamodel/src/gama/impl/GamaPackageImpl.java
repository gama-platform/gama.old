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
import gama.EChartLayer;
import gama.EContinuousTopology;
import gama.EDisplay;
import gama.EDisplayLink;
import gama.EExperiment;
import gama.EExperimentLink;
import gama.EGUIExperiment;
import gama.EGamaLink;
import gama.EGamaModel;
import gama.EGamaObject;
import gama.EGraphLink;
import gama.EGraphTopologyEdge;
import gama.EGraphTopologyNode;
import gama.EGridTopology;
import gama.EInheritLink;
import gama.ELayer;
import gama.ELayerAspect;
import gama.EMonitor;
import gama.EParameter;
import gama.EReflex;
import gama.EReflexLink;
import gama.ESpecies;
import gama.ESubSpeciesLink;
import gama.ETopology;
import gama.EVariable;
import gama.EWorldAgent;
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
	private EClass eVariableEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass eWorldAgentEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass eLayerEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass eGraphTopologyNodeEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass eExperimentLinkEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass eLayerAspectEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass eGridTopologyEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass eContinuousTopologyEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass eTopologyEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass eInheritLinkEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass eGraphTopologyEdgeEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass eGraphLinkEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass eChartLayerEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass eParameterEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass eMonitorEClass = null;

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
	public EAttribute getEGamaObject_ColorPicto() {
		return (EAttribute)eGamaObjectEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getEGamaObject_HasError() {
		return (EAttribute)eGamaObjectEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getEGamaObject_Error() {
		return (EAttribute)eGamaObjectEClass.getEStructuralFeatures().get(4);
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
	public EAttribute getESpecies_ReflexList() {
		return (EAttribute)eSpeciesEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getESpecies_Torus() {
		return (EAttribute)eSpeciesEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getESpecies_ExperimentLinks() {
		return (EReference)eSpeciesEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getESpecies_AspectLinks() {
		return (EReference)eSpeciesEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getESpecies_ActionLinks() {
		return (EReference)eSpeciesEClass.getEStructuralFeatures().get(5);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getESpecies_ReflexLinks() {
		return (EReference)eSpeciesEClass.getEStructuralFeatures().get(6);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getESpecies_Shape() {
		return (EAttribute)eSpeciesEClass.getEStructuralFeatures().get(7);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getESpecies_Location() {
		return (EAttribute)eSpeciesEClass.getEStructuralFeatures().get(8);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getESpecies_Size() {
		return (EAttribute)eSpeciesEClass.getEStructuralFeatures().get(9);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getESpecies_Width() {
		return (EAttribute)eSpeciesEClass.getEStructuralFeatures().get(10);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getESpecies_Heigth() {
		return (EAttribute)eSpeciesEClass.getEStructuralFeatures().get(11);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getESpecies_Radius() {
		return (EAttribute)eSpeciesEClass.getEStructuralFeatures().get(12);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getESpecies_MicroSpeciesLinks() {
		return (EReference)eSpeciesEClass.getEStructuralFeatures().get(13);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getESpecies_MacroSpeciesLinks() {
		return (EReference)eSpeciesEClass.getEStructuralFeatures().get(14);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getESpecies_Skills() {
		return (EAttribute)eSpeciesEClass.getEStructuralFeatures().get(15);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getESpecies_Topology() {
		return (EReference)eSpeciesEClass.getEStructuralFeatures().get(16);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getESpecies_InheritsFrom() {
		return (EReference)eSpeciesEClass.getEStructuralFeatures().get(17);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getESpecies_TorusType() {
		return (EAttribute)eSpeciesEClass.getEStructuralFeatures().get(18);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getESpecies_ShapeType() {
		return (EAttribute)eSpeciesEClass.getEStructuralFeatures().get(19);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getESpecies_LocationType() {
		return (EAttribute)eSpeciesEClass.getEStructuralFeatures().get(20);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getESpecies_Points() {
		return (EAttribute)eSpeciesEClass.getEStructuralFeatures().get(21);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getESpecies_ExpressionShape() {
		return (EAttribute)eSpeciesEClass.getEStructuralFeatures().get(22);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getESpecies_ExpressionLoc() {
		return (EAttribute)eSpeciesEClass.getEStructuralFeatures().get(23);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getESpecies_ExpressionTorus() {
		return (EAttribute)eSpeciesEClass.getEStructuralFeatures().get(24);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getESpecies_ShapeFunction() {
		return (EAttribute)eSpeciesEClass.getEStructuralFeatures().get(25);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getESpecies_ShapeUpdate() {
		return (EAttribute)eSpeciesEClass.getEStructuralFeatures().get(26);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getESpecies_ShapeIsFunction() {
		return (EAttribute)eSpeciesEClass.getEStructuralFeatures().get(27);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getESpecies_LocationIsFunction() {
		return (EAttribute)eSpeciesEClass.getEStructuralFeatures().get(28);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getESpecies_LocationFunction() {
		return (EAttribute)eSpeciesEClass.getEStructuralFeatures().get(29);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getESpecies_LocationUpdate() {
		return (EAttribute)eSpeciesEClass.getEStructuralFeatures().get(30);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getESpecies_Init() {
		return (EAttribute)eSpeciesEClass.getEStructuralFeatures().get(31);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getESpecies_InheritingLinks() {
		return (EReference)eSpeciesEClass.getEStructuralFeatures().get(32);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getESpecies_Schedules() {
		return (EAttribute)eSpeciesEClass.getEStructuralFeatures().get(33);
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
	public EAttribute getEAction_GamlCode() {
		return (EAttribute)eActionEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getEAction_ActionLinks() {
		return (EReference)eActionEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getEAction_Variables() {
		return (EReference)eActionEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getEAction_ReturnType() {
		return (EAttribute)eActionEClass.getEStructuralFeatures().get(3);
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
	public EAttribute getEAspect_GamlCode() {
		return (EAttribute)eAspectEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getEAspect_AspectLinks() {
		return (EReference)eAspectEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getEAspect_Layers() {
		return (EReference)eAspectEClass.getEStructuralFeatures().get(2);
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
	public EAttribute getEReflex_GamlCode() {
		return (EAttribute)eReflexEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getEReflex_Condition() {
		return (EAttribute)eReflexEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getEReflex_ReflexLinks() {
		return (EReference)eReflexEClass.getEStructuralFeatures().get(2);
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
	public EReference getEExperiment_ExperimentLink() {
		return (EReference)eExperimentEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getEExperiment_DisplayLinks() {
		return (EReference)eExperimentEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getEExperiment_Parameters() {
		return (EReference)eExperimentEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getEExperiment_Monitors() {
		return (EReference)eExperimentEClass.getEStructuralFeatures().get(3);
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
	public EReference getESubSpeciesLink_Macro() {
		return (EReference)eSubSpeciesLinkEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getESubSpeciesLink_Micro() {
		return (EReference)eSubSpeciesLinkEClass.getEStructuralFeatures().get(1);
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
	public EReference getEActionLink_Action() {
		return (EReference)eActionLinkEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getEActionLink_Species() {
		return (EReference)eActionLinkEClass.getEStructuralFeatures().get(1);
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
	public EReference getEAspectLink_Aspect() {
		return (EReference)eAspectLinkEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getEAspectLink_Species() {
		return (EReference)eAspectLinkEClass.getEStructuralFeatures().get(1);
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
	public EReference getEReflexLink_Reflex() {
		return (EReference)eReflexLinkEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getEReflexLink_Species() {
		return (EReference)eReflexLinkEClass.getEStructuralFeatures().get(1);
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
	public EReference getEDisplayLink_Experiment() {
		return (EReference)eDisplayLinkEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getEDisplayLink_Display() {
		return (EReference)eDisplayLinkEClass.getEStructuralFeatures().get(1);
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
	public EReference getEDisplay_Layers() {
		return (EReference)eDisplayEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getEDisplay_DisplayLink() {
		return (EReference)eDisplayEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getEDisplay_Opengl() {
		return (EAttribute)eDisplayEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getEDisplay_Refresh() {
		return (EAttribute)eDisplayEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getEDisplay_Background() {
		return (EAttribute)eDisplayEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getEDisplay_LayerList() {
		return (EAttribute)eDisplayEClass.getEStructuralFeatures().get(5);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getEDisplay_Color() {
		return (EAttribute)eDisplayEClass.getEStructuralFeatures().get(6);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getEDisplay_IsColorCst() {
		return (EAttribute)eDisplayEClass.getEStructuralFeatures().get(7);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getEDisplay_ColorRBG() {
		return (EAttribute)eDisplayEClass.getEStructuralFeatures().get(8);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getEDisplay_GamlCode() {
		return (EAttribute)eDisplayEClass.getEStructuralFeatures().get(9);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getEDisplay_AmbientLight() {
		return (EAttribute)eDisplayEClass.getEStructuralFeatures().get(10);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getEDisplay_DrawDiffuseLight() {
		return (EAttribute)eDisplayEClass.getEStructuralFeatures().get(11);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getEDisplay_DiffuseLight() {
		return (EAttribute)eDisplayEClass.getEStructuralFeatures().get(12);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getEDisplay_DiffuseLightPos() {
		return (EAttribute)eDisplayEClass.getEStructuralFeatures().get(13);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getEDisplay_ZFighting() {
		return (EAttribute)eDisplayEClass.getEStructuralFeatures().get(14);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getEDisplay_CameraPos() {
		return (EAttribute)eDisplayEClass.getEStructuralFeatures().get(15);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getEDisplay_CameraLookPos() {
		return (EAttribute)eDisplayEClass.getEStructuralFeatures().get(16);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getEDisplay_CameraUpVector() {
		return (EAttribute)eDisplayEClass.getEStructuralFeatures().get(17);
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
	public EAttribute getEVariable_Init() {
		return (EAttribute)eVariableEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getEVariable_Min() {
		return (EAttribute)eVariableEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getEVariable_Max() {
		return (EAttribute)eVariableEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getEVariable_Update() {
		return (EAttribute)eVariableEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getEVariable_Function() {
		return (EAttribute)eVariableEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getEVariable_Type() {
		return (EAttribute)eVariableEClass.getEStructuralFeatures().get(5);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getEVariable_Name() {
		return (EAttribute)eVariableEClass.getEStructuralFeatures().get(6);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getEVariable_HasError() {
		return (EAttribute)eVariableEClass.getEStructuralFeatures().get(7);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getEVariable_Error() {
		return (EAttribute)eVariableEClass.getEStructuralFeatures().get(8);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getEVariable_Owner() {
		return (EReference)eVariableEClass.getEStructuralFeatures().get(9);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getEWorldAgent() {
		return eWorldAgentEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getEWorldAgent_BoundsWidth() {
		return (EAttribute)eWorldAgentEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getEWorldAgent_BoundsHeigth() {
		return (EAttribute)eWorldAgentEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getEWorldAgent_BoundsPath() {
		return (EAttribute)eWorldAgentEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getEWorldAgent_BoundsExpression() {
		return (EAttribute)eWorldAgentEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getEWorldAgent_BoundsType() {
		return (EAttribute)eWorldAgentEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getELayer() {
		return eLayerEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getELayer_GamlCode() {
		return (EAttribute)eLayerEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getELayer_Display() {
		return (EReference)eLayerEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getELayer_Type() {
		return (EAttribute)eLayerEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getELayer_File() {
		return (EAttribute)eLayerEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getELayer_Text() {
		return (EAttribute)eLayerEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getELayer_Size() {
		return (EAttribute)eLayerEClass.getEStructuralFeatures().get(5);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getELayer_Species() {
		return (EAttribute)eLayerEClass.getEStructuralFeatures().get(6);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getELayer_Transparency() {
		return (EAttribute)eLayerEClass.getEStructuralFeatures().get(7);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getELayer_Agents() {
		return (EAttribute)eLayerEClass.getEStructuralFeatures().get(8);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getELayer_Position_x() {
		return (EAttribute)eLayerEClass.getEStructuralFeatures().get(9);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getELayer_Position_y() {
		return (EAttribute)eLayerEClass.getEStructuralFeatures().get(10);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getELayer_Size_x() {
		return (EAttribute)eLayerEClass.getEStructuralFeatures().get(11);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getELayer_Size_y() {
		return (EAttribute)eLayerEClass.getEStructuralFeatures().get(12);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getELayer_Aspect() {
		return (EAttribute)eLayerEClass.getEStructuralFeatures().get(13);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getELayer_Color() {
		return (EAttribute)eLayerEClass.getEStructuralFeatures().get(14);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getELayer_IsColorCst() {
		return (EAttribute)eLayerEClass.getEStructuralFeatures().get(15);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getELayer_ColorRBG() {
		return (EAttribute)eLayerEClass.getEStructuralFeatures().get(16);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getELayer_Grid() {
		return (EAttribute)eLayerEClass.getEStructuralFeatures().get(17);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getELayer_Refresh() {
		return (EAttribute)eLayerEClass.getEStructuralFeatures().get(18);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getELayer_Chartlayers() {
		return (EReference)eLayerEClass.getEStructuralFeatures().get(19);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getELayer_Chart_type() {
		return (EAttribute)eLayerEClass.getEStructuralFeatures().get(20);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getELayer_ShowLines() {
		return (EAttribute)eLayerEClass.getEStructuralFeatures().get(21);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getEGraphTopologyNode() {
		return eGraphTopologyNodeEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getEExperimentLink() {
		return eExperimentLinkEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getEExperimentLink_Species() {
		return (EReference)eExperimentLinkEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getEExperimentLink_Experiment() {
		return (EReference)eExperimentLinkEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getELayerAspect() {
		return eLayerAspectEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getELayerAspect_GamlCode() {
		return (EAttribute)eLayerAspectEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getELayerAspect_Shape() {
		return (EAttribute)eLayerAspectEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getELayerAspect_Color() {
		return (EAttribute)eLayerAspectEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getELayerAspect_Empty() {
		return (EAttribute)eLayerAspectEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getELayerAspect_Rotate() {
		return (EAttribute)eLayerAspectEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getELayerAspect_Size() {
		return (EAttribute)eLayerAspectEClass.getEStructuralFeatures().get(5);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getELayerAspect_Width() {
		return (EAttribute)eLayerAspectEClass.getEStructuralFeatures().get(6);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getELayerAspect_Heigth() {
		return (EAttribute)eLayerAspectEClass.getEStructuralFeatures().get(7);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getELayerAspect_Radius() {
		return (EAttribute)eLayerAspectEClass.getEStructuralFeatures().get(8);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getELayerAspect_Path() {
		return (EAttribute)eLayerAspectEClass.getEStructuralFeatures().get(9);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getELayerAspect_Text() {
		return (EAttribute)eLayerAspectEClass.getEStructuralFeatures().get(10);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getELayerAspect_Type() {
		return (EAttribute)eLayerAspectEClass.getEStructuralFeatures().get(11);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getELayerAspect_Expression() {
		return (EAttribute)eLayerAspectEClass.getEStructuralFeatures().get(12);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getELayerAspect_Points() {
		return (EAttribute)eLayerAspectEClass.getEStructuralFeatures().get(13);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getELayerAspect_At() {
		return (EAttribute)eLayerAspectEClass.getEStructuralFeatures().get(14);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getELayerAspect_ShapeType() {
		return (EAttribute)eLayerAspectEClass.getEStructuralFeatures().get(15);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getELayerAspect_IsColorCst() {
		return (EAttribute)eLayerAspectEClass.getEStructuralFeatures().get(16);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getELayerAspect_TextSize() {
		return (EAttribute)eLayerAspectEClass.getEStructuralFeatures().get(17);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getELayerAspect_ImageSize() {
		return (EAttribute)eLayerAspectEClass.getEStructuralFeatures().get(18);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getELayerAspect_ColorRBG() {
		return (EAttribute)eLayerAspectEClass.getEStructuralFeatures().get(19);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getELayerAspect_Aspect() {
		return (EReference)eLayerAspectEClass.getEStructuralFeatures().get(20);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getELayerAspect_Depth() {
		return (EAttribute)eLayerAspectEClass.getEStructuralFeatures().get(21);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getELayerAspect_Texture() {
		return (EAttribute)eLayerAspectEClass.getEStructuralFeatures().get(22);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getEGridTopology() {
		return eGridTopologyEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getEGridTopology_Nb_columns() {
		return (EAttribute)eGridTopologyEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getEGridTopology_Nb_rows() {
		return (EAttribute)eGridTopologyEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getEGridTopology_Neighbourhood() {
		return (EAttribute)eGridTopologyEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getEGridTopology_NeighbourhoodType() {
		return (EAttribute)eGridTopologyEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getEContinuousTopology() {
		return eContinuousTopologyEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getETopology() {
		return eTopologyEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getETopology_Species() {
		return (EReference)eTopologyEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getEInheritLink() {
		return eInheritLinkEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getEInheritLink_Parent() {
		return (EReference)eInheritLinkEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getEInheritLink_Child() {
		return (EReference)eInheritLinkEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getEGraphTopologyEdge() {
		return eGraphTopologyEdgeEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getEGraphLink() {
		return eGraphLinkEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getEGraphLink_Node() {
		return (EReference)eGraphLinkEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getEGraphLink_Edge() {
		return (EReference)eGraphLinkEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getEChartLayer() {
		return eChartLayerEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getEChartLayer_Style() {
		return (EAttribute)eChartLayerEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getEChartLayer_Color() {
		return (EAttribute)eChartLayerEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getEChartLayer_Value() {
		return (EAttribute)eChartLayerEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getEParameter() {
		return eParameterEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getEParameter_Variable() {
		return (EAttribute)eParameterEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getEParameter_Min() {
		return (EAttribute)eParameterEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getEParameter_Init() {
		return (EAttribute)eParameterEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getEParameter_Step() {
		return (EAttribute)eParameterEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getEParameter_Max() {
		return (EAttribute)eParameterEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getEParameter_Among() {
		return (EAttribute)eParameterEClass.getEStructuralFeatures().get(5);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getEParameter_Category() {
		return (EAttribute)eParameterEClass.getEStructuralFeatures().get(6);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getEMonitor() {
		return eMonitorEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getEMonitor_Value() {
		return (EAttribute)eMonitorEClass.getEStructuralFeatures().get(0);
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
		createEAttribute(eGamaObjectEClass, EGAMA_OBJECT__COLOR_PICTO);
		createEAttribute(eGamaObjectEClass, EGAMA_OBJECT__HAS_ERROR);
		createEAttribute(eGamaObjectEClass, EGAMA_OBJECT__ERROR);

		eSpeciesEClass = createEClass(ESPECIES);
		createEReference(eSpeciesEClass, ESPECIES__VARIABLES);
		createEAttribute(eSpeciesEClass, ESPECIES__REFLEX_LIST);
		createEAttribute(eSpeciesEClass, ESPECIES__TORUS);
		createEReference(eSpeciesEClass, ESPECIES__EXPERIMENT_LINKS);
		createEReference(eSpeciesEClass, ESPECIES__ASPECT_LINKS);
		createEReference(eSpeciesEClass, ESPECIES__ACTION_LINKS);
		createEReference(eSpeciesEClass, ESPECIES__REFLEX_LINKS);
		createEAttribute(eSpeciesEClass, ESPECIES__SHAPE);
		createEAttribute(eSpeciesEClass, ESPECIES__LOCATION);
		createEAttribute(eSpeciesEClass, ESPECIES__SIZE);
		createEAttribute(eSpeciesEClass, ESPECIES__WIDTH);
		createEAttribute(eSpeciesEClass, ESPECIES__HEIGTH);
		createEAttribute(eSpeciesEClass, ESPECIES__RADIUS);
		createEReference(eSpeciesEClass, ESPECIES__MICRO_SPECIES_LINKS);
		createEReference(eSpeciesEClass, ESPECIES__MACRO_SPECIES_LINKS);
		createEAttribute(eSpeciesEClass, ESPECIES__SKILLS);
		createEReference(eSpeciesEClass, ESPECIES__TOPOLOGY);
		createEReference(eSpeciesEClass, ESPECIES__INHERITS_FROM);
		createEAttribute(eSpeciesEClass, ESPECIES__TORUS_TYPE);
		createEAttribute(eSpeciesEClass, ESPECIES__SHAPE_TYPE);
		createEAttribute(eSpeciesEClass, ESPECIES__LOCATION_TYPE);
		createEAttribute(eSpeciesEClass, ESPECIES__POINTS);
		createEAttribute(eSpeciesEClass, ESPECIES__EXPRESSION_SHAPE);
		createEAttribute(eSpeciesEClass, ESPECIES__EXPRESSION_LOC);
		createEAttribute(eSpeciesEClass, ESPECIES__EXPRESSION_TORUS);
		createEAttribute(eSpeciesEClass, ESPECIES__SHAPE_FUNCTION);
		createEAttribute(eSpeciesEClass, ESPECIES__SHAPE_UPDATE);
		createEAttribute(eSpeciesEClass, ESPECIES__SHAPE_IS_FUNCTION);
		createEAttribute(eSpeciesEClass, ESPECIES__LOCATION_IS_FUNCTION);
		createEAttribute(eSpeciesEClass, ESPECIES__LOCATION_FUNCTION);
		createEAttribute(eSpeciesEClass, ESPECIES__LOCATION_UPDATE);
		createEAttribute(eSpeciesEClass, ESPECIES__INIT);
		createEReference(eSpeciesEClass, ESPECIES__INHERITING_LINKS);
		createEAttribute(eSpeciesEClass, ESPECIES__SCHEDULES);

		eActionEClass = createEClass(EACTION);
		createEAttribute(eActionEClass, EACTION__GAML_CODE);
		createEReference(eActionEClass, EACTION__ACTION_LINKS);
		createEReference(eActionEClass, EACTION__VARIABLES);
		createEAttribute(eActionEClass, EACTION__RETURN_TYPE);

		eAspectEClass = createEClass(EASPECT);
		createEAttribute(eAspectEClass, EASPECT__GAML_CODE);
		createEReference(eAspectEClass, EASPECT__ASPECT_LINKS);
		createEReference(eAspectEClass, EASPECT__LAYERS);

		eReflexEClass = createEClass(EREFLEX);
		createEAttribute(eReflexEClass, EREFLEX__GAML_CODE);
		createEAttribute(eReflexEClass, EREFLEX__CONDITION);
		createEReference(eReflexEClass, EREFLEX__REFLEX_LINKS);

		eExperimentEClass = createEClass(EEXPERIMENT);
		createEReference(eExperimentEClass, EEXPERIMENT__EXPERIMENT_LINK);
		createEReference(eExperimentEClass, EEXPERIMENT__DISPLAY_LINKS);
		createEReference(eExperimentEClass, EEXPERIMENT__PARAMETERS);
		createEReference(eExperimentEClass, EEXPERIMENT__MONITORS);

		eguiExperimentEClass = createEClass(EGUI_EXPERIMENT);

		eBatchExperimentEClass = createEClass(EBATCH_EXPERIMENT);

		eGamaLinkEClass = createEClass(EGAMA_LINK);
		createEReference(eGamaLinkEClass, EGAMA_LINK__TARGET);
		createEReference(eGamaLinkEClass, EGAMA_LINK__SOURCE);
		createEReference(eGamaLinkEClass, EGAMA_LINK__MODEL);

		eSubSpeciesLinkEClass = createEClass(ESUB_SPECIES_LINK);
		createEReference(eSubSpeciesLinkEClass, ESUB_SPECIES_LINK__MACRO);
		createEReference(eSubSpeciesLinkEClass, ESUB_SPECIES_LINK__MICRO);

		eActionLinkEClass = createEClass(EACTION_LINK);
		createEReference(eActionLinkEClass, EACTION_LINK__ACTION);
		createEReference(eActionLinkEClass, EACTION_LINK__SPECIES);

		eAspectLinkEClass = createEClass(EASPECT_LINK);
		createEReference(eAspectLinkEClass, EASPECT_LINK__ASPECT);
		createEReference(eAspectLinkEClass, EASPECT_LINK__SPECIES);

		eReflexLinkEClass = createEClass(EREFLEX_LINK);
		createEReference(eReflexLinkEClass, EREFLEX_LINK__REFLEX);
		createEReference(eReflexLinkEClass, EREFLEX_LINK__SPECIES);

		eDisplayLinkEClass = createEClass(EDISPLAY_LINK);
		createEReference(eDisplayLinkEClass, EDISPLAY_LINK__EXPERIMENT);
		createEReference(eDisplayLinkEClass, EDISPLAY_LINK__DISPLAY);

		eDisplayEClass = createEClass(EDISPLAY);
		createEReference(eDisplayEClass, EDISPLAY__LAYERS);
		createEReference(eDisplayEClass, EDISPLAY__DISPLAY_LINK);
		createEAttribute(eDisplayEClass, EDISPLAY__OPENGL);
		createEAttribute(eDisplayEClass, EDISPLAY__REFRESH);
		createEAttribute(eDisplayEClass, EDISPLAY__BACKGROUND);
		createEAttribute(eDisplayEClass, EDISPLAY__LAYER_LIST);
		createEAttribute(eDisplayEClass, EDISPLAY__COLOR);
		createEAttribute(eDisplayEClass, EDISPLAY__IS_COLOR_CST);
		createEAttribute(eDisplayEClass, EDISPLAY__COLOR_RBG);
		createEAttribute(eDisplayEClass, EDISPLAY__GAML_CODE);
		createEAttribute(eDisplayEClass, EDISPLAY__AMBIENT_LIGHT);
		createEAttribute(eDisplayEClass, EDISPLAY__DRAW_DIFFUSE_LIGHT);
		createEAttribute(eDisplayEClass, EDISPLAY__DIFFUSE_LIGHT);
		createEAttribute(eDisplayEClass, EDISPLAY__DIFFUSE_LIGHT_POS);
		createEAttribute(eDisplayEClass, EDISPLAY__ZFIGHTING);
		createEAttribute(eDisplayEClass, EDISPLAY__CAMERA_POS);
		createEAttribute(eDisplayEClass, EDISPLAY__CAMERA_LOOK_POS);
		createEAttribute(eDisplayEClass, EDISPLAY__CAMERA_UP_VECTOR);

		eVariableEClass = createEClass(EVARIABLE);
		createEAttribute(eVariableEClass, EVARIABLE__INIT);
		createEAttribute(eVariableEClass, EVARIABLE__MIN);
		createEAttribute(eVariableEClass, EVARIABLE__MAX);
		createEAttribute(eVariableEClass, EVARIABLE__UPDATE);
		createEAttribute(eVariableEClass, EVARIABLE__FUNCTION);
		createEAttribute(eVariableEClass, EVARIABLE__TYPE);
		createEAttribute(eVariableEClass, EVARIABLE__NAME);
		createEAttribute(eVariableEClass, EVARIABLE__HAS_ERROR);
		createEAttribute(eVariableEClass, EVARIABLE__ERROR);
		createEReference(eVariableEClass, EVARIABLE__OWNER);

		eWorldAgentEClass = createEClass(EWORLD_AGENT);
		createEAttribute(eWorldAgentEClass, EWORLD_AGENT__BOUNDS_WIDTH);
		createEAttribute(eWorldAgentEClass, EWORLD_AGENT__BOUNDS_HEIGTH);
		createEAttribute(eWorldAgentEClass, EWORLD_AGENT__BOUNDS_PATH);
		createEAttribute(eWorldAgentEClass, EWORLD_AGENT__BOUNDS_EXPRESSION);
		createEAttribute(eWorldAgentEClass, EWORLD_AGENT__BOUNDS_TYPE);

		eLayerEClass = createEClass(ELAYER);
		createEAttribute(eLayerEClass, ELAYER__GAML_CODE);
		createEReference(eLayerEClass, ELAYER__DISPLAY);
		createEAttribute(eLayerEClass, ELAYER__TYPE);
		createEAttribute(eLayerEClass, ELAYER__FILE);
		createEAttribute(eLayerEClass, ELAYER__TEXT);
		createEAttribute(eLayerEClass, ELAYER__SIZE);
		createEAttribute(eLayerEClass, ELAYER__SPECIES);
		createEAttribute(eLayerEClass, ELAYER__TRANSPARENCY);
		createEAttribute(eLayerEClass, ELAYER__AGENTS);
		createEAttribute(eLayerEClass, ELAYER__POSITION_X);
		createEAttribute(eLayerEClass, ELAYER__POSITION_Y);
		createEAttribute(eLayerEClass, ELAYER__SIZE_X);
		createEAttribute(eLayerEClass, ELAYER__SIZE_Y);
		createEAttribute(eLayerEClass, ELAYER__ASPECT);
		createEAttribute(eLayerEClass, ELAYER__COLOR);
		createEAttribute(eLayerEClass, ELAYER__IS_COLOR_CST);
		createEAttribute(eLayerEClass, ELAYER__COLOR_RBG);
		createEAttribute(eLayerEClass, ELAYER__GRID);
		createEAttribute(eLayerEClass, ELAYER__REFRESH);
		createEReference(eLayerEClass, ELAYER__CHARTLAYERS);
		createEAttribute(eLayerEClass, ELAYER__CHART_TYPE);
		createEAttribute(eLayerEClass, ELAYER__SHOW_LINES);

		eGraphTopologyNodeEClass = createEClass(EGRAPH_TOPOLOGY_NODE);

		eExperimentLinkEClass = createEClass(EEXPERIMENT_LINK);
		createEReference(eExperimentLinkEClass, EEXPERIMENT_LINK__SPECIES);
		createEReference(eExperimentLinkEClass, EEXPERIMENT_LINK__EXPERIMENT);

		eLayerAspectEClass = createEClass(ELAYER_ASPECT);
		createEAttribute(eLayerAspectEClass, ELAYER_ASPECT__GAML_CODE);
		createEAttribute(eLayerAspectEClass, ELAYER_ASPECT__SHAPE);
		createEAttribute(eLayerAspectEClass, ELAYER_ASPECT__COLOR);
		createEAttribute(eLayerAspectEClass, ELAYER_ASPECT__EMPTY);
		createEAttribute(eLayerAspectEClass, ELAYER_ASPECT__ROTATE);
		createEAttribute(eLayerAspectEClass, ELAYER_ASPECT__SIZE);
		createEAttribute(eLayerAspectEClass, ELAYER_ASPECT__WIDTH);
		createEAttribute(eLayerAspectEClass, ELAYER_ASPECT__HEIGTH);
		createEAttribute(eLayerAspectEClass, ELAYER_ASPECT__RADIUS);
		createEAttribute(eLayerAspectEClass, ELAYER_ASPECT__PATH);
		createEAttribute(eLayerAspectEClass, ELAYER_ASPECT__TEXT);
		createEAttribute(eLayerAspectEClass, ELAYER_ASPECT__TYPE);
		createEAttribute(eLayerAspectEClass, ELAYER_ASPECT__EXPRESSION);
		createEAttribute(eLayerAspectEClass, ELAYER_ASPECT__POINTS);
		createEAttribute(eLayerAspectEClass, ELAYER_ASPECT__AT);
		createEAttribute(eLayerAspectEClass, ELAYER_ASPECT__SHAPE_TYPE);
		createEAttribute(eLayerAspectEClass, ELAYER_ASPECT__IS_COLOR_CST);
		createEAttribute(eLayerAspectEClass, ELAYER_ASPECT__TEXT_SIZE);
		createEAttribute(eLayerAspectEClass, ELAYER_ASPECT__IMAGE_SIZE);
		createEAttribute(eLayerAspectEClass, ELAYER_ASPECT__COLOR_RBG);
		createEReference(eLayerAspectEClass, ELAYER_ASPECT__ASPECT);
		createEAttribute(eLayerAspectEClass, ELAYER_ASPECT__DEPTH);
		createEAttribute(eLayerAspectEClass, ELAYER_ASPECT__TEXTURE);

		eGridTopologyEClass = createEClass(EGRID_TOPOLOGY);
		createEAttribute(eGridTopologyEClass, EGRID_TOPOLOGY__NB_COLUMNS);
		createEAttribute(eGridTopologyEClass, EGRID_TOPOLOGY__NB_ROWS);
		createEAttribute(eGridTopologyEClass, EGRID_TOPOLOGY__NEIGHBOURHOOD);
		createEAttribute(eGridTopologyEClass, EGRID_TOPOLOGY__NEIGHBOURHOOD_TYPE);

		eContinuousTopologyEClass = createEClass(ECONTINUOUS_TOPOLOGY);

		eTopologyEClass = createEClass(ETOPOLOGY);
		createEReference(eTopologyEClass, ETOPOLOGY__SPECIES);

		eInheritLinkEClass = createEClass(EINHERIT_LINK);
		createEReference(eInheritLinkEClass, EINHERIT_LINK__PARENT);
		createEReference(eInheritLinkEClass, EINHERIT_LINK__CHILD);

		eGraphTopologyEdgeEClass = createEClass(EGRAPH_TOPOLOGY_EDGE);

		eGraphLinkEClass = createEClass(EGRAPH_LINK);
		createEReference(eGraphLinkEClass, EGRAPH_LINK__NODE);
		createEReference(eGraphLinkEClass, EGRAPH_LINK__EDGE);

		eChartLayerEClass = createEClass(ECHART_LAYER);
		createEAttribute(eChartLayerEClass, ECHART_LAYER__STYLE);
		createEAttribute(eChartLayerEClass, ECHART_LAYER__COLOR);
		createEAttribute(eChartLayerEClass, ECHART_LAYER__VALUE);

		eParameterEClass = createEClass(EPARAMETER);
		createEAttribute(eParameterEClass, EPARAMETER__VARIABLE);
		createEAttribute(eParameterEClass, EPARAMETER__MIN);
		createEAttribute(eParameterEClass, EPARAMETER__INIT);
		createEAttribute(eParameterEClass, EPARAMETER__STEP);
		createEAttribute(eParameterEClass, EPARAMETER__MAX);
		createEAttribute(eParameterEClass, EPARAMETER__AMONG);
		createEAttribute(eParameterEClass, EPARAMETER__CATEGORY);

		eMonitorEClass = createEClass(EMONITOR);
		createEAttribute(eMonitorEClass, EMONITOR__VALUE);
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
		eWorldAgentEClass.getESuperTypes().add(this.getESpecies());
		eLayerEClass.getESuperTypes().add(this.getEGamaObject());
		eGraphTopologyNodeEClass.getESuperTypes().add(this.getETopology());
		eExperimentLinkEClass.getESuperTypes().add(this.getEGamaLink());
		eLayerAspectEClass.getESuperTypes().add(this.getEGamaObject());
		eGridTopologyEClass.getESuperTypes().add(this.getETopology());
		eContinuousTopologyEClass.getESuperTypes().add(this.getETopology());
		eTopologyEClass.getESuperTypes().add(this.getEGamaObject());
		eInheritLinkEClass.getESuperTypes().add(this.getEGamaLink());
		eGraphTopologyEdgeEClass.getESuperTypes().add(this.getETopology());
		eGraphLinkEClass.getESuperTypes().add(this.getEGamaLink());
		eChartLayerEClass.getESuperTypes().add(this.getEGamaObject());
		eParameterEClass.getESuperTypes().add(this.getEGamaObject());
		eMonitorEClass.getESuperTypes().add(this.getEGamaObject());

		// Initialize classes and features; add operations and parameters
		initEClass(eGamaModelEClass, EGamaModel.class, "EGamaModel", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getEGamaModel_Objects(), this.getEGamaObject(), this.getEGamaObject_Model(), "objects", null, 0, -1, EGamaModel.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getEGamaModel_Name(), ecorePackage.getEString(), "name", null, 0, 1, EGamaModel.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getEGamaModel_Links(), this.getEGamaLink(), this.getEGamaLink_Model(), "links", null, 0, -1, EGamaModel.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(eGamaObjectEClass, EGamaObject.class, "EGamaObject", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getEGamaObject_Name(), ecorePackage.getEString(), "name", null, 0, 1, EGamaObject.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getEGamaObject_Model(), this.getEGamaModel(), this.getEGamaModel_Objects(), "model", null, 1, 1, EGamaObject.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getEGamaObject_ColorPicto(), ecorePackage.getEIntegerObject(), "colorPicto", null, 0, 3, EGamaObject.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getEGamaObject_HasError(), ecorePackage.getEBooleanObject(), "hasError", "false", 0, 1, EGamaObject.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getEGamaObject_Error(), ecorePackage.getEString(), "error", null, 0, 1, EGamaObject.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(eSpeciesEClass, ESpecies.class, "ESpecies", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getESpecies_Variables(), this.getEVariable(), null, "variables", null, 0, -1, ESpecies.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getESpecies_ReflexList(), ecorePackage.getEString(), "reflexList", null, 0, -1, ESpecies.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getESpecies_Torus(), ecorePackage.getEString(), "torus", null, 0, 1, ESpecies.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getESpecies_ExperimentLinks(), this.getEExperimentLink(), null, "experimentLinks", null, 0, -1, ESpecies.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getESpecies_AspectLinks(), this.getEAspectLink(), null, "aspectLinks", null, 0, -1, ESpecies.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getESpecies_ActionLinks(), this.getEActionLink(), null, "actionLinks", null, 0, -1, ESpecies.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getESpecies_ReflexLinks(), this.getEReflexLink(), null, "reflexLinks", null, 0, -1, ESpecies.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getESpecies_Shape(), ecorePackage.getEString(), "shape", null, 0, 1, ESpecies.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getESpecies_Location(), ecorePackage.getEString(), "location", null, 0, 1, ESpecies.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getESpecies_Size(), ecorePackage.getEString(), "size", null, 0, 1, ESpecies.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getESpecies_Width(), ecorePackage.getEString(), "width", null, 0, 1, ESpecies.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getESpecies_Heigth(), ecorePackage.getEString(), "heigth", null, 0, 1, ESpecies.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getESpecies_Radius(), ecorePackage.getEString(), "radius", null, 0, 1, ESpecies.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getESpecies_MicroSpeciesLinks(), this.getESubSpeciesLink(), null, "microSpeciesLinks", null, 0, -1, ESpecies.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getESpecies_MacroSpeciesLinks(), this.getESubSpeciesLink(), null, "macroSpeciesLinks", null, 0, -1, ESpecies.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getESpecies_Skills(), ecorePackage.getEString(), "skills", null, 0, -1, ESpecies.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getESpecies_Topology(), this.getETopology(), null, "topology", null, 0, 1, ESpecies.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getESpecies_InheritsFrom(), this.getESpecies(), null, "inheritsFrom", null, 0, 1, ESpecies.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getESpecies_TorusType(), ecorePackage.getEString(), "torusType", null, 0, 1, ESpecies.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getESpecies_ShapeType(), ecorePackage.getEString(), "shapeType", null, 0, 1, ESpecies.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getESpecies_LocationType(), ecorePackage.getEString(), "locationType", null, 0, 1, ESpecies.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getESpecies_Points(), ecorePackage.getEString(), "points", null, 0, 1, ESpecies.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getESpecies_ExpressionShape(), ecorePackage.getEString(), "expressionShape", null, 0, 1, ESpecies.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getESpecies_ExpressionLoc(), ecorePackage.getEString(), "expressionLoc", null, 0, 1, ESpecies.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getESpecies_ExpressionTorus(), ecorePackage.getEString(), "expressionTorus", null, 0, 1, ESpecies.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getESpecies_ShapeFunction(), ecorePackage.getEString(), "shapeFunction", null, 0, 1, ESpecies.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getESpecies_ShapeUpdate(), ecorePackage.getEString(), "shapeUpdate", null, 0, 1, ESpecies.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getESpecies_ShapeIsFunction(), ecorePackage.getEBooleanObject(), "shapeIsFunction", null, 0, 1, ESpecies.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getESpecies_LocationIsFunction(), ecorePackage.getEBooleanObject(), "locationIsFunction", null, 0, 1, ESpecies.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getESpecies_LocationFunction(), ecorePackage.getEString(), "locationFunction", null, 0, 1, ESpecies.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getESpecies_LocationUpdate(), ecorePackage.getEString(), "locationUpdate", null, 0, 1, ESpecies.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getESpecies_Init(), ecorePackage.getEString(), "init", null, 0, 1, ESpecies.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getESpecies_InheritingLinks(), this.getEInheritLink(), null, "inheritingLinks", null, 0, -1, ESpecies.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getESpecies_Schedules(), ecorePackage.getEString(), "schedules", null, 0, 1, ESpecies.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(eActionEClass, EAction.class, "EAction", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getEAction_GamlCode(), ecorePackage.getEString(), "gamlCode", null, 0, 1, EAction.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
		initEReference(getEAction_ActionLinks(), this.getEActionLink(), null, "actionLinks", null, 0, -1, EAction.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getEAction_Variables(), this.getEVariable(), null, "variables", null, 0, -1, EAction.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getEAction_ReturnType(), ecorePackage.getEString(), "returnType", null, 0, 1, EAction.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(eAspectEClass, EAspect.class, "EAspect", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getEAspect_GamlCode(), ecorePackage.getEString(), "gamlCode", null, 0, 1, EAspect.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
		initEReference(getEAspect_AspectLinks(), this.getEAspectLink(), null, "aspectLinks", null, 0, -1, EAspect.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getEAspect_Layers(), this.getELayerAspect(), null, "layers", null, 0, -1, EAspect.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(eReflexEClass, EReflex.class, "EReflex", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getEReflex_GamlCode(), ecorePackage.getEString(), "gamlCode", null, 0, 1, EReflex.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
		initEAttribute(getEReflex_Condition(), ecorePackage.getEString(), "condition", null, 0, 1, EReflex.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
		initEReference(getEReflex_ReflexLinks(), this.getEReflexLink(), null, "reflexLinks", null, 0, -1, EReflex.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(eExperimentEClass, EExperiment.class, "EExperiment", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getEExperiment_ExperimentLink(), this.getEExperimentLink(), null, "experimentLink", null, 0, 1, EExperiment.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getEExperiment_DisplayLinks(), this.getEDisplayLink(), null, "displayLinks", null, 0, -1, EExperiment.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getEExperiment_Parameters(), this.getEParameter(), null, "parameters", null, 0, -1, EExperiment.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getEExperiment_Monitors(), this.getEMonitor(), null, "monitors", null, 0, -1, EExperiment.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(eguiExperimentEClass, EGUIExperiment.class, "EGUIExperiment", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

		initEClass(eBatchExperimentEClass, EBatchExperiment.class, "EBatchExperiment", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

		initEClass(eGamaLinkEClass, EGamaLink.class, "EGamaLink", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getEGamaLink_Target(), this.getEGamaObject(), null, "target", null, 1, 1, EGamaLink.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getEGamaLink_Source(), this.getEGamaObject(), null, "source", null, 1, 1, EGamaLink.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getEGamaLink_Model(), this.getEGamaModel(), this.getEGamaModel_Links(), "model", null, 1, 1, EGamaLink.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(eSubSpeciesLinkEClass, ESubSpeciesLink.class, "ESubSpeciesLink", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getESubSpeciesLink_Macro(), this.getESpecies(), null, "macro", null, 0, 1, ESubSpeciesLink.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getESubSpeciesLink_Micro(), this.getESpecies(), null, "micro", null, 0, 1, ESubSpeciesLink.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(eActionLinkEClass, EActionLink.class, "EActionLink", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getEActionLink_Action(), this.getEAction(), null, "action", null, 0, 1, EActionLink.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getEActionLink_Species(), this.getESpecies(), null, "species", null, 0, 1, EActionLink.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(eAspectLinkEClass, EAspectLink.class, "EAspectLink", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getEAspectLink_Aspect(), this.getEAspect(), null, "aspect", null, 0, 1, EAspectLink.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getEAspectLink_Species(), this.getESpecies(), null, "species", null, 0, 1, EAspectLink.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(eReflexLinkEClass, EReflexLink.class, "EReflexLink", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getEReflexLink_Reflex(), this.getEReflex(), null, "reflex", null, 0, 1, EReflexLink.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getEReflexLink_Species(), this.getESpecies(), null, "species", null, 0, 1, EReflexLink.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(eDisplayLinkEClass, EDisplayLink.class, "EDisplayLink", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getEDisplayLink_Experiment(), this.getEGUIExperiment(), null, "experiment", null, 0, 1, EDisplayLink.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getEDisplayLink_Display(), this.getEDisplay(), null, "display", null, 0, 1, EDisplayLink.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(eDisplayEClass, EDisplay.class, "EDisplay", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getEDisplay_Layers(), this.getELayer(), null, "layers", null, 0, -1, EDisplay.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getEDisplay_DisplayLink(), this.getEDisplayLink(), null, "displayLink", null, 0, 1, EDisplay.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getEDisplay_Opengl(), ecorePackage.getEBooleanObject(), "opengl", null, 0, 1, EDisplay.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getEDisplay_Refresh(), ecorePackage.getEString(), "refresh", null, 0, 1, EDisplay.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getEDisplay_Background(), ecorePackage.getEString(), "background", null, 0, 1, EDisplay.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getEDisplay_LayerList(), ecorePackage.getEString(), "layerList", null, 0, -1, EDisplay.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getEDisplay_Color(), ecorePackage.getEString(), "color", null, 0, 1, EDisplay.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getEDisplay_IsColorCst(), ecorePackage.getEBooleanObject(), "isColorCst", null, 0, 1, EDisplay.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
		initEAttribute(getEDisplay_ColorRBG(), ecorePackage.getEIntegerObject(), "colorRBG", null, 0, 3, EDisplay.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getEDisplay_GamlCode(), ecorePackage.getEString(), "gamlCode", null, 0, 1, EDisplay.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getEDisplay_AmbientLight(), ecorePackage.getEString(), "ambientLight", null, 0, 1, EDisplay.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getEDisplay_DrawDiffuseLight(), ecorePackage.getEString(), "drawDiffuseLight", null, 0, 1, EDisplay.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getEDisplay_DiffuseLight(), ecorePackage.getEString(), "diffuseLight", null, 0, 1, EDisplay.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getEDisplay_DiffuseLightPos(), ecorePackage.getEString(), "diffuseLightPos", null, 0, 1, EDisplay.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getEDisplay_ZFighting(), ecorePackage.getEString(), "zFighting", null, 0, 1, EDisplay.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getEDisplay_CameraPos(), ecorePackage.getEString(), "cameraPos", null, 0, 1, EDisplay.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getEDisplay_CameraLookPos(), ecorePackage.getEString(), "cameraLookPos", null, 0, 1, EDisplay.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getEDisplay_CameraUpVector(), ecorePackage.getEString(), "cameraUpVector", null, 0, 1, EDisplay.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(eVariableEClass, EVariable.class, "EVariable", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getEVariable_Init(), ecorePackage.getEString(), "init", null, 0, 1, EVariable.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getEVariable_Min(), ecorePackage.getEString(), "min", null, 0, 1, EVariable.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getEVariable_Max(), ecorePackage.getEString(), "max", null, 0, 1, EVariable.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getEVariable_Update(), ecorePackage.getEString(), "update", null, 0, 1, EVariable.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getEVariable_Function(), ecorePackage.getEString(), "function", null, 0, 1, EVariable.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getEVariable_Type(), ecorePackage.getEString(), "type", null, 0, 1, EVariable.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getEVariable_Name(), ecorePackage.getEString(), "name", null, 0, 1, EVariable.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getEVariable_HasError(), ecorePackage.getEBooleanObject(), "hasError", "false", 0, 1, EVariable.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getEVariable_Error(), ecorePackage.getEString(), "error", null, 0, 1, EVariable.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getEVariable_Owner(), this.getEGamaObject(), null, "owner", null, 0, 1, EVariable.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(eWorldAgentEClass, EWorldAgent.class, "EWorldAgent", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getEWorldAgent_BoundsWidth(), ecorePackage.getEString(), "boundsWidth", null, 0, 1, EWorldAgent.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getEWorldAgent_BoundsHeigth(), ecorePackage.getEString(), "boundsHeigth", null, 0, 1, EWorldAgent.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getEWorldAgent_BoundsPath(), ecorePackage.getEString(), "boundsPath", null, 0, 1, EWorldAgent.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getEWorldAgent_BoundsExpression(), ecorePackage.getEString(), "boundsExpression", null, 0, 1, EWorldAgent.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getEWorldAgent_BoundsType(), ecorePackage.getEString(), "boundsType", null, 0, 1, EWorldAgent.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(eLayerEClass, ELayer.class, "ELayer", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getELayer_GamlCode(), ecorePackage.getEString(), "gamlCode", null, 0, 1, ELayer.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
		initEReference(getELayer_Display(), this.getEDisplay(), null, "display", null, 0, 1, ELayer.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getELayer_Type(), ecorePackage.getEString(), "type", null, 0, 1, ELayer.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getELayer_File(), ecorePackage.getEString(), "file", null, 0, 1, ELayer.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getELayer_Text(), ecorePackage.getEString(), "text", null, 0, 1, ELayer.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getELayer_Size(), ecorePackage.getEString(), "size", null, 0, 1, ELayer.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getELayer_Species(), ecorePackage.getEString(), "species", null, 0, 1, ELayer.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getELayer_Transparency(), ecorePackage.getEString(), "transparency", null, 0, 1, ELayer.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getELayer_Agents(), ecorePackage.getEString(), "agents", null, 0, 1, ELayer.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getELayer_Position_x(), ecorePackage.getEString(), "position_x", null, 0, 1, ELayer.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getELayer_Position_y(), ecorePackage.getEString(), "position_y", null, 0, 1, ELayer.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getELayer_Size_x(), ecorePackage.getEString(), "size_x", null, 0, 1, ELayer.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getELayer_Size_y(), ecorePackage.getEString(), "size_y", null, 0, 1, ELayer.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getELayer_Aspect(), ecorePackage.getEString(), "aspect", null, 0, 1, ELayer.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getELayer_Color(), ecorePackage.getEString(), "color", null, 0, 1, ELayer.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getELayer_IsColorCst(), ecorePackage.getEBooleanObject(), "isColorCst", null, 0, 1, ELayer.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
		initEAttribute(getELayer_ColorRBG(), ecorePackage.getEIntegerObject(), "colorRBG", null, 0, 3, ELayer.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getELayer_Grid(), ecorePackage.getEString(), "grid", null, 0, 1, ELayer.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getELayer_Refresh(), ecorePackage.getEString(), "refresh", null, 0, 1, ELayer.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getELayer_Chartlayers(), this.getEChartLayer(), null, "chartlayers", null, 0, -1, ELayer.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getELayer_Chart_type(), ecorePackage.getEString(), "chart_type", null, 0, 1, ELayer.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getELayer_ShowLines(), ecorePackage.getEBoolean(), "showLines", "false", 0, 1, ELayer.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(eGraphTopologyNodeEClass, EGraphTopologyNode.class, "EGraphTopologyNode", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

		initEClass(eExperimentLinkEClass, EExperimentLink.class, "EExperimentLink", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getEExperimentLink_Species(), this.getESpecies(), null, "species", null, 0, 1, EExperimentLink.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getEExperimentLink_Experiment(), this.getEExperiment(), null, "experiment", null, 0, 1, EExperimentLink.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(eLayerAspectEClass, ELayerAspect.class, "ELayerAspect", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getELayerAspect_GamlCode(), ecorePackage.getEString(), "gamlCode", null, 0, 1, ELayerAspect.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
		initEAttribute(getELayerAspect_Shape(), ecorePackage.getEString(), "shape", null, 0, 1, ELayerAspect.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getELayerAspect_Color(), ecorePackage.getEString(), "color", null, 0, 1, ELayerAspect.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getELayerAspect_Empty(), ecorePackage.getEString(), "empty", null, 0, 1, ELayerAspect.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getELayerAspect_Rotate(), ecorePackage.getEString(), "rotate", null, 0, 1, ELayerAspect.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getELayerAspect_Size(), ecorePackage.getEString(), "size", null, 0, 1, ELayerAspect.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getELayerAspect_Width(), ecorePackage.getEString(), "width", null, 0, 1, ELayerAspect.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getELayerAspect_Heigth(), ecorePackage.getEString(), "heigth", null, 0, 1, ELayerAspect.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getELayerAspect_Radius(), ecorePackage.getEString(), "radius", null, 0, 1, ELayerAspect.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getELayerAspect_Path(), ecorePackage.getEString(), "path", null, 0, 1, ELayerAspect.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getELayerAspect_Text(), ecorePackage.getEString(), "text", null, 0, 1, ELayerAspect.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getELayerAspect_Type(), ecorePackage.getEString(), "type", null, 0, 1, ELayerAspect.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getELayerAspect_Expression(), ecorePackage.getEString(), "expression", null, 0, 1, ELayerAspect.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getELayerAspect_Points(), ecorePackage.getEString(), "points", null, 0, 1, ELayerAspect.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getELayerAspect_At(), ecorePackage.getEString(), "at", null, 0, 1, ELayerAspect.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getELayerAspect_ShapeType(), ecorePackage.getEString(), "shapeType", null, 0, 1, ELayerAspect.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
		initEAttribute(getELayerAspect_IsColorCst(), ecorePackage.getEBooleanObject(), "isColorCst", null, 0, 1, ELayerAspect.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
		initEAttribute(getELayerAspect_TextSize(), ecorePackage.getEString(), "textSize", null, 0, 1, ELayerAspect.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
		initEAttribute(getELayerAspect_ImageSize(), ecorePackage.getEString(), "imageSize", null, 0, 1, ELayerAspect.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
		initEAttribute(getELayerAspect_ColorRBG(), ecorePackage.getEIntegerObject(), "colorRBG", null, 0, 3, ELayerAspect.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getELayerAspect_Aspect(), this.getEAspect(), null, "aspect", null, 0, 1, ELayerAspect.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getELayerAspect_Depth(), ecorePackage.getEString(), "depth", null, 0, 1, ELayerAspect.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getELayerAspect_Texture(), ecorePackage.getEString(), "texture", null, 0, 1, ELayerAspect.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(eGridTopologyEClass, EGridTopology.class, "EGridTopology", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getEGridTopology_Nb_columns(), ecorePackage.getEString(), "nb_columns", "100", 0, 1, EGridTopology.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getEGridTopology_Nb_rows(), ecorePackage.getEString(), "nb_rows", "100", 0, 1, EGridTopology.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getEGridTopology_Neighbourhood(), ecorePackage.getEString(), "neighbourhood", "", 0, 1, EGridTopology.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getEGridTopology_NeighbourhoodType(), ecorePackage.getEString(), "neighbourhoodType", "4 (square - von Neumann)", 0, 1, EGridTopology.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(eContinuousTopologyEClass, EContinuousTopology.class, "EContinuousTopology", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

		initEClass(eTopologyEClass, ETopology.class, "ETopology", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getETopology_Species(), this.getESpecies(), null, "species", null, 0, 1, ETopology.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(eInheritLinkEClass, EInheritLink.class, "EInheritLink", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getEInheritLink_Parent(), this.getESpecies(), null, "parent", null, 0, 1, EInheritLink.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getEInheritLink_Child(), this.getESpecies(), null, "child", null, 0, 1, EInheritLink.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(eGraphTopologyEdgeEClass, EGraphTopologyEdge.class, "EGraphTopologyEdge", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

		initEClass(eGraphLinkEClass, EGraphLink.class, "EGraphLink", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getEGraphLink_Node(), this.getESpecies(), null, "node", null, 0, 1, EGraphLink.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getEGraphLink_Edge(), this.getESpecies(), null, "edge", null, 0, 1, EGraphLink.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(eChartLayerEClass, EChartLayer.class, "EChartLayer", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getEChartLayer_Style(), ecorePackage.getEString(), "style", null, 0, 1, EChartLayer.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getEChartLayer_Color(), ecorePackage.getEString(), "color", null, 0, 1, EChartLayer.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getEChartLayer_Value(), ecorePackage.getEString(), "value", null, 0, 1, EChartLayer.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(eParameterEClass, EParameter.class, "EParameter", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getEParameter_Variable(), ecorePackage.getEString(), "variable", null, 0, 1, EParameter.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getEParameter_Min(), ecorePackage.getEString(), "min", null, 0, 1, EParameter.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getEParameter_Init(), ecorePackage.getEString(), "init", null, 0, 1, EParameter.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getEParameter_Step(), ecorePackage.getEString(), "step", null, 0, 1, EParameter.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getEParameter_Max(), ecorePackage.getEString(), "max", null, 0, 1, EParameter.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getEParameter_Among(), ecorePackage.getEString(), "among", null, 0, 1, EParameter.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getEParameter_Category(), ecorePackage.getEString(), "category", null, 0, 1, EParameter.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(eMonitorEClass, EMonitor.class, "EMonitor", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getEMonitor_Value(), ecorePackage.getEString(), "value", null, 0, 1, EMonitor.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		// Create resource
		createResource(eNS_URI);
	}

} //GamaPackageImpl
