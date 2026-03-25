package com.payment.app.di;

import com.payment.app.data.db.AppDatabase;
import com.payment.app.data.db.NotificationSettingDao;
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
public final class AppModule_ProvideNotificationSettingDaoFactory implements Factory<NotificationSettingDao> {
  private final Provider<AppDatabase> dbProvider;

  public AppModule_ProvideNotificationSettingDaoFactory(Provider<AppDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public NotificationSettingDao get() {
    return provideNotificationSettingDao(dbProvider.get());
  }

  public static AppModule_ProvideNotificationSettingDaoFactory create(
      Provider<AppDatabase> dbProvider) {
    return new AppModule_ProvideNotificationSettingDaoFactory(dbProvider);
  }

  public static NotificationSettingDao provideNotificationSettingDao(AppDatabase db) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideNotificationSettingDao(db));
  }
}
