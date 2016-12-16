package eg.game;

import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;

import eg.engine.input.Input;
import eg.engine.render.Draw;
import eg.engine.render.Window;


public class MainMenu
{	
	private enum STATE
	{
		MAIN, VIDEO, KEYS, JOIN, SETTINGS, SETNAME, PAUSED, INGAMESETTINGS
	}
	
	private STATE state;
	private Button[] b;
	private TextIn input = null;
	private Label label = null;
	private String acceptedChars, Name, acceptedChars1;
	private boolean fullscreen;
	private DisplayMode[] modes;
	private int displayIndex, rebind, blinkTimer;
	private ScrollPane scroll;
	
	public MainMenu()
	{
		acceptedChars = "1234567890qwertyuiopasdfghjklzxcvbnm.QWERTYUIOPASDFGHJKL:ZXCVBNM";
		acceptedChars1 = "[]1234567890-=!@#$%^&*()_+`~qwertyuiop[]asdfghjkl;'zxcvbnm./ QWERTYUIOP{}|ASDFGHJKL:ZXCVBNM<>?";
		blinkTimer = 0;
		loadName();
		setStateMain();
	}

	public void resume ()
	{
		setStatePaused();
	}
	
	public void update ()
	{
		if (b == null)
			return;
		for (int i = 0; i < b.length; i++)
		{
			Rectangle r = new Rectangle(b[i].x, b[i].y, b[i].width, b[i].height);
			if (r.contains(Mouse.getX(), (Display.getHeight() - Mouse.getY())))
				b[i].selected = true;
			else
				b[i].selected = false;
			
			GL11.glColor3f(0.5f,0.5f,0.5f);
			Draw.Quad(b[i].x, b[i].y, b[i].width + b[i].x, b[i].height + b[i].y);
			if (!b[i].selected)
			{
				GL11.glColor3f(0.2f,0.2f,0.2f);
				Draw.Quad(b[i].x + 5, b[i].y + 5, b[i].width + b[i].x - 5, b[i].height + b[i].y - 5);
			}
			GL11.glColor3f(1,1,1);
			Draw.String(b[i].text, b[i].x + (b[i].width / 2) - ((20 * b[i].text.length()) / 2), b[i].y + ((b[i].height / 2) - 15), 30);
		}
		
		if (state == STATE.SETNAME || state == STATE.JOIN)
		{
			if (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) && Keyboard.isKeyDown(Keyboard.KEY_V))
			{
				try
				{
					String text = (String) Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor);
					boolean accepted = true;
					for (int c = 0; c < text.length(); c++)
						if (state == STATE.SETNAME && !acceptedChars1.contains("" + text.charAt(c)))
						{
							accepted = false;
							break;
						} else if (state == STATE.JOIN && !acceptedChars.contains("" + text.charAt(c)))
						{
							accepted = false;
							break;
						}
					
					if (accepted)
						input.text += text;
				} catch (Exception e)
				{
					e.printStackTrace();
				} 
			}
			while (Keyboard.next())
			{
				if (Keyboard.getEventKeyState() && !input.message)
				{
					if (Keyboard.getEventKey() == Keyboard.KEY_BACK && input.text.length() > 0)
						input.text = input.text.substring(0, input.text.length() - 1);
					else if (state == STATE.SETNAME && acceptedChars1.contains("" + Keyboard.getEventCharacter()) && input.text.length() < 15d)
						input.text += Keyboard.getEventCharacter();
					else if (state == STATE.JOIN && acceptedChars.contains("" + Keyboard.getEventCharacter()))
						input.text += Keyboard.getEventCharacter();
				}
			}
			
