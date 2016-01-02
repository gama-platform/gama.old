/**
 * Created by drogoul, 17 déc. 2014
 *
 */
package msi.gaml.descriptions;

import java.lang.reflect.AnnotatedElement;
import java.util.*;
import msi.gama.common.interfaces.*;
import msi.gama.precompiler.GamlAnnotations.*;
import msi.gaml.compilation.GamaBundleLoader;

/**
 * Class AbstractProto.
 *
 * @author drogoul
 * @since 17 déc. 2014
 *
 */
public abstract class AbstractProto implements IGamlDescription, INamed, IGamlable {

	private final String name;
	private final String plugin;
	protected final AnnotatedElement support;

	AbstractProto(final String name, final AnnotatedElement support) {
		this.name = name;
		plugin = GamaBundleLoader.CURRENT_PLUGIN_NAME;
		this.support = support;
	}

	@Override
	public String getDocumentation() {
		doc d = getDocAnnotation();
		if ( d == null ) { return ""; }
		StringBuilder sb = new StringBuilder(200);
		String s = d.value(); /* AbstractGamlDocumentation.getMain(getDoc()); */
		if ( s != null && !s.isEmpty() ) {
			sb.append(s);
			sb.append("<br/>");
		}
		s = d.deprecated(); /* AbstractGamlDocumentation.getDeprecated(getDoc()); */
		if ( s != null && !s.isEmpty() ) {
			sb.append("<b>Deprecated</b>: ");
			sb.append("<i>");
			sb.append(s);
			sb.append("</i><br/>");
		}

		return sb.toString();
	}

	public String getDeprecated() {
		doc d = getDocAnnotation();
		if ( d == null ) { return null; }
		String s = d.deprecated();
		if ( s.isEmpty() ) { return null; }
		return s;
	}

	public String getMainDoc() {
		doc d = getDocAnnotation();
		if ( d == null ) { return null; }
		String s = d.value();
		if ( s.isEmpty() ) { return null; }
		return s;
	}

	@Override
	public String getName() {
		return name;
	}

	/**
	 * Method getTitle()
	 * @see msi.gaml.descriptions.IGamlDescription#getTitle()
	 */
	@Override
	public String getTitle() {
		return "";
	}

	/**
	 * Method setName()
	 * @see msi.gama.common.interfaces.INamed#setName(java.lang.String)
	 */
	@Override
	public void setName(final String newName) {}

	public List<usage> getUsages() {
		doc d = getDocAnnotation();
		if ( d != null ) {
			usage[] tt = d.usages();
			if ( tt.length > 0 ) { return new ArrayList(Arrays.asList(tt)); }
		}
		return Collections.EMPTY_LIST;
	}

	@Override
	public String getDefiningPlugin() {
		return plugin;
	}

	public doc getDocAnnotation() {
		doc d = null;
		if ( support != null && support.isAnnotationPresent(doc.class) ) {
			d = support.getAnnotation(doc.class);
		}
		return d;
	}

	//
	// public int getDoc() {
	// return doc;
	// }

	/**
	 * @return
	 */
	public abstract int getKind();
}
