package idees.gama.ui.editFrame;

import gama.EGamaObject;
import gama.ELayerAspect;
import idees.gama.diagram.GamaDiagramEditor;
import idees.gama.features.edit.EditFeature;
import idees.gama.features.modelgeneration.ModelGenerator;

import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.transaction.util.TransactionUtil;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.ColorDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

public class EditLayerAspectFrame extends EditFrame {

	// Shapes
	private CCombo comboShape;
	private String[] type_shape = { "polyline", "polygon", "circle",
			"square", "rectangle", "hexagon", "cube", "sphere", "pyramid", "image", "text", "expression" };
	private ValidateText textRadius;
	private ValidateText textHeight;
	private ValidateText textWidth;
	private ValidateText textSize;
	private ValidateText textPoints;
	private ValidateText textShape;
	private ValidateText textColor;
	private ValidateText textEmpty;
	private ValidateText textRotate;
	private ValidateText textSizeText;
	private ValidateText textSizeImage;
	private ValidateText textPath;
	private ValidateText textText;
	
	private ValidateText textLoc;
	private ValidateText textTexture;
	private ValidateText textDepth;
	Button btnCstCol;
	Button btnExpressionCol;
	
	Composite sizeComp;
	Composite radiusComp;
	Composite wHComp;
	Composite pointsComp;
	Composite expShapeComp;
	Composite textComp;
	Composite imageComp;
	Composite shapeComp;
	ELayerAspect elayer;
	EditAspectFrame frame;
	EditLayerAspectFrame layerFrame;
	Color color;
	RGB rgb;
	Label colorLabel;
	boolean edit;
	
	public EditLayerAspectFrame(Diagram diagram, IFeatureProvider fp, EditFeature ef,EGamaObject eobject, String name) {
		super(diagram,fp,ef,eobject,name);
	}
	
	public EditLayerAspectFrame(ELayerAspect elayer, EditAspectFrame asp, boolean edit, Diagram diagram,IFeatureProvider fp, EditFeature ef) {
		super(diagram,fp,ef,elayer,"Edit Aspect Layer");
		frame = asp;
		layerFrame = this;
		
		this.elayer = elayer;
		this.edit = edit;
		
	}
	
	@Override
	protected Control createContents(Composite parent) {
		
		Composite comp = new Composite(parent, SWT.NONE);
		comp.setBounds(0, 0, 740, 390);
		
		canvasName(comp, false);
		buildCanvasTopo(comp);

		comp.pack();
		if (edit) {
			loadData();
		} 
		updateVisibility();
		((ValidateText)textRadius).setSaveData(true);
		((ValidateText)textHeight).setSaveData(true);
		((ValidateText)textWidth).setSaveData(true);
		((ValidateText)textSize).setSaveData(true);
		((ValidateText)textPoints).setSaveData(true);
		((ValidateText)textShape).setSaveData(true);
		((ValidateText)textColor).setSaveData(true);
		((ValidateText)textEmpty).setSaveData(true);
		((ValidateText)textRotate).setSaveData(true);
		((ValidateText)textLoc).setSaveData(true);
		((ValidateText)textDepth).setSaveData(true);
		((ValidateText)textTexture).setSaveData(true);
		
		((ValidateText)textSizeText).setSaveData(true);
		((ValidateText)textSizeImage).setSaveData(true);
		((ValidateText)textPath).setSaveData(true);
		((ValidateText)textText).setSaveData(true);
			
		if (!edit) save("");
		
		return comp;
	}
	
