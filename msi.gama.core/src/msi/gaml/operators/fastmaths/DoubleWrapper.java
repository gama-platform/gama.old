/*********************************************************************************************
 *
 * 'DoubleWrapper.java, in plugin msi.gama.core, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package msi.gaml.operators.fastmaths;

public class DoubleWrapper {

	public double value;

	@Override
	public String toString() {
		return Double.toString(this.value);
	}
}
