package jmetal.util.front.util;

import jmetal.util.front.Front;
import jmetal.util.point.Point;
import jmetal.util.point.util.PointDistance;

public class FrontUtils {
	/**
	   * Gets the maximum values for each objectives in a front 
	   * 
	   * @param front A front of objective values 
	   * @return double [] An array with the maximum values for each objective 
	   */ 
	
	 
	  /**
	   * Gets the distance between a point and the nearest one in a given front. The Euclidean distance 
	   * is assumed 
	   * 
	   * @param point The point 
	   * @param front The front that contains the other points to calculate the 
	   *              distances 
	   * @return The minimum distance between the point and the front 
	   */ 
	
	  /**
	   * Gets the distance between a point and the nearest one in a given front 
	   * 
	   * @param trueParetoFront The point 
	   * @param front The front that contains the other points to calculate the 
	   *              distances 
	   * @return The minimum distance between the point and the front 
	   */ 
	  public static double distanceToClosestPoint(double[] trueParetoFront, double[][] front, PointDistance distance) { 

	 
	    double minDistance = distance.compute(trueParetoFront, front[0]); 
	 
	    for (int i = 1; i < front.length; i++) { 
	      double aux = distance.compute(trueParetoFront, front[i]); 
	      if (aux < minDistance) { 
	        minDistance = aux; 
	      } 
	    } 
	 
	    return minDistance; 
	  } 
	 
	 
}
