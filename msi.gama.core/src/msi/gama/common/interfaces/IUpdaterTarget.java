/**
 * Created by drogoul, 10 mars 2014
 * 
 */
package msi.gama.common.interfaces;

public interface IUpdaterTarget<Message> {

	public boolean isDisposed();

	public void updateWith(Message m);
}