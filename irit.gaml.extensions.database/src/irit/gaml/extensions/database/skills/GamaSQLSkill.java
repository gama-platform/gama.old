package irit.gaml.extensions.database.skills;

import org.geotools.data.DataStore;
import msi.gama.database.geosql.GamaSqlConnection;
import msi.gama.precompiler.GamlAnnotations.*;
import msi.gama.precompiler.IConcept;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.skills.Skill;
import msi.gaml.types.IType;

@skill(name = "GAMASQL", concept = { IConcept.DATABASE, IConcept.SKILL })
public class GamaSQLSkill extends Skill {
	// private static final boolean DEBUG = false; // Change DEBUG = false for release version

	@action(name = "testConnection",
		args = { @arg(name = "params", type = IType.MAP, optional = false, doc = @doc("Connection parameters") ) })
	public boolean testConnection(final IScope scope) {

		GamaSqlConnection gamaSqlConn;
		gamaSqlConn = new GamaSqlConnection(scope);
		DataStore dataStore;
		try {
			dataStore = gamaSqlConn.Connect(scope);
		} catch (Exception e) {
			return false;
		}
		gamaSqlConn.setDataStore(dataStore);
		gamaSqlConn.close(scope);
		return true;
	}

	@action(name = "read",
		args = { @arg(name = "params", type = IType.MAP, optional = false, doc = @doc("Connection parameters") ),
			@arg(name = "table",
				type = IType.STRING,
				optional = false,
				doc = @doc("select string with question marks") ),
			@arg(name = "filter",
				type = IType.LIST,
				optional = true,
				doc = @doc("List of values that are used to replace question marks") )
		// ,@arg(name = "transform", type = IType.BOOL, optional = true, doc =
		// @doc("if transform = true then geometry will be tranformed from absolute to gis otherways it will be not transformed. Default value is false "))

	})
	public void read(final IScope scope) throws GamaRuntimeException {
		GamaSqlConnection gamaSqlConn;
		gamaSqlConn = new GamaSqlConnection(scope);
		DataStore dataStore;
		try {
			dataStore = gamaSqlConn.Connect(scope);
			gamaSqlConn.setDataStore(dataStore);
			gamaSqlConn.read(scope);
		} catch (Exception e) {
			throw GamaRuntimeException.error("GamaSqlConnection.fillBuffer; geometry could not be read", scope);
		}
		gamaSqlConn.close(scope);

	}

	@action(name = "SqlObject",
		args = { @arg(name = "params", type = IType.MAP, optional = false, doc = @doc("Connection parameters") ),
			@arg(name = "table",
				type = IType.STRING,
				optional = false,
				doc = @doc("select string with question marks") ),
			@arg(name = "filter", type = IType.STRING, optional = true, doc = @doc("Filter for select") ) })
	public GamaSqlConnection SqlObject(final IScope scope) throws GamaRuntimeException {
		GamaSqlConnection gamaSqlConn;
		gamaSqlConn = new GamaSqlConnection(scope);
		return gamaSqlConn;
	}

}
