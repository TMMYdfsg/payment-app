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
public final class DownloadSharedBackupFromDriveUseCase_Factory implements Factory<DownloadSharedBackupFromDriveUseCase> {
  @Override
  public DownloadSharedBackupFromDriveUseCase get() {
    return newInstance();
  }

  public static DownloadSharedBackupFromDriveUseCase_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static DownloadSharedBackupFromDriveUseCase newInstance() {
    return new DownloadSharedBackupFromDriveUseCase();
  }

  private static final class InstanceHolder {
    private static final DownloadSharedBackupFromDriveUseCase_Factory INSTANCE = new DownloadSharedBackupFromDriveUseCase_Factory();
  }
}
