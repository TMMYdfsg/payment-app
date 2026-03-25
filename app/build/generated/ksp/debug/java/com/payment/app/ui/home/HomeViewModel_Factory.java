package com.payment.app.ui.home;

import com.payment.app.data.repository.PaymentRepository;
import com.payment.app.domain.usecase.ExportBackupJsonUseCase;
import com.payment.app.domain.usecase.ExportPaymentsUseCase;
import com.payment.app.domain.usecase.GetBudgetUseCase;
import com.payment.app.domain.usecase.GetMonthlyPaymentsOnceUseCase;
import com.payment.app.domain.usecase.GetMonthlyPaymentsUseCase;
import com.payment.app.domain.usecase.MarkAllPaidUseCase;
import com.payment.app.domain.usecase.ResetMonthAmountsUseCase;
import com.payment.app.domain.usecase.UpdateBudgetUseCase;
import com.payment.app.domain.usecase.UploadBackupToDriveUseCase;
import com.payment.app.notifications.ReminderScheduler;
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
public final class HomeViewModel_Factory implements Factory<HomeViewModel> {
  private final Provider<GetMonthlyPaymentsUseCase> getMonthlyPaymentsUseCaseProvider;

  private final Provider<GetBudgetUseCase> getBudgetUseCaseProvider;

  private final Provider<MarkAllPaidUseCase> markAllPaidUseCaseProvider;

  private final Provider<UpdateBudgetUseCase> updateBudgetUseCaseProvider;

  private final Provider<ExportPaymentsUseCase> exportPaymentsUseCaseProvider;

  private final Provider<ExportBackupJsonUseCase> exportBackupJsonUseCaseProvider;

  private final Provider<UploadBackupToDriveUseCase> uploadBackupToDriveUseCaseProvider;

  private final Provider<GetMonthlyPaymentsOnceUseCase> getMonthlyPaymentsOnceUseCaseProvider;

  private final Provider<ResetMonthAmountsUseCase> resetMonthAmountsUseCaseProvider;

  private final Provider<PaymentRepository> repositoryProvider;

  private final Provider<ReminderScheduler> reminderSchedulerProvider;

  public HomeViewModel_Factory(
      Provider<GetMonthlyPaymentsUseCase> getMonthlyPaymentsUseCaseProvider,
      Provider<GetBudgetUseCase> getBudgetUseCaseProvider,
      Provider<MarkAllPaidUseCase> markAllPaidUseCaseProvider,
      Provider<UpdateBudgetUseCase> updateBudgetUseCaseProvider,
      Provider<ExportPaymentsUseCase> exportPaymentsUseCaseProvider,
      Provider<ExportBackupJsonUseCase> exportBackupJsonUseCaseProvider,
      Provider<UploadBackupToDriveUseCase> uploadBackupToDriveUseCaseProvider,
      Provider<GetMonthlyPaymentsOnceUseCase> getMonthlyPaymentsOnceUseCaseProvider,
      Provider<ResetMonthAmountsUseCase> resetMonthAmountsUseCaseProvider,
      Provider<PaymentRepository> repositoryProvider,
      Provider<ReminderScheduler> reminderSchedulerProvider) {
    this.getMonthlyPaymentsUseCaseProvider = getMonthlyPaymentsUseCaseProvider;
    this.getBudgetUseCaseProvider = getBudgetUseCaseProvider;
    this.markAllPaidUseCaseProvider = markAllPaidUseCaseProvider;
    this.updateBudgetUseCaseProvider = updateBudgetUseCaseProvider;
    this.exportPaymentsUseCaseProvider = exportPaymentsUseCaseProvider;
    this.exportBackupJsonUseCaseProvider = exportBackupJsonUseCaseProvider;
    this.uploadBackupToDriveUseCaseProvider = uploadBackupToDriveUseCaseProvider;
    this.getMonthlyPaymentsOnceUseCaseProvider = getMonthlyPaymentsOnceUseCaseProvider;
    this.resetMonthAmountsUseCaseProvider = resetMonthAmountsUseCaseProvider;
    this.repositoryProvider = repositoryProvider;
    this.reminderSchedulerProvider = reminderSchedulerProvider;
  }

  @Override
  public HomeViewModel get() {
    return newInstance(getMonthlyPaymentsUseCaseProvider.get(), getBudgetUseCaseProvider.get(), markAllPaidUseCaseProvider.get(), updateBudgetUseCaseProvider.get(), exportPaymentsUseCaseProvider.get(), exportBackupJsonUseCaseProvider.get(), uploadBackupToDriveUseCaseProvider.get(), getMonthlyPaymentsOnceUseCaseProvider.get(), resetMonthAmountsUseCaseProvider.get(), repositoryProvider.get(), reminderSchedulerProvider.get());
  }

  public static HomeViewModel_Factory create(
      Provider<GetMonthlyPaymentsUseCase> getMonthlyPaymentsUseCaseProvider,
      Provider<GetBudgetUseCase> getBudgetUseCaseProvider,
      Provider<MarkAllPaidUseCase> markAllPaidUseCaseProvider,
      Provider<UpdateBudgetUseCase> updateBudgetUseCaseProvider,
      Provider<ExportPaymentsUseCase> exportPaymentsUseCaseProvider,
      Provider<ExportBackupJsonUseCase> exportBackupJsonUseCaseProvider,
      Provider<UploadBackupToDriveUseCase> uploadBackupToDriveUseCaseProvider,
      Provider<GetMonthlyPaymentsOnceUseCase> getMonthlyPaymentsOnceUseCaseProvider,
      Provider<ResetMonthAmountsUseCase> resetMonthAmountsUseCaseProvider,
      Provider<PaymentRepository> repositoryProvider,
      Provider<ReminderScheduler> reminderSchedulerProvider) {
    return new HomeViewModel_Factory(getMonthlyPaymentsUseCaseProvider, getBudgetUseCaseProvider, markAllPaidUseCaseProvider, updateBudgetUseCaseProvider, exportPaymentsUseCaseProvider, exportBackupJsonUseCaseProvider, uploadBackupToDriveUseCaseProvider, getMonthlyPaymentsOnceUseCaseProvider, resetMonthAmountsUseCaseProvider, repositoryProvider, reminderSchedulerProvider);
  }

  public static HomeViewModel newInstance(GetMonthlyPaymentsUseCase getMonthlyPaymentsUseCase,
      GetBudgetUseCase getBudgetUseCase, MarkAllPaidUseCase markAllPaidUseCase,
      UpdateBudgetUseCase updateBudgetUseCase, ExportPaymentsUseCase exportPaymentsUseCase,
      ExportBackupJsonUseCase exportBackupJsonUseCase,
      UploadBackupToDriveUseCase uploadBackupToDriveUseCase,
      GetMonthlyPaymentsOnceUseCase getMonthlyPaymentsOnceUseCase,
      ResetMonthAmountsUseCase resetMonthAmountsUseCase, PaymentRepository repository,
      ReminderScheduler reminderScheduler) {
    return new HomeViewModel(getMonthlyPaymentsUseCase, getBudgetUseCase, markAllPaidUseCase, updateBudgetUseCase, exportPaymentsUseCase, exportBackupJsonUseCase, uploadBackupToDriveUseCase, getMonthlyPaymentsOnceUseCase, resetMonthAmountsUseCase, repository, reminderScheduler);
  }
}
