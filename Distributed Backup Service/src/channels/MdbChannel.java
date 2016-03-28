package channels;

import java.io.IOException;
import java.util.concurrent.ThreadLocalRandom;

import data.Data;
import messages.Header;
import messages.Message;
import peers.Peer;

public class MdbChannel extends Channel{
	public MdbChannel(String mdbAddress, String mdbPort) throws IOException {
		super(mdbAddress, mdbPort);
		this.thread = new MdbThread();
	}
	
	public void handlePutChunk(Header header, byte[] body) throws InterruptedException {
		// If file was backed up by this peer
		if (Data.getBackedUpFiles().get(header.getFileId()) != null) 
			return;
		//save chunk
		try {
			Peer.getStorage().saveChunk(header, body);
		} catch (IOException e) {
			System.out.println("Could not save the chunk number " + header.getChunkNo() + "from file " + header.getFileId());
			return;
		}
		//reply
		Header replyHeader = new Header(Message.STORED, header.getVersion(),
				Peer.getServerId(), header.getFileId(), header.getChunkNo(), null);
		Message reply = new Message(Peer.getMcChannel().getSocket(), Peer.getMcChannel().getAddress(), replyHeader, null);
		int timeout = ThreadLocalRandom.current().nextInt(0, 400);
		Thread.sleep(timeout);
		new Thread(reply).start();
		System.out.println("Replying...");
	}
	
	public class MdbThread extends Thread {
		public void run() {
			System.out.println("Listening the MDB channel...");
			while(true) {
				try {
					socket.joinGroup(address);
					// separate data
					byte[] data = rcvMultiCastData();
					Message message = Message.getMessageFromData(data);
					Header header = message.getHeader();
					byte[] body = message.getBody();
					
					//analyzing data
					if(!Peer.getServerId().equals(header.getSenderId())) {
						switch (header.getMsgType()) {
						case Message.PUTCHUNK:
							System.out.println("Received PUTCHUNK");
							McChannel.setReceivedPutchunk(true);
							handlePutChunk(header, body);
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
}
