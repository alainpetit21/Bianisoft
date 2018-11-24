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
import java.util.ArrayList;
import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.DisplayMode;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;


//Standard JOGL imports
import javax.media.opengl.GL;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.GLCanvas;
import com.sun.opengl.util.Animator;
import com.sun.opengl.util.j2d.TextRenderer;

//Bianisoft imports
import java.applet.Applet;
import com.bianisoft.engine.helper.Int;
import com.bianisoft.engine.helper.Random;
import com.bianisoft.engine.manager.MngContextSwitcher;
import com.bianisoft.engine.manager.MngInput;
import com.bianisoft.engine.resmng.FontCache;


// Modifier order
/*
 public		static	abstract	synchronized	transient	final	native
 private										volatile
 protected	
*/

public class App implements GLEventListener{
	public static final boolean	PRINT_DEBUG= true;

	public static GraphicsDevice	m_objGraphDevice;
	public static DisplayMode		m_objDisplayModeOriginal;
	public static DisplayMode		m_objDisplayModeWanted;

	public static GLU				m_objGLU		= new GLU();
	public static MngInput			m_mngInput		= new MngInput();
	public static Animator			m_objAnimator;
	public static GLCanvas			m_objCanvas;
	public static Thread			g_parentThread;
	public static GLAutoDrawable	g_CurrentDrawable;
	public static GL				g_CurrentGL;
	public static App				g_theApp;
	public static int				m_nCptLoop;

	private static Frame			m_objFrame;
	private static Applet			m_objApplet;

	private MngContextSwitcher	m_objContextSwitcher;

	public ArrayList<Obj>	m_arObj			= new ArrayList<Obj>();
	public ArrayList<Int>	m_stkIdxContext	= new ArrayList<Int>();

	public TextRenderer m_objTextRenderer= new TextRenderer(FontCache.m_fontSystem);

	public Context	m_ctxCur;
	public String	m_stGameName;
	public boolean	m_isFullScreen;
	public int		m_nWidth;
	public int		m_nHeight;
	public int		m_nTileSize		= 16;
	public boolean	m_isReshapable;

	public boolean m_isDrawingOrtho;

	public long	m_lastFrameTick;
	public long	m_thisFrameTick;

//	private int m_nToChangeContext= -1;


