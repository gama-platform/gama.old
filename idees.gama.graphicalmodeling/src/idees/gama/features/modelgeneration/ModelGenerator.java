package idees.gama.features.modelgeneration;

import gama.EActionLink;
import gama.EAspect;
import gama.EAspectLink;
import gama.EBatchExperiment;
import gama.EChartLayer;
import gama.EDisplay;
import gama.EDisplayLink;
import gama.EExperiment;
import gama.EExperimentLink;
import gama.EGridTopology;
import gama.EInheritLink;
import gama.ELayer;
import gama.ELayerAspect;
import gama.EParameter;
import gama.EReflexLink;
import gama.ESpecies;
import gama.ESubSpeciesLink;
import gama.EVariable;
import gama.EWorldAgent;
import idees.gama.diagram.GamaDiagramEditor;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import msi.gama.kernel.model.IModel;
import msi.gama.lang.gaml.gaml.Model;
import msi.gama.lang.gaml.resource.GamlResource;
import msi.gama.lang.gaml.validation.GamlJavaValidator;
import msi.gama.lang.utils.EGaml;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GAML;
import msi.gama.util.GamaList;
import msi.gaml.compilation.GamlCompilationError;
import msi.gaml.descriptions.ErrorCollector;
import msi.gaml.descriptions.ModelDescription;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.xtext.resource.XtextResourceSet;

public class ModelGenerator {

	private static String EL = System.getProperty("line.separator");

