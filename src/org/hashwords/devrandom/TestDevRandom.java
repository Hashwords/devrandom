/**
 * Copyright (c) 2015, Sam Hart
 * All rights reserved.
 */

package org.hashwords.devrandom ;

import java.io.ByteArrayInputStream ;
import java.io.ByteArrayOutputStream ;
import java.io.FileNotFoundException ;
import java.io.IOException ;
import java.io.ObjectInputStream ;
import java.io.ObjectOutputStream ;
import java.util.Locale ;
import java.util.Random ;

/**
 * <p>Miscellaneous package-based tests.</p>
 * These tests demonstrate the working status of {@link DevRandom}.<br>
 * They <strong>should not</strong> be considered a form of unit test or any sort of test of randomness.
 * <p><span style="color:red;">WARNING :</span>
 * This class is not supported in anyway, abandon all hope all ye who enter here !</p>
 */
public class TestDevRandom
{
	/**
	 * Test the current status of {@link DevRandom}
	 * @param args	device to use
	 * @throws FileNotFoundException	device not found
	 * @throws InterruptedException
	 * @see DevRandom#DEVICE_NAMES
	 */
	public static void main( String[] args ) throws FileNotFoundException , InterruptedException
	{
		String device = DevRandom.DEV_RANDOM ;
		if( args.length > 0 )
			device = args[ 0 ] ;

		System.out.println( "Attempting to create DevRandom with device : " + device ) ;
		DevRandom random = DevRandomFactory.getInstance( device ) ;

		testSeeding( random ) ;
		piTest( random , 1000000 ) ;
		Thread.sleep( 2000 ) ;

		generalTest( random ) ;
		serializationTest( random ) ;

		popCount( random , 100000000 ) ;

		// check that the stream closes upon garbage collection.
		System.out.println( "Attempting to close " + device ) ;
		random = null ;
		System.out.println( "Object pointer null'd" ) ;
		System.gc() ;
		System.out.println( "Garbage collecter called." ) ;

		Thread.sleep( 4000 ) ;
	}

	/** Do not allow direct instantiation. */
	private TestDevRandom(){}

	/**
	 * Monte Carlo &pi; test.
	 * @param random	object to test.
	 * @param n	number of iterations e.g. 1,000,000
	 */
	public static void piTest( Random random , int n )
	{
		printHeadings( random , "\u03C0" ) ;

		int inside = 0 ;

		double x , y ;
		long start = System.nanoTime() ;
        for( int i = 0 ; i < n ; i++ )
        {
            x = random.nextDouble() ;
            y = random.nextDouble() ;

            if( Math.sqrt( ( x * x ) + ( y * y ) ) <= 1. )
                inside++ ;
        }
        long time = System.nanoTime() - start ;
        time /= 1000 ;

        double myPI = 4. * ( ( double ) inside / n ) ;
        double error = ( myPI - Math.PI ) / Math.PI * 100. ;
		boolean success = Math.abs( myPI - Math.PI ) <= myPI * 0.001 ;

		System.out.println( myPI + " == " + success ) ;
		System.out.println( "Error : " + error + " %" ) ;
		System.out.println( "time = " + time + " ( magnitude 10^" + Math.round( Math.log10( time ) ) + " )\n" ) ;
	}

	/**
	 * Count number of bits set for random uniformly distributed int values.<br>
	 * Outputs number of bits set from 0 to 31.
	 * @param random	object to test.
	 * @param n		number of values e.g. 100,000,000
	 * @see java.util.Random#nextInt()
	 */
	public static void popCount( Random random , int n )
	{
		printHeadings( random , "population count" ) ;

		int[] stats = new int[ 32 ] ;
		for( int i = 0 ; i < n ; i++ )
		{
			if( i % 1000000 == 0 )
				System.out.println( i + " / " + n ) ;

			int value = random.nextInt() ;
			int count = Integer.bitCount( value ) ;
			stats[ count ]++ ;
		}
		for( int i = 0 ; i < stats.length ; i++ )
			System.out.print( stats[ i ] + " ") ;
		System.out.println( "\n" ) ;
	}

