/**
 * Created by drogoul, 17 déc. 2014
 *
 */
package msi.gaml.descriptions;

import java.lang.reflect.AnnotatedElement;
import java.util.*;
import msi.gama.common.interfaces.*;
import msi.gama.precompiler.GamlAnnotations.*;

/**
 * Class AbstractProto.
 *
 * @author drogoul
 * @since 17 déc. 2014
 *
 */
public abstract class AbstractProto implements IGamlDescription, INamed, IGamlable {

	protected String name;
	protected String plugin;
	protected AnnotatedElement support;

	protected AbstractProto(final String name, final AnnotatedElement support, final String plugin) {
		this.name = name;
		this.plugin = plugin;
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

	@Override
	public void collectPlugins(final Set<String> plugins) {
		plugins.add(plugin);
	}

	public AnnotatedElement getSupport() {
		return support;
	}

	protected void setSupport(final AnnotatedElement support) {
		this.support = support;
	}

	public doc getDocAnnotation() {
		doc d = null;
		if ( support != null && support.isAnnotationPresent(doc.class) ) {
			d = support.getAnnotation(doc.class);
		}
		return d;
	}

	/**
	 * @return
	 */
	public abstract int getKind();
}
