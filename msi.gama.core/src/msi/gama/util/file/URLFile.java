/*********************************************************************************************
 *
 * 'URLFile.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling and simulation platform. (c)
 * 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package msi.gama.util.file;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import msi.gama.common.geometry.Envelope3D;
import msi.gama.precompiler.GamlAnnotations.file;
import msi.gama.precompiler.IConcept;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaListFactory;
import msi.gama.util.IList;
import msi.gaml.operators.Strings;
import msi.gaml.types.IContainerType;
import msi.gaml.types.IType;
import msi.gaml.types.Types;

@file (
		name = "URL",
		extensions = { "url" },
		buffer_type = IType.LIST,
		buffer_content = IType.STRING,
		concept = { IConcept.TEXT, IConcept.FILE })
public class URLFile extends GamaFile<IList<String>, String> {

	private final String URL;

	public URLFile(final IScope scope, final String pathName) {
		super(scope, pathName);
		URL = "";
	}

	public URLFile(final IScope scope, final String pathName, final String u) {
		super(scope, pathName);
		URL = u;
	}

	@Override
	public String _stringValue(final IScope scope) throws GamaRuntimeException {
		getContents(scope);
		final StringBuilder sb = new StringBuilder(getBuffer().length(scope) * 200);
		for (final String s : getBuffer().iterable(scope)) {
			sb.append(s).append(Strings.LN);
		}
		sb.setLength(sb.length() - 1);
		return sb.toString();
	}

	@Override
	public IList<String> getAttributes(final IScope scope) {
		// TODO Dont know what to return
		return GamaListFactory.create();
	}

	public IList<String> getURLContent(final IScope scope, final String u_str) {
		URL url;

		final IList<String> allLines = GamaListFactory.create(Types.STRING);
		try {
			// get URL content
			url = new URL(u_str);
			final URLConnection conn = url.openConnection();

			// open the stream and put it into BufferedReader
			try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {

				String inputLine;

				// save to this filename
				// String fileName = "/users/mkyong/test.html";
				final File file = new File(this.getPath(scope));

				// if (!file.exists()) {
				// file.createNewFile();
				// }

				// use FileWriter to write file
				try (final FileWriter fw = new FileWriter(file.getAbsoluteFile());
						final BufferedWriter bw = new BufferedWriter(fw)) {

					while ((inputLine = br.readLine()) != null) {
						bw.write(inputLine + "\n");

						allLines.add(inputLine);
					}

				}
			}

		} catch (final MalformedURLException e) {
			e.printStackTrace();
		} catch (final IOException e) {
			e.printStackTrace();
		}
		return allLines;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see msi.gama.util.GamaFile#fillBuffer()
	 */
	@Override
	protected void fillBuffer(final IScope scope) throws GamaRuntimeException {
		if (getBuffer() != null) { return; }

		setBuffer(getURLContent(scope, this.URL));
		// scope.getGui().informConsole(""+getURLContent(this.URL));

	}

	@Override
	public Envelope3D computeEnvelope(final IScope scope) {
		return null;
	}

	/**
	 * Method getType()
	 * 
	 * @see msi.gama.util.IContainer#getGamlType()
	 */
	@Override
	public IContainerType<?> getGamlType() {
		return Types.FILE.of(Types.INT, Types.STRING);
	}

}
