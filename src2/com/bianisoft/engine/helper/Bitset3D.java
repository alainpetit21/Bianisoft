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


public class Bitset3D extends Bitset{
	protected int m_nWidth;
	protected int m_nDepth;

	public Bitset3D(int p_nX, int p_nY, int p_nZ){
		super(p_nX*p_nY*p_nZ);
		m_nWidth= p_nX;
		m_nDepth= p_nZ;
	}

	public void set(int p_nX, int p_nY, int p_nZ){
		set((((p_nY*m_nWidth)+p_nX)*m_nDepth)+p_nZ);
	}

	public void clear(int p_nX, int p_nY, int p_nZ){
		clear((((p_nY*m_nWidth)+p_nX)*m_nDepth)+p_nZ);
	}

	public void setPacket(int p_nX, int p_nY, int p_nValue, int p_nLength){
		for(int i= 0; i < p_nLength; ++i){
			if((p_nValue&(1<<i)) != 0)
				set(p_nX, p_nY, i);
			else
				clear(p_nX, p_nY, i);
		}
	}

	public boolean get(int p_nX, int p_nY, int p_nZ){
		return get((((p_nY*m_nWidth)+p_nX)*m_nDepth)+p_nZ);
	}

	public long getPacket(int p_nX, int p_nY, int p_nLenght){
		long res= 0;

		for(int i= 0; i < p_nLenght; ++i)
			if(get(p_nX, p_nY, i))
				res|= 1<<i;

		return res;
	}
}
