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
package msi.gama.gui.xmleditor;

import java.util.*;
import org.eclipse.core.runtime.*;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.graphics.Image;
import org.osgi.framework.Bundle;
import org.w3c.dom.*;

public class GAMLXMLOutlineLabelProvider implements ILabelProvider {

	static final Bundle bundle = Platform.getBundle("msi.gama.lang.gaml.ui");
	static final String path = "icons/";
	static Map<String, Image> images = new HashMap<String, Image>();
	static Image var;
	static Image other;

	static {
		final ImageDescriptor inte =
			ImageDescriptor.createFromURL(FileLocator.find(bundle, new Path(path + "_int.png"),
				null));
		final ImageDescriptor floate =
			ImageDescriptor.createFromURL(FileLocator.find(bundle, new Path(path + "_float.png"),
				null));
		final ImageDescriptor bool =
			ImageDescriptor.createFromURL(FileLocator.find(bundle, new Path(path + "_bool.png"),
				null));
		final ImageDescriptor string =
			ImageDescriptor.createFromURL(FileLocator.find(bundle, new Path(path + "_string.png"),
				null));
		final ImageDescriptor list =
			ImageDescriptor.createFromURL(FileLocator.find(bundle, new Path(path + "_list.png"),
				null));
		final ImageDescriptor matrix =
			ImageDescriptor.createFromURL(FileLocator.find(bundle, new Path(path + "_matrix.png"),
				null));
		final ImageDescriptor point =
			ImageDescriptor.createFromURL(FileLocator.find(bundle, new Path(path + "_point.png"),
				null));
		final ImageDescriptor agent =
			ImageDescriptor.createFromURL(FileLocator.find(bundle, new Path(path + "_agent.png"),
				null));
		final ImageDescriptor rgb =
			ImageDescriptor.createFromURL(FileLocator.find(bundle, new Path(path + "_rgb.png"),
				null));
		final ImageDescriptor species =
			ImageDescriptor.createFromURL(FileLocator.find(bundle, new Path(path + "_species.png"),
				null));
		final ImageDescriptor world =
			ImageDescriptor.createFromURL(FileLocator.find(bundle, new Path(path + "_global.png"),
				null));
		final ImageDescriptor model =
			ImageDescriptor.createFromURL(FileLocator.find(bundle, new Path(path + "_model.png"),
				null));
		final ImageDescriptor environment =
			ImageDescriptor.createFromURL(FileLocator.find(bundle, new Path(path +
				"_environment.png"), null));
		final ImageDescriptor output =
			ImageDescriptor.createFromURL(FileLocator.find(bundle, new Path(path + "_output.png"),
				null));
		final ImageDescriptor monitor =
			ImageDescriptor.createFromURL(FileLocator.find(bundle, new Path(path + "_monitor.png"),
				null));
		final ImageDescriptor chart =
			ImageDescriptor.createFromURL(FileLocator.find(bundle, new Path(path + "_chart.png"),
				null));
		final ImageDescriptor file =
			ImageDescriptor.createFromURL(FileLocator.find(bundle, new Path(path + "_file.png"),
				null));
		final ImageDescriptor display =
			ImageDescriptor.createFromURL(FileLocator.find(bundle, new Path(path + "_display.png"),
				null));
		final ImageDescriptor inspect =
			ImageDescriptor.createFromURL(FileLocator.find(bundle, new Path(path + "_inspect.png"),
				null));
		final ImageDescriptor action =
			ImageDescriptor.createFromURL(FileLocator.find(bundle, new Path(path + "_action.png"),
				null));
		final ImageDescriptor grid =
			ImageDescriptor.createFromURL(FileLocator.find(bundle, new Path(path + "_grid.png"),
				null));
		final ImageDescriptor gis =
			ImageDescriptor.createFromURL(FileLocator.find(bundle, new Path(path + "_gis.png"),
				null));
		final ImageDescriptor init =
			ImageDescriptor.createFromURL(FileLocator.find(bundle, new Path(path + "_init.png"),
				null));
		final ImageDescriptor reflex =
			ImageDescriptor.createFromURL(FileLocator.find(bundle, new Path(path + "_reflex.png"),
				null));
		final ImageDescriptor state =
			ImageDescriptor.createFromURL(FileLocator.find(bundle, new Path(path + "_state.png"),
				null));
		final ImageDescriptor ife =
			ImageDescriptor.createFromURL(FileLocator
				.find(bundle, new Path(path + "_if.png"), null));
		final ImageDescriptor returne =
			ImageDescriptor.createFromURL(FileLocator.find(bundle, new Path(path + "_return.png"),
				null));
		final ImageDescriptor loop =
			ImageDescriptor.createFromURL(FileLocator.find(bundle, new Path(path + "_loop.png"),
				null));
		final ImageDescriptor create =
			ImageDescriptor.createFromURL(FileLocator.find(bundle, new Path(path + "_create.png"),
				null));
		final ImageDescriptor ask =
			ImageDescriptor.createFromURL(FileLocator.find(bundle, new Path(path + "_ask.png"),
				null));
		final ImageDescriptor enter =
			ImageDescriptor.createFromURL(FileLocator.find(bundle, new Path(path + "_enter.png"),
				null));
		final ImageDescriptor exit =
			ImageDescriptor.createFromURL(FileLocator.find(bundle, new Path(path + "_exit.png"),
				null));
		final ImageDescriptor transition =
			ImageDescriptor.createFromURL(FileLocator.find(bundle, new Path(path +
				"_transition.png"), null));
		final ImageDescriptor doe =
			ImageDescriptor.createFromURL(FileLocator
				.find(bundle, new Path(path + "_do.png"), null));
		final ImageDescriptor layer =
			ImageDescriptor.createFromURL(FileLocator.find(bundle, new Path(path + "_layer.png"),
				null));
		final ImageDescriptor set =
			ImageDescriptor.createFromURL(FileLocator.find(bundle, new Path(path + "_set.png"),
				null));
		final ImageDescriptor let =
			ImageDescriptor.createFromURL(FileLocator.find(bundle, new Path(path + "_let.png"),
				null));
		final ImageDescriptor data =
			ImageDescriptor.createFromURL(FileLocator.find(bundle, new Path(path + "_data.png"),
				null));
		final ImageDescriptor include =
			ImageDescriptor.createFromURL(FileLocator.find(bundle, new Path(path + "_include.png"),
				null));
		final ImageDescriptor arg =
			ImageDescriptor.createFromURL(FileLocator.find(bundle, new Path(path + "_arg.png"),
				null));
		final ImageDescriptor entities =
			ImageDescriptor.createFromURL(FileLocator.find(bundle,
				new Path(path + "_entities.png"), null));

		var =
			ImageDescriptor.createFromURL(
				FileLocator.find(bundle, new Path(path + "_var.png"), null)).createImage();
		other =
			ImageDescriptor.createFromURL(
				FileLocator.find(bundle, new Path(path + "_unknown.png"), null)).createImage();

		images.put("species", species.createImage());
		images.put("int", inte.createImage());
		images.put("float", floate.createImage());
		images.put("bool", bool.createImage());
		images.put("list", list.createImage());
		images.put("matrix", matrix.createImage());
		images.put("rgb", rgb.createImage());
		images.put("string", string.createImage());
		images.put("agent", agent.createImage());
		images.put("point", point.createImage());
		images.put("global", world.createImage());
		images.put("model", model.createImage());
		images.put("environment", environment.createImage());
		images.put("output", output.createImage());
		images.put("monitor", monitor.createImage());
		images.put("chart", chart.createImage());
		images.put("display", display.createImage());
		images.put("inspect", inspect.createImage());
		images.put("file", file.createImage());
		images.put("include", include.createImage());
		images.put("action", action.createImage());
		images.put("grid", grid.createImage());
		images.put("gis", gis.createImage());
		images.put("init", init.createImage());
		images.put("reflex", reflex.createImage());
		images.put("state", state.createImage());
		images.put("if", ife.createImage());
		images.put("return", returne.createImage());
		images.put("loop", loop.createImage());
		images.put("create", create.createImage());
		images.put("ask", ask.createImage());
		images.put("enter", enter.createImage());
		images.put("exit", exit.createImage());
		images.put("transition", transition.createImage());
		images.put("do", doe.createImage());
		images.put("layer", layer.createImage());
		images.put("gis-layer", layer.createImage());
		images.put("set", set.createImage());
		images.put("let", let.createImage());
		images.put("data", data.createImage());
		images.put("arg", arg.createImage());
		images.put("entities", entities.createImage());
	}

