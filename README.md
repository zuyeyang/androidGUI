# androidGUI
# **Bunny World Program Description**

Emma Sun (emmasun@stanford.edu)  
Zixuan Xu (zixuanxu@stanford.edu)  
Zhan Zhang (zhan1998@stanford.edu)  
Runqiu Zhang (rqzhang@stanford.edu)  
Benson Zu (zuyeyang@stanford.edu)  

## I. OVERVIEW
The project is to design an editor for a graphical adventure game called Bunny World. The users can use the game engine to create, edit, play, and delete games. Besides the predefined actions, we implement editor extensions including error checking, shape fitting in Inventory, copy/paste for shapes, and exporting the game database. We also performed game features extension, such as custom background/music for pages setup and support for rich text (bold, italic, underline, and font size) within a text item.

Our implementation is supposed to work for the landscape orientation of Android Mobile with API later than or equal to 29. The test virtual device of this project is *Nexus 5X API 29.*
## II. GAME MODE FEATURE
#### Game View 
Game View is a custom view that allows the upper 75% area for drawing canvas, and the 25% below for inventory tasks. 

1. Inventory: 
   1. When a shape is on the inventory boundary, if less than half of the shape is above the inventory line, the shape will be bounced back to the draw area right above the inventory line. On the other hand if equal or more than half of the shape is below the inventory line, the shape will be shifted to right below the inventory line.
1. Overlap:
   1. If a shape is on top of another shape and there is no on drop command, the shape will be bounced back to its original position. If overlap and inventory boundary check occurs at the same time, overlap takes precedence and will be checked.
   1. A hidden shape cannot be clicked or moved. However, it cannot be overlapped by other shapes since it could potentially be shown later. In this case, a shape will be bounced back even if there seems nothing there. 
1. Boundary check:
   1. If the shape in the current page is partly outside of the view, it will bounce back to its original location.
#### Game Class 
Game Class is a top-level data structure, which contains a list of paga and stores the current page status. Manage pages of each game, including add page and set current page. Current page is the only visible page during the game mode.
#### Page Class
Page Class is a static inventory list which contains all the shapes in the inventory area and is shared by all the Page instances. Each Page instance contains a shapeList which contains all the shapes in the game area, a unique page name, a background image shape which is null in the default status, and a selected shape which stores the selected shape when page is the current visible page

1. Draw: This method is called by the GameView when the page is the current visible page. And this method will call the draw method of each shape in the order of background image shape, shapeList and inventory list so that the background image is located below all the shapes.
1. selectShape: Called by the GameView when the mouse is down and see if any shape is selected. Return the selected shape and set the selected attribute of the shape.
1. updateCurshape: Update the shapeList and inventory list when a shape is dragged from the game area to the inventory area or when a shape is dragged from the inventory area to the game area. 
1. overLap: Called by the GameView when two shapes are overlapped and check if the shape underneath has an ondrop method for the current shape, and if so, call the ondrop method for that shape. 
#### Shape Class
Shape Class stores every property of a shape, including shape type(text, image, gray rect), shape location, shape size, and other properties, such as visibility, moviability, and so on. A shape instance knows how to draw itself, a shape instance also can tell outside whether it contains the given point.  What’s more, a shape also stores all the information about the script, and has a method to execute the script.

1. Text Shape implement:
1. Text shape is different from an image shape or a default shape. It is hard to calculate text shape size, using getFontMetrics and getTextBounds to get precise width, height. 

1. Draw method: 
1. According to the shape type, every method knows how to draw itself. Different drawing behavior depends on different shape type, different activity, visibility and inventory and so on properties.


1. Script:
   1. Every Shape instance has an onClickActions list, an onEnterActions list, and an onDropActions map, since each shape is only allowed to own one onclick script, one onenter script, but multiple ondrop scripts with different shapes that can be dropped. parseScript() and parseActions() methods take each script that is passed in, parse it to some Action objects which is described below, and construct the action list/map based on the first two words of the script. Accordingly, there are isClickable()/clicked(), isEnterable()/entered(), isDroppable()/dropped() methods for other classes to call to check or execute the scripts. The clearScript() method is designed to support script deletion in Edit Mode, while the allScripts member variable is for database storage.
