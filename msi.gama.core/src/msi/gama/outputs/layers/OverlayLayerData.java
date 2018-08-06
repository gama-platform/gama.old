package msi.gama.outputs.layers;

import java.awt.Color;

import msi.gama.common.interfaces.IGraphics;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaColor;
import msi.gaml.types.GamaBoolType;
import msi.gaml.types.GamaColorType;
import msi.gaml.types.Types;

public class OverlayLayerData extends LayerData {

	final Attribute<GamaColorType, GamaColor> border;
	final Attribute<GamaColorType, GamaColor> background;
	final Attribute<GamaBoolType, Boolean> rounded;
	boolean computed;

	public OverlayLayerData(final ILayerStatement def) throws GamaRuntimeException {
		super(def);
		border = create(def.getFacet(IKeyword.BORDER), Types.COLOR, null);
		background = create(def.getFacet(IKeyword.BACKGROUND), Types.COLOR, new GamaColor(Color.black));
		rounded = create(def.getFacet(IKeyword.ROUNDED), Types.BOOL, true);
	}

	public Color getBackgroundColor() {
		return new Color(background.value.getRed(), background.value.getGreen(), background.value.getBlue(),
				(int) (getTransparency() * 255));
	}

	@Override
	public void compute(final IScope scope, final IGraphics g) throws GamaRuntimeException {
		super.compute(scope, g);
		border.refresh(scope);
		background.refresh(scope);
		rounded.refresh(scope);
	}

	@Override
	public void computePixelsDimensions(final IGraphics g) {
		if (computed) { return; }
		super.computePixelsDimensions(g);
		computed = true;
	}

	public Color getBorderColor() {
		return border.value;
	}

	public boolean isRounded() {
		return rounded.value;
	}

}
