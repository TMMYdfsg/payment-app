package com.payment.app.domain.usecase;

import com.payment.app.data.repository.PaymentRepository;
import com.payment.app.widget.WidgetUpdater;
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
public final class MarkAllPaidUseCase_Factory implements Factory<MarkAllPaidUseCase> {
  private final Provider<PaymentRepository> repositoryProvider;

  private final Provider<WidgetUpdater> widgetUpdaterProvider;

  public MarkAllPaidUseCase_Factory(Provider<PaymentRepository> repositoryProvider,
      Provider<WidgetUpdater> widgetUpdaterProvider) {
    this.repositoryProvider = repositoryProvider;
    this.widgetUpdaterProvider = widgetUpdaterProvider;
  }

  @Override
  public MarkAllPaidUseCase get() {
    return newInstance(repositoryProvider.get(), widgetUpdaterProvider.get());
  }

  public static MarkAllPaidUseCase_Factory create(Provider<PaymentRepository> repositoryProvider,
      Provider<WidgetUpdater> widgetUpdaterProvider) {
    return new MarkAllPaidUseCase_Factory(repositoryProvider, widgetUpdaterProvider);
  }

  public static MarkAllPaidUseCase newInstance(PaymentRepository repository,
      WidgetUpdater widgetUpdater) {
    return new MarkAllPaidUseCase(repository, widgetUpdater);
  }
}
