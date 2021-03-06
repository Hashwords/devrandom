/**
 * Copyright (c) 2015, Sam Hart
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
 * 
 * 3. Neither the name of the copyright holder nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, 
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY 
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES 
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; 
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, 
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.hashwords.devrandom ;

import java.io.File ;
import java.io.FileInputStream ;
import java.io.FileNotFoundException ;
import java.io.FileOutputStream ;
import java.io.IOException ;
import java.nio.ByteBuffer ;
import java.nio.channels.FileChannel ;
import java.util.Random ;

/**
 * <p>Pseudo random number generator using an operating-system-based device.</p>
 * Bytes are read directly from the device and returned as a byte[]<br>
 * Other types ( ints, longs, booleans ) are derived from the returned bytes.<br>
 * Floating point methods are implemented by the super-class java.util.Random via this class' {@link #next(int)}.<br>
 * Seed values received via {@link #setSeed(long)} are written to the device, if possible, updating it's entropy pool.<br>
 * 
 * <p><span style="color:red;">WARNING :</span> for performance, the random device is held open until the object's 
 * {@link #finalize()} method is called by garbage collection.
 * As this method is protected, the best practice when a {@link DevRandom} object is no longer needed, 
 * is to null the {@link DevRandom} object and call garbage collection with {@link java.lang.System#gc()}.</p>
 * 
 * <p><span style="color:red;">WARNING :</span> Unlike it's superclass, and because of it's operating system based dependency,
 * {@link DevRandom} objects are not {@link java.io.Serializable}.</p>
 *
 * @see <a href="https://en.wikipedia.org/wiki//dev/random">/dev/random</a> and random(4) in your operating system's man pages.
 */
@SuppressWarnings( "serial" )
public class DevRandom extends Random
{
	/**
	 * Use with constructor to select a device that may block in order to provide a guaranteed level of entropy.
	 * @see <a href="https://en.wikipedia.org/wiki//dev/random">/dev/random</a> and random(4) in your operating system's man pages.
	 */
	public static final boolean BLOCKING = true ;

	/**
	 * Use with constructor to select a device that will not block regardless of the level of entropy available.
	 * @see <a href="https://en.wikipedia.org/wiki//dev/random">/dev/random</a> and random(4) in your operating system's man pages.
	 */
	public static final boolean NON_BLOCKING = false ;

	/**
	 * The String "/dev/random" for use with constructor.
	 * @see #DevRandom(String)
	 * @see <a href="https://en.wikipedia.org/wiki//dev/random">/dev/random</a> and random(4) in your operating system's man pages.
	 */
	public final static String DEV_RANDOM = "/dev/random" ;

	/**
	 * The String "/dev/urandom" for use with constructor.
	 * @see #DevRandom(String)
	 * @see <a href="https://en.wikipedia.org/wiki//dev/random">/dev/random</a> and random(4) in your operating system's man pages.
	 */
	public final static String DEV_URANDOM = "/dev/urandom" ;

	/**
	 * String[] containing all devices known to this system.<br>
	 * Calls to {@link #DevRandom(String)} will consult this array and throw IllegalArgumentException if no match is found.
	 * Override, if appropriate, in subclasses.
	 * @see #DevRandom(String)
	 */
	protected static String[] DEVICE_NAMES = { DEV_RANDOM , DEV_URANDOM } ;

	/**
	 * Preferred blocking device.<br>
	 * Override, if appropriate, in subclasses.
	 */
	protected static String PREFERRED_BLOCKING = DEV_RANDOM ;

	/**
	 * Preferred non-blocking device.<br>
	 * Override, if appropriate, in subclasses.
	 */
	protected static String PREFERRED_NON_BLOCKING = DEV_URANDOM ;

	private transient FileInputStream fis ;
	private transient FileChannel iChannel ;
	private transient FileOutputStream fos ;
	private transient FileChannel oChannel ;

	/**
	 * <p>Construct a DevRandom object using an operating-system-based device.</p>
	 * Chooses a device that may block.<br>
	 * @see #DevRandom(boolean)
	 * @see #PREFERRED_BLOCKING
	 * @see <a href="https://en.wikipedia.org/wiki//dev/random">/dev/random</a> and random(4) in your operating system's man pages.
	 * @throws FileNotFoundException	if the PRNG device cannot be found.
	 */
	public DevRandom() throws FileNotFoundException
	{
		this( true ) ;
	}

	/**
	 * <p>Construct a DevRandom object using an operating-system-based device.</p>
	 * Some devices may block while waiting for entropy to reach a certain threshold.<br>
	 * @param blocking	boolean that selects between blocking and non-blocking devices.
	 * @throws FileNotFoundException	if the PRNG device cannot be found.
	 * @see #DevRandom(String)
	 * @see #BLOCKING
	 * @see #NON_BLOCKING
	 * @see #PREFERRED_BLOCKING
	 * @see #PREFERRED_NON_BLOCKING
	 * @see <a href="https://en.wikipedia.org/wiki//dev/random">/dev/random</a> and random(4) in your operating system's man pages.
	 */
	public DevRandom( boolean blocking ) throws FileNotFoundException
	{
		this( blocking ? PREFERRED_BLOCKING : PREFERRED_NON_BLOCKING ) ;
	}

