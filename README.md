# SutoriApp - The Dicoding's Android Intermediate Course Project

## Overview

This application allows users to log in, register, share stories, and view a list of stories through an API. It also includes features such as adding new stories with photos, viewing detailed story information, displaying a map of story locations, and applying animations.

## Features

### Authentication Pages
- **Login**: Users can log in by entering their email and password.
- **Register**: Users can register by providing their name, email, and password (password is hidden).
- **Session Management**: Store session data and token in preferences to maintain the login state. After logging in, the user is directed to the main page. If not logged in, the user is redirected to the login page.
- **Logout**: The app has a logout button that clears session data and the token.

### Story List
- **View Stories**: The app displays a list of stories fetched from an API. Each story includes:
  - User name
  - User photo
- **Story Details**: Clicking a story item shows detailed information, including:
  - User name
  - User photo
  - Story description

### Add New Story
- Users can add a new story by uploading a photo from the gallery and entering a description.
- The "Add" button uploads the data to the server, and the story appears at the top of the list after a successful upload.

### Animation
- Animations are added using either Property Animation, Motion Animation, or Shared Element Animation. The location and type of animation are specified in the student note.

### Map
- The app displays a map showing stories with location data (latitude and longitude).
- Stories are marked on the map as either markers or image icons.

### Paging
- Story list is displayed using Paging 3 for efficient data handling.

### Testing
- Unit tests are implemented for functions in the ViewModel that manage the story list and Paging data.
  - Tests include:
    - Successful data loading
    - Handling empty data
    - Ensuring data consistency (e.g., correct number of stories)

## Requirements

### Application Features
1. **Login and Register**:
   - Email and password validation.
   - Show error messages for invalid email or password formats.
2. **Story List**:
   - Display stories with basic information: name, photo.
   - View detailed information when selecting a story.
3. **Add Story**:
   - Upload a photo (from gallery or camera) and provide a description.
   - Stories appear at the top of the list after upload.
4. **Animations**:
   - Implement animations (e.g., property, motion, shared element).
5. **Map Integration**:
   - Display stories on a map based on location data.
6. **Paging**:
   - Use Paging 3 for displaying large sets of story data.
7. **Testing**:
   - Implement unit tests to verify data loading, correctness, and empty states.

### Resources
- **Story API**: [API Documentation](https://story-api.dicoding.dev/v1/)
  - The API provides endpoints to fetch, add, and manage stories.
  - The maximum file size for uploads is 1 MB.
  - Save and use the token received upon login for subsequent API requests.

### Discussions and Help
If you encounter issues, feel free to ask questions on the [Discussion Forum](https://www.dicoding.com/academies/352/discussions).
