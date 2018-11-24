/*
 * 11/19/04		1.0 moved to LGPL. 
 *-----------------------------------------------------------------------
 *   This program is free software; you can redistribute it and/or modify
 *   it under the terms of the GNU Library General Public License as published
 *   by the Free Software Foundation; either version 2 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Library General Public License for more details.
 *
 *   You should have received a copy of the GNU Library General Public
 *   License along with this program; if not, write to the Free Software
 *   Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 *----------------------------------------------------------------------
 */

package javazoom.jl.player.advanced;

import javazoom.jl.decoder.Bitstream;
import javazoom.jl.decoder.Decoder;
import javazoom.jl.decoder.Header;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.decoder.SampleBuffer;
import javazoom.jl.player.AudioDevice;
import javazoom.jl.player.FactoryRegistry;

public class AdvancedPlayer{
	private Bitstream	m_bitstream;
	private Decoder		m_decoder;
	private AudioDevice m_audio;
	private boolean		m_bUserStopped= false;

	private PlaybackListener	m_listener;

	public AdvancedPlayer(String p_stFilename) throws JavaLayerException{
		this(p_stFilename, null);
	}

	public AdvancedPlayer(String p_stFilename, AudioDevice device) throws JavaLayerException{
		m_bitstream= new Bitstream(p_stFilename);

		if(device != null)
			m_audio= device;
		else 
			m_audio= FactoryRegistry.systemRegistry().createAudioDevice();

		m_audio.open(m_decoder= new Decoder());
	}

	protected boolean decodeFrame() throws JavaLayerException{
		try{
			if(m_audio == null)
				return false;

			Header h= m_bitstream.readFrame();

			if(h == null)
				return false;

			SampleBuffer output= (SampleBuffer)m_decoder.decodeFrame(h, m_bitstream);

			m_audio.write(output.getBuffer(), 0, output.getBufferLength());

			m_bitstream.closeFrame();
		}catch (RuntimeException ex){
			throw new JavaLayerException("Exception decoding audio frame", ex);
		}

		return true;
	}

	public void play() throws JavaLayerException{
		boolean ret= true;

		m_bUserStopped= false;

		if(m_listener != null)
			m_listener.playbackStarted(createEvent(PlaybackEvent.STARTED));

		do{
			while(ret){
				ret= decodeFrame();

				synchronized(this){
					if(m_bUserStopped)
						break;
				}
			}

			if(m_listener != null)
				if(!m_bUserStopped)
					ret= m_listener.playbackFinished(createEvent(m_audio, PlaybackEvent.STOPPED));
			
		}while(ret);

		if(m_audio != null)
			m_audio.flush();
	}

	public synchronized void stop(){
		m_bUserStopped= true;
	}

	public void seek(int p_nNbFrames){
		m_bitstream.seek(p_nNbFrames);
	}

	private PlaybackEvent createEvent(int id){
		return createEvent(m_audio, id);
	}

	private PlaybackEvent createEvent(AudioDevice dev, int id){
		return new PlaybackEvent(this, id, dev.getPosition());
	}

	public void setPlayBackListener(PlaybackListener listener){
		this.m_listener= listener;
	}

	public PlaybackListener getPlayBackListener(){
		return m_listener;
	}
}