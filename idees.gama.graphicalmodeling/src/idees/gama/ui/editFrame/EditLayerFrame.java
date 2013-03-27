package idees.gama.ui.editFrame;

import gama.ELayer;
import gama.ESpecies;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class EditLayerFrame {

	// Types
	private CCombo comboType;
	private String[] type_shape = { "species", "grid", "agents","image", "text" };
	private String[] species_list;
	private String[] grid_list;
	
	private Text textX;
	private Text textY;
	private Text positionX;
	private Text positionY;
	private Text textColor;
	private Text textPath;
	private Text textText;
	private Text textSizeText;
	private Text textName;
	private Text textAgents;
	
	Composite speciesComp;
	Composite gridComp;
	Composite agentsComp;
	Composite textComp;
	Composite imageComp;
	
	private CCombo comboSpecies;
	private CCombo comboGrid;
	boolean ok = false;
	
	ELayer elayer;
	EditDisplayFrame frame;

	public EditLayerFrame(ELayer elayer, EditDisplayFrame asp, List<ESpecies> species, List<ESpecies> grids) {
		frame = asp;
		species_list = new String[species.size()];
		for (int i = 0; i < species_list.length; i++) {
			species_list[i] = species.get(i).getName();
		}
		grid_list = new String[grids.size()];
		for (int i = 0; i < grid_list.length; i++) {
			grid_list[i] = grids.get(i).getName();
		}
		final Shell dialog = new Shell(asp.getShell(), SWT.APPLICATION_MODAL
				| SWT.DIALOG_TRIM );
		this.elayer = elayer;
		dialog.setText("Edit Layer");
		canvasName(dialog);
		buildCanvasTopo(dialog);
		builtQuitButtons(dialog);
		dialog.pack();
		dialog.open();
		dialog.setSize(740, 390);
	}
	
	public void builtQuitButtons(final Shell  dialog) {

		Canvas quitTopo = new Canvas(dialog, SWT.BORDER);
		quitTopo.setBounds(10, 300, 720, 40);
		final Button buttonOK = new Button(quitTopo, SWT.PUSH);
		buttonOK.setText("OK");
		buttonOK.setBounds(20, 10, 80, 20);
		buttonOK.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				frame.getLayers().add(elayer);
				dialog.close();
			} 
		});

		Button buttonCancel = new Button(quitTopo, SWT.PUSH);
		buttonCancel.setText("Cancel");
		buttonCancel.setBounds(120, 10, 80, 20);
		buttonCancel.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				
				dialog.close();
			}
		});
	}

	public void buildCanvasTopo(Composite container) {
		// ****** CANVAS TYPE *********

		Canvas canvasTopo = new Canvas(container, SWT.BORDER);
		canvasTopo.setBounds(10, 50, 720, 240);

		// Shape
		final Composite shapeComp = new Composite(canvasTopo, SWT.BORDER);
		shapeComp.setBounds(10, 5, 700, 110);
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
					// modifyShape(comboShape.getText()+ "("+textPoints+")");
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
					// modifyShape(comboShape.getText()+ "(" +
					// textRadius.getText()+")");
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
					// modifyShape(comboShape.getText()+ "(" +
					// textSize.getText()+")");
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
					// modifyShape(comboShape.getText()+ "({" +
					// textWidth.getText() + ","+ textHeight.getText()+"})");
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
				}
				shapeComp.pack();
			}

		});

		// Species
		speciesComp = new Composite(shapeComp, SWT.NONE);
		speciesComp.setVisible(true);
		speciesComp.setEnabled(true);
		speciesComp.setBounds(20, 40, 600, 60);
		CLabel lblSpecies = new CLabel(speciesComp, SWT.NONE);
		lblSpecies.setBounds(0, 0, 60, 20);
		lblSpecies.setText("species");

		
		comboSpecies = new CCombo(speciesComp, SWT.BORDER);
		comboSpecies.setItems(species_list);
		comboSpecies.setBounds(70, 0, 300, 20);
		if (species_list.length > 0)
			comboSpecies.setText(species_list[0]);
		comboSpecies.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent event) {
				// modifyShape(comboShape.getText()+ "(" +
				// textSize.getText()+")");
			}
		});
		
		// Grid
		gridComp = new Composite(shapeComp, SWT.NONE);
		gridComp.setVisible(true);
		gridComp.setEnabled(true);
		gridComp.setBounds(20, 40, 600, 60);
		CLabel lblGrid = new CLabel(gridComp, SWT.NONE);
		lblGrid.setBounds(0, 0, 60, 20);
		lblGrid.setText("grid");

				
		comboGrid = new CCombo(gridComp, SWT.BORDER);
		comboGrid.setItems(grid_list);
		comboGrid.setBounds(70, 0, 300, 20);
				if (grid_list.length > 0)
					comboGrid.setText(grid_list[0]);
		comboGrid.addModifyListener(new ModifyListener() {
					public void modifyText(ModifyEvent event) {
						// modifyShape(comboShape.getText()+ "(" +
						// textSize.getText()+")");
					}
				});
				
		
		// Image
		imageComp = new Composite(shapeComp, SWT.NONE);
		imageComp.setBounds(20, 40, 600, 60);
		imageComp.setVisible(false);
		imageComp.setEnabled(false);
		CLabel lblPath = new CLabel(imageComp, SWT.NONE);
		lblPath.setBounds(0, 0, 60, 20);
		lblPath.setText("Path");

		textPath = new Text(imageComp, SWT.BORDER);
		textPath.setBounds(70, 0, 300, 20);
		textPath.setText("../images/background.png");
		textPath.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent event) {
				// modifyShape(comboShape.getText()+ "({" + textWidth.getText()
				// + ","+ textHeight.getText()+"})");
			}
		});
		
		// Text		
		textComp = new Composite(shapeComp, SWT.NONE);
		textComp.setBounds(20, 40, 600, 60);
		textComp.setVisible(false);
		textComp.setEnabled(false);
		CLabel lbltext = new CLabel(textComp, SWT.NONE);
		lbltext.setBounds(0, 0, 60, 20);
		lbltext.setText("Text");

		textText = new Text(textComp, SWT.BORDER);
		textText.setBounds(70, 0, 300, 20);
		textText.setText("");
		textText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent event) {
				// modifyShape(comboShape.getText()+ "({" + textWidth.getText()
				// + ","+ textHeight.getText()+"})");
			}
		});

		CLabel lblSizeTxt = new CLabel(textComp, SWT.NONE);
		lblSizeTxt.setBounds(0, 30, 60, 20);
		lblSizeTxt.setText("Size");

		textSizeText = new Text(textComp, SWT.BORDER);
		textSizeText.setBounds(70, 30, 300, 20);
		textSizeText.setText("1.0");
		textSizeText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent event) {
				// modifyShape(comboShape.getText()+ "({" + textWidth.getText()
						// + ","+ textHeight.getText()+"})");
			}
		});
		
		// Agents
		agentsComp = new Composite(shapeComp, SWT.NONE);
		agentsComp.setVisible(false);
		agentsComp.setEnabled(false);
		agentsComp.setBounds(20, 40, 600, 60);
		CLabel lblAgents = new CLabel(agentsComp, SWT.NONE);
		lblAgents.setBounds(0, 0, 60, 20);
		lblAgents.setText("agents");

		textAgents = new Text(agentsComp, SWT.BORDER);
		textAgents.setBounds(70, 0, 300, 20);
		textAgents.setText("[]");
		textAgents.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent event) {
				// modifyShape(comboShape.getText()+ "({" + textWidth.getText()
				// + ","+ textHeight.getText()+"})");
			}
		});	
		
				
	
	}
	protected Canvas canvasName(Composite container) {
		Canvas canvasName = new Canvas(container, SWT.BORDER);
		textName = new Text(canvasName, SWT.BORDER);
		UtilEditFrame.buildCanvasName(container, canvasName, textName, elayer, null);
		canvasName.setBounds(10, 10, 720, 30);
		return canvasName;
	}

}
