package main;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;

public class Tests {
	
	public void test() {
		
//		testMDLPId();
//		testSystemId();
//		testBlockSize();
//		testSourceId();
//		testCoordinates();
//		testTrackId();
//		testDateTime();
//		testBearingSpeed();		
//		testComplement();
//		testFieldSpec();
		testCalcAzimFromXY();
		
	}
	
	public void testBearingSpeed() {

		int testCounter = 0, errors = 0;
		MDLP_Protocol_v2021 libProtMDLP = new MDLP_Protocol_v2021();
		double minTest = 0.0, maxTest = 0.0, testValue = 0.0, incr = 0.0, bearingValue = 0.0;
		int speedValue = 0;
		byte[] bytesBearingSpeed;
		
		minTest = -0.1; maxTest = 360.1; incr = 0.1; testValue = minTest;
		System.out.println("\nBEARING TEST: testando de " + String.valueOf(minTest) + " até " + String.valueOf(maxTest) + ", incremento " + String.valueOf(incr));

		BigDecimal bdTestValue;
		
		while ( testValue <= maxTest ) {
			bytesBearingSpeed = libProtMDLP.createBearingSpeedRcd(0, testValue, 1.0);
			bearingValue = libProtMDLP.getBearing( bytesBearingSpeed );
			bdTestValue = new BigDecimal( testValue );
			if ( bdTestValue.setScale(1, RoundingMode.HALF_EVEN).doubleValue() != bearingValue ) {
				System.out.println("  BEARING: erro em " + String.valueOf(testValue) + ", retornou " + String.valueOf(bearingValue) );
				errors++;
			}
			testValue += incr;
			testCounter++;
		}
		System.out.println( "  BEARING: fim dos testes, Qt erros = " + errors );
		
		testCounter = 0; errors = 0;
		minTest = -0.6; maxTest = 4095.6; incr = 0.1; testValue = minTest; 
		System.out.println("\nSPEED TEST: testando de " + String.valueOf(minTest) + " até " + String.valueOf(maxTest) + ", incremento " + String.valueOf(incr));
		
		while ( testValue <= maxTest ) {
			bytesBearingSpeed = libProtMDLP.createBearingSpeedRcd(0, 0.0, testValue);
			speedValue = libProtMDLP.getSpeed( bytesBearingSpeed );
			bdTestValue = new BigDecimal( testValue );
			if ( bdTestValue.setScale(0, RoundingMode.HALF_EVEN).doubleValue() != speedValue ) {
				System.out.println("  SPEED: erro em " + String.valueOf(testValue) + ", retornou " + speedValue );
				errors++;
			}
			testValue += incr;
			testCounter++;
		}
		System.out.println( "  SPEED: fim dos testes, Qt erros = " + errors );
		
	}

	public void testBlockSize() {

		int testCounter = 0, errors = 0, blockSizeValue = 0;
		MDLP_Protocol_v2021 libProtMDLP = new MDLP_Protocol_v2021();
		int minTest = 0, maxTest = 0;
		byte[] bytesBlockSize;
		
		minTest = -1; maxTest = 65536;
		System.out.println("\nBLOCK SIZE TEST: testando " + (maxTest-minTest+1) + " valores (" + minTest + " até " + maxTest + "), incremento 1");
		
		for ( int i = minTest; i <= maxTest; i++ ) {
			bytesBlockSize = libProtMDLP.createBlockSize( i );
			blockSizeValue = libProtMDLP.getBlockSize( bytesBlockSize );
			if ( i != blockSizeValue ) {
				System.out.println("  BLOCK SIZE: erro em " + i + ", retorno = " + blockSizeValue + " (INVALID_BLOCK_SIZE)");
				errors++;
			}
			testCounter++;
		}
		System.out.println( "  BLOCK SIZE: fim de " + testCounter + " testes, Qt erros = " + errors );
	}	
	
	public void testComplement() {

		int testCounter = 0, errors = 0, complementValue = 0;
		MDLP_Protocol_v2021 libProtMDLP = new MDLP_Protocol_v2021();
		int minTest = 0, maxTest = 0;
		byte[] bytesComplement;
		
		minTest = -1; maxTest = 128;
		System.out.println("\nCOMPLEMENT TEST: testando " + (maxTest-minTest+1) + " valores (" + minTest + " até +" + maxTest + "), incremento 1");
		
		for ( int i = minTest; i <= maxTest; i++ ) {
			bytesComplement = libProtMDLP.createComplementRcd(i, i);
			complementValue = libProtMDLP.getComplement( bytesComplement );
			if (i != complementValue) {
				System.out.println("  COMPLEMENT: erro em " + i + ", retorno = " + complementValue + ", (INVALID_COMPLEMENT)");
				errors++;
			}
			testCounter++;
		}
		System.out.println( "  COMPLEMENT: fim de " + testCounter + " testes, qt erros = " + errors );
	}

