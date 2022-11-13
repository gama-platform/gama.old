package ummisco.gama.network.websocket;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

import org.java_websocket.server.WebSocketServer;

public class WebSocketPrintStream extends PrintStream{
	public WebSocketPrintStream(String fileName, Charset charset) throws IOException {
		super(fileName, charset);
		// TODO Auto-generated constructor stub
	}
	public WebSocketPrintStream(OutputStream out, boolean autoFlush, String encoding) throws UnsupportedEncodingException {
		super(out, autoFlush, encoding);
		// TODO Auto-generated constructor stub
	}
	public WebSocketPrintStream(OutputStream out, boolean autoFlush, Charset charset) {
		super(out, autoFlush, charset);
		// TODO Auto-generated constructor stub
	}
	public WebSocketPrintStream(OutputStream out, boolean autoFlush) {
		super(out, autoFlush);
		// TODO Auto-generated constructor stub
	}
	public WebSocketPrintStream(OutputStream out) {
		super(out);
		// TODO Auto-generated constructor stub
	}
	public WebSocketPrintStream(File file, String csn) throws FileNotFoundException, UnsupportedEncodingException {
		super(file, csn);
		// TODO Auto-generated constructor stub
	}
	public WebSocketPrintStream(File file) throws FileNotFoundException {
		super(file);
		// TODO Auto-generated constructor stub
	}
	public WebSocketPrintStream(File file, Charset charset) throws IOException {
		super(file, charset);
		// TODO Auto-generated constructor stub
	}
	public WebSocketPrintStream()
	{
	    super(System.out);
	}
	public WebSocketPrintStream(String fileName) throws FileNotFoundException {
		super(fileName);
		// TODO Auto-generated constructor stub
	}
	
	WebSocketServer mm;
	public WebSocketPrintStream(OutputStream out, WebSocketServer myChatServer) {
		super(out);
		mm=myChatServer;
	}
	
	@Override
	public void println(String x) {
		
		super.println(x);
		mm.broadcast(x);
	}

	
}