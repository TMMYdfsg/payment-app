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
public final class RemoveDriveMemberUseCase_Factory implements Factory<RemoveDriveMemberUseCase> {
  @Override
  public RemoveDriveMemberUseCase get() {
    return newInstance();
  }

  public static RemoveDriveMemberUseCase_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static RemoveDriveMemberUseCase newInstance() {
    return new RemoveDriveMemberUseCase();
  }

  private static final class InstanceHolder {
    private static final RemoveDriveMemberUseCase_Factory INSTANCE = new RemoveDriveMemberUseCase_Factory();
  }
}
