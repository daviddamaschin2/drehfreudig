package org.example.baeume;

import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;

public class TreeManager {


    private Tree tree;
    //used to check if a tree has been successfully loaded, necessary when checking if the tree can be drawn
    private boolean isTreeLoaded;
    private boolean canBeEdited;
    private int boxWidth;
    private int boxHeight;
    private int boxOffsetX;
    private int boxOffsetY;
    private boolean isFlipped;


    public void loadTreeFromFile(String treeFile) {

        //checking if all tree characters are valid
        for(int i = 0; i < treeFile.length(); i++){
            if(treeFile.charAt(i) != '(' && treeFile.charAt(i) != ')'){
                System.out.println("Invalid character in file!");
                return; // stopping processing on invalid file
            }
        }
        //checking if tree string starts with '(' and then stepping through the string character by character
        if(treeFile.charAt(0) == '('){
            TreeNode rootNode = new TreeNode();
            rootNode.setWidth(1);
            tree = new Tree(rootNode);
            int index = 1; // start after the first '('
            stepTroughString(rootNode, treeFile, index);
            //calculating width of each node, this is needed for drawing the tree and checking if the tree can be flipped
            calculateWidth(rootNode);
        }
        else{
            System.out.println("Invalid tree string!");
            isTreeLoaded = false;
        }
    }


    public void calculateWidth(TreeNode currentNode){
        if(!currentNode.getChildren().isEmpty())
        {
            for(int i = 0; i < currentNode.getChildrenLength(); i++){
                currentNode.getChildren().get(i).setWidth(currentNode.getWidth() * currentNode.getChildrenLength());
                calculateWidth(currentNode.getChildren().get(i));
            }
        }
    }

    public void newEmptyTree(){
        TreeNode rootNode = new TreeNode();
        rootNode.setWidth(1);
        tree = new Tree(rootNode);
        isTreeLoaded = true;
    }


    public void stepTroughString(TreeNode currentNode, String treeFile, int index){
        //checking if current index is valid
        if(index >= treeFile.length()){
            System.out.println("Invalid tree string!");
            isTreeLoaded = false;
            return;
        }
        if(treeFile.charAt(index) == '('){
            //creating new node and adding it as a child to the current node, then stepping to the next character with the new node as current node
            TreeNode newNode = new TreeNode(currentNode);
            currentNode.addChild(newNode);
            stepTroughString(newNode, treeFile, index + 1);
        }
        //since we already checked for invalid characters, we can assume that if it is not '(', it must be ')'
        else{
            if(currentNode.getParent() != null){
                stepTroughString(currentNode.getParent(), treeFile, index + 1);
            } else {
                //we are back at the root node and the string has been fully processed
                System.out.println("Finished processing tree string.");
                isTreeLoaded = true;
            }
        }
    }

    public boolean isTreeLoaded(){
        return isTreeLoaded;
    }

    public void drawTree(AnchorPane rootPane, int offsetX, int offsetY, int boxWidth, int boxHeight){
        if(!isTreeLoaded) return;
        int layerAmount = tree.getLayers();
        isFlipped = false;

        // Draw starting from the root at position 0
        drawNode(rootPane, tree.getRootTreeNode(), 0, 0, offsetX, offsetY, boxWidth, boxHeight, layerAmount);
    }

    public void drawReversedTree(AnchorPane rootPane, int offsetX, int offsetY, int boxWidth, int boxHeight){
        if(!isTreeLoaded) return;
        int layerAmount = tree.getLayers();
        isFlipped = true;

        // Draw starting from the root at position 0
        drawReversedNode(rootPane, tree.getRootTreeNode(), 0, 0, offsetX, offsetY, boxWidth, boxHeight, layerAmount);
    }

    public int[] getBoxDimensions(){
        return new int[]{boxWidth, boxOffsetX, boxHeight, boxOffsetY};
    }

