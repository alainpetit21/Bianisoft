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
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.awt.image.BufferedImage;

//Standard JOGL imports
import javax.media.opengl.GL;
import com.sun.opengl.util.texture.Texture;
import com.sun.opengl.util.texture.TextureCoords;
import com.sun.opengl.util.texture.TextureIO;

//Bianisoft imports
import com.bianisoft.engine.App;
import com.bianisoft.engine.Camera;
import com.bianisoft.engine.helper.Real;


public class LabelGradual extends Label{
	private ArrayList<String>	m_vecText= new ArrayList<String>();
	private ArrayList<Real> m_vecCpt= new ArrayList<Real>();
	private ArrayList<Texture>	m_vecTexture= new ArrayList<Texture>();

	private int		m_nCurLine;
	private double	m_fInc= 0.05;
	public boolean	m_isPaused;


	public LabelGradual(String p_stFontName, int p_nFontSize, Color p_nColor, String p_stText, int p_nMode, boolean p_isMultiline, Rectangle p_rect){
		super(p_stFontName, p_nFontSize, p_nColor, p_stText, p_nMode, p_isMultiline, p_rect);
		setSubClassID(TYPE_GRADUAL);
	}

	public void set(String p_stText) {
		super.set(p_stText);
		
		//Chop full text into Vector of lines of text
		//Init
		m_vecText= new ArrayList<String>();
		m_vecCpt= new ArrayList<Real>();
		m_vecTexture= new ArrayList<Texture>();
		m_nCurLine= 0;
		m_isPaused= false;

		//Temp object needed to calculate metrics
		BufferedImage	tempImage= new BufferedImage(8, 8, BufferedImage.TYPE_4BYTE_ABGR);
		Graphics2D		objJava2d= (Graphics2D)tempImage.getGraphics();

		objJava2d.setFont(m_font);

		//Temp variables needed for String calculations
		char[]			temp	= new char[256];
		int				cptSrc	= 0;


		//Loop through Whole Text
		while(cptSrc < m_stText.length()){
			int		nLastCptSrc		= 0;	//Keep track of last space of \n, in whole src buffer
			int		nLastCptDst		= -1;	//Keep track of last space of \n, in dst buffer (on a per-line basis)
			int		nX				= 0;
			int		cptDst			= 0;

			//Within a line loop
			while(cptSrc < m_stText.length()){
				if(((m_stText.charAt(cptSrc) == '\\') && (m_stText.charAt(cptSrc+1) == 'n'))){
					cptSrc+=2;
					temp[cptDst++]	= '\n';
					nLastCptDst		= cptDst;
					nLastCptSrc		= cptSrc;
					break;
				}else if(m_stText.charAt(cptSrc) == '\n'){
					cptSrc+=1;
					temp[cptDst++]	= '\n';
					nLastCptDst		= cptDst;
					nLastCptSrc		= cptSrc;
					break;
				}

				if(m_stText.charAt(cptSrc) == ' '){
					nLastCptDst		= cptDst+1;
					nLastCptSrc		= cptSrc+1;
				}

				temp[cptDst++]= m_stText.charAt(cptSrc++);

				//Put new line in last good spot
				int nLetterWidth=  objJava2d.getFontMetrics().charWidth(temp[cptDst - 1]);
				if((nX + nLetterWidth) > m_recLimit.width){
					if(nLastCptSrc == 0){
						nLastCptDst		= cptDst;
						nLastCptSrc		= cptSrc;
					}

					temp[nLastCptDst]	= '\0';
					cptSrc				= nLastCptSrc;
					break;
				}

				nX			+= nLetterWidth;
			}

			if(cptSrc == m_stText.length())
				nLastCptDst= cptDst;

			m_vecText.add(new String(temp, 0, nLastCptDst));
			m_vecCpt.add(new Real(0));
		}

		for(int i= 0; i < m_vecText.size(); ++i){
			tempImage= new BufferedImage(m_recLimit.width, m_nFontSize*2, BufferedImage.TYPE_4BYTE_ABGR);
			objJava2d= (Graphics2D)tempImage.getGraphics();

			Composite orig = objJava2d.getComposite();
			objJava2d.setComposite(AlphaComposite.Clear);
			objJava2d.fillRect(0, 0, m_recLimit.width, m_recLimit.height);
			objJava2d.setComposite(orig);

			objJava2d.setColor(m_nColor);
			objJava2d.setFont(m_font);

			preRenderLine(objJava2d, 0, m_nFontSize*2, m_vecText.get(i));
			m_vecTexture.add(TextureIO.newTexture(tempImage, false));
		}
	}

	public boolean isDone()				{return ((int)m_nCurLine == (m_vecCpt.size()));}
	public void setSpeed(double p_fInc)	{m_fInc= p_fInc;};
	public void clear(){
		set("");
	}

	public void append(String p_stText){
		set(m_stText + p_stText);
	}

	public boolean click(){
		m_isPaused= !m_isPaused;
		return false;
	}

	public boolean doubleClick(){
		return false;
	}

	public void manage(double p_fTimeScaleFactor){
		if(!m_isShown || m_isPaused || isDone())
			return;

		for(int i= 0; i <= m_nCurLine; ++i){
			if(i >= m_vecTexture.size())
				continue;

			Real cpt= m_vecCpt.get(i);

			if((cpt.add(m_fInc)) >= 1.0){
				cpt.set(1.0);

				if(m_nCurLine == i){
					m_nCurLine++;

					String text= m_vecText.get(i);
					if(text.endsWith("\n")){
						m_isPaused= true;
						break;
					}
				}
			}
		}
	}

	public void draw(){
		if((!isShown()) || (m_stText == null))
			return;

		App.g_theApp.orthogonalStart(App.g_CurrentDrawable);

		GL gl= App.g_CurrentGL;
		gl.glPushMatrix();

		Camera.getCur(Camera.TYPE_2D).doProjection();
		gl.glTranslated(m_vPos[0], m_vPos[1], 0);

		for(int i= 0; i <= m_nCurLine; ++i){
			if(i >= m_vecTexture.size())
				continue;

			Texture texRendered= m_vecTexture.get(i);

			TextureCoords texCoor	= texRendered.getImageTexCoords();
			texRendered.enable();
			texRendered.bind();

			gl.glEnable(GL.GL_BLEND);
			gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
			gl.glColor4d(m_colorFilterRed, m_colorFilterGreen, m_colorFilterBlue, m_colorFilterAlpha);


			Real cpt= m_vecCpt.get(i);
			double	nDestLeft	= m_recLimit.x;
			double	nDestTop	= m_recLimit.y + (i*m_nFontSize);
			double	nDestRight	= nDestLeft + (m_recLimit.width* cpt.get());
			double	nDestBottom	= nDestTop + m_nFontSize*2;


			gl.glBegin (GL.GL_QUADS);
				gl.glTexCoord2d(texCoor.left(), texCoor.bottom());
				gl.glVertex2d(nDestLeft, nDestBottom);

				gl.glTexCoord2d(texCoor.right() * cpt.get(), texCoor.bottom());
				gl.glVertex2d(nDestRight, nDestBottom);

				gl.glTexCoord2d(texCoor.right() * cpt.get(), texCoor.top());
				gl.glVertex2d(nDestRight, nDestTop);

				gl.glTexCoord2d(texCoor.left(), texCoor.top());
				gl.glVertex2d(nDestLeft, nDestTop);
			gl.glEnd ();
		}
		gl.glPopMatrix();

		//Print Debug Rect
		if(App.PRINT_DEBUG)
			drawDebug();
	}
}
