
# School Scheduler

## How to Use
- Add data by using the following sub-menus under the "Data" tab
![alt text](https://imgur.com/fA6V9O4)

-  If you already have data you can use the import button located on the "Data" tab. You can also export or clear data from here if you've already added some data.
![alt text](https://imgur.com/wManpXK)

- Items in the sub-menus can also be deleted by right clicking on the row and clicking the delete button. Additionally, items can be sorted by clicking on the column name that you would like to sort by. Clicking multiple times cycles through modes (ascending, descending, no sort).
![alt text](https://imgur.com/o5jiI2p)

- Once you have loaded your data, you can click on the "View" tab to see valid schedules. This is also where you can cycle through schedules using the arrows located at the bottom left. The "Save" button located at the bottom right will save the current schedule to a ".png" file for later use.
![alt text](https://imgur.com/q0CmU8Q)

- If you're looking for something more specific, you can use the "Constraints" tab. Upon clicking on the tab, there will be a grid of empty slots. Click on the empty slot that you want to occupy with a subject. Please note: if an empty cell is clicked that is under the inappropriate grade for the subject, it will automatically populate in the correct cell.
![alt text](https://imgur.com/Zi24iks)

- After you have selected your constraints, you can go back to the "View" tab and it should show a valid schedule with the classes in their correct positions according to what you specific on the "Constraints" tab. Please note: there is a quirk in the program that sometimes requires the user to cycle to the next schedule using the right arrow button at the bottom left for their consraints to be applied.
![alt text](https://imgur.com/RWVd6DS)

- Simply select the schedule you like and save it using the "Save" button so you don't have to reopen the application later.

## Caveats & Known Issues
- It turns out that finding valid schedules given a significant amount of subjects can be a resource consuming task. There is an algorithm that is implemented into the program that uses backtracking, but sometimes this can still take some time. Please be pateient as the program attempts to find a valid schedule. 
- In extreme circumstances, the program can freeze entirely and will not recover. The user will be forced to close the program and restart. The reason for this is unknown and attempts to fix the problem simply created more problems. Please export your data as you input it to prevent this.

## Known Bugs
- The program sometimes freezes and does not recover as mentioned previously. Reasons for this are unknown.
- Because of JavaFX's spotty support for MacOS, the text on the Mac specific application can sometimes be fuzzy. This is related to a bug with JavaFX and there is nothing that can be done. In an attempt to remedy this, anti-aliasing has been set to grey so that black text does not turn into a rainbow. The text will still look a bit fuzzy, though. 
- Items that are listed in the data entry sub-sections can be sorted by clicking on the column name. This works most of the time, but it is unable to account for multiple characters during type changes. For example, if you have grade 20 and grade 1 and attempt to sort the entries in descending order, the program will put 1 above 20, which is backwards. This is because of type conversion and JavaFX not recognizing these as integers, rather treating them as strings.
- Spam clicking the next button in the "View" tab will crash the program. Again, this is something where there is not much to be done. Spam clicking a button that executes an intensive task will always result in some sort of hiccup. 

## Prebuilt Applications
[Mac (Apple Silicon)](https://www.dropbox.com/scl/fi/q0uaq92am7ywwf4j5lv5o/SchoolScheduler_AppleSilicon.dmg?rlkey=gfy9erjhcz1gp8h26hbk1guim&st=dkvvfogj&dl=0)


