package idees.gama.features.editSpecies;


import gama.ESpecies;
import gama.EVariable;

import idees.gama.features.modelgeneration.ModelGenerationFeature;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import msi.gama.util.GamaList;

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
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

public class EditSpeciesFrame extends ApplicationWindow {
	private Table table;
	private String[] types_base = {"int", "float", "string", "list", "map", "geometry", "path", "graph"};
	private List<String> types = new GamaList<String>(types_base);
	private StyledText validationResult;
	ESpecies species;
	Diagram diagram;
	EditSpeciesFeature esf;
	IFeatureProvider fp;
	int cpt = 1;
	/**
	 * Create the application window.
	 */
	public EditSpeciesFrame(Diagram diagram, IFeatureProvider fp, EditSpeciesFeature esf, ESpecies species, List<ESpecies> speciesList) {
		super(null);
		this.diagram = diagram;
		this.fp = fp;
		this.esf = esf;
		this.species = species;
		
		createActions();
		addToolBar(SWT.FLAT | SWT.WRAP);
		addMenuBar();
		addStatusLine();
		
		for (ESpecies sp : speciesList) 
			types.add(sp.getName());
		
	}

	/**
	 * Create contents of the application window.
	 * @param parent
	 */
	@Override
	protected Control createContents(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new org.eclipse.swt.layout.FormLayout());
		
		table = createTableEditor(container);
		org.eclipse.swt.layout.FormData fd_table = new org.eclipse.swt.layout.FormData();
		fd_table.bottom = new org.eclipse.swt.layout.FormAttachment(0, 189);
		fd_table.right = new org.eclipse.swt.layout.FormAttachment(0, 682);
		fd_table.top = new org.eclipse.swt.layout.FormAttachment(0, 36);
		fd_table.left = new org.eclipse.swt.layout.FormAttachment(0, 22);
		table.setLayoutData(fd_table);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		table.setLinesVisible(true);
		initTable();
		
		CLabel lblVariables = new CLabel(container, SWT.NONE);
		org.eclipse.swt.layout.FormData fd_lblVariables = new org.eclipse.swt.layout.FormData();
		fd_lblVariables.right = new org.eclipse.swt.layout.FormAttachment(0, 83);
		fd_lblVariables.top = new org.eclipse.swt.layout.FormAttachment(0, 10);
		fd_lblVariables.left = new org.eclipse.swt.layout.FormAttachment(0, 22);
		lblVariables.setLayoutData(fd_lblVariables);
		lblVariables.setText("Variables");
		
		final StyledText validationResult = new StyledText(container, SWT.BORDER);
		org.eclipse.swt.layout.FormData fd_styledText = new org.eclipse.swt.layout.FormData();
		fd_styledText.bottom = new org.eclipse.swt.layout.FormAttachment(0, 330);
		fd_styledText.right = new org.eclipse.swt.layout.FormAttachment(0, 682);
		fd_styledText.top = new org.eclipse.swt.layout.FormAttachment(0, 261);
		fd_styledText.left = new org.eclipse.swt.layout.FormAttachment(0, 22);
		validationResult.setLayoutData(fd_styledText);
		validationResult.setEditable(false);
		
		Canvas canvas = new Canvas(container, SWT.NONE);
		org.eclipse.swt.layout.FormData fd_canvas = new org.eclipse.swt.layout.FormData();
		fd_canvas.bottom = new org.eclipse.swt.layout.FormAttachment(0, 360);
		fd_canvas.right = new org.eclipse.swt.layout.FormAttachment(0, 699);
		fd_canvas.top = new org.eclipse.swt.layout.FormAttachment(0, 10);
		fd_canvas.left = new org.eclipse.swt.layout.FormAttachment(0, 10);
		canvas.setLayoutData(fd_canvas);
		
		Button btnValidate = new Button(canvas, SWT.NONE);
		btnValidate.addSelectionListener(new SelectionAdapter() {
		 
			@Override
			public void widgetSelected(SelectionEvent e) {
				validationResult.setText(compileModel());	
			}
		});
		btnValidate.setBounds(565, 195, 94, 28);
		btnValidate.setText("Validate");
		
		CLabel lblCompilation = new CLabel(canvas, SWT.NONE);
		lblCompilation.setText("Validation");
		lblCompilation.setBounds(10, 229, 61, 19);
		
		
		Button btnAddVariable = new Button(canvas, SWT.NONE);
		btnAddVariable.addSelectionListener(new SelectionAdapter() {
			 
			@Override
			public void widgetSelected(SelectionEvent e) {
				TableItem ti =  new TableItem(table, SWT.NONE);
				final String name = "var_name" + cpt;
				ti.setText(new String[] {name, "","","","","",""});
				final EVariable var = gama.GamaFactory.eINSTANCE.createEVariable();
				 var.setName(name);
				 TransactionalEditingDomain domain = TransactionUtil.getEditingDomain(species);
            	domain.getCommandStack().execute(new RecordingCommand(domain) {
            	     public void doExecute() {
            	    	 species.getVariables().add(var);
            	     }
            	  });
				
				cpt++;
				esf.hasDoneChanges = true;
			}
		});
		btnAddVariable.setBounds(62, 195, 94, 28);
		btnAddVariable.setText("Add variable");
		