	public void testCoordinates() {
		
		
		int testCounter = 0, errors = 0;
		MDLP_Protocol_v2021 libProtMDLP = new MDLP_Protocol_v2021();
		double minTest = 0.0, maxTest = 0.0, testValue = 0.0, incr = 0.0, longitudeValue = 0.0, latValue = 0.0;
		int altValue = 0;
		BigDecimal bdTestValue;
		
		minTest = -180.0; maxTest = 180.0001; incr = 0.0001; testValue = minTest;
		System.out.println("\nLONGITUDE TEST: testando de " + String.valueOf(minTest) + " a " + String.valueOf(maxTest) + ", incremento " + String.valueOf(incr));
		byte[] bytesLongitude;

		while ( testValue <= maxTest ) {
			//try { Thread.sleep(1000); } catch (InterruptedException e) { e.printStackTrace();}
			bytesLongitude = libProtMDLP.createCoordRcd(1, testValue, 0.0, 0.0);
			longitudeValue = libProtMDLP.getLongitude( bytesLongitude );
			bdTestValue = new BigDecimal( testValue ).setScale(4, RoundingMode.HALF_EVEN);
			if ( bdTestValue.doubleValue() != longitudeValue ) {
				System.out.println("  LONGITUDE: erro em " + Double.toString(testValue) + ", retornou " + Double.toString(longitudeValue) + " (INVALID_LONGITUDE)" );
				errors++;
			}
			//System.out.println("    Test Value = " + bdTestValue.toString() + ", retornado = " + String.valueOf(longitudeValue));
			testValue += incr;
			testCounter++;
		}
		System.out.println( "  LONGITUDE: fim dos testes, Qt erros = " + errors);
		
		minTest = -90.0001; maxTest = 90.0001; incr = 0.0001; testValue = minTest; testCounter = 0; errors = 0;
		System.out.println("\nLATITUDE TEST: testando de " + String.valueOf(minTest) + " a " + String.valueOf(maxTest) + ", incremento " + String.valueOf(incr));
		byte[] bytesLatitude;

		while ( testValue <= maxTest ) {
			bytesLatitude = libProtMDLP.createCoordRcd(1, 0.0, testValue, 0.0);
			latValue = libProtMDLP.getLatitude( bytesLatitude );
			bdTestValue = new BigDecimal( testValue ).setScale(4, RoundingMode.HALF_EVEN);
			if ( bdTestValue.doubleValue() != latValue ) {
				System.out.println("  LATITUDE: erro em " + Double.toString(testValue) + ", retornou " + Double.toString(latValue) + " (INVALID_LATITUDE)" );
				errors++;
			}
			//System.out.println("    Test Value = " + bdTestValue.toString() + ", retornado = " + String.valueOf(latValue));
			testValue = bdTestValue.doubleValue() + incr;
			testCounter++;
		}
		System.out.println( "  LATITUDE: fim dos testes, Qt erros = " + errors );

		minTest = -10000.1; maxTest = 55534.1; incr = 0.1; testValue = minTest; testCounter = 0; errors = 0;
		System.out.println("\nALTITUDE TEST: testando de " + String.valueOf(minTest) + " a " + String.valueOf(maxTest) + ", incremento " + String.valueOf(incr));
		byte[] bytesAltitude;

		while ( testValue <= maxTest ) {
			bytesAltitude = libProtMDLP.createCoordRcd(1, 0.0, 0.0, testValue);
			altValue = libProtMDLP.getAltitude( bytesAltitude );
			bdTestValue = new BigDecimal( testValue ).setScale(0, RoundingMode.HALF_EVEN);
			if ( bdTestValue.intValue() != altValue ) {
				System.out.println("  ALTITUDE: erro em " + Double.toString(testValue) + ", retornou " + Double.toString(altValue) + " (INVALID_ALTITUDE)" );
				errors++;
			}
			//System.out.println("    Test Value = " + Double.toString(testValue) + ", retornado = " + String.valueOf(altValue));
			testValue += incr;
			testCounter++;
		}
		System.out.println( "  ALTITUDE: fim dos testes, Qt erros = " + errors);
		
	}
	
