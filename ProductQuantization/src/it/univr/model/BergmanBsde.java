package it.univr.model;

import org.apache.commons.math3.linear.RealVector;

import it.univr.model.parameters.ParameterFunctionInterface;

public class BergmanBsde implements ModelInterface{
	private BlackScholesModel model;
	private int numberOfProcess = 1;
	private int numberOfRiskFactors = 1;
	private double mu;
	private double sigma;
	private double lendingRate;
	private double borrowingRate;
	
	public BergmanBsde(double mu, double sigma, double lendingRate, double borrowingRate) {
		super();
		this.mu = mu;
		this.sigma = sigma;
		this.lendingRate = lendingRate;
		this.borrowingRate = borrowingRate;
		this.model = new BlackScholesModel(mu,sigma);
	}

	@Override
	public double getDrift(double[] stateVariable, double time, int process) {
		return this.model.getParameters().getDriftValue(stateVariable, time).getEntry(0);
	}

	@Override
	public double getDiffusion(double[] stateVariable, double time, int process, int riskFactor) {

		return this.model.getParameters().getDiffusionValue(stateVariable, time).getEntry(0, 0);
	}

	@Override
	public RealVector getDiffusionRow(double[] stateVariable, double time, int process) {

		return this.model.getParameters().getDiffusionValue(stateVariable, time).getRowVector(0);
	}

	@Override
	public int getNumberOfProcess() {

		return this.numberOfProcess;
	}

	@Override
	public int getNumberOfRiskFactors() {

		return this.numberOfRiskFactors;
	}

	@Override
	public double getRiskFreeRate() {
		return this.lendingRate;
	}
	
	public double getVolatility() {
		return this.sigma;
	}
	
	/**
	 * returns the driver of the BSDE f(t,Y,U,V)
	 * @param time
	 * @param stateVariable
	 * @param control
	 * @return
	 */
	public double getDriver(double time, double[] forwardStateVariable, double backwardStateVariable, double[] control) {
		return -this.lendingRate*backwardStateVariable - (this.mu - this.lendingRate)/this.sigma * control[0]
				- (this.borrowingRate - this.lendingRate) * Math.min(backwardStateVariable - control[0]/this.sigma, 0);
	}
	
	/**
	 * The control is a function of time and of the vectorZ
	 * @param time
	 * @param forwardStateVariable
	 * @return
	 */
	public double getControl(double time, double[] forwardStateVariable) {
		return 0.0;
	}


	@Override
	public ParameterFunctionInterface getParameters() {
		return this.model.getParameters();
	}
	
	
}
