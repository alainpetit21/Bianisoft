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
package com.bianisoft.engine.manager.Physic;


public class Line{
	public double[]	m_fStart= new double[2];
	public double[]	m_fEnd	= new double[2];
	public double	m_fAngle;
	public int		m_nGroup;

	public Line(int p_nX1, int p_nY1, int p_nX2, int p_nY2, int p_nGroup){
		m_fStart[0]	= p_nX1;
		m_fStart[1]	= p_nY1;
		m_fEnd[0]	= p_nX2;
		m_fEnd[1]	= p_nY2;
		m_nGroup	= p_nGroup;
		m_fAngle	= Math.atan2(m_fStart[1]-m_fEnd[1], m_fStart[0]-m_fEnd[0]);
	}

	public void setStart(int p_nX, int p_nY){
		m_fStart[0]	= p_nX;
		m_fStart[1]	= p_nY;
		m_fAngle	= Math.atan2(m_fStart[1]-m_fEnd[1], m_fStart[0]-m_fEnd[0]);
	}

	public void setEnd(int p_nX, int p_nY){
		m_fEnd[0]	= p_nX;
		m_fEnd[1]	= p_nY;
		m_fAngle	= Math.atan2(m_fStart[1]-m_fEnd[1], m_fStart[0]-m_fEnd[0]);
	}

	public void updateAngle(){
		m_fAngle= Math.atan2(m_fStart[1]-m_fEnd[1], m_fStart[0]-m_fEnd[0]);
	}
}
