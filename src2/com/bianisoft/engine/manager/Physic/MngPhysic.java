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
package com.bianisoft.engine.manager.Physic;


//Standard Java imports
import java.io.DataInputStream;
import java.io.InputStream;
import java.util.ArrayList;

//Standard JOGL imports
import javax.media.opengl.GL;

//Bianisoft imports
import com.bianisoft.engine.App;
import com.bianisoft.engine.Camera;
import com.bianisoft.engine.PhysObj;


public class MngPhysic implements IColliderCallback{
	private IColliderCallback m_overloadCB= this;

	public ArrayList<Line>	m_arLines= new ArrayList<Line>();


	public void setCallback(IColliderCallback p_objColliderCallback)	{m_overloadCB= p_objColliderCallback;}

	public void addLine(Line p_objLine)	{m_arLines.add(p_objLine);}
	public void addLine(int p_x1, int p_y1, int p_x2, int p_y2, int p_group){
		m_arLines.add(new Line(p_x1, p_y1, p_x2, p_y2, p_group));
	}

	public void loadCollisionFile(String p_stRessource){
		try{
			InputStream objIS= Thread.currentThread().getContextClassLoader().getResourceAsStream(p_stRessource);
			DataInputStream objDIS	= new DataInputStream(objIS);

			//Polygons
			int nbPolygons= objDIS.readInt();
			for(int i= 0; i < nbPolygons; ++i){
				int nbPoints= objDIS.readInt();
				int nGroup= objDIS.readInt();

				int[] m_vBufferX= new int[nbPoints];
				int[] m_vBufferY= new int[nbPoints];

				for(int j= 0; j < nbPoints; ++j){
					m_vBufferX[j]= objDIS.readInt();
					m_vBufferY[j]= objDIS.readInt();
				}

				for(int j= 1; j < nbPoints; ++j)
					addLine(m_vBufferX[j-1], m_vBufferY[j-1], m_vBufferX[j], m_vBufferY[j], nGroup);

				//Close the polygon
				addLine(m_vBufferX[nbPoints-1], m_vBufferY[nbPoints-1], m_vBufferX[0], m_vBufferY[0], nGroup);
			}

			//Polylines
			int nbPolylines= objDIS.readInt();
			for(int i= 0; i < nbPolylines; ++i){
				int nbPoints= objDIS.readInt();
				int nGroup= objDIS.readInt();

				int[] m_vBufferX= new int[nbPoints];
				int[] m_vBufferY= new int[nbPoints];

				for(int j= 0; j < nbPoints; ++j){
					m_vBufferX[j]= objDIS.readInt();
					m_vBufferY[j]= objDIS.readInt();
				}

				for(int j= 1; j < nbPoints; ++j)
					addLine(m_vBufferX[j-1], m_vBufferY[j-1], m_vBufferX[j], m_vBufferY[j], nGroup);
			}

			//Lines
			int nbLines= objDIS.readInt();
			for(int i= 0; i < nbLines; ++i){
				int	sX		= objDIS.readInt();
				int	sY		= objDIS.readInt();
				int	eX		= objDIS.readInt();
				int	eY		= objDIS.readInt();
				int	group	= objDIS.readInt();

				addLine(sX, sY, eX, eY, group);
			}

			objDIS.close();
		}catch (Exception e){
			e.printStackTrace();
		}
	}

	public void manageCollision(double p_fRatioMovement, ArrayList<PhysObj> p_vecPhysObj){
		for(int i= 0; i < p_vecPhysObj.size(); ++i){
			PhysObj physObj1= p_vecPhysObj.get(i);

			if(!((PhysObj)physObj1).m_isCollidable)
				continue;

			for(int j= i+1; j < p_vecPhysObj.size(); ++j){
				PhysObj physObj2= p_vecPhysObj.get(j);

				if(!((PhysObj)physObj2).m_isCollidable)
					continue;

				HitInfo hitStruct= new HitInfo();

				if(isCollidingObj2Obj(hitStruct, physObj1, physObj2))
					m_overloadCB.onCollideObj2Obj(hitStruct, physObj1, physObj2);
			}

			//Find Nearest Line
			HitInfo hitStruct= new HitInfo();
			boolean hadCollision;
			do{
				hadCollision= false;
				double nearestValue= 1000000000;

				for(Line objLine : m_arLines){
					if((hitStruct.lineHitten != objLine) && isCollidingObj2Line(hitStruct, physObj1, objLine)){
						if(hitStruct.u2 < nearestValue){
							nearestValue= hitStruct.u2;
							hadCollision= true;
						}
					}
				}

				if(hadCollision)
					m_overloadCB.onCollideObj2Line(hitStruct, physObj1);

			}while(hadCollision);
		}
	}

