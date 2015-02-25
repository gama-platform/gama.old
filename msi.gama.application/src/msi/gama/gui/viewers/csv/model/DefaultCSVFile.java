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

import msi.gama.util.file.*;
import msi.gama.util.file.CsvReader.Stats;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;

/**
 * 
 * {@link DefaultCSVFile} implements the {@link AbstractCSVFile} abstract
 * methods based on the values stored in the preferences system
 * @author jpizar
 * 
 */
public class DefaultCSVFile extends AbstractCSVFile {

	private final Stats stats;

	/** Preferences provider */
	// private final ICsvOptionsProvider optionsProvider;

	/**
	 * Constructor
	 * @param provider the {@link PreferencesCSVOptionsProvider}
	 */
	public DefaultCSVFile(final Stats stats/* ICsvOptionsProvider provider */) {
		super();
		this.stats = stats;
		// this.optionsProvider = provider;
	}

	/**
	 * @throws CoreException
	 * @param editorInput
	 */
	public DefaultCSVFile(final IFile file) throws CoreException {
		this(CsvReader.getStats(file.getContents()));
	}

	@Override
	public boolean isFirstLineHeader() {
		return stats.header;
		// return optionsProvider.getUseFirstLineAsHeader();
	}

	@Override
	public boolean getSensitiveSearch() {
		return false;
		// return optionsProvider.getSensitiveSearch();
	}

	@Override
	public char getCustomDelimiter() {
		return stats.delimiter;
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
		return true;
		// return optionsProvider.useTextQualifier();
	}
}