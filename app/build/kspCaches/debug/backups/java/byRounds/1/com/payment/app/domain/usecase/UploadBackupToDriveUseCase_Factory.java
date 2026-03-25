package com.payment.app.domain.usecase;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

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
public final class UploadBackupToDriveUseCase_Factory implements Factory<UploadBackupToDriveUseCase> {
  @Override
  public UploadBackupToDriveUseCase get() {
    return newInstance();
  }

  public static UploadBackupToDriveUseCase_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static UploadBackupToDriveUseCase newInstance() {
    return new UploadBackupToDriveUseCase();
  }

  private static final class InstanceHolder {
    private static final UploadBackupToDriveUseCase_Factory INSTANCE = new UploadBackupToDriveUseCase_Factory();
  }
}
