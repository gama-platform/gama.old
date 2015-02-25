package idees.gama.ui.editFrame;

import idees.gama.diagram.GamaDiagramEditor;
import idees.gama.features.modelgeneration.ModelGenerator;
import java.util.*;
import java.util.List;
import msi.gama.util.TOrderedHashMap;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.*;

public class ValidateText extends StyledText {

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

	final static int TOOLTIP_HIDE_DELAY = 200; // 0.2s
	final static int TOOLTIP_SHOW_DELAY = 500; // 0.5s
	final List<ValidateStyledText> linkedVsts = new ArrayList<ValidateStyledText>();
	final List<ValidateText> linkedVts = new ArrayList<ValidateText>();
	boolean allErrors;

	boolean isString = false;

	boolean saveData = false;

	boolean nameFeature = true;

	String addToLoc;

	public ValidateText(final Composite parent, final int style, final Diagram diagram, final IFeatureProvider fp,
		final EditFrame frame, final GamaDiagramEditor editor, final String name, final List<String> uselessName,
		final String addToLoc) {
		super(parent, style);
		this.diagram = diagram;
		new UndoRedoStyledText(this);

		tip = new ToolTip(getShell(), SWT.BALLOON);
		tip.setText("ERROR");
		tip.setAutoHide(false);
		this.uselessName = uselessName;
		allErrors = false;

		this.fp = fp;
		this.nameLoc = name;

		// System.out.println("ValidateText");
		this.addToLoc = addToLoc;
		loc = new ArrayList<String>();
		editor.buildLocation(frame.eobject, loc);
		// System.out.println("loc: " + loc);
		if ( addToLoc != null && !addToLoc.isEmpty() ) {
			loc.add(addToLoc);
			// loc.add(0, "world");
		}

		colValid = new Color(getDisplay(), 100, 255, 100);
		colNotValid = new Color(getDisplay(), 255, 100, 100);
		error = editor.containErrors(loc, name, uselessName);
		tip.setMessage(error);

		isValid = error.equals("");
		this.setBackground(isValid ? colValid : colNotValid);
		this.frame = frame;
		this.editor = editor;

		addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(final ModifyEvent event) {
				applyModification();
			}
		});

		addListener(SWT.MouseHover, new Listener() {

			@Override
			public void handleEvent(final Event event) {
				tip.getDisplay().timerExec(TOOLTIP_SHOW_DELAY, new Runnable() {

					@Override
					public void run() {
						if ( !isValid ) {
							tip.setVisible(true);
						} else {
							tip.setVisible(false);
						}
					}
				});
			}
		});

		addListener(SWT.MouseExit, new Listener() {

			@Override
			public void handleEvent(final Event event) {
				tip.getDisplay().timerExec(TOOLTIP_HIDE_DELAY, new Runnable() {

					@Override
					public void run() {
						tip.setVisible(false);
					}
				});
			}
		});
	}

	public void updateColor() {
		if ( nameLoc.equals("name") ) {
			isValid =
				!getText().isEmpty() && !getText().contains(" ") && !getText().contains(";") &&
					!getText().contains("{") && !getText().contains("}") && !getText().contains("\t");
		} else {
			isValid = !ModelGenerator.hasSyntaxError(fp, getText(), true, isString);
		}
		if ( isValid ) {
			Map<String, String> locs = editor.getSyntaxErrorsLoc().get(loc);
			if ( locs != null ) {
				locs.remove(nameLoc);
			}
			if ( nameFeature && nameLoc.equals("name") ) {
				addToLoc = getText();
				List<String> oldLoc = new ArrayList<String>();
				oldLoc.addAll(loc);
				loc.clear();
				editor.buildLocation(frame.eobject, loc);
				if ( addToLoc != null && !addToLoc.isEmpty() && !loc.get(loc.size() - 1).equals(addToLoc) ) {
					loc.add(addToLoc);
				}

				for ( ValidateStyledText vst : linkedVsts ) {
					if ( vst != null ) {
						vst.updateLoc(loc);
					}
				}
				for ( ValidateText vst : linkedVts ) {
					if ( vst != null ) {
						vst.updateLoc(loc);
					}
				}
				editor.updateErrors(oldLoc, loc);
				editor.getIdsEObjects().clear();
				editor.initIdsEObjects();

			}
			if ( allErrors ) {
				error = editor.containErrors(loc, "", uselessName);
			} else {
				error = editor.containErrors(loc, nameLoc, uselessName);
			}

		} else {
			error = "Syntax errors detected ";
			List<String> wStr = new ArrayList<String>();
			wStr.add("world");
			editor.getSyntaxErrorsLoc().remove(wStr);
			Map<String, String> locs = editor.getSyntaxErrorsLoc().get(loc);

			if ( locs == null ) {
				locs = new TOrderedHashMap<String, String>();
			}
			locs.put(nameLoc, "Syntax errors detected ");
			// .out.println("loc: " + loc);
			editor.getSyntaxErrorsLoc().put(loc, locs);
		}
		if ( error != null ) {
			tip.setMessage(error);
			isValid = error.equals("");
		}
		setBackground(isValid ? colValid : colNotValid);
		if ( isValid ) {
			tip.setVisible(false);
		}
		editor.updateEObjectErrors();
	}

	public void applyModification() {
		// System.out.println("Loc: " + loc + "nameLoc: " + nameLoc + " saveData:" + saveData);

		if ( saveData ) {
			frame.save(nameLoc);
		}
		// System.out.println("isString");
		frame.getShell().forceFocus();
		if ( nameLoc.equals("name") ) {
			isValid =
				!getText().isEmpty() && !getText().contains(" ") && !getText().contains(";") &&
					!getText().contains("{") && !getText().contains("}") && !getText().contains("\t");
		} else {
			isValid = !ModelGenerator.hasSyntaxError(fp, getText(), true, isString);
		}
		if ( isValid ) {
			ModelGenerator.modelValidation(fp, diagram);
			Map<String, String> locs = editor.getSyntaxErrorsLoc().get(loc);
			if ( locs != null ) {
				locs.remove(nameLoc);
			}
			if ( nameFeature && nameLoc.equals("name") ) {
				addToLoc = getText();
				List<String> oldLoc = new ArrayList<String>();
				oldLoc.addAll(loc);
				// System.out.println("oldLoc: " + oldLoc);
				loc.clear();
				// editor.buildLocation(frame.eobject, loc);
				editor.buildLocation(frame.eobject, loc);
				if ( addToLoc != null && !addToLoc.isEmpty() && !loc.get(loc.size() - 1).equals(addToLoc) ) {
					loc.add(addToLoc);
				}

				// System.out.println("newLoc: " + loc);
				for ( ValidateStyledText vst : linkedVsts ) {
					if ( vst != null ) {
						vst.updateLoc(loc);
					}
				}
				for ( ValidateText vst : linkedVts ) {
					if ( vst != null ) {
						vst.updateLoc(loc);
					}
				}
				editor.updateErrors(oldLoc, loc);
				editor.getIdsEObjects().clear();
				editor.initIdsEObjects();

			}
			if ( allErrors ) {
				error = editor.containErrors(loc, "", uselessName);
			} else {
				error = editor.containErrors(loc, nameLoc, uselessName);
			}

		} else {
			error = "Syntax errors detected ";
			if ( editor.isWasOK() ) {
				ModelGenerator.modelValidation(fp, diagram);
			}

			List<String> wStr = new ArrayList<String>();
			wStr.add("world");
			editor.getSyntaxErrorsLoc().remove(wStr);
			System.out.println("editor.getSyntaxErrorsLoc() : " + editor.getSyntaxErrorsLoc());
			System.out.println("loc : " + loc);
			Map<String, String> locs = editor.getSyntaxErrorsLoc().get(loc);

			if ( locs == null ) {
				locs = new TOrderedHashMap<String, String>();
			}
			locs.put(nameLoc, "Syntax errors detected ");

			editor.getSyntaxErrorsLoc().put(loc, locs);
		}
		/*
		 * System.out.println("location:" + loc);
		 * System.out.println("name:" + nameLoc);
		 * System.out.println("isValid: " + isValid);
		 */if ( error != null ) {
			tip.setMessage(error);
			isValid = error.equals("");
		}
		setBackground(isValid ? colValid : colNotValid);
		if ( isValid ) {
			tip.setVisible(false);
		}
		setFocus();
		editor.updateEObjectErrors();
		frame.updateError();
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

	public void updateLoc(final List<String> nwLoc) {
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

	public void setAllErrors(final boolean allErrors) {
		this.allErrors = allErrors;
	}

	public boolean isSaveData() {
		return saveData;
	}

	public void setSaveData(final boolean saveData) {
		this.saveData = saveData;
	}

	public boolean isString() {
		return isString;
	}

	public void setString(final boolean isString) {
		this.isString = isString;
	}

	public boolean isNameFeature() {
		return nameFeature;
	}

	public void setNameFeature(final boolean nameFeature) {
		this.nameFeature = nameFeature;
	}

}
