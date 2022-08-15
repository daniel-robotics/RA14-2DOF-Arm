/**
 * @(#)DrawingArmPC3.java
 *
 * DrawingArmPC3 application
 *
 * @author
 * @version 1.00 2016/4/28
 */

import javax.swing.JFrame;
import javax.swing.JPanel;
import danui.GraphicButton;
import danui.GraphicPanel;
import java.util.ArrayList;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseEvent;
import java.text.NumberFormat;
import java.text.DecimalFormat;
import java.math.RoundingMode;

public class DrawingArmPC3
{
	//Graphical constants
	public static final int WINDOW_WIDTH	= 800;
	public static final int WINDOW_HEIGHT	= 800;
	public static final int X_TRANSLATION	= 180;	//must be a multiple of 20
	public static final int Y_TRANSLATION	= 240;	//must be a multiple of 20
	public static final double ZOOM_FACTOR_1= 2.0;
	public static final double ZOOM_FACTOR_2= 4.0;
	public static final String WINDOW_TITLE	= "Drawing Arm V2 (Unfficially RA 15)";
	public static final Color LIGHT_BLUE	= new Color(0,240,255);
	public static final Color GRID_GRAY		= new Color(50,50,50);
	public static DecimalFormat form		= new DecimalFormat();

	public static final int DRAW_LINES	= 1;
	public static final int DRAW_POINTS	= 2;
	public static final int DRAW_RECT	= 3;
	public static final int DRAW_CIRCLE = 4;


	//Mechanical constants
	public static final int SEGMENT_1_LENGTH		= 40*3;		//millimeters physically, 1/3 pixels graphically. 1 Lego stud is 4 mm.
	public static final int SEGMENT_2_LENGTH		= 45*3;

	public static final int PIVOT_1_MIN_ANGLE		= -70;		//min angle limit, in degrees
	public static final int PIVOT_1_MAX_ANGLE		= 75;		//max angle limit, in degrees

	public static final int PIVOT_2_MIN_ANGLE		= 90;
	public static final int PIVOT_2_MAX_ANGLE		= 180;

	private static int arc1x, arc1y, arc1start, arc2x, arc2y, arc2start, minRadius, maxRadius;
	private static boolean saved = false;
	private static JFrame f;

	public static void main(String[] args)
	{
		AngleCalculator.setMechanicalConstants(SEGMENT_1_LENGTH, SEGMENT_2_LENGTH, PIVOT_1_MIN_ANGLE, PIVOT_1_MAX_ANGLE, PIVOT_2_MIN_ANGLE, PIVOT_2_MAX_ANGLE, false);
		calculateArcs();

        form.setRoundingMode(RoundingMode.HALF_EVEN);
        form.setMinimumFractionDigits(1);
        form.setMaximumFractionDigits(2);

		f = new JFrame(WINDOW_TITLE);
		f.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
     	f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
     	f.setResizable(false);
        f.add(new MainPanel());
		f.setVisible(true);
    }

	private static class MainPanel extends GraphicPanel implements MouseListener, MouseMotionListener
	{
		private int x0,y0,x1,y1,x2,y2,t1,t2,w,h,mx,my,mxo,myo;
		private int drawMode = DRAW_LINES;
		private boolean mousePressed=false, liftPen=true;
		private ArrayList<GraphicButton> buttons = new ArrayList<GraphicButton>(6);
		public ArrayList<Shape> shapes = new ArrayList<Shape>(1);


		public MainPanel()
		{
			super(WINDOW_WIDTH, WINDOW_HEIGHT, Color.DARK_GRAY, true);
			setBaseTransform(GraphicPanel.CENTERED_FLIP_Y);
			setTranslation(WINDOW_WIDTH/-2+X_TRANSLATION, WINDOW_HEIGHT/-2+Y_TRANSLATION);
			setScaler(ZOOM_FACTOR_1, ZOOM_FACTOR_1);
   	   		addMouseListener(this);
   	   		addMouseMotionListener(this);

   	   		buttons.add(new GraphicButton(350, 12, 85, 17, LIGHT_BLUE, "Shape: Line"));
   	   		buttons.add(new GraphicButton(350, 32, 85, 17, LIGHT_BLUE, "Load File"));
   	   		buttons.add(new GraphicButton(450, 12, 85, 17, LIGHT_BLUE, "Undo Shape"));
   	   		buttons.add(new GraphicButton(450, 32, 85, 17, LIGHT_BLUE, "Save File"));
   	   		buttons.add(new GraphicButton(550, 12, 85, 17, LIGHT_BLUE, "Lift Pen"));
   	   		buttons.add(new GraphicButton(550, 32, 85, 17, LIGHT_BLUE, "Send to NXT"));
		}

