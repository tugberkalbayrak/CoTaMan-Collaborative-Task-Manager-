package com.example.Services.ArchiveService;

import com.example.Entity.AcademicFile;
import com.example.Entity.Group;
import com.example.Entity.FileType;

import java.util.ArrayList;
import java.util.List;

public class ArchiveSearchSystem {

    public List<AcademicFile> searchFiles(Group group, String query, FileType typeFilter) {

        List<AcademicFile> result = new ArrayList<>();
        if (group.getGroupArchive() == null) {
            return result;
        }
        for (AcademicFile file : group.getGroupArchive()) {
            boolean matchesQuery = false;
            boolean matchesType = false;
            if (query == null || query.isEmpty()) {
                matchesQuery = true;
            } else {
                if (file.getFileName() != null &&
                        file.getFileName().toLowerCase().contains(query.toLowerCase())) {
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
                result.add(file);
            }
        }

        return result;
    }
}