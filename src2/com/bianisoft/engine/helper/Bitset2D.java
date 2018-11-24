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


public class Bitset2D extends Bitset{
	protected int m_nWidth;

	public Bitset2D(int p_nX, int p_nY){
		super(p_nX*p_nY);
		m_nWidth= p_nX;
	}

	public void set(int p_nX, int p_nY){
		set((p_nY*m_nWidth)+p_nX);
	}

	public void clear(int p_nX, int p_nY){
		clear((p_nY*m_nWidth)+p_nX);
	}

	public void setPacket(int p_nIdx, int p_nValue, int p_nLength){
		for(int i= 0; i < p_nLength; ++i){
			if((p_nValue&(1<<i)) != 0)
				set(i, p_nIdx);
			else
				clear(i, p_nIdx);
		}
	}

	public boolean get(int p_nX, int p_nY){
		return get((p_nY*m_nWidth)+p_nX);
	}

	public long getPacket(int p_nIdx, int p_nLenght){
		long res= 0;

		for(int i= 0; i < p_nLenght; ++i)
			if(get(i, p_nIdx))
				res|= 1<<i;

		return res;
	}
}
