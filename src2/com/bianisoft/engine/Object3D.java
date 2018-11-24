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


//Standard JOGL imports
import com.sun.opengl.util.texture.Texture;
import com.sun.opengl.util.texture.TextureCoords;
import javax.media.opengl.GL;
import com.bianisoft.engine.resmng.ImageCache;


public class Object3D extends Drawable{
	public class Vertex{
		public float x, y, z;

		public Vertex()	{}
		public Vertex(float p_x, float p_y, float p_z)	{x= p_x; y= p_y; z= p_z;}
	}

	public class TexCoord{
		public float u, v;

		public TexCoord()	{}
		public TexCoord(float p_u, float p_v)	{u= p_u; v= p_v;}
	}

	public class Color{
		public float r, g, b;

		public Color()	{}
		public Color(float p_r, float p_g, float p_b)	{r= p_r; g= p_g; b= p_b;}
	}

	public class Triangle{
		public Vertex	ver1, ver2, ver3;
		public Color	col1, col2, col3;
		public TexCoord	tex1, tex2, tex3;


		public Triangle()	{}
		public Triangle(Vertex p_tri1, Vertex p_tri2, Vertex p_tri3){
			ver1= p_tri1; ver2= p_tri2; ver3= p_tri3;
		}

		public Triangle(Vertex p_tri1, Vertex p_tri2, Vertex p_tri3,
						Color p_col1, Color p_col2, Color p_col3){
			ver1= p_tri1; ver2= p_tri2; ver3= p_tri3;
			col1= p_col1; col2= p_col2; col3= p_col3;
		}
		public Triangle(Vertex p_tri1, Vertex p_tri2, Vertex p_tri3,
						Color p_col1, Color p_col2, Color p_col3,
						TexCoord p_tex1, TexCoord p_tex2, TexCoord p_tex3){
			ver1= p_tri1; ver2= p_tri2; ver3= p_tri3;
			col1= p_col1; col2= p_col2; col3= p_col3;
			tex1= p_tex1; tex2= p_tex2; tex3= p_tex3;
		}
	}

	private String		m_stResTexture;
	private Vertex[]	m_arVertices;
	private TexCoord[]	m_arTexCoords;
	private Color[]		m_arColors;
	private Triangle[]	m_arTriangles;
	private Texture		m_image;


	public Object3D(){
		super(IDCLASS_Object3D);
	}

	public Object3D(Object3D p_obj3D){
		this();

		m_stResTexture	= p_obj3D.m_stResTexture;
		m_arVertices	= p_obj3D.m_arVertices;
		m_arTexCoords	= p_obj3D.m_arTexCoords;
		m_arColors		= p_obj3D.m_arColors;
		m_arTriangles	= p_obj3D.m_arTriangles;
		m_image			= p_obj3D.m_image;
	}

	public void load(){
		if(m_stResTexture == null)
			return;
		
		m_image		= ImageCache.loadImage(m_stResTexture);

		//Adjust arTexCoord depending on padding of image
		if(m_arTexCoords != null){
			TextureCoords texCoor	= m_image.getImageTexCoords();
			double multFactorX= texCoor.right();
			double multFactorY= texCoor.bottom();

			for(TexCoord tex : m_arTexCoords){
				tex.u*= multFactorX;
				tex.v*= multFactorY;
			}
		}
	}

	public Vertex[] createVertexBuffer(int p_nSize){
		m_arVertices= new Vertex[p_nSize];
		return m_arVertices;
	}

	public TexCoord[] createTexCoordBuffer(int p_nSize){
		m_arTexCoords= new TexCoord[p_nSize];
		return m_arTexCoords;
	}

	public Color[] createColorBuffer(int p_nSize){
		m_arColors= new Color[p_nSize];
		return m_arColors;
	}

	public Triangle[] createTriangleBuffer(int p_nSize){
		m_arTriangles= new Triangle[p_nSize];
		return m_arTriangles;
	}

	public Vertex[]		getVertexBuffer()	{return m_arVertices;}
	public TexCoord[]	getTexCoordBuffer()	{return m_arTexCoords;}
	public Color[]		getColorBuffer()	{return m_arColors;}
	public Triangle[]	getTriangleBuffer()	{return m_arTriangles;}
	public boolean		isLoaded()			{return m_image != null;}
	public Texture		getImage()			{return m_image;}