    public void addChildAt(double x, double y){
        stepTroughChildAndAdd(tree.getRootTreeNode(), x, y, 0, 1);
        calculateWidth(tree.getRootTreeNode());
    }

    public void stepTroughChildAndAdd(TreeNode node, double x, double y, double currentX, int layer){

        if(x >= boxOffsetX +  currentX && x <= boxOffsetX + currentX + boxWidth/node.getWidth()){
            if(y >= boxOffsetY + (layer-1) * (boxHeight/(double)tree.getLayers()) && y<= boxOffsetY + layer * (boxHeight/(double)tree.getLayers())){
                node.addChild(new TreeNode(node));
            }
            else {
                if (node.getChildren() == null || node.getChildren().isEmpty()) {
                    node.addChild(new TreeNode(node));
                    System.out.println("Added child to node.");
                } else {
                    double childX = currentX;
                    for (TreeNode child : node.getChildren()) {
                        stepTroughChildAndAdd(child, x, y, childX, layer + 1);
                        double childPixelWidth = boxWidth / child.getWidth();
                        childX += childPixelWidth;
                    }
                }
            }
        }
    }

    public void removeChildAt(double x, double y){
        stepTroughChildAndRemove(tree.getRootTreeNode(), x, y, 0, 1);
        calculateWidth(tree.getRootTreeNode());
    }

    public void stepTroughChildAndRemove(TreeNode node, double x, double y, double currentX, int layer){

        if(x >= boxOffsetX +  currentX && x <= boxOffsetX + currentX + boxWidth/node.getWidth()){
            if(y >= boxOffsetY + (layer-1) * (boxHeight/(double)tree.getLayers()) && y<= boxOffsetY + layer * (boxHeight/(double)tree.getLayers())){
                if(node.getChildren() != null && !node.getChildren().isEmpty())
                    node.getChildren().removeLast();
            }
            else {
                if (node.getChildren() != null || !node.getChildren().isEmpty()) {
                    double childX = currentX;
                    for (TreeNode child : node.getChildren()) {
                        stepTroughChildAndRemove(child, x, y, childX, layer + 1);
                        double childPixelWidth = boxWidth / child.getWidth();
                        childX += childPixelWidth;
                    }
                }
            }
        }
    }

    private void drawNode(AnchorPane rootPane, TreeNode node, int layer, double currentX,
                               int offsetX, int offsetY, int boxWidth, int boxHeight, int layerAmount) {

        final double nodeRadius = calculateNodeRadius(boxWidth, boxHeight);
        this.boxHeight = boxHeight;
        this.boxWidth = boxWidth;
        this.boxOffsetX = offsetX;
        this.boxOffsetY = offsetY;

        // Calculate dimensions - width is a denominator, so actual width is boxWidth / node.getWidth()
        double layerHeight = boxHeight / (double)layerAmount;
        double nodePixelWidth = boxWidth / node.getWidth();

        // Check if this is a leaf node
        boolean isLeaf = (node.getChildren() == null || node.getChildren().isEmpty());

        // If it's a leaf, extend the box to the bottom layer
        double boxHeight_actual;
        if(isLeaf) {
            // Calculate how many layers remain from current layer to the bottom
            int remainingLayers = layerAmount - layer;
            boxHeight_actual = layerHeight * remainingLayers;
        } else {
            boxHeight_actual = layerHeight;
        }

        // Draw this node's box
        Rectangle rect = new Rectangle(nodePixelWidth, boxHeight_actual);
        rect.setLayoutX(offsetX + currentX);
        rect.setLayoutY(offsetY + layer * layerHeight);
        rect.setFill(null);
        rect.getStyleClass().add("rounded-rect");
        rootPane.getChildren().add(rect);


        //Draw circle at the center of the box
        Circle circle = new Circle(nodeRadius);
        node.setXPos(offsetX + currentX + nodePixelWidth / 2);
        circle.setCenterX(node.getXPos());
        if(isLeaf){
            node.setYPos(offsetY + layer * layerHeight + boxHeight_actual -layerHeight/2);
            circle.setCenterY(node.getYPos());
        }
        else{
            node.setYPos(offsetY + layer * layerHeight + boxHeight_actual/2);
            circle.setCenterY(node.getYPos());
        }
        circle.setFill(null);
        circle.getStyleClass().add("glass-circle");
        rootPane.getChildren().add(circle);


        //Drawing lines to parent node (if not root)
        if(node.getParent() != null){
            Line line = new Line();
            line.setStartX(node.getParent().getXPos());
            line.setStartY(node.getParent().getYPos()+nodeRadius);
            line.setEndX(node.getXPos());
            line.setEndY(node.getYPos()-nodeRadius);
            line.setStroke(Color.WHITE);
            line.setStrokeWidth(1);
            rootPane.getChildren().add(line);
        }

        // Draw children (only if not a leaf)
        if(!isLeaf) {
            double childX = currentX;
            for(TreeNode child : node.getChildren()) {
                drawNode(rootPane, child, layer + 1, childX, offsetX, offsetY, boxWidth, boxHeight, layerAmount);
                double childPixelWidth = boxWidth / child.getWidth();
                childX += childPixelWidth;
            }
        }
    }

