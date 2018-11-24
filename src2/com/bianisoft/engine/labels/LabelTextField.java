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
package com.bianisoft.engine.labels;


//Standard Java imports
import java.awt.Color;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

//Standard JOGL imports
import javax.media.opengl.GL;

//Bianisoft imports
import com.bianisoft.engine.App;
import com.bianisoft.engine.Camera;


public class LabelTextField extends Label implements KeyListener{
	public interface Callback{
		public boolean callbackLostFocus(LabelTextField p_obj);
	};


	public LabelTextField.Callback m_objCallback;


	public LabelTextField(String p_stFontName, int p_nFontSize, Color p_nColor, String p_stText, int p_nMode, boolean p_isMultiline, Rectangle p_rect){
		super(p_stFontName, p_nFontSize, p_nColor, p_stText, p_nMode, p_isMultiline, p_rect);
		setSubClassID(TYPE_TEXTFIELD);

		m_nColor= Color.BLACK;
		App.g_CurrentDrawable.addKeyListener(this);
	}

	public boolean click(){
		return m_hasKeyFocus= true;
	}

	public void keyTyped(KeyEvent p_event){
		if(!m_hasKeyFocus)
			return;

		char retChar= p_event.getKeyChar();

		if(retChar == '\b'){
			if(m_stText.length() > 0){
				m_stText= m_stText.substring(0, m_stText.length()-1);
				m_isDirty= true;
			}
		}else if(retChar == '\n'){
			if(m_isMultiline){
				m_stText+= retChar;
				m_isDirty= true;
			}else{
				if(m_objCallback != null)
					if(m_objCallback.callbackLostFocus(this))
						m_hasKeyFocus= false;
			}
		}else if(retChar != KeyEvent.CHAR_UNDEFINED){
			m_stText+= retChar;
			m_isDirty= true;
		}
	}

	public void keyPressed(KeyEvent p_event){
		if(!m_hasKeyFocus)
			return;
	}

	public void keyReleased(KeyEvent p_event){
		if(!m_hasKeyFocus)
			return;
	}

	public void draw(){
		if(!m_isShown)
			return;

		int	nCumulLeft	= (int)m_vPos[0] + m_recLimit.x;
		int	nCumulTop	= (int)m_vPos[1] + m_recLimit.y;
		int	nCumulRight	= nCumulLeft + m_recLimit.width;
		int	nCumulBottom= nCumulTop + m_recLimit.height;

		GL gl= App.g_CurrentGL;

		gl.glPushMatrix();
		Camera.getCur(Camera.TYPE_2D).doProjection();

		gl.glDisable(GL.GL_TEXTURE_2D);

		if(m_hasKeyFocus)
			gl.glColor3d(1.0, 1.0, 1.0);
		else
			gl.glColor3d(0.75, 0.75, 1.0);

		gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL.GL_FILL);
		gl.glBegin(GL.GL_QUADS);
			gl.glVertex2d(nCumulLeft+1, nCumulTop+1);
			gl.glVertex2d(nCumulRight-1, nCumulTop+1);
			gl.glVertex2d(nCumulRight-1, nCumulBottom-1);
			gl.glVertex2d(nCumulLeft+1, nCumulBottom-1);
		gl.glEnd();
		gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL.GL_LINE);
		gl.glColor3d(0.0, 0.0, 0.0);

		gl.glBegin(GL.GL_LINE);
			gl.glVertex2d(nCumulLeft+1, nCumulTop+1);
			gl.glVertex2d(nCumulRight-1, nCumulTop+1);
			gl.glVertex2d(nCumulRight-1, nCumulBottom-1);
			gl.glVertex2d(nCumulLeft+1, nCumulBottom-1);
		gl.glEnd();

		gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL.GL_FILL);
		gl.glEnable(GL.GL_TEXTURE_2D);
		gl.glPopMatrix();

		super.draw();
	}
}
