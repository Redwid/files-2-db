# files-2-db
Tool for storing files tree into local db

Tool will go through current directory files and folders recursively and store all files into db.

For stored properties have a look at [LocalMediaItem.java](https://github.com/Redwid/files-2-db/blob/master/src/main/java/org/redwid/tools/db/LocalMediaItem.java) model file.

The [ormlite](http://ormlite.com/) is used for simplifying db access.

The result sqlite db: local-items.db will be created in the current folder.

