/*******************************************************************************************************
 *
 * MyArduino.java, in ummisco.gama.network, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gama.network.serial;

import java.awt.Dimension;
import java.io.PrintWriter;
import java.util.Scanner;

import com.fazecast.jSerialComm.SerialPort;

import ummisco.gama.dev.utils.THREADS;

/**
 * The Class MyArduino.
 */
public class MyArduino {

	/** The com port. */
	private SerialPort comPort;

	/** The port description. */
	private String portDescription;

	/** The baud rate. */
	private int baud_rate;

	/**
	 * Instantiates a new my arduino.
	 */
	public MyArduino() {
		// empty constructor if port undecided
	}

	/**
	 * Instantiates a new my arduino.
	 *
	 * @param portDescription
	 *            the port description
	 */
	public MyArduino(final String portDescription) {
		// make sure to set baud rate after
		this.portDescription = portDescription;
		comPort = SerialPort.getCommPort(this.portDescription);
	}

	/**
	 * Instantiates a new my arduino.
	 *
	 * @param portDescription
	 *            the port description
	 * @param baud_rate
	 *            the baud rate
	 */
	public MyArduino(final String portDescription, final int baud_rate) {
		// preferred constructor
		this.portDescription = portDescription;
		comPort = SerialPort.getCommPort(this.portDescription);
		this.baud_rate = baud_rate;
		comPort.setBaudRate(this.baud_rate);
	}

	/**
	 * Open connection.
	 *
	 * @return true, if successful
	 */
	public boolean openConnection() {
		if (comPort.openPort()) {
			THREADS.WAIT(100);
			return true;
		}
		AlertBox alert = new AlertBox(new Dimension(400, 100), "Error Connecting", "Try Another port");
		alert.display();
		return false;
	}

	/**
	 * Close connection.
	 */
	public void closeConnection() {
		comPort.closePort();
	}

	/**
	 * Sets the port description.
	 *
	 * @param portDescription
	 *            the new port description
	 */
	public void setPortDescription(final String portDescription) {
		this.portDescription = portDescription;
		comPort = SerialPort.getCommPort(this.portDescription);
	}

	/**
	 * Sets the baud rate.
	 *
	 * @param baud_rate
	 *            the new baud rate
	 */
	public void setBaudRate(final int baud_rate) {
		this.baud_rate = baud_rate;
		comPort.setBaudRate(this.baud_rate);
	}

	/**
	 * Gets the port description.
	 *
	 * @return the port description
	 */
	public String getPortDescription() { return portDescription; }

	/**
	 * Gets the serial port.
	 *
	 * @return the serial port
	 */
	public SerialPort getSerialPort() { return comPort; }

	/**
	 * Serial read.
	 *
	 * @return the string
	 */
	public String serialRead() {
		// will be an infinite loop if incoming data is not bound
		comPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 0, 0);
		StringBuilder out = new StringBuilder();
		Scanner in = new Scanner(comPort.getInputStream());
		try {
			while (in.hasNext()) { out.append(in.next()).append("\n"); }
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return out.toString();
	}

	/**
	 * Serial read.
	 *
	 * @param limit
	 *            the limit
	 * @return the string
	 */
	public String serialRead(final int limit) {
		// in case of unlimited incoming data, set a limit for number of readings
		comPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 0, 0);
		StringBuilder out = new StringBuilder();
		int count = 0;
		Scanner in = new Scanner(comPort.getInputStream());
		try {
			while (in.hasNext() && count <= limit) {
				out.append(in.next()).append("\n");
				count++;
			}
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return out.toString();
	}

	/**
	 * Serial write.
	 *
	 * @param s
	 *            the s
	 */
	public void serialWrite(final String s) {
		// writes the entire string at once.
		comPort.setComPortTimeouts(SerialPort.TIMEOUT_SCANNER, 0, 0);
		THREADS.WAIT(5);
		PrintWriter pout = new PrintWriter(comPort.getOutputStream());
		pout.print(s);
		pout.flush();

	}

	/**
	 * Serial write.
	 *
	 * @param s
	 *            the s
	 * @param noOfChars
	 *            the no of chars
	 * @param delay
	 *            the delay
	 */
	public void serialWrite(final String s, final int noOfChars, final int delay) {
		// writes the entire string at once.
		comPort.setComPortTimeouts(SerialPort.TIMEOUT_SCANNER, 0, 0);
		THREADS.WAIT(5);
		PrintWriter pout = new PrintWriter(comPort.getOutputStream());
		for (int i = 0; i < s.length(); i += noOfChars) {
			pout.write(s.substring(i, i + noOfChars));
			pout.flush();
			System.out.println(s.substring(i, i + noOfChars));
			THREADS.WAIT(delay);

		}
		pout.write(noOfChars);
		pout.flush();

	}

	/**
	 * Serial write.
	 *
	 * @param c
	 *            the c
	 */
	public void serialWrite(final char c) {
		// writes the entire string at once.
		comPort.setComPortTimeouts(SerialPort.TIMEOUT_SCANNER, 0, 0);
		THREADS.WAIT(5);
		PrintWriter pout = new PrintWriter(comPort.getOutputStream());
		pout.write(c);
		pout.flush();
	}

	/**
	 * Serial write.
	 *
	 * @param c
	 *            the c
	 * @param delay
	 *            the delay
	 */
	public void serialWrite(final char c, final int delay) {
		// writes the entire string at once.
		comPort.setComPortTimeouts(SerialPort.TIMEOUT_SCANNER, 0, 0);
		THREADS.WAIT(5);
		PrintWriter pout = new PrintWriter(comPort.getOutputStream());
		pout.write(c);
		pout.flush();
		THREADS.WAIT(delay);
	}
}
