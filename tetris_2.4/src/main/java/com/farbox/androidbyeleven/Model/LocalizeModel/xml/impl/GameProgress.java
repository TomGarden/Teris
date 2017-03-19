package com.farbox.androidbyeleven.Model.LocalizeModel.xml.impl;

import android.content.SharedPreferences;
import android.graphics.Point;

import com.farbox.androidbyeleven.Controller.Control.GameThread;
import com.farbox.androidbyeleven.Model.LocalizeModel.xml.IReadGameProgress;
import com.farbox.androidbyeleven.Model.LocalizeModel.xml.IWriteGameProgress;
import com.farbox.androidbyeleven.Model.RunModel.BaseModel;
import com.farbox.androidbyeleven.Model.RunModel.HiScore;
import com.farbox.androidbyeleven.Model.RunModel.ITetrisMoveModelSet;
import com.farbox.androidbyeleven.Model.RunModel.ITetrisShowModelSet;
import com.farbox.androidbyeleven.Utils.LogUtil;
import com.farbox.androidbyeleven.View.Weight.TetrisMove;
import com.farbox.androidbyeleven.View.Weight.TetrisShow;

/**
 * describe:保存游戏进度
 * time: 2017/3/14 9:02
 * email: tom.work@foxmail.com
 */
public class GameProgress extends BaseLocalize implements IReadGameProgress, IWriteGameProgress {

    public final String fileName = "GameProgress";

    public final String beakerMatrix = "beakerMatrix";
    public final String showTetrisMatrix = "showTetrisMatrix";
    public final String moveTetrisMatrix = "moveTetrisMatrix";
    //public final String moveTetrisPixPos = "moveTetrisPixPos";//像素位置
    public final String moveTetrisLogicPos = "moveTetrisLogicPos";//逻辑位置
    public final String level = "level";
    public final String hiScore = "hiScore";
    public final String defaultStr = "default";

    private ITetrisShowModelSet tetrisShowModelSet;
    private ITetrisMoveModelSet tetrisMoveModelSet;

    public GameProgress(ITetrisShowModelSet tetrisShowModelSet, ITetrisMoveModelSet tetrisMoveModelSet) {
        this.tetrisShowModelSet = tetrisShowModelSet;
        this.tetrisMoveModelSet = tetrisMoveModelSet;
    }

    public SharedPreferences getSP() {
        return super.getSP(this.fileName);
    }

    public SharedPreferences.Editor getEditor() {
        return super.getEditor(this.fileName);
    }

    /**
     * 调用本方法本地化游戏进度。
     * <p>
     * 调用入口包括，手动暂停，息屏暂停
     */
    public void saveProgress() {
        SharedPreferences.Editor editor = super.getEditor(this.fileName);
        //保存内容的格式：行数:列数：矩阵内容String
        editor.putString(beakerMatrix, BaseModel.getInstance().getBakerMatris2Str());//20:10:111111111
        editor.putString(showTetrisMatrix, TetrisShow.getInstance().getMatris2Str());//2:3:1111111111
        editor.putString(moveTetrisMatrix, TetrisMove.getInstance().getMatris2Str());//2:3:1111111111
        //editor.putString(moveTetrisPixPos, TetrisMove.getInstance().getTetrisPixPos2Str());//1:2
        editor.putString(moveTetrisLogicPos, TetrisMove.getInstance().getTetrisLogicPos2Str());//1:2

        editor.putString(level, "" + GameThread.getInstance().getLevel());
        editor.putString(hiScore, "" + HiScore.getInstance().getScore());
        editor.apply();
    }

    public void print() {
        SharedPreferences sharedPreferences = super.getSP(this.fileName);
        LogUtil.i(LogUtil.msg() + beakerMatrix + "\t" + sharedPreferences.getString(beakerMatrix, defaultStr));//20:10:111111111
        LogUtil.i(LogUtil.msg() + showTetrisMatrix + "\t" + sharedPreferences.getString(showTetrisMatrix, defaultStr));//2:3:1111111111
        LogUtil.i(LogUtil.msg() + moveTetrisMatrix + "\t" + sharedPreferences.getString(moveTetrisMatrix, defaultStr));//2:3:1111111111
        //LogUtil.i(LogUtil.msg() + moveTetrisPixPos + "\t" + sharedPreferences.getString(moveTetrisPixPos, defaultStr));//1:2
        LogUtil.i(LogUtil.msg() + moveTetrisLogicPos + "\t" + sharedPreferences.getString(moveTetrisLogicPos, defaultStr));//1:2
        LogUtil.i(LogUtil.msg() + level + "\t" + sharedPreferences.getString(level, defaultStr));
        LogUtil.i(LogUtil.msg() + hiScore + "\t" + sharedPreferences.getString(hiScore, defaultStr));
    }

