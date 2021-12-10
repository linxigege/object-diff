package xyz.arbres.objdiff.core;

import xyz.arbres.objdiff.core.diff.Diff;
import xyz.arbres.objdiff.core.diff.DiffFactory;

/**
 * ObjDiffCore
 *
 * @author carlos
 * @date 2021-12-07
 */
public class ObjDiffCore implements ObjDiff{

    private final DiffFactory diffFactory;

    ObjDiffCore(DiffFactory diffFactory) {
        this.diffFactory = diffFactory;
    }

    @Override
    public Diff compare(Object oldVersion, Object currentVersion) {
        return diffFactory.compare(oldVersion, currentVersion);
    }


}
