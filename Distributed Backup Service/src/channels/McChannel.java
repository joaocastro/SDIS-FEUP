package channels;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

import database.Storage;
import messages.Header;
import messages.Message;
import peers.Peer;

public class McChannel extends Channel {
	ArrayList<Message> storedReplies;
	boolean waitingReplies = false;


	public McChannel(String mcAddress, String mcPort) throws IOException {
		super(mcAddress, mcPort);
		this.thread = new MulticastThread();
		this.storedReplies = new ArrayList<>();
	}
	
	private void handleGetChunk(Header header) throws InterruptedException, IOException {
		byte[] body = Storage.getChunkBody(header.getFileId(), header.getChunkNo());		
		Header replyHeader = new Header(Message.CHUNK, Peer.getServerId(),
				Peer.getServerId(), header.getFileId(), header.getChunkNo(), null);
		Message reply = new Message(Peer.getMdrChannel().getSocket(), Peer.getMdrChannel().getAddress(), replyHeader, body);
		int timeout = ThreadLocalRandom.current().nextInt(0, 400);
		Thread.sleep(timeout);
		new Thread(reply).start();
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
							if (!Storage.chunkIsStored(header.getFileId(), Integer.parseInt(header.getChunkNo()))) {
								System.out.println("Chunk is not stored");
								break;
							}
							handleGetChunk(header);
							break;
						case Message.STORED:
							if (waitingReplies)
								storedReplies.add(message);
							break;
						}
					}
					socket.leaveGroup(address);
				} catch (IOException | InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public ArrayList<Message> getStoredReplies() {
		return storedReplies;
	}

	public void setWaitingReplies(boolean waitingReplies) {
		this.waitingReplies = waitingReplies;
	}
}
