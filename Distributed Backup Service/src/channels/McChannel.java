package channels;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

import data.ChunkInfo;
import data.Data;
import messages.Header;
import messages.Message;
import peers.Peer;
import subprotocols.Backup;
import utilities.Constants;
import utilities.Utilities;

public class McChannel extends Channel {
	ArrayList<Message> storedReplies;
	static boolean putchunkWhileWaiting = false;
	public McChannel(String mcAddress, String mcPort) throws IOException {
		super(mcAddress, mcPort);
		this.thread = new MulticastThread();
		this.storedReplies = new ArrayList<>();
	}
	
	private void handleGetChunk(Header header) throws InterruptedException, IOException {
		byte[] body = Data.getChunkBody(header.getFileId(), header.getChunkNo());		
		Header replyHeader = new Header(Message.CHUNK, Peer.getServerId(),
				Peer.getServerId(), header.getFileId(), header.getChunkNo(), null);
		Message reply = new Message(Peer.getMdrChannel().getSocket(), Peer.getMdrChannel().getAddress(), replyHeader, body);
		int timeout = ThreadLocalRandom.current().nextInt(0, 400);
		Thread.sleep(timeout);
		new Thread(reply).start();
	}
	private void handleDelete(Header header) {
		Data.clearStoredChunks(header.getFileId());
		File file =  new File(Constants.FILES_ROOT + Constants.CHUNKS_ROOT + "/" + header.getFileId() + "/");
		if (file.isDirectory())
			Utilities.deleteFolder(file);
	}
	private void handleRemoved(Header header) throws InterruptedException {
		ChunkInfo chunkInfo = Data.removeFromReceivedStoreMessages(header);
		if (chunkInfo != null) {
			putchunkWhileWaiting = false;
			int timeout = ThreadLocalRandom.current().nextInt(0, 400);
			Thread.sleep(timeout);
			if (putchunkWhileWaiting) {
				handleRemoved(header);
				return;
			}
			prepareChunk(chunkInfo);
		} else {
			System.out.println("Chunk info is null.");
		}
	}
	private void prepareChunk(ChunkInfo chunkInfo) {
		String fileName = Constants.CHUNKS_ROOT + "/" + chunkInfo.getFileId() + "/" + chunkInfo.getChunkNo() + ".data";
		String chunkPath = Constants.FILES_ROOT + fileName;
		
		byte[] chunk = new byte[0];
		try {
			chunk = Files.readAllBytes(Paths.get(chunkPath));
		} catch (IOException e) {
			System.out.println("Could not read bytes from " + chunkPath);
		}
		Header header = new Header(Message.PUTCHUNK, Constants.PROTOCOL_VERSION, Peer.getServerId(), chunkInfo.getFileId(), "" + chunkInfo.getChunkNo(), "" + chunkInfo.getReplicationDeg());
		Backup.sendChunk(header, chunk);
	}

	public class MulticastThread extends Thread {
		public void run() {
			System.out.println("Listening the MC channel...");
			while(true) {
				try {
					socket.joinGroup(address);
					byte[] data = rcvMultiCastData();
					Message message = Message.getMessageFromData(data);
					Header header = message.getHeader();
					if(!Peer.getServerId().equals(header.getSenderId())) {
						switch (header.getMsgType()) {
						case Message.GETCHUNK:
							if (!Data.chunkIsStored(header.getFileId(), Integer.parseInt(header.getChunkNo()))) {
								System.out.println("Chunk is not stored");
								break;
							}
							handleGetChunk(header);
							break;
						case Message.STORED:
							storedReplies.add(message);
							Data.addToReceivedStoreMessages(header);
							break;
						case Message.DELETE:
							handleDelete(header);
							break;
						}
					} 
					switch (header.getMsgType()) {
						case Message.REMOVED:
							System.out.println("Received REMOVED");
							handleRemoved(header);
							break;
					}
					socket.leaveGroup(address);
				} catch (IOException | InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public static void setReceivedPutchunk(boolean putchunkWhileWaiting) {
		McChannel.putchunkWhileWaiting = putchunkWhileWaiting;
	}
	public ArrayList<Message> getStoredReplies() {
		return storedReplies;
	}
	public static void sendRemoved(ChunkInfo chunkInfo) {
		Header header = new Header(Message.REMOVED, Peer.getServerId(),
				Peer.getServerId(), chunkInfo.getFileId(), chunkInfo.getChunkNo() + "", null);
		Message message = new Message(Peer.getMcChannel().getSocket(), Peer.getMcChannel().getAddress(), header, null);
		new Thread(message).start();
	}
}
