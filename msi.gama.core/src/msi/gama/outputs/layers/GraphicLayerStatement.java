package msi.gama.outputs.layers;

import java.util.List;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.*;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.compilation.ISymbol;
import msi.gaml.descriptions.IDescription;
import msi.gaml.factories.DescriptionFactory;
import msi.gaml.statements.AspectStatement;
import msi.gaml.types.IType;

@symbol(name = "graphics", kind = ISymbolKind.LAYER, with_sequence = true)
@inside(symbols = IKeyword.DISPLAY)
@facets(value = { @facet(name = IKeyword.POSITION, type = IType.POINT, optional = true),
	@facet(name = IKeyword.SIZE, type = IType.POINT, optional = true),
	@facet(name = IKeyword.TRANSPARENCY, type = IType.FLOAT, optional = true),
	@facet(name = IKeyword.NAME, type = IType.LABEL, optional = false),
	@facet(name = IKeyword.Z, type = IType.FLOAT, optional = true),
	@facet(name = IKeyword.REFRESH, type = IType.BOOL, optional = true) }, omissible = IKeyword.NAME)
public class GraphicLayerStatement extends AbstractLayerStatement {

	AspectStatement aspect;
	static int i;

	public GraphicLayerStatement(final IDescription desc) throws GamaRuntimeException {
		super(desc);
		IDescription d = DescriptionFactory.create(IKeyword.ASPECT, desc, IKeyword.NAME, "graphic_aspect" + i++);
		aspect = new AspectStatement(d);
	}

	@Override
	public void setChildren(final List<? extends ISymbol> children) {
		aspect.setChildren(children);
	}

	public AspectStatement getAspect() {
		return aspect;
	}

	@Override
	public short getType() {
		return ILayerStatement.GRAPHICS;
	}

	@Override
	protected void _init(IScope scope) {}

	@Override
	protected void _step(IScope scope) {}

}
