package com.payment.app.widget;

import android.content.Context;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
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
public final class WidgetUpdater_Factory implements Factory<WidgetUpdater> {
  private final Provider<Context> contextProvider;

  public WidgetUpdater_Factory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public WidgetUpdater get() {
    return newInstance(contextProvider.get());
  }

  public static WidgetUpdater_Factory create(Provider<Context> contextProvider) {
    return new WidgetUpdater_Factory(contextProvider);
  }

  public static WidgetUpdater newInstance(Context context) {
    return new WidgetUpdater(context);
  }
}
