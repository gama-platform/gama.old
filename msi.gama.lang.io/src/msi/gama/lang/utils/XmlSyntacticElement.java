/**
 * Created by drogoul, 18 nov. 2011
 * 
 */
package msi.gama.lang.utils;

import java.util.*;
import msi.gaml.parser.xml.LineNumberElement;
import org.jdom.Attribute;

/**
 * Written by drogoul
 * Modified on 18 nov. 2011
 * 
 * @todo Description
 * 
 */
public class XmlSyntacticElement implements ISyntacticElement {

	LineNumberElement element;

	public XmlSyntacticElement(final LineNumberElement e) {
		element = e;
	}

	/*
	 * @see msi.gama.kernel.ISyntacticElement#getName()
	 */
	@Override
	public String getName() {
		return element.getName();
	}

	/*
	 * @see msi.gama.kernel.ISyntacticElement#getAttribute(java.lang.String)
	 */
	@Override
	public String getAttribute(final String name) {
		return element.getAttributeValue(name, (String) null);
	}

	/*
	 * @see msi.gama.kernel.ISyntacticElement#getAttributes()
	 */
	@Override
	public Map<String, String> getAttributes() {
		Map<String, String> result = new HashMap();
		for ( Attribute e : (List<Attribute>) element.getAttributes() ) {
			result.put(e.getName(), e.getValue());
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.kernel.ISyntacticElement#setAttribute(java.lang.String, java.lang.String)
	 */
	@Override
	public void setAttribute(final String name, final String value) {
		element.setAttribute(name, value);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.kernel.ISyntacticElement#getChildren()
	 */
	@Override
	public List<ISyntacticElement> getChildren() {
		return new ChildrenList();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.kernel.ISyntacticElement#getChildren(java.lang.String)
	 */
	@Override
	public List<ISyntacticElement> getChildren(final String name) {
		return new ChildrenList(name);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.kernel.ISyntacticElement#getChild(java.lang.String)
	 */
	@Override
	public ISyntacticElement getChild(final String name) {
		LineNumberElement e = element.getChild(name);
		if ( e != null ) { return new XmlSyntacticElement(e); }
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.kernel.ISyntacticElement#getParent()
	 */
	@Override
	public boolean hasParent(final String name) {
		LineNumberElement e = element.getParentElement();
		while (e != null) {
			if ( e.getName().equals(name) ) { return true; }
			e = e.getParentElement();
		}
		return false;
	}

	@Override
	public Object getUnderlyingElement() {
		return element;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.lang.utils.ISyntacticElement#getLineNumber()
	 */
	@Override
	public int getLineNumber() {
		return element.getStartLine();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.lang.utils.ISyntacticElement#getFilename()
	 */
	@Override
	public String getFilename() {
		return element.getFileName();
	}

	@Override
	public String toString() {
		return "XML element " + getName() + " : " + getAttributes();
	}

	public class ChildrenList extends AbstractList<ISyntacticElement> {

		private final List<LineNumberElement> inner;

		protected ChildrenList() {
			inner = element.getChildren();
		}

		protected ChildrenList(final String name) {
			inner = element.getChildren(name);
		}

		@Override
		public ISyntacticElement remove(final int index) {
			inner.remove(index);
			return null;
		}

		@Override
		public ISyntacticElement get(final int index) {
			return new XmlSyntacticElement(inner.get(index));
		}

		@Override
		public int size() {
			return inner.size();
		}

	}
}
