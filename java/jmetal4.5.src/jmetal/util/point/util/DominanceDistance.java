package jmetal.util.point.util;

import jmetal.util.point.Point;

public class DominanceDistance implements PointDistance { 
	 
	  @Override 
	  public double compute(double[] a, double[] b) { 
	    
	 
	    double distance = 0.0; 
	 
	    for (int i = 0; i < a.length; i++) { 
	      double max = Math.max(b[i] - a[i], 0.0) ; 
	      distance += Math.pow(max, 2); 
	    } 
	    return Math.sqrt(distance); 
	  }
 
	}