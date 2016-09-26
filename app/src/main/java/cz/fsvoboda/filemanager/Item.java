package cz.fsvoboda.filemanager;


import java.io.File;

public class Item implements Comparable {
    private String name;
    private String path;
    private Type type;
    private boolean canRead;

    public enum Type {DIR, FILE};

    public Item(String name, String path) {
        this.name = name;
        this.path = path;

        File file = new File(path);
        if (!file.exists()) {
            file = new File("/");
            this.name = "/";
            this.path = "/";
        }
        this.type = file.isDirectory() ? Item.Type.DIR : Item.Type.FILE;
        this.canRead = file.canRead();
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    public Type getType() {
        return type;
    }

    public boolean canRead() {
        return this.canRead;
    }

    public String extension() {
        if (name.lastIndexOf(".") == -1) {
            return null;
        } else {
            String ext = name.substring(name.lastIndexOf(".") + 1);
            if (ext.indexOf("%") > -1) {
                ext = ext.substring(0, ext.indexOf("%"));
            }
            if (ext.indexOf("/") > -1) {
                ext = ext.substring(0, ext.indexOf("/"));
            }
            return ext.toLowerCase();
        }
    }

    public void delete() {
        File file = new File(path);
        file.delete();
    }

    @Override
    public int compareTo(Object o) {
        return this.name.toLowerCase().compareTo(((Item)o).getName().toLowerCase());
    }
}
