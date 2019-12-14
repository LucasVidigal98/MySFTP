
package user;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class User {
    
    public int portConnection;
    private Integer[] ports;
    private final String ip, user;
    
    public User(String ip, String user) throws IOException, ClassNotFoundException, Exception{
        this.ip = ip;
        this.user = user;
        this.ports = new Integer[2];
        this.portConnection = 6090;
        this.initUser();
    }
    
     public class ListenServer extends Thread{
        
        @Override
        public void run(){
            //FAZER O CÓDIGO AINDA, TEM QUE VER COMO O SERVIDOR VAI MANDAR A MENSAGEM.
        }
    }
    
    private void initUser() throws IOException, ClassNotFoundException, Exception{
       
        Socket client = null;
        ObjectInputStream io = null;

        try {
            client = new Socket(ip, portConnection);
        } catch (IOException ex) {
            Logger.getLogger(User.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            io = new ObjectInputStream(client.getInputStream());
        } catch (IOException ex) {
            Logger.getLogger(User.class.getName()).log(Level.SEVERE, null, ex);
        }

        ports = (Integer[]) io.readObject();

        io.close();

        client.close();
        
        this.validatePasswd();
    }
    
    private void validatePasswd() throws IOException, Exception{
        
        System.out.println("Conectado com sucesso!");
        System.out.print("Digite sua senha: ");
        Scanner input = new Scanner(System.in);
        String passwd = input.nextLine();
        
        Socket client = null;
        ObjectOutputStream io = null;
        
        client = new Socket(ip, ports[0]);
        
        io = new ObjectOutputStream(client.getOutputStream());
        
        io.flush();
        
        String command = "login " + user + " " + passwd;
        io.writeObject(command);
        
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
            
            Socket client = new Socket(ip, ports[0]);
            
            ObjectOutputStream io = new ObjectOutputStream(client.getOutputStream());
            
            io.writeObject(command);
            
            io.close();
            
            client.close();
        }
        
    }
    
}
