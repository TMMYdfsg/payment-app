package com.payment.app.ui.input;

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
public final class InputFlowViewModel_Factory implements Factory<InputFlowViewModel> {
  private final Provider<PaymentRepository> repositoryProvider;

  public InputFlowViewModel_Factory(Provider<PaymentRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public InputFlowViewModel get() {
    return newInstance(repositoryProvider.get());
  }

  public static InputFlowViewModel_Factory create(Provider<PaymentRepository> repositoryProvider) {
    return new InputFlowViewModel_Factory(repositoryProvider);
  }

  public static InputFlowViewModel newInstance(PaymentRepository repository) {
    return new InputFlowViewModel(repository);
  }
}
