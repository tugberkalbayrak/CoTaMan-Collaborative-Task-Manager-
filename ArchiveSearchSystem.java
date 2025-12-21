package com.example.Services.ArchiveService;

import com.example.Entity.AcademicFile;
import com.example.Entity.Group;
import com.example.Entity.FileType;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ArchiveSearchSystem {

    public List<AcademicFile> searchFiles(Group group, String query, FileType typeFilter) {
        if (group.getGroupArchive() == null) return new ArrayList<>();

        return group.getGroupArchive().stream()
            .filter(f -> query == null || query.isEmpty() || f.getFileName().toLowerCase().contains(query.toLowerCase()))
            .filter(f -> typeFilter == null || f.getType() == typeFilter)
            .collect(Collectors.toList());
    }
}