	private void loadData() {
		if (elayer.getShapeType() != null && ! elayer.getShapeType().isEmpty())
			comboShape.setText(elayer.getShapeType());
		if (elayer.getRadius() != null)
			textRadius.setText(elayer.getRadius());
		if (elayer.getHeigth() != null)
			textHeight.setText(elayer.getHeigth());
		if (elayer.getWidth() != null)
			textWidth.setText(elayer.getWidth());
		if (elayer.getSize() != null)
			textSize.setText(elayer.getSize());
		if (elayer.getPoints() != null)
			textPoints.setText(elayer.getPoints());
		if (elayer.getColor() != null)
			textColor.setText(elayer.getColor());
		if (elayer.getEmpty() != null)
			textEmpty.setText(elayer.getEmpty());
		if (elayer.getRotate() != null)
			textRotate.setText(elayer.getRotate());
		if (elayer.getTexture() != null)
			textTexture.setText(elayer.getTexture());
		if (elayer.getDepth() != null)
			textDepth.setText(elayer.getDepth());
		if (elayer.getAt() != null)
			textLoc.setText(elayer.getAt());
		if (elayer.getExpression() != null)
			textShape.setText(elayer.getExpression());
		if (elayer.getTextSize() != null)
			textSizeText.setText(elayer.getTextSize());
		if (elayer.getImageSize() != null)
			textSizeImage.setText(elayer.getImageSize());
		if (elayer.getPath() != null)
			textPath.setText(elayer.getPath());
		if (elayer.getText()!= null)
			textText.setText(elayer.getText());
		if (elayer.getIsColorCst()!= null) {
			btnCstCol.setSelection(elayer.getIsColorCst());
			btnExpressionCol.setSelection(! elayer.getIsColorCst());
		}
			if (! elayer.getColorRBG().isEmpty()) {
			rgb = new RGB(elayer.getColorRBG().get(0), elayer.getColorRBG().get(1), elayer.getColorRBG().get(2));
			 if (color != null) color.dispose();
	         color = new Color(frame.getShell().getDisplay(), rgb);
	         colorLabel.setBackground(color);
		}
			if (elayer.getName()!= null)
				textName.setText(elayer.getName());
		
		
	}

	
	

	protected void save(String name) {
		TransactionalEditingDomain domain = TransactionUtil.getEditingDomain(frame.eobject);
			if (domain != null) {
			    domain.getCommandStack().execute(new RecordingCommand(domain) {
			    	     public void doExecute() {
			    	    	 modifyLayer();
			    	    	
			    	     }
			    	  });
			} 
	}
	
	private void modifyLayer() {
		elayer.setName(textName.getText());
		elayer.setExpression(textShape.getText());
		elayer.setPoints(textPoints.getText());
		elayer.setEmpty(textEmpty.getText());
		elayer.setRadius(textRadius.getText());
		elayer.setHeigth(textHeight.getText());
		elayer.setWidth(textWidth.getText());
		elayer.setSize(textSize.getText());
		elayer.setRotate(textRotate.getText());
		elayer.setText(textText.getText());
		elayer.setPath(textPath.getText());
		elayer.setShapeType(comboShape.getText());
		elayer.setIsColorCst(btnCstCol.getSelection());
		elayer.setColor(textColor.getText());
		elayer.setTextSize(textSizeText.getText());
		elayer.setImageSize(textSizeImage.getText());
		elayer.setTexture(textTexture.getText());
		elayer.setAt(textLoc.getText());
		elayer.setDepth(textDepth.getText());
		elayer.getColorRBG().clear();
		elayer.getColorRBG().add(rgb.red > 0 ? rgb.red : 0);
		elayer.getColorRBG().add(rgb.green > 0 ? rgb.green : 0);
		elayer.getColorRBG().add(rgb.blue > 0 ? rgb.blue : 0);
	}
	
	public void updateVisibility(){
		String val = comboShape.getText();
		if (val.equals("polyline") || val.equals("polygon")) {
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
			imageComp.setVisible(false);
			imageComp.setEnabled(false);
			textComp.setEnabled(false);
			textComp.setVisible(false);
			
			// modifyShape(comboShape.getText()+ "("+textPoints+")");
		} else if (val.equals("circle") || val.equals("sphere")) {
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
			imageComp.setVisible(false);
			imageComp.setEnabled(false);
			textComp.setEnabled(false);
			textComp.setVisible(false);
		} else if (val.equals("square") || val.equals("pyramid") || val.equals("cube")) {
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
			imageComp.setVisible(false);
			imageComp.setEnabled(false);
			textComp.setEnabled(false);
			textComp.setVisible(false);
		} else if (val.equals("rectangle") || val.equals("hexagon")) {
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
			imageComp.setVisible(false);
			imageComp.setEnabled(false);
			textComp.setEnabled(false);
			textComp.setVisible(false);
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
			imageComp.setVisible(false);
			imageComp.setEnabled(false);
			textComp.setEnabled(false);
			textComp.setVisible(false);
		} else if (val.equals("image")) {
			sizeComp.setVisible(false);
			sizeComp.setEnabled(false);
			radiusComp.setVisible(false);
			radiusComp.setEnabled(false);
			pointsComp.setVisible(false);
			pointsComp.setEnabled(false);
			wHComp.setVisible(false);
			wHComp.setEnabled(false);
			expShapeComp.setVisible(false);
			expShapeComp.setEnabled(false);
			imageComp.setVisible(true);
			imageComp.setEnabled(true);
			textComp.setEnabled(false);
			textComp.setVisible(false);
		} else if (val.equals("text")) {
			sizeComp.setVisible(false);
			sizeComp.setEnabled(false);
			radiusComp.setVisible(false);
			radiusComp.setEnabled(false);
			pointsComp.setVisible(false);
			pointsComp.setEnabled(false);
			wHComp.setVisible(false);
			wHComp.setEnabled(false);
			expShapeComp.setVisible(false);
			expShapeComp.setEnabled(false);
			imageComp.setVisible(false);
			imageComp.setEnabled(false);
			textComp.setEnabled(true);
			textComp.setVisible(true);
		}
		shapeComp.pack();
	}
	

