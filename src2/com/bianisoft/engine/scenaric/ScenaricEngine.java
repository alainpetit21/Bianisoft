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
package com.bianisoft.engine.scenaric;


//Standard Java imports
import java.util.ArrayList;

//Bianisoft imports
import com.bianisoft.engine.Drawable;
import com.bianisoft.engine.Obj;
import com.bianisoft.engine.PhysObj;
import com.bianisoft.engine.sprites.Sprite;
import com.bianisoft.engine.helper.Bool;
import com.bianisoft.engine.helper.Int;


public class ScenaricEngine{
	public static final int SECLSID_Node								= 0;
	public static final int SECLSID_Node_ArrayDrawableObj_AnimateAlpha	= 1;
	public static final int SECLSID_Node_Boolean_Wait					= 2;
	public static final int SECLSID_Node_DrawableObj_AnimateAlpha		= 3;
	public static final int SECLSID_Node_Context_Change					= 4;
	public static final int SECLSID_Node_DrawableObj_Show				= 5;
	public static final int SECLSID_Node_DrawableObj_ZoomTo				= 6;
	public static final int SECLSID_Node_Node_Stop						= 7;
	public static final int SECLSID_Node_PhysObj_MoveTo					= 8;
	public static final int SECLSID_Node_PhysObj_SetPos					= 9;
	public static final int SECLSID_Node_Sprite_SetFrame				= 10;
	public static final int SECLSID_Node_Sprite_SetState				= 11;
	public static final int SECLSID_Node_Timer_Wait						= 12;
	public static final int SECLSID_Last								= 13;

//	public static final int SECLSID_CNodeIncVariable					= 2;
//	public static final int SECLSID_CNodeWaitRand						= 4;
//	public static final int SECLSID_CNodeWaitKey						= 5;
//	public static final int SECLSID_CNodeClearLabelGradual				= 9;
//	public static final int SECLSID_CNodeAnimateEntityRandStateRandPos	= 10;
//	public static final int SECLSID_CNodeStartMusic						= 12;
	
	public static ScenaricEngine m_theInstance;

	public ArrayList<ScenaricNode>	m_vecNodes			= new ArrayList<ScenaricNode>();
	public ArrayList<ScenaricNode>	m_vecRunningNodes	= new ArrayList<ScenaricNode>();

	public ScenaricNode			m_objNodeRoot;


	public ScenaricEngine(){
		m_theInstance= this;
		m_objNodeRoot= new ScenaricNode();
	}

	public void load(){
		//this can load the Engine from a ressoruce file
	}

	public void manage(double p_fTimeTick){
		for(ScenaricNode objNode : m_vecRunningNodes){
			if(objNode.manage(p_fTimeTick) == false){
				objNode.end();

				for(Int objIdxNodeChilds : objNode.m_vecIdxChilds)
					run(objIdxNodeChilds.get());

				m_vecRunningNodes.remove(objNode);
				break;
			}
		}
	}

	public void addNode(ScenaricNode p_objNode){
		p_objNode.m_nIdx= m_vecNodes.size();
		m_vecNodes.add(p_objNode);
	}

	public void run(int p_idxNode)		{run(m_vecNodes.get(p_idxNode));}
	public void run(ScenaricNode p_objNode){
		m_vecRunningNodes.add(p_objNode);

		p_objNode.start();
	}

	public void stop(int p_idxNode)	{stop(m_vecNodes.get(p_idxNode));}
	public void stop(ScenaricNode p_objNode){
		p_objNode.end();
		m_vecRunningNodes.remove(p_objNode);
	}

	private ScenaricNode preventLoopStart= null;
	public void stopWithChilds(int p_idxNode)	{stopWithChilds(m_vecNodes.get(p_idxNode));}
	public void stopWithChilds(ScenaricNode p_objNode){
		ScenaricNode objNode;

		preventLoopStart= p_objNode;
		stop(p_objNode);

		for(int i= 0; i < p_objNode.m_vecIdxChilds.size(); ++i){
			objNode= m_vecNodes.get(p_objNode.m_vecIdxChilds.get(i).get());
			stopWithChildsInner(objNode);
		}
	}

	public void stopWithChildsInner(ScenaricNode p_objNode){
		ScenaricNode objNode;

		if(p_objNode == preventLoopStart)
			return;

		stop(p_objNode);
		for(int i= 0; i < p_objNode.m_vecIdxChilds.size(); ++i){
			objNode= m_vecNodes.get(p_objNode.m_vecIdxChilds.get(i).get());
			stopWithChildsInner(objNode);
		}
	}

	public ScenaricNode	getNode(int p_idxNode)	{return m_vecNodes.get(p_idxNode);}
	public ScenaricNode	getNodeRoot()			{return m_objNodeRoot;}
	public boolean isDone()						{return m_vecRunningNodes.size() == 0;}
	public int getFirstRunningNodeID(){
		if(m_vecRunningNodes.size() == 0)
			return -1;

		return m_vecRunningNodes.get(0).m_nIdx;
	}


	/*Helper Functions*/
	public ScnNode_ArrayDrawableObj_AnimateAlpha addNode_ArrayDrawableObj_AnimateAlpha(Drawable[] p_arDrawableObj){
		ScnNode_ArrayDrawableObj_AnimateAlpha node;

		node= new ScnNode_ArrayDrawableObj_AnimateAlpha(p_arDrawableObj);
		addNode(node);
		return node;
	}

