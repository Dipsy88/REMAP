package jmetal.util.point;

public interface Point { 
	  public int getNumberOfDimensions(); 
	  public double[] getValues() ; 
	  public double getDimensionValue(int index) ; 
	  public void setDimensionValue(int index, double value) ; 
}