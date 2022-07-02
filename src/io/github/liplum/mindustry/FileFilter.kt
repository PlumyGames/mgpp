package io.github.liplum.mindustry;

import java.io.File;
import java.io.Serializable;
import java.util.HashSet;

@FunctionalInterface
public interface FileFilter {
    boolean isAccept(File file);

    FileFilter always = file -> true;

    class Set implements FileFilter {
        public HashSet<FileFilter> set = new HashSet<>();

        public void add(FileFilter filter) {
            set.add(filter);
        }

        public boolean remove(FileFilter filter) {
            return set.remove(filter);
        }

        public void plusAssign(FileFilter filter) {
            set.add(filter);
        }

        public void minusAssign(FileFilter filter) {
            set.remove(filter);
        }

        public void clear() {
            set.clear();
        }

        public boolean contains(FileFilter filter) {
            return set.contains(filter);
        }

        @Override
        public boolean isAccept(File file) {
            for (FileFilter filter : set) {
                if (!filter.isAccept(file)) return false;
            }
            return true;
        }
    }
}
