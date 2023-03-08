package it.univr.products;

import java.util.Arrays;
import java.util.Map;

import it.univr.model.BergmanBsde;
import it.univr.quantizedprocess.BackwardIteration;
import it.univr.quantizedprocess.BackwardIterationControl;
import it.univr.quantizedprocess.QuantizedModel;

public class BergmanBsdeSolver {

	private BergmanBsde bsde;
	private double strike1;
	private double strike2;
	private double optionMaturity;
	private BackwardIteration valueIteration;
	private BackwardIterationControl controlIteration;
	
	public BergmanBsdeSolver(BergmanBsde bsde, double strike1, double strike2, double optionMaturity) {
		this.bsde = bsde;
		this.strike1 = strike1;
		this.strike2 = strike2;
		this.optionMaturity = optionMaturity;
		this.valueIteration = null;
		this.controlIteration = null;
	}
	
	/**
	 * Service method to compute the exact control.
	 * @param time
	 * @param stock
	 * @return
	 * @throws Exception 
	 */
	public double getExactControl(double time, double stock) throws Exception {
		throw new Exception("No exact control known");
	}
	
	public double getExactSolution(double time, double stock) throws Exception {
		throw new Exception("No exact solution known");
	}
	
	public double evaluate(QuantizedModel quantizedModel) {
		
		//Terminal time
		//Create an empty backward iteration grid
		BackwardIteration valueFunctionDiscretization = new BackwardIteration(quantizedModel);
		BackwardIterationControl controlDiscretization = new BackwardIterationControl(quantizedModel, 1);
		
		/*
		 * Initialize the backward iteration by means of the payoff
		 */
		for(Map.Entry<double[], Double> element : quantizedModel.getDistributionOf(quantizedModel.getNumberOfTimeSteps()).entrySet()) {
			
			double value = Math.max(element.getKey()[0] - this.strike1,0) - 2 * Math.max(element.getKey()[0] - this.strike2,0);
			//Initialize the discretized solution via the terminal condition of the BSDE
			valueFunctionDiscretization.replace(quantizedModel.getNumberOfTimeSteps(), element.getKey(), value);
			
			double[] zero = {0.0};
			
			//The final control is zero.
			controlDiscretization.replace(quantizedModel.getNumberOfTimeSteps(), element.getKey(), zero);
		}
		
		double riskFree = quantizedModel.getRiskFreeRate();
		double deltaT = quantizedModel.getDeltaT();
		double sigma = bsde.getVolatility();
		
		//Backward induction step
		for(int i = quantizedModel.getNumberOfTimeSteps(); i >=  1; i--) {
			System.out.println("Time step" + i);
			//Starting node at the previous time step.
			for(Map.Entry<double[], Double> quantizerk : quantizedModel.getDistributionOf(i-1).entrySet()) {
				
				double value = 0;
				
				//Last time step
				if(i == 1) {
					double initialValue = 0;
					double[] diff = new double[quantizerk.getKey().length];
					Arrays.fill(diff, 0.0);
					
					//Target nodes at the next time step
					for(Map.Entry<double[], Double> element : quantizedModel.getDistributionOf(1).entrySet()) {
						initialValue = initialValue + valueFunctionDiscretization.getValue(1, element.getKey())*
								quantizedModel.getTransitionProbabilityLattice().getTransitionProbability(0, quantizerk.getKey(), element.getKey());
						
						
						//Increment of the quantized process
						double[] Ytk = quantizerk.getKey();
						double[] Ytkp1 = element.getKey();
						
						
						/*
						 * On each node, loop over the dimension.
						 */
						for(int j = 0; j< Ytk.length; j++) {
							diff[j] = diff[j] + 1.0 /  (Ytk[j] * sigma * deltaT) 
									* valueFunctionDiscretization.getValue(i, element.getKey()) * (Ytkp1[j] - Ytk[j])
									* quantizedModel.getTransitionProbabilityLattice().getTransitionProbability(0, quantizerk.getKey(), element.getKey());
						}
					}
					double[] control = diff;
					for(int j = 0; j< quantizerk.getKey().length; j++) {
						control[j] = control[j] - value * riskFree / sigma;
					}
					
					
					initialValue = initialValue - riskFree *initialValue* deltaT;
					
					controlDiscretization.setValue(0, quantizerk.getKey(), control);
					valueFunctionDiscretization.setValue(0, quantizerk.getKey(), initialValue);
					System.out.println(Arrays.toString(quantizerk.getKey()) + " " + Arrays.toString(control));
					
					this.controlIteration = controlDiscretization;
					this.valueIteration = valueFunctionDiscretization;
					
					return initialValue;
				}
				
				double[] diff = new double[quantizerk.getKey().length];
				Arrays.fill(diff, 0.0);
				
				/*
				 * Loop over Quantization nodes for the next point in time.
				 */
				for(Map.Entry<double[], Double> quantizerk1 : quantizedModel.getDistributionOf(i).entrySet()) {
					value = value + valueFunctionDiscretization.getValue(i, quantizerk1.getKey())
							*quantizedModel.getTransitionProbabilityLattice().getTransitionProbability(i-1, quantizerk.getKey(), quantizerk1.getKey());
					
					//Increment of the quantized process
					double[] Ytk = quantizerk.getKey();
					double[] Ytkp1 = quantizerk1.getKey();
					
					
					/*
					 * On each node, loop over the dimension.
					 */
					for(int j = 0; j< Ytk.length; j++) {
						diff[j] = diff[j] + 1.0 /  (Ytk[j] * sigma * deltaT) 
								* valueFunctionDiscretization.getValue(i, quantizerk1.getKey()) * (Ytkp1[j] - Ytk[j])
								* quantizedModel.getTransitionProbabilityLattice().getTransitionProbability(i-1, quantizerk.getKey(), quantizerk1.getKey());
					}

				}
				
				double[] control = diff;
				for(int j = 0; j< quantizerk.getKey().length; j++) {
					control[j] = control[j] - value * riskFree / sigma;
				}
				
				
				value = value - riskFree * value * deltaT;
				valueFunctionDiscretization.setValue(i-1, quantizerk.getKey(), value);
				
				controlDiscretization.setValue(i - 1, quantizerk.getKey(), control);
				//double time = (double) i/ (quantizedModel.getNumberOfTimeSteps() +1);
		
				System.out.println(quantizerk.getKey()[0] + " " + control[0]);
			}
		}
		
		return 1.0;
	}

	public BergmanBsde getBsde() {
		return bsde;
	}

	public double getStrike() {
		return strike1;
	}

	public double getOptionMaturity() {
		return optionMaturity;
	}

	public BackwardIteration getValueIteration() {
		return valueIteration;
	}

	public BackwardIterationControl getControlIteration() {
		return controlIteration;
	}
}
