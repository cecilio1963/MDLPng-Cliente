package main;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.ProtocolException;

import main.MDLP_Protocol_v2021_constants;

public class MDLP_Protocol_v2021 {
	
	/**
	 * <p>
	 * Cria o record Proa e Velocidade em vetor de bytes com 4 bytes
	 * </p>
	 * 
	 * @param integerFieldSpec valor inteiro da Expecif. do record BearingSpeed (0 a 127)
	 * @param doubleBearing valor double da proa em graus decimais (0,0 a 359,9)
	 * @param doubleSpeed valor double da velocidade em Km/h (0.0 a 4095.0) km/h
	 * @return vetor de 4 bytes com Field Spec, proa e velocidade
	 */
	public byte[] createBearingSpeedRcd(int integerFieldSpec, double doubleBearing, double doubleSpeed) {

		// VERIFICA SE VALORES ESTÃO DENTRO DA FAIXA DE VALIDADE

		// verifica se o field spec está entre 0 e 127
		if (integerFieldSpec < 0 || integerFieldSpec > 127)
			return null;

		// transforma proa para Big Decimal
		BigDecimal bigBearing = new BigDecimal(doubleBearing).setScale(1, RoundingMode.HALF_EVEN);
		
		//System.out.println(" ------ COMPARE " + bigBearing.toString() + " COM 0.0   = " + bigBearing.compareTo(new BigDecimal("0.0")));
		//System.out.println(" ------ COMPARE " + bigBearing.toString() + " COM 359.9 = " + bigBearing.compareTo(new BigDecimal("359.9")));

		// verifica se a proa está entre 0.0 e 359.9
		if (bigBearing.compareTo(new BigDecimal("0.0")) < 0)
			return null;
		if (bigBearing.compareTo(new BigDecimal("359.9")) > 0)
			return null;

		// tranforma velocidade para Big Decimal
		BigDecimal bigSpeed = new BigDecimal(doubleSpeed).setScale(0, RoundingMode.HALF_EVEN);

		// verifica se a velocidade está entre 0 e 4095.0 km/h
		if (bigSpeed.compareTo(new BigDecimal("0.0")) < 0)
			return null;
		if (bigSpeed.compareTo(new BigDecimal("4095.0")) > 0)
			return null;

		// CODIFICA ESPECIFICAÇÃO DO RECORD BEARING SPEED

		// transforme field spec em binário
		String binaryFieldSpec = Integer.toBinaryString(integerFieldSpec);

		// completa o filed spec em binário com zeros à esquerda para ter 7 bits
		while (binaryFieldSpec.length() < 8)
			binaryFieldSpec = "0" + binaryFieldSpec;
		// System.out.println( "\nField Spec inteiro = " + fieldSpec + ", Field Spec
		// binário = " + binaryFieldSpec );

		// CODIFICA BEARING

		// multiplica proa por dez
		BigDecimal shiftedBigBearing = bigBearing.multiply(new BigDecimal(10.0));

		// converte proa multiplicada por 10 para inteiro
		int intBearing = shiftedBigBearing.setScale(0, RoundingMode.HALF_EVEN).intValue();
		// System.out.println(" Longitude = " + originalLongitude + ", int = " +
		// intLongitude );

		// converte o valor inteiro da proa multiplicada por dez para binário
		String binaryIntBearing = Integer.toBinaryString(intBearing);

		// completa a proa em binário com zeros à esquerda para ter 12 bits
		while (binaryIntBearing.length() < 12)
			binaryIntBearing = "0" + binaryIntBearing;
		// System.out.println( "Proa original = " + bearing + ", Proa inteira = " +
		// intBearing + ", Proa em binário = " + binaryIntBearing );

		// CODIFICA VELOCIDADE

		// arredonda converte velocidade para inteiro
		int intSpeed = bigSpeed.setScale(0, RoundingMode.HALF_EVEN).intValue();

		// converte o valor inteiro da velocidade transformada para binário
		String binaryIntSpeed = Integer.toBinaryString(intSpeed);

		// completa a velocidade em binário com zeros à esquerda para ter 12 bits
		while (binaryIntSpeed.length() < 12)
			binaryIntSpeed = "0" + binaryIntSpeed;
		// System.out.println( "Vel original = " + speed + ", Inteira = " + intSpeed +
		// ", em binário = " + binaryIntSpeed );

		// MONTA O REECORD COM TODOS OS CAMPOS

		// monta o record ainda em string
		String binaryBearingSpeedRcd = binaryFieldSpec + binaryIntBearing + binaryIntSpeed;

		// declara variável que vai receber o record em vetor de 4 bytes
		byte[] bytesBearingSpeedRcd = new byte[4];

		// zera os campos por segurança
		for (int i = 0; i < 4; i++)
			bytesBearingSpeedRcd[i] = 0x00;

		// converte spec, proa e velocidade em binário para vetor de bytes
		for (int i = 0; i < 32; i++)
			if (binaryBearingSpeedRcd.charAt(i) == '1')
				bytesBearingSpeedRcd[i / 8] |= bytesBearingSpeedRcd[i / 8] | (0x01 << (7 - (i % 8)));

		// retorna o valor calculado
		return bytesBearingSpeedRcd;
	}

