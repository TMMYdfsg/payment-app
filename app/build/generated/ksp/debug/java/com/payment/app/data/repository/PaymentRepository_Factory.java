package com.payment.app.data.repository;

import com.payment.app.data.db.AccountDao;
import com.payment.app.data.db.CardDao;
import com.payment.app.data.db.PaymentDao;
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

  public PaymentRepository_Factory(Provider<CardDao> cardDaoProvider,
      Provider<PaymentDao> paymentDaoProvider, Provider<AccountDao> accountDaoProvider) {
    this.cardDaoProvider = cardDaoProvider;
    this.paymentDaoProvider = paymentDaoProvider;
    this.accountDaoProvider = accountDaoProvider;
  }

  @Override
  public PaymentRepository get() {
    return newInstance(cardDaoProvider.get(), paymentDaoProvider.get(), accountDaoProvider.get());
  }

  public static PaymentRepository_Factory create(Provider<CardDao> cardDaoProvider,
      Provider<PaymentDao> paymentDaoProvider, Provider<AccountDao> accountDaoProvider) {
    return new PaymentRepository_Factory(cardDaoProvider, paymentDaoProvider, accountDaoProvider);
  }

  public static PaymentRepository newInstance(CardDao cardDao, PaymentDao paymentDao,
      AccountDao accountDao) {
    return new PaymentRepository(cardDao, paymentDao, accountDao);
  }
}
