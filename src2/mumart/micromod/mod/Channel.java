//---
//Copyright (c) 2010, Martin Cameron
//All rights reserved.
//
// Licenced under the term of New BSD - 2 Clauses Licences
//---
package mumart.micromod.mod;


public class Channel{
	private static final int FP_SHIFT = 15;
	private static final int FP_ONE = 1 << FP_SHIFT;
	private static final int FP_MASK = FP_ONE - 1;


	private static final short[] fine_tuning= {
		4096, 4067, 4037, 4008, 3979, 3951, 3922, 3894,
		4340, 4308, 4277, 4247, 4216, 4186, 4156, 4126
	};

	private static final short[] arp_tuning= {
		4096, 4340, 4598, 4871, 5161, 5468, 5793, 6137,
		6502, 6889, 7298, 7732, 8192, 8679, 9195, 9742
	};

	private static final short[] sine_table= {
		   0,  24,  49,  74,  97, 120, 141, 161, 180, 197, 212, 224, 235, 244, 250, 253,
		 255, 253, 250, 244, 235, 224, 212, 197, 180, 161, 141, 120,  97,  74,  49,  24
	};


	private Module m_objModule;
	private int m_nNoteKey, m_nNoteEffect, m_nNoteParam;
	private int m_nNoteIns, m_nInstrument, m_nAssigned;
	private int m_nSampleIdx, m_nSampleFra, m_nStep;
	private int m_nVolume, m_nPanning, m_nFineTune, m_nAmpl;
	private int m_nPeriod, m_nPortaPeriod, m_nPortaSpeed, m_FxCount;
	private int m_nVibratoType, m_nVibratoPhase, m_VibratoSpeed, m_nVibratoDepth;
	private int m_nTremoloType, m_nTremoloPhase, m_nTremoloSpeed, m_nTremoloDepth;
	private int m_nTremoloAdd, m_nVibratoAdd, m_nArpeggioAdd;
	private int m_nId, m_nC2Rate, m_nSampleRate, m_nGain, m_nRandomSeed;
	public int m_nPlRow;


	public Channel(Module p_objModule, int p_nId, int p_nSampleRate){
		m_nSampleRate	= p_nSampleRate;
		m_objModule		= p_objModule;
		m_nRandomSeed	= p_nId;
		m_nId			= p_nId;

		switch(p_nId & 0x3){
			case 0: case 3: m_nPanning=  51; break;
			case 1: case 2: m_nPanning= 204; break;
		}
	}
	
	public void resample(int[] p_outBuffer, int p_nOffset, int p_nLength, boolean p_isInterpolate){
		if(m_nAmpl <= 0)
			return;

		int l_ampl= m_nAmpl * m_nPanning >> 8;
		int r_ampl= m_nAmpl * (255 - m_nPanning) >> 8;
		int sam_idx= m_nSampleIdx;
		int sam_fra= m_nSampleFra;
		int step= m_nStep;
		Instrument ins= m_objModule.m_arInstruments[m_nInstrument];
		int loop_len= ins.m_nLoopLength;
		int loop_ep1= ins.m_nLoopStart + loop_len;
		byte[] sample_data= ins.m_bufSampleData;
		int out_idx= p_nOffset << 1;
		int out_ep1= p_nOffset + p_nLength << 1;

		if(p_isInterpolate){
			while(out_idx < out_ep1){
				if(sam_idx >= loop_ep1){
					if(loop_len <= 1) 
						break;

					while(sam_idx >= loop_ep1)
						sam_idx-= loop_len;
				}

				int c= sample_data[sam_idx];
				int m= sample_data[sam_idx + 1] - c;
				int y= (m * sam_fra >> FP_SHIFT - 8) + (c << 8);

				p_outBuffer[out_idx++]+= y * l_ampl >> FP_SHIFT;
				p_outBuffer[out_idx++]+= y * r_ampl >> FP_SHIFT;

				sam_fra+= step;
				sam_idx+= sam_fra >> FP_SHIFT;
				sam_fra&= FP_MASK;
			}
		}else{
			while(out_idx < out_ep1){
				if(sam_idx >= loop_ep1){
					if(loop_len <= 1)
						break;

					while(sam_idx >= loop_ep1)
						sam_idx -= loop_len;
				}

				int y= sample_data[sam_idx];
				
				p_outBuffer[out_idx++]+= y * l_ampl >> FP_SHIFT - 8;
				p_outBuffer[out_idx++]+= y * r_ampl >> FP_SHIFT - 8;

				sam_fra+= step;
				sam_idx+= sam_fra >> FP_SHIFT;
				sam_fra&= FP_MASK;
			}
		}
	}

