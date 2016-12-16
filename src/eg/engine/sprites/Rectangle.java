package eg.engine.sprites;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.opengl.Texture;

import eg.engine.render.Draw;

public class Rectangle 
{
	public int x1, x2, y1, y2;
	public Texture t;
	public String textureName = "";
	public boolean isSelected = false;
	public float rot = 0;
	
	public Rectangle(int _x1, int _y1, int _x2, int _y2)
	{
		x1 = _x1;
		y1 = _y1;
		x2 = _x2;
		y2 = _y2;
		if (x1 > x2)
		{
			int temp = x1;
			x1 = x2;
			x2 = temp;
		}
		if (y1 > y2)
		{
			int temp = y1;
			y1 = y2;
			y2 = temp;
		}
	}
	
	public void draw (int scrolx, int scroly)
	{
		if (isSelected)
		{
			GL11.glColor3d(1, 0.1, 0.5);
			if (rot == 0)
				Draw.Quad(x1 + scrolx, y1 + scroly, x2 + scrolx, y2 + scroly);
			else
				Draw.Quad(x1 + scrolx, y1 + scroly, x2 + scrolx, y2 + scroly, rot);
			GL11.glColor3d(1, 1, 1);
		}
		else if (t == null)
			if (rot == 0)
				Draw.Quad(x1 + scrolx, y1 + scroly, x2 + scrolx, y2 + scroly);
			else
				Draw.Quad(x1 + scrolx, y1 + scroly, x2 + scrolx, y2 + scroly, rot);
		else
			Draw.Image(t, x1 + scrolx, y1 + scroly, x2 + scrolx, y2 + scroly, rot);
	}
	
	public java.awt.Rectangle getRectangle (int scrolx, int scroly)
	{
		return new java.awt.Rectangle(x1 + scrolx, y1 + scroly, x2 - x1, y2 - y1);
	}
}
