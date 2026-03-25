package com.payment.app.data.db;

import androidx.annotation.NonNull;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomDatabase;
import androidx.room.RoomOpenHelper;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.room.util.DBUtil;
import androidx.room.util.TableInfo;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class AppDatabase_Impl extends AppDatabase {
  private volatile CardDao _cardDao;

  private volatile PaymentDao _paymentDao;

  private volatile AccountDao _accountDao;

  private volatile BudgetDao _budgetDao;

  private volatile SubscriptionDao _subscriptionDao;

  private volatile InstallmentDao _installmentDao;

  private volatile NotificationSettingDao _notificationSettingDao;

  @Override
  @NonNull
  protected SupportSQLiteOpenHelper createOpenHelper(@NonNull final DatabaseConfiguration config) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(config, new RoomOpenHelper.Delegate(6) {
      @Override
      public void createAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `cards` (`cardId` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `cardName` TEXT NOT NULL, `dueDate` INTEGER NOT NULL, `category` TEXT NOT NULL, `rewardRate` REAL NOT NULL, `annualFee` INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `payments` (`paymentId` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `cardId` INTEGER NOT NULL, `yearMonth` TEXT NOT NULL, `amount` INTEGER NOT NULL, `isPaid` INTEGER NOT NULL, `accountId` INTEGER, `completedAt` INTEGER, `updatedAt` INTEGER NOT NULL, FOREIGN KEY(`cardId`) REFERENCES `cards`(`cardId`) ON UPDATE NO ACTION ON DELETE CASCADE , FOREIGN KEY(`accountId`) REFERENCES `bank_accounts`(`accountId`) ON UPDATE NO ACTION ON DELETE SET NULL )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_payments_cardId` ON `payments` (`cardId`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_payments_yearMonth` ON `payments` (`yearMonth`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_payments_accountId` ON `payments` (`accountId`)");
        db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_payments_cardId_yearMonth` ON `payments` (`cardId`, `yearMonth`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `bank_accounts` (`accountId` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `accountName` TEXT NOT NULL, `bankName` TEXT NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `budgets` (`budgetId` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `yearMonth` TEXT NOT NULL, `category` TEXT, `amount` INTEGER NOT NULL)");
        db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_budgets_yearMonth_category` ON `budgets` (`yearMonth`, `category`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `subscriptions` (`subscriptionId` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `cardId` INTEGER NOT NULL, `serviceName` TEXT NOT NULL, `amount` INTEGER NOT NULL, `billingDay` INTEGER NOT NULL, `isActive` INTEGER NOT NULL, FOREIGN KEY(`cardId`) REFERENCES `cards`(`cardId`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_subscriptions_cardId` ON `subscriptions` (`cardId`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `installments` (`installmentId` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `paymentId` INTEGER NOT NULL, `totalAmount` INTEGER NOT NULL, `totalMonths` INTEGER NOT NULL, `startYearMonth` TEXT NOT NULL, FOREIGN KEY(`paymentId`) REFERENCES `payments`(`paymentId`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_installments_paymentId` ON `installments` (`paymentId`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `notification_settings` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `reminderLeadDays` INTEGER NOT NULL, `budgetAlertThreshold` INTEGER NOT NULL, `monthlyReminderDay` INTEGER NOT NULL, `enabled` INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '7e701bc84931a3b9e58c629752c3c168')");
      }

      @Override
      public void dropAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS `cards`");
        db.execSQL("DROP TABLE IF EXISTS `payments`");
        db.execSQL("DROP TABLE IF EXISTS `bank_accounts`");
        db.execSQL("DROP TABLE IF EXISTS `budgets`");
        db.execSQL("DROP TABLE IF EXISTS `subscriptions`");
        db.execSQL("DROP TABLE IF EXISTS `installments`");
        db.execSQL("DROP TABLE IF EXISTS `notification_settings`");
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onDestructiveMigration(db);
          }
        }
      }

      @Override
      public void onCreate(@NonNull final SupportSQLiteDatabase db) {
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onCreate(db);
          }
        }
      }

      @Override
      public void onOpen(@NonNull final SupportSQLiteDatabase db) {
        mDatabase = db;
        db.execSQL("PRAGMA foreign_keys = ON");
        internalInitInvalidationTracker(db);
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onOpen(db);
          }
        }
      }

      @Override
      public void onPreMigrate(@NonNull final SupportSQLiteDatabase db) {
        DBUtil.dropFtsSyncTriggers(db);
      }

      @Override
      public void onPostMigrate(@NonNull final SupportSQLiteDatabase db) {
      }

      @Override
      @NonNull
      public RoomOpenHelper.ValidationResult onValidateSchema(
          @NonNull final SupportSQLiteDatabase db) {
        final HashMap<String, TableInfo.Column> _columnsCards = new HashMap<String, TableInfo.Column>(6);
        _columnsCards.put("cardId", new TableInfo.Column("cardId", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCards.put("cardName", new TableInfo.Column("cardName", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCards.put("dueDate", new TableInfo.Column("dueDate", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCards.put("category", new TableInfo.Column("category", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCards.put("rewardRate", new TableInfo.Column("rewardRate", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCards.put("annualFee", new TableInfo.Column("annualFee", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysCards = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesCards = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoCards = new TableInfo("cards", _columnsCards, _foreignKeysCards, _indicesCards);
        final TableInfo _existingCards = TableInfo.read(db, "cards");
        if (!_infoCards.equals(_existingCards)) {
          return new RoomOpenHelper.ValidationResult(false, "cards(com.payment.app.data.db.entity.CardEntity).\n"
                  + " Expected:\n" + _infoCards + "\n"
                  + " Found:\n" + _existingCards);
        }
        final HashMap<String, TableInfo.Column> _columnsPayments = new HashMap<String, TableInfo.Column>(8);
        _columnsPayments.put("paymentId", new TableInfo.Column("paymentId", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPayments.put("cardId", new TableInfo.Column("cardId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPayments.put("yearMonth", new TableInfo.Column("yearMonth", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPayments.put("amount", new TableInfo.Column("amount", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPayments.put("isPaid", new TableInfo.Column("isPaid", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPayments.put("accountId", new TableInfo.Column("accountId", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPayments.put("completedAt", new TableInfo.Column("completedAt", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPayments.put("updatedAt", new TableInfo.Column("updatedAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysPayments = new HashSet<TableInfo.ForeignKey>(2);
        _foreignKeysPayments.add(new TableInfo.ForeignKey("cards", "CASCADE", "NO ACTION", Arrays.asList("cardId"), Arrays.asList("cardId")));
        _foreignKeysPayments.add(new TableInfo.ForeignKey("bank_accounts", "SET NULL", "NO ACTION", Arrays.asList("accountId"), Arrays.asList("accountId")));
        final HashSet<TableInfo.Index> _indicesPayments = new HashSet<TableInfo.Index>(4);
        _indicesPayments.add(new TableInfo.Index("index_payments_cardId", false, Arrays.asList("cardId"), Arrays.asList("ASC")));
        _indicesPayments.add(new TableInfo.Index("index_payments_yearMonth", false, Arrays.asList("yearMonth"), Arrays.asList("ASC")));
        _indicesPayments.add(new TableInfo.Index("index_payments_accountId", false, Arrays.asList("accountId"), Arrays.asList("ASC")));
        _indicesPayments.add(new TableInfo.Index("index_payments_cardId_yearMonth", true, Arrays.asList("cardId", "yearMonth"), Arrays.asList("ASC", "ASC")));
        final TableInfo _infoPayments = new TableInfo("payments", _columnsPayments, _foreignKeysPayments, _indicesPayments);
        final TableInfo _existingPayments = TableInfo.read(db, "payments");
        if (!_infoPayments.equals(_existingPayments)) {
          return new RoomOpenHelper.ValidationResult(false, "payments(com.payment.app.data.db.entity.PaymentEntity).\n"
                  + " Expected:\n" + _infoPayments + "\n"
                  + " Found:\n" + _existingPayments);
        }
        final HashMap<String, TableInfo.Column> _columnsBankAccounts = new HashMap<String, TableInfo.Column>(3);
        _columnsBankAccounts.put("accountId", new TableInfo.Column("accountId", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBankAccounts.put("accountName", new TableInfo.Column("accountName", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBankAccounts.put("bankName", new TableInfo.Column("bankName", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysBankAccounts = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesBankAccounts = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoBankAccounts = new TableInfo("bank_accounts", _columnsBankAccounts, _foreignKeysBankAccounts, _indicesBankAccounts);
        final TableInfo _existingBankAccounts = TableInfo.read(db, "bank_accounts");
        if (!_infoBankAccounts.equals(_existingBankAccounts)) {
          return new RoomOpenHelper.ValidationResult(false, "bank_accounts(com.payment.app.data.db.entity.BankAccountEntity).\n"
                  + " Expected:\n" + _infoBankAccounts + "\n"
                  + " Found:\n" + _existingBankAccounts);
        }
        final HashMap<String, TableInfo.Column> _columnsBudgets = new HashMap<String, TableInfo.Column>(4);
        _columnsBudgets.put("budgetId", new TableInfo.Column("budgetId", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBudgets.put("yearMonth", new TableInfo.Column("yearMonth", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBudgets.put("category", new TableInfo.Column("category", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBudgets.put("amount", new TableInfo.Column("amount", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysBudgets = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesBudgets = new HashSet<TableInfo.Index>(1);
        _indicesBudgets.add(new TableInfo.Index("index_budgets_yearMonth_category", true, Arrays.asList("yearMonth", "category"), Arrays.asList("ASC", "ASC")));
        final TableInfo _infoBudgets = new TableInfo("budgets", _columnsBudgets, _foreignKeysBudgets, _indicesBudgets);
        final TableInfo _existingBudgets = TableInfo.read(db, "budgets");
        if (!_infoBudgets.equals(_existingBudgets)) {
          return new RoomOpenHelper.ValidationResult(false, "budgets(com.payment.app.data.db.entity.BudgetEntity).\n"
                  + " Expected:\n" + _infoBudgets + "\n"
                  + " Found:\n" + _existingBudgets);
        }
        final HashMap<String, TableInfo.Column> _columnsSubscriptions = new HashMap<String, TableInfo.Column>(6);
        _columnsSubscriptions.put("subscriptionId", new TableInfo.Column("subscriptionId", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSubscriptions.put("cardId", new TableInfo.Column("cardId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSubscriptions.put("serviceName", new TableInfo.Column("serviceName", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSubscriptions.put("amount", new TableInfo.Column("amount", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSubscriptions.put("billingDay", new TableInfo.Column("billingDay", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSubscriptions.put("isActive", new TableInfo.Column("isActive", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysSubscriptions = new HashSet<TableInfo.ForeignKey>(1);
        _foreignKeysSubscriptions.add(new TableInfo.ForeignKey("cards", "CASCADE", "NO ACTION", Arrays.asList("cardId"), Arrays.asList("cardId")));
        final HashSet<TableInfo.Index> _indicesSubscriptions = new HashSet<TableInfo.Index>(1);
        _indicesSubscriptions.add(new TableInfo.Index("index_subscriptions_cardId", false, Arrays.asList("cardId"), Arrays.asList("ASC")));
        final TableInfo _infoSubscriptions = new TableInfo("subscriptions", _columnsSubscriptions, _foreignKeysSubscriptions, _indicesSubscriptions);
        final TableInfo _existingSubscriptions = TableInfo.read(db, "subscriptions");
        if (!_infoSubscriptions.equals(_existingSubscriptions)) {
          return new RoomOpenHelper.ValidationResult(false, "subscriptions(com.payment.app.data.db.entity.SubscriptionEntity).\n"
                  + " Expected:\n" + _infoSubscriptions + "\n"
                  + " Found:\n" + _existingSubscriptions);
        }
        final HashMap<String, TableInfo.Column> _columnsInstallments = new HashMap<String, TableInfo.Column>(5);
        _columnsInstallments.put("installmentId", new TableInfo.Column("installmentId", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsInstallments.put("paymentId", new TableInfo.Column("paymentId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsInstallments.put("totalAmount", new TableInfo.Column("totalAmount", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsInstallments.put("totalMonths", new TableInfo.Column("totalMonths", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsInstallments.put("startYearMonth", new TableInfo.Column("startYearMonth", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysInstallments = new HashSet<TableInfo.ForeignKey>(1);
        _foreignKeysInstallments.add(new TableInfo.ForeignKey("payments", "CASCADE", "NO ACTION", Arrays.asList("paymentId"), Arrays.asList("paymentId")));
        final HashSet<TableInfo.Index> _indicesInstallments = new HashSet<TableInfo.Index>(1);
        _indicesInstallments.add(new TableInfo.Index("index_installments_paymentId", false, Arrays.asList("paymentId"), Arrays.asList("ASC")));
        final TableInfo _infoInstallments = new TableInfo("installments", _columnsInstallments, _foreignKeysInstallments, _indicesInstallments);
        final TableInfo _existingInstallments = TableInfo.read(db, "installments");
        if (!_infoInstallments.equals(_existingInstallments)) {
          return new RoomOpenHelper.ValidationResult(false, "installments(com.payment.app.data.db.entity.InstallmentEntity).\n"
                  + " Expected:\n" + _infoInstallments + "\n"
                  + " Found:\n" + _existingInstallments);
        }
        final HashMap<String, TableInfo.Column> _columnsNotificationSettings = new HashMap<String, TableInfo.Column>(5);
        _columnsNotificationSettings.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsNotificationSettings.put("reminderLeadDays", new TableInfo.Column("reminderLeadDays", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsNotificationSettings.put("budgetAlertThreshold", new TableInfo.Column("budgetAlertThreshold", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsNotificationSettings.put("monthlyReminderDay", new TableInfo.Column("monthlyReminderDay", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsNotificationSettings.put("enabled", new TableInfo.Column("enabled", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysNotificationSettings = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesNotificationSettings = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoNotificationSettings = new TableInfo("notification_settings", _columnsNotificationSettings, _foreignKeysNotificationSettings, _indicesNotificationSettings);
        final TableInfo _existingNotificationSettings = TableInfo.read(db, "notification_settings");
        if (!_infoNotificationSettings.equals(_existingNotificationSettings)) {
          return new RoomOpenHelper.ValidationResult(false, "notification_settings(com.payment.app.data.db.entity.NotificationSettingEntity).\n"
                  + " Expected:\n" + _infoNotificationSettings + "\n"
                  + " Found:\n" + _existingNotificationSettings);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "7e701bc84931a3b9e58c629752c3c168", "23728c9b9094ca1eabf983210d20eafe");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "cards","payments","bank_accounts","budgets","subscriptions","installments","notification_settings");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    final boolean _supportsDeferForeignKeys = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP;
    try {
      if (!_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA foreign_keys = FALSE");
      }
      super.beginTransaction();
      if (_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA defer_foreign_keys = TRUE");
      }
      _db.execSQL("DELETE FROM `cards`");
      _db.execSQL("DELETE FROM `payments`");
      _db.execSQL("DELETE FROM `bank_accounts`");
      _db.execSQL("DELETE FROM `budgets`");
      _db.execSQL("DELETE FROM `subscriptions`");
      _db.execSQL("DELETE FROM `installments`");
      _db.execSQL("DELETE FROM `notification_settings`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
      if (!_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA foreign_keys = TRUE");
      }
      _db.query("PRAGMA wal_checkpoint(FULL)").close();
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM");
      }
    }
  }

  @Override
  @NonNull
  protected Map<Class<?>, List<Class<?>>> getRequiredTypeConverters() {
    final HashMap<Class<?>, List<Class<?>>> _typeConvertersMap = new HashMap<Class<?>, List<Class<?>>>();
    _typeConvertersMap.put(CardDao.class, CardDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(PaymentDao.class, PaymentDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(AccountDao.class, AccountDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(BudgetDao.class, BudgetDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(SubscriptionDao.class, SubscriptionDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(InstallmentDao.class, InstallmentDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(NotificationSettingDao.class, NotificationSettingDao_Impl.getRequiredConverters());
    return _typeConvertersMap;
  }

  @Override
  @NonNull
  public Set<Class<? extends AutoMigrationSpec>> getRequiredAutoMigrationSpecs() {
    final HashSet<Class<? extends AutoMigrationSpec>> _autoMigrationSpecsSet = new HashSet<Class<? extends AutoMigrationSpec>>();
    return _autoMigrationSpecsSet;
  }

  @Override
  @NonNull
  public List<Migration> getAutoMigrations(
      @NonNull final Map<Class<? extends AutoMigrationSpec>, AutoMigrationSpec> autoMigrationSpecs) {
    final List<Migration> _autoMigrations = new ArrayList<Migration>();
    return _autoMigrations;
  }

  @Override
  public CardDao cardDao() {
    if (_cardDao != null) {
      return _cardDao;
    } else {
      synchronized(this) {
        if(_cardDao == null) {
          _cardDao = new CardDao_Impl(this);
        }
        return _cardDao;
      }
    }
  }

  @Override
  public PaymentDao paymentDao() {
    if (_paymentDao != null) {
      return _paymentDao;
    } else {
      synchronized(this) {
        if(_paymentDao == null) {
          _paymentDao = new PaymentDao_Impl(this);
        }
        return _paymentDao;
      }
    }
  }

  @Override
  public AccountDao accountDao() {
    if (_accountDao != null) {
      return _accountDao;
    } else {
      synchronized(this) {
        if(_accountDao == null) {
          _accountDao = new AccountDao_Impl(this);
        }
        return _accountDao;
      }
    }
  }

  @Override
  public BudgetDao budgetDao() {
    if (_budgetDao != null) {
      return _budgetDao;
    } else {
      synchronized(this) {
        if(_budgetDao == null) {
          _budgetDao = new BudgetDao_Impl(this);
        }
        return _budgetDao;
      }
    }
  }

  @Override
  public SubscriptionDao subscriptionDao() {
    if (_subscriptionDao != null) {
      return _subscriptionDao;
    } else {
      synchronized(this) {
        if(_subscriptionDao == null) {
          _subscriptionDao = new SubscriptionDao_Impl(this);
        }
        return _subscriptionDao;
      }
    }
  }

  @Override
  public InstallmentDao installmentDao() {
    if (_installmentDao != null) {
      return _installmentDao;
    } else {
      synchronized(this) {
        if(_installmentDao == null) {
          _installmentDao = new InstallmentDao_Impl(this);
        }
        return _installmentDao;
      }
    }
  }

  @Override
  public NotificationSettingDao notificationSettingDao() {
    if (_notificationSettingDao != null) {
      return _notificationSettingDao;
    } else {
      synchronized(this) {
        if(_notificationSettingDao == null) {
          _notificationSettingDao = new NotificationSettingDao_Impl(this);
        }
        return _notificationSettingDao;
      }
    }
  }
}
