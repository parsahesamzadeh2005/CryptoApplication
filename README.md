# Crypto Application (Bit Max)

A comprehensive Android cryptocurrency tracking application built with Java that allows users to monitor crypto prices, manage portfolios, and track transactions.

## Project Overview

This is an Android application that provides cryptocurrency market data, portfolio management, and user authentication features. The app uses the CoinGecko API for real-time crypto data and implements a local SQLite database for user data persistence.

## Important Files and Their Functions

### Configuration Files

**`app/build.gradle`**
- Main build configuration for the Android app
- Defines dependencies including Retrofit for networking, Glide for image loading, and testing frameworks
- Contains API key configuration for CoinGecko API
- Sets minimum SDK version to 24 (Android 7.0) and target SDK to 33 (Android 13)

**`app/src/main/AndroidManifest.xml`**
- Defines app permissions (Internet access, media reading)
- Declares all activities and their launch configurations
- Sets SplashScreen as the main launcher activity
- Configures app metadata and theme

**`settings.gradle`**
- Project-level Gradle settings
- Defines repository sources and plugin management
- Sets root project name as "crypto"

**`gradle.properties`**
- Global Gradle configuration properties

**`gradlew` / `gradlew.bat`**
- Gradle wrapper scripts for Unix/Windows systems

### Java Source Files

#### Main Application Structure

**`app/src/main/java/com/example/cryptoapplication/`**

#### Database Layer

**`database/CryptoDatabaseContract.java`**
- Defines database schema constants and table structures
- Contains column names and SQL creation statements

**`database/CryptoDatabaseHelper.java`**
- SQLite database helper class
- Manages database creation and version upgrades

**`database/CryptoDatabaseManager.java`**
- High-level database management interface
- Provides centralized database operations

**`database/DatabaseMigration.java`**
- Handles database schema migrations between versions
- Ensures smooth app updates with database changes

**`database/SimpleDatabaseService.java`**
- Simplified database service layer
- Provides basic CRUD operations

#### Data Access Objects (DAO)

**`database/dao/BaseDao.java`**
- Base interface for all DAO classes
- Defines common database operations

**`database/dao/CoinDao.java` / `CoinDaoImpl.java`**
- Interface and implementation for coin data operations
- Handles cryptocurrency data persistence

**`database/dao/PortfolioDao.java` / `PortfolioDaoImpl.java`**
- Interface and implementation for portfolio management
- Manages user's cryptocurrency holdings

**`database/dao/UserDao.java` / `UserDaoImpl.java`**
- Interface and implementation for user data operations
- Handles user authentication and profile data

#### Data Models

**`models/CoinModel.java`**
- Data model representing cryptocurrency information
- Contains price, market cap, and other coin details

**`models/ConsolidatedAsset.java`**
- Model for aggregated asset information
- Combines multiple asset data points

**`models/PortfolioItem.java`**
- Represents individual items in user's portfolio
- Contains holding amounts and purchase information

**`models/User.java`**
- User data model for authentication and profile
- Stores user credentials and personal information

#### Network Layer

**`network/CoinGeckoApi.java`**
- Retrofit interface defining CoinGecko API endpoints
- Specifies HTTP methods and request parameters

**`network/RetrofitClient.java`**
- Singleton class for Retrofit HTTP client configuration
- Manages API base URL and network interceptors

#### Repository Layer

**`repository/CoinRepositoryRetrofit.java`**
- Repository pattern implementation for coin data
- Bridges network layer with UI layer
- Handles data caching and API calls

#### Service Layer

**`service/AuthResult.java`**
- Data class representing authentication operation results
- Contains success/failure status and user data

**`service/AuthService.java`**
- Handles user authentication logic
- Manages login, registration, and session management

#### User Interface (UI)

##### Authentication

**`ui/auth/LoginActivity.java`**
- User login screen implementation
- Handles user credential validation and authentication

**`ui/auth/RegisterActivity.java`**
- User registration screen
- Manages new user account creation

##### Loading

**`ui/loading/SplashScreen.java`**
- App startup screen with loading animation
- Entry point activity that redirects to appropriate screen

##### Home

**`ui/home/HomeActivity.java`**
- Main dashboard displaying cryptocurrency market data
- Shows trending coins and portfolio overview

**`ui/home/adapter/CoinAdapter.java`**
- RecyclerView adapter for displaying coin list
- Handles coin item layout and click events

**`ui/home/adapter/ConsolidatedAssetsAdapter.java`**
- Adapter for displaying consolidated asset information
- Shows aggregated portfolio data

**`ui/home/adapter/WalletAssetsAdapter.java`**
- Adapter for wallet asset display
- Manages individual wallet holdings presentation

##### Detail

**`ui/detail/CoinDetailActivity.java`**
- Detailed view for individual cryptocurrency
- Shows price charts, market data, and trading options

**`ui/detail/adapter/PriceHistoryAdapter.java`**
- Adapter for displaying price history data
- Handles historical price chart information

##### Profile

**`ui/profile/ProfileActivity.java`**
- User profile management screen
- Displays user information and account settings

**`ui/profile/AssetsActivity.java`**
- Detailed view of user's cryptocurrency assets
- Shows complete portfolio breakdown

**`ui/profile/TransactionHistoryActivity.java`**
- Displays user's transaction history
- Shows buy/sell operations and transfers

**`ui/profile/TransactionAdapter.java`**
- RecyclerView adapter for transaction list
- Handles transaction item display and formatting

#### Utilities

**`utils/PasswordUtils.java`**
- Utility class for password operations
- Handles password hashing, validation, and security

### Resource Files

**`app/src/main/res/`**
- Contains all Android resources (layouts, drawables, values, animations)
- Includes UI layouts, images, strings, colors, and styles

**Animation Files (`res/anim/`)**
- `loader.xml` - Loading animation configuration
- `slide_in_from_left.xml` - Left slide-in transition
- `slide_in_from_right.xml` - Right slide-in transition  
- `slide_out_to_left.xml` - Left slide-out transition
- `slide_out_to_right.xml` - Right slide-out transition

## Key Features

- Real-time cryptocurrency price tracking
- User authentication and registration
- Portfolio management and tracking
- Transaction history
- Detailed coin information and charts
- Secure local data storage
- Smooth animations and transitions

## Technology Stack

- **Language**: Java
- **Platform**: Android (API 24+ / Android 7.0+)
- **Networking**: Retrofit 2 + OkHttp
- **Image Loading**: Glide
- **Database**: SQLite
- **Architecture**: Repository Pattern with DAO
- **API**: CoinGecko API for cryptocurrency data

## Getting Started

1. Clone the repository
2. Open in Android Studio
3. Replace the CoinGecko API key in `app/build.gradle`
4. Build and run the application

## Build Requirements

- Android Studio
- Java 11+
- Android SDK API 24+ (supports Android 7.0 through Android 13+)
- Internet connection for API data