package idees.gama.ui.editFrame;

import gama.EAspectLink;
import gama.EChartLayer;
import gama.ELayer;
import gama.ESpecies;

import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.util.GamaList;

import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.events.ShellListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.ColorDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

public class EditLayerFrame {

	// Types
	private CCombo comboType;
	private String[] type_shape = { "species", "grid", "agents","image", "text", "chart" };
	private String[] species_list;
	private String[] grid_list;
	private Map<String, String[]> aspectsSpecies;
	private final String[] styles_layer = { IKeyword.LINE, IKeyword.WHISKER, IKeyword.AREA,
			IKeyword.BAR, IKeyword.DOT, IKeyword.STEP, IKeyword.SPLINE, IKeyword.STACK, 
			IKeyword.THREE_D, IKeyword.RING, IKeyword.EXPLODED };
	private final String[] types_chart =  {IKeyword.XY, IKeyword.HISTOGRAM,
			IKeyword.SERIES, IKeyword.PIE, IKeyword.BOX_WHISKER};
	

	private String[] aspects;
	EditLayerFrame layerFrame;
	
	private Text textX;
	private Text textY;
	private Text positionX;
	private Text positionY;
	private Text textPath;
	private Text textText;
	private Text textSizeText;
	private Text textName;
	private Text textAgents;

	private Text transparency;
	private Text refresh;
	
	
	Composite speciesComp;
	Composite chartComp;
	Composite gridComp;
	Composite agentsComp;
	Composite textComp;
	Composite imageComp;
	Composite shapeComp;
	
	private CCombo comboSpecies;
	private CCombo comboAspectsSpecies;
	private CCombo comboAspectsAgents;
	private CCombo comboGrid;
	private CCombo comboTypeChart;
	boolean ok = false;
	boolean edit;
	
	private Table table_chart_layers;
	private List<EChartLayer> chartLayers;

	private List<EChartLayer> chartLayersTot;
	
	ELayer elayer;
	EditDisplayFrame frame;

	Color color;
	RGB rgb;

	Text textColorGrid;
	Button btnCstColGrid;
	Label colorLabelGrid;
	Text textColorText;
	Button btnCstColText;
	Label colorLabelText;
	Text textColorImage;
	Button btnCstColImage;
	Label colorLabelImage;
	Text textColorChart;
	Button btnCstColChart;
	Label colorLabelChart;
	
	public EditLayerFrame(ELayer elayer, EditDisplayFrame asp, List<ESpecies> species, List<ESpecies> grids, boolean edit) {
		init(elayer, asp, species, grids);
		this.edit = edit;
		if (edit) {
			loadData();
			updateVisible();
		}
	}

	private void loadData() {
		chartLayersTot.addAll(elayer.getChartlayers());
		chartLayers.addAll(elayer.getChartlayers());
		if (elayer.getType() != null) 
			comboType.setText(elayer.getType());
		if (elayer.getName() != null)
			textName.setText(elayer.getName());
		if (elayer.getSize_x() != null)
			textX.setText(elayer.getSize_x());
		if (elayer.getSize_y() != null)
			textY.setText(elayer.getSize_y());
		if (elayer.getPosition_x() != null)
			positionX.setText(elayer.getPosition_x());
		if (elayer.getPosition_y() != null)
			positionY.setText(elayer.getPosition_y());
		if (elayer.getFile() != null)
			textPath.setText(elayer.getFile());
		if (elayer.getRefresh() != null)
			refresh.setText(elayer.getRefresh());
		if (elayer.getTransparency() != null)
			transparency.setText(elayer.getTransparency());
		if (elayer.getText() != null)
			textText.setText(elayer.getText());
		if (elayer.getSize() != null)
			textSizeText.setText(elayer.getSize());
		if (elayer.getAgents() != null)
			textAgents.setText(elayer.getAgents());
		if (elayer.getSpecies() != null)
			comboSpecies.setText(elayer.getSpecies());
		if (elayer.getGrid() != null)
			comboGrid.setText(elayer.getGrid());
		if (elayer.getAspect() != null) {
			comboAspectsSpecies.setText(elayer.getAspect());
			comboAspectsAgents.setText(elayer.getAspect());
		}
		initTable();
	}
	
