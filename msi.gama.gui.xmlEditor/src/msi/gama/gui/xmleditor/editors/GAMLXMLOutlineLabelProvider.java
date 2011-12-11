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
package msi.gama.gui.xmleditor.editors;

import java.util.*;
import org.eclipse.core.runtime.*;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.graphics.Image;
import org.w3c.dom.*;

public class GAMLXMLOutlineLabelProvider implements ILabelProvider {

	static Map<String, Image> images = new HashMap<String, Image>();
	static Image var;
	static Image other;

	static {
		final ImageDescriptor inte =
			ImageDescriptor.createFromURL(FileLocator.find(
				Platform.getBundle("msi.gama.gui.xmlEditor"), new Path("icons/outline/_int.png"),
				null));
		final ImageDescriptor floate =
			ImageDescriptor.createFromURL(FileLocator.find(
				Platform.getBundle("msi.gama.gui.xmlEditor"), new Path("icons/outline/_float.png"),
				null));
		final ImageDescriptor bool =
			ImageDescriptor.createFromURL(FileLocator.find(
				Platform.getBundle("msi.gama.gui.xmlEditor"), new Path("icons/outline/_bool.png"),
				null));
		final ImageDescriptor string =
			ImageDescriptor.createFromURL(FileLocator.find(
				Platform.getBundle("msi.gama.gui.xmlEditor"),
				new Path("icons/outline/_string.png"), null));
		final ImageDescriptor list =
			ImageDescriptor.createFromURL(FileLocator.find(
				Platform.getBundle("msi.gama.gui.xmlEditor"), new Path("icons/outline/_list.png"),
				null));
		final ImageDescriptor matrix =
			ImageDescriptor.createFromURL(FileLocator.find(
				Platform.getBundle("msi.gama.gui.xmlEditor"),
				new Path("icons/outline/_matrix.png"), null));
		final ImageDescriptor point =
			ImageDescriptor.createFromURL(FileLocator.find(
				Platform.getBundle("msi.gama.gui.xmlEditor"), new Path("icons/outline/_point.png"),
				null));
		final ImageDescriptor agent =
			ImageDescriptor.createFromURL(FileLocator.find(
				Platform.getBundle("msi.gama.gui.xmlEditor"), new Path("icons/outline/_agent.png"),
				null));
		final ImageDescriptor rgb =
			ImageDescriptor.createFromURL(FileLocator.find(
				Platform.getBundle("msi.gama.gui.xmlEditor"), new Path("icons/outline/_rgb.png"),
				null));
		final ImageDescriptor species =
			ImageDescriptor.createFromURL(FileLocator.find(
				Platform.getBundle("msi.gama.gui.xmlEditor"),
				new Path("icons/outline/_species.png"), null));
		final ImageDescriptor world =
			ImageDescriptor.createFromURL(FileLocator.find(
				Platform.getBundle("msi.gama.gui.xmlEditor"),
				new Path("icons/outline/_global.png"), null));
		final ImageDescriptor model =
			ImageDescriptor.createFromURL(FileLocator.find(
				Platform.getBundle("msi.gama.gui.xmlEditor"), new Path("icons/outline/_model.png"),
				null));
		final ImageDescriptor environment =
			ImageDescriptor.createFromURL(FileLocator.find(Platform
				.getBundle("msi.gama.gui.xmlEditor"), new Path("icons/outline/_environment.png"),
				null));
		final ImageDescriptor output =
			ImageDescriptor.createFromURL(FileLocator.find(
				Platform.getBundle("msi.gama.gui.xmlEditor"),
				new Path("icons/outline/_output.png"), null));
		final ImageDescriptor monitor =
			ImageDescriptor.createFromURL(FileLocator.find(
				Platform.getBundle("msi.gama.gui.xmlEditor"),
				new Path("icons/outline/_monitor.png"), null));
		final ImageDescriptor chart =
			ImageDescriptor.createFromURL(FileLocator.find(
				Platform.getBundle("msi.gama.gui.xmlEditor"), new Path("icons/outline/_chart.png"),
				null));
		final ImageDescriptor file =
			ImageDescriptor.createFromURL(FileLocator.find(
				Platform.getBundle("msi.gama.gui.xmlEditor"), new Path("icons/outline/_file.png"),
				null));
		final ImageDescriptor display =
			ImageDescriptor.createFromURL(FileLocator.find(
				Platform.getBundle("msi.gama.gui.xmlEditor"),
				new Path("icons/outline/_display.png"), null));
		final ImageDescriptor inspect =
			ImageDescriptor.createFromURL(FileLocator.find(
				Platform.getBundle("msi.gama.gui.xmlEditor"),
				new Path("icons/outline/_inspect.png"), null));
		final ImageDescriptor action =
			ImageDescriptor.createFromURL(FileLocator.find(
				Platform.getBundle("msi.gama.gui.xmlEditor"),
				new Path("icons/outline/_action.png"), null));
		final ImageDescriptor grid =
			ImageDescriptor.createFromURL(FileLocator.find(
				Platform.getBundle("msi.gama.gui.xmlEditor"), new Path("icons/outline/_grid.png"),
				null));
		final ImageDescriptor gis =
			ImageDescriptor.createFromURL(FileLocator.find(
				Platform.getBundle("msi.gama.gui.xmlEditor"), new Path("icons/outline/_gis.png"),
				null));
		final ImageDescriptor init =
			ImageDescriptor.createFromURL(FileLocator.find(
				Platform.getBundle("msi.gama.gui.xmlEditor"), new Path("icons/outline/_init.png"),
				null));
		final ImageDescriptor reflex =
			ImageDescriptor.createFromURL(FileLocator.find(
				Platform.getBundle("msi.gama.gui.xmlEditor"),
				new Path("icons/outline/_reflex.png"), null));
		final ImageDescriptor state =
			ImageDescriptor.createFromURL(FileLocator.find(
				Platform.getBundle("msi.gama.gui.xmlEditor"), new Path("icons/outline/_state.png"),
				null));
		final ImageDescriptor ife =
			ImageDescriptor.createFromURL(FileLocator.find(
				Platform.getBundle("msi.gama.gui.xmlEditor"), new Path("icons/outline/_if.png"),
				null));
		final ImageDescriptor returne =
			ImageDescriptor.createFromURL(FileLocator.find(
				Platform.getBundle("msi.gama.gui.xmlEditor"),
				new Path("icons/outline/_return.png"), null));
		final ImageDescriptor loop =
			ImageDescriptor.createFromURL(FileLocator.find(
				Platform.getBundle("msi.gama.gui.xmlEditor"), new Path("icons/outline/_loop.png"),
				null));
		final ImageDescriptor create =
			ImageDescriptor.createFromURL(FileLocator.find(
				Platform.getBundle("msi.gama.gui.xmlEditor"),
				new Path("icons/outline/_create.png"), null));
		final ImageDescriptor ask =
			ImageDescriptor.createFromURL(FileLocator.find(
				Platform.getBundle("msi.gama.gui.xmlEditor"), new Path("icons/outline/_ask.png"),
				null));
		final ImageDescriptor enter =
			ImageDescriptor.createFromURL(FileLocator.find(
				Platform.getBundle("msi.gama.gui.xmlEditor"), new Path("icons/outline/_enter.png"),
				null));
		final ImageDescriptor exit =
			ImageDescriptor.createFromURL(FileLocator.find(
				Platform.getBundle("msi.gama.gui.xmlEditor"), new Path("icons/outline/_exit.png"),
				null));
		final ImageDescriptor transition =
			ImageDescriptor.createFromURL(FileLocator.find(Platform
				.getBundle("msi.gama.gui.xmlEditor"), new Path("icons/outline/_transition.png"),
				null));
		final ImageDescriptor doe =
			ImageDescriptor.createFromURL(FileLocator.find(
				Platform.getBundle("msi.gama.gui.xmlEditor"), new Path("icons/outline/_do.png"),
				null));
		final ImageDescriptor layer =
			ImageDescriptor.createFromURL(FileLocator.find(
				Platform.getBundle("msi.gama.gui.xmlEditor"), new Path("icons/outline/_layer.png"),
				null));
		final ImageDescriptor set =
			ImageDescriptor.createFromURL(FileLocator.find(
				Platform.getBundle("msi.gama.gui.xmlEditor"), new Path("icons/outline/_set.png"),
				null));
		final ImageDescriptor let =
			ImageDescriptor.createFromURL(FileLocator.find(
				Platform.getBundle("msi.gama.gui.xmlEditor"), new Path("icons/outline/_let.png"),
				null));
		final ImageDescriptor data =
			ImageDescriptor.createFromURL(FileLocator.find(
				Platform.getBundle("msi.gama.gui.xmlEditor"), new Path("icons/outline/_data.png"),
				null));
		final ImageDescriptor include =
			ImageDescriptor.createFromURL(FileLocator.find(
				Platform.getBundle("msi.gama.gui.xmlEditor"),
				new Path("icons/outline/_include.png"), null));
		final ImageDescriptor arg =
			ImageDescriptor.createFromURL(FileLocator.find(
				Platform.getBundle("msi.gama.gui.xmlEditor"), new Path("icons/outline/_arg.png"),
				null));
		final ImageDescriptor entities =
			ImageDescriptor
				.createFromURL(FileLocator.find(Platform.getBundle("msi.gama.gui.xmlEditor"),
					new Path("icons/outline/_entities.png"), null));

		var =
			ImageDescriptor.createFromURL(
				FileLocator.find(Platform.getBundle("msi.gama.gui.xmlEditor"), new Path(
					"icons/outline/_var.png"), null)).createImage();
		other =
			ImageDescriptor.createFromURL(
				FileLocator.find(Platform.getBundle("msi.gama.gui.xmlEditor"), new Path(
					"icons/outline/_unknown.png"), null)).createImage();

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

	public void addListener(final ILabelProviderListener listener) {}

	public void dispose() {}

	public boolean isLabelProperty(final Object element, final String property) {
		return false;
	}

	public void removeListener(final ILabelProviderListener listener) {}

}