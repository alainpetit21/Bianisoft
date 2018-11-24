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
package com.bianisoft.engine.manager;


import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import com.bianisoft.engine.App;
import com.bianisoft.engine.Camera;
import com.bianisoft.engine.Context;


public class MngContextSwitcher {
	private boolean m_isFinishedFadeout	= false;
	private boolean m_isFinishedActivate= false;
	private boolean m_isFinishedFadeIn	= false;
	private double	m_fAlphaFade		= 0.0;
	private double	m_fIncAlpha			= 0.05;

	private Context m_ctxFrom;
	private Context m_ctxTo;
	private int		m_nCtxTo= -1;


	public MngContextSwitcher(Context p_ctxCur, Context p_ctxTo){
		m_ctxFrom= p_ctxCur;
		m_ctxTo= p_ctxTo;
	}

	public MngContextSwitcher(Context p_ctxCur, int p_nCtxTo){
		m_ctxFrom= p_ctxCur;
		m_nCtxTo= p_nCtxTo;
	}

	public boolean manage(){
		if(!m_isFinishedFadeout){
			m_fAlphaFade+= m_fIncAlpha;
			if(m_fAlphaFade >= 1.0)
				m_isFinishedFadeout= true;

			if(App.get().m_ctxCur != null)
				draw();
		}else if(!m_isFinishedActivate){
			if(m_ctxTo == null){
				if(App.get().m_arObj.size() != 0)
					m_ctxTo= (Context)App.get().m_arObj.get(m_nCtxTo);

				return false;
			}

			if(m_ctxFrom != null)
				m_ctxFrom.deActivate();

			App.get().m_ctxCur= m_ctxTo;
			m_ctxTo.activate();
			m_ctxTo.manage(1.0);
			draw();
			m_isFinishedActivate= true;
		}else if(!m_isFinishedFadeIn){
			m_fAlphaFade-= m_fIncAlpha;
			if(m_fAlphaFade <= 0.0)
				m_isFinishedFadeIn= true;

			draw();
		}

		return m_isFinishedFadeIn;
	}

	private void draw(){
		GLAutoDrawable glDrawable= App.g_CurrentDrawable;
		GL gl= App.g_CurrentGL;

		//Initiate Draw
		gl.glClear(GL.GL_COLOR_BUFFER_BIT);
		gl.glClear(GL.GL_DEPTH_BUFFER_BIT);
		gl.glLoadIdentity();

		//Draw 2D
		gl.glEnable(GL.GL_BLEND);
		gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);

		if(App.get().m_ctxCur != null)
			App.get().m_ctxCur.draw();
		if(App.get().m_isFullScreen)
			App.get().drawFullscreenPadding();

		//Draw Fade
		App.g_theApp.orthogonalStart(App.g_CurrentDrawable);

		gl.glPushMatrix();

		Camera.getCur(Camera.TYPE_2D).doProjection();

		gl.glColor4d(0, 0, 0, m_fAlphaFade);
		gl.glDisable(GL.GL_TEXTURE_2D);
		gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL.GL_FILL);

	    gl.glBegin (GL.GL_QUADS);
			gl.glVertex2d(-(App.get().m_nWidth/2),  (App.get().m_nHeight/2));
			gl.glVertex2d( (App.get().m_nWidth/2),  (App.get().m_nHeight/2));
			gl.glVertex2d( (App.get().m_nWidth/2), -(App.get().m_nHeight/2));
			gl.glVertex2d(-(App.get().m_nWidth/2), -(App.get().m_nHeight/2));
        gl.glEnd ();

//		gl.glBegin(GL.GL_QUADS);
//			gl.glVertex2d(p_nBBWidth/2, -m_nHeight/2);	//Right	Top
//			gl.glVertex2d(p_nBBWidth/2, m_nHeight/2);	//Right	Bottom
//			gl.glVertex2d(m_nWidth/2, m_nHeight/2);		//Left	Bottom
//			gl.glVertex2d(m_nWidth/2, -m_nHeight/2);	//Left	Top
//		gl.glEnd();

//		gl.glBegin(GL.GL_QUADS);
//			gl.glVertex2d( 10, -10);
//			gl.glVertex2d( 10, 10);
//			gl.glVertex2d(-10, 10);
//			gl.glVertex2d(-10, -10);
//		gl.glEnd();

		gl.glPopMatrix();
		gl.glEnable(GL.GL_TEXTURE_2D);
		gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL.GL_FILL);

		gl.glEnd();
		glDrawable.swapBuffers();
	}
}
