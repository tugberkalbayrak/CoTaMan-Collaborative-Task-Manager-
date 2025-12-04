package com.example;

import java.util.List;

public class AcademicFile {
    private String fileId;
    private String fileName;
    private String diskPath;
    private User uploader;
    private FileType type;
    private Visibility visibility;
    private List<String> versionHistoryPaths;

    public void createNewVersion(String newPath, User editor) {}
}
