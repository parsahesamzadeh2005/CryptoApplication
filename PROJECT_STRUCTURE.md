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

**DatabaseService.java** - Main database operations
- User login/registration
- Balance management
- Coin data caching
- Portfolio operations

**CryptoDatabaseHelper.java** - Database setup and migrations
- Creates database tables
- Handles version upgrades
- Database schema management

**CryptoDatabaseManager.java** - Database access coordinator
- Manages all DAO instances
- Transaction handling
- Database statistics

### 📊 **Data Models**

**CoinModel.java** - Cryptocurrency data structure
- Coin ID, name, symbol
- Price and market data
- Used in: HomeActivity, CoinDetailActivity

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

**CoinGeckoService.java** - Network service implementation
- Makes HTTP requests
- Handles API responses

**RetrofitClient.java** - HTTP client setup
- Configures network requests
- Base URL and timeouts

### 🎨 **UI Components**

**CoinAdapter.java** - Displays coin list in HomeActivity
- RecyclerView adapter
- Coin item layout binding

**TransactionAdapter.java** - Shows transaction history
- Used in ProfileActivity
- Transaction item display

### 🔧 **Utilities**

**PasswordUtils.java** - Password security
- Hash passwords
- Verify login credentials

**AuthService.java** - Authentication management
- Login/logout logic
- Session management

## File Usage Map

### Where Each File is Used:

**HomeActivity** uses:
- CoinModel (display data)
- CoinAdapter (list display)
- CoinGeckoService (fetch data)
- DatabaseService (cache data)

**ProfileActivity** uses:
- User (profile data)
- PortfolioItem (holdings)
- TransactionAdapter (history)
- DatabaseService (balance operations)

**LoginActivity** uses:
- User (login data)
- AuthService (authentication)
- DatabaseService (user verification)

**DatabaseService** uses:
- CryptoDatabaseManager (database access)
- All model classes (data operations)
- PasswordUtils (security)

**CoinGeckoService** uses:
- CoinGeckoApi (API interface)
- RetrofitClient (HTTP client)
- CoinModel (data structure)

## Key Relationships

1. **Activities** → **DatabaseService** → **Database**
2. **Activities** → **Models** (data display)
3. **HomeActivity** → **CoinGeckoService** → **API**
4. **ProfileActivity** → **AuthService** → **User management**
5. **All Activities** → **Adapters** → **UI display**

## Data Flow

1. **User opens app** → HomeActivity loads
2. **HomeActivity** → CoinGeckoService → Fetches coin data
3. **Coin data** → DatabaseService → Cached locally
4. **User clicks coin** → CoinDetailActivity → Shows details
5. **User buys coin** → DatabaseService → Updates portfolio
6. **Portfolio changes** → ProfileActivity → Shows updated balance

This structure separates concerns clearly: UI (Activities), Data (Models), Network (Services), and Storage (Database).