package br.com.claw.fileTransfer;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class SFTPConnection {

    private String HOSTNAME;
    private String USERNAME;
    private String PASSWORD;

    private InputStream is;

    private Integer PORT;

    private ChannelSftp channel;
    private Session session;

    private Map<Boolean, String> response;

    public SFTPConnection(String hostName, String username, String password,Integer port){
        response = new HashMap<>();
        HOSTNAME = hostName;
        USERNAME = username;
        PASSWORD = password;
        PORT = port;
    }

    public SFTPConnection(String hostName, String username, String password){
        response = new HashMap<>();
        HOSTNAME = hostName;
        USERNAME = username;
        PASSWORD = password;
        PORT = null;
    }

    public void uploadFile(File file, String path){
        try{
            openChannel();
            channel.put(file.getAbsolutePath(), path);
            closeChannel();
            response.put(true, "Success!");
        }catch (Exception e){
            e.printStackTrace();
            response.put(false, e.getMessage());
        }
    }

    public void downloadFile(String path, String dest){
        try{
            openChannel();
            channel.get(path, dest);
            is = channel.getInputStream();
            closeChannel();
            response.put(true,"Success!");
        }catch (Exception e){
            e.printStackTrace();
            response.put(false, e.getMessage());
        }
    }

    public void openConnection(){
        try{
            JSch jSch = new JSch();
            jSch.setKnownHosts(System.getProperty("user.home")+"/.ssh/known_hosts");
            session = PORT == null ? jSch.getSession(USERNAME, HOSTNAME) : jSch.getSession(USERNAME, HOSTNAME, PORT);
            session.setPassword(PASSWORD);
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();
            response.put(true, "Success!");
        }catch (Exception e){
            e.printStackTrace();
            response.put(false, e.getMessage());
        }
    }

    private void openChannel() throws JSchException {
        channel = (ChannelSftp) session.openChannel("sftp");
        channel.connect();
    }

    private void closeChannel() {
        channel.disconnect();
    }

    public void closeConnection(){
        session.disconnect();
    }

    public String getResponse(boolean b) {
        String response = this.response.get(b);
        this.response.clear();
        return response;
    }
}
