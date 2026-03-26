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
public final class InviteDriveMemberUseCase_Factory implements Factory<InviteDriveMemberUseCase> {
  @Override
  public InviteDriveMemberUseCase get() {
    return newInstance();
  }

  public static InviteDriveMemberUseCase_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static InviteDriveMemberUseCase newInstance() {
    return new InviteDriveMemberUseCase();
  }

  private static final class InstanceHolder {
    private static final InviteDriveMemberUseCase_Factory INSTANCE = new InviteDriveMemberUseCase_Factory();
  }
}
