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

//Standard Java imports
import java.awt.Rectangle;
import java.util.ArrayList;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;

//Standard JOGL imports
import javax.media.opengl.GL;
import com.sun.opengl.util.texture.Texture;
import com.sun.opengl.util.texture.TextureCoords;

//Bianisoft imports
import com.bianisoft.engine.App;
import com.bianisoft.engine.Camera;
import com.bianisoft.engine.Drawable;
import com.bianisoft.engine.resmng.ImageCache;


public class Sprite extends Drawable{
	public static final int TYPE_LOOP_ANIMATION	= 0;
	public static final int TYPE_FIXED			= 1;
	public static final int TYPE_ONE_TIMER		= 2;
	public static final int TYPE_STANDARD		= 3;


	public static Sprite create(int p_nType, String p_stResImage, int p_nNbFrame, double p_fSpeed){
		if(p_nType == TYPE_LOOP_ANIMATION)	return new SpriteLoopAnimation(p_stResImage, p_nNbFrame, p_fSpeed);
		else if(p_nType == TYPE_FIXED)		return new SpriteLoopAnimation(p_stResImage, p_nNbFrame, 0);
		else if(p_nType == TYPE_ONE_TIMER)	return new SpriteOneTimer(p_stResImage, p_nNbFrame, p_fSpeed);
		else if(p_nType == TYPE_STANDARD)	return new Sprite(p_stResImage);

		return null;
	}


	public class Frame{
		public Rectangle	m_rectSource;
		public int[]		m_vHotSpot= {0, 0};

		public Frame(int p_nX, int p_nY, int p_nWidth, int p_nHeight){
			m_rectSource= new Rectangle(p_nX, p_nY, p_nWidth, p_nHeight);
			m_vHotSpot[0]= p_nWidth / 2;
			m_vHotSpot[1]= p_nHeight / 2;
		}
	}


	public class State{
		public ArrayList<Frame>	m_vecFrames= new ArrayList<Frame>();

		public String	m_stName;
		public int		m_nIndex;
		public int		m_nMaxFrames;
		public int		m_nSpeed;
		public int		m_nCurFrame;

		public State(State p_refState){
			m_nMaxFrames= p_refState.m_nMaxFrames;
			m_nCurFrame= p_refState.m_nCurFrame;
			m_vecFrames= p_refState.m_vecFrames;
			m_stName= p_refState.m_stName;
			m_nIndex= p_refState.m_nIndex;
			m_nSpeed= p_refState.m_nSpeed;
		}

		public State(String p_stID, int p_nNbFrame, double p_fSpeed){
			m_nMaxFrames	= p_nNbFrame;
			m_nSpeed		= (int)(p_fSpeed*32);
			m_stName		= p_stID;
		}

		public void setAnimationSpeed(double p_fSpeed)	{m_nSpeed= (int)(p_fSpeed*32);}
		public double getAnimationSpeed()				{return (double)m_nSpeed / 32.0;}
	}


	public ArrayList<State>		m_vecStates		= new ArrayList<State>();
	public ArrayList<Method>	m_vecFctManage	= new ArrayList<Method>();
	public int					m_nCurState;

	public Texture	m_image;
	public String	m_stResImage;
	public double	m_nWidthFrame;
	public double	m_nHeightFrame;
	public double	m_nWidthImage;
	public double	m_nHeightImage;


	public Sprite()	{this((String)null);}

	public Sprite(String p_stResImage){
		super(IDCLASS_Sprite);
		m_stResImage= p_stResImage;
	}

	public Sprite(Sprite p_refSprite){
		super(p_refSprite);

		m_nHeightImage= p_refSprite.m_nHeightImage;
		m_nHeightFrame= p_refSprite.m_nHeightFrame;
		m_nWidthImage= p_refSprite.m_nWidthImage;
		m_nWidthFrame= p_refSprite.m_nWidthFrame;
		m_stResImage= p_refSprite.m_stResImage;
		m_nCurState= p_refSprite.m_nCurState;
		m_image= p_refSprite.m_image;
		
		for(State refState : p_refSprite.m_vecStates)
			m_vecStates.add(new State(refState));
	}

