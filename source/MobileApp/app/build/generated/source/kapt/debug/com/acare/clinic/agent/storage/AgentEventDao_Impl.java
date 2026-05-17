package com.acare.clinic.agent.storage;

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

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class AgentEventDao_Impl implements AgentEventDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<AgentEventEntity> __insertionAdapterOfAgentEventEntity;

  private final SharedSQLiteStatement __preparedStmtOfMarkSynced;

  private final SharedSQLiteStatement __preparedStmtOfDeleteSynced;

  public AgentEventDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfAgentEventEntity = new EntityInsertionAdapter<AgentEventEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `agent_events` (`id`,`deviceId`,`platform`,`userId`,`sourceType`,`eventType`,`action`,`violationType`,`severity`,`contentSnippet`,`detailsJson`,`timestamp`,`synced`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final AgentEventEntity entity) {
        statement.bindLong(1, entity.getId());
        if (entity.getDeviceId() == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, entity.getDeviceId());
        }
        if (entity.getPlatform() == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.getPlatform());
        }
        if (entity.getUserId() == null) {
          statement.bindNull(4);
        } else {
          statement.bindLong(4, entity.getUserId());
        }
        if (entity.getSourceType() == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.getSourceType());
        }
        if (entity.getEventType() == null) {
          statement.bindNull(6);
        } else {
          statement.bindString(6, entity.getEventType());
        }
        if (entity.getAction() == null) {
          statement.bindNull(7);
        } else {
          statement.bindString(7, entity.getAction());
        }
        if (entity.getViolationType() == null) {
          statement.bindNull(8);
        } else {
          statement.bindString(8, entity.getViolationType());
        }
        if (entity.getSeverity() == null) {
          statement.bindNull(9);
        } else {
          statement.bindString(9, entity.getSeverity());
        }
        if (entity.getContentSnippet() == null) {
          statement.bindNull(10);
        } else {
          statement.bindString(10, entity.getContentSnippet());
        }
        if (entity.getDetailsJson() == null) {
          statement.bindNull(11);
        } else {
          statement.bindString(11, entity.getDetailsJson());
        }
        if (entity.getTimestamp() == null) {
          statement.bindNull(12);
        } else {
          statement.bindString(12, entity.getTimestamp());
        }
        final int _tmp = entity.getSynced() ? 1 : 0;
        statement.bindLong(13, _tmp);
      }
    };
    this.__preparedStmtOfMarkSynced = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE agent_events SET synced = 1 WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteSynced = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM agent_events WHERE synced = 1";
        return _query;
      }
    };
  }

  @Override
  public Object insert(final AgentEventEntity event, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfAgentEventEntity.insert(event);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object markSynced(final long id, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfMarkSynced.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, id);
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
          __preparedStmtOfMarkSynced.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteSynced(final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteSynced.acquire();
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
          __preparedStmtOfDeleteSynced.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object getPendingEvents(final int limit,
      final Continuation<? super List<AgentEventEntity>> $completion) {
    final String _sql = "SELECT * FROM agent_events WHERE synced = 0 ORDER BY id ASC LIMIT ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, limit);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<AgentEventEntity>>() {
      @Override
      @NonNull
      public List<AgentEventEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfDeviceId = CursorUtil.getColumnIndexOrThrow(_cursor, "deviceId");
          final int _cursorIndexOfPlatform = CursorUtil.getColumnIndexOrThrow(_cursor, "platform");
          final int _cursorIndexOfUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "userId");
          final int _cursorIndexOfSourceType = CursorUtil.getColumnIndexOrThrow(_cursor, "sourceType");
          final int _cursorIndexOfEventType = CursorUtil.getColumnIndexOrThrow(_cursor, "eventType");
          final int _cursorIndexOfAction = CursorUtil.getColumnIndexOrThrow(_cursor, "action");
          final int _cursorIndexOfViolationType = CursorUtil.getColumnIndexOrThrow(_cursor, "violationType");
          final int _cursorIndexOfSeverity = CursorUtil.getColumnIndexOrThrow(_cursor, "severity");
          final int _cursorIndexOfContentSnippet = CursorUtil.getColumnIndexOrThrow(_cursor, "contentSnippet");
          final int _cursorIndexOfDetailsJson = CursorUtil.getColumnIndexOrThrow(_cursor, "detailsJson");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfSynced = CursorUtil.getColumnIndexOrThrow(_cursor, "synced");
          final List<AgentEventEntity> _result = new ArrayList<AgentEventEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final AgentEventEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpDeviceId;
            if (_cursor.isNull(_cursorIndexOfDeviceId)) {
              _tmpDeviceId = null;
            } else {
              _tmpDeviceId = _cursor.getString(_cursorIndexOfDeviceId);
            }
            final String _tmpPlatform;
            if (_cursor.isNull(_cursorIndexOfPlatform)) {
              _tmpPlatform = null;
            } else {
              _tmpPlatform = _cursor.getString(_cursorIndexOfPlatform);
            }
            final Long _tmpUserId;
            if (_cursor.isNull(_cursorIndexOfUserId)) {
              _tmpUserId = null;
            } else {
              _tmpUserId = _cursor.getLong(_cursorIndexOfUserId);
            }
            final String _tmpSourceType;
            if (_cursor.isNull(_cursorIndexOfSourceType)) {
              _tmpSourceType = null;
            } else {
              _tmpSourceType = _cursor.getString(_cursorIndexOfSourceType);
            }
            final String _tmpEventType;
            if (_cursor.isNull(_cursorIndexOfEventType)) {
              _tmpEventType = null;
            } else {
              _tmpEventType = _cursor.getString(_cursorIndexOfEventType);
            }
            final String _tmpAction;
            if (_cursor.isNull(_cursorIndexOfAction)) {
              _tmpAction = null;
            } else {
              _tmpAction = _cursor.getString(_cursorIndexOfAction);
            }
            final String _tmpViolationType;
            if (_cursor.isNull(_cursorIndexOfViolationType)) {
              _tmpViolationType = null;
            } else {
              _tmpViolationType = _cursor.getString(_cursorIndexOfViolationType);
            }
            final String _tmpSeverity;
            if (_cursor.isNull(_cursorIndexOfSeverity)) {
              _tmpSeverity = null;
            } else {
              _tmpSeverity = _cursor.getString(_cursorIndexOfSeverity);
            }
            final String _tmpContentSnippet;
            if (_cursor.isNull(_cursorIndexOfContentSnippet)) {
              _tmpContentSnippet = null;
            } else {
              _tmpContentSnippet = _cursor.getString(_cursorIndexOfContentSnippet);
            }
            final String _tmpDetailsJson;
            if (_cursor.isNull(_cursorIndexOfDetailsJson)) {
              _tmpDetailsJson = null;
            } else {
              _tmpDetailsJson = _cursor.getString(_cursorIndexOfDetailsJson);
            }
            final String _tmpTimestamp;
            if (_cursor.isNull(_cursorIndexOfTimestamp)) {
              _tmpTimestamp = null;
            } else {
              _tmpTimestamp = _cursor.getString(_cursorIndexOfTimestamp);
            }
            final boolean _tmpSynced;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfSynced);
            _tmpSynced = _tmp != 0;
            _item = new AgentEventEntity(_tmpId,_tmpDeviceId,_tmpPlatform,_tmpUserId,_tmpSourceType,_tmpEventType,_tmpAction,_tmpViolationType,_tmpSeverity,_tmpContentSnippet,_tmpDetailsJson,_tmpTimestamp,_tmpSynced);
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
