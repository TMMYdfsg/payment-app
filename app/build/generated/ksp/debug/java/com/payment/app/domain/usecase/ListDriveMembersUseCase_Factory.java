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
public final class ListDriveMembersUseCase_Factory implements Factory<ListDriveMembersUseCase> {
  @Override
  public ListDriveMembersUseCase get() {
    return newInstance();
  }

  public static ListDriveMembersUseCase_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static ListDriveMembersUseCase newInstance() {
    return new ListDriveMembersUseCase();
  }

  private static final class InstanceHolder {
    private static final ListDriveMembersUseCase_Factory INSTANCE = new ListDriveMembersUseCase_Factory();
  }
}
