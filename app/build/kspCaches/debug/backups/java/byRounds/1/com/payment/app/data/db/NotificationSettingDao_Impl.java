package com.payment.app.data.db;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.payment.app.data.db.entity.NotificationSettingEntity;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class NotificationSettingDao_Impl implements NotificationSettingDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<NotificationSettingEntity> __insertionAdapterOfNotificationSettingEntity;

  private final SharedSQLiteStatement __preparedStmtOfDeleteOthers;

  public NotificationSettingDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfNotificationSettingEntity = new EntityInsertionAdapter<NotificationSettingEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `notification_settings` (`id`,`reminderLeadDays`,`budgetAlertThreshold`,`monthlyReminderDay`,`enabled`) VALUES (nullif(?, 0),?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final NotificationSettingEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getReminderLeadDays());
        statement.bindLong(3, entity.getBudgetAlertThreshold());
        statement.bindLong(4, entity.getMonthlyReminderDay());
        final int _tmp = entity.getEnabled() ? 1 : 0;
        statement.bindLong(5, _tmp);
      }
    };
    this.__preparedStmtOfDeleteOthers = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM notification_settings WHERE id != ?";
        return _query;
      }
    };
  }

  @Override
  public Object upsert(final NotificationSettingEntity setting,
      final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfNotificationSettingEntity.insertAndReturnId(setting);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteOthers(final long keepId, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteOthers.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, keepId);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfDeleteOthers.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<NotificationSettingEntity> getSettings() {
    final String _sql = "SELECT * FROM notification_settings ORDER BY id DESC LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"notification_settings"}, new Callable<NotificationSettingEntity>() {
      @Override
      @Nullable
      public NotificationSettingEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfReminderLeadDays = CursorUtil.getColumnIndexOrThrow(_cursor, "reminderLeadDays");
          final int _cursorIndexOfBudgetAlertThreshold = CursorUtil.getColumnIndexOrThrow(_cursor, "budgetAlertThreshold");
          final int _cursorIndexOfMonthlyReminderDay = CursorUtil.getColumnIndexOrThrow(_cursor, "monthlyReminderDay");
          final int _cursorIndexOfEnabled = CursorUtil.getColumnIndexOrThrow(_cursor, "enabled");
          final NotificationSettingEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final int _tmpReminderLeadDays;
            _tmpReminderLeadDays = _cursor.getInt(_cursorIndexOfReminderLeadDays);
            final int _tmpBudgetAlertThreshold;
            _tmpBudgetAlertThreshold = _cursor.getInt(_cursorIndexOfBudgetAlertThreshold);
            final int _tmpMonthlyReminderDay;
            _tmpMonthlyReminderDay = _cursor.getInt(_cursorIndexOfMonthlyReminderDay);
            final boolean _tmpEnabled;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfEnabled);
            _tmpEnabled = _tmp != 0;
            _result = new NotificationSettingEntity(_tmpId,_tmpReminderLeadDays,_tmpBudgetAlertThreshold,_tmpMonthlyReminderDay,_tmpEnabled);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object getSettingsOnce(final Continuation<? super NotificationSettingEntity> $completion) {
    final String _sql = "SELECT * FROM notification_settings ORDER BY id DESC LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<NotificationSettingEntity>() {
      @Override
      @Nullable
      public NotificationSettingEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfReminderLeadDays = CursorUtil.getColumnIndexOrThrow(_cursor, "reminderLeadDays");
          final int _cursorIndexOfBudgetAlertThreshold = CursorUtil.getColumnIndexOrThrow(_cursor, "budgetAlertThreshold");
          final int _cursorIndexOfMonthlyReminderDay = CursorUtil.getColumnIndexOrThrow(_cursor, "monthlyReminderDay");
          final int _cursorIndexOfEnabled = CursorUtil.getColumnIndexOrThrow(_cursor, "enabled");
          final NotificationSettingEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final int _tmpReminderLeadDays;
            _tmpReminderLeadDays = _cursor.getInt(_cursorIndexOfReminderLeadDays);
            final int _tmpBudgetAlertThreshold;
            _tmpBudgetAlertThreshold = _cursor.getInt(_cursorIndexOfBudgetAlertThreshold);
            final int _tmpMonthlyReminderDay;
            _tmpMonthlyReminderDay = _cursor.getInt(_cursorIndexOfMonthlyReminderDay);
            final boolean _tmpEnabled;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfEnabled);
            _tmpEnabled = _tmp != 0;
            _result = new NotificationSettingEntity(_tmpId,_tmpReminderLeadDays,_tmpBudgetAlertThreshold,_tmpMonthlyReminderDay,_tmpEnabled);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
