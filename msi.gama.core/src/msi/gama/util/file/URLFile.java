/*********************************************************************************************
 *
 *
 * 'GamaTextFile.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gama.util.file;

import java.io.*;
import java.net.*;
import com.vividsolutions.jts.geom.Envelope;
import msi.gama.precompiler.GamlAnnotations.file;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.*;
import msi.gaml.types.*;

@file(name = "URL", extensions = { "txt" }, buffer_type = IType.LIST, buffer_content = IType.STRING)
public class URLFile extends GamaFile<IList<String>, String, Integer, String> {

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
		StringBuilder sb = new StringBuilder(getBuffer().length(scope) * 200);
		for ( String s : getBuffer().iterable(scope) ) {
			sb.append(s).append("\n"); // TODO Factorize the different calls to "new line" ...
		}
		sb.setLength(sb.length() - 1);
		return sb.toString();
	}

	@Override
	public IList<String> getAttributes(final IScope scope) {
		// TODO Dont know what to return
		return GamaListFactory.EMPTY_LIST;
	}

	public IList<String> getURLContent(final String u_str) {
		URL url;

		final IList<String> allLines = GamaListFactory.create(Types.STRING);
		try {
			// get URL content
			url = new URL(u_str);
			URLConnection conn = url.openConnection();

			// open the stream and put it into BufferedReader
			BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

			String inputLine;

			// save to this filename
			// String fileName = "/users/mkyong/test.html";
			File file = new File(this.getPath());

			// if (!file.exists()) {
			// file.createNewFile();
			// }

			// use FileWriter to write file
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);

			while ((inputLine = br.readLine()) != null) {
				bw.write(inputLine + "\n");

				allLines.add(inputLine);
			}

			bw.close();
			br.close();

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
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
		if ( getBuffer() != null ) { return; }

		setBuffer(getURLContent(this.URL));
		// GuiUtils.informConsole(""+getURLContent(this.URL));

	}

	// private static String computeVariable(final String string) {
	// String[] tokens = string.split("<-");
	// return tokens[0];
	// }

	/*
	 * (non-Javadoc)
	 *
	 * @see msi.gama.util.GamaFile#flushBuffer()
	 */
	@Override
	protected void flushBuffer() throws GamaRuntimeException {
		// TODO A faire.

	}

	@Override
	public Envelope computeEnvelope(final IScope scope) {
		return null;
	}

	/**
	 * Method getType()
	 * @see msi.gama.util.IContainer#getType()
	 */
	@Override
	public IContainerType getType() {
		return Types.FILE.of(Types.INT, Types.STRING);
	}

}