	/**
	 * <p>
	 * Cria o tamanho do bloco em bytes com 2 bytes
	 * </p>
	 * 
	 * @param integerBlockSize tamanho inteiro entre 1 e 65.535 bytes
	 * @return vetor de 2 bytes contendo o tamanho do bloco em binário
	 */
	public byte[] createBlockSize(int integerBlockSize) {

		// verifica se o tamanho está na faixa de valores válidos, de 1 a 65.535
		if (integerBlockSize < 1 || integerBlockSize > 65535)
			return null;

		// converte o tamanho em binário
		String binaryBlockSize = Integer.toBinaryString(integerBlockSize);

		// completa o filed spec em binário com zeros à esquerda para ter 7 bits
		while (binaryBlockSize.length() < 16)
			binaryBlockSize = "0" + binaryBlockSize;

		// declara o o valor do tamanho do bloco em vetor com 2 bytes
		byte[] bytesBlockSize = new byte[2];

		// percorre os bits do tamanho do bloco em binário e define seus valores
		for (int i = 0; i < binaryBlockSize.length(); i++)
			if (binaryBlockSize.charAt(15 - i) == '1')
				bytesBlockSize[1 - (i / 8)] |= (0x01 << (i % 8));

		return bytesBlockSize;
	}

	/**
	 * <p>
	 * Cria o record Complemento em vetor de bytes com 2 bytes
	 * </p>
	 * 
	 * @param integerFieldSpec valor inteiro da Especif. do record Coordenadas (0 a 127)
	 * @param integerComplement valor inteiro do Complemento (0 a 127)
	 * @return vetor de 2 bytes com Field Spec e complemento
	 */

	public byte[] createComplementRcd( int integerFieldSpec, int integerComplement ) {
		
		// verifica se o field spec está entre 0 e 127
		if (integerFieldSpec < 0 || integerFieldSpec > 127)
			return null;

		// CODIFICA ESPECIFICAÇÃO DO RECORD COMPLEMENTO

		// transforme field spec em binário
		String binaryFieldSpec = Integer.toBinaryString(integerFieldSpec);

		// completa o filed spec em binário com zeros à esquerda para ter 7 bits
		while (binaryFieldSpec.length() < 8)
			binaryFieldSpec = "0" + binaryFieldSpec;
		//System.out.println("Field Spec inteiro = " + fieldSpec + ", Field Spec binário = " + binaryFieldSpec);

		// CODIFICA COMPLEMENTO

		// converte o valor inteiro do Track Id para binário
		String binaryComplement = Integer.toBinaryString(integerComplement) ;

		// completa o complemento em binário com zeros à esquerda para ter 8 bits
		while (binaryComplement.length() < 8)
			binaryComplement = "0" + binaryComplement;
		//System.out.println("Complemento inteiro = " + complement + ", Complemento binário sem extensão = " + binaryComplement);

		// MONTA O REECORD COM TODOS OS CAMPOS

		String binaryComplementRecd = binaryFieldSpec + binaryComplement;

		// declara variável que vai receber o record em vetor de 2 bytes
		byte [] bytesComplement = new byte[2];

		// armazena os bits da String do record completo no vetor de bytes
		for (int i = 0; i < binaryComplementRecd.length(); i++)
			if (binaryComplementRecd.charAt(15 - i) == '1')
				bytesComplement[1 - (i / 8)] |= (0x01 << (i % 8));
		
		// retorna o valor calculado
		return bytesComplement;
	}
	
