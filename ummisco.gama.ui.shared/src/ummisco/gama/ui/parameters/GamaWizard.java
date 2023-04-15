/*******************************************************************************************************
 *
 * GamaWizard.java, in ummisco.gama.ui.shared, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.1).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package ummisco.gama.ui.parameters;

import java.util.List;

import org.eclipse.jface.wizard.Wizard;

import msi.gama.runtime.GAMA;
import msi.gama.util.GamaMapFactory;
import msi.gama.util.IMap;
import msi.gaml.descriptions.ActionDescription;
import msi.gaml.descriptions.ConstantExpressionDescription;
import msi.gaml.expressions.IExpression;
import msi.gaml.statements.ActionStatement;
import msi.gaml.statements.Arguments;


/**
 * The Class GamaWizard.
 */
public class GamaWizard extends Wizard{
	
	/** The pages. */
	protected List<GamaWizardPage> pages;
	
	/** The title. */
	protected String title;
	
	/** The exp. */
	protected IExpression exp;
	
	/** The finish. */
	protected ActionDescription finish;
	
	/**
	 * Instantiates a new gama wizard.
	 *
	 * @param title the title
	 * @param finish the finish
	 * @param pages the pages
	 */
	public GamaWizard(String title, ActionDescription finish, List<GamaWizardPage> pages) {
        super();
        this.title = title;
        this.pages = pages;
        setNeedsProgressMonitor(true);
       	this.finish = finish;
    }

    @Override
    public String getWindowTitle() {
        return title;
    }
    
    /**
     * Gets the values.
     *
     * @return the values
     */
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
    public boolean canFinish() {
    	if (finish == null) return true;
    	ActionStatement actionSC = (ActionStatement) finish.compile();
    	if(finish.getArgNames().isEmpty()) return  (Boolean) actionSC.executeOn(GAMA.getRuntimeScope());
        final Arguments argsSC = new Arguments();
    	argsSC.put(finish.getArgNames().get(0), ConstantExpressionDescription.create(getValues()));
		actionSC.setRuntimeArgs(GAMA.getRuntimeScope(), argsSC);
		final Boolean isFinished = (Boolean) actionSC.executeOn(GAMA.getRuntimeScope());
		return isFinished;
    }

	@Override
	public boolean performFinish() {
		return true;
	}
    
   

}