	public void testDateTime() {
		
		int testCounter = 0, errors = 0, dateTimeValue = 0;
		MDLP_Protocol_v2021 libProtMDLP = new MDLP_Protocol_v2021();
		int minTest = 0, maxTest = 0;
		byte[] bytesDateTime;
		
		int integerSecondsNow = (int) Instant.now().getEpochSecond();		
		minTest = integerSecondsNow; maxTest = minTest + 5000000;
		System.out.println("\nDATE TIME TEST: testando " + (maxTest-minTest+1) + " valores (" + Instant.ofEpochMilli((long) minTest * 1000L).toString() + " até " + Instant.ofEpochMilli((long) maxTest * 1000L).toString() + "), incremento 1 seg");
		String strTested = null, strReturned = null;
		
		for ( int i = minTest; i <= maxTest; i++ ) {
			bytesDateTime = libProtMDLP.createDateTimeRcd( 0, i );
			dateTimeValue = libProtMDLP.getDateTime( bytesDateTime );
			strTested = Instant.ofEpochMilli((long) i * 1000L).toString();
			strReturned = Instant.ofEpochMilli((long) dateTimeValue * 1000L).toString();
			if (!strTested.equals(strReturned)) {
				System.out.println( "  DATE TIME: erro em " + i + ", retorno = " + dateTimeValue );
				errors++;	
			}
			testCounter++;
		}
		System.out.println( "  DATE TIME: fim de " + testCounter + " testes, qt erros = " + errors );
		
		
	}
	
	public void testFieldSpec() {
		
		int testCounter = 0, errors = 0, fieldSpec = 0;
		MDLP_Protocol_v2021 libProtMDLP = new MDLP_Protocol_v2021();
		int minTest = 0, maxTest = 0;

		System.out.println("\nFIELD SPEC SOURCE ID RECORD: testando " + (maxTest-minTest+1) + " valores (" + minTest + " até +" + maxTest + ")");
		minTest = -1; maxTest = 128;
		for (int i = minTest; i <= maxTest; i++) {
			byte[] bytesFieldSpecTest = libProtMDLP.createSourceIdRcd(i, 1);
			fieldSpec = libProtMDLP.getFieldSpec(bytesFieldSpecTest);
			if ( fieldSpec != i ) {
				errors++;
				System.out.println("  FIELD SPEC: erro em " + i + ", retorno = " + fieldSpec);
			}
			testCounter++;
		}
		System.out.println( "  FIELD SPEC: fim de " + testCounter + " testes, qt erros = " + errors );
		
		System.out.println("\nFIELD SPEC COORD RECORD TEST: testando " + (maxTest-minTest+1) + " valores (" + minTest + " até +" + maxTest + ")");
		minTest = -1; maxTest = 128; testCounter = 0; errors = 0;
		for (int i = minTest; i <= maxTest; i++) {
			byte[] bytesFieldSpecTest = libProtMDLP.createCoordRcd(i, 1.0, 1.0, 1.0);
			fieldSpec = libProtMDLP.getFieldSpec(bytesFieldSpecTest);
			if ( fieldSpec != i ) {
				errors++;
				System.out.println("  FIELD SPEC: erro em " + i + ", retorno = " + fieldSpec);
			}
			testCounter++;
		}
		System.out.println( "  FIELD SPEC: fim de " + testCounter + " testes, qt erros = " + errors );

		System.out.println("\nFIELD SPEC TRACK ID RECORD: testando " + (maxTest-minTest+1) + " valores (" + minTest + " até +" + maxTest + ")");
		minTest = -1; maxTest = 128; testCounter = 0; errors = 0;
		for (int i = minTest; i <= maxTest; i++) {
			byte[] bytesFieldSpecTest = libProtMDLP.createTrackIdRcd(i, 1);
			fieldSpec = libProtMDLP.getFieldSpec(bytesFieldSpecTest);
			if ( fieldSpec != i ) {
				errors++;
				System.out.println("  FIELD SPEC: erro em " + i + ", retorno = " + fieldSpec);
			}
			testCounter++;
		}
		System.out.println( "  FIELD SPEC: fim de " + testCounter + " testes, qt erros = " + errors );

		System.out.println("\nFIELD SPEC DATE TIME RECORD: testando " + (maxTest-minTest+1) + " valores (" + minTest + " até +" + maxTest + ")");
		minTest = -1; maxTest = 128; testCounter = 0; errors = 0;
		for (int i = minTest; i <= maxTest; i++) {
			byte[] bytesFieldSpecTest = libProtMDLP.createDateTimeRcd(i, 10000000);
			fieldSpec = libProtMDLP.getFieldSpec(bytesFieldSpecTest);
			if ( fieldSpec != i ) {
				errors++;
				System.out.println("  FIELD SPEC: erro em " + i + ", retorno = " + fieldSpec);
			}
			testCounter++;
		}
		System.out.println( "  FIELD SPEC: fim de " + testCounter + " testes, qt erros = " + errors );

		System.out.println("\nFIELD SPEC EEARING SPEED RECORD: testando " + (maxTest-minTest+1) + " valores (" + minTest + " até +" + maxTest + ")");
		minTest = -1; maxTest = 128; testCounter = 0; errors = 0;
		for (int i = minTest; i <= maxTest; i++) {
			byte[] bytesFieldSpecTest = libProtMDLP.createBearingSpeedRcd(i, 1.0, 1.0);
			fieldSpec = libProtMDLP.getFieldSpec(bytesFieldSpecTest);
			if ( fieldSpec != i ) {
				errors++;
				System.out.println("  FIELD SPEC: erro em " + i + ", retorno = " + fieldSpec);
			}
			testCounter++;
		}
		System.out.println( "  FIELD SPEC: fim de " + testCounter + " testes, qt erros = " + errors );

		System.out.println("\nFIELD SPEC COMPLEMENT RECORD: testando " + (maxTest-minTest+1) + " valores (" + minTest + " até +" + maxTest + ")");
		minTest = -1; maxTest = 128; testCounter = 0; errors = 0;
		for (int i = minTest; i <= maxTest; i++) {
			byte[] bytesFieldSpecTest = libProtMDLP.createComplementRcd(i, 1);
			fieldSpec = libProtMDLP.getFieldSpec(bytesFieldSpecTest);
			if ( fieldSpec != i ) {
				errors++;
				System.out.println("  FIELD SPEC: erro em " + i + ", retorno = " + fieldSpec);
			}
			testCounter++;
		}
		System.out.println( "  FIELD SPEC: fim de " + testCounter + " testes, qt erros = " + errors );

		
	}
	
