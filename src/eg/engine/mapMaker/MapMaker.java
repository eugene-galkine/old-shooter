package eg.engine.mapMaker;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

import eg.engine.State;
import eg.engine.render.Draw;
import eg.engine.sprites.Rectangle;

public class MapMaker extends State
{
	static ArrayList<eg.engine.sprites.Rectangle> rects;
	public Textures[] textures;
	public int[][] spawnPoints;
	int x, y, initialx = 0, initialy = 0, screenx = 0, screeny = 0, scrolx = 0, scroly = 0, texture = 0, selectedID = -1, offx, offy;
	boolean started = false, vsync= true, scroling = false;
	
	public MapMaker ()
	{
		rects = new ArrayList<eg.engine.sprites.Rectangle>();
		Display.setVSyncEnabled(vsync);
		load();
	}
	
	@Override
	public void update()
	{
		draw();
		x = Mouse.getX();
		y = Display.getHeight() - Mouse.getY() - 1;
		offx = 1900 - (Display.getWidth() / 2);
		offy = 2000 - (Display.getHeight() / 2);
		
		if (Mouse.isButtonDown(1))
		{
			if (!scroling)
			{
				scrolx = x;
				scroly = y;
				scroling = true;
			}
			
			screenx -= scrolx - x;
			screeny -= scroly - y;
			scrolx = x;
			scroly = y;
		} else
			scroling = false;
		
//		if (Mouse.isButtonDown(1))
//		{
//			java.awt.Rectangle temp;
//			selectedID = -1;
//			for (int i = 0; i < rects.size(); i++)
//			{
//				temp = new java.awt.Rectangle();
//				temp.height = Math.abs(rects.get(i).y1 - rects.get(i).y2);
//				temp.width = Math.abs(rects.get(i).x1 - rects.get(i).x2);
//				temp.x = (rects.get(i).x1 > rects.get(i).x2) ? rects.get(i).x2 : rects.get(i).x1;
//				temp.y = (rects.get(i).y1 > rects.get(i).y2) ? rects.get(i).y2 : rects.get(i).y1;
//				if (temp.contains(x - screenx, y - screeny) && selectedID == -1)
//				{
//					rects.get(i).isSelected = true;
//					selectedID = i;
//				}
//				else
//					rects.get(i).isSelected = false;
//			}
//		}
//		
		texture += Mouse.getDWheel() / 120;
			
		while (texture >= textures.length)
			texture -= textures.length;
		while (texture < 0)
			texture += textures.length;
		
		while (Keyboard.next())
		{
			if (Keyboard.getEventKeyState())
			{
				switch (Keyboard.getEventKey())
				{
				case Keyboard.KEY_ESCAPE://exit
					System.exit(0);
					break;
				case Keyboard.KEY_V://v-sync
					vsync = !vsync;
					Display.setVSyncEnabled(vsync);
					break;
				case Keyboard.KEY_C:
					rects = new ArrayList<Rectangle>();
					break;
				case Keyboard.KEY_Z://undo
					if (rects.size() > 0)
						rects.remove(rects.size() - 1);
					break;
				case Keyboard.KEY_T://texture
					if (selectedID != -1)
					{
						rects.get(selectedID).t = textures[texture].t;
						rects.get(selectedID).textureName = textures[texture].name;
						rects.get(selectedID).isSelected = false;
						textures[texture].used = true;
						selectedID = -1;
					} else
					{
						rects.get(rects.size() - 1).t = textures[texture].t;
						rects.get(rects.size() - 1).textureName = textures[texture].name;
						textures[texture].used = true;
					}
					break;
				case Keyboard.KEY_S://save
					saveMap();
					break;
				case Keyboard.KEY_L://load
					loadMap();
					break;
				case Keyboard.KEY_D:
					if (selectedID != -1)
					{
						rects.remove(selectedID);
						selectedID = -1;
					}
					break;
				case Keyboard.KEY_0:
					screenx = 0;
					screeny = 0;
					break;
				case Keyboard.KEY_LEFT:
					if (selectedID == -1)
						selectedID = rects.size() - 1;
					else
						rects.get(selectedID).isSelected = false;
					selectedID--;
					if (selectedID < 0)
						selectedID = rects.size() - 1;
					rects.get(selectedID).isSelected = true;
					break;
				case Keyboard.KEY_RIGHT:
					if (selectedID != -1)
						rects.get(selectedID).isSelected = false;
					
					selectedID++;
					if (selectedID > rects.size() - 1)
						selectedID = 0;
					rects.get(selectedID).isSelected = true;
					break;
				case Keyboard.KEY_1:
					if (selectedID == -1)
						selectedID = rects.size() - 1;
					rects.get(selectedID).isSelected = true;
					rects.get(selectedID).rot = 90;
					break;
				case Keyboard.KEY_2:
					if (selectedID == -1)
						selectedID = rects.size() - 1;
					rects.get(selectedID).isSelected = true;
					rects.get(selectedID).rot = 180;
					break;
				case Keyboard.KEY_3:
					if (selectedID == -1)
						selectedID = rects.size() - 1;
					rects.get(selectedID).isSelected = true;
					rects.get(selectedID).rot = 270;
					break;
				case Keyboard.KEY_4:
					if (selectedID == -1)
						selectedID = rects.size() - 1;
					rects.get(selectedID).isSelected = true;
					rects.get(selectedID).rot = 0;
					break;
				case Keyboard.KEY_UP:
					if (selectedID == -1)
						selectedID = rects.size() - 1;
					rects.get(selectedID).isSelected = true;
					rects.get(selectedID).rot += 1;
					break;
				case Keyboard.KEY_Q://add spawn point
					//int[][] temp = spawnPoints.clone();
					//spawnPoints = new int[temp.length + 1][2];
					//for (int i = 0; i < temp.length; i++)
					
					break;
				default:
					break;
				}
			}
		}
		
		if (Keyboard.isKeyDown(Keyboard.KEY_DOWN))
		{
			if (selectedID == -1)
				selectedID = rects.size() - 1;
			rects.get(selectedID).isSelected = true;
			rects.get(selectedID).rot -= 1;
		}
		
		if (Keyboard.isKeyDown(Keyboard.KEY_UP))
		{
			if (selectedID == -1)
				selectedID = rects.size() - 1;
			rects.get(selectedID).isSelected = true;
			rects.get(selectedID).rot += 1;
		}
		
		if (Keyboard.isKeyDown(Keyboard.KEY_R))
		{
			if (selectedID == -1)
			{
				selectedID = rects.size() - 1;
				rects.get(selectedID).isSelected = true;
			}
			
			rects.get(selectedID).rot = (float) -Math.toDegrees( Math.atan2((x - screenx) - (rects.get(selectedID).x1 + Math.abs((rects.get(selectedID).x1 - rects.get(selectedID).x2) / 2)),
					(y - screeny) - (rects.get(selectedID).y1 + Math.abs((rects.get(selectedID).y1 - rects.get(selectedID).y2) / 2))));
		}
		
		for (int i = 0; i < rects.size(); i++)
			rects.get(i).draw(screenx, screeny);
		
		if (Mouse.isButtonDown(0))
		{
			if (!started)
			{
				initialx = x;
				initialy = y;
				started = true;
			}
			Draw.Quad(initialx, initialy , x, y);
		} else if (started)
		{
			Draw.Quad(initialx, initialy, x, y);
			rects.add(new Rectangle(initialx - screenx, initialy - screeny, x - screenx, y - screeny));
			started = false;
		}
		
		GL11.glColor3f(0f, 0.5f, 0.8f);
		if (spawnPoints != null)
			for (int i = 0; i < spawnPoints.length; i++)//TODO: finish this
				Draw.Quad((-(spawnPoints[i][0] - 3) + screenx) + offx + 725, (screeny - ((spawnPoints[i][1] - 3))) + offy + 450, (-(spawnPoints[i][0] + 3) + screenx) + offx + 725, (screeny - ((spawnPoints[i][1] + 3))) + offy + 450);
				
		GL11.glColor3f(1, 1, 1);
		
		//System.out.println(screenx + ", " + screeny);
		
		Draw.String(textures[texture].name, 5, 5, 15);
	}

