package com.Alvaro.h2;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.List;

public class ConnectionUtil {

    private static Connection con;
    private static final Logger logger = LoggerFactory.getLogger(ConnectionUtil.class);

    public static void connect() {
        if (con == null) {
            org.h2.Driver b = new org.h2.Driver();
            try {
                con = DriverManager.getConnection("jdbc:h2:./data;USER=sa;PASSWORD=");
            } catch (SQLException e) {
                logger.error("Error en la conexión H2:\n " + e.getMessage());
            }
            checkStructure();
        }
    }

    /**
     * Esta función ejecuta consultas de un PreparedStatement
     *
     * @param q      Sentencia a ejecutar
     * @param params parámetros de la sentencia
     * @return El último id insertado ó el número de filas afectadas de la consulta
     */
    public static ResultSet execQuery(String q, List<Object> params) {
        ResultSet result;
        if (con != null) {
            PreparedStatement ps = prepareQuery(q, params);
            try {
                result = ps.executeQuery();
            } catch (SQLException e) {
                if (e.getClass().equals(SQLTimeoutException.class)) {
                    logger.error("El driver ha determinado que el tiempo especificado por setQueryTimeOut ha sido excedido\nCon la sentencia: " + q);
                } else
                    logger.error("Hubo un error en la conexión con la base de datos" + e.getMessage() + "\nCon la sentencia: " + q);
                result = null;
            }
        } else {
            result = null;
        }
        return result;
    }

    /**
     * Esta función ejecuta actualizaciones de un PreparedStatement
     *
     * @param q      Sentencia a ejecutar
     * @param params parámetros de la sentencia
     * @param insert Si es true devuelve el último id insertado
     * @return El último id insertado ó el número de filas afectadas de la consulta
     */
    public static long execUpdate(String q, List<Object> params, boolean insert) {
        long result;
        if (con != null) {
            PreparedStatement ps = prepareQuery(q, params);
            try {
                result = ps.executeUpdate();
            } catch (SQLException e) {
                if (e.getClass().equals(SQLTimeoutException.class)) {
                    logger.error("El driver ha determinado que el tiempo especificado por setQueryTimeOut ha sido excedido\nCon la sentencia: " + q);
                } else
                    logger.error("Hubo un error en la conexión con la base de datos" + e.getMessage() + "\nCon la sentencia: " + q);
                result = -1;
            }
            if (insert) {
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next())
                        result = generatedKeys.getLong(1);
                    else
                        result = -1;
                } catch (SQLException e) {
                    if (e.getClass().equals(SQLFeatureNotSupportedException.class))
                        logger.error("El driver JDBC no soporta el método getGeneratedKeys()");
                    else
                        logger.error("Error en la conexión mysql: " + e.getMessage() + "\nCon la sentencia" + q);
                }
            }
        } else {
            result = -1;
        }
        return result;
    }

    /**
     * Este método devuelve PreparedStatements a partir de una lista de objetos que se le pasan como parámetro
     * Esta lista debe estar en orden
     *
     * @param q      Sentencia con "?"
     * @param params parámetros a sustituir las "?"
     * @return PreparedStatement ya listo
     */
    private static PreparedStatement prepareQuery(String q, List<Object> params) {
        PreparedStatement ps = null;
        try {
            ps = con.prepareStatement(q, Statement.RETURN_GENERATED_KEYS);
        } catch (SQLException e) {
            if (e.getClass().equals(SQLFeatureNotSupportedException.class)) {
                logger.error("El driver JDBC no soporta Statement.RETURN_GENERATED_KEYS");
            } else
                logger.error("Error en la conexión mysql: " + e.getMessage() + "\nCon la sentencia" + q);
        }
        if (params != null && ps != null) {
            int i = 1;
            for (Object o : params) {
                try {
                    ps.setObject(i++, o);
                } catch (SQLException e) {
                    logger.error("Hubo un error al intentar setear el parámetro " + i + " del objeto " + o.getClass().getName() + e.getMessage());
                    break;
                }
            }
        }
        return ps;
    }

    /**
     * Esta función revisa que las tablas estén creadas y en caso contrario, las crea con las claves foráneas.
     */
    private static void checkStructure() {
        String sql1 = "CREATE TABLE IF NOT EXISTS history (" +
                " id bigint(20) NOT NULL AUTO_INCREMENT," +
                " time TIME NOT NULL," +
                " PRIMARY KEY (id)" +
                ")";
        try {
            con.setAutoCommit(false);
            execUpdate(sql1, null, false);
            con.commit();
            con.setAutoCommit(true);
        } catch (SQLException e) {
            logger.error("Hubo un error en la conexión con la base de datos o se intentó acceder a una conexión ya cerrada: " + e.getMessage());
        }
    }
    /**
     * This method is used to close DB Connection on com.EarthSound.utils.connections.SQL
     * Must be called once, at the end of the app
     */
    public static void disconnect(){
        if(con!=null){
            try {
                con.close();
            } catch (SQLException e) {
                logger.error("Ocurrió un error de conexión con la base de datos: \n"+e.getMessage());
            }
        }
    }
}
