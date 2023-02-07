package ummisco.gama.network.serial;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
public class AlertBox {
	JFrame alertWindow;
	public AlertBox(Dimension obj, String title, String message) {
		alertWindow = new JFrame();
		alertWindow.setTitle(title);
		alertWindow.setSize(obj);
		alertWindow.setPreferredSize(obj);
		alertWindow.setLayout(new BorderLayout());
		alertWindow.setLocationRelativeTo(null);
		JLabel lblMessage = new JLabel(message, SwingConstants.CENTER);
		JButton btnOk = new JButton("Ok");
		btnOk.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				alertWindow.setVisible(false);
				alertWindow.dispose();
			}
			
		});
		alertWindow.add(btnOk, BorderLayout.SOUTH);
		alertWindow.add(lblMessage, BorderLayout.CENTER);
	}
	
	public void display(){
		alertWindow.setVisible(true);
	}
}
