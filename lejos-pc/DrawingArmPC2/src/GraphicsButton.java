import java.awt.Graphics;
import java.awt.Color;

public class GraphicsButton
{
	private int x,y,width,height;
	private String text;
	private Color color;

	public GraphicsButton(int x, int y, int width, int height, Color color, String text) //x and y are for the upper left corner of the rectangular button
	{
		this.x=x;
		this.y=y;
		this.width=width;
		this.height=height;
		this.color=color;
		this.text=text;
	}

	public boolean isWithin(int mouseX, int mouseY)
	{
		if(mouseX>=x && mouseX<=(x+width) && mouseY>=y && mouseY<=(y+height))
			return true;
		else
			return false;
	}

	public void setColor(Color color)
	{
		this.color=color;
	}

	public void setSize(int width, int height)
	{
		this.width=width;
		this.height=height;
	}

	public void setPosition(int x, int y)
	{
		this.x=x;
		this.y=y;
	}

	public void setText(String text)
	{
		this.text=text;
	}

	public int getX()
	{
		return x;
	}

	public int getY()
	{
		return y;
	}

	public int getWidth()
	{
		return width;
	}

	public int getHeight()
	{
		return height;
	}

	public Color getColor()
	{
		return color;
	}

	public String getText()
	{
		return text;
	}

	public void draw(Graphics g)
	{
		g.setColor(color);
		g.drawRect(x,y,width,height);
		g.drawString(text,x+5,y+13);
	}
}
