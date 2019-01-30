package org.redwid.tools;

import org.redwid.tools.db.LocalDbHelper;
import org.redwid.tools.db.LocalMediaItem;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Files2Db {

    public static void main(String[] args) {
        final Files2Db files2db = new Files2Db();
        files2db.list();
    }

    public Files2Db() {

    }

    private void list() {
        final LocalDbHelper localDbHelper = new LocalDbHelper();
        try {
            Files.find(Paths.get("."),
                    Integer.MAX_VALUE,
                    (filePath, fileAttr) -> fileAttr.isRegularFile())
                    .forEach((f)->addFileIfNeeded(f, localDbHelper));
        } catch (IOException e) {
            e.printStackTrace();
        }
        localDbHelper.close();
    }

    private void addFileIfNeeded(final Path path, final LocalDbHelper localDbHelper) {
        System.out.println(path);

        final LocalMediaItem localMediaItem = new LocalMediaItem(path);
        System.out.println("  localMediaItem: " + localMediaItem);
        localDbHelper.addMediaItem(localMediaItem);
    }
}
