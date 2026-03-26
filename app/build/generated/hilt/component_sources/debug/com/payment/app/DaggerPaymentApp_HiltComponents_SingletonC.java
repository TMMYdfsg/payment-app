package com.payment.app;

import android.app.Activity;
import android.app.Service;
import android.view.View;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;
import com.payment.app.data.datastore.SettingsDataStore;
import com.payment.app.data.db.AccountDao;
import com.payment.app.data.db.AppDatabase;
import com.payment.app.data.db.BudgetDao;
import com.payment.app.data.db.CardDao;
import com.payment.app.data.db.InstallmentDao;
import com.payment.app.data.db.NotificationSettingDao;
import com.payment.app.data.db.PaymentDao;
import com.payment.app.data.db.SubscriptionDao;
import com.payment.app.data.repository.PaymentRepository;
import com.payment.app.di.AppModule_ProvideAccountDaoFactory;
import com.payment.app.di.AppModule_ProvideBudgetDaoFactory;
import com.payment.app.di.AppModule_ProvideCardDaoFactory;
import com.payment.app.di.AppModule_ProvideDatabaseFactory;
import com.payment.app.di.AppModule_ProvideInstallmentDaoFactory;
import com.payment.app.di.AppModule_ProvideNotificationSettingDaoFactory;
import com.payment.app.di.AppModule_ProvidePaymentDaoFactory;
import com.payment.app.di.AppModule_ProvideSettingsDataStoreFactory;
import com.payment.app.di.AppModule_ProvideSubscriptionDaoFactory;
import com.payment.app.domain.usecase.ApplyPreviousMonthTemplateUseCase;
import com.payment.app.domain.usecase.ClearPinHashUseCase;
import com.payment.app.domain.usecase.CreateDriveFolderUseCase;
import com.payment.app.domain.usecase.DeletePaymentUseCase;
import com.payment.app.domain.usecase.DownloadSharedBackupFromDriveUseCase;
import com.payment.app.domain.usecase.ExportBackupJsonUseCase;
import com.payment.app.domain.usecase.ExportPaymentsUseCase;
import com.payment.app.domain.usecase.GetBudgetUseCase;
import com.payment.app.domain.usecase.GetCloudSyncPrefsUseCase;
import com.payment.app.domain.usecase.GetDriveAccessTokenUseCase;
import com.payment.app.domain.usecase.GetDriveFolderIdUseCase;
import com.payment.app.domain.usecase.GetDriveGroupNameUseCase;
import com.payment.app.domain.usecase.GetInstallmentsUseCase;
import com.payment.app.domain.usecase.GetLockEnabledUseCase;
import com.payment.app.domain.usecase.GetMonthlyPaymentsOnceUseCase;
import com.payment.app.domain.usecase.GetMonthlyPaymentsUseCase;
import com.payment.app.domain.usecase.GetNotificationSettingsUseCase;
import com.payment.app.domain.usecase.GetOcrProfilesJsonUseCase;
import com.payment.app.domain.usecase.GetPinHashUseCase;
import com.payment.app.domain.usecase.GetSubscriptionsUseCase;
import com.payment.app.domain.usecase.GetSyncAccountEmailUseCase;
import com.payment.app.domain.usecase.GetThemeAccentUseCase;
import com.payment.app.domain.usecase.GetThemeModeUseCase;
import com.payment.app.domain.usecase.GetUnlockGraceEnabledUseCase;
import com.payment.app.domain.usecase.ImportBackupJsonUseCase;
import com.payment.app.domain.usecase.InviteDriveMemberUseCase;
import com.payment.app.domain.usecase.ListDriveMembersUseCase;
import com.payment.app.domain.usecase.MarkAllPaidUseCase;
import com.payment.app.domain.usecase.RecognizeAmountFromImageUseCase;
import com.payment.app.domain.usecase.RemoveDriveMemberUseCase;
import com.payment.app.domain.usecase.ResetMonthAmountsUseCase;
import com.payment.app.domain.usecase.SetCloudLastSyncUseCase;
import com.payment.app.domain.usecase.SetCloudSyncEnabledUseCase;
import com.payment.app.domain.usecase.SetDriveAccessTokenUseCase;
import com.payment.app.domain.usecase.SetDriveFolderIdUseCase;
import com.payment.app.domain.usecase.SetDriveGroupNameUseCase;
import com.payment.app.domain.usecase.SetLockEnabledUseCase;
import com.payment.app.domain.usecase.SetOcrProfilesJsonUseCase;
import com.payment.app.domain.usecase.SetPinHashUseCase;
import com.payment.app.domain.usecase.SetSyncAccountEmailUseCase;
import com.payment.app.domain.usecase.SetThemeAccentUseCase;
import com.payment.app.domain.usecase.SetThemeModeUseCase;
import com.payment.app.domain.usecase.SetUnlockGraceEnabledUseCase;
import com.payment.app.domain.usecase.SetUnlockGraceUntilUseCase;
import com.payment.app.domain.usecase.UpdateBudgetUseCase;
import com.payment.app.domain.usecase.UpdateCardCategoryUseCase;
import com.payment.app.domain.usecase.UpdateDriveMemberRoleUseCase;
import com.payment.app.domain.usecase.UpdatePaymentAccountUseCase;
import com.payment.app.domain.usecase.UpdatePaymentAmountUseCase;
import com.payment.app.domain.usecase.UpdatePaymentPaidUseCase;
import com.payment.app.domain.usecase.UploadBackupToDriveUseCase;
import com.payment.app.domain.usecase.UpsertInstallmentUseCase;
import com.payment.app.domain.usecase.UpsertNotificationSettingUseCase;
import com.payment.app.domain.usecase.UpsertSharedBackupToDriveUseCase;
import com.payment.app.domain.usecase.UpsertSubscriptionUseCase;
import com.payment.app.notifications.ReminderScheduler;
import com.payment.app.ui.account.AccountManageViewModel;
import com.payment.app.ui.account.AccountManageViewModel_HiltModules;
import com.payment.app.ui.analytics.AnalyticsViewModel;
import com.payment.app.ui.analytics.AnalyticsViewModel_HiltModules;
import com.payment.app.ui.analytics.YearlySummaryViewModel;
import com.payment.app.ui.analytics.YearlySummaryViewModel_HiltModules;
import com.payment.app.ui.calendar.CalendarViewModel;
import com.payment.app.ui.calendar.CalendarViewModel_HiltModules;
import com.payment.app.ui.card.CardManageViewModel;
import com.payment.app.ui.card.CardManageViewModel_HiltModules;
import com.payment.app.ui.home.HomeViewModel;
import com.payment.app.ui.home.HomeViewModel_HiltModules;
import com.payment.app.ui.input.InputFlowViewModel;
import com.payment.app.ui.input.InputFlowViewModel_HiltModules;
import com.payment.app.ui.list.ListViewModel;
import com.payment.app.ui.list.ListViewModel_HiltModules;
import com.payment.app.ui.notification.NotificationSettingsViewModel;
import com.payment.app.ui.notification.NotificationSettingsViewModel_HiltModules;
import com.payment.app.ui.subscription.SubscriptionViewModel;
import com.payment.app.ui.subscription.SubscriptionViewModel_HiltModules;
import com.payment.app.widget.WidgetUpdater;
import dagger.hilt.android.ActivityRetainedLifecycle;
import dagger.hilt.android.ViewModelLifecycle;
import dagger.hilt.android.internal.builders.ActivityComponentBuilder;
import dagger.hilt.android.internal.builders.ActivityRetainedComponentBuilder;
import dagger.hilt.android.internal.builders.FragmentComponentBuilder;
import dagger.hilt.android.internal.builders.ServiceComponentBuilder;
import dagger.hilt.android.internal.builders.ViewComponentBuilder;
import dagger.hilt.android.internal.builders.ViewModelComponentBuilder;
import dagger.hilt.android.internal.builders.ViewWithFragmentComponentBuilder;
import dagger.hilt.android.internal.lifecycle.DefaultViewModelFactories;
import dagger.hilt.android.internal.lifecycle.DefaultViewModelFactories_InternalFactoryFactory_Factory;
import dagger.hilt.android.internal.managers.ActivityRetainedComponentManager_LifecycleModule_ProvideActivityRetainedLifecycleFactory;
import dagger.hilt.android.internal.managers.SavedStateHandleHolder;
import dagger.hilt.android.internal.modules.ApplicationContextModule;
import dagger.hilt.android.internal.modules.ApplicationContextModule_ProvideContextFactory;
import dagger.internal.DaggerGenerated;
import dagger.internal.DoubleCheck;
import dagger.internal.IdentifierNameString;
import dagger.internal.KeepFieldType;
import dagger.internal.LazyClassKeyMap;
import dagger.internal.MapBuilder;
import dagger.internal.Preconditions;
import dagger.internal.Provider;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

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
public final class DaggerPaymentApp_HiltComponents_SingletonC {
  private DaggerPaymentApp_HiltComponents_SingletonC() {
  }

