package com.payment.app.di;

import com.payment.app.data.db.AppDatabase;
import com.payment.app.data.db.CardDao;
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
public final class AppModule_ProvideCardDaoFactory implements Factory<CardDao> {
  private final Provider<AppDatabase> dbProvider;

  public AppModule_ProvideCardDaoFactory(Provider<AppDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public CardDao get() {
    return provideCardDao(dbProvider.get());
  }

  public static AppModule_ProvideCardDaoFactory create(Provider<AppDatabase> dbProvider) {
    return new AppModule_ProvideCardDaoFactory(dbProvider);
  }

  public static CardDao provideCardDao(AppDatabase db) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideCardDao(db));
  }
}
