package idees.gama.ui.editFrame;

import gama.EGamaObject;
import gama.EReflex;
import idees.gama.diagram.GamaDiagramEditor;
import idees.gama.features.edit.EditFeature;
import idees.gama.features.modelgeneration.ModelGenerator;

import java.util.List;

import msi.gama.util.GamaList;

import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.transaction.util.TransactionUtil;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;

public class EditReflexFrame extends EditActionFrame {
	
	ValidateText conditionCode;
	/**
	 * Create the application window.
	 */
	public EditReflexFrame(Diagram diagram, IFeatureProvider fp, EditFeature erf, EGamaObject reflex) {
		super(diagram, fp, erf,  reflex, "Reflex definition" );
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
		groupCondition(container);
				
		//****** CANVAS GAMLCODE *********
		groupGamlCode(container);
		
		//****** CANVAS OK/CANCEL *********
	//	groupOkCancel(container);
		
		return container;
	}
	
	protected void groupCondition(Composite container) {
		
		//****** CANVAS CONDITION *********
		Group group = new Group(container, SWT.NONE);
		
		group.setLayout( new FillLayout(SWT.HORIZONTAL));
	    group.setText("Condition");
	    
	   GridData gridData = new GridData();
	   gridData.horizontalAlignment = SWT.FILL;
	   gridData.grabExcessHorizontalSpace = true; 
	   group.setLayoutData(gridData);
	   group.setLayout(new GridLayout(1, false));
	   
	   GridData gridData2 = new GridData();
	   gridData2.horizontalAlignment = SWT.FILL;
	   gridData2.grabExcessHorizontalSpace = true;
	  
	   GamaDiagramEditor diagramEditor = ((GamaDiagramEditor)fp.getDiagramTypeProvider().getDiagramEditor());
	   List<String> uselessName = new GamaList<String>();
	   uselessName.add("name");
	   conditionCode = new ValidateText(group, SWT.BORDER, diagram, fp, this, diagramEditor, "",uselessName,null);
	   conditionCode.setLayoutData(gridData2);
		
	   if (((EReflex) eobject).getCondition() != null)
			conditionCode.setText(((EReflex) eobject).getCondition());
	   
	   ((ValidateText)conditionCode).setSaveData(true);
		((ValidateText) textName).getLinkedVts().add((ValidateText) conditionCode);
		
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
	 
	   GamaDiagramEditor diagramEditor = ((GamaDiagramEditor)fp.getDiagramTypeProvider().getDiagramEditor());
	   List<String> uselessName = new GamaList<String>();
	   uselessName.add("name");
	   gamlCode = new ValidateStyledText(group, SWT.BORDER, diagram, fp, this, diagramEditor, "",uselessName);
	   ((ValidateText) textName).getLinkedVsts().add((ValidateStyledText) gamlCode);
		  
	   gamlCode.setLayoutData(gridData2);
		
		//gamlCode.setBounds(5, 30, 700, 265);
		if (((EReflex) eobject).getGamlCode() != null)
			gamlCode.setText(((EReflex) eobject).getGamlCode());
		gamlCode.setEditable(true);
		
		((ValidateStyledText)gamlCode).setSaveData(true);
		((ValidateText) textName).getLinkedVsts().add((ValidateStyledText) gamlCode);
	}
	
	
	/**
	 * Return the initial size of the window.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(743, 490);
	}
	
	@Override
	protected void save(final String name) {
		TransactionalEditingDomain domain = TransactionUtil.getEditingDomain(eobject);
		if (domain != null) {
			domain.getCommandStack().execute(new RecordingCommand(domain) {
	    	     public void doExecute() {
	    	    	 if (name.equals("name")) {
	    	    		 eobject.setName(textName.getText());
	    	    	 } else {
	    	    		if  (gamlCode != null)
	    	    			((EReflex) eobject).setGamlCode(gamlCode.getText());
	    	    		if  (conditionCode != null)
	    	    			((EReflex) eobject).setCondition(conditionCode.getText());
	    	    	 }
	    	     }
	    	  });
		} 
		
       	 ef.hasDoneChanges = true;
       	ModelGenerator.modelValidation(fp, diagram);
	}
	
}

