/*********************************************************************************************
 *
 *
 * 'MDXSkill.java', in plugin 'irit.gaml.extensions.database', is part of the source code of the GAMA modeling and
 * simulation platform. (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 *
 *
 **********************************************************************************************/
package irit.gaml.extensions.database.skills;

import org.olap4j.OlapConnection;

import msi.gama.database.mdx.MdxConnection;
import msi.gama.database.mdx.MdxUtils;
import msi.gama.precompiler.GamlAnnotations.action;
import msi.gama.precompiler.GamlAnnotations.arg;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.skill;
import msi.gama.precompiler.IConcept;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.IList;
import msi.gaml.skills.Skill;
import msi.gaml.types.IType;

/*
 * @Author TRUONG Minh Thai
 *
 * @Supervisors: Christophe Sibertin-BLANC Fredric AMBLARD Benoit GAUDOU
 *
 * Description: Define MultiDimensional eXpressions features
 *
 * created date: 04-Jul-2013 Modified: 08-Jul-2013: - correct error on testConnection method - add question mark and
 * values to select method
 *
 * Last Modified: 08-Jul-2013
 */
@skill (
		name = "MDXSKILL",
		concept = { IConcept.DATABASE, IConcept.SKILL })
@SuppressWarnings ({ "rawtypes", "unchecked" })
@doc ("This skill allows agents to be provided with actions and attributes in order to connect to MDX databases")
public class MDXSkill extends Skill {

	// Get current time of system
	@action (
			name = "timeStamp")
	public Long timeStamp(final IScope scope) throws GamaRuntimeException {
		final Long timeStamp = System.currentTimeMillis();
		return timeStamp;
	}

	/*
	 * Make a connection to BDMS
	 *
	 * @syntax: do action: connectDB { arg params value:[ "olaptype":"SSAS/XMLA" //"MONDRIAN"/"MONDRIAN/XMLA"
	 * "dbtype":"SQLSERVER", //MySQL/postgres/sqlite "url":"host address", "port":"port number", "database":
	 * "database name", "user": "user name", "passwd": "password" ]; }
	 */
	@action (
			name = "testConnection",
			args = { @arg (
					name = "params",
					type = IType.MAP,
					optional = false,
					doc = @doc ("Connection parameters")) })
	public boolean testConnection(final IScope scope) {
		final java.util.Map params = (java.util.Map) scope.getArg("params", IType.MAP);
		try (final OlapConnection oConn = MdxUtils.createConnectionObject(scope, params).connectMDB(scope)) {
			oConn.getCatalog();
		} catch (final Exception e) {
			return false;
		}
		return true;
	}

	@action (
			name = "select",
			args = { @arg (
					name = "params",
					type = IType.MAP,
					optional = false,
					doc = @doc ("Connection parameters")),
					@arg (
							name = "onColumns",
							type = IType.STRING,
							optional = false,
							doc = @doc ("select string with question marks")),
					@arg (
							name = "onRows",
							type = IType.LIST,
							optional = false,
							doc = @doc ("List of values that are used to replace question marks")),
					@arg (
							name = "from",
							type = IType.LIST,
							optional = false,
							doc = @doc ("List of values that are used to replace question marks")),
					@arg (
							name = "where",
							type = IType.LIST,
							optional = true,
							doc = @doc ("List of values that are used to replace question marks")),
					@arg (
							name = "values",
							type = IType.LIST,
							optional = true,
							doc = @doc ("List of values that are used to replace question marks")),

			})
	public IList<Object> select_QM(final IScope scope) throws GamaRuntimeException {

		// ------------------------------------------------------------------------------------------
		final java.util.Map params = (java.util.Map) scope.getArg("params", IType.MAP);
		final String onRowStr = (String) scope.getArg("onRows", IType.STRING);
		final String onColumnStr = (String) scope.getArg("onColumns", IType.STRING);
		final String fromStr = (String) scope.getArg("from", IType.STRING);
		final String whereStr = scope.hasArg("where") ? (String) scope.getArg("where", IType.STRING) : null;
		final IList<Object> values = scope.hasArg("values") ? (IList<Object>) scope.getArg("values", IType.LIST) : null;
		String selectStr = "SELECT " + onColumnStr + " ON COLUMNS , " + onRowStr + " ON ROWS FROM " + fromStr;

		MdxConnection mdxConn;
		IList<Object> repRequest;
		try {
			if (whereStr != null) {
				selectStr = selectStr + " WHERE " + whereStr;
			}
			mdxConn = MdxUtils.createConnectionObject(scope, params);
			if (values != null) {
				repRequest = mdxConn.selectMDB(scope, selectStr, values);
			} else {
				repRequest = mdxConn.selectMDB(scope, selectStr);
			}

		} catch (final Exception e) {
			e.printStackTrace();
			throw GamaRuntimeException.error("MDXSkill.select_QM: " + e.toString(), scope);
		}

		// ------------------------------------------------------------------------------------------
		return repRequest;
	}
}
