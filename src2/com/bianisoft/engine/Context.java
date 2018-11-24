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
package com.bianisoft.engine;


//Standard Java imports
import java.util.ArrayList;

//Bianisoft imports
import com.bianisoft.engine.sprites.Sprite;
import com.bianisoft.engine.labels.Label;
import com.bianisoft.engine.helper.Timer;
import com.bianisoft.engine.manager.MngInput;
import com.bianisoft.engine.manager.Physic.MngPhysic;


public class Context extends Countainer{
	private Camera		m_cam2D;
	private Camera		m_cam3D;
	private Sprite		m_sprMouse;
	private MngPhysic	m_objPhysic;
	private Obj			m_objWithKeyFocus;

	public ArrayList<Timer>	m_arTimer= new ArrayList<Timer>();
	public int	m_nIndex;

	public boolean	m_is3DFirst= true;


	public Context(){
		super(IDCLASS_Context);
		m_objWithKeyFocus= this;
	}

	public void setCursor(Sprite p_physObj){
		m_sprMouse		= p_physObj;
		m_physObjIgnored= (PhysObj)p_physObj;
	}

	private void setKeyFocusObj(Obj p_obj){
		if(m_objWithKeyFocus.isKindOf(IDCLASS_PhysObj)){
			PhysObj physObj= (PhysObj)m_objWithKeyFocus;
			physObj.m_hasKeyFocus= false;
		}
		if(p_obj.isKindOf(IDCLASS_PhysObj)){
			PhysObj physObj= (PhysObj)p_obj;
			physObj.m_hasKeyFocus= true;
		}

		m_objWithKeyFocus= p_obj;
	}

	public MngPhysic getPhysic()		{return ((m_objPhysic == null)? m_objPhysic= new MngPhysic():m_objPhysic);}
	public Sprite getCursor()			{return m_sprMouse;}
	public void removeAllTimers()		{m_arTimer.clear();}

	public void addTimer(int p_nDelay, Timer.Callback p_objTimerCallback, Context p_objHint){
		m_arTimer.add(new Timer(p_nDelay, p_objTimerCallback, p_objHint));
	}

	public void addTimer(Timer p_timer){
		m_arTimer.add(p_timer);
	}

	public void activate(){
		m_cam2D= Camera.createCamera(Camera.TYPE_2D);
		m_cam3D= Camera.createCamera(Camera.TYPE_3D);
		m_cam2D.setCur();
		m_cam3D.setCur();
	}

	public void deActivate(){
		m_cam2D= null;
		m_cam3D= null;
		removeAllChilds();
		getPhysic().m_arLines.clear();
	}

	public void manageSort(){
		for(int i= 0; i < m_vecPhysObj.size(); ++i){
			PhysObj physObj1= m_vecPhysObj.get(i);

			for(int j= i+1; j < m_vecPhysObj.size(); ++j){
				PhysObj physObj2= m_vecPhysObj.get(j);

				if(physObj2.m_vPos[2] > physObj1.m_vPos[2]){
					m_vecPhysObj.set(i, physObj2);
					m_vecPhysObj.set(j, physObj1);
					i= -1;
					break;
				}
			}
		}
	}

	public boolean keyManage(MngInput p_input){
		return m_objWithKeyFocus == this;
	}

