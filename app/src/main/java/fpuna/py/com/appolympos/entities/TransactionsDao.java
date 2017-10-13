package fpuna.py.com.appolympos.entities;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "TRANSACTIONS".
*/
public class TransactionsDao extends AbstractDao<Transactions, Long> {

    public static final String TABLENAME = "TRANSACTIONS";

    /**
     * Properties of entity Transactions.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property Type = new Property(1, String.class, "type", false, "TYPE");
        public final static Property ClientId = new Property(2, String.class, "ClientId", false, "CLIENT_ID");
        public final static Property ClientName = new Property(3, String.class, "ClientName", false, "CLIENT_NAME");
        public final static Property Amount = new Property(4, Integer.class, "amount", false, "AMOUNT");
        public final static Property HttpDetail = new Property(5, String.class, "httpDetail", false, "HTTP_DETAIL");
        public final static Property Status = new Property(6, Integer.class, "status", false, "STATUS");
        public final static Property Attempts = new Property(7, Integer.class, "attempts", false, "ATTEMPTS");
        public final static Property CreatedAt = new Property(8, java.util.Date.class, "createdAt", false, "CREATED_AT");
        public final static Property UpdatedAt = new Property(9, java.util.Date.class, "updatedAt", false, "UPDATED_AT");
        public final static Property Observation = new Property(10, String.class, "observation", false, "OBSERVATION");
        public final static Property Queued = new Property(11, Integer.class, "queued", false, "QUEUED");
        public final static Property Tries = new Property(12, Integer.class, "tries", false, "TRIES");
    };


    public TransactionsDao(DaoConfig config) {
        super(config);
    }
    
    public TransactionsDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"TRANSACTIONS\" (" + //
                "\"_id\" INTEGER PRIMARY KEY ," + // 0: id
                "\"TYPE\" TEXT," + // 1: type
                "\"CLIENT_ID\" TEXT," + // 2: ClientId
                "\"CLIENT_NAME\" TEXT," + // 3: ClientName
                "\"AMOUNT\" INTEGER," + // 4: amount
                "\"HTTP_DETAIL\" TEXT," + // 5: httpDetail
                "\"STATUS\" INTEGER," + // 6: status
                "\"ATTEMPTS\" INTEGER," + // 7: attempts
                "\"CREATED_AT\" INTEGER," + // 8: createdAt
                "\"UPDATED_AT\" INTEGER," + // 9: updatedAt
                "\"OBSERVATION\" TEXT," + // 10: observation
                "\"QUEUED\" INTEGER," + // 11: queued
                "\"TRIES\" INTEGER);"); // 12: tries
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"TRANSACTIONS\"";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, Transactions entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String type = entity.getType();
        if (type != null) {
            stmt.bindString(2, type);
        }
 
        String ClientId = entity.getClientId();
        if (ClientId != null) {
            stmt.bindString(3, ClientId);
        }
 
        String ClientName = entity.getClientName();
        if (ClientName != null) {
            stmt.bindString(4, ClientName);
        }
 
        Integer amount = entity.getAmount();
        if (amount != null) {
            stmt.bindLong(5, amount);
        }
 
        String httpDetail = entity.getHttpDetail();
        if (httpDetail != null) {
            stmt.bindString(6, httpDetail);
        }
 
        Integer status = entity.getStatus();
        if (status != null) {
            stmt.bindLong(7, status);
        }
 
        Integer attempts = entity.getAttempts();
        if (attempts != null) {
            stmt.bindLong(8, attempts);
        }
 
        java.util.Date createdAt = entity.getCreatedAt();
        if (createdAt != null) {
            stmt.bindLong(9, createdAt.getTime());
        }
 
        java.util.Date updatedAt = entity.getUpdatedAt();
        if (updatedAt != null) {
            stmt.bindLong(10, updatedAt.getTime());
        }
 
        String observation = entity.getObservation();
        if (observation != null) {
            stmt.bindString(11, observation);
        }
 
        Integer queued = entity.getQueued();
        if (queued != null) {
            stmt.bindLong(12, queued);
        }
 
        Integer tries = entity.getTries();
        if (tries != null) {
            stmt.bindLong(13, tries);
        }
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public Transactions readEntity(Cursor cursor, int offset) {
        Transactions entity = new Transactions( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // type
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // ClientId
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // ClientName
            cursor.isNull(offset + 4) ? null : cursor.getInt(offset + 4), // amount
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // httpDetail
            cursor.isNull(offset + 6) ? null : cursor.getInt(offset + 6), // status
            cursor.isNull(offset + 7) ? null : cursor.getInt(offset + 7), // attempts
            cursor.isNull(offset + 8) ? null : new java.util.Date(cursor.getLong(offset + 8)), // createdAt
            cursor.isNull(offset + 9) ? null : new java.util.Date(cursor.getLong(offset + 9)), // updatedAt
            cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10), // observation
            cursor.isNull(offset + 11) ? null : cursor.getInt(offset + 11), // queued
            cursor.isNull(offset + 12) ? null : cursor.getInt(offset + 12) // tries
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, Transactions entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setType(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setClientId(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setClientName(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setAmount(cursor.isNull(offset + 4) ? null : cursor.getInt(offset + 4));
        entity.setHttpDetail(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setStatus(cursor.isNull(offset + 6) ? null : cursor.getInt(offset + 6));
        entity.setAttempts(cursor.isNull(offset + 7) ? null : cursor.getInt(offset + 7));
        entity.setCreatedAt(cursor.isNull(offset + 8) ? null : new java.util.Date(cursor.getLong(offset + 8)));
        entity.setUpdatedAt(cursor.isNull(offset + 9) ? null : new java.util.Date(cursor.getLong(offset + 9)));
        entity.setObservation(cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10));
        entity.setQueued(cursor.isNull(offset + 11) ? null : cursor.getInt(offset + 11));
        entity.setTries(cursor.isNull(offset + 12) ? null : cursor.getInt(offset + 12));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(Transactions entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(Transactions entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    /** @inheritdoc */
    @Override    
    protected boolean isEntityUpdateable() {
        return true;
    }
    
}