package app.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public class JPanelRobot extends JPanel {
	/*------------------------------------------------------------------*\
	|*							Constructeurs							*|
	\*------------------------------------------------------------------*/

	public JPanelRobot() {

		geometry();
		control();
		appearance();
	}

	/*------------------------------------------------------------------*\
	|*							Methodes Public							*|
	\*------------------------------------------------------------------*/

	/*------------------------------*\
	|*				Set				*|
	\*------------------------------*/

	/**
	 * @param message
	 */
	public void setStatut(String message) {
		lblStatut.setText(message);
	}

	/*------------------------------*\
	|*				Get				*|
	\*------------------------------*/

	/**
	 * @return the ip
	 */
	public String getAddrIP() {
		return tfIP.getText();
	}

	/**
	 * @return the port
	 */
	public int getPort() {
		return Integer.parseInt(tfPort.getText());
	}

	/*------------------------------------------------------------------*\
	|*							Methodes Private						*|
	\*------------------------------------------------------------------*/

	private void geometry() {

		lblStatut = new JLabel("Programme non démarré");
		tfIP = new JTextField("localhost");
		tfPort = new JTextField("1111");
		btnDemarrerSynapxis = new JButton("Démarrer Synapxis");
		JPanel panel = new JPanel();

		// Layout : Specification
		{
			BorderLayout layout = new BorderLayout();

			setLayout(layout);
		}

		// JComponent : add

		GridLayout gridlayout = new GridLayout(3, 2);

		Box bv1 = Box.createVerticalBox();

		Box bh1 = Box.createHorizontalBox();

		bh1.add(lblStatut);

		panel.setLayout(gridlayout);

		panel.add(new JLabel("Adresse IP", SwingConstants.CENTER));
		panel.add(new JPanelDecorator(tfIP, 10));
		panel.add(new JLabel("Port", SwingConstants.CENTER));
		panel.add(new JPanelDecorator(tfPort, 10));
		panel.add(new JPanel());
		panel.add(new JPanelDecorator(btnDemarrerSynapxis, 10));

		bv1.add(bh1);
		bv1.add(panel);
		add(bv1, BorderLayout.CENTER);
	}

	private void control() {

		btnDemarrerSynapxis.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String cmd = "C:\\Program Files (x86)\\Synapxis\\Synapxis.exe";
				try {
					Runtime r = Runtime.getRuntime();
					Process p = r.exec(cmd);
					// p.waitFor();// si l'application doit attendre a ce que ce
					// process fini

				} catch (Exception e1) {
					setStatut("Execution error " + cmd + e1.toString());
				}
			}
		});
	}

	private void appearance() {
	}

	/*------------------------------------------------------------------*\
	|*							Attributs Private						*|
	\*------------------------------------------------------------------*/

	// Tools

	// input
	private JTextField tfIP;
	private JTextField tfPort;
	private JButton btnDemarrerSynapxis;
	private JLabel lblStatut;

}