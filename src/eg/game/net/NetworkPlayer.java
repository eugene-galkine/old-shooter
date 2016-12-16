package eg.game.net;

public class NetworkPlayer
{
	private int x, y;
	private String name;
	private int id;
	private float dir;
	private boolean alive;
	
	public NetworkPlayer (String _name, int _id)
	{
		name = _name;
		setAlive(true);
		setID(_id);
	}

	public int getID()
	{
		return id;
	}

	public void setID(int id)
	{
		this.id = id;
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

	public void setDir(float mdir)
	{
		dir = mdir;
	}
	
	public float getDir ()
	{
		return dir;
	}

	public String getName()
	{
		return name;
	}

	public boolean isAlive()
	{
		return alive;
	}

	public void setAlive(boolean alive)
	{
		this.alive = alive;
	}
}
