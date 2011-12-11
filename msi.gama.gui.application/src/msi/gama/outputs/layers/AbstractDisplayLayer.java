/*
 * GAMA - V1.4  http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2011
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2011
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen  (Batch, GeoTools & JTS), 2009-2011
 * - Beno”t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2011
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.outputs.layers;

import java.awt.geom.Rectangle2D;
import java.util.*;
import msi.gama.gui.displays.IDisplay;
import msi.gama.interfaces.*;
import msi.gama.kernel.Symbol;
import msi.gama.kernel.exceptions.GamaRuntimeException;
import msi.gama.outputs.LayerDisplayOutput;
import msi.gama.precompiler.GamlAnnotations.inside;

/**
 * Written by drogoul Modified on 9 nov. 2009
 * 
 * @todo Description
 * 
 */
@inside(symbols = ISymbol.DISPLAY)
public abstract class AbstractDisplayLayer extends Symbol {

	private LayerBox	box;
	IExpression			title;
	String				constantTitle;
	// String currentTitle;
	LayerDisplayOutput	output;
	protected IDisplay	physicalLayer;

	public AbstractDisplayLayer(final IDescription desc) throws GamaRuntimeException {
		super(desc);
		setBox(new LayerBox(getFacet(ISymbol.TRANSPARENCY), getFacet(ISymbol.POSITION),
			getFacet(ISymbol.SIZE)));
		title = getFacet(ISymbol.NAME);
		if ( title.isConst() ) {
			setName(title.literalValue());
			constantTitle = title.literalValue();
			// currentTitle = constantTitle;
		}

	}

	public void prepare(final LayerDisplayOutput out, final IScope sim) throws GamaRuntimeException {
		output = out;
	}

	public abstract short getType();

	public String serializeToGaml() {
		return "";
	}

	public void compute(final IScope scope, final long cycle) throws GamaRuntimeException {
		getBox().compute(scope);
	}

	public final Double getTransparency() {
		return getBox().getTransparency();
	}

	public Rectangle2D.Double getBoundingBox() {
		return getBox().getBoundingBox();
	}

	@Override
	public void setChildren(final List<? extends ISymbol> commands) {}

	public List<? extends ISymbol> getChildren() {
		return Collections.EMPTY_LIST;
	}

	public void setBox(final LayerBox box) {
		this.box = box;
	}

	public LayerBox getBox() {
		return box;
	}

	public void setOpacity(final Double opacity) {
		getBox().setTransparency(opacity);
	}

	/**
	 * @param abstractDisplay
	 */
	public void setPhysicalLayer(final IDisplay abstractDisplay) {
		physicalLayer = abstractDisplay;
	}
}
