package main;

public class Utils {
	
	public static double calcAzimFromXY(double x, double y) {

		// verifica se o vetor est� na linha N-S
		if (x == 0.0) // � 0 ou 180
			if (y > 0.0) // � 0
				return 0.0;
			else
				if (y < 0.0) // � 180
					return 180.0;
				else // � 0 (x = y = 0)
					return 0.0;

		// verifica se o vetor est� na linha L-O
		if (y == 0.0) // � 90 ou 270
			if (x > 0.0) // � 90
				return 90.0;
			else
				if (x < 0.0) // � 270
					return 270.0;
		
		// nem x e nem y s�o 0, verifica o quadrante e calcula o azimute
		if (x > 0.0) // est� no 1o ou no 2o quadrante
			if (y > 0.0) // est� no 1o quadrante
				return (90.0 - 180.0*Math.atan(y/x)/Math.PI);
			else // est� no 2o quadrante
				 return (90.0 + Math.abs(180.0*Math.atan(y/x)/Math.PI));
		else // est� no 3o ou 4o quadrante
			if (y > 0.0) // est� no 4o quadrante
				return (270.0 + Math.abs(180.0*Math.atan(y/x)/Math.PI));
			else // est� no 3o quadrante
				 return (270 - 180.0*Math.atan(y/x)/Math.PI);
	}
	
	public static double calcVelocityFromXY(double x, double y) {

		return Math.sqrt(y*y + x*x);
	
	}

	
	

}
