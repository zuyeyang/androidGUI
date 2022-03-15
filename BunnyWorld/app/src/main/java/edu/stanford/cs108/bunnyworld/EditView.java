package edu.stanford.cs108.bunnyworld;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.MediaPlayer;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EditView extends View {
    private boolean isClick;
    private Page previousPage, currentPage;
    private float x1, y1, x2, y2;
    private float top, left, bottom, right;
    private Paint possessionsLine;
    private Game currentGame;
    private Shape underLayerShape;
    private Shape clickedShape;
    private float edgeW;
    private String pageName;
    private boolean onEdge = false;

    private float viewWidth, viewHeight;
    private GameSingleton gameSingleton;
    private MediaPlayer mp; // final new

    public EditView(Context context, AttributeSet attrs) throws IOException, JSONException {
        super(context, attrs);
        init();
    }

    private void init() throws IOException, JSONException {
        //Test
        //LOAD THE GAME BY USING loadJSONIntoGame(Context context, String fileName)
//        currentGame = new Game();
//        currentGame.loadJSONIntoGame(this.getContext(),"ABensonTestGame");



//        Page testPage = new Page("test", currentGame);
//        Page testPage2 = new Page("test2", currentGame);
//        Shape testShape = new Shape("text", 250, 100, 100, 100,
//                getContext(), testPage);
//        Shape testShape2 = new Shape("carrot", 100, 100, 100, 100,
//                getContext(), testPage);
//        Shape testShape3 = new Shape("greyBox", 100, 100, 100, 100,
//                getContext(), testPage2);
//        Shape testShape4 = new Shape("rect", 100, 100, 100, 100,
//                getContext(), testPage);
//        testShape2.setImgName("carrot");
//        testShape.parseScript("on click play evillaugh;");
//        testShape.parseScript("on click goto test2;");
//        testShape2.parseScript("on drop text play evillaugh;");
//        testShape2.parseScript("on drop rect play evillaugh;");
//        testShape.setText("no leetcode then dog");
//        testShape3.parseScript("on click goto test;");
//        testShape3.parseScript("on enter play evillaugh;");
//        testShape.setFontSize(100f);
//        testPage.addShape(testShape);
//        testPage.addShape(testShape2);
//        testPage.addShape(testShape4);
//        testPage2.addShape(testShape3);

//        Game newGame = new Game();
//        currentGame = newGame;
//        currentGame.addPage(testPage);
//        currentGame.addPage(testPage2);
//        currentGame.setCurPage(testPage);
        //SAVE THE GAME loadGameintoJSON(Context, context, String fileName)
        currentGame = GameSingleton.getGameInstance().getGame();

        currentPage = currentGame.getCurPage();
        possessionsLine = new Paint();
        possessionsLine.setColor(Color.BLACK);
        possessionsLine.setStyle(Paint.Style.STROKE);
        possessionsLine.setStrokeWidth(5.0f);
        edgeW = 10;

        gameSingleton = GameSingleton.getGameInstance();

    }

    private static String gameName;
    public static void setGameName(String input){
        gameName = input;
    }

//    @Override
//    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
//        super.onSizeChanged(w, h, oldw, oldh);
//
//        viewWidth = w;
//        viewHeight = h * 0.5f;
//    }


    private float leftx, rightx, topy, boty;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        currentPage = currentGame.getCurPage();
        currentPage.setBoundaryHeight(viewHeight);

        if (previousPage == null || !previousPage.getName().equalsIgnoreCase(currentPage.getName())) {
            if (gameSingleton.getSelectedShape() != null) {
                gameSingleton.getSelectedShape().setSelected(false);
                gameSingleton.setSelectedShape(null);
            }
            // final new
            playBGM(currentPage.getBGM());
            clickedShape = null;
        }


        if (clickedShape != null) {
            for (Shape shape: currentPage.getShapeList()) {
                if (shape.isDroppable(clickedShape.getName())) {
                    shape.drawGreenOutline();
                }
            }
        }

        previousPage = currentPage;
        currentPage.draw(canvas);
        canvas.drawLine(0.0f, viewHeight,
                viewWidth, viewHeight, possessionsLine);
    }


    //final new
    private void playBGM(String bgm) {
        if (mp != null) mp.stop();
        if (bgm.equals("")) return;
        int soundId = getContext().getResources().getIdentifier(
                bgm, "raw", getContext().getPackageName());
        mp = MediaPlayer.create(getContext(), soundId);
        mp.start();
    }
    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        if (visibility != View.VISIBLE) {
            if (mp != null) {
                mp.stop();
                mp.release();
                mp = null;
            }
//            System.out.println(GameSingleton.getGameInstance().getSelectedShape() != null);
//            if (GameSingleton.getGameInstance().getSelectedShape() != null) {
//                GameSingleton.getGameInstance().getSelectedShape().setSelected(false);
//            }
//            GameSingleton.getGameInstance().setSelectedShape(null);
            super.onWindowVisibilityChanged(visibility);
        } else {
            super.onWindowVisibilityChanged(visibility);
            playBGM(currentPage.getBGM());
        }
    }


    private boolean insideShape = false;
    private boolean isResize = false;

    private float xDistance, yDistance;
    private long startTime = 0;
    private long moveTime = 0;
    private long endTime = 0;


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                System.out.println("clicked + 1");
                x1 = event.getX();
                y1 = event.getY();

                clickedShape = currentPage.selectShape(x1, y1);
                startTime = System.currentTimeMillis();
                if (clickedShape != null) {
                    gameSingleton.setSelectedShape(clickedShape);//can be written in page class
                    insideShape = true;
                    xDistance = clickedShape.getX() - x1;
                    yDistance = clickedShape.getY() - y1;

                    leftx = clickedShape.getX();
                    rightx = clickedShape.getX() + clickedShape.getWidth();
                    topy = clickedShape.getY();
                    boty = clickedShape.getY() + clickedShape.getHeight();
                }

