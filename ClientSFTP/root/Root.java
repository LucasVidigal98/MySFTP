
package root;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;

public class Root {
    
    public int portConnection;
    private Integer[] ports;
    private final String ip;
    private SecretKeySpec key;
    
    public Root(String ip) throws IOException, Exception{
        this.ip = ip;
        this.ports = new Integer[2];
        this.portConnection = 6080;
        this.initRoot();
    }
    
    public class ListenServer extends Thread{
        
        @Override
        public void run(){
            //FAZER O CÓDIGO AINDA, TEM QUE VER COMO O SERVIDOR VAI MANDAR A MENSAGEM.
        }
    }
    
    private void initRoot() throws IOException, Exception{
       
        Socket client = new Socket(ip, portConnection);
        
        ObjectInputStream io = null;
        
        io = new ObjectInputStream(client.getInputStream());
        
        try {
            ports = (Integer[]) io.readObject();
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Root.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        io.close();
        
        client.close();
        
        this.validatePasswd();
    }
    
    public void getKey(int port) throws IOException, ClassNotFoundException{
        
        Socket client = null;
        
        client = new Socket(ip, port);
        
        ObjectInputStream io = null;
        
        io = new ObjectInputStream(client.getInputStream());
        
        this.key = (SecretKeySpec) io.readObject();
        
        io.close();
        
        client.close();
        
        System.out.println("Pegou a chave");
    }
    
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
    
    private void validatePasswd() throws IOException, Exception{
        
        try{
            this.getKey(ports[2]);
        }catch(Exception e){
            System.out.println("Retry Connection - " + e.getMessage());
            this.getKey(ports[2]);
        }
        
        System.out.println("Conectado com o servidor!");
        System.out.print("Digite sua senha: ");
        Scanner input = new Scanner(System.in);
        String passwd = input.nextLine();
       
        Socket client = new Socket(ip, ports[0]);
        
        ObjectOutputStream io = null;
        
        io = new ObjectOutputStream(client.getOutputStream());
        
        io.flush();
        
        String command = "login " + "root " + passwd;
        io.writeObject(Encripty(command.getBytes(), key));
        
        io.close();
       
        client.close();
        
        // ----------------------------------------- RECEBE O VALIDAÇÃO DA SENHA -------------------------------------------------
        // Se a validação for verdadeira inicia o shell para a digitação do comando e a thread que recebe dados do servidor
        new ListenServer().start();
        this.shell();
    }
    
    private void shell() throws Exception{
        
        System.out.println("Autenticado!!");
        
        while(true){
            
            System.out.print("$> ");
            Scanner input = new Scanner(System.in);
            String command = input.nextLine();
            
            byte[] encrypted = Encripty(command.getBytes(), key);
            
            Socket client = new Socket(ip, ports[0]);
            
            ObjectOutputStream io = new ObjectOutputStream(client.getOutputStream());
            
            io.writeObject(encrypted);
            
            io.close();
            
            client.close();
        }
        
    }
}
