package xyz.arbres.objdiff.core.metamodel.object;


import xyz.arbres.objdiff.common.exception.ObjDiffException;
import xyz.arbres.objdiff.common.exception.ObjDiffExceptionCode;
import xyz.arbres.objdiff.common.validation.Validate;

import java.util.function.Supplier;

public abstract class ValueObjectIdWithHash extends ValueObjectId {
    private static final String HASH_PLACEHOLDER = "{hashPlaceholder}";

    public ValueObjectIdWithHash(String typeName, GlobalId ownerId, String fragment) {
        super(typeName, ownerId, fragment);
    }

    public static boolean containsHashPlaceholder(String fragment) {
        if (fragment == null) {
            return false;
        }
        return fragment.contains(HASH_PLACEHOLDER);
    }

    public abstract boolean requiresHash();

    public abstract boolean hasHashOnParent();

    public abstract ValueObjectId freeze();

    public abstract ValueObjectId freeze(String hash);

    @Override
    public String toString() {
        return getOwnerId().toString() + "#" + getFragment() + " (" + this.getClass().getSimpleName() + ")";
    }

    static class ValueObjectIdWithPlaceholder extends ValueObjectIdWithHash {
        private final Supplier<String> parentFragment;
        private final String localPath;
        private final boolean requiresHash;
        private final boolean hasHashOnParent;
        private String hash;

        ValueObjectIdWithPlaceholder(String typeName, GlobalId ownerId, Supplier<String> parentFragment,
                                     String localPath, boolean requiresHash) {
            super(typeName, ownerId, parentFragment.get() + localPath +
                    (requiresHash ? "/" + HASH_PLACEHOLDER : ""));
            this.parentFragment = parentFragment;
            this.localPath = localPath;
            this.hash = requiresHash ? HASH_PLACEHOLDER : "";
            this.requiresHash = requiresHash;
            this.hasHashOnParent = containsHashPlaceholder(parentFragment.get());
        }

        @Override
        public ValueObjectId freeze(String hash) {
            Validate.conditionFulfilled(requiresHash, "Illegal state - hash not required");
            if (!HASH_PLACEHOLDER.equals(this.hash)) {
                throw new ObjDiffException(ObjDiffExceptionCode.RUNTIME_EXCEPTION, "already frozen");
            }
            this.hash = hash;
            if (!hasHashOnParent()) {
                return new ValueObjectId(getTypeName(), getOwnerId(), this.getFragment());
            }
            return new ValueObjectIdWithPlaceholder(getTypeName(), getOwnerId(), parentFragment,
                    localPath + "/" + hash, false);
        }

        @Override
        public ValueObjectId freeze() {
            Validate.conditionFulfilled(!requiresHash, "Illegal state - hash required");
            if (getFragment().contains(HASH_PLACEHOLDER)) {
                throw new ObjDiffException(ObjDiffExceptionCode.RUNTIME_EXCEPTION, "can't freeze ValueObjectId, there is still a hash in parent fragment");
            }
            return new ValueObjectId(getTypeName(), getOwnerId(), this.getFragment());
        }

        @Override
        public boolean requiresHash() {
            return requiresHash;
        }

        @Override
        public boolean hasHashOnParent() {
            return hasHashOnParent;
        }

        @Override
        public String getFragment() {
            if (requiresHash) {
                return parentFragment.get() + localPath + "/" + hash;
            }
            return parentFragment.get() + localPath;
        }
    }
}