	public App(GLCanvas p_canvas, Frame p_objFrame, int p_nWidth, int p_nHeight){
		m_objGraphDevice		= GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		m_objDisplayModeOriginal= m_objGraphDevice.getDisplayMode();

		g_theApp		= this;
		m_objCanvas		= p_canvas;
		m_objAnimator	= new Animator(m_objCanvas);
		m_objFrame		= p_objFrame;
		m_nHeight		= p_nHeight;
		m_nWidth		= p_nWidth;
		m_isFullScreen	= false;

		m_objCanvas.addGLEventListener(this);
		m_objFrame.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e){exit();}
		});

		m_objFrame.setResizable(false);
		m_objFrame.setVisible(true);
		m_objCanvas.requestFocus();
		m_objAnimator.start();
	}

	public App(Applet p_objApplet, GLCanvas p_objCanvas, Animator p_objAnimator, int p_nWidth, int p_nHeight){
		m_objGraphDevice		= GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		m_objDisplayModeOriginal= m_objGraphDevice.getDisplayMode();

		g_theApp		= this;
		m_objApplet		= p_objApplet;
		m_objCanvas		= p_objCanvas;
		m_objAnimator	= p_objAnimator;
		m_nHeight		= p_nHeight;
		m_nWidth		= p_nWidth;
		m_isFullScreen	= false;

		m_objCanvas.addGLEventListener(this);
		m_objCanvas.requestFocus();

		//Hide Cursor
		BufferedImage cursorImg= new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
		Cursor blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(cursorImg, new Point(0, 0), "blank cursor");
		m_objApplet.setCursor(blankCursor);
	}

	public App(String p_stName, int p_nWidth, int p_nHeight, boolean p_isFullscreen){
		m_objGraphDevice		= GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		m_objDisplayModeOriginal= m_objGraphDevice.getDisplayMode();

		g_theApp		= this;
		m_stGameName	= p_stName;
		m_objCanvas		= new GLCanvas();
		m_objFrame		= new Frame(p_stName);
		m_objAnimator	= new Animator(m_objCanvas);
		m_nHeight		= p_nHeight;
		m_nWidth		= p_nWidth;
		m_isFullScreen	= p_isFullscreen && m_objGraphDevice.isFullScreenSupported();

		m_objCanvas.addGLEventListener(this);
		m_objFrame.add(m_objCanvas);
		m_objFrame.setSize(m_nWidth+2, m_nHeight+32);
		m_objFrame.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e){exit();}
		});

		m_objFrame.setUndecorated(m_isFullScreen);
		m_objFrame.setResizable(false);	//for some reason it has to be true in Fullscreen, false in windowed
		m_objFrame.setVisible(true);

		if(m_isFullScreen){
			//Set FullScreen & Change DisplayMode
			try{
				m_objFrame.setSize(m_nWidth, m_nHeight); // <- and again the setSize call
				m_objGraphDevice.setFullScreenWindow(m_objFrame);

				findMatchingDisplayMode();
				m_objGraphDevice.setDisplayMode(m_objDisplayModeWanted);
			}catch(Exception e){
				m_isFullScreen= false;
				m_objGraphDevice.setDisplayMode(m_objDisplayModeOriginal);
				m_objGraphDevice.setFullScreenWindow(null);
			}
		}


		m_objCanvas.requestFocusInWindow();
		m_objAnimator.start();
		m_objFrame.setState(Frame.NORMAL);

		//Hide Cursor
		BufferedImage cursorImg= new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
		Cursor blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(cursorImg, new Point(0, 0), "blank cursor");
		m_objFrame.setCursor(blankCursor);
	}

	private void findMatchingDisplayMode(){
		DisplayMode[] arDisplayModes= m_objGraphDevice.getDisplayModes();

		for(int i= 0; i < arDisplayModes.length; ++i){
			if((arDisplayModes[i].getWidth() == m_nWidth) && (arDisplayModes[i].getHeight() == m_nHeight)){
				int nBitDepth= arDisplayModes[i].getBitDepth();

				if((nBitDepth == 8) || (nBitDepth == 15) || (nBitDepth == 16))
					continue;

				m_objDisplayModeWanted= arDisplayModes[i];
				break;
			}
		}
	}

	public void displayChanged(GLAutoDrawable gLDrawable, boolean modeChanged, boolean deviceChanged){	}

	public static Context getCurContext()		{return App.g_theApp.m_ctxCur;}
	public void setCurContext(int p_nContext){
		if(m_arObj.isEmpty())
			m_objContextSwitcher= new MngContextSwitcher(m_ctxCur, p_nContext);
		else
			m_objContextSwitcher= new MngContextSwitcher(m_ctxCur, (Context)m_arObj.get(p_nContext));
	}

	public void init(GLAutoDrawable gLDrawable){
		GL gl= gLDrawable.getGL();

		gl.glEnable(GL.GL_TEXTURE_2D);
		gl.glLineWidth(1.0f);
		gl.glPointSize(5.0f);

		//Real 3D
		gl.glShadeModel(GL.GL_SMOOTH);
		gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		gl.glClearDepth(1000.0f);
		gl.glEnable(GL.GL_DEPTH_TEST);
		gl.glDepthFunc(GL.GL_LEQUAL);
		gl.glHint(GL.GL_PERSPECTIVE_CORRECTION_HINT, GL.GL_NICEST);

		gLDrawable.addKeyListener(m_mngInput);
		gLDrawable.addMouseListener(m_mngInput);
		gLDrawable.addMouseMotionListener(m_mngInput);
		gLDrawable.addMouseWheelListener(m_mngInput);
		gLDrawable.setAutoSwapBufferMode(false);

		Random.setSeed((int)System.nanoTime());
	}

	public void addContext(Context p_ctx){
		Context pTemp= p_ctx;
		p_ctx.m_nIndex= m_arObj.size();

		m_arObj.add(pTemp);
	}

	public void pushContext(int p_nContext){
		m_stkIdxContext.add(new Int(m_ctxCur.m_nIndex));
		setCurContext(p_nContext);
	}

	public void popContext(){
		setCurContext(m_stkIdxContext.size()-1);
		m_stkIdxContext.remove(m_stkIdxContext.size()-1);
	}

    public static void exit(){
		if(get().m_isFullScreen){
			m_objGraphDevice.setDisplayMode(m_objDisplayModeOriginal);
		}
		
		m_objAnimator.stop();
		m_objFrame.dispose();

		if(g_parentThread != null)
			g_parentThread.interrupt();

		System.exit(0);
    }

    public static void sleep(int p_nMS){
		try{
			Thread.sleep(p_nMS);
		}catch(InterruptedException e){
		}
	}

	public void orthogonalStart(GLAutoDrawable gLDrawable){
		if(m_isDrawingOrtho)
			return;

		GL gl= gLDrawable.getGL();

		gl.glMatrixMode(GL.GL_PROJECTION);
		gl.glPushMatrix();
		gl.glLoadIdentity();

		gl.glOrtho(0.0f, m_nWidth, m_nHeight, 0.0f, -10.0f, 1000.0f);

		gl.glMatrixMode(GL.GL_MODELVIEW);
		gl.glLoadIdentity();
		m_isDrawingOrtho= true;
	}

	public void orthogonalEnd(GLAutoDrawable gLDrawable){
		if(!m_isDrawingOrtho)
			return;

		GL gl= gLDrawable.getGL();

		gl.glMatrixMode(GL.GL_PROJECTION);
		gl.glPopMatrix();

		gl.glMatrixMode(GL.GL_MODELVIEW);
		gl.glLoadIdentity();
		m_isDrawingOrtho= false;
	}

	public void reshape(GLAutoDrawable gLDrawable, int p_nX, int p_nY, int p_nWidth, int p_nHeight){
		GL gl= gLDrawable.getGL();

		if(p_nHeight <= 0)
			p_nHeight= 1;

		if(!m_isReshapable){
			p_nWidth= m_nWidth;
			p_nHeight= m_nHeight;
		}

		float h= (float)p_nWidth / (float)p_nHeight;
		gl.glViewport(0, 0, p_nWidth, p_nHeight);
		gl.glMatrixMode(GL.GL_PROJECTION);
		gl.glLoadIdentity();
		m_objGLU.gluPerspective(60.0f, h, 0.1, 1000.0);

		gl.glMatrixMode(GL.GL_MODELVIEW);
		gl.glLoadIdentity();
		gLDrawable.swapBuffers();
	}

	public void display(GLAutoDrawable glDrawable){
		GL gl= g_CurrentGL= glDrawable.getGL();
		g_CurrentDrawable= glDrawable;

		m_thisFrameTick= System.nanoTime();

		long	nbMsPerFrame= m_thisFrameTick - m_lastFrameTick;
		double	nratioMove= ((double)nbMsPerFrame)/16666667.0;

		if(m_objContextSwitcher != null){
			m_mngInput.flush();
			
			if(m_objContextSwitcher.manage() == true)
				m_objContextSwitcher= null;

			m_lastFrameTick= m_thisFrameTick;
			m_nCptLoop+= 1;
			return;
		}

		if(m_ctxCur == null)
			return;
		
		Camera.getCur(Camera2D.TYPE_2D).manage(nratioMove);
		Camera.getCur(Camera3D.TYPE_3D).manage(nratioMove);
		
		m_ctxCur.keyManage(m_mngInput);
		m_ctxCur.manage(nratioMove);
//		m_ctxCur.manage(1.0);
		m_mngInput.manage();

		//Initiate Draw
		gl.glClear(GL.GL_COLOR_BUFFER_BIT);
		gl.glClear(GL.GL_DEPTH_BUFFER_BIT);
		gl.glLoadIdentity();

		//Draw 2D
		gl.glEnable(GL.GL_BLEND);
		gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
		m_ctxCur.draw();
		if(m_isFullScreen)
			drawFullscreenPadding();
		if(PRINT_DEBUG)
			drawDebug();

		gl.glEnd();
		glDrawable.swapBuffers();

		m_lastFrameTick= m_thisFrameTick;
		m_nCptLoop+= 1;
	}

	public void drawFullscreenPadding()	{	}
	public void drawFullscreenPadding(int p_nBBWidth, int p_nBBHeight){
		App.g_theApp.orthogonalStart(App.g_CurrentDrawable);

		GL gl= App.g_CurrentGL;
		gl.glPushMatrix();

		Camera.getCur(Camera.TYPE_2D).doProjection();

		gl.glColor4d(0, 0, 0, 1);
		gl.glDisable(GL.GL_TEXTURE_2D);
		gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL.GL_FILL);

		gl.glBegin(GL.GL_QUADS);
			gl.glVertex2d(p_nBBWidth/2, -m_nHeight/2);
			gl.glVertex2d(p_nBBWidth/2, m_nHeight/2);
			gl.glVertex2d(m_nWidth/2, m_nHeight/2);
			gl.glVertex2d(m_nWidth/2, -m_nHeight/2);
		gl.glEnd();
		gl.glBegin(GL.GL_QUADS);
			gl.glVertex2d(-m_nWidth/2, -m_nHeight/2);
			gl.glVertex2d(-m_nWidth/2, m_nHeight/2);
			gl.glVertex2d(-p_nBBWidth/2, m_nHeight/2);
			gl.glVertex2d(-p_nBBWidth/2, -m_nHeight/2);
		gl.glEnd();
		gl.glBegin(GL.GL_QUADS);
			gl.glVertex2d(-m_nWidth/2, p_nBBHeight/2);
			gl.glVertex2d(-m_nWidth/2, m_nHeight/2);
			gl.glVertex2d(m_nWidth/2, m_nHeight/2);
			gl.glVertex2d(m_nWidth/2, p_nBBHeight/2);
		gl.glEnd();
		gl.glBegin(GL.GL_QUADS);
			gl.glVertex2d(-m_nWidth/2, -m_nHeight/2);
			gl.glVertex2d(-m_nWidth/2, -p_nBBHeight/2);
			gl.glVertex2d(m_nWidth/2, -p_nBBHeight/2);
			gl.glVertex2d(m_nWidth/2, -m_nHeight/2);
		gl.glEnd();

//		gl.glBegin(GL.GL_QUADS);
//			gl.glVertex2d(-10, -10);
//			gl.glVertex2d(-10, 10);
//			gl.glVertex2d(10, 10);
//			gl.glVertex2d(10, -10);
//		gl.glEnd();

		gl.glPopMatrix();
		gl.glEnable(GL.GL_TEXTURE_2D);
		gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL.GL_FILL);

	}

	public void drawDebug(){
		//Print FPS
		double fps= 1000000000.0/((m_thisFrameTick - m_lastFrameTick)+1);

		if(FontCache.m_fontSystem != null){
			m_objTextRenderer.beginRendering(App.g_CurrentDrawable.getWidth(), App.g_CurrentDrawable.getHeight());
				m_objTextRenderer.setColor(1.0f, 1.0f, 1.0f, 1.0f);
				m_objTextRenderer.draw(Double.toString(fps), 0, 0);
			m_objTextRenderer.endRendering();
		}
	}

	public static App get(){
		return App.g_theApp;
	}
}
