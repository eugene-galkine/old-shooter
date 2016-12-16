package eg.game.net;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

import eg.game.Game;

public class ClientFileReciever extends Thread
{
	String name;
	ServerSocket ss = null;
	InputStream in = null;
    Socket clientSocket;
    FileOutputStream fos = null;
    int port = 1337;
	
	public ClientFileReciever(String fileName)
	{
		name = fileName;
	}
	
	public int BindPort ()
	{
		boolean validPort = false;
		while (!validPort)
		{
			validPort = true;
			try
			{
				ss = new ServerSocket(port);
			} catch (IOException e)
			{
				validPort = false;
				port++;
				//e.printStackTrace();
				System.out.println("Port in use, trying a different one");
			}
		}
		
		return port;
	}
	
	@Override
	public void run()
	{
		try
		{
			clientSocket = ss.accept();
			in = clientSocket.getInputStream();
			fos = new FileOutputStream("assets/maps/" + name + ".map");
		} catch (IOException e)
		{
			e.printStackTrace();
		}
        
		try
		{
			int x = 0;
	        while(true)
	        {
	            x = in.read();
	            if(x==-1)
	            	break;
	            fos.write(x);
	        }
	        //fos.flush();
	        fos.close();
	        ss.close();
	        in.close();
	        clientSocket.close();
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		
		Game.instance.readyToLoadMap(name);
	}
}
