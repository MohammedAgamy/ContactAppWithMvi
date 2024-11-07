# ContactApp

A simple contact management application built with Jetpack Compose and Kotlin, utilizing the Model-View-Intent (MVI) architecture pattern.

## Features

- **Add Contacts**: Create and save new contacts.
- **View Contacts**: Display the list of all saved contacts.
- **Delete Contacts**: Remove selected contacts with an "Undo" option.
- **Search Contacts**: Filter contacts by name, email, or phone number.
- **Clear All Contacts**: Delete all saved contacts.

## Architecture

### Model-View-Intent (MVI)

The MVI architecture provides a unidirectional data flow to ensure consistency between the UI and the state. 

- **Model**: Holds the state of the app, data classes that represent the UI's view state.
- **View**: Displays the state provided by the ViewModel and sends user actions as intents.
- **Intent**: Represents actions triggered by the user or system. The `ContactViewModel` interprets these and updates the `ContactViewState` accordingly.

### Project Structure

- **`ContactViewModel`**: Manages the app's state and responds to intents from the UI.
- **`ContactViewIntent`**: A sealed class representing all possible user actions or intents.
- **`ContactViewState`**: A data class holding the UI's state, which is observed by the view.
- **`SnackbarViewEffect`**: For displaying feedback to the user using Snackbar messages.

### UI Components

- **`ConatctListScreen`**: Main screen displaying contacts with search, add, delete, and clear functionality.
- **`ContactItem`**: Reusable UI component to display individual contacts.
- **`SearchInput`**: Input field for searching contacts.
- **`UserInPut`**: Form to add or edit contact details.

### Dependency Injection

- **`ContactRepository`**: An interface defining data operations, implemented by `ContactRepositoryImp`.
- **`ContactRepositoryImp`**: Provides data handling for contacts, simulating data operations.

