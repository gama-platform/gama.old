/*********************************************************************************************
 *
 *
 * 'GamlResourceDocManager.java', in plugin 'msi.gama.lang.gaml', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gama.lang.gaml.resource;

import java.nio.charset.Charset;
import java.util.Set;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.EcoreUtil2;
import gnu.trove.map.hash.THashMap;
import msi.gaml.descriptions.*;
import msi.gaml.expressions.IOperator;
import msi.gaml.factories.DescriptionFactory.IDocManager;

/**
 * Class GamlResourceDocManager.
 *
 * @author drogoul
 * @since 13 avr. 2014
 *
 */
public class GamlResourceDocManager implements IDocManager {

	private static final Charset UTF8_CHARSET = Charset.forName("UTF-8");

	public static byte[] encode(final String string) {
		if ( string == null ) { return null; }
		return string.getBytes(UTF8_CHARSET);
	}

	public static String decode(final byte[] bytes) {
		if ( bytes == null ) { return null; }
		return new String(bytes, UTF8_CHARSET);
	}

	static int MAX_SIZE = 10000;

	// 0 is the default value of EObject gamlDoc attribute
	static int count = 1;

	private static THashMap<URI, THashMap<String, IGamlDescription>> CACHE2 = new THashMap();

	private static IDocManager instance;

	static int getIndex() {
		return count++;
	}

	public static class Documentation implements IGamlDescription {

		final byte[] doc;
		final byte[] title;
		final String plugin;

		Documentation(final IGamlDescription desc) {
			plugin = desc.getDefiningPlugin();
			title = encode(desc.getTitle());
			String documentation = null;
			if ( desc instanceof IOperator ) {
				OperatorProto proto = ((IOperator) desc).getPrototype();
				if ( proto != null ) {
					documentation = proto.getMainDoc();
				} else {
					documentation = desc.getDocumentation();
				}
			} else {
				documentation = desc.getDocumentation();
			}
			if ( plugin != null ) {
				documentation += "\n<p/><i> [defined in " + plugin + "] </i>";
			}
			doc = encode(documentation);

		}

		/**
		 * Method collectPlugins()
		 * @see msi.gaml.descriptions.IGamlDescription#collectPlugins(java.util.Set)
		 */
		@Override
		public void collectPlugins(final Set<String> plugins) {}

		@Override
		public String getDocumentation() {
			return decode(doc);
		}

		@Override
		public String getTitle() {
			return decode(title);
		}

		@Override
		public String getName() {
			return "Online documentation";
		}

		@Override
		public String getDefiningPlugin() {
			return plugin;
		}

		@Override
		public void setName(final String name) {
			// Nothing
		}

		@Override
		public String toString() {
			return getTitle() + " - " + getDocumentation();
		}

		/**
		 * Method serialize()
		 * @see msi.gama.common.interfaces.IGamlable#serialize(boolean)
		 */
		@Override
		public String serialize(final boolean includingBuiltIn) {
			return toString();
		}

	}

	private GamlResourceDocManager() {}

	public static IDocManager getInstance() {
		if ( instance == null ) {
			instance = new GamlResourceDocManager();
		}
		return instance;
	}

	//
	// @Override
	// public int getGamlDocIndex(final EObject object) {
	// return EGaml.getGamlDocIndex(object);
	// }
	//
	// @Override
	// public int setGamlDocIndex(final EObject object, final int index) {
	// return EGaml.setGamlDocIndex(object, index);
	// }

	@Override
	public void setGamlDocumentation(final EObject object, final IGamlDescription description) {
		if ( description == null || object == null ) { return; }
		// int i = getIndex();
		// i = setGamlDocIndex(object, i);
		// if ( i == -1 ) { return; }
		URI key = getKey(object);
		if ( key == null ) { return; }
		// if ( CACHE.contains(key) ) {
		// CACHE.get(key).put(i, getGamlDocumentation(description));
		// }
		if ( CACHE2.contains(key) ) {
			CACHE2.get(key).put(EcoreUtil2.getURIFragment(object), getGamlDocumentation(description));
		}

	}

	// To be called once the validation has been done
	@Override
	public void document(final IDescription desc) {
		if ( desc == null ) { return; }
		EObject e = desc.getUnderlyingElement(null);
		if ( e == null ) { return; }
		if ( !CACHE2.containsKey(e.eResource().getURI()) ) { return; }
		setGamlDocumentation(e, desc);
		for ( IDescription d : desc.getChildren() ) {
			document(d);
		}
	}

	@Override
	public IGamlDescription getGamlDocumentation(final IGamlDescription o) {
		if ( o == null ) { return null; }
		return new Documentation(o);
	}

	@Override
	public IGamlDescription getGamlDocumentation(final EObject object) {
		if ( object == null ) { return null; }
		// int index = getGamlDocIndex(object);
		// if ( index == -1 ) { return null; }
		URI key = getKey(object);
		if ( key == null ) { return null; }
		if ( !CACHE2.containsKey(key) ) { return null; }
		return CACHE2.get(key).get(EcoreUtil2.getURIFragment(object));
	}

	private URI getKey(final EObject object) {
		if ( object == null ) { return null; }
		Resource r = object.eResource();
		if ( r == null ) { return null; }
		return r.getURI();
	}

	@Override
	public void document(final Resource gamlResource, final boolean accept) {
		if ( accept ) {
			CACHE2.putIfAbsent(gamlResource.getURI(), new THashMap(MAX_SIZE, 0.95f));
		} else {
			CACHE2.remove(gamlResource.getURI());
		}
	}

}