	private void manageMouse(double p_fRatioMovement){
		if(m_sprMouse == null)
			return;

		Camera cam= Camera.getCur(Camera.TYPE_2D);
		m_sprMouse.m_vPos[0]= -cam.m_vPos[0] + App.m_mngInput.m_nMouseX+2;
		m_sprMouse.m_vPos[1]= -cam.m_vPos[1] + App.m_mngInput.m_nMouseY+32;

		Button objButton= (Button)findAtByRtti((int)m_sprMouse.m_vPos[0], (int)m_sprMouse.m_vPos[1], Integer.MIN_VALUE, Obj.IDCLASS_Button);
		if((objButton != null) && (!objButton.m_isDisabled)){
			if(objButton.m_nCurState == Button.ST_IDLE){
				objButton.m_nOldState= objButton.m_nCurState;

				if(App.m_mngInput.isMouseClicked(MngInput.M_LEFT)){
					setKeyFocusObj(this);

					if(objButton.m_objCallback != null)
						objButton.m_objCallback.callbackStateChanged(Button.ST_CLICKED, objButton);
				}else if(App.m_mngInput.isMouseDown(MngInput.M_LEFT)){
					setKeyFocusObj(this);
					objButton.m_nCurState= Button.ST_DOWN;

					if(objButton.m_objCallback != null)
						objButton.m_objCallback.callbackStateChanged(Button.ST_DOWN, objButton);
				}else{
					objButton.m_nCurState= Button.ST_OVER;

					if(objButton.m_objCallback != null)
						objButton.m_objCallback.callbackStateChanged(Button.ST_OVER, objButton);
				}
				
				if(m_sprMouse.m_vecStates.size() > 1)
					m_sprMouse.m_nCurState= 1;
			}
		}

		Label objLabel= (Label)findAtByRtti((int)m_sprMouse.m_vPos[0], (int)m_sprMouse.m_vPos[1], Integer.MIN_VALUE, Obj.IDCLASS_Label);
		if(objLabel != null){
			if(App.m_mngInput.isMouseClicked(MngInput.M_LEFT))
				if(objLabel.click())
					setKeyFocusObj(objLabel);
		}else{
			m_sprMouse.m_nCurState= 0;

			if(App.m_mngInput.isMouseClicked(MngInput.M_LEFT))
				setKeyFocusObj(this);
		}
	}

	public void manage(double p_fRatioMovement){
		manageSort();
		getPhysic().manageCollision(p_fRatioMovement, m_vecPhysObj);

		//Object Manage
		m_isDefferingModification= true;
		for(PhysObj physObj1 : m_vecPhysObj){
			boolean isInDeletionList= false;

			for(PhysObj physObj2 : m_vecPhysObjToDeleted){
				if(physObj1 == physObj2){
					isInDeletionList= true;
					break;
				}
			}

			if(!isInDeletionList)
				physObj1.manage(p_fRatioMovement);

		}
		m_isDefferingModification= false;

		//If any Phys were removed/added during Manage, they were actually deffered to be deleted here
		for(PhysObj physObj : m_vecPhysObjToAdd)
			m_vecPhysObj.add(physObj);
		for(PhysObj physObj : m_vecPhysObjToDeleted)
			m_vecPhysObj.remove(physObj);
		m_vecPhysObjToDeleted.clear();
		m_vecPhysObjToAdd.clear();

		//Timers Manage
		for(Timer objTimer : m_arTimer)
			objTimer.manage(16.67 * p_fRatioMovement);

		manageMouse(p_fRatioMovement);
	}

	public void draw(){
		App.g_theApp.orthogonalStart(App.g_CurrentDrawable);

		if(m_is3DFirst){
			for(PhysObj physObject : m_vecPhysObj)
				if(physObject.isKindOf(Obj.IDCLASS_Object3D))
					((Drawable)physObject).draw();
			for(PhysObj physObject : m_vecPhysObj)
				if(((physObject.isKindOf(Obj.IDCLASS_Sprite)) || (physObject.isKindOf(Obj.IDCLASS_Label)) || (physObject.isKindOf(Obj.IDCLASS_Background))))
					((Drawable)physObject).draw();
		}else{
			for(PhysObj physObject : m_vecPhysObj)
				if(((physObject.isKindOf(Obj.IDCLASS_Sprite)) || (physObject.isKindOf(Obj.IDCLASS_Label)) || (physObject.isKindOf(Obj.IDCLASS_Background))))
					((Drawable)physObject).draw();
			for(PhysObj physObject : m_vecPhysObj)
				if(physObject.isKindOf(Obj.IDCLASS_Object3D))
					((Drawable)physObject).draw();
		}

		if(App.PRINT_DEBUG)
			drawDebug();

		App.g_theApp.orthogonalEnd(App.g_CurrentDrawable);
	}

	public void drawDebug()	{
		getPhysic().drawDebug();
	}
}

