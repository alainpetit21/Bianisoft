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


//Standard Java imports
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

//Bianisoft imports
import com.bianisoft.engine.App;


public final class MngInput implements KeyListener, MouseListener, MouseMotionListener, MouseWheelListener{
	public static final int K_A= 0x0001;	public static final int K_B= 0x0002;
	public static final int K_C= 0x0003;	public static final int K_D= 0x0004;
	public static final int K_E= 0x0005;	public static final int K_F= 0x0006;
	public static final int K_G= 0x0007;	public static final int K_H= 0x0008;
	public static final int K_I= 0x0009;	public static final int K_J= 0x000A;
	public static final int K_K= 0x000B;	public static final int K_L= 0x000C;
	public static final int K_M= 0x000D;	public static final int K_N= 0x000E;
	public static final int K_O= 0x000F;	public static final int K_P= 0x0010;
	public static final int K_Q= 0x0011;	public static final int K_R= 0x0012;
	public static final int K_S= 0x0013;	public static final int K_T= 0x0014;
	public static final int K_U= 0x0015;	public static final int K_V= 0x0016;
	public static final int K_W= 0x0017;	public static final int K_X= 0x0018;
	public static final int K_Y= 0x0019;	public static final int K_Z= 0x001A;

	public static final int K_0= 0x001B;	public static final int K_1= 0x001C;
	public static final int K_2= 0x001D;	public static final int K_3= 0x001E;
	public static final int K_4= 0x001F;	public static final int K_5= 0x0020;
	public static final int K_6= 0x0021;	public static final int K_7= 0x0022;
	public static final int K_8= 0x0023;	public static final int K_9= 0x0024;

	public static final int K_MINUS				= 0x0025;
	public static final int K_EQUALS			= 0x0026;
	public static final int K_EXCLAMATION		= 0x0027;
	public static final int K_AT				= 0x0028;
	public static final int K_SHARP				= 0x0029;
	public static final int K_DOLLAR			= 0x002A;
	public static final int K_PERCENT			= 0x002B;
	public static final int K_CIRCUMFLEX		= 0x002C;
	public static final int K_AMPERSAND			= 0x002D;
	public static final int K_ASTERISK			= 0x002E;
	public static final int K_LEFTPARENTHESIS	= 0x002F;
	public static final int K_RIGHTPARENTHESIS	= 0x0030;
	public static final int K_UNDERSCORE		= 0x0031;
	public static final int K_PLUS				= 0x0032;

	public static final int K_SLASH= 0x0033;
	public static final int K_GRAVE= 0x0034;
	public static final int K_TILDE= 0x0035;

	public static final int K_ENTER		= 0x0036;
	public static final int K_SPACE		= 0x0037;
	public static final int K_ARROWLEFT	= 0x0038;
	public static final int K_ARROWUP	= 0x0039;
	public static final int K_ARROWRIGHT= 0x003A;
	public static final int K_ARROWDOWN	= 0x003B;
	public static final int K_TAB		= 0x003C;
	public static final int K_DELETE	= 0x003D;
	public static final int K_BACKSPACE	= 0x003E;
	public static final int K_LAST		= 0x003F;


	public static final int J_RIGHT	= 0x0001;
	public static final int J_DOWN	= 0x0002;
	public static final int J_LEFT	= 0x0004;
	public static final int J_UP	= 0x0008;
	public static final int J_A		= 0x0010;
	public static final int J_B		= 0x0020;
	public static final int J_X		= 0x0040;
	public static final int J_Y		= 0x0080;
	public static final int J_L		= 0x0100;
	public static final int J_R		= 0x0200;
	public static final int J_SELECT= 0x0400;
	public static final int J_START	= 0x0800;
	public static final int M_LEFT	= 0x0001;
	public static final int M_MIDDLE= 0x0002;
	public static final int M_RIGHT	= 0x0004;

	public boolean[]	m_bKeyboardBack= new boolean[K_LAST];
	public boolean[]	m_bKeyboard= new boolean[K_LAST];

	public int		m_nJoystick;
	public int		m_nJoystickBack;

	public int		m_nMouse;
	public int		m_nMouseBack;

	public int		m_nMouseX;
	public int		m_nMouseY;
	public int		m_nMouseZ;
	public int		m_nMouseBackX;
	public int		m_nMouseBackY;
	public int		m_nMouseBackZ;
	public int		m_nMouseDeltaX;
	public int		m_nMouseDeltaY;
	public int		m_nMouseDeltaZ;


	public int		m_nMouseRawX;
	public int		m_nMouseRawY;


	public int convertVirtualJoystickCode(int p_nInput){
		switch(p_nInput){
		case KeyEvent.VK_UP:	return J_UP;
		case KeyEvent.VK_RIGHT:	return J_RIGHT;
		case KeyEvent.VK_DOWN:	return J_DOWN;
		case KeyEvent.VK_LEFT:	return J_LEFT;
		case KeyEvent.VK_A:		return J_A;
		case KeyEvent.VK_B:		return J_B;
		case KeyEvent.VK_X:		return J_X;
		case KeyEvent.VK_Y:		return J_Y;
		case KeyEvent.VK_L:		return J_L;
		case KeyEvent.VK_R:		return J_R;
		case KeyEvent.VK_SPACE:	return J_SELECT;
		case KeyEvent.VK_ENTER:	return J_START;
		}
		return 0xFFFFFFFF;
	}