			GL11.glColor3f(0.5f,0.5f,0.5f);
			Draw.Quad(input.x, input.y, input.width + input.x, input.height + input.y);
			GL11.glColor3f(0.1f,0.1f,0.1f);
			Draw.Quad(input.x + 5, input.y + 5, input.width + input.x - 5, input.height + input.y - 5);
			GL11.glColor3f(1,1,1);
			Draw.String(input.text + ((blinkTimer <= 0 ) ? "_" : ""), input.x + (input.width / 2) - ((40 * input.text.length()) / 2), input.y + ((input.height / 2) - 30), 60);
		} else if (state == STATE.VIDEO)
		{
			GL11.glColor3f(0.5f,0.5f,0.5f);
			Draw.Quad(label.x, label.y, label.width + label.x, label.height + label.y);
			GL11.glColor3f(0.1f,0.1f,0.1f);
			Draw.Quad(label.x + 5, label.y + 5, label.width + label.x - 5, label.height + label.y - 5);
			GL11.glColor3f(1,1,1);
			Draw.String(label.text, label.x + (label.width / 2) - ((20 * label.text.length()) / 2), label.y + ((label.height / 2) - 15), 30);
		} else if (state == STATE.KEYS)
		{
			GL11.glColor3f(0.5f,0.5f,0.5f);
			Draw.Quad(scroll.x, scroll.y, scroll.w + scroll.x, scroll.h + scroll.y);
			GL11.glColor3f(0.1f,0.1f,0.1f);
			Draw.Quad(scroll.x + 5, scroll.y + 5, scroll.w + scroll.x - 5, scroll.h + scroll.y - 5);
			GL11.glColor3f(1,1,1);
			
			for (int i = 0; i < scroll.lines.length; i++)
			{
				//button
				Rectangle r = new Rectangle(scroll.lines[i].b.x, scroll.lines[i].b.y, scroll.lines[i].b.width, scroll.lines[i].b.height);
				if (r.contains(Mouse.getX(), (Display.getHeight() - Mouse.getY())))
					scroll.lines[i].b.selected = true;
				else
					scroll.lines[i].b.selected = false;
				
				GL11.glColor3f(0.5f,0.5f,0.5f);
				Draw.Quad(scroll.lines[i].b.x, scroll.lines[i].b.y, scroll.lines[i].b.width + scroll.lines[i].b.x, scroll.lines[i].b.height + scroll.lines[i].b.y);
				if (!scroll.lines[i].b.selected)
				{
					GL11.glColor3f(0.2f,0.2f,0.2f);
					Draw.Quad(scroll.lines[i].b.x + 5, scroll.lines[i].b.y + 5, scroll.lines[i].b.width + scroll.lines[i].b.x - 5, scroll.lines[i].b.height + scroll.lines[i].b.y - 5);
				}
				GL11.glColor3f(1,1,1);
				Draw.String(scroll.lines[i].b.text, scroll.lines[i].b.x + (scroll.lines[i].b.width / 2) - ((20 * scroll.lines[i].b.text.length()) / 2), scroll.lines[i].b.y + ((scroll.lines[i].b.height / 2) - 15), 30);
				
				GL11.glColor3f(0.5f,0.5f,0.5f);
				Draw.Quad(scroll.lines[i].b.x - (scroll.lines[i].b.width * 2), scroll.lines[i].b.y,  scroll.lines[i].b.x, scroll.lines[i].b.height + scroll.lines[i].b.y);
				GL11.glColor3f(0.15f,0.15f,0.15f);
				Draw.Quad(scroll.lines[i].b.x - (scroll.lines[i].b.width * 2) + 5, scroll.lines[i].b.y + 5, scroll.lines[i].b.x - 5, scroll.lines[i].b.height + scroll.lines[i].b.y - 5);
				GL11.glColor3f(1,1,1);
				Draw.String(scroll.lines[i].name + ": " + scroll.lines[i].key, scroll.lines[i].b.x - (scroll.lines[i].b.width * 2) + (scroll.lines[i].b.width / 2) - ((20 * scroll.lines[i].b.text.length()) / 2), scroll.lines[i].b.y + ((scroll.lines[i].b.height / 2) - 15), 30);
			}
			
			if (rebind != -1)
			{
				while (Keyboard.next())
					if (Keyboard.getEventKeyState())
					{
						Input.keys[rebind] = Keyboard.getEventKey();
						scroll.lines[rebind].key = Keyboard.getKeyName(Input.keys[rebind]);
						scroll.lines[rebind].b.text = "Rebind";
						rebind = -1;
					}
			}
		}
		
		while (Mouse.next())
			if (Mouse.getEventButtonState())
				if (Mouse.getEventButton() == 0)
				{
					for (int i = 0; i < b.length; i++)
						if (b[i].selected)
							ButtonClicked(i);
					if (state == STATE.KEYS)
						for (int i = 0; i < scroll.lines.length; i++)
							if (scroll.lines[i].b.selected && rebind != i)
							{
								if (rebind != -1)
								{
									scroll.lines[rebind].b.text = "Rebind";
									scroll.lines[rebind].key = Keyboard.getKeyName(Input.keys[i]);
								}
								scroll.lines[i].b.text = "Cancel";
								scroll.lines[i].key = "Press Any Key";
								rebind = i;
							}
							else if (scroll.lines[i].b.selected)
							{
								scroll.lines[i].b.text = "Rebind";
								scroll.lines[i].key = Keyboard.getKeyName(Input.keys[i]);
								rebind = -1;
							}
				}
	}
	
	public void fixedUpdate()
	{
		if (input != null)
			input.timeDown();
		
		if (blinkTimer < -25)
			blinkTimer = 25;
		else
			blinkTimer--;
	}
	
	private void ButtonClicked(int i)
	{
		switch (state)
		{
		case MAIN:
			if (i == 0)//join
			{
				setStateJoin();
			} else if (i == 1)//settings
			{
				setStateSetting();
			} else if (i == 2)//exit
				System.exit(0);
			break;
		case VIDEO:
			if (i == 0)//res++
			{
				displayIndex++;
				if (displayIndex == modes.length)
					displayIndex = 0;
				label.text = modes[displayIndex].toString();
			} else if (i == 1)//res--
			{
				displayIndex--;
				if (displayIndex == -1)
					displayIndex = modes.length - 1;
				label.text = modes[displayIndex].toString();
			} else if (i == 2)//vsync
			{
				Window.vsync = !Window.vsync;
				b[2].text = Window.vsync ? "VSync: true" : "VSync: false";
			} else if (i == 3)//fullscreen
			{
				fullscreen = !fullscreen;
				b[3].text = fullscreen ? "Fullscreen: true" : "Fullscreen: false";
			} else if (i == 4)//apply
			{
				Window.setDisplayMode(modes[displayIndex].getWidth(), modes[displayIndex].getHeight(), fullscreen, modes[displayIndex].getFrequency(), modes[displayIndex].getBitsPerPixel());
				Game.instance.resized();
				setStateVideo();
			} else if (i == 5)//back
			{
				if (!Game.instance.paused)
					setStateSetting();
				else
					setStateinGameSetting();
			}
			break;
		case KEYS:
			if (i == 0)//back
			{
				if (!Game.instance.paused)
					setStateSetting();
				else
					setStateinGameSetting();
			} else if (i == 1)//save
				Input.save();
			break;
		case JOIN:
			if (i == 0)//join
			{
				if (Game.instance.joinServer(input.text))
				{
					setStatePaused();
					Game.instance.setID(-1);
					Game.instance.resized();
					//Game.instance.Spawn();
					Game.instance.joiningServer();
				}
				else if (!input.message)
					input.setFeedback("invalid ip");
			} else if (i == 1)//back
				setStateMain();
			break;
		case SETTINGS:
			if (i == 0)//set name
			{
				setStateSetName();
			} else if (i == 1)//controls
			{
				setStateKeys();
			}  else if (i == 2)//video
			{
				setStateVideo();
			} else if (i == 3)//back
				setStateMain();
			break;
		case SETNAME:
			if (i == 0)//set name
			{	
				if (input.text == null || input.text.length() == 0)
					input.setFeedback("Please enter a name");
				else if (input.text.contains(","))
					input.setFeedback("Name cannot contain a comma");
				else
				{
					if (!input.message)
					{
						Name = input.text;
						saveName();
						Game.instance.setName(Name);
						input.setFeedback("Name saved");
					}
				}
			} else if (i == 1)//back
				setStateSetting();
			break;
		case INGAMESETTINGS:
			if (i == 0)//controls
			{
				setStateKeys();
			} else if (i == 1)//video
			{
				setStateVideo();
			} else if (i == 2)//back
			{
				setStatePaused();
			}
			break;
		case PAUSED:
			if (i == 0)//resume
			{
				Game.instance.paused = false;
				Game.instance.setInMenu(false);
			} else if (i == 1)//settings
			{
				setStateinGameSetting();
			} else if (i == 2)//exit
			{
				Game.instance.disconnect();
				Game.instance.setName(Name);
				setStateMain();
			}
			break;
		}
	}
	
	private void setStateKeys()
	{
		state = STATE.KEYS;
		b = new Button[2];
		int x = Display.getWidth() / 5;
		int y = Display.getHeight() - (Display.getHeight() / 6);
		int w = Display.getWidth() / 5;
		int h = Display.getHeight() / 11;
		
		scroll = new ScrollPane(Display.getWidth() / 8, Display.getHeight() / 11, Display.getWidth() - (Display.getWidth() / 4), Display.getHeight() - (Display.getHeight() / 4));
		
		b[0] = new Button(x, y, w, h, "Back");
		x += (Display.getWidth() / 5);
		b[1] = new Button(x, y, w, h, "Save");
		
		input = null;
		label = null;
		rebind = -1;
	}

	public void setStateinGameSetting ()
	{
		state = STATE.INGAMESETTINGS;
		b = new Button[3];
		int x = Display.getWidth() / 4;
		int y = Display.getHeight() / 8 + (Display.getHeight() / 8);
		int w = Display.getWidth() / 2;
		int h = Display.getHeight() / 7;
		
		b[0] = new Button(x, y, w, h, "Controls");
		y = Display.getHeight() / 8 + ((Display.getHeight() / 8) * 3);
		b[1] = new Button(x, y, w, h, "Video");
		y = Display.getHeight() / 8 + ((Display.getHeight() / 8) * 5);
		b[2] = new Button(x, y, w, h, "Back");
		input = null;
		label = null;
		scroll = null;
	}
	
	public void setStatePaused()
	{
		state = STATE.PAUSED;
		b = new Button[3];
		int x = Display.getWidth() / 4;
		int y = Display.getHeight() / 8 + (Display.getHeight() / 8);
		int w = Display.getWidth() / 2;
		int h = Display.getHeight() / 7;
		
		b[0] = new Button(x, y, w, h, "Resume");
		y = Display.getHeight() / 8 + ((Display.getHeight() / 8) * 3);
		b[1] = new Button(x, y, w, h, "Settings");
		y = Display.getHeight() / 8 + ((Display.getHeight() / 8) * 5);
		b[2] = new Button(x, y, w, h, "Main Menu");
		input = null;
		label = null;
		scroll = null;
	}
	
	public void setStateMain ()
	{
		state = STATE.MAIN;
		b = new Button[3];
		
		int x = Display.getWidth() / 4;
		int y = Display.getHeight() / 8 + (Display.getHeight() / 8);
		int w = Display.getWidth() / 2;
		int h = Display.getHeight() / 7;
		
		b[0] = new Button(x, y, w, h, "Join Server");
		y = Display.getHeight() / 8 + ((Display.getHeight() / 8) * 3);
		b[1] = new Button(x, y, w, h, "Settings");
		y = Display.getHeight() / 8 + ((Display.getHeight() / 8) * 5);
		b[2] = new Button(x, y, w, h, "Exit");
		input = null;
		label = null;
		scroll = null;
	}
	
	public void setStateJoin ()
	{
		state = STATE.JOIN;
		b = new Button[2];
		int x = Display.getWidth() / 4;
		int y = Display.getHeight() / 7;
		int w = Display.getWidth() / 2;
		int h = Display.getHeight() / 7;
		
		input = new TextIn(x, y, w, h);
		y = Display.getHeight() / 7 + ((Display.getHeight() / 7) * 2);
		b[0] = new Button(x, y, w, h, "Connect");
		y = Display.getHeight() / 7 + ((Display.getHeight() / 7) * 4);
		b[1] = new Button(x, y, w, h, "Back");
		label = null;
		scroll = null;
	}
	
	public void setStateSetting ()
	{
		state = STATE.SETTINGS;
		b = new Button[4];
		int x = Display.getWidth() / 4;
		int y = Display.getHeight() / 9;
		int w = Display.getWidth() / 2;
		int h = Display.getHeight() / 9;
		
		b[0] = new Button(x, y, w, h, "Edit Name");
		y = Display.getHeight() / 9 + ((Display.getHeight() / 9) * 2);
		b[1] = new Button(x, y, w, h, "Controls");
		y = Display.getHeight() / 9 + ((Display.getHeight() / 9) * 4);
		b[2] = new Button(x, y, w, h, "Video");
		y = Display.getHeight() / 9 + ((Display.getHeight() / 9) * 6);
		b[3] = new Button(x, y, w, h, "Back");
		input = null;
		label = null;
		scroll = null;
	}
	
	private void setStateSetName()
	{
		state = STATE.SETNAME;
		b = new Button[2];
		int x = Display.getWidth() / 4;
		int y = Display.getHeight() / 7;
		int w = Display.getWidth() / 2;
		int h = Display.getHeight() / 7;
		
		input = new TextIn(x, y, w, h, Name);
		y = Display.getHeight() / 7 + ((Display.getHeight() / 7) * 2);
		b[0] = new Button(x, y, w, h, "Save");
		y = Display.getHeight() / 7 + ((Display.getHeight() / 7) * 4);
		b[1] = new Button(x, y, w, h, "Back");
		label = null;
		scroll = null;
	}
	
	private void setStateVideo()
	{
		state = STATE.VIDEO;
		fullscreen = Display.isFullscreen();
		try
		{
			modes = Display.getAvailableDisplayModes();
			DisplayMode current = Display.getDisplayMode();
			for (int i = 0; i < modes.length; i++)
			{
				if (modes[i].getWidth() == current.getWidth() && modes[i].getHeight() == current.getHeight()
						&& modes[i].getFrequency() == current.getFrequency() && modes[i].getBitsPerPixel() == current.getBitsPerPixel())
				{
					displayIndex = i;
					break;
				}
			}
		} catch (LWJGLException e)
		{
			e.printStackTrace();
		}
		b = new Button[6];
		int x = Display.getWidth() / 4;
		int y = Display.getHeight() / 11;
		int w = Display.getWidth() / 2;
		int h = Display.getHeight() / 11;
		
		int w1 = w / 4;
		b[0] = new Button(x - w1, y, w1, h, "<");
		label = new Label(x, y, w1 * 4, h, modes[displayIndex].toString());
		b[1] = new Button(x + (w1 * 4), y, w1, h, ">");
		y = Display.getHeight() / 11 + ((Display.getHeight() / 11) * 2);
		b[2] = new Button(x, y, w, h, Window.vsync ? "VSync: true" : "VSync: false");
		y = Display.getHeight() / 11 + ((Display.getHeight() / 11) * 4);
		b[3] = new Button(x, y, w, h, fullscreen ? "Fullscreen: true" : "Fullscreen: false");
		y = Display.getHeight() / 11 + ((Display.getHeight() / 11) * 6);
		y = Display.getHeight() / 11 + ((Display.getHeight() / 11) * 8);
		b[4] = new Button(x + w/2, y, w/2, h, "Apply");
		b[5] = new Button(x, y, w/2, h, "Back");
		scroll = null;
	}
	
	private void loadName() //loads the player name
	{
		try {
			BufferedReader read = new BufferedReader(new FileReader("config/playerID.cfg"));
			String x1;
			
			if ((x1 = read.readLine()) != null)
			{
				Name = x1;
				Game.instance.setName(Name);
			}
			read.close();
		} catch (IOException e) {
			Game.instance.setName("player1");
			Name = "player1";
			//e.printStackTrace();
		}
	}
	
	private void saveName () //saves player name
	{
		try {
			FileWriter file = new FileWriter("config/playerID.cfg");
			file.write(Name);
			file.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * private classes
	 */
	
	private class Button
	{
		private int x, y, width, height;
		private String text;
		private boolean selected;
		
		public Button (int _x, int _y, int _w, int _h, String _t)
		{
			x = _x;
			y = _y;
			width = _w;
			height = _h;
			text = _t;
			selected = false;
		}
	}
	
	private class TextIn
	{
		private int x, y, width, height, timer;
		private String text, textOld;
		private boolean message = false;
		
		public TextIn (int _x, int _y, int _w, int _h)
		{
			x = _x;
			y = _y;
			width = _w;
			height = _h;
			text = "localhost";
		}
		
		public TextIn (int _x, int _y, int _w, int _h, String def)
		{
			x = _x;
			y = _y;
			width = _w;
			height = _h;
			text = def;
		}
		
		public void setFeedback (String t)
		{
			timer = 30;
			message = true;
			textOld = text;
			text = t;
		}
		
		public void timeDown ()
		{
			if (!message)
				return;
			timer--;
			if (timer <= 0)
			{
				message = false;
				text = textOld;
			}
		}
	}
	
	private class Label
	{
		private int x, y, width, height;
		private String text;
		
		public Label (int _x, int _y, int _w, int _h, String _t)
		{
			x = _x;
			y = _y;
			width = _w;
			height = _h;
			text = _t;
		}
	}
	
	private class ScrollPane
	{
		private ScrollLine[] lines;
		public int x, y, w, h;
		
		public ScrollPane (int _x, int _y, int _w, int _h)
		{
			x = _x;
			y = _y;
			w = _w;
			h = _h;
			lines = new ScrollLine[Input.keys.length];
			
			_h = _h / 10;
			
			for (int i= 0; i < lines.length; i++)
				lines[i] = new ScrollLine(Input.actionNames[i], Keyboard.getKeyName(Input.keys[i]), x + (2*w/3), y + (_h * i), w/3, _h);
		}
	}
	
	private class ScrollLine
	{
		public String name;
		public String key;
		public Button b;
		
		public ScrollLine (String _name, String _key, int _x, int _y, int _w, int _h)
		{
			name = _name;
			key = _key;
			b = new Button(_x, _y, _w, _h, "Rebind");
		}
	}
}
