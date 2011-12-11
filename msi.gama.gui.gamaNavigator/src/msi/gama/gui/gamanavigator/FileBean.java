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
package msi.gama.gui.gamanavigator;

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
