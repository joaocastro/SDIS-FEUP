package peers;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import exceptions.ArgsException;

public class Peer {
	private String serverId;
	
	private MulticastSocket mcSocket;
	private InetAddress mcAddress;
	private int mcPort;
	
	private InetAddress mdbAddress;
	private int mdbPort;
	
	private InetAddress mdrAddress;
	private int mdrPort;
	
	private MulticastThread mcthread;
	
	public Peer(String serverId, String mcAddress, String mcPort,
			String mdbAddress, String mdbPort, String mdrAddress,
			String mdrPort) throws IOException {
		this.serverId = serverId;
		
		this.mcAddress = InetAddress.getByName(mcAddress);
		this.mcPort = Integer.parseInt(mcPort);
		this.mcSocket = new MulticastSocket(this.mcPort);
		this.mcthread = new MulticastThread();
		this.mcthread.start();
		
		
		this.mdbAddress = InetAddress.getByName(mdbAddress);
		this.mdbPort = Integer.parseInt(mdbPort);
		
		this.mdrAddress = InetAddress.getByName(mdrAddress);
		this.mdrPort = Integer.parseInt(mdrPort);
		
	}

	public static void main(String[] args) throws ArgsException, IOException {
		if (args.length != 7)
			throw new ArgsException("peer <Server ID> <MC> <MC port> <MDB> <MDB port> <MDR> <MDR port>");
		Peer peer = new Peer(args[0], args[1], args[2], args[3], args[4], args[5], args[6]);
	}
	
	private class MulticastThread extends Thread {
		public void run() {
			
		}
	}
}
