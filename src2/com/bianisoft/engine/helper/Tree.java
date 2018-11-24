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
package com.bianisoft.engine.helper;


//Standard Java imports
import java.util.ArrayList;


public class Tree<T>{
	public static int m_nNodesTotal= 0;

	private ArrayList<Tree<T>>	m_lstLeafs= new ArrayList<Tree<T>>();
	private Tree<T>			m_treeParent= null;
	private T				m_objHead;


	public Tree(T p_objHead)			{m_objHead= p_objHead;}
	public T getHead()					{return m_objHead;}
	public Tree<T> getParent()			{return m_treeParent;}
	public ArrayList<Tree<T>> getSubTrees(){return m_lstLeafs;}

	public Tree<T> addLeaf(Tree<T> p_objSubTree){
		m_nNodesTotal++;
		m_lstLeafs.add(p_objSubTree);
		p_objSubTree.m_treeParent= this;
		return p_objSubTree;
	}

	public Tree<T> addLeaf(T p_objLeaf){
		Tree<T> newTree= new Tree<T>(p_objLeaf);

		m_nNodesTotal++;
		m_lstLeafs.add(newTree);
		newTree.m_treeParent= this;
		return newTree;
	}

	public Tree<T> setAsParent(T p_objParentRoot){
		Tree<T> t= new Tree<T>(p_objParentRoot);

		t.m_lstLeafs.add(this);
		m_treeParent= t;

		return t;
	}
}
