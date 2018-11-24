//---
//Copyright (c) 2010, Martin Cameron
//All rights reserved.
//
// Licenced under the term of New BSD - 2 Clauses Licences
//---
package mumart.micromod.mod;


public class Module{
	public static final int C2_PAL= 8287;
	public static final int C2_NTSC= 8363;


	public String m_stSongName;
	public int m_nNbChannels, m_nNBInstruments, m_nNbPatterns;
	public int m_nSequenceLength, m_nRestartPos, m_nC2Rate, m_nGain;
	public byte[] m_bufPatterns;
	public byte[] m_bufSequence;
	public Instrument[] m_arInstruments;


	public Module(){
		m_stSongName		= "Blank";
		m_nNbChannels		= 4;
		m_nNBInstruments	= 1;
		m_nNbPatterns		= 1;
		m_nSequenceLength	= 1;
		m_nC2Rate			= C2_PAL;
		m_nGain				= 64;

		m_bufPatterns	= new byte[64 * 4 * m_nNbChannels];
		m_bufSequence	= new byte[1];
		m_arInstruments	= new Instrument[m_nNBInstruments + 1];

		m_arInstruments[0]= m_arInstruments[1]= new Instrument();
	}

	public Module(byte[] p_bufModule){
		m_stSongName		= ascii(p_bufModule, 0, 20);
		m_nSequenceLength	= p_bufModule[950] & 0x7F;
		m_nRestartPos		= p_bufModule[951] & 0x7F;

		if(m_nRestartPos >= m_nSequenceLength)
			m_nRestartPos= 0;

		m_bufSequence= new byte[128];

		for(int i= 0; i < 128; i++){
			int pat_idx= p_bufModule[952 + i] & 0x7F;
			m_bufSequence[i]= (byte)pat_idx;
			
			if(pat_idx >= m_nNbPatterns)
				m_nNbPatterns= pat_idx + 1;
		}

		switch(ushortbe(p_bufModule, 1082)){
			case 0x4b2e: /* M.K. */
			case 0x4b21: /* M!K! */
			case 0x5434: /* FLT4 */
				m_nNbChannels= 4;
				m_nC2Rate= C2_PAL;
				m_nGain= 64;
			break;
			case 0x484e: /* xCHN */
				m_nNbChannels= p_bufModule[1080]-48;
				m_nC2Rate= C2_NTSC;
				m_nGain= 32;
			break;
			case 0x4348: /* xxCH */
				m_nNbChannels= (p_bufModule[1080]- 48) * 10;
				m_nNbChannels+= p_bufModule[1081]-48;

				m_nC2Rate= C2_NTSC;
				m_nGain= 32;
			break;
			default:
				System.out.print("---ERROR---\nMOD Format not recognised!\n");
				return;
		}

		int nNBNotes= m_nNbPatterns * 64 * m_nNbChannels;
		m_bufPatterns= new byte[nNBNotes * 4];
		System.arraycopy(p_bufModule, 1084, m_bufPatterns, 0, nNBNotes * 4);

		m_nNBInstruments= 31;
		m_arInstruments= new Instrument[m_nNBInstruments + 1];
		m_arInstruments[0]= new Instrument();

		int idxMod= 1084 + nNBNotes * 4;
		for(int i= 1; i <= m_nNBInstruments; i++){
			Instrument objInst= new Instrument();
			objInst.m_stName= ascii(p_bufModule, i * 30 - 10, 22);

			int nSampleLength= ushortbe(p_bufModule, i * 30 + 12) * 2;

			objInst.m_nFineTune= p_bufModule[i * 30 + 14] & 0xF;
			objInst.m_nVolume= p_bufModule[i * 30 + 15] & 0x7F;
			if(objInst.m_nVolume > 64)
				objInst.m_nVolume= 64;

			int nLoopStart= ushortbe(p_bufModule, i * 30 + 16) * 2;
			int nLoopLength= ushortbe(p_bufModule, i * 30 + 18) * 2;
			byte[] bufSampleData= new byte[nSampleLength + 1];

			if(idxMod + nSampleLength > p_bufModule.length)
				nSampleLength= p_bufModule.length - idxMod;

			System.arraycopy(p_bufModule, idxMod, bufSampleData, 0, nSampleLength);
			
			idxMod+= nSampleLength;
			if(nLoopStart + nLoopLength > nSampleLength)
				nLoopLength= nSampleLength - nLoopStart;
			
			if(nLoopLength < 4){
				nLoopStart = nSampleLength;
				nLoopLength = 0;
			}
			
			bufSampleData[nLoopStart + nLoopLength]= bufSampleData[nLoopStart];
			
			objInst.m_nLoopStart= nLoopStart;
			objInst.m_nLoopLength= nLoopLength;
			objInst.m_bufSampleData= bufSampleData;
			
			m_arInstruments[i]= objInst;
		}
	}

	private static int ushortbe(byte[] p_buf, int p_nOffset){
		return ((p_buf[p_nOffset]&0xFF) << 8) | (p_buf[p_nOffset+1]& 0xFF);
	}
	
	private static String ascii(byte[] p_buf, int p_nOffset, int p_nLength){
		char[] str= new char[p_nLength];

		for( int i= 0; i < p_nLength; i++){
			int c= p_buf[p_nOffset + i] & 0xFF;

			str[i]= (c < 32)? 32 : (char) c;
		}

		return new String( str );
	}
}
