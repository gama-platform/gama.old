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
			case GamaPackage.EVARIABLE: return createEVariable();
			case GamaPackage.EWORLD_AGENT: return createEWorldAgent();
			case GamaPackage.ELAYER: return createELayer();
			case GamaPackage.EGRAPH_TOPOLOGY_NODE: return createEGraphTopologyNode();
			case GamaPackage.EEXPERIMENT_LINK: return createEExperimentLink();
			case GamaPackage.ELAYER_ASPECT: return createELayerAspect();
			case GamaPackage.EGRID_TOPOLOGY: return createEGridTopology();
			case GamaPackage.ECONTINUOUS_TOPOLOGY: return createEContinuousTopology();
			case GamaPackage.ETOPOLOGY: return createETopology();
			case GamaPackage.EINHERIT_LINK: return createEInheritLink();
			case GamaPackage.EGRAPH_TOPOLOGY_EDGE: return createEGraphTopologyEdge();
			case GamaPackage.EGRAPH_LINK: return createEGraphLink();
			case GamaPackage.ECHART_LAYER: return createEChartLayer();
			case GamaPackage.EPARAMETER: return createEParameter();
			case GamaPackage.EMONITOR: return createEMonitor();
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
	public EGraphTopologyNode createEGraphTopologyNode() {
		EGraphTopologyNodeImpl eGraphTopologyNode = new EGraphTopologyNodeImpl();
		return eGraphTopologyNode;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EExperimentLink createEExperimentLink() {
		EExperimentLinkImpl eExperimentLink = new EExperimentLinkImpl();
		return eExperimentLink;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ELayerAspect createELayerAspect() {
		ELayerAspectImpl eLayerAspect = new ELayerAspectImpl();
		return eLayerAspect;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EGridTopology createEGridTopology() {
		EGridTopologyImpl eGridTopology = new EGridTopologyImpl();
		return eGridTopology;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EContinuousTopology createEContinuousTopology() {
		EContinuousTopologyImpl eContinuousTopology = new EContinuousTopologyImpl();
		return eContinuousTopology;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ETopology createETopology() {
		ETopologyImpl eTopology = new ETopologyImpl();
		return eTopology;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EInheritLink createEInheritLink() {
		EInheritLinkImpl eInheritLink = new EInheritLinkImpl();
		return eInheritLink;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EGraphTopologyEdge createEGraphTopologyEdge() {
		EGraphTopologyEdgeImpl eGraphTopologyEdge = new EGraphTopologyEdgeImpl();
		return eGraphTopologyEdge;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EGraphLink createEGraphLink() {
		EGraphLinkImpl eGraphLink = new EGraphLinkImpl();
		return eGraphLink;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EChartLayer createEChartLayer() {
		EChartLayerImpl eChartLayer = new EChartLayerImpl();
		return eChartLayer;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EParameter createEParameter() {
		EParameterImpl eParameter = new EParameterImpl();
		return eParameter;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EMonitor createEMonitor() {
		EMonitorImpl eMonitor = new EMonitorImpl();
		return eMonitor;
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