	/**
	 * <p>
	 * Cria o record Coordenadas em vetor de bytes com 9 bytes
	 * </p>
	 * 
	 * @param integerFieldSpec valor inteiro da Especif. do record Coordenadas (0 a 127)
	 * @param doubleLongitude valor double em graus decimais (-179,9999 a +179,9999)
	 * @param doubleLatitude valor double em graus decimais (-90,0000 a +90,0000)
	 * @param doubleAltitude valor double em metros (-10.0000 até 55.534)
	 * @return vetor de 9 bytes com Field Spec, longitude, latitude, altitude
	 */
	public byte[] createCoordRcd(int integerFieldSpec, double doubleLongitude, double doubleLatitude, double doubleAltitude) {

		// VERIFICA SE VALORES ESTÃO DENTRO DA FAIXA DE VALIDADE

		// verifica se o field spec está entre 0 e 127
		if (integerFieldSpec < 0 || integerFieldSpec > 127)
			return null;

		// transforma longitude para Big Decimal
		BigDecimal bigLongitude = new BigDecimal(doubleLongitude).setScale(4, RoundingMode.HALF_EVEN);
		//System.out.println("     BIG LONG = " + bigLongitude.toString() + ", LONG = " + String.valueOf(doubleLongitude));

		// verifica se a longitude está entre -179.9999 e +179.9999
		if (bigLongitude.compareTo(new BigDecimal("-179.9999")) < 0)
			return null;
		if (bigLongitude.compareTo(new BigDecimal("179.9999")) > 0)
			return null;

		// tranforma latitude para Big Decimal
		BigDecimal bigLatitude = new BigDecimal(doubleLatitude);
		
		// verifica se a latitude está entre -90.0000 e + 90.0000		
		if (bigLatitude.compareTo(new BigDecimal("-90.0000")) < 0)
			return null;
		if (bigLatitude.compareTo(new BigDecimal("90.0000")) > 0)
			return null;

		// tranforma altitude para BigDecimal
		BigDecimal bigAltitude = new BigDecimal(doubleAltitude);

		// verifica se a altitude está entre -10.000 e + 55.534 m
		if (bigAltitude.compareTo(new BigDecimal("-10000.0")) < 0)
			return null;
		if (bigAltitude.compareTo(new BigDecimal("55534.0")) > 0)
			return null;

		// CODIFICA ESPECIFICAÇÃO DO RECORD COORDENADA

		// transforme field spec em binário
		String binaryFieldSpec = Integer.toBinaryString(integerFieldSpec);

		// completa o filed spec em binário com zeros à esquerda para ter 7 bits
		while (binaryFieldSpec.length() < 8)
			binaryFieldSpec = "0" + binaryFieldSpec;

		// CODIFICA LONGITUDE

		// elimina o ponto decimal e o sinal da longitude
		BigDecimal unsignedShiftedBigLongitude = bigLongitude.multiply(new BigDecimal("10000")).abs();

		// converte longitude sem sinal e sem ponto decimal para inteiro
		int integerLongitude = unsignedShiftedBigLongitude.setScale(4, RoundingMode.HALF_EVEN).intValue();

		// converte o valor inteiro da longitude transformada para binário
		String binaryIntegerLongitude = Integer.toBinaryString(integerLongitude);

		// completa a longitude em binário com zeros à esquerda para ter 23 bits
		while (binaryIntegerLongitude.length() < 23)
			binaryIntegerLongitude = "0" + binaryIntegerLongitude;

		// insere o bit de sinal da longitude em binário
		if (bigLongitude.compareTo(BigDecimal.ZERO) < 0)
			binaryIntegerLongitude = "1" + binaryIntegerLongitude;
		else
			binaryIntegerLongitude = "0" + binaryIntegerLongitude;

		// CODIFICA LATITUDE

		// elimina o ponto decimal e o sinal da latitude
		BigDecimal unsignedShiftedBigLatitude = bigLatitude.multiply(new BigDecimal("10000")).abs();

		// converte longitude sem sinal e sem ponto decimal para inteiro
		int integerLatitude = unsignedShiftedBigLatitude.setScale(4, RoundingMode.HALF_EVEN).intValue();

		// converte o valor inteiro da latitude transformada para binário
		String binaryIntegerLatitude = Integer.toBinaryString(integerLatitude);

		// completa a latitude em binário com zeros à esquerda para ter 23 bits
		while (binaryIntegerLatitude.length() < 23)
			binaryIntegerLatitude = "0" + binaryIntegerLatitude;

		// insere o bit de sinal da longitude em binário
		if (bigLatitude.compareTo(BigDecimal.ZERO) < 0)
			binaryIntegerLatitude = "1" + binaryIntegerLatitude;
		else
			binaryIntegerLatitude = "0" + binaryIntegerLatitude;

		// CODIFICAÇÃO DA ALTITUDE

		// arredonda para 0 casas decimais
		BigDecimal roundedBigAltitude = bigAltitude.setScale(0, RoundingMode.HALF_EVEN);

		// aplica o deslocamento no valor da altitude para ficar entre -32.767 e +32.767
		BigDecimal shiftedRoundedBigAltitude = roundedBigAltitude.subtract(new BigDecimal("22767.0"));

		// elimina o sinal
		BigDecimal unsignedShiftedRoundedBigAltitude = shiftedRoundedBigAltitude.abs();

		// converte de big decimal para inteiro
		int intTransfRoundAlt = unsignedShiftedRoundedBigAltitude.intValue();

		// converte o valor da altitude transformada para binário
		String binaryIntTransformedAlt = Integer.toBinaryString(intTransfRoundAlt);

		// completa a altitude em binário com 0 à esquerda para ter 15 bits
		while (binaryIntTransformedAlt.length() < 15)
			binaryIntTransformedAlt = "0" + binaryIntTransformedAlt;

		// insere o bit de sinal da altitude em binário
		if (shiftedRoundedBigAltitude.compareTo(BigDecimal.ZERO) < 0)
			binaryIntTransformedAlt = "1" + binaryIntTransformedAlt;
		else
			binaryIntTransformedAlt = "0" + binaryIntTransformedAlt;

		// MONTA O REECORD COM TODOS OS CAMPOS

		// monta o record das coordenadas ainda em string
		String binaryCoord = binaryFieldSpec + binaryIntegerLongitude + binaryIntegerLatitude + binaryIntTransformedAlt;

		// declara variável que vai receber as coordenadas em vetor de 9 bytes
		byte[] bytesCoord = new byte[9];

		// zera os campos por segurança
		for (int i = 0; i < 9; i++)
			bytesCoord[i] = 0x00;

		// converte coordenadas em binário para vetor de bytes
		for (int i = 0; i < 72; i++)
			if (binaryCoord.charAt(i) == '1')
				bytesCoord[i / 8] |= bytesCoord[i / 8] | (0x01 << (7 - (i % 8)));

		// retorna o valor calculado
		return bytesCoord;
		
	}

	/**
	 * <p>
	 * Cria o record com a especificação do record e o time stamp unix em vetor de
	 * bytes
	 * </p>
	 * 
	 * @param integerFieldSpec     (int) valor inteiro da especificação do record
	 * @param integerTimeStamp (long): instante em long (Unix time stamp)
	 * @return vetor de bytes com 5 bytes representando Spec e time stamp
	 */
	public byte[] createDateTimeRcd(int integerFieldSpec, int integerTimeStamp) {

		// verifica se o field spec está entre 0 e 127
		if (integerFieldSpec < 0 || integerFieldSpec > 127)
			return null;

		// CODIFICA ESPECIFICAÇÃO DO RECORD TIME STAMP

		// transforme field spec em binário
		String binaryFieldSpec = Integer.toBinaryString(integerFieldSpec);

		// completa o filed spec em binário com zeros à esquerda para ter 8 bits
		while (binaryFieldSpec.length() < 8)
			binaryFieldSpec = "0" + binaryFieldSpec;
		// System.out.println( "Field Spec inteiro = " + fieldSpec + ", Field Spec
		// binário = " + binaryFieldSpec );

		// CODIFICA TIME STAMP

		// transforme time stamp em binário
		String binaryTimeStamp = Long.toBinaryString(integerTimeStamp);

		// completa o time stamp em binário com zeros à esquerda para ter 32 bits
		while (binaryTimeStamp.length() < 32)
			binaryTimeStamp = "0" + binaryTimeStamp;
		// System.out.println( "Time stamp inteiro = " + String.valueOf(longTimeStamp) +
		// ", em binário = " + binaryTimeStamp );

		String strSpecTimeStamp = binaryFieldSpec + binaryTimeStamp;

		byte[] bytesTimeStamp = new byte[5];

		// converte spec e tims stamp em binário para vetor de bytes
		for (int i = 0; i < 40; i++)
			if (strSpecTimeStamp.charAt(i) == '1')
				bytesTimeStamp[i / 8] |= bytesTimeStamp[i / 8] | (0x01 << (7 - (i % 8)));

		return bytesTimeStamp;
	}

