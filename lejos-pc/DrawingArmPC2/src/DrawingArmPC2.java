/**
 * @(#)DrawingArmPC2.java
 *
 * DrawingArmPC2 application
 *
 * @author
 * @version 1.00 2016/4/28
 */

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.util.ArrayList;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseEvent;
import java.text.NumberFormat;
import java.text.DecimalFormat;
import java.math.RoundingMode;

public class DrawingArmPC2
{
	//Graphical constants
	public static final int WINDOW_WIDTH	= 600;
	public static final int WINDOW_HEIGHT	= 600;
	public static final int X_TRANSLATION	= 60;	//must be a multiple of 20
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

	public static int MAX_RADIUS, MIN_RADIUS;
	public static String filename = "";
	public static boolean saved = false;
	public static JFrame f;


	public static void main(String[] args)
	{
		AngleCalculator.setMechanicalConstants(SEGMENT_1_LENGTH, SEGMENT_2_LENGTH, PIVOT_1_MIN_ANGLE, PIVOT_1_MAX_ANGLE, PIVOT_2_MIN_ANGLE, PIVOT_2_MAX_ANGLE, false);
		MIN_RADIUS = (int)AngleCalculator.getMinRadius();
		MAX_RADIUS = (int)AngleCalculator.getMaxRadius();

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
		private boolean mousePressed=false, liftPen=false;
		private ArrayList<GraphicsButton> buttons = new ArrayList<GraphicsButton>(6);
		public ArrayList<Drawing> drawings = new ArrayList<Drawing>();;


		public MainPanel()
		{
			super(WINDOW_WIDTH, WINDOW_HEIGHT, Color.DARK_GRAY, true);
			setBaseTransform(GraphicPanel.CENTERED_FLIP_Y);
			setTranslation(WINDOW_WIDTH/-2+X_TRANSLATION, WINDOW_HEIGHT/-2+Y_TRANSLATION);
			setScaler(ZOOM_FACTOR_1, ZOOM_FACTOR_1);
   	   		addMouseListener(this);
   	   		addMouseMotionListener(this);

   	   		buttons.add(new GraphicsButton(100, 12, 85, 17, LIGHT_BLUE, "Shape: Line"));
   	   		buttons.add(new GraphicsButton(100, 32, 85, 17, LIGHT_BLUE, "Load File"));
   	   		buttons.add(new GraphicsButton(200, 12, 85, 17, LIGHT_BLUE, "Undo Shape"));
   	   		buttons.add(new GraphicsButton(200, 32, 85, 17, LIGHT_BLUE, "Save File"));
   	   		buttons.add(new GraphicsButton(300, 12, 85, 17, LIGHT_BLUE, "Lift Pen"));
   	   		buttons.add(new GraphicsButton(300, 32, 85, 17, LIGHT_BLUE, "Send to NXT"));
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

			//*****In "proper" coordinate space:*******


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
			g.drawArc(-MIN_RADIUS, -MIN_RADIUS, 2*MIN_RADIUS, 2*MIN_RADIUS, 0, 360);
			g.drawArc(-MAX_RADIUS, -MAX_RADIUS, 2*MAX_RADIUS, 2*MAX_RADIUS, 0, 360);

			//The drawings
			g.setColor(Color.YELLOW);
			if(!drawings.isEmpty())
			for(Drawing d : drawings)
				d.draw(g,0,0,false);

			//The preview line, if drawMode==DRAW_LINES
			if(drawMode == DRAW_LINES && !liftPen && !drawings.isEmpty())
			{
				g.setColor(Color.YELLOW);
				Shape last = getLastDrawing().getLastShape();
				if(last!=null)
					g.drawLine((int)last.getEndX(), (int)last.getEndY(), x2, y2);
			}
			//The preview rectangle, if drawMode==DRAW_RECT
			if(drawMode == DRAW_RECT && !liftPen && !drawings.isEmpty())
			{
				g.setColor(Color.YELLOW);
				Shape last = getLastDrawing().getLastShape();
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
			if(drawMode == DRAW_CIRCLE && !liftPen && !drawings.isEmpty())
			{
				g.setColor(Color.YELLOW);
				Shape last = getLastDrawing().getLastShape();
				if(last!=null)
				{
					int x1 = (int)last.getEndX(), y1 = (int)last.getEndY();
					int midX = (x2+x1)/2, midY = (y2+y1)/2;
					int r = (int)Math.sqrt( (midX-x1)*(midX-x1) + (midY-y1)*(midY-y1) );
					g.drawOval((int)(midX-r), (int)(midY-r), (int)(2*r), (int)(2*r));
				}
			}

			//*****In "normal Java" coordinate space:******

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
			for(GraphicsButton button : buttons)
				button.draw(g);

			//The number of shapes/points
			int shapes=0;
			if(!drawings.isEmpty()) shapes = getLastDrawing().getShapes();
			g.setColor(LIGHT_BLUE);
			g.drawString("Drawings: "+drawings.size(), w-180, h-27);
			g.drawString("Shapes in this drawing: "+shapes, w-180, h-10);
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
				if(drawings.isEmpty() || liftPen==true)	// If there are no drawings, or the Lift Pen button was just clicked, start a new drawing
				{
					drawings.add(new Drawing(AngleCalculator.getX2(), AngleCalculator.getY2()));
					liftPen=false;
				}
				else									// otherwise, we're ready to go so just add the point
				{
					getLastDrawing().addPoint(AngleCalculator.getX2(), AngleCalculator.getY2());
				}
			}
			saved = false;

			repaint();
		}

		public void mouseClicked(MouseEvent e)
		{
			if(e.getButton()==MouseEvent.BUTTON2 || e.getButton()==MouseEvent.BUTTON3)		// If right clicked, toggle zoom and do nothing else
			{
				if(getScalerX()==1) setScaler(ZOOM_FACTOR_2, ZOOM_FACTOR_2);
				else				setScaler(ZOOM_FACTOR_1, ZOOM_FACTOR_1);
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
				ArrayList<Drawing> temp = FileLoader.load(filename);
				if(temp != null)
				{
					drawings = temp;
					filename = FileLoader.getFilename();
					saved = true;
				}
			}
			else if(buttons.get(2).isWithin(mxo,myo))	// Undo button
			{
				if(drawings.size()>0)
				{
					Drawing last = getLastDrawing();
					if(last.getShapes()>0)					// If possible, remove the most recent shape in the most recent drawing
						last.removeLastShape();
					else
						removeLastDrawing();				// If there are no more shapes left in the drawing, remove the drawing
				}
			}
			else if(buttons.get(3).isWithin(mxo,myo))	// Save file
			{
				FileSaver.save(drawings, filename);
				filename = FileSaver.getFilename();
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
					FileSaver.save(drawings, filename);
					filename = FileSaver.getFilename();
					saved = true;
				}
				NXTInterface.sendToNXT(drawings, filename);
			}
			else										// Otherwise, treat the click as drawing
			{
				if(drawings.isEmpty() || liftPen==true)	// If there are no drawings, or the Lift Pen button was just clicked, start a new drawing
				{
					drawings.add(new Drawing(AngleCalculator.getX2(), AngleCalculator.getY2()));
					liftPen=false;
				}

				else									// Otherwise, check whether the shape button is set for lines or points and add a new shape to the drawing accordingly
				{
					if(drawMode == DRAW_LINES)
						getLastDrawing().addLine(AngleCalculator.getX2(), AngleCalculator.getY2());
					else if(drawMode == DRAW_POINTS)
						getLastDrawing().addPoint(AngleCalculator.getX2(), AngleCalculator.getY2());
					else if(drawMode == DRAW_RECT)
						getLastDrawing().addRect(AngleCalculator.getX2(), AngleCalculator.getY2());
					else if(drawMode == DRAW_CIRCLE)
						getLastDrawing().addCircle(AngleCalculator.getX2(), AngleCalculator.getY2());
					saved = false;
				}

			}

			repaint();
		}

		public Drawing getLastDrawing()
		{
			if(drawings.size()>0)
				return drawings.get(drawings.size()-1);
			else
				return null;
		}

		public void removeLastDrawing()
		{
			if(drawings.size()>0)
				drawings.remove(getLastDrawing());
		}

		public int getPoints()
		{
			int points=0;
			for(Drawing drawing : drawings)
				points+=drawing.getPoints();
			return points;
		}


		public void mousePressed(MouseEvent e){}
		public void mouseReleased(MouseEvent e){}
		public void mouseEntered(MouseEvent e){}
		public void mouseExited(MouseEvent e){}
	}
}
