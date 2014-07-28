package idees.gama.ui.editFrame;


import java.util.List;
import java.util.Map;

import idees.gama.diagram.GamaDiagramEditor;
import idees.gama.features.modelgeneration.ModelGenerator;

import msi.gama.util.GamaList;
import msi.gama.util.GamaMap;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolTip;

public class ValidateText extends Text{

	Color colValid;
	Color colNotValid;
	boolean isValid;
	String error;
	final Diagram diagram;
	final IFeatureProvider fp;
	final EditFrame frame;
	final String nameLoc;
	final List<String> loc;
	final GamaDiagramEditor editor;
	final ToolTip tip;
	List<String> uselessName;
	
	final static int TOOLTIP_HIDE_DELAY = 200;   // 0.2s
    final static int TOOLTIP_SHOW_DELAY = 500;  // 0.5s
    final List<ValidateStyledText> linkedVsts = new GamaList<ValidateStyledText>();
	final List<ValidateText> linkedVts = new GamaList<ValidateText>();
	boolean allErrors;
	
	boolean isString = false;
	
	boolean saveData = false;
	
	 String addToLoc;
	
	public ValidateText(Composite parent, int style,final Diagram diagram,final IFeatureProvider fp,final EditFrame frame, final GamaDiagramEditor editor, final String name,final List<String> uselessName, final String addToLoc) {
		super(parent, style);
		this.diagram = diagram;
		
		tip = new ToolTip(getShell(), SWT.BALLOON);
        tip.setText("ERROR");
        tip.setAutoHide(false);
        this.uselessName = uselessName;
        allErrors = false;
		
		this.fp = fp;
		this.nameLoc = name;
		
		System.out.println("ValidateText");
		this.addToLoc = addToLoc;
		loc = new GamaList<String>();
		editor.buildLocation(frame.eobject, loc);
		if(addToLoc != null && !addToLoc.isEmpty()) loc.add(addToLoc);
		//loc.add(0, "world");
		
		colValid = new Color(getDisplay(), 100, 255, 100);
		colNotValid = new Color(getDisplay(), 255, 100, 100);
		error = editor.containErrors(loc, name, uselessName);
		tip.setMessage(error);
        
		isValid = error.equals(""); 
		this.setBackground(isValid ? colValid: colNotValid);
		this.frame = frame;
		this.editor = editor;
		
		
		
		addModifyListener(new ModifyListener() {
			
			@Override
		      public void modifyText(ModifyEvent event) {
				applyModification();
		      }
		    });
		
		addListener(SWT.MouseHover, new Listener() {
            public void handleEvent(Event event) {
                tip.getDisplay().timerExec(TOOLTIP_SHOW_DELAY, new Runnable() {
                    public void run() {
                    	if (!isValid)
                    		tip.setVisible(true);
                    	else tip.setVisible(false);
                    }
                });             
            }
        });

        addListener(SWT.MouseExit, new Listener() {
            public void handleEvent(Event event) {
                tip.getDisplay().timerExec(TOOLTIP_HIDE_DELAY, new Runnable() {
                    public void run() {
                        tip.setVisible(false);
                    }
                });
            }
        });
	}
	
	
	public void applyModification() {
		System.out.println("nameLoc: " + nameLoc + " saveData:" + saveData);
		
		if (saveData)frame.save(nameLoc);
		System.out.println("isString");
		frame.getShell().forceFocus();
		if (nameLoc.equals("name")) 
			isValid = !getText().isEmpty() && !getText().contains(" ") && !getText().contains(";") && !getText().contains("{") && !getText().contains("}") && !getText().contains("\t");
		else 
			isValid = !ModelGenerator.hasSyntaxError(getText(),  true, isString);
		if (isValid) {
			  ModelGenerator.modelValidation(fp, diagram);
			  Map<String,String> locs = editor.getSyntaxErrorsLoc().get(loc) ;
			  if (locs != null)
				  locs.remove(nameLoc);
			  if (nameLoc.equals("name")) {
				  addToLoc = getText();
				  List<String> oldLoc = new GamaList<String>();
				  oldLoc.addAll(loc);
				  System.out.println("oldLoc: " + oldLoc);
				  loc.clear();
				  //editor.buildLocation(frame.eobject, loc);
				  editor.buildLocation(frame.eobject, loc);
					if(addToLoc != null && !addToLoc.isEmpty() && !loc.get(loc.size()-1).equals(addToLoc)) loc.add(addToLoc);
						

				  System.out.println("newLoc: " + loc);
		        	for (ValidateStyledText vst :linkedVsts) {
		        		if (vst != null)vst.updateLoc(loc);
		        	}
		        	for (ValidateText vst :linkedVts) {
		        		if (vst != null)vst.updateLoc(loc);
		        	}
		        	editor.updateErrors(oldLoc,loc);
		    		
		        }
			  if (allErrors)
				  error = editor.containErrors(loc, "", uselessName);
			  else 
				  error = editor.containErrors(loc, nameLoc, uselessName);
		        
		 } else {
			 error = "Syntax errors detected";
			 Map<String,String> locs = editor.getSyntaxErrorsLoc().get(loc) ;
				
				if (locs == null) {
					locs = new GamaMap<String,String>();
				}
				locs.put(nameLoc,"Syntax errors detected");
			 editor.getSyntaxErrorsLoc().put(loc, locs);
		 } 
       System.out.println("location:" + loc);
        System.out.println("name:" + nameLoc);
    	System.out.println("isValid: " + isValid);
       if (error != null) {	
        	tip.setMessage(error);
        	isValid = error.equals(""); 
        }
        setBackground(isValid ? colValid: colNotValid);
        if (isValid) {
        	tip.setVisible(false);
        }
        setFocus();
        editor.updateEObjectErrors();
	}
	@Override
	protected void checkSubclass() {
		return;
	}
	public String getNameLoc() {
		return nameLoc;
	}
	public List<String> getLoc() {
		return loc;
	}

	public void updateLoc(List<String> nwLoc){
		loc.clear();
		loc.addAll(nwLoc);
	}
	public List<ValidateStyledText> getLinkedVsts() {
		return linkedVsts;
	}
	public List<ValidateText> getLinkedVts() {
		return linkedVts;
	}


	public boolean isAllErrors() {
		return allErrors;
	}


	public void setAllErrors(boolean allErrors) {
		this.allErrors = allErrors;
	}


	public boolean isSaveData() {
		return saveData;
	}


	public void setSaveData(boolean saveData) {
		this.saveData = saveData;
	}


	public boolean isString() {
		return isString;
	}


	public void setString(boolean isString) {
		this.isString = isString;
	}
	
 
}