	/**
	 * <p>
	 * Cria Id do MDLP, limitado a 7 bits (0 a 127). Extensível no futuro.
	 * </p>
	 * 
	 * @param integerMDLPId valor inteiro postivo (0 a 127) do Id do MDLP
	 * @return Vetor de 1 byte com Id do MDLP binário e bit de extensão. Nulo se inválido.
	 */
	public byte[] createMDLPId(int integerMDLPId) {
		
		// verifica se valor submetido é válido
		if ( integerMDLPId < 0 || integerMDLPId > 127 )
			return null;

		// obtem valor do Id em binário
		String binaryMDLPId = Integer.toBinaryString(integerMDLPId);

		// obtem o nr de bits do valor binário
		int nrBitsIntValue = binaryMDLPId.length();

		// obtem o nr bytes do valor do Id em bytes
		int nrBytesMDLPId = (nrBitsIntValue - 1) / 7 + 1;

		// obtem o nr bits significativos do valor em bytes
		int nrBitsBytesValue = binaryMDLPId.length() + nrBytesMDLPId;

		// declara o valor do Id em bytes
		byte[] bytesMDLPId = new byte[nrBytesMDLPId];

		// zera todos valores dos bytes por segurança
		for (int i = 0; i < nrBytesMDLPId; i++)
			bytesMDLPId[i] = 0x00;

		// declara indicador do bit do MDLPId inteiro em binário a ser processado
		int bitBinaryIntegerMDLPIdToBeProcessed = nrBitsIntValue - 1;

		// percorre os bits do Id em bytes e define seus valores
		for (int i = 0; i < nrBitsBytesValue; i++) {
			// verifica se é bit de extensão
			if (i % 8 == 0) {
				// verifica se é o byte menos significativo (mais à direita, último do vetor), que é sempre zero, tornando os demais = 1
				if (i > 7)
					bytesMDLPId[nrBytesMDLPId - (i / 8) - 1] |= 0x01;
			}
			// não é bit de extensão
			else {
				if (binaryMDLPId.charAt(bitBinaryIntegerMDLPIdToBeProcessed) == '1')
					bytesMDLPId[nrBytesMDLPId - (i / 8) - 1] |= (0x01 << (i % 8));
				bitBinaryIntegerMDLPIdToBeProcessed--;
			}
		}

		return bytesMDLPId;
	}
	
	/**
	 * <p>
	 * Cria o record do Id da fonte dentro do EAD/Sistema em questão. Extensível no futuro.
	 * </p>
	 * 
	 * @param integerFieldSpec valor inteiro da Especif. do record do Id da fonte (0 a 127)
	 * @param integerSourceId valor inteiro da Especif da fonte (0 a 16.383). Extensível no futuro.
	 * @return Vetor de bytes com Field Spec e Source Id
	 */
	public byte[] createSourceIdRcd(int integerFieldSpec, int integerSourceId) {

		// verifica se o field spec está entre 0 e 127
		if (integerFieldSpec < 0 || integerFieldSpec > 127)
			return null;
		
		// verifica se o Source Id é maior do que zero ou maior do que 16.383
		if (integerSourceId < 0 || integerSourceId > 16383)
			return null;

		// CODIFICA ESPECIFICAÇÃO DO RECORD SOURCE ID

		// transforme field spec em binário
		String binaryFieldSpec = Integer.toBinaryString(integerFieldSpec);

		// completa o filed spec em binário com zeros à esquerda para ter 7 bits
		while (binaryFieldSpec.length() < 8)
			binaryFieldSpec = "0" + binaryFieldSpec;

		// CODIFICA SOURCE ID

		// converte o valor inteiro do Source Id para binário
		String binarySourceIdAux = Integer.toBinaryString(integerSourceId);

		// completa o SourceId em binário com zeros à esquerda para ser múltiplo de 7 bit
		while ((binarySourceIdAux.length() % 7) != 0)
			binarySourceIdAux = "0" + binarySourceIdAux;

		// insere os bits de extensão no Source Id
		String binarySourceId = "";
		int extensionCount = 0;
		for (int i = 0; i < (8 * (binarySourceIdAux.length() / 7)); i++) // percorre as n*8 posições do Source Id
																			// binário com extensões
			if ((i % 8) == 0) { // verifica se é local do bit de extensão
				extensionCount++;
				if (i == 0) // bit de extensão é zero
					binarySourceId = "0"; // primeiro bit de extensão
				else
					binarySourceId = "1" + binarySourceId; // demais bits de extensão
			} else
				binarySourceId = binarySourceIdAux.charAt(binarySourceIdAux.length() - 1 - i + extensionCount)
						+ binarySourceId;

		// MONTA O REECORD COM TODOS OS CAMPOS

		String binarySourceIdRecd = binaryFieldSpec + binarySourceId;

		// declara variável que vai receber o record em vetor de 4 bytes
		byte[] bytesSourceId = new byte[1 + binarySourceId.length() / 7];

		// armazena os bits da String do record completo no vetor de bytes
		for (int i = 0; i < (bytesSourceId.length * 8); i++)
			if (binarySourceIdRecd.charAt(i) == '1')
				bytesSourceId[i / 8] |= bytesSourceId[i / 8] | (0x01 << (7 - (i % 8)));

		// retorna o valor calculado
		return bytesSourceId;
		
	}
	
