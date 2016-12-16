package eg.engine.sprites;

import org.newdawn.slick.opengl.Texture;

import eg.engine.render.Draw;

public abstract class EObject
{
	protected int x, y;
	protected Texture img;
	
	public void draw()
	{
		Draw.Image(img, x, y);
	}
	
	public int getX()
	{
		return x;
	}
	
	public int getY()
	{
		return y;
	}
	
	public void setX(int x)
	{
		this.x = x;
	}
	
	public void setY(int y)
	{
		this.y = y;
	}
}
