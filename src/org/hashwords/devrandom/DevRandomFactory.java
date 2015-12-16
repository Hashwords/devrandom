/**
 * Copyright (c) 2015, Sam Hart / Hashwords.
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

import java.lang.reflect.Constructor ;

/**
 * Factory pattern for creating and using {@link DevRandom} objects.
 */
public class DevRandomFactory
{
	/**
	 * The string "DragonFly" for selecting supported operating systems.<br>
	 * <p><span style="color:red;">WARNING :</span> Not currently tested.</p>
	 * @see #SUPPORTED_OSES
	 * @see <a href="http://www.dragonflybsd.org/cgi/web-man?command=random&section=4">
	 * random(4)</a> for <a href="http://www.dragonflybsd.org/">DragonFlyBSD</a>.<br>
	 */
	public static final String DRAGONFLY	= "DragonFly" ;

	/**
	 * The string "FreeBSD" for selecting supported operating systems.
	 * @see #SUPPORTED_OSES
	 * @see <a href="http://www.freebsd.org/cgi/man.cgi?query=random&apropos=0&sektion=4">
	 * random(4)</a> for <a href="http://www.freebsd.org/">FreeBSD</a>.<br>
	 */
	public static final String FREEBSD		= "FreeBSD" ;

	/**
	 * The string "Linux" for selecting supported operating systems.
	 * @see #SUPPORTED_OSES
	 * @see <a href="http://man7.org/linux/man-pages/man4/random.4.html">
	 * random(4)</a> for <a href="http://kernel.org/">Linux</a>.<br>
	 */
	public static final String LINUX		= "Linux" ;

	/**
	 * The string "Mac OS X" for selecting supported operating systems.
	 * @see #SUPPORTED_OSES
	 * @see <a href="https://developer.apple.com/library/mac/documentation/Darwin/Reference/ManPages/man4/random.4.html">
	 * random(4)</a> for <a href="http://www.apple.com/osx/">Darwin / Mac OS X</a>.<br>
	 */
	public static final String MACOSX		= "Mac OS X" ;

	/**
	 * The string "NetBSD" for selecting supported operating systems.<br>
	 * <p><span style="color:red;">WARNING :</span> Not currently tested.</p>
	 * @see #SUPPORTED_OSES
	 * @see <a href="http://man.netbsd.org/HEAD/usr/share/man/html4/random.html">
	 * random(4)</a> for <a href="http://www.netbsd.org/">NetBSD</a>.<br>
	 */
	public static final String NETBSD		= "NetBSD" ;

	/**
	 * The string "OpenBSD" for selecting supported operating systems.
	 * @see #SUPPORTED_OSES
	 * @see <a href="http://www.openbsd.org/cgi-bin/man.cgi/OpenBSD-current/man4/arandom.4">
	 * random(4)</a> for <a href="http://www.openbsd.org/">OpenBSD</a>.<br>
	 */
	public static final String OPENBSD		= "OpenBSD" ;
	
	/**
	 * Current list of supported operating systems.
	 * List should match entry returned by system property "os.name".
	 * @see java.lang.System#getProperty(String)
	 */
	public static final String[] SUPPORTED_OSES = { MACOSX , LINUX , FREEBSD , OPENBSD , NETBSD , DRAGONFLY } ;

	private DevRandomFactory(){}

	/**
	 * Check wether or not a given operating system is currently supported.
	 * @param OS	name of the operating system.
	 * @return boolean indicating wether or not a given operating system is currently supported.
	 */
	private static boolean supported( String OS )
	{
		boolean found = false ;
		for( String currentOS : SUPPORTED_OSES )
		{
			if( currentOS.equals( OS ) )
			{
				found = true ;
				break ;
			}
		}
		return found ;
	}

	/**
	 * Return the appropriate {@link DevRandom} class for the current operating system.
	 * @return Appropriate class that extends {@link DevRandom} for the current operating system.
	 * @see java.lang.System#getProperty(String)
	 */
	private static Class<? extends DevRandom> getOperatingSystemClass()
	{
		Class<? extends DevRandom> klass = null ;

		String OS = System.getProperty( "os.name" ) ;

		if( supported( OS ) )
		{
    		if( OPENBSD.equals( OS ) )
    			klass = OpenBSDRandom.class ;
    		else if( FREEBSD.equals( OS ) )
    			klass = FreeBSDRandom.class ;
    		else
    			klass = DevRandom.class ;
		}
		else
		{
			throw new IllegalArgumentException( OS + " not currently supported." ) ;
		}

		return klass ;
	}

	/**
	 * Create and return a {@link DevRandom} object for this operating system with the default configuration.
	 * @return {@link DevRandom} object.
	 * @see DevRandom#DevRandom()
	 * @see <a href="https://en.wikipedia.org/wiki//dev/random">/dev/random</a> and random(4) in your operating system's man pages.
	 */
	public static DevRandom getInstance()
	{
		DevRandom random = null ;

		Class<? extends DevRandom> klass = getOperatingSystemClass() ;

		try
		{
			Constructor<?> constructor = klass.getConstructor() ;
			random = ( DevRandom )constructor.newInstance() ;
		}
		catch( Exception e )
		{
			throw new IllegalArgumentException( e ) ;
		}

		return random ;
	}

	/**
	 * Create and return a {@link DevRandom} object for this operating system
	 * using the preferred blocking or non-blocking device.
	 * @param blocking	boolean that selects between blocking and non-blocking devices.
	 * @return {@link DevRandom} object.
	 * @see DevRandom#DevRandom(boolean)
	 * @see DevRandom#BLOCKING
	 * @see DevRandom#NON_BLOCKING
	 * @see <a href="https://en.wikipedia.org/wiki//dev/random">/dev/random</a> and random(4) in your operating system's man pages.
	 */
	public static DevRandom getInstance( boolean blocking )
	{
		DevRandom random = null ;

		Class<? extends DevRandom> klass = getOperatingSystemClass() ;

		try
		{
			Constructor<?> constructor = klass.getConstructor( boolean.class ) ;
			random = ( DevRandom )constructor.newInstance( blocking ) ;
		}
		catch( Exception e )
		{
			throw new IllegalArgumentException( e ) ;
		}

		return random ;
	}

	/**
	 * Create and return a {@link DevRandom} object for this operating system using the given device.
	 * @param device	path to device, accepts only known devices or throws IllegalArgumentException
	 * @return {@link DevRandom} object.
	 * @see DevRandom#DevRandom(String)
	 * @see <a href="https://en.wikipedia.org/wiki//dev/random">/dev/random</a> and random(4) in your operating system's man pages.
	 */
	public static DevRandom getInstance( String device )
	{
		DevRandom random = null ;

		Class<? extends DevRandom> klass = getOperatingSystemClass() ;

		try
		{
			Constructor<?> constructor = klass.getConstructor( String.class ) ;
			random = ( DevRandom )constructor.newInstance( device ) ;
		}
		catch( Exception e )
		{
			throw new IllegalArgumentException( e ) ;
		}

		return random ;
	}
}