    /**
     * 读取用户上次的游戏进度
     *
     * @return
     */
    @Override
    public boolean readProgress() {
        SharedPreferences sharedPreferences = super.getSP(this.fileName);
        String result = sharedPreferences.getString(beakerMatrix, defaultStr);//20:10:111111111
        if (result.equals(defaultStr)) {
            return false;
        }
        if (!this.setBeakerMatrix(result)) {
            return false;
        }

        result = sharedPreferences.getString(showTetrisMatrix, defaultStr);//2:3:1111111111
        if (result.equals(defaultStr)) {
            return false;
        }
        if (!this.setShowTetrisMatrix(result)) {
            return false;
        }

        result = sharedPreferences.getString(moveTetrisMatrix, defaultStr);//2:3:1111111111
        if (result.equals(defaultStr)) {
            return false;
        }
        if (!this.setMoveTetrisMatrix(result)) {
            return false;
        }


        result = sharedPreferences.getString(moveTetrisLogicPos, defaultStr);//1:2
        if (result.equals(defaultStr)) {
            return false;
        }
        if (!this.setMoveTetrisLogicPos(result)) {
            return false;
        }

        result = sharedPreferences.getString(level, defaultStr);
        if (result.equals(defaultStr)) {
            return false;
        }
        this.setLevel(result);

        result = sharedPreferences.getString(hiScore, defaultStr);
        if (result.equals(defaultStr)) {
            return false;
        }
        this.setHiScore(result);

        return true;
    }

    private int[][] getArrayFormStr(String strMatrix) {
        String[] strArray = strMatrix.split(":");
        if (strArray.length != 3) {
            LogUtil.w(LogUtil.msg() + "保存的内容有误，请检查保存逻辑");
            return null;
        }
        int h = Integer.parseInt(strArray[0]);
        int v = Integer.parseInt(strArray[1]);
        if (strArray[2].length() != h * v) {
            LogUtil.w(LogUtil.msg() + "保存的内容有误，请检查保存逻辑");
            return null;
        }
        int[][] result = new int[h][v];
        for (int hIndex = 0; hIndex < h; hIndex++) {
            for (int vIndex = 0; vIndex < v; vIndex++) {
                int index = hIndex * v + vIndex;
                result[hIndex][vIndex] = Character.getNumericValue(strArray[2].charAt(index));
            }
        }
        return result;
    }

    private boolean setBeakerMatrix(String strMatrix) {
        int[][] result = this.getArrayFormStr(strMatrix);
        if (result == null) {
            return false;
        }
        LogUtil.i(LogUtil.msg() + "这里还有待测试，如果没有，现在得到的矩阵和保存的矩阵是否有不同");
        BaseModel.getInstance().setBeakerMatris(result);
        return true;
    }

    private boolean setShowTetrisMatrix(String strMatrix) {
        int[][] result = this.getArrayFormStr(strMatrix);
        if (result == null) {
            return false;
        }
        LogUtil.i(LogUtil.msg() + "这里还有待测试，如果没有，现在得到的矩阵和保存的矩阵是否有不同");
        this.tetrisShowModelSet.setCurrentMatrix(result);
        return true;
    }

    private boolean setMoveTetrisMatrix(String strMatrix) {
        int[][] result = this.getArrayFormStr(strMatrix);
        if (result == null) {
            return false;
        }
        LogUtil.i(LogUtil.msg() + "这里还有待测试，如果没有，现在得到的矩阵和保存的矩阵是否有不同");

        LogUtil.i(LogUtil.msg() + "this.tetrisMoveModelSet.setCurrentMatrix(result);");
        this.tetrisMoveModelSet.setCurrentMatrix(result);
        return true;
    }

    private Point getPointFormStr(String strPoint) {
        String[] strArray = strPoint.split(":");
        if (strArray.length != 2) {
            LogUtil.w(LogUtil.msg() + "保存的内容有误，请检查保存逻辑");
            return null;
        }
        Point result = new Point();
        result.x = Integer.parseInt(strArray[0]);
        result.y = Integer.parseInt(strArray[1]);
        return result;
    }

    private boolean setMoveTetrisLogicPos(String strPoint) {
        Point result = this.getPointFormStr(strPoint);
        if (result == null) {
            return false;
        }
        TetrisMove.getInstance().setTetrisLogicPos(result);
        return true;
    }

    private void setLevel(String str) {
        int level = Integer.parseInt(str);
        GameThread.getInstance().setLevel(level);
    }

    private void setHiScore(String strScor) {
        int hiScore = Integer.parseInt(strScor);
        HiScore.getInstance().setScore(hiScore);
    }
}
