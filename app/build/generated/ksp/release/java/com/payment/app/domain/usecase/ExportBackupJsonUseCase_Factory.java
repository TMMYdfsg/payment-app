package com.payment.app.domain.usecase;

import android.content.Context;
import com.payment.app.data.repository.PaymentRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
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
public final class ExportBackupJsonUseCase_Factory implements Factory<ExportBackupJsonUseCase> {
  private final Provider<PaymentRepository> repositoryProvider;

  private final Provider<Context> contextProvider;

  public ExportBackupJsonUseCase_Factory(Provider<PaymentRepository> repositoryProvider,
      Provider<Context> contextProvider) {
    this.repositoryProvider = repositoryProvider;
    this.contextProvider = contextProvider;
  }

  @Override
  public ExportBackupJsonUseCase get() {
    return newInstance(repositoryProvider.get(), contextProvider.get());
  }

  public static ExportBackupJsonUseCase_Factory create(
      Provider<PaymentRepository> repositoryProvider, Provider<Context> contextProvider) {
    return new ExportBackupJsonUseCase_Factory(repositoryProvider, contextProvider);
  }

  public static ExportBackupJsonUseCase newInstance(PaymentRepository repository, Context context) {
    return new ExportBackupJsonUseCase(repository, context);
  }
}
