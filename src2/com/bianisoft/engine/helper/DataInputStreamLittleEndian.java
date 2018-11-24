/* This file is part of the Bianisoft game library.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 *----------------------------------------------------------------------
 * Copyright (C) Alain Petit - alainpetit21@hotmail.com
 *
 * 18/12/10			0.1 First beta initial Version.
 *
 *-----------------------------------------------------------------------
 */
package com.bianisoft.engine.helper;


import java.io.DataInputStream;
import java.io.InputStream;
import com.bianisoft.engine.Obj;


public class DataInputStreamLittleEndian{
	String			m_stFilename;
	InputStream		m_objIS;
	DataInputStream m_objDIS;


	public DataInputStreamLittleEndian(String p_stFilename){
		m_stFilename= p_stFilename;

		m_stFilename= Obj.fixResFilename(m_stFilename);
		m_objIS= Thread.currentThread().getContextClassLoader().getResourceAsStream(m_stFilename);
		m_objDIS= new DataInputStream(m_objIS);
	}

    public int readInt(){
		try{
			int b1 = m_objDIS.read();
			int b2 = m_objDIS.read();
			int b3 = m_objDIS.read();
			int b4 = m_objDIS.read();

			return (b4 << 24)
				+ ((b3 << 24) >>> 8)
				+ ((b2 << 24) >>> 16)
				+ ((b1 << 24) >>> 24);
 		}catch (Exception e){
			e.printStackTrace();
		}

		return 0;
   }

	public void close(){
		try{
			m_objDIS.close();
		}catch (Exception e){
			e.printStackTrace();
		}
	}

	public void seek(int p_nOffset){
	   rewind();
	   skip(p_nOffset);
   }

   public void rewind(){
		close();

		m_objIS= Thread.currentThread().getContextClassLoader().getResourceAsStream(m_stFilename);
		m_objDIS= new DataInputStream(m_objIS);
   }

    public short readUnsignedShort(){
		try{
			int b1 = m_objDIS.read();
			int b2 = m_objDIS.read();

			if (b1 < 0)
				b1 += 256;
			if (b2 < 0)
				b2 += 256;

			return (short)(b2*256+b1);
		}catch (Exception e){
			e.printStackTrace();
		}

		return 0;
    }

    public short readShort(){
		try{
			int b1 = m_objDIS.read();
			int b2 = m_objDIS.read();

			if (b1 < 0)
				b1 += 256;

			return (short)(b2*256+b1);
		}catch (Exception e){
			e.printStackTrace();
		}

		return 0;
    }

    public char readUnsignedChar(){
		try{
			int b = m_objDIS.read();
			if (b < 0)
				b+=256;
			return (char)b;
		}catch (Exception e){
			e.printStackTrace();
		}

		return 0;
    }

    public final float readFloat(){
        return Float.intBitsToFloat(readInt());
    }

    public int read(byte[] buff){
		try{
			return m_objDIS.read(buff);
		}catch (Exception e){
			e.printStackTrace();
		}

		return 0;
    }


    public String readString(int length){
		byte[] buff= new byte[length];

		try{
			m_objDIS.read(buff);
			return new String(buff);
		}catch (Exception e){
			e.printStackTrace();
		}

		return null;
    }

	public void skip(int p_nNb){
		try{
			m_objDIS.skip(p_nNb);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
