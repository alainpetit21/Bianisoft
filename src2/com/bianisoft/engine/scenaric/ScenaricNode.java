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
package com.bianisoft.engine.scenaric;


//Standard Java imports
import java.util.ArrayList;

//Bianisoft imports
import com.bianisoft.engine.helper.Int;


public class ScenaricNode{
	public int			m_nIdx;
	public boolean		m_isRunning;
	public boolean		m_isForcedToStop;
	public ArrayList<Int>	m_vecIdxChilds= new ArrayList<Int>();


	public void addChild(int p_idxNode){
		m_vecIdxChilds.add(new Int(p_idxNode));
	}

	public void start()							{m_isRunning= true;}
	public boolean manage(double p_fTimeTick)	{return !m_isForcedToStop;}
	public void end()							{m_isRunning= false;}
	
	public void stop()							{m_isForcedToStop= true;}
}
