package test;

import de.beckhoff.jni.Convert;
import de.beckhoff.jni.JNIByteBuffer;
import de.beckhoff.jni.tcads.AdsCallDllFunction;
import de.beckhoff.jni.tcads.AmsAddr;

public class ConnexionTwincat {
	private long err;
	private AmsAddr addr;

	public ConnexionTwincat() {

	}

	private void setAmsAdress(String addresse, int port) {
		addr.setNetIdStringEx(addresse);
		addr.setPort(port);
		System.out.println("NetID: " + addr.getNetIdString() + " Port: " + addr.getPort());
	}

	public Boolean initializeConnection(String addresse, int port) {

		try {

			addr = new AmsAddr();

			JNIByteBuffer handleBuff = new JNIByteBuffer(Integer.SIZE / Byte.SIZE);
			JNIByteBuffer symbolBuff = new JNIByteBuffer(Convert.StringToByteArr("MAIN.PLCVar", true));
			JNIByteBuffer dataBuff = new JNIByteBuffer(Integer.SIZE / Byte.SIZE);

			// Open communication
			AdsCallDllFunction.adsPortOpen();
			AdsCallDllFunction.getLocalAddress(addr);
			// addr.setPort(AdsCallDllFunction.AMSPORT_R0_PLC_RTS1);
			addr.setPort(port);


			// Get handle by symbol name
			err = AdsCallDllFunction.adsSyncReadWriteReq(addr, AdsCallDllFunction.ADSIGRP_SYM_HNDBYNAME, 0x0,
					handleBuff.getUsedBytesCount(), handleBuff, symbolBuff.getUsedBytesCount(), symbolBuff);
			if (err != 0) {
				System.out.println("Error: Get handle: 0x" + Long.toHexString(err));
				closeConnection();
			} else {
				System.out.println("Success: Get handle!");
			}

			return true;

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

	}

	public Boolean closeConnection() {

		// Close communication
		err = AdsCallDllFunction.adsPortClose();
		if (err != 0) {
			System.out.println("Error: Close Communication: 0x" + Long.toHexString(err));
			return false;
		}
		System.out.println("Connexion closed");
		return true;

	}

	public void sendMessageToTwinCat(String message) {

		try {
			System.in.read();
		} catch (Exception e) {
			System.out.println("Error: Close program");
		}
	}

	public String changeModePresse() {
		return null;
	}

	public String startPresse() {

		return null;
	}

	public String resetPresse() {

		return null;
	}

	public String getNombrePieceFromPresse() {

		return null;
	}

	public void changerVitesse(int vitesse) {

	}

	public void startMoteur() {

	}

}
