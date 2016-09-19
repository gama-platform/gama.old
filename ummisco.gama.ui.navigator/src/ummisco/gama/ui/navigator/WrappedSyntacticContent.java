package ummisco.gama.ui.navigator;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.runtime.GAMA;
import msi.gaml.compilation.ISyntacticElement;
import msi.gaml.compilation.ISyntacticElement.SyntacticVisitor;
import msi.gaml.compilation.SyntacticModelElement;
import ummisco.gama.ui.resources.IGamaColors;

public class WrappedSyntacticContent extends VirtualContent implements Comparable<WrappedSyntacticContent> {

	final ISyntacticElement element;

	public WrappedSyntacticContent(final Object root, final ISyntacticElement e) {
		super(root, e instanceof SyntacticModelElement ? "Contents" : GAMA.getGui().getGamlLabelProvider().getText(e));
		element = e;
	}

	@Override
	public boolean hasChildren() {
		if (!element.hasChildren())
			return false;
		if (element.isSpecies() || element.isExperiment() || element instanceof SyntacticModelElement)
			return true;
		return false;
	}

	@Override
	public Object[] getNavigatorChildren() {
		if (!hasChildren())
			return null;
		final List<WrappedSyntacticContent> children = new ArrayList();
		element.visitAllChildren(new SyntacticVisitor() {

			@Override
			public void visit(final ISyntacticElement element) {
				children.add(new WrappedSyntacticContent(WrappedSyntacticContent.this, element));

			}
		});
		return children.toArray();
	}

	@Override
	public Image getImage() {
		return (Image) GAMA.getGui().getGamlLabelProvider().getImage(element);
	}

	@Override
	public Color getColor() {
		return IGamaColors.BLACK.inactive();
	}

	@Override
	public boolean handleDoubleClick() {
		GAMA.getGui().editModel(element.getElement());
		return true;
	}

	@Override
	public int compareTo(final WrappedSyntacticContent o) {
		final ISyntacticElement e = o.element;
		if (element.isSpecies()) {
			if (e.isSpecies())
				return getName().compareTo(o.getName());
			if (element.getKeyword().equals(IKeyword.GRID))
				return 1;
			return 1;
		} else if (e.isSpecies()) {
			return -1;
		} else
			return getName().compareTo(o.getName());

	}

}