	/**
	 * <p>Construct a DevRandom object using an operating-system-based device.</p>
	 * @param deviceName	accepts only device names known to {@link DevRandom} or throws IllegalArgumentException
	 * @throws FileNotFoundException	if the PRNG device cannot be found.
	 * @see #DEVICE_NAMES
	 * @see <a href="https://en.wikipedia.org/wiki//dev/random">/dev/random</a> and random(4) in your operating system's man pages.
	 */
	public DevRandom( String deviceName ) throws FileNotFoundException
	{
		boolean found = false ;
		for( String current : DEVICE_NAMES )
		{
			if( current.equals( deviceName ) )
			{
				found = true ;
				break ;
			}
		}

		if( !found )
			throw new IllegalArgumentException( deviceName + " is not a known random device." ) ;

		File device = new File( deviceName ) ;
		if( !device.exists() || !device.canRead() || !device.isAbsolute() )
			throw new FileNotFoundException( "Couldn't get " + deviceName + " device." ) ;

		fis = new FileInputStream( device ) ;
		iChannel = fis.getChannel() ;

		if( device.canWrite() )
		{
			fos = new FileOutputStream( device ) ;
			oChannel = fos.getChannel() ;
		}
	}

	@Override
    public synchronized void nextBytes( byte[] bytes )
	{
		ByteBuffer buffer = ByteBuffer.allocate( bytes.length ) ;
		try
		{
			iChannel.read( buffer ) ;
			System.arraycopy( buffer.array() , 0 , bytes , 0 , bytes.length ) ;
		}
		catch( IOException e )
		{
			e.printStackTrace() ;
		}
	}

	@Override
	protected void finalize()
	{
		System.out.println( "Closing random device ..." ) ;
		try
		{
			if( iChannel != null )
				iChannel.close() ;
			if( fis != null )
				fis.close() ;
		}
		catch( IOException e )
		{
			e.printStackTrace() ;
		}

		try
		{
			if( oChannel != null )
        		oChannel.close() ;
        	if( fos != null )
        		fos.close() ;
		}
		catch( IOException e )
		{
			e.printStackTrace() ;
		}
		System.out.println( "Random device closed." ) ;
	}

	@Override
    public boolean nextBoolean()
	{
		boolean randomBoolean = true ; // guaranteed random 50% of the time.

		byte[] bite = new byte[ 1 ] ;
		nextBytes( bite ) ;
		randomBoolean = ( bite[ 0 ] & 1 ) == 1 ;
		return randomBoolean ;
	}

	@Override
    public int nextInt()
	{
		byte[] bytes = new byte[ 4 ] ;
		nextBytes( bytes ) ;
		int randomInt = ( 128 + bytes[ 0 ] ) << 24 ;
		randomInt |= ( 128 + bytes[ 1 ] ) << 16 ;
		randomInt |= ( 128 + bytes[ 2 ] ) << 8 ;
		randomInt |= ( 128 + bytes[ 3 ] ) ;
		return randomInt ;
	}

	@Override
    public int nextInt( int n )
	{
		return Math.abs( nextInt() % n ) ;
	}

	@Override
	public long nextLong()
	{
		byte[] bytes = new byte[ 8 ] ;
		nextBytes( bytes ) ;
		long randomLong = (( long )( 128 + bytes[ 0 ] )) << 56 ;
		randomLong |= (( long )( 128 + bytes[ 1 ] )) << 48 ;
		randomLong |= (( long )( 128 + bytes[ 2 ] )) << 40 ;
		randomLong |= (( long )( 128 + bytes[ 3 ] )) << 32 ;
		randomLong |= (( long )( 128 + bytes[ 4 ] )) << 24 ;
		randomLong |= (( long )( 128 + bytes[ 5 ] )) << 16 ;
		randomLong |= (( long )( 128 + bytes[ 6 ] )) << 8 ;
		randomLong |= (( long )( 128 + bytes[ 7 ] )) ;
		return randomLong ;
	}

	@Override
	protected synchronized int next( int bits )
	{
		int mask = ( 1 << bits ) - 1 ;
		return nextInt() & mask ;
	}

	@Override
	public void setSeed( long seed )
	{
		if( oChannel == null )
			return ;

		ByteBuffer buffer = ByteBuffer.allocate( 8 ) ;

		buffer.put( ( byte )( seed & 0xff ) ) ;
		buffer.put( ( byte )(( seed >> 8 ) & 0xff ) ) ;
		buffer.put( ( byte )(( seed >> 16 ) & 0xff ) ) ;
		buffer.put( ( byte )(( seed >> 24 ) & 0xff ) ) ;
		buffer.put( ( byte )(( seed >> 32 ) & 0xff ) ) ;
		buffer.put( ( byte )(( seed >> 40 ) & 0xff ) ) ;
		buffer.put( ( byte )(( seed >> 48 ) & 0xff ) ) ;
		buffer.put( ( byte )(( seed >> 56 ) & 0xff ) ) ;
		buffer.flip() ;

		try
        {
			oChannel.write( buffer ) ;
			oChannel.force( false ) ;
        }
        catch( IOException e )
        {
	        e.printStackTrace() ;
        }
	}

	/**
	 * <span style="color:red;">WARNING :</span>
	 * Due to the operating-system-based nature of special devices, serialization is prohibited.<br>
	 * Attempts to serialize these objects will result in a {@link java.io.NotSerializableException}.
	 * @param in
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private void readObject( java.io.ObjectInputStream in ) throws IOException, ClassNotFoundException
	{
		throw new java.io.NotSerializableException( this.getClass().getName() ) ;
	}

	/**
	 * <span style="color:red;">WARNING :</span>
	 * Due to the operating-system-based nature of special devices, serialization is prohibited.<br>
	 * Attempts to serialize these objects will result in a {@link java.io.NotSerializableException}.
	 * @param oos
	 * @throws IOException
	 */
	private void writeObject( java.io.ObjectOutputStream oos ) throws IOException
	{
		throw new java.io.NotSerializableException( this.getClass().getName() ) ;
	}
}
