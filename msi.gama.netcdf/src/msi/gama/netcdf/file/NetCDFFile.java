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
package msi.gama.netcdf.file;

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

	GamaMap<String, IList> ncdata = GamaMapFactory.create(Types.STRING, Types.LIST);

	
//	private static NetcdfFile dataFile = null;
	static String NCFile ="";
	public NetCDFFile(final IScope scope, final String pathName) throws GamaRuntimeException {
		super(scope, pathName);

		initializeNetCDF(scope);
	}

	public NetCDFFile(final IScope scope, final String pathName, final IContainer p) {
		super(scope, pathName);
		initializeNetCDF(scope);
	}


	
	
	
	
	
	
	
	
    /** 
     * Prints schema (structure) of an existing netCDF file with a
     * specified file name.
     *
     * @param args name of netCDF file to be read.  */
//    public static void main(String[] args) {
//	
//
//	try {
//	    NetcdfFile nc = new NetcdfFile("D:/first.nc"); // open it readonly
//	    System.out.println(nc); // output schema in CDL form (like ncdump)
//	} catch (java.io.IOException e) {
//	    e.printStackTrace();
//	}
//    	try {
//    	    NetcdfFile nc = NetcdfFile.open("D:/first.nc",null); // open it readonly
//    	    
//    	    ListIterator<Variable> vi = nc.getVariables().listIterator();
//    	    while(vi.hasNext()) {
//	    		Variable var = vi.next();
//	    		System.out.println(var.getName() + " ...");
//
//    	    }
//    	} catch (java.io.IOException e) {
//    	    e.printStackTrace();
//    	}
//    }
    
    
    
    
    
    
    
    
    
    
//	  public static void main(String args[]) throws IOException {
//
//	    final int NX = 1200;
//	    final int NY = 1200;
//	    // This is the array we will read.
//	    double[][] dataIn = new double[NX][NY];
//
//	    // Open the file. The ReadOnly parameter tells netCDF we want
//	    // read-only access to the file.
//	    NetcdfFile dataFile = null;
//	    String filename = "D://simple.nc";
//	    // Open the file.
//	    try {
//
//	      dataFile = NetcdfFile.open(filename, null);
//
//	      // Retrieve the variable named "data"
//	      Variable dataVar = dataFile.findVariable("lon_bnds");
//
//	      if (dataVar == null) {
//	        System.out.println("Cant find Variable data");
//	        return;
//	      }
//
//	      // Read all the values from the "data" variable into memory.
//	      int[] shape = dataVar.getShape();
//	      int[] origin = new int[2];
//
//	      ArrayDouble.D2 dataArray;
//
//	      dataArray = (ArrayDouble.D2) dataVar.read(origin, shape);
//	      // Check the values.
//	      assert shape[0] == NX;
//	      assert shape[1] == NY;
//
//	      for (int j = 0; j < shape[0]; j++) {
//	        for (int i = 0; i < shape[1]; i++) {
//	          dataIn[j][i] = dataArray.get(j, i);
//		      System.out.println(""+ dataIn[j][i]);
//
//	        }
//	      }
//
//	      // The file is closed no matter what by putting inside a try/catch block.
//	    } catch (java.io.IOException e) {
//	      e.printStackTrace();
//	      
//	    } catch (InvalidRangeException e) {
//	      e.printStackTrace();
//	      
//	    } finally {
//	      if (dataFile != null)
//	        try {
//	          dataFile.close();
//	        } catch (IOException ioe) {
//	          ioe.printStackTrace();
//	        }
//	    }
//
//	    System.out.println("*** SUCCESS reading example file simple_xy.nc!");
//
//	  }
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
//		initializeNetCDF(scope);
		setBuffer(ncdata);
	}

	@operator(value = "getNetCDFvar", can_be_const = false, category = IOperatorCategory.FILE)
	@doc(value = "Test whether the parameter is the path to an existing file.")
	public static IList getNetCDFvar(final IScope scope, final String varName, final IList offsets) {
		if ( varName == null ) { return GamaListFactory.create(scope, Types.LIST, 0); }
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
							throw GamaRuntimeException.error("RCallerExecutionException " + ioe.getMessage(), scope);
				        }
				    }
		}
		return GamaListFactory.create(scope, Types.NO_TYPE, 0);
	}
	
