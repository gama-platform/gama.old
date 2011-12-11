/*
 * GAMA - V1.4  http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2011
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2011
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen  (Batch, GeoTools & JTS), 2009-2011
 * - Beno”t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2011
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.gui.gamanavigator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;

public class VirtualProjectFolder extends VirtualFolder {

	public VirtualProjectFolder(String name) {
		super(name);	
	}

	@Override
	public Object[] getChildren() {
		/* Filter to get only non-linked projects from current workspace */
		IProject[] projs = ResourcesPlugin.getWorkspace().getRoot().getProjects();
		List<IProject> tempList = new ArrayList<IProject>(Arrays.asList(projs));
		IPath worspaceLoc = ResourcesPlugin.getWorkspace().getRoot().getLocation();

		for(int i = 0 ; i < projs.length ; i++) {
			String pathProject = projs[i].getLocation().toString();
			String pathws = pathProject.substring(0, pathProject.lastIndexOf("/"));
			if(!pathws.equals(worspaceLoc.toString())) {
				tempList.remove(projs[i]);
			}
		}
		return tempList.toArray();
	}
}
