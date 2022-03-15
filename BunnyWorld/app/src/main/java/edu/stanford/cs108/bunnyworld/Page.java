package edu.stanford.cs108.bunnyworld;

import android.graphics.Canvas;
import android.util.Log;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONException;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.graphics.Canvas;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;

public class Page {
    private String pageName;
    private int pageId;
    boolean isCurPage;
    private List<Shape> shapeList;
    private static List<Shape> inventory = new ArrayList<>();
    private Shape selectedShape;
    private Game game;
    private float boundaryHeight;
    private Shape bg;

    private float xDistance = 15;
    private float yDistance = 15;
    private float inventoryShapeSize = 85;



    //final new
    private String BGM = "";

    public Page(String pageName, Game game) {
        this.pageName = pageName;
        this.shapeList = new ArrayList<>();
        this.game = game;
        this.bg = null;
    }

    public Page(String pageName){
        this.pageName = pageName;
        this.shapeList = new ArrayList<>();
        this.bg = null;
    }

    public void setBG(Shape shape){
        bg = shape;
        System.out.println("BGNAME");
    }
    public Shape getGB(){
        return bg;
    }

    public void setBoundaryHeight(float H){
        boundaryHeight = H;
    }

    //final new
    public String getBGM() { return BGM; }
    public void setBGM(String bgm) { BGM = bgm; }

    /*Data Stroage/Representation Benson Zu
     *  Add/Set getPageID
     */
    public int getPageId(){
        return pageId;
    }
    public void setPageId(int pageIdInput){
        pageId = pageIdInput;
    }
    public void setPageName(String name) { pageName = name; }

    public void setInventory(List<Shape> inventoryInput){
        inventory = inventoryInput;
    }

    public void setIsCurPage(boolean isCurPageInput){
        isCurPage = isCurPageInput;
    }
    public boolean getIsCurPage(){
        return isCurPage;
    }

    public void addShape(Shape shape){
        shapeList.add(shape);
        shape.setInPossession(false);
    }

    public void removeShape(Shape shape){
        shapeList.remove(shape);
    }

    //FileData to Page (Benson)
    public void setShapeList(List<Shape> shapeListInput){
        shapeList = shapeListInput;
    }

    public static void addToInventory(Shape shape){
        inventory.add(shape);
        shape.setInPossession(true);
    }

    public static void removeInventory(Shape shape){
        inventory.remove(shape);
    }

    public String getName(){
        return pageName;
    }

    public Game getParentGame(){
        return game;
    }

    public void draw(Canvas canvas){
        if (this.bg != null){
            System.out.println("DRAW BG");
            bg.draw(canvas);
        }
        for (Shape i: inventory){
            i.draw(canvas);
        }
        for(Shape s: shapeList){
            s.draw(canvas);
        }
    }

    public void enter(){
        for(Shape s: shapeList){
            if(s.isEnterable()){
                s.entered();
            }
        }
        return;
    }

    //see if any shape in shapeList or inventoryList is selected
    //return shape if it is founded
    //return null if no shape is selected
    public Shape selectShape(float x, float y){
        for(Shape s: shapeList){
            if (s.contains(x, y)){
                unSetSelectedShape();
                setSelectedShape(s);
                return s;
            }
        }
        for(Shape i: inventory){
            if(i.contains(x, y)){
                unSetSelectedShape();
                setSelectedShape(i);
                return i;
            }
        }
        unSetSelectedShape();
        GameSingleton.getGameInstance().setSelectedShape(null);
        return null;
    }

    private void unSetSelectedShape() {
        if (selectedShape != null) {
            selectedShape.setSelected(false);
            selectedShape = null;
        }
    }

    private void setSelectedShape(Shape s){
        s.setSelected(true);
        selectedShape = s;
    }

    //Zhichao Sun 03072022------------------------------------------------------------------------------------------
    private static Map<Shape, Integer> inventoryMap = new HashMap<>();
    private static Queue<Integer> countPQ = new PriorityQueue<>();
    private float leftx, topy;
    private static int count;

