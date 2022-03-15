package edu.stanford.cs108.bunnyworld;

import java.util.*;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;

import com.fasterxml.jackson.core.JsonProcessingException;

import org.json.JSONException;
import org.json.simple.JSONObject;

public class Shape {
    private Page parentPage;

    private String name;
    private float x, y;
    private float width, height;
    private Context context;
    private String imgName = "";
    private String text = "";
    private Canvas canvas;
    //Zhichao Sun 03072022 ---------------------------------------------------------------
    private float backgroundBoundary = 5;
    private int inventoryShapeSize = 85;

    //Zhichao Sun 03072022 ----------------
    //these var can be changed with specified method API
    //Zhichao Sun 03072022 ---------------------------------------------------------------
    private Paint inventoryBackgroud;
    //Zhichao Sun 03072022 --
    private boolean isVisible = true;
    private boolean isInPossession = false;
    private boolean isSelected = false;
    private boolean isMovable = true;
    private boolean isBold = false;
    private boolean isItalic = false;
    private boolean isUnderline = false;

    //Paint method and var
    private Paint greenOutline;
    private Paint grayOutline;
    private Paint textPaint;
    private Paint selectedOutline;
    private Paint transparentPaint;
    private float fontSize = 50.0f;
    private BitmapDrawable curBitmapDrawable;
    private Paint.FontMetrics textFontMetrics;
    private Rect textBound;

    private final List<String> allScripts = new ArrayList<>();
    private final List<Action> onClickActions = new ArrayList<>(); //final new
    private final List<Action> onEnterActions = new ArrayList<>(); //final new

    private final Map<String, List<Action>> onDropActions = new HashMap<>(); //final new


    //constructor
    public Shape(String name, float x, float y, float width, float height, Context context,
                 Page parentPage){
        this.name = name;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.context = context;
        this.parentPage = parentPage;
        // final new (delete)
        //default setting,if want to modify these var, change it later with specified method

        init();

    }

    private void init(){
        this.greenOutline = new Paint();
        this.grayOutline = new Paint();
        this.textPaint = new Paint();
        this.selectedOutline = new Paint();
        this.transparentPaint = new Paint();
        this.inventoryBackgroud = new Paint();

        textBound = new Rect();


        greenOutline.setStyle(Paint.Style.STROKE);
        greenOutline.setColor(Color.GREEN);
        greenOutline.setStrokeWidth(4.0f);

        selectedOutline.setStyle(Paint.Style.STROKE);
        selectedOutline.setColor(Color.BLUE);
        selectedOutline.setStrokeWidth(4.0f);

        grayOutline.setStyle(Paint.Style.STROKE);
        grayOutline.setColor(Color.rgb(69, 69, 69));
        grayOutline.setStrokeWidth(4.0f);

        transparentPaint.setStyle(Paint.Style.FILL);
        transparentPaint.setColor(Color.YELLOW);
        transparentPaint.setAlpha(160);

        textPaint.setTextSize(fontSize);
        textPaint.setColor(Color.BLACK);
        //textPaint.setStyle(Paint.Style.FILL);
        textPaint.setTextAlign(Paint.Align.LEFT);
        textFontMetrics = textPaint.getFontMetrics();

        inventoryBackgroud.setStyle(Paint.Style.FILL);
        inventoryBackgroud.setColor(Color.BLUE);
        inventoryBackgroud.setAlpha(40);


    }

    private void bitmapInit() {

        int imgId = this.context.getResources().getIdentifier(
                this.imgName, "drawable", this.context.getPackageName());
        this.curBitmapDrawable = (BitmapDrawable) this.context.getResources().getDrawable(imgId);
    }

