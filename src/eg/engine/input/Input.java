package eg.engine.input;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import org.lwjgl.input.Keyboard;

public class Input 
{
	public static final int WALK_FORWARD = 0;
	public static final int WALK_BACKWARD = 1;
	public static final int WALK_LEFT = 2;
	public static final int WALK_RIGHT = 3;
	public static final int RELOAD = 4;
	public static final int START_TYPING = 5;
	public static final int PREV_WEAPON = 6;
	public static final int NEXT_WEAPON = 7;
	public static final int GERNADE = 8;
	
	public static int[] keys;
	public static String[] actionNames = {"Up", "Down", "Left", "Right", 
										  "Reload", "Chat", "Previous Weapon", "Next Weapon",
										  "Throw Gernade"};
	
	public static int[] actionDefaults = {Keyboard.KEY_W, Keyboard.KEY_S, Keyboard.KEY_A, Keyboard.KEY_D, 
										  Keyboard.KEY_R, Keyboard.KEY_T, Keyboard.KEY_Q, Keyboard.KEY_E,
										  Keyboard.KEY_G};
	
	public static void load ()
	{
		Properties setting = new Properties();
		
		try 
		{
			setting.load(new FileInputStream("config/input.cfg"));
		} catch (FileNotFoundException e) 
		{
			for (int i = 0; i < actionDefaults.length; i++)
			{
				setting.setProperty(actionNames[i], actionDefaults[i] + "");
			/*setting.setProperty(actionNames[WALK_FORWARD], Keyboard.KEY_W + "");
			setting.setProperty(actionNames[WALK_BACKWARD], Keyboard.KEY_S + "");
			setting.setProperty(actionNames[WALK_LEFT], Keyboard.KEY_A + "");
			setting.setProperty(actionNames[WALK_RIGHT], Keyboard.KEY_D + "");
			setting.setProperty(actionNames[RELOAD], Keyboard.KEY_R + "");
			setting.setProperty(actionNames[START_TYPING], Keyboard.KEY_T + "");
			setting.setProperty(actionNames[PAN], Keyboard.KEY_LSHIFT + "");
			setting.setProperty(actionNames[PREV_WEAPON], Keyboard.KEY_Q + "");
			setting.setProperty(actionNames[NEXT_WEAPON], Keyboard.KEY_E + "");*/
			}
			
			File file = new File("config/input.cfg");
			FileOutputStream fileOut;
			try
			{
				fileOut = new FileOutputStream(file);
				setting.store(fileOut, "Input");
			} catch (IOException e1)
			{
				e1.printStackTrace();
			}
			
			try
			{
				setting.load(new FileInputStream("config/input.cfg"));
			} catch (IOException e1)
			{
				e1.printStackTrace();
			}
		} catch (IOException e) 
		{
			e.printStackTrace();
		}
		
		keys = new int[actionDefaults.length];
		for (int i = 0; i < keys.length; i++)
		{
			try
			{
				keys[i] = new Integer(setting.getProperty(actionNames[i]));
			} catch (Exception e)
			{
				keys[i] = actionDefaults[i];
				setting.setProperty(actionNames[i], keys[i] + "");
				/*switch (i)
				{
				case WALK_FORWARD:
					keys[i] = Keyboard.KEY_W;
					setting.setProperty(actionNames[i], keys[i] + "");
					break;
				case WALK_BACKWARD:
					keys[i] = Keyboard.KEY_S;
					setting.setProperty(actionNames[i], keys[i] + "");
					break;
				case WALK_LEFT:
					keys[i] = Keyboard.KEY_A;
					setting.setProperty(actionNames[i], keys[i] + "");
					break;
				case WALK_RIGHT:
					keys[i] = Keyboard.KEY_D;
					setting.setProperty(actionNames[i], keys[i] + "");
					break;
				case RELOAD:
					keys[i] = Keyboard.KEY_R;
					setting.setProperty(actionNames[i], keys[i] + "");
					break;
				case START_TYPING:
					keys[i] = Keyboard.KEY_T;
					setting.setProperty(actionNames[i], keys[i] + "");
					break;
				case PAN:
					keys[i] = Keyboard.KEY_LSHIFT;
					setting.setProperty(actionNames[i], keys[i] + "");
					break;
				case PREV_WEAPON:
					keys[i] = Keyboard.KEY_Q;
					setting.setProperty(actionNames[i], keys[i] + "");
					break;
				case NEXT_WEAPON:
					keys[i] = Keyboard.KEY_E;
					setting.setProperty(actionNames[i], keys[i] + "");
					break;
				}*/
			}
		}
		
		try
		{
			setting.store(new FileOutputStream("config/input.cfg"), "Input");
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public static void save ()
	{
		Properties setting = new Properties();
		
		try 
		{
			for (int i = 0; i < keys.length; i++)
				setting.setProperty(actionNames[i], keys[i] + "");
			
			setting.store(new FileOutputStream("config/input.cfg"), "Input");
		} catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
}