	public void testMDLPId() {
		
		int testCounter = 0, errors = 0, fieldSpec = 0;
		MDLP_Protocol_v2021 libProtMDLP = new MDLP_Protocol_v2021();
		int minTest = 0, maxTest = 0;
		byte [] bytesMDLPId; int mdlpIdValue;
		minTest = -1; maxTest = 128;
		System.out.println("\nMDLP ID TEST: testando " + (maxTest-minTest+1) + " valores (" + minTest + " até +" + maxTest + "), incremento 1");
		
		for (int i = minTest; i <= maxTest; i++) {
			bytesMDLPId = libProtMDLP.createMDLPId(i);
			mdlpIdValue = libProtMDLP.getMDLPId(bytesMDLPId);
			if ( mdlpIdValue != i ) {
				errors++;
				System.out.println("  MDLP ID: erro em " + i + ", retorno = " + fieldSpec + " (INVALID_MDLP_ID)");
			}
			testCounter++;
		}
		System.out.println( "  MDLP ID: fim de " + testCounter + " testes, qt erros = " + errors );
		
	}
	
	public void testSourceId() {
		
		int testCounter = 0, errors = 0, sourceIdValue = 0;
		MDLP_Protocol_v2021 libProtMDLP = new MDLP_Protocol_v2021();
		int minTest = 0, maxTest = 0;
		byte[] bytesSourceId;
		
		minTest = -1; maxTest = 16385;
		System.out.println("\nSOURCE ID TEST: testando " + (maxTest-minTest+1) + " valores (" + minTest + " até " + maxTest + "), incremento 1");
		
		for ( int i = minTest; i <= maxTest; i++ ) {
			bytesSourceId = libProtMDLP.createSourceIdRcd( 1, i );
			sourceIdValue = libProtMDLP.getSourceId(bytesSourceId);
			if ( i != sourceIdValue ) {
				System.out.println("  SOURCE ID: erro em " + i + ", retorno = " + sourceIdValue + " (INVALID_SOURCE_ID)" );
				errors++;
			}
			testCounter++;
		}
		
		System.out.println( "  SOURCE ID: fim de " + testCounter + " testes, Qt erros = " + errors );
		
	}
	
	public void testSystemId() {

		int testCounter = 0, errors = 0, fieldSpec = 0;
		MDLP_Protocol_v2021 libProtMDLP = new MDLP_Protocol_v2021();
		int minTest = 0, maxTest = 0;
		byte [] bytesSystemId; int systemIdValue;
		minTest = -1; maxTest = 128;
		System.out.println("\nSYSTEM ID TEST: testando " + (maxTest-minTest+1) + " valores (" + minTest + " até +" + maxTest + "), incremento 1");
		
		for (int i = minTest; i <= maxTest; i++) {
			bytesSystemId = libProtMDLP.createSystemId(i);
			systemIdValue = libProtMDLP.getSystemId(bytesSystemId);
			if ( systemIdValue != i ) {
				errors++;
				System.out.println("  SYSTEM ID: erro em " + i + ", retorno = " + fieldSpec + " (INVALID_SYSTEM_ID)");
			}
			testCounter++;
		}
		System.out.println( "  SYSTEM ID: fim de " + testCounter + " testes, qt erros = " + errors );
				
	}
	
