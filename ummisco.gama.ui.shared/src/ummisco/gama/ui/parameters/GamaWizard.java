package ummisco.gama.ui.parameters;

import java.util.List;

import org.eclipse.jface.wizard.Wizard;

import msi.gama.util.GamaListFactory;
import msi.gama.util.IList;
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
    
    public IList<IMap<String, Object>> getValues() {
    	IList<IMap<String, Object>> values = GamaListFactory.create();
    	for(GamaWizardPage p : pages) {
    		System.out.println("page: " + p);
    		values.add(p.getValues());
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
		return true;
	}

   

}
