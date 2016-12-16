package eg.game.net;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class ServerFileSender extends Thread
{
	private OutputStream out;
	private FileInputStream f;
	
	@SuppressWarnings("resource")
	public ServerFileSender(InetAddress ip, int port, FileInputStream file) throws UnknownHostException, IOException
	{
		//System.out.println(ip);
		out = new Socket(ip, port).getOutputStream();
		f = file;
		//fileName =  filen;
	}
	
	@Override
	public void run()
	{
		try
		{
			int x = 0;
	        while(true)
	        {
				x = f.read();
	            if(x == -1)
	            	break;
	            out.write(x);
	        }
	        out.close();
	        f.close();
	       // f = new  FileInputStream("assets/serverMaps/" + fileName + ".map");
		} catch (Exception e)
		{
			e.printStackTrace();
		} catch (Throwable e)
		{
			e.printStackTrace();
		}
	}
}
