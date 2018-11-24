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


public class Bitset{
	protected long m_bits[];
	protected int m_nBitsSize;
	protected int m_nUnitSize;


	public Bitset(int p_nSize){
		m_nBitsSize= p_nSize;
		m_nUnitSize= (p_nSize>>5)+1;

		m_bits= new long[m_nUnitSize];
	}

	public void reset(){
		for(int i= 0; i < m_nUnitSize; ++i)
			m_bits[i]= 0;
	}

	public void set(int p_nIdx){
		int unitIdx= p_nIdx>>5;
		int bitIdx= p_nIdx-(unitIdx<<5);

		m_bits[unitIdx]|= 1<<bitIdx;
	}

	public void clear(int p_nIdx){
		int unitIdx= p_nIdx>>5;
		int bitIdx= p_nIdx-(unitIdx<<5);

		m_bits[unitIdx]&= ~(1<<bitIdx);
	}

	public boolean get(int p_nIdx){
		return ((m_bits[p_nIdx>>5] & (1<<p_nIdx-((p_nIdx>>5)<<5))) != 0);
	}
}
