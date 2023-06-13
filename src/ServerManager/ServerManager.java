/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ServerManager;

import com.girlkun.jdbc.daos.PlayerDAO;
import com.girlkun.server.Client;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import java.util.concurrent.TimeUnit;
public class ServerManager {
    private static ServerManager instance = null;
    
    private ServerManager() {
        compositeDisposable = new CompositeDisposable();
    }

    // Static method
    // Static method to create instance of Singleton class
    public static synchronized ServerManager getInstance() {
        if (instance == null) {
            instance = new ServerManager();
        }
        return instance;
    }
    
    private CompositeDisposable compositeDisposable;
    
    public void autoSave() {
        System.out.println("[AutoSaveManager] start autosave");
        Disposable subscribe = Observable.interval(60, 90, TimeUnit.SECONDS)
                .observeOn(Schedulers.io())
                .subscribe(i -> {
                    this.handleAutoSave();
                },  throwable -> {
              System.out.println("[AutoSaveManager] start autosave error: " + throwable.getLocalizedMessage());
        });
        compositeDisposable.add(subscribe);               
    }
    
    private void handleAutoSave() {
        Client.gI().getPlayers().forEach(player -> {
            System.out.println("Save data success of player:" + player.name);
            PlayerDAO.updatePlayer(player);
        });
    }
    
    private void dispose() {
        compositeDisposable.dispose();
        compositeDisposable = null;
    }
}
