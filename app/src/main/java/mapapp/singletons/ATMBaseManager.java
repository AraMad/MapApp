package mapapp.singletons;

import mapapp.MapApplication;
import mapapp.managers.DataBaseManager;

/**
 * Created by Arina on 06.06.2017
 */

public class ATMBaseManager {

    private static DataBaseManager dataBaseManager;

    public static synchronized DataBaseManager getInstance(){
        if (dataBaseManager == null){
            dataBaseManager = new DataBaseManager(MapApplication.getMainContext());
        }

        return dataBaseManager;
    }
}
