package idees.gama.ui.editFrame;

import gama.EAction;
import gama.EGamaObject;
import idees.gama.features.edit.EditFeature;

import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.transaction.util.TransactionUtil;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

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
		
		//****** CANVAS NAME *********
		Canvas canvasName = canvasName(container);
		canvasName.setBounds(10, 10, 720, 30);
		
		//****** CANVAS GAMLCODE *********
		Canvas canvasGamlCode = canvasGamlCode(container);
		canvasGamlCode.setBounds(10, 50, 720, 305);

		//****** CANVAS OK/CANCEL *********
		Canvas canvasOkCancel = canvasOkCancel(container);
		canvasOkCancel.setBounds(10, 365, 720, 30);
		return container;
	}
	
	protected Canvas canvasGamlCode(Composite container) {
		
		//****** CANVAS GAMLCODE *********
		Canvas canvasGamlCode = new Canvas(container, SWT.BORDER);
		canvasGamlCode.setBounds(10, 515, 720, 305);
		
		gamlCode = new StyledText(canvasGamlCode, SWT.BORDER);
		gamlCode.setBounds(5, 30, 700, 265);
		if (((EAction) eobject).getGamlCode() != null)
			gamlCode.setText(((EAction) eobject).getGamlCode());
		gamlCode.setEditable(true);
				
		CLabel lblCompilation = new CLabel(canvasGamlCode, SWT.NONE);
		lblCompilation.setText("gaml code");
		lblCompilation.setBounds(5, 5, 70, 20);
		return canvasGamlCode;
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
		
       	 ef.hasDoneChanges = true;
		
	}
	
	
}

