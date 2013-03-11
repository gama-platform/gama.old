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
	ESpecies species;
	Diagram diagram;
	EditSpeciesFeature esf;
	IFeatureProvider fp;
	
	//Variables
	int cpt = 1;
	private Table table_vars;
	private String[] types_base = {"int", "float", "string", "list", "map", "geometry", "path", "graph"};
	private List<String> types = new GamaList<String>(types_base);
	private StyledText validationResult;
	
	//Name
	private Text textName;
	
	//Shapes
	private CCombo comboShape;
	private String[] type_shape = {"point", "polyline", "polygon", "circle", "square", "rectangle", "hexagon", "sphere", "expression"};
	private Text textRadius;
	private Text textHeight;
	private Text textWidth;
	private Text textSize;
	private Text textPoints;
	private Text textShape;
	Composite sizeComp;
	Composite radiusComp;
	Composite wHComp;
	Composite pointsComp;
	Composite expShapeComp;
	
	//Torus
	private Text textTorus;
	private String torusStr;
	
	//Location
	private Text textLoc;
	private String locStr;
	
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
		//container.setSize(740, 800);
		//****** CANVAS VARIABLES *********
		Canvas canvasVariable = new Canvas(container, SWT.NONE);
		canvasVariable.setBounds(10, 250, 720, 250);
		
		table_vars = createTableEditor(canvasVariable);
		table_vars.setBounds(10, 30, 700, 170);
		table_vars.setHeaderVisible(true);
		table_vars.setLinesVisible(true);
		table_vars.setLinesVisible(true);
		initTable();
		
		CLabel lblVariables = new CLabel(canvasVariable, SWT.NONE);
		lblVariables.setBounds(10, 5, 100, 20);
		lblVariables.setText("Variables");
		
		Button btnAddVariable = new Button(canvasVariable, SWT.NONE);
		btnAddVariable.addSelectionListener(new SelectionAdapter() {
			 
			@Override
			public void widgetSelected(SelectionEvent e) {
				TableItem ti =  new TableItem(table_vars, SWT.NONE);
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
		btnAddVariable.setBounds(62, 212, 94, 28);
		btnAddVariable.setText("Add variable");
		
		Button btnDeleteVariable = new Button(canvasVariable, SWT.NONE);
		btnDeleteVariable.addSelectionListener(new SelectionAdapter() {
			 
			@Override
			public void widgetSelected(SelectionEvent e) {
				int[] indices = table_vars.getSelectionIndices();
				for (int i : indices) {
					TableItem item = table_vars.getItem(i);
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
				table_vars.remove( indices);
				table_vars.redraw();
			}
		});
		btnDeleteVariable.setBounds(163, 212, 112, 28);
		btnDeleteVariable.setText("Delete variable");
		
		
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
		
		
		
		//****** CANVAS NAME *********
		Canvas canvasName = new Canvas(container, SWT.NONE);
		canvasName.setBounds(10, 10, 720, 30);
		
		textName = new Text(canvasName, SWT.BORDER);
		textName.setBounds(70, 5, 300, 20);
		textName.setText(species.getName());
		textName.addModifyListener(new ModifyListener() {
             public void modifyText(ModifyEvent event) {
               species.setName(textName.getText());
               esf.hasDoneChanges = true;
               
           	     }
           	  });
		
		CLabel lblName = new CLabel(canvasName, SWT.NONE);
		lblName.setBounds(10, 5, 60, 20);
		lblName.setText("Name");
		
		//****** CANVAS TOPOLOGY *********
		Canvas canvasTopo = new Canvas(container, SWT.NONE);
		canvasTopo.setBounds(10, 50, 720, 190);
		
		// Shape
		final Composite shapeComp = new Composite(canvasTopo, SWT.BORDER);
		shapeComp.setForeground(this.getShell().getDisplay().getSystemColor(SWT.COLOR_BLACK));
		shapeComp.setBounds(10, 5, 700, 110);
		CLabel lblShape = new CLabel(shapeComp, SWT.NONE);
		lblShape.setBounds(5, 5, 50, 20);
		lblShape.setText("Shape");
		
		comboShape = new CCombo(shapeComp, SWT.BORDER);
		comboShape.setBounds(60, 5, 300, 20);
		comboShape.setItems(type_shape);
		comboShape.setText("point");
	//	"point", "polyline", "polygon", "circle", "square", "rectangle", "hexagon", "sphere", "expression"
		comboShape.addSelectionListener(new SelectionAdapter() {
             public void widgetSelected(SelectionEvent event) {
               String val = comboShape.getText();
               if (val.equals("point")) {
            	  sizeComp.setVisible(false);
           		  sizeComp.setEnabled(false);
           		  radiusComp.setVisible(false);
           		  radiusComp.setEnabled(false);
           		  wHComp.setVisible(false);
           		  wHComp.setEnabled(false);
           		  pointsComp.setVisible(false);
           		  pointsComp.setEnabled(false);
           		  expShapeComp.setVisible(false);
           		  expShapeComp.setEnabled(false); 
           		  modifyShape(comboShape.getText()+ "(location)");
               } else if (val.equals("polyline") || val.equals("polygon") ) {
            	   sizeComp.setVisible(false);
            	   sizeComp.setEnabled(false);
            	   radiusComp.setVisible(false);
            	   radiusComp.setEnabled(false);
            	   wHComp.setVisible(false);
            	   wHComp.setEnabled(false);
            	   pointsComp.setVisible(true);
            	   pointsComp.setEnabled(true);
            	   expShapeComp.setVisible(false);
            	   expShapeComp.setEnabled(false);
            	   modifyShape(comboShape.getText()+ "("+textPoints+")");
             } else if (val.equals("circle") || val.equals("sphere") ) {
	          	   sizeComp.setVisible(false);
	          	   sizeComp.setEnabled(false);
	          	   radiusComp.setVisible(true);
	      		   radiusComp.setEnabled(true);
	      		 pointsComp.setVisible(false);
          		  pointsComp.setEnabled(false);
	          	   wHComp.setVisible(false);
	          	   wHComp.setEnabled(false);
	          	   expShapeComp.setVisible(false);
	          	   expShapeComp.setEnabled(false); 
	        	   expShapeComp.setEnabled(false); 
	        	   modifyShape(comboShape.getText()+ "(" + textRadius.getText()+")");
             } else if (val.equals("square")) {
	          	   sizeComp.setVisible(true);
	          	   sizeComp.setEnabled(true);
	          	   radiusComp.setVisible(false);
	      		   radiusComp.setEnabled(false);
	      		  pointsComp.setVisible(false);
          		   pointsComp.setEnabled(false);
	          	   wHComp.setVisible(false);
	          	   wHComp.setEnabled(false);
	          	   expShapeComp.setVisible(false);
	          	   expShapeComp.setEnabled(false); 
	          	 modifyShape(comboShape.getText()+ "(" + textSize.getText()+")");
             } else if (val.equals("rectangle") || val.equals("hexagon") ) {
	          	   sizeComp.setVisible(false);
	          	   sizeComp.setEnabled(false);
	          	   radiusComp.setVisible(false);
	      		   radiusComp.setEnabled(false);
	      		  pointsComp.setVisible(false);
          		  pointsComp.setEnabled(false);
	          	   wHComp.setVisible(true);
	          	   wHComp.setEnabled(true);
	          	   expShapeComp.setVisible(false);
	          	   expShapeComp.setEnabled(false); 
	          	 modifyShape(comboShape.getText()+ "({" + textWidth.getText() + ","+ textHeight.getText()+"})");
             } else if (val.equals("expression")) {
	          	   sizeComp.setVisible(false);
	          	   sizeComp.setEnabled(false);
	          	   radiusComp.setVisible(false);
	      		   radiusComp.setEnabled(false);
	      		   pointsComp.setVisible(false);
        		   pointsComp.setEnabled(false);
	          	   wHComp.setVisible(false);
	          	   wHComp.setEnabled(false);
	          	   expShapeComp.setVisible(true);
	          	   expShapeComp.setEnabled(true); 
	          	   modifyShape(textShape.getText());
           } 
               shapeComp.pack();
             }
             
           });
		
		//Square
		sizeComp = new Composite(shapeComp, SWT.NONE);
		sizeComp.setVisible(false);
		sizeComp.setEnabled(false);
		sizeComp.setBounds(20, 40, 600, 60);
		CLabel lblSize = new CLabel(sizeComp, SWT.NONE);
		lblSize.setBounds(0, 0, 60, 20);
		lblSize.setText("Size");
				
		textSize = new Text(sizeComp, SWT.BORDER);
		textSize.setBounds(70, 0, 300, 20);
		textSize.setText("1.0");
		textSize.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent event) {
            	modifyShape(comboShape.getText()+ "(" + textSize.getText()+")");
            }
         });
		
		//Circle - Sphere
		radiusComp = new Composite(shapeComp, SWT.NONE);
		radiusComp.setVisible(false);
		radiusComp.setEnabled(false);
		radiusComp.setBounds(20, 40, 600, 60);
		CLabel lblRadius = new CLabel(radiusComp, SWT.NONE);
		lblRadius.setBounds(0, 0, 60, 20);
		lblRadius.setText("Radius");
		
		
		textRadius = new Text(radiusComp, SWT.BORDER);
		textRadius.setBounds(70, 0, 300, 20);
		textRadius.setText("1.0");
		textRadius.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent event) {
            	modifyShape(comboShape.getText()+ "(" + textRadius.getText()+")");
            }
         });
		
		//Hexagon - Rectangle
		wHComp = new Composite(shapeComp, SWT.NONE);
		wHComp.setBounds(20, 40, 600, 60);
		wHComp.setVisible(false);
		wHComp.setEnabled(false);
		CLabel lblHeight = new CLabel(wHComp, SWT.NONE);
		lblHeight.setBounds(0, 30, 60, 20);
		lblHeight.setText("Height");
				
		textHeight = new Text(wHComp, SWT.BORDER);
		textHeight.setBounds(70, 30, 300, 20);
		textHeight.setText("1.0");
		textHeight.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent event) {
            	modifyShape(comboShape.getText()+ "({" + textWidth.getText() + ","+ textHeight.getText()+"})");
            }
         });
		
		CLabel lblWidth = new CLabel(wHComp, SWT.NONE);
		lblWidth.setBounds(0, 0, 60, 20);
		lblWidth.setText("Width");
				
		textWidth = new Text(wHComp, SWT.BORDER);
		textWidth.setBounds(70, 0, 300, 20);
		textWidth.setText("1.0");
		textWidth.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent event) {
            	modifyShape(comboShape.getText()+ "({" + textWidth.getText() + ","+ textHeight.getText()+"})");
            }
         });
		
		//Polygon, Polyline
		pointsComp = new Composite(shapeComp, SWT.NONE);
		pointsComp.setVisible(false);
		pointsComp.setEnabled(false);
		pointsComp.setBounds(20, 40, 600, 60);
		CLabel lblPoints = new CLabel(pointsComp, SWT.NONE);
		lblPoints.setBounds(0, 0, 60, 20);
		lblPoints.setText("Points");
						
		textPoints = new Text(pointsComp, SWT.BORDER);
		textPoints.setBounds(70, 0, 300, 20);
		textPoints.setText("[{0.0,0.0},{0.0,1.0},{1.0,1.0},{1.0,0.0}]");
		textPoints.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent event) {
            	modifyShape(comboShape.getText()+ "("+textPoints+")");
            }
         });
		
		//Expression shape
		expShapeComp = new Composite(shapeComp, SWT.NONE);
		expShapeComp.setVisible(false);
		expShapeComp.setEnabled(false);
		expShapeComp.setBounds(20, 50, 600, 60);
		CLabel lblExpShape = new CLabel(expShapeComp, SWT.NONE);
		lblExpShape.setBounds(0, 0, 70, 20);
		lblExpShape.setText("Expression");
								
		textShape = new Text(expShapeComp, SWT.BORDER);
		textShape.setBounds(70, 0, 300, 20);
		textShape.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent event) {
            	modifyShape(textShape.getText());
            }
         });
		
		
		
		// Location
		CLabel lblLocation = new CLabel(canvasTopo, SWT.NONE);
		lblLocation.setBounds(10, 130, 60, 20);
		lblLocation.setText("Location");
		
		textLoc = new Text(canvasTopo, SWT.BORDER);
		textLoc.setBounds(270, 130, 300, 18);
		textLoc.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent event) {
            	modifyLocation(textLoc.getText());
            }
         });
		
		Button btnRnd = new Button(canvasTopo, SWT.RADIO);
		btnRnd.setBounds(80, 130, 100, 18);
		btnRnd.setText("Random");
		btnRnd.setSelection(true);
		btnRnd.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				textLoc.setEnabled(false);	
				locStr = "random";
				modifyLocation("any_location_in(world.shape)");
			}
		});
		
		Button btnExpressionLoc = new Button(canvasTopo, SWT.RADIO);
		btnExpressionLoc.setBounds(180, 130, 85, 18);
		btnExpressionLoc.setText("Expression:");
		btnExpressionLoc.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				textLoc.setEnabled(true);	
				modifyLocation(textLoc.getText());
			}
		});
		
		//is Torus
		CLabel lblIsTorus = new CLabel(canvasTopo, SWT.NONE);
		lblIsTorus.setBounds(10, 160, 60, 20);
		lblIsTorus.setText("is Torus?");
		
		Button btnYes = new Button(canvasTopo, SWT.RADIO);
		btnYes.setBounds(80, 160, 50, 18);
		btnYes.setText("Yes");
		btnYes.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				textTorus.setEnabled(false);	
				modifyIsTorus("true");
			}
		});
		
		Button btnNo = new Button(canvasTopo, SWT.RADIO);
		btnNo.setBounds(130, 160, 50, 18);
		btnNo.setText("No");
		btnNo.setSelection(true);
		btnNo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				modifyIsTorus("false");
				textTorus.setEnabled(false);	
			}
		});
		
		Button btnExpressionTorus = new Button(canvasTopo, SWT.RADIO);
		btnExpressionTorus.setBounds(180, 160, 85, 18);
		btnExpressionTorus.setText("Expression:");
		btnExpressionTorus.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				textTorus.setEnabled(true);	
				modifyIsTorus(textTorus.getText());
			}
		});
		
		textTorus = new Text(canvasTopo, SWT.BORDER);
		textTorus.setBounds(270, 160, 300, 18);
		textTorus.setEnabled(false);
		textTorus.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent event) {
            	modifyIsTorus(textTorus.getText());
            }
         });
		
	
		
		/*ListViewer listViewer = new ListViewer(container, SWT.BORDER | SWT.V_SCROLL);
		org.eclipse.swt.widgets.List list = listViewer.getList();
		FormData fd_list = new FormData();
		fd_list.right = new FormAttachment(table_1, 0, SWT.RIGHT);
		fd_list.top = new FormAttachment(100, -129);
		fd_list.bottom = new FormAttachment(100, -44);
		fd_list.left = new FormAttachment(0, 22);
		list.add("test1");
		list.add("test2");
		list.add("test3");
		
		list.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseUp(MouseEvent e) {
				System.out.println("mouseUp e.getSource() : " + e.getSource());
				
			}
			
			@Override
			public void mouseDown(MouseEvent e) {
				
			}
			
			@Override
			public void mouseDoubleClick(MouseEvent e) {
				
			}
		});
		list.setLayoutData(fd_list);
		
		CLabel lblReflexOrder = new CLabel(container, SWT.NONE);
		FormData fd_lblReflexOrder = new FormData();
		fd_lblReflexOrder.bottom = new FormAttachment(list, -6);
		fd_lblReflexOrder.left = new FormAttachment(0, 28);
		lblReflexOrder.setLayoutData(fd_lblReflexOrder);
		lblReflexOrder.setText("Reflex order");*/
		
		
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
	
	private void modifyShape(final String newShape) {
		final EVariable var = getEVariable("shape") == null ? gama.GamaFactory.eINSTANCE.createEVariable() : getEVariable("shape");
		var.setName("shape");
		var.setType("geometry");
    	
	    TransactionalEditingDomain domain = TransactionUtil.getEditingDomain(var);
	    	domain.getCommandStack().execute(new RecordingCommand(domain) {
	    	     public void doExecute() {
	    	    	 if (! species.getVariables().contains(var))
	    	    		 species.getVariables().add(var);
	    	    	 var.setInit(newShape);
	    	     }
	    	  });
	    esf.hasDoneChanges = true;
  	     
	}
	
	private void modifyLocation(final String newLoc) {
		final EVariable var = getEVariable("location") == null ? gama.GamaFactory.eINSTANCE.createEVariable() : getEVariable("location");
		var.setName("location");
		var.setType("point");
    	
	    TransactionalEditingDomain domain = TransactionUtil.getEditingDomain(var);
	    	domain.getCommandStack().execute(new RecordingCommand(domain) {
	    	     public void doExecute() {
	    	    	 if (! species.getVariables().contains(var))
	    	    		 species.getVariables().add(var);
	    	    	 var.setInit(newLoc);
	    	     }
	    	  });
	    esf.hasDoneChanges = true;
  	     
	}
	
	private void modifyIsTorus(final String newisTorus) {
		species.setTorus(newisTorus);
	    esf.hasDoneChanges = true;
  	     
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

	    TableColumn tblclmnName = new TableColumn(tableVars, SWT.NONE);
		tblclmnName.setWidth(100);
		tblclmnName.setText("Name");
		
		TableColumn tblclmnType = new TableColumn(tableVars, SWT.NONE);
		tblclmnType.setWidth(100);
		tblclmnType.setText("Type");
		
		TableColumn tblclmnInitValue = new TableColumn(tableVars, SWT.NONE);
		tblclmnInitValue.setWidth(100);
		tblclmnInitValue.setText("init value");
		
		TableColumn tblclmnUpdate = new TableColumn(tableVars, SWT.NONE);
		tblclmnUpdate.setWidth(100);
		tblclmnUpdate.setText("update");
		
		TableColumn tblclmnFunction = new TableColumn(tableVars, SWT.NONE);
		tblclmnFunction.setWidth(100);
		tblclmnFunction.setText("function");
		
		TableColumn tblclmnMin = new TableColumn(tableVars, SWT.NONE);
		tblclmnMin.setWidth(100);
		tblclmnMin.setText("min");
		
		TableColumn tblclmnMax = new TableColumn(tableVars, SWT.NONE);
		tblclmnMax.setWidth(100);
		tblclmnMax.setText("max");
		
		

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

	          // Column 2 holds dropdowns
	          if (column == 1) {
	            // Create the dropdown and add data to it
	            final CCombo combo = new CCombo(tableVars, SWT.READ_ONLY);
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
	    return tableVars;
	  }
	  
	 void initTable() {
		 for (EVariable var: species.getVariables()) {
			TableItem ti =  new TableItem(table_vars, SWT.NONE);
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
		newShell.setText("Species definition");
	}

	/**
	 * Return the initial size of the window.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(743, 707);
	}
	
	
}
