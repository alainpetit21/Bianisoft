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
import com.bianisoft.engine.helper.Real;



public class PhysObj extends Obj{
	class MoveToCtrl{
		class Sextulpe{
			//posX, posY, posZ, angleX, angleY, angleZ
			public double[] m_vData= new double[6];
		}

		PhysObj				m_physObj;
		Sextulpe			m_ptFrom= new Sextulpe();
		ArrayList<Sextulpe>	m_pathTo= new ArrayList<Sextulpe>();
		ArrayList<Real>		m_pathToTimer= new ArrayList<Real>();
		boolean				m_isOn= false;

		Sextulpe			m_delta= new Sextulpe();


		public MoveToCtrl(PhysObj p_physObj){
			m_physObj= p_physObj;
		}

		public void readData(){
			m_ptFrom.m_vData[0]= m_physObj.m_vPos[0];
			m_ptFrom.m_vData[1]= m_physObj.m_vPos[1];
			m_ptFrom.m_vData[2]= m_physObj.m_vPos[2];
			m_ptFrom.m_vData[3]= m_physObj.m_vAngle[0];
			m_ptFrom.m_vData[4]= m_physObj.m_vAngle[1];
			m_ptFrom.m_vData[5]= m_physObj.m_vAngle[2];
		}

		public void saveData(){
			m_physObj.m_vPos[0]= m_ptFrom.m_vData[0];
			m_physObj.m_vPos[1]= m_ptFrom.m_vData[1];
			m_physObj.m_vPos[2]= m_ptFrom.m_vData[2];
			m_physObj.m_vAngle[0]= m_ptFrom.m_vData[3];
			m_physObj.m_vAngle[1]= m_ptFrom.m_vData[4];
			m_physObj.m_vAngle[2]= m_ptFrom.m_vData[5];
		}

		public void moveTo(double  p_fPosX, double p_fPosY, double p_fPosZ, double p_fAngleX, double p_fAngleY, double p_fAngleZ, int p_nNbMS){
			Sextulpe newSept= new Sextulpe();
			newSept.m_vData[0]= p_fPosX;
			newSept.m_vData[1]= p_fPosY;
			newSept.m_vData[2]= p_fPosZ;
			newSept.m_vData[3]= p_fAngleX;
			newSept.m_vData[4]= p_fAngleY;
			newSept.m_vData[5]= p_fAngleZ;

			m_pathTo.add(newSept);
			m_pathToTimer.add(new Real(p_nNbMS));
			m_isOn= true;
		}

		public void manage(double p_fTimeScaleFactor) {
			Sextulpe	dst= m_pathTo.get(0);
			Real		timer= m_pathToTimer.get(0);

			readData();

			//Get percentage Move
			double percentage= (p_fTimeScaleFactor*60) / timer.get();

			//Get Distance
			for(int i= 0; i < 6; ++i)
				m_delta.m_vData[i]= (dst.m_vData[i] - m_ptFrom.m_vData[i])*percentage;

			//Alter cur
			for(int i= 0; i < 6; ++i)
				m_ptFrom.m_vData[i]+= m_delta.m_vData[i];

			timer.set(timer.get() - (p_fTimeScaleFactor * 60));
			if(timer.get() <= 0){
				for(int i= 0; i < 6; ++i)
					m_ptFrom.m_vData[i]= dst.m_vData[i];

				m_pathTo.remove(0);
				m_pathToTimer.remove(0);

				if(m_pathTo.size() == 0)
					m_isOn= false;
			}

			saveData();
		}
	}

	private MoveToCtrl	m_objMoveToCtrl= new MoveToCtrl(this);

	public boolean m_isCollidable	= false;
	public boolean m_hasKeyFocus	= false;

	public double	m_fRadius= 0.0;

	public double[]	m_vPos			= {0.0, 0.0, 0.0};
	public double[]	m_vVel			= {0.0, 0.0, 0.0};
	public double[]	m_vAccel		= {0.0, 0.0, 0.0};
	public double[]	m_vAngle		= {0.0, 0.0, 0.0};
	public double[]	m_vAngleVel		= {0.0, 0.0, 0.0};
	public double[]	m_vAngleAccel	= {0.0, 0.0, 0.0};
	public double	m_nVelMax;
	public int		m_nDeepnessLevel= 1;

	//Map Mode
	public int[]	m_nPosMap= {0, 0, 0};



	public PhysObj()			{super(IDCLASS_PhysObj);}
	public PhysObj(int p_nType)	{super(p_nType);}

	public PhysObj(PhysObj p_refPhysObj){
		super(p_refPhysObj);

		m_isCollidable= p_refPhysObj.m_isCollidable;
		m_fRadius= p_refPhysObj.m_fRadius;
		m_nVelMax= p_refPhysObj.m_nVelMax;

		m_vAngleAccel= new double[]{p_refPhysObj.m_vAngleAccel[0], p_refPhysObj.m_vAngleAccel[1], p_refPhysObj.m_vAngleAccel[2]};
		m_vAngleVel= new double[]{p_refPhysObj.m_vAngleVel[0], p_refPhysObj.m_vAngleVel[1], p_refPhysObj.m_vAngleVel[2]};
		m_nPosMap= new int[]{p_refPhysObj.m_nPosMap[0], p_refPhysObj.m_nPosMap[1], p_refPhysObj.m_nPosMap[2]};
		m_vAccel= new double[]{p_refPhysObj.m_vAccel[0], p_refPhysObj.m_vAccel[1], p_refPhysObj.m_vAccel[2]};
		m_vAngle= new double[]{p_refPhysObj.m_vAngle[0], p_refPhysObj.m_vAngle[1], p_refPhysObj.m_vAngle[2]};
		m_vPos= new double[]{p_refPhysObj.m_vPos[0], p_refPhysObj.m_vPos[1], p_refPhysObj.m_vPos[2]};
		m_vVel= new double[]{p_refPhysObj.m_vVel[0], p_refPhysObj.m_vVel[1], p_refPhysObj.m_vVel[2]};
	}

