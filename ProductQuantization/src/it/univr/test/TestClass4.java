package it.univr.test;

import static java.util.Arrays.asList;

import java.io.IOException;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import it.univr.model.BlackScholesBsde;
import it.univr.model.BlackScholesModel;
import it.univr.productquantizer.ProductQuantizer;
import it.univr.products.AmericanOption;
import it.univr.products.EuropeanOption;
import it.univr.products.EuropeanOptionBsdeSolver;
import it.univr.quantizedprocess.ModelQuantizer;
import it.univr.quantizedprocess.QuantizedModel;
import net.finmath.montecarlo.*;
import net.finmath.stochastic.RandomVariable;
import net.finmath.time.*;

public class TestClass4 {

	public static void main(String[] args) throws InvalidFormatException, IOException {
		
		double strike = 100;
		BlackScholesModel model = new BlackScholesModel(0.04,0.25);

		ProductQuantizer normalGrid =
				ProductQuantizer.buildWithCostantValues(asList(100.0), asList(50));
		
		int numberOfTimeSteps = 50;
		double maturity = 1.0;
		double deltaT = maturity / numberOfTimeSteps;
		ModelQuantizer modelQuantizer = new ModelQuantizer(model,numberOfTimeSteps,deltaT,normalGrid);

		long startTime = System.currentTimeMillis();
		QuantizedModel quantizedModel = modelQuantizer.run();
		long endTime = System.currentTimeMillis();
		long time = endTime-startTime;
		System.out.println("The computation of the quantization grid required  "+time +" milliseconds.");


		EuropeanOption europeanOption = new EuropeanOption(strike,'c');
		
		
		System.out.println(europeanOption.evaluate(quantizedModel));
		
		


		AmericanOption americanOption = new AmericanOption(strike);
		
		System.out.println("American Option price as benchmark - Exercise is not optimal");
		System.out.println(americanOption.evaluate(quantizedModel));
		
		
		/*
		 * Try with the BSDE without control
		 */
		BlackScholesBsde bsde = new BlackScholesBsde(0.04,0.25);
		EuropeanOptionBsdeSolver solver = new EuropeanOptionBsdeSolver(bsde, strike, 1.0);
		
		System.out.println("Price according to BSDE solver");
		startTime = System.currentTimeMillis();
		System.out.println(solver.evaluate(quantizedModel));
		endTime = System.currentTimeMillis();
		time = endTime-startTime;
		System.out.println("The BSDE was solved in "+time +" milliseconds.");

		
		System.out.println(solver.getExactSolution(0, 100));
		//N(d_1)
		System.out.println("Hedging strategy at inception");
		System.out.println(solver.getExactControl(0, 100));	
		
		
		
		startTime = System.currentTimeMillis();
		TimeDiscretization td = new TimeDiscretizationFromArray(0.0, 10, 0.1);
		BrownianMotion bm = new BrownianMotionLazyInit(td, 1, 10000000, 3213);   
		
		RandomVariable myIncr = bm.getBrownianIncrement(9,0);
		endTime = System.currentTimeMillis();
		time = endTime-startTime;
		System.out.println("Generation of the Brownian motion completed in "+time +" milliseconds.");
		  
	}

}
