package com.example.Handlers;

import com.example.Entity.AcademicFile;
import com.example.Entity.Group;
import com.example.Entity.User;
import com.example.Services.ArchiveService.ArchiveSearchSystem;
import com.example.database.CloudRepository;
import com.example.Entity.FileType;
import com.example.Entity.Visibility;
import com.example.Entity.AcademicFile; // Explicitly using DB Entity
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
        this.searchSystem = new ArchiveSearchSystem(); // Assuming this service exists or logic is integrated
    }

    public void onFileDrop(File rawFile, String fileName, String courseCode, String visibilityStr, User uploader) {
        if (rawFile != null && rawFile.exists()) {

            // Map Visibility String from UI to Enum
            Visibility vis = Visibility.PUBLIC;
            if (visibilityStr != null) {
                if (visibilityStr.equalsIgnoreCase("Group Only")) {
                    vis = Visibility.GROUP;
                } else if (visibilityStr.equalsIgnoreCase("Private (Only Me)")) {
                    vis = Visibility.PRIVATE;
                }
            }

            // Create Database Entity
            AcademicFile newFile = new AcademicFile(
                    fileName,
                    rawFile.getAbsolutePath(), // Storing path as download link
                    uploader,
                    FileType.LECTURE_NOTE, // Defaulting to OTHER, ideally logic detects extension
                    vis,
                    courseCode);

            // Save to Cloud Repository
            repository.saveFileMetadata(newFile);
            System.out.println("File metadata saved to DB: " + fileName);
        }
    }

    /**
     * Internal logic to filter files based on query and type.
     * Operates on the Group's cached list of Core AcademicFile entities.
     */
    public List<AcademicFile> searchFiles(Group group, String query, FileType typeFilter) {
        // Delegate to the ArchiveSearchSystem service
        return searchSystem.searchFiles(group, query, typeFilter);
    }

    /**
     * Handler called by the UI search bar input listener.
     * Delegates to the searchFiles method.
     */
    public List<AcademicFile> onSearchInput(Group group, String query, FileType type) {
        return searchFiles(group, query, type);
    }
}