	public ScnNode_Boolean_Wait addNode_Boolean_Wait(Bool p_objBooleanToWait, boolean p_bExpectedValue){
		ScnNode_Boolean_Wait node;

		node= new ScnNode_Boolean_Wait(p_objBooleanToWait, p_bExpectedValue);
		addNode(node);
		return node;
	}

	public ScnNode_Context_Change addNode_Context_Change(int p_nContextToChangeTo){
		ScnNode_Context_Change node;

		node= new ScnNode_Context_Change(p_nContextToChangeTo);
		addNode(node);
		return node;
	}

	public ScnNode_DrawableObj_AnimateAlpha addNode_DrawableObj_AnimateAlpha(Drawable p_drawableObj){
		ScnNode_DrawableObj_AnimateAlpha node;

		node= new ScnNode_DrawableObj_AnimateAlpha(p_drawableObj);
		addNode(node);
		return node;
	}

	public ScnNode_DrawableObj_Show addNode_DrawableObj_Show(Drawable p_drawableObj, boolean p_willBeShown){
		ScnNode_DrawableObj_Show node;

		node= new ScnNode_DrawableObj_Show(p_drawableObj, p_willBeShown);
		addNode(node);
		return node;
	}

	public ScnNode_DrawableObj_ZoomTo addNode_DrawableObj_ZoomTo(Drawable p_drawableObj, double p_fZoomToDest, double p_fInc){
		ScnNode_DrawableObj_ZoomTo node;

		node= new ScnNode_DrawableObj_ZoomTo(p_drawableObj, p_fZoomToDest, p_fInc);
		addNode(node);
		return node;
	}

	public ScnNode_Node_Stop addNode_Node_Stop(ScenaricNode p_node){
		ScnNode_Node_Stop node;

		node= new ScnNode_Node_Stop(p_node);
		addNode(node);
		return node;
	}

	public ScnNode_PhysObj_MoveTo addNode_PhysObj_MoveTo(PhysObj p_physObj, int p_nPosX, int p_nPosY){
		ScnNode_PhysObj_MoveTo node;

		node= new ScnNode_PhysObj_MoveTo(p_physObj, p_nPosX, p_nPosY);
		addNode(node);
		return node;
	}

	public ScnNode_PhysObj_SetPos addNode_PhysObj_SetPos(PhysObj p_physObj, int p_nPosX, int p_nPosY, int p_nPosZ){
		ScnNode_PhysObj_SetPos node;

		node= new ScnNode_PhysObj_SetPos(p_physObj, p_nPosX, p_nPosY, p_nPosZ);
		addNode(node);
		return node;
	}

	public ScnNode_Sprite_SetFrame addNode_Sprite_SetFrame(Sprite p_sprite, int p_nFrame){
		ScnNode_Sprite_SetFrame node;

		node= new ScnNode_Sprite_SetFrame(p_sprite, p_nFrame);
		addNode(node);
		return node;
	}

	public ScnNode_Sprite_SetState addNode_Sprite_SetState(Sprite p_sprite, int p_nState){
		ScnNode_Sprite_SetState node;

		node= new ScnNode_Sprite_SetState(p_sprite, p_nState);
		addNode(node);
		return node;
	}

	public ScnNode_Timer_Wait addNode_Timer_Wait(int p_nMsToWait){
		ScnNode_Timer_Wait node;

		node= new ScnNode_Timer_Wait(p_nMsToWait);
		addNode(node);
		return node;
	}


	public static int helper_StringToNodeType(String p_stType){
		if(p_stType.equals("SECLSID_Node"))										return SECLSID_Node;
		else if(p_stType.equals("SECLSID_Node_ArrayDrawableObj_AnimateAlpha"))	return SECLSID_Node_ArrayDrawableObj_AnimateAlpha;
		else if(p_stType.equals("SECLSID_Node_Boolean_Wait"))					return SECLSID_Node_Boolean_Wait;
		else if(p_stType.equals("SECLSID_Node_Context_Change"))					return SECLSID_Node_Context_Change;
		else if(p_stType.equals("SECLSID_Node_DrawableObj_AnimateAlpha"))		return SECLSID_Node_DrawableObj_AnimateAlpha;
		else if(p_stType.equals("SECLSID_Node_DrawableObj_Show"))				return SECLSID_Node_DrawableObj_Show;
		else if(p_stType.equals("SECLSID_Node_DrawableObj_ZoomTo"))				return SECLSID_Node_DrawableObj_ZoomTo;
		else if(p_stType.equals("SECLSID_Node_Node_Stop"))						return SECLSID_Node_Node_Stop;
		else if(p_stType.equals("SECLSID_Node_PhysObj_MoveTo"))					return SECLSID_Node_PhysObj_MoveTo;
		else if(p_stType.equals("SECLSID_Node_PhysObj_SetPos"))					return SECLSID_Node_PhysObj_SetPos;
		else if(p_stType.equals("SECLSID_Node_Sprite_SetFrame"))				return SECLSID_Node_Sprite_SetFrame;
		else if(p_stType.equals("SECLSID_Node_Sprite_SetState"))				return SECLSID_Node_Sprite_SetState;
		else if(p_stType.equals("SECLSID_Node_Timer_Wait"))						return SECLSID_Node_Timer_Wait;
		else																	return -1;
	}

	public static boolean helper_NodeIDIs(Obj p_obj, String p_stType){
		return p_obj.getTextClassID().equals(p_stType);
	}
}

