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


import java.net.URL;
import com.bianisoft.engine.Obj;
import blargg.javagme.VGMPlayer;


public class MusicEMU extends Music{
	public VGMPlayer	m_player= new VGMPlayer(44100);
	public int			m_nTrack= 0;


	public MusicEMU(String p_stResSong, int p_nTrack){
		super(IDCLASS_Music);
		setSubClassID(TYPE_EMU);

		m_stResSong	= p_stResSong;
		m_nTrack	= p_nTrack;
	}

	public void load(){
		try{
			m_stResSong= Obj.fixResFilename(m_stResSong);
			URL modurl = Thread.currentThread().getContextClassLoader().getResource(m_stResSong);

			m_player.loadFile(modurl.getFile(), ".");
		}catch(Exception e){
			System.out.print("Exception\n");
		}
	}

	public void play(){
		try {
			m_player.startTrack(m_nTrack, 200000);
		}catch(Exception e){
			System.out.print("Exception\n");
		}
	}

	public void stop(){
		try {
			m_player.stop();
		}catch(Exception e){
			System.out.print("Exception\n");
		}
	}

	public boolean isPlaying()	{return m_player.isPlaying();}
}
