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
import java.awt.Rectangle;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

//Standard JOGL imports
import javax.media.opengl.GL;
import com.sun.opengl.util.texture.Texture;
import com.sun.opengl.util.texture.TextureCoords;
import com.sun.opengl.util.texture.TextureIO;

//Bianisoft imports
import com.bianisoft.engine.App;
import com.bianisoft.engine.Camera;
import com.bianisoft.engine.Drawable;
import com.bianisoft.engine.resmng.FontCache;


public class Label extends Drawable{
	public static final int TYPE_NORMAL= 0;
	public static final int TYPE_GRADUAL= 1;
	public static final int TYPE_TEXTFIELD= 2;

	public static final int MODE_LEFT= 0;
	public static final int MODE_CENTER= 1;
	public static final int MODE_RIGHT= 2;


	public Font			m_font;
	public String		m_stText;
	public Rectangle	m_recLimit;
	public int			m_nMode;
	public Color		m_nColor;
	public String		m_stFontName;
	public int			m_nFontSize;
	public boolean		m_isMultiline;

	private int			m_nOffsetChar= 0;
	private int			m_nOffsetY= 0;

	private BufferedImage	m_bufImageTemp;
	private Graphics2D		m_objJava2dTemp;
	private Texture			m_imgPreRendered;

	public boolean			m_isScrollable= false;
	protected boolean		m_isDirty= true;


	public static Label create(int p_nType, String p_stFontName, int p_nFontSize, String p_stText, int p_nMode, boolean p_isMultiline, Rectangle p_rect){
		if(p_nType == TYPE_GRADUAL)
			return new LabelGradual(p_stFontName, p_nFontSize, Color.WHITE, p_stText, p_nMode, p_isMultiline, p_rect);
		else if(p_nType == TYPE_TEXTFIELD)
			return new LabelTextField(p_stFontName, p_nFontSize, Color.BLACK, p_stText, p_nMode, p_isMultiline, p_rect);
		else
			return new Label(p_stFontName, p_nFontSize, Color.WHITE, p_stText, p_nMode, p_isMultiline, p_rect);
	}

	protected Label(String p_stFontName, int p_nFontSize, Color p_nColor, String p_stText, int p_nMode, boolean p_isMultiline, Rectangle p_rect){
		super(IDCLASS_Label);
		setSubClassID(TYPE_NORMAL);

		m_stText		= p_stText;
		m_recLimit		= p_rect;
		m_nColor		= p_nColor;
		m_stFontName	= p_stFontName;
		m_nFontSize		= p_nFontSize;
		m_nMode			= p_nMode;
		m_isMultiline	= p_isMultiline;

		m_bufImageTemp	= new BufferedImage(1, 1, BufferedImage.TYPE_4BYTE_ABGR);
		m_objJava2dTemp	= (Graphics2D)m_bufImageTemp.getGraphics();
	}

	public void load(){
		m_font= FontCache.getFontFromFile(m_stFontName, m_nFontSize);
	}


