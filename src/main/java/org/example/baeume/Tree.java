package org.example.baeume;

public class Tree {
    private final TreeNode rootTreeNode;

    public Tree(TreeNode rootTreeNode){
        this.rootTreeNode = rootTreeNode;
    }

    public TreeNode getRootTreeNode(){return rootTreeNode;}

    public int getLayers(){
        return calculateLayers(rootTreeNode);
    }

    public int calculateLayers(TreeNode node){
        if(node.getChildrenLength() == 0){
            return 1;
        } else {
            int maxChildLayers = 0;
            for(int i = 0; i < node.getChildrenLength(); i++){
                int childLayers = calculateLayers(node.getChildren().get(i));
                if(childLayers > maxChildLayers){
                    maxChildLayers = childLayers;
                }
            }
            return maxChildLayers + 1;
        }
    }
}