  public static Builder builder() {
    return new Builder();
  }

  public static final class Builder {
    private ApplicationContextModule applicationContextModule;

    private Builder() {
    }

    public Builder applicationContextModule(ApplicationContextModule applicationContextModule) {
      this.applicationContextModule = Preconditions.checkNotNull(applicationContextModule);
      return this;
    }

    public PaymentApp_HiltComponents.SingletonC build() {
      Preconditions.checkBuilderRequirement(applicationContextModule, ApplicationContextModule.class);
      return new SingletonCImpl(applicationContextModule);
    }
  }

  private static final class ActivityRetainedCBuilder implements PaymentApp_HiltComponents.ActivityRetainedC.Builder {
    private final SingletonCImpl singletonCImpl;

    private SavedStateHandleHolder savedStateHandleHolder;

    private ActivityRetainedCBuilder(SingletonCImpl singletonCImpl) {
      this.singletonCImpl = singletonCImpl;
    }

    @Override
    public ActivityRetainedCBuilder savedStateHandleHolder(
        SavedStateHandleHolder savedStateHandleHolder) {
      this.savedStateHandleHolder = Preconditions.checkNotNull(savedStateHandleHolder);
      return this;
    }

    @Override
    public PaymentApp_HiltComponents.ActivityRetainedC build() {
      Preconditions.checkBuilderRequirement(savedStateHandleHolder, SavedStateHandleHolder.class);
      return new ActivityRetainedCImpl(singletonCImpl, savedStateHandleHolder);
    }
  }

  private static final class ActivityCBuilder implements PaymentApp_HiltComponents.ActivityC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private Activity activity;

