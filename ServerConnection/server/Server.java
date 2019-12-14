
package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;

public class Server {
   
    public static int portBaseUser;
    public static int portBaseRoot;
    private SecretKeySpec key;
   
     public static byte[] Encripty(byte[] msg, SecretKeySpec key) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] encrypted = cipher.doFinal(msg);
        return encrypted;
    }

    public static byte[] Decripty(byte[] msg, SecretKeySpec key) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] decrypted = cipher.doFinal(msg);
        return decrypted;
    }
    
    public class ThreadListenRoot extends Thread{
        
        private int port;
        
        public ThreadListenRoot(int port){
            this.port = port;
        }
        
        @Override
        public void run(){
            
            ServerSocket server = null;
    
            try {
               server = new ServerSocket(port);
            } catch (IOException ex) {
               Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            System.out.println(" - Servidor aberto na porta " + port + " para comunicação segura com o root");
            
            while(true){
                
                Socket client = null;
                byte[] command = null;
                
                try {
                    client = server.accept();
                } catch (IOException ex) {
                    Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                //Recebe o comando
                
                ObjectInputStream io = null;
                
                try {
                    io = new ObjectInputStream(client.getInputStream());
                } catch (IOException ex) {
                    Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                try {
                    command = (byte[]) io.readObject();
                } catch (IOException ex) {
                    Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                }
               
                try {
                    System.out.println(new String(Decripty(command, key)));
                } catch (Exception ex) {
                    Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                try {
                    io.close();
                } catch (IOException ex) {
                    Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                try {
                    client.close();
                } catch (IOException ex) {
                    Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                /* ------------ OPERAÇÃO COM COMANDOS ENTRAM AQUI ------------ */
            }
        }
        
    }
    
    public class ThreadListenUser extends Thread{
        
        private int port;
        
        public ThreadListenUser(int port){
            this.port = port;
        }
        
        @Override
        public void run(){
            
            ServerSocket server = null;
            
            try {
               server = new ServerSocket(port);
            } catch (IOException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
             
            System.out.println(" * Servidor conectado na porta " + port + " para conexão com usário");
        
            while(true){ 
                Socket client = null;
                ObjectInputStream io = null;
                String command = "";
                
                try {
                    client = server.accept();
                } catch (IOException ex) {
                    Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                try {
                    io = new ObjectInputStream(client.getInputStream());
                } catch (IOException ex) {
                    Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                try {
                    command = (String) io.readObject();
                } catch (IOException ex) {
                    Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                try {
                    io.close();
                } catch (IOException ex) {
                    Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                try {
                    client.close();
                } catch (IOException ex) {
                    Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                System.out.println(command);
                
                /* ------------ OPERAÇÃO COM COMANDOS ENTRAM AQUI ------------ */
            }
        }
    }
    
    public class ThreadSendKey extends Thread{
        
        private int port;
        
        public ThreadSendKey(int port){
            this.port = port;
        }
        
        @Override
        public void run(){
           
            KeyGenerator keyGenerator = null;
            
            try {
                keyGenerator = KeyGenerator.getInstance("AES");
            } catch (NoSuchAlgorithmException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            key = (SecretKeySpec) keyGenerator.generateKey();
            
            ServerSocket server = null;
            try {
                server = new ServerSocket(port);
            } catch (IOException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            System.out.println(" - Evio da chave na porta " + port);
            
            while(true){
            
                Socket client = null;

                try {
                    client = server.accept();
                } catch (IOException ex) {
                    Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                }

                ObjectOutputStream io = null;

                try {
                    io = new ObjectOutputStream(client.getOutputStream());
                } catch (IOException ex) {
                    Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                }

                try {
                    io.flush();
                } catch (IOException ex) {
                    Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                }

                try {
                    io.writeObject(key);
                } catch (IOException ex) {
                    Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                }

                try {
                    io.close();
                } catch (IOException ex) {
                    Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                }

                try {
                    client.close();
                } catch (IOException ex) {
                    Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
    
    public class ThreadConnectionRoot extends Thread{
        
       @Override
       public void run(){
           
            ServerSocket server = null;
            int port = 6080;
           
            try {
               server = new ServerSocket(port);
            } catch (IOException ex) {
               Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
           
            System.out.println("Servidor aberto na porta " + port + " para conexão segura com o root");
            
            while(true){
               
                Socket client = null;
                Integer[] ports = new Integer[3];
                
                try {
                   client = server.accept();
                } catch (IOException ex) {
                   Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                ports[0] = Server.portBaseRoot + 1;
                ports[1] = Server.portBaseRoot + 2;
                ports[2] = Server.portBaseRoot + 3;
                
                Server.portBaseRoot += 4;
                
                ObjectOutputStream io = null;
                
                try {
                    io = new ObjectOutputStream(client.getOutputStream());
                } catch (IOException ex) {
                    Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                try {
                    io.flush();
                } catch (IOException ex) {
                    Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                try {
                    io.writeObject(ports);
                } catch (IOException ex) {
                    Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                try {
                    io.close();
                } catch (IOException ex) {
                    Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                try {
                    client.close();
                } catch (IOException ex) {
                    Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                //Inicia a thread de envio e recebimento
                new ThreadSendKey(ports[2]).start();
                new ThreadListenRoot(ports[0]).start();
            }
        }
        
    }
    
    public class ThreadConnectionUser extends Thread{
        
        @Override
        public void run(){
            
            ServerSocket server = null;
            int port = 6090;
            
            try {
                server = new ServerSocket(port);
            } catch (IOException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            System.out.println("Servidor aberto na porta " + port + " para comunicação com usuário.");
            
            while(true){
                
                Socket client = null;
                Integer[] ports = new Integer[2];
                
                try {
                    client = server.accept();
                } catch (IOException ex) {
                    Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                ports[0] = Server.portBaseUser + 1;
                ports[1] = Server.portBaseUser + 2;
                
                Server.portBaseUser += 3;
                
                ObjectOutputStream io = null;
                
                try {
                    io = new ObjectOutputStream(client.getOutputStream());
                } catch (IOException ex) {
                    Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                try {
                    io.flush();
                } catch (IOException ex) {
                    Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                try {
                    io.writeObject(ports);
                } catch (IOException ex) {
                    Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                try {
                    io.close();
                } catch (IOException ex) {
                    Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                try {
                    client.close();
                } catch (IOException ex) {
                    Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                //Inicia a thread de envio e recebimento
                new ThreadListenUser(ports[0]).start();
            }
        }
    }
    
    private void init(){
        
        new ThreadConnectionRoot().start();
        new ThreadConnectionUser().start();
    }
    
    public static void main(String[] args) {
        Server.portBaseUser = 10000;
        Server.portBaseRoot = 60000;
        new Server().init();
    }
}
