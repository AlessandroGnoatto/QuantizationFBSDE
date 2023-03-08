package it.univr.quantizedprocess;

import java.util.HashMap;

public class QuantizedModel {

	public QuantizedProcessLattice quantizedProcess;
	public TransitionProbabilityLattice transitionProbabilityLattice;

	public QuantizedModel(QuantizedProcessLattice quantizedProcess,
			TransitionProbabilityLattice transitionProbabilityLattice) {

		this.quantizedProcess = quantizedProcess;
		this.transitionProbabilityLattice = transitionProbabilityLattice;
	}		

	/**
	 * Get the number of time step of the discretized process.
	 * 
	 * @return the number of time steps
	 */
	public int getNumberOfTimeSteps() {

		return this.quantizedProcess.getNumberOfTimeSteps();
	}

	/**
	 * Get the distribution of the product quantizer at the time step
	 * of interest.
	 * 
	 * @param timeStep
	 * @return The distribution of the product quantizer
	 */
	public HashMap<double[], Double> getDistributionOf(int timeStep) {

		return this.quantizedProcess.getQuantizedProcessOutput(timeStep)
				.getDistribution();
	}

	/**
	 * Get the marginal distribution at the last time step of the specified
	 * component of the process.
	 * 
	 * @param process the component of the process
	 * @return HashMap where the keys are the quantizers and the
	 * values their companion weights
	 */
	public HashMap<Double, Double> getMarginalDistributionOf(int process) {

		return this.quantizedProcess.getMarginalDistributionOf(process);
	}

	/**
	 * Get \Delta.
	 * 
	 * @return deltaT
	 */
	public double getDeltaT() {

		return this.quantizedProcess.deltaT;
	}

	/**
	 * Get the risk free rate of the model.
	 * 
	 * @return the risk free rate
	 */
	public double getRiskFreeRate() {

		return this.quantizedProcess.model.getRiskFreeRate();
	}

	/**
	 * Get the transition probabiility lattice generated by the process.
	 * 
	 * @return the transition probability lattice
	 */
	public TransitionProbabilityLattice getTransitionProbabilityLattice() {

		return this.transitionProbabilityLattice;
	}

	/**
	 * Get the quantized process lattice.
	 * 
	 * @return the quantized process lattice
	 */
	public QuantizedProcessLattice getQuantizedProcessLattice() {

		return this.quantizedProcess;
	}

}