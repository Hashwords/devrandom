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
 * <p><font color="red">WARNING :</font> Abandon all hope all ye who enter here !</p>
 */
public class TestDevRandom
{
	public static void main( String[] args ) throws FileNotFoundException, InterruptedException
	{
		String device = DevRandom.DEV_RANDOM ;
		if( args.length > 0 )
			device = args[ 0 ] ;

		System.out.println( "Attempting to create DevRandom with device : " + device ) ;
		DevRandom random = DevRandomFactory.getInstance( device ) ;

		testSeeding( random ) ;
		piTest( random ) ;
		Thread.sleep( 2000 ) ;
		generalTest( random ) ;
		serializationTest( random ) ;

		random = null ;
		System.gc() ;

		Thread.sleep( 4000 ) ;
	}

	public static void testSeeding( Random random )
	{
		System.out.println( random.getClass().getSimpleName() ) ;
		printHeadings( "seeding" ) ;

		random.setSeed( random.nextLong() ) ;
		random.setSeed( random.nextLong() ) ;
		random.setSeed( random.nextLong() ) ;
		random.setSeed( random.nextLong() ) ;
		random.setSeed( random.nextLong() ) ;
		random.setSeed( random.nextLong() ) ;
	}

	public static void piTest( Random random ) throws FileNotFoundException
	{
		System.out.println( random.getClass().getSimpleName() ) ;

		int inside = 0 ;

		printHeadings( "\u03C0" ) ;

		double x , y ;
		long start = System.nanoTime() ;
        for( int i = 0 ; i < 1000000 ; i++ )
        {
            x = random.nextDouble() ;
            y = random.nextDouble() ;

            if( Math.sqrt( ( x * x ) + ( y * y ) ) <= 1. )
                inside++ ;
        }
        long time = System.nanoTime() - start ;
        time /= 1000 ;

        double myPI = 4 * ( ( double ) inside / 1000000 ) ;
		boolean success = Math.abs( myPI - Math.PI ) <= myPI * 0.001 ;

		System.out.println( myPI + " == " + success ) ;
		System.out.println( "time = " + time + " ( magnitude 10^" + Math.round( Math.log10( time ) ) + " )\n" ) ;
	}

	public static void generalTest( Random random ) throws FileNotFoundException
	{
		System.out.println( random.getClass().getSimpleName() ) ;

		byte[] bytes = new byte[ 16 ] ;

		printHeadings( "bytes" ) ;

		for( int i = 0 ; i < 3 ; i++ )
		{
			random.nextBytes( bytes ) ;
			printBytes( bytes ) ;
		}

		printHeadings( "booleans" ) ;

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

		printHeadings( "ints" ) ;

		int pluses = 0 ;
		int minuses = 0 ;
		for( int i = 0 ; i < 24 ; i++ )
		{
			int randomInt = random.nextInt() ;
			System.out.print( randomInt + " " ) ;
			if( randomInt >= 0 )
				pluses++ ;
			else
				minuses++ ;
		}
		System.out.println( "\n\n" + "+ / -  : " + pluses + " / " + minuses ) ;

		printHeadings( "ints % 24" ) ;

		for( int i = 0 ; i < 24 ; i++ )
			System.out.print( random.nextInt( 24 ) + " " ) ;
		System.out.println() ;

		printHeadings( "longs" ) ;

		pluses = 0 ;
		minuses = 0 ;
		for( int i = 0 ; i < 24 ; i++ )
		{
			long randomLong = random.nextLong() ;
			System.out.print( randomLong + " " ) ;
			if( randomLong >= 0 )
				pluses++ ;
			else
				minuses++ ;
		}
		System.out.println( "\n\n" + "+ / -  : " + pluses + " / " + minuses ) ;

		printHeadings( "doubles" ) ;
		for( int i = 0 ; i < 24 ; i++ )
			System.out.print( random.nextDouble() + " " ) ;
		System.out.println() ;

		printHeadings( "floats" ) ;
		for( int i = 0 ; i < 24 ; i++ )
			System.out.print( random.nextFloat() + " " ) ;
		System.out.println() ;

		printHeadings( "guassians" ) ;
		for( int i = 0 ; i < 24 ; i++ )
			System.out.print( random.nextGaussian() + " " ) ;
		System.out.println() ;
	}

	private static boolean serializationTest( Random random )
	{
		printHeadings( "serialization" ) ;

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
        }
        catch( IOException e )
        {
	        success = true ; // test *should* fail on write
        }
        catch( ClassNotFoundException e )
        {
	        e.printStackTrace() ;
        }

		if( success )
			System.out.println( "Success - prevented serialization." ) ;
		System.out.println() ;

		return success ;
	}

	private static void printBytes( byte[] bytes )
	{
		for( byte bite : bytes )
			System.out.print( bite + " " ) ;
		System.out.println() ;
	}

	private static final void printHeadings( String heading )
	{
		System.out.println( "\n\n\n\t===\t" + heading.toUpperCase( Locale.getDefault() ) + "\t===\n\n\n" ) ;
	}
}