	public boolean isCollidingObj2Obj(HitInfo p_hitStruct, PhysObj p_physObj1, PhysObj p_physObj2){
		double	dpx		= p_physObj1.m_vPos[0] - p_physObj2.m_vPos[0];
		double	dpy		= p_physObj1.m_vPos[1] - p_physObj2.m_vPos[1];
		double	lenPSqr	= (dpx*dpx) + (dpy*dpy);

		double	dvpx	= (p_physObj1.m_vPos[0]+p_physObj1.m_vVel[0]+p_physObj1.m_vAccel[0]) - (p_physObj2.m_vPos[0]+p_physObj2.m_vVel[0]+p_physObj2.m_vAccel[0]);
		double	dvpy	= (p_physObj1.m_vPos[1]+p_physObj1.m_vVel[1]+p_physObj1.m_vAccel[1]) - (p_physObj2.m_vPos[1]+p_physObj2.m_vVel[1]+p_physObj2.m_vAccel[1]);
		double	lenVPSqr= (dvpx*dvpx) + (dvpy*dvpy);

		//double	dvx		= p_physObj1.m_vVel[0]-p_physObj2.m_vVel[0];
		//double	dvy		= p_physObj1.m_vVel[1]-p_physObj2.m_vVel[1];

		if(lenVPSqr <= ((p_physObj1.m_fRadius + p_physObj2.m_fRadius) * (p_physObj1.m_fRadius + p_physObj2.m_fRadius))){
			double dX	= p_physObj1.m_vPos[0] - p_physObj2.m_vPos[0];
			double dY	= p_physObj1.m_vPos[1] - p_physObj2.m_vPos[1];
			double len	= Math.sqrt(lenPSqr);

			p_hitStruct.ptHit[0]	= p_physObj1.m_vPos[0];
			p_hitStruct.ptHit[1]	= p_physObj1.m_vPos[1];
			p_hitStruct.norm[0]		= dX/len;
			p_hitStruct.norm[1]		= dY/len;
			p_hitStruct.u2			= 0;
			p_hitStruct.idHitten	= 1;
			return true;
		}
		return false;
	}

	public void onCollideObj2Obj(HitInfo p_hitStruct, PhysObj p_physObj1, PhysObj p_physObj2){
		double	vel1[]= {p_physObj1.m_vVel[0]+p_physObj1.m_vAccel[0], p_physObj1.m_vVel[1]+p_physObj1.m_vAccel[1], 0};
		double	vel2[]= {p_physObj2.m_vVel[0]+p_physObj2.m_vAccel[0], p_physObj2.m_vVel[1]+p_physObj2.m_vAccel[1], 0};
		double	nor1[]= {-p_hitStruct.norm[0], -p_hitStruct.norm[1], 0};
		double	para1[]	={-nor1[1], nor1[0], 0};
		double	nor2[]	={-nor1[0], -nor1[1], 0};
		double	para2[]	={-para1[0], -para1[1], 0};
		double	scale;
		double	tempR2[]= new double[3];
		double	tempR1[]= new double[3];

			scale		= (vel1[0] * para1[0]) + (vel1[1] * para1[1]);
			para1[0]	= para1[0] * scale;
			para1[1]	= para1[1] * scale;

			scale		= (vel1[0] * nor1[0]) + (vel1[1] * nor1[1]);
			tempR1[0]	= nor1[0] * scale;
			tempR1[1]	= nor1[1] * scale;

			scale		= (vel2[0] * para2[0]) + (vel2[1] * para2[1]);
			para2[0]	= para2[0] * scale;
			para2[1]	= para2[1] * scale;

			scale		= (vel2[0] * nor2[0]) + (vel2[1] * nor2[1]);
			tempR2[0]	= nor2[0] * scale;
			tempR2[1]	= nor2[1] * scale;

			p_physObj1.m_vVel[0]= (para1[0] + tempR2[0]) - p_physObj1.m_vAccel[0];
			p_physObj1.m_vVel[1]= (para1[1] + tempR2[1]) - p_physObj1.m_vAccel[1];
			p_physObj2.m_vVel[0]= (para2[0] + tempR1[0]) - p_physObj2.m_vAccel[0];
			p_physObj2.m_vVel[1]= (para2[1] + tempR1[1]) - p_physObj2.m_vAccel[1];
	}

