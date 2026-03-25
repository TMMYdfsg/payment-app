package com.payment.app.di;

import com.payment.app.data.db.AppDatabase;
import com.payment.app.data.db.InstallmentDao;
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
public final class AppModule_ProvideInstallmentDaoFactory implements Factory<InstallmentDao> {
  private final Provider<AppDatabase> dbProvider;

  public AppModule_ProvideInstallmentDaoFactory(Provider<AppDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public InstallmentDao get() {
    return provideInstallmentDao(dbProvider.get());
  }

  public static AppModule_ProvideInstallmentDaoFactory create(Provider<AppDatabase> dbProvider) {
    return new AppModule_ProvideInstallmentDaoFactory(dbProvider);
  }

  public static InstallmentDao provideInstallmentDao(AppDatabase db) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideInstallmentDao(db));
  }
}
