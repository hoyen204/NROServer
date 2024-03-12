package com.girlkun.server;

import com.girlkun.services.Service;
import com.girlkun.utils.Logger;


public class Maintenance extends Thread {

    public static boolean isRuning = false;
    public boolean canUseCode;
    private static Maintenance i;

    private int min;

    private Maintenance() {

    }

    public static Maintenance gI() {
        if (i == null) {
            i = new Maintenance();
        }
        return i;
    }

    public void start(int min) {
        if (!isRuning) {
            isRuning = true;
            this.min = min;
            this.start();
        }
    }

    @Override
    public void run() {
        while (this.min > 0) {
            this.min--;
            int timeMaintance = min > 60 ? min / 60 : min % 60;
            String time = timeMaintance + " " + (min >= 60 ? "phút" : "giây");
            if (min % 20 == 0 || min < 15)
                Service.gI().sendThongBaoAllPlayer("Hệ thống sẽ bảo trì sau " + time
                        + " nữa, vui lòng thoát game để tránh mất vật phẩm");
            try {
                Thread.sleep(1000);
            } catch (Exception e) {
            }

        }
        Logger.error("BEGIN MAINTENANCE...............................\n");
        ServerManager.gI().close(100);
    }

}