	/**
	 * Count number of bits set for random uniformly distributed long values.<br>
	 * Outputs number of bits set from 0 to 63.
	 * @param random	object to test.
	 * @param n		number of values e.g. 100,000,000
	 * @see java.util.Random#nextLong()
	 */
	public static void popCountLong( Random random , int n )
	{
		printHeadings( random , "long population count" ) ;

		int[] stats = new int[ 64 ] ;
		for( int i = 0 ; i < n ; i++ )
		{
			if( i % 1000000 == 0 )
				System.out.println( i + " / " + n ) ;

			long value = random.nextLong() ;
			int count = Long.bitCount( value ) ;
			stats[ count ]++ ;
		}
		for( int i = 0 ; i < stats.length ; i++ )
			System.out.print( stats[ i ] + " ") ;
		System.out.println( "\n" ) ;
	}

	/**
	 * Count number of bits set for random uniformly distributed double values.<br>
	 * Outputs number of bits set from 0 to 63.
	 * @param random	object to test.
	 * @param n		number of values e.g. 100,000,000
	 * @see java.util.Random#nextDouble()
	 */
	public static void popCountDouble( Random random , int n )
	{
		printHeadings( random , "double population count" ) ;

		int[] stats = new int[ 64 ] ;
		for( int i = 0 ; i < n ; i++ )
		{
			if( i % 1000000 == 0 )
				System.out.println( i + " / " + n ) ;

			double value = random.nextDouble() ;
			long bits = Double.doubleToLongBits( value ) ;
			int count = Long.bitCount( bits ) ;
			stats[ count ]++ ;
		}
		for( int i = 0 ; i < stats.length ; i++ )
			System.out.print( stats[ i ] + " ") ;
		System.out.println( "\n" ) ;
	}

	/**
	 * Count number of bits set for random Gaussian ("normally") distributed double values.<br>
	 * Outputs number of bits set from 0 to 63.
	 * @param random	object to test.
	 * @param n		number of values e.g. 100,000,000
	 * @see java.util.Random#nextGaussian()
	 */
	public static void popCountGauss( Random random , int n )
	{
		printHeadings( random , "gaussian population count" ) ;

		int[] stats = new int[ 64 ] ;
		for( int i = 0 ; i < n ; i++ )
		{
			if( i % 1000000 == 0 )
				System.out.println( i + " / " + n ) ;

			double value = random.nextGaussian() ;
			long bits = Double.doubleToLongBits( value ) ;
			int count = Long.bitCount( bits ) ;
			stats[ count ]++ ;
		}
		for( int i = 0 ; i < stats.length ; i++ )
			System.out.print( stats[ i ] + " ") ;
		System.out.println( "\n" ) ;
	}

	/**
	 * Tests wether or not {@link java.util.Random} object can be serialized.
	 * @param random	object to test.
	 * @return	boolean indicating success.
	 */
	public static boolean serializationTest( Random random )
	{
		printHeadings( random , "serialization" ) ;

		ByteArrayOutputStream baos ;
		ByteArrayInputStream bais ;

		boolean success = false ;

		try
        {
			baos = new ByteArrayOutputStream() ;
	        ObjectOutputStream oos = new ObjectOutputStream( baos ) ;
	        oos.writeObject( random ) ;
	        oos.flush() ;
	        oos.close() ;
	        baos.flush() ;
	        baos.close() ;

	        byte[] bytes = baos.toByteArray() ;

	        printBytes( bytes ) ;

	        bais = new ByteArrayInputStream( bytes ) ;
	        ObjectInputStream ois = new ObjectInputStream( bais ) ;
	        random = ( Random )ois.readObject() ;
	        ois.close() ;
	        bais.close() ;

	        if( !DevRandom.class.isInstance( random ) )
	        	success = true ;
        }
        catch( IOException e )
        {
        	System.out.println( "serialization failed." ) ;

        	if( random instanceof DevRandom )
        		success = true ; // test *should* fail on write
        }
        catch( ClassNotFoundException e )
        {
	        e.printStackTrace() ;
        }

		if( success )
			System.out.println( "Success !" ) ;
		System.out.println() ;

		return success ;
	}