//writer	
//	public static void main(String args[]) throws Exception {
//
//	    final int NLVL = 2;
//	    final int NLAT = 6;
//	    final int NLON = 12;
//	    final int NREC = 2;
//
//	    final float SAMPLE_PRESSURE = 900.0f;
//	    final float SAMPLE_TEMP = 9.0f;
//	    final float START_LAT = 25.0f;
//	    final float START_LON = -125.0f;
//
//	    // Create the file.
//	    String filename = "D:/pres_temp_4D.nc";
//	    NetcdfFileWriter dataFile = null;
//
//	    try {
//	      // Create new netcdf-3 file with the given filename
//	      dataFile = NetcdfFileWriter.createNew(NetcdfFileWriter.Version.netcdf3, filename);
//
//	      //add dimensions  where time dimension is unlimit
//	      Dimension lvlDim = dataFile.addDimension(null, "level", NLVL);
//	      Dimension latDim = dataFile.addDimension(null, "latitude", NLAT);
//	      Dimension lonDim = dataFile.addDimension(null, "longitude", NLON);
//	      Dimension timeDim = dataFile.addUnlimitedDimension("time");
//
//	      // Define the coordinate variables.
//	      Variable latVar = dataFile.addVariable(null, "latitude", DataType.FLOAT, "latitude");
//	      Variable lonVar = dataFile.addVariable(null, "longitude", DataType.FLOAT, "longitude");
//
//	      // Define units attributes for data variables.
//	      dataFile.addVariableAttribute(latVar, new Attribute("units", "degrees_north"));
//	      dataFile.addVariableAttribute(lonVar, new Attribute("units", "degrees_east"));
//
//	      // Define the netCDF variables for the pressure and temperature
//	      // data.
//	      String dims = "time level latitude longitude";
//	      Variable presVar = dataFile.addVariable(null, "pressure", DataType.FLOAT, dims);
//	      Variable tempVar = dataFile.addVariable(null, "temperature", DataType.FLOAT, dims);
//
//	      // Define units attributes for data variables.
//	      dataFile.addVariableAttribute(presVar, new Attribute("units", "hPa"));
//	      dataFile.addVariableAttribute(tempVar, new Attribute("units", "celsius"));
//
//	      // Create some pretend data. If this wasn't an example program, we
//	      // would have some real data to write for example, model output.
//	      ArrayFloat.D1 lats = new ArrayFloat.D1(latDim.getLength());
//	      ArrayFloat.D1 lons = new ArrayFloat.D1(lonDim.getLength());
//	      int i, j;
//
//	      for (i = 0; i < latDim.getLength(); i++) {
//	        lats.set(i, START_LAT + 5.f * i);
//	      }
//
//	      for (j = 0; j < lonDim.getLength(); j++) {
//	        lons.set(j, START_LON + 5.f * j);
//	      }
//
//	      // Create the pretend data. This will write our surface pressure and
//	      // surface temperature data.
//	      ArrayFloat.D4 dataTemp = new ArrayFloat.D4(NREC, lvlDim.getLength(), latDim.getLength(), lonDim.getLength());
//	      ArrayFloat.D4 dataPres = new ArrayFloat.D4(NREC, lvlDim.getLength(), latDim.getLength(), lonDim.getLength());
//
//	      for (int record = 0; record < NREC; record++) {
//	        i = 0;
//	        for (int lvl = 0; lvl < NLVL; lvl++)
//	          for (int lat = 0; lat < NLAT; lat++)
//	            for (int lon = 0; lon < NLON; lon++) {
//	              dataPres.set(record, lvl, lat, lon, SAMPLE_PRESSURE + i);
//	              dataTemp.set(record, lvl, lat, lon, SAMPLE_TEMP + i++);
//	            }
//	      }
//
//	      //Create the file. At this point the (empty) file will be written to disk
//	      dataFile.create();
//
//	      // A newly created Java integer array to be initialized to zeros.
//	      int[] origin = new int[4];
//
//	      dataFile.write(latVar, lats);
//	      dataFile.write(lonVar, lons);
//	      dataFile.write(presVar, origin, dataPres);
//	      dataFile.write(tempVar, origin, dataTemp);
//
//
//	    } catch (IOException e) {
//	      e.printStackTrace(System.err);
//
//	    } catch (InvalidRangeException e) {
//	      e.printStackTrace(System.err);
//
//	    } finally {
//	      if (dataFile != null)
//	        try {
//	          dataFile.close();
//	        } catch (IOException ioe) {
//	          ioe.printStackTrace();
//	        }
//	    }
//	    System.out.println("*** SUCCESS writing example file " + filename);
//	  }

	
	
	
	
	
	
	
	
	
//reader	
//	public static void main(String args[]) {
//	    final int NLVL = 2;
//	    final int NLAT = 6;
//	    final int NLON = 12;
//
//	    // These are used to construct some example data.
//	    final float SAMPLE_PRESSURE = 900.0f;
//	    final float SAMPLE_TEMP = 9.0f;
//	    final float START_LAT = 25.0f;
//	    final float START_LON = -125.0f;
//
//	    // Open the file.
//	    String filename = "D:/pres_temp_4D.nc";
//	    NetcdfFile dataFile = null;
//	    try {
//
//	      dataFile = NetcdfFile.open(filename, null);
//
//	      // Get the latitude and longitude Variables.
//	      Variable latVar = dataFile.findVariable("latitude");
//	      if (latVar == null) {
//	        System.out.println("Cant find Variable latitude");
//	        return;
//	      }
//
//	      Variable lonVar = dataFile.findVariable("longitude");
//	      if (lonVar == null) {
//	        System.out.println("Cant find Variable longitude");
//	        return;
//	      }
//
//	      // Get the lat/lon data from the file.
//	      ArrayFloat.D1 latArray;
//	      ArrayFloat.D1 lonArray;
//
//	      latArray = (ArrayFloat.D1) latVar.read();
//	      lonArray = (ArrayFloat.D1) lonVar.read();
//
//
//	      // Check the coordinate variable data.
//	      for (int lat = 0; lat < NLAT; lat++)
//	        if (latArray.get(lat) != START_LAT + 5. * lat)
//	          System.err.println("ERROR incorrect value in variable latitude");
//
//	      for (int lon = 0; lon < NLON; lon++)
//	        if (lonArray.get(lon) != START_LON + 5. * lon)
//	          System.err.println("ERROR incorrect value in variable longtitude");
//
//	      // Get the pressure and temperature variables.
//	      Variable presVar = dataFile.findVariable("pressure");
//	      if (presVar == null) {
//	        System.out.println("Cant find Variable pressure");
//	        return;
//	      }
//
//	      Variable tempVar = dataFile.findVariable("temperature");
//	      if (lonVar == null) {
//	        System.out.println("Cant find Variable temperature");
//	        return;
//	      }
//
//	      int[] shape = presVar.getShape();
//	      int recLen = shape[0]; // number of times
//
//	      int[] origin = new int[4];
//	      shape[0] = 1; // only one rec per read
//
//	      // loop over the rec dimension
//	      for (int rec = 0; rec < recLen; rec++) {
//	        origin[0] = rec;  // read this index
//
//	        // read 3D array for that index
//	        ArrayFloat.D3 presArray, tempArray;
//
//	        presArray = (ArrayFloat.D3) (presVar.read(origin, shape).reduce());
//	        tempArray = (ArrayFloat.D3) (tempVar.read(origin, shape).reduce());
//
//
//	        // now checking the value
//	        int count = 0;
//	        for (int lvl = 0; lvl < NLVL; lvl++)
//	          for (int lat = 0; lat < NLAT; lat++)
//	            for (int lon = 0; lon < NLON; lon++) {
//	              if ((presArray.get(lvl, lat, lon) != SAMPLE_PRESSURE + count) ||
//	                      (tempArray.get(lvl, lat, lon) != SAMPLE_TEMP + count))
//	                System.err.println("ERROR incorrect value in variable pressure or temperature");
//	              count++;
//	            }
//	      }
//
//	      // The file is closed no matter what by putting inside a try/catch block.
//	    } catch (java.io.IOException e) {
//	      e.printStackTrace();
//	      return;
//
//	    } catch (InvalidRangeException e) {
//	      e.printStackTrace();
//	      return;
//
//	    } finally {
//	      if (dataFile != null)
//	        try {
//	          dataFile.close();
//	        } catch (IOException ioe) {
//	          ioe.printStackTrace();
//	        }
//	    }
//	    System.out.println("*** SUCCESS reading example file " + filename);
//	  }
//	
	
	public void initializeNetCDF(final IScope scope) {
		NCFile = getPath();
		NetcdfFile dataFile = null;
		 try {

			 dataFile = NetcdfFile.open(NCFile, null);

		      // Retrieve the variable named "data"
			ListIterator<Variable> vi=dataFile.getVariables().listIterator();
//		      Variable dataVar = dataFile.findVariable("lon");
//
//		      if (dataVar == null) {
//		        System.out.println("Cant find Variable data");
//		        return;
//		      }

		      // Read all the values from the "data" variable into memory.
//		      int[] shape = dataVar.getShape();
//		      int[] origin = new int[2];
//
//		      ArrayDouble.D1 dataArray;
//
//		      dataArray = (ArrayDouble.D1) dataVar.read(origin, shape);
			
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

//		      // Check the values.
//		      assert shape[0] == 1200;
//		      assert shape[1] == 1200;
//		      double[][] dataIn = new double[1200][1200];
//
//				String fullPath = FileUtils.constructAbsoluteFilePath(scope, NCFile, true);
//
//
//		      for (int j = 0; j < shape[0]; j++) {
//		        for (int i = 0; i < shape[1]; i++) {
//		          dataIn[j][i] = dataArray.get(j, i);
//			      System.out.println(""+ dataIn[j][i]);
//
//
//
//
//
//
//						String[] results = null;
////						results = caller.getParser().getAsStringArray(name);
//
//
//
//
////						result.put(name, GamaListFactory.create(scope, Types.NO_TYPE, results));
//
//
//
//
//
//
//		        }
//		      }

		      // The file is closed no matter what by putting inside a try/catch block.
		    } catch (java.io.IOException e) {
				throw GamaRuntimeException.error("RCallerExecutionException " + e.getMessage(), scope);
		      
		    } finally {
		      if (dataFile != null)
		        try {
		          dataFile.close();
		        } catch (IOException ioe) {
					throw GamaRuntimeException.error("RCallerExecutionException " + ioe.getMessage(), scope);
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
