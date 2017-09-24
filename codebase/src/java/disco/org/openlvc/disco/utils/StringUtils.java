/*
 *   Copyright 2016 Open LVC Project.
 *
 *   This file is part of Open LVC Disco.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package org.openlvc.disco.utils;

public class StringUtils
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
	////////////////////////////////////////////////////////////////////////////////////////////
	/// Convert, Trim, Manipulate Methods   ////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////	

	/**
	 * Converts the given string value to a boolean.
	 * <p/>
	 * Valid values are on/off, true/false, yes/no, enabled/disabled
	 */
	public static boolean stringToBoolean( String value )
	{
		value = value.trim();
		if( value.equalsIgnoreCase("true")   ||
			value.equalsIgnoreCase("on")     ||
			value.equalsIgnoreCase("yes")    ||
			value.equalsIgnoreCase("enabled") )
			return true;
		else if( value.equalsIgnoreCase("false") ||
			value.equalsIgnoreCase("off")        ||
			value.equalsIgnoreCase("no")         ||
			value.equalsIgnoreCase("disabled") )
			return false;
		else
			throw new IllegalArgumentException( value+" is not a valid binary constant (on/off, true/false, yes/no, enabled/disabled)" );
	}

	/**
	 * Returns the given string truncated to a maximum of the givne number of characters
	 */
	public static String max( String string, int characters )
	{
		return characters > string.length() ? string : string.substring(0,characters);
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Size Methods   /////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////	
	/**
	 * Returns a human readable character string for the given number of bytes. Can specify
	 * whether this should display in SI units (decimal, 1000x) or binary (1024x).
	 * 
	 * Awesome code taken from here excellent SO answer here:
	 * http://stackoverflow.com/questions/3758606/how-to-convert-byte-size-into-human-readable-format-in-java
	 * 
	 * <pre>
	 *                               SI     BINARY
	 * 
	 *                    0:        0 B        0 B
	 *                   27:       27 B       27 B
	 *                  999:      999 B      999 B
	 *                 1000:     1.0 kB     1000 B
	 *                 1023:     1.0 kB     1023 B
	 *                 1024:     1.0 kB    1.0 KiB
	 *                 1728:     1.7 kB    1.7 KiB
	 *               110592:   110.6 kB  108.0 KiB
	 *              7077888:     7.1 MB    6.8 MiB
	 *            452984832:   453.0 MB  432.0 MiB
	 *          28991029248:    29.0 GB   27.0 GiB
	 *        1855425871872:     1.9 TB    1.7 TiB
	 *  9223372036854775807:     9.2 EB    8.0 EiB   (Long.MAX_VALUE)
	 * </pre>
	 * 
	 * @param bytes Number of bytes to convert
	 * @param si Whether to use SI format or binary
	 * @return Formatted string
	 */
	public static String humanReadableSize( long bytes, boolean si )
	{
	    int unit = si ? 1000 : 1024;
	    if( bytes < unit ) return bytes + " B";
	    int exp = (int)(Math.log(bytes) / Math.log(unit));
	    String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp-1) + (si ? "" : "i");
	    return String.format( "%.1f %sB", bytes / Math.pow(unit,exp), pre );
	}

	/**
	 * Calls {@link #humanReadableSize(long, boolean)} with <code>true</code> to return in SI units.
	 * @see #humanReadableSize(long, boolean)
	 */
	public static String humanReadableSize( long bytes )
	{
		return humanReadableSize( bytes, true );
	}

	/**
	 * Converts the given human readable string representing the size into its value in bytes.
	 */
	public static long bytesFromString( String humanReadable )
	{
		humanReadable = humanReadable.toLowerCase();
		String sizePortion = null;
		ByteUnit byteUnit = null;

		if( humanReadable.endsWith("mb") )
		{
			sizePortion = humanReadable.substring(0,humanReadable.length()-2).trim();
			byteUnit = ByteUnit.MEGABYTES;
		}
		else if( humanReadable.endsWith("kb") )
		{
			sizePortion = humanReadable.substring(0,humanReadable.length()-2).trim();
			byteUnit = ByteUnit.KILOBYTES;
		}
		else if( humanReadable.endsWith("gb") )
		{
			sizePortion = humanReadable.substring(0,humanReadable.length()-2).trim();
			byteUnit = ByteUnit.GIGABYTES;
		}
		else if( humanReadable.endsWith("tb") )
		{
			sizePortion = humanReadable.substring(0,humanReadable.length()-2).trim();
			byteUnit = ByteUnit.TERABYTES;
		}
		else if( humanReadable.endsWith("b") )
		{
			sizePortion = humanReadable.substring(0,humanReadable.length()-1).trim();
			byteUnit = ByteUnit.BYTES;
		}
		else
		{
			throw new IllegalArgumentException( "Value doesn't have size suffix: "+humanReadable );
		}

		if( sizePortion.contains(".") )
			return byteUnit.toBytes( Double.valueOf(sizePortion) );
		else
			return byteUnit.toBytes( Integer.valueOf(sizePortion) );
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////
	/// Char Methods   /////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////	
	public static boolean isAsciiPrintable( char ch )
	{
		return ch >= 32 && ch < 127;
	}

	public static boolean isAsciiPrintable( byte b )
	{
		// http://www.asciitable.com/
		return b >= 0x20 && b < 0x7F;
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Wireshark Formatting   /////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////	
	/**
	 * Convert the given byte[] into a "wireshark formatted" hex/ascii grid. Example output format
	 * for single row:
	 * <p/>
	 * <code>00 11 22 33 44 55 66 77  88 99 AA BB CC DD EE FF  ........ ........</code>
	 */
	public static String formatAsWireshark( byte[] bytes )
	{
		StringBuilder builder = new StringBuilder();
		// write the first one outside the loop otherwise the modulo check
		// will pass on 0 and we'll have a leading "  "
		builder.append( String.format(" %02X",bytes[0]) );
		
		boolean newline = true;
		for( int i = 1; i < bytes.length; i++ )
		{
			if( i % 8 == 0 )
			{
				newline = !newline;
				if( newline )
					appendRowSummary( builder, bytes, i, 16 );
				else
					builder.append( "  " );
			}
			
			builder.append( String.format(" %02X",bytes[i]) );
		}
		
		appendRowSummary( builder, bytes, bytes.length, bytes.length % 16 );
		return builder.toString();
	}
	
	/**
	 * Append a row summary that shows the bytes printed as ASCII characters.
	 * Non-printable characters are represented with a "."
	 * 
	 * @param builder The builder to append to
	 * @param bytes The bytes to pull from
	 * @param limit Read the last 16 bytes from this value backwards (non-inclusive)
	 */
	private static void appendRowSummary( StringBuilder builder, byte[] bytes, int limit, int count )
	{
		// write some padding in first
		if( count != 16 )
		{
    		int padding = 15 - count;
    		for( int i = 0; i < padding; i++ )
    		{
    			if( i % 8 == 0 )
    				builder.append( "  " );
    
    			builder.append( "   " );
    		}
    		builder.append( " " );
		}

		// write the summary
		for( int i = (limit-count); i < limit; i++ )
		{
			if( i % 8 == 0 )
				builder.append( " " );
			
			if( StringUtils.isAsciiPrintable(bytes[i]) )
				builder.append( (char)bytes[i] );
			else
				builder.append( "." );
		}
		
		builder.append( "\n" );
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Special Value Formatting   /////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public static String toHex( short value )
	{
		return String.format( "0x%x", value );
	}
	
	public static String toHex( int value )
	{
		return String.format( "0x%x", value );
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////
	/// General Formatting   ///////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////	
	/**
	 * Return a string that is of the given `width` where the contained `text` is centered with
	 * empty padding left and right.
	 */
	public static String center( String text, int width )
	{
		int textLength = text.length();
		if( textLength > width )
			throw new IllegalArgumentException( "Cannot center text. Given text ["+text+"] wider than given width ["+width+"]" );
		
		if( textLength == width )
			return text;

		int paddingTotal = width - textLength;
		if( paddingTotal == 1 )
			return text+" ";

		int paddingLeft = Math.floorDiv( paddingTotal, 2 );
		int paddingRight = paddingTotal - paddingLeft;
		
		String formatString = "%"+paddingLeft+"s%s%"+paddingRight+"s";
		return String.format(formatString," ",text," ");
	}
}
