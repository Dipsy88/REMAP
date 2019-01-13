package com.main.SSBP;

public class TestCase {
	private String name;
	private double totalExecutionTime;
	private double executionTime;
	private int numFail;
	private int numExecution;
	private double FDC;
	private double fitnessGrA;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getExecutionTime() {
		return executionTime;
	}

	public void setExecutionTime(double executionTime) {
		this.executionTime = executionTime;
	}

	public int getNumFail() {
		return numFail;
	}

	public void setNumFail(int numFail) {
		this.numFail = numFail;
	}

	public double getFDC() {
		return FDC;
	}

	public void setFDC(double fDC) {
		FDC = fDC;
	}

	public double getFitnessGrA() {
		return fitnessGrA;
	}

	public void setFitnessGrA(double fitnessGrA) {
		this.fitnessGrA = fitnessGrA;
	}

	public int getNumExecution() {
		return numExecution;
	}

	public void setNumExecution(int numExecution) {
		this.numExecution = numExecution;
	}

	public double getTotalExecutionTime() {
		return totalExecutionTime;
	}

	public void setTotalExecutionTime(double totalExecutionTime) {
		this.totalExecutionTime = totalExecutionTime;
	}

}