	public int convertVirtualKeyboardCode(int p_nInput){

		if((p_nInput >= 65) && (p_nInput <= 90)){
			return (p_nInput-65)+K_A;
		}else if((p_nInput >= 48) && (p_nInput <= 57)){
			return (p_nInput-48)+K_0;
		}else if((p_nInput >= 0x25) && (p_nInput <= 0x38)){
			return (p_nInput-0x25)+K_ARROWLEFT;
		}else{
			switch(p_nInput){
			case KeyEvent.VK_MINUS:				return K_MINUS;
			case KeyEvent.VK_EQUALS:			return K_EQUALS;
			case KeyEvent.VK_EXCLAMATION_MARK:	return K_EXCLAMATION;
			case KeyEvent.VK_AT:				return K_AT;
			}
		}
		return 0xFFFFFFFF;
	}

	public int convertMouseCode(int p_nInput){
		switch(p_nInput){
		case MouseEvent.BUTTON1:	return M_LEFT;
		case MouseEvent.BUTTON2:	return M_MIDDLE;
		case MouseEvent.BUTTON3:	return M_RIGHT;
		}
		return 0xFFFFFFFF;
	}
	public void keyTyped(KeyEvent p_event)		{	}
	public void mouseClicked(MouseEvent p_event){	}
	public void mouseEntered(MouseEvent p_event){	}
	public void mouseExited(MouseEvent p_event)	{	}

	public void mouseMoved(MouseEvent p_event){
		m_nMouseX= p_event.getX() - (App.g_theApp.m_nWidth/2) - 1;
		m_nMouseY= p_event.getY() - (App.g_theApp.m_nHeight/2) - 27;

		m_nMouseRawX= p_event.getX();
		m_nMouseRawY= p_event.getY();
	}

	public void mouseDragged(MouseEvent p_event){
		m_nMouseX= p_event.getX() - (App.g_theApp.m_nWidth/2) - 1;
		m_nMouseY= p_event.getY() - (App.g_theApp.m_nHeight/2) - 27;
		m_nMouseRawX= p_event.getX();
		m_nMouseRawY= p_event.getY();
	}

	public void mousePressed(MouseEvent p_event){
		int value= convertMouseCode(p_event.getButton());

		if(value != 0xFFFFFFFF)
			m_nMouse|= value;
	}

	public void mouseReleased(MouseEvent p_event){
		int value= convertMouseCode(p_event.getButton());

		if(value != 0xFFFFFFFF)
			m_nMouse&= ~value;
	}

	public void keyPressed(KeyEvent p_event){
		int value= convertVirtualJoystickCode(p_event.getKeyCode());

		if(value != 0xFFFFFFFF)
			m_nJoystick|= value;


		value= convertVirtualKeyboardCode(p_event.getKeyCode());
		if(value != 0xFFFFFFFF)
			m_bKeyboard[value]= true;
	}

	public void keyReleased(KeyEvent p_event){
		int value= convertVirtualJoystickCode(p_event.getKeyCode());

		if(value != 0xFFFFFFFF)
			m_nJoystick&= ~value;

		value= convertVirtualKeyboardCode(p_event.getKeyCode());
		if(value != 0xFFFFFFFF)
			m_bKeyboard[value]= false;
	}

	public void mouseWheelMoved(MouseWheelEvent p_event){
		m_nMouseZ+= p_event.getWheelRotation();
	}

	public void flush(){
		m_nJoystick		= 0;
		m_nJoystickBack	= 0;
		m_nMouse	= 0;
		m_nMouseBack= 0;

		for(int i= 0; i < K_LAST; ++i)
			m_bKeyboard[i]= m_bKeyboardBack[i]= false;
	}

	public void manage(){
		m_nJoystickBack= m_nJoystick;
		m_nMouseBack= m_nMouse;

		m_nMouseDeltaX= -(m_nMouseX - m_nMouseBackX);
		m_nMouseDeltaY= m_nMouseY - m_nMouseBackY;
		m_nMouseDeltaZ= m_nMouseZ - m_nMouseBackZ;

		m_nMouseBackX= m_nMouseX;
		m_nMouseBackY= m_nMouseY;
		m_nMouseBackZ= m_nMouseZ;

		for(int i= 0; i < K_LAST; ++i)
			m_bKeyboardBack[i]= m_bKeyboard[i];
	}

	public boolean isJoystickDown(int p_nKeyCode){
		return (m_nJoystick&p_nKeyCode)==p_nKeyCode;
	}

	public boolean isJoystickClicked(int p_nKeyCode){
		return ((m_nJoystick&p_nKeyCode)!=p_nKeyCode) && ((m_nJoystickBack&p_nKeyCode)==p_nKeyCode);
	}
	
	public boolean isMouseDown(int p_nKeyCode){
		return (m_nMouse&p_nKeyCode)==p_nKeyCode;
	}

	public boolean isMouseClicked(int p_nKeyCode){
		return ((m_nMouse&p_nKeyCode)!=p_nKeyCode) && ((m_nMouseBack&p_nKeyCode)==p_nKeyCode);
	}

	public boolean isKeyboardDown(int p_nKeyCode){
		return m_bKeyboard[p_nKeyCode];
	}

	public boolean isKeyboardClicked(int p_nKeyCode){
		return !m_bKeyboard[p_nKeyCode] && m_bKeyboardBack[p_nKeyCode];
	}
}
