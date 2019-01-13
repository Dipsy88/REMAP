package jmetal.util.front;

import java.io.Serializable;
import java.util.Comparator;

import jmetal.util.point.Point;

public interface Front extends Serializable { 
	  public int getNumberOfPoints() ; 
	  public int getPointDimensions() ; 
	  public Point getPoint(int index) ; 
	  public void setPoint(int index, Point point) ; 
	  public void sort(Comparator<Point> comparator) ; 
	}