		public void paintComponent(Graphics g)
   	   	{
   	   		super.paintComponent(g);

			w = g.getClipBounds().width;
   	   		h = g.getClipBounds().height;
   	   		x0 = 0;
   	   		y0 = 0;
   	   		x1 = AngleCalculator.getX1();
   	   		x2 = AngleCalculator.getX2();
   	   		y1 = AngleCalculator.getY1();
   	   		y2 = AngleCalculator.getY2();
   	   		t1 = (int)AngleCalculator.getT1();
   	   		t2 = (int)AngleCalculator.getT2();
   	   		boolean outOfRange = AngleCalculator.outOfRange();

			//***** In "proper" coordinate space: *******


			//The background gridlines
   	   		g.setColor(GRID_GRAY);
   	   		for(int i=-Y_TRANSLATION; i<h; i+=20)
				g.drawLine(-X_TRANSLATION, i, w, i);	//lines parallel to x axis
			for(int i=-X_TRANSLATION; i<w; i+=20)
				g.drawLine(i, -Y_TRANSLATION , i, h);	//lines parallel to y axis

			//The coordinate axes
   	   		g.setColor(Color.BLACK);
			g.drawLine(-X_TRANSLATION, 0, w, 0);	//x axis
			g.drawLine(0, -Y_TRANSLATION , 0, h);	//y axis

			//The two arm segments
			g.setColor(Color.GREEN);
			g.drawLine(0, 0, x1, y1);
			g.drawLine(x1, y1, x2, y2);

			//The arcs indicating the pivot angles
			if(outOfRange) g.setColor(Color.RED);
			else g.setColor(Color.BLUE);
			g.drawArc(-20, -20, 40, 40, 0, -t1);
			g.drawArc(x1-20, y1-20, 40, 40, 180+t2-t1, -t2);

			//The arcs indicating the min and max drawing radii
			g.setColor(LIGHT_BLUE);
			g.drawArc(-minRadius, -minRadius, 2*minRadius, 2*minRadius, 0, 360);
			g.drawArc(-maxRadius, -maxRadius, 2*maxRadius, 2*maxRadius, 0, 360);

			//The arcs indicating the upper and lower drawing bounds within the two drawing radii
			g.setColor(LIGHT_BLUE);
			g.drawArc(arc1x, arc1y, 2*SEGMENT_2_LENGTH, 2*SEGMENT_2_LENGTH, arc1start, PIVOT_2_MAX_ANGLE-PIVOT_2_MIN_ANGLE);
			g.drawArc(arc2x, arc2y, 2*SEGMENT_2_LENGTH, 2*SEGMENT_2_LENGTH, arc2start, PIVOT_2_MAX_ANGLE-PIVOT_2_MIN_ANGLE);

			//The drawings
			g.setColor(Color.YELLOW);
			if(!shapes.isEmpty())
			for(Shape shape : shapes)
				shape.draw(g,0,0);

			//The preview line, if drawMode==DRAW_LINES
			if(drawMode == DRAW_LINES && !liftPen && !shapes.isEmpty())
			{
				g.setColor(Color.YELLOW);
				Shape last = getLastShape();
				if(last!=null)
					g.drawLine((int)last.getEndX(), (int)last.getEndY(), x2, y2);
			}
			//The preview rectangle, if drawMode==DRAW_RECT
			if(drawMode == DRAW_RECT && !liftPen && !shapes.isEmpty())
			{
				g.setColor(Color.YELLOW);
				Shape last = getLastShape();
				if(last!=null)
				{
					int x1 = (int)last.getEndX(), y1 = (int)last.getEndY();
					if(x1<x2)
					{
						if(y1<y2)
							g.drawRect(x1, y1, x2-x1, y2-y1);
						else
							g.drawRect(x1, y2, x2-x1, y1-y2);
					}
					else
					{
						if(y1<y2)
							g.drawRect(x2, y1, x1-x2, y2-y1);
						else
							g.drawRect(x2, y2, x1-x2, y1-y2);
					}
				}
			}
			//The preview circle, if drawMode==DRAW_CIRCLE
			if(drawMode == DRAW_CIRCLE && !liftPen && !shapes.isEmpty())
			{
				g.setColor(Color.YELLOW);
				Shape last = getLastShape();
				if(last!=null)
				{
					int x1 = (int)last.getEndX(), y1 = (int)last.getEndY();
					int midX = (x2+x1)/2, midY = (y2+y1)/2;
					int r = (int)Math.sqrt( (midX-x1)*(midX-x1) + (midY-y1)*(midY-y1) );
					g.drawOval((int)(midX-r), (int)(midY-r), (int)(2*r), (int)(2*r));
				}
			}

			//***** In "normal Java" coordinate space: ******

			replaceTransform(getBaseTransform(GraphicPanel.DEFAULT));
			x0 = (int)inverseConvertX(x0);
   	   		x1 = (int)inverseConvertX(x1);
   	   		x2 = (int)inverseConvertX(x2);
   	   		y0 = (int)inverseConvertY(y0);
   	   		y1 = (int)inverseConvertY(y1);
   	   		y2 = (int)inverseConvertY(y2);

			//The pivot angle text
			if(outOfRange) g.setColor(Color.RED);
			else g.setColor(LIGHT_BLUE);
			g.drawString(form.format(t1), x0+2, y0+12);
			g.drawString(form.format(t2), x1+2, y1+12);

			//The buttons
			for(GraphicButton button : buttons)
				button.draw(g);

			//The number of shapes/points
			g.setColor(LIGHT_BLUE);
			g.drawString("Shapes: "+shapes.size(), 25, 25);
		}



