# Crypto Application - Project Structure

## Important Files Overview

### 📱 **Main Activities (UI Screens)**

**HomeActivity.java** - Main screen showing cryptocurrency list
- Shows all coins, winners, losers
- Search functionality
- Navigation to coin details

**ProfileActivity.java** - User profile and wallet management
- User balance display
- Add/withdraw money
- Portfolio overview
- Transaction history

**LoginActivity.java** - User authentication
- Login form
- Navigation to registration

**RegisterActivity.java** - New user registration
- Registration form
- Account creation

**CoinDetailActivity.java** - Individual coin information
- Coin price details
- Buy/sell functionality
- Price charts

### 🗄️ **Database Layer**

**SimpleDatabaseService.java** - Main database operations
- User login/registration
- Balance management using SharedPreferences for trading data
- Coin data caching
- Portfolio operations
- Optimized to avoid database lock issues

**CryptoDatabaseHelper.java** - Database setup and migrations
- Creates database tables (users, favorite_coins, coin_cache, portfolio, transactions)
- Handles version upgrades
- Database schema management
- **CLEANED**: Removed unused table creation for alerts, search_history, coin_price_history

**CryptoDatabaseManager.java** - Database access coordinator
- Manages all DAO instances
- Transaction handling
- Database statistics

**DatabaseMigration.java** - Database version migration logic
- **CLEANED**: Removed unused Migration2To3 and Migration3To4 classes
- **CLEANED**: Consolidated migration numbering (removed gaps)
- Only contains migrations for actually used tables

**CryptoDatabaseContract.java** - Database schema definitions
- **CLEANED**: Removed unused table definitions (SearchHistoryEntry, CoinPriceHistoryEntry, AlertEntry)
- Contains only tables with corresponding DAO implementations

### 📊 **Data Models**

**CoinModel.java** - Consolidated cryptocurrency data structure
- Coin ID, name, symbol
- Price and market data with Gson annotations
- Supports both API deserialization and database operations
- Used in: HomeActivity, CoinDetailActivity, all network operations

**User.java** - User account information
- Username, email, password
- Balance and profile data
- Used in: ProfileActivity, LoginActivity

**PortfolioItem.java** - User's crypto holdings
- Coin quantity owned
- Purchase price and date
- Used in: ProfileActivity, portfolio display

### 🌐 **Network Layer**

**CoinGeckoApi.java** - API interface definitions
- Defines API endpoints
- Request/response structure

**RetrofitClient.java** - HTTP client setup
- Configures network requests
- Base URL and timeouts

**CoinRepositoryRetrofit.java** - Repository pattern implementation
- Handles data fetching and caching
- Coordinates between network and local storage

### 🎨 **UI Components**

**CoinAdapter.java** - Displays coin list in HomeActivity
- RecyclerView adapter
- Coin item layout binding

**TransactionAdapter.java** - Shows transaction history
- Used in ProfileActivity
- Transaction item display

### 🔧 **Utilities**

**PasswordUtils.java** - Password security
- Hash passwords using SHA-256
- Verify login credentials
- Used by: SimpleDatabaseService for authentication

**AuthService.java** - Authentication management
- Login/logout logic
- Session management
- **CLEANED**: Removed unused methods (getAuthToken, getUserToken, getUserEmail)

### 📁 **Database Access Objects (DAOs)**

**UserDao.java & UserDaoImpl.java** - User data operations
- CRUD operations for users table
- Authentication queries

**CoinDao.java & CoinDaoImpl.java** - Coin cache operations
- Coin data caching and retrieval
- Market data storage

**PortfolioDao.java & PortfolioDaoImpl.java** - Portfolio management
- User holdings tracking
- Transaction recording

## File Usage Map

### Where Each File is Used:

**HomeActivity** uses:
- CoinModel (consolidated model with API support)
- CoinAdapter (list display)
- CoinRepositoryRetrofit (fetch data)
- SimpleDatabaseService (cache data)

**ProfileActivity** uses:
- User (profile data)
- PortfolioItem (holdings)
- TransactionAdapter (history)
- SimpleDatabaseService (balance operations)

**LoginActivity** uses:
- User (login data)
- AuthService (authentication)
- SimpleDatabaseService (user verification)

**SimpleDatabaseService** uses:
- CryptoDatabaseManager (database access)
- All model classes (data operations)
- PasswordUtils (security)
- SharedPreferences (trading data)

**CoinRepositoryRetrofit** uses:
- CoinGeckoApi (API interface)
- RetrofitClient (HTTP client)
- CoinModel (data structure)

## Key Relationships

1. **Activities** → **SimpleDatabaseService** → **Database**
2. **Activities** → **Models** (consolidated data display)
3. **HomeActivity** → **CoinRepositoryRetrofit** → **API**
4. **ProfileActivity** → **AuthService** → **User management**
5. **All Activities** → **Adapters** → **UI display**

## Data Flow

1. **User opens app** → HomeActivity loads
2. **HomeActivity** → CoinRepositoryRetrofit → Fetches coin data
3. **Coin data** → SimpleDatabaseService → Cached locally
4. **User clicks coin** → CoinDetailActivity → Shows details
5. **User buys coin** → SimpleDatabaseService → Updates portfolio
6. **Portfolio changes** → ProfileActivity → Shows updated balance

## Recent Cleanup (Phase 2)

### Removed Unused Code:
- **Database migrations**: Migration2To3, Migration3To4 (created unused tables)
- **Database contracts**: SearchHistoryEntry, CoinPriceHistoryEntry, AlertEntry
- **String resources**: logoLoading, home, profile, coins_list, top_gainers, top_losers (all hardcoded in layouts)
- **Database helper**: Removed creation/deletion of unused tables
- **AuthService methods**: getAuthToken, getUserToken, getUserEmail (never called)

### Benefits:
- Reduced database schema complexity
- Eliminated dead migration code
- Cleaner codebase with only actively used components
- Improved maintainability

This structure separates concerns clearly: UI (Activities), Data (Models), Network (Services), and Storage (Database). The SimpleDatabaseService uses a hybrid approach with SharedPreferences for trading data to avoid database lock issues while maintaining data integrity.