	public void testTrackId () {
		
		int testCounter = 0, errors = 0, trackIdValue = 0;
		MDLP_Protocol_v2021 libProtMDLP = new MDLP_Protocol_v2021();
		int minTest = 0, maxTest = 0;
		byte[] bytesTrackId;
		
		minTest = -1; maxTest = 16384;
		System.out.println("\nTRACK ID TEST: testando " + (maxTest-minTest+1) + " valores (" + minTest + " até " + maxTest + "), incremento 1");
		
		for ( int i = minTest; i <= maxTest; i++ ) {
			bytesTrackId = libProtMDLP.createTrackIdRcd( 1, i );
			trackIdValue = libProtMDLP.getTrackId(bytesTrackId);
			if ( i != trackIdValue ) {
				System.out.println("  TRACK ID: erro em " + i + ", retorno = " + trackIdValue + " (INVALID_TRACK_ID)" );
				errors++;
			}
			testCounter++;
		}
		
		System.out.println( "  TRACK ID: fim de " + testCounter + " testes, Qt erros = " + errors );
		
	}
	
	
	public void testCalcAzimFromXY() {
		
		double x = 0.0, y = 0.0;
		
		x = 0.0; y = 0.0;
		System.out.println("X = " + String.valueOf(x) + ", Y = " + String.valueOf(y) + ", Azimute = " + String.valueOf(Utils.calcAzimFromXY(x, y)) + ", Vel = " + String.valueOf(Utils.calcVelocityFromXY(x, y)) );
		x = 0.0; y = 10.0;
		System.out.println("X = " + String.valueOf(x) + ", Y = " + String.valueOf(y) + ", Azimute = " + String.valueOf(Utils.calcAzimFromXY(x, y)) + ", Vel = " + String.valueOf(Utils.calcVelocityFromXY(x, y))  );
		x = 10.0; y = 0.0;
		System.out.println("X = " + String.valueOf(x) + ", Y = " + String.valueOf(y) + ", Azimute = " + String.valueOf(Utils.calcAzimFromXY(x, y)) + ", Vel = " + String.valueOf(Utils.calcVelocityFromXY(x, y))  );
		x = 0.0; y = -10.0;
		System.out.println("X = " + String.valueOf(x) + ", Y = " + String.valueOf(y) + ", Azimute = " + String.valueOf(Utils.calcAzimFromXY(x, y)) + ", Vel = " + String.valueOf(Utils.calcVelocityFromXY(x, y))  );
		x = -10.0; y = 0.0;
		System.out.println("X = " + String.valueOf(x) + ", Y = " + String.valueOf(y) + ", Azimute = " + String.valueOf(Utils.calcAzimFromXY(x, y)) + ", Vel = " + String.valueOf(Utils.calcVelocityFromXY(x, y))  );
		x = 10.0; y = 10.0;
		System.out.println("X = " + String.valueOf(x) + ", Y = " + String.valueOf(y) + ", Azimute = " + String.valueOf(Utils.calcAzimFromXY(x, y)) + ", Vel = " + String.valueOf(Utils.calcVelocityFromXY(x, y))  );
		x = 10.0; y = -10.0;
		System.out.println("X = " + String.valueOf(x) + ", Y = " + String.valueOf(y) + ", Azimute = " + String.valueOf(Utils.calcAzimFromXY(x, y)) + ", Vel = " + String.valueOf(Utils.calcVelocityFromXY(x, y))  );
		x = -10.0; y = -10.0;
		System.out.println("X = " + String.valueOf(x) + ", Y = " + String.valueOf(y) + ", Azimute = " + String.valueOf(Utils.calcAzimFromXY(x, y)) + ", Vel = " + String.valueOf(Utils.calcVelocityFromXY(x, y))  );
		x = -10.0; y = 10.0;
		System.out.println("X = " + String.valueOf(x) + ", Y = " + String.valueOf(y) + ", Azimute = " + String.valueOf(Utils.calcAzimFromXY(x, y)) + ", Vel = " + String.valueOf(Utils.calcVelocityFromXY(x, y))  );
		
	}
	
}
