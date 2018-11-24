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
package com.bianisoft.engine.sprites;


import com.sun.opengl.util.texture.TextureCoords;
import javax.media.opengl.GL;
import com.bianisoft.engine.App;
import com.bianisoft.engine.Camera;



public class Sprite3D extends Sprite{
	public void draw(){
		if(!isShown() || !isLoaded())
			return;

		GL gl= App.g_CurrentGL;

		App.g_theApp.orthogonalEnd(App.g_CurrentDrawable);
		gl.glPushMatrix();

		Camera.getCur(Camera.TYPE_3D).doProjection();

		gl.glTranslated(m_vPos[0], m_vPos[1], -m_vPos[2]);
		gl.glRotated(m_vAngle[0], 1.0, 0.0, 0.0);
		gl.glRotated(m_vAngle[1], 0.0, 1.0, 0.0);
		gl.glRotated(m_vAngle[2], 0.0, 0.0, 1.0);
		gl.glColor4d(m_colorFilterRed, m_colorFilterGreen, m_colorFilterBlue, m_colorFilterAlpha);

		State stateCur= m_vecStates.get(m_nCurState);
		Frame frameCur= stateCur.m_vecFrames.get(stateCur.m_nCurFrame>>5);

		TextureCoords texCoor	= m_image.getImageTexCoords();

		double nMaxX= texCoor.right() - texCoor.left();
		double nMaxY= texCoor.bottom() - texCoor.top();

		double nSrcX1= (stateCur.m_nCurFrame>>5) * (nMaxX * (m_nWidthFrame/m_nWidthImage));
		double nSrcX2= ((stateCur.m_nCurFrame>>5)+1) * (nMaxX * (m_nWidthFrame/m_nWidthImage));
		double nSrcY1= m_nCurState * (nMaxY * (m_nHeightFrame/m_nHeightImage));
		double nSrcY2= (m_nCurState+1) * (nMaxY * (m_nHeightFrame/m_nHeightImage));

		m_image.bind();
		gl.glEnable(GL.GL_BLEND);
		gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);

		double nDestX1= (-frameCur.m_vHotSpot[0]) * m_fZoom;
		double nDestX2= (m_nWidthFrame - frameCur.m_vHotSpot[0]) * m_fZoom;
		double nDestY1= (-frameCur.m_vHotSpot[1]) * m_fZoom;
		double nDestY2= (m_nHeightFrame - frameCur.m_vHotSpot[1]) * m_fZoom;

		//Rotate
		double[] nDestNW= {(nDestX1 * Math.cos(m_vAngle[2])) - (nDestY1 * Math.sin(m_vAngle[2])), (nDestX1 * Math.sin(m_vAngle[2])) + (nDestY1 * Math.cos(m_vAngle[2]))};
		double[] nDestNE= {(nDestX2 * Math.cos(m_vAngle[2])) - (nDestY1 * Math.sin(m_vAngle[2])), (nDestX2 * Math.sin(m_vAngle[2])) + (nDestY1 * Math.cos(m_vAngle[2]))};
		double[] nDestSE= {(nDestX2 * Math.cos(m_vAngle[2])) - (nDestY2 * Math.sin(m_vAngle[2])), (nDestX2 * Math.sin(m_vAngle[2])) + (nDestY2 * Math.cos(m_vAngle[2]))};
		double[] nDestSW= {(nDestX1 * Math.cos(m_vAngle[2])) - (nDestY2 * Math.sin(m_vAngle[2])), (nDestX1 * Math.sin(m_vAngle[2])) + (nDestY2 * Math.cos(m_vAngle[2]))};

		gl.glBegin(GL.GL_QUADS);
			gl.glTexCoord2d(nSrcX1, nSrcY1);
			gl.glVertex2d(nDestNW[0], nDestNW[1]);

			gl.glTexCoord2d(nSrcX1, nSrcY2);
			gl.glVertex2d(nDestSW[0], nDestSW[1]);

			gl.glTexCoord2d(nSrcX2, nSrcY2);
			gl.glVertex2d(nDestSE[0], nDestSE[1]);

			gl.glTexCoord2d(nSrcX2, nSrcY1);
			gl.glVertex2d(nDestNE[0], nDestNE[1]);
		gl.glEnd();
		gl.glPopMatrix();
	}
}
