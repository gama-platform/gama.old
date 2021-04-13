package ummisco.gama.ui.parameters;

import java.util.List;

import org.eclipse.jface.wizard.Wizard;

import msi.gama.util.GamaMapFactory;
import msi.gama.util.IMap;


public class GamaWizard extends Wizard{
	protected List<GamaWizardPage> pages;
	protected String title;
	
	public GamaWizard(String title, List<GamaWizardPage> pages) {
        super();
        this.title = title;
        this.pages = pages;
        setNeedsProgressMonitor(true);
    }

    @Override
    public String getWindowTitle() {
        return title;
    }
    
    public IMap<String,IMap<String, Object>> getValues() {
    	IMap<String,IMap<String, Object>> values = GamaMapFactory.create();
    	for(GamaWizardPage p : pages) {
    		values.put(p.getTitle(),p.getValues());
    	}
    	return values;
    }

    @Override
    public void addPages() {
    	for (GamaWizardPage p : pages) {
    		  addPage(p);
    	}
    }

    @Override
    public boolean performFinish() {
    	for (GamaWizardPage p : pages) {
    		if (! p.isPageComplete())
    			return false;
    	}
    	return true;
    }
    
   

}
