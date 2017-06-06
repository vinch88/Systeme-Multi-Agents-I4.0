package app.gui;

import java.awt.Image;
import java.awt.Toolkit;
import java.net.URL;

import javax.swing.ImageIcon;

public class ImageLoader {

	/*------------------------------------------------------------------*\
	|*							Methodes Public							*|
	\*------------------------------------------------------------------*/

	/*----------------------------------------------*\
	|*		 Version Synchrone (bloquant)			*|
	\*----------------------------------------------*/

	/**
	 * Bloquant
	 */
	public static ImageIcon loadSynchrone(String nameFile) {
		return new ImageIcon(nameFile);
	}

	/**
	 * Bloquant Depuis un .jar
	 */
	public static ImageIcon loadSynchroneJar(String nameFile) {
		URL url = ClassLoader.getSystemResource(nameFile);
		return new ImageIcon(url);
	}

	/*---------------------------------------------*\
	|*		 Version Assynchrone (non-bloquant)		*|
	\*----------------------------------------------*/

	/**
	 * Non Bloquant
	 */
	public static ImageIcon loadAsynchrone(String nameFile) {
		Image image = Toolkit.getDefaultToolkit().getImage(nameFile);
		return new ImageIcon(image);
	}

	/**
	 * Non Bloquant Depuis un .jar
	 */
	public static ImageIcon loadAsynchroneJar(String nameFile)// Non Bloquant
	{
		URL url = ClassLoader.getSystemResource(nameFile);
		Image image = Toolkit.getDefaultToolkit().getImage(url);
		return new ImageIcon(image);
	}

	/*------------------------------------------------------------------*\
	|*							Methodes Private						*|
	\*------------------------------------------------------------------*/

}