package com.payment.app.di;

import com.payment.app.data.db.AppDatabase;
import com.payment.app.data.db.SubscriptionDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
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
public final class AppModule_ProvideSubscriptionDaoFactory implements Factory<SubscriptionDao> {
  private final Provider<AppDatabase> dbProvider;

  public AppModule_ProvideSubscriptionDaoFactory(Provider<AppDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public SubscriptionDao get() {
    return provideSubscriptionDao(dbProvider.get());
  }

  public static AppModule_ProvideSubscriptionDaoFactory create(Provider<AppDatabase> dbProvider) {
    return new AppModule_ProvideSubscriptionDaoFactory(dbProvider);
  }

  public static SubscriptionDao provideSubscriptionDao(AppDatabase db) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideSubscriptionDao(db));
  }
}