	public void addState(Sprite.State p_sprState){
		p_sprState.m_nIndex= m_vecStates.size();
		m_vecStates.add(p_sprState);
	}

	public void load(){
		m_image= ImageCache.loadImage(m_stResImage);
		m_nWidthImage	= m_image.getImageWidth();
		m_nHeightImage	= m_image.getImageHeight();

		//Do calculation
		//Find Highest Frame Count within all states
		int nHighestFrameCount= 0;
		for(int i= 0; i < m_vecStates.size(); ++i){
			State stateCur= m_vecStates.get(i);

			if(stateCur.m_nMaxFrames > nHighestFrameCount)
				nHighestFrameCount= stateCur.m_nMaxFrames;
		}

		//Using Widht of image, and highest mNbFrame find each frame width
		m_nWidthFrame= m_nWidthImage / nHighestFrameCount;
		m_nHeightFrame= m_nHeightImage / m_vecStates.size();
		for(int i= 0; i < m_vecStates.size(); ++i){
			State stateCur = m_vecStates.get(i);
			for(int j = 0; j < stateCur.m_nMaxFrames; ++j){
				stateCur.m_vecFrames.add(new Sprite.Frame((int) (j*m_nWidthFrame), (int) (i*m_nHeightFrame), (int) m_nWidthFrame, (int) m_nHeightFrame));
			}
		}

		registerManageFunctions();
	}

	public void registerManageFunctions(){
		//Set All rectangles of all frames of all states
		for(int i= 0; i < m_vecStates.size(); ++i){
			try{
				Class[] parTypes = new Class[1];
				parTypes[0] = Double.TYPE;
				m_vecFctManage.add(getClass().getMethod("onManage", parTypes));
			}catch(NoSuchMethodException ex){
				ex.printStackTrace();
			}catch(SecurityException ex){
				ex.printStackTrace();
			}
		}
	}

	public void registerManageFunction(int p_nNumber, String p_stFunctionName){
		try{
			Class[] parTypes = new Class[1];
			parTypes[0] = Double.TYPE;
			m_vecFctManage.set(p_nNumber, getClass().getMethod(p_stFunctionName, parTypes));
		}catch(NoSuchMethodException ex){
		}catch(SecurityException ex){
		}
	}

	public void onManage(double p_tick)	{	}
	public boolean isLoaded()	{return m_image != null;}
	public Texture getImage()	{return m_image;}
	public int getCurState()	{return m_nCurState;}

	public int getWidh(){
		State stateCur= m_vecStates.get(m_nCurState);
		Frame frameCur= stateCur.m_vecFrames.get(stateCur.m_nCurFrame>>5);
		return frameCur.m_rectSource.width;
	}

	public int getHeight(){
		State stateCur= m_vecStates.get(m_nCurState);
		Frame frameCur= stateCur.m_vecFrames.get(stateCur.m_nCurFrame>>5);
		return frameCur.m_rectSource.height;
	}

	public boolean pointIsOver(double p_nX, double p_nY){
		p_nX-= getPosX();
		p_nY-= getPosY();

		p_nX+= getWidh() / 2;
		p_nY+= getHeight() / 2;

		return (getRect().contains(p_nX, p_nY));
	}

	public Rectangle getRect(){
		State stateCur= m_vecStates.get(m_nCurState);
		Frame frameCur= stateCur.m_vecFrames.get(stateCur.m_nCurFrame>>5);
		return frameCur.m_rectSource;
	}

	public int getHotSpotX(){
		State stateCur= (State)m_vecStates.get(m_nCurState);
		Frame frameCur= (Frame)stateCur.m_vecFrames.get(stateCur.m_nCurFrame>>5);
		return frameCur.m_vHotSpot[0];
	}

