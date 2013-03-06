package idees.gama.features.modelgeneration;

import gama.EActionLink;
import gama.EAspectLink;
import gama.EBatchExperiment;
import gama.EDisplayLink;
import gama.EExperiment;
import gama.EGamaLink;
import gama.EGrid;
import gama.EReflexLink;
import gama.ESpecies;
import gama.ESubSpeciesLink;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.custom.AbstractCustomFeature;
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
       
    	List<Shape> contents = getDiagram().getChildren();
        if (contents != null) {
        	ESpecies worldAgent = null;
            for (Shape obj : contents) {
            	Object bo = getBusinessObjectForPictogramElement(obj);
            	if (bo instanceof ESpecies) {
            		ESpecies eSpecies = (ESpecies) bo;
	                if (eSpecies.getIncomingLinks() == null || eSpecies.getIncomingLinks().isEmpty()) {
	                	worldAgent = eSpecies;
	                	break;
	                }
	            }
            }
            String gamlModel = "model " + getDiagram().getName() + "\n\nglobal {\n";
            int level = 1;
            for (EGamaLink link : worldAgent.getOutcomingLinks()) {
            	
            	if (link instanceof EActionLink) {
            		 gamlModel += defineAction((EActionLink)link,level);
            	} else if (link instanceof EReflexLink) {
            		 gamlModel += defineReflex((EReflexLink)link,level);
            	} else if (link instanceof EAspectLink) {
            		 gamlModel += defineAspect((EAspectLink)link,level);
            	}
            }
            gamlModel += "}";
            gamlModel += "\nentities {";
            for (EGamaLink link : worldAgent.getOutcomingLinks()) {
            	if (link instanceof ESubSpeciesLink) {
            		 gamlModel += defineSpecies((ESpecies) link.getTarget(),1);
            	}
            }
            
            gamlModel += "\n}";
            gamlModel += defineEnvironment();
            for (Shape obj : contents) {
            	Object bo = getBusinessObjectForPictogramElement(obj);
            	if (bo instanceof EExperiment) {
            		EExperiment eExp = (EExperiment) bo;
            		 gamlModel += defineExperiment(eExp);
	            }
            }
            URI uri = EcoreUtil.getURI( worldAgent );
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
    }
    
       
    String defineSpecies(ESpecies species, int level) {
    	String model = "\n";
    	for (int i =0; i < level;i++) {
    		model += "\t";
    	}
    	if(species instanceof EGrid) 
    		model += "grid " + species.getName() + " {\n";
    	else 
    		model += "species " + species.getName() + " {\n";
    		
    	 for (EGamaLink link : species.getOutcomingLinks()) {
         	if (link instanceof EActionLink) {
         		model += defineAction((EActionLink)link, level+1);
         	} else if (link instanceof EReflexLink) {
         		model += defineReflex((EReflexLink)link,level+1);
         	} else if (link instanceof EAspectLink) {
         		model += defineAspect((EAspectLink)link,level+1);
         	} else if (link instanceof ESubSpeciesLink) {
         		model += defineSpecies((ESpecies) link.getTarget(),level+1); 
         	}
         }
    	 for (int i =0; i < level;i++) {
     		model += "\t";
     	}
    	 model += "}\n";
    	 
    	 return model;
    }
    
    String defineAction(EActionLink link, int level) {
    	String result = "";
    	for (int i =0; i < level;i++) {
    		result += "\t";
    	}
    	result += "action " + link.getTarget().getName() + " {\n";
    	for (int i =0; i < level;i++) {
    		result += "\t";
    	}
    	result +="}\n";
    	return result;
    }
    
    String defineReflex(EReflexLink link, int level) {
    	String result = "";
    	for (int i =0; i < level;i++) {
    		result += "\t";
    	}
    	result += "reflex " + link.getTarget().getName() + " {\n";
    	for (int i =0; i < level;i++) {
    		result += "\t";
    	}
    	result +="}\n";
    	return result;
    }
    
    String defineAspect(EAspectLink link, int level) {
    	String result = "";
    	for (int i =0; i < level;i++) {
    		result += "\t";
    	}
    	result += "aspect " + link.getTarget().getName() + " {\n";
    	for (int i =0; i < level;i++) {
    		result += "\t";
    	}
    	result +="}\n";
    	return result;
    }
    
    String defineEnvironment() {
    	return "\n\nenvironment {}";
    }
    
    String defineExperiment(EExperiment exp) {
    	String model = "";
    	if (exp instanceof EBatchExperiment) {
    		model += "\n\nexperiment " + exp.getName() + " type:batch {}";
    	} else {
    		model += "\n\nexperiment " + exp.getName() + " type:gui {\n\toutput{";
    		for (EGamaLink link : exp.getOutcomingLinks()) {
    			if (link instanceof EDisplayLink) {
    				model += defineDisplay((EDisplayLink)link);
    			}
    		}
    		model += "\n\t}\n}\n";
    	}
    	return model;
    	
    }
    
    String defineDisplay(EDisplayLink link) {
    	return "\n\t\tdisplay " + link.getTarget().getName() + " {\n" + "\t\t}";
    }
 
    @Override
    public boolean hasDoneChanges() {
           return this.hasDoneChanges;
    }
}
 