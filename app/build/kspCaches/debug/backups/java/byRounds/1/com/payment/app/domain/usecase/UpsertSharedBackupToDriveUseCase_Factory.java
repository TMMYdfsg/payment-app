package com.payment.app.domain.usecase;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
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
public final class UpsertSharedBackupToDriveUseCase_Factory implements Factory<UpsertSharedBackupToDriveUseCase> {
  private final Provider<UploadBackupToDriveUseCase> uploadBackupToDriveUseCaseProvider;

  public UpsertSharedBackupToDriveUseCase_Factory(
      Provider<UploadBackupToDriveUseCase> uploadBackupToDriveUseCaseProvider) {
    this.uploadBackupToDriveUseCaseProvider = uploadBackupToDriveUseCaseProvider;
  }

  @Override
  public UpsertSharedBackupToDriveUseCase get() {
    return newInstance(uploadBackupToDriveUseCaseProvider.get());
  }

  public static UpsertSharedBackupToDriveUseCase_Factory create(
      Provider<UploadBackupToDriveUseCase> uploadBackupToDriveUseCaseProvider) {
    return new UpsertSharedBackupToDriveUseCase_Factory(uploadBackupToDriveUseCaseProvider);
  }

  public static UpsertSharedBackupToDriveUseCase newInstance(
      UploadBackupToDriveUseCase uploadBackupToDriveUseCase) {
    return new UpsertSharedBackupToDriveUseCase(uploadBackupToDriveUseCase);
  }
}
