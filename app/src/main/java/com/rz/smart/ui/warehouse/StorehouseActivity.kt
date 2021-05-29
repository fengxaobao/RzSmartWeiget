package com.rz.smart.ui.warehouse

import android.view.Menu
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import com.jetpack.base.mvvm.ui.activity.BaseVMActivity
import com.rz.command.net.RxNetworkUtil
import com.rz.smart.R
import com.rz.smart.databinding.ActivityStorehouseBinding
import org.koin.androidx.viewmodel.ext.android.getViewModel

class StorehouseActivity : BaseVMActivity<StorehouseViewModel>() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityStorehouseBinding

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.storehouse, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_storehouse)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun initVM(): StorehouseViewModel  = getViewModel()

    override fun initView() {
        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_storehouse)
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_provider, R.id.nav_record, R.id.nav_setting
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    override fun startObserve() {
    }

    override fun onNetworkConnected(type: RxNetworkUtil.NetType?) {
    }

    override fun initTitleBar() {
    }

    override fun getChildLayoutView(): Int  =R.layout.activity_storehouse
}