		public void mouseMoved(MouseEvent e)	//While the mouse is moving, calculate the angles so the graphical arm can still be displayed (and the preview line)
		{
			mxo=e.getX();	mx=(int)convertX(mxo);
			myo=e.getY();	my=(int)convertY(myo);
			AngleCalculator.calculateAngles(mx, my);

			repaint();
		}

		public void mouseDragged(MouseEvent e)	// If mouse is being dragged, and the button is set for Points, draw a continuous stream of points.
		{
			mxo=e.getX();	mx=(int)convertX(mxo);
			myo=e.getY();	my=(int)convertY(myo);
			AngleCalculator.calculateAngles(mx, my);

			if(drawMode == DRAW_POINTS)
			{
				addPoint(AngleCalculator.getX2(), AngleCalculator.getY2());
				liftPen = false;
				saved = false;
			}

			repaint();
		}

		public void mouseClicked(MouseEvent e)
		{
			if(e.getButton()==MouseEvent.BUTTON2 || e.getButton()==MouseEvent.BUTTON3)		// If right clicked, toggle zoom and do nothing else
			{
				if(getScalerX()==1) setScaler(ZOOM_FACTOR_2, ZOOM_FACTOR_2);
				else				setScaler(ZOOM_FACTOR_1, ZOOM_FACTOR_1);
				repaint();
				return;
			}

			mxo=e.getX();	mx=(int)convertX(mxo);
			myo=e.getY();	my=(int)convertY(myo);
			AngleCalculator.calculateAngles(mx, my);

			if(buttons.get(0).isWithin(mxo,myo))		// Change shape button
			{
				if(drawMode == DRAW_LINES)
				{
					buttons.get(0).setText("Shape: Points");
					drawMode = DRAW_POINTS;
				}
				else if(drawMode == DRAW_POINTS)
				{
					buttons.get(0).setText("Shape: Circle");
					drawMode = DRAW_CIRCLE;
				}
				else if(drawMode == DRAW_CIRCLE)
				{
					buttons.get(0).setText("Shape: Rectangle");
					drawMode = DRAW_RECT;
				}
				else if(drawMode == DRAW_RECT)
				{
					buttons.get(0).setText("Shape: Line");
					drawMode = DRAW_LINES;
				}
			}
			else if(buttons.get(1).isWithin(mxo,myo))	// Load file
			{
				ArrayList<Shape> temp = FileWorker.load();
				if(temp != null)
				{
					shapes = temp;
					saved = true;
				}
			}
			else if(buttons.get(2).isWithin(mxo,myo))	// Undo button
			{
				//liftPen = true;
				removeLastShape();
			}
			else if(buttons.get(3).isWithin(mxo,myo))	// Save file
			{
				FileWorker.save(shapes);
				saved = true;
			}
			else if(buttons.get(4).isWithin(mxo,myo))	// Lift pen
			{
				liftPen=true;
			}
			else if(buttons.get(5).isWithin(mxo,myo))	// Send to NXT button was pressed
			{
				if(!saved)
				{
					FileWorker.save(shapes);
					saved = true;
				}
				NXTInterface.sendToNXT(shapes);
			}
			else										// Otherwise, treat the click as drawing
			{
				if(liftPen==true)						// If the Lift Pen button was just clicked, add a Point at that location
				{
					shapes.add(new Point(AngleCalculator.getX2(), AngleCalculator.getY2()));
					liftPen=false;
				}

				else									// Otherwise, check what the Shape button is set to and add a new shape accordingly
				{
					if(drawMode == DRAW_LINES)
						addLine(AngleCalculator.getX2(), AngleCalculator.getY2());
					else if(drawMode == DRAW_POINTS)
						addPoint(AngleCalculator.getX2(), AngleCalculator.getY2());
					else if(drawMode == DRAW_RECT)
						addRect(AngleCalculator.getX2(), AngleCalculator.getY2());
					else if(drawMode == DRAW_CIRCLE)
						addCircle(AngleCalculator.getX2(), AngleCalculator.getY2());
					saved = false;
				}

			}

			repaint();
		}

