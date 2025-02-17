package com.pic.catcher.service

import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import com.pic.catcher.SelfHook
import com.pic.catcher.ui.MainActivity

class AppTileService : TileService() {

    override fun onStartListening() {
        super.onStartListening()
        // 更新磁贴的状态
        val tile: Tile = qsTile;
        if (SelfHook.getInstance().isModuleEnable) {
            tile.state = Tile.STATE_ACTIVE
        } else {
            tile.state = Tile.STATE_INACTIVE
        }
        tile.updateTile();
    }

    override fun onClick() {
        super.onClick()
        PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE
        )?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                startActivityAndCollapse(it)
            } else {
                it.send()
            }
        }
    }
}