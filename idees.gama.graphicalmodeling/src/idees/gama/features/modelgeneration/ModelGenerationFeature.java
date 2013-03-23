package idees.gama.features.modelgeneration;

import gama.EActionLink;
import gama.EAspectLink;
import gama.EBatchExperiment;
import gama.EDisplayLink;
import gama.EExperiment;
import gama.EExperimentLink;
import gama.EGrid;
import gama.EReflexLink;
import gama.ESpecies;
import gama.ESubSpeciesLink;
import gama.EVariable;
import gama.EWorldAgent;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import msi.gama.common.interfaces.ISyntacticElement;
import msi.gama.kernel.model.IModel;
import msi.gama.lang.gaml.resource.GamlResource;
import msi.gama.lang.gaml.validation.GamlBuilder;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.factories.DescriptionFactory;
import msi.gaml.factories.ModelStructure;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.custom.AbstractCustomFeature;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.Shape;

public class ModelGenerationFeature extends AbstractCustomFeature {
 
    private boolean hasDoneChanges = false;
     
    public ModelGenerationFeature(IFeatureProvider fp) {
        super(fp);
       
 
    }
 
    @Override
    public String getName() {
        return "Generate Gaml model";
    }
 
    @Override
    public String getDescription() {
        return "Generate Gaml model from diagram";
    }
 
    @Override
    public boolean canExecute(ICustomContext context) {
      return true;
    }
 
