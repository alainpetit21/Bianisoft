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
import java.net.URL;
import java.io.IOException;

//Standard Java Sound Imports
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;


public class Sound extends PhysObj{
	private class MyThread implements Runnable{
		public void run(){
			try{
				m_isPlaying= true;
	  			SourceDataLine output_line= AudioSystem.getSourceDataLine(m_audioFormat);
				output_line.open();
				output_line.start();

				output_line.write(m_arBuffer, 0, m_arBuffer.length);

				output_line.drain();
				output_line.close();
				m_isPlaying= false;
			}catch(IllegalStateException lue){
				//ignore, oh well we just won't play that sound then
			}catch(LineUnavailableException lue){
				//ignore, oh well we just won't play that sound then
			}
		}
	}

	private Thread	m_thrPlay;
	private byte[]	m_arBuffer;

	public String		m_stResSound;
	public AudioFormat	m_audioFormat;
	public boolean		m_isLoaded= false;
	public boolean		m_isPlaying= false;

	public Sound(String p_stResSound){
		super(IDCLASS_Sound);
		m_stResSound= p_stResSound;
	}

	public void load(){
		if(m_isLoaded)
			return;

		m_stResSound= Obj.fixResFilename(m_stResSound);
		URL modurl= Thread.currentThread().getContextClassLoader().getResource(m_stResSound);

		try{
			AudioInputStream	soundStream= AudioSystem.getAudioInputStream(modurl);
			
			m_audioFormat= soundStream.getFormat();
			m_arBuffer= new byte[(int)((m_audioFormat.getSampleSizeInBits()>>3) * soundStream.getFrameLength())];

			int offsetRead= 0;
			while(offsetRead < m_arBuffer.length)
				offsetRead+= soundStream.read(m_arBuffer, offsetRead, m_arBuffer.length - offsetRead);

		}catch(UnsupportedAudioFileException e1){
			System.out.print("UnsupportedAudioFileException\n");
		}catch(IOException e2){
			System.out.print("IOException\n");
		}

		m_isLoaded= true;
	}

	public void play(){
		m_thrPlay= new Thread(new MyThread());
		m_thrPlay.start();
	}

	public boolean isPlaying()	{return m_isPlaying;}
}
