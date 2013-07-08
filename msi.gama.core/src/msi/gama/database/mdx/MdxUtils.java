package msi.gama.database.mdx;

import java.util.Map;

import msi.gama.common.util.GuiUtils;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;

public class MdxUtils {
	private static final boolean DEBUG=false;
	public static MdxConnection createConnectionObject(final IScope scope,final Map<String, Object> params) throws GamaRuntimeException 
	{
		String olaptype = (String) params.get("olaptype");
		String dbtype = (String) params.get("dbtype");
		String host = (String) params.get("host");
		String port = (String) params.get("port");
		String database = (String) params.get("database");
		String catalog = (String) params.get("catalog");
		String user = (String) params.get("user");
		String passwd = (String) params.get("passwd");
		if (DEBUG){
			GuiUtils.debug("MdxlUtils.createConnectionObject:"+olaptype+" - "+dbtype+" - "+host+" - "+ port+" - "+database+" - "+catalog+" - "+user+" - "+passwd);
		}
		MdxConnection mdxConn;
		// create connection
		if ( olaptype.equalsIgnoreCase(MdxConnection.MSAS) ) {
			
			mdxConn = new MSASConnection(olaptype, host, port, database, user, passwd);
		}else if ( olaptype.equalsIgnoreCase(MdxConnection.MONDRIAN)) {
			String fullPath =
					scope.getSimulationScope().getModel().getRelativeFilePath(catalog, true);
			
			//catalog=fullPath.replace('\\', '/');
			catalog=fullPath;
			if (DEBUG){
				GuiUtils.debug("MdxlUtils.createConnectionObject.catalog.Mondrian:"+olaptype+" - "+dbtype+" - "+" - "+host+" - "
						+ port+" - "+database+" - "+catalog+" - "+user+" - "+passwd+" - ");
			}
			
			mdxConn = new MondrianConnection(olaptype, dbtype,host, port, database,catalog, user, passwd);
			if (DEBUG){
				GuiUtils.debug("MdxlUtils.createConnectionObject.connectionObject.Mondrian.Object:" + mdxConn.toString());
			}

		}else if ( olaptype.equalsIgnoreCase(MdxConnection.MONDRIANXMLA)) {
			if (DEBUG){
				GuiUtils.debug("MdxlUtils.createConnectionObject.catalog.MondrianXMLA:"+olaptype+" - "+dbtype+" - "+" - "+host+" - "
						+ port+" - "+database+" - "+catalog+" - "+user+" - "+passwd+" - ");
			}

			mdxConn = new MondrianXmlaConnection(olaptype, dbtype, host, port, database, catalog, user, passwd);
			if (DEBUG){
				GuiUtils.debug("MdxlUtils.createConnectionObject.connectionObject.MondrianXMLA.Object:" + mdxConn.toString());
			}
			
		}else{
			throw GamaRuntimeException.error("GAMA does not support: " + olaptype);
		}
		if (DEBUG){
			GuiUtils.debug("MdxUtils.createConnection:"+mdxConn.toString());
		}
		return mdxConn;
	}
	
	public static MdxConnection createConnectionObject(final Map<String, Object> params) throws GamaRuntimeException 
	{
		String olaptype = (String) params.get("olaptype");
		String dbtype = (String) params.get("dbtype");
		String host = (String) params.get("host");
		String port = (String) params.get("port");
		String database = (String) params.get("database");
		String catalog = (String) params.get("catalog");
		String user = (String) params.get("user");
		String passwd = (String) params.get("passwd");
		if (DEBUG){
			GuiUtils.debug("MdxlUtils.createConnectionObject:"+olaptype+" - "+dbtype+" - "+" - "+host+" - "+ port+" - "
					+database+" - "+catalog+" - "+user+" - "+passwd);
		}
		MdxConnection mdxConn;
		// create connection
		if ( olaptype.equalsIgnoreCase(MdxConnection.MSAS) ) {
			
			mdxConn = new MSASConnection(olaptype, host, port, database, user, passwd);
		}else if ( olaptype.equalsIgnoreCase(MdxConnection.MONDRIAN)) {
			mdxConn = new MondrianConnection(olaptype, dbtype,host, port, database,catalog, user, passwd);
		}else if ( dbtype.equalsIgnoreCase(MdxConnection.MONDRIANXMLA)) {
			mdxConn = new MondrianXmlaConnection(olaptype, host, port, database,catalog, user, passwd);
		}else{
			throw GamaRuntimeException.error("GAMA does not support: " + olaptype);
		}
		if (DEBUG){
			GuiUtils.debug("MdxUtils.createConnection:"+mdxConn.toString());
		}
		return mdxConn;
	}
	
