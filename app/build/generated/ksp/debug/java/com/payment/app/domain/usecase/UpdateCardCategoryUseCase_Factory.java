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
public final class UpdateCardCategoryUseCase_Factory implements Factory<UpdateCardCategoryUseCase> {
  private final Provider<PaymentRepository> repositoryProvider;

  public UpdateCardCategoryUseCase_Factory(Provider<PaymentRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public UpdateCardCategoryUseCase get() {
    return newInstance(repositoryProvider.get());
  }

  public static UpdateCardCategoryUseCase_Factory create(
      Provider<PaymentRepository> repositoryProvider) {
    return new UpdateCardCategoryUseCase_Factory(repositoryProvider);
  }

  public static UpdateCardCategoryUseCase newInstance(PaymentRepository repository) {
    return new UpdateCardCategoryUseCase(repository);
  }
}
