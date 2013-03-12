package idees.gama.ui.editFrame;

import gama.EGamaObject;
import gama.EWorldAgent;
import idees.gama.features.edit.EditFeature;
import idees.gama.features.modelgeneration.ModelGenerationFeature;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.transaction.util.TransactionUtil;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.StatusLineManager;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public abstract class EditFrame  extends ApplicationWindow {
	Diagram diagram;
	EditFeature ef;
	IFeatureProvider fp;
	String name;
	EGamaObject eobject;
	StyledText validationResult;
	Text textName;
	/**
	 * Create the application window.
	 */
	public EditFrame(Diagram diagram, IFeatureProvider fp, EditFeature ef,EGamaObject eobject, String name) {
		super(null);
		this.diagram = diagram;
		this.fp = fp;
		this.ef = ef; 
		this.name = name;		
		this.eobject = eobject;
		addToolBar(SWT.FLAT | SWT.WRAP);
		addMenuBar();
		addStatusLine();
		
	}

	protected String compileModel() {
	     URI uri = EcoreUtil.getURI( eobject );
         uri = uri.trimFragment();
         if (uri.isPlatform()) {
             uri = URI.createURI( uri.toPlatformString( true ) );
         }
         String path = ResourcesPlugin.getWorkspace().getRoot().getLocation() + uri.path();
         path = path.replace(".diagram", "_tmp_mb.gaml");
         String gamlModel = ModelGenerationFeature.generateModel(fp, diagram);
         File file = new File(path);
         FileWriter fw;
			try {
				fw = new FileWriter(file, false);
				fw.write(gamlModel);
		        fw.close();
			} catch (IOException e) {
				e.printStackTrace();
		  }
			String result = ModelGenerationFeature.loadModel(path);
		file.delete();
         return result;   
	}
	
	protected Canvas canvasValidation(Composite container) {
		//****** CANVAS VALIDATION *********
		Canvas canvasValidation = new Canvas(container, SWT.NONE);
		canvasValidation.setBounds(10, 515, 720, 105);
				
		validationResult = new StyledText(canvasValidation, SWT.BORDER);
		validationResult.setBounds(5, 30, 700, 70);
		validationResult.setEditable(false);
				
		Button btnValidate = new Button(canvasValidation, SWT.NONE);
		btnValidate.addSelectionListener(new SelectionAdapter() {
				 
			@Override
			public void widgetSelected(SelectionEvent e) {
				validationResult.setText(compileModel());	
			}
		});
		btnValidate.setBounds(75, 5, 80, 20);;
		btnValidate.setText("Validate");
				
		CLabel lblCompilation = new CLabel(canvasValidation, SWT.NONE);
		lblCompilation.setText("Validation");
		lblCompilation.setBounds(5, 5, 70, 20);
		return canvasValidation;
	}
	
	protected Canvas canvasName(Composite container) {
		//****** CANVAS NAME *********
		Canvas canvasName = new Canvas(container, SWT.NONE);
		canvasName.setBounds(10, 10, 720, 30);
				
		textName = new Text(canvasName, SWT.BORDER);
		textName.setBounds(70, 5, 300, 20);
		textName.setText(eobject.getName());
		if (!(eobject instanceof EWorldAgent)) {
			textName.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent event) {
					TransactionalEditingDomain domain = TransactionUtil.getEditingDomain(eobject);
			    	domain.getCommandStack().execute(new RecordingCommand(domain) {
			    	     public void doExecute() {
			    	    	 eobject.setName(textName.getText());
			    	     }
			    	  });
			       	 ef.hasDoneChanges = true;
			    }
			});
		}
		else {
			textName.setEditable(false);
		}
			
		CLabel lblName = new CLabel(canvasName, SWT.NONE);
		lblName.setBounds(10, 5, 60, 20);
		lblName.setText("Name");
		return canvasName;
	}
	

	/**
	 * Create the menu manager.
	 * @return the menu manager
	 */
	@Override
	protected MenuManager createMenuManager() {
		MenuManager menuManager = new MenuManager("menu");
		return menuManager;
	}

	/**
	 * Create the toolbar manager.
	 * @return the toolbar manager
	 */
	@Override
	protected ToolBarManager createToolBarManager(int style) {
		ToolBarManager toolBarManager = new ToolBarManager(style);
		return toolBarManager;
	}

	/**
	 * Create the status line manager.
	 * @return the status line manager
	 */
	@Override
	protected StatusLineManager createStatusLineManager() {
		StatusLineManager statusLineManager = new StatusLineManager();
		return statusLineManager;
	}


	/**
	 * Configure the shell.
	 * @param newShell
	 */
	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText(name);
	}

	/**
	 * Return the initial size of the window.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(743, 707);
	}
	
	
}
