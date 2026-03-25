package com.payment.app.data.repository;

import com.payment.app.data.db.AccountDao;
import com.payment.app.data.db.BudgetDao;
import com.payment.app.data.db.CardDao;
import com.payment.app.data.db.InstallmentDao;
import com.payment.app.data.db.NotificationSettingDao;
import com.payment.app.data.db.PaymentDao;
import com.payment.app.data.db.SubscriptionDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast",
    "deprecation"
})
public final class PaymentRepository_Factory implements Factory<PaymentRepository> {
  private final Provider<CardDao> cardDaoProvider;

  private final Provider<PaymentDao> paymentDaoProvider;

  private final Provider<AccountDao> accountDaoProvider;

  private final Provider<BudgetDao> budgetDaoProvider;

  private final Provider<SubscriptionDao> subscriptionDaoProvider;

  private final Provider<InstallmentDao> installmentDaoProvider;

  private final Provider<NotificationSettingDao> notificationSettingDaoProvider;

  public PaymentRepository_Factory(Provider<CardDao> cardDaoProvider,
      Provider<PaymentDao> paymentDaoProvider, Provider<AccountDao> accountDaoProvider,
      Provider<BudgetDao> budgetDaoProvider, Provider<SubscriptionDao> subscriptionDaoProvider,
      Provider<InstallmentDao> installmentDaoProvider,
      Provider<NotificationSettingDao> notificationSettingDaoProvider) {
    this.cardDaoProvider = cardDaoProvider;
    this.paymentDaoProvider = paymentDaoProvider;
    this.accountDaoProvider = accountDaoProvider;
    this.budgetDaoProvider = budgetDaoProvider;
    this.subscriptionDaoProvider = subscriptionDaoProvider;
    this.installmentDaoProvider = installmentDaoProvider;
    this.notificationSettingDaoProvider = notificationSettingDaoProvider;
  }

  @Override
  public PaymentRepository get() {
    return newInstance(cardDaoProvider.get(), paymentDaoProvider.get(), accountDaoProvider.get(), budgetDaoProvider.get(), subscriptionDaoProvider.get(), installmentDaoProvider.get(), notificationSettingDaoProvider.get());
  }

  public static PaymentRepository_Factory create(Provider<CardDao> cardDaoProvider,
      Provider<PaymentDao> paymentDaoProvider, Provider<AccountDao> accountDaoProvider,
      Provider<BudgetDao> budgetDaoProvider, Provider<SubscriptionDao> subscriptionDaoProvider,
      Provider<InstallmentDao> installmentDaoProvider,
      Provider<NotificationSettingDao> notificationSettingDaoProvider) {
    return new PaymentRepository_Factory(cardDaoProvider, paymentDaoProvider, accountDaoProvider, budgetDaoProvider, subscriptionDaoProvider, installmentDaoProvider, notificationSettingDaoProvider);
  }

  public static PaymentRepository newInstance(CardDao cardDao, PaymentDao paymentDao,
      AccountDao accountDao, BudgetDao budgetDao, SubscriptionDao subscriptionDao,
      InstallmentDao installmentDao, NotificationSettingDao notificationSettingDao) {
    return new PaymentRepository(cardDao, paymentDao, accountDao, budgetDao, subscriptionDao, installmentDao, notificationSettingDao);
  }
}
