package com.payment.app.ui.list;

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
public final class ListViewModel_Factory implements Factory<ListViewModel> {
  private final Provider<PaymentRepository> repositoryProvider;

  public ListViewModel_Factory(Provider<PaymentRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public ListViewModel get() {
    return newInstance(repositoryProvider.get());
  }

  public static ListViewModel_Factory create(Provider<PaymentRepository> repositoryProvider) {
    return new ListViewModel_Factory(repositoryProvider);
  }

  public static ListViewModel newInstance(PaymentRepository repository) {
    return new ListViewModel(repository);
  }
}
