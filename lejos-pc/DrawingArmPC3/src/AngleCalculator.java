
public class AngleCalculator
{
	private static double r1, r2, maxT1, minT1, maxT2, minT2, d, t1, t2, x1, y1, x2, y2, maxRad, minRad;
	private static boolean outOfRange, positiveAngles;

	public static void setMechanicalConstants(double newR1, double newR2, double newMinT1, double newMaxT1, double newMinT2, double newMaxT2, boolean positiveAnglesOnly) //input angles are in degrees
	{
		r1=newR1;
		r2=newR2;
		minT1=Math.toRadians(newMinT1);
		maxT1=Math.toRadians(newMaxT1);
		minT2=Math.toRadians(newMinT2);
		maxT2=Math.toRadians(newMaxT2);
		positiveAngles=positiveAnglesOnly;

		maxRad = calculateMaximumRadius();
		minRad = calculateMinimumRadius();
	}



	//calculates t1 and t2 based on the arm segment lengths r1,r2 and endpoint x,y

	public static void calculateAngles(double x, double y)
	{
		d = Math.sqrt(x*x+y*y);

		if(d>r1+r2)			// If the desired endpoint is too far from the origin, it is out of range.
		{
			outOfRange=true;
			t1=Math.atan(y/x);
			t2=maxT2;
		}
		else if(d<r1-r2)	// If the desired endpoint is too close to the origin, it is out of range.
		{
			outOfRange=true;
			t1=Math.atan(y/x);
			t2=minT2;
		}
		else				// If the desired endpoint is not out of distance range, calculate t1 and t2.
		{
			outOfRange=false;
			t1=calculateT1(x,y,d);
			t2=calculateT2(x,y);
		}

		if(x<0)				// Adjust angles to compensate for atan limitations and coordinate system specifications
			t1+=Math.PI;
		if(t1<0 && positiveAngles)
			t1+=2*Math.PI;

		if(t1>maxT1)		// Make sure that t1 and t2 are within the mechanical constraints of the arm
		{
			outOfRange=true;
			t1=maxT1;
		}
		if(t1<minT1)
		{
			outOfRange=true;
			t1=minT1;
		}
		if(t2>maxT2)
		{
			outOfRange=true;
			t2=maxT2;
		}
		if(t2<minT2)
		{
			outOfRange=true;
			t2=minT2;
		}

		double t2ex=Math.PI-t2+t1;	// Calculate (x1,y1) and (x2,y2) which are the middle and endpoints of the arm (only useful for graphical purposes)
 	   	x1=r1*Math.cos(t1);
   	   	y1=r1*Math.sin(t1);
   	   	x2=r2*Math.cos(t2ex)+x1;
   	   	y2=r2*Math.sin(t2ex)+y1;


		t1=Math.toDegrees(t1);	//up until this point all calculations have been in radians, but from now on degree angles are needed.
		t2=Math.toDegrees(t2);
	}

	public static double getT1()
	{
		return t1;
	}

	public static double getT2()
	{
		return t2;
	}

	public static int getX1()
	{
		return (int)x1;
	}

	public static int getY1()
	{
		return (int)y1;
	}

	public static int getX2()
	{
		return (int)x2;
	}

	public static int getY2()
	{
		return (int)y2;
	}

	public static double getD()
	{
		return d;
	}

	public static double getMaxRadius()
	{
		return maxRad;
	}

	public static double getMinRadius()
	{
		return minRad;
	}

	public static boolean outOfRange()
	{
		return outOfRange;
	}


//-------------------ACTUAL MATH STUFF-------------------------------------------


	private static double calculateMinimumRadius()	// Calculates the minimum radius around the base that the arm can reach
	{
		//let t1 be 0 (the first arm segment resting on the x-axis
		//then the closest point to the base that the arm can reach is: r2*cos(PI-minT2)+r1, r2*sin(PI-minT2)
		//then the minimum radius can be calculated using the usual pythagorean method

		double x = r2*Math.cos(Math.PI-minT2) + r1;
		double y = r2*Math.sin(Math.PI-minT2);
		return Math.sqrt(x*x + y*y);
	}

	private static double calculateMaximumRadius()	// Calculates the maximum radius around the base that the arm can reach
	{
		//let t1 be 0 (the first arm segment resting on the x-axis
		//then the farthest point from the base that the arm can reach is: r2*cos(maxT2)+r1, r2*sin(maxT2)
		//then the minimum radius can be calculated using the usual pythagorean method

		double x = r2*Math.cos(Math.PI-maxT2) + r1;
		double y = r2*Math.sin(Math.PI-maxT2);
		return Math.sqrt(x*x + y*y);
	}

	private static double calculateT1(double x, double y, double d)
	{
		return Math.atan(y/x)-Math.acos((r1*r1-r2*r2+x*x+y*y)/(2*r1*d));
	}

	private static double calculateT2(double x, double y)
	{
		return Math.acos((r1*r1+r2*r2-x*x-y*y)/(2*r1*r2));
	}
}