	/**
	 * <p>
	 * Cria Id do Sistema (EAD, Sistema de C2 etc), limitado a 7 bits (0 a 127). Extensível no futuro
	 * </p>
	 * 
	 * @param integerSystemId valor inteiro positivo (0 a 127) do Id do Sistema
	 * @return Vetor de 1 byte com Id do Sistema binário e bit de extensão. Nulo se inválido.
	 */
	public byte[] createSystemId(int integerSystemId) {
		
		if (integerSystemId < 0 || integerSystemId > 127)
			return null;

		// obtem valor do Id em binário
		String binarySystemId = Integer.toBinaryString(integerSystemId);

		// obtem o nr de bits do valor binário
		int nrBitsIntValue = binarySystemId.length();

		// obtem o nr bytes do valor do Id em bytes
		int nrBytesSystemId = (nrBitsIntValue - 1) / 7 + 1;

		// obtem o nr bits significativos do valor em bytes
		int nrBitsBytesValue = binarySystemId.length() + nrBytesSystemId;

		// declara o valor do Id em bytes
		byte[] bytesSystemId = new byte[nrBytesSystemId];

		// zera todos valores dos bytes por segurança
		for (int i = 0; i < nrBytesSystemId; i++)
			bytesSystemId[i] = 0x00;

		// declara indicador do bit do MDLPId inteiro em binário a ser processado
		int bitBinaryIntegerSystemIdToBeProcessed = nrBitsIntValue - 1;

		// percorre os bits do Id em bytes e define seus valores
		for (int i = 0; i < nrBitsBytesValue; i++) {
			// verifica se é bit de extensão
			if (i % 8 == 0) {
				// verifica se é o byte menos significativo (mais à direita, último do vetor), que é sempre zero, tornando os demais = 1
				if (i > 7)
					bytesSystemId[nrBytesSystemId - (i / 8) - 1] |= 0x01;
			}
			// não é bit de extensão
			else {
				if (binarySystemId.charAt(bitBinaryIntegerSystemIdToBeProcessed) == '1') {
					bytesSystemId[nrBytesSystemId - (i / 8) - 1] |= (0x01 << (i % 8));
				}
				bitBinaryIntegerSystemIdToBeProcessed--;
			}
		}

		return bytesSystemId;
	}
	
	/**
	 * <p>
	 * Cria o record Id do acompanhamento. Extensível no futuro
	 * </p>
	 * 
	 * @param integerFieldSpec valor inteiro da Especif. do record do Id do acompanhamento (0 a 127)
	 * @param integerTrackId  valor inteiro do Id do acompanhamento (0 a 16.383). Extensível no futuro.
	 * @return vetor de bytes com Field Id e Track Id
	 */
	public byte[] createTrackIdRcd(int integerFieldSpec, int integerTrackId) {

		// verifica se o field spec está entre 0 e 127
		if (integerFieldSpec < 0 || integerFieldSpec > 127)
			return null;
		
		// verifica se o track id é menor do que zero ou maior do que 16.383
		if (integerTrackId < 0 || integerTrackId > 16383)
			return null;

		// CODIFICA ESPECIFICAÇÃO DO RECORD TRACK ID

		// transforme field spec em binário
		String binaryFieldSpec = Integer.toBinaryString(integerFieldSpec);

		// completa o filed spec em binário com zeros à esquerda para ter 7 bits
		while (binaryFieldSpec.length() < 8)
			binaryFieldSpec = "0" + binaryFieldSpec;

		// CODIFICA TRACK ID

		// converte o valor inteiro do Track Id para binário
		String binaryTrackIdAux = Integer.toBinaryString(integerTrackId);

		// completa o TrackId em binário com zeros à esquerda para ser múltiplo de 7
		// bites
		while ((binaryTrackIdAux.length() % 7) != 0)
			binaryTrackIdAux = "0" + binaryTrackIdAux;

		// insere os bits de extensão no Track Id
		String binaryTrackId = "";
		int extensionCount = 0;
		 // percorre as n*8 posições do Track Id binário com extensões
		for (int i = 0; i < (8 * (binaryTrackIdAux.length() / 7)); i++)
			// verifica se é local do bit de extensão
			if ((i % 8) == 0) { 
				extensionCount++;
				 // bit de extensão é zero
				if (i == 0)
					binaryTrackId = "0"; // primeiro bit de extensão
				else
					binaryTrackId = "1" + binaryTrackId; // demais bits de extensão
			} else
				binaryTrackId = binaryTrackIdAux.charAt(binaryTrackIdAux.length() - 1 - i + extensionCount)
						+ binaryTrackId;

		// MONTA O REECORD COM TODOS OS CAMPOS

		String binaryTrackIdRecd = binaryFieldSpec + binaryTrackId;

		// declara variável que vai receber o record em vetor de 4 bytes
		byte[] bytesTrackId = new byte[1 + binaryTrackId.length() / 7];

		// armazena os bits da String do record completo no vetor de bytes
		for (int i = 0; i < (bytesTrackId.length * 8); i++)
			if (binaryTrackIdRecd.charAt(i) == '1')
				bytesTrackId[i / 8] |= bytesTrackId[i / 8] | (0x01 << (7 - (i % 8)));

		// retorna o valor calculado
		return bytesTrackId;
		
	}
	
