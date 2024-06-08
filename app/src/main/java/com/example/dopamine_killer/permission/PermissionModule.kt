package com.example.dopamine_killer.permission

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class Permissionodule {

    @Binds
    abstract fun bindPermissionChecker(
        permissionUtils: PermissionUtils
    ): PermissionChecker
}