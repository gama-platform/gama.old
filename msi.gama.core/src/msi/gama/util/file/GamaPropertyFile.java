/*
 * GAMA - V1.4 http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2012
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2012
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen (Batch, GeoTools & JTS), 2009-2012
 * - Beno”t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.util.file;

import java.io.*;
import java.util.Properties;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaMap;
import msi.gaml.operators.Files;
import msi.gaml.types.GamaFileType;
import com.vividsolutions.jts.geom.Envelope;

public class GamaPropertyFile extends GamaFile<String, String> {

	public GamaPropertyFile(final IScope scope, final String pathName) throws GamaRuntimeException {
		super(scope, pathName);
	}

	@Override
	protected void checkValidity() throws GamaRuntimeException {
		super.checkValidity();
		if ( !GamaFileType.isProperties(getFile().getName()) ) { throw GamaRuntimeException.error("The extension " + this.getExtension() + " is not recognized for properties files"); }
	}

	@Override
	protected IGamaFile _copy(IScope scope) {
		// TODO A faire
		return null;
	}

	//
	// @Override
	// protected boolean _isFixedLength() {
	// return false;
	// }

	@Override
	public String getKeyword() {
		return Files.PROPERTIES;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.util.GamaFile#fillBuffer()
	 */
	@Override
	protected void fillBuffer(IScope scope) throws GamaRuntimeException {
		Properties p = new Properties();
		GamaMap m = new GamaMap();
		try {
			p.load(new FileReader(getFile()));
		} catch (FileNotFoundException e) {
			throw GamaRuntimeException.create(e);
		} catch (IOException e) {
			throw GamaRuntimeException.create(e);
		}
		m.putAll(p);
		buffer = m;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.util.GamaFile#flushBuffer()
	 */
	@Override
	protected void flushBuffer() throws GamaRuntimeException {
		// TODO A faire

	}

	@Override
	public Envelope computeEnvelope(final IScope scope) {
		// TODO Probably possible to get some information there
		return null;
	}

}
