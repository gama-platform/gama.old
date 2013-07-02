package msi.gama.database.mdx;

import java.util.Map;

import msi.gama.common.util.GuiUtils;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;

public class MdxUtils {
	private static final boolean DEBUG=false;
	public static MdxConnection createConnectionObject(final IScope scope,final Map<String, Object> params) throws GamaRuntimeException 
	{
		String dbtype = (String) params.get("dbtype");
		String host = (String) params.get("host");
		String port = (String) params.get("port");
		String database = (String) params.get("database");
		String user = (String) params.get("user");
		String passwd = (String) params.get("passwd");
		if (DEBUG){
			GuiUtils.debug("MdxlUtils.createConnectionObject:"+dbtype+" - "+host+" - "+ port+" - "+database+" - ");
		}
		MdxConnection mdxConn;
		// create connection
		if ( dbtype.equalsIgnoreCase(MdxConnection.MSAS) ) {
			
			mdxConn = new MSASConnection(dbtype, host, port, database, user, passwd);
		}else if ( dbtype.equalsIgnoreCase(MdxConnection.MONDRIAN)) {
			mdxConn = new MondrianConnection(dbtype, host, port, database, user, passwd);
		}else if ( dbtype.equalsIgnoreCase(MdxConnection.MONDRIANXMLA)) {
			mdxConn = new MondrianXmlaConnection(dbtype, host, port, database, user, passwd);
		}else{
			throw GamaRuntimeException.error("GAMA does not support: " + dbtype);
		}
		if (DEBUG){
			GuiUtils.debug("MdxUtils.createConnection:"+mdxConn.toString());
		}

		return mdxConn;
	}
	
	public static MdxConnection createConnectionObject(final Map<String, Object> params) throws GamaRuntimeException 
	{
		String dbtype = (String) params.get("dbtype");
		String host = (String) params.get("host");
		String port = (String) params.get("port");
		String database = (String) params.get("database");
		String user = (String) params.get("user");
		String passwd = (String) params.get("passwd");
		if (DEBUG){
			GuiUtils.debug("MdxlUtils.createConnectionObject:"+dbtype+" - "+host+" - "+ port+" - "+database+" - ");
		}
		MdxConnection mdxConn;
		// create connection
		if ( dbtype.equalsIgnoreCase(MdxConnection.MSAS) ) {
			
			mdxConn = new MSASConnection(dbtype, host, port, database, user, passwd);
		}else if ( dbtype.equalsIgnoreCase(MdxConnection.MONDRIAN)) {
			mdxConn = new MondrianConnection(dbtype, host, port, database, user, passwd);
		}else if ( dbtype.equalsIgnoreCase(MdxConnection.MONDRIANXMLA)) {
			mdxConn = new MondrianXmlaConnection(dbtype, host, port, database, user, passwd);
		}else{
			throw GamaRuntimeException.error("GAMA does not support: " + dbtype);
		}
		if (DEBUG){
			GuiUtils.debug("MdxUtils.createConnection:"+mdxConn.toString());
		}
		return mdxConn;
	}
	
	public static MdxConnection createConnectionObject(String dbtype,String host,String port,String database,String user,String passwd ) throws GamaRuntimeException 
	{
		if (DEBUG){
			GuiUtils.debug("MdxlUtils.createConnectionObject:"+dbtype+" - "+host+" - "+ port+" - "+database+" - ");
		}
		MdxConnection mdxConn;
		// create connection
		if ( dbtype.equalsIgnoreCase(MdxConnection.MSAS) ) {
			
			mdxConn = new MSASConnection(dbtype, host, port, database, user, passwd);
		}else if ( dbtype.equalsIgnoreCase(MdxConnection.MONDRIAN)) {
			mdxConn = new MondrianConnection(dbtype, host, port, database, user, passwd);
		}else if ( dbtype.equalsIgnoreCase(MdxConnection.MONDRIANXMLA)) {
			mdxConn = new MondrianXmlaConnection(dbtype, host, port, database, user, passwd);
		}else  {
			throw GamaRuntimeException.error("GAMA does not support: " + dbtype);
		}
		if (DEBUG){
			GuiUtils.debug("MdxUtils.createConnection:"+mdxConn.toString());
		}
		return mdxConn;
	} 
	public static MdxConnection createConnectionObject(String dbtype,String host,String port,
			String database,String user,String passwd, String catalog ) throws GamaRuntimeException 
	{
		if (DEBUG){
			GuiUtils.debug("MdxlUtils.createConnectionObject:"+dbtype+" - "+host+" - "+ port+" - "+database+" - ");
		}
		MdxConnection mdxConn;
		// create connection
		if ( dbtype.equalsIgnoreCase(MdxConnection.MSAS) ) {
			
			mdxConn = new MSASConnection(dbtype, host, port, database, user, passwd);
		}else if ( dbtype.equalsIgnoreCase(MdxConnection.MONDRIAN)) {
			mdxConn = new MondrianConnection(dbtype, host, port, database, user, passwd,catalog);
		}else if ( dbtype.equalsIgnoreCase(MdxConnection.MONDRIANXMLA)) {
			mdxConn = new MondrianXmlaConnection(dbtype, host, port, database, user, passwd);
		}else {
			throw GamaRuntimeException.error("GAMA does not support: " + dbtype);
		}
		if (DEBUG){
			GuiUtils.debug("MdxUtils.createConnection:"+mdxConn.toString());
		}
		return mdxConn;
	}

}
