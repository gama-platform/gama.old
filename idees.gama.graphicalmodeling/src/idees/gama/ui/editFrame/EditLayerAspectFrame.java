package idees.gama.ui.editFrame;

import gama.ELayerAspect;

import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.transaction.util.TransactionUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.events.ShellListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.ColorDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class EditLayerAspectFrame {

	// Shapes
	private CCombo comboShape;
	private String[] type_shape = { "polyline", "polygon", "circle",
			"square", "rectangle", "hexagon", "sphere", "image", "text", "expression" };
	private Text textRadius;
	private Text textHeight;
	private Text textWidth;
	private Text textSize;
	private Text textPoints;
	private Text textShape;
	private Text textColor;
	private Text textEmpty;
	private Text textRotate;
	
	private Text textSizeText;
	private Text textSizeImage;
	private Text textPath;
	private Text textText;
	
	Button btnCstCol;
	
	Composite sizeComp;
	Composite radiusComp;
	Composite wHComp;
	Composite pointsComp;
	Composite expShapeComp;
	Composite textComp;
	Composite imageComp;
	Composite shapeComp;
	Text textName;
	ELayerAspect elayer;
	EditAspectFrame frame;
	EditLayerAspectFrame layerFrame;
	Color color;
	RGB rgb;
	Label colorLabel;
	boolean edit;

	private boolean quitWithoutSaving;
	
	public EditLayerAspectFrame(ELayerAspect elayer, EditAspectFrame asp, boolean edit) {
		init(elayer, asp);
		this.edit = edit;
		quitWithoutSaving = true;
		if (edit)
			loadData();
		setVisibility();
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
		if (elayer.getShape() != null)
			textShape.setText(elayer.getShape());
		if (elayer.getShape() != null)
			textColor.setText(elayer.getColor());
		if (elayer.getEmpty() != null)
			textEmpty.setText(elayer.getEmpty());
		if (elayer.getRotate() != null)
			textRotate.setText(elayer.getRotate());
		if (elayer.getTextSize() != null)
			textSizeText.setText(elayer.getTextSize());
		if (elayer.getImageSize() != null)
			textSizeImage.setText(elayer.getImageSize());
		if (elayer.getPath() != null)
			textPath.setText(elayer.getPath());
		if (elayer.getText()!= null)
			textText.setText(elayer.getText());
		if (elayer.getIsColorCst()!= null)
			btnCstCol.setSelection(elayer.getIsColorCst());
		if (elayer.getName()!= null)
			textName.setText(elayer.getName());
		if (! elayer.getColorRBG().isEmpty()) {
			rgb = new RGB(elayer.getColorRBG().get(0), elayer.getColorRBG().get(1), elayer.getColorRBG().get(2));
			 color.dispose();
	         color = new Color(frame.getShell().getDisplay(), rgb);
	         colorLabel.setBackground(color);
		}
			
		
	}

	public void init(final ELayerAspect elayer, EditAspectFrame asp) {
		frame = asp;
		layerFrame = this;
		
		final Shell dialog = new Shell(asp.getShell(), SWT.APPLICATION_MODAL
				| SWT.DIALOG_TRIM );
		this.elayer = elayer;
		dialog.setText("Edit Aspect Layer");
		dialog.addShellListener(new ShellListener() {

		      public void shellActivated(ShellEvent event) {
		      }

		      public void shellClosed(ShellEvent event) {
		       if (quitWithoutSaving) {
		    	  MessageBox messageBox = new MessageBox(dialog, SWT.ICON_WARNING | SWT.APPLICATION_MODAL | SWT.OK | SWT.CANCEL);
		        messageBox.setText("Warning");
		        messageBox.setMessage("You have unsaved data. Close the 'Edit Aspect Layer' window anyway?");
		        if (messageBox.open() == SWT.OK) {
		        	if (! layerFrame.edit)
						EcoreUtil.delete(elayer);
		        	event.doit = true;
		        }   else
		          event.doit = false;
		       } else {
		    	   event.doit = true;
		       }
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
				int index = 0;
				quitWithoutSaving = false;
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
				dialog.close();
			} 
		});

		Button buttonCancel = new Button(quitTopo, SWT.PUSH);
		buttonCancel.setText("Cancel");
		buttonCancel.setBounds(120, 10, 80, 20);
		buttonCancel.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {

				quitWithoutSaving = false;
				if (! layerFrame.edit)
					EcoreUtil.delete(elayer);
				dialog.close();
			}
		});
	}

	protected void saveLayer() {
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
		elayer.setText(textSizeText.getText());
		elayer.setPath(textPath.getText());
		elayer.setShapeType(comboShape.getText());
		elayer.setIsColorCst(btnCstCol.getSelection());
		elayer.setColor(textColor.getText());
		elayer.setTextSize(textSizeText.getText());
		elayer.setImageSize(textSizeImage.getText());
		elayer.getColorRBG().add(rgb.red > 0 ? rgb.red : 0);
		elayer.getColorRBG().add(rgb.green > 0 ? rgb.green : 0);
		elayer.getColorRBG().add(rgb.blue > 0 ? rgb.blue : 0);
		String code = "draw ";
		String val = comboShape.getText();
		if (val.equals("polyline") || val.equals("polygon")) {
			code += val + "("+textPoints.getText()+")";
		} else if (val.equals("circle") || val.equals("sphere")) {
			code += val + "("+textRadius.getText()+")";
		} else if (val.equals("square")) {
			code += val + "("+textSize.getText()+")";
		} else if (val.equals("rectangle") || val.equals("hexagon")) {
			code += val + "({"+textWidth.getText()+ ","+ textHeight.getText()+"})";
		} else if (val.equals("expression")) {
			code += textShape.getText();
		} else if (val.equals("image")) {
			code += "file(" + textPath.getText() + ")" + " size:" + textSizeImage.getText();
		} else if (val.equals("text")) {
			code +=  textText.getText() + " size:" + textSizeText.getText();
		}
		if (elayer.getIsColorCst()) {
			code += " color: rgb(" + elayer.getColorRBG() + ")" ;
		} else {
			code += " color: " + elayer.getColor();
		}
		if (elayer.getEmpty() != null && ! elayer.getEmpty().isEmpty() && ! elayer.getEmpty().equals("false"))
			code += " empty: " + elayer.getEmpty();
		if (elayer.getRotate() != null && ! elayer.getRotate().isEmpty() && ! elayer.getEmpty().equals(0.0))
			code += " rotate: " + elayer.getRotate();
		elayer.setGamlCode(code);
	}
	
	public void setVisibility(){
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

		Canvas canvasTopo = new Canvas(container, SWT.BORDER);
		canvasTopo.setBounds(10, 50, 720, 240);

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
				setVisibility();
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

		textSize = new Text(sizeComp, SWT.BORDER);
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

		textPath = new Text(imageComp, SWT.BORDER);
		textPath.setBounds(70, 30, 300, 20);
		textPath.setText("../images/icon.png");
		
		CLabel lblSizeIm = new CLabel(imageComp, SWT.NONE);
		lblSizeIm.setBounds(0, 0, 60, 20);
		lblSizeIm.setText("Size");

		textSizeImage = new Text(imageComp, SWT.BORDER);
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

		textText = new Text(textComp, SWT.BORDER);
		textText.setBounds(70, 30, 300, 20);
		textText.setText("");
	
		CLabel lblSizeTxt = new CLabel(textComp, SWT.NONE);
		lblSizeTxt.setBounds(0, 0, 60, 20);
		lblSizeTxt.setText("Size");

		textSizeText = new Text(textComp, SWT.BORDER);
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

		textRadius = new Text(radiusComp, SWT.BORDER);
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

		textHeight = new Text(wHComp, SWT.BORDER);
		textHeight.setBounds(70, 30, 300, 20);
		textHeight.setText("1.0");
		
		CLabel lblWidth = new CLabel(wHComp, SWT.NONE);
		lblWidth.setBounds(0, 0, 60, 20);
		lblWidth.setText("Width");

		textWidth = new Text(wHComp, SWT.BORDER);
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

		textPoints = new Text(pointsComp, SWT.BORDER);
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

		textShape = new Text(expShapeComp, SWT.BORDER);
		textShape.setBounds(70, 0, 300, 20);
		
		
		// COLOR
		CLabel lblColor = new CLabel(canvasTopo, SWT.NONE);
		lblColor.setBounds(10, 130, 60, 20);
		lblColor.setText("Color");
					 
		textColor = new Text(canvasTopo, SWT.BORDER);
		textColor.setBounds(425, 130, 250, 18);
		rgb = new RGB(0, 0, 255);	
		color = new Color(frame.getShell().getDisplay(), rgb);
		
	  // Use a label full of spaces to show the color
	    colorLabel = new Label(canvasTopo, SWT.NONE);
	    colorLabel.setText("    ");
	    colorLabel.setBackground(color);
	    colorLabel.setBounds(160, 130, 50, 18);

	    Button buttonColor = new Button(canvasTopo, SWT.PUSH);
	    buttonColor.setText("Color...");
	    buttonColor.setBounds(220, 130, 80, 20);
	    buttonColor.addSelectionListener(new SelectionAdapter() {
	      public void widgetSelected(SelectionEvent event) {
	        // Create the color-change dialog
	        ColorDialog dlg = new ColorDialog(frame.getShell());

	        // Set the selected color in the dialog from
	        // user's selected color
	        dlg.setRGB(colorLabel.getBackground().getRGB());

	        // Change the title bar text
	        dlg.setText("Choose a Color");

	        // Open the dialog and retrieve the selected color
	        RGB rgbN = dlg.open();
	        if (rgbN != null) {
	        	rgb = rgbN;
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
			}
		});
		
					
		Button btnExpressionCol = new Button(cColor, SWT.RADIO);
		btnExpressionCol.setBounds(260,2, 85, 18);
		btnExpressionCol.setText("Expression:");
		btnExpressionCol.addSelectionListener(new SelectionAdapter() {
						@Override
			public void widgetSelected(SelectionEvent e) {
					textColor.setEnabled(true);	
			}
		});
		
		// EMPTY
		CLabel lblEmpty = new CLabel(canvasTopo, SWT.NONE);
		lblEmpty.setBounds(10, 170, 60, 20);
		lblEmpty.setText("Empty");
							 
		textEmpty = new Text(canvasTopo, SWT.BORDER);
		textEmpty.setText("false");
		textEmpty.setBounds(80, 170, 250, 18);
			
		// Rotate
		CLabel lblRotate = new CLabel(canvasTopo, SWT.NONE);
		lblRotate.setBounds(10, 210, 60, 20);
		lblRotate.setText("Rotate");
									 
		textRotate = new Text(canvasTopo, SWT.BORDER);
		textRotate.setText("0.0");
		textRotate.setBounds(80, 210, 250, 18);	
	}
	protected Canvas canvasName(Composite container) {
		Canvas canvasName = new Canvas(container, SWT.BORDER);
		textName = new Text(canvasName, SWT.BORDER);
		UtilEditFrame.buildCanvasName(container, canvasName, textName, elayer, null);
		canvasName.setBounds(10, 10, 720, 30);
		return canvasName;
	}

}
