package eg.game.net;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Calendar;

//import org.lwjgl.Sys;

public class Server extends Thread
{
	DatagramSocket socket;
	private ArrayList<MPPlayer> players;
	private int port = -1;
	BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
	int temp = 0;
	private FileInputStream file;
	private String fileName;
	
	public static void main (String[] args) 
	{
		try
		{
			new Server().run();
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public Server () throws Exception
	{
		players = new ArrayList<MPPlayer>();
	}
	
	public void run()
	{
		System.out.println("Enter the file name of the map you want the server to run: ");
		try
		{
			fileName = input.readLine();
			file = new  FileInputStream("assets/serverMaps/" + fileName + ".map");
			file.close();
		} catch (FileNotFoundException e)
		{
			e.printStackTrace();
			System.exit(-1);
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		
		System.out.println("Please enter a port to run the server on: ");
		try
		{
			port = new Integer(input.readLine());
			socket = new DatagramSocket(port);
			System.out.println("Server Started on port " + port);
		} catch (Exception e1)
		{
			e1.printStackTrace();
			System.exit(-1);
		}

		byte[] data;
        while (true)
        {
        	data = new byte[64];
        	DatagramPacket packet = new DatagramPacket(data, data.length);
        	try
			{
        		socket.receive(packet);
			} catch (IOException e)
			{
				e.printStackTrace();
			}
        	
        	try
			{
				dataRecieved(packet);
			} catch (Exception e)
			{
				e.printStackTrace();
			}
        }
    }
	
	private void dataRecieved(DatagramPacket packet) throws UnknownHostException, IOException
	{
		String data = new String(packet.getData()).trim();
		if (data.length() > temp)
		{
			System.out.println("biggest packet so far: " + data.length());
			temp = data.length();
		}
		if (data.startsWith("0"))//move
		{
			data = data.substring(1, data.length());
			
			int pID = new Integer(data.substring(0, data.indexOf(",")));
			
			for (int i = 0; i < players.size(); i++)//client timeout
			{
				if (players.get(i).id == pID)
				{
					players.get(i).lastMessage = getTime();
				} else if (players.get(i).lastMessage - getTime() < -4000)
				{
					int playerID = players.get(i).id;
					players.remove(i);
					System.out.println("kicked player " + pID + " because they stopped sending data");
					
					for (int j = 0; j < players.size(); j++)
						sendData(("1" + playerID).getBytes(), players.get(j).address, players.get(j).port);
					i--;
				} else
					sendData(("0" + data).getBytes(), players.get(i).address, players.get(i).port);
			}
			
			//for (int i = 0; i < players.size(); i++)
			//	if (!data.startsWith(players.get(i).id + ""))
			//		sendData(("0" + data).getBytes(), players.get(i).address, players.get(i).port);
		} else if (data.startsWith("3"))//shoot bullet
		{
			data = data.substring(1, data.length());
			for (int i = 0; i < players.size(); i++)
				if (!data.startsWith(players.get(i).id + ""))
					sendData(("5" + data).getBytes(), players.get(i).address, players.get(i).port);
		} else if (data.startsWith("7"))//gernade
		{
			data = data.substring(1, data.length());
			for (int i = 0; i < players.size(); i++)
				sendData(("9" + data).getBytes(), players.get(i).address, players.get(i).port);
		} else if (data.startsWith("4"))//dead	
		{
			data = data.substring(1, data.length());
			for (int i = 0; i < players.size(); i++)
				sendData(("6" + data).getBytes(), players.get(i).address, players.get(i).port);
		} else if (data.startsWith("5"))//respawned
		{
			data = data.substring(1, data.length());
			for (int i = 0; i < players.size(); i++)
				sendData(("7" + data).getBytes(), players.get(i).address, players.get(i).port);
			
		} else if (data.startsWith("6"))//message
		{
			data = data.substring(1, data.length());
			for (int i = 0; i < players.size(); i++)
				sendData(("8" + data).getBytes(), players.get(i).address, players.get(i).port);
			
		} else if (data.startsWith("1"))//initial connect
		{
			data = data.substring(1, data.length());
			
			int count = 1;
			for (int i = 0; i < players.size(); i++)
				if (players.get(i).name.equals(data))
				{
					if (count > 1)
					{
						data = data.substring(0, data.length() - (3 + (count / 10)));
					}
					
					data += "(" + count + ")";
					count++;
					i = 0;
				}
			
			if (count > 1)
				sendData(("4" + data).getBytes(), packet.getAddress(), packet.getPort());
			
			int id = 0;
			for (int i = 0; i < players.size(); i++)
			{
				if (players.get(i).id == id)
				{
					id++;
					i = -1;
				}
			}
			
			for (int i = 0; i < players.size(); i++)
				sendData(("2" + data + "," + id).getBytes(), players.get(i).address, players.get(i).port);
			
			sendData(("3" + id).getBytes(), packet.getAddress(), packet.getPort());
			
			for (int i = 0; i < players.size(); i++)
				sendData(("2" + players.get(i).name + "," + players.get(i).id).getBytes(), packet.getAddress(), packet.getPort());
			
			players.add(new MPPlayer(data, packet.getAddress(), packet.getPort(), id));
			players.get(players.size() - 1).lastMessage = getTime();
			sendData(("A" + fileName).getBytes(), packet.getAddress(), packet.getPort());
		} else if (data.startsWith("2"))//disconnect
		{
			data = data.substring(1, data.length());
			int index = new Integer(data);
			
			for (int i = 0; i < players.size(); i++)
			{
				if (players.get(i).id == index)
				{
					players.remove(i);
					i--;
				}
				else
					sendData(("1" + index).getBytes(), players.get(i).address, players.get(i).port);
			}
		} else if (data.startsWith("A"))//ready to recieve map
		{
			int pID = new Integer(data.substring(1, data.indexOf(",")));
			data = data.substring(data.indexOf(",") + 1, data.length());
			
			for (int i = 0; i < players.size(); i++)
			{
				if (players.get(i).id == pID)
				{
					file = new  FileInputStream("assets/serverMaps/" + fileName + ".map");
					players.get(players.size() - 1).sender = new ServerFileSender(packet.getAddress(), new Integer(data), file);
					players.get(players.size() - 1).sender.run();
				}
			}
		}
	}

	public void sendData (byte[] data, InetAddress IPAddress, int port)
	{
		DatagramPacket packet = new DatagramPacket(data, data.length, IPAddress, port);
		try
		{
			socket.send(packet);
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public long getTime() 
	{
		return Calendar.getInstance().getTimeInMillis();
	}
	
	private class MPPlayer
	{
		public String name;
		public InetAddress address;
		public int port;
		public int id;
		public long lastMessage;
		public ServerFileSender sender;
		
		public MPPlayer (String _name, InetAddress ip, int p, int _id)
		{
			name = _name;
			address = ip;
			port = p;
			id = _id;
		}
	}
}
