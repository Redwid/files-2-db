package org.redwid.tools.db;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LocalDbHelper {
    private final static String databaseUrl = "jdbc:sqlite:local-items.db";

    private ConnectionSource connectionSource;
    private Dao<LocalMediaItem, String> mediaItemStringDao;

    public LocalDbHelper() {
        try {
            // create a connection source to our database
            connectionSource = new JdbcConnectionSource(databaseUrl);

            // instantiate the dao
            mediaItemStringDao = DaoManager.createDao(connectionSource, LocalMediaItem.class);

            // if you need to create the 'MediaItemPersisted' table make this call
            TableUtils.createTableIfNotExists(connectionSource, LocalMediaItem.class);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void close() {
        if(mediaItemStringDao != null) {
            mediaItemStringDao = null;
        }

        if(connectionSource != null) {
            connectionSource.closeQuietly();
            connectionSource = null;
        }
    }

    public void addMediaItem(final LocalMediaItem localMediaItem) {
        System.out.println("addMediaItem()");
        try {
            System.out.println("addMediaItems(), item to add: " + localMediaItem.path);

            final Map<String, Object> queryMap = new HashMap();
            queryMap.put("fileName", localMediaItem.fileName);
            queryMap.put("parentPath", localMediaItem.parentPath);
            if(localMediaItem.extension != null) {
                queryMap.put("extension", localMediaItem.extension);
            }
            queryMap.put("fileSize", localMediaItem.fileSize);
            queryMap.put("width", localMediaItem.width);
            queryMap.put("height", localMediaItem.height);
            if(mediaItemStringDao.queryForFieldValues(queryMap).isEmpty()) {
                mediaItemStringDao.createOrUpdate(localMediaItem);
            }
            else {
                System.out.println("addMediaItems(), ignored");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addMediaItems(List<LocalMediaItem> mediaItemList) {
        System.out.println("addMediaItems()");
        int count = 1;
        for(LocalMediaItem localMediaItem: mediaItemList) {
            try {
                System.out.println("addMediaItems(), item to add: " + localMediaItem.path);
                mediaItemStringDao.createOrUpdate(localMediaItem);
                System.out.println("addMediaItems(), added: " + (count++));
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
