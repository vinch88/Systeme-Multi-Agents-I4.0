package test;

import java.io.BufferedReader;

import de.beckhoff.jni.Convert;
import de.beckhoff.jni.JNIByteBuffer;
import de.beckhoff.jni.tcads.AdsCallDllFunction;
import de.beckhoff.jni.tcads.AdsSymbolEntry;
import de.beckhoff.jni.tcads.AmsAddr;

public class ConnexionTwincat {
	private long err;
	private AmsAddr addr;
	private JNIByteBuffer readBuffer;
	private JNIByteBuffer writeBuffer;

	private long iGroup;
	private long iOff;
	private int hdlBuffToInt;

	private void setAmsAdress(String addresse, int port) {
		addr.setNetIdStringEx(addresse);
		addr.setPort(port);
		System.out.println("NetID: " + addr.getNetIdString() + " Port: " + addr.getPort());
	}

	public void initializeConnection(String addresse, int port) {

		addr = new AmsAddr();
		readBuffer = new JNIByteBuffer(0xFFFF);

		// Open communication
		AdsCallDllFunction.adsPortOpen();

		// Set the netId and port of the Twincat machine
		addr.setNetIdStringEx(addresse);
		addr.setPort(port);

		initializeWriteBuffer("GVL.fbStation.fbCycleMain.iStep");
	}

	public void initializeWriteBuffer(String variable) {
		// Initialize writeBuff with user data
		writeBuffer = new JNIByteBuffer(Convert.StringToByteArr(variable, false));
		readBuffer = new JNIByteBuffer(0xFFFF);

		// Get variable declaration
		err = AdsCallDllFunction.adsSyncReadWriteReq(addr, AdsCallDllFunction.ADSIGRP_SYM_INFOBYNAMEEX, 0,
				readBuffer.getUsedBytesCount(), readBuffer, writeBuffer.getUsedBytesCount(), writeBuffer);
		if (err != 0) {
			System.out.println("Error: AdsSyncReadReq: 0x" + Long.toHexString(err));
		} else {
			// Convert stream to AdsSymbolEntry
			AdsSymbolEntry adsSymbolEntry = new AdsSymbolEntry(readBuffer.getByteArray());

			// Write information to stdout
			System.out.println("Name:\t\t" + adsSymbolEntry.getName());
			System.out.println("Index Group:\t" + adsSymbolEntry.getiGroup());
			System.out.println("Index Offset:\t" + adsSymbolEntry.getiOffs());
			System.out.println("Size:\t\t" + adsSymbolEntry.getSize());
			System.out.println("Type:\t\t" + adsSymbolEntry.getType());
			System.out.println("Comment:\t" + adsSymbolEntry.getComment());

			iGroup = adsSymbolEntry.getiGroup();
			iOff = adsSymbolEntry.getiOffs();

		}

	}

	public Boolean initializeConnectionDepr(String addresse, int port) {

		try {

			addr = new AmsAddr();

			JNIByteBuffer handleBuff = new JNIByteBuffer(Integer.SIZE / Byte.SIZE);
			JNIByteBuffer symbolBuff = new JNIByteBuffer(
					Convert.StringToByteArr("GVL.fbStation.fbCycleMain.iStep", true));
			JNIByteBuffer dataBuff = new JNIByteBuffer(Integer.SIZE / Byte.SIZE);

			// Open communication
			AdsCallDllFunction.adsPortOpen();
			AdsCallDllFunction.getLocalAddress(addr);
			// addr.setPort(AdsCallDllFunction.AMSPORT_R0_PLC_RTS1);
			addr.setPort(port);

			// setAmsAdress(addresse, port);

			// Get handle by symbol name
			err = AdsCallDllFunction.adsSyncReadWriteReq(addr, AdsCallDllFunction.ADSIGRP_SYM_HNDBYNAME, 0x0,
					handleBuff.getUsedBytesCount(), handleBuff, symbolBuff.getUsedBytesCount(), symbolBuff);

			if (err != 0) {
				System.out.println("Error: Get handle: 0x" + Long.toHexString(err));
				closeConnection();
			} else {
				System.out.println("Success: Get handle!");
			}

			// Handle: byte[] to int
			hdlBuffToInt = Convert.ByteArrToInt(handleBuff.getByteArray());

			// Read value by handle
			err = AdsCallDllFunction.adsSyncReadReq(addr, AdsCallDllFunction.ADSIGRP_SYM_VALBYHND, hdlBuffToInt, 0x4,
					dataBuff);
			if (err != 0) {
				System.out.println("Error: Read by handle: 0x" + Long.toHexString(err));
			} else {
				// Data: byte[] to int
				int intVal = Convert.ByteArrToInt(dataBuff.getByteArray());
				System.out.println("Success: iStep value: " + intVal);
			}

			// JNIByteBuffer newBuff = new
			// JNIByteBuffer(Convert.IntToByteArr(100));
			// System.out.println(Convert.IntToByteArr(100));
			//
			// err = AdsCallDllFunction.adsSyncWriteReq(addr, 0xF005,
			// hdlBuffToInt, newBuff.getUsedBytesCount(), newBuff);
			// if (err != 0) {
			// System.out.println("Error: Write by adress: 0x" +
			// Long.toHexString(err));
			// }

			initializeWriteBuffer("GVL.fbStation.fbCycleMain.iStep");

			JNIByteBuffer dbuff = new JNIByteBuffer(Convert.IntToByteArr(100));

			// Specify IndexGroup, IndexOffset and write PLCVar
			err = AdsCallDllFunction.adsSyncWriteReq(addr, iGroup, iOff, dbuff.getUsedBytesCount(), dbuff);
			if (err != 0) {
				System.out.println("Error: Write by adress: 0x" + Long.toHexString(err));
			}

			// Read value by handle
			err = AdsCallDllFunction.adsSyncReadReq(addr, AdsCallDllFunction.ADSIGRP_SYM_VALBYHND, hdlBuffToInt, 0x4,
					dataBuff);
			if (err != 0) {
				System.out.println("Error: Read by handle: 0x" + Long.toHexString(err));
			} else {
				// Data: byte[] to int
				int intVal = Convert.ByteArrToInt(dataBuff.getByteArray());
				System.out.println("Success: iStep value: " + intVal);
			}

			// Release handle
			err = AdsCallDllFunction.adsSyncWriteReq(addr, AdsCallDllFunction.ADSIGRP_SYM_RELEASEHND, 0,
					handleBuff.getUsedBytesCount(), handleBuff);

			if (err != 0) {
				System.out.println("Error: Release Handle: 0x" + Long.toHexString(err));
			} else {
				System.out.println("Success: Release Handle!");
			}

			// closeConnection();

			return true;

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

	}

	public void writeValue() {
		// Specify IndexGroup, IndexOffset and write PLCVar
		err = AdsCallDllFunction.adsSyncWriteReq(addr, AdsCallDllFunction.ADSIGRP_SYM_VALBYHND, 0x0,
				Integer.SIZE / Byte.SIZE, new JNIByteBuffer(Convert.IntToByteArr(300)));
		if (err != 0) {
			System.out.println("Error: Write by adress: 0x" + Long.toHexString(err));
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