    private void drawReversedNode(AnchorPane rootPane, TreeNode node, int layer, double currentX,
                          int offsetX, int offsetY, int boxWidth, int boxHeight, int layerAmount) {

        final double nodeRadius = calculateNodeRadius(boxWidth, boxHeight);
        this.boxHeight = boxHeight;
        this.boxWidth = boxWidth;
        this.boxOffsetX = offsetX;
        this.boxOffsetY = offsetY;

        // Calculate dimensions - width is a denominator, so actual width is boxWidth / node.getWidth()
        double layerHeight = boxHeight / (double)layerAmount;
        double nodePixelWidth = boxWidth / node.getWidth();

        // Check if this is a leaf node
        boolean isLeaf = (node.getChildren() == null || node.getChildren().isEmpty());

        // If it's a leaf, extend the box to the bottom layer
        double boxHeight_actual;
        if(isLeaf) {
            // Calculate how many layers remain from current layer to the bottom
            int remainingLayers = layerAmount - layer;
            boxHeight_actual = layerHeight * remainingLayers;
        } else {
            boxHeight_actual = layerHeight;
        }

        // Draw this node's box
        Rectangle rect = new Rectangle(nodePixelWidth, boxHeight_actual);
        rect.setLayoutX(offsetX + boxWidth - currentX-nodePixelWidth);
        rect.setLayoutY(offsetY + boxHeight - layer * layerHeight - boxHeight_actual);
        rect.setFill(null);
        rect.getStyleClass().add("rounded-rect");
        rootPane.getChildren().add(rect);


        //Draw circle at the center of the box
        Circle circle = new Circle(nodeRadius);
        node.setXPos(offsetX + boxWidth - nodePixelWidth / 2 - currentX);
        circle.setCenterX(node.getXPos());
        if(isLeaf){
            node.setYPos(offsetY + boxHeight - layer * layerHeight - boxHeight_actual +layerHeight/2);
            circle.setCenterY(node.getYPos());
        }
        else{
            node.setYPos(offsetY + boxHeight - layer * layerHeight - boxHeight_actual/2);
            circle.setCenterY(node.getYPos());
        }
        circle.setFill(null);
        circle.getStyleClass().add("glass-circle");
        rootPane.getChildren().add(circle);


        //Drawing lines to parent node (if not root)
        if(node.getParent() != null){
            Line line = new Line();
            line.setStartX(node.getParent().getXPos());
            line.setStartY(node.getParent().getYPos()-nodeRadius);
            line.setEndX(node.getXPos());
            line.setEndY(node.getYPos()+nodeRadius);
            line.setStroke(Color.WHITE);
            line.setStrokeWidth(1);
            rootPane.getChildren().add(line);
        }

        // Draw children (only if not a leaf)
        if(!isLeaf) {
            double childX = currentX;
            for(TreeNode child : node.getChildren()) {
                drawReversedNode(rootPane, child, layer + 1, childX, offsetX, offsetY, boxWidth, boxHeight, layerAmount);
                double childPixelWidth = boxWidth / child.getWidth();
                childX += childPixelWidth;
            }
        }
    }

