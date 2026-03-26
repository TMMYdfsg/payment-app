package com.payment.app.domain.usecase;

import com.payment.app.data.repository.PaymentRepository;
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
public final class ImportBackupJsonUseCase_Factory implements Factory<ImportBackupJsonUseCase> {
  private final Provider<PaymentRepository> repositoryProvider;

  public ImportBackupJsonUseCase_Factory(Provider<PaymentRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public ImportBackupJsonUseCase get() {
    return newInstance(repositoryProvider.get());
  }

  public static ImportBackupJsonUseCase_Factory create(
      Provider<PaymentRepository> repositoryProvider) {
    return new ImportBackupJsonUseCase_Factory(repositoryProvider);
  }

  public static ImportBackupJsonUseCase newInstance(PaymentRepository repository) {
    return new ImportBackupJsonUseCase(repository);
  }
}
