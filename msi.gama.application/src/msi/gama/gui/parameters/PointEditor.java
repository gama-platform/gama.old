/*
 * GAMA - V1.4 http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2012
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2012
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen (Batch, GeoTools & JTS), 2009-2012
 * - Beno”t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.gui.parameters;

import msi.gama.common.interfaces.EditorListener;
import msi.gama.common.util.StringUtils;
import msi.gama.kernel.experiment.IParameter;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gaml.operators.Cast;
import msi.gaml.types.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

public class PointEditor extends AbstractEditor implements VerifyListener {

	private Text xText, yText;
	Composite pointEditor;

	PointEditor(final IParameter param) {
		super(param);
	}

	PointEditor(final IAgent agent, final IParameter param) {
		this(agent, param, null);
	}

	PointEditor(final IAgent agent, final IParameter param, final EditorListener l) {
		super(agent, param, l);
	}

	PointEditor(final Composite parent, final String title, final Object value,
		final EditorListener<GamaPoint> whenModified) {
		// Convenience method
		super(new InputParameter(title, value), whenModified);
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
		xText.setBackground(normal_bg);
		final Composite yComposite = new Composite(pointEditor, SWT.NONE);
		yComposite.setLayout(subCompositeLayout);
		yComposite.setLayoutData(subCompositeGridData);
		final Label yLabel = new Label(yComposite, SWT.NONE);
		yLabel.setText("y");
		yText = new Text(yComposite, SWT.BORDER);
		yText.setLayoutData(textGridData);
		yText.setBackground(normal_bg);
		displayParameterValue();
		// xText.addMouseTrackListener(this);
		// yText.addMouseTrackListener(this);
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
		xText.setText(currentValue == null ? "0" : StringUtils.toGaml(p.getX()));
		yText.setText(currentValue == null ? "0" : StringUtils.toGaml(p.getY()));
	}

	@Override
	public void modifyText(final ModifyEvent me) {
		if ( internalModification ) { return; }
		modifyValue(new GamaPoint(Cast.asFloat(getScope(), xText.getText()), Cast.asFloat(
			getScope(), yText.getText())));
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