	public void setAngleX(double p_nAngleX)	{m_vAngle[0]= p_nAngleX;}
	public void setAngleY(double p_nAngleY)	{m_vAngle[1]= p_nAngleY;}
	public void setAngleZ(double p_nAngleZ)	{m_vAngle[2]= p_nAngleZ;}

	public double getAngleX()	{return m_vAngle[0];}
	public double getAngleY()	{return m_vAngle[1];}
	public double getAngleZ()	{return m_vAngle[2];}

	public void setPosX(double p_nPosX)	{m_vPos[0]= p_nPosX;}
	public void setPosY(double p_nPosY)	{m_vPos[1]= p_nPosY;}
	public void setPosZ(double p_nPosZ)	{m_vPos[2]= p_nPosZ;}

	public double getPosX()	{return m_vPos[0];}
	public double getPosY()	{return m_vPos[1];}
	public double getPosZ()	{return m_vPos[2];}

	public void setPos(double p_nPosX, double p_nPosY, double p_nPosZ){
		m_vPos[0]= p_nPosX;
		m_vPos[1]= p_nPosY;
		m_vPos[2]= p_nPosZ;
	}

	public void setPos(double p_nPosX, double p_nPosY){
		m_vPos[0]= p_nPosX;
		m_vPos[1]= p_nPosY;
	}

	public void setVelX(double p_nVelX)	{m_vVel[0]= p_nVelX;}
	public void setVelY(double p_nVelY)	{m_vVel[1]= p_nVelY;}
	public void setVelZ(double p_nVelZ)	{m_vVel[2]= p_nVelZ;}

	public double getVelX()	{return m_vVel[0];}
	public double getVelY()	{return m_vVel[1];}
	public double getVelZ()	{return m_vVel[2];}


	public void setVel(double p_nVelX, double p_nVelY, double p_nVelZ){
		m_vVel[0]= p_nVelX;
		m_vVel[1]= p_nVelY;
		m_vVel[2]= p_nVelZ;
	}

	public void setVel(double p_nVelX, double p_nVelY){
		m_vVel[0]= p_nVelX;
		m_vVel[1]= p_nVelY;
	}

	public void setAccel(double p_nAccelX, double p_nAccelY, double p_nAccelZ){
		m_vAccel[0]= p_nAccelX;
		m_vAccel[1]= p_nAccelY;
		m_vAccel[2]= p_nAccelZ;
	}
	public void setAccel(double p_nAccelX, double p_nAccelY){
		m_vAccel[0]= p_nAccelX;
		m_vAccel[1]= p_nAccelY;
	}

	public void	moveTo(double  p_fPosX, double p_fPosY, double p_fPosZ, double p_fAngleX, double p_fAngleY, double p_fAngleZ, int p_nNbMS){
		m_objMoveToCtrl.moveTo(p_fPosX, p_fPosY, p_fPosZ, p_fAngleX, p_fAngleY, p_fAngleZ, p_nNbMS);
	}

	public boolean isMoving(){
		return (m_objMoveToCtrl.m_isOn) || (m_vVel[0] != 0) || (m_vVel[1] != 0) || (m_vVel[2] != 0) ||
			   (m_vAngleVel[0] != 0)|| (m_vAngleVel[1] != 0)|| (m_vAngleVel[2] != 0);
	}

	public void stopMoveTo(){
		m_objMoveToCtrl.m_isOn= false;
		m_vVel[0]= m_vAngleVel[0]= 0;
		m_vVel[1]= m_vAngleVel[1]= 0;
		m_vVel[2]= m_vAngleVel[2]= 0;
	}

	public void finishMoveTo(){
		for(int i= 0; i < 6; ++i)
			m_objMoveToCtrl.m_ptFrom.m_vData[i]= m_objMoveToCtrl.m_pathTo.get(m_objMoveToCtrl.m_pathTo.size()-1).m_vData[i];

		stopMoveTo();
	}

	public void manage(double p_fTimeScaleFactor){

		if(m_objMoveToCtrl.m_isOn){
			m_objMoveToCtrl.manage(p_fTimeScaleFactor);
		}else{
			m_vVel[0]+= m_vAccel[0];
			m_vVel[1]+= m_vAccel[1];
			m_vVel[2]+= m_vAccel[2];

			m_vPos[0]+= m_vVel[0] * p_fTimeScaleFactor;
			m_vPos[1]+= m_vVel[1] * p_fTimeScaleFactor;
			m_vPos[2]+= m_vVel[2] * p_fTimeScaleFactor;
		}

		m_nPosMap[0]= (int)m_vPos[0]/App.g_theApp.m_nTileSize;
		m_nPosMap[1]= (int)m_vPos[1]/App.g_theApp.m_nTileSize;
		m_nPosMap[2]= (int)m_vPos[2]/App.g_theApp.m_nTileSize;
	}

	public String toString() {
		return "PhysObj @ " + (int)m_vPos[0] + ";"+ (int)m_vPos[1] + ";"+ (int)m_vPos[2] + ";";
	}
	
	public void load()	{	}
}
