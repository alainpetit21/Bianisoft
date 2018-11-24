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
package com.bianisoft.engine;


//Bianisoft imports
import com.bianisoft.engine.sprites.Sprite;
import com.bianisoft.engine.sprites.Sprite.State;


public class Button extends Sprite{
	public interface Callback{
		public void callbackStateChanged(int p_nNewState, Button p_obj);
	};

	public static final int ST_IDLE		= 0;
	public static final int ST_OVER		= 1;
	public static final int ST_DOWN		= 2;
	public static final int ST_CLICKED	= 4;

	public Callback	m_objCallback;
	public int		m_nOldState;
	public boolean	m_isSelected;
	public boolean	m_isDisabled;


	public Button(){
		super();
		setClassID(IDCLASS_Button);
	}

	public Button(String p_stResImage, int p_nFrameIdle, double p_fSpeedIdle,int p_nFrameOver, double p_fSpeedOver,
				  int p_nFrameDown, double p_fSpeedDown,int p_nFrameSelected, double p_fSpeedSelected){
		super(p_stResImage);
		setClassID(IDCLASS_Button);

		m_vecStates.add(new State("Idle", p_nFrameIdle, p_fSpeedIdle));
		m_vecStates.add(new State("Over", p_nFrameOver, p_fSpeedOver));
		m_vecStates.add(new State("Down", p_nFrameDown, p_fSpeedDown));
		m_vecStates.add(new State("Selected", p_nFrameSelected, p_fSpeedSelected));
	}

	public void setSelected(boolean p_isSelected)	{m_isSelected= p_isSelected;}
	public void setDisabled(boolean p_isDisabled)	{m_isDisabled= p_isDisabled;}

	public boolean isSelected()	{return m_isSelected;}
	public boolean isDisabled()	{return m_isDisabled;}

	public void setCurState(int p_nIdx){
		super.setCurState(p_nIdx);
		m_nOldState= p_nIdx;
	}

	public void manage(double p_fTimeScaleFactor){
		super.manage(p_fTimeScaleFactor);
		m_nCurState= m_nOldState;
	}

	public void draw() {
		setFilterAlpha(m_isDisabled? 0.5:1);

		super.draw();

		if(m_isSelected){
			int nOldState= m_nCurState;
			m_nCurState= 3;
			super.draw();
			m_nCurState= nOldState;
		}
	}
}
