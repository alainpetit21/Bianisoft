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


import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import mumart.micromod.mod.Micromod;
import mumart.micromod.mod.Module;
import com.bianisoft.engine.Obj;



public class MusicMOD extends Music{
	private Micromod		m_player;
	private Module			m_module;
	private FileInputStream	m_file;


	public MusicMOD(String p_stResModule){
		super(IDCLASS_Music);
		setSubClassID(TYPE_MOD);

		m_stResSong= p_stResModule;
	}

	public void load(){
		try{
			//String preparation manipulation
			m_stResSong= Obj.fixResFilename(m_stResSong);
			URL modurl = Thread.currentThread().getContextClassLoader().getResource(m_stResSong);

			File objFileModule= new File(modurl.getFile());
			byte[] dataModule= new byte[(int)objFileModule.length()];

			DataInputStream objDIS= new DataInputStream(new FileInputStream(objFileModule));
			objDIS.readFully(dataModule);
			objDIS.close();
		}catch(Exception e){
			System.out.print("Exception\n");
		}
	}

	public void play(){
	}

	public void stop(){
	}

	public boolean isPlaying(){
		return false;
	}
}
