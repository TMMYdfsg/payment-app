package com.payment.app.data.db;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.payment.app.data.db.entity.CardEntity;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Integer;
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
public final class CardDao_Impl implements CardDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<CardEntity> __insertionAdapterOfCardEntity;

  private final EntityDeletionOrUpdateAdapter<CardEntity> __deletionAdapterOfCardEntity;

  public CardDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfCardEntity = new EntityInsertionAdapter<CardEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `cards` (`cardId`,`cardName`,`dueDate`) VALUES (nullif(?, 0),?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final CardEntity entity) {
        statement.bindLong(1, entity.getCardId());
        statement.bindString(2, entity.getCardName());
        statement.bindLong(3, entity.getDueDate());
      }
    };
    this.__deletionAdapterOfCardEntity = new EntityDeletionOrUpdateAdapter<CardEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `cards` WHERE `cardId` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final CardEntity entity) {
        statement.bindLong(1, entity.getCardId());
      }
    };
  }

  @Override
  public Object insertCard(final CardEntity card, final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfCardEntity.insertAndReturnId(card);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertCards(final List<CardEntity> cards,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfCardEntity.insert(cards);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteCard(final CardEntity card, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfCardEntity.handle(card);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<CardEntity>> getAllCards() {
    final String _sql = "SELECT * FROM cards ORDER BY dueDate ASC, cardId ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"cards"}, new Callable<List<CardEntity>>() {
      @Override
      @NonNull
      public List<CardEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfCardId = CursorUtil.getColumnIndexOrThrow(_cursor, "cardId");
          final int _cursorIndexOfCardName = CursorUtil.getColumnIndexOrThrow(_cursor, "cardName");
          final int _cursorIndexOfDueDate = CursorUtil.getColumnIndexOrThrow(_cursor, "dueDate");
          final List<CardEntity> _result = new ArrayList<CardEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final CardEntity _item;
            final long _tmpCardId;
            _tmpCardId = _cursor.getLong(_cursorIndexOfCardId);
            final String _tmpCardName;
            _tmpCardName = _cursor.getString(_cursorIndexOfCardName);
            final int _tmpDueDate;
            _tmpDueDate = _cursor.getInt(_cursorIndexOfDueDate);
            _item = new CardEntity(_tmpCardId,_tmpCardName,_tmpDueDate);
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
  public Object getAllCardsOnce(final Continuation<? super List<CardEntity>> $completion) {
    final String _sql = "SELECT * FROM cards ORDER BY dueDate ASC, cardId ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<CardEntity>>() {
      @Override
      @NonNull
      public List<CardEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfCardId = CursorUtil.getColumnIndexOrThrow(_cursor, "cardId");
          final int _cursorIndexOfCardName = CursorUtil.getColumnIndexOrThrow(_cursor, "cardName");
          final int _cursorIndexOfDueDate = CursorUtil.getColumnIndexOrThrow(_cursor, "dueDate");
          final List<CardEntity> _result = new ArrayList<CardEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final CardEntity _item;
            final long _tmpCardId;
            _tmpCardId = _cursor.getLong(_cursorIndexOfCardId);
            final String _tmpCardName;
            _tmpCardName = _cursor.getString(_cursorIndexOfCardName);
            final int _tmpDueDate;
            _tmpDueDate = _cursor.getInt(_cursorIndexOfDueDate);
            _item = new CardEntity(_tmpCardId,_tmpCardName,_tmpDueDate);
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

  @Override
  public Flow<List<CardEntity>> getCardsByDueDate(final int dueDate) {
    final String _sql = "SELECT * FROM cards WHERE dueDate = ? ORDER BY cardId ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, dueDate);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"cards"}, new Callable<List<CardEntity>>() {
      @Override
      @NonNull
      public List<CardEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfCardId = CursorUtil.getColumnIndexOrThrow(_cursor, "cardId");
          final int _cursorIndexOfCardName = CursorUtil.getColumnIndexOrThrow(_cursor, "cardName");
          final int _cursorIndexOfDueDate = CursorUtil.getColumnIndexOrThrow(_cursor, "dueDate");
          final List<CardEntity> _result = new ArrayList<CardEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final CardEntity _item;
            final long _tmpCardId;
            _tmpCardId = _cursor.getLong(_cursorIndexOfCardId);
            final String _tmpCardName;
            _tmpCardName = _cursor.getString(_cursorIndexOfCardName);
            final int _tmpDueDate;
            _tmpDueDate = _cursor.getInt(_cursorIndexOfDueDate);
            _item = new CardEntity(_tmpCardId,_tmpCardName,_tmpDueDate);
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
  public Flow<List<Integer>> getDistinctDueDates() {
    final String _sql = "SELECT DISTINCT dueDate FROM cards ORDER BY dueDate ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"cards"}, new Callable<List<Integer>>() {
      @Override
      @NonNull
      public List<Integer> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final List<Integer> _result = new ArrayList<Integer>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Integer _item;
            _item = _cursor.getInt(0);
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
  public Object getCardCount(final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM cards";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Integer>() {
      @Override
      @NonNull
      public Integer call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Integer _result;
          if (_cursor.moveToFirst()) {
            final int _tmp;
            _tmp = _cursor.getInt(0);
            _result = _tmp;
          } else {
            _result = 0;
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
