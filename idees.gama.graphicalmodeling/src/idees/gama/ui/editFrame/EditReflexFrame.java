package idees.gama.ui.editFrame;

import gama.EGamaObject;
import gama.EReflex;
import idees.gama.features.edit.EditFeature;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;

public class EditReflexFrame extends EditActionFrame {
	
	Text conditionCode;
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
		
		//****** CANVAS NAME *********
		Canvas canvasName = canvasName(container);
		canvasName.setBounds(10, 10, 720, 30);
		
		//****** CANVAS GAMLCODE *********
		Canvas canvasCondition = canvasCondition(container);
		canvasCondition.setBounds(10, 50, 720, 30);
				
		//****** CANVAS GAMLCODE *********
		Canvas canvasGamlCode = canvasGamlCode(container);
		canvasGamlCode.setBounds(10, 90, 720, 305);

		//****** CANVAS VALIDATION *********
		Canvas canvasValidation = canvasValidation(container);
		canvasValidation.setBounds(10, 405, 720, 95);
		return container;
	}
	
	protected Canvas canvasCondition(Composite container) {
		
		//****** CANVAS GAMLCODE *********
		Canvas canvasCondition = new Canvas(container, SWT.BORDER);
		canvasCondition.setBounds(10, 45, 720, 30);
				
		conditionCode = new Text(canvasCondition, SWT.BORDER);
		conditionCode.setBounds(80, 5, 625, 20);
		conditionCode.setEditable(true);
		if (((EReflex) eobject).getCondition() != null)
			conditionCode.setText(((EReflex) eobject).getCondition());
		conditionCode.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent event) {
		       	 ((EReflex) eobject).setCondition(conditionCode.getText());
		       	 ef.hasDoneChanges = true;
		    }
		});
			
		CLabel lblCondition = new CLabel(canvasCondition, SWT.NONE);
		lblCondition.setText("condition");
		lblCondition.setBounds(5, 5, 70, 20);
		return canvasCondition;
	}
	
	protected Canvas canvasGamlCode(Composite container) {
		
		//****** CANVAS GAMLCODE *********
		Canvas canvasGamlCode = new Canvas(container, SWT.BORDER);
		canvasGamlCode.setBounds(10, 515, 720, 305);
				
		gamlCode = new StyledText(canvasGamlCode, SWT.BORDER);
		gamlCode.setBounds(5, 30, 700, 265);
		if (((EReflex) eobject).getGamlCode() != null)	
			gamlCode.setText(((EReflex) eobject).getGamlCode());
		gamlCode.setEditable(true);
		gamlCode.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent event) {
		       	 ((EReflex) eobject).setGamlCode(gamlCode.getText());
		       	 ef.hasDoneChanges = true;
		    }
		});
				
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
		return new Point(743, 550);
	}
	
	
}

