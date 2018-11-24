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
package com.bianisoft.engine.backgrounds;


//Standard JOGL imports
import javax.media.opengl.GL;
import com.sun.opengl.util.texture.Texture;
import com.sun.opengl.util.texture.TextureCoords;

//Bianisoft imports
import com.bianisoft.engine.App;
import com.bianisoft.engine.Camera;
import com.bianisoft.engine.Drawable;
import com.bianisoft.engine.resmng.ImageCache;


public class Background extends Drawable{
	public static final int TYPE_NORMAL	= 0;
	public static final int TYPE_LOOPED	= 1;
	public static final int TYPE_TILED	= 2;

	public String	m_stResImage;
	public Texture	m_image;
	public double	m_nWidth;
	public double	m_nHeight;


	public static Background create(int p_nType, String p_stResImage, String p_stMapFile){
		if(p_nType == TYPE_LOOPED)		return null;
		else if(p_nType == TYPE_TILED)	return new BackgroundTiled(p_stResImage, p_stMapFile);
		else							return new Background(p_stResImage);
	}


	protected Background(String p_stResImage){
		super(IDCLASS_Background);
		setSubClassID(TYPE_NORMAL);

		m_stResImage= p_stResImage;

		if(m_stResImage != null){
			int pos=  m_stResImage.lastIndexOf("/");
			if(pos == -1)
				pos=  m_stResImage.lastIndexOf("\\");

			String idText= m_stResImage.substring(pos+1);
			pos= idText.lastIndexOf(".png");
			if(pos == -1)
				pos= idText.lastIndexOf(".tga");
			
			setTextID(idText.substring(0, pos));
		}
	}

	public boolean isLoaded()	{return m_image != null;}
	public Texture getImage()	{return m_image;}

	public void load(){
		m_image		= ImageCache.loadImage(m_stResImage);
		m_nWidth	= m_image.getImageWidth();
		m_nHeight	= m_image.getImageHeight();
	}

	public void draw(){
		if(!isShown() || !isLoaded())
			return;

		App.g_theApp.orthogonalStart(App.g_CurrentDrawable);

		GL gl= App.g_CurrentGL;
		gl.glPushMatrix();
		
		TextureCoords texCoor	= m_image.getImageTexCoords();

		m_image.bind();
		gl.glEnable(GL.GL_BLEND);
		gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);

		Camera.getCur(Camera.TYPE_2D).doProjection();

		gl.glTranslated(m_vPos[0], m_vPos[1], 0);
		gl.glColor4d(m_colorFilterRed, m_colorFilterGreen, m_colorFilterBlue, m_colorFilterAlpha);

		gl.glBegin(GL.GL_QUADS);
			gl.glTexCoord2d(texCoor.left(), texCoor.top());
			gl.glVertex2d((-(m_nWidth/2)) * m_fZoom, (-(m_nHeight/2)) * m_fZoom);

			gl.glTexCoord2d(texCoor.left(), texCoor.bottom());
			gl.glVertex2d((-(m_nWidth/2)) * m_fZoom, (m_nHeight/2) * m_fZoom);

			gl.glTexCoord2d(texCoor.right(), texCoor.bottom());
			gl.glVertex2d((m_nWidth/2) * m_fZoom, (m_nHeight/2) * m_fZoom);
			
			gl.glTexCoord2d(texCoor.right(), texCoor.top());
			gl.glVertex2d((m_nWidth/2) * m_fZoom, (-(m_nHeight/2)) * m_fZoom);
		gl.glEnd();
		gl.glPopMatrix();
	}
}
