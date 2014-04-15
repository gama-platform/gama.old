/*********************************************************************************************
 * 
 *
 * 'FileBean.java', in plugin 'msi.gama.application', is part of the source code of the 
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.gui.navigator;

import java.io.*;

public class FileBean {

	private final File file;
	private static final FileBean[] EMPTY_ARRAY = new FileBean[0];

	public FileBean(final File file) {
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
		if ( files.length != 0 ) {
			FileBean[] gamaFiles = new FileBean[files.length];

			for ( int i = 0; i < files.length; i++ ) {
				gamaFiles[i] = new FileBean(files[i]);
			}
			return gamaFiles;
		}
		return EMPTY_ARRAY;
	}

	public FileBean[] getFirstChildren() {
		File[] files = file.listFiles(noDirectories);
		if ( files.length != 0 ) {
			FileBean[] gamaFiles = new FileBean[files.length];

			for ( int i = 0; i < files.length; i++ ) {
				gamaFiles[i] = new FileBean(files[i]);
			}
			return gamaFiles;
		}
		return EMPTY_ARRAY;
	}

	public FileBean[] getChildrenWithHiddenFiles() {
		File[] files = file.listFiles();
		if ( files.length != 0 ) {
			FileBean[] gamaFiles = new FileBean[files.length];

			for ( int i = 0; i < files.length; i++ ) {
				gamaFiles[i] = new FileBean(files[i]);
			}
			return gamaFiles;
		}
		return EMPTY_ARRAY;
	}

	public FileBean getParent() {
		File parent = file.getParentFile();
		return parent == null ? null : new FileBean(parent);
	}

	/* Filter to hide file and directory starting with '.' */
	private final FileFilter noHiddenFiles = new FileFilter() {

		@Override
		public boolean accept(final File arg0) {
			File f = arg0;
			String s = f.getName();
			return !s.startsWith(".");
		}
	};

	/* Filter to get only the first level of children */
	private final FileFilter noDirectories = new FileFilter() {

		@Override
		public boolean accept(final File arg0) {
			File f = arg0;
			return !f.isDirectory();
		}
	};
}
