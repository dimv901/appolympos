package fpuna.py.com.appolympos.entities;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "VENDEDORES".
*/
public class VendedoresDao extends AbstractDao<Vendedores, Long> {

    public static final String TABLENAME = "VENDEDORES";

    /**
     * Properties of entity Vendedores.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "ID");
        public final static Property Nombre = new Property(1, String.class, "nombre", false, "NOMBRE");
        public final static Property Apellido = new Property(2, String.class, "apellido", false, "APELLIDO");
        public final static Property Cedula = new Property(3, String.class, "cedula", false, "CEDULA");
        public final static Property Activo = new Property(4, Boolean.class, "activo", false, "ACTIVO");
        public final static Property Android = new Property(5, Boolean.class, "android", false, "ANDROID");
        public final static Property TelefonoContacto = new Property(6, String.class, "telefonoContacto", false, "TELEFONO_CONTACTO");
        public final static Property IdCircuito = new Property(7, Long.class, "idCircuito", false, "ID_CIRCUITO");
    };


    public VendedoresDao(DaoConfig config) {
        super(config);
    }
    
    public VendedoresDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"VENDEDORES\" (" + //
                "\"ID\" INTEGER PRIMARY KEY ," + // 0: id
                "\"NOMBRE\" TEXT," + // 1: nombre
                "\"APELLIDO\" TEXT," + // 2: apellido
                "\"CEDULA\" TEXT," + // 3: cedula
                "\"ACTIVO\" INTEGER," + // 4: activo
                "\"ANDROID\" INTEGER," + // 5: android
                "\"TELEFONO_CONTACTO\" TEXT," + // 6: telefonoContacto
                "\"ID_CIRCUITO\" INTEGER);"); // 7: idCircuito
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"VENDEDORES\"";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, Vendedores entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String nombre = entity.getNombre();
        if (nombre != null) {
            stmt.bindString(2, nombre);
        }
 
        String apellido = entity.getApellido();
        if (apellido != null) {
            stmt.bindString(3, apellido);
        }
 
        String cedula = entity.getCedula();
        if (cedula != null) {
            stmt.bindString(4, cedula);
        }
 
        Boolean activo = entity.getActivo();
        if (activo != null) {
            stmt.bindLong(5, activo ? 1L: 0L);
        }
 
        Boolean android = entity.getAndroid();
        if (android != null) {
            stmt.bindLong(6, android ? 1L: 0L);
        }
 
        String telefonoContacto = entity.getTelefonoContacto();
        if (telefonoContacto != null) {
            stmt.bindString(7, telefonoContacto);
        }
 
        Long idCircuito = entity.getIdCircuito();
        if (idCircuito != null) {
            stmt.bindLong(8, idCircuito);
        }
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public Vendedores readEntity(Cursor cursor, int offset) {
        Vendedores entity = new Vendedores( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // nombre
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // apellido
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // cedula
            cursor.isNull(offset + 4) ? null : cursor.getShort(offset + 4) != 0, // activo
            cursor.isNull(offset + 5) ? null : cursor.getShort(offset + 5) != 0, // android
            cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6), // telefonoContacto
            cursor.isNull(offset + 7) ? null : cursor.getLong(offset + 7) // idCircuito
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, Vendedores entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setNombre(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setApellido(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setCedula(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setActivo(cursor.isNull(offset + 4) ? null : cursor.getShort(offset + 4) != 0);
        entity.setAndroid(cursor.isNull(offset + 5) ? null : cursor.getShort(offset + 5) != 0);
        entity.setTelefonoContacto(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
        entity.setIdCircuito(cursor.isNull(offset + 7) ? null : cursor.getLong(offset + 7));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(Vendedores entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(Vendedores entity) {
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
