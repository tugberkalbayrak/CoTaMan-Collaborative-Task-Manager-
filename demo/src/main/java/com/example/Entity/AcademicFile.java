package com.example.Entity;

import java.util.List;

import org.bson.types.ObjectId;

public class AcademicFile {
    private ObjectId fileId;
    private String fileName;
    private String diskPath;
    private User uploader;
    private FileType type;
    private Visibility visibility;
    private List<String> versionHistoryPaths;

    public AcademicFile() {}

    public AcademicFile(String fileName, String downloadLink, User uploader, FileType type, Visibility visibility) {
        this.fileName = fileName;
        this.diskPath = downloadLink;
        this.uploader = uploader;
        this.type = type;
        this.visibility = visibility;
    }

    public void createNewVersion(String newPath, User editor) {}

    public ObjectId getFileId() {
        return fileId;
    }

    public void setFileId(ObjectId fileId) {
        this.fileId = fileId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getDiskPath() {
        return diskPath;
    }

    public void setDiskPath(String diskPath) {
        this.diskPath = diskPath;
    }

    public User getUploader() {
        return uploader;
    }

    public void setUploader(User uploader) {
        this.uploader = uploader;
    }

    public FileType getType() {
        return type;
    }

    public void setType(FileType type) {
        this.type = type;
    }

    public Visibility getVisibility() {
        return visibility;
    }

    public void setVisibility(Visibility visibility) {
        this.visibility = visibility;
    }

    public List<String> getVersionHistoryPaths() {
        return versionHistoryPaths;
    }

    public void setVersionHistoryPaths(List<String> versionHistoryPaths) {
        this.versionHistoryPaths = versionHistoryPaths;
    }
}