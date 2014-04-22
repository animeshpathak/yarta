package fr.inria.arles.yarta.desktop.library;

import java.io.File;
import java.io.RandomAccessFile;

import fr.inria.arles.yarta.desktop.library.util.Installer;
import fr.inria.arles.yarta.middleware.msemanagement.ContentClient;

public class ContentClientDesktop implements ContentClient {

	private static final String DataPath = Installer.InstallPath + "data/";

	@Override
	public byte[] getData(String id) {
		try {
			new File(DataPath).mkdirs();
			RandomAccessFile f = new RandomAccessFile(DataPath + id, "r");
			byte[] b = new byte[(int) f.length()];
			f.read(b);
			f.close();
			return b;
		} catch (Exception ex) {
			// do nothing
		}
		return null;
	}

	@Override
	public void setData(String id, byte[] data) {
		try {
			new File(DataPath).mkdirs();
			RandomAccessFile f = new RandomAccessFile(DataPath + id, "rw");
			f.write(data);
			f.close();
		} catch (Exception ex) {
			ex.printStackTrace();
			// do nothing
		}
	}
}
