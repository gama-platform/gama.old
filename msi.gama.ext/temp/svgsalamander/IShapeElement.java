package msi.gama.ext.svgsalamander;

import org.xml.sax.Attributes;

public interface IShapeElement {

	void loaderAddChild(IShapeElement svgEle) throws SVGElementException;

	void loaderStartElement(final Attributes attrs, final IShapeElement parent2);

	void loaderBuild() throws SVGException;

}