	/**
	 * Self seeds PRNG.
	 * @param random	object to test.
	 * @see java.util.Random#nextLong()
	 * @see java.util.Random#setSeed(long)
	 */
	public static void testSeeding( Random random )
	{
		printHeadings( random , "seeding" ) ;

		random.setSeed( random.nextLong() ) ;
		random.setSeed( random.nextLong() ) ;
		random.setSeed( random.nextLong() ) ;
		random.setSeed( random.nextLong() ) ;
		random.setSeed( random.nextLong() ) ;
		random.setSeed( random.nextLong() ) ;
	}

	/**
	 * Miscellaneous
	 * @param random	object to test.
	 */
	private static void generalTest( Random random )
	{
		printHeadings( random , "bytes" ) ;

		byte[] bytes = new byte[ 16 ] ;

		for( int i = 0 ; i < 3 ; i++ )
		{
			random.nextBytes( bytes ) ;
			printBytes( bytes ) ;
		}

		printHeadings( random , "booleans" ) ;

		int trues = 0 ;
		int falses = 0 ;
		for( int i = 0 ; i < 100 ; i++ )
		{
			if( random.nextBoolean() )
				trues++ ;
			else
				falses++ ;
		}
		System.out.println( "true / false : " + trues + " / " + falses ) ;

		printHeadings( random , "ints" ) ;

		int pluses = 0 ;
		int minuses = 0 ;
		for( int i = 0 ; i < 100 ; i++ )
		{
			int randomInt = random.nextInt() ;
			System.out.print( randomInt + " " ) ;
			if( i % 25 == 24 )
				System.out.println() ;
			if( randomInt >= 0 )
				pluses++ ;
			else
				minuses++ ;
		}
		System.out.println( "\n\n" + "+ / -  : " + pluses + " / " + minuses ) ;

		printHeadings( random , "ints % 24" ) ;

		for( int i = 0 ; i < 24 ; i++ )
			System.out.print( random.nextInt( 24 ) + " " ) ;
		System.out.println() ;

		printHeadings( random , "longs" ) ;

		pluses = 0 ;
		minuses = 0 ;
		for( int i = 0 ; i < 100 ; i++ )
		{
			long randomLong = random.nextLong() ;
			System.out.print( randomLong + " " ) ;
			if( i % 25 == 24 )
				System.out.println() ;
			if( randomLong >= 0 )
				pluses++ ;
			else
				minuses++ ;
		}
		System.out.println( "\n\n" + "+ / -  : " + pluses + " / " + minuses ) ;

		printHeadings( random , "doubles" ) ;
		for( int i = 0 ; i < 24 ; i++ )
			System.out.print( random.nextDouble() + " " ) ;
		System.out.println() ;

		printHeadings( random , "floats" ) ;
		for( int i = 0 ; i < 24 ; i++ )
			System.out.print( random.nextFloat() + " " ) ;
		System.out.println() ;

		printHeadings( random , "guassians" ) ;
		for( int i = 0 ; i < 24 ; i++ )
			System.out.print( random.nextGaussian() + " " ) ;
		System.out.println() ;
	}

	/**
	 * print bytes ... obviously
	 * @param bytes	byte[] to print
	 */
	private static void printBytes( byte[] bytes )
	{
		for( byte bite : bytes )
			System.out.print( bite + "\t" ) ;
		System.out.println() ;
	}

	/**
	 * print headings for tests
	 * @param random	object being tested.
	 * @param heading	brief description of test being performed.
	 */
	private static final void printHeadings( Random random , String heading )
	{
		System.out.print( "\n\n\t=== " ) ;
		System.out.print( random.getClass().getSimpleName() ) ;
		System.out.print( "\t" ) ;
		System.out.print( heading.toUpperCase( Locale.UK ) ) ;
		System.out.print( " ===\n\n" ) ;
	}
}