	public void updateSampleIdx(int length){
		m_nSampleFra+= m_nStep * length;
		m_nSampleIdx+= m_nSampleFra >> FP_SHIFT;
		Instrument ins= m_objModule.m_arInstruments[ m_nInstrument ];
		int loop_start= ins.m_nLoopStart;
		int loop_length= ins.m_nLoopLength;
		int loop_offset= m_nSampleIdx - loop_start;

		if(loop_offset > 0){
			m_nSampleIdx= loop_start;
			
			if(loop_length > 1)
				m_nSampleIdx += loop_offset % loop_length;
		}

		m_nSampleFra&= FP_MASK;
	}

	public void row(int key, int ins, int effect, int param){
		m_nNoteKey= key;
		m_nNoteIns= ins;
		m_nNoteEffect= effect;
		m_nNoteParam= param;
		m_nVibratoAdd= m_nTremoloAdd = m_nArpeggioAdd = m_FxCount = 0;

		if(effect != 0x1D)
			trigger();

		switch(effect){
			case 0x3: /* Tone Portamento.*/
				if(param > 0)
					m_nPortaSpeed= param;
			break;
			case 0x4: /* Vibrato.*/
				if((param & 0xF0) > 0) 
					m_VibratoSpeed= param >> 4;

				if((param & 0x0F) > 0) 
					m_nVibratoDepth= param & 0xF;

				vibrato();
			break;
			case 0x6: /* Vibrato + Volume Slide.*/
				vibrato();
			break;
			case 0x7: /* Tremolo.*/
				if((param & 0xF0) > 0) 
					m_nTremoloSpeed= param >> 4;

				if((param & 0x0F) > 0) 
					m_nTremoloDepth= param & 0xF;

				tremolo();
			break;
			case 0x8: /* Set Panning. Not for Protracker. */
				if(m_objModule.m_nC2Rate == Module.C2_NTSC)
					m_nPanning= param;
			break;
			case 0x9: /* Set Sample Position.*/
				m_nSampleIdx= param << 8;
				m_nSampleFra= 0;
			break;
			case 0xC: /* Set Volume.*/
				m_nVolume= (param > 64)? 64:param;
			break;
			case 0x11: /* Fine Portamento Up.*/
				m_nPeriod-= param;
				if(m_nPeriod < 0)
					m_nPeriod = 0;
			break;
			case 0x12: /* Fine Portamento Down.*/
				m_nPeriod+= param;
				if(m_nPeriod > 65535)
					m_nPeriod = 65535;
			break;
			case 0x14: /* Set Vibrato Waveform.*/
				if(param < 8)
					m_nVibratoType= param;
			break;
			case 0x15: /* Set Finetune.*/
				m_nFineTune= param;
			break;
			case 0x17: /* Set Tremolo Waveform.*/
				if(param < 8)
					m_nTremoloType= param;
			break;
			case 0x1A: /* Fine Volume Up.*/
				m_nVolume+= param;
				if(m_nVolume > 64)
					m_nVolume= 64;
			break;
			case 0x1B: /* Fine Volume Down.*/
				m_nVolume-= param;
				if(m_nVolume < 0)
					m_nVolume= 0;
			break;
			case 0x1C: /* Note Cut.*/
				if(param <= 0)
					m_nVolume= 0;
			break;
			case 0x1D: /* Note Delay.*/
				if(param <= 0)
					trigger();
			break;
		}

		updateFrequency();
	}

	public void tick(){
		m_FxCount++;

		switch(m_nNoteEffect ){
			case 0x1: /* Portamento Up.*/
				m_nPeriod-= m_nNoteParam;
				if(m_nPeriod < 0)
					m_nPeriod = 0;
			break;
			case 0x2: /* Portamento Down.*/
				m_nPeriod+= m_nNoteParam;
				if(m_nPeriod > 65535)
					m_nPeriod = 65535;
			break;
			case 0x3: /* Tone Portamento.*/
				tonePortamento();
			break;
			case 0x4: /* Vibrato.*/
				m_nVibratoPhase+= m_VibratoSpeed;
				vibrato();
			break;
			case 0x5: /* Tone Porta + Volume Slide.*/
				tonePortamento();
				volumeSlide(m_nNoteParam);
			break;
			case 0x6: /* Vibrato + Volume Slide.*/
				m_nVibratoPhase+= m_VibratoSpeed;
				vibrato();
				volumeSlide(m_nNoteParam);
			break;
			case 0x7: /* Tremolo.*/
				m_nTremoloPhase+= m_nTremoloSpeed;
				tremolo();
			break;
			case 0xA: /* Volume Slide.*/
				volumeSlide(m_nNoteParam);
			break;
			case 0xE: /* Arpeggio.*/
				if(m_FxCount > 2)	m_FxCount		= 0;
				if(m_FxCount == 0)	m_nArpeggioAdd	= 0;
				if(m_FxCount == 1)	m_nArpeggioAdd	= m_nNoteParam >> 4;
				if(m_FxCount == 2)	m_nArpeggioAdd	= m_nNoteParam & 0xF;
			break;
			case 0x19: /* Retrig.*/
				if(m_FxCount >= m_nNoteParam) {
					m_FxCount= 0;
					m_nSampleIdx= m_nSampleFra= 0;
				}
			break;
			case 0x1C: /* Note Cut.*/
				if(m_nNoteParam == m_FxCount)
					m_nVolume = 0;
			break;
			case 0x1D: /* Note Delay.*/
				if(m_nNoteParam == m_FxCount)
					trigger();
			break;
		}

		if(m_nNoteEffect > 0)
			updateFrequency();
	}

