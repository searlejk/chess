package dataaccess;

public class DataAccessProvider {
    public static DataAccess dataAccess;

    public static void setDataAccess(DataAccess da) {
        dataAccess = da;
    }

    public static DataAccess getDataAccess() {
        return dataAccess;
    }
}
