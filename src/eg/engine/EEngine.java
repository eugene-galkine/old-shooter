/**
 * @author Eugene Galkine
 */
package eg.engine;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import javax.swing.Timer;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

import eg.engine.render.Window;

public abstract class EEngine implements ActionListener
{
	protected static String GameName = "Game";
	protected State state;
	
	protected Timer timer;
	protected static int FixedUpdateDelay = 16;
	
	public void Start ()
	{
		Properties setting = new Properties();
		
		try 
		{
			//attempt to load settings, if fail use given defaults
			setting.load(new FileInputStream("config/resolution.cfg"));
			int w = new Integer(setting.getProperty("Width", "720"));
			int h = new Integer(setting.getProperty("Height", "480"));
			int freq = new Integer(setting.getProperty("Freq", "60"));
			int bpp = new Integer(setting.getProperty("BPP", "32"));
			boolean fullscreen = new Boolean(setting.getProperty("Fullscreen", "false"));
			Window.vsync = new Boolean(setting.getProperty("Vsync", "false"));
			Window.setDisplayMode(w, h, fullscreen, freq, bpp);
		} catch (FileNotFoundException e) 
		{
			//if no file found just set the display through window class which will create settings file
			Window.setDisplayMode(720, 480, false, 60, 32);
		} catch (IOException e) 
		{
			e.printStackTrace();
		}
		
		//set the window to not be resizable and the do other setting up
		Display.setResizable(false);
		Display.setTitle(GameName);
		init();
		timer = new Timer(FixedUpdateDelay, this);
		timer.start();
		loop();
		timer.stop();
		exit();
	}
	
	private void loop ()
	{
		while (!Display.isCloseRequested())
		{	
			Display.update();
			//if (Display.isActive())
			//{
				GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
				update();
				Display.sync(Display.getDisplayMode().getFrequency());
			//}
		}
	}
	
	abstract protected void init();//must be overridden
	
	protected void update()
	{
		//if (state != null)
		//state.draw();
		state.update();
	}
	
	public static void exit()
	{
		//can be called from anywhere, destroys the display and closes the program
		Display.destroy();
		System.exit(0);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) 
	{
		state.fixedUpdate();
	}
	
	protected void setTimer ()
	{
		timer.stop();
		timer = new Timer(FixedUpdateDelay, this);
		timer.start();
	}
}
