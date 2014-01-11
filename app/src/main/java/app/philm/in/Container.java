package app.philm.in;

import com.google.common.base.Preconditions;

import com.jakewharton.trakt.Trakt;
import com.squareup.otto.Bus;

import android.accounts.AccountManager;
import android.content.Context;

import java.text.DateFormat;
import java.util.concurrent.Executors;

import app.philm.in.account.AndroidAccountManager;
import app.philm.in.accounts.PhilmAccountManager;
import app.philm.in.state.AsyncDatabaseHelper;
import app.philm.in.state.AsyncDatabaseHelperImpl;
import app.philm.in.state.DatabaseHelper;
import app.philm.in.state.PhilmSQLiteOpenHelper;
import app.philm.in.util.AndroidLogger;
import app.philm.in.util.BackgroundExecutor;
import app.philm.in.util.Logger;
import app.philm.in.util.PhilmBackgroundExecutor;
import app.philm.in.util.TypefaceManager;

public class Container {

    private static Container sInstance;

    public static Container getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new Container(context.getApplicationContext());
        }
        return sInstance;
    }

    private final Context mContext;

    private Bus mEventBus;

    private BackgroundExecutor mThreadPoolExecutor;
    private BackgroundExecutor mSingleThreadExecutor;

    private Trakt mTrakt;
    private AsyncDatabaseHelperImpl mAsyncDatabaseHelper;
    private DatabaseHelper mDatabaseHelper;
    private TypefaceManager mTypefaceManager;
    private Logger mLogger;
    private PhilmAccountManager mAccountManager;

    private DateFormat mMediumDateFormat;

    private Container(Context context) {
        mContext = Preconditions.checkNotNull(context, "context cannot be null");
    }

    public Bus getEventBus() {
        if (mEventBus == null) {
            mEventBus = new Bus();
        }
        return mEventBus;
    }

    public BackgroundExecutor getMultiThreadExecutor() {
        if (mThreadPoolExecutor == null) {
            final int numberCores = Runtime.getRuntime().availableProcessors();
            mThreadPoolExecutor =
                    new PhilmBackgroundExecutor(Executors.newFixedThreadPool(numberCores * 2 + 1));
        }
        return mThreadPoolExecutor;
    }

    public BackgroundExecutor getSingleThreadExecutor() {
        if (mSingleThreadExecutor == null) {
            mSingleThreadExecutor =
                    new PhilmBackgroundExecutor(Executors.newSingleThreadExecutor());
        }
        return mSingleThreadExecutor;
    }

    public Trakt getTraktClient() {
        if (mTrakt == null) {
            mTrakt = new Trakt();
            mTrakt.setApiKey(Constants.TRAKT_API_KEY);
            mTrakt.setIsDebug(Constants.DEBUG_NETWORK);
        }
        return mTrakt;
    }

    public AsyncDatabaseHelper getAsyncDatabaseHelper() {
        if (mAsyncDatabaseHelper == null) {
            mAsyncDatabaseHelper = new AsyncDatabaseHelperImpl(
                    getSingleThreadExecutor(), getDatabaseHelper());
        }
        return mAsyncDatabaseHelper;
    }

    public DatabaseHelper getDatabaseHelper() {
        if (mDatabaseHelper == null) {
            mDatabaseHelper = new PhilmSQLiteOpenHelper(mContext);
        }
        return mDatabaseHelper;
    }

    public TypefaceManager getTypefaceManager() {
        if (mTypefaceManager == null) {
            mTypefaceManager = new TypefaceManager(mContext.getAssets());
        }
        return mTypefaceManager;
    }

    public Logger getLogger() {
        if (mLogger == null) {
            mLogger = new AndroidLogger();
        }
        return mLogger;
    }

    public PhilmAccountManager getAccountManager() {
        if (mAccountManager == null) {
            mAccountManager = new AndroidAccountManager(AccountManager.get(mContext));
        }
        return mAccountManager;
    }

    public DateFormat getMediumDateFormat() {
        if (mMediumDateFormat == null) {
            mMediumDateFormat = android.text.format.DateFormat.getMediumDateFormat(mContext);
        }
        return mMediumDateFormat;
    }
}
