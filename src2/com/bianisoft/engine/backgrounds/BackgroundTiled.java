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


//Standard Java imports
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.net.URL;

//Standard JOGL imports
import javax.media.opengl.GL;
import com.sun.opengl.util.texture.TextureCoords;

//Bianisoft imports
import com.bianisoft.engine.App;
import com.bianisoft.engine.Camera;
import com.bianisoft.engine.Obj;


public final class BackgroundTiled extends Background{
	String	m_stMapFile;
	int		m_nTileSize;
	int		m_nbTilesInBank;
	int		m_nTileMapWidth;
	int		m_nTileMapHeight;
	int		m_nbTileBankX;
	int		m_nbTileBankY;
	double	m_nWidthBank;
	double	m_nHeightBank;
	int[][]	m_map;


	public BackgroundTiled(String p_stResImage, String p_stMapFile){
		super(p_stResImage);
		setSubClassID(TYPE_TILED);

		m_stMapFile= p_stMapFile;
	}

	public void load(){
		super.load();

		//String preparation manipulation
		m_stMapFile= Obj.fixResFilename(m_stMapFile);
		URL url= Thread.currentThread().getContextClassLoader().getResource(m_stMapFile);

		try{
			FileInputStream file= new FileInputStream(url.getFile());
			DataInputStream dis	= new DataInputStream(file);

			m_nTileSize		= dis.readInt();
			m_nbTilesInBank	= dis.readInt();
			m_nTileMapWidth	= dis.readInt();
			m_nTileMapHeight= dis.readInt();
			m_nWidth	= m_nTileMapWidth * m_nTileSize;
			m_nHeight	= m_nTileMapHeight * m_nTileSize;

			m_nbTileBankX= (m_image.getImageWidth() / m_nTileSize);
			m_nbTileBankY= (int)(((double)m_nbTilesInBank / (double)m_nbTileBankX) + 0.999999);

			m_map= new int[m_nTileMapWidth][m_nTileMapHeight];

			for(int j= 0; j < m_nTileMapHeight; ++j){
				for(int i= 0; i < m_nTileMapWidth; ++i){
					m_map[i][j]= dis.readInt();
				}
			}

		}catch(Exception e){
			System.out.print(e);
		}

		m_vPos[0]= 0;
		m_vPos[1]= 0;
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

//		gl.glTranslated(m_vPos[0], m_vPos[1], 0);
		gl.glTranslated(0, 0, 0);
		gl.glColor4d(m_colorFilterRed, m_colorFilterGreen, m_colorFilterBlue, 1.0);

		int startPosX= (int) (m_vPos[0] - (m_nWidth/2));
		int startPosY= (int) (m_vPos[1] - (m_nHeight/2));

		Camera cam= Camera.getCur(Camera.TYPE_2D);
		int posCamX1= (int)(cam.m_vPos[0] - (App.g_theApp.m_nWidth>>1));
		int posCamX2= (int)(cam.m_vPos[0] + (App.g_theApp.m_nWidth>>1));
		int camMapX1= ((posCamX1 - startPosX) / m_nTileSize) - 1;
		int camMapX2= ((posCamX2 - startPosX) / m_nTileSize) + 1;

		int posCamY1= (int)(cam.m_vPos[1] - (App.g_theApp.m_nHeight>>1));
		int posCamY2= (int)(cam.m_vPos[1] + (App.g_theApp.m_nHeight>>1));
		int camMapY1= ((posCamY1 - startPosY) / m_nTileSize) - 1;
		int camMapY2= ((posCamY2 - startPosY) / m_nTileSize) + 1;

		for(int i= camMapX1; i < camMapX2; ++i){
			for(int j= camMapY1; j < camMapY2; ++j){
				double dstX1= startPosX + (i * m_nTileSize);
				double dstX2= startPosX + ((i+1) * m_nTileSize);
				double dstY1= startPosY + (j * m_nTileSize);
				double dstY2= startPosY + ((j+1) * m_nTileSize);

				int nIdxX= i;
				int nIdxY= j;

				while(nIdxX < 0)	nIdxX+= m_nTileMapWidth;
				while(nIdxY < 0)	nIdxY+= m_nTileMapHeight;
				while(nIdxX >= m_nTileMapWidth)		nIdxX-= m_nTileMapWidth;
				while(nIdxY >= m_nTileMapHeight)	nIdxY-= m_nTileMapHeight;

				int nTileID= m_map[nIdxX][nIdxY];

				int nTileOffsetX= nTileID % m_nbTileBankX;
				int nTileOffsetY= nTileID / m_nbTileBankX;

				double left= texCoor.left();
				double right= texCoor.right();
				double top= texCoor.top();
				double bottom= texCoor.bottom();
				double width= right - left;
				double height= bottom - top;
				double widthTile= width / m_nbTileBankX;
				double heightTile= height / m_nbTileBankY;

				double srcX1= left + (nTileOffsetX * widthTile);
				double srcX2= left + ((nTileOffsetX+1) * widthTile);
				double srcY1= top + (nTileOffsetY * heightTile);
				double srcY2= top + ((nTileOffsetY+1) * heightTile);

				gl.glBegin(GL.GL_QUADS);
					gl.glTexCoord2d(srcX1, srcY1);
					gl.glVertex2d(dstX1, dstY1);

					gl.glTexCoord2d(srcX1, srcY2);
					gl.glVertex2d(dstX1, dstY2);

					gl.glTexCoord2d(srcX2, srcY2);
					gl.glVertex2d(dstX2, dstY2);

					gl.glTexCoord2d(srcX2, srcY1);
					gl.glVertex2d(dstX2, dstY1);
				gl.glEnd();
			}
		}

		gl.glPopMatrix();
	}
}
