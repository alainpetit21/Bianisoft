//---
//Copyright (c) 2010, Martin Cameron
//All rights reserved.
//
// Licenced under the term of New BSD - 2 Clauses Licences
//---
package mumart.micromod.mod;


import mumart.micromod.replay.Replay;


/*
	java protracker replay (c)2011 mumart@gmail.com
*/
public class Micromod implements Replay{
	public static final String VERSION= "20110801a (c)2011 mumart@gmail.com";


	private Module m_objModule;
	private int[] m_bufRamp;
	private Channel[] m_arChannels;
	private int m_nSamplingRate, m_nTickLength, m_nRampLength, m_nRampRate;
	private int m_SeqPos, m_nBreakSeqPos, m_nRow, m_nNextRow, m_nTick;
	private int m_nSpeed, m_PlCount, m_nPlChannel;
	private boolean m_isInterpolate;


	public Micromod(Module p_objModule, int p_nSamplingRate, boolean p_isInterpolate){
		m_objModule		= p_objModule;
		m_nSamplingRate	= p_nSamplingRate;
		m_isInterpolate	= p_isInterpolate;

		if(p_nSamplingRate < 16000){
			System.out.print("---ERROR---\nUnsupported sampling rate!\n");
			return;
		}

		m_nRampLength= 256;
		while((m_nRampLength * 1024) > p_nSamplingRate)
			m_nRampLength/= 2;

		m_bufRamp= new int[m_nRampLength * 2];
		m_nRampRate= 256 / m_nRampLength;
		m_arChannels= new Channel[p_objModule.m_nNbChannels];
		setSequencePos(0);
	}

	public String getVersion() {
		return VERSION;
	}

	public int getSamplingRate() {
		return m_nSamplingRate;
	}

	public int getMixBufferLength() {
		return ((m_nSamplingRate * 5)/ 32) + (m_nRampLength * 2);
	}

	public String getString(int p_nIndex){
		if(p_nIndex == 0)
			return m_objModule.m_stSongName;

		if(p_nIndex < 0 || p_nIndex > m_objModule.m_nNBInstruments)
			return null;

		return m_objModule.m_arInstruments[p_nIndex].m_stName;
	}

	public void setSequencePos(int p_nPos){
		if(p_nPos >= m_objModule.m_nSequenceLength)
			p_nPos= 0;

		m_nBreakSeqPos= p_nPos;
		m_nNextRow= 0;
		m_nTick= 1;
		m_nSpeed= 6;

		setTempo(125);
		
		m_PlCount= m_nPlChannel = -1;

		for(int i= 0; i < m_objModule.m_nNbChannels; i++)
			m_arChannels[i]= new Channel(m_objModule, i, m_nSamplingRate);

		for(int i= 0; i < (m_nRampLength * 2); i++)
			m_bufRamp[i] = 0;

		tick();
	}

	public int calculateSongDuration(){
		boolean isSongEnd= false;
		int		nDuration= 0;

		setSequencePos( 0 );

		while(!isSongEnd){
			nDuration+= m_nTickLength;
			isSongEnd= tick();
		}

		setSequencePos(0);

		return nDuration;
	}

	public int seek(int p_nSamplePos){
		int current_pos= 0;
		
		setSequencePos(0);

		while((p_nSamplePos - current_pos) >= m_nTickLength){
			for(int i= 0; i < m_objModule.m_nNbChannels; i++)
				m_arChannels[i].updateSampleIdx(m_nTickLength);

			current_pos+= m_nTickLength;
			tick();
		}

		return current_pos;
	}

	/*
		Generate audio.
		The number of samples placed into output_buf is returned.
		The output buffer length must be at least that returned by getMixBufferLength().
		A "sample" is a pair of 16-bit integer amplitudes, one for each of the stereo channels.
	*/
	public int getAudio(int[] p_bufOutput){

		// Clear output buffer.
		int out_idx= 0;
		int out_ep1= m_nTickLength + (m_nRampLength << 1);
		while(out_idx < out_ep1 ) 
			p_bufOutput[ out_idx++ ] = 0;

		// Resample.
		for(int i= 0; i < m_objModule.m_nNbChannels; i++){
			Channel chan= m_arChannels[i];

			chan.resample(p_bufOutput, 0, m_nTickLength + m_nRampLength, m_isInterpolate);
			chan.updateSampleIdx(m_nTickLength);
		}

		volumeRamp(p_bufOutput);
		tick();

		return m_nTickLength;
	}

	private void setTempo(int p_nTempo){
		m_nTickLength= ((m_nSamplingRate * 5) / (p_nTempo * 2)) & -2;
	}