		public Shape getLastShape()
		{
			if(shapes.size()>0)
				return shapes.get(shapes.size()-1);
			else
				return null;
		}

		public void removeLastShape()
		{
			if(shapes.size()>0)
			{
				shapes.remove(shapes.size()-1);
				shapes.trimToSize();
			}
		}

		public int getPoints()
		{
			int points=0;
			for(Shape shape : shapes)
				points+=shape.getPoints();
			return points;
		}

		public void addPoint(double x, double y)		//Adds a Point shape, does not necessarily need to be connected to the other shapes
		{
			shapes.add(new Point(x, y));
		}

		public void addLine(double endX, double endY)	//Adds a Line shape, beginning at the previous shape's endpoint
		{
			if(shapes.size()==0)	//but if there hasn't been a previous shape to start at, just put a point there instead.
			{
				addPoint(endX,endY);
				return;
			}
			double startX = getLastShape().getEndX();
			double startY = getLastShape().getEndY();
			shapes.add(new Line(startX, startY, endX, endY));
		}

		public void addRect(double endX, double endY)	//Adds a Rectangle shape, beginning at the previous shape's endpoint
		{
			if(shapes.size()==0)	//but if there hasn't been a previous shape to start at, just put a point there instead.
			{
				addPoint(endX,endY);
				return;
			}
			double startX = getLastShape().getEndX();
			double startY = getLastShape().getEndY();
			shapes.add(new Rectangle(startX, startY, endX, endY));
		}

		public void addCircle(double endX, double endY)	//Adds a Circle shape, beginning at the previous shape's endpoint
		{
			if(shapes.size()==0)	//but if there hasn't been a previous shape to start at, just put a point there instead.
			{
				addPoint(endX,endY);
				return;
			}
			double startX = getLastShape().getEndX();
			double startY = getLastShape().getEndY();
			shapes.add(new Circle(startX, startY, endX, endY));
		}

		public void mousePressed(MouseEvent e){}
		public void mouseReleased(MouseEvent e){}
		public void mouseEntered(MouseEvent e){}
		public void mouseExited(MouseEvent e){}
	}

	private static void calculateArcs()
	{
		arc1x = (int)( SEGMENT_1_LENGTH*Math.cos(Math.toRadians(PIVOT_1_MIN_ANGLE)) - SEGMENT_2_LENGTH );
		arc1y = (int)( SEGMENT_1_LENGTH*Math.sin(Math.toRadians(PIVOT_1_MIN_ANGLE)) - SEGMENT_2_LENGTH );
		arc1start = 180+PIVOT_2_MIN_ANGLE-PIVOT_1_MIN_ANGLE;

		arc2x = (int)( SEGMENT_1_LENGTH*Math.cos(Math.toRadians(PIVOT_1_MAX_ANGLE)) - SEGMENT_2_LENGTH );
		arc2y = (int)( SEGMENT_1_LENGTH*Math.sin(Math.toRadians(PIVOT_1_MAX_ANGLE)) - SEGMENT_2_LENGTH );
		arc2start = 180+PIVOT_2_MIN_ANGLE-PIVOT_1_MAX_ANGLE;

		minRadius = (int)AngleCalculator.getMinRadius();
		maxRadius = (int)AngleCalculator.getMaxRadius();
	}
}
