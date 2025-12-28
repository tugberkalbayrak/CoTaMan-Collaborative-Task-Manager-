# CoTaMan - Collaborative Task Manager

CoTaMan is a collaborative task management application built with Java based on the MVC (Model-View-Controller) architecture. It helps students and groups manage their tasks, files, and calendar events efficiently.

## Features
- **Task Management**: Create, update, and delete tasks.
- **Collaborative Groups**: Join groups to share resources.
- **Archive System**: Upload and manage academic files (Notes, Exams, Syllabuses).
- **Calendar Integration**: Sync events from Moodle (via web scraping) and manage personal schedules.
- **User Authentication**: Login and registration system.

## Prerequisites
- **Maven** (for dependency management).


## Dependencies
The project uses the following key dependencies (managed via `pom.xml`):
- **JavaFX 13**: For the Graphical User Interface (GUI).
- **MongoDB Sync Driver (4.11.1)**: For database connectivity.
- **Jsoup (1.17.2)**: For web scraping (Moodle integration).
- **SLF4J**: For logging.

## How to Run
1.  Navigate to `src/main/java/com/example/MainApp.java`.
2.  Right-click the file and select **Run 'MainApp.main()'**.

## Project Structure
- `com.example.Entity`: Data models (User, Group, AcademicFile, CalendarEvent).
- `com.example.Manager`: Business logic and state management (SessionManager).
- `com.example.database`: Database connection and repository pattern (CloudRepository).
- `com.example.ui`: JavaFX UI views and components.
- `com.example.WebScraping`: Tools for fetching data from external sources (Moodle).

## Web Scraping Note
The application uses **Jsoup** and **Selenium** concepts to scrape Moodle data. Ensure you have a stable internet connection for this feature to work during login.
