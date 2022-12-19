package com.example.rcwol.ds;

public class Computer {
    private int id;
    private int folderId;
    private String name;
    private String MAC;
    private String IP;

    public Computer() {
    }

    public Computer(int id, int folderId, String name, String MAC, String IP) {
        this.id = id;
        this.folderId = folderId;
        this.name = name;
        this.MAC = MAC;
        this.IP = IP;
    }

    public Computer(int folderId, String name, String MAC, String IP) {
        this.folderId = folderId;
        this.name = name;
        this.MAC = MAC;
        this.IP = IP;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getFolderId() {
        return folderId;
    }

    public void setFolderId(int folderId) {
        this.folderId = folderId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMAC() {
        return MAC;
    }

    public void setMAC(String MAC) {
        this.MAC = MAC;
    }

    public String getIP() {
        return IP;
    }

    public void setIP(String IP) {
        this.IP = IP;
    }
}
