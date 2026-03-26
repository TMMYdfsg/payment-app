package com.payment.app;

import com.payment.app.data.datastore.SettingsDataStore;
import com.payment.app.domain.usecase.SetUnlockGraceUntilUseCase;
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

  private final Provider<SetUnlockGraceUntilUseCase> setUnlockGraceUntilProvider;

  public MainActivity_MembersInjector(Provider<SettingsDataStore> settingsDataStoreProvider,
      Provider<SetUnlockGraceUntilUseCase> setUnlockGraceUntilProvider) {
    this.settingsDataStoreProvider = settingsDataStoreProvider;
    this.setUnlockGraceUntilProvider = setUnlockGraceUntilProvider;
  }

  public static MembersInjector<MainActivity> create(
      Provider<SettingsDataStore> settingsDataStoreProvider,
      Provider<SetUnlockGraceUntilUseCase> setUnlockGraceUntilProvider) {
    return new MainActivity_MembersInjector(settingsDataStoreProvider, setUnlockGraceUntilProvider);
  }

  @Override
  public void injectMembers(MainActivity instance) {
    injectSettingsDataStore(instance, settingsDataStoreProvider.get());
    injectSetUnlockGraceUntil(instance, setUnlockGraceUntilProvider.get());
  }

  @InjectedFieldSignature("com.payment.app.MainActivity.settingsDataStore")
  public static void injectSettingsDataStore(MainActivity instance,
      SettingsDataStore settingsDataStore) {
    instance.settingsDataStore = settingsDataStore;
  }

  @InjectedFieldSignature("com.payment.app.MainActivity.setUnlockGraceUntil")
  public static void injectSetUnlockGraceUntil(MainActivity instance,
      SetUnlockGraceUntilUseCase setUnlockGraceUntil) {
    instance.setUnlockGraceUntil = setUnlockGraceUntil;
  }
}
