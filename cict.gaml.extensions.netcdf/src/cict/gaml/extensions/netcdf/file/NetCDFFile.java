package cict.gaml.extensions.netcdf.file;

import java.io.*;
import java.util.*;
import msi.gama.precompiler.IOperatorCategory;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.file;
import msi.gama.precompiler.GamlAnnotations.operator;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.*;
import msi.gama.util.file.GamaFile;
import msi.gaml.operators.Cast;
import msi.gaml.types.*;

import ucar.ma2.*;
import ucar.nc2.NetcdfFile;
import ucar.nc2.Variable;
import com.vividsolutions.jts.geom.Envelope;

@file(name = "netcdf",
	extensions = { "nc" },
	buffer_type = IType.MAP,
	buffer_content = IType.LIST,
	buffer_index = IType.STRING)
public class NetCDFFile extends GamaFile<GamaMap<String, IList>, IList, String, IList> {

	final GamaMap<String, IList> ncdata = GamaMapFactory.create(Types.STRING, Types.LIST);

	
	public NetCDFFile(final IScope scope, final String pathName) throws GamaRuntimeException {
		super(scope, pathName);
	}

	public NetCDFFile(final IScope scope, final String pathName, final IContainer p) {
		super(scope, pathName);
	}


	
	
	
	
	
	
	
	
	@Override
	public String _stringValue(final IScope scope) throws GamaRuntimeException {
		getContents(scope);
		StringBuilder sb = new StringBuilder(getBuffer().length(scope) * 200);
		for ( IList s : getBuffer().iterable(scope) ) {
			sb.append(s).append("\n"); // TODO Factorize the different calls to "new line" ...
		}
		sb.setLength(sb.length() - 1);
		return sb.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.util.GamaFile#fillBuffer()
	 */
	@Override
	protected void fillBuffer(final IScope scope) throws GamaRuntimeException {
		if ( getBuffer() != null ) { return; }
		initializeNetCDF(scope);
		setBuffer(ncdata);
	}

	@operator(value = "reduce_dimension", can_be_const = false, category = IOperatorCategory.LIST)
	@doc(value = "general operator to manipylate multidimension netcdf data.")
	public static IList reduce_dimension(final IScope scope, final String varName, final IList offsets) {
		final String NCFile = "";
		if ( varName == null ) { return GamaListFactory.create(scope, Types.NO_TYPE, 0); }
		if ( scope == null ) {
			return GamaListFactory.create(scope, Types.NO_TYPE, 0);
		} else {
			NetcdfFile dataFile = null;

			try {

				 dataFile = NetcdfFile.open(NCFile, null);

				Variable v = dataFile.findVariable(varName);
				
				Array a = v.read(); 
				int sum=0; int prev=0;
				for (int index = offsets.length(scope) -1; index >-1 ; index--) {
					int idx=Cast.asInt(scope, offsets.get(index));
					sum += idx>0?(prev + idx):idx;
					prev+=Cast.asInt(scope, a.getShape()[index]);
				}
				
				return GamaListFactory.create(scope, Types.NO_TYPE, a.getObject(sum));
			} catch (IOException e) {
				e.printStackTrace();
			}finally {
			      if (dataFile != null)
				        try {
				          dataFile.close();
				        } catch (IOException ioe) {
							throw GamaRuntimeException.error("NetCDFExecutionException " + ioe.getMessage(), scope);
				        }
				    }
		}
		return GamaListFactory.create(scope, Types.NO_TYPE, 0);
	}
	
	public void initializeNetCDF(final IScope scope) {
		final String NCFile = getPath();
		NetcdfFile dataFile = null;
		 try {

			 dataFile = NetcdfFile.open(NCFile, null);

		      // Retrieve the variable named "data"
			ListIterator<Variable> vi=dataFile.getVariables().listIterator();

			  while (vi.hasNext()){
				  Variable v=vi.next();
					  System.out.println(""+ v.getFullName()+"    "+v.getShape().length);
					  for(int i =0 ; i<v.getShape().length; i++){
						  
					  }
					  Array a=v.read();
					  ArrayList res=new ArrayList<>();
					  while(a.hasNext()){res.add(a.next());}
					ncdata.put(v.getFullName(), GamaListFactory.create(scope, Types.NO_TYPE, res));

			  }



		      // The file is closed no matter what by putting inside a try/catch block.
		    } catch (java.io.IOException e) {
				throw GamaRuntimeException.error("NetCDFExecutionException " + e.getMessage(), scope);
		      
		    } finally {
		      if (dataFile != null)
		        try {
		          dataFile.close();
		        } catch (IOException ioe) {
					throw GamaRuntimeException.error("NrtCDFExecutionException " + ioe.getMessage(), scope);
		        }
		    }

	    System.out.println("*** SUCCESS reading example file simple_xy.nc!");
	    




	}


	private static String computeVariable(final String string) {
		String[] tokens = string.split("<-");
		return tokens[0];
	}

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
