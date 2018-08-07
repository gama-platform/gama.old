package msi.gama.outputs.layers;

import java.awt.Color;

import msi.gama.common.interfaces.IGraphics;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaColor;
import msi.gaml.types.Types;

public class OverlayLayerData extends LayerData {

	final Attribute<GamaColor> border;
	final Attribute<GamaColor> background;
	final Attribute<Boolean> rounded;
	boolean computed;

	public OverlayLayerData(final ILayerStatement def) throws GamaRuntimeException {
		super(def);
		border = create(IKeyword.BORDER, Types.COLOR, null);
		background = create(IKeyword.BACKGROUND, Types.COLOR, new GamaColor(Color.black));
		rounded = create(IKeyword.ROUNDED, Types.BOOL, true);
	}

	public Color getBackgroundColor() {
		return new Color(background.get().getRed(), background.get().getGreen(), background.get().getBlue(),
				(int) (getTransparency() * 255));
	}

	@Override
	public void computePixelsDimensions(final IGraphics g) {
		if (computed) { return; }
		super.computePixelsDimensions(g);
		computed = true;
	}

	public Color getBorderColor() {
		return border.get();
	}

	public boolean isRounded() {
		return rounded.get();
	}

}
