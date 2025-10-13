package org.example.baeume;

import java.util.List;
import java.util.ArrayList;

public class TreeNode {
    private TreeNode parent;
    private List<TreeNode> children;
    private float width;

    private double XPos;
    private double YPos;


    public void setXPos(double XPos) {
        this.XPos = XPos;
    }

    public void setYPos(double YPos) {
        this.YPos = YPos;
    }

    public double getYPos() {
        return YPos;
    }

    public double getXPos() {
        return XPos;
    }


    public TreeNode(){
        parent = null;
        children = new ArrayList<>();
    }

    public TreeNode(TreeNode parent){
        this.parent = parent;
        children = new ArrayList<>();
    }

    public void addChild(TreeNode child){
        children.add(child);
        child.setParent(this);
    }

    public void setParent(TreeNode parent){
        this.parent = parent;
    }

    public TreeNode getParent(){
        return parent;
    }

    public int getChildrenLength(){
        return children.size();
    }

    public List<TreeNode> getChildren(){
        return children;
    }

    public void setWidth (float width){
        this.width = width;
    }

    public float getWidth(){
        return width;
    }
}
