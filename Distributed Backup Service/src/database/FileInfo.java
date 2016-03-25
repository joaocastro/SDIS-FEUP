package database;

import java.util.ArrayList;
import java.util.HashMap;

import messages.Header;

public class FileInfo {
	private String fileId;
	private int numberOfChunks;
	HashMap<Integer, ArrayList<Header>> backedUpChunks;
	
	public HashMap<Integer, ArrayList<Header>> getBackedUpChunks() {
		return backedUpChunks;
	}
	public int countChunkBackUps(int chunkNo) {
		ArrayList<Header> headers = backedUpChunks.get(chunkNo);
		if (headers != null)
			return headers.size();
		else {
			return 0;
		}
	}
	public String getFileId() {
		return fileId;
	}

	public int getNumberOfChunks() {
		return numberOfChunks;
	}

	public FileInfo(String fileId, int numberOfChunks, long size) {
		this.fileId = fileId;
		this.numberOfChunks = numberOfChunks;
		this.backedUpChunks = new HashMap<Integer, ArrayList<Header>>();
	}
	
	
	
}