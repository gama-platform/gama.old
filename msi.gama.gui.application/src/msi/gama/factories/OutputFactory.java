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
package msi.gama.factories;

import java.awt.Color;
import msi.gama.interfaces.*;
import msi.gama.kernel.exceptions.*;
import msi.gama.outputs.LayerDisplayOutput;
import msi.gama.outputs.layers.*;
import msi.gama.precompiler.GamlAnnotations.handles;
import msi.gama.precompiler.GamlAnnotations.uses;
import msi.gama.precompiler.*;
import msi.gama.util.GamaList;

/**
 * The Class OutputFactory.
 * 
 * @author drogoul
 */
@handles({ ISymbolKind.OUTPUT })
@uses({ ISymbolKind.LAYER })
public class OutputFactory extends SymbolFactory {

	static public LayerBox largeBox = new LayerBox(1d, 0d, 0d, 1d, 1d);

	public static LayerDisplayOutput createDisplay(final String name, final int refresh,
		final Color background, final AbstractDisplayLayer ... layers) throws GamaRuntimeException {
		IDescription desc;
		try {
			desc = DescriptionFactory.createDescription(ISymbol.DISPLAY, ISymbol.NAME, name);
		} catch (GamlException e) {
			throw new GamaRuntimeException(e);
		}
		LayerDisplayOutput l = new LayerDisplayOutput(desc);
		l.setRefreshRate(refresh);
		l.setBackgroundColor(background);
		l.setLayers(new GamaList(layers));
		return l;
	}

	public static LayerDisplayOutput createDisplay(final String name, final int refresh,
		final Color background) throws GamaRuntimeException {
		return createDisplay(name, refresh, background, new AbstractDisplayLayer[0]);
	}

	public static LayerDisplayOutput createDisplay(final String name, final int refresh)
		throws GamaRuntimeException {
		return createDisplay(name, refresh, Color.white);
	}

	public static LayerDisplayOutput createDisplay(final String name) throws GamaRuntimeException {
		return createDisplay(name, 1);
	}

}
