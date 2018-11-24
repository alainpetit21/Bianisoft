/*
 * 11/19/04  1.0 moved to LGPL.
 *
 * 11/17/04	 Uncomplete frames discarded. E.B, javalayer@javazoom.net
 *
 * 12/05/03	 ID3v2 tag returned. E.B, javalayer@javazoom.net
 *
 * 12/12/99	 Based on Ibitstream. Exceptions thrown on errors,
 *			 Temporary removed seek functionality. mdm@techie.com
 *
 * 02/12/99 : Java Conversion by E.B , javalayer@javazoom.net
 *
 * 04/14/97 : Added function prototypes for new syncing and seeking
 * mechanisms. Also made this file portable. Changes made by Jeff Tsay
 *
 *  @(#) ibitstream.h 1.5, last edit: 6/15/94 16:55:34
 *  @(#) Copyright (C) 1993, 1994 Tobias Bading (bading@cs.tu-berlin.de)
 *  @(#) Berlin University of Technology
 *-----------------------------------------------------------------------
 *   This program is free software; you can redistribute it and/or modify
 *   it under the terms of the GNU Library General Public License as published
 *   by the Free Software Foundation; either version 2 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Library General Public License for more details.
 *
 *   You should have received a copy of the GNU Library General Public
 *   License along with this program; if not, write to the Free Software
 *   Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 *----------------------------------------------------------------------
 */

package javazoom.jl.decoder;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.logging.Level;
import java.util.logging.Logger;


public final class Bitstream implements BitstreamErrors{
	private static final int	BUFFER_INT_SIZE= 433;

	static byte		INITIAL_SYNC= 0;
	static byte		STRICT_SYNC= 1;

	private final int		BITMASK[]= {0,	// dummy
		0x00000001, 0x00000003, 0x00000007, 0x0000000F,
		0x0000001F, 0x0000003F, 0x0000007F, 0x000000FF,
		0x000001FF, 0x000003FF, 0x000007FF, 0x00000FFF,
		0x00001FFF, 0x00003FFF, 0x00007FFF, 0x0000FFFF,
		0x0001FFFF
	};


	private final int[]		m_frameBuffer		= new int[BUFFER_INT_SIZE];
	private byte[]			m_frameBufferBytes	= new byte[BUFFER_INT_SIZE*4];
	private int				m_nFrameSize;
	private int				m_nWordPointer;
	private int				m_nBitIdx;
	private int				m_nSyncWord;
	private int				m_nHeaderStart = 0;
	private long			m_nFramesStart = 0;

	private boolean			m_isSingleChannelMode;
//	private int 			current_frame_number;
//	private int				last_frame_number;


//	private PushbackInputStream	m_source;
	private RandomAccessFile	m_source= null;

	private final Header	m_header		= new Header();
	private final byte		m_syncBuffer[]	= new byte[4];
	private Crc16[]			m_crc			= new Crc16[1];
	private byte[]			m_rawID3V2		= null;
	private boolean			m_isFirstFrame	= true;


	public Bitstream(String p_stFilename){
		if(p_stFilename == null)
			throw new NullPointerException("p_stFilename");

		try {
			m_source= new RandomAccessFile(p_stFilename, "r");
			loadID3v2();
			m_nFramesStart= m_source.getFilePointer();
		}catch (Exception ex){
			System.out.print(ex);
		}

		m_isFirstFrame= true;
		closeFrame();
	}

	public int getHeaderPos(){
		return m_nHeaderStart;
	}

	private void loadID3v2() throws IOException{
		int size= readID3v2Header();

		try{
			if(size > 0){
				m_rawID3V2= new byte[size];

				m_source.read(m_rawID3V2, 0, m_rawID3V2.length);
			}
		}catch (IOException e){
		}
	}

	private int readID3v2Header() throws IOException{
		byte[] id3header= new byte[4];
		int size= -10;

		m_source.read(id3header, 0, 3);

		// Look for ID3v2
		if((id3header[0]=='I') && (id3header[1]=='D') && (id3header[2]=='3')){
			m_source.read(id3header, 0, 3);

			int majorVersion= id3header[0];
			int revision= id3header[1];

			m_source.read(id3header, 0, 4);
			size= (int)(id3header[0] << 21) + (id3header[1] << 14) + (id3header[2] << 7) + (id3header[3]);
		}

		return(size+10);
	}

