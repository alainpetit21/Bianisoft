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


//Bianisoft imports
import com.bianisoft.engine.helper.Bool;


public class ScnNode_Boolean_Wait extends ScenaricNode{
	private Bool	m_objBooleanToWait;
	private boolean	m_bExpectedValue;


	public ScnNode_Boolean_Wait(Bool p_objBooleanToWait, boolean p_bExpectedValue){
		m_objBooleanToWait= p_objBooleanToWait;
		m_bExpectedValue= p_bExpectedValue;
	}

	public boolean manage(double p_fTimeTick){
		if(m_objBooleanToWait.m_bValue == m_bExpectedValue)
			return false;

		return super.manage(p_fTimeTick);
	}
}
