package xyz.arbres.objdiff.core;

import xyz.arbres.objdiff.core.diff.Diff;

/***
 * @author carlos
 * @date 2021/12/7
 * @description TODO
 */
public interface ObjDiff {

    Diff compare(Object oldVersion, Object currentVersion);


}
