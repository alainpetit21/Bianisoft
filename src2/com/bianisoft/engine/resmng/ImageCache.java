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
import java.net.URL;

//Standard JOGL imports
import com.sun.opengl.util.texture.Texture;
import com.sun.opengl.util.texture.TextureIO;
import com.bianisoft.engine.Obj;


public final class ImageCache{
	private static Map<String, Texture> m_mapCache= new ConcurrentHashMap<String, Texture>();


	public static Texture loadImage(String p_stRessource){
		Texture temptex= null;

		if((temptex= m_mapCache.get(p_stRessource)) != null)
			return temptex;

		p_stRessource= Obj.fixResFilename(p_stRessource);
		URL url= Thread.currentThread().getContextClassLoader().getResource(p_stRessource);
		if(url == null){
			System.out.printf("Error while loading:%s - Pass 2\n", p_stRessource);
			System.exit(1);
		}

		try{
			temptex= TextureIO.newTexture(url, false, null);
		}catch(Exception e1){
			System.out.printf("Error while loading:%s - Pass 3", p_stRessource);
			System.exit(1);
		}

		m_mapCache.put(p_stRessource, temptex);
		return temptex;
	}
}
