package com.zesa07.security.di

import android.content.Context
import androidx.room.Room
import com.zesa07.security.data.db.AppDatabase
import com.zesa07.security.data.db.dao.AchievementDao
import com.zesa07.security.data.db.dao.CtfProgressDao
import com.zesa07.security.data.db.dao.LabProgressDao
import com.zesa07.security.data.db.dao.ScanLogDao
import com.zesa07.security.data.db.dao.TutorMessageDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext ctx: Context): AppDatabase =
        Room.databaseBuilder(ctx, AppDatabase::class.java, AppDatabase.DB_NAME)
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideScanLogDao(db: AppDatabase): ScanLogDao = db.scanLogDao()

    @Provides
    fun provideCtfProgressDao(db: AppDatabase): CtfProgressDao = db.ctfProgressDao()

    @Provides
    fun provideLabProgressDao(db: AppDatabase): LabProgressDao = db.labProgressDao()

    @Provides
    fun provideAchievementDao(db: AppDatabase): AchievementDao = db.achievementDao()

    @Provides
    fun provideTutorMessageDao(db: AppDatabase): TutorMessageDao = db.tutorMessageDao()

    /**
     * OkHttpClient used ONLY for:
     *  (a) short-timeout TCP connect probes against user-confirmed private-lab IPs
     *      (see LocalNetworkScanner / LabPortScanner), and
     *  (b) HTTPS calls to the Claude tutor API.
     * Short timeouts prevent the port-probe use case from hanging the UI.
     */
    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(1500, TimeUnit.MILLISECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .writeTimeout(10, TimeUnit.SECONDS)
        .build()
}
