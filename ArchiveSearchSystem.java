package com.example.Services.ArchiveService;

import com.example.Entity.AcademicFile;
import com.example.Entity.FileType;
import com.example.Entity.Group;
import java.util.ArrayList;
import java.util.List;

public class ArchiveSearchSystem {

    public List<AcademicFile> searchFiles(Group group, String query, FileType typeFilter) {
        
        if (group == null || group.getGroupArchive() == null) {
            return new ArrayList<>();
        }

        List<AcademicFile> allFiles = group.getGroupArchive();
        List<AcademicFile> matchingFiles = new ArrayList<>();
        for (AcademicFile file : allFiles) {
            boolean matchesQuery = false;
            boolean matchesType = false;

            boolean isQueryEmpty = (query == null || query.isEmpty());
            
            if (isQueryEmpty) {
                matchesQuery = true;
            } else {
                String fileName = file.getFileName().toLowerCase();
                String searchText = query.toLowerCase();
                if (fileName.contains(searchText)) {
                    matchesQuery = true;
                }
            }

            if (typeFilter == null) {
                matchesType = true;
            } else {
                if (file.getType() == typeFilter) {
                    matchesType = true;
                }
            }
            if (matchesQuery && matchesType) {
                matchingFiles.add(file);
            }
        }

        return matchingFiles;
    }
}