    @Override
    public void execute(ICustomContext context) {
    		String gamlModel = generateModel(this.getFeatureProvider(), getDiagram());
    		List<Shape> contents = getDiagram().getChildren();
    		URI uri = null;
    		if (contents != null) {
            	uri = EcoreUtil.getURI( (EObject) getBusinessObjectForPictogramElement(contents.get(0)) );
    		} else {
    			return;
    		}
    		
            uri = uri.trimFragment();
            if (uri.isPlatform()) {
                uri = URI.createURI( uri.toPlatformString( true ) );
            }
            String path = ResourcesPlugin.getWorkspace().getRoot().getLocation() + uri.path();
            path = path.replace(".diagram", ".gaml");
           
            File file = new File(path);
     
            FileWriter fw;
			try {
				fw = new FileWriter(file, false);
				fw.write(gamlModel);
		        fw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
        
    }
    
       
    static String defineSpecies(ESpecies species, int level) {
    	String model = "\n";
    	String sp = "";
    	for (int i =0; i < level;i++) {
    		sp += "\t";
    	}
    	model += sp;
    	if(species instanceof EGrid) 
    		model += "grid " + species.getName() + " {\n";
    	else 
    		model += "species " + species.getName() + " {\n";
    	
    	for (EVariable var: species.getVariables()) {
    		model += defineVariable(var,level+1);
    	}
    	
    	Map<String, EReflexLink> reflexMap = new Hashtable<String, EReflexLink>();
    	 for (EActionLink link : species.getActionLinks()) {
         	model += defineAction(link, level+1);
    	 }
    	 for (EReflexLink link : species.getReflexLinks()) {
    		reflexMap.put(link.getTarget().getName(), (EReflexLink) link);
     	 }
    	 for (String reflex : species.getReflexList()) {
    		 model += defineReflex(reflexMap.get(reflex),level+1);
     	 }
    	 for (EAspectLink link : species.getAspectLinks()) {
    		 model += defineAspect(link,level+1);
     	 }
    	 for (ESubSpeciesLink link : species.getMicroSpeciesLinks()) {
    		 model += defineSpecies(link.getMicro(),level+1); 
     	 }
    	 
    	 model += sp+ "}\n";
    	 
    	 return model;
    }
    
    static String defineVariable(EVariable var, int level) {
    	String varStr = "";
		for (int i =0; i < level;i++) {
			varStr += "\t";
    	}
		if (var.getType() == null  || var.getType().equals("")) {
			varStr += "var ";
		} else {varStr += var.getType() + " ";}
		varStr += var.getName();
		if (var.getInit() != null  && !var.getInit().equals(""))
			varStr += " <- " + var.getInit();
		if (var.getUpdate() != null  && !var.getUpdate().equals(""))
			varStr += " update: " + var.getUpdate();
		if (var.getFunction() != null  && !var.getFunction().equals(""))
			varStr += " -> {" + var.getFunction() + "}";
		if (var.getMin() != null  && !var.getMin().equals(""))
			varStr += " min: " + var.getMin();
		if (var.getMax() != null  && !var.getMax().equals(""))
			varStr += " max: " + var.getMax();
		varStr += ";\n";
		return varStr;
    }
    
    static String defineAction(EActionLink link, int level) {
    	String result = "";
    	String sp = "";
    	for (int i =0; i < level;i++) {
    		sp += "\t";
    	}
    	result += sp + "action " + link.getTarget().getName() + " {\n";
    	String code = link.getAction().getGamlCode();
    	if (code != null && ! code.isEmpty()) {
	    	for (String line : code.split("\n")) {
	    		result += sp+ "\t" + line+"\n";
	    	}
    	}
    	result +=sp + "}\n";
    	return result;
    }
    
    static String defineReflex(EReflexLink link, int level) {
    	String result = "";
    	String sp = "";
    	for (int i =0; i < level;i++) {
    		sp += "\t";
    	}
    	if (link.getReflex().getCondition() != null && link.getReflex().getCondition().isEmpty()) {
    		result += sp + "reflex " + link.getReflex().getName() + " when: "+ link.getReflex().getCondition() + " {\n";
    	} else {
    		result += sp + "reflex " + link.getReflex().getName() + " {\n";
    	}
    	String code = link.getReflex().getGamlCode();
    	if (code != null && ! code.isEmpty()) {
	    	for (String line : code.split("\n")) {
	    		result += sp+ "\t" + line+"\n";
	    	}
    	}
    	result +=sp + "}\n";
    	return result;
    }
    
    static String defineAspect(EAspectLink link, int level) {
    	String result = "";
    	String sp = "";
    	for (int i =0; i < level;i++) {
    		sp += "\t";
    	}
    	result += sp + "aspect " + link.getAspect() + " {\n";
    	result +=sp + "}\n";
    	return result;
    }
      
    static String defineExperiment(EExperiment exp) {
    	String model = "";
    	if (exp instanceof EBatchExperiment) {
    		model += "\n\nexperiment " + exp.getName() + " type:batch {}";
    	} else {
    		model += "\n\nexperiment " + exp.getName() + " type:gui {\n\toutput{";
    		for (EDisplayLink link : exp.getDisplayLinks()) {
    			model += defineDisplay(link);
    		}
    		model += "\n\t}\n}\n";
    	}
    	return model;
    	
    }
    
    static String defineDisplay(EDisplayLink link) {
    	return "\n\t\tdisplay " + link.getDisplay().getName() + " {\n" + "\t\t}";
    }
 
    @Override
    public boolean hasDoneChanges() {
           return this.hasDoneChanges;
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
            model = "model " + diagram.getName() + "\n\nglobal {\n";
            int level = 1;
            for (EVariable var: worldAgent.getVariables()) {
            	model += defineVariable(var,level);
        	}
            Map<String, EReflexLink> reflexMap = new Hashtable<String, EReflexLink>();
	       	 for (EActionLink link : worldAgent.getActionLinks()) {
	            	model += defineAction(link, level+1);
	       	 }
	       	 for (EReflexLink link : worldAgent.getReflexLinks()) {
	       		reflexMap.put(link.getTarget().getName(), (EReflexLink) link);
	        	 }
	       	 for (String reflex : worldAgent.getReflexList()) {
	       		 model += defineReflex(reflexMap.get(reflex),level+1);
	        	 }
	       	 for (EAspectLink link : worldAgent.getAspectLinks()) {
	       		 model += defineAspect(link,level+1);
	        	 }
	      
	       	model += "}";
	       	model += "\nentities {";
            for (ESubSpeciesLink link : worldAgent.getMicroSpeciesLinks()) {
            	model += defineSpecies((ESpecies) link.getTarget(),1);
            }
            
            model += "\n}";
            
            for (EExperimentLink link : worldAgent.getExperimentLinks()) {
	       		 model += defineExperiment(link.getExperiment());
	        }
	 
        }
        return model;
    }
    
    public static String loadModel(final String fileName) {
		String result="";
    	IModel lastModel = null;
		ResourceSet rs = new ResourceSetImpl();
		GamlResource r = (GamlResource) rs.getResource(URI.createURI("file:///" + fileName), true);
		try {
			Map<URI, ISyntacticElement> elements =
				GamlBuilder.INSTANCE.buildCompleteSyntacticTree(r, rs);
			if ( r.getErrors().isEmpty() ) {
				
				ModelStructure ms =
					new ModelStructure("", fileName, new ArrayList(elements.values()));
				lastModel = DescriptionFactory.getModelFactory().compile(ms);
				result = "Validation ok";
			} else {
				result = r.getErrors().toString();
			}
		} catch (GamaRuntimeException e1) {
			result = "Exception during compilation:" + e1.getMessage();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		}
		return result;
	}
}
 