	public void buildCanvasTopo(Composite container) {
		// ****** CANVAS TOPOLOGY *********
		final GamaDiagramEditor diagramEditor = ((GamaDiagramEditor)fp.getDiagramTypeProvider().getDiagramEditor());
		   
		Canvas canvasTopo = new Canvas(container, SWT.BORDER);
		canvasTopo.setBounds(10, 50, 720, 370);

		// Shape
		shapeComp = new Composite(canvasTopo, SWT.BORDER);
		shapeComp.setBounds(10, 5, 700, 110);
		CLabel lblShape = new CLabel(shapeComp, SWT.NONE);
		lblShape.setBounds(5, 5, 50, 20);
		lblShape.setText("Shape");

		comboShape = new CCombo(shapeComp, SWT.BORDER);
		comboShape.setBounds(60, 5, 300, 20);
		comboShape.setItems(type_shape);
		comboShape.setText("circle");
		// "point", "polyline", "polygon", "circle", "square", "rectangle",
		// "hexagon", "sphere", "expression"
		comboShape.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				updateVisibility();
			}

		});

		// Square
		sizeComp = new Composite(shapeComp, SWT.NONE);
		sizeComp.setVisible(false);
		sizeComp.setEnabled(false);
		sizeComp.setBounds(20, 40, 600, 60);
		CLabel lblSize = new CLabel(sizeComp, SWT.NONE);
		lblSize.setBounds(0, 0, 60, 20);
		lblSize.setText("Size");

		textSize = new ValidateText(sizeComp, SWT.BORDER,diagram, fp,this, diagramEditor, "", null, null);
		textSize.setBounds(70, 0, 300, 20);
		textSize.setText("1.0");
		
		// Image
		imageComp = new Composite(shapeComp, SWT.NONE);
		imageComp.setBounds(20, 40, 600, 60);
		imageComp.setVisible(false);
		imageComp.setEnabled(false);
		CLabel lblPath = new CLabel(imageComp, SWT.NONE);
		lblPath.setBounds(0, 30, 60, 20);
		lblPath.setText("Path");

		textPath = new ValidateText(imageComp, SWT.BORDER,diagram, fp,this, diagramEditor, "file:", null, null);
		textPath.setBounds(70, 30, 300, 20);
		textPath.setString(true);
		textPath.setText("../images/icon.png");
		
		CLabel lblSizeIm = new CLabel(imageComp, SWT.NONE);
		lblSizeIm.setBounds(0, 0, 60, 20);
		lblSizeIm.setText("Size");

		textSizeImage = new ValidateText(imageComp, SWT.BORDER,diagram, fp,this, diagramEditor, "size:", null, null);
		textSizeImage.setBounds(70, 0, 300, 20);
		textSizeImage.setText("1.0");
		
		// Text		
		textComp = new Composite(shapeComp, SWT.NONE);
		textComp.setBounds(20, 40, 600, 60);
		textComp.setVisible(false);
		textComp.setEnabled(false);
		CLabel lbltext = new CLabel(textComp, SWT.NONE);
		lbltext.setBounds(0, 30, 60, 20);
		lbltext.setText("Text");

		textText = new ValidateText(textComp, SWT.BORDER,diagram, fp,this, diagramEditor, "text:", null, null);
		textText.setBounds(70, 30, 300, 20);
		textText.setString(true);
		textText.setText("");
	
		CLabel lblSizeTxt = new CLabel(textComp, SWT.NONE);
		lblSizeTxt.setBounds(0, 0, 60, 20);
		lblSizeTxt.setText("Size");

		textSizeText = new ValidateText(textComp, SWT.BORDER,diagram, fp,this, diagramEditor, "size:", null, null);
		textSizeText.setBounds(70, 0, 300, 20);
		textSizeText.setText("1.0");
		
		// Circle - Sphere
		radiusComp = new Composite(shapeComp, SWT.NONE);
		radiusComp.setVisible(false);
		radiusComp.setEnabled(false);
		radiusComp.setBounds(20, 40, 600, 60);
		
		CLabel lblRadius = new CLabel(radiusComp, SWT.NONE);
		lblRadius.setBounds(0, 0, 60, 20);
		lblRadius.setText("Radius");

		textRadius = new ValidateText(radiusComp, SWT.BORDER,diagram, fp,this, diagramEditor, "", null, null);
		textRadius.setBounds(70, 0, 300, 20);
		textRadius.setText("1.0");
		
		// Hexagon - Rectangle
		wHComp = new Composite(shapeComp, SWT.NONE);
		wHComp.setBounds(20, 40, 600, 60);
		wHComp.setVisible(false);
		wHComp.setEnabled(false);
		
		CLabel lblHeight = new CLabel(wHComp, SWT.NONE);
		lblHeight.setBounds(0, 30, 60, 20);
		lblHeight.setText("Height");

		textHeight = new ValidateText(wHComp, SWT.BORDER,diagram, fp,this, diagramEditor, "", null, null);
		textHeight.setBounds(70, 30, 300, 20);
		textHeight.setText("1.0");
		
		CLabel lblWidth = new CLabel(wHComp, SWT.NONE);
		lblWidth.setBounds(0, 0, 60, 20);
		lblWidth.setText("Width");

		textWidth = new ValidateText(wHComp, SWT.BORDER,diagram, fp,this, diagramEditor, "", null, null);
		textWidth.setBounds(70, 0, 300, 20);
		textWidth.setText("1.0");
		

		// Polygon, Polyline
		pointsComp = new Composite(shapeComp, SWT.NONE);
		pointsComp.setVisible(false);
		pointsComp.setEnabled(false);
		pointsComp.setBounds(20, 40, 600, 60);
		CLabel lblPoints = new CLabel(pointsComp, SWT.NONE);
		lblPoints.setBounds(0, 0, 60, 20);
		lblPoints.setText("Points");

		textPoints = new ValidateText(pointsComp, SWT.BORDER,diagram, fp,this, diagramEditor, "size:", null, null);
		textPoints.setBounds(70, 0, 300, 20);
		textPoints.setText("[{0.0,0.0},{0.0,1.0},{1.0,1.0},{1.0,0.0}]");
		
		// Expression shape
		expShapeComp = new Composite(shapeComp, SWT.NONE);
		expShapeComp.setVisible(false);
		expShapeComp.setEnabled(false);
		expShapeComp.setBounds(20, 50, 600, 60);
		CLabel lblExpShape = new CLabel(expShapeComp, SWT.NONE);
		lblExpShape.setBounds(0, 0, 70, 20);
		lblExpShape.setText("Expression");

		textShape = new ValidateText(expShapeComp, SWT.BORDER,diagram, fp,this, diagramEditor, "", null, null);
		textShape.setBounds(70, 0, 300, 20);
		
		
		// COLOR
		CLabel lblColor = new CLabel(canvasTopo, SWT.NONE);
		lblColor.setBounds(10, 130, 60, 20);
		lblColor.setText("Color");
		
		textColor = new ValidateText(canvasTopo, SWT.BORDER,diagram, fp,this, diagramEditor, "color:", null, null);
		textColor.setBounds(425, 130, 250, 18);
			
	  // Use a label full of spaces to show the color
	    colorLabel = new Label(canvasTopo, SWT.NONE);
	    colorLabel.setText("    ");
	    if (elayer.getColorRBG().isEmpty()) {
	    	rgb = new RGB(0, 0, 255);	
	    	color = new Color(frame.getShell().getDisplay(), rgb);
	    	
		    colorLabel.setBackground(color);
		  
	    }
		  colorLabel.setBounds(160, 130, 50, 18);

	    Button buttonColor = new Button(canvasTopo, SWT.PUSH);
	    buttonColor.setText("Color...");
	    buttonColor.setBounds(220, 130, 80, 20);
	    buttonColor.addSelectionListener(new SelectionAdapter() {
	      public void widgetSelected(SelectionEvent event) {
	        // Create the color-change dialog
	        ColorDialog dlg = new ColorDialog(layerFrame.getShell());

	        // Set the selected color in the dialog from
	        // user's selected color
	        dlg.setRGB(colorLabel.getBackground().getRGB());

	        // Change the title bar text
	        dlg.setText("Choose a Color");

	        // Open the dialog and retrieve the selected color
	        RGB rgbN = dlg.open();
	        if (rgbN != null) {
	        	rgb = rgbN;
	        	save("");
				 ModelGenerator.modelValidation(fp, diagram);
				 diagramEditor.updateEObjectErrors();
			
	          // Dispose the old color, create the
	          // new one, and set into the label
	          color.dispose();
	          color = new Color(frame.getShell().getDisplay(), rgb);
	          colorLabel.setBackground(color);
	        }
	      }
	    });
	    
		Composite cColor = new Composite (canvasTopo, SWT.NONE);
		cColor.setBounds(80, 128, 400, 22);
					
		btnCstCol = new Button(cColor, SWT.RADIO);
		btnCstCol.setBounds(0, 2, 80, 18);
		btnCstCol.setText("Constant");
		btnCstCol.setSelection(true);
		btnCstCol.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				textColor.setEnabled(false);	
				if (((ValidateText)textColor).isSaveData()) {
					save("");
					 ModelGenerator.modelValidation(fp, diagram);
					 diagramEditor.updateEObjectErrors();
				}
			}
		});
		
					
		btnExpressionCol = new Button(cColor, SWT.RADIO);
		btnExpressionCol.setBounds(260,2, 85, 18);
		btnExpressionCol.setText("Expression:");
		btnExpressionCol.addSelectionListener(new SelectionAdapter() {
						@Override
			public void widgetSelected(SelectionEvent e) {
					textColor.setEnabled(true);	
					if (((ValidateText)textColor).isSaveData()) {
						save("");
						 ModelGenerator.modelValidation(fp, diagram);
						 diagramEditor.updateEObjectErrors();
					}
			}
		});
		
		// EMPTY
		CLabel lblEmpty = new CLabel(canvasTopo, SWT.NONE);
		lblEmpty.setBounds(10, 170, 60, 20);
		lblEmpty.setText("Empty");
					
		textEmpty = new ValidateText(canvasTopo, SWT.BORDER,diagram, fp,this, diagramEditor, "empty:", null, null);
		textEmpty.setText("false");
		textEmpty.setBounds(80, 170, 250, 18);
			
		// Rotate
		CLabel lblRotate = new CLabel(canvasTopo, SWT.NONE);
		lblRotate.setBounds(10, 210, 60, 20);
		lblRotate.setText("Rotate");
									 
		textRotate = new ValidateText(canvasTopo, SWT.BORDER,diagram, fp,this, diagramEditor, "rotate:", null, null);
		textRotate.setText("0.0");
		textRotate.setBounds(80, 210, 250, 18);	
		
		// At
		CLabel lblLoc = new CLabel(canvasTopo, SWT.NONE);
		lblLoc.setBounds(10, 250, 60, 20);
		lblLoc.setText("Location");
											 
		textLoc = new ValidateText(canvasTopo, SWT.BORDER,diagram, fp,this, diagramEditor, "at:", null, null);
		textLoc.setText("");
		textLoc.setBounds(80, 250, 250, 18);	
				
		// Depth
		CLabel lblDepth = new CLabel(canvasTopo, SWT.NONE);
		lblDepth.setBounds(10, 290, 60, 20);
		lblDepth.setText("Depth");
											 
		textDepth = new ValidateText(canvasTopo, SWT.BORDER,diagram, fp,this, diagramEditor, "depth:", null, null);
		textDepth.setText("");
		textDepth.setBounds(80, 290, 250, 18);	
				
		// Texture
		CLabel lblTexture = new CLabel(canvasTopo, SWT.NONE);
		lblTexture.setBounds(10, 330, 60, 20);
		lblTexture.setText("Texture");
											 
		textTexture = new ValidateText(canvasTopo, SWT.BORDER,diagram, fp,this, diagramEditor, "texture:", null, null);
		textTexture.setText("");
		textTexture.setBounds(80, 330, 250, 18);	
	}
	
	 @Override
		protected Point getInitialSize() {
			return new Point(753, 490);
		}
		
		@Override
		public void updateError() {
			frame.updateLayer();
		}
}
