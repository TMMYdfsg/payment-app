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
public final class UpdateDriveMemberRoleUseCase_Factory implements Factory<UpdateDriveMemberRoleUseCase> {
  @Override
  public UpdateDriveMemberRoleUseCase get() {
    return newInstance();
  }

  public static UpdateDriveMemberRoleUseCase_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static UpdateDriveMemberRoleUseCase newInstance() {
    return new UpdateDriveMemberRoleUseCase();
  }

  private static final class InstanceHolder {
    private static final UpdateDriveMemberRoleUseCase_Factory INSTANCE = new UpdateDriveMemberRoleUseCase_Factory();
  }
}
