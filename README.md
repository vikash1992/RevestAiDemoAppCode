# Revest E-commerce App

A modern Android e-commerce application built with Jetpack Compose and Clean Architecture principles. The app demonstrates best practices in Android development, including MVVM architecture, dependency injection, offline support, and comprehensive testing.

## Architecture Overview

The application follows Clean Architecture principles with three main layers:

### 1. Presentation Layer (UI)
- Uses MVVM pattern with Jetpack Compose
- ViewModels manage UI state and business logic
- Unidirectional data flow with sealed classes for state management
- Material 3 theming with dark mode support

### 2. Domain Layer (Business Logic)
- Contains use cases for business operations
- Repository interfaces
- Domain models
- Pure Kotlin with no Android dependencies

### 3. Data Layer (Data Access)
- Repository implementations
- Remote data source (Retrofit)
- Local data source (Room)
- Data models and mappers

## Key Features

- **Product Listing**: Display products in a scrollable grid with search and category filtering
- **Product Details**: Detailed product view with image gallery and specifications
- **Search**: Real-time product search with debouncing
- **Category Filtering**: Filter products by categories
- **Offline Support**: Cache products locally using Room database
- **Error Handling**: Proper error states and retry mechanisms
- **Pull-to-Refresh**: Update product listings
- **Deep Linking**: Support for deep linking to products
- **Dark Mode**: Material 3 dynamic theming

## Tech Stack

- **Kotlin**: Primary programming language
- **Jetpack Compose**: Modern UI toolkit
- **Coroutines & Flow**: Asynchronous programming
- **Hilt**: Dependency injection
- **Room**: Local database
- **Retrofit**: Network calls
- **Coil**: Image loading
- **Material 3**: UI components and theming
- **Navigation Component**: In-app navigation
- **MockK**: Mocking for tests
- **JUnit**: Unit testing
- **Compose Testing**: UI testing

## Project Structure

```
app/src/
├── main/
│   ├── java/com/revest/ecommerce/
│   │   ├── data/
│   │   │   ├── local/          # Room database, entities, DAOs
│   │   │   ├── remote/         # Retrofit API interfaces and DTOs
│   │   │   └── repository/     # Repository implementations
│   │   ├── di/                 # Dependency injection modules
│   │   ├── domain/
│   │   │   ├── model/          # Domain models
│   │   │   ├── repository/     # Repository interfaces
│   │   │   └── usecase/        # Business logic use cases
│   │   └── presentation/
│   │       ├── components/     # Reusable Compose components
│   │       ├── products/       # Product list screen
│   │       ├── productdetail/  # Product detail screen
│   │       ├── navigation/     # Navigation setup
│   │       └── theme/          # Material 3 theming
│   └── res/                    # Resources
├── test/                       # Unit tests
└── androidTest/                # UI tests
```

## Testing Strategy

### Unit Tests
- Repository tests with mock API and database
- ViewModel tests with mock use cases
- Use case tests with mock repositories
- Test doubles using MockK

### UI Tests
- Compose UI testing for screens
- Navigation testing
- Integration tests for main user flows

## Getting Started

1. Clone the repository
2. Open in Android Studio
3. Sync project with Gradle files
4. Run the app

## API Integration

The app integrates with the DummyJSON API (https://dummyjson.com/products) for product data. Features include:
- Product listing
- Product search
- Category filtering
- Product details

## Performance Considerations

- Efficient image loading with Coil
- Pagination for product lists
- Database caching for offline support
- Debounced search
- Proper state management to prevent unnecessary recompositions

## Future Improvements

1. Implement product cart functionality
2. Add user authentication
3. Implement checkout process
4. Add product reviews and ratings
   

## Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## License

This project is licensed under the MIT License - see the LICENSE file for details.