    //update position of current shape
    //also need to check if it should be removed from shapeList or inventoryList
    public void updateCurShape(float newX, float newY) {
        boolean inPossessions = isInInventory(newX, newY + selectedShape.getHeight() / 2);
        if (selectedShape == null) {
            return;
        }
        if (selectedShape.isMovable()) {
            //if currently was in inventory while previously was not in inventory
            if (inPossessions && !selectedShape.isInPossession()) {
                shapeList.remove(selectedShape);
                inventory.add(selectedShape);
                selectedShape.setInPossession(true);
                System.out.println("add to inventory");

                if (!countPQ.isEmpty()) {
                    count = countPQ.poll();
                } else {
                    count = inventory.size();
                }
                inventoryMap.put(selectedShape, count);

            }
            //if currently was not in while previously was in
            else if (!inPossessions && selectedShape.isInPossession()) {
                inventory.remove(selectedShape);
                shapeList.add(selectedShape);

                if (inventoryMap.containsKey(selectedShape)) {
                    countPQ.add(inventoryMap.get(selectedShape));
                    inventoryMap.remove(selectedShape);
                }

                selectedShape.setInPossession(false);
                System.out.println("add to shape");
            }

            if (selectedShape.isInPossession()) {
                if (inventoryMap.containsKey(selectedShape)) {
                    int localCount = 0;
                    localCount = inventoryMap.get(selectedShape);
                    leftx = (float) (xDistance + (xDistance + inventoryShapeSize) * (Math.ceil((double) localCount / 2) - 1));
                    if ((double) localCount % 2 != 0) {
                        topy = boundaryHeight + yDistance;
                    } else {
                        topy = boundaryHeight + inventoryShapeSize + yDistance * 2;
                    }
                    selectedShape.updatePosition(leftx, topy);
                }

            } else {
                selectedShape.updatePosition(newX, newY);
            }
        }
        return;
    }
    //Zhichao Sun 03072022-------------------

    public void update(float newX, float newY){
        if(selectedShape != null){
            selectedShape.updatePosition(newX, newY);
        }
        return;
    }


    protected boolean isInInventory(float x, float y){
        //            System.out.println("in inventory");
        return y >= boundaryHeight;
//        System.out.println("not in inventory");
    }


    public List<Shape> getShapeList(){
        return shapeList;
    }

    public List<Shape> getInventory(){
        return inventory;
    }

    // when two shape is overlapped
    // if any ondrop need to be triggered
    public void overLap(Shape prev, Shape cur, boolean inInventory, float x, float y) {
        if (!cur.getContext().toString().startsWith("edu.stanford.cs108.bunnyworld.EditMode")
                && !inInventory && prev.isDroppable(cur.getName())) {
            prev.dropped(cur.getName());
//            shapeList.remove(cur);
            return;
        }
        cur.updatePosition(x, y);
    }



    /*Data Stroage/Representation Benson Zu
    /*Page Stroage Benson Zu
     * import this page in form of JSON
     * --> String getPageJSON()
     *        -> Input: page instance itself
     *        -> Output: String of JSON
     * */
    public String getPageJSON() throws JsonProcessingException, JSONException {
        JSONObject pageJSON = new JSONObject();
        HashMap<String, String> pageMap = new HashMap<>();
        pageMap.put("pageName", pageName);
        pageMap.put("pageId", String.valueOf(pageId));
        pageMap.put("isCurPage", isCurPage? "true":"false");
        pageMap.put("shapes", getshapeListJSONString());
        pageMap.put("BGM",getBGM());
        pageMap.put("bg",getshapepgJSONString());


        //Update the shapeJSON
        pageJSON.putAll(pageMap);

        return prettyJSON(pageJSON);

    }
    /*Helper function for getPageJSON()
     *  return the string JSON form of all shapes
     * in the shapeList */
    private String getshapeListJSONString() throws JSONException, JsonProcessingException {
        String[] shapeListJSONString = new String[shapeList.size()];
        for (int i = 0; i < shapeList.size();i++){
            shapeListJSONString[i] = shapeList.get(i).getShapeJSON();
        }
        return prettyJSON(shapeListJSONString);
    }
    private String getshapepgJSONString() throws JSONException, JsonProcessingException {
        String[] shapeListJSONString;
        if (bg != null){
            shapeListJSONString = new String[1];
            shapeListJSONString[0] = bg.getShapeJSON();
        } else {
            shapeListJSONString = new String[0];
        }

        return prettyJSON(shapeListJSONString);
    }

    public static String prettyJSON(Object jsonObj) {
        /* return pretty print version of json str. */
        /* useful for debugging */
        ObjectMapper mapper = new ObjectMapper();
        String gameString = "";
        try {
            gameString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonObj); // return string format
        } catch (Exception e) {
            Log.d("prettyJSON", "error of JSON to String");
            e.printStackTrace();
        }

        // the horror of https://github.com/tidwall/pretty
        gameString = gameString.replace("\\\"", "\"").replace("\\n", "\n");
        gameString = gameString.replace("\"{", "{").replace( "}\"", "}");
        gameString = gameString.replace("\"[", "[").replace("]\"", "]");
        gameString = gameString.replace("\"null\"", "null");
        gameString = gameString.replace("\"true\"", "true").replace("\"false\"", "false");
        gameString = gameString.replace(", {", ", \n{");

        return gameString;
    }


}
