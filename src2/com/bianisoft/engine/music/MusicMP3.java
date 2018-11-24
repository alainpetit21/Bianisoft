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
import javazoom.jl.player.advanced.AdvancedPlayer;
import javazoom.jl.player.advanced.PlaybackEvent;
import javazoom.jl.player.advanced.PlaybackListener;
import com.bianisoft.engine.Obj;




public class MusicMP3 extends Music{
	public class MyInfoListener implements PlaybackListener{
		public void playbackStarted(PlaybackEvent evt)	{	}
		public boolean playbackFinished(PlaybackEvent evt){
			m_isPlaying= false;

			if(m_isLooping){
				m_player.seek(0);
				return true;
			}

			return false;
		}
	};


	public MyInfoListener	m_lst= new MyInfoListener();
	public AdvancedPlayer	m_player;
	public boolean			m_isPlaying= false;
	public boolean			m_isLooping= true;


	public MusicMP3(String p_stResSong){
		super(IDCLASS_Music);
		setSubClassID(TYPE_MP3);

		m_stResSong	= p_stResSong;
	}

	public void load(){
		try{
			m_stResSong= Obj.fixResFilename(m_stResSong);
			URL modurl = Thread.currentThread().getContextClassLoader().getResource(m_stResSong);

			m_player= new AdvancedPlayer(modurl.getFile());
			m_player.setPlayBackListener(m_lst);
		}catch(Exception e){
			System.out.print("Exception\n");
		}
	}

	public void play(){
		if(m_isPlaying)
			return;

		m_isPlaying= true;

		new Thread(){
			public void run(){
				try{
					m_player.play();
				}catch (Exception e){
					throw new RuntimeException(e.getMessage());
				}
			}
		}.start();
	}

	public void stop()			{m_player.stop();}
	public boolean isPlaying()	{return m_isPlaying;}
}