    //Zhichao Sun 03072022 ---------------------------------------------------------------
    public void draw(Canvas canvas){
        System.out.println(context);
        this.canvas = canvas;
        if (!this.context.toString().startsWith("edu.stanford.cs108.bunnyworld.GameMode")) {
            if (text.length() != 0) {
                canvas.drawText(text, x - textBound.left, y - textFontMetrics.ascent, textPaint);
            } else if (text.length() == 0 & imgName.length() != 0) {
                Bitmap output = Bitmap.createScaledBitmap(curBitmapDrawable.getBitmap(),
                        (int) width, (int) height, true);
                canvas.drawBitmap(output, x, y, null);
            } else if (text.length() == 0 & imgName.length() == 0) {
                canvas.drawRect(x, y, x + width, y + height, grayOutline);
            }

            if(!isVisible){
                canvas.drawRect(x, y, x + width, y + height, transparentPaint);
            }
            if(isSelected){
                canvas.drawRect(x, y, x + width, y + height, selectedOutline);
            }
        } else {
            if (!isVisible) {
                return;
            } else if (text.length() != 0) {
                if (isInPossession) {
                    canvas.drawText("Text", x - textBound.left, y - textFontMetrics.ascent, textPaint);
                    canvas.drawRect(x - backgroundBoundary, y - backgroundBoundary,
                            x + inventoryShapeSize + backgroundBoundary,
                            y + inventoryShapeSize + backgroundBoundary, inventoryBackgroud);
                } else {
                    canvas.drawText(text, x - textBound.left, y - textFontMetrics.ascent, textPaint);
                }

            } else if (text.length() == 0 & imgName.length() != 0) {
                Bitmap output;
                if (isInPossession) {
                    output = Bitmap.createScaledBitmap(curBitmapDrawable.getBitmap(),
                            inventoryShapeSize, inventoryShapeSize, true);
                    canvas.drawRect(x - backgroundBoundary, y - backgroundBoundary,
                            x + inventoryShapeSize + backgroundBoundary,
                            y + inventoryShapeSize + backgroundBoundary, inventoryBackgroud);
                } else {
                    output = Bitmap.createScaledBitmap(curBitmapDrawable.getBitmap(),
                            (int) width, (int) height, true);
                }

                canvas.drawBitmap(output, x, y, null);

            } else if (text.length() == 0 & imgName.length() == 0) {
                if (isInPossession) {
                    canvas.drawRect(x - backgroundBoundary, y - backgroundBoundary,
                            x + inventoryShapeSize, y + inventoryShapeSize, grayOutline);
                    canvas.drawRect(x - backgroundBoundary, y - backgroundBoundary,
                            x + inventoryShapeSize + backgroundBoundary,
                            y + inventoryShapeSize + backgroundBoundary, inventoryBackgroud);
                } else {
                    canvas.drawRect(x, y, x + width, y + height, grayOutline);
                }
            }
        }

    }

    public void drawGreenOutline(){
        if(this.context.toString().startsWith("edu.stanford.cs108.bunnyworld.GameMode")) {

            canvas.drawRect(x, y, x + width, y + height, greenOutline);
        }
    }

