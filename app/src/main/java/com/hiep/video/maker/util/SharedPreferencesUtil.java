package com.hiep.video.maker.util;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SharedPreferencesUtil {
    protected SharedPreferences mSp;

    public SharedPreferencesUtil(Context context, String nameShare) {
        this.mSp = context.getSharedPreferences(nameShare, Context.MODE_PRIVATE);
    }

    private boolean executeWithEditor(SharedPreferencesUtil.Executable executable) {
        Editor editor = this.mSp.edit();
        executable.excute(editor);
        return editor.commit();
    }

    public void clear() {
        this.executeWithEditor(new SharedPreferencesUtil.Executable() {
            public void excute(Editor editor) {
                editor.clear();
            }
        });
    }

    public boolean getBoolean(String key, boolean defValue) {
        return this.mSp.getBoolean(key, defValue);
    }

    public Editor getEditor() {
        return this.mSp.edit();
    }

    public float getFloat(String key, float defValue) {
        return this.mSp.getFloat(key, defValue);
    }

    public int getInt(String var1, int var2) {
        return this.mSp.getInt(var1, var2);
    }

    public long getLong(String var1, long var2) {
        return this.mSp.getLong(var1, var2);
    }

    public SharedPreferences getSharedPreferences() {
        return this.mSp;
    }

    public String getString(String var1, String var2) {
        return this.mSp.getString(var1, var2);
    }

    public boolean put(final String var1, final float var2) {
        return this.executeWithEditor(new SharedPreferencesUtil.Executable() {
            public void excute(Editor var1x) {
                var1x.putFloat(var1, var2);
            }
        });
    }

    public boolean put(final String var1, final int var2) {
        return this.executeWithEditor(new SharedPreferencesUtil.Executable() {
            public void excute(Editor var1x) {
                var1x.putInt(var1, var2);
            }
        });
    }

    public boolean put(final String var1, final long var2) {
        return this.executeWithEditor(new SharedPreferencesUtil.Executable() {
            public void excute(Editor var1x) {
                var1x.putLong(var1, var2);
            }
        });
    }

    public boolean put(final String var1, final String var2) {
        return this.executeWithEditor(new SharedPreferencesUtil.Executable() {
            public void excute(Editor var1x) {
                var1x.putString(var1, var2);
            }
        });
    }

    public boolean put(final String var1, final boolean var2) {
        return this.executeWithEditor(new SharedPreferencesUtil.Executable() {
            public void excute(Editor var1x) {
                var1x.putBoolean(var1, var2);
            }
        });
    }

    public void remove(final String var1) {
        this.executeWithEditor(new SharedPreferencesUtil.Executable() {
            public void excute(Editor var1x) {
                var1x.remove(var1);
            }
        });
    }

    interface Executable {
        void excute(Editor var1);
    }
}