    private ActivityCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
    }

    @Override
    public ActivityCBuilder activity(Activity activity) {
      this.activity = Preconditions.checkNotNull(activity);
      return this;
    }

    @Override
    public PaymentApp_HiltComponents.ActivityC build() {
      Preconditions.checkBuilderRequirement(activity, Activity.class);
      return new ActivityCImpl(singletonCImpl, activityRetainedCImpl, activity);
    }
  }

  private static final class FragmentCBuilder implements PaymentApp_HiltComponents.FragmentC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private Fragment fragment;

    private FragmentCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
    }

    @Override
    public FragmentCBuilder fragment(Fragment fragment) {
      this.fragment = Preconditions.checkNotNull(fragment);
      return this;
    }

    @Override
    public PaymentApp_HiltComponents.FragmentC build() {
      Preconditions.checkBuilderRequirement(fragment, Fragment.class);
      return new FragmentCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, fragment);
    }
  }

  private static final class ViewWithFragmentCBuilder implements PaymentApp_HiltComponents.ViewWithFragmentC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl;

    private View view;

    private ViewWithFragmentCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        FragmentCImpl fragmentCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
      this.fragmentCImpl = fragmentCImpl;
    }

    @Override
    public ViewWithFragmentCBuilder view(View view) {
      this.view = Preconditions.checkNotNull(view);
      return this;
    }

    @Override
    public PaymentApp_HiltComponents.ViewWithFragmentC build() {
      Preconditions.checkBuilderRequirement(view, View.class);
      return new ViewWithFragmentCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, fragmentCImpl, view);
    }
  }

  private static final class ViewCBuilder implements PaymentApp_HiltComponents.ViewC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private View view;

    private ViewCBuilder(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
        ActivityCImpl activityCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
    }

    @Override
    public ViewCBuilder view(View view) {
      this.view = Preconditions.checkNotNull(view);
      return this;
    }

    @Override
    public PaymentApp_HiltComponents.ViewC build() {
      Preconditions.checkBuilderRequirement(view, View.class);
      return new ViewCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, view);
    }
  }

  private static final class ViewModelCBuilder implements PaymentApp_HiltComponents.ViewModelC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private SavedStateHandle savedStateHandle;

    private ViewModelLifecycle viewModelLifecycle;

    private ViewModelCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
    }

    @Override
    public ViewModelCBuilder savedStateHandle(SavedStateHandle handle) {
      this.savedStateHandle = Preconditions.checkNotNull(handle);
      return this;
    }

    @Override
    public ViewModelCBuilder viewModelLifecycle(ViewModelLifecycle viewModelLifecycle) {
      this.viewModelLifecycle = Preconditions.checkNotNull(viewModelLifecycle);
      return this;
    }

    @Override
    public PaymentApp_HiltComponents.ViewModelC build() {
      Preconditions.checkBuilderRequirement(savedStateHandle, SavedStateHandle.class);
      Preconditions.checkBuilderRequirement(viewModelLifecycle, ViewModelLifecycle.class);
      return new ViewModelCImpl(singletonCImpl, activityRetainedCImpl, savedStateHandle, viewModelLifecycle);
    }
  }

  private static final class ServiceCBuilder implements PaymentApp_HiltComponents.ServiceC.Builder {
    private final SingletonCImpl singletonCImpl;

    private Service service;

    private ServiceCBuilder(SingletonCImpl singletonCImpl) {
      this.singletonCImpl = singletonCImpl;
    }

    @Override
    public ServiceCBuilder service(Service service) {
      this.service = Preconditions.checkNotNull(service);
      return this;
    }

    @Override
    public PaymentApp_HiltComponents.ServiceC build() {
      Preconditions.checkBuilderRequirement(service, Service.class);
      return new ServiceCImpl(singletonCImpl, service);
    }
  }

  private static final class ViewWithFragmentCImpl extends PaymentApp_HiltComponents.ViewWithFragmentC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl;

    private final ViewWithFragmentCImpl viewWithFragmentCImpl = this;

    private ViewWithFragmentCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        FragmentCImpl fragmentCImpl, View viewParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
      this.fragmentCImpl = fragmentCImpl;


    }
  }

  private static final class FragmentCImpl extends PaymentApp_HiltComponents.FragmentC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl = this;

    private FragmentCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        Fragment fragmentParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;


    }

    @Override
    public DefaultViewModelFactories.InternalFactoryFactory getHiltInternalFactoryFactory() {
      return activityCImpl.getHiltInternalFactoryFactory();
    }

    @Override
    public ViewWithFragmentComponentBuilder viewWithFragmentComponentBuilder() {
      return new ViewWithFragmentCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl, fragmentCImpl);
    }
  }

  private static final class ViewCImpl extends PaymentApp_HiltComponents.ViewC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final ViewCImpl viewCImpl = this;

    private ViewCImpl(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
        ActivityCImpl activityCImpl, View viewParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;


    }
  }

  private static final class ActivityCImpl extends PaymentApp_HiltComponents.ActivityC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl = this;

    private ActivityCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, Activity activityParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;


    }

    private SetUnlockGraceUntilUseCase setUnlockGraceUntilUseCase() {
      return new SetUnlockGraceUntilUseCase(singletonCImpl.provideSettingsDataStoreProvider.get());
    }

    @Override
    public void injectMainActivity(MainActivity mainActivity) {
      injectMainActivity2(mainActivity);
    }

    @Override
    public DefaultViewModelFactories.InternalFactoryFactory getHiltInternalFactoryFactory() {
      return DefaultViewModelFactories_InternalFactoryFactory_Factory.newInstance(getViewModelKeys(), new ViewModelCBuilder(singletonCImpl, activityRetainedCImpl));
    }

    @Override
    public Map<Class<?>, Boolean> getViewModelKeys() {
      return LazyClassKeyMap.<Boolean>of(MapBuilder.<String, Boolean>newMapBuilder(10).put(LazyClassKeyProvider.com_payment_app_ui_account_AccountManageViewModel, AccountManageViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_payment_app_ui_analytics_AnalyticsViewModel, AnalyticsViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_payment_app_ui_calendar_CalendarViewModel, CalendarViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_payment_app_ui_card_CardManageViewModel, CardManageViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_payment_app_ui_home_HomeViewModel, HomeViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_payment_app_ui_input_InputFlowViewModel, InputFlowViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_payment_app_ui_list_ListViewModel, ListViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_payment_app_ui_notification_NotificationSettingsViewModel, NotificationSettingsViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_payment_app_ui_subscription_SubscriptionViewModel, SubscriptionViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_payment_app_ui_analytics_YearlySummaryViewModel, YearlySummaryViewModel_HiltModules.KeyModule.provide()).build());
    }

    @Override
    public ViewModelComponentBuilder getViewModelComponentBuilder() {
      return new ViewModelCBuilder(singletonCImpl, activityRetainedCImpl);
    }

    @Override
    public FragmentComponentBuilder fragmentComponentBuilder() {
      return new FragmentCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl);
    }

    @Override
    public ViewComponentBuilder viewComponentBuilder() {
      return new ViewCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl);
    }

    private MainActivity injectMainActivity2(MainActivity instance) {
      MainActivity_MembersInjector.injectSettingsDataStore(instance, singletonCImpl.provideSettingsDataStoreProvider.get());
      MainActivity_MembersInjector.injectSetUnlockGraceUntil(instance, setUnlockGraceUntilUseCase());
      return instance;
    }

    @IdentifierNameString
    private static final class LazyClassKeyProvider {
      static String com_payment_app_ui_calendar_CalendarViewModel = "com.payment.app.ui.calendar.CalendarViewModel";

      static String com_payment_app_ui_home_HomeViewModel = "com.payment.app.ui.home.HomeViewModel";

      static String com_payment_app_ui_account_AccountManageViewModel = "com.payment.app.ui.account.AccountManageViewModel";

      static String com_payment_app_ui_analytics_AnalyticsViewModel = "com.payment.app.ui.analytics.AnalyticsViewModel";

      static String com_payment_app_ui_list_ListViewModel = "com.payment.app.ui.list.ListViewModel";

      static String com_payment_app_ui_notification_NotificationSettingsViewModel = "com.payment.app.ui.notification.NotificationSettingsViewModel";

      static String com_payment_app_ui_input_InputFlowViewModel = "com.payment.app.ui.input.InputFlowViewModel";

      static String com_payment_app_ui_card_CardManageViewModel = "com.payment.app.ui.card.CardManageViewModel";

      static String com_payment_app_ui_analytics_YearlySummaryViewModel = "com.payment.app.ui.analytics.YearlySummaryViewModel";

      static String com_payment_app_ui_subscription_SubscriptionViewModel = "com.payment.app.ui.subscription.SubscriptionViewModel";

      @KeepFieldType
      CalendarViewModel com_payment_app_ui_calendar_CalendarViewModel2;

      @KeepFieldType
      HomeViewModel com_payment_app_ui_home_HomeViewModel2;

      @KeepFieldType
      AccountManageViewModel com_payment_app_ui_account_AccountManageViewModel2;

      @KeepFieldType
      AnalyticsViewModel com_payment_app_ui_analytics_AnalyticsViewModel2;

      @KeepFieldType
      ListViewModel com_payment_app_ui_list_ListViewModel2;

      @KeepFieldType
      NotificationSettingsViewModel com_payment_app_ui_notification_NotificationSettingsViewModel2;

      @KeepFieldType
      InputFlowViewModel com_payment_app_ui_input_InputFlowViewModel2;

      @KeepFieldType
      CardManageViewModel com_payment_app_ui_card_CardManageViewModel2;

      @KeepFieldType
      YearlySummaryViewModel com_payment_app_ui_analytics_YearlySummaryViewModel2;

      @KeepFieldType
      SubscriptionViewModel com_payment_app_ui_subscription_SubscriptionViewModel2;
    }
  }

  private static final class ViewModelCImpl extends PaymentApp_HiltComponents.ViewModelC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ViewModelCImpl viewModelCImpl = this;

    private Provider<AccountManageViewModel> accountManageViewModelProvider;

    private Provider<AnalyticsViewModel> analyticsViewModelProvider;

    private Provider<CalendarViewModel> calendarViewModelProvider;

    private Provider<CardManageViewModel> cardManageViewModelProvider;

    private Provider<HomeViewModel> homeViewModelProvider;

    private Provider<InputFlowViewModel> inputFlowViewModelProvider;

    private Provider<ListViewModel> listViewModelProvider;

    private Provider<NotificationSettingsViewModel> notificationSettingsViewModelProvider;

    private Provider<SubscriptionViewModel> subscriptionViewModelProvider;

    private Provider<YearlySummaryViewModel> yearlySummaryViewModelProvider;

    private ViewModelCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, SavedStateHandle savedStateHandleParam,
        ViewModelLifecycle viewModelLifecycleParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;

      initialize(savedStateHandleParam, viewModelLifecycleParam);

    }

    private GetMonthlyPaymentsOnceUseCase getMonthlyPaymentsOnceUseCase() {
      return new GetMonthlyPaymentsOnceUseCase(singletonCImpl.paymentRepositoryProvider.get());
    }

    private GetBudgetUseCase getBudgetUseCase() {
      return new GetBudgetUseCase(singletonCImpl.paymentRepositoryProvider.get());
    }

    private GetMonthlyPaymentsUseCase getMonthlyPaymentsUseCase() {
      return new GetMonthlyPaymentsUseCase(singletonCImpl.paymentRepositoryProvider.get());
    }

    private MarkAllPaidUseCase markAllPaidUseCase() {
      return new MarkAllPaidUseCase(singletonCImpl.paymentRepositoryProvider.get(), singletonCImpl.widgetUpdaterProvider.get());
    }

    private ApplyPreviousMonthTemplateUseCase applyPreviousMonthTemplateUseCase() {
      return new ApplyPreviousMonthTemplateUseCase(singletonCImpl.paymentRepositoryProvider.get(), singletonCImpl.widgetUpdaterProvider.get());
    }

    private UpdateBudgetUseCase updateBudgetUseCase() {
      return new UpdateBudgetUseCase(singletonCImpl.paymentRepositoryProvider.get());
    }

    private ExportPaymentsUseCase exportPaymentsUseCase() {
      return new ExportPaymentsUseCase(singletonCImpl.paymentRepositoryProvider.get(), ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));
    }

    private ExportBackupJsonUseCase exportBackupJsonUseCase() {
      return new ExportBackupJsonUseCase(singletonCImpl.paymentRepositoryProvider.get(), ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));
    }

    private ResetMonthAmountsUseCase resetMonthAmountsUseCase() {
      return new ResetMonthAmountsUseCase(singletonCImpl.paymentRepositoryProvider.get(), singletonCImpl.widgetUpdaterProvider.get());
    }

    private RecognizeAmountFromImageUseCase recognizeAmountFromImageUseCase() {
      return new RecognizeAmountFromImageUseCase(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));
    }

    private GetOcrProfilesJsonUseCase getOcrProfilesJsonUseCase() {
      return new GetOcrProfilesJsonUseCase(singletonCImpl.provideSettingsDataStoreProvider.get());
    }

    private SetOcrProfilesJsonUseCase setOcrProfilesJsonUseCase() {
      return new SetOcrProfilesJsonUseCase(singletonCImpl.provideSettingsDataStoreProvider.get());
    }

    private UpdateCardCategoryUseCase updateCardCategoryUseCase() {
      return new UpdateCardCategoryUseCase(singletonCImpl.paymentRepositoryProvider.get());
    }

    private UpdatePaymentAmountUseCase updatePaymentAmountUseCase() {
      return new UpdatePaymentAmountUseCase(singletonCImpl.paymentRepositoryProvider.get(), singletonCImpl.widgetUpdaterProvider.get());
    }

    private UpdatePaymentAccountUseCase updatePaymentAccountUseCase() {
      return new UpdatePaymentAccountUseCase(singletonCImpl.paymentRepositoryProvider.get());
    }

    private DeletePaymentUseCase deletePaymentUseCase() {
      return new DeletePaymentUseCase(singletonCImpl.paymentRepositoryProvider.get(), singletonCImpl.widgetUpdaterProvider.get());
    }

    private GetNotificationSettingsUseCase getNotificationSettingsUseCase() {
      return new GetNotificationSettingsUseCase(singletonCImpl.paymentRepositoryProvider.get());
    }

    private GetThemeModeUseCase getThemeModeUseCase() {
      return new GetThemeModeUseCase(singletonCImpl.provideSettingsDataStoreProvider.get());
    }

    private GetThemeAccentUseCase getThemeAccentUseCase() {
      return new GetThemeAccentUseCase(singletonCImpl.provideSettingsDataStoreProvider.get());
    }

    private GetLockEnabledUseCase getLockEnabledUseCase() {
      return new GetLockEnabledUseCase(singletonCImpl.provideSettingsDataStoreProvider.get());
    }

    private GetPinHashUseCase getPinHashUseCase() {
      return new GetPinHashUseCase(singletonCImpl.provideSettingsDataStoreProvider.get());
    }

    private GetUnlockGraceEnabledUseCase getUnlockGraceEnabledUseCase() {
      return new GetUnlockGraceEnabledUseCase(singletonCImpl.provideSettingsDataStoreProvider.get());
    }

    private GetDriveAccessTokenUseCase getDriveAccessTokenUseCase() {
      return new GetDriveAccessTokenUseCase(singletonCImpl.provideSettingsDataStoreProvider.get());
    }

    private GetDriveFolderIdUseCase getDriveFolderIdUseCase() {
      return new GetDriveFolderIdUseCase(singletonCImpl.provideSettingsDataStoreProvider.get());
    }

    private GetDriveGroupNameUseCase getDriveGroupNameUseCase() {
      return new GetDriveGroupNameUseCase(singletonCImpl.provideSettingsDataStoreProvider.get());
    }

    private GetSyncAccountEmailUseCase getSyncAccountEmailUseCase() {
      return new GetSyncAccountEmailUseCase(singletonCImpl.provideSettingsDataStoreProvider.get());
    }

    private GetCloudSyncPrefsUseCase getCloudSyncPrefsUseCase() {
      return new GetCloudSyncPrefsUseCase(singletonCImpl.provideSettingsDataStoreProvider.get());
    }

    private SetThemeModeUseCase setThemeModeUseCase() {
      return new SetThemeModeUseCase(singletonCImpl.provideSettingsDataStoreProvider.get());
    }

    private SetThemeAccentUseCase setThemeAccentUseCase() {
      return new SetThemeAccentUseCase(singletonCImpl.provideSettingsDataStoreProvider.get());
    }

    private SetLockEnabledUseCase setLockEnabledUseCase() {
      return new SetLockEnabledUseCase(singletonCImpl.provideSettingsDataStoreProvider.get());
    }

    private SetPinHashUseCase setPinHashUseCase() {
      return new SetPinHashUseCase(singletonCImpl.provideSettingsDataStoreProvider.get());
    }

    private ClearPinHashUseCase clearPinHashUseCase() {
      return new ClearPinHashUseCase(singletonCImpl.provideSettingsDataStoreProvider.get());
    }

    private SetUnlockGraceEnabledUseCase setUnlockGraceEnabledUseCase() {
      return new SetUnlockGraceEnabledUseCase(singletonCImpl.provideSettingsDataStoreProvider.get());
    }

    private SetDriveAccessTokenUseCase setDriveAccessTokenUseCase() {
      return new SetDriveAccessTokenUseCase(singletonCImpl.provideSettingsDataStoreProvider.get());
    }

    private SetDriveFolderIdUseCase setDriveFolderIdUseCase() {
      return new SetDriveFolderIdUseCase(singletonCImpl.provideSettingsDataStoreProvider.get());
    }

    private SetDriveGroupNameUseCase setDriveGroupNameUseCase() {
      return new SetDriveGroupNameUseCase(singletonCImpl.provideSettingsDataStoreProvider.get());
    }

    private SetSyncAccountEmailUseCase setSyncAccountEmailUseCase() {
      return new SetSyncAccountEmailUseCase(singletonCImpl.provideSettingsDataStoreProvider.get());
    }

    private SetCloudSyncEnabledUseCase setCloudSyncEnabledUseCase() {
      return new SetCloudSyncEnabledUseCase(singletonCImpl.provideSettingsDataStoreProvider.get());
    }

    private SetCloudLastSyncUseCase setCloudLastSyncUseCase() {
      return new SetCloudLastSyncUseCase(singletonCImpl.provideSettingsDataStoreProvider.get());
    }

    private UpsertNotificationSettingUseCase upsertNotificationSettingUseCase() {
      return new UpsertNotificationSettingUseCase(singletonCImpl.paymentRepositoryProvider.get());
    }

    private UpsertSharedBackupToDriveUseCase upsertSharedBackupToDriveUseCase() {
      return new UpsertSharedBackupToDriveUseCase(new UploadBackupToDriveUseCase());
    }

    private GetSubscriptionsUseCase getSubscriptionsUseCase() {
      return new GetSubscriptionsUseCase(singletonCImpl.paymentRepositoryProvider.get());
    }

    private GetInstallmentsUseCase getInstallmentsUseCase() {
      return new GetInstallmentsUseCase(singletonCImpl.paymentRepositoryProvider.get());
    }

    private UpsertSubscriptionUseCase upsertSubscriptionUseCase() {
      return new UpsertSubscriptionUseCase(singletonCImpl.paymentRepositoryProvider.get());
    }

    private UpsertInstallmentUseCase upsertInstallmentUseCase() {
      return new UpsertInstallmentUseCase(singletonCImpl.paymentRepositoryProvider.get());
    }

    @SuppressWarnings("unchecked")
    private void initialize(final SavedStateHandle savedStateHandleParam,
        final ViewModelLifecycle viewModelLifecycleParam) {
      this.accountManageViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 0);
      this.analyticsViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 1);
      this.calendarViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 2);
      this.cardManageViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 3);
      this.homeViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 4);
      this.inputFlowViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 5);
      this.listViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 6);
      this.notificationSettingsViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 7);
      this.subscriptionViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 8);
      this.yearlySummaryViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 9);
    }

    @Override
    public Map<Class<?>, javax.inject.Provider<ViewModel>> getHiltViewModelMap() {
      return LazyClassKeyMap.<javax.inject.Provider<ViewModel>>of(MapBuilder.<String, javax.inject.Provider<ViewModel>>newMapBuilder(10).put(LazyClassKeyProvider.com_payment_app_ui_account_AccountManageViewModel, ((Provider) accountManageViewModelProvider)).put(LazyClassKeyProvider.com_payment_app_ui_analytics_AnalyticsViewModel, ((Provider) analyticsViewModelProvider)).put(LazyClassKeyProvider.com_payment_app_ui_calendar_CalendarViewModel, ((Provider) calendarViewModelProvider)).put(LazyClassKeyProvider.com_payment_app_ui_card_CardManageViewModel, ((Provider) cardManageViewModelProvider)).put(LazyClassKeyProvider.com_payment_app_ui_home_HomeViewModel, ((Provider) homeViewModelProvider)).put(LazyClassKeyProvider.com_payment_app_ui_input_InputFlowViewModel, ((Provider) inputFlowViewModelProvider)).put(LazyClassKeyProvider.com_payment_app_ui_list_ListViewModel, ((Provider) listViewModelProvider)).put(LazyClassKeyProvider.com_payment_app_ui_notification_NotificationSettingsViewModel, ((Provider) notificationSettingsViewModelProvider)).put(LazyClassKeyProvider.com_payment_app_ui_subscription_SubscriptionViewModel, ((Provider) subscriptionViewModelProvider)).put(LazyClassKeyProvider.com_payment_app_ui_analytics_YearlySummaryViewModel, ((Provider) yearlySummaryViewModelProvider)).build());
    }

    @Override
    public Map<Class<?>, Object> getHiltViewModelAssistedMap() {
      return Collections.<Class<?>, Object>emptyMap();
    }

    @IdentifierNameString
    private static final class LazyClassKeyProvider {
      static String com_payment_app_ui_card_CardManageViewModel = "com.payment.app.ui.card.CardManageViewModel";

      static String com_payment_app_ui_home_HomeViewModel = "com.payment.app.ui.home.HomeViewModel";

      static String com_payment_app_ui_calendar_CalendarViewModel = "com.payment.app.ui.calendar.CalendarViewModel";

      static String com_payment_app_ui_account_AccountManageViewModel = "com.payment.app.ui.account.AccountManageViewModel";

      static String com_payment_app_ui_analytics_AnalyticsViewModel = "com.payment.app.ui.analytics.AnalyticsViewModel";

      static String com_payment_app_ui_input_InputFlowViewModel = "com.payment.app.ui.input.InputFlowViewModel";

      static String com_payment_app_ui_analytics_YearlySummaryViewModel = "com.payment.app.ui.analytics.YearlySummaryViewModel";

      static String com_payment_app_ui_list_ListViewModel = "com.payment.app.ui.list.ListViewModel";

      static String com_payment_app_ui_notification_NotificationSettingsViewModel = "com.payment.app.ui.notification.NotificationSettingsViewModel";

      static String com_payment_app_ui_subscription_SubscriptionViewModel = "com.payment.app.ui.subscription.SubscriptionViewModel";

      @KeepFieldType
      CardManageViewModel com_payment_app_ui_card_CardManageViewModel2;

      @KeepFieldType
      HomeViewModel com_payment_app_ui_home_HomeViewModel2;

      @KeepFieldType
      CalendarViewModel com_payment_app_ui_calendar_CalendarViewModel2;

      @KeepFieldType
      AccountManageViewModel com_payment_app_ui_account_AccountManageViewModel2;

      @KeepFieldType
      AnalyticsViewModel com_payment_app_ui_analytics_AnalyticsViewModel2;

      @KeepFieldType
      InputFlowViewModel com_payment_app_ui_input_InputFlowViewModel2;

      @KeepFieldType
      YearlySummaryViewModel com_payment_app_ui_analytics_YearlySummaryViewModel2;

      @KeepFieldType
      ListViewModel com_payment_app_ui_list_ListViewModel2;

      @KeepFieldType
      NotificationSettingsViewModel com_payment_app_ui_notification_NotificationSettingsViewModel2;

      @KeepFieldType
      SubscriptionViewModel com_payment_app_ui_subscription_SubscriptionViewModel2;
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final ActivityRetainedCImpl activityRetainedCImpl;

      private final ViewModelCImpl viewModelCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
          ViewModelCImpl viewModelCImpl, int id) {
        this.singletonCImpl = singletonCImpl;
        this.activityRetainedCImpl = activityRetainedCImpl;
        this.viewModelCImpl = viewModelCImpl;
        this.id = id;
      }

      @SuppressWarnings("unchecked")
      @Override
      public T get() {
        switch (id) {
          case 0: // com.payment.app.ui.account.AccountManageViewModel 
          return (T) new AccountManageViewModel(singletonCImpl.paymentRepositoryProvider.get());

          case 1: // com.payment.app.ui.analytics.AnalyticsViewModel 
          return (T) new AnalyticsViewModel(viewModelCImpl.getMonthlyPaymentsOnceUseCase(), viewModelCImpl.getBudgetUseCase());

          case 2: // com.payment.app.ui.calendar.CalendarViewModel 
          return (T) new CalendarViewModel(viewModelCImpl.getMonthlyPaymentsUseCase());

          case 3: // com.payment.app.ui.card.CardManageViewModel 
          return (T) new CardManageViewModel(singletonCImpl.paymentRepositoryProvider.get());

          case 4: // com.payment.app.ui.home.HomeViewModel 
          return (T) new HomeViewModel(viewModelCImpl.getMonthlyPaymentsUseCase(), viewModelCImpl.getBudgetUseCase(), viewModelCImpl.markAllPaidUseCase(), viewModelCImpl.applyPreviousMonthTemplateUseCase(), viewModelCImpl.updateBudgetUseCase(), viewModelCImpl.exportPaymentsUseCase(), viewModelCImpl.exportBackupJsonUseCase(), new UploadBackupToDriveUseCase(), viewModelCImpl.getMonthlyPaymentsOnceUseCase(), viewModelCImpl.resetMonthAmountsUseCase(), singletonCImpl.paymentRepositoryProvider.get(), singletonCImpl.reminderSchedulerProvider.get());

          case 5: // com.payment.app.ui.input.InputFlowViewModel 
          return (T) new InputFlowViewModel(singletonCImpl.paymentRepositoryProvider.get(), viewModelCImpl.getMonthlyPaymentsOnceUseCase(), viewModelCImpl.recognizeAmountFromImageUseCase(), viewModelCImpl.getOcrProfilesJsonUseCase(), viewModelCImpl.setOcrProfilesJsonUseCase(), viewModelCImpl.updateCardCategoryUseCase(), viewModelCImpl.updatePaymentAmountUseCase(), singletonCImpl.updatePaymentPaidUseCase(), viewModelCImpl.updatePaymentAccountUseCase());

          case 6: // com.payment.app.ui.list.ListViewModel 
          return (T) new ListViewModel(viewModelCImpl.getMonthlyPaymentsUseCase(), viewModelCImpl.deletePaymentUseCase(), viewModelCImpl.updatePaymentAmountUseCase(), singletonCImpl.updatePaymentPaidUseCase(), viewModelCImpl.updatePaymentAccountUseCase(), singletonCImpl.paymentRepositoryProvider.get());

          case 7: // com.payment.app.ui.notification.NotificationSettingsViewModel 
          return (T) new NotificationSettingsViewModel(viewModelCImpl.getNotificationSettingsUseCase(), viewModelCImpl.getThemeModeUseCase(), viewModelCImpl.getThemeAccentUseCase(), viewModelCImpl.getLockEnabledUseCase(), viewModelCImpl.getPinHashUseCase(), viewModelCImpl.getUnlockGraceEnabledUseCase(), viewModelCImpl.getDriveAccessTokenUseCase(), viewModelCImpl.getDriveFolderIdUseCase(), viewModelCImpl.getDriveGroupNameUseCase(), viewModelCImpl.getSyncAccountEmailUseCase(), viewModelCImpl.getCloudSyncPrefsUseCase(), viewModelCImpl.setThemeModeUseCase(), viewModelCImpl.setThemeAccentUseCase(), viewModelCImpl.setLockEnabledUseCase(), viewModelCImpl.setPinHashUseCase(), viewModelCImpl.clearPinHashUseCase(), viewModelCImpl.setUnlockGraceEnabledUseCase(), viewModelCImpl.setDriveAccessTokenUseCase(), viewModelCImpl.setDriveFolderIdUseCase(), viewModelCImpl.setDriveGroupNameUseCase(), viewModelCImpl.setSyncAccountEmailUseCase(), viewModelCImpl.setCloudSyncEnabledUseCase(), viewModelCImpl.setCloudLastSyncUseCase(), viewModelCImpl.upsertNotificationSettingUseCase(), viewModelCImpl.exportBackupJsonUseCase(), singletonCImpl.importBackupJsonUseCase(), new CreateDriveFolderUseCase(), new InviteDriveMemberUseCase(), new ListDriveMembersUseCase(), new UpdateDriveMemberRoleUseCase(), new RemoveDriveMemberUseCase(), viewModelCImpl.upsertSharedBackupToDriveUseCase(), new DownloadSharedBackupFromDriveUseCase(), singletonCImpl.reminderSchedulerProvider.get());

          case 8: // com.payment.app.ui.subscription.SubscriptionViewModel 
          return (T) new SubscriptionViewModel(singletonCImpl.paymentRepositoryProvider.get(), viewModelCImpl.getSubscriptionsUseCase(), viewModelCImpl.getInstallmentsUseCase(), viewModelCImpl.upsertSubscriptionUseCase(), viewModelCImpl.upsertInstallmentUseCase());

          case 9: // com.payment.app.ui.analytics.YearlySummaryViewModel 
          return (T) new YearlySummaryViewModel(viewModelCImpl.getMonthlyPaymentsOnceUseCase());

          default: throw new AssertionError(id);
        }
      }
    }
  }

  private static final class ActivityRetainedCImpl extends PaymentApp_HiltComponents.ActivityRetainedC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl = this;

    private Provider<ActivityRetainedLifecycle> provideActivityRetainedLifecycleProvider;

    private ActivityRetainedCImpl(SingletonCImpl singletonCImpl,
        SavedStateHandleHolder savedStateHandleHolderParam) {
      this.singletonCImpl = singletonCImpl;

      initialize(savedStateHandleHolderParam);

    }

    @SuppressWarnings("unchecked")
    private void initialize(final SavedStateHandleHolder savedStateHandleHolderParam) {
      this.provideActivityRetainedLifecycleProvider = DoubleCheck.provider(new SwitchingProvider<ActivityRetainedLifecycle>(singletonCImpl, activityRetainedCImpl, 0));
    }

    @Override
    public ActivityComponentBuilder activityComponentBuilder() {
      return new ActivityCBuilder(singletonCImpl, activityRetainedCImpl);
    }

    @Override
    public ActivityRetainedLifecycle getActivityRetainedLifecycle() {
      return provideActivityRetainedLifecycleProvider.get();
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final ActivityRetainedCImpl activityRetainedCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
          int id) {
        this.singletonCImpl = singletonCImpl;
        this.activityRetainedCImpl = activityRetainedCImpl;
        this.id = id;
      }

      @SuppressWarnings("unchecked")
      @Override
      public T get() {
        switch (id) {
          case 0: // dagger.hilt.android.ActivityRetainedLifecycle 
          return (T) ActivityRetainedComponentManager_LifecycleModule_ProvideActivityRetainedLifecycleFactory.provideActivityRetainedLifecycle();

          default: throw new AssertionError(id);
        }
      }
    }
  }

  private static final class ServiceCImpl extends PaymentApp_HiltComponents.ServiceC {
    private final SingletonCImpl singletonCImpl;

    private final ServiceCImpl serviceCImpl = this;

    private ServiceCImpl(SingletonCImpl singletonCImpl, Service serviceParam) {
      this.singletonCImpl = singletonCImpl;


    }
  }

  private static final class SingletonCImpl extends PaymentApp_HiltComponents.SingletonC {
    private final ApplicationContextModule applicationContextModule;

    private final SingletonCImpl singletonCImpl = this;

    private Provider<ReminderScheduler> reminderSchedulerProvider;

    private Provider<AppDatabase> provideDatabaseProvider;

    private Provider<CardDao> provideCardDaoProvider;

    private Provider<PaymentDao> providePaymentDaoProvider;

    private Provider<AccountDao> provideAccountDaoProvider;

    private Provider<BudgetDao> provideBudgetDaoProvider;

    private Provider<SubscriptionDao> provideSubscriptionDaoProvider;

    private Provider<InstallmentDao> provideInstallmentDaoProvider;

    private Provider<NotificationSettingDao> provideNotificationSettingDaoProvider;

    private Provider<PaymentRepository> paymentRepositoryProvider;

    private Provider<WidgetUpdater> widgetUpdaterProvider;

    private Provider<SettingsDataStore> provideSettingsDataStoreProvider;

    private SingletonCImpl(ApplicationContextModule applicationContextModuleParam) {
      this.applicationContextModule = applicationContextModuleParam;
      initialize(applicationContextModuleParam);

    }

    private ImportBackupJsonUseCase importBackupJsonUseCase() {
      return new ImportBackupJsonUseCase(paymentRepositoryProvider.get());
    }

    @SuppressWarnings("unchecked")
    private void initialize(final ApplicationContextModule applicationContextModuleParam) {
      this.reminderSchedulerProvider = DoubleCheck.provider(new SwitchingProvider<ReminderScheduler>(singletonCImpl, 0));
      this.provideDatabaseProvider = DoubleCheck.provider(new SwitchingProvider<AppDatabase>(singletonCImpl, 2));
      this.provideCardDaoProvider = DoubleCheck.provider(new SwitchingProvider<CardDao>(singletonCImpl, 3));
      this.providePaymentDaoProvider = DoubleCheck.provider(new SwitchingProvider<PaymentDao>(singletonCImpl, 4));
      this.provideAccountDaoProvider = DoubleCheck.provider(new SwitchingProvider<AccountDao>(singletonCImpl, 5));
      this.provideBudgetDaoProvider = DoubleCheck.provider(new SwitchingProvider<BudgetDao>(singletonCImpl, 6));
      this.provideSubscriptionDaoProvider = DoubleCheck.provider(new SwitchingProvider<SubscriptionDao>(singletonCImpl, 7));
      this.provideInstallmentDaoProvider = DoubleCheck.provider(new SwitchingProvider<InstallmentDao>(singletonCImpl, 8));
      this.provideNotificationSettingDaoProvider = DoubleCheck.provider(new SwitchingProvider<NotificationSettingDao>(singletonCImpl, 9));
      this.paymentRepositoryProvider = DoubleCheck.provider(new SwitchingProvider<PaymentRepository>(singletonCImpl, 1));
      this.widgetUpdaterProvider = DoubleCheck.provider(new SwitchingProvider<WidgetUpdater>(singletonCImpl, 10));
      this.provideSettingsDataStoreProvider = DoubleCheck.provider(new SwitchingProvider<SettingsDataStore>(singletonCImpl, 11));
    }

    @Override
    public void injectPaymentApp(PaymentApp paymentApp) {
      injectPaymentApp2(paymentApp);
    }

    @Override
    public PaymentRepository repository() {
      return paymentRepositoryProvider.get();
    }

    @Override
    public ReminderScheduler scheduler() {
      return reminderSchedulerProvider.get();
    }

    @Override
    public UpdatePaymentPaidUseCase updatePaymentPaidUseCase() {
      return new UpdatePaymentPaidUseCase(paymentRepositoryProvider.get(), widgetUpdaterProvider.get());
    }

    @Override
    public Set<Boolean> getDisableFragmentGetContextFix() {
      return Collections.<Boolean>emptySet();
    }

    @Override
    public ActivityRetainedComponentBuilder retainedComponentBuilder() {
      return new ActivityRetainedCBuilder(singletonCImpl);
    }

    @Override
    public ServiceComponentBuilder serviceComponentBuilder() {
      return new ServiceCBuilder(singletonCImpl);
    }

    private PaymentApp injectPaymentApp2(PaymentApp instance) {
      PaymentApp_MembersInjector.injectReminderScheduler(instance, reminderSchedulerProvider.get());
      PaymentApp_MembersInjector.injectRepository(instance, paymentRepositoryProvider.get());
      PaymentApp_MembersInjector.injectWidgetUpdater(instance, widgetUpdaterProvider.get());
      PaymentApp_MembersInjector.injectSettingsDataStore(instance, provideSettingsDataStoreProvider.get());
      PaymentApp_MembersInjector.injectDownloadSharedBackupFromDriveUseCase(instance, new DownloadSharedBackupFromDriveUseCase());
      PaymentApp_MembersInjector.injectImportBackupJsonUseCase(instance, importBackupJsonUseCase());
      return instance;
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, int id) {
        this.singletonCImpl = singletonCImpl;
        this.id = id;
      }

      @SuppressWarnings("unchecked")
      @Override
      public T get() {
        switch (id) {
          case 0: // com.payment.app.notifications.ReminderScheduler 
          return (T) new ReminderScheduler(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 1: // com.payment.app.data.repository.PaymentRepository 
          return (T) new PaymentRepository(singletonCImpl.provideDatabaseProvider.get(), singletonCImpl.provideCardDaoProvider.get(), singletonCImpl.providePaymentDaoProvider.get(), singletonCImpl.provideAccountDaoProvider.get(), singletonCImpl.provideBudgetDaoProvider.get(), singletonCImpl.provideSubscriptionDaoProvider.get(), singletonCImpl.provideInstallmentDaoProvider.get(), singletonCImpl.provideNotificationSettingDaoProvider.get());

          case 2: // com.payment.app.data.db.AppDatabase 
          return (T) AppModule_ProvideDatabaseFactory.provideDatabase(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 3: // com.payment.app.data.db.CardDao 
          return (T) AppModule_ProvideCardDaoFactory.provideCardDao(singletonCImpl.provideDatabaseProvider.get());

          case 4: // com.payment.app.data.db.PaymentDao 
          return (T) AppModule_ProvidePaymentDaoFactory.providePaymentDao(singletonCImpl.provideDatabaseProvider.get());

          case 5: // com.payment.app.data.db.AccountDao 
          return (T) AppModule_ProvideAccountDaoFactory.provideAccountDao(singletonCImpl.provideDatabaseProvider.get());

          case 6: // com.payment.app.data.db.BudgetDao 
          return (T) AppModule_ProvideBudgetDaoFactory.provideBudgetDao(singletonCImpl.provideDatabaseProvider.get());

          case 7: // com.payment.app.data.db.SubscriptionDao 
          return (T) AppModule_ProvideSubscriptionDaoFactory.provideSubscriptionDao(singletonCImpl.provideDatabaseProvider.get());

          case 8: // com.payment.app.data.db.InstallmentDao 
          return (T) AppModule_ProvideInstallmentDaoFactory.provideInstallmentDao(singletonCImpl.provideDatabaseProvider.get());

          case 9: // com.payment.app.data.db.NotificationSettingDao 
          return (T) AppModule_ProvideNotificationSettingDaoFactory.provideNotificationSettingDao(singletonCImpl.provideDatabaseProvider.get());

          case 10: // com.payment.app.widget.WidgetUpdater 
          return (T) new WidgetUpdater(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 11: // com.payment.app.data.datastore.SettingsDataStore 
          return (T) AppModule_ProvideSettingsDataStoreFactory.provideSettingsDataStore(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          default: throw new AssertionError(id);
        }
      }
    }
  }
}
