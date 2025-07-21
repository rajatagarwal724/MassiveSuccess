                     DIGITAL WALLET SYSTEM ARCHITECTURE
┌────────────────────────────────────────────────────────────────────────────────┐
│                                                                                │
│  ┌─────────────┐   ┌─────────────┐   ┌─────────────┐   ┌─────────────┐        │
│  │ Mobile App  │   │  Web App    │   │ Partner API │   │  POS        │        │
│  │ Clients     │   │  Clients    │   │  Clients    │   │  Terminals  │        │
│  └──────┬──────┘   └──────┬──────┘   └──────┬──────┘   └──────┬──────┘        │
│         │                 │                 │                 │               │
│         └─────────────────┼─────────────────┼─────────────────┘               │
│                           │                 │                                 │
│                           ▼                 ▼                                 │
│  ┌─────────────────────────────────────────────────────────────────────┐     │
│  │                       API Gateway Layer                              │     │
│  │  ┌─────────────┐   ┌─────────────┐   ┌─────────────┐   ┌──────────┐ │     │
│  │  │ Rate        │   │ Auth        │   │ Request     │   │ API      │ │     │
│  │  │ Limiting    │   │ Service     │   │ Validation  │   │ Versioning│ │     │
│  │  └─────────────┘   └─────────────┘   └─────────────┘   └──────────┘ │     │
│  └───────────────────────────────┬─────────────────────────────────────┘     │
│                                  │                                            │
│                                  ▼                                            │
│  ┌─────────────────────────────────────────────────────────────────────┐     │
│  │                     Service Layer                                    │     │
│  │  ┌─────────────┐   ┌─────────────┐   ┌─────────────┐   ┌──────────┐ │     │
│  │  │ User        │   │ Wallet      │   │ Payment     │   │ Notifi-  │ │     │
│  │  │ Service     │   │ Service     │   │ Service     │   │ cation   │ │     │
│  │  └─────────────┘   └─────────────┘   └─────────────┘   └──────────┘ │     │
│  │                                                                     │     │
│  │  ┌─────────────┐   ┌─────────────┐   ┌─────────────┐   ┌──────────┐ │     │
│  │  │ KYC         │   │ Transaction │   │ Fraud       │   │ Reporting│ │     │
│  │  │ Service     │   │ Service     │   │ Detection   │   │ Service  │ │     │
│  │  └─────────────┘   └─────────────┘   └─────────────┘   └──────────┘ │     │
│  └───────────────────────────────┬─────────────────────────────────────┘     │
│                                  │                                            │
│                                  ▼                                            │
│  ┌─────────────────────────────────────────────────────────────────────┐     │
│  │                     Data Layer                                       │     │
│  │  ┌─────────────┐   ┌─────────────┐   ┌─────────────┐   ┌──────────┐ │     │
│  │  │ User        │   │ Wallet      │   │ Transaction │   │ Ledger   │ │     │
│  │  │ Database    │   │ Database    │   │ Database    │   │ Database │ │     │
│  │  └─────────────┘   └─────────────┘   └─────────────┘   └──────────┘ │     │
│  │                                                                     │     │
│  │  ┌─────────────┐   ┌─────────────┐   ┌─────────────┐   ┌──────────┐ │     │
│  │  │ Redis Cache │   │ Time-series │   │ Fraud ML    │   │ Document │ │     │
│  │  │             │   │ Database    │   │ Database    │   │ Store    │ │     │
│  │  └─────────────┘   └─────────────┘   └─────────────┘   └──────────┘ │     │
│  └─────────────────────────────────────────────────────────────────────┘     │
│                                                                               │
│  ┌─────────────────────────────────────────────────────────────────────┐     │
│  │                     External Integration Layer                       │     │
│  │  ┌─────────────┐   ┌─────────────┐   ┌─────────────┐   ┌──────────┐ │     │
│  │  │ Payment     │   │ Banking     │   │ Regulatory  │   │ Partner  │ │     │
│  │  │ Gateways    │   │ Systems     │   │ Reporting   │   │ APIs     │ │     │
│  │  └─────────────┘   └─────────────┘   └─────────────┘   └──────────┘ │     │
│  └─────────────────────────────────────────────────────────────────────┘     │
│                                                                               │
└────────────────────────────────────────────────────────────────────────────────┘
CREATE TABLE users (
    user_id UUID PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    phone_number VARCHAR(20) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    salt VARCHAR(64) NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    date_of_birth DATE NOT NULL,
    status VARCHAR(20) NOT NULL, -- ACTIVE, SUSPENDED, LOCKED, PENDING_VERIFICATION
    kyc_status VARCHAR(20) NOT NULL, -- PENDING, VERIFIED, REJECTED
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    last_login_at TIMESTAMP WITH TIME ZONE,
    failed_login_attempts INT DEFAULT 0,
    version INT NOT NULL DEFAULT 1
);

