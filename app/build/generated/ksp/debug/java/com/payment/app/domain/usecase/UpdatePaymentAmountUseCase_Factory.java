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
public final class UpdatePaymentAmountUseCase_Factory implements Factory<UpdatePaymentAmountUseCase> {
  private final Provider<PaymentRepository> repositoryProvider;

  private final Provider<WidgetUpdater> widgetUpdaterProvider;

  public UpdatePaymentAmountUseCase_Factory(Provider<PaymentRepository> repositoryProvider,
      Provider<WidgetUpdater> widgetUpdaterProvider) {
    this.repositoryProvider = repositoryProvider;
    this.widgetUpdaterProvider = widgetUpdaterProvider;
  }

  @Override
  public UpdatePaymentAmountUseCase get() {
    return newInstance(repositoryProvider.get(), widgetUpdaterProvider.get());
  }

  public static UpdatePaymentAmountUseCase_Factory create(
      Provider<PaymentRepository> repositoryProvider,
      Provider<WidgetUpdater> widgetUpdaterProvider) {
    return new UpdatePaymentAmountUseCase_Factory(repositoryProvider, widgetUpdaterProvider);
  }

  public static UpdatePaymentAmountUseCase newInstance(PaymentRepository repository,
      WidgetUpdater widgetUpdater) {
    return new UpdatePaymentAmountUseCase(repository, widgetUpdater);
  }
}
