package it.univr.test;

import static java.util.Arrays.asList;

import java.io.IOException;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import it.univr.model.BergmanBsde;
import it.univr.model.BlackScholesModel;
import it.univr.productquantizer.ProductQuantizer;
import it.univr.products.AmericanOption;
import it.univr.products.EuropeanOption;
import it.univr.products.BergmanBsdeSolver;
import it.univr.quantizedprocess.ModelQuantizer;
import it.univr.quantizedprocess.QuantizedModel;

public class TestClassBergman {

	public static void main(String[] args) throws InvalidFormatException, IOException {
		
		double sigma = 0.2;
		double mu = 0.05;
		BlackScholesModel model = new BlackScholesModel(mu,sigma);

		ProductQuantizer normalGrid =
				ProductQuantizer.buildWithCostantValues(asList(100.0), asList(100));
		
		int numberOfTimeSteps = 10;
		double maturity = 0.25;
		double deltaT = maturity / numberOfTimeSteps;
		ModelQuantizer modelQuantizer = new ModelQuantizer(model,numberOfTimeSteps,deltaT,normalGrid);

		QuantizedModel quantizedModel = modelQuantizer.run();
		
		/*
		 * Try with the BSDE without control
		 */
		
		double lendingRate = 0.01;
		double borrowingRate = 0.06;
		
		double strike1 = 95.0;
		double strike2 = 105.0;

		BergmanBsde bsde = new BergmanBsde( mu, sigma, lendingRate, borrowingRate);
		BergmanBsdeSolver solver = new BergmanBsdeSolver(bsde, strike1, strike2, maturity);
		
		System.out.println("Price according to BSDE solver");
		System.out.println(solver.evaluate(quantizedModel));
		
		
		System.out.println("Exact price");
		System.out.println(2.96);
		
		System.out.println("Hedging strategy at inception");
		System.out.println(0.55);		
		
	}

}
