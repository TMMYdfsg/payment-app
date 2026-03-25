package com.payment.app;

import com.payment.app.data.datastore.SettingsDataStore;
import dagger.MembersInjector;
import dagger.internal.DaggerGenerated;
import dagger.internal.InjectedFieldSignature;
import dagger.internal.QualifierMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

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
public final class MainActivity_MembersInjector implements MembersInjector<MainActivity> {
  private final Provider<SettingsDataStore> settingsDataStoreProvider;

  public MainActivity_MembersInjector(Provider<SettingsDataStore> settingsDataStoreProvider) {
    this.settingsDataStoreProvider = settingsDataStoreProvider;
  }

  public static MembersInjector<MainActivity> create(
      Provider<SettingsDataStore> settingsDataStoreProvider) {
    return new MainActivity_MembersInjector(settingsDataStoreProvider);
  }

  @Override
  public void injectMembers(MainActivity instance) {
    injectSettingsDataStore(instance, settingsDataStoreProvider.get());
  }

  @InjectedFieldSignature("com.payment.app.MainActivity.settingsDataStore")
  public static void injectSettingsDataStore(MainActivity instance,
      SettingsDataStore settingsDataStore) {
    instance.settingsDataStore = settingsDataStore;
  }
}
