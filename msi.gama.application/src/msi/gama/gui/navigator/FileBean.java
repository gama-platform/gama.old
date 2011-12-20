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
package msi.gama.gui.navigator;

import java.io.File;
import java.io.FileFilter;

public class FileBean {

	File file;
	private static final FileBean[] EMPTY_ARRAY = new FileBean[0];

	public FileBean(File file) {
		this.file = file;
	}

	@Override
	public String toString() {
		return file.getName();
	}

	public String getPath() {
		return file.getAbsolutePath();
	}

	public boolean hasChildren() {
		return file.list() != null;
	}

	public FileBean[] getChildren() {
		File[] files = file.listFiles(noHiddenFiles);
		if (files.length != 0) {
			FileBean[] gamaFiles = new FileBean[files.length];

			for (int i = 0; i < files.length; i++) {
				gamaFiles[i] = new FileBean(files[i]);
			}
			return gamaFiles;
		}
		return EMPTY_ARRAY;
	}

	public FileBean[] getFirstChildren() {
		File[] files = file.listFiles(noDirectories);
		if (files.length != 0) {
			FileBean[] gamaFiles = new FileBean[files.length];

			for (int i = 0; i < files.length; i++) {
				gamaFiles[i] = new FileBean(files[i]);
			}
			return gamaFiles;
		}
		return EMPTY_ARRAY;
	}

	public FileBean[] getChildrenWithHiddenFiles() {
		File[] files = file.listFiles();
		if (files.length != 0) {
			FileBean[] gamaFiles = new FileBean[files.length];

			for (int i = 0; i < files.length; i++) {
				gamaFiles[i] = new FileBean(files[i]);
			}
			return gamaFiles;
		}
		return EMPTY_ARRAY;
	}

	/* Filter to hide file and directory starting with '.' */
	final FileFilter noHiddenFiles = new FileFilter() {
		@Override
		public boolean accept(File arg0) {
			File f = arg0;
			String s = f.getName();
			return !s.startsWith(".");
		}
	};

	/* Filter to get only the first level of children */
	final FileFilter noDirectories = new FileFilter() {
		@Override
		public boolean accept(File arg0) {
			File f = arg0;
			return !f.isDirectory();
		}
	};
}
