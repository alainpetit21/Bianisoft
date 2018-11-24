/* This file is part of the Java Game Music Emu library.
 *
 *  The Java Game Music Emu library is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 2.1 of the License, or
 *  (at your option) any later version.
 *
 *  The Java Game Music Emu library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with the Java Game Music Emu library.
 *  If not, see <http://www.gnu.org/licenses/>.
 *----------------------------------------------------------------------
 * Copyright (C) 2003-2007 Shay Green - http://www.slack.net/~ant/
 *
 * ??/??/2007			1.0
 *
 *-----------------------------------------------------------------------
 */
package blargg.javagme;

import java.io.*;
import java.util.zip.*;
import java.net.*;

// Helpers for loading/decompressing data from various sources
class DataReader
{
	// Opens InputStream to file stored in various ways
	static InputStream openHttp( String path ) throws Exception
	{
		return new URL( path ).openConnection().getInputStream();
	}

	static InputStream openFile( String path ) throws Exception
	{
		return new FileInputStream( new File( path ) );
	}
	
	static InputStream openGZIP( InputStream in ) throws Exception
	{
		return new GZIPInputStream( in );
	}
	
	// "Resizes" array to new size and preserves elements from in
	static byte [] resize( byte [] in, int size )
	{
		byte [] out = new byte [size];
		if ( size > in.length )
			size = in.length;
		System.arraycopy( in, 0, out, 0, size );
		return out;
	}
	
	// Loads entire stream into byte array, then closes stream
	static byte [] loadData( InputStream in ) throws Exception
	{
		byte [] data = new byte [256 * 1024];
		int size = 0;
		int count;
		while ( (count = in.read( data, size, data.length - size )) != -1 )
		{
			size += count;
			if ( size >= data.length )
				data = resize( data, data.length * 2 );
		}
		in.close();
		
		if ( data.length - size > data.length / 4 )
			data = resize( data, size );
		
		return data;
	}
	
	// Loads stream into ByteArrayInputStream
	static ByteArrayInputStream cacheStream( InputStream in ) throws Exception
	{
		return new ByteArrayInputStream( loadData( in ) );
	}
	
	// Finds file named 'path' inside zip file, or returns null if not found.
	// You should use a BufferedInputStream or cacheStream() for input.
	static InputStream openZip( InputStream in, String path ) throws Exception
	{
		ZipInputStream zis = new ZipInputStream( in );
		for ( ZipEntry entry; (entry = zis.getNextEntry()) != null; )
		{
			if ( path.equals( entry.getName() ) )
				return zis;
		}
		return null;
	}
}
