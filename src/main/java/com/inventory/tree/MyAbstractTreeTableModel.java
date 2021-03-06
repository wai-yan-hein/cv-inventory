/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.tree;

/**
 *
 * @author Lenovo
 */
import javax.swing.event.EventListenerList;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreePath;

public abstract class MyAbstractTreeTableModel implements MyTreeTableModel {

    protected Object root;
    protected EventListenerList listenerList = new EventListenerList();

    private static final int CHANGED = 0;
    private static final int INSERTED = 1;
    private static final int REMOVED = 2;
    private static final int STRUCTURE_CHANGED = 3;

    public MyAbstractTreeTableModel(Object root) {
        this.root = root;
    }

    @Override
    public Object getRoot() {
        return root;
    }

    @Override
    public boolean isLeaf(Object node) {
        return getChildCount(node) == 0;
    }

    @Override
    public void valueForPathChanged(TreePath path, Object newValue) {
    }

    /**
     * Die Methode wird normalerweise nicht aufgerufen.
     */
    @Override
    public int getIndexOfChild(Object parent, Object child) {
        for (int i = 0; i < getChildCount(parent); i++) {
            if (getChild(parent, i).equals(child)) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public void addTreeModelListener(TreeModelListener l) {
        listenerList.add(TreeModelListener.class, l);
    }

    @Override
    public void removeTreeModelListener(TreeModelListener l) {
        listenerList.remove(TreeModelListener.class, l);
    }

    private void fireTreeNode(int changeType, Object source, Object[] path, int[] childIndices, Object[] children) {
        Object[] listeners = listenerList.getListenerList();
        TreeModelEvent e = new TreeModelEvent(source, path, childIndices, children);
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == TreeModelListener.class) {
                switch (changeType) {
                    case CHANGED ->
                        ((TreeModelListener) listeners[i + 1]).treeNodesChanged(e);
                    case INSERTED ->
                        ((TreeModelListener) listeners[i + 1]).treeNodesInserted(e);
                    case REMOVED ->
                        ((TreeModelListener) listeners[i + 1]).treeNodesRemoved(e);
                    case STRUCTURE_CHANGED ->
                        ((TreeModelListener) listeners[i + 1]).treeStructureChanged(e);
                    default -> {
                    }
                }

            }
        }
    }

    protected void fireTreeNodesChanged(Object source, Object[] path, int[] childIndices, Object[] children) {
        fireTreeNode(CHANGED, source, path, childIndices, children);
    }

    protected void fireTreeNodesInserted(Object source, Object[] path, int[] childIndices, Object[] children) {
        fireTreeNode(INSERTED, source, path, childIndices, children);
    }

    protected void fireTreeNodesRemoved(Object source, Object[] path, int[] childIndices, Object[] children) {
        fireTreeNode(REMOVED, source, path, childIndices, children);
    }

    protected void fireTreeStructureChanged(Object source, Object[] path, int[] childIndices, Object[] children) {
        fireTreeNode(STRUCTURE_CHANGED, source, path, childIndices, children);
    }

}
