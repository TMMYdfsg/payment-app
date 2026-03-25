package com.payment.app;

import com.payment.app.data.repository.PaymentRepository;
import com.payment.app.notifications.ReminderScheduler;
import dagger.MembersInjector;
import dagger.internal.DaggerGenerated;
import dagger.internal.InjectedFieldSignature;
import dagger.internal.QualifierMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

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
public final class PaymentApp_MembersInjector implements MembersInjector<PaymentApp> {
  private final Provider<ReminderScheduler> reminderSchedulerProvider;

  private final Provider<PaymentRepository> repositoryProvider;

  public PaymentApp_MembersInjector(Provider<ReminderScheduler> reminderSchedulerProvider,
      Provider<PaymentRepository> repositoryProvider) {
    this.reminderSchedulerProvider = reminderSchedulerProvider;
    this.repositoryProvider = repositoryProvider;
  }

  public static MembersInjector<PaymentApp> create(
      Provider<ReminderScheduler> reminderSchedulerProvider,
      Provider<PaymentRepository> repositoryProvider) {
    return new PaymentApp_MembersInjector(reminderSchedulerProvider, repositoryProvider);
  }

  @Override
  public void injectMembers(PaymentApp instance) {
    injectReminderScheduler(instance, reminderSchedulerProvider.get());
    injectRepository(instance, repositoryProvider.get());
  }

  @InjectedFieldSignature("com.payment.app.PaymentApp.reminderScheduler")
  public static void injectReminderScheduler(PaymentApp instance,
      ReminderScheduler reminderScheduler) {
    instance.reminderScheduler = reminderScheduler;
  }

  @InjectedFieldSignature("com.payment.app.PaymentApp.repository")
  public static void injectRepository(PaymentApp instance, PaymentRepository repository) {
    instance.repository = repository;
  }
}