		Button btnDeleteVariable = new Button(canvas, SWT.NONE);
		btnDeleteVariable.addSelectionListener(new SelectionAdapter() {
			 
			@Override
			public void widgetSelected(SelectionEvent e) {
				int[] indices = table.getSelectionIndices();
				for (int i : indices) {
					TableItem item = table.getItem(i);
					final EVariable varSup = getEVariable(item.getText(0));
					if (varSup != null) {
						TransactionalEditingDomain domain = TransactionUtil.getEditingDomain(species);
		            	domain.getCommandStack().execute(new RecordingCommand(domain) {
		            	     public void doExecute() {
		            	    	 species.getVariables().remove(varSup);
		            	     }
		            	  });
					}
				}
				esf.hasDoneChanges = true;
				table.remove( indices);
				table.redraw();
			}
		});
		btnDeleteVariable.setBounds(162, 195, 112, 28);
		btnDeleteVariable.setText("Delete variable");
		
		
		return container;
	}
	
	private EVariable getEVariable(String name) {
		for (EVariable var : species.getVariables()) {
			if (var.getName().equals(name)) {
				return var;
			}
		}
		return null;
	}
	
	private String compileModel() {
	     URI uri = EcoreUtil.getURI( species );
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
	
	 /**
	   * Creates the main window's contents
	   * 
	   * @param shell the main window
	   */
	  private Table createTableEditor(Composite container) {
	    // Create the table
	    final Table table = new Table(container, SWT.SINGLE | SWT.FULL_SELECTION
	        | SWT.HIDE_SELECTION);
	    table.setHeaderVisible(true);
	    table.setLinesVisible(true);

	    TableColumn tblclmnName = new TableColumn(table, SWT.NONE);
		tblclmnName.setWidth(100);
		tblclmnName.setText("Name");
		
		TableColumn tblclmnType = new TableColumn(table, SWT.NONE);
		tblclmnType.setWidth(100);
		tblclmnType.setText("Type");
		
		TableColumn tblclmnInitValue = new TableColumn(table, SWT.NONE);
		tblclmnInitValue.setWidth(100);
		tblclmnInitValue.setText("init value");
		
		TableColumn tblclmnUpdate = new TableColumn(table, SWT.NONE);
		tblclmnUpdate.setWidth(100);
		tblclmnUpdate.setText("update");
		
		TableColumn tblclmnFunction = new TableColumn(table, SWT.NONE);
		tblclmnFunction.setWidth(100);
		tblclmnFunction.setText("function");
		
		TableColumn tblclmnMin = new TableColumn(table, SWT.NONE);
		tblclmnMin.setWidth(100);
		tblclmnMin.setText("min");
		
		TableColumn tblclmnMax = new TableColumn(table, SWT.NONE);
		tblclmnMax.setWidth(100);
		tblclmnMax.setText("max");
		
		

	    // Create an editor object to use for text editing
	    final TableEditor editor = new TableEditor(table);
	    editor.horizontalAlignment = SWT.LEFT;
	    editor.grabHorizontal = true;

	    // Use a mouse listener, not a selection listener, since we're interested
	    // in the selected column as well as row
	    table.addMouseListener(new MouseAdapter() {
	      public void mouseDown(MouseEvent event) {
	        // Dispose any existing editor
	        Control old = editor.getEditor();
	        if (old != null) old.dispose();

	        // Determine where the mouse was clicked
	        Point pt = new Point(event.x, event.y);

	        // Determine which row was selected
	        final TableItem item = table.getItem(pt);
	        if (item != null) {
	          // Determine which column was selected
	          int column = -1;
	          for (int i = 0, n = table.getColumnCount(); i < n; i++) {
	            Rectangle rect = item.getBounds(i);
	            if (rect.contains(pt)) {
	              // This is the selected column
	              column = i;
	              break;
	            }
	          }

	          // Column 2 holds dropdowns
	          if (column == 1) {
	            // Create the dropdown and add data to it
	            final CCombo combo = new CCombo(table, SWT.READ_ONLY);
	            for (int i = 0, n = types.size(); i < n; i++) {
	              combo.add(types.get(i));
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
	                final EVariable var = getEVariable(item.getText(0));
	                TransactionalEditingDomain domain = TransactionUtil.getEditingDomain(var);
	            	domain.getCommandStack().execute(new RecordingCommand(domain) {
	            	     public void doExecute() {
	            	    	 var.setType(combo.getText());
	            	     }
	            	  });
	            	esf.hasDoneChanges = true;
	                // They selected an item; end the editing session
	                combo.dispose();
	              }
	            });
	          } else if (column != 1) {
	            // Create the Text object for our editor
	            final Text text = new Text(table, SWT.NONE);
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
	            	   final EVariable var = getEVariable(item.getText(0));
	            	  
	            	  TransactionalEditingDomain domain = TransactionUtil.getEditingDomain(var);
	            	  domain.getCommandStack().execute(new RecordingCommand(domain) {
	            	     public void doExecute() {
	            	    	 switch (col) 
		   	            	  { 
		   	            	 	case 0: var.setName(text.getText()); break; 
		   	            	 	case 2: var.setInit(text.getText()); break; 
		   	            	 	case 3: var.setUpdate(text.getText()); break; 
		   	            	 	case 4: var.setFunction(text.getText()); break; 
		   	            	 	case 5: var.setMin(text.getText()); break; 
		   	            	 	case 6: var.setMax(text.getText()); break; 
		   	            		
		   	            	 }
	            	     }
	            	  });
	           
	            	 item.setText(col, text.getText());
	            	 esf.hasDoneChanges = true;
	              }
	            });
	          }
	        }
	      }
	    });
	    return table;
	  }
	  
	 void initTable() {
		 for (EVariable var: species.getVariables()) {
			TableItem ti =  new TableItem(table, SWT.NONE);
			ti.setText(new String[] {var.getName(),var.getType(),var.getInit(),var.getUpdate(),var.getFunction(),var.getMin(),var.getMax()});
			cpt++; 
		 }
	  }

	/**
	 * Create the actions.
	 */
	private void createActions() {
		// Create the actions
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
		newShell.setText(species.getName());
	}

	/**
	 * Return the initial size of the window.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(743, 531);
	}
}