	private void volumeRamp(int[] p_bufMix){
		int a1, a2, s1, s2, offset = 0;

		for(a1= 0; a1 < 256; a1+= m_nRampRate){
			a2= 256 - a1;
			
			s1= p_bufMix[offset] * a1;
			s2= m_bufRamp[offset] * a2;
			p_bufMix[offset++]= s1 + (s2 >> 8);

			s1=  p_bufMix[offset] * a1;
			s2= m_bufRamp[offset] * a2;
			p_bufMix[offset++]= s1 + (s2 >> 8);
		}

		System.arraycopy(p_bufMix, m_nTickLength << 1, m_bufRamp, 0, offset);
	}

	private boolean tick(){
		boolean isSongEnd= false;

		if(--m_nTick <= 0){
			m_nTick= m_nSpeed;
			isSongEnd= row();
		}else{
			for(int i= 0; i < m_objModule.m_nNbChannels; i++)
				m_arChannels[i].tick();
		}

		return isSongEnd;
	}

	private boolean row(){
		boolean isSongEnd= false;

		if(m_nBreakSeqPos >= 0){
			if(m_nBreakSeqPos >= m_objModule.m_nSequenceLength)
				m_nBreakSeqPos= m_nNextRow= 0;

			if(m_nBreakSeqPos <= m_SeqPos) 
				isSongEnd= true;

			m_SeqPos= m_nBreakSeqPos;

			for(int i= 0; i < m_objModule.m_nNbChannels; i++)
				m_arChannels[i].m_nPlRow= 0;

			m_nBreakSeqPos= -1;
		}

		m_nRow= m_nNextRow;
		m_nNextRow= m_nRow + 1;

		if(m_nNextRow >= 64){
			m_nBreakSeqPos= m_SeqPos + 1;
			m_nNextRow= 0;
		}

		int nPatOffset= ((m_objModule.m_bufSequence[m_SeqPos] * 64) + m_nRow) * m_objModule.m_nNbChannels * 4;
		for(int i= 0; i < m_objModule.m_nNbChannels; i++){
			Channel channel= m_arChannels[i];

			int key= (m_objModule.m_bufPatterns[nPatOffset] & 0xF) << 8;
			key = key | (m_objModule.m_bufPatterns[nPatOffset + 1] & 0xFF);

			int ins = (m_objModule.m_bufPatterns[nPatOffset + 2] & 0xF0) >> 4;
			ins = ins | (m_objModule.m_bufPatterns[nPatOffset] & 0x10);

			int effect= m_objModule.m_bufPatterns[nPatOffset + 2] & 0x0F;
			int param= m_objModule.m_bufPatterns[nPatOffset + 3] & 0xFF;

			nPatOffset += 4;
			if(effect == 0xE){
				effect= 0x10 | (param >> 4);
				param &= 0xF;
			}

			if(effect == 0 && param > 0) 
				effect= 0xE;

			channel.row(key, ins, effect, param);

			switch(effect){
				case 0xB: /* Pattern Jump.*/
					if(m_PlCount < 0){
						m_nBreakSeqPos= param;
						m_nNextRow= 0;
					}
				break;
				case 0xD: /* Pattern Break.*/
					if(m_PlCount < 0){
						m_nBreakSeqPos= m_SeqPos + 1;
						m_nNextRow= (param >> 4) * 10 + (param & 0xF);
						
						if(m_nNextRow >= 64)
							m_nNextRow= 0;
					}
				break;
				case 0xF: /* Set Speed.*/
					if(param > 0){
						if(param < 32)
							m_nTick= m_nSpeed= param;
						else 
							setTempo(param);
					}
				break;
				case 0x16: /* Pattern Loop.*/
					if(param == 0) /* Set loop marker on this channel. */
						channel.m_nPlRow= m_nRow;

					if(channel.m_nPlRow < m_nRow){ /* Marker valid. Begin looping. */
						if(m_PlCount < 0){ /* Not already looping, begin. */
							m_PlCount = param;
							m_nPlChannel = i;
						}

						if(m_nPlChannel == i){ /* Next Loop.*/
							if(m_PlCount == 0){ /* Loop finished. */
								/* Invalidate current marker. */
								channel.m_nPlRow= m_nRow + 1;
							}else{ /* Loop and cancel any breaks on this row. */
								m_nNextRow= channel.m_nPlRow;
								m_nBreakSeqPos= -1;
							}

							m_PlCount--;
						}
					}
				break;
				case 0x1E: /* Pattern Delay.*/
					m_nTick= m_nSpeed + (m_nSpeed * param);
				break;
			}
		}
		
		return isSongEnd;
	}
}
