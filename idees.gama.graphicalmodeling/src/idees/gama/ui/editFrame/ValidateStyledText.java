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

public class ValidateStyledText extends StyledText {

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
	final ValidateStyledText vst;
	List<String> uselessName;
	boolean saveData = false;

	final static int TOOLTIP_HIDE_DELAY = 200; // 0.2s
	final static int TOOLTIP_SHOW_DELAY = 500; // 0.5s

	public ValidateStyledText(final Composite parent, final int style, final Diagram diagram,
		final IFeatureProvider fp, final EditFrame frame, final GamaDiagramEditor editor, final String name,
		final List<String> uselessName) {
		super(parent, style);
		this.diagram = diagram;
		vst = this;
		new UndoRedoStyledText(this);

		tip = new ToolTip(getShell(), SWT.BALLOON);
		tip.setText("ERROR");
		tip.setAutoHide(false);
		this.uselessName = uselessName;

		this.fp = fp;
		this.nameLoc = name;
		System.out.println("ValidateText");
		loc = new ArrayList<String>();
		editor.buildLocation(frame.eobject, loc);
		// if (!loc.contains("world")) loc.add(0, "world");

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

	public void applyModification() {
		if ( saveData ) {
			frame.save(nameLoc);
		}
		frame.getShell().forceFocus();
		isValid = !ModelGenerator.hasSyntaxError(fp, getText(), false);

		// System.out.println("isValid1: " + isValid);
		if ( isValid ) {
			ModelGenerator.modelValidation(fp, diagram);
			error = editor.containErrors(loc, nameLoc, uselessName);

		} else {
			error = "Syntax errors detected";
			ArrayList<String> wStr = new ArrayList<String>();
			wStr.add("world");
			editor.getSyntaxErrorsLoc().remove(wStr);
			// System.out.println("editor.getSyntaxErrorsLoc() : " + editor.getSyntaxErrorsLoc());

			Map<String, String> locs = editor.getSyntaxErrorsLoc().get(loc);
			if ( locs == null ) {
				locs = new TOrderedHashMap<String, String>();
			}
			locs.put(nameLoc, error);

			editor.getSyntaxErrorsLoc().put(loc, locs);
		}
		// System.out.println("location:" + loc);
		// System.out.println("name:" + name);
		// System.out.println("isValid: " + isValid);
		if ( error != null ) {
			tip.setMessage(error);
			isValid = error.equals("");
		}
		setBackground(isValid ? colValid : colNotValid);
		if ( isValid ) {
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

	public void updateLoc(final List<String> nwLoc) {
		loc.clear();
		loc.addAll(nwLoc);
	}

	public boolean isSaveData() {
		return saveData;
	}

	public void setSaveData(final boolean saveData) {
		this.saveData = saveData;
	}

}
