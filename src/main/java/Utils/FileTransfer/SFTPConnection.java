package Utils.FileTransfer;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
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

    public SFTPConnection(String hostName, String username, String password,Integer port){
        HOSTNAME = hostName;
        USERNAME = username;
        PASSWORD = password;
        PORT = port;
    }

    public SFTPConnection(String hostName, String username, String password){
        HOSTNAME = hostName;
        USERNAME = username;
        PASSWORD = password;
        PORT = null;
    }

    public Map<Boolean, String> uploadFile(File file, String path){
        Map<Boolean, String> response = new HashMap<>();
        try{
            openChannel();
            channel.put(is,path);
            closeChannel();
            response.put(true, "Success!");
        }catch (Exception e){
            e.printStackTrace();
            response.put(false, e.getMessage());
        }
        return response;
    }

    public Map<Boolean, String> downloadFile(String path, String dest){
        Map<Boolean, String> response = new HashMap<>();
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
        return response;
    }

    public Map<Boolean, String> openConnection(){
        Map<Boolean, String> response = new HashMap<>();
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

        return response;
    }

    private void openChannel() throws JSchException {
        channel = (ChannelSftp) session.openChannel("sftp");
        channel.connect();
    }

    private void closeChannel() throws JSchException{
        channel.disconnect();
    }

    public void closeConnection(){
        session.disconnect();
    }
}