	public void close() throws BitstreamException{
		try{
			m_source.close();
		}catch (IOException ex){
			throw newBitstreamException(STREAM_ERROR, ex);
		}
	}

	public Header readFrame() throws BitstreamException{
		Header result= null;

		try{
			result= readNextFrame();

			if(m_isFirstFrame == true){
				result.parseVBR(m_frameBufferBytes);

				m_isFirstFrame= false;
			}
		}catch(BitstreamException ex){
			if((ex.getErrorCode()==INVALIDFRAME)){
				try{
					closeFrame();
					result= readNextFrame();
				}catch (BitstreamException e){
					if((e.getErrorCode()!=STREAM_EOF)){
						throw newBitstreamException(e.getErrorCode(), e);
					}
				}
			}else if ((ex.getErrorCode()!=STREAM_EOF)){
				throw newBitstreamException(ex.getErrorCode(), ex);
			}
		}

		return result;
	}

	private Header readNextFrame() throws BitstreamException{
		if (m_nFrameSize == -1)
			nextFrame();

		return m_header;
	}

	private void nextFrame() throws BitstreamException{
		m_header.read_header(this, m_crc);
	}

	public void unreadFrame() throws BitstreamException{
		if((m_nWordPointer == -1) && (m_nBitIdx == -1) && (m_nFrameSize > 0)){
			try {
				long nPos= m_source.getFilePointer();
				m_source.seek(nPos - m_nFrameSize);
			}catch (IOException ex){
				System.out.print(ex);
			}
		}
	}

	public void closeFrame(){
		m_nFrameSize	= -1;
		m_nWordPointer	= -1;
		m_nBitIdx	= -1;
	}

	public boolean isSyncCurrentPosition(int syncmode) throws BitstreamException{
		int read= readBytes(m_syncBuffer, 0, 4);
		int headerstring= ((m_syncBuffer[0] << 24) & 0xFF000000) | ((m_syncBuffer[1] << 16) & 0x00FF0000) | ((m_syncBuffer[2] << 8) & 0x0000FF00) | ((m_syncBuffer[3] << 0) & 0x000000FF);

		try{
			long nPos= m_source.getFilePointer();
			m_source.seek(nPos - read);
		}catch (IOException ex){
			System.out.print(ex);
		}

		boolean sync= false;
		switch(read){
			case 0:
				sync= true;
			break;
			case 4:
				sync= isSyncMark(headerstring, syncmode, m_nSyncWord);
			break;
		}

		return sync;
	}

	public int readBits(int n){
		return getBits(n);
	}

	public int readCheckedBits(int n){
		return getBits(n);
	}

	protected BitstreamException newBitstreamException(int errorcode){
		return new BitstreamException(errorcode, null);
	}

	protected BitstreamException newBitstreamException(int errorcode, Throwable throwable){
		return new BitstreamException(errorcode, throwable);
	}

	int syncHeader(byte syncmode) throws BitstreamException{
		boolean sync;
		int headerstring;

		int bytesRead= readBytes(m_syncBuffer, 0, 3);

		if(bytesRead != 3)
			throw newBitstreamException(STREAM_EOF, null);

		headerstring= ((m_syncBuffer[0] << 16) & 0x00FF0000) | ((m_syncBuffer[1] << 8) & 0x0000FF00) | ((m_syncBuffer[2] << 0) & 0x000000FF);

		do{
			headerstring <<= 8;

			if(readBytes(m_syncBuffer, 3, 1) != 1)
				throw newBitstreamException(STREAM_EOF, null);

			headerstring|= (m_syncBuffer[3] & 0x000000FF);
			sync= isSyncMark(headerstring, syncmode, m_nSyncWord);
		}while (!sync);

		long nFrameLengh;

		if(m_isFirstFrame){
			try {
				m_nFramesStart = m_source.getFilePointer() - 4;
			} catch (IOException ex){
				System.out.print(ex);
			}
		}

//		current_frame_number++;
//		if(last_frame_number < current_frame_number)
//			last_frame_number= current_frame_number;

		return headerstring;
	}

	public boolean isSyncMark(int headerstring, int syncmode, int word){
		boolean sync= false;

		if(syncmode == INITIAL_SYNC)
			sync= ((headerstring & 0xFFE00000) == 0xFFE00000);	// SZD: MPEG 2.5
		else
			sync= ((headerstring & 0xFFF80C00) == word) && (((headerstring & 0x000000C0) == 0x000000C0) == m_isSingleChannelMode);

		if(sync)
			sync= (((headerstring >>> 10) & 3)!=3);

		if(sync)
			sync= (((headerstring >>> 17) & 3)!=0);

		if(sync)
			sync= (((headerstring >>> 19) & 3)!=1);

		return sync;
	}

