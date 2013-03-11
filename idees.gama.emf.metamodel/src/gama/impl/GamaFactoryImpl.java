/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package gama.impl;

import gama.*;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;

import org.eclipse.emf.ecore.impl.EFactoryImpl;

import org.eclipse.emf.ecore.plugin.EcorePlugin;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Factory</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class GamaFactoryImpl extends EFactoryImpl implements GamaFactory {
	/**
	 * Creates the default factory implementation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static GamaFactory init() {
		try {
			GamaFactory theGamaFactory = (GamaFactory)EPackage.Registry.INSTANCE.getEFactory("http://gama/1.0"); 
			if (theGamaFactory != null) {
				return theGamaFactory;
			}
		}
		catch (Exception exception) {
			EcorePlugin.INSTANCE.log(exception);
		}
		return new GamaFactoryImpl();
	}

	/**
	 * Creates an instance of the factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public GamaFactoryImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EObject create(EClass eClass) {
		switch (eClass.getClassifierID()) {
			case GamaPackage.EGAMA_MODEL: return createEGamaModel();
			case GamaPackage.EGAMA_OBJECT: return createEGamaObject();
			case GamaPackage.ESPECIES: return createESpecies();
			case GamaPackage.EACTION: return createEAction();
			case GamaPackage.EASPECT: return createEAspect();
			case GamaPackage.EREFLEX: return createEReflex();
			case GamaPackage.EEXPERIMENT: return createEExperiment();
			case GamaPackage.EGUI_EXPERIMENT: return createEGUIExperiment();
			case GamaPackage.EBATCH_EXPERIMENT: return createEBatchExperiment();
			case GamaPackage.EGAMA_LINK: return createEGamaLink();
			case GamaPackage.ESUB_SPECIES_LINK: return createESubSpeciesLink();
			case GamaPackage.EACTION_LINK: return createEActionLink();
			case GamaPackage.EASPECT_LINK: return createEAspectLink();
			case GamaPackage.EREFLEX_LINK: return createEReflexLink();
			case GamaPackage.EDISPLAY_LINK: return createEDisplayLink();
			case GamaPackage.EDISPLAY: return createEDisplay();
			case GamaPackage.EGRID: return createEGrid();
			case GamaPackage.EVARIABLE: return createEVariable();
			case GamaPackage.EWORLD_AGENT: return createEWorldAgent();
			case GamaPackage.ELAYER: return createELayer();
			case GamaPackage.EGRAPH: return createEGraph();
			default:
				throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier");
		}
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EGamaModel createEGamaModel() {
		EGamaModelImpl eGamaModel = new EGamaModelImpl();
		return eGamaModel;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EGamaObject createEGamaObject() {
		EGamaObjectImpl eGamaObject = new EGamaObjectImpl();
		return eGamaObject;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ESpecies createESpecies() {
		ESpeciesImpl eSpecies = new ESpeciesImpl();
		return eSpecies;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAction createEAction() {
		EActionImpl eAction = new EActionImpl();
		return eAction;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAspect createEAspect() {
		EAspectImpl eAspect = new EAspectImpl();
		return eAspect;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReflex createEReflex() {
		EReflexImpl eReflex = new EReflexImpl();
		return eReflex;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EExperiment createEExperiment() {
		EExperimentImpl eExperiment = new EExperimentImpl();
		return eExperiment;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EGUIExperiment createEGUIExperiment() {
		EGUIExperimentImpl eguiExperiment = new EGUIExperimentImpl();
		return eguiExperiment;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EBatchExperiment createEBatchExperiment() {
		EBatchExperimentImpl eBatchExperiment = new EBatchExperimentImpl();
		return eBatchExperiment;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EGamaLink createEGamaLink() {
		EGamaLinkImpl eGamaLink = new EGamaLinkImpl();
		return eGamaLink;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ESubSpeciesLink createESubSpeciesLink() {
		ESubSpeciesLinkImpl eSubSpeciesLink = new ESubSpeciesLinkImpl();
		return eSubSpeciesLink;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EActionLink createEActionLink() {
		EActionLinkImpl eActionLink = new EActionLinkImpl();
		return eActionLink;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAspectLink createEAspectLink() {
		EAspectLinkImpl eAspectLink = new EAspectLinkImpl();
		return eAspectLink;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReflexLink createEReflexLink() {
		EReflexLinkImpl eReflexLink = new EReflexLinkImpl();
		return eReflexLink;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EDisplayLink createEDisplayLink() {
		EDisplayLinkImpl eDisplayLink = new EDisplayLinkImpl();
		return eDisplayLink;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EDisplay createEDisplay() {
		EDisplayImpl eDisplay = new EDisplayImpl();
		return eDisplay;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EGrid createEGrid() {
		EGridImpl eGrid = new EGridImpl();
		return eGrid;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EVariable createEVariable() {
		EVariableImpl eVariable = new EVariableImpl();
		return eVariable;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EWorldAgent createEWorldAgent() {
		EWorldAgentImpl eWorldAgent = new EWorldAgentImpl();
		return eWorldAgent;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ELayer createELayer() {
		ELayerImpl eLayer = new ELayerImpl();
		return eLayer;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EGraph createEGraph() {
		EGraphImpl eGraph = new EGraphImpl();
		return eGraph;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public GamaPackage getGamaPackage() {
		return (GamaPackage)getEPackage();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @deprecated
	 * @generated
	 */
	@Deprecated
	public static GamaPackage getPackage() {
		return GamaPackage.eINSTANCE;
	}

} //GamaFactoryImpl
