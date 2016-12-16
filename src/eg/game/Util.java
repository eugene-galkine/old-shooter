package eg.game;

import java.io.IOException;

import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

public class Util
{
	/*
	 * Util Classes
	 */
	
	public static class Textures
	{
		public Texture t;
		public String name;
		
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
	
	public static class Bullet
	{
		public int x, y;
		public float dir;
		public int weaponID;
		boolean isSpecial;
		
		public Bullet (float _dir, int _x, int _y, int _weaponID, boolean _isSpecial)
		{
			x = _x;
			y = _y;
			dir = _dir;
			weaponID = _weaponID;
			isSpecial = _isSpecial;
		}
		
		public java.awt.Rectangle getRect ()
		{
			return new java.awt.Rectangle(x, y, 8, 8);
		}
	}
	
	public static class EBullet extends Bullet
	{
		int owner;
		
		public EBullet(float _dir, int _x, int _y, int _weaponID, int id, boolean _isSpecial)
		{
			super(_dir, _x, _y, _weaponID, _isSpecial);
			owner = id;
		}
	}
	
	public static class Message
	{
		public String text;
		public int timer;
		
		public Message (String _text)
		{
			text = _text;
			timer = 500;
		}
	}
	
	public static interface Useable
	{
		abstract public void use ();
	}
	
	public static interface Useable1
	{
		abstract public void use (int x, int y, int owner);
	}
	
	public static interface Drawable
	{
		abstract public void draw (int x, int y, float dir);
	}
}
