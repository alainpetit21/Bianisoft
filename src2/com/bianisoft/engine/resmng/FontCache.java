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
import java.awt.Font;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

//Bianisoft imports
import com.bianisoft.engine.App;


public final class FontCache{
	private static Map<String, Font> m_mapCache= new ConcurrentHashMap<String, Font>();
	public static Font m_fontSystem= new Font("serif", Font.PLAIN, 8);

	
	public static Font getFontFromFile(String p_stName, float p_fSize){
		Font font= null;

		if(m_mapCache != null)
			if((font= m_mapCache.get(p_stName + Float.toString(p_fSize))) != null)
				return font;

		try{
			InputStream obIS= FontCache.class.getResourceAsStream(p_stName);

			Font tempFont= Font.createFont(Font.TRUETYPE_FONT, obIS);
			font= tempFont.deriveFont(p_fSize);
			m_mapCache.put(p_stName + Float.toString(p_fSize), font);
			obIS.close();
		}catch (Exception ex){
			ex.printStackTrace();
			System.err.println(p_stName + " not loaded. ");
			App.exit();
		}

		return font;
	}
}