	public void buildColorComposite (Composite compositeColor, final Text textColor, final Label colorLabel, Button btnCstCol, String text){
		// COLOR
		compositeColor.setSize(700, 20);
		CLabel lblColor = new CLabel(compositeColor, SWT.NONE);
		lblColor.setBounds(0, 0, 110, 20);
		lblColor.setText(text);

		//textColor = new Text(compositeColor, SWT.BORDER);
		textColor.setBounds(465, 0, 200, 18);

				// Start with white

				rgb = new RGB(0, 0, 0);		
				color = new Color(frame.getShell().getDisplay(), rgb);

				// Use a label full of spaces to show the color
				//colorLabel = new Label(compositeColor, SWT.NONE);
				colorLabel.setText("    ");
				colorLabel.setBackground(color);
				colorLabel.setBounds(190, 0, 50, 18);

				Button button = new Button(compositeColor, SWT.PUSH);
				button.setText("Color...");
				button.setBounds(250, 0, 80, 20);
				button.addSelectionListener(new SelectionAdapter() {
					public void widgetSelected(SelectionEvent event) {
						// Create the color-change dialog
						ColorDialog dlg = new ColorDialog(frame.getShell());

						// Set the selected color in the dialog from
						// user's selected color
						dlg.setRGB(rgb);

						// Change the title bar text
						dlg.setText("Choose a Color");

						// Open the dialog and retrieve the selected color
						RGB rgb = dlg.open();
						if (rgb != null) {
							// Dispose the old color, create the
							// new one, and set into the label
							color.dispose();
							color = new Color(frame.getShell().getDisplay(), rgb);
							colorLabel.setBackground(color);
						}
					}
				});
				Composite cColor = new Composite(compositeColor, SWT.NONE);
				cColor.setBounds(110, 0, 400, 18);

				btnCstCol = new Button(cColor, SWT.RADIO);
				btnCstCol.setBounds(0, 0, 85, 18);
				btnCstCol.setText("Constant");
				btnCstCol.setSelection(true);
				btnCstCol.addSelectionListener(new SelectionAdapter() {
					public void widgetSelected(SelectionEvent e) {
						textColor.setEnabled(false);
					}
				});

				Button btnExpressionCol = new Button(cColor, SWT.RADIO);
				btnExpressionCol.setBounds(260, 0, 85, 18);
				btnExpressionCol.setText("Expression:");
				btnExpressionCol.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						textColor.setEnabled(true);
					}
				});

	}

	public void init(final ELayer elayer, EditDisplayFrame asp, List<ESpecies> species, List<ESpecies> grids) {
		frame = asp;
		layerFrame = this;
		aspectsSpecies = new Hashtable<String, String[]>();
		species_list = new String[species.size()];
		chartLayers = new GamaList<EChartLayer>();
		chartLayersTot = new GamaList<EChartLayer>();
		List<String> aspectsL = new GamaList<String>();
		for (int i = 0; i < species_list.length; i++) {
			ESpecies sp = species.get(i);
			List<String> aspL = new GamaList<String>();
			for (EAspectLink al : sp.getAspectLinks()) {
				String apN = al.getTarget().getName();
				aspL.add(apN);
				if (!aspectsL.contains(apN)) aspectsL.add(apN);
			}
			if (aspL.isEmpty()) 
				aspL.add("default");
			aspectsSpecies.put(sp.getName(), (String[]) aspL.toArray(new String[aspL.size()]));
			species_list[i] = sp.getName();
		}
		aspects = (String[]) aspectsL.toArray(new String[aspectsL.size()]);
		grid_list = new String[grids.size()];
		for (int i = 0; i < grid_list.length; i++) {
			grid_list[i] = grids.get(i).getName();
		}
		
		final Shell dialog = new Shell(asp.getShell(), SWT.APPLICATION_MODAL
				| SWT.DIALOG_TRIM );
		this.elayer = elayer;
		dialog.setText("Edit Layer");
		dialog.addShellListener(new ShellListener() {

		      public void shellActivated(ShellEvent event) {
		      }

		      public void shellClosed(ShellEvent event) {
		        MessageBox messageBox = new MessageBox(dialog, SWT.ICON_WARNING | SWT.APPLICATION_MODAL | SWT.OK | SWT.CANCEL);
		        messageBox.setText("Warning");
		        messageBox.setMessage("You have unsaved data. Close the 'Edit Display Layer' window anyway?");
		        if (messageBox.open() == SWT.OK) {
		        	if (! layerFrame.edit)
						EcoreUtil.delete(elayer);
		        	event.doit = true;
		        }   else
		          event.doit = false;
		      }

		      public void shellDeactivated(ShellEvent arg0) {
		      }

		      public void shellDeiconified(ShellEvent arg0) {
		      }

		      public void shellIconified(ShellEvent arg0) {
		      }
		    });
		canvasName(dialog);
		buildCanvasTopo(dialog);

		Canvas canvas = canvasProperties(dialog);
		canvas.setLocation(10, 310);
		builtQuitButtons(dialog);
		dialog.pack();
		dialog.open();
		dialog.setSize(740, 550);
	}
	
	public void builtQuitButtons(final Shell  dialog) {

		Canvas quitTopo = new Canvas(dialog, SWT.BORDER);
		quitTopo.setBounds(10, 460, 720, 40);
		final Button buttonOK = new Button(quitTopo, SWT.PUSH);
		buttonOK.setText("Ok");
		buttonOK.setBounds(150, 10, 80, 20);
		buttonOK.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				save();
				
				dialog.close();
			}

			private void save() {
				int index = 0;
				if (!layerFrame.edit) {
					frame.getLayers().add(elayer);
				} else {
					index = frame.getLayers().indexOf(elayer);
					frame.layerViewer.remove(index);
				}
				
				layerFrame.saveLayer();
				if (!layerFrame.edit) {
					frame.layerViewer.add(elayer.getName());
				} else {
					frame.layerViewer.add(elayer.getName(), index);
				}
			} 
		});

		Button buttonCancel = new Button(quitTopo, SWT.PUSH);
		buttonCancel.setText("Cancel");
		buttonCancel.setBounds(400, 10, 80, 20);
		buttonCancel.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				for (EChartLayer cl : chartLayersTot) {
					if (! elayer.getChartlayers().contains(cl)) {
						EcoreUtil.delete(cl);
					}
				}
				EcoreUtil.delete(elayer);
				dialog.close();
			}
		});
	}
	
	protected void saveLayer() {
		for (EChartLayer cl : chartLayersTot) {
			if (! chartLayers.contains(cl)) {
				EcoreUtil.delete(cl);
			}
		}
		elayer.setType(comboType.getText());
		elayer.setName(textName.getText());
		elayer.setSize_x(textX.getText());
		elayer.setSize_y(textY.getText());
		elayer.setPosition_x(positionX.getText());
		elayer.setRefresh(refresh.getText());
		elayer.setTransparency(transparency.getText());
		elayer.setPosition_y(positionY.getText());
		elayer.setFile(textPath.getText());
		elayer.setText(textText.getText());
		elayer.setSize(textSizeText.getText());
		elayer.setAgents(textAgents.getText());
		elayer.setSpecies(comboSpecies.getText());
		elayer.setGrid(comboGrid.getText());
		elayer.setAspect(comboType.getText().equals("species") ? comboAspectsSpecies.getText() : comboAspectsAgents.getText());
		
		elayer.getChartlayers().clear();
		elayer.getChartlayers().addAll(chartLayers);
		
		
	}

	public Canvas canvasProperties(Composite container) {
		Canvas canvasProp = new Canvas(container, SWT.BORDER);
		canvasProp.setSize(720, 130);
		CLabel lblPosition = new CLabel(canvasProp, SWT.NONE);
		lblPosition.setBounds(10, 10, 90, 20);
		lblPosition.setText("Position");
		
		CLabel lblPositionX = new CLabel(canvasProp, SWT.NONE);
		lblPositionX.setBounds(100, 10, 100, 20);
		lblPositionX.setText("X ([0,1])");

		positionX = new Text(canvasProp, SWT.BORDER);
		positionX.setBounds(200, 10, 100, 20);
		positionX.setText("0.0");
	
		CLabel lblPositionY = new CLabel(canvasProp, SWT.NONE);
		lblPositionY.setBounds(400, 10, 100, 20);
		lblPositionY.setText("Y ([0,1])");

		positionY = new Text(canvasProp, SWT.BORDER);
		positionY.setBounds(500, 10, 100, 20);
		positionY.setText("0.0");
		
		CLabel lblSize = new CLabel(canvasProp, SWT.NONE);
		lblSize.setBounds(10,40, 90, 20);
		lblSize.setText("Size");
		
		CLabel lblSizeX = new CLabel(canvasProp, SWT.NONE);
		lblSizeX.setBounds(100, 40, 100, 20);
		lblSizeX.setText("width ([0.0,1.0])");

		textX = new Text(canvasProp, SWT.BORDER);
		textX.setBounds(200, 40, 100, 20);
		textX.setText("1.0");
	
		CLabel lblSizeY = new CLabel(canvasProp, SWT.NONE);
		lblSizeY.setBounds(400, 40, 100, 20);
		lblSizeY.setText("height ([0.0,1.0])");

		textY = new Text(canvasProp, SWT.BORDER);
		textY.setBounds(500, 40, 100, 20);
		textY.setText("1.0");
		
		CLabel lblTransp = new CLabel(canvasProp, SWT.NONE);
		lblTransp.setBounds(10, 70, 90, 20);
		lblTransp.setText("Transparency");

		transparency = new Text(canvasProp, SWT.BORDER);
		transparency.setBounds(100, 70, 200, 20);
		transparency.setText("0.0");
		
		CLabel lblRefresh = new CLabel(canvasProp, SWT.NONE);
		lblRefresh.setBounds(10, 100, 90, 20);
		lblRefresh.setText("Refresh every");

		refresh = new Text(canvasProp, SWT.BORDER);
		refresh.setBounds(100, 100, 200, 20);
		refresh.setText("1.0");
		
		return canvasProp;
	}
	
	
	private void updateVisible(){
		String val = comboType.getText();
		if (val.equals("species")) {
			speciesComp.setVisible(true);
			speciesComp.setEnabled(true);
			imageComp.setVisible(false);
			imageComp.setEnabled(false);
			textComp.setEnabled(false);
			textComp.setVisible(false);
			gridComp.setEnabled(false);
			gridComp.setVisible(false);
			agentsComp.setEnabled(false);
			agentsComp.setVisible(false);
			chartComp.setEnabled(false);
			chartComp.setVisible(false);
		} else if (val.equals("grid")) {
			speciesComp.setVisible(false);
			speciesComp.setEnabled(false);
			imageComp.setVisible(false);
			imageComp.setEnabled(false);
			textComp.setEnabled(false);
			textComp.setVisible(false);
			gridComp.setEnabled(true);
			gridComp.setVisible(true);
			agentsComp.setEnabled(false);
			agentsComp.setVisible(false);
			chartComp.setEnabled(false);
			chartComp.setVisible(false);
		} else if (val.equals("agents")) {
			speciesComp.setVisible(false);
			speciesComp.setEnabled(false);
			imageComp.setVisible(false);
			imageComp.setEnabled(false);
			textComp.setEnabled(false);
			textComp.setVisible(false);
			gridComp.setEnabled(false);
			gridComp.setVisible(false);
			agentsComp.setEnabled(true);
			agentsComp.setVisible(true);
			chartComp.setEnabled(false);
			chartComp.setVisible(false);
		} else if (val.equals("image")) {
			speciesComp.setVisible(false);
			speciesComp.setEnabled(false);
			imageComp.setVisible(true);
			imageComp.setEnabled(true);
			textComp.setEnabled(false);
			textComp.setVisible(false);
			gridComp.setEnabled(false);
			gridComp.setVisible(false);
			agentsComp.setEnabled(false);
			agentsComp.setVisible(false);
			chartComp.setEnabled(false);
			chartComp.setVisible(false);
		} else if (val.equals("text")) {
			speciesComp.setVisible(false);
			speciesComp.setEnabled(false);
			imageComp.setVisible(false);
			imageComp.setEnabled(false);
			textComp.setEnabled(true);
			textComp.setVisible(true);
			gridComp.setEnabled(false);
			gridComp.setVisible(false);
			agentsComp.setEnabled(false);
			agentsComp.setVisible(false);
			chartComp.setEnabled(false);
			chartComp.setVisible(false);
		} else if (val.equals("chart")) {
			speciesComp.setVisible(false);
			speciesComp.setEnabled(false);
			imageComp.setVisible(false);
			imageComp.setEnabled(false);
			textComp.setEnabled(false);
			textComp.setVisible(false);
			gridComp.setEnabled(false);
			gridComp.setVisible(false);
			agentsComp.setEnabled(false);
			agentsComp.setVisible(false);
			chartComp.setEnabled(true);
			chartComp.setVisible(true);
		}
		shapeComp.pack();
	}

	public void buildCanvasTopo(Composite container) {
		// ****** CANVAS TYPE *********

		Canvas canvasTopo = new Canvas(container, SWT.BORDER);
		canvasTopo.setBounds(10, 50, 720, 250);

		// Shape
		shapeComp = new Composite(canvasTopo, SWT.BORDER);
		shapeComp.setBounds(10, 5, 700, 190);
		CLabel lblShape = new CLabel(shapeComp, SWT.NONE);
		lblShape.setBounds(5, 5, 50, 20);
		lblShape.setText("Type");

		comboType = new CCombo(shapeComp, SWT.BORDER);
		comboType.setBounds(60, 5, 300, 20);
		comboType.setItems(type_shape);
		comboType.setText("species");
		// "point", "polyline", "polygon", "circle", "square", "rectangle",
		// "hexagon", "sphere", "expression"
		comboType.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				updateVisible();
			}

		});
		
		

		// Species
		speciesComp = new Composite(shapeComp, SWT.NONE);
		speciesComp.setVisible(true);
		speciesComp.setEnabled(true);
		speciesComp.setBounds(20, 40, 680, 180);
		CLabel lblSpecies = new CLabel(speciesComp, SWT.NONE);
		lblSpecies.setBounds(0, 0, 60, 20);
		lblSpecies.setText("Species");

		
		comboSpecies = new CCombo(speciesComp, SWT.BORDER);
		comboSpecies.setItems(species_list);
		comboSpecies.setBounds(70, 0, 300, 20);
		if (species_list.length > 0)
			comboSpecies.setText(species_list[0]);
		comboSpecies.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent event) {
				comboAspectsSpecies.setItems(aspectsSpecies.get(comboSpecies.getText()));
				comboAspectsSpecies.setText(aspectsSpecies.get(comboSpecies.getText())[0]);
			}
		});
		
		CLabel lblAspect = new CLabel(speciesComp, SWT.NONE);
		lblAspect.setBounds(0, 30, 60, 20);
		lblAspect.setText("Aspect");
	
		comboAspectsSpecies = new CCombo(speciesComp, SWT.BORDER);
		comboAspectsSpecies.setItems(aspectsSpecies.get(comboSpecies.getText()));
		comboAspectsSpecies.setBounds(70, 30, 300, 20);
		if (species_list.length > 0)
			comboAspectsSpecies.setText(aspectsSpecies.get(comboSpecies.getText())[0]);
			
		
		// Grid
		gridComp = new Composite(shapeComp, SWT.NONE);
		gridComp.setVisible(false);
		gridComp.setEnabled(false);
		gridComp.setBounds(20, 40, 670, 190);
		CLabel lblGrid = new CLabel(gridComp, SWT.NONE);
		lblGrid.setBounds(0, 0, 60, 20);
		lblGrid.setText("grid");

				
		comboGrid = new CCombo(gridComp, SWT.BORDER);
		comboGrid.setItems(grid_list);
		comboGrid.setBounds(70, 0, 300, 20);
				if (grid_list.length > 0)
					comboGrid.setText(grid_list[0]);
		
		Composite ccg = new Composite(gridComp, SWT.NONE);
		textColorGrid = new Text(ccg, SWT.NONE);
		btnCstColGrid = new Button(ccg, SWT.BORDER);
		colorLabelGrid = new Label(ccg, SWT.NONE);
		buildColorComposite(ccg, textColorGrid, colorLabelGrid, btnCstColGrid, "line color");
		ccg.setLocation(0, 30);
		
		// Image
		imageComp = new Composite(shapeComp, SWT.NONE);
		imageComp.setBounds(20, 40, 680, 180);
		imageComp.setVisible(false);
		imageComp.setEnabled(false);
		CLabel lblPath = new CLabel(imageComp, SWT.NONE);
		lblPath.setBounds(0, 0, 60, 20);
		lblPath.setText("Path");

		textPath = new Text(imageComp, SWT.BORDER);
		textPath.setBounds(70, 0, 300, 20);
		textPath.setText("../images/background.png");
		
		Composite cci = new Composite(imageComp, SWT.NONE);
		textColorImage = new Text(cci, SWT.NONE);
		btnCstColImage = new Button(cci, SWT.BORDER);
		colorLabelImage = new Label(cci, SWT.NONE);
		buildColorComposite(cci, textColorImage, colorLabelImage, btnCstColImage, "color");
		cci.setLocation(0, 30);
		
		
		// Text		
		textComp = new Composite(shapeComp, SWT.NONE);
		textComp.setBounds(20, 40, 680, 180);
		textComp.setVisible(false);
		textComp.setEnabled(false);
		CLabel lbltext = new CLabel(textComp, SWT.NONE);
		lbltext.setBounds(0, 0, 60, 20);
		lbltext.setText("Text");

		textText = new Text(textComp, SWT.BORDER);
		textText.setBounds(70, 0, 300, 20);
		textText.setText("");

		CLabel lblSizeTxt = new CLabel(textComp, SWT.NONE);
		lblSizeTxt.setBounds(0, 30, 60, 20);
		lblSizeTxt.setText("Size");

		textSizeText = new Text(textComp, SWT.BORDER);
		textSizeText.setBounds(70, 30, 300, 20);
		textSizeText.setText("1.0");
		
		Composite cct = new Composite(textComp, SWT.NONE);
		textColorText = new Text(cct, SWT.NONE);
		btnCstColText = new Button(cct, SWT.BORDER);
		colorLabelText = new Label(cct, SWT.NONE);
		buildColorComposite(cct, textColorText, colorLabelText, btnCstColText, "color");
		cct.setLocation(0, 60);
		
		// Agents
		agentsComp = new Composite(shapeComp, SWT.NONE);
		agentsComp.setVisible(false);
		agentsComp.setEnabled(false);
		agentsComp.setBounds(20, 40, 680, 180);
		CLabel lblAgents = new CLabel(agentsComp, SWT.NONE);
		lblAgents.setBounds(0, 0, 60, 20);
		lblAgents.setText("agents");

		textAgents = new Text(agentsComp, SWT.BORDER);
		textAgents.setBounds(70, 0, 300, 20);
		textAgents.setText("[]");
		
		CLabel lblAspectA = new CLabel(agentsComp, SWT.NONE);
		lblAspectA.setBounds(0, 30, 60, 20);
		lblAspectA.setText("Aspect");
	
		comboAspectsAgents = new CCombo(agentsComp, SWT.BORDER);
		comboAspectsAgents.setItems(aspects);
		comboAspectsAgents.setBounds(70, 30, 300, 20);
		if (aspects.length > 0)
			comboAspectsAgents.setText(aspects[0]);
			
		
		// Chart	
		chartComp = new Composite(shapeComp, SWT.NONE);
		chartComp.setVisible(false);
		chartComp.setEnabled(false);
		chartComp.setBounds(20, 40, 680, 180);
		
		Composite ccc = new Composite(chartComp, SWT.NONE);
		textColorChart = new Text(ccc, SWT.NONE);
		btnCstColChart = new Button(ccc, SWT.BORDER);
		colorLabelChart = new Label(ccc, SWT.NONE);
		buildColorComposite(ccc, textColorChart, colorLabelChart, btnCstColChart, "background color");
		ccc.setLocation(0, 0);
		
		CLabel lbltypeChart = new CLabel(chartComp, SWT.NONE);
		lbltypeChart.setBounds(0, 30, 80, 20);
		lbltypeChart.setText("Chart type");
	
		comboTypeChart = new CCombo(chartComp, SWT.BORDER);
		comboTypeChart.setItems(types_chart);
		comboTypeChart.setBounds(90, 30, 200, 20);
		if (types_chart.length > 0)
			comboTypeChart.setText(types_chart[0]);
		
		Canvas canvasCL = canvasChartLayer(chartComp);
		canvasCL.setLocation(0, 70);
	}
	
	 public Canvas canvasChartLayer(Composite container) {
			//****** CANVAS CHART LAYER *********
			Canvas canvasChartLayer = new Canvas(container, SWT.NONE);
			canvasChartLayer.setSize(720, 120);
				
			table_chart_layers = createTableEditor(canvasChartLayer);
			table_chart_layers.setBounds(10, 0, 660, 80);
			table_chart_layers.setHeaderVisible(true);
			table_chart_layers.setLinesVisible(true);
			table_chart_layers.setLinesVisible(true);
			
			Button btnAddChartLayer = new Button(canvasChartLayer, SWT.NONE);
			btnAddChartLayer.addSelectionListener(new SelectionAdapter() {
					 
					@Override
					public void widgetSelected(SelectionEvent e) {
						TableItem ti =  new TableItem(table_chart_layers, SWT.NONE);
						final String name = "layer_name" ;
						ti.setText(new String[] {name,styles_layer[0] ,"",""});
						EChartLayer clayer = gama.GamaFactory.eINSTANCE.createEChartLayer();
						clayer.setName(name);
						clayer.setStyle(styles_layer[0]);
						chartLayers.add(clayer);
						chartLayersTot.add(clayer);
					}
				});
			btnAddChartLayer.setBounds(62, 90, 94, 20);
			btnAddChartLayer.setText("Add layer");
				
				Button btnDeleteChartLayer = new Button(canvasChartLayer, SWT.NONE);
				btnDeleteChartLayer.addSelectionListener(new SelectionAdapter() {
					 
					@Override
					public void widgetSelected(SelectionEvent e) {
						int[] indices = table_chart_layers.getSelectionIndices();
						table_chart_layers.remove( indices);
						chartLayers.remove(indices);
						table_chart_layers.redraw();
					}
				});
				btnDeleteChartLayer.setBounds(163, 90, 112, 20);
				btnDeleteChartLayer.setText("Delete layer");
				return canvasChartLayer;
		 }
	 
	 /**
	   * Creates the main window's contents
	   * 
	   * @param shell the main window
	   */
	  private Table createTableEditor(Composite container) {
	    // Create the table
	    final Table tableChartLayer = new Table(container, SWT.SINGLE | SWT.FULL_SELECTION
	        | SWT.HIDE_SELECTION);
	    tableChartLayer.setHeaderVisible(true);
	    tableChartLayer.setLinesVisible(true);

	    TableColumn tblclmnName = new TableColumn(tableChartLayer, SWT.NONE);
		tblclmnName.setWidth(230);
		tblclmnName.setText("Name");
		
		TableColumn tblclmnType = new TableColumn(tableChartLayer, SWT.NONE);
		tblclmnType.setWidth(100);
		tblclmnType.setText("Style");
		
		TableColumn tblclmnColor = new TableColumn(tableChartLayer, SWT.NONE);
		tblclmnColor.setWidth(100);
		tblclmnColor.setText("Color");
		
		TableColumn tblclmnValue = new TableColumn(tableChartLayer, SWT.NONE);
		tblclmnValue.setWidth(230);
		tblclmnValue.setText("Value");
		
		
	    // Create an editor object to use for text editing
	    final TableEditor editor = new TableEditor(tableChartLayer);
	    editor.horizontalAlignment = SWT.LEFT;
	    editor.grabHorizontal = true;

	    // Use a mouse listener, not a selection listener, since we're interested
	    // in the selected column as well as row
	    tableChartLayer.addMouseListener(new MouseAdapter() {
	      public void mouseDown(MouseEvent event) {
	        // Dispose any existing editor
	        Control old = editor.getEditor();
	        if (old != null) old.dispose();

	        // Determine where the mouse was clicked
	        Point pt = new Point(event.x, event.y);

	        // Determine which row was selected
	        final TableItem item = tableChartLayer.getItem(pt);
	        if (item != null) {
	          // Determine which column was selected
	          int column = -1;
	          for (int i = 0, n = tableChartLayer.getColumnCount(); i < n; i++) {
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
	            final CCombo combo = new CCombo(tableChartLayer, SWT.READ_ONLY);
	            combo.setItems(styles_layer);

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
	            final Text text = new Text(tableChartLayer, SWT.NONE);
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
	    return tableChartLayer;
	  }
	  
	 void initTable() {
		 for (EChartLayer var: chartLayers) {
			TableItem ti =  new TableItem(table_chart_layers, SWT.NONE);
			ti.setText(new String[] {var.getName(),var.getStyle(),var.getColor(), var.getValue()});
		 }
	  }
	 
	 
	protected Canvas canvasName(Composite container) {
		Canvas canvasName = new Canvas(container, SWT.BORDER);
		textName = new Text(canvasName, SWT.BORDER);
		UtilEditFrame.buildCanvasName(container, canvasName, textName, elayer, null);
		canvasName.setBounds(10, 10, 720, 30);
		return canvasName;
	}

}