	public int getMetricHeight(){
		int		y= (int)(m_nFontSize*1.5);
		
		if(m_isMultiline){
			char[]	temp	= new char[256];
			int		cptSrc	= 0;

			while(cptSrc < m_stText.length()){
				int		nLastCptSrc		= 0;
				int		nLastCptDst		= -1;
				int		nX				= 0;
				int		cptDst			= 0;

				for(int i= 0; i < 256; ++i)
					temp[i]= 0;
				while(cptSrc < m_stText.length()){
					if(((m_stText.charAt(cptSrc) == '\\') && (m_stText.charAt(cptSrc+1) == 'n'))){
						cptSrc+=2;
						nLastCptDst		= cptDst;
						nLastCptSrc		= cptSrc;
						break;
					}else if(m_stText.charAt(cptSrc) == '\n'){
						cptSrc+=1;
						nLastCptDst		= cptDst;
						nLastCptSrc		= cptSrc;
						break;
					}

					if(m_stText.charAt(cptSrc) == ' '){
						nLastCptDst		= cptDst;
						nLastCptSrc		= cptSrc;
					}

					temp[cptDst++]= m_stText.charAt(cptSrc++);

					// put new line in last good spot
					int nLetterWidth=  m_objJava2dTemp.getFontMetrics().charWidth(temp[cptDst - 1]);
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

				if(cptSrc == m_stText.length()){
					nLastCptDst= cptDst;
				}

				y+= m_nFontSize;
			}
		}
		
		return y;
	}

	public int getMetricWidth(){
		return m_recLimit.width;
	}

	public void preRenderLine(Graphics2D p_objJava2d, int p_nX, int p_nY, String p_stText){
		int	nCumulLeft	= p_nX;
		int	nCumulRight	= p_nX + m_recLimit.width;
		int nTextWidth	= p_objJava2d.getFontMetrics().stringWidth(p_stText);

		switch(m_nMode){
		case MODE_LEFT:
			p_objJava2d.drawString(p_stText, nCumulLeft, p_nY - m_nFontSize);
		break;
		case MODE_CENTER:
			p_objJava2d.drawString(p_stText, nCumulLeft + ((nCumulRight-nCumulLeft)/2) - (nTextWidth/2), p_nY - m_nFontSize);
		break;
		case MODE_RIGHT:
			p_objJava2d.drawString(p_stText, nCumulRight-nTextWidth, p_nY - m_nFontSize);
		break;
		}
	}

	public void preRender(BufferedImage	p_bufImage){
		Graphics2D objJava2d= (Graphics2D)p_bufImage.getGraphics();
		m_isDirty= false;

		if(m_imgPreRendered != null){
//			m_imgPreRendered.disable();
//			m_imgPreRendered.dispose();

			Composite orig = objJava2d.getComposite();
			objJava2d.setComposite(AlphaComposite.Clear);
			objJava2d.fillRect(0, 0, m_recLimit.width, m_recLimit.height);
			objJava2d.setComposite(orig);
		}

		objJava2d.setColor(m_nColor);
		objJava2d.setFont(m_font);

		if(!m_isMultiline){
			preRenderLine(objJava2d, 0, (int)(m_nFontSize*2), m_stText.substring(m_nOffsetChar));
		}else{
			char[]	temp	= new char[256];
			int		cptSrc	= m_nOffsetChar;
			int		y		= (int)(m_nFontSize*2);

			while(cptSrc < m_stText.length()){

				int		nLastCptSrc		= 0;
				int		nLastCptDst		= -1;
				int		nX				= 0;
				int		cptDst			= 0;

				for(int i= 0; i < 256; ++i)
					temp[i]= 0;
				while(cptSrc < m_stText.length()){
					if(((m_stText.charAt(cptSrc) == '\\') && (m_stText.charAt(cptSrc+1) == 'n'))){
						cptSrc+=2;
						nLastCptDst		= cptDst;
						nLastCptSrc		= cptSrc;
						break;
					}else if(m_stText.charAt(cptSrc) == '\n'){
						cptSrc+=1;
						nLastCptDst		= cptDst;
						nLastCptSrc		= cptSrc;
						break;
					}

					if(m_stText.charAt(cptSrc) == ' '){
						nLastCptDst		= cptDst;
						nLastCptSrc		= cptSrc;
					}

					temp[cptDst++]= m_stText.charAt(cptSrc++);

					// put new line in last good spot
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

				if(cptSrc == m_stText.length()){
					nLastCptDst= cptDst;
				}

				String toDraw;
				toDraw= new String(temp, 0, nLastCptDst);
				preRenderLine(objJava2d, 0, y, toDraw);
				y+= m_nFontSize;
			}
		}

		m_imgPreRendered= TextureIO.newTexture(p_bufImage, false);
	}

	public boolean click()			{return false;}
	public boolean doubleClick()	{return false;}

	public boolean drag(int p_nX, int p_nY){
		if(m_isScrollable){
			setOffsetY(m_nOffsetY + p_nY);
			return true;
		}
		return false;
	}

	public void set(int p_nValue)	{set(Integer.toString(p_nValue));}
	public void set(String p_stText){
		if(m_stText.equals(p_stText))
			return;

		m_stText= p_stText;
		m_isDirty= true;
	}

	public void setOffsetY(int p_nOffsetY){
		m_nOffsetY= p_nOffsetY;

		if(m_nOffsetY > (m_imgPreRendered.getHeight() - m_recLimit.height))
			m_nOffsetY= m_imgPreRendered.getHeight() - m_recLimit.height;

		if(m_nOffsetY < 0)
			m_nOffsetY= 0;
	}

	public void set(double p_nValue, int p_nNbDigit){
		int		intValue= (int)p_nValue;
		double	decValue= p_nValue - intValue;

		decValue*= Math.pow(10, p_nNbDigit);

		String	intPart= Integer.toString(intValue);
		String	digitPart= Integer.toString((int)(decValue));

		for(int i= 0; i < p_nNbDigit-1; ++i){
			if(decValue < (10 ^ i)){
				digitPart= "0" + digitPart;
			}
		}

		set(intPart + "." + digitPart);
	}
	public void append(String p_stText){
		m_stText+= p_stText;
		m_isDirty= true;
	}

	public void manage(double p_fTimeScaleFactor) {
		super.manage(p_fTimeScaleFactor);

		if(m_isDirty){
			//Recreate a backBuffer
			int nWidth= getMetricWidth();
			int nHeight= getMetricHeight();

			preRender(new BufferedImage(nWidth, nHeight, BufferedImage.TYPE_4BYTE_ABGR));
		}
	}

	public void draw(){
		if((!isShown()) || (m_stText == null) || m_isDirty)
			return;

		App.g_theApp.orthogonalStart(App.g_CurrentDrawable);

		GL gl= App.g_CurrentGL;
		gl.glPushMatrix();

		Camera.getCur(Camera.TYPE_2D).doProjection();

		TextureCoords texCoor	= m_imgPreRendered.getImageTexCoords();
        m_imgPreRendered.enable();
        m_imgPreRendered.bind();

		gl.glEnable(GL.GL_BLEND);
		gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
    	gl.glTranslated(m_vPos[0], m_vPos[1], 0);
		gl.glColor4d(m_colorFilterRed, m_colorFilterGreen, m_colorFilterBlue, m_colorFilterAlpha);

		double nMaxX= texCoor.right() - texCoor.left();
		double nMaxY= texCoor.bottom() - texCoor.top();

		double	nSrcLeft	= texCoor.left();
		double	nSrcTop		= texCoor.top() + ((double)m_nOffsetY / (double)m_imgPreRendered.getHeight());
		double	nSrcRight	= texCoor.right();
		double	nSrcBottom	= nSrcTop + ((double)m_recLimit.height / (double)m_imgPreRendered.getHeight());

		double	nDestLeft	= m_recLimit.x;
		double	nDestTop	= m_recLimit.y;
		double	nDestRight	= nDestLeft + m_recLimit.width;
		double	nDestBottom	= nDestTop + m_recLimit.height;

		if(m_imgPreRendered.getHeight() < m_recLimit.height){
			nDestBottom= nDestTop + m_imgPreRendered.getHeight();
			nSrcBottom= 1.0;
		}

	    gl.glBegin (GL.GL_QUADS);
			gl.glTexCoord2d(nSrcLeft, nSrcBottom);
			gl.glVertex2d(nDestLeft, nDestBottom);

			gl.glTexCoord2d(nSrcRight, nSrcBottom);
			gl.glVertex2d(nDestRight, nDestBottom);

			gl.glTexCoord2d(nSrcRight, nSrcTop);
			gl.glVertex2d(nDestRight, nDestTop);

			gl.glTexCoord2d(nSrcLeft, nSrcTop);
			gl.glVertex2d(nDestLeft, nDestTop);
        gl.glEnd ();
		gl.glPopMatrix();

		//Print Debug Rect
		if(App.PRINT_DEBUG)
			drawDebug();
	}

	public void drawDebug(){
		int	nCumulLeft	= (int)m_vPos[0] + m_recLimit.x;
		int	nCumulTop	= (int)m_vPos[1] + m_recLimit.y;
		int	nCumulRight	= nCumulLeft + m_recLimit.width;
		int	nCumulBottom= nCumulTop + m_recLimit.height;

		GL gl= App.g_CurrentGL;

		gl.glPushMatrix();
		Camera.getCur(Camera.TYPE_2D).doProjection();

		gl.glColor3d(1.0, 1.0, 1.0);
		gl.glDisable(GL.GL_TEXTURE_2D);
		gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL.GL_LINE);
		gl.glBegin(GL.GL_LINE_LOOP);
			gl.glVertex2d(nCumulLeft, nCumulTop);
			gl.glVertex2d(nCumulRight, nCumulTop);
			gl.glVertex2d(nCumulRight, nCumulBottom);
			gl.glVertex2d(nCumulLeft, nCumulBottom);
		gl.glEnd();

		gl.glColor3d(0.0, 0.0, 1.0);
		gl.glBegin(GL.GL_LINE);
			switch(m_nMode){
			case MODE_LEFT:
				gl.glVertex2d(nCumulLeft, nCumulTop);
				gl.glVertex2d(nCumulLeft, nCumulBottom);
			break;
			case MODE_CENTER:
				gl.glVertex2d(nCumulLeft + ((nCumulRight-nCumulLeft)/2), nCumulTop);
				gl.glVertex2d(nCumulLeft + ((nCumulRight-nCumulLeft)/2), nCumulBottom);
			break;
			case MODE_RIGHT:
				gl.glVertex2d(nCumulRight, nCumulTop);
				gl.glVertex2d(nCumulRight, nCumulBottom);
			}
		gl.glEnd();

		gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL.GL_FILL);
		gl.glEnable(GL.GL_TEXTURE_2D);
		gl.glPopMatrix();
	}
}