	private void updateFrequency(){
		int period= m_nPeriod + m_nVibratoAdd;
		int freq, volume;

		if(period < 7)
			period = 6848;

		freq= m_objModule.m_nC2Rate * 428 / period;
		freq= ( freq * arp_tuning[ m_nArpeggioAdd ] >> 12 ) & 0x7FFFF;

		if(freq < 65536) 
			m_nStep= (freq << FP_SHIFT) / m_nSampleRate;
		else
			m_nStep= (freq << (FP_SHIFT - 3)) / (m_nSampleRate >> 3);

		volume= m_nVolume + m_nTremoloAdd;

		if(volume > 64)
			volume= 64;

		if(volume < 0)
			volume= 0;

		volume= volume * (FP_ONE>>6);
		m_nAmpl= volume * (m_objModule.m_nGain>>7);
	}

	private void trigger(){
		if((m_nNoteIns > 0) && (m_nNoteIns <= m_objModule.m_nNBInstruments)){
			m_nAssigned= m_nNoteIns;

			Instrument assigned_ins = m_objModule.m_arInstruments[ m_nAssigned ];

			m_nFineTune = assigned_ins.m_nFineTune & 0xF;
			m_nVolume = assigned_ins.m_nVolume >= 64 ? 64 : assigned_ins.m_nVolume & 0x3F;
			
			if((assigned_ins.m_nLoopLength > 0) && (m_nInstrument > 0))
				m_nInstrument = m_nAssigned;
		}

		if(m_nNoteKey > 0){
			int key= (m_nNoteKey * fine_tuning[m_nFineTune]) >> 11;
			
			m_nPortaPeriod= (key >> 1) + (key & 1);

			if((m_nNoteEffect != 0x3) && (m_nNoteEffect != 0x5)){
				m_nInstrument	= m_nAssigned;
				m_nPeriod		= m_nPortaPeriod;
				m_nSampleIdx	= m_nSampleFra	= 0;

				if(m_nVibratoType < 4)
					m_nVibratoPhase= 0;
				if(m_nTremoloType < 4)
					m_nTremoloPhase= 0;
			}
		}
	}
	
	private void volumeSlide(int param){
		int vol= m_nVolume + (param >> 4) - (param & 0xF);

		if(vol > 64)
			vol= 64;

		if(vol < 0) 
			vol= 0;

		m_nVolume= vol;
	}

	private void tonePortamento(){
		int src= m_nPeriod;
		int dst= m_nPortaPeriod;

		if(src < dst){
			src+= m_nPortaSpeed;

			if(src > dst)
				src= dst;
		}else if(src > dst){
			src-= m_nPortaSpeed;

			if(src < dst)
				src= dst;
		}

		m_nPeriod = src;
	}

	private void vibrato(){
		m_nVibratoAdd= waveform(m_nVibratoPhase, m_nVibratoType) * (m_nVibratoDepth >> 7);
	}
	
	private void tremolo(){
		m_nTremoloAdd= waveform(m_nTremoloPhase, m_nTremoloType ) * (m_nTremoloDepth >> 6);
	}

	private int waveform(int phase, int type){
		int amplitude= 0;

		switch(type & 0x3){
			case 0: /* Sine. */
				amplitude= sine_table[phase & 0x1F];
				if((phase & 0x20) > 0)
					amplitude = -amplitude;
			break;
			case 1: /* Saw Down. */
				amplitude= 255 - (((phase + 0x20) & 0x3F) << 3);
			break;
			case 2: /* Square. */
				amplitude= ((phase & 0x20) > 0)? 255:-255;
			break;
			case 3: /* Random. */
				amplitude= m_nRandomSeed - 255;
				m_nRandomSeed= ((m_nRandomSeed * 65)+ 17) & 0x1FF;
			break;
		}

		return amplitude;
	}
}
