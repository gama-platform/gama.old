/*
 * GAMA - V1.4  http://gama-platform.googlecode.com
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

import java.util.List;
import msi.gama.interfaces.*;
import msi.gama.internal.types.*;
import msi.gama.util.Cast;
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
		modifyValue(GamaStringType.toJavaString(textBox.getText()));
	}

	@Override
	protected Control createCustomParameterControl(final Composite comp) {
		textBox = new Text(comp, SWT.BORDER);
		textBox.addModifyListener(this);
		return textBox;
	}

	@Override
	protected void displayParameterValue() {
		textBox.setText(asLabel ? (String) currentValue : Cast.toGaml(currentValue));
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
