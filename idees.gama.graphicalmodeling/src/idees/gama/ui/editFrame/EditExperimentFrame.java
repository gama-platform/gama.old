package idees.gama.ui.editFrame;

import gama.EExperiment;
import gama.EParameter;
import gama.ESpecies;
import gama.EVariable;
import idees.gama.features.edit.EditFeature;

import java.util.List;

import msi.gama.util.GamaList;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.transaction.util.TransactionUtil;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

public class EditExperimentFrame extends EditFrame {
	
	StyledText gamlCode;
	ESpecies species;
	Table table_params;
	List<String> variables;
	List<String> types_parameter_tot = GamaList.with("int", "float", "string", "file", "list", "matrix", "map");
	/**
	 * Create the application window.
	 */
	public EditExperimentFrame(Diagram diagram, IFeatureProvider fp, EditFeature eaf, EExperiment experiment, String name) {	
		super(diagram, fp, eaf,  experiment, name == null ? "Experiment definition" : name );
		this.species = experiment.getExperimentLink().getSpecies();
	}

	/**
	 * Create contents of the application window.
	 * @param parent
	 */
	@Override
	protected Control createContents(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		variables = new GamaList<String>();
		for (EVariable var : species.getVariables()) {
			if (types_parameter_tot.contains(var.getType())) {
				variables.add(var.getName());
			}
		}
				
		
		//****** CANVAS NAME *********
		Canvas canvasName = canvasName(container);
		canvasName.setBounds(10, 10, 720, 30);
		
		//****** CANVAS PARAMETER *********
		Canvas canvasParameter = canvasParameter(container);
		canvasParameter.setBounds(10, 50, 720, 305);

		//****** CANVAS OK/CANCEL *********
		Canvas canvasOkCancel = canvasOkCancel(container);
		canvasOkCancel.setBounds(10, 365, 720, 30);
		return container;
	}
	
	protected Canvas canvasParameter(Composite container) {
		
		//****** CANVAS PARAMETER *********
		Canvas canvasParameter = new Canvas(container, SWT.BORDER);
		canvasParameter.setBounds(10, 515, 720, 305);
		
		CLabel lblCompilation = new CLabel(canvasParameter, SWT.NONE);
		lblCompilation.setText("Parameters");
		lblCompilation.setBounds(5, 5, 70, 20);
		
		table_params = createTableEditor(canvasParameter);
		table_params.setBounds(10, 30, 700, 230);
		table_params.setHeaderVisible(true);
		table_params.setLinesVisible(true);
		table_params.setLinesVisible(true);
		initTable();
		
		CLabel lblVariables = new CLabel(canvasParameter, SWT.NONE);
		lblVariables.setBounds(10, 5, 100, 20);
		lblVariables.setText("Parameters");
		Button btnAddVariable = new Button(canvasParameter, SWT.NONE);
		btnAddVariable.addSelectionListener(new SelectionAdapter() {
			 
			@Override
			public void widgetSelected(SelectionEvent e) {
				TableItem ti =  new TableItem(table_params, SWT.NONE);
				final String var = variables.get(0);
				ti.setText(new String[] {var, var,"","","","",""});
			}
		});
		btnAddVariable.setBounds(50, 275, 130, 20);
		btnAddVariable.setText("Add parameter");
		
		Button btnDeleteVariable = new Button(canvasParameter, SWT.NONE);
		btnDeleteVariable.addSelectionListener(new SelectionAdapter() {
			 
			@Override
			public void widgetSelected(SelectionEvent e) {
				int[] indices = table_params.getSelectionIndices();
				table_params.remove( indices);
				table_params.redraw();
			}
		});
		btnDeleteVariable.setBounds(220, 275, 130, 20);
		btnDeleteVariable.setText("Delete parameter");
		return canvasParameter;
	}
	
	  
	 void initTable() {
		 for (EParameter var: ((EExperiment) eobject).getParameters()) {
			TableItem ti =  new TableItem(table_params, SWT.NONE);
			ti.setText(new String[] {var.getVariable(),var.getName(),var.getCategory(),var.getInit(),var.getMin(),var.getMax(), var.getStep(), var.getAmong()});
		 }
	  }
		 
