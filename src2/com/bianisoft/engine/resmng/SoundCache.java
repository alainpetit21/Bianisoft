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
package com.bianisoft.engine.resmng;


//Standard Java imports
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

//Bianisoft imports
import com.bianisoft.engine.Sound;


public final class SoundCache {
	private static Map<String, Sound> m_mapCache= new ConcurrentHashMap<String, Sound>();

	
	public static Sound getSound(String p_stRessource){
		Sound tempSound= null;

		if((tempSound= m_mapCache.get(p_stRessource)) != null){
			return tempSound;
		}

		tempSound= new Sound(p_stRessource);
		m_mapCache.put(p_stRessource, tempSound);
		return tempSound;
	}
}
