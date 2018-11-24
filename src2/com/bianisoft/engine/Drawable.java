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


public class Drawable extends PhysObj{
	public double	m_colorFilterRed	=1;
	public double	m_colorFilterGreen	=1;
	public double	m_colorFilterBlue	=1;
	public double	m_colorFilterAlpha	=1;
	public double	m_fZoom				=1;
	public boolean	m_isShown			= true;


	public Drawable()			{super(IDCLASS_PhysObj);}
	public Drawable(int p_nType)	{super(p_nType);}

	public Drawable(Drawable p_refDrawableObj){
		super(p_refDrawableObj);

		m_colorFilterAlpha= p_refDrawableObj.m_colorFilterAlpha;
		m_colorFilterGreen= p_refDrawableObj.m_colorFilterGreen;
		m_colorFilterBlue= p_refDrawableObj.m_colorFilterBlue;
		m_colorFilterRed= p_refDrawableObj.m_colorFilterRed;
		m_isShown= p_refDrawableObj.m_isShown;
		m_fZoom= p_refDrawableObj.m_fZoom;
	}

	public void show()					{m_isShown= true;}
	public void hide()					{m_isShown= false;}
	public void show(boolean p_toShow)	{if(p_toShow) show(); else hide();}
	public boolean isShown()			{return m_isShown;}

	public void setFilterColor(double p_fRedComponent, double p_fGreenComponent, double p_fBlueComponent){
		setFilterColor(p_fRedComponent, p_fGreenComponent, p_fBlueComponent, getFilterAlpha());
	}

	public void setFilterColor(double p_fRedComponent, double p_fGreenComponent, double p_fBlueComponent, double p_fAlphaComponent){
		m_colorFilterRed= p_fRedComponent;
		m_colorFilterGreen= p_fGreenComponent;
		m_colorFilterBlue= p_fBlueComponent;
		m_colorFilterAlpha= p_fAlphaComponent;
	}

	public void setFilterRed(double p_fRedComponent)	{m_colorFilterRed= p_fRedComponent;}
	public void setFilterGreen(double p_fGreenComponent){m_colorFilterGreen= p_fGreenComponent;}
	public void setFilterBlue(double p_fBlueComponent)	{m_colorFilterBlue= p_fBlueComponent;}
	public void setFilterAlpha(double p_fAlphaComponent){m_colorFilterAlpha= p_fAlphaComponent;}

	public double getFilterRed()	{return m_colorFilterRed;}
	public double getFilterGreen()	{return m_colorFilterGreen;}
	public double getFilterBlue()	{return m_colorFilterBlue;}
	public double getFilterAlpha()	{return m_colorFilterAlpha;}

	public double getZoom()				{return m_fZoom;}
	public void setZoom(double p_fZoom)	{m_fZoom= p_fZoom;}

	public void draw()	{	}
}
