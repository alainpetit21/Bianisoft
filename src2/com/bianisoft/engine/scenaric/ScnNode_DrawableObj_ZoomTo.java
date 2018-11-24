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
import com.bianisoft.engine.Drawable;


public class ScnNode_DrawableObj_ZoomTo extends ScenaricNode{
	Drawable m_drawableObj;
	double	m_fZoomTo;
	double	m_fInc;
	int		m_nDir= 1;


	public ScnNode_DrawableObj_ZoomTo(Drawable p_drawableObj, double p_fZoomTo, double p_fInc){
		m_drawableObj= p_drawableObj;
		m_fZoomTo= p_fZoomTo;
		m_fInc= p_fInc;
	}

	public void start(){
		super.start();

		m_drawableObj.m_isShown= true;
		if((m_fZoomTo - m_drawableObj.m_fZoom) < 0)
			m_nDir= -1;
	}

	public boolean manage(double p_fTimeTick){
		if(Math.abs(m_drawableObj.m_fZoom - m_fZoomTo) < m_fInc){
			m_drawableObj.m_fZoom= m_fZoomTo;
			return false;
		}

		m_drawableObj.m_fZoom+= m_nDir * m_fInc;
		return super.manage(p_fTimeTick);
	}
}
