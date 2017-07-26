package app.gui;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import app.gestion.GestionUsine;

public class JFrameCopyright extends JFrame {

	/*------------------------------------------------------------------*\
	|*							Constructeurs							*|
	\*------------------------------------------------------------------*/

	/**
	 * @param gu
	 */
	public JFrameCopyright(GestionUsine gu) {
		gestionUsine = gu;
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

	/*------------------------------------------------------------------*\
	|*							Methodes Private						*|
	\*------------------------------------------------------------------*/

	private void geometry() {
		// JComponent : Instanciation
		JPanel panel = new JPanel();
		JPanel panel2 = new JPanel();
		btnDemarrer = new JButton("Démarrer la simulation");

		ImageIcon logo = MagasinImage.HeArc;
		JLabel img = new JLabel(logo);

		// Layout : Specification
		{
			GridLayout layout = new GridLayout(4, 1);

			layout.setHgap(5);
			layout.setVgap(5);

			panel.setLayout(layout);
		}

		// JComponent : add
		panel.add(img);
		panel.add(new JLabel("© copyright 2017 - He-Arc", SwingConstants.CENTER));
		panel2.add(new JPanel());
		panel2.add(new JPanelDecorator(btnDemarrer, 30));
		panel2.add(new JPanel());
		panel.add(panel2);
		add(new JPanelDecorator(panel, 15));
	}

	private void control() {
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		btnDemarrer.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				new JFrameUI(gestionUsine);
				dispose();
			}
		});

	}

	private void appearance() {
		setSize(600, 400);
		setResizable(false);
		setTitle("Système Multi-Agents - Industrie 4.0");
		setLocationRelativeTo(null); // frame centrer
		setVisible(true); // last!

	}

	/*------------------------------------------------------------------*\
	|*							Attributs Private						*|
	\*------------------------------------------------------------------*/

	// Tools
	private GestionUsine gestionUsine;
	private JButton btnDemarrer;

}
