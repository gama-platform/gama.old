/*
 * GAMA - V1.4 http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC
 * 
 * Developers :
 * 
 * - Alexis Drogoul, IRD (Kernel, Metamodel, XML-based GAML), 2007-2011
 * - Vo Duc An, IRD & AUF (SWT integration, multi-level architecture), 2008-2011
 * - Patrick Taillandier, AUF & CNRS (batch framework, GeoTools & JTS integration), 2009-2011
 * - Pierrick Koch, IRD (XText-based GAML environment), 2010-2011
 * - Romain Lavaud, IRD (project-based environment), 2010
 * - Francois Sempe, IRD & AUF (EMF behavioral model, batch framework), 2007-2009
 * - Edouard Amouroux, IRD (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, IRD (OpenMap integration), 2007-2008
 */
package msi.gama.gui.parameters;

import msi.gama.gui.application.GUI;
import msi.gama.interfaces.*;
import msi.gama.internal.types.Types;
import msi.gama.util.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

public class PointEditor extends AbstractEditor implements VerifyListener {

	private Text	xText, yText;
	Composite		pointEditor;

	PointEditor(final IParameter param) {
		super(param);
	}

	PointEditor(final IAgent agent, final IParameter param) {
		super(agent, param, null);
	}

	PointEditor(final Composite parent, final String title, final Object value,
		final EditorListener<GamaPoint> whenModified) {
		// Convenience method
		super(new SupportParameter(title, value), whenModified);
		this.createComposite(parent);
	}

	@Override
	public Control createCustomParameterControl(final Composite comp) {
		pointEditor = new Composite(comp, SWT.NONE);
		final GridData pointEditorGridData =
			new GridData(GridData.FILL, GridData.CENTER, true, false);
		pointEditorGridData.widthHint = 100;
		pointEditor.setLayoutData(pointEditorGridData);
		final GridLayout pointEditorLayout = new GridLayout(2, true);
		pointEditorLayout.horizontalSpacing = 10;
		pointEditorLayout.verticalSpacing = 0;
		pointEditorLayout.marginHeight = 0;
		pointEditorLayout.marginWidth = 0;
		pointEditor.setLayout(pointEditorLayout);

		final Composite xComposite = new Composite(pointEditor, SWT.NONE);
		final GridLayout subCompositeLayout = new GridLayout(2, false);
		subCompositeLayout.marginHeight = 0;
		subCompositeLayout.marginWidth = 0;
		xComposite.setLayout(subCompositeLayout);
		final GridData subCompositeGridData =
			new GridData(GridData.FILL, GridData.CENTER, true, false);
		xComposite.setLayoutData(subCompositeGridData);
		final Label xLabel = new Label(xComposite, SWT.NONE);
		xLabel.setText("x");
		xText = new Text(xComposite, SWT.BORDER);
		final GridData textGridData = new GridData(GridData.FILL, GridData.CENTER, true, false);
		textGridData.widthHint = 40;
		xText.setLayoutData(textGridData);
		xText.setBackground(GUI.normal_bg);
		final Composite yComposite = new Composite(pointEditor, SWT.NONE);
		yComposite.setLayout(subCompositeLayout);
		yComposite.setLayoutData(subCompositeGridData);
		final Label yLabel = new Label(yComposite, SWT.NONE);
		yLabel.setText("y");
		yText = new Text(yComposite, SWT.BORDER);
		yText.setLayoutData(textGridData);
		yText.setBackground(GUI.normal_bg);
		displayParameterValue();
		xText.addMouseTrackListener(this);
		yText.addMouseTrackListener(this);
		xText.addModifyListener(this);
		xText.addVerifyListener(this);
		yText.addModifyListener(this);
		yText.addVerifyListener(this);
		return pointEditor;
	}

	@Override
	public void verifyText(final VerifyEvent event) {
		if ( internalModification ) { return; }
		char myChar = event.character;
		// Assume we don't allow it
		event.doit = Character.isDigit(myChar) || myChar == '\b' || myChar == '.';
	}

	@Override
	protected void displayParameterValue() {
		GamaPoint p = (GamaPoint) currentValue;
		xText.setText(currentValue == null ? "0" : Cast.toGaml(p.first()));
		yText.setText(currentValue == null ? "0" : Cast.toGaml(p.last()));
	}

	@Override
	public void modifyText(final ModifyEvent me) {
		if ( internalModification ) { return; }
		modifyValue(new GamaPoint(Cast.asFloat(xText.getText()), Cast.asFloat(yText.getText())));
	}

	@Override
	public Control getEditorControl() {
		return pointEditor;
	}

	@Override
	public IType getExpectedType() {
		return Types.get(IType.POINT);
	}

}