//                if ((x1 > leftx && x1 < rightx && y1 > topy && y1 < boty) &&
//                        !(x1 > leftx + edgeW && x1 < rightx - edgeW && y1 > topy + edgeW && y1 < boty - edgeW)) {
//                    onEdge = true;
//                }

                invalidate();
                break;

            case MotionEvent.ACTION_MOVE:
                x2 = event.getX();
                y2 = event.getY();
                if (insideShape) {
                    currentPage.updateCurShape(x2 + xDistance, y2 + yDistance);
                    invalidate();
                }
                break;

            case MotionEvent.ACTION_UP:
                x2 = event.getX();
                y2 = event.getY();
                endTime = System.currentTimeMillis();
                if ((endTime - startTime) > 200) {
                    isClick = false;
                } else {
                    isClick = true;
                }

                if (x1 > x2) {
                    left = x2;
                    right = x1;
                } else {
                    left = x1;
                    right = x2;
                }

                if (y1 > y2) {
                    top = y2;
                    bottom = y1;
                } else {
                    top = y1;
                    bottom = y2;
                }


                if (clickedShape != null && insideShape) {
                    boolean overlapped = false;
                    //Check inventory:
                    float newX = x2 + xDistance;
                    float newY = y2 + yDistance;
                    if (isClick) {
                        clickedShape.clicked();
                    }

                    //Zhichao Sun 03072022---------------------------------------------------------------
                    Shape underShape = checkOverlap(clickedShape.getName(), newX, newY, clickedShape.getWidth(), clickedShape.getHeight());
                    if (underShape != null) {
                        currentPage.overLap(underShape, clickedShape, false,x1 + xDistance, y1 + yDistance);
                    } else if (newX < 0 || newX + clickedShape.getWidth() > viewWidth || newY < 0 || newY + clickedShape.getHeight() > viewHeight) {
                        currentPage.updateCurShape(x1 + xDistance, y1 + yDistance);
                    } else {
                        currentPage.updateCurShape(newX, newY);
                    }
                    insideShape = false;
                    invalidate();
                    //Zhichao Sun 03072022---------------------------------------------------------------

                }
        }
        return true;
    }
    //Zhichao Sun 03072022---------------------------------------------------------------
    private Shape checkOverlap(String shapeName, float x, float y, float width, float height) {
        String curShapeName = shapeName.toLowerCase();
        float curRight = x + width;
        float curBottom = y + height;
        for (Shape s : currentPage.getShapeList()) {
            String checkShapeName = s.getName().toLowerCase();
            if (!curShapeName.equals(checkShapeName) &&
                    !(x > s.getX() + s.getWidth() ||
                            curRight < s.getX() ||
                            y > s.getY() + s.getHeight() ||
                            curBottom < s.getY())) {
                System.out.println("overlap");
                return s;
            }
        }
        for (Shape s : currentPage.getInventory()) {
            String checkShapeName = s.getName().toLowerCase();
            if (!curShapeName.equals(checkShapeName) &&
                    !(x > s.getX() + s.getWidth() ||
                            curRight < s.getX() ||
                            y > s.getY() + s.getHeight() ||
                            curBottom < s.getY())) {
                System.out.println("overlap");
                return s;
            }
        }
        return null;
    }

    public boolean isOverLap(String shapeName, float x, float y, float width, float height) {
        Shape overlap = checkOverlap(shapeName, x, y, width, height);
        if (overlap != null) {
            System.out.println("overlap");
            return true;
        }
        return false;
    }
    //Zhichao Sun 03072022-----------------
    private float initialx = 5;
    private float initialy = 5;
    private float initialWidth = 100;
    private float initialHeight = 100;
    private boolean initialDup = false;

    // TODO: ZIXUAN
    public void createBG(String filename){
        Shape newShape = new Shape(filename, 0, 0, viewWidth, viewHeight, getContext(), currentPage);
        newShape.setImgName(filename);
        currentPage.setBG(newShape);
        invalidate();
    }
    public void clearBG(){
        currentPage.setBG(null);
        invalidate();
    }

    public void createShape(String shapeContent, boolean textShape) {
        initialDup = false;
        int shapeCount = 0;
        for (Page currPage : currentGame.getPageList()) {
            shapeCount += currPage.getShapeList().size();
        }
        String name = "shape" + String.valueOf(shapeCount + 1);
        while (nameRepeated(null, name)) {
            name = name +"_1";
        }
        Shape newShape = new Shape(name, initialx, initialy, initialWidth, initialHeight,
                getContext(), currentPage);
        if (!shapeContent.equals("") && !textShape) {
            newShape.setImgName(shapeContent);
        } else if (!TextUtils.isEmpty(shapeContent) && textShape) {//when there is no text, will it be a grey box or nothing?
            newShape.setText(shapeContent);
        }
        newShape.applyTextSet();
        currentPage.addShape(newShape);
        invalidate();
    }

    public boolean intialDuplicate() {
        List<Shape> curShapeList = currentPage.getShapeList();
        if (curShapeList.size() == 0) return false;
        Shape lastShape = curShapeList.get(curShapeList.size() - 1);
        initialDup = lastShape.getX() <= initialx + initialWidth
                && lastShape.getY() <= initialy + initialHeight;
        return initialDup;
    }
    //Zhichao Sun 03102022---------------------------------------
    public boolean initialDuplicate() {
        List<Shape> curShapeList = currentPage.getShapeList();
        if (curShapeList.size() == 0) return false;
        for (Shape s : curShapeList) {
            if ( s.getX() <= initialx + initialWidth
                    && s.getY() <= initialy + initialHeight) {
                return true;
            }
        }
        return false;
    }

    //Assume passed in copied shape is already checked.
    public boolean copyDuplicate(Shape copied) {
        if (copied != null) {
            List<Shape> curShapeList = currentPage.getShapeList();
            if (curShapeList.size() == 0) return false;
            for (Shape s : curShapeList) {
                if (s.getX() <= initialx + copied.getWidth()
                        && s.getY() <= initialy + copied.getHeight()) {
                    return true;
                }
            }
        }
        return false;
    }
    //Zhichao Sun 03102022---------------------------------------
    public void pasteShape(Shape shape) {
        //initialDup = false;
        int shapeCount = 0;
        for (Page currPage : currentGame.getPageList()) {
            shapeCount += currPage.getShapeList().size();
        }
        String name = "shape" + String.valueOf(shapeCount + 1);
        while (nameRepeated(null, name)) {
            name = name +"_1";
        }
        Shape newShape = shape.copyShape();
        newShape.setName(name);
        currentPage.addShape(newShape);
        invalidate();
    }
    public void setViewDimension(float viewWidth, float viewHeight) {
        this.viewWidth = viewWidth;
        this.viewHeight = viewHeight;
    }


    //Zhichao Sun 03072022------------------------------------------------------
    public boolean nameRepeated(Shape shape, String shapeName) {
        String curShapeName = shapeName.toLowerCase();
        for (Page p : currentGame.getPageList()) {
            for (Shape s : p.getShapeList()) {
                String checkName = s.getName().toLowerCase();
                if (checkName.equals(curShapeName) && s != shape) {
                    return true;
                }
            }
            for (Shape s : p.getInventory()) {
                String checkName = s.getName().toLowerCase();
                if (checkName.equals(curShapeName) && s != shape) {
                    return true;
                }
            }
        }
        return false;
    }


    //Zhichao Sun 03072022---------------------------------------------------------------


}
