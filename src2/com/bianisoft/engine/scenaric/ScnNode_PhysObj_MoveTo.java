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
import com.bianisoft.engine.PhysObj;


public class ScnNode_PhysObj_MoveTo extends ScenaricNode{
	PhysObj m_physObj;
	int		m_nPosToX;
	int		m_nPosToY;


	public ScnNode_PhysObj_MoveTo(PhysObj p_physObj, int p_nPosX, int p_nPosY){
		m_physObj= p_physObj;
		m_nPosToX= p_nPosX;
		m_nPosToY= p_nPosY;
	}

	public boolean manage(double p_fTimeTick){
		if(!m_physObj.isMoving())
			return false;

		return super.manage(p_fTimeTick);
	}

	public void start(){
		m_physObj.moveTo(m_nPosToX, m_nPosToY, m_physObj.getPosZ(), m_physObj.getAngleX(), m_physObj.getAngleY(), m_physObj.getAngleZ(), 1000);
	}
}
