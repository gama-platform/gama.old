package idees.gama.ui.editFrame;

import gama.EAction;
import gama.EGamaObject;
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
		canvasGamlCode.setBounds(10, 40, 720, 305);

		//****** CANVAS VALIDATION *********
		Canvas canvasValidation = canvasValidation(container);
		canvasValidation.setBounds(10, 360, 720, 105);
		return container;
	}
	
	protected Canvas canvasGamlCode(Composite container) {
		
		//****** CANVAS GAMLCODE *********
		Canvas canvasGamlCode = new Canvas(container, SWT.NONE);
		canvasGamlCode.setBounds(10, 515, 720, 305);
				
		gamlCode = new StyledText(canvasGamlCode, SWT.BORDER);
		gamlCode.setBounds(5, 30, 700, 270);
		if (((EAction) eobject).getGamlCode() != null)
			gamlCode.setText(((EAction) eobject).getGamlCode());
		gamlCode.setEditable(true);
		gamlCode.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent event) {
		       	 ((EAction) eobject).setGamlCode(gamlCode.getText());
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
		return new Point(743, 495);
	}
	
	
}

