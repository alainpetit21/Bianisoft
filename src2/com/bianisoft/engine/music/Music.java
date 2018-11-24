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
package com.bianisoft.engine.music;

//Bianisoft Music Imports
import com.bianisoft.engine.Obj;


public abstract class Music extends Obj{
	public static final int TYPE_OGG= 0;
	public static final int TYPE_MP3= 1;
	public static final int TYPE_EMU= 2;
	public static final int TYPE_MOD= 3;

	public String	m_stResSong;


	public Music(int p_ntype)	{super(p_ntype);}

	public abstract void load();
	public abstract void play();
	public abstract void stop();
	public abstract boolean isPlaying();
}