	public static IModel modelGeneration(IFeatureProvider fp, Diagram diagram) {
		GamaDiagramEditor diagramEditor = ((GamaDiagramEditor)fp.getDiagramTypeProvider().getDiagramEditor());
		XtextResourceSet rs = EGaml.getInstance(XtextResourceSet.class);
		rs.setClasspathURIContext(ModelGenerator.class);
		URI uri = URI.createPlatformResourceURI("toto/"+ diagramEditor.getTitle()+".gaml", true);
		GamlResource resource = (GamlResource) rs.createResource(uri);
		String gamlModel = ModelGenerator.generateModel(fp, diagram);
		InputStream is = new ByteArrayInputStream(gamlModel.getBytes());
		diagramEditor.setResource(resource);
		try {
			resource.load(is, null);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		try {
			Set<GamlResource> resources = new HashSet<GamlResource>();
			resources.add(resource);
			IModel model = resource.build(resource.getResourceSet(), new ArrayList<GamlCompilationError>());//GAML.getModelFactory().compile(resource);
			((ModelDescription)model.getDescription()).setModelFilePath(getPath(fp, diagram));
			return model;
		} catch (GamaRuntimeException e1) {
			return null;
		} catch (Exception e) {
			return null;
		} 
	}
	
	public static String getPath(IFeatureProvider fp,Diagram diagram) {
		List<Shape> contents = diagram.getChildren();
		URI uri = null;
		if (contents != null) {
        	uri = EcoreUtil.getURI( (EObject) fp.getBusinessObjectForPictogramElement(contents.get(0)) );
		} else {
			return "";
		}
		 uri = uri.trimFragment();
        if (uri.isPlatform()) {
            uri = URI.createURI( uri.toPlatformString( true ) );
        }
        String containerStr = "/"+ uri.segment(0);
        String path = ResourcesPlugin.getWorkspace().getRoot().getLocation() + uri.path();
        path = path.replace(".gadl", ".gaml");
        return path;
	}
	public static List<GamlCompilationError> modelValidation(IFeatureProvider fp, Diagram diagram) {
		GamaDiagramEditor diagramEditor = ((GamaDiagramEditor)fp.getDiagramTypeProvider().getDiagramEditor());
		XtextResourceSet rs = EGaml.getInstance(XtextResourceSet.class);
		rs.setClasspathURIContext(ModelGenerator.class);
		URI uri = URI.createPlatformResourceURI("toto/"+ diagramEditor.getTitle()+".gaml", true);
		GamlResource resource = (GamlResource) rs.createResource(uri);
		String gamlModel = ModelGenerator.generateModel(fp, diagram);
		InputStream is = new ByteArrayInputStream(gamlModel.getBytes());
		diagramEditor.setResource(resource);
		
		try {
			resource.load(is, null);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		try {
			//GamlJavaValidator validator = EGaml.getInstance(GamlJavaValidator.class);
			List<GamlCompilationError> errors = new ArrayList<GamlCompilationError>();
			
			ErrorCollector erColl = GAML.getModelFactory().validate(resource);
			if (erColl != null) {
				for ( GamlCompilationError error : erColl ) {
					if (error.isError())
						errors.add(error);
				}
			}
			diagramEditor.setErrors(errors);
			return errors;
		} catch (GamaRuntimeException e1) {
			return null;
		} catch (Exception e) {
			return null;
		} 
	}

	static String defineSpecies(ESpecies species, int level) {
		if (species == null)
			return "";
		String model = EL;
		String sp = "";
		for (int i = 0; i < level; i++) {
			sp += "\t";
		}
		model += sp;
		if (species.getTopology() != null
				&& species.getTopology() instanceof EGridTopology) {
			EGridTopology gt = (EGridTopology) species.getTopology();
			model += "grid " + species.getName() + " width:"
					+ gt.getNb_columns() + " height:" + gt.getNb_rows();
			model += " neighbours:";
			if (gt.getNeighbourhoodType() == null) {
				model += "4";
			} else {
				if (gt.getNeighbourhoodType().equals("expression"))
					model += gt.getNeighbourhood();
				else
					model += gt.getNeighbourhoodType().toCharArray()[0];
			}
		} else
			model += "species " + species.getName();
		if (species.getInheritsFrom() != null) {
			model += " parent:" + species.getInheritsFrom().getName();
		}
		if (species.getSkills() != null && !species.getSkills().isEmpty()) {
			model += " skills:" + species.getSkills();
		}
		model += " {" + EL;
		for (EVariable var : species.getVariables()) {
			model += defineVariable(var, level + 1);
		}
		model += defineInit(species, level + 1);
		Map<String, EReflexLink> reflexMap = new Hashtable<String, EReflexLink>();
		for (EActionLink link : species.getActionLinks()) {
			model += defineAction(link, level + 1);
		}
		for (EReflexLink link : species.getReflexLinks()) {
			if (link.getTarget() == null)
				continue;
			reflexMap.put(link.getTarget().getName(), (EReflexLink) link);
		}
		List<String> reflexes = new GamaList<String>();
		if (species.getReflexList().isEmpty() && !reflexMap.isEmpty()) {
			reflexes.addAll(reflexMap.keySet());
		} else {
			reflexes.addAll(species.getReflexList());
		}
		for (String reflex : reflexes) {
			if (reflexMap.containsKey(reflex))
				model += defineReflex(reflexMap.get(reflex), level + 1);
		}
		for (EAspectLink link : species.getAspectLinks()) {
			model += defineAspect(link, level + 1);
		}
		for (ESubSpeciesLink link : species.getMicroSpeciesLinks()) {
			model += defineSpecies(link.getMicro(), level + 1);
		}

		model += sp + "}" + EL;

		for (EInheritLink link : species.getInheritingLinks()) {
			model += defineSpecies(link.getChild(), level);
		}

		return model;
	}

	static String defineVariable(EVariable var, int level) {
		if (var == null)
			return "";
		String varStr = "";
		for (int i = 0; i < level; i++) {
			varStr += "\t";
		}
		if (var.getType() == null || var.getType().equals("")) {
			varStr += "var ";
		} else {
			varStr += var.getType() + " ";
		}
		varStr += var.getName();
		if (var.getInit() != null && !var.getInit().equals(""))
			varStr += " <- " + var.getInit();
		if (var.getUpdate() != null && !var.getUpdate().equals(""))
			varStr += " update: " + var.getUpdate();
		if (var.getFunction() != null && !var.getFunction().equals(""))
			varStr += " -> {" + var.getFunction() + "}";
		if (var.getMin() != null && !var.getMin().equals(""))
			varStr += " min: " + var.getMin();
		if (var.getMax() != null && !var.getMax().equals(""))
			varStr += " max: " + var.getMax();
		varStr += ";" + EL;
		return varStr;
	}

	static String defineAction(EActionLink link, int level) {
		if (link == null || link.getAction() == null)
			return "";
		String result = "";
		String sp = "";
		for (int i = 0; i < level; i++) {
			sp += "\t";
		}
		result += sp + "action " + link.getTarget().getName() + " {" + EL;
		String code = link.getAction().getGamlCode();
		if (code != null && !code.isEmpty()) {
			for (String line : code.split(EL)) {
				result += sp + "\t" + line + EL;
			}
		}
		result += sp + "}" + EL;
		return result;
	}

	static String defineInit(ESpecies species, int level) {
		if (species == null)
			return "";
		String result = "";
		String code = species.getInit();
		if (code != null && !code.isEmpty()) {

			String sp = "";
			for (int i = 0; i < level; i++) {
				sp += "\t";
			}
			result += sp + "init {" + EL;
			for (String line : code.split(EL)) {
				result += sp + "\t" + line + EL;
			}
			result += sp + "}" + EL;
		}
		return result;
	}

	static String defineReflex(EReflexLink link, int level) {
		if (link == null || link.getReflex() == null)
			return "";
		String result = "";
		String sp = "";
		for (int i = 0; i < level; i++) {
			sp += "\t";
		}
		if (link.getReflex().getCondition() != null
				&& !link.getReflex().getCondition().isEmpty()) {
			result += sp + "reflex " + link.getReflex().getName() + " when: "
					+ link.getReflex().getCondition() + " {" + EL;
		} else {
			result += sp + "reflex " + link.getReflex().getName() + " {" + EL;
		}
		String code = link.getReflex().getGamlCode();
		if (code != null && !code.isEmpty()) {
			for (String line : code.split(EL)) {
				result += sp + "\t" + line + EL;
			}
		}
		result += sp + "}" + EL;
		return result;
	}

	static String defineAspect(EAspectLink link, int level) {
		if (link == null || link.getAspect() == null)
			return "";
		String result = "";
		EAspect asp = link.getAspect();
		String sp = "";
		for (int i = 0; i < level; i++) {
			sp += "\t";
		}
		result += sp + "aspect " + asp.getName() + " {" + EL;
		for (ELayerAspect lay : asp.getLayers()) {
			result += sp + "\t" + lay.getGamlCode() + ";" + EL;
		}
		result += EL + sp + "}" + EL;
		return result;
	}

	static String defineExperiment(EExperiment exp) {
		String model = "";
		if (exp == null)
			return model;
		if (exp instanceof EBatchExperiment) {
			model += EL + EL + "experiment " + exp.getName() + " type:batch {}";
		} else {
			model += EL + EL + "experiment " + exp.getName() + " type:gui {"
					+ EL;
			for (EParameter link : exp.getParameters()) {
				model += defineParameter(link);
			}

			model += "\toutput{";
			for (EDisplayLink link : exp.getDisplayLinks()) {
				model += defineDisplay(link);
			}
			model += EL + "\t}" + EL + "}" + EL;
		}
		return model;

	}

	static String defineParameter(EParameter par) {
		if (par == null)
			return "";
		String parStr = "\tparameter";
		parStr += "\"" + par.getName() + "\"";
		parStr += " var:" + par.getVariable();
		if (par.getCategory() != null && !par.getCategory().isEmpty())
			parStr += " category: \"" + par.getCategory() + "\"";
		if (par.getAmong() != null && !par.getAmong().isEmpty())
			parStr += " among:" + par.getCategory();
		if (par.getInit() != null && !par.getInit().isEmpty())
			parStr += " init:" + par.getInit();
		if (par.getMin() != null && !par.getMin().isEmpty())
			parStr += " min:" + par.getMin();
		if (par.getMax() != null && !par.getMax().isEmpty())
			parStr += " max:" + par.getMax();
		if (par.getStep() != null && !par.getStep().isEmpty())
			parStr += " step:" + par.getStep();
		parStr += ";" + EL;
		return parStr;
	}

	static String defineDisplay(EDisplayLink link) {
		if (link == null || link.getDisplay() == null)
			return "";
		EDisplay disp = link.getDisplay();
		String model = EL + "\t\t";
		if (disp.getGamlCode() == null || disp.getGamlCode().isEmpty()) {
			model += "display " + disp.getName() + "{}";
			return model;
		} else {
			model += disp.getGamlCode() + EL;
		}
		Map<String, ELayer> layerMap = new Hashtable<String, ELayer>();
		for (ELayer lay : disp.getLayers()) {
			layerMap.put(lay.getName(), lay);
		}
		for (String layStr : disp.getLayerList()) {
			ELayer lay = layerMap.get(layStr);
			model += "\t\t\t";
			// "species", "grid", "agents","image", "text"
			if (lay.getType().equals("species")) {
				model += lay.getType() + " " + lay.getSpecies() + " aspect: "
						+ lay.getAspect();
			} else if (lay.getType().equals("grid")) {
				model += lay.getType() + " " + lay.getGrid();
			} else if (lay.getType().equals("agents")) {
				model += lay.getType() + " " + lay.getAgents() + " aspect: "
						+ lay.getAspect();
			} else if (lay.getType().equals("image")) {
				model += lay.getType() + lay.getFile() + " size: "
						+ lay.getSize();
			} else if (lay.getType().equals("text")) {
				model += lay.getType() + lay.getText() + " size: "
						+ lay.getSize();
			} else if (lay.getType().equals("chart")) {
				String background = "";
				if (lay.getColor() != null
						&& lay.getColor().equals("rgb(255,255,255)"))
					background = " background:" + lay.getColor();
				model += lay.getType() + " \"" + lay.getName() + "\" type:"
						+ lay.getChart_type() + background;
			}

			String size = "";
			if (lay.getSize_x() != null
					&& lay.getSize_y() != null
					&& (!lay.getSize_x().equals("1.0") || !lay.getSize_y()
							.equals("1.0"))) {
				size = " size:{" + lay.getSize_x() + "," + lay.getSize_y()
						+ "}";
			}
			String position = "";
			if (lay.getPosition_x() != null
					&& lay.getPosition_y() != null
					&& (!lay.getPosition_x().equals("0.0") && !lay
							.getPosition_y().equals("0.0"))) {
				position = " position:{" + lay.getPosition_x() + ","
						+ lay.getPosition_y() + "}";
			}
			if (lay.getType().equals("chart")) {
				model += size + position + "{" + EL;
				if (lay.getChartlayers() != null
						&& !lay.getChartlayers().isEmpty()) {
					for (EChartLayer cl : lay.getChartlayers()) {
						model += "\t\t\t\tdata \"" + cl.getName() + "\" style:"
								+ cl.getStyle() + " value:" + cl.getValue()
								+ " color:" + cl.getColor() + ";" + EL;
					}
				}
				model += "\t\t\t}" + EL;
			} else {
				model += size + position + ";" + EL;
			}
		}

		model += "\t\t}";
		return model;
	}

	public static String generateModel(IFeatureProvider fp, Diagram diagram) {
		String model = "";
		List<Shape> contents = diagram.getChildren();
		if (contents != null) {
			EWorldAgent worldAgent = null;
			for (Shape obj : contents) {
				Object bo = fp.getBusinessObjectForPictogramElement(obj);
				if (bo instanceof EWorldAgent) {
					worldAgent = (EWorldAgent) bo;
					break;

				}
			}
			model = "model " + diagram.getName() + EL + EL + "global";
			if (worldAgent.getTorus() != null
					&& !worldAgent.getTorus().isEmpty()
					&& !worldAgent.getTorus().equals("false")) {
				model += " torus:" + worldAgent.getTorus();
			}
			if (worldAgent.getSkills() != null
					&& !worldAgent.getSkills().isEmpty()) {
				model += " skills:" + worldAgent.getSkills();
			}
			model += " {" + EL;
			int level = 1;
			for (EVariable var : worldAgent.getVariables()) {
				model += defineVariable(var, level);
			}
			if (worldAgent.getBoundsType() != null) {
				if (worldAgent.getBoundsType().equals("expression")) {
					model += "\tgeometry shape <-"
							+ worldAgent.getBoundsExpression() + ";" + EL;
				} else if (worldAgent.getBoundsType().equals("width-height")) {
					model += "\tgeometry shape <- rectangle("
							+ worldAgent.getBoundsWidth() + ","
							+ worldAgent.getBoundsHeigth() + ");" + EL;
				} else if (worldAgent.getBoundsType().equals("file")) {
					model += "\tgeometry shape <- envelope(file(\""
							+ worldAgent.getBoundsPath() + "\"));" + EL;
				}
			}

			Map<String, EReflexLink> reflexMap = new Hashtable<String, EReflexLink>();
			for (EActionLink link : worldAgent.getActionLinks()) {
				model += defineAction(link, level + 1);
			}
			for (EReflexLink link : worldAgent.getReflexLinks()) {
				reflexMap.put(link.getTarget().getName(), (EReflexLink) link);
			}
			List<String> reflexes = new GamaList<String>();
			if (worldAgent.getReflexList().isEmpty() && !reflexMap.isEmpty()) {
				reflexes.addAll(reflexMap.keySet());
			} else {
				reflexes.addAll(worldAgent.getReflexList());
			}
			for (String reflex : reflexes) {
				model += defineReflex(reflexMap.get(reflex), level + 1);
			}
			for (EAspectLink link : worldAgent.getAspectLinks()) {
				model += defineAspect(link, level + 1);
			}
			model += defineInit(worldAgent, 1);
			model += "}";
			model += EL;
			for (ESubSpeciesLink link : worldAgent.getMicroSpeciesLinks()) {
				model += defineSpecies((ESpecies) link.getMicro(), 0);
			}

			model += EL;

			for (EExperimentLink link : worldAgent.getExperimentLinks()) {
				model += defineExperiment(link.getExperiment());
			}

		}
		return model;
	}
}
