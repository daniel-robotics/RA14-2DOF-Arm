//*******************************************************************************************************************************************
// GraphicPanel.java																														*
//																																			*
// This class should be used in place of a normal JPanel when its purpose is simply to display custom graphics.								*
// A GraphicPanel implements methods which assist in graphics operations. Mainly, it allows the user to easily								*
//     change the coordinate system used by the graphics system.																			*
// The coordinate system is changed by concatenating a series of transformation matrices (AffineTransform objects) in the following order:	*
// 	      1:  Base Transform		 - one of 4 predefined coordinate systems. The user can also input a custom base.						*
//		  2:  Translation            - user-defined translation in the x and y directions, relative to the Base transform coordinate system.*
//		  3:  Scaler				 - user-defined scaling in the x and y directions.														*
//		  4:  Rotation				 - user-defined rotation around either an anchor point, or the origin, in radians (or quadrants)		*
//																																			*
//*******************************************************************************************************************************************



import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.awt.geom.NoninvertibleTransformException;

public class GraphicPanel extends JPanel
{
	public static final int DEFAULT			= 0;		// Origin at top right of the panel, positive y downward. Default base transform.
	public static final int FLIP_Y			= 1;		// Origin at top left of the panel, positive y upward
	public static final int CENTERED		= 2;		// Origin centered within the panel, positive y downward. Automatically updates if the panel gets resized.
	public static final int CENTERED_FLIP_Y	= 3;		// Origin centered within the panel, positive y upward. Automatically updates if the panel gets resized.
	public static final int USER_DEFINED	= 4;		// User-defined base coordinate system

	private AffineTransform base1 = new AffineTransform();					// DEFAULT
	private AffineTransform base2 = AffineTransform.getScaleInstance(1,-1);	// FLIP_Y
	private AffineTransform base3 = new AffineTransform();					// CENTERED
	private AffineTransform base4 = new AffineTransform();					// CENTERED_FLIP_Y
	private AffineTransform base5 = new AffineTransform();					// USER_DEFINED
	private AffineTransform[] base = {base1, base2, base3, base4, base5};	// Array containing each of the above
	private int baseNum = DEFAULT;											// Indicates the base coordinate system that was selected by the user.

	private AffineTransform trans = new AffineTransform(base1);			// The actual coordinate system transform being used. A combination of the base transform and the translation, scaler, and rotation transforms.
	private AffineTransform transInv = new AffineTransform(trans);		// Inverse of the coordinate system transformation, used to convert back to the default coordinate system.
		private AffineTransform translation = new AffineTransform();	// User defined translation, scaler, and rotation transformations to be applied in addition to the selected base transform
			private double translateX, translateY;
		private AffineTransform scaler		= new AffineTransform();
			private double scaleX = 1, scaleY = 1;
		private AffineTransform rotation	= new AffineTransform();
			private double rot;
			private double rotX, rotY;


	private boolean antialias = false;		// Antialiasing used in display, or not

	private int width=0, height=0;			// Width and Height of display area, updated automatically.
	private Graphics2D graphics;


	//**************************************
	// CONSTRUCTORS
	//**************************************

	public GraphicPanel(int width, int height)
	{
		setSize(width,height);
	}

	public GraphicPanel(int width, int height, Color color)
	{
		setSize(width,height);
		setBackground(color);
	}

	public GraphicPanel(int width, int height, Color color, boolean antialias)
	{
		setSize(width,height);
		setBackground(color);
		setAntialias(antialias);
	}



	//**************************************
	// FUNCTIONAL
	//**************************************