	public void			setVertexBuffer(Vertex[] p_arVertex)		{m_arVertices	= p_arVertex;}
	public void			setTexCoordBuffer(TexCoord[] p_arTexCoord)	{m_arTexCoords	= p_arTexCoord;}
	public void			setColorBuffer(Color[] p_arColor)			{m_arColors		= p_arColor;}
	public void			setTriangleBuffer(Triangle[] p_arTriangle)	{m_arTriangles	= p_arTriangle;}
	public void			setTextureFileName(String p_stTexture)		{m_stResTexture	= p_stTexture;}

	public void manage(double p_fTimeScaleFactor){
		super.manage(p_fTimeScaleFactor);
	}

	public void draw(){
		if(!isShown())
			return;

		GL gl= App.g_CurrentGL;

		App.g_theApp.orthogonalEnd(App.g_CurrentDrawable);
		gl.glPushMatrix();

		Camera.getCur(Camera.TYPE_3D).doProjection();

		gl.glEnable(GL.GL_BLEND);
		gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
		gl.glTranslated(m_vPos[0], m_vPos[1], -m_vPos[2]);
		gl.glRotated(m_vAngle[0], 1.0, 0.0, 0.0);
		gl.glRotated(m_vAngle[1], 0.0, 1.0, 0.0);
		gl.glRotated(m_vAngle[2], 0.0, 0.0, 1.0);
		gl.glColor4d(m_colorFilterRed, m_colorFilterGreen, m_colorFilterBlue, m_colorFilterAlpha);

		//Has a "indexed Buffer"
		if(m_arTriangles != null){
			//Has both Color adjustment & TexCoord & Vertex
			if((m_arColors != null) && (m_arTexCoords != null)){

			//Has TexCoord & Vertex
			}else if(m_arTexCoords != null){
				gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL.GL_FILL);
				gl.glEnable(GL.GL_TEXTURE_2D);
				m_image.bind();
				gl.glBegin(GL.GL_TRIANGLES);
					for(int i= 0; i < m_arTriangles.length; ++i){
						Triangle tri= m_arTriangles[i];

						gl.glTexCoord2f(tri.tex1.u, tri.tex1.v);	gl.glVertex3f(tri.ver1.x, tri.ver1.y, tri.ver1.z);
						gl.glTexCoord2f(tri.tex2.u, tri.tex2.v);	gl.glVertex3f(tri.ver2.x, tri.ver2.y, tri.ver2.z);
						gl.glTexCoord2f(tri.tex3.u, tri.tex3.v);	gl.glVertex3f(tri.ver3.x, tri.ver3.y, tri.ver3.z);
					}
				gl.glEnd();
			//Has Color & Vertex
			}else if(m_arColors != null){

			//Has only Vertex
			}else{

			}
		}else if(m_arVertices != null){
			//Has both Color adjustment & TexCoord & Vertex
			if((m_arColors != null) && (m_arTexCoords != null)){

			//Has TexCoord & Vertex
			}else if(m_arTexCoords != null){
				gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL.GL_FILL);
				gl.glEnable(GL.GL_TEXTURE_2D);
				m_image.bind();
				gl.glBegin(GL.GL_TRIANGLES);
					for(int i= 0; i < m_arVertices.length; ++i){
						Vertex ver= m_arVertices[i];
						TexCoord tex= m_arTexCoords[i];
						gl.glTexCoord2f(tex.u, tex.v);
						gl.glVertex3f(ver.x, ver.y, ver.z);
					}
				gl.glEnd();

			//Has Color & Vertex
			}else if(m_arColors != null){
				gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL.GL_FILL);
				gl.glDisable(GL.GL_TEXTURE_2D);
				gl.glBegin(GL.GL_TRIANGLES);
					for(int i= 0; i < m_arVertices.length; ++i){
						Vertex ver= m_arVertices[i];

						Color col= m_arColors[i];
						col.r= (float)(col.r * m_colorFilterRed);
						col.g= (float)(col.g * m_colorFilterGreen);
						col.b= (float)(col.b * m_colorFilterBlue);

						gl.glColor4f(col.r, col.g, col.b, (float)m_colorFilterAlpha);
						gl.glVertex3f(ver.x, ver.y, ver.z);
					}
				gl.glEnd();
				gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL.GL_FILL);
				gl.glEnable(GL.GL_TEXTURE_2D);
			//Has only Vertex
			}else{
				gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL.GL_FILL);
				gl.glDisable(GL.GL_TEXTURE_2D);
				gl.glBegin(GL.GL_TRIANGLES);
					for(Vertex ver : m_arVertices)
						gl.glVertex3f(ver.x, ver.y, ver.z);
				gl.glEnd();
				gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL.GL_FILL);
				gl.glEnable(GL.GL_TEXTURE_2D);
			}
		}
		gl.glPopMatrix();
	}
}
