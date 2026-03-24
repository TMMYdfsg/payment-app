package com.payment.app.data.repository;

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

  public PaymentRepository_Factory(Provider<CardDao> cardDaoProvider,
      Provider<PaymentDao> paymentDaoProvider) {
    this.cardDaoProvider = cardDaoProvider;
    this.paymentDaoProvider = paymentDaoProvider;
  }

  @Override
  public PaymentRepository get() {
    return newInstance(cardDaoProvider.get(), paymentDaoProvider.get());
  }

  public static PaymentRepository_Factory create(Provider<CardDao> cardDaoProvider,
      Provider<PaymentDao> paymentDaoProvider) {
    return new PaymentRepository_Factory(cardDaoProvider, paymentDaoProvider);
  }

  public static PaymentRepository newInstance(CardDao cardDao, PaymentDao paymentDao) {
    return new PaymentRepository(cardDao, paymentDao);
  }
}