    public void textShapeSet(){
        this.textPaint.setTextSize(fontSize);
        this.textPaint.setTextAlign(Paint.Align.LEFT);
        if (isBold && isItalic) {
            textPaint.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD_ITALIC));
        } else if (isBold) {
            textPaint.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        } else if (isItalic) {
            textPaint.setTypeface(Typeface.defaultFromStyle(Typeface.ITALIC));
        } else {
            textPaint.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
        }
        if (isUnderline) {
            textPaint.setUnderlineText(true);
        }else{
            textPaint.setUnderlineText(false);
        }
        textFontMetrics = textPaint.getFontMetrics();
        textPaint.getTextBounds(text, 0, text.length(), textBound);

        this.height = textFontMetrics.descent - textFontMetrics.ascent;
        this.width = textBound.width();
    }

    public boolean isOutOfBoundary(String text, float fSize, float maxX, float maxY){
        if(text.length() == 0){
            return false;
        }
        Paint testTextPaint = new Paint();
        testTextPaint.setTextSize(fSize);
        testTextPaint.setTextAlign(Paint.Align.LEFT);
        Paint.FontMetrics testTextFontMetrics = testTextPaint.getFontMetrics();
        Rect testTextBound = new Rect();
        testTextPaint.getTextBounds(text, 0, text.length(), testTextBound);

        float testHeight = testTextFontMetrics.descent - testTextFontMetrics.ascent;
        float testWidth = testTextBound.width();
        if(this.x + testWidth > maxX || this.y + testHeight > maxY || this.x > maxX || this.y > maxY){
            return true;
        }else{
            return false;
        }
    }

    public void applyTextSet(){
        if(this.text.length() == 0){
            return;
        }else{
            textShapeSet();
        }
    }

    public float[] getFutureTextProperty(String text, float fSize, boolean isBold, boolean isItalic, boolean isUnderline){

        Paint testTextPaint = new Paint();
        testTextPaint.setTextSize(fSize);
        testTextPaint.setTextAlign(Paint.Align.LEFT);
        if (isBold && isItalic) {
            testTextPaint.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD_ITALIC));
        } else if (isBold) {
            testTextPaint.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        } else if (isItalic) {
            testTextPaint.setTypeface(Typeface.defaultFromStyle(Typeface.ITALIC));
        }
        if (isUnderline) {
            testTextPaint.setUnderlineText(true);
        }
        Paint.FontMetrics testTextFontMetrics = testTextPaint.getFontMetrics();
        Rect testTextBound = new Rect();
        testTextPaint.getTextBounds(text, 0, text.length(), testTextBound);

        float testHeight = testTextFontMetrics.descent - testTextFontMetrics.ascent;
        float testWidth = testTextBound.width();
        float[] output = new float[4];
        output[0] = this.x;
        output[1] = this.y;
        output[2] = testWidth;
        output[3] = testHeight;
        return output;
    }

    public boolean contains(float x, float y){
        if (isInPossession) {
            return x >= this.x & x <= this.x + inventoryShapeSize & y >= this.y & y <= this.y + inventoryShapeSize;
        }
        return x >= this.x & x <= this.x + width & y >= this.y & y <= this.y + height;
    }

    public float getInventoryShapeSize() {
        return inventoryShapeSize;
    }

    public String getName(){
        return this.name;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getImgName(){
        return this.imgName;
    }

    public void setImgName(String imgName){
        this.imgName = imgName;
        if(!this.imgName.trim().isEmpty()) {
            bitmapInit();
        }
    }

    public float getX(){
        return this.x;
    }

    public float getY(){
        return this.y;
    }

    public float getWidth(){
        return this.width;
    }

    public float getHeight(){
        return this.height;
    }

    public void setX(float x){
        this.x = x;
    }

    public void setY(float y){
        this.y = y;
    }

    public void setWidth(float width){
        this.width = width;
    }

    public void setHeight(float height){
        this.height = height;
    }

    public void updatePosition(float x, float y){
        this.x = x;
        this.y = y;
    }


    public String getText(){
        return this.text;
    }

    public void setText(String text){
        this.text = text;

    }

    public boolean isVisible(){
        return this.isVisible;
    }

    public void setVisible(boolean bool){
        this.isVisible = bool;
    }

    public boolean isSelected(){ return this.isSelected;}

    public void setSelected(boolean bool){
        this.isSelected = bool;
    }


    public boolean isInPossession(){
        return this.isInPossession;
    }

    public void setInPossession(boolean bool){
        this.isInPossession = bool;
    }

    public boolean isMovable(){
        return this.isMovable;
    }

    public void setMovable(boolean bool){
        this.isMovable = bool;
    }

    public void setFontSize(float size){
        this.fontSize = size;
    }

    public float getFontSize(){
        return fontSize;
    }
    public boolean isBold(){
        return this.isBold;
    }

    public void setBold(boolean bool){
        this.isBold = bool;
    }





    public boolean isItalic(){
        return this.isItalic;
    }

    public void setItalic(boolean bool){
        this.isItalic = bool;
    }



    public boolean isUnderline(){
        return this.isUnderline;
    }

    public void setUnderline(boolean bool){
        this.isUnderline = bool;
    }


    public void setContext(Context context){
        this.context = context;
    }

    public Context getContext(){
        return this.context;
    }

    public Page getParentPage() {
        return parentPage;
    }

    public void setParentPage(Page page){
        this.parentPage = page;
    }

    public List<String> getAllScripts() {
        return allScripts;
    }

    public Shape copyShape(){
        Shape outputShape = new Shape("copied" + this.name, 5,5,this.width,this.height,this.context,this.parentPage);
        outputShape.setContext(this.context);
        outputShape.setImgName(this.imgName);
        outputShape.setText(this.text);
        outputShape.setVisible(this.isVisible);
        outputShape.setInPossession(this.isInPossession);
        outputShape.setMovable(this.isMovable);
        outputShape.setBold(this.isBold);
        outputShape.setItalic(this.isItalic);
        outputShape.setUnderline(this.isUnderline);
        outputShape.applyTextSet();
        return outputShape;
    }

    public void parseScript(String script) {
//        script = script.toLowerCase();

        if (script.startsWith("on click")) {
            if (parseActions(script, "on click")) allScripts.add(script);
        } else if (script.startsWith("on enter")) {
            if (parseActions(script, "on enter")) allScripts.add(script);
        } else if (script.startsWith("on drop")) {
            if (parseActions(script, "on drop")) allScripts.add(script);
        }
    }

    private boolean parseActions(String script, String trigger) {
        String[] actions = script.substring(trigger.length() + 1,
                script.length() - 1).split(" ");

        if (trigger.equals("on drop")) {
            String shapeName = actions[0];
            if (onDropActions.containsKey(shapeName)) return false;
            onDropActions.put(shapeName, new ArrayList<>());
            for (int i = 1; i <= actions.length / 2; ++i) {
                Action currAction = new Action(actions[i * 2 - 1], actions[i * 2]);
                onDropActions.get(shapeName).add(currAction);
            }
            return true;
        }

        List<Action> actionList = trigger.equals("on click") ? onClickActions : onEnterActions;
        if (actionList.size() > 0) return false;
        for (int i = 1; i <= actions.length / 2; ++i) {
            Action currAction = new Action(actions[i * 2 - 2], actions[i * 2 - 1]);
            actionList.add(currAction);
        }
        return true;
    }

    public void clearScript(String script) {
        if (script.startsWith("on click")) {
            onClickActions.clear();
            allScripts.remove(script);
        } else if (script.startsWith("on enter")) {
            onEnterActions.clear();
            allScripts.remove(script);
        } else {
            allScripts.remove(script);
            script = script.substring("on drop".length() + 1);
            int spaceAfterShape = script.indexOf(" ");

            onDropActions.remove(script.substring(0, spaceAfterShape));
        }
    }

    public void clicked() {
        for (Action action : onClickActions) {
            action.execute();
        }
    }

    public void entered() {
        for (Action action : onEnterActions) {
            action.execute();
        }
    }

    public void dropped(String shapeName) {
        if (!onDropActions.containsKey(shapeName)) return;
        for (Action action : onDropActions.get(shapeName)) {
            action.execute();
        }
    }

    public boolean isClickable() {
        return onClickActions.size() > 0;
    }

    public boolean isEnterable() {
        return onEnterActions.size() > 0;
    }

    public boolean isDroppable(String shapeName) {
        return onDropActions.containsKey(shapeName);
    }

    protected List<Action> getOnClickActions() { return onClickActions; }

    protected List<Action> getOnEnterActions() { return onEnterActions; }

    protected Map<String, List<Action>> getOnDropActions() { return onDropActions; }

    protected final class Action {
        private final String action;
        private final String objectName;

        public String getAction() {
            return action;
        }

        public String getObjectName() {
            return objectName;
        }


        public Action(String a, String o) {
            action = a;
            objectName = o;
        }

        public void execute() {
            if (!context.toString().startsWith("edu.stanford.cs108.bunnyworld.GameMode")) {
                return;
            }
            switch (action) {
                case "goto":
                    getParentPage().getParentGame().setCurPage(objectName);
                    break;
                case "play":
                    playSound(objectName);
                    break;
                default:
                    Game currGame = getParentPage().getParentGame();
                    for (Page currPage : currGame.getPageList()) {
                        for (Shape currShape : currPage.getShapeList()) {
                            if (currShape.getName().equalsIgnoreCase(objectName)) {
                                currShape.setVisible(action.equals("show"));
                            }
                        }
                        for (Shape currShape : currPage.getInventory()) {
                            if (currShape.getName().equalsIgnoreCase(objectName)) {
                                currShape.setVisible(action.equals("show"));
                            }
                        }
                    }
            }
        }

        private void playSound(String objectName) {
            int soundId = context.getResources().getIdentifier(
                    objectName, "raw", context.getPackageName());
            MediaPlayer mp = MediaPlayer.create(context, soundId);
            mp.start();
        }
    }

    /*Data Stroage/Representation Benson Zu
     * Store the data in form of JSON, we need each shape instance can
     * be converted into a JSON format dictionary
     * --> String getShapeJSON()
     *        -> Input: img instance itself
     *        -> Output: String of JSON
     * --> String getTextJSONHelper()
     *        -> Input: text instance
     *        -> Output: String of JSON that will be putting back to getShapeJSON
     * */

    public String getShapeJSON() throws JSONException, JsonProcessingException {
        //create a empty JSONObject store data in Dictionary
        JSONObject shapeJSON = new JSONObject();
        //use a hashmap to store the data and putAll to the JSON object
        HashMap<String, String> shapeMap = new HashMap<>();
        shapeMap.put("name", name);
        shapeMap.put("imgName", imgName);
        shapeMap.put("parentPageID", String.valueOf(parentPage.getPageId()));
        shapeMap.put("x", String.valueOf(x));
        shapeMap.put("y", String.valueOf(y));
        shapeMap.put("width", String.valueOf(width));
        shapeMap.put("height", String.valueOf(height));
        shapeMap.put("scripts", getAllScriptsString());
        shapeMap.put("isVisible", isVisible? "true":"false");
        shapeMap.put("isInPossession", isInPossession? "true":"false");
        shapeMap.put("isMovable", isMovable? "true":"false");

        shapeMap.put("isBold", isBold? "true":"false");
        shapeMap.put("isItalic", isItalic? "true":"false");
        shapeMap.put("isUnderline", isUnderline? "true":"false");

        shapeMap.put("fontSize", String.valueOf(fontSize));
        //QUESTION: Shouldn't text be a separate class object, otherwise will be a very wired database
        shapeMap.put("text", text);
        /*need to add font, bold... separately*/

        //Update the shapeJSON
        shapeJSON.putAll(shapeMap);
//
//        ObjectMapper mapper = new ObjectMapper();
//        String shapeJSONString = "";
//        shapeJSONString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(shapeJSON);

        return Page.prettyJSON(shapeJSON);
    }

    private String getAllScriptsString() throws JsonProcessingException, JSONException {
        String AllScriptsString = Page.prettyJSON(getAllScripts());
        return AllScriptsString;
    }
}