	public GAMLXMLOutlineLabelProvider() {
		super();
	}

	@Override
	public Image getImage(final Object element) {
		if ( element instanceof Element ) {
			final Element dtdElement = (Element) element;
			String textToShow = dtdElement.getNodeName();
			if ( textToShow.equals("var") ) {
				String s = dtdElement.getAttribute("type");
				if ( s != null ) {
					textToShow = s;
				}
			}
			Image image = images.get(textToShow);
			if ( image == null ) {
				if ( textToShow.equals("var") ) {
					image = var;
				} else {
					image = other;
				}
			}
			return image;
		}
		if ( element instanceof ProcessingInstruction ) { return images.get("entity"); }
		return other;
	}

	@Override
	public String getText(final Object element) {
		String[] names =
			new String[] { "name", "action", "file", "species", "condition", "when", "to", "from",
				"over", "times", "in" };
		if ( element instanceof Element ) {
			final Element e = (Element) element;
			String textToShow = e.getNodeName();
			String n = "";
			String s = null;
			for ( int i = 0; i < names.length; i++ ) {
				n = names[i];
				try {
					s = e.getAttribute(n);
				} catch (NullPointerException e1) {
					s = null;
				}
				if ( s != null && s.length() > 0 ) {
					break;
				}
			}
			if ( s == null || s.length() == 0 ) { return textToShow; }
			if ( textToShow.equals("var") ) {
				String type = e.getAttribute("type");
				if ( type == null ) {
					type = "no type";
				}
				textToShow += " (" + type + ")";
			}
			return textToShow + " " + n + " = " + s;
		}
		if ( element instanceof Comment ) {
			Comment e = (Comment) element;
			String c = e.getData();
			if ( c.length() > 20 ) {
				c = c.substring(0, 20) + "...";
			}
			return "comment: " + c;
		}
		if ( element instanceof ProcessingInstruction ) { return "GAML outline"; }
		return null;
	}

	@Override
	public void addListener(final ILabelProviderListener listener) {}

	@Override
	public void dispose() {}

	@Override
	public boolean isLabelProperty(final Object element, final String property) {
		return false;
	}

	@Override
	public void removeListener(final ILabelProviderListener listener) {}

}