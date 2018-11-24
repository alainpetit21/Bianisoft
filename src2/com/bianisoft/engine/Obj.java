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


public class Obj{												//
	public static final int IDCLASS_Obj				= 0x0001;	// 0b00000000000001
	public static final int IDCLASS_PhysObj			= 0x0003;	// 0b00000000000011
	public static final int IDCLASS_Sprite			= 0x0007;	// 0b00000000000111
	public static final int IDCLASS_Button			= 0x000F;	// 0b00000000001111
	public static final int IDCLASS_Background		= 0x0013;	// 0b00000000010011
	public static final int IDCLASS_Countainer		= 0x0041;	// 0b00000001000001
	public static final int IDCLASS_Context			= 0x00C1;	// 0b00000011000001
	public static final int IDCLASS_Label			= 0x0103;	// 0b00000100000011
	public static final int IDCLASS_Object3D		= 0x0203;	// 0b00001000000011
	public static final int IDCLASS_Sound			= 0x0403;	// 0b00010000000011
	public static final int IDCLASS_Music			= 0x0803;	// 0b00100000000011
	public static final int IDCLASS_Camera			= 0x1003;	// 0b01000000000011
	public static final int IDCLASS_GAME			= 0x2000;	// 0b10000000000000
//	CLSID_CSceneEngine				= 5,
//	CLSID_CMaze3D					= 16,

	private int			m_idClass		= IDCLASS_Obj;
	private String		m_idClassText	= "";
	private int			m_idSubClass	= 0;

	private String		m_idText		= "";
	public Countainer	m_objParent		= null;

	public Obj()						{	}
	public Obj(int p_idClass)			{setClassID(p_idClass);}
	public Obj(String p_idClassText)	{setClassID(p_idClassText);}
	public Obj(Obj p_refObj){
		m_objParent	= p_refObj.m_objParent;
		m_idText	= p_refObj.m_idText;
		setClassID(p_refObj.m_idClass);
	}

	public void setClassID(int p_idClass, String p_idClassText){
		m_idClassText= p_idClassText;
		m_idClass= p_idClass;
	}

	public void setClassID(String p_idClassText){
		m_idClassText= p_idClassText;
		if(m_idClassText.equalsIgnoreCase("IDCLASS_Obj"))				m_idClass= IDCLASS_Obj;
		if(m_idClassText.equalsIgnoreCase("IDCLASS_PhysObj"))			m_idClass= IDCLASS_PhysObj;
		if(m_idClassText.equalsIgnoreCase("IDCLASS_Sprite"))			m_idClass= IDCLASS_Sprite;
		if(m_idClassText.equalsIgnoreCase("IDCLASS_Button"))			m_idClass= IDCLASS_Button;
		if(m_idClassText.equalsIgnoreCase("IDCLASS_Background"))		m_idClass= IDCLASS_Background;
		if(m_idClassText.equalsIgnoreCase("IDCLASS_Countainer"))		m_idClass= IDCLASS_Countainer;
		if(m_idClassText.equalsIgnoreCase("IDCLASS_Context"))			m_idClass= IDCLASS_Context;
		if(m_idClassText.equalsIgnoreCase("IDCLASS_Label"))				m_idClass= IDCLASS_Label;
		if(m_idClassText.equalsIgnoreCase("IDCLASS_Object3D"))			m_idClass= IDCLASS_Object3D;
		if(m_idClassText.equalsIgnoreCase("IDCLASS_Sound"))				m_idClass= IDCLASS_Sound;
		if(m_idClassText.equalsIgnoreCase("IDCLASS_Music"))				m_idClass= IDCLASS_Music;
		if(m_idClassText.equalsIgnoreCase("IDCLASS_Camera"))			m_idClass= IDCLASS_Camera;
	}

	public void setClassID(int p_idClass){
		m_idClass= p_idClass;
		switch(p_idClass){
			case IDCLASS_Obj:				m_idClassText= "IDCLASS_Obj";				break;
			case IDCLASS_PhysObj:			m_idClassText= "IDCLASS_PhysObj";			break;
			case IDCLASS_Sprite:			m_idClassText= "IDCLASS_Sprite";			break;
			case IDCLASS_Button:			m_idClassText= "IDCLASS_Button";			break;
			case IDCLASS_Background:		m_idClassText= "IDCLASS_Background";		break;
			case IDCLASS_Countainer:		m_idClassText= "IDCLASS_Countainer";		break;
			case IDCLASS_Context:			m_idClassText= "IDCLASS_Context";			break;
			case IDCLASS_Label:				m_idClassText= "IDCLASS_Label";				break;
			case IDCLASS_Object3D:			m_idClassText= "IDCLASS_Object3D";			break;
			case IDCLASS_Sound:				m_idClassText= "IDCLASS_Sound";				break;
			case IDCLASS_Music:				m_idClassText= "IDCLASS_Music";				break;
			case IDCLASS_Camera:			m_idClassText= "IDCLASS_Camera";			break;
		}
	}

	public boolean isKindOf(int p_idClass)		{return ((m_idClass & p_idClass) == p_idClass);}

	public int getClassID()						{return m_idClass;}
	public String getTextClassID()				{return m_idClassText;}

	public void setSubClassID(int p_idClass)	{m_idSubClass= p_idClass;}
	public int getSubClassID()					{return m_idSubClass;}

	public void setTextID(String p_idClassText)	{m_idText= p_idClassText;}
	public String getTextID()					{return m_idText;}

	public static String fixResFilename(String p_stFileName){

		p_stFileName= p_stFileName.replace("\\", "/");
		if(p_stFileName.charAt(0) == '/')
			p_stFileName= p_stFileName.substring(1);

		return p_stFileName;
	}
}
