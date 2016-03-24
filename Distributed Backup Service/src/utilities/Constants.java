package utilities;

import java.security.MessageDigest;

public class Constants {
	public static final String IP = "225.0.0.0";
	public static final int MC_PORT = 4444;
	public static final int MDB_PORT = 4445;
	public static final int MDR_PORT = 4446;
	
	public static final char CR = (char) 0x0D;
	public static final char LF = (char) 0x0A;
	public static final String CRLF = CR + LF + "";

	public static final String FILES_ROOT = "./files/";
	public static final String CHUNKS_ROOT = "chunks";
	
	public static final int CHUNK_SIZE = 64 * 1000;
	public static final int MAX_CHUNK_RETRY = 5;
	public static final int DEFAULT_WAITING_TIME = 1000;
	
	public static final int MESSAGE_TYPE = 0;
	public static final int VERSION = 1;
	public static final int SENDER_ID = 2;
	public static final int FILE_ID = 3;
	public static final int CHUNK_NO = 4;
	public static final int REPLICATION_DEG = 5;
	public static final int DATA = 6;
	
}
