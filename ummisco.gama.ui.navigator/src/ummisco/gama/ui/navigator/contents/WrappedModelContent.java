package ummisco.gama.ui.navigator.contents;

import msi.gaml.compilation.ast.ISyntacticElement;

public class WrappedModelContent extends WrappedSyntacticContent {

	public WrappedModelContent(final WrappedGamaFile file, final ISyntacticElement e) {
		super(file, e, "Contents");
	}

	@Override
	public WrappedGamaFile getFile() {
		return (WrappedGamaFile) getParent();
	}

	@Override
	public boolean hasChildren() {
		return true;
	}

}