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


public class ScnNode_ArrayDrawableObj_AnimateAlpha extends ScenaricNode{
	Drawable[]	m_arDrawableObj;
	double			m_fInc= 0.05;
	double			m_fValue= 1.0;



	public ScnNode_ArrayDrawableObj_AnimateAlpha(Drawable[] p_arDrawableObj){
		m_arDrawableObj= p_arDrawableObj;
	}

	public boolean manage(double p_fTimeTick){
		m_fValue+= m_fInc;
		if(m_fValue <= 0.0){
			m_fValue= 0.0;
			m_fInc*= -1;
		}else if(m_fValue >= 1.0){
			m_fValue= 1.0;
			m_fInc*= -1;
		}

		for(Drawable obj : m_arDrawableObj)
			obj.setFilterAlpha(m_fValue);

		return super.manage(p_fTimeTick);
	}

	public void stop(){
		super.stop();

		for(Drawable obj : m_arDrawableObj)
			obj.setFilterAlpha(m_fValue= 1.0);

	}
}
