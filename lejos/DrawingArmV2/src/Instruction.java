import lejos.util.Delay;

public class Instruction
{
	public static final int EMPTY	= 0;
	public static final int MOVE	= 1;
	public static final int RAISE	= 2;
	public static final int LOWER	= 3;
	public static final int EXIT	= 4;

	private int type = EMPTY;
	private float angle1, angle2;

	public synchronized int getType()
	{
		return type;
	}

	public synchronized void setType(int type)
	{
		this.type = type;
	}

	public void setAngle1(float angle1)
	{
		this.angle1 = angle1;
	}

	public void setAngle2(float angle2)
	{
		this.angle2 = angle2;
	}

	public float getAngle1()
	{
		return angle1;
	}

	public float getAngle2()
	{
		return angle2;
	}

	public String toString()
	{
		if(type==MOVE)
			return "Type:"+type+" "+getAngle1()+" "+getAngle2();
		else
			return "Type:"+type;
	}
}
