package com.payment.app.data.db;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.payment.app.data.db.entity.InstallmentEntity;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class InstallmentDao_Impl implements InstallmentDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<InstallmentEntity> __insertionAdapterOfInstallmentEntity;

  private final SharedSQLiteStatement __preparedStmtOfClearAll;

  public InstallmentDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfInstallmentEntity = new EntityInsertionAdapter<InstallmentEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `installments` (`installmentId`,`paymentId`,`totalAmount`,`totalMonths`,`startYearMonth`) VALUES (nullif(?, 0),?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final InstallmentEntity entity) {
        statement.bindLong(1, entity.getInstallmentId());
        statement.bindLong(2, entity.getPaymentId());
        statement.bindLong(3, entity.getTotalAmount());
        statement.bindLong(4, entity.getTotalMonths());
        statement.bindString(5, entity.getStartYearMonth());
      }
    };
    this.__preparedStmtOfClearAll = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM installments";
        return _query;
      }
    };
  }

  @Override
  public Object upsert(final InstallmentEntity entity,
      final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfInstallmentEntity.insertAndReturnId(entity);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object upsertAll(final List<InstallmentEntity> entities,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfInstallmentEntity.insert(entities);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object clearAll(final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfClearAll.acquire();
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
          __preparedStmtOfClearAll.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<InstallmentEntity>> getAllInstallments() {
    final String _sql = "SELECT * FROM installments";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"installments"}, new Callable<List<InstallmentEntity>>() {
      @Override
      @NonNull
      public List<InstallmentEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfInstallmentId = CursorUtil.getColumnIndexOrThrow(_cursor, "installmentId");
          final int _cursorIndexOfPaymentId = CursorUtil.getColumnIndexOrThrow(_cursor, "paymentId");
          final int _cursorIndexOfTotalAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "totalAmount");
          final int _cursorIndexOfTotalMonths = CursorUtil.getColumnIndexOrThrow(_cursor, "totalMonths");
          final int _cursorIndexOfStartYearMonth = CursorUtil.getColumnIndexOrThrow(_cursor, "startYearMonth");
          final List<InstallmentEntity> _result = new ArrayList<InstallmentEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final InstallmentEntity _item;
            final long _tmpInstallmentId;
            _tmpInstallmentId = _cursor.getLong(_cursorIndexOfInstallmentId);
            final long _tmpPaymentId;
            _tmpPaymentId = _cursor.getLong(_cursorIndexOfPaymentId);
            final long _tmpTotalAmount;
            _tmpTotalAmount = _cursor.getLong(_cursorIndexOfTotalAmount);
            final int _tmpTotalMonths;
            _tmpTotalMonths = _cursor.getInt(_cursorIndexOfTotalMonths);
            final String _tmpStartYearMonth;
            _tmpStartYearMonth = _cursor.getString(_cursorIndexOfStartYearMonth);
            _item = new InstallmentEntity(_tmpInstallmentId,_tmpPaymentId,_tmpTotalAmount,_tmpTotalMonths,_tmpStartYearMonth);
            _result.add(_item);
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
  public Object getAllInstallmentsOnce(
      final Continuation<? super List<InstallmentEntity>> $completion) {
    final String _sql = "SELECT * FROM installments";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<InstallmentEntity>>() {
      @Override
      @NonNull
      public List<InstallmentEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfInstallmentId = CursorUtil.getColumnIndexOrThrow(_cursor, "installmentId");
          final int _cursorIndexOfPaymentId = CursorUtil.getColumnIndexOrThrow(_cursor, "paymentId");
          final int _cursorIndexOfTotalAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "totalAmount");
          final int _cursorIndexOfTotalMonths = CursorUtil.getColumnIndexOrThrow(_cursor, "totalMonths");
          final int _cursorIndexOfStartYearMonth = CursorUtil.getColumnIndexOrThrow(_cursor, "startYearMonth");
          final List<InstallmentEntity> _result = new ArrayList<InstallmentEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final InstallmentEntity _item;
            final long _tmpInstallmentId;
            _tmpInstallmentId = _cursor.getLong(_cursorIndexOfInstallmentId);
            final long _tmpPaymentId;
            _tmpPaymentId = _cursor.getLong(_cursorIndexOfPaymentId);
            final long _tmpTotalAmount;
            _tmpTotalAmount = _cursor.getLong(_cursorIndexOfTotalAmount);
            final int _tmpTotalMonths;
            _tmpTotalMonths = _cursor.getInt(_cursorIndexOfTotalMonths);
            final String _tmpStartYearMonth;
            _tmpStartYearMonth = _cursor.getString(_cursorIndexOfStartYearMonth);
            _item = new InstallmentEntity(_tmpInstallmentId,_tmpPaymentId,_tmpTotalAmount,_tmpTotalMonths,_tmpStartYearMonth);
            _result.add(_item);
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
