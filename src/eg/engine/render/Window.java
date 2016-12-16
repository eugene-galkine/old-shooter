/**
 * @author Eugene Galkine
 */
package eg.engine.render;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;

public class Window 
{
	public static boolean vsync = false;
	
	public static void setDisplayMode(int width, int height, boolean fullscreen, int freq, int bpp) 
	{
		try 
		{
			//try and set the video settings
			DisplayMode[] modes = Display.getAvailableDisplayModes();
			DisplayMode mode = modes[0];
			for (int i = 0; i < modes.length; i++)
			{
				if (modes[i].getWidth() == width && modes[i].getHeight() == height
						&& modes[i].getFrequency() == freq && modes[i].getBitsPerPixel() == bpp)
				{
					mode = modes[i];
					freq = mode.getFrequency();
				}
			}
			
			//destroy the display to reset it all
			Display.destroy();
			Display.setDisplayMode(mode);
			Display.setFullscreen(fullscreen);
			Display.create();
			//set up everything
			GL11.glMatrixMode(GL11.GL_PROJECTION);
			GL11.glMatrixMode(GL11.GL_MODELVIEW);
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			GL11.glDisable(GL11.GL_BLEND);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			GL11.glDisable(GL11.GL_DEPTH_TEST);
			GL11.glLoadIdentity();
			GL11.glOrtho(0, Display.getWidth(), Display.getHeight(), 0, 1, -1);
			
			Display.setVSyncEnabled(vsync);
			Draw.reload();
			
			Properties setting = new Properties();
			
			try
			{
				setting.setProperty("Width", mode.getWidth() + "");
				setting.setProperty("Height", mode.getHeight() + "");
				setting.setProperty("Freq", mode.getFrequency() + "");
				setting.setProperty("BPP", mode.getBitsPerPixel() + "");
				setting.setProperty("Fullscreen", fullscreen + "");
				setting.setProperty("Vsync", vsync + "");
				
				setting.store(new FileOutputStream("config/resolution.cfg"), "Video");
			} catch (FileNotFoundException e)
			{
				File file = new File("config/resolution.cfg");
				FileOutputStream fileOut;
				try
				{
					fileOut = new FileOutputStream(file);
					setting.store(fileOut, "Video");
				} catch (IOException e1)
				{
					e1.printStackTrace();
				}
			} catch (IOException e1)
			{
				e1.printStackTrace();
			}
		} catch (LWJGLException e) 
		{
			e.printStackTrace();
		}
	}
}
