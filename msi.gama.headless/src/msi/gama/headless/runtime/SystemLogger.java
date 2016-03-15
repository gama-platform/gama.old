package msi.gama.headless.runtime;

import java.io.OutputStream;
import java.io.PrintStream;


public class SystemLogger extends PrintStream {
	private static SystemLogger display;
	private static SystemLogger error;
	private static PrintStream oldOutStream = null;
	private static PrintStream oldErrStream = null;
	
	
	public static void removeDisplay()
	{
	 	if(display==null)
	 	{
			display = new SystemLogger(System.out);
			error = new SystemLogger(System.err);
			oldOutStream = System.out;
			oldErrStream = System.err;
	 	}
	 	System.setOut(display);
	 	System.setErr(error);
	}
	
	public static void activeDisplay()
	{
		
		if(display!=null)
		{
			System.setOut(oldOutStream);
			System.setErr(oldErrStream);
			oldOutStream = null;
			display = null;
		}
	}
	
	static void forceDisplay(String p)
	{
		display.forcePrint(p);
	}
	
	private void forcePrint(String p)
	{
		super.print(p);
	}
	public SystemLogger(OutputStream out)
	{
	        super(out, true);
	}
	@Override
	public void print(String s)
	 {//do what ever you like
	  super.print("");
	 }
	
	@Override
	public void println(String s)
	 {//do what ever you like
	  super.print("");
	 }
	
	
}

