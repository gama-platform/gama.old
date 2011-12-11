/*
 * GAMA - V1.4 http://gama-platform.googlecode.com
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
package msi.gama.environment;

import msi.gama.interfaces.IGeometry;

public interface IAgentFilter {

	public static class Or implements IAgentFilter {

		IAgentFilter a, b;

		public Or(final IAgentFilter a, final IAgentFilter b) {
			this.a = a;
			this.b = b;
		}

		@Override
		public boolean accept(final IGeometry source, final IGeometry agent) {
			return a.accept(source, agent) || b.accept(source, agent);
		}
	}

	public static class And implements IAgentFilter {

		IAgentFilter a, b;

		public And(final IAgentFilter a, final IAgentFilter b) {
			this.a = a;
			this.b = b;
		}

		@Override
		public boolean accept(final IGeometry source, final IGeometry agent) {
			return a.accept(source, agent) && b.accept(source, agent);
		}
	}

	public boolean accept(IGeometry source, IGeometry a);
}