package com.payment.app.ui.input;

import com.payment.app.data.repository.PaymentRepository;
import com.payment.app.domain.usecase.GetMonthlyPaymentsOnceUseCase;
import com.payment.app.domain.usecase.GetOcrProfilesJsonUseCase;
import com.payment.app.domain.usecase.RecognizeAmountFromImageUseCase;
import com.payment.app.domain.usecase.SetOcrProfilesJsonUseCase;
import com.payment.app.domain.usecase.UpdateCardCategoryUseCase;
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
public final class InputFlowViewModel_Factory implements Factory<InputFlowViewModel> {
  private final Provider<PaymentRepository> repositoryProvider;

  private final Provider<GetMonthlyPaymentsOnceUseCase> getMonthlyPaymentsOnceUseCaseProvider;

  private final Provider<RecognizeAmountFromImageUseCase> recognizeAmountFromImageUseCaseProvider;

  private final Provider<GetOcrProfilesJsonUseCase> getOcrProfilesJsonUseCaseProvider;

  private final Provider<SetOcrProfilesJsonUseCase> setOcrProfilesJsonUseCaseProvider;

  private final Provider<UpdateCardCategoryUseCase> updateCardCategoryUseCaseProvider;

  private final Provider<UpdatePaymentAmountUseCase> updatePaymentAmountUseCaseProvider;

  private final Provider<UpdatePaymentPaidUseCase> updatePaymentPaidUseCaseProvider;

  private final Provider<UpdatePaymentAccountUseCase> updatePaymentAccountUseCaseProvider;

  public InputFlowViewModel_Factory(Provider<PaymentRepository> repositoryProvider,
      Provider<GetMonthlyPaymentsOnceUseCase> getMonthlyPaymentsOnceUseCaseProvider,
      Provider<RecognizeAmountFromImageUseCase> recognizeAmountFromImageUseCaseProvider,
      Provider<GetOcrProfilesJsonUseCase> getOcrProfilesJsonUseCaseProvider,
      Provider<SetOcrProfilesJsonUseCase> setOcrProfilesJsonUseCaseProvider,
      Provider<UpdateCardCategoryUseCase> updateCardCategoryUseCaseProvider,
      Provider<UpdatePaymentAmountUseCase> updatePaymentAmountUseCaseProvider,
      Provider<UpdatePaymentPaidUseCase> updatePaymentPaidUseCaseProvider,
      Provider<UpdatePaymentAccountUseCase> updatePaymentAccountUseCaseProvider) {
    this.repositoryProvider = repositoryProvider;
    this.getMonthlyPaymentsOnceUseCaseProvider = getMonthlyPaymentsOnceUseCaseProvider;
    this.recognizeAmountFromImageUseCaseProvider = recognizeAmountFromImageUseCaseProvider;
    this.getOcrProfilesJsonUseCaseProvider = getOcrProfilesJsonUseCaseProvider;
    this.setOcrProfilesJsonUseCaseProvider = setOcrProfilesJsonUseCaseProvider;
    this.updateCardCategoryUseCaseProvider = updateCardCategoryUseCaseProvider;
    this.updatePaymentAmountUseCaseProvider = updatePaymentAmountUseCaseProvider;
    this.updatePaymentPaidUseCaseProvider = updatePaymentPaidUseCaseProvider;
    this.updatePaymentAccountUseCaseProvider = updatePaymentAccountUseCaseProvider;
  }

  @Override
  public InputFlowViewModel get() {
    return newInstance(repositoryProvider.get(), getMonthlyPaymentsOnceUseCaseProvider.get(), recognizeAmountFromImageUseCaseProvider.get(), getOcrProfilesJsonUseCaseProvider.get(), setOcrProfilesJsonUseCaseProvider.get(), updateCardCategoryUseCaseProvider.get(), updatePaymentAmountUseCaseProvider.get(), updatePaymentPaidUseCaseProvider.get(), updatePaymentAccountUseCaseProvider.get());
  }

  public static InputFlowViewModel_Factory create(Provider<PaymentRepository> repositoryProvider,
      Provider<GetMonthlyPaymentsOnceUseCase> getMonthlyPaymentsOnceUseCaseProvider,
      Provider<RecognizeAmountFromImageUseCase> recognizeAmountFromImageUseCaseProvider,
      Provider<GetOcrProfilesJsonUseCase> getOcrProfilesJsonUseCaseProvider,
      Provider<SetOcrProfilesJsonUseCase> setOcrProfilesJsonUseCaseProvider,
      Provider<UpdateCardCategoryUseCase> updateCardCategoryUseCaseProvider,
      Provider<UpdatePaymentAmountUseCase> updatePaymentAmountUseCaseProvider,
      Provider<UpdatePaymentPaidUseCase> updatePaymentPaidUseCaseProvider,
      Provider<UpdatePaymentAccountUseCase> updatePaymentAccountUseCaseProvider) {
    return new InputFlowViewModel_Factory(repositoryProvider, getMonthlyPaymentsOnceUseCaseProvider, recognizeAmountFromImageUseCaseProvider, getOcrProfilesJsonUseCaseProvider, setOcrProfilesJsonUseCaseProvider, updateCardCategoryUseCaseProvider, updatePaymentAmountUseCaseProvider, updatePaymentPaidUseCaseProvider, updatePaymentAccountUseCaseProvider);
  }

  public static InputFlowViewModel newInstance(PaymentRepository repository,
      GetMonthlyPaymentsOnceUseCase getMonthlyPaymentsOnceUseCase,
      RecognizeAmountFromImageUseCase recognizeAmountFromImageUseCase,
      GetOcrProfilesJsonUseCase getOcrProfilesJsonUseCase,
      SetOcrProfilesJsonUseCase setOcrProfilesJsonUseCase,
      UpdateCardCategoryUseCase updateCardCategoryUseCase,
      UpdatePaymentAmountUseCase updatePaymentAmountUseCase,
      UpdatePaymentPaidUseCase updatePaymentPaidUseCase,
      UpdatePaymentAccountUseCase updatePaymentAccountUseCase) {
    return new InputFlowViewModel(repository, getMonthlyPaymentsOnceUseCase, recognizeAmountFromImageUseCase, getOcrProfilesJsonUseCase, setOcrProfilesJsonUseCase, updateCardCategoryUseCase, updatePaymentAmountUseCase, updatePaymentPaidUseCase, updatePaymentAccountUseCase);
  }
}
