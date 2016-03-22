package messages;

public class Message {
	private Header header;
	private String body;
	
	public Message(Header header) {
		this.header = header;
		this.body = "";
	}
	
	public void setBody(String body) {
		this.body = body;
	}
}
