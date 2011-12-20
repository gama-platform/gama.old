/*
 * GAMA - V1.4  http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2012
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2012
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen  (Batch, GeoTools & JTS), 2009-2012
 * - Beno”t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.gui.parameters;

import java.util.List;
import msi.gama.common.interfaces.*;
import msi.gama.common.util.StringUtils;

import msi.gama.kernel.experiment.IParameter;
import msi.gama.metamodel.agent.IAgent;

import msi.gaml.types.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.widgets.*;

public class StringEditor extends AbstractEditor {

	Text textBox;
	boolean asLabel;

	StringEditor(final IParameter param) {
		super(param);
	}

	StringEditor(final IAgent agent, final IParameter param) {
		super(agent, param, null);
		this.asLabel = param.isLabel();
	}

	StringEditor(final Composite parent, final String title, final Object value,
		final EditorListener<String> whenModified, final boolean asLabel) {
		// Convenience method
		super(new SupportParameter(title, value), whenModified);
		this.asLabel = asLabel;
		this.createComposite(parent);

	}

	StringEditor(final Composite parent, final String title, final String value,
		final List<String> among, final EditorListener<String> whenModified, final boolean asLabel) {
		super(new SupportParameter(title, value, among), whenModified);
		this.createComposite(parent);
		this.asLabel = asLabel;
	}

	@Override
	public void modifyText(final ModifyEvent me) {
		if ( internalModification ) { return; }
		modifyValue(StringUtils.toJavaString(textBox.getText()));
	}

	@Override
	protected Control createCustomParameterControl(final Composite comp) {
		textBox = new Text(comp, SWT.BORDER);
		textBox.addModifyListener(this);
		return textBox;
	}

	@Override
	protected void displayParameterValue() {
		textBox.setText(asLabel ? (String) currentValue : StringUtils.toGaml(currentValue));
	}

	@Override
	public Control getEditorControl() {
		return textBox;
	}

	@Override
	public IType getExpectedType() {
		return Types.get(IType.STRING);
	}

}
