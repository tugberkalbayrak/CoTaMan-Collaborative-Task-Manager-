package com.example.Handlers;

import com.example.Entity.AcademicFile;
import com.example.Entity.Group;
import com.example.Entity.User;
import com.example.Services.ArchiveService.ArchiveSearchSystem;
import com.example.database.CloudRepository;
import com.example.Entity.FileType;
import com.example.Entity.Visibility;
import com.example.Entity.AcademicFile;
import org.bson.types.ObjectId;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ArchiveInteractionHandler {

    private CloudRepository repository;
    private ArchiveSearchSystem searchSystem;

    public ArchiveInteractionHandler() {
        this.repository = new CloudRepository();
        this.searchSystem = new ArchiveSearchSystem();
    }

    public void onFileDrop(File rawFile, String fileName, String courseCode, String visibilityStr, User uploader) {
        if (rawFile != null && rawFile.exists()) {

            Visibility vis = Visibility.PUBLIC;
            if (visibilityStr != null) {
                if (visibilityStr.equalsIgnoreCase("Group Only")) {
                    vis = Visibility.GROUP;
                } else if (visibilityStr.equalsIgnoreCase("Private (Only Me)")) {
                    vis = Visibility.PRIVATE;
                }
            }

AcademicFile newFile = new AcademicFile(
                    fileName,
                    rawFile.getAbsolutePath(),  
                    uploader,
                    FileType.LECTURE_NOTE,  
                    vis,
                    courseCode);

repository.saveFileMetadata(newFile);
            System.out.println("File metadata saved to DB: " + fileName);
        }
    }

    public List<AcademicFile> searchFiles(Group group, String query, FileType typeFilter) {
        return searchSystem.searchFiles(group, query, typeFilter);
    }

    public List<AcademicFile> onSearchInput(Group group, String query, FileType type) {
        return searchFiles(group, query, type);
    }
}