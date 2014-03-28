package idees.gama.ui.editFrame;

import gama.EAction;
import gama.EGamaObject;
import idees.gama.features.edit.EditFeature;
import idees.gama.features.modelgeneration.ModelGenerator;

import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.transaction.util.TransactionUtil;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;

public class EditActionFrame extends EditFrame {
	
	StyledText gamlCode;
	/**
	 * Create the application window.
	 */
	public EditActionFrame(Diagram diagram, IFeatureProvider fp, EditFeature eaf, EGamaObject action, String name) {	
		super(diagram, fp, eaf,  action, name == null ? "Action definition" : name );
	}

	/**
	 * Create contents of the application window.
	 * @param parent
	 */
	@Override
	protected Control createContents(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new GridLayout(1, false));
		//****** CANVAS NAME *********
		groupName(container);
		//****** CANVAS GAMLCODE *********
		groupGamlCode(container);
		
		//canvasValidation(container);
		
		//****** CANVAS OK/CANCEL *********
		groupOkCancel(container);
		
		return container;
	}
	
	protected void groupGamlCode(Composite container) {
		
		//****** CANVAS GAMLCODE *********
		Group group = new Group(container, SWT.NONE);
		
		group.setLayout( new FillLayout(SWT.HORIZONTAL));
	    group.setText("Gaml code");
	    
	   GridData gridData = new GridData();
	   gridData.horizontalAlignment = SWT.FILL;
	   gridData.verticalAlignment = SWT.FILL;
	   gridData.grabExcessHorizontalSpace = true;
	   gridData.grabExcessVerticalSpace= true;
	   group.setLayoutData(gridData);
	   group.setLayout(new GridLayout(1, false));
	   
	   GridData gridData2 = new GridData();
	   gridData2.horizontalAlignment = SWT.FILL;
	   gridData2.verticalAlignment = SWT.FILL;
	   gridData2.grabExcessHorizontalSpace = true;
	   gridData2.grabExcessVerticalSpace= true;
	 
	   gamlCode = new StyledText(group, SWT.BORDER);
	   gamlCode.setLayoutData(gridData2);
		
		//gamlCode.setBounds(5, 30, 700, 265);
		if (((EAction) eobject).getGamlCode() != null)
			gamlCode.setText(((EAction) eobject).getGamlCode());
		gamlCode.setEditable(true);
	}
	
	
	/**
	 * Return the initial size of the window.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(743, 450);
	}

	@Override
	protected void save() {
		TransactionalEditingDomain domain = TransactionUtil.getEditingDomain(eobject);
		if (domain != null) {
			domain.getCommandStack().execute(new RecordingCommand(domain) {
	    	     public void doExecute() {
	    	    	 eobject.setName(textName.getText());
		    	    ((EAction) eobject).setGamlCode(gamlCode.getText());
	    	     }
	    	  });
		} 
		ModelGenerator.modelValidation(fp, diagram);
       	 ef.hasDoneChanges = true;
		
	}
	
	
}

