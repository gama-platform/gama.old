package irit.gaml.skills;

import java.sql.Connection;

import msi.gama.common.util.GuiUtils;
import msi.gama.database.mdx.MdxConnection;
import msi.gama.database.mdx.MdxUtils;
import msi.gama.database.sql.SqlConnection;
import msi.gama.database.sql.SqlUtils;
import msi.gama.precompiler.GamlAnnotations.action;
import msi.gama.precompiler.GamlAnnotations.args;
import msi.gama.precompiler.GamlAnnotations.skill;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.skills.Skill;
import msi.gaml.types.IType;

/*
 * @Author 
 * 	TRUONG Minh Thai
 * 
 * @Supervisors:
 * 	Christophe Sibertin-BLANC
 * 	Fredric AMBLARD
 * 	Benoit GAUDOU
 * 
 * Description: Define MultiDimensional eXpressions features
 * 
 * created date: 04-Jul-2013
 * Modified:

 *   
 * Last Modified: 29-Apr-2013
 */
@skill(name = "MDXSKILL")
public class MDXSkill extends Skill{

	private static final boolean DEBUG = false; // Change DEBUG = false for release version

	/*
	 * for test only
	 */
	@action(name = "helloWorld")
	@args(names = {})
	public Object helloWorld(final IScope scope) throws GamaRuntimeException {
		GuiUtils.informConsole("Hello World");
		return null;
	}

	// Get current time of system
	// added from MaeliaSkill
	@action(name = "timeStamp")
	@args(names = {})
	public Long timeStamp(final IScope scope) throws GamaRuntimeException {
		Long timeStamp = System.currentTimeMillis();
		return timeStamp;
	}

	/*
	 * Make a connection to BDMS
	 * 
	 * @syntax: do action: connectDB {
	 * arg params value:[
	 * "olaptype":"MSAS" //"MONDRIAN"/"MONDRIAN/XMLA"
	 * "dbtype":"SQLSERVER", //MySQL/sqlserver/sqlite
	 * "url":"host address",
	 * "port":"port number",
	 * "database":"database name",
	 * "user": "user name",
	 * "passwd": "password"
	 * ];
	 * }
	 */
	@action(name = "testConnection")
	@args(names = { "params" })
	public boolean testConnection(final IScope scope)  {
		MdxConnection mdxConn;
		java.util.Map params = (java.util.Map) scope.getArg("params", IType.MAP);
		try {
			mdxConn = MdxUtils.createConnectionObject(scope, params);
			Connection conn=mdxConn.connectMDB();
			conn.close();
		} catch (Exception e) {
			// throw new GamaRuntimeException("SQLSkill.connectDB: " + e.toString());
			return false;
		} 
		return true;
	}

}
