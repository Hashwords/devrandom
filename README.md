
# DevRandom

## What is DevRandom ?

DevRandom is a Java-based Psuedo-Random-Number-Generator (PRNG) that extends
java.util.Random and uses an operating-system-based PRNG device such as
/dev/random or /dev/urandom.


## Why DevRandom ?

DevRandom was originally developed for use with Android applications after
confirmed reports of a vulnerability in BitCoin applications caused by a
weakness in Android's Java Cryptography Architecture (JCA).

As Android is based on Linux it contains a special file that provides high
quality psuedo-random-numbers, DevRandom provides a convenient means by which
use these devices either as a stand-alone PRNG or to seed other PRNGs.


## What is the recommended practice for using DevRandom ?

It is recommended that instances of DevRandom are initialised via the factory
methods in DevRandomFactory :

`import java.util.Random ;`
`import org.hashwords.devrandom.DevRandomFactory ;`

`Random random = DevRandomFactory.getInstance() ;`

For performance reasons, DevRandom will hold open the operating-system device,
when you are finished using the device it can be closed again by setting the
object pointer to null and calling garbage collection :


`random = null ;	// set the object pointer to null`
`System.gc() ; 	// call garbage collection`


For performance reasons you may not want to call garbage collection immediately
after null'ing the object.

DevRandom differs from other instances of java.util.Random in that, due to the
cross-platform nature of Java and that DevRandom uses operating-system-based
devices, it is not serializable and attempts to serialize DevRandom objects
will throw a java.io.NotSerializableException


## What operating systems does DevRandom support ?

Operating systems supported by DevRandom include :

Android / Linux
Darwin / Mac OS X
OpenBSD
FreeBSD		(not yet tested)
DragonFlyBSD    (not yet tested)

Using DevRandomFactory will return the appropriate instance for supported OSes.

If your choice of operating system is not currently supported there are a
number of options available to you :

If both /dev/random and /dev/urandom devices are available, you can try
instantiating a DevRandom object directly and checking for error messages. If
this strategy succeeds, please inform the DevRandom maintainers, the inclusion
of the output of `System.getProperty( "os.name" )` would be greatly appreciated.

If only one of /dev/random or /dev/urandom or some other PRNG devices are
available, Please notify the DevRandom maintainers, again the inclusion of the
output of `System.getProperty( "os.name" )` would be greatly appreciated.
Alternatively you can extend DevRandom to include or exclude devices as is done
for FreeBSD or OpenBSD; patches of these changes would be appreciated by the
DevRandom maintainers under a compatible license.


## What versions of Java does DevRandom support ?

Any Java Runtime Edition (JRE) greater or equal to 1.5 (Java 5)

For Android, if required to choose a target then currently JRE 1.7 appears to
be the correct one. Please notify the DevRandom maintainers if this is not the
case.


## Who is the current DevRandom maintainer ?

DevRandom was written and is currently maintained by Sam Hart for hashwords.org

All emails regarding DevRandom should be directed to

`devrandom @ hashwords.org`


### Legal

Java is a registered trademark of Oracle and/or its affiliates.

Linux is the registered trademark of Linus Torvalds in the U.S. and other
countries.

Android is a trademark of Google Inc.

FreeBSD is a registered trademark of The FreeBSD Foundation.

Apple and Mac OS, are trademarks of Apple Computer, Inc., registered in the
United States and other countries.