	/**
	 * <p>
	 * Obtem o valor da altitude do record Coordenadas
	 * </p>
	 * 
	 * @param bytesCoordRcd (byte[]): record Coordenadas com 9 bytes
	 * @return Altitude em inteiro em metros
	 */
	public int getAltitude(byte[] bytesCoordRcd) {

		if (bytesCoordRcd == null)
			return MDLP_Protocol_v2021_constants.INVALID_ALTITUDE ;
		
		int intAlt = 0;

		// percorre bytes da altitude (7 e 8) dentro das coordenadas
		for (int i = 8; i >= 7; i--)
			// percorre os bits de cada byte da altitude
			for (int j = 0; j < 8; j++)
				// desconsidera o bit de sinal
				if ((i != 7) || (j != 7))
					// verifica se o bit é 1 e calcula a sua potência de 2
					if ((bytesCoordRcd[i] & (0x01 << j)) > 0)
						intAlt += Math.pow(2, 8 * (8 - i) + j);

		// converte altitude inteira em BigDecimal
		BigDecimal bigTransfUnsignedAlt = new BigDecimal(intAlt);

		// recupera o sinal da altitude deslocada
		BigDecimal bigSignedTransfAlt;
		if ((bytesCoordRcd[7] & 0x80) != 0)
			bigSignedTransfAlt = bigTransfUnsignedAlt.multiply(new BigDecimal("-1.0"));
		else
			bigSignedTransfAlt = bigTransfUnsignedAlt;

		// desfaz o deslocamento
		BigDecimal bigSignedAlt = bigSignedTransfAlt.add(new BigDecimal("22767"));

		return bigSignedAlt.intValue();
		
	}
	
	/**
	 * <p>
	 * Obtem o valor da proa do record Proa e velocidade
	 * </p>
	 * 
	 * @param bytesCoord (byte[]): record Proa e Velocidade com 4 bytes
	 * @return Proa em double em graus decimais
	 */
	public double getBearing(byte[] bytesBearingSpeedRcd) {
		
		if (bytesBearingSpeedRcd == null)
			return MDLP_Protocol_v2021_constants.INVALID_ALTITUDE;

		int intBearing = 0;

		// percorre os bytes do bearing (1 e metade do 2) dentro do vetor de bytes
		for (int i = 2; i >= 1; i--)
			// percorre os bits de cada byte da proa
			for (int j = 0; j < 8; j++)
				// trata cada byte separadamente
				if ((i == 2) && (j > 3)) {
					// verifica se o bit é 1 e calcula a sua potência de 2
					if ((bytesBearingSpeedRcd[i] & (0x01 << j)) > 0)
						intBearing += Math.pow(2, (j - 4));
				} else if (i == 1) {
					// verifica se o bit é 1 e calcula a sua potência de 2
					if ((bytesBearingSpeedRcd[i] & (0x01 << j)) > 0)
						intBearing += Math.pow(2, 4 + j);
				}

		// converte bearing inteira em BigDecimal
		BigDecimal bigTransfBearing = new BigDecimal(intBearing);

		// retorna o ponto decimal
		BigDecimal bigBearing = bigTransfBearing.divide(new BigDecimal(10.0));

		// System.out.println(" Na decodificação: " + " Em inteiro = " + intBearing + ",
		// em Big Decimal = " + bigTransfBearing.toString() + ", Bearing = " +
		// bigBearing.toString());
		return bigBearing.doubleValue();
	}
	
	/**
	 * <p>
	 * Obtém o valor tamanho do bloco em bytes (1 a 65.535)
	 * </p>
	 * 
	 * @param bytesBlockSize (byte[]): vetor de bytes com tamanho do bloco
	 * @return Tamanho do bloco inteiro (1 a 65.535)
	 */
	public int getBlockSize(byte[] bytesBlockSize) {

		if (bytesBlockSize == null)
			return MDLP_Protocol_v2021_constants.INVALID_BLOCK_SIZE;
		
		int integerBlockSize = 0;

		// percorre os bits a partir do menos significativo e calcula o valor inteiro
		for (int i = bytesBlockSize.length - 1; i >= 0; i--)
			for (int j = 0; j <= 7; j++)
				if ((bytesBlockSize[i] & (0x01 << j)) != 0)
					integerBlockSize += Math.pow(2, (bytesBlockSize.length - 1 - i) * 8 + j);

		return integerBlockSize;
		
	}
	
	/**
	 * <p>
	 * Obtem o valor do complemento. Não extensível
	 * </p>
	 * 
	 * @param bytesComplementRcd (byte[]): record Complemento com 2 bytes
	 * @return Complemento em inteiro
	 */
	public int getComplement(byte[] bytesComplementRcd) {

		if (bytesComplementRcd == null)
			return MDLP_Protocol_v2021_constants.INVALID_COMPLEMENT;
		
		int integerComplement = 0;

		// percorre os bits a partir do bit menos significativo e calcula o valor inteiro
		for (int j = 0; j <= 7; j++)
			if ((bytesComplementRcd[1] & (0x01 << j)) != 0)
					integerComplement += Math.pow(2, j);

		return integerComplement;
	}