#### Action Class (Nested in Shape)
Action Class (Nested in Shape is the final nested class nested in Shape designed to store and deal with each action in the scripts associated with each Shape instance. This class is very object-oriented and straightforward, which only has two final member variables, i.e., action (goto/play/hide/show) and objectName (page/sound/shape) and one main method, namely, execute().

1. execute():
1. This method handles the execution of all action types in Game Mode, including “goto” some page, which is realized by set the current page property of the current game, “hide” or “show” some shape, which is accessed through the shape list of the game, and play some sound, which makes the private helper method, playSound(), involved in.
## III. EDIT MODE FEATURE
#### Page related operations: 
1. Create page:
   1. A new Page instance will be created when clicking the “CREATE PAGE” button at the bottom of the screen and this page will automatically be assigned a page name like “page2”, “page3”, etc.
   1. When a new Page instance is created, the current page will automatically be the newly created page. 
   1. Error check: Each Page name is unique and case insensitive. So when the automatically generated page name conflicts with the existing page name, the new page name will be followed by "\_1" to distinguish it.
1. Delete page:
   1. Current page will be deleted when clicking the “DELETE PAGE” button and the EditView will automatically go to the default page1 and display all the shape in page one.
   1. Error check: The default page1 cannot be deleted. Users are not allowed to delete page1 or delete the page when there’s only one page left. A toast will prompt the user that the current page cannot be deleted.
1. Change page
   1. A popup window with a scroll view displaying all the page names will show when clicking the “CHANGE PAGE” button and the user can choose whichever page he wants to go to.
1. Rename page
   1. The user can rename the current page by clicking the “RENAME PAGE” button and rename the page in the popup window. 
   1. Error check: If the user enters a page name that already exists, toast will prompt the user that the page already exists. 
1. Clear page
   1. Clear all the shapes in the current page when clicking the “CLEAR PAGE” button. 
   1. Error check: If the current page is already empty, a toast will prompt the user that the current page is already empty.
#### Create shape:
1. The user can create a shape by choosing different shapes (image, default, and text shape) in the top right “Create Shape” menu. A new shape will be created on the top left corner of the edit view with default height and width of 100, or the width of the text length, and an auto-assigned name. 
1. Error check: 
   1. If there is already a shape in the top left area, a toast will show to indicate to the user that there is an existing shape that needs to be moved before creating a new shape. 
   1. If the auto-created name conflicts with one of the existing shape names, a “\_1” will be added. For instance, there is one shape in a page, and the user changes its name to “shape2”, a newly created shape will be named “shape2\_1”. The next shape will go back to normal “shape 3”.
   1. Shape name is set to be case insensitive. A shape name made up of the same characters that only differs in the upper and lower case will be treated as the same name. If the user input an existing name, an error message will be displayed.
#### Edit shape:
1. Create script:
   1. For onClick and onEnter script creation, the user would be directed to a popup window by clicking the menu items associated with the “Create Script” item in the Edit Current Shape menu list. To avoid errors caused by user typing, we employ spinners selection with adapter to replace text typing. After each selection, the APPEND button should be clicked so that the selected actions could be appended to the current script shown on the top of the popup window. If multiple spinners are selected, they will be appended in order from top to bottom; if other orders are required, the user could use multiple appends instead. The COMPLETE button would be clicked to save the current script to the shape, while the DISCARD button would be clicked if the user is going to discard the current edit. For onDrop script creation, a shape that could be dropped onto the current selected shape should be selected first by choosing from the shape list of the current game in the spinner of the popup window. Then the user would be redirected again to the same script set window as the onClick and onEnter.
1. Modify shape:
1. Show and delete script: Every shape may have multiple on drop script, using spinner to show and to delete on drop script.
1. Set property: Supported to rename, set position, set size, change movability and visibility. Specially, changing size is invalid in text shape mode.
1. Set text: Supported to set text and font size.
1. Change image: Supported to change image with a spinner
1. Delete shape:User can delete selected shape.
1. Immediate error check: When user doesn’t select a shape, every menu that modifies shape will popup a warning window. Every operation that resizes the shape will warn the user if it is going to be out of boundary or overlap. When the user rename the shape, it will warn the user if the name already existed.

1. Error check:
   1. ` `There are many possible errors related to the script creation which are handled at different phases. (1) As the other operations in shape edit, if no shape is selected (shown in blue rect) when clicking onClick/onEnter/onDrop menu, a popup window would display to remind the user to select a shape first. (2) If no object is selected in any spinner when SELECT/APPEND/COMPLETE, the corresponding operation would not be executed, and the window would stay there waiting for the user’s following valid behaviors. (3) If when creating an onDrop script, the user selects the shape itself to be dropped on, e.g., shape1 on drop shape1 goto page2, there would be a toast displayed saying this is not reasonable. (4) If there are two consecutive “goto” actions, or two consecutive hide-hide/show-hide/hide-show/show-show actions on the same shape, there would be a toast displayed saying this is not reasonable. (5) The case that the user changes the name of or deletes a page or shape that is referred to in another script is handled when the user clicks the SAVE GAME button to save the current game. A toast would show to remind the user that [shapeName/pageName] does not exist, but is referred to in the [onClick/onEnter/onDrop] script of [shapeName], and prevents the user from saving this error-prone game.

## IV. DATABASE
#### Load a Game into JSON:
The goal of data storage is to save the parameters settings of each class object so that we can extract them to reconstruct the object back into the game. Therefore, we choose to use JSON files as our primary approach to store a game as text, combining JSONObject with JSONArray to store our game-page-shapes objects.

Since a game class object is created and edited, it possesses a list of page class objects, which includes a list of shape class objects. Each parameter for an object will be converted into a HashMap with keys to be variable names, and values to be the string values of the instance. Afterward, the game object will be converted into a JSONObject with JSONArray of pages, which includes JSONArray of shapes. Then, it will be packed as a single .json file under the data/cs108.stanford.edu.bunnyworld folder. Meanwhile, in order to determine the existence of games, a game\_name.txt file will be created to record all existing games’ names as a list of strings. If a game does not exist, a new file will be created; otherwise, the existing file will be rewritten.
#### Load a Game back from JSON:
While a game is loaded, the engine will load the game based on the selection of our main page spinner, which is restricted to the game listed in the game\_name.txt file. Then, the .json file will be converted back into a JSONObject to reconstruct a game class. All pages JSONArray and shapes JSONArray will also be initialized and reconstructed. As a result, a game object will be ready to run depending on the game context or editor context.
#### New game:
Users can create as many games he/she wants with nonrepetitive game names (not case sensitive). The game will be empty with only one default page called page1 without any shapes. NOTES: if a repetitive game name is entered, a warning toast will be printed and nothing changes in the databases.
#### Delete game / Reset Database
Games can also be deleted in the MainActivity. Selected gamename.json will be removed and its game will also be deleted from the game\_name.txt file. Deleting function works for all newly created games but is unpermitted from the default game bunnyworldg2023.json and bunnyworldg2023\_extension.json. Bunnyworldg2023.json stores the basic version of bunnyworld required by the assignment, and bunnyworldg2023\_extension.json saves an advanced version of the bunny world considering all our added extensions like custom text style, background package, and music. In the MainActivity, the games database will rewrite the game\_name.txt file and delete bunny world related JSON file from the virtual machine. Meanwhile, the default games stored in the res/raw folder will be imported to ensure that there will be at least 2 games in the engine.
## IV. EXTENSIONS
### A.Game Extension
1. Inventory automatically resize and line up
   1. Description: when a shape is moved from the current page to the inventory list, it will be resized and lined up from left to right simulating an item bag as the video bag. 
   1. A text shape will be abbreviated to “Text” and only show the full text when dragged back to the draw view.
   1. When a shape is in the inventory and hidden, it still takes a position and the following shapes cannot take that position. 
   1. Boundary check: when a large shape enters the inventory list from the draw view, it will be checked by the uniform inventory shape size instead of its own size. 

### B. Editor Extension
1. Copy and paste
1. Description: in the “Edit Current Shape” menu on the top right corner, there is the copy method that the user can copy the current selected shape. After copying the shape, the user can find the paste option in the “Create Shape” menu on the top right next to “Edit Current Shape”. The user can paste multiple copies and also paste copies across different pages and games.  Pasting a shape is the same as creating a new shape, which is first drawn on the top left corner.

1. Edit background image
   1. Description: Click the “Set Background” menu then select “Image” on the top bar in the edit mode and choose one image as the background image of the current page. The selected image will fill the entire area of the EditView in the edit mode and the game area of the GameView in the game mode. Users can choose different background images for different pages or clear the background by clicking on the “Clear background” option.
1. Edit background music
1. Description: Click the “Set Background” menu then select “Music” on the top bar in the edit mode and choose one song as the background music of the current page. The selected song would be played when the user enters this page in the edit mode or game mode, and stopped when the user leaves this page, or this mode/view, e.g., return to main activity or the home screen (by overriding the onWindowVisibilityChange() method in GameView and EditView). The user can choose different music for different pages or clear the current background music by clicking on the “No BGM” option.

`     `5.   Support for rich text: Users can set bold, italic text and add underline to text.

`     `6.   Proportional scaling: Supported to resize shape proportionally.

### C. Database Extension:
`    `7.   Database Export: 

Games will be stored in the local data/cs108.stanford.bunnyworld folder in each phone automatically. The game also supports users to view and copy SOURCE JSON data in the MainActivity by clipboard. The packed JSON file can be imported into any device and played with our game engine.




CS 108: Object-Oriented Programming, Winter 2021, Stanford, CA
