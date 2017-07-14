package test;

import de.beckhoff.jni.Convert;
import de.beckhoff.jni.JNIByteBuffer;
import de.beckhoff.jni.tcads.AdsCallDllFunction;
import de.beckhoff.jni.tcads.AdsSymbolEntry;
import de.beckhoff.jni.tcads.AmsAddr;

public class ConnexionClean {

	private AmsAddr addr;
	private long err;

	private JNIByteBuffer handleBuff;
	private JNIByteBuffer dataBuff;

	private long iGroup;
	private long iOff;
	private AdsSymbolEntry adsSymbolEntry;

	private String testResetXEtat = "GVL.fbStation.fbBtn.HmiReset.xEtat";
	private String testBtnAutoXEtat = "GVL.fbStation.fbBtn.Auto.xEtat";
	private String testLrVitesse = "GVL.stParam.stRecipe.lrVitesse";
	private String testCycleMainIStep = "GVL.fbStation.fbCycleMain.iStep";

	public void openConnection(String addresse, int port) {

		addr = new AmsAddr();
		handleBuff = new JNIByteBuffer(Integer.SIZE / Byte.SIZE);
		dataBuff = new JNIByteBuffer(Integer.SIZE / Byte.SIZE);

		// AdsCallDllFunction.getLocalAddress(addr);
		addr.setNetIdStringEx(addresse);
		addr.setPort(port);

		// Open communication
		AdsCallDllFunction.adsPortOpen();

		System.out.println("Connexion open");

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

	public void releaseHandle() {

		// Release handle
		err = AdsCallDllFunction.adsSyncWriteReq(addr, AdsCallDllFunction.ADSIGRP_SYM_RELEASEHND, 0,
				handleBuff.getUsedBytesCount(), handleBuff);

		if (err != 0) {
			System.out.println("Error: Release Handle: 0x" + Long.toHexString(err));
		} else {
			System.out.println("Success: Release Handle!");
		}
	}

	public String readValue(String variable) {

		setIGroupIOffAdsSymbolEntry(variable);

		JNIByteBuffer symbolBuff = new JNIByteBuffer(Convert.StringToByteArr(variable, true));
		dataBuff = new JNIByteBuffer(Integer.SIZE / Byte.SIZE);
		String val = "";

		// Get handle by symbol name
		err = AdsCallDllFunction.adsSyncReadWriteReq(addr, AdsCallDllFunction.ADSIGRP_SYM_HNDBYNAME, 0x0,
				handleBuff.getUsedBytesCount(), handleBuff, symbolBuff.getUsedBytesCount(), symbolBuff);

		if (err != 0) {
			return "Error: Get handle: 0x" + Long.toHexString(err);

		} else {
			System.out.println("Success: Get handle!");
		}

		// Handle: byte[] to int
		int hdlBuffToInt = Convert.ByteArrToInt(handleBuff.getByteArray());

		// Read value by handle
		err = AdsCallDllFunction.adsSyncReadReq(addr, AdsCallDllFunction.ADSIGRP_SYM_VALBYHND, hdlBuffToInt, 0x4,
				dataBuff);
		if (err != 0) {
			return "Error: Read by handle: 0x" + Long.toHexString(err);
		} else {

			val = Convert.ByteArrToInt(dataBuff.getByteArray()) + "";
			System.out.println("Success: " + adsSymbolEntry.getName() + " value: " + val);
		}
		return val;
	}

	public void setIGroupIOffAdsSymbolEntry(String variable) {
		JNIByteBuffer symbolBuff = new JNIByteBuffer(Convert.StringToByteArr(variable, true));
		dataBuff = new JNIByteBuffer(0xFFFF);
		// Get variable declaration
		err = AdsCallDllFunction.adsSyncReadWriteReq(addr, AdsCallDllFunction.ADSIGRP_SYM_INFOBYNAMEEX, 0,
				dataBuff.getUsedBytesCount(), dataBuff, symbolBuff.getUsedBytesCount(), symbolBuff);
		if (err != 0) {
			System.out.println("Error: AdsSyncReadReq: 0x" + Long.toHexString(err));
		} else {
			// Convert stream to AdsSymbolEntry
			adsSymbolEntry = new AdsSymbolEntry(dataBuff.getByteArray());

			// Watch informations (debug)
			// System.out.println("Name:\t\t" + adsSymbolEntry.getName());
			// System.out.println("Index Group:\t" +
			// adsSymbolEntry.getiGroup());
			// System.out.println("Index Offset:\t" +
			// adsSymbolEntry.getiOffs());
			// System.out.println("Size:\t\t" + adsSymbolEntry.getSize());
			// System.out.println("Type:\t\t" + adsSymbolEntry.getType());
			// System.out.println("Comment:\t" + adsSymbolEntry.getComment());

			iGroup = adsSymbolEntry.getiGroup();
			iOff = adsSymbolEntry.getiOffs();
		}
	}

	public String writeIntValue(String variable, int value) {

		// Be sure that the handle is the good one
		readValue(variable);

		dataBuff = new JNIByteBuffer(Convert.IntToByteArr(value));

		// Specify IndexGroup, IndexOffset and write PLCVar
		err = AdsCallDllFunction.adsSyncWriteReq(addr, iGroup, iOff, dataBuff.getUsedBytesCount(), dataBuff);
		if (err != 0) {
			System.out.println("Error: Write by adress: 0x" + Long.toHexString(err));
		}

		releaseHandle();

		return variable;
	}

	public String writeBoolValue(String variable, Boolean value) {

		// Be sure that the handle is the good one
		readValue(variable);

		JNIByteBuffer dbuff = new JNIByteBuffer(Convert.BoolToByteArr(value));

		// Specify IndexGroup, IndexOffset and write PLCVar
		err = AdsCallDllFunction.adsSyncWriteReq(addr, iGroup, iOff, dbuff.getUsedBytesCount(), dbuff);
		if (err != 0) {
			System.out.println("Error: Write by adress: 0x" + Long.toHexString(err));
		}

		releaseHandle();
		return variable;
	}

	public void testReadAndWrite() {
		readValue(testResetXEtat);
		writeBoolValue(testResetXEtat, true);
		writeBoolValue(testBtnAutoXEtat, true);
		readValue(testLrVitesse);
		writeIntValue(testLrVitesse, 5);
		readValue(testLrVitesse);

	}
}
