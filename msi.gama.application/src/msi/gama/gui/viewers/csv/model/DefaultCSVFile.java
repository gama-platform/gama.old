/*
 * Copyright 2011 csvedit
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package msi.gama.gui.viewers.csv.model;

import java.io.*;
import msi.gama.gui.navigator.FileMetaDataProvider;
import msi.gama.util.file.GamaCSVFile.CSVInfo;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;

/**
 * 
 * {@link DefaultCSVFile} implements the {@link AbstractCSVFile} abstract
 * methods based on the values stored in the metada or given by users.
 * @author jpizar
 * @version 2 adapted for GAMA by A. Drogoul
 * 
 */
public class DefaultCSVFile extends AbstractCSVFile {

	private final CSVInfo info;
	private final IFile file;

	/** Preferences provider */
	// private final ICsvOptionsProvider optionsProvider;

	/**
	 * Constructor
	 * @param provider the {@link PreferencesCSVOptionsProvider}
	 */
	// public DefaultCSVFile(final CSVInfo info/* ICsvOptionsProvider provider */) {
	// super();
	// this.info = info;
	// // this.optionsProvider = provider;
	// }

	/**
	 * @throws CoreException
	 * @param editorInput
	 */
	public DefaultCSVFile(final IFile file) throws CoreException {
		info = (CSVInfo) FileMetaDataProvider.getInstance().getMetaData(file);
		this.file = file;
	}

	@Override
	public boolean isFirstLineHeader() {
		return info.header;
		// return optionsProvider.getUseFirstLineAsHeader();
	}

	@Override
	public void setFirstLineHeader(final boolean header) {
		info.header = header;
		if ( header ) {
			BufferedReader br;
			try {
				br = new BufferedReader(new InputStreamReader(file.getContents()));
				readLines(br.readLine());
				info.setHeaders(this.getHeader().toArray(new String[0]));
			} catch (CoreException e) {
				info.setHeaders(null);
			} catch (IOException e) {
				info.setHeaders(null);
			}
		} else {
			info.setHeaders(null);
		}
		FileMetaDataProvider.getInstance().storeMetadata(file, info);
	}

	@Override
	public boolean getSensitiveSearch() {
		return false;
		// return optionsProvider.getSensitiveSearch();
	}

	@Override
	public char getCustomDelimiter() {
		return info.delimiter;
		// return optionsProvider.getCustomDelimiter().charAt(0);
	}

	@Override
	public char getCommentChar() {
		// String commentChar = optionsProvider.getCommentChar();
		char result = Character.UNASSIGNED;
		// if ( commentChar != null && commentChar != "" ) {
		// result = commentChar.charAt(0);
		// }
		return result;
	}

	@Override
	public char getTextQualifier() {
		// String qualifierChar = optionsProvider.getTextQualifier();
		char result = Character.UNASSIGNED;
		// if ( qualifierChar != null && qualifierChar != "" && qualifierChar.length() > 0 ) {
		// result = qualifierChar.charAt(0);
		// }
		return result;
	}

	@Override
	public boolean useQualifier() {
		return false;
		// return optionsProvider.useTextQualifier();
	}
}