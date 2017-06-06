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

	/*------------------------------*\
	|*				Get				*|
	\*------------------------------*/

	public String getAddrIP() {
		return tfIP.getText();
	}

	public int getPort() {
		return Integer.parseInt(tfPort.getText());
	}

	/*------------------------------------------------------------------*\
	|*							Methodes Private						*|
	\*------------------------------------------------------------------*/

	private void geometry() {

		strStatut = new String("Programme non démarré");
		lblStatut = new JLabel(strStatut);
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
		// Box bv2 = Box.createVerticalBox();
		Box bh1 = Box.createHorizontalBox();
		// Box bh2 = Box.createHorizontalBox();
		// Box bh3 = Box.createHorizontalBox();
		//
		bh1.add(lblStatut);
		bh1.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Statut de l'agent", 0, 0,
				new Font("Dialog", 1, 16), Color.BLACK));
		panel.setLayout(gridlayout);
		//
		// bh1.add(new JLabel("Adresse IP", SwingConstants.CENTER));
		// bh1.add(tfIP);
		// bh2.add(new JLabel("Port", SwingConstants.CENTER));
		// bh2.add(tfPort);
		// bh3.add(new JPanel());
		// bh3.add(btnDemarrerSynapxis);
		//
		// bv2.add(bh1);
		// bv2.add(bh2);
		// bv2.add(bh3);
		// add(bv1);
		// add(bv2);

		panel.add(new JLabel("Adresse IP", SwingConstants.CENTER));
		panel.add(tfIP);
		panel.add(new JLabel("Port", SwingConstants.CENTER));
		panel.add(tfPort);
		panel.add(new JPanel());
		panel.add(btnDemarrerSynapxis);

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
					System.out.println("erreur d'execution " + cmd + e1.toString());
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
	private String strStatut;
	private JLabel lblStatut;

}