	public int getHotSpotY(){
		State stateCur= m_vecStates.get(m_nCurState);
		Frame frameCur= stateCur.m_vecFrames.get(stateCur.m_nCurFrame>>5);
		return frameCur.m_vHotSpot[1];
	}

	public void setCurState(int p_nIdx){
		m_nCurState= p_nIdx;
		m_vecStates.get(m_nCurState).m_nCurFrame= 0;
	}

	public void setCurFrame(int p_nIdx){
		m_vecStates.get(m_nCurState).m_nCurFrame= p_nIdx<<5;
	}

	public int getCurFrame(){
		return m_vecStates.get(m_nCurState).m_nCurFrame>>5;
	}

	public int getMaxFrame(){
		return m_vecStates.get(m_nCurState).m_nMaxFrames;
	}

	public void setHotSpot(int p_nX, int p_nY){
		for(int i= 0; i < m_vecStates.size(); ++i){
			State stateCur= (State)m_vecStates.get(i);

			for(int j= 0; j < stateCur.m_nMaxFrames; ++j){
				Frame frameCur= (Frame)stateCur.m_vecFrames.get(j);

				frameCur.m_vHotSpot[0]= p_nX;
				frameCur.m_vHotSpot[1]= p_nY;
			}
		}
	}

	public void setAnimationSpeed(double p_fSpeed){
		State stateCur		= m_vecStates.get(m_nCurState);
		stateCur.m_nSpeed	= (int)(p_fSpeed*32);
	}

	public void manage(double p_fTimeScaleFactor){
		super.manage(p_fTimeScaleFactor);

		State stateCur= m_vecStates.get(m_nCurState);
		stateCur.m_nCurFrame+= (stateCur.m_nSpeed*p_fTimeScaleFactor);

		try{
			if(m_vecFctManage.size() != 0){
				Method fct= m_vecFctManage.get(m_nCurState);
				fct.invoke(this, p_fTimeScaleFactor);
			}
		}catch(IllegalAccessException ex){
			ex.printStackTrace();
		}catch(IllegalArgumentException ex){
			ex.printStackTrace();
		}catch(InvocationTargetException ex){
			ex.printStackTrace();
		}

		if((stateCur.m_nCurFrame>>5) >= stateCur.m_nMaxFrames){
			stateCur.m_nCurFrame= 0;
		}
	}

	public void draw(){
		if(!isShown() || !isLoaded())
			return;

		App.g_theApp.orthogonalStart(App.g_CurrentDrawable);

		GL gl= App.g_CurrentGL;
		gl.glPushMatrix();

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

		Camera.getCur(Camera.TYPE_2D).doProjection();

		//This Obj
		gl.glTranslated(m_vPos[0], m_vPos[1], 0);
		gl.glColor4d(m_colorFilterRed, m_colorFilterGreen, m_colorFilterBlue, m_colorFilterAlpha);

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

		//Print Debug Collision
		if(App.PRINT_DEBUG && m_isCollidable)
			drawDebug();

		gl.glPopMatrix();
	}

	public void drawDebug(){
		GL gl= App.g_CurrentGL;

		gl.glColor3d(1.0, 0.0, 0.0);
		gl.glDisable(GL.GL_TEXTURE_2D);
		gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL.GL_LINE);
		gl.glBegin(GL.GL_LINE_LOOP);
			for(double a= 0; a < 6.29; a+= 0.629){
			   double x= m_fRadius * (Math.cos(a));
			   double y= m_fRadius * (Math.sin(a));
			   gl.glVertex2d(x, y);
			}
		gl.glEnd();
		gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL.GL_FILL);
		gl.glEnable(GL.GL_TEXTURE_2D);
	}

	public String toString() {
		return "Sprite @ " + (int)m_vPos[0] + ";"+ (int)m_vPos[1] + ";"+ (int)m_vPos[2] + ";";
	}
}
