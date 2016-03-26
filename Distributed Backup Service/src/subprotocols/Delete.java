package subprotocols;

import java.io.File;

import messages.Header;
import messages.Message;
import peers.Peer;
import utilities.Constants;
import utilities.Utilities;

public class Delete extends Thread {
	String fileName;
	
	public Delete(String fileName) {
		this.fileName = fileName;
	}

	public void run() {
		File file = new File(Constants.FILES_ROOT + fileName);
		String fileId = Utilities.getFileId(file);
		Header header = new Header(Message.DELETE, Constants.VERSION + "", Peer.getServerId(), fileId, null, null);
		Message deleteMsg = new Message(Peer.getMcChannel().getSocket(), Peer.getMcChannel().getAddress(), header, null);
		new Thread(deleteMsg).start();
		if(file.delete()){
			System.out.println(file.getName() + " is deleted!");
		}else{
			System.out.println("Delete operation failed.");
		}
	}
}
