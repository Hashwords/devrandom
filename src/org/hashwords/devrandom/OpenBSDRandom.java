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

import java.io.FileNotFoundException ;

/**
 * <p>Pseudo random number generator using an <a href="http://www.openbsd.org/">OpenBSD</a>-based device.</p>
 * @see <a href="http://www.openbsd.org/cgi-bin/man.cgi/OpenBSD-current/man4/arandom.4">random(4)</a>
 */
@SuppressWarnings( "serial" )
public final class OpenBSDRandom extends DevRandom
{
	/**
	 * The String "/dev/arandom" for use with constructor, used for legacy reasons.
	 * @see DevRandom#DevRandom(String)
	 * @see #random(4) in your operating system's man pages.
	 */
	public final static String DEV_ARANDOM = "/dev/arandom" ;

	/**
	 * The String "/dev/srandom" for use with constructor, used for legacy reasons.
	 * @see DevRandom#DevRandom(String)
	 * @see random(4) in your operating system's man pages.
	 */
	public final static String DEV_SRANDOM = "/dev/srandom" ;

	static
	{
		deviceNames = new String[]{ DEV_RANDOM , DEV_ARANDOM , DEV_SRANDOM , DEV_URANDOM } ;
		PREFERRED_NON_BLOCKING = DEV_RANDOM ;
	}

	public OpenBSDRandom() throws FileNotFoundException
    {
	    super() ;
    }

	public OpenBSDRandom( boolean blocking ) throws FileNotFoundException
    {
	    super( blocking ) ;
    }

	public OpenBSDRandom( String deviceName ) throws FileNotFoundException
    {
	    super( deviceName ) ;
    }

}