	int readFrameData(int bytesize) throws BitstreamException{
		int	numread= readFully(m_frameBufferBytes, 0, bytesize);

		m_nFrameSize	= bytesize;
		m_nWordPointer = -1;
		m_nBitIdx	= -1;

		return numread;
	}

	void parseFrame() throws BitstreamException{
		int	b= 0;
		byte[] byteread = m_frameBufferBytes;
		int bytesize = m_nFrameSize;

//		for (int t= 0; t < (byteread.length - 2); ++t){
//			if ((byteread[t] == 'T') && (byteread[t+1] == 'A') && (byteread[t+2] == 'G')){
//				System.out.println("ID3v1 detected at offset " + t);
//				throw newBitstreamException(INVALIDFRAME, null);
//			}
//		}

		for (int k= 0; k < bytesize; k+= 4){
			byte b0= byteread[k];
			byte b1= ((k+1) < bytesize)? b1= byteread[k+1] : 0;
			byte b2= ((k+2) < bytesize)? b2= byteread[k+2] : 0;
			byte b3= ((k+3) < bytesize)? b3= byteread[k+3] : 0;

			m_frameBuffer[b++]= ((b0 << 24) &0xFF000000) | ((b1 << 16) & 0x00FF0000) | ((b2 << 8) & 0x0000FF00) | (b3 & 0x000000FF);
		}

		m_nWordPointer	= 0;
		m_nBitIdx	= 0;
	}

	public int getBits(int p_nBitsNb){
		int sum			= m_nBitIdx + p_nBitsNb;
		int	returnvalue	= 0;

		if(m_nWordPointer < 0)
			m_nWordPointer= 0;

		if(sum <= 32){
			returnvalue= (m_frameBuffer[m_nWordPointer] >>> (32 - sum)) & BITMASK[p_nBitsNb];

			if((m_nBitIdx += p_nBitsNb) == 32){
				m_nBitIdx= 0;
				m_nWordPointer++;
			}

			return returnvalue;
		}

		int right= (m_frameBuffer[m_nWordPointer++] & 0x0000FFFF);
		int left= (m_frameBuffer[m_nWordPointer] & 0xFFFF0000);

		returnvalue= ((right << 16) & 0xFFFF0000) | ((left >>> 16)& 0x0000FFFF);
		returnvalue>>>= 48 - sum;
		returnvalue&= BITMASK[p_nBitsNb];

		m_nBitIdx= sum - 32;

		return returnvalue;
	}

	void setSyncWord(int syncword0){
		m_nSyncWord= syncword0 & 0xFFFFFF3F;
		m_isSingleChannelMode= ((syncword0 & 0x000000C0) == 0x000000C0);
	}

	private int readFully(byte[] b, int offs, int len)	throws BitstreamException{
		int nRead= 0;

		try{
			while (len > 0){

				int nBytesRead= m_source.read(b, offs, len);
				if(nBytesRead == -1){
					while(len-->0)
						b[offs++]= 0;

					break;
				}

				nRead+= nBytesRead;
				offs+= nBytesRead;
				len-= nBytesRead;
			}
		}catch (IOException ex){
			throw newBitstreamException(STREAM_ERROR, ex);
		}

		return nRead;
	}

	private int readBytes(byte[] b, int offs, int len)	throws BitstreamException{
		int totalBytesRead= 0;

		try{
			while(len > 0){

				int nBytesRead= m_source.read(b, offs, len);
				if(nBytesRead == -1)
					break;

				totalBytesRead+= nBytesRead;
				offs+= nBytesRead;
				len-= nBytesRead;
			}
		}catch (IOException ex){
			throw newBitstreamException(STREAM_ERROR, ex);
		}

		return totalBytesRead;
	}

	public void seek(int p_nFrame){
		try{
			m_source.seek(m_nFramesStart);
			while((p_nFrame--) > 0){
				skipFrame();
			}
		}catch(Exception ex){
			System.out.print(ex);
		}
	}

	protected void skipFrame() throws JavaLayerException{
		readFrame();
		closeFrame();
	}
}