	public static MdxConnection createConnectionObject(String olaptype,String host,String port,String database,String user,String passwd ) throws GamaRuntimeException 
	{
		if (DEBUG){
			GuiUtils.debug("MdxlUtils.createConnectionObject:"+olaptype+" - "+host+" - "+ port+" - "+database+" - ");
		}
		MdxConnection mdxConn;
		// create connection
		if ( olaptype.equalsIgnoreCase(MdxConnection.MSAS) ) {
			
			mdxConn = new MSASConnection(olaptype, host, port, database, user, passwd);
		}else if ( olaptype.equalsIgnoreCase(MdxConnection.MONDRIAN)) {
			mdxConn = new MondrianConnection(olaptype, host, port, database, user, passwd);
		}else if ( olaptype.equalsIgnoreCase(MdxConnection.MONDRIANXMLA)) {
			mdxConn = new MondrianXmlaConnection(olaptype, host, port, database, user, passwd);
		}else  {
			throw GamaRuntimeException.error("GAMA does not support: " + olaptype);
		}
		if (DEBUG){
			GuiUtils.debug("MdxUtils.createConnection:"+mdxConn.toString());
		}
		return mdxConn;
	} 
	public static MdxConnection createConnectionObject(String olaptype,String host,String port,
			String database, String catalog, String user,String passwd ) throws GamaRuntimeException 
	{
		if (DEBUG){
			GuiUtils.debug("MdxlUtils.createConnectionObject:"+olaptype+" - "+host+" - "+ port+" - "
					+database+" - "+catalog+" - "+user+" - "+passwd);
		}
		MdxConnection mdxConn;
		// create connection
		if ( olaptype.equalsIgnoreCase(MdxConnection.MSAS) ) {
			
			mdxConn = new MSASConnection(olaptype, host, port, database, user, passwd);
		}else if ( olaptype.equalsIgnoreCase(MdxConnection.MONDRIAN)) {
			mdxConn = new MondrianConnection(olaptype, host, port, database, user, passwd,catalog);
		}else if ( olaptype.equalsIgnoreCase(MdxConnection.MONDRIANXMLA)) {
			mdxConn = new MondrianXmlaConnection(olaptype, host, port, database, catalog, user, passwd);
		}else {
			throw GamaRuntimeException.error("GAMA does not support: " + olaptype);
		}
		if (DEBUG){
			GuiUtils.debug("MdxUtils.createConnection:"+mdxConn.toString());
		}
		return mdxConn;
	}
	
	public static MdxConnection createConnectionObject(String olaptype,String dbtype, String host,String port,
			String database , String catalog,String user,String passwd) throws GamaRuntimeException 
	{
		if (DEBUG){
			GuiUtils.debug("MdxlUtils.createConnectionObject:"+olaptype+" - "+host+" - "+ port+" - "
					+database+" - "+catalog+" - "+user+" - "+passwd);
		}
		MdxConnection mdxConn;
		// create connection
		if ( olaptype.equalsIgnoreCase(MdxConnection.MSAS) ) {
			
			mdxConn = new MSASConnection(olaptype, host, port, database, user, passwd);
		}else if ( olaptype.equalsIgnoreCase(MdxConnection.MONDRIAN)) {
			mdxConn = new MondrianConnection(olaptype, dbtype,host, port, database, catalog, user, passwd);
		}else if ( olaptype.equalsIgnoreCase(MdxConnection.MONDRIANXMLA)) {
			mdxConn = new MondrianXmlaConnection(olaptype, host, port, database,catalog, user, passwd);
		}else {
			throw GamaRuntimeException.error("GAMA does not support: " + dbtype);
		}
		if (DEBUG){
			GuiUtils.debug("MdxUtils.createConnection:"+mdxConn.toString());
		}
		return mdxConn;
	}

}