	@Override
	public void fixedUpdate()
	{
		
	}
	
	private void saveMap() 
	{
		try {
			FileWriter file = new FileWriter("assets/maps/test.map");
			
			for (Textures texture : textures)
				if (texture.used)
					file.write(texture.name + System.lineSeparator());
			
			file.write("-" + System.lineSeparator());
			
			file.write(spawnPoints.length + System.lineSeparator());
			for (int i = 0; i < spawnPoints.length; i++)
				file.write(spawnPoints[i][0] + System.lineSeparator() + spawnPoints[i][1] + System.lineSeparator());
			
			file.write("-" + System.lineSeparator());
			
			for (Rectangle rect : rects)
				file.write(rect.x1 + System.lineSeparator() + rect.y1 + System.lineSeparator() + rect.x2 + System.lineSeparator() + rect.y2 + System.lineSeparator() + rect.textureName + System.lineSeparator());
			
			file.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void loadMap() 
	{
		try {
			BufferedReader read = new BufferedReader(new FileReader("assets/maps/test.map"));
			String x1;
			rects = new ArrayList<Rectangle>();
			
			while ((x1 = read.readLine()) != null)
			{
				if (x1.equals("-"))
					break;
				
				for (Textures texture : textures)
					if (texture.name.equals(x1))
						texture.used = true;
			}
			
			//load spawn points
			x1 = read.readLine();
			spawnPoints = new int[new Integer(x1)][2];
			int i = 0;
			
			while ((x1 = read.readLine()) != null)
			{
				if (x1.equals("-"))
					break;
				
				spawnPoints[i][0] = new Integer(x1);
				x1 = read.readLine();
				spawnPoints[i][1] = new Integer(x1);
				
				i++;
			}
			
			while ((x1 = read.readLine()) != null)
			{
				rects.add(new Rectangle(new Integer(x1), new Integer(read.readLine()), new Integer(read.readLine()), new Integer(read.readLine())));
				String tn = read.readLine();
				rects.get(rects.size() - 1).textureName = tn;
				
				for (i = 0; i < textures.length; i++)
				{
					if (tn.isEmpty())
						break;
					
					if (textures[i].name.compareTo(tn) == 0)
						rects.get(rects.size() - 1).t = textures[i].t;
				}
			}
			read.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void load ()
	{
		File folder = new File("assets/images/environments");
		File[] listOfFiles = folder.listFiles();
		
		textures = new Textures[listOfFiles.length];
		
		for (int i = 0; i < listOfFiles.length; i++)
			if (listOfFiles[i].isFile())
			{
				textures[i] = new Textures(listOfFiles[i].getName());
				textures[i].load();
			}
	}
	
	
	private class Textures
	{
		public Texture t;
		public String name;
		public boolean used = false;
		
		public Textures (String _name)
		{
			name = _name;
		}
		
		public void load()
		{
			try
			{
				t = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("assets/images/environments/" + name));
			} catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}
}