	public void paintComponent(Graphics g)
    {
    	super.paintComponent(g);
    	graphics = (Graphics2D)g;
		if(antialias)	graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);	// Enable antialiasing if necessary
		else			graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
		if(getWidth()!=width || getHeight()!=height)	// Recalculate panel-size-related things if necessary
			setSize(getWidth(),getHeight());
		graphics.setTransform(trans);						// Apply coordinate system to graphics system
    }

	public void setSize(int width, int height)
	{
		super.setSize(width, height);
		this.width = width; this.height = height;
		setPreferredSize(new Dimension(width,height));
		base[CENTERED] = AffineTransform.getTranslateInstance((double)width/2, (double)height/2);	// Setup the base centered/centeredFlipY coordinate systems to reflect the new panel size
		base[CENTERED_FLIP_Y].setTransform(base[CENTERED]);
		base[CENTERED_FLIP_Y].concatenate(base[FLIP_Y]);
		if(baseNum == CENTERED || baseNum == CENTERED_FLIP_Y)	// If the user specified one of the centered base coordinate systems initially,
			updateCoordSys();									// Update the final coordinate system to reflect the changes to the base
	}

	public boolean getAntialias()
	{
		return antialias;
	}

	public void setAntialias(boolean antialias)
	{
		this.antialias=antialias;
	}

	private void updateCoordSys()						// Updates the final coordinate system. Called after changes to component transformations.
	{
		trans.setTransform(base[baseNum]);
		trans.concatenate(translation);
		trans.concatenate(scaler);
		trans.concatenate(rotation);
		transInv.setTransform(trans);
		try{	transInv.invert();	}
		catch(NoninvertibleTransformException e){	System.err.println("Error: transform matrix not invertible");	}
	}


	//***************************************
	// METHODS FOR CHANGING COORDINATE SYSTEM
	//***************************************

	public AffineTransform getCoordinateSystem()		// Returns the final coordinate system transform being used by the graphics system
	{
		return graphics.getTransform();
	}


	//BASE
	public void setBaseTransform(int type)				// Sets the base transform to one of the predefined coordinate systems.
	{													// All other user-defined transformations are applied AFTER the base transform.
		baseNum = type;
		updateCoordSys();
	}

	public void setBaseTransform(AffineTransform base)	// Sets the base transform to a user-defined transformation.
	{
		this.base[USER_DEFINED] = base;
		setBaseTransform(USER_DEFINED);
	}

	public AffineTransform getBaseTransform()			// Get the base transform
	{
		return base[baseNum];
	}

	public AffineTransform getBaseTransform(int type)	// Get a particular base transform
	{
		return base[type];
	}

	public int getBaseTransformType()					// Get the type of base transform
	{
		return baseNum;
	}


	//TRANSLATION
	public void setTranslation(double x, double y)		// Set the translation transform
	{
		translateX = x;
		translateY = y;
		translation.setTransform(AffineTransform.getTranslateInstance(x, y));
		updateCoordSys();
	}

	public void setTranslation(AffineTransform t)		// Set the translation transform
	{
		translation.setTransform(t);
		updateCoordSys();
	}

	public void addTranslation(double dx, double dy)	// Add to the translation transform
	{
		setTranslation(translateX+dx, translateY+dy);
	}

	public AffineTransform getTranslation()				// Get the translation transform
	{
		return translation;
	}

	public double getTranslationX()						// Get the X component of the translation transform
	{
		return translateX;
	}

	public double getTranslationY()						// Get the Y component of the translation transform
	{
		return translateY;
	}


	//SCALING
	public void setScaler(double xs, double ys)			// Set the scaler transform
	{
		scaleX = xs;
		scaleY = ys;
		scaler.setTransform(AffineTransform.getScaleInstance(xs, ys));
		updateCoordSys();
	}

	public void setScaler(AffineTransform t)			// Set the scaler transform
	{
		scaler.setTransform(t);
		updateCoordSys();
	}

	public void addScaler(double dxs, double dys)		// Add to the scaler transform
	{
		setScaler(scaleX+dxs, scaleY+dys);
	}

	public AffineTransform getScaler()					// Get the scaler transform
	{
		return scaler;
	}

	public double getScalerX()							// Get the X component of the scaler transform
	{
		return scaleX;
	}

	public double getScalerY()							// Get the Y component of the scaler transform
	{
		return scaleY;
	}


	//ROTATION
	public void setRotationPoint(double x, double y)	// Set the rotation anchor point (the coordinate system will rotate around this x,y location)
	{
		rotX = x;
		rotY = y;
		setRotation(rot);
	}

	public void setRotation(double angle)				// Set the rotation transform (in radians, around the rotation anchor point)
	{
		rot = angle;
		rotation.setTransform(AffineTransform.getRotateInstance(rot, rotX, rotY));
		updateCoordSys();
	}

	public void setRotationQuadrants(int num)			// Set the rotation transform (in quadrants, around the rotation anchor point)
	{
		rot = num*Math.PI/2;
		rotation.setTransform(AffineTransform.getQuadrantRotateInstance(num, rotX, rotY));
		updateCoordSys();
	}

	public void setRotation(AffineTransform t)			// Set the rotation transform
	{
		rotation.setTransform(t);
		updateCoordSys();
	}

	public void addRotation(double dAngle)				// Add to the rotation transform (in radians, around the rotation anchor point)
	{
		setRotation(rot+dAngle);
	}

	public AffineTransform getRotation()				// Get the rotation transform
	{
		return rotation;
	}

	public double getRotationAngle()					// Get the angle of the rotation transform, in radians
	{
		return rot;
	}

	public double getRotationPointX()					// Get the X component of the rotation anchor point
	{
		return rotX;
	}

	public double getRotationPointY()					// Get the Y component of the rotation anchor point
	{
		return rotY;
	}


	//POINT CONVERTION
	public Point2D convert(Point2D point)				// Convert a Point2D from the standard Java coordinate system to the new coordinate system
	{
		return transInv.transform(point, new Point2D.Double());
	}

	public Point2D convert(double x, double y)			// Convert a point from the standard Java coordinate system to the new coordinate system
	{
		return convert(new Point2D.Double(x, y));
	}

	public Point2D convert(MouseEvent e)				// Convert the coordinates of a MouseEvent from the standard Java coordinate system to the new coordinate system
	{
		return convert(new Point(e.getX(), e.getY()));
	}

	public double convertX(double x)					// Convert an X-coordinate from the standard Java coordinate system to the new coordinate system
	{
		return convert(x,0).getX();
	}

	public double convertX(MouseEvent e)				// Convert an X-coordinate of a MouseEvent from the standard Java coordinate system to the new coordinate system
	{
		return convert(e.getX(),0).getX();
	}

	public double convertY(double y)					// Convert a  Y-coordinate from the standard Java coordinate system to the new coordinate system
	{
		return convert(0,y).getY();
	}

	public double convertY(MouseEvent e)				// Convert a  Y-coordinate of a MouseEvent from the standard Java coordinate system to the new coordinate system
	{
		return convert(0,e.getY()).getY();
	}


	//INVERSE POINT CONVERSION
	public Point2D inverseConvert(Point2D point)		// Convert a Point2D from the new coordinate system back to the standard Java coordinate system
	{
		return trans.transform(point, new Point2D.Double());
	}

	public Point2D inverseConvert(double x, double y)	// Convert a point from the new coordinate system back to the standard Java coordinate system
	{
		return inverseConvert(new Point2D.Double(x, y));
	}

	public double inverseConvertX(double x)				// Convert an X-coordinate from the new coordinate system back to the standard Java coordinate system
	{
		return inverseConvert(x,0).getX();
	}

	public double inverseConvertY(double y)				// Convert a  Y-coordinate from the new coordinate system back to the standard Java coordinate system
	{
		return inverseConvert(0,y).getY();
	}


	//TEMPORARY TRANSFORM REPLACEMENT
	// These methods allow for the use of different transforms mid-repaint.
	public void replaceTransform(AffineTransform t)		// Switch the graphics transform. Reverts to normal at the beginning of next repaint.
	{
		graphics.setTransform(t);
	}

	public void reinstateTransform()					// Switch to the normal graphics transform.
	{
		graphics.setTransform(trans);
	}

	public AffineTransform makeTransform(int type, boolean translate, boolean scale, boolean rotate)	// Returns a new transform based on parts of the existing transform.
	{
		AffineTransform t = new AffineTransform(base[type]);
		if(translate)	t.concatenate(this.translation);
		if(scale)		t.concatenate(this.scaler);
		if(rotate)		t.concatenate(this.rotation);
		return t;
	}

	public AffineTransform makeTransform(double x, double y)			// Returns a transform in which 0,0 corresponds to an x,y coordinate in the current transform space
	{
		AffineTransform t = new AffineTransform(trans);
		t.translate(x,y);
		return t;
	}

	public AffineTransform makeTransform(double x, double y, double scaleX, double scaleY) // Returns a transform in which 0,0 corresponds to an x,y coordinate in the current transform space
	{
		AffineTransform t = new AffineTransform(trans);
		t.translate(x,y);
		t.scale(scaleX,scaleY);
		return t;
	}

	public AffineTransform makeTransform(double x, double y, double scaleX, double scaleY, double angle) // Returns a transform in which 0,0 corresponds to an x,y coordinate in the current transform space
	{
		AffineTransform t = new AffineTransform(trans);
		t.translate(x,y);
		t.scale(scaleX,scaleY);
		t.rotate(angle);
		return t;
	}

	public AffineTransform makeTransform(double x, double y, double scaleX, double scaleY, double angle, double rotX, double rotY) // Returns a transform in which 0,0 corresponds to an x,y coordinate in the current transform space
	{
		AffineTransform t = new AffineTransform(trans);
		t.translate(x,y);
		t.scale(scaleX,scaleY);
		t.rotate(angle, rotX, rotY);
		return t;
	}

}