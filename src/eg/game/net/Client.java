package eg.game.net;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import eg.game.Game;

public class Client extends Thread
{
	private InetAddress IPAddress;
	private DatagramSocket clientSocket;
	private Game game;
	private int port;
	
	public Client (Game _game, String ip) throws Exception
	{
		game = _game;
		clientSocket = new DatagramSocket();
		if (ip.contains(":"))
		{
			port = new Integer(ip.substring(ip.indexOf(":") + 1, ip.length()));
			ip = ip.substring(0, ip.indexOf(":"));
		} else
			port = 1331;
		
		IPAddress = InetAddress.getByName(ip);
		Runtime.getRuntime().addShutdownHook(new Thread() {
	        @Override
	        public void run() {
	        	sendData (("2" + game.getID()).getBytes());
	        	//clientSocket.close();
	        }
	    });
	}
	
	public void run() 
	{
		byte[] data;
		
        while (true)
        {
        	data = new byte[1024];
        	DatagramPacket packet = new DatagramPacket(data, data.length);
        	try
			{
				clientSocket.receive(packet);
			} catch (IOException e)
			{
				e.printStackTrace();
			}
        	recievedPacket(packet.getData());
        }
    }
	
	public void recievedPacket(byte[] in)
	{
		String data = new String(in).trim();
		//System.out.println(data);
		if (data.startsWith("0"))//move
		{
			data = data.substring(1, data.length());
			game.moveMPPlayer(data);
		} else if(data.startsWith("5"))//bullet
		{
			data = data.substring(1, data.length());
			game.newBullet(data);
		} else if(data.startsWith("9"))//gernade
		{
			data = data.substring(1, data.length());
			game.gernade(data);
		} else if(data.startsWith("6"))//death
		{
			data = data.substring(1, data.length());
			game.death(data);
		} else if(data.startsWith("7"))//respawned
		{
			data = data.substring(1, data.length());
			game.respawned(data);
		} else if(data.startsWith("8"))//message
		{
			data = data.substring(1, data.length());
			game.newMessage(data);
		} else if(data.startsWith("1"))//disconnect
		{
			data = data.substring(1, data.length());
			game.removeMPPlayer(new Integer(data));
		} else if (data.startsWith("2")) //connected
		{
			data = data.substring(1, data.length());
			game.addMPPlayer(data);
		} else if (data.startsWith("3")) //recieving ID
		{
			data = data.substring(1, data.length());
			game.setID(new Integer(data));
			//game.Spawn();
		} else if (data.startsWith("4")) //bad name
		{
			data = data.substring(1, data.length());
			game.setName(data);
		}  else if (data.startsWith("A")) //map name
		{
			ClientFileReciever cfr = new ClientFileReciever(data.substring(1, data.length()));
			int port = cfr.BindPort();
			sendData(("A" + Game.instance.getID() + "," + port).getBytes());
			cfr.run();
		}
	}
	
	public void sendData (byte[] data)
	{
		DatagramPacket packet = new DatagramPacket(data, data.length, IPAddress, port);
		try
		{
			if (!clientSocket.isClosed())
				clientSocket.send(packet);
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