    public boolean checkIfTreeCanBeFlipped(){
        if(!isTreeLoaded) return false;

        int[] widths = getLastLayerWidths();
        if(widths != null){
            for(int i = 0; i < widths.length/2; i++){
                if(widths[i] != widths[widths.length-1-i]){
                    return false; // Tree cannot be flipped
                }
            }
        }
        else{
            System.out.println("Error calculating last layer widths.");
            return false;
        }
        return true;
    }

    private int[] getLastLayerWidths(){
        if(tree == null || !isTreeLoaded) return null;

        java.util.ArrayList<Integer> widths = new java.util.ArrayList<>();

        collectLastLayerWidths(tree.getRootTreeNode(), widths);

        int[] widthsArray = new int[widths.size()];
        for(int i = 0; i < widths.size(); i++){
            widthsArray[i] = widths.get(i);
        }

        return widthsArray;
    }

    private void collectLastLayerWidths(TreeNode node, java.util.ArrayList<Integer> widths){
        //checking if current node is a leaf (even if this node isn't in the last layer, leaf node will extend to last layer when drawn)
        boolean isLeaf = (node.getChildren() == null || node.getChildren().isEmpty());

        if(isLeaf){
            widths.add((int)node.getWidth());
        }
        else{
            for(TreeNode children: node.getChildren()){
                collectLastLayerWidths(children, widths);
            }
        }
    }

    private double calculateNodeRadius(int boxWidth, int boxHeight){
        if(tree == null || !isTreeLoaded) return 0;

        int layerAmount = tree.getLayers();
        if(layerAmount == 0) return 0;
        double maxWidth = findMaxWidth(tree.getRootTreeNode());

        double smallestBox = boxWidth/maxWidth;
        double layerHeight = boxHeight/(double)layerAmount;

        double maxRadiusFromWidth = smallestBox/2.0;
        double maxRadiusFromHeight = layerHeight/2.0;

        return Math.min(maxRadiusFromWidth, maxRadiusFromHeight)*0.5;
    }

    private double findMaxWidth(TreeNode node){
        double maxWidth = node.getWidth();
        if(node.getChildren() != null && !node.getChildren().isEmpty()) {
            for(TreeNode child : node.getChildren()) {
                double childMaxWidth = findMaxWidth(child);
                if(childMaxWidth > maxWidth) {
                    maxWidth = childMaxWidth;
                }
            }
        }

        return maxWidth;
    }

    public boolean canBeEdited(){
        return canBeEdited;
    }

    public void makeEditable(boolean isEditable){
        canBeEdited = isEditable;
    }

    public boolean isTreeFlipped(){ return isFlipped;}

    public void unloadTree(){
        if(isTreeLoaded){
            tree = null;
            isTreeLoaded = false;
            canBeEdited = false;
        }
    }

    public void drawRedLine(AnchorPane rootPane, double startX, double startY, double endX, double endY, double strokeWidth){
        Line line = new Line();
        line.setStartX(startX);
        line.setStartY(startY);
        line.setEndX(endX);
        line.setEndY(endY);
        line.getStyleClass().add("glowing-line");
        rootPane.getChildren().add(line);

    }

    public String getTreeString(){
        if(!isTreeLoaded) return "";
        StringBuilder sb = new StringBuilder();
        buildTreeString(tree.getRootTreeNode(), sb);
        return sb.toString();
    }

    public void buildTreeString(TreeNode node, StringBuilder sb){
        sb.append('(');
        for(TreeNode child : node.getChildren()){
            buildTreeString(child, sb);
        }
        sb.append(')');
    }

}
