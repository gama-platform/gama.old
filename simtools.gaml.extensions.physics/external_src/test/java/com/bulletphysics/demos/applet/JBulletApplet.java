/*
 * Java port of Bullet (c) 2008 Martin Dvorak <jezek2@advel.cz>
 *
 * This software is provided 'as-is', without any express or implied warranty.
 * In no event will the authors be held liable for any damages arising from
 * the use of this software.
 * 
 * Permission is granted to anyone to use this software for any purpose, 
 * including commercial applications, and to alter it and redistribute it
 * freely, subject to the following restrictions:
 * 
 * 1. The origin of this software must not be misrepresented; you must not
 *    claim that you wrote the original software. If you use this software
 *    in a product, an acknowledgment in the product documentation would be
 *    appreciated but is not required.
 * 2. Altered source versions must be plainly marked as such, and must not be
 *    misrepresented as being the original software.
 * 3. This notice may not be removed or altered from any source distribution.
 */

package com.bulletphysics.demos.applet;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import com.bulletphysics.demos.basic.BasicDemo;
import com.bulletphysics.demos.bsp.BspDemo;
import com.bulletphysics.demos.character.CharacterDemo;
import com.bulletphysics.demos.concave.ConcaveDemo;
import com.bulletphysics.demos.concaveconvexcast.ConcaveConvexcastDemo;
import com.bulletphysics.demos.constraint.ConstraintDemo;
import com.bulletphysics.demos.dynamiccontrol.DynamicControlDemo;
import com.bulletphysics.demos.forklift.ForkLiftDemo;
import com.bulletphysics.demos.genericjoint.GenericJointDemo;
import com.bulletphysics.demos.movingconcave.MovingConcaveDemo;
import com.bulletphysics.demos.opengl.DemoApplication;
import com.bulletphysics.demos.opengl.IGL;
import com.bulletphysics.demos.vehicle.VehicleDemo;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;

/**
 *
 * @author  jezek2
 */
public class JBulletApplet extends javax.swing.JApplet {

	private DemoPanel demoPanel;
	private DemoEntry currentDemo;
	
	/** Initializes the applet JBulletApplet */
	@Override
	public void init() {
		try {
			EventQueue.invokeAndWait(new Runnable() {
				public void run() {
					try {
						UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
					}
					catch (Exception e) {
						// ignored
					}
					
					initComponents();
					
					topPanel.setBorder(new BevelBorder(BevelBorder.RAISED, new Color(0xCCCCCC), new Color(0xFFFFFF), new Color(0x333333), new Color(0x999999)));
					
					final DefaultComboBoxModel model = (DefaultComboBoxModel)cmbDemos.getModel();
					model.addElement("---");
					model.addElement(new DemoEntry("Basic Demo", BasicDemo.class));
					model.addElement(new DemoEntry("Generic Joint Demo", GenericJointDemo.class));
					model.addElement(new DemoEntry("Bsp Demo", BspDemo.class));
					model.addElement(new DemoEntry("Static Concave Mesh Demo", ConcaveDemo.class));
					model.addElement(new DemoEntry("Vehicle Demo", VehicleDemo.class));
					model.addElement(new DemoEntry("Dynamic Control Demo", DynamicControlDemo.class));
					model.addElement(new DemoEntry("Moving Concave Demo", MovingConcaveDemo.class));
					model.addElement(new DemoEntry("ForkLift Demo", ForkLiftDemo.class));
					model.addElement(new DemoEntry("Concave Convexcast Demo", ConcaveConvexcastDemo.class));
					model.addElement(new DemoEntry("Character Demo", CharacterDemo.class));
                                        model.addElement(new DemoEntry("Constraint Demo", ConstraintDemo.class));

					cmbDemos.addItemListener(new ItemListener() {
						public void itemStateChanged(final ItemEvent e) {
							if (e.getStateChange() == ItemEvent.SELECTED) {
								if (e.getItem() instanceof DemoEntry) {
									EventQueue.invokeLater(new Runnable() {
										public void run() {
											runDemo((DemoEntry)e.getItem());
										}
									});
									//runDemo((DemoEntry)e.getItem());
									if (model.getElementAt(0) instanceof String) {
										model.removeElementAt(0);
										btnRestart.setEnabled(true);
									}
								}
							}
						}
					});
					
					btnRestart.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							currentDemo = null;
							runDemo((DemoEntry)cmbDemos.getSelectedItem());
						}
					});
					
					demoPanel = new DemoPanel();
					mainPanel.add(demoPanel, BorderLayout.CENTER);
				}
			});
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	private void runDemo(DemoEntry entry) {
		if (currentDemo == entry) return;
		
		currentDemo = entry;
		
		try {
			DemoApplication demoApp = entry.cls.getConstructor(IGL.class).newInstance(demoPanel.getGL());
			demoApp.initPhysics();
			demoPanel.runDemo(demoApp);
			
			demoPanel.requestFocusInWindow();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static class DemoEntry {
		public String name;
		public Class<? extends DemoApplication> cls;

		public DemoEntry(String name, Class<? extends DemoApplication> cls) {
			this.name = name;
			this.cls = cls;
		}
		
		@Override
		public String toString() {
			return name;
		}
	}
	
	/** This method is called from within the init() method to
	 * initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        topPanel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        cmbDemos = new javax.swing.JComboBox();
        btnRestart = new javax.swing.JButton();
        mainPanel = new javax.swing.JPanel();

        topPanel.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED, new java.awt.Color(204, 204, 204), new java.awt.Color(255, 255, 255), new java.awt.Color(51, 51, 51), new java.awt.Color(153, 153, 153)));

        jLabel1.setText("Choose demo:");

        btnRestart.setText("Restart");
        btnRestart.setEnabled(false);

        org.jdesktop.layout.GroupLayout topPanelLayout = new org.jdesktop.layout.GroupLayout(topPanel);
        topPanel.setLayout(topPanelLayout);
        topPanelLayout.setHorizontalGroup(
            topPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(topPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel1)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(cmbDemos, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 236, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 84, Short.MAX_VALUE)
                .add(btnRestart)
                .addContainerGap())
        );
        topPanelLayout.setVerticalGroup(
            topPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(topPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(topPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE, false)
                    .add(jLabel1)
                    .add(btnRestart)
                    .add(cmbDemos, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        getContentPane().add(topPanel, java.awt.BorderLayout.NORTH);

        mainPanel.setBackground(new java.awt.Color(0, 0, 0));
        mainPanel.setLayout(new java.awt.BorderLayout());
        getContentPane().add(mainPanel, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents
	
	public static void main(String[] args) {
		final JBulletApplet app = new JBulletApplet();
		app.init();

		EventQueue.invokeLater(new Runnable() {
			public void run() {
				JFrame frm = new JFrame();
				frm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frm.add(app);
				frm.setSize(600, 450+50);
				//frm.setSize(512, 384+50);
				frm.setVisible(true);
			}
		});
	}
	
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnRestart;
    private javax.swing.JComboBox cmbDemos;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JPanel topPanel;
    // End of variables declaration//GEN-END:variables
	
}
