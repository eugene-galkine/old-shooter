package eg.game.weapons;

import eg.game.Util.Useable1;
import eg.game.Util.Drawable;

public class Throwable
{
	int x, y, ownerID;
	float velocity, timer, mass;
	double xv, yv;
	double dir;
	boolean instant;
	Useable1 explosion;
	Drawable draw;
	
	public Throwable (int _x, int _y, float _mass, float _dir, float _speed, float time, boolean _instant, Useable1 explode, int owner, Drawable _draw)
	{
		x = _x;
		y = _y;
		mass = _mass;
		dir = Math.toRadians(_dir  - 90);
		velocity = _speed;
		timer = time;
		instant = _instant;
		explosion = explode;
		xv = Math.cos(dir);
		yv = Math.sin(dir);
		ownerID = owner;
		draw = _draw;
	}
	
	public void draw ()
	{
		draw.draw(x, y, (float)dir);
	}
	
	public boolean update (int movex, int movey, float time)
	{
		timer -= time;
		
		if (timer <= 0)
		{
			explosion.use(x, y, ownerID);
			return true;
		}
			
		x += velocity * xv;
		y += velocity * yv;
		
		x += movex;
		y += movey;
		
		velocity -= mass;
		
		if (velocity < 0)
			velocity = 0;
		
		return false;
	}
	
	public int getX()
	{
		return x;
	}
	
	public int getY()
	{
		return y;
	}
	
	public double getDir ()
	{
		return dir;
	}
	
	public void setDir (double x)
	{
		dir = x;
		xv = Math.cos(dir);
		yv = Math.sin(dir);
	}
	
	public float getVelocity ()
	{
		return velocity;
	}
	
	public void explode ()
	{
		explosion.use(x, y, ownerID);
	}
	
	public boolean isInstant()
	{
		return instant;
	}
	
	public double getxVel ()
	{
		return xv;
	}
	
	public double getyVel ()
	{
		return yv;
	}
}
