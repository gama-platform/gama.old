package ummisco.miro.extension.transportation.graph;

import java.util.Comparator;

public class BusStop implements Comparable<BusStop> {
	private long timeStamp;
	private BusStop nextStop;
	private BusStation station;
	private BusLine line;
	
	public BusStop( BusLine line, BusStation st, long cd)
	{
		this.timeStamp= cd;
		this.station= st;
		this.line = line;
		this.nextStop = null;
		this.station.addStop(this);
	}
	public void setNext(BusStop s)
	{
		this.nextStop = s;
	}
	public long getTimeStamp() {
		return timeStamp;
	}
	public void setTimeStamp(long timeStamp) {
		this.timeStamp = timeStamp;
	}
	public BusStop getNextStop() {
		return nextStop;
	}
	public void setNextStop(BusStop nextStop) {
		this.nextStop = nextStop;
	}
	public BusStation getStation() {
		return station;
	}
	public void setStation(BusStation station) {
		this.station = station;
	}
	public BusLine getLine() {
		return line;
	}
	public void setLine(BusLine line) {
		this.line = line;
	}
	@Override
	public int compareTo(BusStop o2) {
		BusStop o1 = this;
		return o1.timeStamp>o2.timeStamp?1:o1.timeStamp<o2.timeStamp?-1:!o1.line.equals(o2.line)?-1:0;
	}

}
