package com.example.database;

import org.bson.types.ObjectId;

public class ArchiveFile {
    private ObjectId id;
    private String fileName;
    private String downloadLink; 
    private ObjectId uploaderId; 
    private FileType type;      
    private Visibility visibility; 

    public ArchiveFile() {}

    public ArchiveFile(String fileName, String downloadLink, ObjectId uploaderId, FileType type, Visibility visibility) {
        this.fileName = fileName;
        this.downloadLink = downloadLink;
        this.uploaderId = uploaderId;
        this.type = type;
        this.visibility = visibility;
    }

    // Getter ve Setter'lar
    public ObjectId getId() { return id; }
    public void setId(ObjectId id) { this.id = id; }
    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }
    public String getDownloadLink() { return downloadLink; }
    public void setDownloadLink(String downloadLink) { this.downloadLink = downloadLink; }
    public ObjectId getUploaderId() { return uploaderId; }
    public void setUploaderId(ObjectId uploaderId) { this.uploaderId = uploaderId; }
    public FileType getType() { return type; }
    public void setType(FileType type) { this.type = type; }
    public Visibility getVisibility() { return visibility; }
    public void setVisibility(Visibility visibility) { this.visibility = visibility; }
}