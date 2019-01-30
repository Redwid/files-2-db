package org.redwid.tools.db;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.MetadataException;
import com.drew.metadata.avi.AviDirectory;
import com.drew.metadata.exif.ExifDirectoryBase;
import com.drew.metadata.file.FileSystemDirectory;
import com.drew.metadata.file.FileTypeDirectory;
import com.drew.metadata.gif.GifHeaderDirectory;
import com.drew.metadata.mov.media.QuickTimeMediaDirectory;
import com.drew.metadata.mov.media.QuickTimeVideoDirectory;
import com.drew.metadata.mp4.media.Mp4VideoDirectory;
import com.drew.metadata.png.PngDirectory;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import javax.activation.MimetypesFileTypeMap;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Date;

import static com.drew.metadata.exif.ExifDirectoryBase.*;
import static com.drew.metadata.file.FileSystemDirectory.TAG_FILE_MODIFIED_DATE;
import static com.drew.metadata.file.FileSystemDirectory.TAG_FILE_SIZE;
import static com.drew.metadata.file.FileTypeDirectory.TAG_DETECTED_FILE_MIME_TYPE;
import static com.drew.metadata.file.FileTypeDirectory.TAG_EXPECTED_FILE_NAME_EXTENSION;

@DatabaseTable(tableName = "local_media_items")
public class LocalMediaItem {

    @DatabaseField(columnName = "id", generatedId = true)
    public int id;

    @DatabaseField(columnName = "fileName")
    public String fileName;

    @DatabaseField(columnName = "parentPath")
    public String parentPath;

    @DatabaseField(columnName = "mimeType")
    public String mimeType;

    @DatabaseField(columnName = "extension")
    public String extension;

    @DatabaseField(columnName = "fileSize")
    public long fileSize;

    @DatabaseField(columnName = "width")
    public long width;

    @DatabaseField(columnName = "height")
    public long height;

    @DatabaseField(columnName = "lastModified")
    public long lastModified;

    @DatabaseField(columnName = "originalDate")
    public long originalDate;

    public Path path;

    public LocalMediaItem() {

    }

    public LocalMediaItem(final Path path) {
        this();
        this.path = path;
        this.fileName = path.getFileName().toString();
        this.parentPath = path.getParent().toString();
        if(parentPath.equals(".")) {
            parentPath = "/";
        }
        if(parentPath.startsWith(".")) {
            parentPath = parentPath.substring(1);
        }
        fillMetadata(path);
    }