	/**
	 * <p>
	 * Obtem o valor da Data hora
	 * </p>
	 * 
	 * @param bytesDateTimeRcd (byte[]): record da data hora com 5 bytes (Spec +
	 *                       data-hora)
	 * @return time stamp em inteiro
	 */
	public int getDateTime(byte[] bytesDateTimeRcd) {
		
		if (bytesDateTimeRcd == null)
			return MDLP_Protocol_v2021_constants.INVALID_DATE_TIME;

		int integerTimeStamp = 0;

		// percorre os bits a partir do menos significativo e calcula o valor inteiro
		for (int i = bytesDateTimeRcd.length - 1; i >= 1; i--)
			for (int j = 0; j <= 7; j++)
				if ((bytesDateTimeRcd[i] & (0x01 << j)) != 0)
					integerTimeStamp += Math.pow(2, (bytesDateTimeRcd.length - 1 - i) * 8 + j);
		// System.out.println(" GET: valor inteiro = " + integerBlockSize );

		return integerTimeStamp;
	}

	/**
	 * <p>
	 * Obtem o valor da especificação de um campo (1o byte)
	 * </p>
	 * 
	 * @param bytesField (byte[]): campo cujo 1o byte é uma especificação de campo
	 * @return Especificação do campo em inteiro
	 */
	public int getFieldSpec(byte[] bytesField) {
		
		if (bytesField == null)
			return MDLP_Protocol_v2021_constants.INVALID_FIELD_SPEC;

		int integerFieldSpec = 0;

		// percorre os bits a partir do bit menos significativo e calcula o valor inteiro
		for (int j = 0; j <= 7; j++)
			if ((bytesField[0] & (0x01 << j)) != 0)
					integerFieldSpec += Math.pow(2, j);

		return integerFieldSpec;
	}

	/**
	 * <p>
	 * Obtem o valor da latitude do record Coordenadas
	 * </p>
	 * 
	 * @param bytesCoordRcd (byte[]): record Coordenadas com 9 bytes
	 * @return Latitude em double em graus decimais com até 4 casas decimais
	 */
	public double getLatitude(byte[] bytesCoordRcd) {
		
		if (bytesCoordRcd == null)
			return MDLP_Protocol_v2021_constants.INVALID_LATITUDE;

		int intLatitude = 0;

		byte[] bytesLatitude = new byte[3];

		// obtem os bytes da latitude
		for (int i = 4; i < 7; i++)
			bytesLatitude[i - 4] = bytesCoordRcd[i];

		// percorre os bytes da latitude
		for (int i = 2; i >= 0; i--)
			// percorre os bits de cada byte da latitude
			for (int j = 0; j < 8; j++)
				// desconsidera o bit de sinal
				if ((i != 0) || (j != 7))
					// verifica se o bit é 1 e calcula a sua potência de 2
					if ((bytesLatitude[i] & (0x01 << j)) > 0)
						intLatitude += Math.pow(2, 8 * (2 - i) + j);

		double latitude = intLatitude / 10000.0;

		if ((bytesLatitude[0] & 0x80) != 0)
			latitude *= -1.0;

		return latitude;
		
	}
	
	/**
	 * <p>
	 * Obtem o valor da longitude do record Coordenadas
	 * </p>
	 * 
	 * @param bytesCoordRcd (byte[]): record Coordenadas com 9 bytes
	 * @return Longitude em double em graus decimais com até 4 casas decimais
	 */
	public double getLongitude(byte[] bytesCoordRcd) {
		
		if (bytesCoordRcd == null)
			return MDLP_Protocol_v2021_constants.INVALID_LONGITUDE;

		int intLongitude = 0;

		byte[] bytesLongitude = new byte[3];

		// obtem os bytes da longitude
		for (int i = 1; i < 4; i++)
			bytesLongitude[i - 1] = bytesCoordRcd[i];

		// percorre os bytes da longitude
		for (int i = 2; i >= 0; i--)
			// percorre os bits de cada byte da longitude
			for (int j = 0; j < 8; j++)
				// desconsidera o bit de sinal
				if ((i != 0) || (j != 7))
					// verifica se o bit é 1 e calcula a sua potência de 2
					if ((bytesLongitude[i] & (0x01 << j)) > 0)
						intLongitude += Math.pow(2, 8 * (2 - i) + j);

		BigDecimal unsignedBigLongitude = new BigDecimal(intLongitude / 10000.0).setScale(4, RoundingMode.HALF_EVEN);

		BigDecimal bigLongitude;
		if ((bytesLongitude[0] & 0x80) != 0)
			bigLongitude = unsignedBigLongitude.multiply(new BigDecimal(-1.0)).setScale(4, RoundingMode.HALF_EVEN);
		else
			bigLongitude = unsignedBigLongitude;

		return bigLongitude.doubleValue();
	}

	/**
	 * <p>
	 * Obtem o valor do Id do MDLP (0 a 127)
	 * </p>
	 * 
	 * @param byteMDLPId (byte[]) vetor de bytes com Id do MDLP, com 1 byte.
	 * @return Id do MDLP inteiro (0 a 127). INVALID_MDLP_ID se inválido.
	 */
	public int getMDLPId(byte[] byteMDLPId) {
		
		// verifica se o byte com MDLP Id é válido
		if (byteMDLPId == null || byteMDLPId.length > 1)
			return MDLP_Protocol_v2021_constants.INVALID_MDLP_ID;

		int integerMDLPId = 0;

		// percorre bits a partir do menos significativo desprezando os bits de extensão e calcula o valor inteiro
		for (int i = byteMDLPId.length - 1; i >= 0; i--)
			for (int j = 1; j <= 7; j++)
				if ((byteMDLPId[i] & (0x01 << j)) != 0)
					integerMDLPId += Math.pow(2, (byteMDLPId.length - 1 - i) * 7 + j - 1);

		return integerMDLPId;

	}
	
