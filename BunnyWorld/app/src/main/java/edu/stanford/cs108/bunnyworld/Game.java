package edu.stanford.cs108.bunnyworld;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.util.Log;

import com.fasterxml.jackson.core.JsonProcessingException;

import org.json.JSONException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class Game {
    private List<Page> pageList;
    private Page curPage;
    private Page prevPage;
    private String currGameName;


    public Game() {
        this.pageList = new ArrayList<>();
    }

    public Game(List<Page> pageList) {
        this.pageList = pageList;
    }

    public List<Page> getPageList(){
        return pageList;
    }

    public String getcurrGameName(){
        return currGameName;
    }

    public Page getPage(String pageName){
        for(Page p: pageList){
            if(p.getName().equals(pageName)){
                return p;
            }
        }
        return null;
    }

    public Page getCurPage(){
        return curPage;
    }

    //static
//    public static Page CurPage  = new Page("curPage");

    public void setCurPage(String pageName){
        if (curPage != null) {
            curPage.isCurPage = false;
        }
        Page p = getPage(pageName);
        if(p != null){
            prevPage = curPage;
            p.isCurPage = true;
            curPage = p;
        }
    }

    public void setCurPage(Page page){
        if (curPage != null) {
            curPage.isCurPage = false;
        }
        prevPage = curPage;
        page.isCurPage = true;
        curPage = page;
    }

    public void addPage(Page page) {
        this.pageList.add(page);
    }




    /*Game data into Stroage Benson Zu
     * import Game into a .JSON
     * */
    public void loadGameintoJSON(Context context, String gameName) throws IOException, JSONException {
        Log.d("loadGameintoJSON", "Start loadGameintoJSON");

        JSONObject gameJSON = new JSONObject();
        HashMap<String, String> GameMap = new HashMap<>();
        GameMap.put("gameName", gameName);
        GameMap.put("pages", getPagesListString());
        Log.d("loadGameintoJSON", "Start putting inventory");
        GameMap.put("inventory", getInventoryJSON());
        Log.d("loadGameintoJSON", "Finished putting inventory");
        GameMap.put("curPageID", String.valueOf(getCurPageId()));
        Log.d("loadGameintoJSON", "Finished putting curPageID");

        //Update the GameJSON
        gameJSON.putAll(GameMap);

//        ObjectMapper mapper = new ObjectMapper();
//        String gameJSONString = "";
//        gameJSONString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(gameJSON);
        String gameString = Page.prettyJSON(gameJSON);

        loadGameJSONIntoFile(context, gameName, gameString);

        addGameNameToList(context, gameName);

        Log.d("loadGameintoJSON", "Finish loadGameintoJSON "+gameName);
    }

    public int getCurPageId(){
        Log.d("getCurPageId","Start getCurPageId");
        for (Page i:pageList){
            if (i.getIsCurPage()==true){
                return i.getPageId();
            }
        }
        Log.d("getCurPageId","Not find curPageId");
        return 0;
    }

    public static boolean fileExists(Context context, String fileName) {
        String path = context.getFilesDir().getAbsolutePath() + "/" + fileName;
        File test = new File(path);
        return test.exists();
    }

    public static void loadGameJSONIntoFile(Context context, String game, String gameJSONString) throws IOException {
        Log.d("loadGameJSONIntoFile", "Start loadGameJSONIntoFile");

        String fileName = game + ".json";
        if (!fileExists(context, fileName)) { // create file
            new File(context.getFilesDir(), fileName);
        }
        FileOutputStream outputstream = context.openFileOutput(fileName, MODE_PRIVATE);
        outputstream.write(gameJSONString.getBytes());
        outputstream.close();
        Log.d("loadGameintoJSON", gameJSONString);
        Log.d("loadGameJSONIntoFile", "Finish loadGameJSONIntoFile");
    }

    /*Helper function for getPagesListString()
     *  return the string JSON form of all shapes
     * in the shapeList */
    private String getPagesListString() throws JSONException, JsonProcessingException {
        String[] pageListJSONString = new String[pageList.size()];
        for (int i = 0; i < pageList.size();i++){
            pageListJSONString[i] = pageList.get(i).getPageJSON();
        }
        String pageJSONString = Page.prettyJSON(pageListJSONString);
        return pageJSONString;
    }

    private String getInventoryJSON() throws JsonProcessingException, JSONException {
        Log.d("getInventoryJSON","Start getInventoryJSON");
        if (curPage != null && curPage.getInventory() != null){
            String[] inventoryListJSONString = new String[curPage.getInventory().size()];
            Log.d("getInventoryJSON","Start Looping");
            for (int i = 0; i < curPage.getInventory().size();i++){
                inventoryListJSONString[i] = curPage.getInventory().get(i).getShapeJSON();
            }
            Log.d("getInventoryJSON","Finish Looping");
            String inventoryString = Page.prettyJSON(inventoryListJSONString);
            Log.d("getInventoryJSON","Ending getInventoryJSON");
            return inventoryString;
        }
        return "[]";
    }




    /* Read the JSON into the game
     * loadJSONIntoGame
     *
     *
     * */
    public void loadJSONIntoGame(Context context, String gameName) throws IOException, JSONException {
        Log.d("loadJSONIntoGame", "Start loadJSONIntoGame");
        //load into
        Log.d("loadJSONIntoGame", "clear pagelist");
        pageList.clear();
        Log.d("loadJSONIntoGame", "loadJSONIntoGame -> getStringFromJSONFile");
        String gameString = getStringFromJSONFile(context, gameName+".json");
        loadDataFromGameString(context, gameString);
        Log.d("loadJSONIntoGame", "Set the game name to "+gameName);
        currGameName = gameName;
        Log.d("loadJSONIntoGame", "Ending loadJSONIntoGame");
    }


    public void loadDataFromGameString(Context context, String gameString) throws IOException, JSONException {
        pageList.clear();

        Log.d("loadDataFromGameString", "Start loadDataFromGameString");
        if (gameString.equals("")){
            Log.d("loadDataFromGameString", "The game name can't be Empty");
            return;
        }
        JSONParser parser = new JSONParser();
        Log.d("loadDataFromGameString", "Begin parsering");
        try{
            JSONObject gameData = (JSONObject) parser.parse(gameString);
            JSONArray gamePages = (JSONArray) gameData.get("pages");
            Iterator<JSONObject> it = gamePages.iterator();

            Log.d("loadDataFromGameString", "Begin Iterating Pages");

            while (it.hasNext()) {
                JSONObject gamePage = it.next();
                String pageName = (String) gamePage.get("pageName");
                int gamePageId = Integer.parseInt((String) gamePage.get("pageId"));
                boolean isCurPage = (boolean) gamePage.get("isCurPage");


                //initial a new page
                Page newPage = new Page(pageName, this);
                newPage.setPageId(gamePageId);
                newPage.setIsCurPage(isCurPage);

                //BENSON ZU NEW 0310
                String BGM = (String) gamePage.get("BGM");
                Log.d("BGM","BGM is"+BGM);
                if (BGM != null) newPage.setBGM(BGM);
//
                Log.d("bgsetup","is there a pg");

                if (gamePage.get("bg")!= null){
                    Log.d("bgsetup","there is a pg");
                    List<Shape> bgshapes = loadJSONShapeIntoList((JSONArray) gamePage.get("bg"),newPage,context);
                    Log.d("bgsetupbg", String.valueOf(bgshapes));
                    if (!String.valueOf(bgshapes).equals("[]")){
                        Log.d("bgsetsetup","there is a pg");
                        newPage.setBG(bgshapes.get(0));
                    }
                }

//               if (bgshapes!=null) Log.d("bgsetupbg", bgshapes.get(0).getName());
//                newPage.setBG(bgshapes.get(0));
                //BENSON ZU NEW 0310

                Log.d("loadDataFromGameString", pageName+": Add shapes");
                List<Shape> shapes = loadJSONShapeIntoList((JSONArray) gamePage.get("shapes"),newPage,context);
                Log.d("loadDataFromGameString", pageName+": Finish Add shapes");
                newPage.setShapeList(shapes);



                Log.d("getCurPageId","Set Current Page");
                if (isCurPage){setCurPage(newPage);}

                Log.d("loadDataFromGameString", "Add Page"+pageName);
                pageList.add(newPage);
            }
            Log.d("loadDataFromGameString", "Finish parsering Page");

            JSONArray inventory = (JSONArray) gameData.get("inventory");
            pageList.get(0).setInventory(loadJSONShapeIntoList(inventory, null, context));

        } catch (ParseException e) {
            Log.d("loadDataFromGameString", "Get a Exceptional Error");
            e.printStackTrace();
        }
        Log.d("loadDataFromGameString", "Ending loadDataFromGameString");

    }
    public Shape loadJSONShapeIntoShape(JSONObject pageShape, Page page, Context context){
        Log.d("loadJSONShapeIntoShape", "Begin loadJSONShapeIntoShape");
        //Construct a shape
        String name = (String) pageShape.get("name");
        int parentPageID = pageShape.get("pageId") != null ? Integer.parseInt((String) pageShape.get("pageID")): -1;
        float x = Float.parseFloat((String) pageShape.get("x"));
        float y = Float.parseFloat((String) pageShape.get("y"));
        float width = Float.parseFloat((String) pageShape.get("width"));
        float height = Float.parseFloat((String) pageShape.get("height"));
        Shape shape = new Shape(name,x,y, width, height, context, page);
        String imgName = (String) pageShape.get("imgName");
        if (!imgName.equals("")){
            Log.d("loadJSONShapeIntoShape", "imgName is not empty "+imgName);
            shape.setImgName(imgName);
            Log.d("loadJSONShapeIntoShape", imgName+" is adding scripts");
        }

        Log.d("loadJSONShapeIntoShape", "Start Adding Scripts");

        //shape class
        String text = (String) pageShape.get("text");
        Log.d("loadJSONShapeIntoShape", "Finish loadJSONShapeIntoShape"+name+" is created");
        return shape;
    }

    public ArrayList<Shape> loadJSONShapeIntoList(JSONArray shapesJSONArray, Page page, Context context){
        Log.d("loadJSONShapeIntoList", "Begin loadJSONShapeIntoPageShapesList");

        ArrayList<Shape> gamePageShapes = new ArrayList<>();
        Iterator<JSONObject> it = shapesJSONArray.iterator();

        Log.d("loadJSONShapeIntoList", "Begin Iterating Shapes JSON list");
        while (it.hasNext()) {
            Log.d("pageShapeIamhere","will show pageShape");
            JSONObject pageShape = it.next();
            Log.d("pageShapeIamhere",Page.prettyJSON(pageShape));
            //Construct a shape
            String name = (String) pageShape.get("name");
            int parentPageID = pageShape.get("pageId") != null ? Integer.parseInt((String) pageShape.get("pageID")): -1;
            float x = Float.parseFloat((String) pageShape.get("x"));
            float y = Float.parseFloat((String) pageShape.get("y"));
            float width = Float.parseFloat((String) pageShape.get("width"));
            float height = Float.parseFloat((String) pageShape.get("height"));
            List<String> scriptsList = (List<String>) pageShape.get("scripts");
            boolean isVisible = (boolean) pageShape.get("isVisible");
            boolean isInPossession = (boolean) pageShape.get("isInPossession");
            boolean isMovable = (boolean) pageShape.get("isMovable");

//            //benson zu new 0310
            Log.d("loadJSONShapeIntoList", "isBold"+String.valueOf(pageShape.get("isBold")));
            boolean isBold = (boolean) pageShape.get("isBold");
            boolean isItalic = (boolean) pageShape.get("isItalic");
            boolean isUnderline = (boolean) pageShape.get("isUnderline");
            //benson zu new 0310

            Log.d("loadJSONShapeIntoList", name+" is extracted");

//            Log.d("loadJSONShapeIntoList", String.valueOf(pageList.get(parentPageID).getPageId()));
            Shape shape = new Shape(name,x,y, width, height, context, page);

            Log.d("loadJSONShapeIntoList", name+" is initilaized");
            shape.setVisible(isVisible);
            shape.setInPossession(isInPossession);
            shape.setMovable(isMovable);

            Log.d("loadJSONShapeIntoList", name+" Add Image Name");
            String imgName = (String) pageShape.get("imgName");
            if (!imgName.equals("")){
                Log.d("loadJSONShapeIntoList", "imgName is not empty "+imgName);
                shape.setImgName(imgName);
                Log.d("loadJSONShapeIntoList", imgName+" is adding scripts");
            }

            Log.d("loadJSONShapeIntoList", "Start Adding Scripts");
            //set the actions scripts
            for (String script:scriptsList){
                Log.d("loadJSONShapeIntoList", script);
                shape.parseScript(script);
            }
            Log.d("loadJSONShapeIntoList", "Finish Reading Scripts");

            //shape class
            String text = (String) pageShape.get("text");
            if (!text.equals("")){
                Log.d("loadJSONShapeIntoList", "Start reading text obj "+text);
                float fontSize = Float.parseFloat((String) pageShape.get("fontSize"));

                shape.setText(text);//this one is only used for
                //TODO: need other method to ini shape! API is below:
                //Benson ZU new 0310
                shape.setBold(isBold);
                shape.setItalic(isItalic);
                shape.setUnderline(isUnderline);
                shape.applyTextSet();
                //Benson ZU new 0310
            }
            Log.d("loadJSONShapeIntoList", name+" is created");
            gamePageShapes.add(shape);
            Log.d("loadJSONShapeIntoList", name+" is added");
        }
        return gamePageShapes;
    }

    public static String getStringFromJSONFile(Context context, String fileName){
        if (fileName.equals("gameName")){
            Log.d("getStringFromJSONFile","gameNames.txt this time");
        }

        Log.d("getStringFromJSONFile","Start getStringFromJSONFile");
        if (!fileExists(context, fileName)){
            new File(context.getFilesDir(), fileName);
        }
        Log.d("getStringFromJSONFile","filename "+fileName);
        if (fileName.equals(".json")){
            Log.d("getStringFromJSONFile","Empty File Name");
            return "";
        }


        StringBuilder gameStringData = new StringBuilder();
        try{
            FileInputStream inputStream = context.openFileInput(fileName);
            BufferedReader inputStreamReader = new BufferedReader(new InputStreamReader(inputStream));
            String gameStringLine;
            while ((gameStringLine = inputStreamReader.readLine()) != null) {
                gameStringData.append(gameStringLine);
            }
            inputStream.close();
        } catch (Exception e){
            e.printStackTrace();
            Log.d("getStringFromJSONFile","Error");
        }
        Log.d("getStringFromJSONFile",gameStringData.toString());
        Log.d("getStringFromJSONFile","Finish Reading data from File");
        return gameStringData.toString();
    }



    /* Use a gameName.txt file to store all games we have
     * It will have a default.json inside, new game will be added
     * if deleted, it will be update as will*/
    final static String gameListFileName = "gameNames.txt";
    private void addGameNameToList(Context context, String gameName){
        Log.d("addGameNameToList", "Starting addGameNameToList");
        try{
            if (!fileExists(context, gameListFileName)){
                new File(context.getFilesDir(), gameListFileName);
            }

            String nameListString = getStringFromJSONFile(context, gameListFileName);
            Log.d("addGameNameToList", "Finish reading getStringFromJSONFile(txt)");
            boolean isNameExist = false;
            String[] nameList = nameListString.split(" ");
            for (String name:nameList){
                if (gameName.equals(name)){
                    isNameExist = true;
                    break;
                }
            }
            if (!isNameExist){
                Log.d("addGameNameToList", "Detect nonexistence so we update the list");
                if (!nameListString.isEmpty()) nameListString += " ";
                nameListString += gameName;

                FileOutputStream outputStream = context.openFileOutput(gameListFileName, MODE_PRIVATE);
                outputStream.write(nameListString.getBytes());
                outputStream.close();
                Log.d("addGameNameToList", "Finish addGameNameToList");
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    //BENSON_NEW===============================================================
    /* Get all names from the gameList.JSON and return to a Arraylist of String
     * */
    public static ArrayList<String> getGameNamesToList(Context context){
        //create a string list for gamesName
        ArrayList<String> gamesList = new ArrayList<>();
        Log.d("getGameNameToList", "Starting getGameNameToList");
        try{
            if (!fileExists(context, gameListFileName)){
                new File(context.getFilesDir(), gameListFileName);
            }

            String nameListString = getStringFromJSONFile(context, gameListFileName);
            Log.d("getGameNameToList", "Finish reading getStringFromJSONFile(txt)");
            String[] nameList = nameListString.split(" ");

            for (String name:nameList){
                gamesList.add(name);
            }

        } catch (Exception e){
            e.printStackTrace();
        }
        return gamesList;
    }

    /* Delete the game*/
    /*Delte the .JSON file of a game
     */
    public static void deleteGameJSONFile(Context context, String deleteGameName) {
        File toDeleteFile = new File(context.getFilesDir(), deleteGameName + ".json");
        toDeleteFile.delete();
        Log.d("deleteGameJSONFile","Finish deleteGameJSONFile");
    }

    public static void deleteGame(Context context, String gameName){
        deleteGameJSONFile(context,gameName); //delete the .json
        //delete the gameName from txt

        Log.d("deleteGame","Start delete Game from both json and txt");
        try{
            if (!fileExists(context, gameListFileName)){
                new File(context.getFilesDir(), gameListFileName);
            }
            Log.d("deleteGame", "Finish reading getStringFromJSONFile(txt)");

            StringBuilder newGameNames = new StringBuilder();
            ArrayList<String> gamesList0 = Game.getGameNamesToList(context);

            boolean start = true;
            for (String name:gamesList0){
                if (name.equals(gameName)){
                    continue;
                } else{
                    if (!start) newGameNames.append(" ");
                    newGameNames.append(name);
                    start = false;
                }
            }

            FileOutputStream outputStream = context.openFileOutput(gameListFileName, MODE_PRIVATE);
            outputStream.write(newGameNames.toString().getBytes());
            outputStream.close();
            Log.d("deleteGame","Finish deleteGame");
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    //DELETE ALL GAMES BACK TO DEFAULT ONES
    public static void deleteAll(Context context){
        Log.d("deleteAll","start deleteAll");
        ArrayList<String> gamesList0 = Game.getGameNamesToList(context);
        if (gamesList0.size()==0){
            Log.d("deleteAll","nothing to delete");
            return;
        }
        for (String name:gamesList0){
            deleteGame(context, name);
        }
        try{FileOutputStream outputStream = context.openFileOutput(gameListFileName, MODE_PRIVATE);
            outputStream.write("".getBytes());
            outputStream.close();
            Log.d("deleteGame","Finish deleteGame");
        } catch (Exception e){
            e.printStackTrace();
        }
    }
    final static String defaultGameName = "bunnyworldg2023";
    final static String defaultGameName1 = "bunnyworldg2023_extension";
    //BENSON_NEW===============================================================
}
