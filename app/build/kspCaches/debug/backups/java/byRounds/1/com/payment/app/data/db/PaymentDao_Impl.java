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
import com.payment.app.data.db.entity.PaymentEntity;
import java.lang.Class;
import java.lang.Exception;
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
public final class PaymentDao_Impl implements PaymentDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<PaymentEntity> __insertionAdapterOfPaymentEntity;

  private final SharedSQLiteStatement __preparedStmtOfUpdateAmount;

  private final SharedSQLiteStatement __preparedStmtOfDeletePaymentByCardId;

  private final SharedSQLiteStatement __preparedStmtOfResetAllAmounts;

  public PaymentDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfPaymentEntity = new EntityInsertionAdapter<PaymentEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `payments` (`paymentId`,`cardId`,`amount`,`updatedAt`) VALUES (nullif(?, 0),?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final PaymentEntity entity) {
        statement.bindLong(1, entity.getPaymentId());
        statement.bindLong(2, entity.getCardId());
        statement.bindLong(3, entity.getAmount());
        statement.bindLong(4, entity.getUpdatedAt());
      }
    };
    this.__preparedStmtOfUpdateAmount = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE payments SET amount = ?, updatedAt = ? WHERE cardId = ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeletePaymentByCardId = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM payments WHERE cardId = ?";
        return _query;
      }
    };
    this.__preparedStmtOfResetAllAmounts = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE payments SET amount = 0";
        return _query;
      }
    };
  }

  @Override
  public Object insertOrUpdatePayment(final PaymentEntity payment,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfPaymentEntity.insert(payment);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateAmount(final long cardId, final long amount, final long updatedAt,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateAmount.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, amount);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, updatedAt);
        _argIndex = 3;
        _stmt.bindLong(_argIndex, cardId);
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
          __preparedStmtOfUpdateAmount.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object deletePaymentByCardId(final long cardId,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeletePaymentByCardId.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, cardId);
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
          __preparedStmtOfDeletePaymentByCardId.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object resetAllAmounts(final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfResetAllAmounts.acquire();
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
          __preparedStmtOfResetAllAmounts.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<PaymentEntity>> getAllPayments() {
    final String _sql = "SELECT * FROM payments";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"payments"}, new Callable<List<PaymentEntity>>() {
      @Override
      @NonNull
      public List<PaymentEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfPaymentId = CursorUtil.getColumnIndexOrThrow(_cursor, "paymentId");
          final int _cursorIndexOfCardId = CursorUtil.getColumnIndexOrThrow(_cursor, "cardId");
          final int _cursorIndexOfAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "amount");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final List<PaymentEntity> _result = new ArrayList<PaymentEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final PaymentEntity _item;
            final long _tmpPaymentId;
            _tmpPaymentId = _cursor.getLong(_cursorIndexOfPaymentId);
            final long _tmpCardId;
            _tmpCardId = _cursor.getLong(_cursorIndexOfCardId);
            final long _tmpAmount;
            _tmpAmount = _cursor.getLong(_cursorIndexOfAmount);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _item = new PaymentEntity(_tmpPaymentId,_tmpCardId,_tmpAmount,_tmpUpdatedAt);
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
  public Object getPaymentByCardId(final long cardId,
      final Continuation<? super PaymentEntity> $completion) {
    final String _sql = "SELECT * FROM payments WHERE cardId = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, cardId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<PaymentEntity>() {
      @Override
      @Nullable
      public PaymentEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfPaymentId = CursorUtil.getColumnIndexOrThrow(_cursor, "paymentId");
          final int _cursorIndexOfCardId = CursorUtil.getColumnIndexOrThrow(_cursor, "cardId");
          final int _cursorIndexOfAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "amount");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final PaymentEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpPaymentId;
            _tmpPaymentId = _cursor.getLong(_cursorIndexOfPaymentId);
            final long _tmpCardId;
            _tmpCardId = _cursor.getLong(_cursorIndexOfCardId);
            final long _tmpAmount;
            _tmpAmount = _cursor.getLong(_cursorIndexOfAmount);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _result = new PaymentEntity(_tmpPaymentId,_tmpCardId,_tmpAmount,_tmpUpdatedAt);
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

  @Override
  public Flow<PaymentEntity> getPaymentByCardIdFlow(final long cardId) {
    final String _sql = "SELECT * FROM payments WHERE cardId = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, cardId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"payments"}, new Callable<PaymentEntity>() {
      @Override
      @Nullable
      public PaymentEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfPaymentId = CursorUtil.getColumnIndexOrThrow(_cursor, "paymentId");
          final int _cursorIndexOfCardId = CursorUtil.getColumnIndexOrThrow(_cursor, "cardId");
          final int _cursorIndexOfAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "amount");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final PaymentEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpPaymentId;
            _tmpPaymentId = _cursor.getLong(_cursorIndexOfPaymentId);
            final long _tmpCardId;
            _tmpCardId = _cursor.getLong(_cursorIndexOfCardId);
            final long _tmpAmount;
            _tmpAmount = _cursor.getLong(_cursorIndexOfAmount);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _result = new PaymentEntity(_tmpPaymentId,_tmpCardId,_tmpAmount,_tmpUpdatedAt);
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

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