	/**
	 * <p>
	 * Obtém o valor do Id da fonte (0 a 16.383)
	 * </p>
	 * 
	 * @param bytesSourceIdRcd (byte[]): vetor de bytes com Id da fonte 
	 * @return Id da fonte em inteiro (0 a 16.383)
	 */
	public int getSourceId(byte[] bytesSourceIdRcd) {
		
		if (bytesSourceIdRcd == null)
			return MDLP_Protocol_v2021_constants.INVALID_SOURCE_ID;

		int integerSourceId = 0;

		// percorre os bits a partir do menos significativo desprezando os bits de
		// extensão e calcula o valor inteiro.
		// O 1o byte é a Especificação do record, não faz parte do Id da Fonte
		for (int i = bytesSourceIdRcd.length - 1; i >= 1; i--)
			for (int j = 1; j <= 7; j++)
				if ((bytesSourceIdRcd[i] & (0x01 << j)) != 0)
					integerSourceId += Math.pow(2, (bytesSourceIdRcd.length - 1 - i) * 7 + j - 1);

		return integerSourceId;
		
	}
	
	/**
	 * <p>
	 * Obtem o valor da velocidade do record de proa e velocidade
	 * </p>
	 * 
	 * @param bytesCoord (byte[]): record Proa e Velocidade com 4 bytes
	 * @return Velocidade em inteiro em km/h (0 a 4095)
	 */
	public int getSpeed(byte[] bytesBearingSpeedRcd) {
		
		if (bytesBearingSpeedRcd == null)
			return MDLP_Protocol_v2021_constants.INVALID_SPEED;

		int intSpeed = 0;

		// percorre os bytes da velocidade (metade do 2 e 3) dentro do vetor de bytes
		for (int i = 3; i >= 2; i--)
			// percorre os bits de cada byte da velocidade
			for (int j = 0; j < 8; j++)
				// trata cada byte separadamente
				if ((i == 2) && (j < 4)) {
					// verifica se o bit é 1 e calcula a sua potência de 2
					if ((bytesBearingSpeedRcd[i] & (0x01 << j)) > 0)
						intSpeed += Math.pow(2, 8 + j);
				} else if (i == 3) {
					// verifica se o bit é 1 e calcula a sua potência de 2
					if ((bytesBearingSpeedRcd[i] & (0x01 << j)) > 0)
						intSpeed += Math.pow(2, j);
				}

		// System.out.println(" Na decodificação: " + " Em inteiro = " + intSpeed );
		return intSpeed;
	}

	/**
	 * <p>
	 * Obtem o valor do Id do Sistema (EAD, Sistema de C2 etc) (0 a 127)
	 * </p>
	 * 
	 * @param byteSystemId (byte[]) vetor de bytes com Id do Sistema, com 1 byte.
	 * @return Id do Sistema inteiro (0 a 127). INVALID_SYSTEM_ID se inválido.
	 */
	public int getSystemId(byte[] byteSystemId) {
		
		if (byteSystemId == null || byteSystemId.length > 1)
			return MDLP_Protocol_v2021_constants.INVALID_SYSTEM_ID;

		int integerSystemId = 0;

		// percorre os bits a partir do menos significativo desprezando os bits de extensão e calcula o valor inteiro
		for (int i = byteSystemId.length - 1; i >= 0; i--)
			for (int j = 1; j <= 7; j++)
				if ((byteSystemId[i] & (0x01 << j)) != 0)
					integerSystemId += Math.pow(2, (byteSystemId.length - 1 - i) * 7 + j - 1);

		return integerSystemId;
	}
	
	/**
	 * <p>
	 * Obtem o valor do Id do acompanhamento (0 a 16.383)
	 * </p>
	 * 
	 * @param bytesTrackIdRcd (byte[]): vetor de bytes com Id do acompanhamento
	 * @return Id do acompanhamento em inteiro (0 a 16.383)
	 */
	public int getTrackId(byte[] bytesTrackIdRcd) {
		
		if (bytesTrackIdRcd == null)
			return MDLP_Protocol_v2021_constants.INVALID_TRACK_ID;

		int integerTrackId = 0;

		// percorre os bits a partir do menos significativo desprezando os bits de
		// extensão e calcula o valor inteiro.
		// O 1o byte é a Especificação do record, não faz parte do Id do acompanhamento
		for (int i = bytesTrackIdRcd.length - 1; i >= 1; i--)
			for (int j = 1; j <= 7; j++)
				if ((bytesTrackIdRcd[i] & (0x01 << j)) != 0)
					integerTrackId += Math.pow(2, (bytesTrackIdRcd.length - 1 - i) * 7 + j - 1);

		return integerTrackId;
		
	}

	/**
	 * <p>
	 * Imprime o valor dos bits de um vetor de bytes
	 * </p>
	 * 
	 * @param label      (String): rótulo do vetor de bytes a ser exibido
	 * @param valueBytes (byte[]): vetor de bytes a ter o valor de seus bits
	 *                   exibidos
	 * @return nenhum
	 */
	public void printValue(String label, byte[] valueBytes) {

		System.out.print(label + " em bytes expresso em bits = ");

		for (int i = 0; i < valueBytes.length; i++) {
			for (int j = 7; j >= 0; j--)
				if ((valueBytes[i] & (0x01 << j)) != 0)
					System.out.print("1");
				else
					System.out.print("0");
			System.out.print(" ");
		}

		System.out.print("\n");
	}

}
