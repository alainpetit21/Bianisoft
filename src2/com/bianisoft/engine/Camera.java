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


//Standard Java imports
import java.awt.Point;
import java.awt.Rectangle;

//Standard JOGL imports
import javax.media.opengl.GL;


public abstract class Camera extends PhysObj{
	public static final int TYPE_2D= 0;
	public static final int TYPE_3D= 1;

	public static Camera	m_nCur2D= null;
	public static Camera	m_nCur3D= null;

	public double	m_fZoom;


	public static Camera createCamera(int p_nType){
		if(p_nType == TYPE_2D)		return new Camera2D();
		else if(p_nType == TYPE_3D)	return new Camera3D();
		else						return null;
	}

	public static Camera getCur(int p_nType){
		if(p_nType == TYPE_2D)		return m_nCur2D;
		else if(p_nType == TYPE_3D)	return m_nCur3D;
		else						return null;
	}

	public Camera(){
		super(IDCLASS_Camera);
		m_fZoom= 1.0;
	}

	public abstract void setCur();
	public abstract void doProjection();
	public abstract double doUnprojectionX(double p_fValue);
	public abstract double doUnprojectionY(double p_fValue);
	public abstract boolean isOnScreen(double p_nX1, double p_nY1);

	public boolean isOnScreen(double p_nX1, double p_nY1, double p_nX2, double p_nY2){
		return (isOnScreen(p_nX2, p_nY2) || isOnScreen(p_nX1, p_nY1));
	}
}


final class Camera2D extends Camera{
	public Camera2D(){
		super();
		setSubClassID(TYPE_2D);
	}

	public void setCur(){m_nCur2D= this;}

	public void doProjection(){
		GL gl= App.g_CurrentGL;

		gl.glTranslated((App.g_theApp.m_nWidth>>1) + m_vPos[0], (App.g_theApp.m_nHeight>>1) + m_vPos[1],  m_vPos[2]);
		gl.glScaled(m_fZoom, m_fZoom, 0);
		gl.glRotated(m_vAngle[0], 1.0, 0.0, 0.0);
		gl.glRotated(m_vAngle[1], 0.0, 1.0, 0.0);
		gl.glRotated(m_vAngle[2], 0.0, 0.0, 1.0);
	}

	public double doUnprojectionX(double p_fValue){
		return (p_fValue - (App.g_theApp.m_nWidth>>1) - m_vPos[0])/m_fZoom;
	}
	
	public double doUnprojectionY(double p_fValue){
		return (p_fValue - (App.g_theApp.m_nHeight>>1) - m_vPos[1])/m_fZoom;
	}

	public boolean isOnScreen(double p_nX, double p_nY){
		int width= App.g_theApp.m_nWidth;
		int height= App.g_theApp.m_nHeight;
		
		Rectangle rect= new Rectangle((int)m_vPos[0] - (width>>1), (int)m_vPos[1] - (height>>1), width, height);

		return rect.contains(new Point((int)p_nX, (int)p_nY));
	}
}


final class Camera3D extends Camera{
	public Camera3D(){
		super();
		setSubClassID(TYPE_3D);
	}

	public void setCur(){m_nCur3D= this;}
	
	public void doProjection(){
		GL gl= App.g_CurrentGL;

		gl.glRotated(m_vAngle[2], 0.0, 0.0, 1.0);
		gl.glRotated(m_vAngle[0], 1.0, 0.0, 0.0);
		gl.glRotated(m_vAngle[1], 0.0, 1.0, 0.0);
		gl.glTranslated(-m_vPos[0], m_vPos[1],  m_vPos[2]);
	}
	
	public double doUnprojectionX(double p_fValue){
		return p_fValue - m_vPos[0];
	}

	public double doUnprojectionY(double p_fValue){
		return p_fValue - m_vPos[1];
	}

	public boolean isOnScreen(double p_nX, double p_nY){
		//TODO : Camera3D.isOnScreen(x, y);
		return true;
	}
}
