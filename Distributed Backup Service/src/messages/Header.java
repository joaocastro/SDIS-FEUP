package messages;

public class Header {
	private String messageType;
	private String version;
	private String senderId;
	private String fileId;
	private String chunkNo;
	private String replicationDeg;
	
	public Header(String messageType, String version, String senderId,
			String fileId, String chunkNo, String replicationDeg) {
		this.messageType = messageType;
		this.version = version;
		this.senderId = senderId;
		this.fileId = fileId;
		this.chunkNo = chunkNo;
		this.replicationDeg = replicationDeg;
	}
	
	public String getMessageType() {
		return messageType;
	}

	public String getVersion() {
		return version;
	}

	public String getSenderId() {
		return senderId;
	}

	public String getFileId() {
		return fileId;
	}

	public String getChunkNo() {
		return chunkNo;
	}

	public String getReplicationDeg() {
		return replicationDeg;
	}
}
