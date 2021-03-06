package app;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;

import Exceptions.ArgsException;
import utilities.Constants;

public class Client {
	private InetAddress adress;
	private int port_number;
	private String oper;
	private ArrayList<String> args;
	private DatagramSocket socket;
	
	Client(String [] args) throws UnknownHostException, SocketException, ArgsException {	
		this.adress = InetAddress.getByName(args[0]);
		this.port_number = Integer.parseInt(args[1]);
		
		this.oper = args[2];
		this.args = new ArrayList<>();
		for (int i = 3; i < args.length; i++)
			this.args.add(args[i]);
		
		this.socket = new DatagramSocket();
		
		if (!checkOperAndArgs()) throw new ArgsException("Oper and Opnd doesn't match");	
	}
	
	
	private boolean checkOperAndArgs() {
		switch (this.oper) {
		case "lookup":
			if(this.args.size() != 1)
				return false;
			break;
		case "register":
			if(this.args.size() != 2)
				return false;
			break;
		default:
			return false;
		}
		return true;
	}

	
	public void sendRequest() throws IOException {	
		String request = oper;
		for (int i = 0; i < this.args.size(); i++)
			request += " " + this.args.get(i);
		byte[] buf = request.getBytes();
		DatagramPacket packet = new DatagramPacket(buf, buf.length, this.adress, port_number);
		this.socket.send(packet);
		
		buf = new byte[utilities.Constants.MAX_MSG_SIZE];
		packet = new DatagramPacket(buf, buf.length);
		this.socket.receive(packet);
		String response = new String(packet.getData(), 0, packet.getLength());
		System.out.println("'" + response + "' response was received.");
	}
	
	
	public static void main(String [] args) throws IOException, ArgsException {
		if (args.length != 4 && args.length != 5) throw new ArgsException("Program was called with the wrong number of arguments (" + args.length + ")");
		Client cl = new Client(args);
		cl.sendRequest();
	}

}