    public void fillMetadata(final Path path) {
        try {
            final Metadata metadata = ImageMetadataReader.readMetadata(path.toFile());
            if(metadata.containsDirectoryOfType(FileTypeDirectory.class)) {
                final Collection<FileTypeDirectory> values = metadata.getDirectoriesOfType(FileTypeDirectory.class);
                for (FileTypeDirectory fileTypeDirectory : values) {
                    mimeType = getStringValue(fileTypeDirectory, TAG_DETECTED_FILE_MIME_TYPE, mimeType);
                    extension = getStringValue(fileTypeDirectory, TAG_EXPECTED_FILE_NAME_EXTENSION, extension);
                }
            }

            if(metadata.containsDirectoryOfType(FileTypeDirectory.class)) {
                final Collection<FileSystemDirectory> values = metadata.getDirectoriesOfType(FileSystemDirectory.class);
                for (FileSystemDirectory fileSystemDirectory : values) {
                    fileSize = getLongValue(fileSystemDirectory, TAG_FILE_SIZE, fileSize);
                    lastModified = getDateValue(fileSystemDirectory, TAG_FILE_MODIFIED_DATE, lastModified);
                }
            }

            if(metadata.containsDirectoryOfType(ExifDirectoryBase.class)) {
                final Collection<ExifDirectoryBase> values = metadata.getDirectoriesOfType(ExifDirectoryBase.class);
                for (ExifDirectoryBase exifDirectoryBase : values) {
                    originalDate = getDateValue(exifDirectoryBase, TAG_DATETIME_ORIGINAL, originalDate);
                    width = getLongValue(exifDirectoryBase, TAG_EXIF_IMAGE_WIDTH, width);
                    height = getLongValue(exifDirectoryBase, TAG_EXIF_IMAGE_HEIGHT, height);
                }
            }

            if(metadata.containsDirectoryOfType(PngDirectory.class)) {
                final Collection<PngDirectory> values = metadata.getDirectoriesOfType(PngDirectory.class);
                for (PngDirectory pngDirectory : values) {
                    width = getLongValue(pngDirectory, PngDirectory.TAG_IMAGE_WIDTH, width);
                    height = getLongValue(pngDirectory, PngDirectory.TAG_IMAGE_HEIGHT, height);
                }
            }

            if(metadata.containsDirectoryOfType(GifHeaderDirectory.class)) {
                final Collection<GifHeaderDirectory> values = metadata.getDirectoriesOfType(GifHeaderDirectory.class);
                for (GifHeaderDirectory gifHeaderDirectory : values) {
                    width = getLongValue(gifHeaderDirectory, GifHeaderDirectory.TAG_IMAGE_WIDTH, width);
                    height = getLongValue(gifHeaderDirectory, GifHeaderDirectory.TAG_IMAGE_HEIGHT, height);
                }
            }

            if(metadata.containsDirectoryOfType(QuickTimeVideoDirectory.class)) {
                final Collection<QuickTimeVideoDirectory> values = metadata.getDirectoriesOfType(QuickTimeVideoDirectory.class);
                for (QuickTimeVideoDirectory quickTimeVideoDirectory : values) {
                    originalDate = getDateValue(quickTimeVideoDirectory, QuickTimeMediaDirectory.TAG_CREATION_TIME, originalDate);
                    width = getLongValue(quickTimeVideoDirectory, QuickTimeVideoDirectory.TAG_WIDTH, width);
                    height = getLongValue(quickTimeVideoDirectory, QuickTimeVideoDirectory.TAG_WIDTH, height);
                }
            }

            if(metadata.containsDirectoryOfType(Mp4VideoDirectory.class)) {
                final Collection<Mp4VideoDirectory> values = metadata.getDirectoriesOfType(Mp4VideoDirectory.class);
                for (Mp4VideoDirectory mp4VideoDirectory : values) {
                    originalDate = getDateValue(mp4VideoDirectory, Mp4VideoDirectory.TAG_CREATION_TIME, originalDate);
                    width = getLongValue(mp4VideoDirectory, Mp4VideoDirectory.TAG_WIDTH, width);
                    height = getLongValue(mp4VideoDirectory, Mp4VideoDirectory.TAG_WIDTH, height);
                }
            }

            if(metadata.containsDirectoryOfType(AviDirectory.class)) {
                final Collection<AviDirectory> values = metadata.getDirectoriesOfType(AviDirectory.class);
                for (AviDirectory aviDirectory : values) {
                    width = getLongValue(aviDirectory, AviDirectory.TAG_WIDTH, width);
                    height = getLongValue(aviDirectory, AviDirectory.TAG_WIDTH, height);
                }
            }

        } catch(ImageProcessingException e) {
            //Ignore
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        if(fileSize == 0) {
            fileSize = path.toFile().length();
        }

        if(lastModified == 0) {
            lastModified = path.toFile().lastModified();
        }

        if(extension == null) {
            if(fileName != null) {
                int index = fileName.lastIndexOf(".");
                if(index > 0) {
                    extension = fileName.substring(index + 1);
                }
            }
        }
        if(mimeType == null) {
            mimeType = new MimetypesFileTypeMap().getContentType(path.toFile());
        }
    }

    private long getLongValue(Directory directory, int tag, long defaultValue) {
        if(directory.containsTag(tag)) {
            try {
                return directory.getLong(tag);
            } catch (MetadataException e) {
                e.printStackTrace();
            }
        }
        return defaultValue;
    }

    private String getStringValue(Directory directory, int tag, String defaultValue) {
        if(directory.containsTag(tag)) {
            return directory.getString(tag);
        }
        return defaultValue;
    }

    private long getDateValue(Directory directory, int tag, long defaultValue) {
        if(directory.containsTag(tag)) {
            final Date date = directory.getDate(tag);
            if(date != null) {
                return date.getTime();
            }
        }
        return defaultValue;
    }

    @Override
    public String toString() {
        return "LocalMediaItem{" +
                "id='" + id + '\'' +
                ", fileName='" + fileName + '\'' +
                ", parentPath='" + parentPath + '\'' +
                ", mimeType='" + mimeType + '\'' +
                ", extension='" + extension + '\'' +
                ", width=" + width +
                ", height=" + height +
                ", lastModified=" + lastModified +
                ", originalDate=" + originalDate +
                ", path=" + path +
                '}';
    }
}
