//---
//Copyright (c) 2010, Martin Cameron
//All rights reserved.
//
// Licenced under the term of New BSD - 2 Clauses Licences
//---
package mumart.micromod.replay;


public interface Replay{
	public String	getVersion();
	public int		getSamplingRate();
	public int		getMixBufferLength();
	public String	getString(int p_nIndex);
	public int		getAudio(int[] p_bufOutput);

	public void setSequencePos(int p_nPos);

	public int calculateSongDuration();
	public int seek(int p_nSamplePos);
}