		 /**
		   * Creates the main window's contents
		   * 
		   * @param shell the main window
		   */
	private Table createTableEditor(Composite container) {
		    // Create the table
		    final Table tableVars = new Table(container, SWT.SINGLE | SWT.FULL_SELECTION
		        | SWT.HIDE_SELECTION);
		    tableVars.setHeaderVisible(true);
		    tableVars.setLinesVisible(true);

		    TableColumn tblclmnVar = new TableColumn(tableVars, SWT.NONE);
		    tblclmnVar.setWidth(100);
		    tblclmnVar.setText("variable");
			
			TableColumn tblclmnText = new TableColumn(tableVars, SWT.NONE);
			tblclmnText.setWidth(100);
			tblclmnText.setText("text");
			
			TableColumn tblclmnCategory = new TableColumn(tableVars, SWT.NONE);
			tblclmnCategory.setWidth(100);
			tblclmnCategory.setText("category");
			
			TableColumn tblclmnInitValue = new TableColumn(tableVars, SWT.NONE);
			tblclmnInitValue.setWidth(100);
			tblclmnInitValue.setText("init value");
			
			TableColumn tblclmnMin = new TableColumn(tableVars, SWT.NONE);
			tblclmnMin.setWidth(100);
			tblclmnMin.setText("min");
			
			TableColumn tblclmnMax = new TableColumn(tableVars, SWT.NONE);
			tblclmnMax.setWidth(100);
			tblclmnMax.setText("max");
			
			TableColumn tblclmnStep = new TableColumn(tableVars, SWT.NONE);
			tblclmnStep.setWidth(100);
			tblclmnStep.setText("step");
			
			TableColumn tblclmnAmong = new TableColumn(tableVars, SWT.NONE);
			tblclmnAmong.setWidth(100);
			tblclmnAmong.setText("among");
			
			

		    // Create an editor object to use for text editing
		    final TableEditor editor = new TableEditor(tableVars);
		    editor.horizontalAlignment = SWT.LEFT;
		    editor.grabHorizontal = true;

		    // Use a mouse listener, not a selection listener, since we're interested
		    // in the selected column as well as row
		    tableVars.addMouseListener(new MouseAdapter() {
		      public void mouseDown(MouseEvent event) {
		        // Dispose any existing editor
		        Control old = editor.getEditor();
		        if (old != null) old.dispose();

		        // Determine where the mouse was clicked
		        Point pt = new Point(event.x, event.y);

		        // Determine which row was selected
		        final TableItem item = tableVars.getItem(pt);
		        if (item != null) {
		          // Determine which column was selected
		          int column = -1;
		          for (int i = 0, n = tableVars.getColumnCount(); i < n; i++) {
		            Rectangle rect = item.getBounds(i);
		            if (rect.contains(pt)) {
		              // This is the selected column
		              column = i;
		              break;
		            }
		          }

		          if (column == 0) {
		            // Create the dropdown and add data to it
		            final CCombo combo = new CCombo(tableVars, SWT.READ_ONLY);
		            for (int i = 0, n = variables.size(); i < n; i++) {
		              combo.add(variables.get(i));
		            }

		            // Select the previously selected item from the cell
		            combo.select(combo.indexOf(item.getText(column)));

		            // Compute the width for the editor
		            // Also, compute the column width, so that the dropdown fits
		            //editor.minimumWidth = combo.computeSize(SWT.DEFAULT, SWT.DEFAULT).x;
		            //table.getColumn(column).setWidth(editor.minimumWidth);

		            // Set the focus on the dropdown and set into the editor
		            combo.setFocus();
		            editor.setEditor(combo, item, column);

		            // Add a listener to set the selected item back into the cell
		            final int col = column;
		            combo.addSelectionListener(new SelectionAdapter() {
		              public void widgetSelected(SelectionEvent event) {
		                item.setText(col, combo.getText());
		                // They selected an item; end the editing session
		                combo.dispose();
		              }
		            });
		          } else if (column != 1) {
		            // Create the Text object for our editor
		            final Text text = new Text(tableVars, SWT.NONE);
		            text.setForeground(item.getForeground());

		            // Transfer any text from the cell to the Text control,
		            // set the color to match this row, select the text,
		            // and set focus to the control
		            text.setText(item.getText(column));
		            text.setForeground(item.getForeground());
		            text.selectAll();
		            text.setFocus();

		            // Recalculate the minimum width for the editor
		            editor.minimumWidth = text.getBounds().width;

		            // Set the control into the editor
		            editor.setEditor(text, item, column);

		            // Add a handler to transfer the text back to the cell
		            // any time it's modified
		            final int col = column;
		            text.addModifyListener(new ModifyListener() {
		              public void modifyText(ModifyEvent event) {
		                // Set the text of the editor's control back into the cell
		          
		            	 item.setText(col, text.getText());
		              }
		            });
		          }
		        }
		      }
		    });
		    return tableVars;
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
	    	    	 EExperiment xp = (EExperiment) eobject;
	    	 		List<EParameter> params = new GamaList<EParameter>();
	    	 		params.addAll(xp.getParameters());
	    	 		xp.getParameters().clear();
	    	 		for (EParameter par : params) {
	    	 			EcoreUtil.delete((EObject) par, true);
	    	 		}
	    	 		for (final TableItem item : table_params.getItems()) {
	    	 			final EParameter par = gama.GamaFactory.eINSTANCE.createEParameter();
	    	 			par.setVariable(item.getText(0));
	    	 			par.setName(item.getText(1));
	    	 			par.setCategory(item.getText(2));
	    	 			par.setInit(item.getText(3));
	    	 			par.setMin(item.getText(4));
	    	 			par.setMax(item.getText(5));
	    	 			par.setStep(item.getText(6));
	    	 			par.setAmong(item.getText(7));
	    	 			xp.getParameters().add(par);  	  
	    	 		}
	    	     }
	    	  });
		} 
		
       	 ef.hasDoneChanges = true;
		
	}
	
	
}