	public boolean isCollidingObj2Line(HitInfo p_hitStruct, PhysObj p_physObj, Line p_pLine){
		double s_x1	=	p_physObj.m_vPos[0];
		double s_x2	=	p_physObj.m_vPos[0] + p_physObj.m_vVel[0] + p_physObj.m_vAccel[0];
		double s_x3	=	p_pLine.m_fStart[0];
		double s_x4	=	p_pLine.m_fEnd[0];
		double s_y1	=	p_physObj.m_vPos[1];
		double s_y2	=	p_physObj.m_vPos[1] + p_physObj.m_vVel[1] + p_physObj.m_vAccel[1];
		double s_y3	=	p_pLine.m_fStart[1];
		double s_y4	=	p_pLine.m_fEnd[1];

		{
			double	y1My3	=	(s_y1-s_y3);
			double	x1Mx3	=	(s_x1-s_x3);
			double	denom	=	((s_y4-s_y3) * (s_x2-s_x1)) - ((s_x4-s_x3) * (s_y2-s_y1));
			double	u1	,u2;
			double	xI1	,yI1;
			double	xI2	,yI2;
			double angle;

			if(denom == 0)
				return false;

			double[] vel	= {(s_x2-s_x1), (s_y2-s_y1)};
			double[] normale= {0, 0};

			u1=	(((s_x4-s_x3) * y1My3) - ((s_y4-s_y3) * x1Mx3)) / denom;
			u2=	(((s_x2-s_x1) * y1My3) - ((s_y2-s_y1) * x1Mx3)) / denom;

			if((u1 > 0) && (u2 >= 0) && (u1 <= 1) && (u2 <= 1)){
				xI1	=	s_x1 + (u1 * (s_x2 - s_x1));
				yI1	=	s_y1 + (u1 * (s_y2 - s_y1));
				xI2	=	s_x3 + (u2 * (s_x4 - s_x3));
				yI2	=	s_y3 + (u2 * (s_y4 - s_y3));

				angle= p_pLine.m_fAngle;
				normale[0]= -Math.sin(angle);
				normale[1]= Math.cos(angle);

				double value= (vel[0] * normale[0]) + (vel[1] * normale[1]);
				if(value <= 0){
					p_hitStruct.angle		= angle;
					p_hitStruct.ptHit[0]	= xI1;
					p_hitStruct.ptHit[1]	= yI1;
					p_hitStruct.u1			= u1;
					p_hitStruct.u2			= u2;
					p_hitStruct.norm[0]		= normale[0];
					p_hitStruct.norm[1]		= normale[1];
					p_hitStruct.lineHitten	= p_pLine;
					p_hitStruct.idHitten	= p_pLine.m_nGroup;
					return true;
				}
			}
		}

		return false;
	}

	public void onCollideObj2Line(HitInfo p_hitStruct, PhysObj p_physObj){
		double[] pos			= {p_physObj.m_vPos[0], p_physObj.m_vPos[1]};
		double[] velNormalized	= {p_physObj.m_vVel[0] + p_physObj.m_vAccel[0], p_physObj.m_vVel[1] + p_physObj.m_vAccel[1]};
		double[] nor			= {-p_hitStruct.norm[1], p_hitStruct.norm[0]};
		double[] u				= {0, 0};
		double[] v				= {0, 0};
		double len				= Math.sqrt((velNormalized[0] * velNormalized[0]) + (velNormalized[1] * velNormalized[1]));
		double scale;
		double numer;
		double denom;

		velNormalized[0]/= len;
		velNormalized[1]/= len;

		pos[0]= p_hitStruct.ptHit[0];
		pos[1]= p_hitStruct.ptHit[1];

		//Calculate Bounce
		numer	= (velNormalized[0] * nor[0]) + (velNormalized[1] * nor[1]);
		denom	= (nor[0] * nor[0]) + (nor[1] * nor[1]);
		scale	= numer / denom;
		u[0]	= nor[0] * scale;
		u[1]	= nor[1] * scale;
		v[0]	= (u[0] - (velNormalized[0])) + u[0];
		v[1]	= (u[1] - (velNormalized[1])) + u[1];

		p_physObj.m_vVel[0]= (v[0] * len) - p_physObj.m_vAccel[0];
		p_physObj.m_vVel[1]= (v[1] * len) - p_physObj.m_vAccel[1];
	}

	public void drawDebug(){
		GL gl= App.g_CurrentGL;

		gl.glPushMatrix();

		Camera.getCur(Camera.TYPE_2D).doProjection();

		gl.glDisable(GL.GL_TEXTURE_2D);
		gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL.GL_LINE);

		//Print Debug Collision
		for(Line objLine : m_arLines){
			gl.glColor3d(1.0, 0.0, 0.0);
			gl.glBegin(GL.GL_LINE);
				gl.glVertex2d(objLine.m_fStart[0], objLine.m_fStart[1]);
				gl.glVertex2d(objLine.m_fEnd[0], objLine.m_fEnd[1]);
			gl.glEnd();

			double middleX= objLine.m_fStart[0] + ((objLine.m_fEnd[0] - objLine.m_fStart[0])/2);
			double middleY= objLine.m_fStart[1] + ((objLine.m_fEnd[1] - objLine.m_fStart[1])/2);
			double middleNormX= middleX - (Math.sin(objLine.m_fAngle) * 10);
			double middleNormY= middleY + (Math.cos(objLine.m_fAngle) * 10);
			gl.glColor3d(0.0, 1.0, 0.0);
			gl.glBegin(GL.GL_LINE);
				gl.glVertex2d(middleX, middleY);
				gl.glVertex2d(middleNormX, middleNormY);
			gl.glEnd();
		}

		gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL.GL_FILL);
		gl.glEnable(GL.GL_TEXTURE_2D);
		gl.glPopMatrix();
	}
}
