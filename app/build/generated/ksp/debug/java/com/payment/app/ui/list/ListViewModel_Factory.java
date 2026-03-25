package com.payment.app.ui.list;

import com.payment.app.data.repository.PaymentRepository;
import com.payment.app.domain.usecase.GetMonthlyPaymentsUseCase;
import com.payment.app.domain.usecase.UpdatePaymentAccountUseCase;
import com.payment.app.domain.usecase.UpdatePaymentAmountUseCase;
import com.payment.app.domain.usecase.UpdatePaymentPaidUseCase;
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
  private final Provider<GetMonthlyPaymentsUseCase> getMonthlyPaymentsUseCaseProvider;

  private final Provider<UpdatePaymentAmountUseCase> updatePaymentAmountUseCaseProvider;

  private final Provider<UpdatePaymentPaidUseCase> updatePaymentPaidUseCaseProvider;

  private final Provider<UpdatePaymentAccountUseCase> updatePaymentAccountUseCaseProvider;

  private final Provider<PaymentRepository> repositoryProvider;

  public ListViewModel_Factory(
      Provider<GetMonthlyPaymentsUseCase> getMonthlyPaymentsUseCaseProvider,
      Provider<UpdatePaymentAmountUseCase> updatePaymentAmountUseCaseProvider,
      Provider<UpdatePaymentPaidUseCase> updatePaymentPaidUseCaseProvider,
      Provider<UpdatePaymentAccountUseCase> updatePaymentAccountUseCaseProvider,
      Provider<PaymentRepository> repositoryProvider) {
    this.getMonthlyPaymentsUseCaseProvider = getMonthlyPaymentsUseCaseProvider;
    this.updatePaymentAmountUseCaseProvider = updatePaymentAmountUseCaseProvider;
    this.updatePaymentPaidUseCaseProvider = updatePaymentPaidUseCaseProvider;
    this.updatePaymentAccountUseCaseProvider = updatePaymentAccountUseCaseProvider;
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public ListViewModel get() {
    return newInstance(getMonthlyPaymentsUseCaseProvider.get(), updatePaymentAmountUseCaseProvider.get(), updatePaymentPaidUseCaseProvider.get(), updatePaymentAccountUseCaseProvider.get(), repositoryProvider.get());
  }

  public static ListViewModel_Factory create(
      Provider<GetMonthlyPaymentsUseCase> getMonthlyPaymentsUseCaseProvider,
      Provider<UpdatePaymentAmountUseCase> updatePaymentAmountUseCaseProvider,
      Provider<UpdatePaymentPaidUseCase> updatePaymentPaidUseCaseProvider,
      Provider<UpdatePaymentAccountUseCase> updatePaymentAccountUseCaseProvider,
      Provider<PaymentRepository> repositoryProvider) {
    return new ListViewModel_Factory(getMonthlyPaymentsUseCaseProvider, updatePaymentAmountUseCaseProvider, updatePaymentPaidUseCaseProvider, updatePaymentAccountUseCaseProvider, repositoryProvider);
  }

  public static ListViewModel newInstance(GetMonthlyPaymentsUseCase getMonthlyPaymentsUseCase,
      UpdatePaymentAmountUseCase updatePaymentAmountUseCase,
      UpdatePaymentPaidUseCase updatePaymentPaidUseCase,
      UpdatePaymentAccountUseCase updatePaymentAccountUseCase, PaymentRepository repository) {
    return new ListViewModel(getMonthlyPaymentsUseCase, updatePaymentAmountUseCase, updatePaymentPaidUseCase, updatePaymentAccountUseCase, repository);
  }
}
