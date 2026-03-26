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
public final class CreateDriveFolderUseCase_Factory implements Factory<CreateDriveFolderUseCase> {
  @Override
  public CreateDriveFolderUseCase get() {
    return newInstance();
  }

  public static CreateDriveFolderUseCase_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static CreateDriveFolderUseCase newInstance() {
    return new CreateDriveFolderUseCase();
  }

  private static final class InstanceHolder {
    private static final CreateDriveFolderUseCase_Factory INSTANCE = new CreateDriveFolderUseCase_Factory();
  }
}