CREATE TABLE user_addresses (
    address_id UUID PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES users(user_id),
    address_type VARCHAR(20) NOT NULL, -- RESIDENTIAL, BUSINESS, MAILING
    address_line1 VARCHAR(255) NOT NULL,
    address_line2 VARCHAR(255),
    city VARCHAR(100) NOT NULL,
    state VARCHAR(100) NOT NULL,
    postal_code VARCHAR(20) NOT NULL,
    country VARCHAR(2) NOT NULL,
    is_verified BOOLEAN NOT NULL DEFAULT FALSE,
    is_primary BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    version INT NOT NULL DEFAULT 1
);

CREATE TABLE user_devices (
    device_id UUID PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES users(user_id),
    device_type VARCHAR(50) NOT NULL, -- ANDROID, IOS, WEB
    device_token VARCHAR(255),
    device_name VARCHAR(100),
    is_trusted BOOLEAN NOT NULL DEFAULT FALSE,
    last_used_at TIMESTAMP WITH TIME ZONE NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE TABLE kyc_documents (
    document_id UUID PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES users(user_id),
    document_type VARCHAR(50) NOT NULL, -- PASSPORT, DRIVERS_LICENSE, ID_CARD
    document_number VARCHAR(100) NOT NULL,
    issuing_country VARCHAR(2) NOT NULL,
    issue_date DATE NOT NULL,
    expiry_date DATE NOT NULL,
    verification_status VARCHAR(20) NOT NULL, -- PENDING, VERIFIED, REJECTED
    verification_notes TEXT,
    document_file_reference VARCHAR(255), -- Reference to document store
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL
);

-- Indexes
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_phone ON users(phone_number);
CREATE INDEX idx_user_addresses_user_id ON user_addresses(user_id);
CREATE INDEX idx_user_devices_user_id ON user_devices(user_id);
CREATE INDEX idx_kyc_documents_user_id ON kyc_documents(user_id);


CREATE TABLE wallets (
    wallet_id UUID PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES users(user_id),
    wallet_type VARCHAR(20) NOT NULL, -- PRIMARY, SAVINGS, BUSINESS
    currency_code VARCHAR(3) NOT NULL,
    status VARCHAR(20) NOT NULL, -- ACTIVE, SUSPENDED, CLOSED
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    version INT NOT NULL DEFAULT 1
);

CREATE TABLE wallet_balances (
    balance_id UUID PRIMARY KEY,
    wallet_id UUID NOT NULL REFERENCES wallets(wallet_id),
    available_balance DECIMAL(19, 4) NOT NULL DEFAULT 0,
    pending_balance DECIMAL(19, 4) NOT NULL DEFAULT 0,
    reserved_balance DECIMAL(19, 4) NOT NULL DEFAULT 0,
    last_updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    version INT NOT NULL DEFAULT 1,
    CONSTRAINT positive_balances CHECK (
        available_balance >= 0 AND
        pending_balance >= 0 AND
        reserved_balance >= 0
    )
);

CREATE TABLE payment_methods (
    payment_method_id UUID PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES users(user_id),
    method_type VARCHAR(20) NOT NULL, -- BANK_ACCOUNT, CREDIT_CARD, DEBIT_CARD
    status VARCHAR(20) NOT NULL, -- ACTIVE, INACTIVE, EXPIRED, REMOVED
    is_default BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    version INT NOT NULL DEFAULT 1
);

CREATE TABLE bank_accounts (
    bank_account_id UUID PRIMARY KEY,
    payment_method_id UUID NOT NULL REFERENCES payment_methods(payment_method_id),
    account_holder_name VARCHAR(255) NOT NULL,
    bank_name VARCHAR(255) NOT NULL,
    account_number VARCHAR(255) NOT NULL,
    routing_number VARCHAR(255) NOT NULL,
    account_type VARCHAR(20) NOT NULL, -- CHECKING, SAVINGS
    is_verified BOOLEAN NOT NULL DEFAULT FALSE,
    verification_method VARCHAR(50), -- MICRO_DEPOSITS, INSTANT_VERIFICATION
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE TABLE payment_cards (
    card_id UUID PRIMARY KEY,
    payment_method_id UUID NOT NULL REFERENCES payment_methods(payment_method_id),
    card_holder_name VARCHAR(255) NOT NULL,
    card_network VARCHAR(20) NOT NULL, -- VISA, MASTERCARD, AMEX
    card_number_hash VARCHAR(255) NOT NULL,
    last_four VARCHAR(4) NOT NULL,
    expiry_month SMALLINT NOT NULL,
    expiry_year SMALLINT NOT NULL,
    billing_address_id UUID REFERENCES user_addresses(address_id),
    is_verified BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL
);

-- Indexes
CREATE INDEX idx_wallets_user_id ON wallets(user_id);
CREATE INDEX idx_wallet_balances_wallet_id ON wallet_balances(wallet_id);
CREATE INDEX idx_payment_methods_user_id ON payment_methods(user_id);
CREATE INDEX idx_bank_accounts_payment_method_id ON bank_accounts(payment_method_id);
CREATE INDEX idx_payment_cards_payment_method_id ON payment_cards(payment_method_id);


CREATE TABLE transactions (
    transaction_id UUID PRIMARY KEY,
    transaction_type VARCHAR(50) NOT NULL, -- DEPOSIT, WITHDRAWAL, TRANSFER, PAYMENT
    transaction_status VARCHAR(20) NOT NULL, -- PENDING, COMPLETED, FAILED, REVERSED
    source_wallet_id UUID REFERENCES wallets(wallet_id),
    destination_wallet_id UUID REFERENCES wallets(wallet_id),
    external_reference_id VARCHAR(255),
    amount DECIMAL(19, 4) NOT NULL,
    currency_code VARCHAR(3) NOT NULL,
    fee_amount DECIMAL(19, 4) NOT NULL DEFAULT 0,
    total_amount DECIMAL(19, 4) NOT NULL,
    description TEXT,
    metadata JSONB,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    completed_at TIMESTAMP WITH TIME ZONE,
    idempotency_key VARCHAR(255),
    ip_address VARCHAR(45),
    user_agent TEXT,
    risk_score DECIMAL(5, 2),
    version INT NOT NULL DEFAULT 1,
    CONSTRAINT check_wallet_ids CHECK (
        (transaction_type = 'TRANSFER' AND source_wallet_id IS NOT NULL AND destination_wallet_id IS NOT NULL) OR
        (transaction_type = 'DEPOSIT' AND destination_wallet_id IS NOT NULL) OR
        (transaction_type = 'WITHDRAWAL' AND source_wallet_id IS NOT NULL) OR
        (transaction_type = 'PAYMENT' AND source_wallet_id IS NOT NULL)
    )
);

CREATE TABLE transaction_events (
    event_id UUID PRIMARY KEY,
    transaction_id UUID NOT NULL REFERENCES transactions(transaction_id),
    event_type VARCHAR(50) NOT NULL, -- INITIATED, AUTHORIZED, SETTLED, FAILED, REVERSED
    event_status VARCHAR(20) NOT NULL, -- SUCCESS, FAILURE, PENDING
    event_data JSONB,
    error_code VARCHAR(50),
    error_message TEXT,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE TABLE ledger_entries (
    entry_id UUID PRIMARY KEY,
    transaction_id UUID NOT NULL REFERENCES transactions(transaction_id),
    wallet_id UUID NOT NULL REFERENCES wallets(wallet_id),
    entry_type VARCHAR(10) NOT NULL, -- DEBIT, CREDIT
    amount DECIMAL(19, 4) NOT NULL,
    balance_after DECIMAL(19, 4) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    version INT NOT NULL DEFAULT 1
);

CREATE TABLE transaction_limits (
    limit_id UUID PRIMARY KEY,
    user_id UUID REFERENCES users(user_id),
    wallet_id UUID REFERENCES wallets(wallet_id),
    transaction_type VARCHAR(50) NOT NULL,
    period_type VARCHAR(20) NOT NULL, -- DAILY, WEEKLY, MONTHLY, YEARLY
    max_amount DECIMAL(19, 4) NOT NULL,
    max_count INT NOT NULL,
    current_amount DECIMAL(19, 4) NOT NULL DEFAULT 0,
    current_count INT NOT NULL DEFAULT 0,
    reset_at TIMESTAMP WITH TIME ZONE NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    version INT NOT NULL DEFAULT 1,
    CONSTRAINT check_reference CHECK (
        (user_id IS NOT NULL AND wallet_id IS NULL) OR
        (user_id IS NULL AND wallet_id IS NOT NULL)
    )
);

-- Indexes
CREATE INDEX idx_transactions_source_wallet_id ON transactions(source_wallet_id);
CREATE INDEX idx_transactions_destination_wallet_id ON transactions(destination_wallet_id);
CREATE INDEX idx_transactions_created_at ON transactions(created_at);
CREATE INDEX idx_transactions_status ON transactions(transaction_status);
CREATE INDEX idx_transaction_events_transaction_id ON transaction_events(transaction_id);
CREATE INDEX idx_ledger_entries_transaction_id ON ledger_entries(transaction_id);
CREATE INDEX idx_ledger_entries_wallet_id ON ledger_entries(wallet_id);
CREATE INDEX idx_transaction_limits_user_id ON transaction_limits(user_id);
CREATE INDEX idx_transaction_limits_wallet_id ON transaction_limits(wallet_id);


@Transactional
public UserProfile updateUserProfile(UUID userId, UserProfileUpdateRequest request, long version) {
    UserProfile userProfile = userRepository.findById(userId)
        .orElseThrow(() -> new UserNotFoundException(userId));
    
    if (userProfile.getVersion() != version) {
        throw new ConcurrentModificationException("Profile was updated by another request");
    }
    
    // Update profile fields
    userProfile.setFirstName(request.getFirstName());
    userProfile.setLastName(request.getLastName());
    // ... other fields
    
    userProfile.setVersion(userProfile.getVersion() + 1);
    return userRepository.save(userProfile);
}

@Transactional
public TransactionResult processPayment(PaymentRequest request) {
    // Acquire lock on wallet to prevent concurrent modifications
    WalletBalance sourceBalance = walletBalanceRepository.findByWalletIdWithLock(request.getSourceWalletId())
        .orElseThrow(() -> new WalletNotFoundException(request.getSourceWalletId()));
    
    // Validate sufficient funds
    if (sourceBalance.getAvailableBalance().compareTo(request.getAmount()) < 0) {
        throw new InsufficientFundsException();
    }
    
    // Create transaction record
    Transaction transaction = new Transaction();
    transaction.setTransactionId(UUID.randomUUID());
    transaction.setTransactionType(TransactionType.PAYMENT);
    transaction.setTransactionStatus(TransactionStatus.PENDING);
    transaction.setSourceWalletId(request.getSourceWalletId());
    transaction.setAmount(request.getAmount());
    transaction.setCurrencyCode(request.getCurrencyCode());
    transaction.setFeeAmount(calculateFee(request));
    transaction.setTotalAmount(request.getAmount().add(transaction.getFeeAmount()));
    transaction.setDescription(request.getDescription());
    transaction.setIdempotencyKey(request.getIdempotencyKey());
    transaction.setCreatedAt(Instant.now());
    transaction.setUpdatedAt(Instant.now());
    
    transactionRepository.save(transaction);
    
    // Update wallet balance
    sourceBalance.setAvailableBalance(sourceBalance.getAvailableBalance().subtract(transaction.getTotalAmount()));
    sourceBalance.setReservedBalance(sourceBalance.getReservedBalance().add(transaction.getTotalAmount()));
    sourceBalance.setLastUpdatedAt(Instant.now());
    sourceBalance.setVersion(sourceBalance.getVersion() + 1);
    
    walletBalanceRepository.save(sourceBalance);
    
    // Create ledger entry
    LedgerEntry debitEntry = new LedgerEntry();
    debitEntry.setEntryId(UUID.randomUUID());
    debitEntry.setTransactionId(transaction.getTransactionId());
    debitEntry.setWalletId(request.getSourceWalletId());
    debitEntry.setEntryType("DEBIT");
    debitEntry.setAmount(transaction.getTotalAmount());
    debitEntry.setBalanceAfter(sourceBalance.getAvailableBalance());
    debitEntry.setCreatedAt(Instant.now());
    
    ledgerEntryRepository.save(debitEntry);
    
    // Asynchronously process the payment with payment processor
    paymentProcessorClient.processPaymentAsync(transaction);
    
    return new TransactionResult(transaction.getTransactionId(), TransactionStatus.PENDING);
}

@Service
public class DistributedLockService {
    private final RedissonClient redissonClient;
    
    public DistributedLockService(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }
    
    public <T> T executeWithLock(String lockKey, long waitTime, long leaseTime, Supplier<T> supplier) {
        RLock lock = redissonClient.getLock(lockKey);
        boolean locked = false;
        try {
            locked = lock.tryLock(waitTime, leaseTime, TimeUnit.MILLISECONDS);
            if (!locked) {
                throw new LockAcquisitionException("Failed to acquire lock: " + lockKey);
            }
            return supplier.get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new LockInterruptedException("Lock acquisition interrupted", e);
        } finally {
            if (locked) {
                lock.unlock();
            }
        }
    }
}

// Usage
@Service
public class TransferService {
    private final DistributedLockService lockService;
    private final WalletService walletService;
    
    public TransactionResult transferFunds(TransferRequest request) {
        return lockService.executeWithLock(
            "transfer:" + request.getSourceWalletId() + ":" + request.getDestinationWalletId(),
            5000, // 5 seconds wait time
            30000, // 30 seconds lease time
            () -> walletService.executeTransfer(request)
        );
    }
}

Shard 1: user_id % 16 = [0-3]
Shard 2: user_id % 16 = [4-7]
Shard 3: user_id % 16 = [8-11]
Shard 4: user_id % 16 = [12-15]

@Configuration
public class ShardingConfiguration {
    @Bean
    public ShardingDataSource shardingDataSource() {
        Map<String, DataSource> dataSourceMap = new HashMap<>();
        dataSourceMap.put("shard1", createDataSource("jdbc:postgresql://shard1:5432/wallet"));
        dataSourceMap.put("shard2", createDataSource("jdbc:postgresql://shard2:5432/wallet"));
        dataSourceMap.put("shard3", createDataSource("jdbc:postgresql://shard3:5432/wallet"));
        dataSourceMap.put("shard4", createDataSource("jdbc:postgresql://shard4:5432/wallet"));
        
        ShardingRuleConfiguration shardingRuleConfig = new ShardingRuleConfiguration();
        
        // Configure transaction table sharding
        TableRuleConfiguration transactionTableRule = new TableRuleConfiguration("transactions", "shard${user_id % 4 + 1}.transactions");
        transactionTableRule.setDatabaseShardingStrategyConfig(
            new StandardShardingStrategyConfiguration("user_id", new UserIdShardingAlgorithm())
        );
        shardingRuleConfig.getTableRuleConfigs().add(transactionTableRule);
        
        return ShardingDataSourceFactory.createDataSource(dataSourceMap, shardingRuleConfig, new Properties());
    }
    
    private DataSource createDataSource(String jdbcUrl) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(jdbcUrl);
        config.setUsername("wallet_user");
        config.setPassword("wallet_password");
        config.setMinimumIdle(10);
        config.setMaximumPoolSize(50);
        return new HikariDataSource(config);
    }
}

public class UserIdShardingAlgorithm implements PreciseShardingAlgorithm<UUID> {
    @Override
    public String doSharding(Collection<String> availableTargetNames, PreciseShardingValue<UUID> shardingValue) {
        int shardIndex = Math.abs(shardingValue.getValue().hashCode() % 4);
        return "shard" + (shardIndex + 1);
    }
}

@Service
public class WalletBalanceService {
    private final WalletBalanceRepository walletBalanceRepository;
    private final RedisTemplate<String, WalletBalanceDTO> redisTemplate;
    private final LoadingCache<UUID, WalletBalanceDTO> localCache;
    
    public WalletBalanceService(WalletBalanceRepository walletBalanceRepository, 
                               RedisTemplate<String, WalletBalanceDTO> redisTemplate) {
        this.walletBalanceRepository = walletBalanceRepository;
        this.redisTemplate = redisTemplate;
        
        this.localCache = CacheBuilder.newBuilder()
            .maximumSize(10_000)
            .expireAfterWrite(10, TimeUnit.SECONDS)
            .build(new CacheLoader<UUID, WalletBalanceDTO>() {
                @Override
                public WalletBalanceDTO load(UUID walletId) {
                    return loadFromRedisOrDatabase(walletId);
                }
            });
    }
    
    public WalletBalanceDTO getWalletBalance(UUID walletId) {
        try {
            return localCache.get(walletId);
        } catch (ExecutionException e) {
            throw new WalletBalanceRetrievalException("Failed to retrieve wallet balance", e);
        }
    }
    
    private WalletBalanceDTO loadFromRedisOrDatabase(UUID walletId) {
        String cacheKey = "wallet:balance:" + walletId;
        WalletBalanceDTO cachedBalance = redisTemplate.opsForValue().get(cacheKey);
        
        if (cachedBalance != null) {
            return cachedBalance;
        }
        
        WalletBalance walletBalance = wallet